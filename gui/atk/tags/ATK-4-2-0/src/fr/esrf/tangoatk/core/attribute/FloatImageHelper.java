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
 
// File:          FloatImageHelper.java
// Created:       2002-01-24 10:13:21, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-16 10:32:25, assum>
//
// $Id$
//
// Description:
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

class FloatImageHelper extends ANumberImageHelper {

  public FloatImageHelper(IAttribute attribute) {
    init(attribute);
  }
  
  protected void insert(double[][] d)
  {
      double     dUnitFactor=1.0;
      double[]   flatd;

      DeviceAttribute da = this.attribute.getAttribute();
      dUnitFactor = this.attribute.getDisplayUnitFactor();
      
      flatd = NumberAttributeHelper.flatten(d);
      float[] tmp = new float[flatd.length];
      
      for (int i = 0; i < tmp.length; i++)
      {
          tmp[i] = (float) (flatd[i] / dUnitFactor);
      }
      
      da.insert(tmp, d[0].length, d.length);
  }

  void setMinAlarm(double d, boolean writable) {
    setProperty("min_alarm", new Float(d), writable);
  }

  void setMaxAlarm(double d, boolean writable) {
    setProperty("max_alarm", new Float(d), writable);
  }

  void setMinValue(double d, boolean writable) {
    setProperty("min_value", new Float(d), writable);
  }

  void setMaxValue(double d, boolean writable) {
    setProperty("max_value", new Float(d), writable);
  }

  void setMinWarning(double d, boolean writable) {
    setProperty("min_warning", new Float(d), writable);
  }

  void setMaxWarning(double d, boolean writable) {
    setProperty("max_warning", new Float(d), writable);
  }

  void setDeltaT(double d, boolean writable) {
    setProperty("delta_t", new Float(d), writable);
  }

  void setDeltaVal(double d, boolean writable) {
    setProperty("delta_val", new Float(d), writable);
  }

  void setMinAlarm(double d) {
    setProperty("min_alarm", new Float(d));
  }

  void setMaxAlarm(double d) {
    setProperty("max_alarm", new Float(d));
  }

  void setMinValue(double d) {
    setProperty("min_value", new Float(d));
  }

  void setMaxValue(double d) {
    setProperty("max_value", new Float(d));
  }

  void setMinWarning(double d) {
    setProperty("min_warning", new Float(d));
  }

  void setMaxWarning(double d) {
    setProperty("max_warning", new Float(d));
  }

  void setDeltaT(double d) {
    setProperty("delta_t", new Float(d));
  }

  void setDeltaVal(double d) {
    setProperty("delta_val", new Float(d));
  }

  double[][] getNumberImageValue(DeviceAttribute deviceAttribute) throws DevFailed {
    float[] tmp;
    tmp = deviceAttribute.extractFloatArray();
    int ydim = deviceAttribute.getDimY();
    int xdim = deviceAttribute.getDimX();

    if (ydim != retval.length || xdim != retval[0].length) {

      retval = new double[ydim][xdim];
    }

    int k = 0;
    for (int y = 0; y < ydim; y++)
      for (int x = 0; x < xdim; x++) {
        retval[y][x] = tmp[k++];
      }

    return retval;
  }


  double[][] getNumberImageDisplayValue(DeviceAttribute deviceAttribute) throws DevFailed {
    float[]  tmp;
    double   dUnitFactor;
    
    tmp = deviceAttribute.extractFloatArray();
    dUnitFactor = this.attribute.getDisplayUnitFactor();
    int ydim = deviceAttribute.getDimY();
    int xdim = deviceAttribute.getDimX();

    if (ydim != retval.length || xdim != retval[0].length) {

      retval = new double[ydim][xdim];
    }

    int k = 0;
    for (int y = 0; y < ydim; y++)
      for (int x = 0; x < xdim; x++) {
        retval[y][x] = tmp[k++] * dUnitFactor; //return the value in the display unit
      }

    return retval;
  }

  String[][] getImageValueAsString(DeviceAttribute deviceAttribute) throws DevFailed {
    float[] tmp;
    tmp = deviceAttribute.extractFloatArray();
    int ydim = deviceAttribute.getDimY();
    int xdim = deviceAttribute.getDimX();
    String[][] retval_str = new String[ydim][xdim];

    int k = 0;
    for (int i = 0; i < ydim; i++)
      for (int j = 0; j < xdim; j++) {
        retval_str[i][j] = Float.toString(tmp[k++]);
      }

    return retval_str;
  }

  public String getVersion() {
    return "$Id$";
  }

}
