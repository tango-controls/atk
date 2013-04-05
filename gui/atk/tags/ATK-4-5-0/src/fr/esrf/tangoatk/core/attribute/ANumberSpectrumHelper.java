/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
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

    @Override
    void init(AAttribute attribute) {
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

    abstract double[] getNumberSpectrumSetPoint(DeviceAttribute attribute) throws DevFailed;
    
    abstract double[] getNumberSpectrumDisplayValue(DeviceAttribute deviceAttribute) throws DevFailed;

    abstract double[] getNumberSpectrumDisplaySetPoint(DeviceAttribute attribute) throws DevFailed;

    protected abstract IAttributeSpectrumHistory[] getSpectrumAttHistory(DeviceDataHistory[] attPollHist);

    protected abstract IAttributeSpectrumHistory[] getSpectrumDeviceAttHistory(DeviceDataHistory[] attPollHist);

    abstract void insert(double [] d);

    public String getVersion() {
	return "$Id$";
    }
    
}
