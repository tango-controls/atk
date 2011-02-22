// File:          NumberAttribute.java
// Created:       2001-10-08 16:35:01, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-10 15:40:49, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;
import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

import java.beans.*;

public class NumberScalar extends NumberSpectrum
  implements INumberScalar {
  double scalarValue;
  ANumberScalarHelper numberScalarHelper;


  public void addSpectrumListener(ISpectrumListener l) {
    numberScalarHelper.addSpectrumListener(l);
    addStateListener(l);
  }

  public void removeSpectrumListener(ISpectrumListener l) {
    numberScalarHelper.removeSpectrumListener(l);
    removeStateListener(l);
  }

  public void addImageListener(IImageListener l) {
    numberScalarHelper.addImageListener(l);
    addStateListener(l);
  }

  public void removeImageListener(IImageListener l) {
    numberScalarHelper.removeImageListener(l);
    removeStateListener(l);
  }

  public double[] getStandardSpectrumValue() {
    return null;

  }

  public IScalarAttribute getWritableAttribute() {
    return null;
  }

  public IScalarAttribute getReadableAttribute() {
    return null;
  }

  public int getXDimension() {
    return 1;
  }

  public int getMaxXDimension() {
    return 1;
  }

  public double[][] getStandardValue() {
    return null;
  }

  public double[][] getValue() {
    double[][] d = new double[1][1];
    d[0][0] = getNumberScalarValue();
    return d;
  }

  public void setNumberHelper(ANumberScalarHelper helper) {
    numberHelper = helper;
    numberScalarHelper = helper;
  }

  public void addNumberScalarListener(INumberScalarListener l) {
    numberScalarHelper.addNumberScalarListener(l);
    addStateListener(l);
  }

  public void removeNumberScalarListener(INumberScalarListener l) {
    numberScalarHelper.removeNumberScalarListener(l);
    removeStateListener(l);
  }

  public double[][] getNumberValue() {
    double[][] retval = new double[1][1];
    retval[0][0] = getNumberScalarValue();
    return retval;
  }

  public double[] getSpectrumValue() {
    double[] retval = new double[1];
    retval[0] = getNumberScalarValue();
    return retval;
  }

  public Number getNumber() {
    try {
      return new Double(getNumberScalarValue());
    } catch (Exception d) {
      readAttError("Couldn't read from network",
        new ConnectionException(d));
    } // end of try-catch
    return new Double(Double.NaN);

  }

  public void setNumber(Number n) throws IllegalArgumentException {
    double d = n.doubleValue();
// 	if (!Double.isNaN(getMaxValue()) && !(d < getMaxValue()) ||
// 	    !Double.isNaN(getMinValue())&& !(d > getMinValue())) {
// 	    throw new IllegalArgumentException();
// 	}


    setValue(d);
  }

  public void setValue(double d[]) {
    setValue(d[0]);
  }

  public void setValue(double d[][]) {
    setValue(d[0][0]);
  }

  protected String scalarExtract() {
    return new Double(getNumberScalarValue()).toString();
  }

  public final void refresh() {

    if (skippingRefresh) return;

    try {
      scalarValue = numberScalarHelper.
        getNumberScalarValue(readValueFromNetwork());
      numberScalarHelper.fireScalarValueChanged(scalarValue, timeStamp);
    } catch (DevFailed e) {
      readException.setError(e);
      readAttError(e.getMessage(), readException);
      scalarValue = Double.NaN;
    } catch (Exception e) {
      readAttError(e.getMessage(), e);
      scalarValue = Double.NaN;
    } // end of catch
  }

  public double getMinValue() {
    return getNumberProperty("min_value");
  }

  public double getMaxValue() {
    return getNumberProperty("max_value");
  }

  public double getMinAlarm() {
    return getNumberProperty("min_alarm");
  }

  public double getMaxAlarm() {
    return getNumberProperty("max_alarm");
  }

  protected double getNumberProperty(String s) {
    NumberProperty p =
      (NumberProperty) getProperty(s);
    if (p != null && p.isSpecified())
      return ((Number) p.getValue()).doubleValue();

    return Double.NaN;
  }


  public void setValue(double d) {
    try {
      insert(d);
      writeAtt();
      refresh();
    } catch (DevFailed e) {
      setAttError("Couldn't set value", new AttributeSetException(e));
    }
  }

  protected fr.esrf.TangoApi.DeviceAttribute scalarInsert(String s)
    throws fr.esrf.Tango.DevFailed {
    insert(s);
    return attribute;
  }

  public double getStandardNumberScalarValue() {
    return getNumberScalarValue() * getStandardUnit();
  }

  protected void insert(double[] d) {
    insert(d[0]);
  }

  protected void insert(double d) {
    numberScalarHelper.insert(d);
  }

  protected void insert(String s) {
    insert(Double.parseDouble(s));
  }

  public double getNumberScalarValue() {
    return scalarValue;
  }


  public double getNumberScalarSetpoint() {
    double setPoint;
    try {
      setPoint =
        numberScalarHelper.getNumberScalarSetPoint(readDeviceValueFromNetwork());
    } catch (DevFailed e) {
      readException.setError(e);
      readAttError(e.getMessage(), readException);
      setPoint = Double.NaN;
    } catch (Exception e) {
      readAttError(e.getMessage(), e);
      setPoint = Double.NaN;
    } // end of catch

    return setPoint;
  }


  public double getStandardNumberScalarSetpoint() {
    return getNumberScalarSetpoint() * getStandardUnit();
  }


  public INumberScalarHistory[] getNumberScalarHistory() {
    NumberScalarHistory[] attHist;

    attHist = null;
    try {
      attHist =
        (NumberScalarHistory[]) numberScalarHelper.getScalarAttHistory(readAttHistoryFromNetwork());
    } catch (DevFailed e) {
      readException.setError(e);
      readAttError(e.getMessage(), readException);
      attHist = null;
    } catch (Exception e) {
      readAttError(e.getMessage(), e);
      attHist = null;
    } // end of catch

    return attHist;
  }


  public String getVersion() {
    return "$Id$";
  }

  private void readObject(java.io.ObjectInputStream in)
    throws java.io.IOException, ClassNotFoundException {
    System.out.print("Loading attribute ");
    in.defaultReadObject();
    serializeInit();
  }


}
