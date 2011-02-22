// File:          LongImageHelper.java
// Created:       2002-01-24 10:12:49, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-16 10:32:15, assum>
//
// $Id$
//
// Description:
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

import java.beans.*;

class LongImageHelper extends ANumberImageHelper {

  public LongImageHelper(IAttribute attribute) {
    init(attribute);
  }

  void insert(double[] d) {
    int[] tmp = new int[d.length];
    for (int i = 0; i < tmp.length; i++) {
      tmp[i] = new Double(d[i]).intValue();
    }

    deviceAttribute.insert(tmp, attribute.getXDimension(),
      attribute.getYDimension());
  }

  void setMinAlarm(double d) {
    setProperty("min_alarm", new Long((long) d));
  }

  void setMaxAlarm(double d) {
    setProperty("max_alarm", new Long((long) d));
  }

  void setMinValue(double d) {
    setProperty("min_value", new Long((long) d));
  }

  void setMaxValue(double d) {
    setProperty("max_value", new Long((long) d));
  }

  void setMinAlarm(double d, boolean writable) {
    setProperty("min_alarm", new Long((long) d), writable);
  }

  void setMaxAlarm(double d, boolean writable) {
    setProperty("max_alarm", new Long((long) d), writable);
  }

  void setMinValue(double d, boolean writable) {
    setProperty("min_value", new Long((long) d), writable);
  }

  void setMaxValue(double d, boolean writable) {
    setProperty("max_value", new Long((long) d), writable);
  }

  double[][] getNumberImageValue(DeviceAttribute deviceAttribute) {

    int ydim = attribute.getYDimension();
    int xdim = attribute.getXDimension();

    if (ydim != retval.length || xdim != retval[0].length) {
      retval = new double[ydim][xdim];
    }

    int[] tmp = deviceAttribute.extractLongArray();

    int k = 0;
    for (int i = 0; i < ydim; i++)
      for (int j = 0; j < xdim; j++) {
        retval[i][j] = tmp[k++];
      }
    return retval;
  }

  String[][] getImageValue(DeviceAttribute deviceAttribute) {
    int[] tmp = deviceAttribute.extractLongArray();
    int ydim = attribute.getYDimension();
    int xdim = attribute.getXDimension();
    String[][] retval = new String[ydim][xdim];

    int k = 0;
    for (int i = 0; i < ydim; i++)
      for (int j = 0; j < xdim; j++) {
        retval[i][j] = Integer.toString(tmp[k++]);
      }
    return retval;
  }

  public String getVersion() {
    return "$Id$";
  }
}
