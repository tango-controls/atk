// File:          LongImageHelper.java
// Created:       2002-01-24 10:12:49, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-16 10:32:15, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;

class LongImageHelper extends ANumberImageHelper {

    public LongImageHelper(IAttribute attribute) {
	init(attribute);
    }

    void insert(double[] d) {
	int [] tmp = new int[d.length];
	for (int i = 0; i < tmp.length; i++) {
	    tmp[i] = new Double(d[i]).intValue();
	}

	deviceAttribute.insert(tmp, attribute.getXDimension(),
			       attribute.getYDimension());
    }


    double [][] getNumberImageValue(DeviceAttribute deviceAttribute) {

	int ydim = attribute.getYDimension();
	int xdim = attribute.getXDimension();

	if (ydim != retval.length || xdim != retval[0].length) {
	    retval = new double[ydim][xdim];	    
	}

	int [] tmp = deviceAttribute.extractLongArray();

	int k = 0;
	for (int i = 0; i < ydim; i++)
  	    for (int j = 0; j < xdim; j++) {
  		retval[i][j] = tmp[k++];
  	    }
	return retval;
    }

    String [][] getImageValue(DeviceAttribute deviceAttribute) {
	int [] tmp = deviceAttribute.extractLongArray();
	int ydim = attribute.getYDimension();
	int xdim = attribute.getXDimension();
	String [][] retval = new String[ydim][xdim];
	
	int k = 0;
	for (int i = 0; i < ydim; i++)
  	    for (int j = 0; j < xdim; j++) {
  		retval[i][j] = Integer.toString(tmp[k++]);
  	    }
	return retval;
    }

    public String getVersion() {
	return "$Id$";
    }
}
