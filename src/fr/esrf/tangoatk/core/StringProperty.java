// File:          StringProperty.java
// Created:       2001-11-23 15:22:14, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-05-17 11:27:51, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

public class StringProperty extends Property {

    public StringProperty(IEntity parent, String name,
			     String value, boolean editable) {
	super(parent, name, value, editable);
    }

    public void setValue(String s) {
	super.setValue(s);
    }

    public String getVersion() {
	return "$Id$";
    }

}
