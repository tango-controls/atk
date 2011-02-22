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
	  insert(imageHelper.flatten(bImage));
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
  
  
  void insert(boolean[] bArr)
  {
      imageHelper.insert(bArr);
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
//System.out.println("BooleanImage.periodic() called for : " + getName() );
      
      try
      {
          da = evt.getValue();
      }
      catch (DevFailed  dfe)
      {
//System.out.println("BooleanImage.periodic() caught DevFailed for : " + getName());
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
//System.out.println("BooleanImage.periodic() caught heartbeat DevFailed : " + getName());
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
//System.out.println("BooleanImage.periodic() caught other DevFailed : " + getName() );
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
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
//System.out.println("BooleanImage.change() called for : " + getName() );
      
      try
      {
          da = evt.getValue();
      }
      catch (DevFailed  dfe)
      {
//System.out.println("BooleanImage.change() caught DevFailed for : " + getName());
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
//System.out.println("BooleanImage.change() caught heartbeat DevFailed : " + getName());
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
//System.out.println("BooleanImage.change() caught other DevFailed : " + getName() );
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
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
