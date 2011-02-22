// File:          UCharSpectrumHelper.java
// Created:       2002-01-24 09:45:56, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 16:56:18, assum>
//
// $Id$
//
// Description:
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

public class UCharSpectrumHelper extends ANumberSpectrumHelper {

  public UCharSpectrumHelper(IAttribute attribute) {
    init(attribute);
  }

  void init(IAttribute attribute) {
    super.init(attribute);
    imageHelper = new UCharImageHelper(attribute);
  }

/* Modified to add support for display_unit property
  protected void insert(double[] d) {
    short[] tmp = new short[d.length];
    for (int i = 0; i < tmp.length; i++) {
      tmp[i] = new Double(d[i]).shortValue();
    }

    deviceAttribute.insert_uc(tmp, attribute.getXDimension(),
      attribute.getYDimension());
  }
*/

  void insert(double[] d)
  {
      double     dUnitFactor=1.0;
      short[]    tmp = new short[d.length];
      
      dUnitFactor = this.attribute.getDisplayUnitFactor();
      DeviceAttribute da = this.attribute.getAttribute();
      
      for (int i = 0; i < tmp.length; i++)
      {
           tmp[i] = (short) (d[i] / dUnitFactor);
      }

      da.insert_uc(tmp, this.attribute.getXDimension(),
	                this.attribute.getYDimension()  );
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

  double[] getNumberSpectrumValue(DeviceAttribute deviceAttribute) throws DevFailed {

      short[] tmp = deviceAttribute.extractUCharArray();
      double[] retval = new double[tmp.length];
      for (int i = 0; i < tmp.length; i++) {
        retval[i] = (double) tmp[i];
      }
      return retval;

  }

  double[] getNumberSpectrumDisplayValue(DeviceAttribute deviceAttribute) throws DevFailed
  {
     short[]   tmp;
     double    dUnitFactor;
     double[]  retSpectVal;

     tmp = deviceAttribute.extractUCharArray();
     dUnitFactor = this.attribute.getDisplayUnitFactor();
     retSpectVal = new double[tmp.length];
     
     for (int i = 0; i < tmp.length; i++)
     {
         retSpectVal[i] = (double) tmp[i] * dUnitFactor; //return the value in the display unit
     }
     return retSpectVal;
  }

  public String getVersion() {
    return "$Id$";
  }
}
