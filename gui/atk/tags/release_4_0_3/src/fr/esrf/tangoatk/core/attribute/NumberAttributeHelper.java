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
 
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;

import fr.esrf.TangoApi.DeviceAttribute;

abstract class NumberAttributeHelper implements java.io.Serializable {
  IAttribute attribute;
  transient DeviceAttribute deviceAttribute;
  EventSupport propChanges;

  public void setAttribute(IAttribute attribute) {
    this.attribute = attribute;
  }

  void init(IAttribute attribute) {
    setAttribute(attribute);
    propChanges = ((AAttribute) attribute).getPropChanges();
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

  public static double[] flatten(double[][] src) {
    int lineSize = src[0].length;
    int size = src.length * src[0].length;
    double[] dst = new double[size];

    for (int i = 0; i < src.length; i++)
    	System.arraycopy(src[i], 0, dst, i * lineSize, lineSize);
    return dst;
  }


  public static double[] flatten2double(String[][] src) {
    int size = src.length * src[0].length;
    double[] dst = new double[size];
    int k = 0;
    for (int i = 0; i < src.length; i++)
      for (int j = 0; j < src[i].length; j++)
        dst[k++] = Double.parseDouble(src[i][j]);

    return dst;
  }


  public static double[][] str2double(String[][] src) {
    double[][] dst = new double[src.length][src[0].length];
    for (int i = 0; i < src.length; i++)
      for (int j = 0; j < src[i].length; j++)
        dst[i][j] = Double.parseDouble(src[i][j]);

    return dst;
  }

  abstract void setMinAlarm(double d, boolean writable);

  abstract void setMaxAlarm(double d, boolean writable);

  abstract void setMaxValue(double d, boolean writable);

  abstract void setMinValue(double d, boolean writable);

  abstract void setMinWarning(double d, boolean writable);

  abstract void setMaxWarning(double d, boolean writable);

  abstract void setDeltaT(double d, boolean writable);

  abstract void setDeltaVal(double d, boolean writable);

  abstract void setMinAlarm(double d);

  abstract void setMaxAlarm(double d);

  abstract void setMaxValue(double d);

  abstract void setMinValue(double d);

  abstract void setMinWarning(double d);

  abstract void setMaxWarning(double d);

  abstract void setDeltaT(double d);

  abstract void setDeltaVal(double d);
  
  public String getVersion() {
    return "$Id$";
  }

}
