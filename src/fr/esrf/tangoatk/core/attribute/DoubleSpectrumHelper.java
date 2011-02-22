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
 
// File:          DoubleSpectrumHelper.java
// Created:       2002-01-24 09:55:13, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 16:56:42, assum>
//
// $Id$
//
// Description:
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

import java.beans.*;

public class DoubleSpectrumHelper extends ANumberSpectrumHelper {

  public DoubleSpectrumHelper(IAttribute attribute) {
    init(attribute);
  }

  void init(IAttribute attribute) {
    super.init(attribute);
  }

  void insert(double[] d) {
    double dUnitFactor = 1.0;

    dUnitFactor = this.attribute.getDisplayUnitFactor();
    DeviceAttribute da = this.attribute.getAttribute();

    if (dUnitFactor == 1.0) {
      da.insert(d);
    } else {
      double[] tmp = new double[d.length];
      for (int i = 0; i < tmp.length; i++) {
        tmp[i] = (d[i] / dUnitFactor);
      }
      da.insert(tmp);
    }
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
  
  double[] getNumberSpectrumValue(DeviceAttribute da) throws DevFailed
  {
      double[]  tmp = da.extractDoubleArray();
      int       nbReadElements = da.getNbRead();
      
      if (nbReadElements == tmp.length)
          return da.extractDoubleArray();
      
      double[]  retval = new double[nbReadElements];
      for (int i = 0; i < nbReadElements; i++)
      {
          retval[i] = tmp[i];
      }
      return retval;
  }
  

  double[] getNumberSpectrumSetPoint(DeviceAttribute da) throws DevFailed
  {
      double[]  tmp = da.extractDoubleArray();
      int       nbReadElements = da.getNbRead();
      int       nbSetElements = tmp.length - nbReadElements;
      
      // The attributes WRITE (WRITE ONLY) return their setPoint in the first sequence of elements
      // In all cases when no "set" element sequence is returned, return the read elements for setPoint
      if (nbSetElements <= 0)
      {
          return getNumberSpectrumValue(da);
      }
      else
      {
         double[] retval = new double[nbSetElements];
         for (int i = nbReadElements; i < tmp.length; i++)
         {
             retval[i] = tmp[i];
         }
         return retval;        
      }
  }


  double[] getNumberSpectrumDisplayValue(DeviceAttribute da) throws DevFailed
  {
     double[]  tmp;
     double    dUnitFactor;
     double[]  retSpectVal;
     int       nbReadElements = da.getNbRead();
     
     dUnitFactor = this.attribute.getDisplayUnitFactor();
     tmp = da.extractDoubleArray();
     
     if ( (tmp.length == nbReadElements) && (dUnitFactor == 1.0) )
         return da.extractDoubleArray();
     
     retSpectVal = new double[nbReadElements];
     for (int i = 0; i < nbReadElements; i++)
     {
         retSpectVal[i] = tmp[i] * dUnitFactor; //return the value in the display unit
     }
     return retSpectVal;
  }
  
  

  double[] getNumberSpectrumDisplaySetPoint(DeviceAttribute da) throws DevFailed
  {
      double     dUnitFactor;
      double[]   tmp = da.extractDoubleArray();
      int        nbReadElements = da.getNbRead();
      int        nbSetElements = tmp.length - nbReadElements;

      dUnitFactor = this.attribute.getDisplayUnitFactor();
      
      // The attributes WRITE (WRITE ONLY) return their setPoint in the first elements
      // In all cases when no "set" element sequence is returned, return the read elements for setPoint
      if (nbSetElements <= 0)
      {
          return getNumberSpectrumDisplayValue(da);
      }
      else
      {
         double[] retval = new double[nbSetElements];
         for (int i = nbReadElements; i < tmp.length; i++)
         {
             retval[i] = tmp[i] * dUnitFactor; //return the value in the display unit
         }
         return retval;        
      }
  }

  public String getVersion() {
    return "$Id$";
  }

}
