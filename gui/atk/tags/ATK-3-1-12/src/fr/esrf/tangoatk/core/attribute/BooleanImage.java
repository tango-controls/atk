// File:          BooleanImage.java
// Created:       2005-02-03 10:45:00, poncet
// By:            <poncet@esrf.fr>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import fr.esrf.TangoApi.events.*;

public class BooleanImage extends AAttribute
  implements IBooleanImage {

  BooleanImageHelper    imageHelper;
  boolean[][]           imageValue = null;

  public BooleanImage()
  {
    imageHelper = new BooleanImageHelper(this);
  }



  public boolean[][] getValue()
  {
      return imageValue;
  }



  public void setValue(boolean[][] bImage)
  {
      try
      {
	  checkDimensions(bImage);
	  insert(bImage);
	  writeAtt();
	  imageHelper.fireImageValueChanged(bImage, System.currentTimeMillis());
      }
      catch (DevFailed df)
      {
	  setAttError("Couldn't set value", new AttributeSetException(df));
      }
      catch (Exception ex)
      {
	  setAttError("Couldn't set value", new ATKException(ex));
      }
  }
  
  
  void insert(boolean[][] bImg)
  {
      imageHelper.insert(bImg);
  }



  public void refresh()
  {
      DeviceAttribute           att = null;
      
      
      if (skippingRefresh) return;

      refreshCount++;
      try
      {
	  try 
	  {
	      // Read the attribute from device cache (readValueFromNetwork)
	      att = readValueFromNetwork();
	      if (att == null) return;
	      
	      // Retreive the read value for the attribute
	      imageValue = imageHelper.getBooleanImageValue(att);

	      // Fire valueChanged
	      fireValueChanged(imageValue);
	  }
	  catch (DevFailed e)
	  {
	      // Fire error event
	      readAttError(e.getMessage(), new AttributeReadException(e));
	  }
      }
      catch (Exception e)
      {
	  // Code failure
	  System.out.println("BooleanImage.refresh() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("BooleanImage.refresh()------------------------------------------------");
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

        // Retreive the read value for the attribute
        imageValue = imageHelper.getBooleanImageValue(attValue);

        // Fire valueChanged
        fireValueChanged(imageValue);
      } catch (DevFailed e) {

        dispatchError(e);

      }
    } catch (Exception e) {
      // Code failure
      System.out.println("BooleanImage.dispatch() Exception caught ------------------------------");
      e.printStackTrace();
      System.out.println("BooleanImage.dispatch()------------------------------------------------");
    }

  }

  public void dispatchError(DevFailed e) {

    imageValue = null;
    // Fire error event
    readAttError(e.getMessage(), new AttributeReadException(e));

  }


  public boolean isWritable()
  {
    return super.isWritable();
  }

  protected void fireValueChanged(boolean[][] newValue) {
    imageHelper.fireImageValueChanged(newValue, timeStamp);
  }

  public void addBooleanImageListener(IBooleanImageListener l) {
    imageHelper.addBooleanImageListener(l);
    addStateListener(l);
  }

  public void removeBooleanImageListener(IBooleanImageListener l) {
    imageHelper.removeBooleanImageListener(l);
    removeStateListener(l);
  }
  
  
  
  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent evt) 
  {
      periodicCount++;
      DeviceAttribute     da=null;
      long t0 = System.currentTimeMillis();

      trace(DeviceFactory.TRACE_PERIODIC_EVENT, "BooleanImage.periodic method called for " + getName(), t0);
      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "BooleanImage.periodicEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "BooleanImage.periodicEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "BooleanImage.periodicEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "BooleanImage.periodicEvt.getValue(" + getName() + ") failed, got other error", t0);
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "BooleanImage.periodicEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  System.out.println("BooleanImage.periodic.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("BooleanImage.periodic.getValue()------------------------------------------------");
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
	      // Retreive the read value for the attribute
	      imageValue = imageHelper.getBooleanImageValue(da);

	      // Fire valueChanged
	      fireValueChanged(imageValue);
          }
	  catch (DevFailed dfe)
	  {
              // Tango error
              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
          }
	  catch (Exception e) // Code failure
          {
              System.out.println("BooleanImage.periodic.extractBoolean() Exception caught ------------------------------");
              e.printStackTrace();
              System.out.println("BooleanImage.periodic.extractBoolean()------------------------------------------------");
          } // end of catch
      }
      
  }
 
  
  
  
  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent evt) 
  {
      changeCount++;
      DeviceAttribute     da=null;
      long t0 = System.currentTimeMillis();

      trace(DeviceFactory.TRACE_CHANGE_EVENT, "BooleanImage.change method called for " + getName(), t0);
      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "BooleanImage.changeEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "BooleanImage.changeEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "BooleanImage.changeEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "BooleanImage.changeEvt.getValue(" + getName() + ") failed, got other error", t0);
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "BooleanImage.changeEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  System.out.println("BooleanImage.change.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("BooleanImage.change.getValue()------------------------------------------------");
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
	      // Retreive the read value for the attribute
	      imageValue = imageHelper.getBooleanImageValue(da);

	      // Fire valueChanged
	      fireValueChanged(imageValue);
          }
	  catch (DevFailed dfe)
	  {
              // Tango error
              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
          }
	  catch (Exception e) // Code failure
          {
              System.out.println("BooleanImage.change.extractBoolean() Exception caught ------------------------------");
              e.printStackTrace();
              System.out.println("BooleanImage.change.extractBoolean()------------------------------------------------");
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
