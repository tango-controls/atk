// File:          AttributeIntegerProperty.java
// Created:       2001-11-23 15:18:17, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-05-17 11:26:24, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;


public class NumberProperty extends Property {

    public NumberProperty(IEntity parent, String name,
			     Number value, boolean editable) {
	super(parent, name, value, editable);
    }

    public void setValue(Number n) {
	super.setValue(n);
    }

    public String getVersion() {
	return "$Id$";
    }
    
}
