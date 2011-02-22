// File:          AbstractCommand.java
// Created:       2001-09-28 15:03:21, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-10 15:6:15, assum>
// 
// $Id$
// 
// Description:       


package fr.esrf.tangoatk.core.command;
import fr.esrf.tangoatk.core.*;
import fr.esrf.TangoDs.*;
import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * <code>ACommand</code> is like {@link fr.esrf.tangoatk.core.attribute.AAttribute} is for Attributes,
 * the mother of all commands. It holds all common behaviour of Commands,
 * and delegates the handeling of input and output to its 
 * {@link ACommandHelper} instances, the inputHelper and the outputHelper.
 *
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version 1.0
 */
public abstract class ACommand implements ICommand {
    protected ACommandHelper inputHelper, outputHelper;
    transient static Logger log =
	ATKLogger.getInstance(ACommand.class.getName());
    protected Map propertyMap;
    transient protected Logger commandLog;
    protected String name;
    protected String nameSansDevice;
    transient private CommandInfo info;
    private fr.esrf.tangoatk.core.Device device;
    protected String error;
    protected List oldResult;
    protected EventSupport propChanges;
    protected Throwable oldt;
    protected String alias;

    private static String VERSION = "$Id$";

    public String getVersion() {
	return VERSION;
    }
    
   
    /**
     * <code>init</code> initializes the Command, 
     *
     * @param d the <code>Device</code> this command is connected to
     * @param name a <code>String</code> value holding the name of this
     * command.
     * @param info a <code>DevCmdInfo</code> value holding the
     * {@link fr.esrf.Tango.DevCmdInfo} for this command
     */
    protected void init(fr.esrf.tangoatk.core.Device d,
			String name, CommandInfo info) {
	device = d;
	propertyMap = new HashMap();
	setInfo(info);
	this.name = name;
	commandLog = ATKLogger.getLogger(getName().replace('/', '.'));

	propChanges = new EventSupport();
    }

    /**
     * <code>getDevice</code>
     *
     * @return the <code>Device</code> of this command
     */
    public fr.esrf.tangoatk.core.Device getDevice() {
	return device;
    }

    /**
     * <code>cmdError</code> sends off an error event to all the 
     * errorlisteners of this command.
     *
     * @param message a <code>String</code> value
     * @param t a <code>Throwable</code> value
     */
    protected void cmdError(String message, Throwable t) {
	propChanges.fireReadErrorEvent(this, t);
	log.error(message, t);
	oldt = t;

    }

    public void storeConfig() {
    }
    
    /**
     * <code>getProperty</code> returns the CommandProperty with the 
     * given name if such a beast exists. Null otherwise
     * @param name a <code>String</code> value
     * @return a <code>Property</code> value
     */
    public Property getProperty(String name) {
	return (Property)propertyMap.get(name);
    }


    /**
     * <code>getInType</code> returns the intype of this command.
     * @see fr.esrf.TangoDs.TangoConst
     * @return an <code>int</code> value containing the in_type.
     * @throws NoSuchElementException if no such property exists.
     */
    int getInType() {
	return getNumericProperty("in_type");
    }
    

    protected String getTypeName(int type) {

	switch (type) {
	   case	Tango_DEV_BOOLEAN:
	       return "boolean";
	   case	Tango_DEV_SHORT:
	       return "short";
 	   case	Tango_DEV_FLOAT:
	       return "float";
 	   case	Tango_DEV_DOUBLE:
	       return "double";
	   case	Tango_DEV_USHORT:
	       return "ushort";
	   case	Tango_DEV_ULONG:
	       return "ulong";
	   case	Tango_DEV_LONG:
	       return "long";
	   case	Tango_DEV_STRING:
	       return "string";
           case Tango_DEV_STATE:
	        return "scalar";
	   case	Tango_DEVVAR_LONGSTRINGARRAY:
	       return "long string array";
 	   case	Tango_DEVVAR_DOUBLESTRINGARRAY:		
	        return "double string array";
	   case	Tango_DEVVAR_SHORTARRAY:
	        return "short array"; 		
 	   case	Tango_DEVVAR_FLOATARRAY:		
	        return "float array"; 		
 	   case	Tango_DEVVAR_DOUBLEARRAY:		
	        return "double double"; 		
	   case	Tango_DEVVAR_USHORTARRAY:		
	        return "unsigned short array"; 		
	   case	Tango_DEVVAR_ULONGARRAY:
	        return "unsigned long array"; 		
	   case	Tango_DEVVAR_LONGARRAY:
	        return "long array"; 		
	   case	Tango_DEVVAR_CHARARRAY:		
	        return "char array"; 		
	   case	Tango_DEVVAR_STRINGARRAY:
	        return "string array"; 		
	   default:
	       return " ";
	}
    }
    




    /**
     * <code>getInTypeElemName</code> returns the type name of an element of the array 
     * if this command has an "array" input argument.
     * @see fr.esrf.TangoDs.TangoConst
     * @return an <code>int</code> value containing the in_type.
     * @throws NoSuchElementException if no such property exists.
     */
    public String getInTypeElemName()
    {
	return getTypeName(getInType());
    }


    public String getOutTypeElemName() {
	return getTypeName(getOutType());
    }

    public String getTag() {
	return getStringProperty("tag");
    }

    public String getLevel() {
	return getStringProperty("level");
    }

    /**
     * <code>getNumericProperty</code>
     *
     * @param name a <code>String</code> value
     * @return an <code>int</code> value containing the value of the property
     * @throws NoSuchElementException if no such property exists
     */
    int getNumericProperty(String name) {
	try {
	    return getProperty(name).getIntValue();	     
	} catch (NullPointerException e) {
	    throw new NoSuchElementException(name +
					     " is not a known property");
	} 
    }

    /**
     * <code>getOutType</code> returns the outtype of this command
     * @see fr.esrf.TangoDs.TangoConst
     * @return an <code>int</code> value
     * @throws NoSuchElementException if no such property exists
     */
    int getOutType() {
	return getNumericProperty("out_type");
    }

    /**
     * <code>getStringProperty</code>
     *
     * @param name a <code>String</code> value
     * @return a <code>String</code> value containing the value of the
     *         property
     * @throws NoSuchElementException if no such property exists
     */
    String getStringProperty(String name) {
	try {
	    return getProperty(name).getStringValue();	     
	} catch (NullPointerException e) {
	    throw new NoSuchElementException(name +
					     " is not a known property");
	} 
    }

    /**
     * <code>getInTypeDescription</code> returns the description of the
     * in type
     * @return a <code>String</code> value holding the description
     */
    public String getInTypeDescription() {
	return getStringProperty("in_type_desc");
    }

    /**
     * <code>getOutTypeDescription</code> returns the description of the
     * out type
     * @return a <code>String</code> value
     */
    public String getOutTypeDescription() {
	return getStringProperty("out_type_desc");
    }


    void setInTypeName(String s) {
	setProperty("in_type_name", s);
    }

    public Map getPropertyMap() {
	return propertyMap;
    }
    
    void setOutTypeName(String s) {
	setProperty("out_type_name", s);
    }

    void setProperty(String name, String value) {
	Property p = (Property)propertyMap.get(name);
	if (p == null) 
	    propertyMap.put(name, new StringProperty(this,
						     name,
						     value,
						     false));
	else 
	    p.setValue(value);
    }
    
    void setProperty(String name, int value) {
	Property p = (Property)propertyMap.get(name);
	if (p == null) 
	    propertyMap.put(name,
			    new TypeProperty(this,
					     name,
					     new Integer(value),
					     false));
	else 
	    p.setValue(new Integer(value));
    }

    protected void setProperty(String name, fr.esrf.Tango.DispLevel value) {
	Property p = (Property)propertyMap.get(name);
	if (p == null) {
	    propertyMap.put(name, new DisplayLevelProperty(this,
							   name,
							   value,
							   false));
	} else {
	    p.setValue(value);
	} // end of else
    }

    void setInfo(CommandInfo info) {
	this.info = info;
	nameSansDevice = info.cmd_name;
	name        = device.getName() + "/" + info.cmd_name;
	setProperty("name", name);
	setProperty("tag", info.cmd_tag);
	setProperty("in_type", info.in_type);
	setProperty("out_type", info.out_type);
	setProperty("in_type_desc", info.in_type_desc);
	setProperty("out_type_desc", info.out_type_desc);

	setProperty("level", info.level);    
	
    }

    public void refresh() {

    }
    

    public String getAlias() {
	return alias;
    }

    public void setAlias(String alias) {
	this.alias = alias;
    }



    public String getName() {
	return getProperty("name").getStringValue();
    }

    public String getNameSansDevice() {
	return nameSansDevice;
    }

    public String toString() {
	return nameSansDevice;
    }

    public void addErrorListener (IErrorListener l) {
	propChanges.addErrorListener(l);
    }

    public void removeErrorListener(IErrorListener l) {
	propChanges.removeErrorListener(l);
    }

    public boolean takesInput() {
	return !isVoid(getInType());
    }

    public boolean takesTableInput() {
	return isTable(getInType());
    }
    public boolean takesArrayInput() {
	return isArray(getInType());
    }

    public boolean takesScalarInput() {
	return isScalar(getInType());
    }

    public boolean doesOutput() {
	return !isVoid(getOutType());
    }

    public boolean doesArrayOutput() {
	return isArray(getOutType());
    }


    public static boolean isVoid(int type) {
	return type == Tango_DEV_VOID;
    }

    
    public static boolean isScalar(int type) {
	switch(type) {
	case	Tango_DEV_BOOLEAN:		
	case	Tango_DEV_SHORT: 		
 	case	Tango_DEV_FLOAT:		
 	case	Tango_DEV_DOUBLE:		
	case	Tango_DEV_USHORT:		
	case	Tango_DEV_ULONG:
	case	Tango_DEV_LONG:			    
	case	Tango_DEV_STRING:
        case    Tango_DEV_STATE:
	    return true;
	default:
	    return false;
	}
    }

    public static boolean isArray(int type) {
	switch(type) {
	case	Tango_DEVVAR_SHORTARRAY: 		
 	case	Tango_DEVVAR_FLOATARRAY:		
 	case	Tango_DEVVAR_DOUBLEARRAY:		
	case	Tango_DEVVAR_USHORTARRAY:		
	case	Tango_DEVVAR_ULONGARRAY:
	case	Tango_DEVVAR_LONGARRAY:
	case	Tango_DEVVAR_CHARARRAY:		
	case	Tango_DEVVAR_STRINGARRAY:
	    return true;
	default:
	    return false;
	}
    }
    
    public static boolean isTable(int type) {
	switch(type) {
	case	Tango_DEVVAR_LONGSTRINGARRAY:
 	case	Tango_DEVVAR_DOUBLESTRINGARRAY:		
	    return true;
	default:
	    return false;
	}
    }
    

    void setDevice(fr.esrf.tangoatk.core.Device d) {
	device = d;
    }

    void setName(String s) {
	setProperty("name", s);
	name = s;
    }
    
    public void execute() {
	execute(null);
    }

    public void addResultListener(IResultListener l) {
	propChanges.addResultListener(l);
    }

    public void removeResultListener(IResultListener l) {
	propChanges.removeResultListener(l);
    }

    protected void publishResult(List result) {
	propChanges.fireResultEvent(this, result);
    }

    public void execute(List l) {
	try {
	    publishResult
		(outputHelper.extractOutput
		 (getDevice().executeCommand(getName(),
					     inputHelper.setInput(l))));
	} catch (DevFailed devfailed) {
	    cmdError("execute(" + getName() + " failed", new
		     CommandExecuteException(devfailed));
	} catch (Exception e) {
	    e.printStackTrace();
	    String message = "execute(" + getName() + " failed: " +
		e + ")";
	    cmdError(message, new CommandExecuteException(message));
	    
	} // end of catch
	
	
    }
}


