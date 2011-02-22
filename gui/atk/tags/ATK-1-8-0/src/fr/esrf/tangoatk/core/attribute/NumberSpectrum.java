// File:          NumberArrayAttribute.java
// Created:       2001-10-10 10:41:58, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-10 15:38:39, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;
import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

import java.beans.*;

public class NumberSpectrum extends NumberImage
  implements INumberSpectrum {
  double[] spectrumValue;
  ANumberSpectrumHelper numberSpectrumHelper;

  public void init(fr.esrf.tangoatk.core.Device d, String name,
                   AttributeInfo config) {
    super.init(d, name, config);
  }

  public double[][] getStandardValue() {
    return numberSpectrumHelper.getStandardNumberValue();
  }


  public void setNumberHelper(ANumberSpectrumHelper helper) {
    numberHelper = helper;
    numberSpectrumHelper = helper;
  }

  public void addImageListener(IImageListener l) {
    numberSpectrumHelper.addImageListener(l);
  }

  public void removeImageListener(IImageListener l) {
    numberSpectrumHelper.removeImageListener(l);
  }

  public void addSpectrumListener(ISpectrumListener l) {
    propChanges.addSpectrumListener(l);
  }

  public void removeSpectrumListener(ISpectrumListener l) {
    propChanges.removeSpectrumListener(l);
  }


/* Replaced by F. Poncet on 06/jan/2003
    public void setValue(double[][] d) throws AttributeSetException {
	setValue(d[0]);
    }
*/

  public void setValue(double[][] d) {
    setValue(d[0]);
  }

  public double[][] getValue() {
    double[][] val = new double[1][];
    val[0] = getSpectrumValue();
    return val;
  }

/* Replaced by F. Poncet on 06/jan/2003
    public void setValue(double [] d) throws AttributeSetException {
	try {
	    insert(d);
	    writeAtt();
	    numberSpectrumHelper.fireSpectrumValueChanged(d,
						  System.currentTimeMillis());
	} catch (DevFailed df) {
	    throw new AttributeSetException(df);
	}
    }
*/

  public void setValue(double[] d) {
    try {
      insert(d);
      writeAtt();
      numberSpectrumHelper.fireSpectrumValueChanged(d,
        System.currentTimeMillis());
    } catch (DevFailed df) {
      setAttError("Couldn't set value", new AttributeSetException(df));
    }
  }

  public double[] getStandardSpectrumValue() {
    double[] retval = getSpectrumValue();
    for (int i = 0; i < retval.length; i++)
      retval[i] *= getStandardUnit();

    return retval;
  }

  protected void checkDimensions(double[] o) {
    if (o.length > getMaxXDimension()) {
      throw new IllegalStateException();
    }
  }


  protected void insert(double[] d) {
    checkDimensions(d);
    numberSpectrumHelper.insert(d);
  }

  public void refresh() {

    if (skippingRefresh) return;

    try {

      try {

        // Retreive the value from the device
        spectrumValue = numberSpectrumHelper.
          getNumberSpectrumValue(readValueFromNetwork());

        // Fire valueChanged
        numberSpectrumHelper.fireSpectrumValueChanged(spectrumValue, timeStamp);

      } catch (DevFailed e) {

        // Tango error
        readException.setError(e);
        spectrumValue = null;

        // Fire error event
        readAttError(e.getMessage(), readException);

      }

    } catch (Exception e) {

      // Code failure
      spectrumValue = null;

      System.out.println("NumberSpectrum.refresh() Exception caught ------------------------------");
      e.printStackTrace();
      System.out.println("NumberSpectrum.refresh()------------------------------------------------");

    }

  }

  public double[] getSpectrumValue() {
    return spectrumValue;
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

  public int getYDimension() {
    return 1;
  }

  public int getMaxYDimension() {
    return 1;
  }


}
