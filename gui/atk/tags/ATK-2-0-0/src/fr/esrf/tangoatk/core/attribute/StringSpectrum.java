// File:          StringSpectrum.java
// Created:       2003-12-11 18:00:00, poncet
// By:            <poncet@esrf.fr>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;


import java.beans.*;

import fr.esrf.tangoatk.core.*;

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import fr.esrf.TangoApi.events.*;

public class StringSpectrum extends AAttribute
  implements IStringSpectrum {

  StringSpectrumHelper stringSpectHelper;
  String[] stringValues = null;

  public StringSpectrum() {
    stringSpectHelper = new StringSpectrumHelper(this);
  }


  public void setStringSpectrumValue(String[] s) {
    try {
      stringSpectHelper.insert(s);
      writeAtt();
      refresh();
    } catch (DevFailed df) {
      setAttError("Couldn't set value", new AttributeSetException(df));
    }
  }


  public String[] getStringSpectrumValue() {
    return stringValues;
  }


  public void addListener(IStringSpectrumListener l) {
    stringSpectHelper.addStringSpectrumListener(l);
    addStateListener(l);
  }

  public void removeListener(IStringSpectrumListener l) {
    stringSpectHelper.removeStringSpectrumListener(l);
    removeStateListener(l);
  }


  public void refresh() {

    if (skippingRefresh) return;

    try {

      try {

        // Retreive the value from the device
        readValueFromNetwork();
        stringValues = stringSpectHelper.extract();

        // Fire valueChanged
        fireValueChanged(stringValues);

      } catch (DevFailed e) {

        // Tango error
        stringValues = null;

        // Fire error event
        readAttError(e.getMessage(), new AttributeReadException(e));

      }

    } catch (Exception e) {

      // Code failure
      stringValues = null;

      System.out.println("StringSpectrum.refresh() Exception caught ------------------------------");
      e.printStackTrace();
      System.out.println("StringSpectrum.refresh()------------------------------------------------");

    }

  }

 
  
  
  
  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent evt) 
  {
      DeviceAttribute     da=null;
//System.out.println("StringSpectrum.periodic() called for : " + getName() );
      
      try
      {
          da = evt.getValue();
      }
      catch (DevFailed  dfe)
      {
//System.out.println("StringSpectrum.periodic() caught DevFailed for : " + getName());
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
//System.out.println("StringSpectrum.periodic() caught heartbeat DevFailed : " + getName());
             // Tango error
             stringValues = null;

             // Fire error event
             readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
//System.out.println("StringSpectrum.periodic() caught other DevFailed : " + getName() );
             // Tango error
             stringValues = null;

             // Fire error event
             readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
	  stringValues = null;

	  System.out.println("StringSpectrum.periodic.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("StringSpectrum.periodic.getValue()------------------------------------------------");
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
          stringValues = da.extractStringArray();
          // Fire valueChanged
          fireValueChanged(stringValues);
        } catch (DevFailed dfe) {
          // Tango error
          stringValues = null;

          // Fire error event
          readAttError(dfe.getMessage(), new AttributeReadException(dfe));
        } catch (Exception e) // Code failure
        {
          stringValues = null;

          System.out.println("StringSpectrum.periodic.extractStringArray() Exception caught ------------------------------");
          e.printStackTrace();
          System.out.println("StringSpectrum.periodic.extractStringArray()------------------------------------------------");
        } // end of catch
      }
      
  }
 
  
  
  
  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent evt) 
  {
      DeviceAttribute     da=null;
//System.out.println("StringSpectrum.change() called for : " + getName() );
      
      try
      {
          da = evt.getValue();
      }
      catch (DevFailed  dfe)
      {
//System.out.println("StringSpectrum.change() caught DevFailed for : " + getName());
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
//System.out.println("StringSpectrum.change() caught heartbeat DevFailed : " + getName());
             // Tango error
             stringValues = null;

             // Fire error event
             readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
//System.out.println("StringSpectrum.change() caught other DevFailed : " + getName() );
             // Tango error
             stringValues = null;

             // Fire error event
             readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
	  stringValues = null;

	  System.out.println("StringSpectrum.change.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("StringSpectrum.change.getValue()------------------------------------------------");
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
          stringValues = da.extractStringArray();
          // Fire valueChanged
          fireValueChanged(stringValues);
        } catch (DevFailed dfe) {
          // Tango error
          stringValues = null;

          // Fire error event
          readAttError(dfe.getMessage(), new AttributeReadException(dfe));
        } catch (Exception e) // Code failure
        {
          stringValues = null;

          System.out.println("StringSpectrum.change.extractStringArray() Exception caught ------------------------------");
          e.printStackTrace();
          System.out.println("StringSpectrum.change.extractStringArray()------------------------------------------------");
        } // end of catch
      }
      
  }
  

  public boolean isWritable() {
    return super.isWritable();
  }


  protected void fireValueChanged(String[] newValue) {
    propChanges.fireStringSpectrumEvent(this, newValue, timeStamp);
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
