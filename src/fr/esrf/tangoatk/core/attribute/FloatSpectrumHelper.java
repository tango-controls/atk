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

  public FloatSpectrumHelper(IAttribute attribute) {
    init(attribute);
  }

  void init(IAttribute attribute) {
    super.init(attribute);
    imageHelper = new FloatImageHelper(attribute);
  }

  void insert(double[] d) {

    float[] tmp = new float[d.length];
    for (int i = 0; i < tmp.length; i++) {
      tmp[i] = (float)d[i];
    }

    deviceAttribute.insert(d,
      attribute.getXDimension(),
      attribute.getYDimension());
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


  double[] getNumberSpectrumValue(DeviceAttribute attribute) throws DevFailed {
    float[] tmp = attribute.extractFloatArray();
    double[] retval = new double[tmp.length];
    for (int i = 0; i < tmp.length; i++) {
      retval[i] = (double) tmp[i];
    }
    return retval;
  }

  public String getVersion() {
    return "$Id$";
  }

}
