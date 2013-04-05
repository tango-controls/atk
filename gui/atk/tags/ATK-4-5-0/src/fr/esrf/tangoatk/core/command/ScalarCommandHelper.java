/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.command;
import fr.esrf.tangoatk.core.Device;
import fr.esrf.TangoApi.DeviceData;
import java.util.*;

class ScalarCommandHelper extends ACommandHelper {

    ScalarCommandHelper(ACommand command) {
	super(command);
    }
    
    public DeviceData setInput(List l) {
        String   input;
        if (l == null)
	   input = null;
	else
	   if (l.size() < 1)
	      input = null;
	   else
	      input = (String)l.get(0);
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
		data.insert_us(Integer.parseInt(input));
		break;				
	    case	Tango_DEV_ULONG:		
		data.insert_ul(Long.parseLong(input));		
		break;				
	    case	Tango_DEV_LONG:		
		data.insert(Integer.parseInt(input));		
		break;				
	    case	Tango_DEV_STRING:		
		data.insert(input);		
		break;
	    }
		     
	} catch (Exception  e) {
	    cmdError("setInput failed with "  + e + " on " + input,
			     e);
	    
	} // end of try-catch
	

	return data;
    }

    protected List<String> extractOutput(DeviceData d) {
	String val = "unsuported type";
	List<String> l = new Vector<String> ();
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
	    val = Integer.toString(d.extractUShort());
	    break;
	case	Tango_DEV_ULONG:		
	    val = Long.toString(d.extractULong());
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
