/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
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
 
// File:          DevStateScalar.java
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

public class DevStateScalar extends AAttribute
  implements IDevStateScalar {

  DevStateScalarHelper   devStateHelper;
  String                 devStateValue = null;
  boolean                invertOpenClose = false;
  boolean                invertInsertExtract = false;

  public DevStateScalar()
  {
      invertOpenClose = false;
      invertInsertExtract = false;
      devStateHelper = new DevStateScalarHelper(this);
  }


  public int getXDimension() {
    return 1;
  }

  public int getMaxXDimension() {
    return 1;
  }
  
  
  public String getValue()
  {
     return devStateValue;
  }
  
  
  public String getDeviceValue()
  {
      String readVal;
      try
      {
	  readVal = fr.esrf.tangoatk.core.Device.toString(readValueFromNetwork().extractState());
	  devStateValue = readVal;
      }
      catch (DevFailed e)
      {
	  // Fire error event
	  readAttError(e.getMessage(), new AttributeReadException(e));
      }
      catch (Exception e)
      {
	  // Code failure
	  System.out.println("DevStateScalar.getDeviceValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("DevStateScalar.getDeviceValue()------------------------------------------------");
      } // end of catch

      return devStateValue;
  }


  public void refresh()
  {
      DeviceAttribute           att = null;
      long                      t0 = System.currentTimeMillis();
     
      
      if (skippingRefresh) return;
      refreshCount++;
      trace(DeviceFactory.TRACE_REFRESHER, "DevStateScalar.refresh() method called for " + getName(), t0);
      try
      {
	  try 
	  {
	      // Read the attribute from device cache (readValueFromNetwork)
	      att = readValueFromNetwork();
              trace(DeviceFactory.TRACE_REFRESHER, "DevStateScalar.refresh(" + getName() + ") readValueFromNetwork success", t0);
	      if (att == null) return;
	      
	      // Retreive the read value for the attribute
	      devStateValue = fr.esrf.tangoatk.core.Device.toString(att.extractState());
	      
	      // Fire valueChanged
	      fireValueChanged(devStateValue);
              trace(DeviceFactory.TRACE_REFRESHER, "DevStateScalar.refresh(" + getName() + ") fireValueChanged(devStateValue) success", t0);
	  }
	  catch (DevFailed e)
	  {
              trace(DeviceFactory.TRACE_REFRESHER, "DevStateScalar.refresh(" + getName() + ") failed, caught DevFailed; will call readAttError", t0);
	      // Fire error event
	      readAttError(e.getMessage(), new AttributeReadException(e));
	  }
      }
      catch (Exception e)
      {
	  // Code failure
          trace(DeviceFactory.TRACE_REFRESHER, "DevStateScalar.refresh(" + getName() + ") Code failure, caught other Exception", t0);
	  System.out.println("DevStateScalar.refresh() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("DevStateScalar.refresh()------------------------------------------------");
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
	      devStateValue = fr.esrf.tangoatk.core.Device.toString(attValue.extractState());

	      // Fire valueChanged
	      fireValueChanged(devStateValue);

	  }
	  catch (DevFailed e)
	  {

          dispatchError(e);

	  }
      }
      catch (Exception e)
      {
	  // Code failure
	  System.out.println("DevStateScalar.dispatch() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("DevStateScalar.dispatch()------------------------------------------------");
      }
  }

  public void dispatchError(DevFailed e) {
    // Fire error event
    readAttError(e.getMessage(), new AttributeReadException(e));
  }

  public boolean isWritable()
  {
    return super.isWritable();
  }

  protected void fireValueChanged(String newValue) {
    devStateHelper.fireValueChanged(newValue, timeStamp);
  }

  public void addDevStateScalarListener(IDevStateScalarListener l) {
    devStateHelper.addDevStateScalarListener(l);
    addStateListener(l);
  }

  public void removeDevStateScalarListener(IDevStateScalarListener l) {
    devStateHelper.removeDevStateScalarListener(l);
    removeStateListener(l);
  }



  public String getDevStateValue()
  {
      return devStateValue;
  }
  
  
  
  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent evt) 
  {
      periodicCount++;
      DeviceAttribute     da=null;
      long                t0 = System.currentTimeMillis();

      trace(DeviceFactory.TRACE_PERIODIC_EVENT, "DevStateScalar.periodic method called for " + getName(), t0);
     
      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "DevStateScalar.periodicEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "DevStateScalar.periodicEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "DevStateScalar.periodicEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "DevStateScalar.periodicEvt.getValue(" + getName() + ") failed, got other error", t0);
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "DevStateScalar.periodicEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  System.out.println("DevStateScalar.periodic.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("DevStateScalar.periodic.getValue()------------------------------------------------");
          return;
      } // end of catch


      // read the attribute value from the received event!      
      if (da != null)
      {
          try
	  {
              setState(da); // To set the quality factor and fire AttributeState event
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "DevStateScalar.periodic(" + getName() + ") setState(da) called", t0);
              attribute = da;
              timeStamp = da.getTimeValMillisSec();
              // Retreive the read value for the attribute
              devStateValue = fr.esrf.tangoatk.core.Device.toString(da.extractState());
              // Fire valueChanged
              fireValueChanged(devStateValue);
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "DevStateScalar.periodic(" + getName() + ") fireValueChanged(devStateValue) called", t0);
          }
	  catch (DevFailed dfe)
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "DevStateScalar.periodic(" + getName() + ") failed, got DevFailed when called fireValueChanged(devStateValue)", t0);
              // Tango error
              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
          }
	  catch (Exception e) // Code failure
          {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "DevStateScalar.periodic(" + getName() + ") failed, got other Exception when called fireValueChanged(devStateValue)", t0);
              System.out.println("DevStateScalar.periodic: Device.toString(extractState()) Exception caught ------------------------------");
              e.printStackTrace();
              System.out.println("DevStateScalar.periodic: Device.toString(extractState())------------------------------------------------");
          } // end of catch
      }
  }
 
  
  
  
  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent evt) 
  {
      changeCount++;
      DeviceAttribute     da=null;
      long                t0 = System.currentTimeMillis();

      trace(DeviceFactory.TRACE_CHANGE_EVENT, "DevStateScalar.change method called for " + getName(), t0);
     
      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "DevStateScalar.changeEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "DevStateScalar.changeEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "DevStateScalar.changeEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "DevStateScalar.changeEvt.getValue(" + getName() + ") failed, got other error", t0);
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "DevStateScalar.changeEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  System.out.println("DevStateScalar.change.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("DevStateScalar.change.getValue()------------------------------------------------");
          return;
      } // end of catch


      // read the attribute value from the received event!      
      if (da != null)
      {
          try
	  {
              setState(da); // To set the quality factor and fire AttributeState event
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "DevStateScalar.change(" + getName() + ") setState(da) called", t0);
              attribute = da;
              timeStamp = da.getTimeValMillisSec();
              // Retreive the read value for the attribute
              devStateValue = fr.esrf.tangoatk.core.Device.toString(da.extractState());
              // Fire valueChanged
              fireValueChanged(devStateValue);
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "DevStateScalar.change(" + getName() + ") fireValueChanged(devStateValue) called", t0);
          }
	  catch (DevFailed dfe)
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "DevStateScalar.change(" + getName() + ") failed, got DevFailed when called fireValueChanged(devStateValue)", t0);
              // Tango error
              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
          }
	  catch (Exception e) // Code failure
          {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "DevStateScalar.change(" + getName() + ") failed, got other Exception when called fireValueChanged(devStateValue)", t0);
              System.out.println("DevStateScalar.change: Device.toString(extractState()) Exception caught ------------------------------");
              e.printStackTrace();
              System.out.println("DevStateScalar.change: Device.toString(extractState())------------------------------------------------");
          } // end of catch
      }
      
  }

  public boolean getInvertedOpenClose()
  {
       return invertOpenClose;
  }
  
  public boolean getInvertedInsertExtract()
  {
       return invertInsertExtract;
  }
  
  @Override
  public void loadAttProperties()
  {
     DbAttribute    dbAtt=null;
     DbDatum        propDbDatum=null;
 
     try
     {
         attPropertiesLoaded = true;
         dbAtt = this.getDevice().get_attribute_property(this.getNameSansDevice());
         if (dbAtt== null) return;
         
         if (!dbAtt.is_empty(fr.esrf.tangoatk.core.Device.OPEN_CLOSE_PROP))
         {
             propDbDatum = dbAtt.datum(fr.esrf.tangoatk.core.Device.OPEN_CLOSE_PROP);
             if (propDbDatum != null)
                if (!propDbDatum.is_empty())
                       invertOpenClose = propDbDatum.extractBoolean();
         }
         
         if (!dbAtt.is_empty(fr.esrf.tangoatk.core.Device.INSERT_EXTRACT_PROP))
         {
             propDbDatum = dbAtt.datum(fr.esrf.tangoatk.core.Device.INSERT_EXTRACT_PROP);
             if (propDbDatum != null)
                if (!propDbDatum.is_empty())
                       invertInsertExtract = propDbDatum.extractBoolean();
         }
     }
     catch (Exception ex)
     {
         System.out.println("get_attribute_property("+this.getName()+") thrown exception");
         ex.printStackTrace();
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
