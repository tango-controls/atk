// File:          StringAttribute.java
// Created:       2001-09-24 13:24:05, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-10 15:33:21, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;


import java.beans.*;

import fr.esrf.tangoatk.core.*;

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

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
	      readException.setError(e);
	      stringValue = null;
	      setPointValue = null;
	      // Fire error event
	      readAttError(e.getMessage(), readException);
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
      readException.setError(e);
      readAttError(e.getMessage(), readException);
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
	  readException.setError(e);
	  readAttError(e.getMessage(), readException);
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
