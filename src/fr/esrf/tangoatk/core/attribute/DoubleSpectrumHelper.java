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

/* Modified to add support for display_unit property
  void insert(double[] d) {
    deviceAttribute.insert(d,
      ((IAttribute) attribute).getXDimension(),
      ((IAttribute) attribute).getYDimension());
  }
  */
  
  void insert(double[] d)
  {
      double   dUnitFactor=1.0;

      dUnitFactor = this.attribute.getDisplayUnitFactor();
      DeviceAttribute da = this.attribute.getAttribute();
      
      if (dUnitFactor == 1.0)
      {
	  da.insert(d, this.attribute.getXDimension(),
	               this.attribute.getYDimension()  );
      }
      else
      {
	  double[] tmp = new double[d.length];
	  for (int i = 0; i < tmp.length; i++)
	  {
              tmp[i] = (d[i] / dUnitFactor);
	  }
	  da.insert(tmp, this.attribute.getXDimension(),
	                 this.attribute.getYDimension()  );
      }
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


  double[] getNumberSpectrumValue(DeviceAttribute attribute) throws DevFailed {
    return attribute.extractDoubleArray();
  }


  double[] getNumberSpectrumDisplayValue(DeviceAttribute deviceAttribute) throws DevFailed
  {
     double[]  tmp;
     double    dUnitFactor;
     double[]  retSpectVal;

     dUnitFactor = this.attribute.getDisplayUnitFactor();
     
     if (dUnitFactor == 1.0)
         return deviceAttribute.extractDoubleArray();
     else
     {
	 tmp = deviceAttribute.extractDoubleArray();
	 retSpectVal = new double[tmp.length];

	 for (int i = 0; i < tmp.length; i++)
	 {
	   retSpectVal[i] = tmp[i] * dUnitFactor; //return the value in the display unit
	 }
	 return retSpectVal;
     }
  }

  public String getVersion() {
    return "$Id$";
  }

}
