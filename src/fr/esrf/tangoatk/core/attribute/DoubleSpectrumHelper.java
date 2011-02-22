// File:          DoubleSpectrumHelper.java
// Created:       2002-01-24 09:55:13, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 16:56:42, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;
import org.apache.log4j.Logger;

public class DoubleSpectrumHelper extends ANumberSpectrumHelper {

    public DoubleSpectrumHelper(IAttribute attribute) {
	init(attribute);
    }

    void init(IAttribute attribute) {
	super.init(attribute);
	imageHelper = new DoubleImageHelper(attribute);
    }
    
    void insert(double[] d) {
	deviceAttribute.insert(d,
			       ((IAttribute)attribute).getXDimension(),
			       ((IAttribute)attribute).getYDimension());
    }


    double [] getNumberSpectrumValue(DeviceAttribute attribute) {
	return attribute.extractDoubleArray();
    }

    public String getVersion() {
	return "$Id$";
    }
    
}
