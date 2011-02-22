// File:          LongScalarHelper.java
// Created:       2001-12-04 13:39:40, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-24 16:55:29, assum>
// 
// $Id$
// 
// Description:       


package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;
import fr.esrf.TangoApi.*;

public class LongScalarHelper extends ANumberScalarHelper {

    public LongScalarHelper(IAttribute attribute) {
	init(attribute);
    }

    void init(IAttribute attribute) {
	super.init(attribute);
	spectrumHelper = new LongSpectrumHelper(attribute);
	imageHelper = new LongImageHelper(attribute);
    }

    void setMinAlarm(double d) {
	setProperty("min_alarm", new Long((long)d));
    }
    
    void setMaxAlarm(double d) {
	setProperty("max_alarm", new Long((long)d));
    }

    void setMinValue(double d) {
	setProperty("min_value", new Long((long)d));
    }
    
    void setMaxValue(double d) {
	setProperty("max_value", new Long((long)d));
    }

    void setMinAlarm(double d, boolean writable) {
	setProperty("min_alarm", new Long((long)d), writable);
    }
    
    void setMaxAlarm(double d, boolean writable) {
	setProperty("max_alarm", new Long((long)d), writable);
    }

    void setMinValue(double d, boolean writable) {
	setProperty("min_value", new Long((long)d), writable);
    }
    
    void setMaxValue(double d, boolean writable) {
	setProperty("max_value", new Long((long)d), writable);
    }

    void insert(double d) {
	attribute.getAttribute().insert((int)d);
    }

    double getNumberScalarValue(DeviceAttribute attribute) {
	return (attribute.extractLongArray())[0];
    }

    public String getVersion() {
	return "$Id$";
    }
}
