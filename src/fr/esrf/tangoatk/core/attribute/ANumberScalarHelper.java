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
import fr.esrf.Tango.DevFailed;

public abstract class ANumberScalarHelper extends NumberAttributeHelper {

    void init(IAttribute attribute) {
	super.init(attribute);
    }

    void addNumberScalarListener(INumberScalarListener l) {
	propChanges.addNumberScalarListener(l);
    }

    void removeNumberScalarListener(INumberScalarListener l) {
	propChanges.removeNumberScalarListener(l);
    }
    

    void fireScalarValueChanged(double newValue, long timeStamp) {
	propChanges.fireNumberScalarEvent((INumberScalar)attribute,
					  newValue, timeStamp);
    }

    abstract double getNumberScalarValue(DeviceAttribute attribute);

    abstract double getNumberScalarSetPoint(DeviceAttribute attribute);

    protected abstract IAttributeScalarHistory[] getScalarAttHistory(DeviceDataHistory[] attPollHist);

    protected abstract IAttributeScalarHistory[] getScalarDeviceAttHistory(DeviceDataHistory[] attPollHist);

    abstract void insert(double d);

    abstract double getNumberScalarDisplayValue(DeviceAttribute attribute);

    abstract double getNumberScalarDisplaySetPoint(DeviceAttribute attribute);

    public String getVersion() {
	return "$Id$";
    }
    
}
