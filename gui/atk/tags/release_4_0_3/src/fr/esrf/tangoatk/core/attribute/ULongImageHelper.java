/*
 *  Copyright (C) :	2009
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
 
// File:          ULongImageHelper.java
// Created:       2009-03-11 11:06:28, poncet
// By:            Faranguiss PONCET
//
// $Id$
//

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

class ULongImageHelper extends ANumberImageHelper
{

  public ULongImageHelper(IAttribute attribute)
  {
     init(attribute);
  }


  protected void insert(double[][] d)
  {
     double     dUnitFactor=1.0;
     double[]   flatd;

     DeviceAttribute da = this.attribute.getAttribute();
     dUnitFactor = this.attribute.getDisplayUnitFactor();
      
     flatd = NumberAttributeHelper.flatten(d);
     long[] tmp = new long[flatd.length];
     for (int i = 0; i < tmp.length; i++)
        tmp[i] = (long) (flatd[i] / dUnitFactor);
      
     da.insert_ul(tmp, d[0].length, d.length);
  }

  void setMinAlarm(double d)
  {
     setProperty("min_alarm", new Long((long) d));
  }

  void setMaxAlarm(double d)
  {
     setProperty("max_alarm", new Long((long) d));
  }

  void setMinValue(double d)
  {
     setProperty("min_value", new Long((long) d));
  }

  void setMaxValue(double d)
  {
     setProperty("max_value", new Long((long) d));
  }

  void setMinWarning(double d)
  {
     setProperty("min_warning", new Long((long) d));
  }

  void setMaxWarning(double d)
  {
     setProperty("max_warning", new Long((long) d));
  }

  void setDeltaT(double d)
  {
     setProperty("delta_t", new Long((long) d));
  }

  void setDeltaVal(double d)
  {
     setProperty("delta_val", new Long((long) d));
  }

  void setMinWarning(double d, boolean writable)
  {
     setProperty("min_warning", new Long((long) d), writable);
  }

  void setMaxWarning(double d, boolean writable)
  {
     setProperty("max_warning", new Long((long) d), writable);
  }

  void setDeltaT(double d, boolean writable)
  {
     setProperty("delta_t", new Long((long) d), writable);
  }

  void setDeltaVal(double d, boolean writable)
  {
     setProperty("delta_val", new Long((long) d), writable);
  }

  void setMinAlarm(double d, boolean writable)
  {
     setProperty("min_alarm", new Long((long) d), writable);
  }

  void setMaxAlarm(double d, boolean writable)
  {
     setProperty("max_alarm", new Long((long) d), writable);
  }

  void setMinValue(double d, boolean writable)
  {
     setProperty("min_value", new Long((long) d), writable);
  }

  void setMaxValue(double d, boolean writable)
  {
     setProperty("max_value", new Long((long) d), writable);
  }

  double[][] getNumberImageValue(DeviceAttribute deviceAttribute) throws DevFailed
  {
      int ydim = deviceAttribute.getDimY();
      int xdim = deviceAttribute.getDimX();

      if (ydim != retval.length || xdim != retval[0].length)
         retval = new double[ydim][xdim];

      long[] tmp = deviceAttribute.extractULongArray();

      int k = 0;
      for (int i = 0; i < ydim; i++)
          for (int j = 0; j < xdim; j++)
              retval[i][j] = (double)tmp[k++];

      return retval;
  }

  double[][] getNumberImageDisplayValue(DeviceAttribute deviceAttribute) throws DevFailed
  {
     long[]    tmp;
     double    dUnitFactor;
    
     tmp = deviceAttribute.extractULongArray();
     dUnitFactor = this.attribute.getDisplayUnitFactor();

     int ydim = deviceAttribute.getDimY();
     int xdim = deviceAttribute.getDimX();

     if (ydim != retval.length || xdim != retval[0].length)
      retval = new double[ydim][xdim];

     int k = 0;
     for (int i = 0; i < ydim; i++)
         for (int j = 0; j < xdim; j++)
             retval[i][j] = (double)tmp[k++] * dUnitFactor; //return the value in the display unit

     return retval;
  }

  String[][] getImageValueAsString(DeviceAttribute deviceAttribute) throws DevFailed
  {
     long[] tmp = deviceAttribute.extractULongArray();

     int ydim = deviceAttribute.getDimY();
     int xdim = deviceAttribute.getDimX();
     String[][] retval_str = new String[ydim][xdim];

     int k = 0;
     for (int i = 0; i < ydim; i++)
         for (int j = 0; j < xdim; j++)
         {
            retval_str[i][j] = Long.toString(tmp[k++]);
         }
     return retval_str;
  }

  public String getVersion()
  {
     return "$Id$";
  }
}
