// File:          StringProperty.java
// Created:       2001-11-23 15:22:14, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-10 15:6:3, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

public class StringProperty extends Property {
    protected String internal;
    public StringProperty(IEntity parent, String name,
			     String value, boolean editable) {
	super(parent, name, value, editable);
	internal = value;
	
    }

    public void setValue(String s) {
	internal = s;
	super.setValue(s);
    }

    public String getStringValue() {

	return internal;
    }
    
    public String getVersion() {
	return "$Id$";
    }

}
