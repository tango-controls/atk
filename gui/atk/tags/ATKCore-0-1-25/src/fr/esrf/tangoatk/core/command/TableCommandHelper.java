// File:          TableCommandHelper.java
// Created:       2002-06-24 15:27:18, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:18:39, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.command;

import fr.esrf.TangoApi.DeviceData;
import fr.esrf.Tango.DevState;
import fr.esrf.Tango.*;

import java.util.*;

class TableCommandHelper extends ACommandHelper {

    TableCommandHelper(ACommand command) {
	super(command);
    }

    protected DeviceData setInput(List l) {
	return setInput((List)l.get(0), (List)l.get(1));
    }

    protected DeviceData setInput(List numerics, List strings) {
	int length = strings.size();
	int i;

	String [] str = new String[length];

	for (i = 0; i < length; i++) {
	    str[i] = (String)strings.get(i);
	} // end of for ()

	length = numerics.size();

	switch (getInType()) {

	case Tango_DEVVAR_LONGSTRINGARRAY:
	    int [] ints = new int[length];

	    for (i = 0; i < length; i++) {
		ints[i] = Integer.parseInt((String)numerics.get(i));
	    } // end of for ()

	    data.insert(new DevVarLongStringArray(ints, str));
	    break;

	case Tango_DEVVAR_DOUBLESTRINGARRAY:
	    double [] doubles = new double[length];

	    for (i = 0; i < length; i++) {
		doubles[i] = Double.parseDouble((String)numerics.get(i));
	    } // end of for ()

	    data.insert(new DevVarDoubleStringArray(doubles, str));
	    break;

	default:
	    
	    break;
	} // end of switch ()
	return data;
    }

    protected List extractOutput(DeviceData d) {
	List strings = new Vector();
	List numerics = new Vector();
	List res = new Vector();
	
	switch (getOutType()) {
	case Tango_DEVVAR_LONGSTRINGARRAY:
	    DevVarLongStringArray lsa = d.extractLongStringArray();
	    for (int i = 0; i < lsa.lvalue.length; i++) {
		numerics.add(new Integer(lsa.lvalue[i]));
	    } // end of for ()


	    strings = Arrays.asList(lsa.svalue);
	    break;

	case Tango_DEVVAR_DOUBLESTRINGARRAY:
	    DevVarDoubleStringArray dsa = d.extractDoubleStringArray();
	    for (int i = 0; i < dsa.dvalue.length; i++) {
		numerics.add(new Double(dsa.dvalue[i]));
	    } // end of for ()
	    
	    strings = Arrays.asList(dsa.svalue);
	    break;
	default:
	    
	    break;
	} // end of switch ()

	res.add(numerics);
	res.add(strings);
	return res;
    }
}
