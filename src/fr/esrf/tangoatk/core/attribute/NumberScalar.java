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
  double              scalarValue;
  double              setPointValue;
  ANumberScalarHelper numberScalarHelper;
  double[]            possibleValues = null;


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



  public final void refresh()
  {
      DeviceAttribute           att = null;
      
      if (skippingRefresh) return;
      try
      {
	  try
	  {
	      // Read the attribute from device cache (readValueFromNetwork)
	      att = readValueFromNetwork();
	      if (att == null) return;
	      
	      // Retreive the read value for the attribute
	      scalarValue = numberScalarHelper.getNumberScalarValue(att);
	      setPointValue = numberScalarHelper.getNumberScalarSetPoint(att);

	      // Fire valueChanged
	      numberScalarHelper.fireScalarValueChanged(scalarValue, timeStamp);
	  }
	  catch (DevFailed e)
	  {
	      // Tango error
	      readException.setError(e);
	      scalarValue = Double.NaN;
	      setPointValue = Double.NaN;

	      // Fire error event
	      readAttError(e.getMessage(), readException);
	  }
      }
      catch (Exception e)
      {
	  // Code failure
	  scalarValue = Double.NaN;
	  setPointValue = Double.NaN;

	  System.out.println("NumberScalar.refresh() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("NumberScalar.refresh()------------------------------------------------");
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
    } 
    catch (DevFailed e)
    {
      setAttError("Couldn't set value", new AttributeSetException(e));
    }
    catch (Exception e)
    {
 System.out.println("Received un exception other than DevFailed while setting a numberScalar");
      setAttError("Couldn't set value", new AttributeSetException("Set Exception other than DevFailed."));
    }
  }

  protected fr.esrf.TangoApi.DeviceAttribute scalarInsert(String s)
    throws fr.esrf.Tango.DevFailed {
    insert(s);
    return attribute;
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



  public double getNumberScalarValue()
  {
      return scalarValue;
  }

  public double getStandardNumberScalarValue()
  {
      return getNumberScalarValue() * getStandardUnit();
  }




  public double getNumberScalarSetPoint()
  {
      return setPointValue;
  }

  public double getStandardNumberScalarSetPoint()
  {
       return getNumberScalarSetPoint() * getStandardUnit();
  }
    



  public double getNumberScalarDeviceSetPoint()
  {
      double setPoint;
      try
      {
	  setPoint =
	  numberScalarHelper.getNumberScalarSetPoint(readDeviceValueFromNetwork());
	  setPointValue = setPoint;
      }
      catch (DevFailed e)
      {
	  readException.setError(e);
	  readAttError(e.getMessage(), readException);
	  setPoint = Double.NaN;
	  setPointValue = Double.NaN;
      }
      catch (Exception e)
      {
	  readAttError(e.getMessage(), e);
	  setPoint = Double.NaN;
	  setPointValue = Double.NaN;
      } // end of catch

      return setPoint;
  }

  public double getStandardNumberScalarDeviceSetPoint()
  {
      return getNumberScalarDeviceSetPoint() * getStandardUnit();
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
  
  
  public void setPossibleValues(double[]  vals)
  {
      if (possibleValues == null)
      {
         if (vals.length > 0)
	    possibleValues = vals;
      }
  }
  
  
  public double[] getPossibleValues()
  {
      return possibleValues;
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
