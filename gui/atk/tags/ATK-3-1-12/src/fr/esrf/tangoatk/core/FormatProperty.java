
// File:          WritableProperty.java
// Created:       2001-11-23 15:23:28, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-05-17 11:20:29, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;
import fr.esrf.Tango.*;
public class FormatProperty extends Property {

    public FormatProperty(IAttribute parent, String name,
			     AttrDataFormat value, boolean editable) {
	super(parent, name, value, editable);
    }

    public String getPresentation() {
	AttrDataFormat wt = (AttrDataFormat)value;
	if (wt == AttrDataFormat.SCALAR) return "SCALAR";
	if (wt == AttrDataFormat.SPECTRUM) return "SPECTRUM";
	if (wt == AttrDataFormat.IMAGE) return "IMAGE";

	return "";

    }

    public String getVersion() {
	return "$Id$";
    }

    public void setValueFromString(String stringValue) {
        if ( "SCALAR".equalsIgnoreCase(stringValue.trim()) ) {
            setValue(AttrDataFormat.SCALAR);
        }
        else if ( "SPECTRUM".equalsIgnoreCase(stringValue.trim()) ) {
            setValue(AttrDataFormat.SPECTRUM);
        }
        else if ( "IMAGE".equalsIgnoreCase(stringValue.trim()) ) {
            setValue(AttrDataFormat.IMAGE);
        }
    }

}
