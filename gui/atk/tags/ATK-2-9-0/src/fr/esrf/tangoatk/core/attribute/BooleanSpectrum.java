// File:          BooleanSpectrum.java
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

public class BooleanSpectrum extends AAttribute
  implements IBooleanSpectrum {

  BooleanSpectrumHelper   spectrumHelper;
  boolean[]               spectrumValue = null;

  public BooleanSpectrum()
  {
    spectrumHelper = new BooleanSpectrumHelper(this);
  }



  public boolean[] getValue()
  {
      return spectrumValue;
  }



  public void setValue(boolean[] bArray)
  {
    try
    {
      attribute.insert(bArray);
      writeAtt();
      refresh();
    }
    catch (DevFailed df)
    {
      setAttError("Couldn't set value", new AttributeSetException(df));
    }
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
	      spectrumValue = att.extractBooleanArray();

	      // Fire valueChanged
	      fireValueChanged(spectrumValue);
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
	  System.out.println("BooleanSpectrum.refresh() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("BooleanSpectrum.refresh()------------------------------------------------");
      }
  }
  
  public void dispatch(DeviceAttribute attValue)
  {
      if (skippingRefresh) return;
      refreshCount++;
      try
      {
	  try
	  {
          // symetric with refresh
          if (attValue == null) return;
          attribute = attValue;

          setState(attValue);
          timeStamp = attValue.getTimeValMillisSec();

	      // Retreive the read value for the attribute
	      spectrumValue = attValue.extractBooleanArray();

	      // Fire valueChanged
	      fireValueChanged(spectrumValue);
	  }
	  catch (DevFailed e)
	  {

	      dispatchError(e);

	  }
      }
      catch (Exception e)
      {
	  // Code failure
	  System.out.println("BooleanSpectrum.dispatch() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("BooleanSpectrum.dispatch()------------------------------------------------");
      }
  }

  public void dispatchError(DevFailed e)
  {
    // Fire error event
    readAttError(e.getMessage(), new AttributeReadException(e));
  }

  public boolean isWritable()
  {
    return super.isWritable();
  }

  protected void fireValueChanged(boolean[] newValue) {
    spectrumHelper.fireSpectrumValueChanged(newValue, timeStamp);
  }

  public void addBooleanSpectrumListener(IBooleanSpectrumListener l) {
    spectrumHelper.addBooleanSpectrumListener(l);
    addStateListener(l);
  }

  public void removeBooleanSpectrumListener(IBooleanSpectrumListener l) {
    spectrumHelper.removeBooleanSpectrumListener(l);
    removeStateListener(l);
  }

   
  
  
  
  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent evt) 
  {
      periodicCount++;
      DeviceAttribute     da=null;
      long t0 = System.currentTimeMillis();
      
      trace(DeviceFactory.TRACE_PERIODIC_EVENT, "BooleanSpectrum.periodic method called for " + getName(), t0);
      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "BooleanSpectrum.periodicEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
         trace(DeviceFactory.TRACE_PERIODIC_EVENT, "BooleanSpectrum.periodicEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
         if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	 {
             trace(DeviceFactory.TRACE_PERIODIC_EVENT, "BooleanSpectrum.periodicEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
	     // Tango error
	     // Fire error event
	     readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	 }
	 else // For the moment the behaviour for all DevFailed is the same
	 {
             trace(DeviceFactory.TRACE_PERIODIC_EVENT, "BooleanSpectrum.periodicEvt.getValue(" + getName() + ") failed, got other error", t0);
	     // Tango error
	     // Fire error event
	     readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	 }
         return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "BooleanSpectrum.periodicEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  System.out.println("BooleanSpectrum.periodic.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("BooleanSpectrum.periodic.getValue()------------------------------------------------");
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
              spectrumValue = da.extractBooleanArray();
              // Fire valueChanged
              fireValueChanged(spectrumValue);
          }
	  catch (DevFailed dfe)
	  {
              // Tango error
              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
          }
	  catch (Exception e) // Code failure
          {
              System.out.println("BooleanSpectrum.periodic.extractBooleanArray() Exception caught ------------------------------");
              e.printStackTrace();
              System.out.println("BooleanSpectrum.periodic.extractBooleanArray()------------------------------------------------");
          } // end of catch
      }
      
  }
 
  
  
  
  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent evt) 
  {
      changeCount++;
      DeviceAttribute     da=null;
      long t0 = System.currentTimeMillis();
      
      trace(DeviceFactory.TRACE_CHANGE_EVENT, "BooleanSpectrum.change method called for " + getName(), t0);
      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "BooleanSpectrum.changeEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
         trace(DeviceFactory.TRACE_CHANGE_EVENT, "BooleanSpectrum.changeEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
         if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	 {
             trace(DeviceFactory.TRACE_CHANGE_EVENT, "BooleanSpectrum.changeEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
	     // Tango error
	     // Fire error event
	     readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	 }
	 else // For the moment the behaviour for all DevFailed is the same
	 {
             trace(DeviceFactory.TRACE_CHANGE_EVENT, "BooleanSpectrum.changeEvt.getValue(" + getName() + ") failed, got other error", t0);
	     // Tango error
	     // Fire error event
	     readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	 }
         return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "BooleanSpectrum.changeEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  System.out.println("BooleanSpectrum.change.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("BooleanSpectrum.change.getValue()------------------------------------------------");
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
              spectrumValue = da.extractBooleanArray();
              // Fire valueChanged
              fireValueChanged(spectrumValue);
          }
	  catch (DevFailed dfe)
	  {
              // Tango error
              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
          }
	  catch (Exception e) // Code failure
          {
              System.out.println("BooleanSpectrum.change.extractBooleanArray() Exception caught ------------------------------");
              e.printStackTrace();
              System.out.println("BooleanSpectrum.change.extractBooleanArray()------------------------------------------------");
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
