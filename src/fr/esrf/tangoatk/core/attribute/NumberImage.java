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
import fr.esrf.TangoApi.events.*;

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
    refreshCount++;
    try {

      try {

        // Retreive the value from the device
        imageValue = numberHelper.
          getNumberImageValue(readValueFromNetwork());

        // Fire valueChanged
        numberHelper.fireImageValueChanged(imageValue, timeStamp);

      } catch (DevFailed e) {

        // Tango error
        imageValue = null;

        // Fire error event
        readAttError(e.getMessage(), new AttributeReadException(e));

      }

    } catch (Exception e) {
      
      // Code failure
      imageValue = null;

      System.out.println("NumberImage.refresh() Exception caught ------------------------------");
      e.printStackTrace();
      System.out.println("NumberImage.refresh()------------------------------------------------");

    }

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

  public double[][] getStandardValue() throws DevFailed {
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
 
  
  
  
  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent evt) 
  {
      periodicCount++;
      DeviceAttribute     da=null;
      long t0 = System.currentTimeMillis();

      trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberImage.periodic method called for " + getName(), t0);

      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberImage.periodicEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberImage.periodicEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberImage.periodicEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
              // Tango error
              imageValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberImage.periodicEvt.getValue(" + getName() + ") failed, got other error", t0);
              // Tango error
              imageValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberImage.periodicEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  imageValue = null;

	  System.out.println("NumberImage.periodic.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("NumberImage.periodic.getValue()------------------------------------------------");
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
            imageValue = numberHelper.getNumberImageValue(da);

            // Fire valueChanged
            numberHelper.fireImageValueChanged(imageValue, timeStamp);
         }
	 catch (DevFailed dfe)
	 {
            imageValue = null;
            // Fire error event
            readAttError(dfe.getMessage(), new AttributeReadException(dfe));
         }
	 catch (Exception e) // Code failure
         {
            imageValue = null;

            System.out.println("NumberImage.periodic.getNumberImageValue() Exception caught ------------------------------");
            e.printStackTrace();
            System.out.println("NumberImage.periodic.getNumberImageValue()------------------------------------------------");
         } // end of catch
      }
      
  }
 
  
  
  
  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent evt) 
  {
      changeCount++;
      DeviceAttribute     da=null;
      long t0 = System.currentTimeMillis();

      trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberImage.change method called for " + getName(), t0);

      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberImage.changeEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberImage.changeEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberImage.changeEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
              // Tango error
              imageValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberImage.changeEvt.getValue(" + getName() + ") failed, got other error", t0);
              // Tango error
              imageValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberImage.changeEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  imageValue = null;

	  System.out.println("NumberImage.change.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("NumberImage.change.getValue()------------------------------------------------");
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
            imageValue = numberHelper.getNumberImageValue(da);

            // Fire valueChanged
            numberHelper.fireImageValueChanged(imageValue, timeStamp);
         }
	 catch (DevFailed dfe)
	 {
            imageValue = null;
            // Fire error event
            readAttError(dfe.getMessage(), new AttributeReadException(dfe));
         }
	 catch (Exception e) // Code failure
         {
            imageValue = null;

            System.out.println("NumberImage.change.getNumberImageValue() Exception caught ------------------------------");
            e.printStackTrace();
            System.out.println("NumberImage.change.getNumberImageValue()------------------------------------------------");
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

}
