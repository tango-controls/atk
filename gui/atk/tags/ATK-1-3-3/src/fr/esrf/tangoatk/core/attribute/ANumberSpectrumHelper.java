// File:          ANumberSpectrumHelper.java
// Created:       2002-01-24 10:22:01, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 16:0:45, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;
import fr.esrf.TangoApi.*;

abstract class ANumberSpectrumHelper extends NumberAttributeHelper {
    ANumberImageHelper imageHelper;

    void init(IAttribute attribute) {
	super.init(attribute);
    }

    double [][] getNumberImageValue(DeviceAttribute attribute) {
	return imageHelper.getNumberImageValue(attribute);
    }

    String [][] getImageValue(DeviceAttribute attribute) {
	return imageHelper.getImageValue(attribute);
    }
    
    public double[][] getStandardNumberValue() {
	int ydim = attribute.getYDimension();
	int xdim = attribute.getXDimension();
	double [][] retval = getNumberImageValue(deviceAttribute);

	for (int i = 0; i < ydim; i++) {
	    for (int j = 0; j < xdim; j++) {
		retval[i][j] *= attribute.getStandardUnit();
	    } // end of for ()
	} // end of for ()
	return retval;
	
    }

    public void addSpectrumListener(ISpectrumListener l) {
	propChanges.addSpectrumListener(l);
    }

    public void removeSpectrumListener(ISpectrumListener l) {
	propChanges.removeSpectrumListener(l);
    }


    void fireSpectrumValueChanged(double [] newValue, long timeStamp) {
	propChanges.fireSpectrumEvent((INumberSpectrum)attribute,
					    newValue, timeStamp);
	fireImageValueChanged(newValue, timeStamp);
    }
	
    void fireImageValueChanged(double [] newValue, long timeStamp) {
	double[][] newIValue = {newValue};
	fireImageValueChanged(newIValue, timeStamp);
    }

    public String [] getSpectrumValue(DeviceAttribute  attribute) {
	double[] val = getNumberSpectrumValue(attribute);
	String [] tmp = new String[val.length];

	for (int i = 0; i < val.length; i++) 
	    tmp[i] = Double.toString(val[i]);

	return tmp;
    }
	
    abstract double[] getNumberSpectrumValue(DeviceAttribute attribute);


    abstract void insert(double [] d);

    public String getVersion() {
	return "$Id$";
    }
    
}
