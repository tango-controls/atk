// File:          ShortAttributeHelper.java
// Created:       2001-12-04 13:37:17, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-24 16:55:21, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;


import java.util.*;

import fr.esrf.tangoatk.core.*;

import fr.esrf.TangoApi.*;
import fr.esrf.Tango.AttrQuality;
import fr.esrf.Tango.DevFailed;

public class UCharScalarHelper extends ANumberScalarHelper {
  UCharImageHelper imageHelper;

  public UCharScalarHelper(IAttribute attribute) {
    init(attribute);
  }


  void init(IAttribute attribute) {
    super.init(attribute);
    spectrumHelper = new UCharSpectrumHelper(attribute);
    imageHelper = new UCharImageHelper(attribute);
  }

/* Modified to add support for display_unit property
  void insert(double d) {
    attribute.getAttribute().insert_uc((short)d);
  }
  */

  void insert(double d)
  {
     double   dUnitFactor=1.0;

     DeviceAttribute da = this.attribute.getAttribute();
     dUnitFactor = this.attribute.getDisplayUnitFactor();

     if (dUnitFactor == 1.0)
         da.insert_uc((short) d);
     else
     {
         short  ds = (short) (d / dUnitFactor);
	 da.insert_uc(ds);
     }
  }

  double getNumberScalarValue(DeviceAttribute devAtt)
  {
    short[] uchar_arr = null;
    
    try
    {
	uchar_arr = devAtt.extractUCharArray();
    }
    catch( DevFailed e )
    {
        return Double.NaN;
    }

    if (uchar_arr == null)
      return Double.NaN;

    if (uchar_arr.length < 1)
      return Double.NaN;

    return uchar_arr[0];
  }

  double getNumberScalarSetPoint(DeviceAttribute devAtt)
  {
    short[] uchar_arr = null;
    
    try
    {
	uchar_arr = devAtt.extractUCharArray();
    }
    catch( DevFailed e )
    {
        return Double.NaN;
    }

    if (uchar_arr == null)
      return Double.NaN;

    if (uchar_arr.length < 1)
      return Double.NaN;

    if (uchar_arr.length > 1)
      return uchar_arr[1];
    else // The attributes WRITE (WRITE ONLY) return their setPoint in the first element
      return uchar_arr[0];
  }

  double getNumberScalarDisplayValue(DeviceAttribute devAtt)
  {
     short[]   uchar_arr = null;
     double    dUnitFactor;

     dUnitFactor = this.attribute.getDisplayUnitFactor();
    
     try
     {
	 uchar_arr = devAtt.extractUCharArray();
     }
     catch( DevFailed e )
     {
         return Double.NaN;
     }

     if (uchar_arr == null)
       return Double.NaN;

     if (uchar_arr.length < 1)
       return Double.NaN;

     return (uchar_arr[0] * dUnitFactor);
  }

  double getNumberScalarDisplaySetPoint(DeviceAttribute devAtt)
  {

     short[]   uchar_arr = null;
     double    dUnitFactor;

     dUnitFactor = this.attribute.getDisplayUnitFactor();
    
     try
     {
	 uchar_arr = devAtt.extractUCharArray();
     }
     catch( DevFailed e )
     {
         return Double.NaN;
     }

     if (uchar_arr == null)
       return Double.NaN;

     if (uchar_arr.length < 1)
       return Double.NaN;

     if (uchar_arr.length > 1)
       return (uchar_arr[1] * dUnitFactor);
     else // The attributes WRITE (WRITE ONLY) return their setPoint in the first element
       return (uchar_arr[0] * dUnitFactor);
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

  protected INumberScalarHistory[] getNumberScalarAttHistory(DeviceDataHistory[] attPollHist) {

    List hist;
    NumberScalarHistory histElem;
    fr.esrf.Tango.AttrQuality attq;
    int i;

    if (attPollHist.length <= 0)
      return null;

    hist = new Vector();

    for (i = 0; i < attPollHist.length; i++) {
      histElem = new NumberScalarHistory();


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
        short ushortVal = 0;
        double doubleVal;
        // TODO:
        // ushortVal = attPollHist[i].extractUChar();
        doubleVal = (double) ushortVal;
        histElem.setValue(doubleVal);
      } catch (Exception ex) {
        histElem.setValue(Double.NaN);
      }

      hist.add(i, histElem);
    }

    NumberScalarHistory[] histArray;

    histArray = (NumberScalarHistory[]) hist.toArray(new NumberScalarHistory[0]);

    return histArray;
  }

  protected IAttributeScalarHistory[] getScalarAttHistory(DeviceDataHistory[] attPollHist) {
    return (getNumberScalarAttHistory(attPollHist));
  }


  public String getVersion() {
    return "$Id$";
  }

}
