// File:          DevicePool.java
// Created:       2001-09-19 09:50:31, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-10 13:53:10, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import fr.esrf.tangoatk.core.Device;
import fr.esrf.Tango.DevFailed;

import fr.esrf.TangoDs.*;
import fr.esrf.TangoApi.*;
import java.util.*;
import org.apache.log4j.Logger;

public class DeviceFactory implements IRefreshee, java.io.Serializable {
    transient static Logger log =
	ATKLogger.getLogger(DeviceFactory.class.getName());

    private Map devices = new Hashtable();
    protected List deviceList = new Vector();
    private static DeviceFactory instance;

    protected int refreshInterval = 1000;
    protected ARefresher refresher = null;



    protected static ThreadGroup refreshers;
    
    static {
	refreshers = new ThreadGroup("factoryRefreshers");
    }


    private DeviceFactory() {
    }

    public void clear() {
	devices.clear();
	deviceList.clear();
    }
    
    public static DeviceFactory getInstance() {
	if (instance == null) {
	    instance = new DeviceFactory();
	}
	return instance;
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
     * @see fr.esrf.tangoatk.core.Device#setRefreshInterval
     * @see java.lang.Thread
     */
    public void startRefresher() {
	if (refresher == null) {
	    refresher  = new ARefresher(refreshers, "device") {
		    
		public Thread addRefreshee(IRefreshee refreshee) {
		    this.refreshee = refreshee;
		    return this;
		}
		    
		public void run() {
		    while (true) {
			if (stop) return;

			refreshee.refresh();
			try {
			    sleep(refreshInterval);  
			} catch (Exception e) {
  			    ;
			}
			    
		    } // end of while ()
		}
	    };
	    
	    
	}
	refresher.addRefreshee(this).start();

	
    }
		
    /**
     * <code>setRefresher</code> sets the resher thread 
     * for this Device.
     * @param r an <code>ARefresher</code> value
     * @see fr.esrf.tangoatk.core.ARefresher
     */
    public void setRefresher(ARefresher r) {
	refresher = r;
    }

    public boolean isDevice(String name) {
	try {
	    getDevice(name);
	    return true;
	} catch (Exception e) {
	    return false;
	} // end of try-catch
    }
    
    public Device getDevice(String name) throws ConnectionException {
	Device d;

	d = (Device)devices.get(name);

	if (d == null /*|| dp.isAlive() */ ) { // need to request isAlive
         	                                  // from pascal.
	    log.info("Creating new Device(" + name + ")");
	    try {
		d = new Device(name);		 
	    } catch (DevFailed e) {
		throw new ConnectionException(e);
	    } // end of try-catch
	    

	    log.info("Ok...");
	    log.debug(d);
	    synchronized(devices) {
		devices.put(name, d);
		deviceList.add(d);
	    }
	    
	    log.info("Starting device-refresher");
	    startRefresher();
	    log.info("Ok...");
	} else {
	    log.info("Reusing DeviceProxy(" + name + ")");
	}
	
	return d;
    }

    public void refresh() {
	synchronized (deviceList) {
	    for (int i = 0; i < deviceList.size(); i++) {
		((Device)deviceList.get(i)).refresh();
	    } // end of for ()
	}
    }
	
    public void deleteDevice(String name) {
 	devices.remove(name);
    }

    public List getDeviceNames() {
	List l = new Vector();
	for (Iterator i = devices.keySet().iterator(); i.hasNext();) {
	    l.add((String)i.next());
	}
	return l;
    }

    public String getVersion() {
	return "$Id$";
    }

	    
}
