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
 
// File:          DevStateScalarHelper.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;




import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.DevState;
import fr.esrf.TangoApi.DeviceAttribute;

import fr.esrf.tangoatk.core.EventSupport;
import fr.esrf.tangoatk.core.IAttribute;
import fr.esrf.tangoatk.core.IDevStateScalar;
import fr.esrf.tangoatk.core.IDevStateScalarListener;

class DevStateScalarHelper implements java.io.Serializable
{
    AAttribute attribute;
    EventSupport propChanges;

    public DevStateScalarHelper(AAttribute att)
    {
      init(att);
    }

    void init(AAttribute att)
    {
      setAttribute(att);
      propChanges = att.getPropChanges();
    }


    public void setAttribute(AAttribute att)
    {
      this.attribute = att;
    }

    public IAttribute getAttribute()
    {
      return attribute;
    }

    protected void setProperty(String name, Number value)
    {
      attribute.setProperty(name, value);
      attribute.storeConfig();
    }

    protected void setProperty(String name, Number value, boolean writable)
    {
      attribute.setProperty(name, value, writable);
    }


    void fireValueChanged(String newValue, long timeStamp)
    {
      propChanges.fireDevStateScalarEvent((IDevStateScalar) attribute,
	newValue, timeStamp);
    }


//    String extract() throws DevFailed
//    {
//      return Device.toString(attribute.getAttribute().extractState());
//    }


    void addDevStateScalarListener(IDevStateScalarListener l)
    {
	propChanges.addDevStateScalarListener(l);
    }


    void removeDevStateScalarListener(IDevStateScalarListener l)
    {
	propChanges.removeDevStateScalarListener(l);
    }



    public String getDevStateScalarSetPoint(DeviceAttribute devAtt) throws DevFailed
    {

	DevState[]  ds_arr=null;
        String      stateSetPoint = null;

	ds_arr = devAtt.extractDevStateArray();
      
	if (ds_arr == null)
	   return null;
	
	if (ds_arr.length < 2)
	   return null;

        stateSetPoint = fr.esrf.tangoatk.core.Device.toString(ds_arr[1]);

        return stateSetPoint;
    }

    public String getVersion() {
	return "$Id$";
    }
}
