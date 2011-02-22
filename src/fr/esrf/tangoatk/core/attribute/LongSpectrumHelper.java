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
 
// File:          LongSpectrumHelper.java
// Created:       2002-01-24 10:02:46, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 16:56:32, assum>
//
// $Id$
//
// Description:
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

import java.beans.*;

public class LongSpectrumHelper extends ANumberSpectrumHelper {

  public LongSpectrumHelper(IAttribute attribute) {
    init(attribute);
  }

  void init(IAttribute attribute) {
    super.init(attribute);
  }

  void insert(double[] d)
  {
      double   dUnitFactor=1.0;
      int[]    tmp = new int[d.length];
      
      dUnitFactor = this.attribute.getDisplayUnitFactor();
      DeviceAttribute da = this.attribute.getAttribute();
      
      for (int i = 0; i < tmp.length; i++)
      {
           tmp[i] = (int) (d[i] / dUnitFactor);
      }

      da.insert(tmp);
  }


  void setMinAlarm(double d) {
    setProperty("min_alarm", new Long((long) d));
  }

  void setMaxAlarm(double d) {
    setProperty("max_alarm", new Long((long) d));
  }

  void setMinValue(double d) {
    setProperty("min_value", new Long((long) d));
  }

  void setMaxValue(double d) {
    setProperty("max_value", new Long((long) d));
  }

  void setMinWarning(double d) {
    setProperty("min_warning", new Long((long) d));
  }

  void setMaxWarning(double d) {
    setProperty("max_warning", new Long((long) d));
  }

  void setDeltaT(double d) {
    setProperty("delta_t", new Long((long) d));
  }

  void setDeltaVal(double d) {
    setProperty("delta_val", new Long((long) d));
  }

  void setMinWarning(double d, boolean writable) {
    setProperty("min_warning", new Long((long) d), writable);
  }

  void setMaxWarning(double d, boolean writable) {
    setProperty("max_warning", new Long((long) d), writable);
  }

  void setDeltaT(double d, boolean writable) {
    setProperty("delta_t", new Long((long) d), writable);
  }

  void setDeltaVal(double d, boolean writable) {
    setProperty("delta_val", new Long((long) d), writable);
  }

  void setMinAlarm(double d, boolean writable) {
    setProperty("min_alarm", new Long((long) d), writable);
  }

  void setMaxAlarm(double d, boolean writable) {
    setProperty("max_alarm", new Long((long) d), writable);
  }

  void setMinValue(double d, boolean writable) {
    setProperty("min_value", new Long((long) d), writable);
  }

  void setMaxValue(double d, boolean writable) {
    setProperty("max_value", new Long((long) d), writable);
  }


  double[] getNumberSpectrumValue(DeviceAttribute deviceAttribute) throws DevFailed {
      int[] tmp = deviceAttribute.extractLongArray();
      double[] retval = new double[tmp.length];
      for (int i = 0; i < tmp.length; i++) {
        retval[i] = (double) tmp[i];
      }
      return retval;
  }

  double[] getNumberSpectrumDisplayValue(DeviceAttribute deviceAttribute) throws DevFailed
  {
     int[]     tmp;
     double    dUnitFactor;
     double[]  retSpectVal;

     tmp = deviceAttribute.extractLongArray();
     dUnitFactor = this.attribute.getDisplayUnitFactor();
     retSpectVal = new double[tmp.length];
     
     for (int i = 0; i < tmp.length; i++)
     {
         retSpectVal[i] = (double) tmp[i] * dUnitFactor; //return the value in the display unit
     }
     return retSpectVal;
  }

  public String getVersion() {
    return "$Id$";
  }
}
