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
 
// File:          RawImageHelper.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <pons@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

class RawImageHelper implements java.io.Serializable {

  IAttribute attribute;
  EventSupport propChanges;
  String encFormat = null;

  public RawImageHelper(IAttribute attribute) {
    init(attribute);
  }

  void init(IAttribute attribute) {
    setAttribute(attribute);
    propChanges = ((AAttribute) attribute).getPropChanges();
  }


  public void setAttribute(IAttribute attribute) {
    this.attribute = attribute;
  }

  public IAttribute getAttribute() {
    return attribute;
  }

  protected void setProperty(String name, Number value) {
    attribute.setProperty(name, value);
    attribute.storeConfig();
  }

  protected void setProperty(String name, Number value, boolean writable) {
    attribute.setProperty(name, value, writable);
  }


  void fireRawImageValueChanged(String encFormat,byte[] newValue, long timeStamp) {
    propChanges.fireRawImageEvent((IRawImage) attribute,encFormat,
            newValue, timeStamp);
  }

  byte[] getRawImageValue(DeviceAttribute deviceAttribute) throws DevFailed {
    DevEncoded e = deviceAttribute.extractDevEncoded();
    encFormat = e.encoded_format;
    return e.encoded_data;
  }

  String getRawImageFormat() {
    return encFormat;
  }

  void addRawImageListener(IRawImageListener l) {
    propChanges.addRawImageListener(l);
  }


  void removeRawImageListener(IRawImageListener l) {
    propChanges.removeRawImageListener(l);
  }


  public String getVersion() {
    return "$Id$";
  }

}
