// File:          AttributeWritableProperty.java
// Created:       2001-11-23 15:23:28, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-04-29 15:3:29, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;
import fr.esrf.Tango.*;
public class AttributeFormatProperty extends AttributeProperty {

    public AttributeFormatProperty(IAttribute parent, String name,
			     AttrDataFormat value, boolean editable) {
	super(parent, name, value, editable);
    }

    public String getPresentation() {
	AttrDataFormat wt = (AttrDataFormat)value;
	if (wt == AttrDataFormat.SCALAR) return "SCALAR";
	if (wt == AttrDataFormat.SPECTRUM) return "SPECTUM";
	if (wt == AttrDataFormat.IMAGE) return "IMAGE";

	return "";

    }

    public String getVersion() {
	return "$Id$";
    }

}
