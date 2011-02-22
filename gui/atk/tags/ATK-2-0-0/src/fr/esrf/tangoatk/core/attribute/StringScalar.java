// File:          StringAttribute.java
// Created:       2001-09-24 13:24:05, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-10 15:33:21, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import fr.esrf.TangoApi.events.*;

public class StringScalar extends AAttribute
  implements IStringScalar {

//    ANumberScalarHelper numberHelper;
  StringAttributeHelper stringHelper;
  String                stringValue = null;
  String                setPointValue = null;
  String[]              possibleValues = null;

  public StringScalar() {
    stringHelper = new StringAttributeHelper(this);
  }

  public String getString() {
    return getStringValue();
  }

  public void setValue(String s) {
    try {
      attribute.insert(s);
      writeAtt();
      //changed 0n 29/07/2003 by F. Poncet use refresh instead of fireValueChange
      //fireValueChanged(s);
      refresh();
    } catch (DevFailed df) {
      //changed 0n 29/07/2003 by F. Poncet ;
      //readException.setError(df);
      //changed 0n 29/07/2003 readAttError("Couldn't set value",  readException);
      setAttError("Couldn't set value", new AttributeSetException(df));
    }
  }

  public IScalarAttribute getWritableAttribute() {
    return null;
  }

  public IScalarAttribute getReadableAttribute() {
    return null;
  }

  public void setString(String s) {
    setValue(s);
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
      try 
      {
	  try 
	  {
	      // Read the attribute from device cache (readValueFromNetwork)
	      att = readValueFromNetwork();
	      if (att == null) return;
	      
	      // Retreive the read value for the attribute
	      stringValue = att.extractString();
	      
	      // Retreive the set point for the attribute
	      setPointValue = stringHelper.getStringScalarSetPoint(att);

	      // Fire valueChanged
	      fireValueChanged(stringValue);
	  }
	  catch (DevFailed e)
	  {
	      // Tango error
	      stringValue = null;
	      setPointValue = null;
	      // Fire error event
	      readAttError(e.getMessage(), new AttributeReadException(e));
	  }
      }
      catch (Exception e)
      {
	  // Code failure
	  stringValue = null;
	  setPointValue = null;

	  System.out.println("StringScalar.refresh() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("StringScalar.refresh()------------------------------------------------");
      }
  }
  
  
  

  public boolean isWritable() {
    return super.isWritable();
  }

  protected void fireValueChanged(String newValue) {
    propChanges.fireStringScalarEvent(this, newValue, timeStamp);
  }

  public void addStringScalarListener(IStringScalarListener l) {
    propChanges.addStringScalarListener(l);
    addStateListener(l);
  }

  public void removeStringScalarListener(IStringScalarListener l) {
    propChanges.removeStringScalarListener(l);
    removeStateListener(l);
  }


  public IStringScalarHistory[] getStringScalarHistory() {
    StringScalarHistory[] attHist;

    attHist = null;
    try {
      attHist = (StringScalarHistory[]) stringHelper.getScalarAttHistory(readAttHistoryFromNetwork());
    } catch (DevFailed e) {
      readAttError(e.getMessage(), new AttributeReadException(e));
      attHist = null;
    } catch (Exception e) {
      readAttError(e.getMessage(), e);
      attHist = null;
    } // end of catch

    return attHist;
  }



  public String getStringValue()
  {
      return stringValue;
  }


  public String getStringSetPoint()
  {
      return setPointValue;
  }


  public String getStringDeviceSetPoint()
  {
      String setPoint;
      try
      {
	  setPoint =
	    stringHelper.getStringScalarSetPoint(readValueFromNetwork());
	  setPointValue = setPoint;
      }
      catch (DevFailed e)
      {
	  readAttError(e.getMessage(), new AttributeReadException(e));
	  setPoint = "DevFailed";
	  setPointValue = null;
      }
      catch (Exception e)
      {
	  readAttError(e.getMessage(), e);
	  setPoint = "Exception";
	  setPointValue = null;
      } // end of catch

      return setPoint;
  }
  
  
  public void setPossibleValues(String[]  vals)
  {
      if (vals == null)
         return;
	 
      if (possibleValues == null)
      {
         if (vals.length > 0)
	    possibleValues = vals;
      }
  }
  
  
  public String[] getPossibleValues()
  {
      return possibleValues;
  }
 
  
  
  
  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent evt) 
  {
      DeviceAttribute     da=null;
//System.out.println("StringScalar.periodic() called for : " + getName() );
      
      try
      {
          da = evt.getValue();
      }
      catch (DevFailed  dfe)
      {
//System.out.println("StringScalar.periodic() caught DevFailed for : " + getName());
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
//System.out.println("StringScalar.periodic() caught heartbeat DevFailed : " + getName());
	      // Tango error
	      stringValue = null;
	      setPointValue = null;
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
//System.out.println("StringScalar.periodic() caught other DevFailed : " + getName() );
	      // Tango error
	      stringValue = null;
	      setPointValue = null;
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
	  stringValue = null;
	  setPointValue = null;

	  System.out.println("StringScalar.periodic.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("StringScalar.periodic.getValue()------------------------------------------------");
          return;
      } // end of catch


      // read the attribute value from the received event!      
      if (da != null)
      {
        try {
          setState(da); // To set the quality factor and fire AttributeState event
          attribute = da;
          timeStamp = da.getTimeValMillisSec();
          // Retreive the read value for the attribute
          stringValue = da.extractString();
          // Retreive the set point for the attribute
          setPointValue = stringHelper.getStringScalarSetPoint(da);
          // Fire valueChanged
          fireValueChanged(stringValue);
        } catch (DevFailed dfe) {
          // Tango error
          stringValue = null;
          setPointValue = null;
          // Fire error event
          readAttError(dfe.getMessage(), new AttributeReadException(dfe));
        } catch (Exception e) // Code failure
        {
          stringValue = null;
          setPointValue = null;

          System.out.println("StringScalar.periodic.extractString() Exception caught ------------------------------");
          e.printStackTrace();
          System.out.println("StringScalar.periodic.extractString()------------------------------------------------");
        } // end of catch
      }
      
  }
 
  
  
  
  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent evt) 
  {
      DeviceAttribute     da=null;
//System.out.println("StringScalar.change() called for : " + getName() );
      
      try
      {
          da = evt.getValue();
      }
      catch (DevFailed  dfe)
      {
//System.out.println("StringScalar.change() caught DevFailed for : " + getName());
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
//System.out.println("StringScalar.change() caught heartbeat DevFailed : " + getName());
	      // Tango error
	      stringValue = null;
	      setPointValue = null;
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
//System.out.println("StringScalar.change() caught other DevFailed : " + getName() );
	      // Tango error
	      stringValue = null;
	      setPointValue = null;
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
	  stringValue = null;
	  setPointValue = null;

	  System.out.println("StringScalar.change.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("StringScalar.change.getValue()------------------------------------------------");
          return;
      } // end of catch


      // read the attribute value from the received event!      
      if (da != null)
      {
        try {
          setState(da); // To set the quality factor and fire AttributeState event
          attribute = da;
          timeStamp = da.getTimeValMillisSec();
          // Retreive the read value for the attribute
          stringValue = da.extractString();
          // Retreive the set point for the attribute
          setPointValue = stringHelper.getStringScalarSetPoint(da);
          // Fire valueChanged
          fireValueChanged(stringValue);
        } catch (DevFailed dfe) {
          // Tango error
          stringValue = null;
          setPointValue = null;
          // Fire error event
          readAttError(dfe.getMessage(), new AttributeReadException(dfe));
        } catch (Exception e) // Code failure
        {
          stringValue = null;
          setPointValue = null;

          System.out.println("StringScalar.change.extractString() Exception caught ------------------------------");
          e.printStackTrace();
          System.out.println("StringScalar.change.extractString()------------------------------------------------");
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
