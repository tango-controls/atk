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
 
// File:          ULongScalarHelper.java
// Created:       2009-03-11 11:06:28, poncet
// By:            Faranguiss PONCET
//
// $Id$
//

package fr.esrf.tangoatk.core.attribute;


import java.util.*;

import fr.esrf.tangoatk.core.*;

import fr.esrf.TangoApi.*;
import fr.esrf.Tango.AttrQuality;
import fr.esrf.Tango.DevFailed;

public class ULongScalarHelper extends ANumberScalarHelper
{

   public ULongScalarHelper(IAttribute attribute)
   {
      init(attribute);
   }


   void insert(double d)
   {
      double   dUnitFactor=1.0;

      DeviceAttribute da = this.attribute.getAttribute();
      dUnitFactor = this.attribute.getDisplayUnitFactor();

      if (dUnitFactor == 1.0)
         da.insert_ul((long) d);
      else
      {
         long  li = (long) (d / dUnitFactor);
	 da.insert_ul(li);
      }
   }

   double getNumberScalarValue(DeviceAttribute devAtt)
   {
      long[] ulong_arr = null;
    
      try
      {
	 ulong_arr = devAtt.extractULongArray();
      }
      catch( DevFailed e )
      {
         return Double.NaN;
      }

      if (ulong_arr == null)
         return Double.NaN;

      if (ulong_arr.length < 1)
         return Double.NaN;

      return ulong_arr[0];
   }

   double getNumberScalarSetPoint(DeviceAttribute devAtt)
   {
      long[] ulong_arr = null;
    
      try
      {
         ulong_arr = devAtt.extractULongArray();
      }
      catch( DevFailed e )
      {
         return Double.NaN;
      }

      if (ulong_arr == null)
         return Double.NaN;

      if (ulong_arr.length < 1)
         return Double.NaN;

      if (ulong_arr.length > 1)
         return ulong_arr[1];
      else // The attributes WRITE (WRITE ONLY) return their setPoint in the first element
         return ulong_arr[0];
  }

  double getNumberScalarDisplayValue(DeviceAttribute devAtt)
  {
     long[]     ulong_arr = null;
     double     dUnitFactor;

     dUnitFactor = this.attribute.getDisplayUnitFactor();
    
     try
     {
	 ulong_arr = devAtt.extractULongArray();
     }
     catch( DevFailed e )
     {
         return Double.NaN;
     }

     if (ulong_arr == null)
        return Double.NaN;

     if (ulong_arr.length < 1)
        return Double.NaN;

     return (ulong_arr[0] * dUnitFactor);
  }

  double getNumberScalarDisplaySetPoint(DeviceAttribute devAtt)
  {
     long[]     ulong_arr = null;
     double     dUnitFactor;

     dUnitFactor = this.attribute.getDisplayUnitFactor();
    
     try
     {
	 ulong_arr = devAtt.extractULongArray();
     }
     catch( DevFailed e )
     {
         return Double.NaN;
     }

     if (ulong_arr == null)
        return Double.NaN;

     if (ulong_arr.length < 1)
        return Double.NaN;

     if (ulong_arr.length > 1)
        return (ulong_arr[1] * dUnitFactor);
     else // The attributes WRITE (WRITE ONLY) return their setPoint in the first element
        return (ulong_arr[0] * dUnitFactor);
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

  protected INumberScalarHistory[] getNumberScalarAttHistory(DeviceDataHistory[] attPollHist)
  {
     List<NumberScalarHistory>      hist;
     NumberScalarHistory            histElem;
     fr.esrf.Tango.AttrQuality      attq;
     int                            i;
     double                         dUnitFactor=1.0;

     if (attPollHist.length <= 0)  return null;

     dUnitFactor = this.attribute.getDisplayUnitFactor();
        
     if (dUnitFactor <= 0)
        dUnitFactor = 1.0;

     hist = new Vector<NumberScalarHistory> ();

     for (i = 0; i < attPollHist.length; i++)
     {
        histElem = new NumberScalarHistory();
        try
        {
           histElem.setTimestamp(attPollHist[i].getTime());
        } 
        catch (Exception ex)
        {
           histElem.setTimestamp(0);
        }
           
        try
        {
           attq = attPollHist[i].getAttrQuality();
           switch (attq.value())
           {
               case AttrQuality._ATTR_VALID    : histElem.setState(IAttribute.VALID); break;
               case AttrQuality._ATTR_INVALID  : histElem.setState(IAttribute.INVALID); break;
               case AttrQuality._ATTR_ALARM    : histElem.setState(IAttribute.ALARM); break;
               case AttrQuality._ATTR_WARNING  : histElem.setState(IAttribute.WARNING); break;
               case AttrQuality._ATTR_CHANGING : histElem.setState(IAttribute.CHANGING); break;
               default                         : histElem.setState(IAttribute.UNKNOWN); break;                      
           }
        }
        catch (Exception ex)
        {
           histElem.setState(IAttribute.UNKNOWN);
        }
           
        try
        {
           int     ulongVal;
           double  doubleVal;
           ulongVal = attPollHist[i].extractULong();
           doubleVal = ((double) ulongVal) * dUnitFactor;
           histElem.setValue(doubleVal);
        }
        catch (Exception ex)
        {
           histElem.setValue(Double.NaN);
        }
        hist.add(i, histElem);
     } // for

     NumberScalarHistory[] histArray;
     histArray = hist.toArray(new NumberScalarHistory[0]);
     return histArray;
  }

  protected INumberScalarHistory[] getNumberScalarDeviceAttHistory(DeviceDataHistory[] attPollHist)
  {
     List<NumberScalarHistory>      hist;
     NumberScalarHistory            histElem;
     fr.esrf.Tango.AttrQuality      attq;
     int                            i;
     double                         dUnitFactor=1.0;

     if (attPollHist.length <= 0)  return null;

     dUnitFactor = this.attribute.getDisplayUnitFactor();
        
     if (dUnitFactor <= 0)
        dUnitFactor = 1.0;

     hist = new Vector<NumberScalarHistory> ();

     for (i = 0; i < attPollHist.length; i++)
     {
        histElem = new NumberScalarHistory();
        try
        {
           histElem.setTimestamp(attPollHist[i].getTime());
        } 
        catch (Exception ex)
        {
           histElem.setTimestamp(0);
        }
           
        try
        {
           attq = attPollHist[i].getAttrQuality();
           switch (attq.value())
           {
               case AttrQuality._ATTR_VALID    : histElem.setState(IAttribute.VALID); break;
               case AttrQuality._ATTR_INVALID  : histElem.setState(IAttribute.INVALID); break;
               case AttrQuality._ATTR_ALARM    : histElem.setState(IAttribute.ALARM); break;
               case AttrQuality._ATTR_WARNING  : histElem.setState(IAttribute.WARNING); break;
               case AttrQuality._ATTR_CHANGING : histElem.setState(IAttribute.CHANGING); break;
               default                         : histElem.setState(IAttribute.UNKNOWN); break;                      
           }
        }
        catch (Exception ex)
        {
           histElem.setState(IAttribute.UNKNOWN);
        }
           
        try
        {
           int     ulongVal;
           double  doubleVal;
           ulongVal = attPollHist[i].extractULong();
           doubleVal = (double) ulongVal;
           histElem.setValue(doubleVal);
        }
        catch (Exception ex)
        {
           histElem.setValue(Double.NaN);
        }
        hist.add(i, histElem);
     } // for

     NumberScalarHistory[] histArray;
     histArray = hist.toArray(new NumberScalarHistory[0]);
     return histArray;
  }

  protected IAttributeScalarHistory[] getScalarDeviceAttHistory(DeviceDataHistory[] attPollHist)
  {
    return (getNumberScalarDeviceAttHistory(attPollHist));
  }

  protected IAttributeScalarHistory[] getScalarAttHistory(DeviceDataHistory[] attPollHist)
  {
    return (getNumberScalarAttHistory(attPollHist));
  }
  


  public String getVersion()
  {
    return "$Id$";
  }

}
