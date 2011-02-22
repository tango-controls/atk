// File:          StringAttributeHelper.java
// Created:       2001-12-04 14:39:50, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-10 17:31:19, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;


import fr.esrf.TangoApi.*;
class StringAttributeHelper extends ANumberScalarHelper {

    StringAttributeHelper(IAttribute attribute) {
	setAttribute(attribute);
    }

    void setMinAlarm(double d, boolean writable) {
	setProperty("min_alarm", new Double(Double.NaN), writable);
    }
    
    void setMaxAlarm(double d, boolean writable) {
	setProperty("max_alarm", new Double(Double.NaN), writable);
    }

    void setMinValue(double d, boolean writable) {
	setProperty("min_value", new Double(Double.NaN), writable);
    }
    
    void setMaxValue(double d, boolean writable) {
	setProperty("max_value", new Double(Double.NaN), writable);
    }

    void setMinAlarm(double d) {
	setProperty("min_alarm", new Double(Double.NaN));
    }
    
    void setMaxAlarm(double d) {
	setProperty("max_alarm", new Double(Double.NaN));
    }

    void setMinValue(double d) {
	setProperty("min_value", new Double(Double.NaN));
    }
    
    void setMaxValue(double d) {
	setProperty("max_value", new Double(Double.NaN));
    }

    void insert(double d) {
	attribute.getAttribute().insert(new Double(d).toString());
    }

    double getNumberScalarValue(DeviceAttribute attribute) {
	return Double.NaN;
    }

    public String getVersion() {
	return "$Id$";
    }
}
