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
 
// File:          BooleanSpectrumHelper.java
// Created:       2005-02-03 10:45:00, poncet
// By:            <poncet@esrf.fr>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;

import fr.esrf.Tango.DevFailed;

public class BooleanSpectrumHelper implements java.io.Serializable
{
  IAttribute attribute;
  EventSupport propChanges;

  public BooleanSpectrumHelper(IAttribute attribute)
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


  void fireSpectrumValueChanged(boolean[] newValue, long timeStamp)
  {
    propChanges.fireBooleanSpectrumEvent((IBooleanSpectrum) attribute,
      newValue, timeStamp);
  }

  void insert(boolean[] boolSpect)
  {
      attribute.getAttribute().insert(boolSpect,
      ((IAttribute) attribute).getXDimension(),
      ((IAttribute) attribute).getYDimension());
  }

  boolean[] extract() throws DevFailed
  {
    return attribute.getAttribute().extractBooleanArray();
  }

  
  void addBooleanSpectrumListener(IBooleanSpectrumListener l)
  {
      propChanges.addBooleanSpectrumListener(l);
  }

  
  void removeBooleanSpectrumListener(IBooleanSpectrumListener l)
  {
      propChanges.removeBooleanSpectrumListener(l);
  }


  public String getVersion() {
    return "$Id$";
  }

}
