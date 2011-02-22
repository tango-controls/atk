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

    public void setValueFromString(String stringValue) {
        try {
            if (value instanceof Double) {
                Double tempVal = Double.valueOf(stringValue);
                setValue( tempVal );
            }
            else if (value instanceof Float) {
                Float tempVal = Float.valueOf(stringValue);
                setValue( tempVal );
            }
            else if (value instanceof Integer) {
                Integer tempVal = Integer.valueOf(stringValue);
                setValue( tempVal );
            }
            else if (value instanceof Long) {
                Long tempVal = Long.valueOf(stringValue);
                setValue( tempVal );
            }
            else if (value instanceof Short) {
                Short tempVal = Short.valueOf(stringValue);
                setValue( tempVal );
            }
            else {
                Byte tempVal = Byte.valueOf(stringValue);
                setValue( tempVal );
            }
        }
        catch (NumberFormatException nfe) {
            //nothing to do
        }
    }

}
