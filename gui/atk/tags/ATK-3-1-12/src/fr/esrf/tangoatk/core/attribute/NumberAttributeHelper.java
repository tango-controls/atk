// File:          NumberAttributeHelper.java
// Created:       2001-12-04 13:31:09, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-10 17:33:45, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;

import java.beans.*;

import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.Tango.DevFailed;

abstract class NumberAttributeHelper implements java.io.Serializable {
  IAttribute attribute;
  transient DeviceAttribute deviceAttribute;
  EventSupport propChanges;

  public void setAttribute(IAttribute attribute) {
    this.attribute = attribute;
  }

  void init(IAttribute attribute) {
    setAttribute(attribute);
    propChanges = ((AAttribute) attribute).getPropChanges();
  }

  public IAttribute getAttribute() {
    return attribute;
  }

  protected void setProperty(String name, Number value) {
    attribute.setProperty(name, value);
    attribute.storeConfig();
  }

  protected void setProperty(String name, Number value, boolean writable) {
    attribute.setProperty(name, value, writable);
  }

  public static double[] flatten(double[][] src) {
    int size = src.length * src[0].length;
    double[] dst = new double[size];

    for (int i = 0; i < src.length; i++)
      System.arraycopy(src[i], 0, dst, i * src.length, src.length);
    return dst;
  }


  public static double[] flatten2double(String[][] src) {
    int size = src.length * src[0].length;
    double[] dst = new double[size];
    int k = 0;
    for (int i = 0; i < src.length; i++)
      for (int j = 0; j < src[i].length; j++)
        dst[k++] = Double.parseDouble(src[i][j]);

    return dst;
  }


  public static double[][] str2double(String[][] src) {
    double[][] dst = new double[src.length][src[0].length];
    for (int i = 0; i < src.length; i++)
      for (int j = 0; j < src[i].length; j++)
        dst[i][j] = Double.parseDouble(src[i][j]);

    return dst;
  }

  public void addImageListener(IImageListener l) {
    propChanges.addImageListener(l);
  }

  public void removeImageListener(IImageListener l) {
    propChanges.removeImageListener(l);
  }

  abstract void setMinAlarm(double d, boolean writable);

  abstract void setMaxAlarm(double d, boolean writable);

  abstract void setMaxValue(double d, boolean writable);

  abstract void setMinValue(double d, boolean writable);

  abstract void setMinWarning(double d, boolean writable);

  abstract void setMaxWarning(double d, boolean writable);

  abstract void setDeltaT(double d, boolean writable);

  abstract void setDeltaVal(double d, boolean writable);

  abstract void setMinAlarm(double d);

  abstract void setMaxAlarm(double d);

  abstract void setMaxValue(double d);

  abstract void setMinValue(double d);

  abstract void setMinWarning(double d);

  abstract void setMaxWarning(double d);

  abstract void setDeltaT(double d);

  abstract void setDeltaVal(double d);


  void fireImageValueChanged(double[][] newValue, long timeStamp) {
    propChanges.fireImageEvent((INumberImage) attribute,
      newValue, timeStamp);
  }

  abstract void insert(double[] d);

  abstract double[][] getNumberImageValue(DeviceAttribute attribute) throws DevFailed;
  
  abstract String[][] getImageValue(DeviceAttribute attribute) throws DevFailed;


  public String getVersion() {
    return "$Id$";
  }

}
