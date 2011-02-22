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
    refreshCount++;
    try {

      try {

	// Retreive the value from the device
	// spectrumValue = numberSpectrumHelper.getNumberSpectrumValue(readValueFromNetwork());
	spectrumValue = numberSpectrumHelper.getNumberSpectrumDisplayValue(readValueFromNetwork()); //convert to display unit

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

  public void dispatch(DeviceAttribute attValue) {

    if (skippingRefresh) return;
    refreshCount++;
    try {

      try {
        // symetric with refresh
        if (attValue == null) return;
        attribute = attValue;

        setState(attValue);
        timeStamp = attValue.getTimeValMillisSec();

        // Retreive the value from the device
        // spectrumValue = numberSpectrumHelper.getNumberSpectrumValue(attValue);
        spectrumValue = numberSpectrumHelper.getNumberSpectrumDisplayValue(attValue); //convert to display unit

        // Fire valueChanged
        numberSpectrumHelper.fireSpectrumValueChanged(spectrumValue, timeStamp);

      } catch (DevFailed e) {

        dispatchError(e);

      }

    } catch (Exception e) {

      // Code failure
      spectrumValue = null;

      System.out.println("NumberSpectrum.dispatch() Exception caught ------------------------------");
      e.printStackTrace();
      System.out.println("NumberSpectrum.dispatch()------------------------------------------------");

    }

  }

  public void dispatchError(DevFailed e) {

    // Tango error
    spectrumValue = null;

    // Fire error event
    readAttError(e.getMessage(), new AttributeReadException(e));

  }

  public double[] getSpectrumValue() {
    return spectrumValue;
  }

  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent evt) 
  {
      periodicCount++;
      DeviceAttribute     da=null;
      long t0 = System.currentTimeMillis();
      
      trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberSpectrum.periodic method called for " + getName(), t0);
      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberSpectrum.periodicEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
	  trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberSpectrum.periodicEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberSpectrum.periodicEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
              // Tango error
              spectrumValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberSpectrum.periodicEvt.getValue(" + getName() + ") failed, got other error", t0);
              // Tango error
              spectrumValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberSpectrum.periodicEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  spectrumValue = null;

	  System.out.println("NumberSpectrum.periodic.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("NumberSpectrum.periodic.getValue()------------------------------------------------");
          return;
      } // end of catch


      // read the attribute value from the received event!      
      if (da != null)
      {
	 try
	 {
            setState(da); // To set the quality factor and fire AttributeState event
            attribute = da;
            timeStamp = da.getTimeValMillisSec();
            // Retreive the value from the device
            // spectrumValue = numberSpectrumHelper.getNumberSpectrumValue(da);
            spectrumValue = numberSpectrumHelper.getNumberSpectrumDisplayValue(da); //convert to display unit

            // Fire valueChanged
            numberSpectrumHelper.fireSpectrumValueChanged(spectrumValue, timeStamp);
	 }
	 catch (DevFailed dfe)
	 {
            spectrumValue = null;
            // Fire error event
            readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	 }
	 catch (Exception e) // Code failure
	 {
            spectrumValue = null;
            System.out.println("NumberSpectrum.periodic.getNumberSpectrumDisplayValue() Exception caught ------------------------------");
            e.printStackTrace();
            System.out.println("NumberSpectrum.periodic.getNumberSpectrumDisplayValue()------------------------------------------------");
	 } // end of catch
      }
      
  }

  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent evt) 
  {
      changeCount++;
      DeviceAttribute     da=null;
      long t0 = System.currentTimeMillis();
      
      trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberSpectrum.change method called for " + getName(), t0);
      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberSpectrum.changeEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
	  trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberSpectrum.changeEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberSpectrum.changeEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
              // Tango error
              spectrumValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberSpectrum.changeEvt.getValue(" + getName() + ") failed, got other error", t0);
              // Tango error
              spectrumValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberSpectrum.changeEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  spectrumValue = null;

	  System.out.println("NumberSpectrum.change.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("NumberSpectrum.change.getValue()------------------------------------------------");
          return;
      } // end of catch


      // read the attribute value from the received event!      
      if (da != null)
      {
	 try
	 {
            setState(da); // To set the quality factor and fire AttributeState event
            attribute = da;
            timeStamp = da.getTimeValMillisSec();
            // Retreive the value from the device
            // spectrumValue = numberSpectrumHelper.getNumberSpectrumValue(da);
            spectrumValue = numberSpectrumHelper.getNumberSpectrumDisplayValue(da); //convert to display unit

            // Fire valueChanged
            numberSpectrumHelper.fireSpectrumValueChanged(spectrumValue, timeStamp);
	 }
	 catch (DevFailed dfe)
	 {
            spectrumValue = null;
            // Fire error event
            readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	 }
	 catch (Exception e) // Code failure
	 {
            spectrumValue = null;
            System.out.println("NumberSpectrum.change.getNumberSpectrumDisplayValue() Exception caught ------------------------------");
            e.printStackTrace();
            System.out.println("NumberSpectrum.change.getNumberSpectrumDisplayValue()------------------------------------------------");
	 } // end of catch
      }
  }


  private void trace(int level,String msg,long time)
  {
    DeviceFactory.getInstance().trace(level,msg,time);
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
