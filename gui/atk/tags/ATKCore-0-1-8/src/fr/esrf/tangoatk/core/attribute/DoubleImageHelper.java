// File:          DoubleImageHelper.java
// Created:       2002-01-24 10:13:21, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 16:57:20, assum>
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

public class DoubleImageHelper extends ANumberImageHelper {

    public DoubleImageHelper(IAttribute attribute) {
	init(attribute);
    }

    protected void insert(double[] d) {
	double [] tmp = new double[d.length];
	for (int i = 0; i < tmp.length; i++) {
	    tmp[i] = new Double(d[i]).doubleValue();
	}

	deviceAttribute.insert(tmp, attribute.getXDimension(),
			       attribute.getYDimension());
    }


    double [][] getNumberImageValue(DeviceAttribute deviceAttribute) {
	double [] tmp = deviceAttribute.extractDoubleArray();
	int ydim = attribute.getYDimension();
	int xdim = attribute.getXDimension();
	double[][] retval = new double[ydim][xdim];

	int k = 0;
	for (int y = 0; y < ydim; y++)
  	    for (int x = 0; x < xdim; x++) {
  		retval[y][x] = tmp[k++];
  	    }
	return retval;
    }

    String [][] getImageValue(DeviceAttribute deviceAttribute) {
	double [] tmp = deviceAttribute.extractDoubleArray();
	int ydim = attribute.getYDimension();
	int xdim = attribute.getXDimension();
	String [][] retval = new String[ydim][xdim];
	
	int k = 0;
	for (int i = 0; i < ydim; i++)
  	    for (int j = 0; j < xdim; j++) {
  		retval[i][j] = Double.toString(tmp[k++]);
  	    }
	return retval;
    }

    public String getVersion() {
	return "$Id$";
    }

}
