// File:          DeviceFactory.java
// Created:       2001-09-19 09:50:31, assum
// By:            <assum@esrf.fr> <pons@esrf.fr>
// Time-stamp:    <2002-07-23 10:30:15, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core;

import fr.esrf.tangoatk.core.Device;
import fr.esrf.Tango.DevFailed;

import java.util.*;

public class DeviceFactory implements IRefreshee, java.io.Serializable {

  // Does not display any trace.
  public final static int TRACE_NONE=0;
  // Trace only device call that fails (during initialisation).
  public final static int TRACE_FAIL=1;
  // Trace only sucessfull device call (during initialisation).
  public final static int TRACE_SUCCESS=2;
  // Trace only calls relative to the device refresher.
  public final static int TRACE_REFRESHER=4;
  // Trace only calls relative to the attribute refreshers.
  public final static int TRACE_ATTREFRESHER=8;
  // Trace only calls relative to commands.
  public final static int TRACE_COMMAND=16;
  // Debug trace for device factory.
  public final static int TRACE_DEVFACTORY=256;
  // Debug trace for attribute factory.
  public final static int TRACE_ATTFACTORY=512;
  // Debug trace for command factory.
  public final static int TRACE_CMDFACTORY=1024;
  // Trace all
  public final static int TRACE_ALL=0xFF;

  // Default trace value
  private int traceMode=TRACE_NONE;

  private Object deviceMonitor = new Object();
  private String[] deviceNames = new String[0]; // For fast string search
  private Vector devices       = new Vector();
  private static DeviceFactory instance;

  protected int refreshInterval = 1000;
  protected Refresher refresher = null;
  protected static boolean autoStart = true;

  private DeviceFactory() {}

  /** Returns an instance of the singleton device factory class */
  public static DeviceFactory getInstance() {
    if (instance == null) {
      instance = new DeviceFactory();
    }
    return instance;
  }

  /**
   * Set the trace level of ATK device calls.
   * @param level Trace level. Can be a combination of the following value:
   * @see #TRACE_NONE
   * @see #TRACE_FAIL
   * @see #TRACE_SUCCESS
   * @see #TRACE_REFRESHER
   * @see #TRACE_ATTREFRESHER
   * @see #TRACE_COMMAND
   * @see #TRACE_ALL
   */
  public void setTraceMode(int level) {
    traceMode = level;
  }

  /**
   * Returns the current trace level.
   * @see #setTraceMode
   */
  public int getTraceMode() {
    return traceMode;
  }

  /**
   * Print trace.
   * @param level Level of trace
   * @param startTime Time of execution in ms. (pass negative to ignore)
   * @param msg Message to display
   */
  public void trace(int level,String msg,long startTime) {

    if((traceMode&level)!=0) {
      long now = System.currentTimeMillis();
      if(startTime>=0)
        System.out.println(msg + " : " + Long.toString(now-startTime) + " ms");
      else
        System.out.println(msg);
    }

  }

  /**
   * <code>setRefreshInterval</code> sets the refresh interval for
   * the Device. This interval decides how often an entity is polled
   * to see if its value has changed. The default value is 1000, that is,
   * the entity is polled once a second.
   * @param milliSeconds an <code>int</code> value
   */
  public void setRefreshInterval(int milliSeconds) {
    refreshInterval = milliSeconds;
    if (refresher != null) {
      refresher.setRefreshInterval(refreshInterval);
    }
  }

  /**
   * <code>getRefreshInterval</code> gets the refresh-interval for
   * the entity list. The default value is 1000 milliseconds.
   * @return an <code>int</code> value which is the refresh-interval
   */
  public long getRefreshInterval() {
    return refreshInterval;
  }


  /**
   * <code>stopRefresher</code> stops the refresher.
   *
   */
  public void stopRefresher() {
    if (refresher != null)
      refresher.stopRunning();
  }

  /**
   * <code>startRefresher</code>  starts the default refresher thread
   * for the Device which sleeps for refreshInterval seconds.
   * @see #setRefreshInterval(int)
   * @see java.lang.Thread
   */
  public void startRefresher() {
    if (refresher == null) {
      refresher = new Refresher("device");
      refresher.setRefreshInterval(getRefreshInterval());
    }

    refresher.addRefreshee(this).start();


  }

  /**
   * <code>setRefresher</code> sets the resher thread
   * for this Device.
   * @param r an <code>ARefresher</code> value
   * @see fr.esrf.tangoatk.core.Refresher
   */
  public void setRefresher(Refresher r) {
    refresher = r;
  }

  /**
   * Check wether the given name correspond to an existing device.
   * @param name Device name.
   * @return true if the deivce exists.
   */
  public boolean isDevice(String name) {

    try {
      getDevice(name);
    } catch (Exception e) {
      return false;
    }

    return true;

  }

  /**
   * Get a handle to a device and add it to the global state/status refresher list.
   * @param name Device name
   * @return Device handle
   * @throws ConnectionException In case of failure.
   */
  public synchronized Device getDevice(String name) throws ConnectionException {

    int    pos;
    Device d=null;
    String lowerName = name.toLowerCase();

    pos = Arrays.binarySearch(deviceNames,lowerName);
    if(pos>=0) d = (Device)devices.get(pos);

    if (pos < 0) {

      // The device has not been found, we have to create it
      long t0 = System.currentTimeMillis();
      try {
        d = new Device(lowerName);
        trace(TRACE_SUCCESS,"DeviceFactory.getDevice("+lowerName+") ok",t0);
      } catch (DevFailed e) {
        trace(TRACE_FAIL,"DeviceFactory.getDevice("+lowerName+") failed",t0);
        throw new ConnectionException(e);
      }

      // Build the new deviceNames array
      int ipos = -(pos+1);
      int lgth = deviceNames.length;
      String[] newDeviceNames=new String[lgth+1];
      System.arraycopy(deviceNames,0,newDeviceNames,0,ipos);
      System.arraycopy(deviceNames,ipos,newDeviceNames,ipos+1,lgth-ipos);
      newDeviceNames[ipos]=lowerName;

      synchronized(deviceMonitor) {
        // Update list
        devices.add(ipos,d);
        deviceNames=newDeviceNames;
      }

      dumpFactory("Adding " + lowerName);

      if (autoStart)
        startRefresher();

    }

    return d;

  }

  /**
   * Executes the global state/status refresh on all device registered.
   * @see #getDevice
   */
  public void refresh() {

    // Build an array of device to be refreshed to
    // avoid locking the devices monitor during
    // the refreshing period.
    Object[] devs;

    synchronized (deviceMonitor) {
      devs = devices.toArray();
    }

    long t0 = System.currentTimeMillis();

    for (int i = 0; i < devs.length; i++) ((Device)devs[i]).refresh();

    trace(TRACE_REFRESHER ,"DeviceFactory.refresh() Loop time",t0);

  }

  private void dumpFactory(String msg) {
    if((traceMode&TRACE_DEVFACTORY)!=0) {
      System.out.println("-- DeviceFactory : " + msg + " --");
      for(int i=0;i<deviceNames.length;i++) {
        System.out.println("  " + i + ":" + deviceNames[i]);
      }
      System.out.println("-- DeviceFactory --------------------------------------");
    }
  }

  /**
   * Remove the given device from the global refresher list.
   * @param name Device to remove.
   */
  public synchronized void deleteDevice(String name) {

    String lowerName = name.toLowerCase();

    int pos = Arrays.binarySearch(deviceNames, lowerName);
    if (pos >= 0) {
      int lgth = deviceNames.length;
      String[] tmp = new String[lgth - 1];
      System.arraycopy(deviceNames, 0, tmp, 0, pos);
      System.arraycopy(deviceNames, pos + 1, tmp, pos, lgth - pos - 1);
      synchronized (deviceMonitor) {
        // Remove the device name
        deviceNames = tmp;
        devices.remove(pos);
      }
      dumpFactory("Removing " + lowerName);
    }

  }

  /**
   * Returns an array of string containing all device name of this factory.
   * @return A list of device name.
   */
  public List getDeviceNames() {
    List l = new Vector();
    synchronized (deviceMonitor) {
      for (int i=0; i<deviceNames.length;i++)
        l.add(deviceNames[i]);
    }
    return l;
  }

  /**
   * Sets the autostart property.
   * @param b True to start automaticaly the state/status refresher.
   */
  public static void setAutoStart(boolean b) {
    autoStart = b;
  }

  /**
   * Returns the autostart property.
   * @return True if the state/status refresher is started automaticaly.
   */
  public static boolean isAutoStart() {
    return autoStart;
  }

  public String getVersion() {
    return "$Id$";
  }


}
