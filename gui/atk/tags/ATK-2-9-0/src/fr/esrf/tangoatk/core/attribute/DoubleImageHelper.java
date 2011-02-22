// File:          DoubleImageHelper.java
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

class DoubleImageHelper extends ANumberImageHelper {

  public DoubleImageHelper(IAttribute attribute) {
    init(attribute);
  }

/* Modified to add support for display_unit property
  protected void insert(double[] d)
  {
    double[] tmp = new double[d.length];
    for (int i = 0; i < tmp.length; i++) {
      tmp[i] = new Double(d[i]).doubleValue();
    }

    deviceAttribute.insert(tmp, attribute.getXDimension(),
      attribute.getYDimension());
  }
  */
  
  protected void insert(double[] d)
  {
      double   dUnitFactor=1.0;
    
      DeviceAttribute da = this.attribute.getAttribute();
      dUnitFactor = this.attribute.getDisplayUnitFactor();
      double[] tmp = new double[d.length];
      
      for (int i = 0; i < tmp.length; i++)
      {
          tmp[i] = (d[i] / dUnitFactor);
      }

      da.insert(tmp, this.attribute.getXDimension(),
                     this.attribute.getYDimension()  );
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


  double[][] getNumberImageValue(DeviceAttribute deviceAttribute) throws DevFailed {
    double[] tmp;
    tmp = deviceAttribute.extractDoubleArray();
    int ydim = attribute.getYDimension();
    int xdim = attribute.getXDimension();

    if (ydim != retval.length || retval.length == 0 || xdim != retval[0].length) {

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
    double[] tmp;
    double   dUnitFactor;
    
    tmp = deviceAttribute.extractDoubleArray();
    dUnitFactor = this.attribute.getDisplayUnitFactor();
    int ydim = attribute.getYDimension();
    int xdim = attribute.getXDimension();

    if (ydim != retval.length || retval.length == 0 || xdim != retval[0].length) {

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
    double[] tmp;
    tmp = deviceAttribute.extractDoubleArray();
    int ydim = attribute.getYDimension();
    int xdim = attribute.getXDimension();
    String[][] retval = new String[ydim][xdim];

    int k = 0;
    for (int i = 0; i < ydim; i++)
      for (int j = 0; j < xdim; j++) {
        retval[i][j] = Double.toString(tmp[k++]);
      }

    return retval;
  }

  public String getVersion() {
    return "$Id$";
  }

}
