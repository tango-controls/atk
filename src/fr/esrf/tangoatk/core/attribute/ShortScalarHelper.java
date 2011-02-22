// File:          ShortAttributeHelper.java
// Created:       2001-12-04 13:37:17, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-24 16:55:21, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;

import fr.esrf.TangoApi.*;

public class ShortScalarHelper extends ANumberScalarHelper {
    ShortImageHelper imageHelper;

    public ShortScalarHelper(IAttribute attribute) {
	init(attribute);
    }


    void init(IAttribute attribute) {
	super.init(attribute);
	spectrumHelper = new ShortSpectrumHelper(attribute);
	imageHelper = new ShortImageHelper(attribute);
    }

    
    void setMinAlarm(double d) {
	setProperty("min_alarm", new Short((short)d));
    }
    
    void setMaxAlarm(double d) {
	setProperty("max_alarm", new Short((short)d));
    }

    void setMinValue(double d) {
	setProperty("min_value", new Short((short)d));
    }
    
    void setMaxValue(double d) {
	setProperty("max_value", new Short((short)d));
    }

    void setMinAlarm(double d, boolean writable) {
	setProperty("min_alarm", new Short((short)d), writable);
    }
    
    void setMaxAlarm(double d, boolean writable) {
	setProperty("max_alarm", new Short((short)d), writable);
    }

    void setMinValue(double d, boolean writable) {
	setProperty("min_value", new Short((short)d), writable);

    }
    
    void setMaxValue(double d, boolean writable) {
	setProperty("max_value", new Short((short)d), writable);
    }

    void insert(double d) {
	attribute.getAttribute().insert((short)d);
    }

    double getNumberScalarValue(DeviceAttribute attribute) {
	return (attribute.extractShortArray())[0];	     
    }

    double getNumberScalarSetPoint(DeviceAttribute attribute)
    {
	short[]  short_arr;
	short_arr = attribute.extractShortArray();
	if (short_arr.length > 1)
	   return short_arr[1];
	else
	   return Double.NaN;
    }

    public String getVersion() {
	return "$Id$";
    }

}
