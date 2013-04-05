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
 
// File:          BooleanScalarHelper.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;



import fr.esrf.tangoatk.core.*;

import fr.esrf.TangoApi.*;
import fr.esrf.Tango.DevFailed;

class BooleanScalarHelper implements java.io.Serializable
{
    AAttribute attribute;
    EventSupport propChanges;

    public BooleanScalarHelper(AAttribute attribute)
    {
      init(attribute);
    }

    void init(AAttribute attribute)
    {
      setAttribute(attribute);
      propChanges = attribute.getPropChanges();
    }


    public void setAttribute(AAttribute attribute)
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


    void fireValueChanged(boolean newValue, long timeStamp)
    {
      propChanges.fireBooleanScalarEvent((IBooleanScalar) attribute,
	newValue, timeStamp);
    }

    void insert(boolean b)
    {
	attribute.getAttribute().insert(b);
    }


    boolean extract() throws DevFailed
    {
      return attribute.getAttribute().extractBoolean();
    }


    void addBooleanScalarListener(IBooleanScalarListener l)
    {
	propChanges.addBooleanScalarListener(l);
    }


    void removeBooleanScalarListener(IBooleanScalarListener l)
    {
	propChanges.removeBooleanScalarListener(l);
    }



    public boolean getBooleanScalarSetPoint(DeviceAttribute attribute) throws DevFailed
    {

	boolean[]  bool_arr=null;

	bool_arr = attribute.extractBooleanArray();
      
	if (bool_arr == null)
	   return false;
	
	if (bool_arr.length < 1)
	   return false;
	   
	if (bool_arr.length > 1)
	   return bool_arr[1];
	else // The attribute WRITE (WRITE ONLY) return their setPoint in the first element
	   return bool_arr[0];
    }



    public String getVersion() {
	return "$Id$";
    }
}
