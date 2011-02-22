// File:          ANumberScalarHelper.java
// Created:       2002-01-24 10:17:37, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 16:2:19, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;
import fr.esrf.TangoApi.*;

abstract class ANumberScalarHelper extends ANumberSpectrumHelper {
    ANumberSpectrumHelper spectrumHelper;

    void init(IAttribute attribute) {
	super.init(attribute);
    }

    void addNumberScalarListener(INumberScalarListener l) {
	propChanges.addNumberScalarListener(l);
    }

    void removeNumberScalarListener(INumberScalarListener l) {
	propChanges.removeNumberScalarListener(l);
    }
    
    void fireSpectrumValueChanged(double newValue, long timeStamp) {
	double [] newSValue = {newValue};
	fireSpectrumValueChanged(newSValue, timeStamp);
    }

    void fireScalarValueChanged(double newValue, long timeStamp) {
	propChanges.fireNumberScalarEvent((INumberScalar)attribute,
					  newValue, timeStamp);
	fireSpectrumValueChanged(newValue, timeStamp);
    }

    abstract double getNumberScalarValue(DeviceAttribute attribute);

    abstract double getNumberScalarSetPoint(DeviceAttribute attribute);

    abstract protected IAttributeScalarHistory[] getScalarAttHistory(DeviceDataHistory[] attPollHist);

    abstract void insert(double d);

    double[] getNumberSpectrumValue(DeviceAttribute attribute) {
	return spectrumHelper.getNumberSpectrumValue(attribute);
    }

    void insert(double [] d) {
	spectrumHelper.insert(d);
    }

    public String getVersion() {
	return "$Id$";
    }
    
}
