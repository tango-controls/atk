// File:          ScalarCommandHelper.java
// Created:       2001-12-21 13:38:42, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-24 18:17:2, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.command;
import fr.esrf.tangoatk.core.Device;
import fr.esrf.TangoApi.DeviceData;
import fr.esrf.Tango.DevState;
import java.util.*;

class ScalarCommandHelper extends ACommandHelper {

    ScalarCommandHelper(ACommand command) {
	super(command);
    }
    
    public DeviceData setInput(List l) {
	String input = (String)l.get(0);
	try {
	    switch (getInType()) {
	    case	Tango_DEV_BOOLEAN:		
		data.insert(new Boolean(input).booleanValue());		
		break;				
	    case	Tango_DEV_SHORT: 		
		data.insert(Short.parseShort(input));		
		break;				
	    case	Tango_DEV_FLOAT:		
		data.insert(Float.parseFloat(input));		
		break;				
	    case	Tango_DEV_DOUBLE:		
		data.insert(Double.parseDouble(input));		
		break;				
	    case	Tango_DEV_USHORT:		
		data.insert_u(Short.parseShort(input));
		break;				
	    case	Tango_DEV_ULONG:		
		data.insert_u(Integer.parseInt(input));		
		break;				
	    case	Tango_DEV_LONG:		
		data.insert(Integer.parseInt(input));		
		break;				
	    case	Tango_DEV_STRING:		
		data.insert(input);		
		break;
	    }
		     
	} catch (Exception  e) {
	    setError("setInput failed with "  + e + " on " + input,
			     e);
	    
	} // end of try-catch
	

	return data;
    }

    protected List extractOutput(DeviceData d) {
	String val = "unsuported type";
	List l = new Vector();
	switch (getOutType()) {
	case	Tango_DEV_BOOLEAN:		
	    val = d.extractBoolean() ? "TRUE" : "FALSE";		
	    break;				
	case	Tango_DEV_SHORT: 		
	    val = Short.toString(d.extractShort());		
	    break;				
 	case	Tango_DEV_FLOAT:		
	    val = Float.toString(d.extractFloat());		
	    break;				
 	case	Tango_DEV_DOUBLE:		
	    val = Double.toString(d.extractDouble());		
	    break;				
	case	Tango_DEV_USHORT:		
	    val = Short.toString(d.extractUShort());		
	    break;
	case	Tango_DEV_ULONG:		
	    val = Integer.toString(d.extractULong());		
	    break;				
	case	Tango_DEV_LONG:		
	    val = Integer.toString(d.extractLong());		
	    break;				
	case	Tango_DEV_STRING:		
	    val = d.extractString();		
	    break;
	case	Tango_DEV_STATE:		
	    val = Device.toString(d.extractDevState());
	    break;
	}
	l.add(val);
	return l;
    }

    private void readObject(java.io.ObjectInputStream in)
	throws java.io.IOException, ClassNotFoundException {
	in.defaultReadObject();
	try {
	    serializeInit();
	} catch (Exception e) {
	    throw new java.io.IOException(e.getMessage());
	}
    }

    public String toString() {
	return "ScalarCommandHelper";
    }
    
    public String getVersion() {
	return "$Id$";
    }

}
