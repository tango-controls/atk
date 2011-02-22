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
    imageHelper = new DoubleImageHelper(attribute);
  }

  void insert(double[] d) {
    deviceAttribute.insert(d,
      ((IAttribute) attribute).getXDimension(),
      ((IAttribute) attribute).getYDimension());
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


  double[] getNumberSpectrumValue(DeviceAttribute attribute) {
    return attribute.extractDoubleArray();
  }

  public String getVersion() {
    return "$Id$";
  }

}
