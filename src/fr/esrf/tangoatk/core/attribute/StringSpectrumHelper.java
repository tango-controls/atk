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
 
// File:          StringSpectrumHelper.java
// Created:       2003-12-11 18:00:00, poncet
// By:            <poncet@esrf.fr>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceAttribute;

public class StringSpectrumHelper implements java.io.Serializable
{
  IAttribute attribute;
  EventSupport propChanges;

  public StringSpectrumHelper(IAttribute attribute)
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


  void fireValueChanged(String[] newValue, long timeStamp)
  {
    propChanges.fireStringSpectrumEvent((IStringSpectrum) attribute,
      newValue, timeStamp);
  }

  void insert(String[] strSpect)
  {
      attribute.getAttribute().insert(strSpect);
  }
  
  String[] getStringSpectrumValue(DeviceAttribute da) throws DevFailed
  {
      String[]   tmp = da.extractStringArray();
      int        nbReadElements = da.getNbRead();
      
      if (nbReadElements == tmp.length)
          return tmp;
      
      String[]  retval = new String[nbReadElements];
      for (int i = 0; i < nbReadElements; i++)
      {
          retval[i] = tmp[i];
      }
      return retval;
  }
  

  String[] getStringSpectrumSetPoint(DeviceAttribute da) throws DevFailed
  {
      String[]   tmp = da.extractStringArray();
      int        nbReadElements = da.getNbRead();
      int        nbSetElements = tmp.length - nbReadElements;
      
      // The attributes WRITE (WRITE ONLY) return their setPoint in the first sequence of elements
      // In all cases when no "set" element sequence is returned, return the read elements for setPoint
      if (nbSetElements <= 0)
      {
          return tmp;
      }
      else
      {
         String[]  retval = new String[nbSetElements];
         for (int i = nbReadElements; i < tmp.length; i++)
         {
             retval[i] = tmp[i];
         }
         return retval;        
      }
  }

  void addStringSpectrumListener(IStringSpectrumListener l)
  {
      propChanges.addStringSpectrumListener(l);
  }

  
  void removeStringSpectrumListener(IStringSpectrumListener l)
  {
      propChanges.removeStringSpectrumListener(l);
  }


  public String getVersion() {
    return "$Id$";
  }

}
