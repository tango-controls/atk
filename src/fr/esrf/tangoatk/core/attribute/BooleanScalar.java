// File:          BooleanScalar.java
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

public class BooleanScalar extends AAttribute
  implements IBooleanScalar {

  BooleanScalarHelper   booleanHelper;
  boolean               booleanValue = false;
  boolean               setPointValue = false;

  public BooleanScalar()
  {
    booleanHelper = new BooleanScalarHelper(this);
  }


  public boolean getValue()
  {
      return booleanValue;
  }
  
  
  public void setValue(boolean b)
  {
    try
    {
      attribute.insert(b);
      writeAtt();
//      refresh();
    }
    catch (DevFailed df)
    {
      setAttError("Couldn't set value", new AttributeSetException(df));
    }
    catch (Exception e)
    {
 System.out.println("Received un exception other than DevFailed while setting a booleanScalar");
      setAttError("Couldn't set value", new AttributeSetException("Set Exception other than DevFailed."));
    }
  }


  public int getXDimension() {
    return 1;
  }

  public int getMaxXDimension() {
    return 1;
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
	      booleanValue = att.extractBoolean();
	      
	      // Retreive the set point for the attribute
	      setPointValue = booleanHelper.getBooleanScalarSetPoint(att);

	      // Fire valueChanged
	      fireValueChanged(booleanValue);
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
	  System.out.println("BooleanScalar.refresh() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("BooleanScalar.refresh()------------------------------------------------");
      }
  }
  
  
  

  public boolean isWritable()
  {
    return super.isWritable();
  }

  protected void fireValueChanged(boolean newValue) {
    booleanHelper.fireValueChanged(newValue, timeStamp);
  }

  public void addBooleanScalarListener(IBooleanScalarListener l) {
    booleanHelper.addBooleanScalarListener(l);
    addStateListener(l);
  }

  public void removeBooleanScalarListener(IBooleanScalarListener l) {
    booleanHelper.removeBooleanScalarListener(l);
    removeStateListener(l);
  }

  

  public boolean getDeviceValue()
  {
      boolean readVal;
      try
      {
	  readVal = readValueFromNetwork().extractBoolean();
	  booleanValue = readVal;
      }
      catch (DevFailed e)
      {
	  // Fire error event
	  readAttError(e.getMessage(), new AttributeReadException(e));
      }
      catch (Exception e)
      {
	  // Code failure
	  System.out.println("BooleanScalar.getBooleanDeviceValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("BooleanScalar.getBooleanDeviceValue()------------------------------------------------");
      } // end of catch

      return booleanValue;
  }


  public boolean getSetPoint()
  {
      return setPointValue;
  }


  public boolean getDeviceSetPoint()
  {
      boolean setPoint = false;
      try
      {
	  setPoint =
	    booleanHelper.getBooleanScalarSetPoint(readDeviceValueFromNetwork());
	  setPointValue = setPoint;
      }
      catch (DevFailed e)
      {
	  readAttError(e.getMessage(), new AttributeReadException(e));
      }
      catch (Exception e)
      {
	  readAttError(e.getMessage(), e);
      } // end of catch

      return setPoint;
  }
   
  
  
  
  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent evt) 
  {
      periodicCount++;
      DeviceAttribute     da=null;
//System.out.println("BooleanScalar.periodic() called for : " + getName() );
      
      try
      {
          da = evt.getValue();
      }
      catch (DevFailed  dfe)
      {
//System.out.println("BooleanScalar.periodic() caught DevFailed for : " + getName());
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
//System.out.println("BooleanScalar.periodic() caught heartbeat DevFailed : " + getName());
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
//System.out.println("BooleanScalar.periodic() caught other DevFailed : " + getName() );
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
	  System.out.println("BooleanScalar.periodic.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("BooleanScalar.periodic.getValue()------------------------------------------------");
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
              booleanValue = da.extractBoolean();
              // Retreive the set point for the attribute
              setPointValue = booleanHelper.getBooleanScalarSetPoint(da);
              // Fire valueChanged
              fireValueChanged(booleanValue);
          }
	  catch (DevFailed dfe)
	  {
              // Tango error
              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
          }
	  catch (Exception e) // Code failure
          {
              System.out.println("BooleanScalar.periodic.extractBoolean() Exception caught ------------------------------");
              e.printStackTrace();
              System.out.println("BooleanScalar.periodic.extractBoolean()------------------------------------------------");
          } // end of catch
      }
      
  }
 
  
  
  
  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent evt) 
  {
      changeCount++;
      DeviceAttribute     da=null;
//System.out.println("BooleanScalar.change() called for : " + getName() );
      
      try
      {
          da = evt.getValue();
      }
      catch (DevFailed  dfe)
      {
//System.out.println("BooleanScalar.change() caught DevFailed for : " + getName());
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
//System.out.println("BooleanScalar.change() caught heartbeat DevFailed : " + getName());
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
//System.out.println("BooleanScalar.change() caught other DevFailed : " + getName() );
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
	  System.out.println("BooleanScalar.change.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("BooleanScalar.change.getValue()------------------------------------------------");
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
              booleanValue = da.extractBoolean();
              // Retreive the set point for the attribute
              setPointValue = booleanHelper.getBooleanScalarSetPoint(da);
              // Fire valueChanged
              fireValueChanged(booleanValue);
          }
	  catch (DevFailed dfe)
	  {
              // Tango error
              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
          }
	  catch (Exception e) // Code failure
          {
              System.out.println("BooleanScalar.change.extractBoolean() Exception caught ------------------------------");
              e.printStackTrace();
              System.out.println("BooleanScalar.change.extractBoolean()------------------------------------------------");
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


}
