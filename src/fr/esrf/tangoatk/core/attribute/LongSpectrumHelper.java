// File:          LongSpectrumHelper.java
// Created:       2002-01-24 10:02:46, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 16:56:32, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;

public class LongSpectrumHelper extends ANumberSpectrumHelper {

    public LongSpectrumHelper(IAttribute attribute) {
	init(attribute);
    }

    void init(IAttribute attribute) {
	super.init(attribute);
	imageHelper = new LongImageHelper(attribute);
    }

    protected void insert(double[] d) {
	int [] tmp = new int[d.length];
	for (int i = 0; i < tmp.length; i++) {
	    tmp[i] = new Double(d[i]).intValue();
	}

	deviceAttribute.insert(tmp,
			       ((IAttribute)attribute).getXDimension(),
			       ((IAttribute)attribute).getYDimension());
    }


    double [] getNumberSpectrumValue(DeviceAttribute deviceAttribute) {
	int [] tmp = deviceAttribute.extractLongArray();
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
