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
 
// File:          ANumberImageHelper.java
// Created:       2002-01-24 11:22:33, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-11 14:29:54, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.TangoApi.*;
import fr.esrf.Tango.DevFailed;
import fr.esrf.tangoatk.core.IImageListener;
import fr.esrf.tangoatk.core.INumberImage;

abstract class ANumberImageHelper extends NumberAttributeHelper {
  double[][] retval = new double[0][0];

  abstract String[][] getImageValueAsString(DeviceAttribute attribute) throws DevFailed;
  
  abstract double[][] getNumberImageDisplayValue(DeviceAttribute deviceAttribute) throws DevFailed;

  abstract double[][] getNumberImageValue(DeviceAttribute attribute) throws DevFailed;

  abstract void insert(double[][] d);
  
  void insert(double[] d)
  {
  }
  
  void fireImageValueChanged(double[][] newValue, long timeStamp) {
	propChanges.fireImageEvent((INumberImage) attribute, newValue, timeStamp);
  }
  
  void addImageListener(IImageListener l) {
    propChanges.addImageListener(l);
  }

  void removeImageListener(IImageListener l) {
    propChanges.removeImageListener(l);
  }  

  public String getVersion() {
    return "$Id$";
  }

}
