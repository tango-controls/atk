// File:          ShortImageHelper.java
// Created:       2002-01-24 10:08:28, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-16 10:32:16, assum>
//
// $Id$
//
// Description:
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

class UShortImageHelper extends ANumberImageHelper {

  public UShortImageHelper(IAttribute attribute) {
    init(attribute);
  }

/* Modified to add support for display_unit property
  protected void insert(double[] d) {
    int[] tmp = new int[d.length];
    for (int i = 0; i < tmp.length; i++) {
      tmp[i] = new Double(d[i]).intValue();
    }

    deviceAttribute.insert_us(tmp, attribute.getXDimension(),
      attribute.getYDimension());
  }
  */

  protected void insert(double[] d)
  {
      double   dUnitFactor=1.0;

      DeviceAttribute da = this.attribute.getAttribute();
      dUnitFactor = this.attribute.getDisplayUnitFactor();
      int[] tmp = new int[d.length];
      
      for (int i = 0; i < tmp.length; i++)
      {
          tmp[i] = (int) (d[i] / dUnitFactor);
      }
      
      da.insert_us(tmp, this.attribute.getXDimension(),
	                this.attribute.getYDimension()  );
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

  double[][] getNumberImageValue(DeviceAttribute deviceAttribute) throws DevFailed {

    int ydim = attribute.getYDimension();
    int xdim = attribute.getXDimension();

    if (ydim != retval.length || xdim != retval[0].length) {
      retval = new double[ydim][xdim];
    }

    int[] tmp = deviceAttribute.extractUShortArray();

    int k = 0;
    for (int i = 0; i < ydim; i++)
      for (int j = 0; j < xdim; j++)
        retval[i][j] = (double)tmp[k++];

    return retval;
  }

  double[][] getNumberImageDisplayValue(DeviceAttribute deviceAttribute) throws DevFailed {
    int[]    tmp;
    double   dUnitFactor;
    
    tmp = deviceAttribute.extractUShortArray();
    dUnitFactor = this.attribute.getDisplayUnitFactor();

    int ydim = attribute.getYDimension();
    int xdim = attribute.getXDimension();

    if (ydim != retval.length || xdim != retval[0].length) {
      retval = new double[ydim][xdim];
    }


    int k = 0;
    for (int i = 0; i < ydim; i++)
      for (int j = 0; j < xdim; j++)
        retval[i][j] = (double)tmp[k++] * dUnitFactor; //return the value in the display unit

    return retval;
  }

  String[][] getImageValue(DeviceAttribute deviceAttribute) throws DevFailed {

    int[] tmp = deviceAttribute.extractUShortArray();

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
