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
 
// File:          DoubleImageHelper.java
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

class DoubleImageHelper extends ANumberImageHelper {

  public DoubleImageHelper(IAttribute attribute) {
    init(attribute);
  }

/* Modified to add support for display_unit property
  protected void insert(double[] d)
  {
    double[] tmp = new double[d.length];
    for (int i = 0; i < tmp.length; i++) {
      tmp[i] = new Double(d[i]).doubleValue();
    }

    deviceAttribute.insert(tmp, attribute.getXDimension(),
      attribute.getYDimension());
  }
  */
  
  protected void insert(double[][] d)
  {
      double     dUnitFactor=1.0;
      double[]   flatd;

      DeviceAttribute da = this.attribute.getAttribute();
      dUnitFactor = this.attribute.getDisplayUnitFactor();
      
      flatd = NumberAttributeHelper.flatten(d);
      if (dUnitFactor==1.0)
      {
         da.insert(flatd, d.length, d[0].length);
	 return;
      }
      
      double[] tmp = new double[flatd.length];
      
      for (int i = 0; i < tmp.length; i++)
      {
          tmp[i] = (flatd[i] / dUnitFactor);
      }
      
      da.insert(tmp, d.length, d[0].length);
  }

  void setMinAlarm(double d, boolean writable) {
    setProperty("min_alarm", new Double(d), writable);
  }

  void setMaxAlarm(double d, boolean writable) {
    setProperty("max_alarm", new Double(d), writable);
  }

  void setMinValue(double d, boolean writable) {
    setProperty("min_value", new Double(d), writable);
  }

  void setMaxValue(double d, boolean writable) {
    setProperty("max_value", new Double(d), writable);
  }

  void setMinWarning(double d, boolean writable) {
    setProperty("min_warning", new Double(d), writable);
  }

  void setMaxWarning(double d, boolean writable) {
    setProperty("max_warning", new Double(d), writable);
  }

  void setDeltaT(double d, boolean writable) {
    setProperty("delta_t", new Double(d), writable);
  }

  void setDeltaVal(double d, boolean writable) {
    setProperty("delta_val", new Double(d), writable);
  }

  void setMinAlarm(double d) {
    setProperty("min_alarm", new Double(d));
  }

  void setMaxAlarm(double d) {
    setProperty("max_alarm", new Double(d));
  }

  void setMinValue(double d) {
    setProperty("min_value", new Double(d));
  }

  void setMaxValue(double d) {
    setProperty("max_value", new Double(d));
  }

  void setMinWarning(double d) {
    setProperty("min_warning", new Double(d));
  }

  void setMaxWarning(double d) {
    setProperty("max_warning", new Double(d));
  }

  void setDeltaT(double d) {
    setProperty("delta_t", new Double(d));
  }

  void setDeltaVal(double d) {
    setProperty("delta_val", new Double(d));
  }

  double[][] getNumberImageValue(DeviceAttribute deviceAttribute) throws DevFailed {
    double[] tmp;
    tmp = deviceAttribute.extractDoubleArray();
    int ydim = deviceAttribute.getDimY();
    int xdim = deviceAttribute.getDimX();

    if (ydim != retval.length || retval.length == 0 || xdim != retval[0].length) {

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
    double[] tmp;
    double   dUnitFactor;
    
    tmp = deviceAttribute.extractDoubleArray();
    dUnitFactor = this.attribute.getDisplayUnitFactor();
    int ydim = deviceAttribute.getDimY();
    int xdim = deviceAttribute.getDimX();

    if (ydim != retval.length || retval.length == 0 || xdim != retval[0].length) {

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
    double[] tmp;
    tmp = deviceAttribute.extractDoubleArray();
    int ydim = deviceAttribute.getDimY();
    int xdim = deviceAttribute.getDimX();
    String[][] retval_str = new String[ydim][xdim];

    int k = 0;
    for (int i = 0; i < ydim; i++)
      for (int j = 0; j < xdim; j++) {
        retval_str[i][j] = Double.toString(tmp[k++]);
      }

    return retval_str;
  }

  public String getVersion() {
    return "$Id$";
  }

}
