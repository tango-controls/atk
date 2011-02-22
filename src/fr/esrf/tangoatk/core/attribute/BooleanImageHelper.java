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
 
// File:          BooleanImageHelper.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

class BooleanImageHelper implements java.io.Serializable
{
  IAttribute      attribute;
  EventSupport    propChanges;
  boolean[][]     retval = new boolean[1][1];

  public BooleanImageHelper(IAttribute attribute)
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


  void fireImageValueChanged(boolean[][] newValue, long timeStamp)
  {
    propChanges.fireBooleanImageEvent((IBooleanImage) attribute,
      newValue, timeStamp);
  }

  void insert(boolean[][] boolImage)
  {
      boolean[]   flatBool;
      flatBool = flatten(boolImage);
      attribute.getAttribute().insert(flatBool, boolImage.length, boolImage[0].length);
  }
  
  
  boolean[] flatten(boolean[][] src)
  {
     int  size = src.length * src[0].length;
     boolean[] dst = new boolean[size];

     for (int i = 0; i < src.length; i++)
       System.arraycopy(src[i], 0, dst, i * src.length, src.length);
     return dst;
  }



  boolean[][] getBooleanImageValue(DeviceAttribute deviceAttribute) throws DevFailed
  {
      boolean[] tmp;
      
      tmp = deviceAttribute.extractBooleanArray();
      int ydim = attribute.getYDimension();
      int xdim = attribute.getXDimension();

      if (ydim != retval.length || xdim != retval[0].length)
      {

	retval = new boolean[ydim][xdim];
      }

      int k = 0;
      for (int y = 0; y < ydim; y++)
	for (int x = 0; x < xdim; x++)
	{
          retval[y][x] = tmp[k++];
	}

      return retval;
  }
  
  void addBooleanImageListener(IBooleanImageListener l)
  {
      propChanges.addBooleanImageListener(l);
  }

  
  void removeBooleanImageListener(IBooleanImageListener l)
  {
      propChanges.removeBooleanImageListener(l);
  }


  public String getVersion() {
    return "$Id$";
  }

}
