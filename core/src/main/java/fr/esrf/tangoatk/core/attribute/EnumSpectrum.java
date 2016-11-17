/*
 *  Copyright (C) :	2015
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
// File:          EnumSpectrum.java
// Created:       2015-03-09 09:31:10, poncet
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Tango Enumerated Spectrum attributes are mapped to EnumSpectrum class of ATK.
 * @author  poncet
 */

public class EnumSpectrum extends AAttribute implements IEnumSpectrum, PropertyChangeListener
{

  EnumSpectrumHelper            enumSpecHelper=null;
  String[]                      spectrumValue = null;
  String[]                      spectrumSetPointValue = null;
  private String[]              enumLabels = null;



  public EnumSpectrum(String[] enums)
  {
      enumLabels=enums;
  }




  @Override
  protected void init(fr.esrf.tangoatk.core.Device d, String name, AttributeInfoEx config, boolean doEvent)
  {
      super.init(d, name, config, doEvent);
      Property p = null;
      p = this.getProperty("enum_label");
      if (p != null)
          p.addPresentationListener(this);
  }
  
 
  /** Overrides the getType() method in AAttribute **/
  public String getType()
  {
      return("EnumSpectrum");
  }

  
  public void setEnumSpectrumHelper(EnumSpectrumHelper helper)
  {
      enumSpecHelper = helper;
  }

  public String[] getEnumSpectrumValue()
  {
      return spectrumValue;
  }

  public void setEnumSpectrumValue(String[] s) 
  {
     try
     {
	enumSpecHelper.insert(s);
	writeAtt();
     }
     catch (AttributeSetException attEx)
     {
	setAttError("Couldn't set value", attEx);
     }
     catch (DevFailed df)
     {
	setAttError("Couldn't set value", new AttributeSetException(df));
     }
  }
  

 // getEnumSpectrumSetPoint returns the attribute's setpoint value
 public String[] getEnumSpectrumSetPoint()
 {
     return spectrumSetPointValue;
 }

 

  // getEnumScalarSetPointFromDevice  returns the attribute's setpoint value
  // This method makes a call to read attribute on the device proxy
  // Will force value reading via the device , ignore polling buffer
  public String[] getEnumScalarSetPointFromDevice()
  {
      try
      {
	  spectrumSetPointValue = enumSpecHelper.getEnumSpectrumSetPoint(readDeviceValueFromNetwork());
      }
      catch (DevFailed e)
      {
	  readAttError(e.getMessage(), new AttributeReadException(e));
	  spectrumSetPointValue = null;
      }
      catch (Exception e)
      {
	  readAttError(e.getMessage(), e);
	  spectrumSetPointValue = null;
      } // end of catch

      return spectrumSetPointValue;
  }
  
  
  public void addEnumSpectrumListener(IEnumSpectrumListener l)
  {
     enumSpecHelper.addEnumSpectrumListener(l);
     addStateListener(l);
  }

  public void removeEnumSpectrumListener(IEnumSpectrumListener l)
  {
     enumSpecHelper.removeEnumSpectrumListener(l);
     removeStateListener(l);
  }


  public void refresh()
  {
      DeviceAttribute           att = null;
      long                      t0 = System.currentTimeMillis();
      
//      if (skippingRefresh) return;
      refreshCount++;
      trace(DeviceFactory.TRACE_REFRESHER, "EnumSpectrum.refresh() method called for " + getName(), t0);
      try
      {
	  try 
	  {
	      // Read the attribute from device cache (readValueFromNetwork)
	      att = readValueFromNetwork();
	      attribute = att;
	      if (att == null) return;
	      
	      // Retreive the read value for the attribute
	      spectrumValue = enumSpecHelper.getEnumSpectrumValue(att);
	      
	      // Retreive the set point for the attribute
	      spectrumSetPointValue = enumSpecHelper.getEnumSpectrumSetPoint(att);

	      // Fire valueChanged
	      enumSpecHelper.fireEnumSpectrumValueChanged(spectrumValue, timeStamp);
	  }
	  catch (AttributeReadException attEx)
	  {
	      spectrumValue = null;
	      spectrumSetPointValue = null;
	      // Fire error event
	      readAttError("Invalid enum value read.", attEx);
	  }
	  catch (DevFailed e)
	  {
	      // Tango error
	      spectrumValue = null;
	      spectrumSetPointValue = null;
	      // Fire error event
	      readAttError(e.getMessage(), new AttributeReadException(e));
	  }
      }
      catch (Exception e)
      {
	  // Code failure
	  spectrumValue = null;
	  spectrumSetPointValue = null;

	  System.out.println("EnumSpectrum.refresh() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("EnumSpectrum.refresh()------------------------------------------------");
      }
  }
  
  public void dispatch(DeviceAttribute attValue)
  {
//      if (skippingRefresh) return;
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
	     spectrumValue = enumSpecHelper.getEnumSpectrumValue(attValue);

	     // Retreive the set point for the attribute
	     spectrumSetPointValue = enumSpecHelper.getEnumSpectrumSetPoint(attValue);

	     // Fire valueChanged
	     enumSpecHelper.fireEnumSpectrumValueChanged(spectrumValue, timeStamp);
	  }
	  catch (AttributeReadException attEx)
	  {
	      spectrumValue = null;
	      spectrumSetPointValue = null;
	      // Fire error event
	      readAttError("Invalid enum value read.", attEx);
	  }
	  catch (DevFailed e)
	  {
	     dispatchError(e);
	  }
      }
      catch (Exception e)
      {
	  // Code failure
	  spectrumValue = null;
	  spectrumSetPointValue = null;

	  System.out.println("EnumSpectrum.dispatch() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("EnumSpectrum.dispatch()------------------------------------------------");
      }
  }

  public void dispatchError(DevFailed e)
  {
      // Tango error
      spectrumValue = null;
      spectrumSetPointValue = null;
      // Fire error event
      readAttError(e.getMessage(), new AttributeReadException(e));
  }

  public boolean isWritable()
  {
    return super.isWritable();
  }

  
  
  public String[] getEnumValues()
  {
      return enumLabels;
  }

 
         
  
  
  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent evt) 
  {
      periodicCount++;
      if(evt.isZmqEvent()) eventType=2; else eventType=1;
      DeviceAttribute da = null;
      long t0 = System.currentTimeMillis();

      trace(DeviceFactory.TRACE_PERIODIC_EVENT, "EnumSpectrum.periodic method called for " + getName(), t0);

      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "EnumSpectrum.periodicEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "EnumSpectrum.periodicEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "EnumSpectrum.periodicEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
	      // Tango error
	      spectrumValue = null;
	      spectrumSetPointValue = null;
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "EnumSpectrum.periodicEvt.getValue(" + getName() + ") failed, got other error", t0);
	      // Tango error
	      spectrumValue = null;
	      spectrumSetPointValue = null;
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "EnumSpectrum.periodicEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  spectrumValue = null;
	  spectrumSetPointValue = null;

	  System.out.println("EnumSpectrum.periodic.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("EnumSpectrum.periodic.getValue()------------------------------------------------");
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
	    spectrumValue = enumSpecHelper.getEnumSpectrumValue(da);

	    // Retreive the set point for the attribute
	    spectrumSetPointValue = enumSpecHelper.getEnumSpectrumSetPoint(da);

	    // Fire valueChanged
	    enumSpecHelper.fireEnumSpectrumValueChanged(spectrumValue, timeStamp);

	 }
	 catch (AttributeReadException attEx)
	 {
	     spectrumValue = null;
	     spectrumSetPointValue = null;
	     // Fire error event
	     readAttError("Invalid enum value read.", attEx);
	 }
	 catch (DevFailed dfe)
	 {
            // Tango error
	    spectrumValue = null;
	    spectrumSetPointValue = null;
            // Fire error event
            readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	 }
	 catch (Exception e) // Code failure
	 {
	    spectrumValue = null;
	    spectrumSetPointValue = null;

            System.out.println("EnumSpectrum.periodic.extractString() Exception caught ------------------------------");
            e.printStackTrace();
            System.out.println("EnumSpectrum.periodic.extractString()------------------------------------------------");
	 } // end of catch
      }
      
  }
 
  
  
  
  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent evt) 
  {
      changeCount++;
      if(evt.isZmqEvent()) eventType=2; else eventType=1;
      DeviceAttribute da = null;
      long t0 = System.currentTimeMillis();

      trace(DeviceFactory.TRACE_CHANGE_EVENT, "EnumSpectrum.change method called for " + getName(), t0);

      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "EnumSpectrum.changeEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "EnumSpectrum.changeEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "EnumSpectrum.changeEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
	      // Tango error
	      spectrumValue = null;
	      spectrumSetPointValue = null;
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "EnumSpectrum.changeEvt.getValue(" + getName() + ") failed, got other error", t0);
	      // Tango error
	      spectrumValue = null;
	      spectrumSetPointValue = null;
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "EnumSpectrum.changeEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  spectrumValue = null;
	  spectrumSetPointValue = null;

	  System.out.println("EnumSpectrum.change.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("EnumSpectrum.change.getValue()------------------------------------------------");
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
	    spectrumValue = enumSpecHelper.getEnumSpectrumValue(da);

	    // Retreive the set point for the attribute
	    spectrumSetPointValue = enumSpecHelper.getEnumSpectrumSetPoint(da);

	    // Fire valueChanged
	    enumSpecHelper.fireEnumSpectrumValueChanged(spectrumValue, timeStamp);

	 }
	 catch (AttributeReadException attEx)
	 {
	     spectrumValue = null;
	     spectrumSetPointValue = null;
	     // Fire error event
	     readAttError("Invalid enum value read.", attEx);
	 }
	 catch (DevFailed dfe)
	 {
            // Tango error
	    spectrumValue = null;
	    spectrumSetPointValue = null;
            // Fire error event
            readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	 }
	 catch (Exception e) // Code failure
	 {
	    spectrumValue = null;
	    spectrumSetPointValue = null;

            System.out.println("EnumSpectrum.change.extractString() Exception caught ------------------------------");
            e.printStackTrace();
            System.out.println("EnumSpectrum.change.extractString()------------------------------------------------");
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

  
    // Interface java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent evt)
    {
        Property src = (Property) evt.getSource();
        if (src == null) return;
        
        if (src.getName().equalsIgnoreCase("enum_label"))
        {
            if (src instanceof StringArrayProperty)
            {
                StringArrayProperty sap = (StringArrayProperty) src;
                String[] newEnums = sap.getStringArrayValue();
                updateEnumLabels(newEnums);
            }
        }
    }
    
    private void updateEnumLabels (String[] enums)
    {
        if (enums == null) return;
        enumLabels = enums;
    }


}
