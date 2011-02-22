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

public class ShortScalarHelper extends ANumberScalarHelper {
  ShortImageHelper imageHelper;

  public ShortScalarHelper(IAttribute attribute) {
    init(attribute);
  }


  void init(IAttribute attribute) {
    super.init(attribute);
    spectrumHelper = new ShortSpectrumHelper(attribute);
    imageHelper = new ShortImageHelper(attribute);
  }

/* Modified to add support for display_unit property
  void insert(double d) {
    attribute.getAttribute().insert((short) d);
  }
  */

  void insert(double d)
  {
     double   dUnitFactor=1.0;

     DeviceAttribute da = this.attribute.getAttribute();
     dUnitFactor = this.attribute.getDisplayUnitFactor();

     if (dUnitFactor == 1.0)
         da.insert((short) d);
     else
     {
         short  ds = (short) (d / dUnitFactor);
	 da.insert(ds);
     }
  }
  

  double getNumberScalarValue(DeviceAttribute devAtt)
  {
    short[] short_arr = null;
    
    try
    {
	short_arr = devAtt.extractShortArray();
    }
    catch( DevFailed e )
    {
        return Double.NaN;
    }

    if (short_arr == null)
      return Double.NaN;

    if (short_arr.length < 1)
      return Double.NaN;

    return short_arr[0];
  }

  double getNumberScalarSetPoint(DeviceAttribute devAtt)
  {
    short[] short_arr = null;
    
    try
    {
	short_arr = devAtt.extractShortArray();
    }
    catch( DevFailed e )
    {
        return Double.NaN;
    }

    if (short_arr == null)
      return Double.NaN;

    if (short_arr.length < 1)
      return Double.NaN;

    if (short_arr.length > 1)
      return short_arr[1];
    else // The attributes WRITE (WRITE ONLY) return their setPoint in the first element
      return short_arr[0];
  }

  double getNumberScalarDisplayValue(DeviceAttribute devAtt)
  {
     short[]   short_arr = null;
     double    dUnitFactor;

     dUnitFactor = this.attribute.getDisplayUnitFactor();
    
     try
     {
	 short_arr = devAtt.extractShortArray();
     }
     catch( DevFailed e )
     {
         return Double.NaN;
     }

     if (short_arr == null)
       return Double.NaN;

     if (short_arr.length < 1)
       return Double.NaN;

     return (short_arr[0] * dUnitFactor);
  }

  double getNumberScalarDisplaySetPoint(DeviceAttribute devAtt)
  {

     short[]   short_arr = null;
     double    dUnitFactor;

     dUnitFactor = this.attribute.getDisplayUnitFactor();
    
     try
     {
	 short_arr = devAtt.extractShortArray();
     }
     catch( DevFailed e )
     {
         return Double.NaN;
     }

     if (short_arr == null)
       return Double.NaN;

     if (short_arr.length < 1)
       return Double.NaN;

     if (short_arr.length > 1)
       return (short_arr[1] * dUnitFactor);
     else // The attributes WRITE (WRITE ONLY) return their setPoint in the first element
       return (short_arr[0] * dUnitFactor);
  }

  void setMinAlarm(double d) {
    setProperty("min_alarm", new Short((short) d));
  }

  void setMaxAlarm(double d) {
    setProperty("max_alarm", new Short((short) d));
  }

  void setMinValue(double d) {
    setProperty("min_value", new Short((short) d));
  }

  void setMaxValue(double d) {
    setProperty("max_value", new Short((short) d));
  }

  void setMinAlarm(double d, boolean writable) {
    setProperty("min_alarm", new Short((short) d), writable);
  }

  void setMaxAlarm(double d, boolean writable) {
    setProperty("max_alarm", new Short((short) d), writable);
  }

  void setMinValue(double d, boolean writable) {
    setProperty("min_value", new Short((short) d), writable);

  }

  void setMaxValue(double d, boolean writable) {
    setProperty("max_value", new Short((short) d), writable);
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
        short shortVal;
        double doubleVal;
        shortVal = attPollHist[i].extractShort();
        doubleVal = (double) shortVal;
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
