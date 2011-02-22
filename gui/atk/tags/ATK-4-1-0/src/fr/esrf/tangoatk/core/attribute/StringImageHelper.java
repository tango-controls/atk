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
 
// File:          StringImageHelper.java
// Created:       2007-05-03 10:46:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

class StringImageHelper implements java.io.Serializable
{
    IAttribute      attribute;
    EventSupport    propChanges;
    String[][]      retval = new String[1][1];

    public StringImageHelper(IAttribute attribute)
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


    void fireImageValueChanged(String[][] newValue, long timeStamp)
    {
        propChanges.fireStringImageEvent(  (IStringImage) attribute,
	                                   newValue, timeStamp);
    }

    void insert(String[][] stringImg)
    {
	 String[]   flatStr;
	 flatStr = flatten(stringImg);
	 attribute.getAttribute().insert(flatStr, stringImg.length, stringImg[0].length);
    }


    String[] flatten(String[][] src)
    {
       int  size = src.length * src[0].length;
       String[] dst = new String[size];

       for (int i = 0; i < src.length; i++)
	 System.arraycopy(src[i], 0, dst, i * src.length, src.length);
       return dst;
    }



    String[][] getStringImageValue(DeviceAttribute deviceAttribute) throws DevFailed
    {
	String[] tmp;

	tmp = deviceAttribute.extractStringArray();
	int ydim = attribute.getYDimension();
	int xdim = attribute.getXDimension();

	if (ydim != retval.length || retval.length == 0 || xdim != retval[0].length)
	{

	  retval = new String[ydim][xdim];
	}

	int k = 0;
	for (int y = 0; y < ydim; y++)
	  for (int x = 0; x < xdim; x++)
	  {
            retval[y][x] = tmp[k++];
	  }

	return retval;
    }

    void addStringImageListener(IStringImageListener l)
    {
	propChanges.addStringImageListener(l);
    }


    void removeStringImageListener(IStringImageListener l)
    {
	propChanges.removeStringImageListener(l);
    }


    public String getVersion() {
      return "$Id$";
    }

}
