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
import fr.esrf.TangoApi.events.*;

public class NumberSpectrum extends NumberImage
  implements INumberSpectrum {
  double[] spectrumValue;
  ANumberSpectrumHelper numberSpectrumHelper;

  public void init(fr.esrf.tangoatk.core.Device d, String name,
                   AttributeInfo config) {
    super.init(d, name, config);
  }

  public double[][] getStandardValue() throws DevFailed {
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
        spectrumValue = null;

        // Fire error event
        readAttError(e.getMessage(), new AttributeReadException(e));

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
  
 
  
  
  
  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent evt) 
  {
      DeviceAttribute     da=null;
      
//System.out.println("NumberSpectrum.periodic() called for : " + getName());
      try
      {
          da = evt.getValue();
      }
      catch (DevFailed  dfe)
      {
//System.out.println("NumberSpectrum.periodic() caught DevFailed for : " + getName());
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
//System.out.println("NumberSpectrum.periodic() caught heartbeat DevFailed : " + getName());
              // Tango error
              spectrumValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
//System.out.println("NumberSpectrum.periodic() caught other DevFailed : " + getName() );
              // Tango error
              spectrumValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
	  spectrumValue = null;

	  System.out.println("NumberSpectrum.periodic.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("NumberSpectrum.periodic.getValue()------------------------------------------------");
          return;
      } // end of catch


      // read the attribute value from the received event!
    if (da != null) {
      try {
        setState(da); // To set the quality factor and fire AttributeState event
        attribute = da;
        timeStamp = da.getTimeValMillisSec();
        // Retreive the value from the device
        spectrumValue = numberSpectrumHelper.getNumberSpectrumValue(da);

        // Fire valueChanged
        numberSpectrumHelper.fireSpectrumValueChanged(spectrumValue, timeStamp);
      } catch (DevFailed dfe) {

        spectrumValue = null;
        // Fire error event
        readAttError(dfe.getMessage(), new AttributeReadException(dfe));

      } catch (Exception e) // Code failure
      {
        spectrumValue = null;

        System.out.println("NumberSpectrum.periodic.getNumberSpectrumValue() Exception caught ------------------------------");
        e.printStackTrace();
        System.out.println("NumberSpectrum.periodic.getNumberSpectrumValue()------------------------------------------------");
      } // end of catch
    }
      
  }
 
  
  
  
  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent evt) 
  {
      DeviceAttribute     da=null;
      
//System.out.println("NumberSpectrum.change() called for : " + getName());
      try
      {
          da = evt.getValue();
      }
      catch (DevFailed  dfe)
      {
//System.out.println("NumberSpectrum.change() caught DevFailed for : " + getName());
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
//System.out.println("NumberSpectrum.change() caught heartbeat DevFailed : " + getName());
              // Tango error
              spectrumValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
//System.out.println("NumberSpectrum.change() caught other DevFailed : " + getName() );
              // Tango error
              spectrumValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
	  spectrumValue = null;

	  System.out.println("NumberSpectrum.change.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("NumberSpectrum.change.getValue()------------------------------------------------");
          return;
      } // end of catch


      // read the attribute value from the received event!      
      if (da != null)
      {
        try {
          setState(da); // To set the quality factor and fire AttributeState event
          attribute = da;
          timeStamp = da.getTimeValMillisSec();
          // Retreive the value from the device
          spectrumValue = numberSpectrumHelper.getNumberSpectrumValue(da);

          // Fire valueChanged
          numberSpectrumHelper.fireSpectrumValueChanged(spectrumValue, timeStamp);
        } catch (DevFailed dfe) {

          spectrumValue = null;
          // Fire error event
          readAttError(dfe.getMessage(), new AttributeReadException(dfe));

        } catch (Exception e) // Code failure
        {
          spectrumValue = null;

          System.out.println("NumberSpectrum.change.getNumberSpectrumValue() Exception caught ------------------------------");
          e.printStackTrace();
          System.out.println("NumberSpectrum.change.getNumberSpectrumValue()------------------------------------------------");
        } // end of catch
      }
      
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
