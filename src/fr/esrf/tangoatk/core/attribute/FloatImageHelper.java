// File:          FloatImageHelper.java
// Created:       2002-01-24 10:13:21, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-16 10:32:25, assum>
//
// $Id$
//
// Description:
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

class FloatImageHelper extends ANumberImageHelper {

  public FloatImageHelper(IAttribute attribute) {
    init(attribute);
  }

/* Modified to add support for display_unit property
  protected void insert(double[] d) {
    float[] tmp = new float[d.length];
    for (int i = 0; i < tmp.length; i++) {
      tmp[i] = (float)d[i];
    }

    deviceAttribute.insert(tmp, attribute.getXDimension(),
      attribute.getYDimension());
  }
  */
  
  protected void insert(double[][] d)
  {
      double     dUnitFactor=1.0;
      double[]   flatd;

      DeviceAttribute da = this.attribute.getAttribute();
      dUnitFactor = this.attribute.getDisplayUnitFactor();
      
      flatd = NumberAttributeHelper.flatten(d);
      float[] tmp = new float[flatd.length];
      
      for (int i = 0; i < tmp.length; i++)
      {
          tmp[i] = (float) (flatd[i] / dUnitFactor);
      }
      
      da.insert(tmp, d.length, d[0].length);
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

  double[][] getNumberImageValue(DeviceAttribute deviceAttribute) throws DevFailed {
    float[] tmp;
    tmp = deviceAttribute.extractFloatArray();
    int ydim = attribute.getYDimension();
    int xdim = attribute.getXDimension();

    if (ydim != retval.length || xdim != retval[0].length) {

      retval = new double[ydim][xdim];
    }

    int k = 0;
    for (int y = 0; y < ydim; y++)
      for (int x = 0; x < xdim; x++) {
        retval[y][x] = tmp[k++];
      }

    return retval;
  }


  double[][] getNumberImageDisplayValue(DeviceAttribute deviceAttribute) throws DevFailed {
    float[]  tmp;
    double   dUnitFactor;
    
    tmp = deviceAttribute.extractFloatArray();
    dUnitFactor = this.attribute.getDisplayUnitFactor();
    int ydim = attribute.getYDimension();
    int xdim = attribute.getXDimension();

    if (ydim != retval.length || xdim != retval[0].length) {

      retval = new double[ydim][xdim];
    }

    int k = 0;
    for (int y = 0; y < ydim; y++)
      for (int x = 0; x < xdim; x++) {
        retval[y][x] = tmp[k++] * dUnitFactor; //return the value in the display unit
      }

    return retval;
  }

  String[][] getImageValue(DeviceAttribute deviceAttribute) throws DevFailed {
    float[] tmp;
    tmp = deviceAttribute.extractFloatArray();
    int ydim = attribute.getYDimension();
    int xdim = attribute.getXDimension();
    String[][] retval = new String[ydim][xdim];

    int k = 0;
    for (int i = 0; i < ydim; i++)
      for (int j = 0; j < xdim; j++) {
        retval[i][j] = Float.toString(tmp[k++]);
      }

    return retval;
  }

  public String getVersion() {
    return "$Id$";
  }

}
