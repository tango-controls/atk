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
import fr.esrf.Tango.DevFailed;

abstract class ANumberSpectrumHelper extends NumberAttributeHelper {

    void init(IAttribute attribute) {
	super.init(attribute);
    }


    void addSpectrumListener(ISpectrumListener l) {
	propChanges.addSpectrumListener(l);
    }

    void removeSpectrumListener(ISpectrumListener l) {
	propChanges.removeSpectrumListener(l);
    }


    void fireSpectrumValueChanged(double [] newValue, long timeStamp) {
	propChanges.fireSpectrumEvent((INumberSpectrum)attribute,
					    newValue, timeStamp);
    }
	

    public String [] getSpectrumValue(DeviceAttribute  attribute) throws DevFailed {
	double[] val = getNumberSpectrumValue(attribute);
	String [] tmp = new String[val.length];

	for (int i = 0; i < val.length; i++) 
	    tmp[i] = Double.toString(val[i]);

	return tmp;
    }
	
    abstract double[] getNumberSpectrumValue(DeviceAttribute attribute) throws DevFailed;
    
    abstract double[] getNumberSpectrumDisplayValue(DeviceAttribute deviceAttribute) throws DevFailed;


    abstract void insert(double [] d);

    public String getVersion() {
	return "$Id$";
    }
    
}
