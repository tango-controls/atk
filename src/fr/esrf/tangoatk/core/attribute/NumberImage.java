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

public class NumberImage extends ANumber implements INumberImage
{
  double[][]                       imageValue;

  public ANumberImageHelper getNumberImageHelper()
  {
	  return (ANumberImageHelper)getNumberHelper();
  }

  public void addNumberImageListener(IImageListener l) {
	  
	  getNumberImageHelper().addImageListener(l);
  }

  public void removeNumberImageListener(IImageListener l) {
	  getNumberImageHelper().removeImageListener(l);
  }

  protected void insert(String[][] s) {
    checkDimensions(s);
    insert(NumberAttributeHelper.str2double(s));
  }

  public void refresh() {

    if (skippingRefresh) return;
    refreshCount++;
    try {

      try {

        // Retreive the value from the device
        // imageValue = numberHelper.getNumberImageValue(readValueFromNetwork());
        imageValue = getNumberImageHelper().getNumberImageDisplayValue(readValueFromNetwork()); //convert to display unit

        // Fire valueChanged
        getNumberImageHelper().fireImageValueChanged(imageValue, timeStamp);

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


  public void addImageListener(IImageListener l) {
    propChanges.addImageListener(l);
  }

  public void removeImageListener(IImageListener l) {
    propChanges.removeImageListener(l);
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
        // imageValue = numberHelper.getNumberImageValue(attValue);
        imageValue = getNumberImageHelper().getNumberImageDisplayValue(attValue); //convert to display unit

        // Fire valueChanged
        getNumberImageHelper().fireImageValueChanged(imageValue, timeStamp);

      } catch (DevFailed e) {

        dispatchError(e);

      }

    } catch (Exception e) {

      // Code failure
      imageValue = null;

      System.out.println("NumberImage.dispatch() Exception caught ------------------------------");
      e.printStackTrace();
      System.out.println("NumberImage.dispatch()------------------------------------------------");

    }

  }

  public void dispatchError(DevFailed e) {
    // Tango error
    imageValue = null;
    // Fire error event
    readAttError(e.getMessage(), new AttributeReadException(e));
  }

  public void setValue(double[][] d) {
    try {
      checkDimensions(d);
      insert(d);
      writeAtt();
      getNumberImageHelper().fireImageValueChanged(d, System.currentTimeMillis());
    } catch (DevFailed df) {
      setAttError("Couldn't set value", new AttributeSetException(df));
    }
  }

  public void setConfiguration(AttributeInfoEx c) {
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
      if(config.alarms!=null)
        setMinAlarm(new Double(config.alarms.min_alarm).doubleValue(), true);
      else
        setMinAlarm(new Double(config.min_alarm).doubleValue(), true);
    } catch (NumberFormatException e) {
      setMinAlarm(Double.NaN, true);
      getProperty("min_alarm").setSpecified(false);
    } // end of try-catch

    try {
      if(config.alarms!=null)
        setMaxAlarm(new Double(config.alarms.max_alarm).doubleValue(), true);
      else
        setMaxAlarm(new Double(config.max_alarm).doubleValue(), true);
    } catch (NumberFormatException e) {
      setMaxAlarm(Double.NaN, true);
      getProperty("max_alarm").setSpecified(false);
    } // end of try-catch

    try {
      if(config.alarms!=null)
        setMinWarning(new Double(config.alarms.min_warning).doubleValue(), true);
    } catch (NumberFormatException e) {
      setMinWarning(Double.NaN, true);
      getProperty("min_warning").setSpecified(false);
    } // end of try-catch

    try {
      if(config.alarms!=null)
        setMaxWarning(new Double(config.alarms.max_warning).doubleValue(), true);
    } catch (NumberFormatException e) {
      setMaxWarning(Double.NaN, true);
      getProperty("max_warning").setSpecified(false);
    } // end of try-catch

    try {
      if(config.alarms!=null)
        setDeltaT(new Double(config.alarms.delta_t).doubleValue(), true);
    } catch (NumberFormatException e) {
      setDeltaT(Double.NaN, true);
      getProperty("delta_t").setSpecified(false);
    } // end of try-catch

    try {
      if(config.alarms!=null)
        setDeltaVal(new Double(config.alarms.delta_val).doubleValue(), true);
    } catch (NumberFormatException e) {
      setDeltaVal(Double.NaN, true);
      getProperty("delta_val").setSpecified(false);
    } // end of try-catch

  }

  void insert(double[][] d) {
    getNumberImageHelper().insert(d);
  }

  public String[][] extract() throws DevFailed {
    return getNumberImageHelper().getImageValue(readValueFromNetwork());

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
            // imageValue = numberHelper.getNumberImageValue(da);
            imageValue = getNumberImageHelper().getNumberImageDisplayValue(da); //convert to display unit

            // Fire valueChanged
            getNumberImageHelper().fireImageValueChanged(imageValue, timeStamp);
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

            System.out.println("NumberImage.periodic.getNumberImageDisplayValue() Exception caught ------------------------------");
            e.printStackTrace();
            System.out.println("NumberImage.periodic.getNumberImageDisplayValue()------------------------------------------------");
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
            // imageValue = numberHelper.getNumberImageValue(da);
            imageValue = getNumberImageHelper().getNumberImageDisplayValue(da); //convert to display unit

            // Fire valueChanged
            getNumberImageHelper().fireImageValueChanged(imageValue, timeStamp);
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

            System.out.println("NumberImage.change.getNumberImageDisplayValue() Exception caught ------------------------------");
            e.printStackTrace();
            System.out.println("NumberImage.change.getNumberImageDisplayValue()------------------------------------------------");
         } // end of catch
      }
      
  }
  
  public void freeInternalData()
  {
     super.freeInternalData();
     imageValue = null;
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
