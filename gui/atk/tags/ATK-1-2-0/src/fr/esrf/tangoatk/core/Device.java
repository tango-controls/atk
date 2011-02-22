// File:          Device.java
// Created:       2001-09-24 13:14:14, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-18 15:35:52, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import java.util.*;
import java.io.*;
import fr.esrf.Tango.DevState;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.*;
import fr.esrf.tangoatk.core.util.AtkTimer;
import org.apache.log4j.Logger;


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
    implements IDevice, Serializable {

    transient static Logger log =
	ATKLogger.getLogger(Device.class.getName());

    transient private Logger deviceLogger ;
    transient private DbDevImportInfo info;
    private String name;
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
	propChanges = new EventSupport();
	this.name = name;
	deviceLogger = Logger.getLogger(getName().replace('/', '.'));
	Log4jConfigurator.addLogger(deviceLogger);
	info = import_device();
	timer = AtkTimer.getInstance();
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
 	String newState, newStatus;
 	newState = getState();
 	propChanges.fireStateEvent(this, newState);
 	newStatus = getStatus();
 	propChanges.fireStatusEvent(this, newStatus);
    }

    /**
     * <code>isAlive</code> checks to see if this device is reachable
     *
     * @return a <code>boolean</code> value which is true if the device is
     * reachable, false otherwise
     * @see fr.esrf.TangoApi.Connection#ping
     */
    public boolean isAlive() {
	try {
	    ping();
	    return true;
	} catch (DevFailed e) {
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
	return this.command_list_query();
    }

    public CommandInfo getCommand(String name) throws DevFailed {
 	return command_query(name);
    }
	
    /**
     * <code>getAttributeConfig</code> returns the attribute configs for 
     * the attributes named in the parameter.
     * @param name a <code>String[]</code> value containing the names of
     * the attributes we want the configs of.
     * @return an <code>AttributeConfig[]</code> value
     * @exception DevFailed if an error occurs
     */
    public AttributeInfo[] getAttributeInfo(String [] name)
	throws DevFailed {
	return get_attribute_config(name);
    }


    /**
     * <code>getAttributeConfig</code> gets the attribute config of an 
     * attribute
     * @param name a <code>String</code> value containing the name of the
     * attribute we want the config of.
     * @return an <code>AttributeConfig</code> value
     * @exception DevFailed if an error occurs
     * @see getAttributeConfig(String[] name)
     */
    public AttributeInfo getAttributeInfo(String name) throws DevFailed {
	return get_attribute_config(AEntityFactory.extractEntityName(name));
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
	log.error(s, t);
	propChanges.fireReadErrorEvent(this, t);
    }

    /**
     * <code>getState</code> returns the state of the device as a string.
     *
     * @return a <code>String</code> value containing the states.
     * @see fr.esrf.Tango.DevState for information on which states can
     * bee returned.
     */
    
    
    public String getState()
    {
	DevState current_state = null;
	try
	{
	    current_state = this.state();
	}
	catch (DevFailed dev)
	{
	    deviceError("Couldn't read state: ", new ConnectionException(dev));
	    return IDevice.UNKNOWN;
	}
	catch (Exception e)
	{
	    deviceError("Couldn't read state: ", e);
	    return IDevice.UNKNOWN;
	}
	return toString(current_state);	
    }


    public static String toString(DevState st)
    {
	switch (st.value())
	{
	case DevState._ON: return IDevice.ON;
	case DevState._OFF: return IDevice.OFF;
	case DevState._CLOSE: return IDevice.CLOSE;
	case DevState._OPEN: return IDevice.OPEN;
	case DevState._INSERT: return IDevice.INSERT;
	case DevState._EXTRACT: return IDevice.EXTRACT;
	case DevState._MOVING: return IDevice.MOVING;
	case DevState._STANDBY: return IDevice.STANDBY;
	case DevState._FAULT: return IDevice.FAULT;
	case DevState._INIT: return IDevice.INIT;
	case DevState._RUNNING: return IDevice.RUNNING;
	case DevState._ALARM: return IDevice.ALARM;
	case DevState._DISABLE: return IDevice.DISABLE;
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
	try
	{
	    return this.status();	     
	} 
	catch (Exception e)
	{
	    deviceError("Couldn't read status: ", new ConnectionException(e));
	    return IDevice.UNKNOWN;
	} 
    }

    
    /**
     * <code>quickReadAttribute</code> makes lots of assumptions and
     * optimisations to be really quick. Don't use it if you don't mean it :)
     * @param name a <code>String</code> value containing the attributename
     * to be read. The attribute name is non-qualified. It will call
     * readAttribute if the device is not connected, since readAttribute
     * does the Right Thing(TM).
     * @return a <code>DeviceAttribute</code> value
     * @exception DevFailed if an error occurs
     * @see readAttribute
     */
/*  commented out by F. Poncet because it seems to be unused
    public final DeviceAttribute quickReadAttribute(String nameSansDevice)
	throws DevFailed {
 	String [] names = {nameSansDevice};
 	try {
 	    return new DeviceAttribute(device.read_attributes(names)[0]);
	} catch (Exception e) {
	    return readAttribute(nameSansDevice);
	} 
    }
*/


/*  Commented out by F. Poncet to use readAttribute instead
    public final AttributeValue readAttributeValue(String nameSansDevice)
	throws DevFailed {

	return read_attribute_value(nameSansDevice);
    }
*/


    /**
     * <code>readAttribute</code> reads the value of an attribute. It is
     * made final to be a bit quicker.
     * @param name a <code>String</code> value
     * @return a <code>DeviceAttribute</code> value
     * @exception DevFailed if an error occurs
     */
    public final DeviceAttribute readAttribute(String name) throws DevFailed {
	DeviceAttribute da;
	da = read_attribute(AEntityFactory.extractEntityName(name));
	return da;
    }

    /**
     * <code>writeAttribute</code> writes an attribute.
     *
     * @param a a <code>DeviceAttribute</code> value to write.
     * @exception DevFailed if an error occurs
     */
    public void writeAttribute(DeviceAttribute a) throws DevFailed {
	write_attribute(a);
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
                                        throws DevFailed 
    {
	DeviceData data = command_inout(AEntityFactory.extractEntityName(command), argin);
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
	AttributeInfo [] ac = {c};
	set_attribute_config(ac);
    }


    public String toString() {
	return getName();
    }

    public String getVersion() {
	return "$Id$";
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
    
}

