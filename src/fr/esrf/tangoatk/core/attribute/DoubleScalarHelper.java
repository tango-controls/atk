// File:          DoubleAttributeHelper.java
// Created:       2001-12-04 13:33:15, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-10 17:31:31, assum>
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

public class DoubleScalarHelper extends ANumberScalarHelper {

  public DoubleScalarHelper(IAttribute attribute) {
    init(attribute);
  }

  void init(IAttribute attribute) {
    super.init(attribute);
    spectrumHelper = new DoubleSpectrumHelper(attribute);
    imageHelper = new DoubleImageHelper(attribute);
  }

  void insert(double d) {
    attribute.getAttribute().insert(d);
  }

  final double getNumberScalarValue(DeviceAttribute attribute) throws DevFailed {

    double[] double_arr = null;

    double_arr = attribute.extractDoubleArray();

    if (double_arr == null)
      return Double.NaN;

    if (double_arr.length < 1)
      return Double.NaN;

    return double_arr[0];
  }

  double getNumberScalarSetPoint(DeviceAttribute attribute) throws DevFailed {

    double[] double_arr = null;

    double_arr = attribute.extractDoubleArray();

    if (double_arr == null)
      return Double.NaN;

    if (double_arr.length < 1)
      return Double.NaN;

    if (double_arr.length > 1)
      return double_arr[1];
    else // The attributes WRITE (WRITE ONLY) return their setPoint in the first element
      return double_arr[0];
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
            } else
              histElem.setState(IAttribute.UNKNOWN);
          }
        }
      } catch (Exception ex) {
        histElem.setState(IAttribute.UNKNOWN);
      }


      try {
        histElem.setValue(attPollHist[i].extractDouble());
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

  public String getVersion() {
    return "$Id$";
  }

}
