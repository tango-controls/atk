// File:          ShortSpectrumHelper.java
// Created:       2002-01-24 09:45:56, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 16:56:18, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;

public class ShortSpectrumHelper extends ANumberSpectrumHelper {

    public ShortSpectrumHelper(IAttribute attribute) {
	init(attribute);
    }

    void init(IAttribute attribute) {
	super.init(attribute);
	imageHelper = new ShortImageHelper(attribute);
    }

    protected void insert(double[] d) {
	short [] tmp = new short[d.length];
	for (int i = 0; i < tmp.length; i++) {
	    tmp[i] = new Double(d[i]).shortValue();
	}

	deviceAttribute.insert(tmp, attribute.getXDimension(),
			       attribute.getYDimension());
    }


    double [] getNumberSpectrumValue(DeviceAttribute deviceAttribute) {
	short [] tmp = deviceAttribute.extractShortArray();
	double[] retval = new double[tmp.length];
	for (int i = 0; i < tmp.length; i++) {
	    retval[i] = (double)tmp[i];
	} 
	return retval;
    }

    public String getVersion() {
	return "$Id$";
    }
}
