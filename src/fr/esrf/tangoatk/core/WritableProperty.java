// File:          AttributeWritableProperty.java
// Created:       2001-11-23 15:23:28, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-05-17 11:21:2, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;
import fr.esrf.Tango.*;
public class WritableProperty extends Property {

    public WritableProperty(IAttribute parent, String name,
				    AttrWriteType value, boolean editable) {
	super(parent, name, value, editable);
    }

    public String getPresentation() {
	String tmp = "";
	AttrWriteType wt = (AttrWriteType)value;
	if (wt == AttrWriteType.READ) tmp = "READ";
	if (wt == AttrWriteType.READ_WITH_WRITE) tmp = "READ_WITH_WRITE";
	if (wt == AttrWriteType.WRITE) tmp = "WRITE";
	if (wt == AttrWriteType.READ_WRITE) tmp = "READ_WRITE";
	
	return tmp;

    }

    public String getVersion() {
	return "$Id$";
    }
    
}
