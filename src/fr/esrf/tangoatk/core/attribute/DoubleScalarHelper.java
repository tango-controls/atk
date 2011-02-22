// File:          DoubleAttributeHelper.java
// Created:       2001-12-04 13:33:15, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-10 17:31:31, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;

import fr.esrf.TangoApi.*;

public class DoubleScalarHelper extends ANumberScalarHelper {

    public DoubleScalarHelper(IAttribute attribute) {
	init(attribute);
    }

    void init(IAttribute attribute) {
	super.init(attribute);
	spectrumHelper = new DoubleSpectrumHelper(attribute);
	imageHelper = new DoubleImageHelper(attribute);
    }

    void setMinAlarm(double d, boolean writable) {
	setProperty("min_alarm", new Double(d), writable);
    }
    
    void setMaxAlarm(double d, boolean writable) {
	setProperty("max_alarm", new Double(d), writable);
    }

    void setMinValue(double d, boolean writable) {
	setProperty("min_value", new Double(d), writable);
    }
    
    void setMaxValue(double d, boolean writable) {
	setProperty("max_value", new Double(d), writable);
    }

    void setMinAlarm(double d) {
	setProperty("min_alarm", new Double(d));
    }
    
    void setMaxAlarm(double d) {
	setProperty("max_alarm", new Double(d));
    }

    void setMinValue(double d) {
	setProperty("min_value", new Double(d));
    }
    
    void setMaxValue(double d) {
	setProperty("max_value", new Double(d));
    }

    void  insert(double d) {
	attribute.getAttribute().insert(d);
    }

    final double getNumberScalarValue(DeviceAttribute attribute) {
	return (attribute.extractDoubleArray())[0];
    }

    public String getVersion() {
	return "$Id$";
    }
    
}
