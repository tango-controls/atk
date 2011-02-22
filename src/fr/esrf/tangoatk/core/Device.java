// File:          Device.java
// Created:       2001-09-24 13:14:14, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-18 15:35:52, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core;

import java.io.*;

import fr.esrf.Tango.DevState;
import fr.esrf.Tango.DevSource;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.*;
import fr.esrf.TangoApi.events.*;
import fr.esrf.tangoatk.core.util.AtkTimer;


/**
 * An object that maps to a Tango Device. It is able to produce the
 * attributes and the commands belonging to this Device.
 * <p>
 * Normally a Device object is obtained through the
 * {@link  fr.esrf.tangoatk.core.DeviceFactory} to
 * make sure that we do not create two instances of the same physical device.
 * The device sends out three events, a Status-change event, a state-change
 * event, and an error event. Objects interested in receiving such events
 * must register as listeners.
 */
public class Device extends DeviceProxy
                    implements IDevice, Serializable,
		               ITangoPeriodicListener, ITangoChangeListener
{

  private String name;
  private boolean  supportsEvents;
  protected String alias;
  transient protected AtkTimer timer;
  protected EventSupport propChanges;
  transient protected DeviceProxy proxy;


  /**
   * Creates a new <code>Device</code> instance.
   *
   * @param name a <code>String</code> containing the name of the device
   * to connect to.
   * @exception DevFailed if an error occurs
   * @see fr.esrf.TangoApi.DeviceProxy#DeviceProxy
   */
  public Device(String name) throws DevFailed {
    super(name);
    init(name);
  }

  public Device(String name, String host, String port) throws DevFailed {
    super(name, host, port);
    init(name);
  }

  protected void init(String name) throws DevFailed {

    long t0 = System.currentTimeMillis();

    propChanges = new EventSupport();
    this.name = name;
    this.supportsEvents = false;
    timer = AtkTimer.getInstance();

    try
    {
      import_device();
      trace(DeviceFactory.TRACE_SUCCESS , "Device.import_device("+name+") ok" , t0);
    } 
    catch(DevFailed e)
    {
      trace(DeviceFactory.TRACE_FAIL , "Device.import_device("+name+") failed" , t0);
      throw e;
    }
    
    // Check if the device is "event compatible"
    
    try
    {
        int idlv = 0;
	idlv = get_idl_version();
	if (idlv >= 3) // all idl versions >= 3 are event compatible
           this.supportsEvents = true;
    }
    catch (DevFailed   dfe)
    {
        trace(DeviceFactory.TRACE_FAIL , "Device.get_idl_version("+name+") failed" , t0);
    }
    
    
    if (supportsEvents == false) // for older IDL versions check event compatibility in another manner
    {
	try
	{
	  String        admName = null;
	  DeviceProxy   admDev = null;

	  admName = adm_name();
	  trace(DeviceFactory.TRACE_SUCCESS , "Device.adm_name("+name+") ok" , t0);
	  if (admName != null)
	  {
             admDev = new DeviceProxy(admName);
             trace(DeviceFactory.TRACE_SUCCESS , "Device.newDeviceProxy("+admName+") ok" , t0);
	  }

	  if (admDev != null)
	  {
             try
	     {
		CommandInfo  cmdInfo=null;
		cmdInfo = admDev.command_query("EventSubscriptionChange");
        	trace(DeviceFactory.TRACE_SUCCESS , "Device.admDev.command_query(EventSubscriptionChange) ok" , t0);
		if (cmdInfo != null)
		{
		   this.supportsEvents = true;
        	   trace(DeviceFactory.TRACE_SUCCESS , "Device.supportsEvents("+name+") ok" , t0);
		}
	     }
	     catch (DevFailed    dfe)
	     {
        	trace(DeviceFactory.TRACE_FAIL , "Device.supportsEvents("+name+") failed" , t0);
	     }
	  }

	} 
	catch(DevFailed e)
	{
	  trace(DeviceFactory.TRACE_FAIL , "Device.supportsEvents("+name+") failed" , t0);
	}
    }
    
    
    // If the device is "event compatible" subscribe for state and status
    /* For the moment do not use events for state and status because they will
       only be available as attributes from Tango 5 (IDL 3 )
    if (supportsEvents == true)
    {
       TangoEventsAdapter   evtAdapt = null;
       String[]             filters = new String[0];
       
       
       
       try
       {
           evtAdapt = new TangoEventsAdapter(this);
       }
       catch (DevFailed ex)
       {
            trace(DeviceFactory.TRACE_FAIL , "Device.new TangoEventsAdapter( "+name+" ) failed" , t0);
	    supportsEvents = false;
       }
       
       
       if (evtAdapt != null)
       {
	    try
	    {
        	 evtAdapt.addTangoPeriodicListener(this, "state", filters);
        	 trace(DeviceFactory.TRACE_SUCCESS , "Device.addTangoPeriodicListener(state, "+name+" ) ok" , t0);
		 try
		 {
                     evtAdapt.addTangoPeriodicListener(this, "status", filters);
                     trace(DeviceFactory.TRACE_SUCCESS , "Device.addTangoPeriodicListener(status, "+name+" ) ok" , t0);
		 }
		 catch (DevFailed dfe)
		 {
        	     trace(DeviceFactory.TRACE_FAIL , "Device.addTangoPeriodicListener(status, "+name+") failed" , t0);
		     supportsEvents = false;
		     evtAdapt.removeTangoPeriodicListener(this, "state");
		 }
	    }
	    catch (DevFailed ex)
	    {
        	 trace(DeviceFactory.TRACE_FAIL , "Device.addTangoPeriodicListener(state, "+name+") failed" , t0);
		 supportsEvents = false;
	    }
       }
    }
    */

  }


  public void addErrorListener(IErrorListener l) {
    propChanges.addErrorListener(l);
  }

  public void removeErrorListener(IErrorListener l) {
    propChanges.removeErrorListener(l);
  }

  /**
   * <code>addStatusListener</code> adds a listener to status-events
   *
   * @param l an <code>IDeviceListener</code> value
   * @see fr.esrf.tangoatk.core.IDeviceListener
   */
  public void addStatusListener(IStatusListener l) {
    propChanges.addStatusListener(l);
  }

  /**
   * <code>removeStatusListener</code> removes a status listener
   *
   * @param l an <code>IDeviceListener</code> value
   */
  public void removeStatusListener(IStatusListener l) {
    propChanges.removeStatusListener(l);
  }

  /**
   * <code>addStateListener</code> adds a listener to state-events
   *
   * @param l an <code>IDeviceListener</code> value
   */
  public void addStateListener(IStateListener l) {
    propChanges.addStateListener(l);
  }

  /**
   * <code>removeStateListener</code> removes a listener to a state-events
   *
   * @param l an <code>IDeviceListener</code> value
   */
  public void removeStateListener(IStateListener l) {
    propChanges.removeStateListener(l);
  }

  /**
   * <code>addListener</code> adds a listener to all device events
   *
   * @param l an <code>IDeviceListener</code> value
   */
  public void addListener(IDeviceListener l) {
    propChanges.addStateListener(l);
    propChanges.addStatusListener(l);
  }

  /**
   * <code>removeListener</code> removes a listener to this device.
   *
   * @param l an <code>IDeviceListener</code> value
   */
  public void removeListener(IDeviceListener l) {
    propChanges.removeStateListener(l);
    propChanges.removeStatusListener(l);
  }

  /**
   * <code>refresh</code> sends out events if status or state has changed
   * since the last time it was called.
   */
  public void refresh() {

    DevState s = null;
    String newStatus;
    long t0 = System.currentTimeMillis();

    try {

      // Get the state
      try {

        s = state(ApiDefs.FROM_CMD);
        trace(DeviceFactory.TRACE_REFRESHER, "Device.refresh(State," + name + ") success", t0);
        propChanges.fireStateEvent(this, toString(s));

      } catch (DevFailed ex) {

        trace(DeviceFactory.TRACE_REFRESHER, "Device.refresh(State," + name + ") failed", t0);
        ConnectionException e = new ConnectionException(ex);
        deviceError("Couldn't read state: ", e);
        newStatus = getName() + ":\n" + e.getDescription();
        propChanges.fireStatusEvent(this, newStatus);
        return;

      }

      // Get the status
      try {

        newStatus = status(ApiDefs.FROM_CMD);
        trace(DeviceFactory.TRACE_REFRESHER, "Device.refresh(Status," + name + ") success",t0);
        propChanges.fireStatusEvent(this, newStatus);

      } catch (DevFailed ex) {

        trace(DeviceFactory.TRACE_REFRESHER, "Device.refresh(Status," + name + ") failed", t0);
        ConnectionException e = new ConnectionException(ex);
        newStatus = getName() + ":\n" + e.getDescription();
        propChanges.fireStatusEvent(this, newStatus);
        return;

      }

    } catch (Exception ex) {

      // Code failure
      System.out.println("-- Device.refresh() : Unexpected exception -----------------------");
      ex.printStackTrace();

      // Try to fire a deviceError event if execption has
      // happened if JavaApi.
      try {
        ConnectionException e = new ConnectionException(ex);
        propChanges.fireStateEvent(this, IDevice.UNKNOWN);
        propChanges.fireStatusEvent(this, IDevice.UNKNOWN);
        deviceError("Couldn't read state: ", e);
      } catch (Exception e) {
      }

    }

  }

  /**
   * <code>isAlive</code> checks to see if this device is reachable
   *
   * @return a <code>boolean</code> value which is true if the device is
   * reachable, false otherwise
   * @see fr.esrf.TangoApi.Connection#ping
   */
  public boolean isAlive() {

    long t0 = System.currentTimeMillis();

    try {
      ping();
      trace(DeviceFactory.TRACE_SUCCESS,"Device.ping(" + name + ") success",t0);
      return true;
    } catch (DevFailed e) {
      trace(DeviceFactory.TRACE_FAIL,"Device.ping(" + name + ") failed",t0);
      return false;
    }

  }


  /**
   * <code>getCommandList</code> returns the list of commands known to
   * this device
   * @return a <code>DevCmdInfo[]</code> value
   * @exception DevFailed if an error occurs
   */
  public CommandInfo[] getCommandList() throws DevFailed {

    long t0 = System.currentTimeMillis();
    CommandInfo[] ret;

    try {
      ret = command_list_query();
      trace(DeviceFactory.TRACE_SUCCESS,"Device.command_list_query(" + name + ") success",t0);
    } catch(DevFailed e) {
      trace(DeviceFactory.TRACE_FAIL,"Device.command_list_query(" + name + ") failed",t0);
      throw e;
    }

    return ret;
  }

  /**
   *  <code>getCommand</code> returns a CommandInfo corresponding to the given name.
   * @param name Command name
   * @return CommandInfo
   * @throws DevFailed in case of failure.
   */
  public CommandInfo getCommand(String name) throws DevFailed {
    CommandInfo ret;
    long t0 = System.currentTimeMillis();

    try {
      ret = command_query(name);
      trace(DeviceFactory.TRACE_SUCCESS,"Device.command_query(" + getName() + "/" + name + ") success",t0);
    } catch(DevFailed e) {
      trace(DeviceFactory.TRACE_FAIL,"Device.command_query(" + getName() + "/" + name + ") failed",t0);
      throw e;
    }

    return ret;
  }

  /**
   * <code>getAttributeInfo</code> returns the attribute configs for
   * the attributes named in the parameter.
   * @param name a <code>String[]</code> value containing the names of
   * the attributes we want the configs of.
   * @return an <code>AttributeConfig[]</code> value
   * @exception DevFailed if an error occurs
   */
  public AttributeInfo[] getAttributeInfo(String[] name)
    throws DevFailed {

    long t0 = System.currentTimeMillis();
    AttributeInfo[] ret;

    try {
      ret = get_attribute_info(name);
      trace(DeviceFactory.TRACE_SUCCESS,"Device.get_attribute_info(" + name + ") success",t0);
    } catch(DevFailed e) {
      trace(DeviceFactory.TRACE_FAIL,"Device.get_attribute_info(" + name + ") failed",t0);
      throw e;
    }

    return ret;
  }


  /**
   * <code>getAttributeInfo</code> gets the attribute config of an
   * attribute
   * @param name a <code>String</code> value containing the name of the
   * attribute we want the config of.
   * @return an <code>AttributeConfig</code> value
   * @exception DevFailed if an error occurs
   * @see #getAttributeInfo(String[] name)
   */
  public AttributeInfo getAttributeInfo(String name) throws DevFailed {
    AttributeInfo ret;
    long t0 = System.currentTimeMillis();

    try {
      ret = get_attribute_info(AEntityFactory.extractEntityName(name));
      trace(DeviceFactory.TRACE_SUCCESS,"Device.get_attribute_config(" + getName() + "/" + name + ") success",t0);
    } catch(DevFailed e) {
      trace(DeviceFactory.TRACE_FAIL,"Device.get_attribute_info("  + getName() + "/" + name + ") failed",t0);
      throw e;
    }

    return ret;

  }


  /**
   * <code>getName</code> returns the name of this device.
   *
   * @return a <code>String</code> value containing the name of the device.
   */
  public String getName() {
    return name;
  }


  /**
   * <code>getAlias</code> returns the alias of this device.
   *
   * @return a <code>String</code> value containing the alias of the device.
   */
  public String getAlias() {
    return alias;
  }


  /**
   * <code>setAlias</code> sets the alias of this device.
   *
   * @param alias  a <code>String</code> value containing the device alias.
   */
  public void setAlias(String alias) {
    this.alias = alias;
  }

  /**
   * <code>deviceError</code> fires of an error event from this device
   *
   * @param s a <code>String</code> value containing an error message.
   * @param t a <code>Throwable</code> value containing the exception
   * which caused this error message to be called.
   */
  protected void deviceError(String s, Throwable t) {
    propChanges.fireReadErrorEvent(this, t);
  }

  /**
   * <code>getState</code> returns the state of the device as a string.
   *
   * @return a <code>String</code> value containing the states.
   * @see fr.esrf.Tango.DevState for information on which states can
   * bee returned.
   */
  public String getState() {

    DevState ret = null;
    long t0 = System.currentTimeMillis();

    try {
      ret = state(ApiDefs.FROM_CMD);
      trace(DeviceFactory.TRACE_SUCCESS,"Device.state(" + name + ") success",t0);
    } catch (Exception e) {
      trace(DeviceFactory.TRACE_FAIL,"Device.state(" + name + ") failed",t0);
      deviceError("Couldn't read state: ", new ConnectionException(e));
    }

    return toString(ret);
  }


  public static String toString(DevState st) {

    if(st==null)
      return IDevice.UNKNOWN;

    switch (st.value()) {
      case DevState._ON:
        return IDevice.ON;
      case DevState._OFF:
        return IDevice.OFF;
      case DevState._CLOSE:
        return IDevice.CLOSE;
      case DevState._OPEN:
        return IDevice.OPEN;
      case DevState._INSERT:
        return IDevice.INSERT;
      case DevState._EXTRACT:
        return IDevice.EXTRACT;
      case DevState._MOVING:
        return IDevice.MOVING;
      case DevState._STANDBY:
        return IDevice.STANDBY;
      case DevState._FAULT:
        return IDevice.FAULT;
      case DevState._INIT:
        return IDevice.INIT;
      case DevState._RUNNING:
        return IDevice.RUNNING;
      case DevState._ALARM:
        return IDevice.ALARM;
      case DevState._DISABLE:
        return IDevice.DISABLE;
      default:
        return IDevice.UNKNOWN;
    } // end of switch ()

  }

  /**
   * <code>getStatus</code> returns the status of the device.
   * returns UNKNOWN and sets an error if a DevFailed is thrown while
   * obtaining the status.
   *
   * @return a <code>String</code> value containing the status.
   */
  public String getStatus() {

    String ret = IDevice.UNKNOWN;
    long t0 = System.currentTimeMillis();

    try {
      ret = status(ApiDefs.FROM_CMD);
      trace(DeviceFactory.TRACE_SUCCESS,"Device.status(" + name + ") success",t0);
    } catch (Exception e) {
      trace(DeviceFactory.TRACE_FAIL,"Device.status(" + name + ") failed",t0);
      deviceError("Couldn't read status: ", new ConnectionException(e));
    }

    return ret;
  }

  /**
   * <code>readAttribute</code> reads the value of an attribute. It is
   * made final to be a bit quicker.
   * @param name a <code>String</code> value
   * @return a <code>DeviceAttribute</code> value
   * @exception DevFailed if an error occurs
   */
  public final DeviceAttribute readAttribute(String name) throws DevFailed {
    DeviceAttribute da;
    long t0 = System.currentTimeMillis();

    try {
      da = read_attribute(AEntityFactory.extractEntityName(name));
      trace(DeviceFactory.TRACE_ATTREFRESHER,"Device.read_attribute(" + getName() + "/" + name + ") success",t0);
    } catch (DevFailed e) {
      trace(DeviceFactory.TRACE_ATTREFRESHER,"Device.read_attribute(" + getName() + "/" + name + ") failed",t0);
      throw e;
    }
    return da;
  }

  /**
   * <code>readAttributeFromDevice</code> reads the value of an attribute.
   * Force the reading from the tango device (ignore polling buffer).
   * @param name a <code>String</code> value
   * @return a <code>DeviceAttribute</code> value
   * @exception DevFailed if an error occurs
   */
  public final DeviceAttribute readAttributeFromDevice(String name) throws DevFailed {
    DeviceAttribute da;
    long t0 = System.currentTimeMillis();

    set_source(DevSource.DEV);

    try {
      da = read_attribute(AEntityFactory.extractEntityName(name));
      trace(DeviceFactory.TRACE_ATTREFRESHER,"Device.read_attribute_from_device(" + getName() + "/" + name + ") success",t0);
    } catch (DevFailed e) {
      trace(DeviceFactory.TRACE_ATTREFRESHER,"Device.read_attribute_from_device(" + getName() + "/" + name + ") failed",t0);
      // Reset to default
      set_source(DevSource.CACHE_DEV);
      throw e;
    }

    // Reset to default
    set_source(DevSource.CACHE_DEV);
    return da;
  }

  /**
   * <code>writeAttribute</code> writes an attribute.
   *
   * @param a a <code>DeviceAttribute</code> value to write.
   * @exception DevFailed if an error occurs
   */
  public void writeAttribute(DeviceAttribute a) throws DevFailed {

    long t0 = System.currentTimeMillis();

    try {
      write_attribute(a);
      trace(DeviceFactory.TRACE_ATTREFRESHER,"Device.write_attribute(" + name + ") success",t0);
    } catch(DevFailed e) {
      trace(DeviceFactory.TRACE_ATTREFRESHER,"Device.write_attribute(" + name + ") failed",t0);
      throw e;
    }

    refresh();

  }


  /**
   * <code>executeCommand</code>
   *
   * @param command a <code>String</code> value containing the name of the
   * command, obtained by getCommandList();
   * @param argin a <code>DeviceData</code> value holding the in argument
   * @return a <code>DeviceData</code> value containing the result
   * @exception DevFailed if an error occurs
   */
  public DeviceData executeCommand(String command, DeviceData argin)
    throws DevFailed {

    DeviceData data = null;
    long t0 = System.currentTimeMillis();

    try {
      data = command_inout(AEntityFactory.extractEntityName(command), argin);
      trace(DeviceFactory.TRACE_COMMAND,"Device.command_inout(" + getName() + "/" + name + ") success",t0);
    } catch(DevFailed e) {
      trace(DeviceFactory.TRACE_COMMAND,"Device.command_inout(" + getName() + "/" + name + ") failed",t0);
      throw e;
    }

    refresh();
    return data;
  }

  /**
   * <code>storeInfo</code>
   *
   * @param c an <code>AttributeInfo</code> value to store
   * @exception DevFailed if an error occurs
   */
  public void storeInfo(AttributeInfo c) throws DevFailed {
    AttributeInfo[] ac = {c};
    long t0 = System.currentTimeMillis();

    try {
      set_attribute_info(ac);
      trace(DeviceFactory.TRACE_ATTREFRESHER,"Device.set_attribute_info(" + name + ") success",t0);
    } catch(DevFailed e) {
      trace(DeviceFactory.TRACE_ATTREFRESHER,"Device.set_attribute_info(" + name + ") failed",t0);
      throw e;
    }
  }


  public String toString() {
    return getName();
  }
  
  
  public boolean doesEvent()
  {
      return supportsEvents;
  }

  public String getVersion() {
    return "$Id$";
  }

  private void trace(int level,String msg,long time) {
    DeviceFactory.getInstance().trace(level,msg,time);
  }

  private void writeObject(java.io.ObjectOutputStream out)
    throws IOException {
    System.out.print("Storing device " + name + "...");
    out.defaultWriteObject();
    System.out.println("Done");
  }

  private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
    System.out.print("Loading device ");
    in.defaultReadObject();
    System.out.print(name + "...");
    try {
      proxy = new DeviceProxy(name);
    } catch (Exception e) {
      throw new IOException(e.getMessage());
    } // end of try-catch
    System.out.println("Done");

  }
  
  
  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent e) 
  {
      DeviceAttribute     da=null;
      String              newStatus=null;
      long                t0 = System.currentTimeMillis();
      
      try
      {
          da = e.getValue();
          trace(DeviceFactory.TRACE_REFRESHER, "Device.periodicEvt.getValue(" + name + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
	      trace(DeviceFactory.TRACE_REFRESHER, "Device.periodicEvt.getValue(" + name + ") failed", t0);
              ConnectionException ce = new ConnectionException(dfe);
              deviceError("Device not responding : ", ce);
              newStatus = getName() + ":\n" + ce.getDescription();
              propChanges.fireStatusEvent(this, newStatus);
	  }
	  else
	  {
	      trace(DeviceFactory.TRACE_REFRESHER, "Device.periodicEvt.getValue(" + name + ") failed", t0);
              ConnectionException ce = new ConnectionException(dfe);
              deviceError("Unknown device state : ", ce);
              newStatus = getName() + ":\n" + ce.getDescription();
              propChanges.fireStatusEvent(this, newStatus);
	  }
          return;
      }
      
      if (da != null)
      {
	  if (da.getName().equals("state"))
	  {
              propChanges.fireStateEvent(this, IDevice.ON); // to be modified later when defined how
	                                                    // to extract DevState data type of attributes
	  }
	  else
             if (da.getName().equals("status"))
	     {
        	newStatus = null;
		try
		{
	            newStatus = da.extractString();
                    trace(DeviceFactory.TRACE_REFRESHER, "Device.periodicEvt(Status," + name + ") success", t0);
		    propChanges.fireStatusEvent(this, newStatus);
		}
		catch (DevFailed   dfe)
		{
        	    trace(DeviceFactory.TRACE_REFRESHER, "Device.periodicEvt(Status," + name + ") failed", t0);
        	    ConnectionException ce = new ConnectionException(dfe);
        	    newStatus = getName() + ":\n" + ce.getDescription();
        	    propChanges.fireStatusEvent(this, newStatus);
        	    return;
		}
	     }
      }
  }


  
  
  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent e) 
  {
      DeviceAttribute     da=null;
      String              newStatus=null;
      long                t0 = System.currentTimeMillis();
      
      try
      {
          da = e.getValue();
          trace(DeviceFactory.TRACE_REFRESHER, "Device.changeEvt.getValue(" + name + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
	      trace(DeviceFactory.TRACE_REFRESHER, "Device.changeEvt.getValue(" + name + ") failed", t0);
              ConnectionException ce = new ConnectionException(dfe);
              deviceError("Device not responding : ", ce);
              newStatus = getName() + ":\n" + ce.getDescription();
              propChanges.fireStatusEvent(this, newStatus);
	  }
	  else
	  {
	      trace(DeviceFactory.TRACE_REFRESHER, "Device.changeEvt.getValue(" + name + ") failed", t0);
              ConnectionException ce = new ConnectionException(dfe);
              deviceError("Unknown device state : ", ce);
              newStatus = getName() + ":\n" + ce.getDescription();
              propChanges.fireStatusEvent(this, newStatus);
	  }
          return;
      }
      
      if (da != null)
      {
	  if (da.getName().equals("state"))
	  {
              propChanges.fireStateEvent(this, IDevice.ON); // to be modified later when defined how
	                                                    // to extract DevState data type of attributes
	  }
	  else
             if (da.getName().equals("status"))
	     {
        	newStatus = null;
		try
		{
	            newStatus = da.extractString();
                    trace(DeviceFactory.TRACE_REFRESHER, "Device.changeEvt(Status," + name + ") success", t0);
		    propChanges.fireStatusEvent(this, newStatus);
		}
		catch (DevFailed   dfe)
		{
        	    trace(DeviceFactory.TRACE_REFRESHER, "Device.changeEvt(Status," + name + ") failed", t0);
        	    ConnectionException ce = new ConnectionException(dfe);
        	    newStatus = getName() + ":\n" + ce.getDescription();
        	    propChanges.fireStatusEvent(this, newStatus);
        	    return;
		}
	     }
      }
  }


}

