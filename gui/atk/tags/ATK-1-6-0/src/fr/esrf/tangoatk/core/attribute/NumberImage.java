// File:          ShortSpectrum.java
// Created:       2001-10-10 13:50:57, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-16 10:31:19, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;
import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

import java.beans.*;

public class NumberImage extends AAttribute
  implements INumberImage {
  protected NumberAttributeHelper numberHelper;
  double[][] imageValue;

  public void init(fr.esrf.tangoatk.core.Device d, String name,
                   AttributeInfo config) {
    super.init(d, name, config);
  }

  public void setNumberHelper(ANumberImageHelper helper) {
    numberHelper = helper;
  }

  public void addNumberImageListener(IImageListener l) {
    numberHelper.addImageListener(l);
  }

  public void removeNumberImageListener(IImageListener l) {
    numberHelper.removeImageListener(l);
  }

  protected void insert(String[][] s) {
    checkDimensions(s);
    insert(NumberAttributeHelper.flatten2double(s));
  }

  public void refresh() {

    if (skippingRefresh) return;

    try {
      imageValue = numberHelper.
        getNumberImageValue(readValueFromNetwork());
      numberHelper.fireImageValueChanged(imageValue, timeStamp);
    } catch (DevFailed e) {
      readException.setError(e);
      readAttError(e.getMessage(), readException);
      imageValue = null;
    } catch (Exception e) {
      readAttError(e.getMessage(), e);
      imageValue = null;
    }
//	refreshProperties();
  }



/* Replaced by F. Poncet on 06/jan/2003
    public void setValue(double[][] d) throws AttributeSetException {
	try {
	    checkDimensions(d);
	    insert(NumberAttributeHelper.flatten(d));
	    writeAtt();
	    numberHelper.fireImageValueChanged(d, System.currentTimeMillis());
	} catch (DevFailed df) {
	    throw new AttributeSetException(df);
	}
    }
*/

  public void setValue(double[][] d) {
    try {
      checkDimensions(d);
      insert(NumberAttributeHelper.flatten(d));
      writeAtt();
      numberHelper.fireImageValueChanged(d, System.currentTimeMillis());
    } catch (DevFailed df) {
      setAttError("Couldn't set value", new AttributeSetException(df));
    }
  }


  protected double getNumberProperty(String s) {
    NumberProperty p =
      (NumberProperty) getProperty(s);
    if (p != null && p.isSpecified())
      return ((Number) p.getValue()).doubleValue();

    return Double.NaN;
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

  public void setConfiguration(AttributeInfo c) {
    super.setConfiguration(c);

    try {
      setMinValue(new Double(config.min_value).doubleValue(), true);
    } catch (NumberFormatException e) {
      setMinValue(Double.NaN, true);
      getProperty("min_value").setSpecified(false);
    } // end of try-catch

    try {
      setMaxValue(new Double(config.max_value).doubleValue(), true);
    } catch (NumberFormatException e) {
      setMaxValue(Double.NaN, true);
      getProperty("max_value").setSpecified(false);
    } // end of try-catch

    try {
      setMinAlarm(new Double(config.min_alarm).doubleValue(), true);
    } catch (NumberFormatException e) {
      setMinAlarm(Double.NaN, true);
      getProperty("min_alarm").setSpecified(false);
    } // end of try-catch

    try {
      setMaxAlarm(new Double(config.max_alarm).doubleValue(), true);
    } catch (NumberFormatException e) {
      setMaxAlarm(Double.NaN, true);
      getProperty("max_alarm").setSpecified(false);
    } // end of try-catch

  }

  public void setMinValue(double d) {
    numberHelper.setMinValue(d);
  }

  public void setMaxValue(double d) {
    numberHelper.setMaxValue(d);
  }

  public void setMinAlarm(double d) {
    numberHelper.setMinAlarm(d);
  }

  public void setMaxAlarm(double d) {
    numberHelper.setMaxAlarm(d);
  }

  public void setMinValue(double d, boolean writable) {
    numberHelper.setMinValue(d, writable);
  }

  public void setMaxValue(double d, boolean writable) {
    numberHelper.setMaxValue(d, writable);
  }

  public void setMinAlarm(double d, boolean writable) {
    numberHelper.setMinAlarm(d, writable);
  }

  public void setMaxAlarm(double d, boolean writable) {
    numberHelper.setMaxAlarm(d, writable);
  }


  public double getStandardUnit() {
    NumberProperty p =
      (NumberProperty) getProperty("standard_unit");
    if (p.isSpecified())
      return ((Number) p.getValue()).doubleValue();

    return Double.NaN;
  }

  public double[][] getStandardValue() {
    double[][] retval = getValue();
    for (int i = 0; i < retval.length; i++)
      for (int j = 0; j < retval.length; j++)
        retval[i][j] *= getStandardUnit();

    return retval;
  }

  void insert(double[] d) {
    numberHelper.insert(d);
  }

  public String[][] extract() throws DevFailed {
    return numberHelper.getImageValue(readValueFromNetwork());

  }

  public double[][] getValue() {
    return imageValue;
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
