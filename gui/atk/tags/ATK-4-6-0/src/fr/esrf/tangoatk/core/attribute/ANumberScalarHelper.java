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

public abstract class ANumberScalarHelper extends NumberAttributeHelper {

    void init(AAttribute attribute) {
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
