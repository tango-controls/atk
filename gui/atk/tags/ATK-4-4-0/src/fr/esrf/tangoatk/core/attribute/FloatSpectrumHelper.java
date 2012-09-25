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
 
// File:          FloatSpectrumHelper.java
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

public class FloatSpectrumHelper extends ANumberSpectrumHelper {

  public FloatSpectrumHelper(AAttribute attribute) {
    init(attribute);
  }

    @Override
  void init(AAttribute attribute) {
    super.init(attribute);
  }

  void insert(double[] d)
  {
      double   dUnitFactor=1.0;
      float[]  tmp = new float[d.length];
      
      dUnitFactor = this.attribute.getDisplayUnitFactor();
      DeviceAttribute da = this.attribute.getAttribute();
      
      for (int i = 0; i < tmp.length; i++)
      {
           tmp[i] = (float) (d[i] / dUnitFactor);
      }

      da.insert(tmp);
  }

  protected INumberSpectrumHistory[] getNumberSpectrumAttHistory(DeviceDataHistory[] attPollHist) {

    NumberSpectrumHistory[] hist = new NumberSpectrumHistory[attPollHist.length];
    NumberSpectrumHistory histElem;
    fr.esrf.Tango.AttrQuality attq;
    int i;
    double    dUnitFactor=1.0;

    if (attPollHist.length <= 0)
      return null;

    dUnitFactor = this.attribute.getDisplayUnitFactor();
    if (dUnitFactor <= 0) dUnitFactor = 1.0;

    for (i = 0; i < attPollHist.length; i++) {

      histElem = new NumberSpectrumHistory();

      try {
        histElem.setTimestamp(attPollHist[i].getTime());
      } catch (Exception ex) {
        histElem.setTimestamp(0);
      }

      try {
        attq = attPollHist[i].getAttrQuality();

        if (AttrQuality._ATTR_VALID == attq.value()) {
          histElem.setState(IAttribute.VALID);
        } else {
          if (AttrQuality._ATTR_INVALID == attq.value()) {
            histElem.setState(IAttribute.INVALID);
          } else {
            if (AttrQuality._ATTR_ALARM == attq.value()) {
              histElem.setState(IAttribute.ALARM);
            } else {
              if (AttrQuality._ATTR_WARNING == attq.value()) {
                histElem.setState(IAttribute.WARNING);
              } else {
                if (AttrQuality._ATTR_CHANGING == attq.value()) {
                  histElem.setState(IAttribute.CHANGING);
                } else
                  histElem.setState(IAttribute.UNKNOWN);
              }
            }
          }
        }

      } catch (Exception ex) {
        histElem.setState(IAttribute.UNKNOWN);
      }


      try {
        float[] vals = attPollHist[i].extractFloatArray();
        double[] newVals = new double[vals.length];
        for(int j=0;j<vals.length;j++) newVals[j] = (double)vals[j] * dUnitFactor;
        histElem.setValue(newVals);
      } catch (Exception ex) {
        histElem.setValue(new double[0]);
      }

      hist[i] = histElem;
    }

    return hist;
  }

  protected INumberSpectrumHistory[] getNumberSpectrumDeviceAttHistory(DeviceDataHistory[] attPollHist) {

    NumberSpectrumHistory[] hist = new NumberSpectrumHistory[attPollHist.length];
    NumberSpectrumHistory histElem;
    fr.esrf.Tango.AttrQuality attq;
    int i;

    if (attPollHist.length <= 0)
      return null;

    for (i = 0; i < attPollHist.length; i++) {

      histElem = new NumberSpectrumHistory();

      try {
        histElem.setTimestamp(attPollHist[i].getTime());
      } catch (Exception ex) {
        histElem.setTimestamp(0);
      }

      try {
        attq = attPollHist[i].getAttrQuality();

        if (AttrQuality._ATTR_VALID == attq.value()) {
          histElem.setState(IAttribute.VALID);
        } else {
          if (AttrQuality._ATTR_INVALID == attq.value()) {
            histElem.setState(IAttribute.INVALID);
          } else {
            if (AttrQuality._ATTR_ALARM == attq.value()) {
              histElem.setState(IAttribute.ALARM);
            } else {
              if (AttrQuality._ATTR_WARNING == attq.value()) {
                histElem.setState(IAttribute.WARNING);
              } else {
                if (AttrQuality._ATTR_CHANGING == attq.value()) {
                  histElem.setState(IAttribute.CHANGING);
                } else
                  histElem.setState(IAttribute.UNKNOWN);
              }
            }
          }
        }

      } catch (Exception ex) {
        histElem.setState(IAttribute.UNKNOWN);
      }


      try {
        float[] vals = attPollHist[i].extractFloatArray();
        double[] newVals = new double[vals.length];
        for(int j=0;j<vals.length;j++) newVals[j] = (double)vals[j];
        histElem.setValue(newVals);
      } catch (Exception ex) {
        histElem.setValue(new double[0]);
      }

      hist[i] = histElem;
    }

    return hist;

  }

  protected IAttributeSpectrumHistory[] getSpectrumDeviceAttHistory(DeviceDataHistory[] attPollHist) {
    return (getNumberSpectrumDeviceAttHistory(attPollHist));
  }

  protected IAttributeSpectrumHistory[] getSpectrumAttHistory(DeviceDataHistory[] attPollHist) {
    return (getNumberSpectrumAttHistory(attPollHist));
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


  double[] getNumberSpectrumValue(DeviceAttribute da) throws DevFailed
  {
      float[] tmp = da.extractFloatArray();
      int   nbReadElements = da.getNbRead();
      double[] retval = new double[nbReadElements];
      for (int i = 0; i < nbReadElements; i++)
      {
          retval[i] = (double) tmp[i];
      }
      return retval;
  }


  double[] getNumberSpectrumSetPoint(DeviceAttribute da) throws DevFailed
  {
      float[] tmp = da.extractFloatArray();
      int   nbReadElements = da.getNbRead();
      int   nbSetElements = tmp.length - nbReadElements;
      
      // The attributes WRITE (WRITE ONLY) return their setPoint in the first sequence of elements
      // In all cases when no "set" element sequence is returned, return the read elements for setPoint
      if (nbSetElements <= 0)
      {
          return getNumberSpectrumValue(da);
      }
      else
      {
         double[] retval = new double[nbSetElements];
         int j = 0;
         for (int i = nbReadElements; i < tmp.length; i++)
         {
             retval[j] = (double) tmp[i];
             j++;
         }
         return retval;        
      }
  }

  double[] getNumberSpectrumDisplayValue(DeviceAttribute da) throws DevFailed
  {
     float[]   tmp;
     double    dUnitFactor;
     double[]  retSpectVal;

     dUnitFactor = this.attribute.getDisplayUnitFactor();
     tmp = da.extractFloatArray();
     int   nbReadElements = da.getNbRead();
     retSpectVal = new double[nbReadElements];
     
     for (int i = 0; i < nbReadElements; i++)
     {
         retSpectVal[i] = (double) tmp[i] * dUnitFactor; //return the value in the display unit
     }
     return retSpectVal;
  }


  double[] getNumberSpectrumDisplaySetPoint(DeviceAttribute da) throws DevFailed
  {
      double     dUnitFactor;
      float[]    tmp = da.extractFloatArray();
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
         int j = 0;
         for (int i = nbReadElements; i < tmp.length; i++)
         {
             retval[j] = (double) tmp[i] * dUnitFactor; //return the value in the display unit
             j++;
         }
         return retval;        
      }
  }

  
  public String getVersion() {
    return "$Id$";
  }

}
