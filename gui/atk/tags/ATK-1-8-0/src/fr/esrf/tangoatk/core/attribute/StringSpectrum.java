// File:          StringSpectrum.java
// Created:       2003-12-11 18:00:00, poncet
// By:            <poncet@esrf.fr>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;


import java.beans.*;

import fr.esrf.tangoatk.core.*;

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

public class StringSpectrum extends AAttribute
  implements IStringSpectrum {

  StringSpectrumHelper stringSpectHelper;
  String[] stringValues = null;

  public StringSpectrum() {
    stringSpectHelper = new StringSpectrumHelper(this);
  }


  public void setStringSpectrumValue(String[] s) {
    try {
      stringSpectHelper.insert(s);
      writeAtt();
      refresh();
    } catch (DevFailed df) {
      setAttError("Couldn't set value", new AttributeSetException(df));
    }
  }


  public String[] getStringSpectrumValue() {
    return stringValues;
  }


  public void addListener(IStringSpectrumListener l) {
    stringSpectHelper.addStringSpectrumListener(l);
    addStateListener(l);
  }

  public void removeListener(IStringSpectrumListener l) {
    stringSpectHelper.removeStringSpectrumListener(l);
    removeStateListener(l);
  }


  public void refresh() {

    if (skippingRefresh) return;

    try {

      try {

        // Retreive the value from the device
        readValueFromNetwork();
        stringValues = stringSpectHelper.extract();

        // Fire valueChanged
        fireValueChanged(stringValues);

      } catch (DevFailed e) {

        // Tango error
        readException.setError(e);
        stringValues = null;

        // Fire error event
        readAttError(e.getMessage(), readException);

      }

    } catch (Exception e) {

      // Code failure
      stringValues = null;

      System.out.println("StringSpectrum.refresh() Exception caught ------------------------------");
      e.printStackTrace();
      System.out.println("StringSpectrum.refresh()------------------------------------------------");

    }

  }


  public boolean isWritable() {
    return super.isWritable();
  }


  protected void fireValueChanged(String[] newValue) {
    propChanges.fireStringSpectrumEvent(this, newValue, timeStamp);
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
