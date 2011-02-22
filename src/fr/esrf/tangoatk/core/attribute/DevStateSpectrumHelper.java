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
 
// File:          DevStateSpectrumHelper.java
// Created:       2008-07-07 15:23:16, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;


import java.util.*;

import fr.esrf.tangoatk.core.*;

import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.DevState;
import fr.esrf.TangoApi.DeviceAttribute;

class DevStateSpectrumHelper implements java.io.Serializable
{
    IAttribute attribute;
    EventSupport propChanges;

    public DevStateSpectrumHelper(IAttribute attribute)
    {
       init(attribute);
    }

    void init(IAttribute attribute)
    {
      setAttribute(attribute);
      propChanges = ((AAttribute) attribute).getPropChanges();
    }


    public void setAttribute(IAttribute attribute)
    {
      this.attribute = attribute;
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

    void fireDevStateSpectrumValueChanged(String[] newValue, long timeStamp)
    {
        propChanges.fireDevStateSpectrumEvent( (IDevStateSpectrum) attribute,
                                                newValue, timeStamp);
    }

    void insert(String[] stateSpect)
    {
        DevState[]        devStatesArray = new DevState[stateSpect.length];      
        DeviceAttribute   da = this.attribute.getAttribute();
      
        for (int i = 0; i < stateSpect.length; i++)
        {
           devStatesArray[i] = Device.getStateFromString(stateSpect[i]);
        }

        da.insert(devStatesArray);
    }

    String[] getStateSpectrumValue(DeviceAttribute da) throws DevFailed
    {
        String[]    retval = null;
        DevState[]  devStates = null;
        int         nbReadElements;
        
        devStates = da.extractDevStateArray();
        nbReadElements = da.getNbRead();
        retval = new String[nbReadElements];
        
        for (int i = 0; i < nbReadElements; i++)
        {
            retval[i] = Device.toString(devStates[i]);
        }
        return retval;
   }
  

   String[] getStateSpectrumSetPoint(DeviceAttribute da) throws DevFailed
   {
      DevState[]  devStates = null;
      int         nbReadElements;
      int         nbSetElements;
      String[]    retval = null;
      
      devStates = da.extractDevStateArray();
      nbReadElements = da.getNbRead();
      nbSetElements = devStates.length - nbReadElements;
      
      // The attributes WRITE (WRITE ONLY) return their setPoint in the first sequence of elements
      // In all cases when no "set" element sequence is returned, return the read elements for setPoint
      if (nbSetElements <= 0)
      {
          return getStateSpectrumValue(da);
      }
      else
      {
         retval = new String[nbSetElements];
         int j = 0;
         for (int i = nbReadElements; i < devStates.length; i++)
         {
             retval[j] = Device.toString(devStates[i]);
             j++;
         }
         return retval;        
      }
   }

    
    void addDevStateSpectrumListener(IDevStateSpectrumListener l)
    {
        propChanges.addDevStateSpectrumListener(l);
    }


    void removeDevStateSpectrumListener(IDevStateSpectrumListener l)
    {
        propChanges.removeDevStateSpectrumListener(l);
    }



    public String getVersion() {
	return "$Id$";
    }
}
