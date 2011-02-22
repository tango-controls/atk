// File:          DeviceFactory.java
// Created:       2001-09-19 09:50:31, assum
// By:            <assum@esrf.fr> <pons@esrf.fr>
// Time-stamp:    <2002-07-23 10:30:15, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core;

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
  // Debug trace for attributes refreshed by change events.
  public final static int TRACE_CHANGE_EVENT=32;
  // Debug trace for only state/status refresher of Device Factory.
  public final static int TRACE_STATE_REFRESHER=64;
  // Debug trace for attributes refreshed by change events.
  public final static int TRACE_PERIODIC_EVENT=128;
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
  //private Vector devices       = new Vector();
  private Vector<Device> devices = new Vector<Device> ();
  private static DeviceFactory instance;

  protected int refreshInterval = 1000;
  protected Refresher refresher = null;
  protected static boolean autoStart = true;
  protected boolean traceUnexpected = false;

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
    // We have to create a new thread object.
    refresher = null;
  }

  /**
   * <code>startRefresher</code>  starts the default refresher thread
   * for the Device which sleeps for refreshInterval seconds.
   * @see #setRefreshInterval(int)
   * @see java.lang.Thread
   */
  public void startRefresher() {
    if ( this.isRefreshing() ) return;
    if (refresher == null) {
      refresher = new Refresher("device");
      refresher.setRefreshInterval(getRefreshInterval());
      refresher.setTraceUnexpected( traceUnexpected );
      refresher.addRefreshee(this);
    }
    refresher.start();
  }

  /**
   * Returns true if the global device refresher is running.
   */ 
  public boolean isRefreshing() {
    if (refresher == null) {
      return false;
    } else {
      return refresher.isRunning();
    }
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
    } catch (ConnectionException de) {
      System.out.println("DeviceFactory.isDevice(" + name + ") : " + de.getErrors()[0].desc);
      return false;
    } catch (Exception e) {
      // Unexpected exception
      System.out.println("DeviceFactory.isDevice(" + name + ") : Unexpected exception caught...");
      e.printStackTrace();
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
    //if(pos>=0) d = (Device)devices.get(pos);
    if(pos>=0) d = devices.get(pos);

    if (pos < 0) {

      // The device has not been found, we have to create it
      long t0 = System.currentTimeMillis();
      try {
        d = new Device(name);
        trace(TRACE_SUCCESS,"DeviceFactory.getDevice("+name+") ok",t0);
      } catch (DevFailed e) {
        trace(TRACE_FAIL,"DeviceFactory.getDevice("+name+") failed",t0);
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

    // We refresh device only if listeners are registered
    for (int i = 0; i < devs.length; i++) {
      Device d = (Device)devs[i];
      if(d.getPropChanges().getListenerCount()>0)
        d.refresh();
    }

    trace(TRACE_STATE_REFRESHER ,"DeviceFactory.refresh() Loop time",t0);

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
  public List<String> getDeviceNames() {
    List<String> l = new Vector<String> ();
    synchronized (deviceMonitor) {
      for (int i=0; i<deviceNames.length;i++)
        l.add(deviceNames[i]);
    }
    return l;
  }

  /**
   * Returns an array containing all device of this factory.
   */
  public Device[] getDevices() {

    Device[] ret = null;
    synchronized (deviceMonitor) {
      ret = new Device[devices.size()];
      for(int i=0;i<devices.size();i++)
        //ret[i] = (Device)devices.get(i);
        ret[i] = devices.get(i);
    }    
    return ret;

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

  /**
   * Check whether the given name corresponds to an already existing device in the factory
   * @param name Device name.
   * @return true if the device has already been created in the factory
   */
  public synchronized boolean containsDevice(String name)
  {
    int    pos;
    Device d=null;
    String lowerName = name.toLowerCase();

    pos = Arrays.binarySearch(deviceNames,lowerName);
    //if(pos>=0) d = (Device)devices.get(pos);
    if(pos>=0) d = devices.get(pos);

    if (pos < 0) // The device has not been found
       return false;
    else
       return true;
  }

  /**
   * Adds a device instance in the factory if the device name is not already existant in the factory
   * @param dev Device instance to add.
   * @return true if the device has already been created in the factory
   */
  public synchronized void addDevice(Device dev)
  {
     int    pos;
     Device d=null;
     String lowerName=null;

     if (dev == null)
        return;
	
     lowerName = dev.getName().toLowerCase();
     pos = Arrays.binarySearch(deviceNames,lowerName);
	
     if (pos>=0) // device is already in the factory
        return;
 
     // Build the new deviceNames array
     int ipos = -(pos+1);
     int lgth = deviceNames.length;
     String[] newDeviceNames=new String[lgth+1];
     System.arraycopy(deviceNames,0,newDeviceNames,0,ipos);
     System.arraycopy(deviceNames,ipos,newDeviceNames,ipos+1,lgth-ipos);
     newDeviceNames[ipos]=lowerName;

     synchronized(deviceMonitor) {
       // Update list
       devices.add(ipos,dev);
       deviceNames=newDeviceNames;
     }

     dumpFactory("Adding " + lowerName);

     if (autoStart)
       startRefresher();
     
  }

  public String getVersion() {
    return "$Id$";
  }

public boolean isTraceUnexpected () {
    return traceUnexpected;
}

public void setTraceUnexpected (boolean traceUnexpected) {
    this.traceUnexpected = traceUnexpected;
    if (refresher != null) {
        refresher.setTraceUnexpected( traceUnexpected );
    }
}


}
