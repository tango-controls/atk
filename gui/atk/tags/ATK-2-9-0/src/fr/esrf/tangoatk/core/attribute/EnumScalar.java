// File:          EnumScalar.java
// Created:       05/02/2007 poncet
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

/**
 * Enumerated scalar attributes (missing in Tango) are mapped in EnumScalar class
 * of ATK. In order to use them the device server attribute should be of type Dev_SHORT
 * or Dev_USHORT and should have a property called "EnumLabels" defined. This property should 
 * contain the list of all "enumerated" labels separated by "\n" (new line, return).
 * By convention the first label in the list is associated to the value zero and the
 * following labels are associated to the values increasing by 1 each time.
 * 
 * If the attribute property "EnumSetExclusion" is defined, it will be used to
 * exclude from the values authorized for writeAttribute some of the enumerated
 * labels defined in EnumLabels property. EnumSetExclusion is used to restrict
 * the authorized values for set to a subset of values defined in EnumLabels.
 * @author  poncet
 */

public class EnumScalar extends AAttribute implements IEnumScalar
{

  EnumScalarHelper              enumHelper=null;
  String                        scalarValue = null;
  String                        setPointValue = null;
  private String[]              enumLabels = null;
  private String[]              enmuSetExcludeLabels = null;
  private String[]              enumSetLabels = null;



  public EnumScalar(String[] enums, String[] setEnumExclusion)
  {
      enumLabels=enums;
      enmuSetExcludeLabels=setEnumExclusion;
      
      if (enmuSetExcludeLabels != null)
         setEnumSetLabels();

      if ( enumSetLabels == null)
         enumSetLabels = enumLabels;
/* Debug trace ... 
      if ( enumSetLabels != null)
      {
          for (int i=0; i<enumSetLabels.length; i++)
	      System.out.println("enumSetLabels["+i+"]="+enumSetLabels[i]);
      }
      */
  }
  
  private void setEnumSetLabels()
  {
      int    nbSetLabs, indSetLabel;
      
      nbSetLabs = enumLabels.length;
      
      for (int i=0; i<enmuSetExcludeLabels.length; i++)
          if (containsEnumLabel(enumLabels, enmuSetExcludeLabels[i]) )
	      nbSetLabs = nbSetLabs - 1;

      if (nbSetLabs <= 0)
         return;

      if (nbSetLabs ==  enumLabels.length)
         return;
	 
      enumSetLabels = new String[nbSetLabs];
      indSetLabel = 0;
      for (int i=0; i<enumLabels.length; i++)
          if (!containsEnumLabel(enmuSetExcludeLabels, enumLabels[i]) )
	  {
	      enumSetLabels[indSetLabel] = new String(enumLabels[i]);
	      indSetLabel++;
	  }
  }
  
  public static boolean containsEnumLabel (String[] labelList, String label)
  {
      
      if (label == null)
         return false;
      if (label.length() < 1)
         return false;
	 
      if (labelList == null)
         return false;
      if (labelList.length < 1)
         return false;
	 
      for (int i=0; i<labelList.length; i++)
          if (label.equals(labelList[i]))
	     return true;
	     
      return false;
  }
  
  public static String[] getNonEmptyLabels(String[] labelList)
  {
      String[]  copiedList, nonEmptyList;
      int       copyIndex;
      
      if (labelList == null)
         return null;
	 
      if (labelList.length < 1)
         return null;
      
      copiedList = new String[labelList.length];
      copyIndex = 0;
      for (int i=0; i<labelList.length; i++)
      {
         if (labelList[i] != null)
	    if (labelList[i].length() > 0)
	    {
	       copiedList[copyIndex]=labelList[i];
	       copyIndex++;
	    }
      }
      
      if (copyIndex == 0)
         return null;
      if (copyIndex == labelList.length)
         return copiedList;
	 
      nonEmptyList = new String[copyIndex];
      
      for (int i=0; i<nonEmptyList.length; i++)
          nonEmptyList[i]=copiedList[i];
      return nonEmptyList;
  }


  /** Overrides the getType() method in AAttribute **/
  public String getType()
  {
      return("EnumScalar");
  }

  
  public void setEnumHelper(EnumScalarHelper helper)
  {
      enumHelper = helper;
  }

  public String getEnumScalarValue()
  {
      return scalarValue;
  }

  public void setEnumScalarValue(String s) 
  {
     try
     {
	enumHelper.insert(s);
	writeAtt();
	   // The call to refresh() is suppressed due to the problem
	   // of polled attribute. All setter handle this
	   // issue by forcing a reading on the device instead
	   // refresh();
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
  

  // getEnumScalarSetPoint returns the attribute's setpoint value
  public String getEnumScalarSetPoint()
  {
      return setPointValue;
  }

  // getEnumScalarSetPointFromDevice  returns the attribute's setpoint value
  // This method makes a call to read attribute on the device proxy
  // Will force value reading via the device , ignore polling buffer
  public String getEnumScalarSetPointFromDevice()
  {
      String setPoint;
      try
      {
	  setPoint = enumHelper.getEnumScalarSetPoint(readDeviceValueFromNetwork());
	  if (containsEnumLabel(enumSetLabels, setPoint))
	     setPointValue = setPoint;
	  else
	     setPointValue = null;
      }
      catch (DevFailed e)
      {
	  readAttError(e.getMessage(), new AttributeReadException(e));
	  setPoint = null;
	  setPointValue = null;
      }
      catch (Exception e)
      {
	  readAttError(e.getMessage(), e);
	  setPoint = null;
	  setPointValue = null;
      } // end of catch

      return setPoint;
  }


  public int getXDimension()
  {
      return 1;
  }

  public int getMaxXDimension()
  {
     return 1;
  }
  
  
  public void addEnumScalarListener(IEnumScalarListener l)
  {
     enumHelper.addEnumScalarListener(l);
     addStateListener(l);
  }

  public void removeEnumScalarListener(IEnumScalarListener l)
  {
     enumHelper.removeEnumScalarListener(l);
     removeStateListener(l);
  }


  public void refresh()
  {
      DeviceAttribute           att = null;
      long                      t0 = System.currentTimeMillis();
      
      if (skippingRefresh) return;
      refreshCount++;
      trace(DeviceFactory.TRACE_REFRESHER, "EnumScalar.refresh() method called for " + getName(), t0);
      try
      {
	  try 
	  {
	      // Read the attribute from device cache (readValueFromNetwork)
	      att = readValueFromNetwork();
	      attribute = att;
	      if (att == null) return;
	      
	      // Retreive the read value for the attribute
	      scalarValue = enumHelper.getEnumScalarValue(att);
	      
	      // Retreive the set point for the attribute
	      setPointValue = enumHelper.getEnumScalarSetPoint(att);
	      if (!containsEnumLabel(enumSetLabels, setPointValue))
		 setPointValue = null;

	      // Fire valueChanged
	      enumHelper.fireEnumScalarValueChanged(scalarValue, timeStamp);
	  }
	  catch (AttributeReadException attEx)
	  {
	      scalarValue = null;
	      setPointValue = null;
	      // Fire error event
	      readAttError("Invalid enum value read.", attEx);
	  }
	  catch (DevFailed e)
	  {
	      // Tango error
	      scalarValue = null;
	      setPointValue = null;
	      // Fire error event
	      readAttError(e.getMessage(), new AttributeReadException(e));
	  }
      }
      catch (Exception e)
      {
	  // Code failure
	  scalarValue = null;
	  setPointValue = null;

	  System.out.println("EnumScalar.refresh() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("EnumScalar.refresh()------------------------------------------------");
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
	     scalarValue = enumHelper.getEnumScalarValue(attValue);

	     // Retreive the set point for the attribute
	     setPointValue = enumHelper.getEnumScalarSetPoint(attValue);
	     if (!containsEnumLabel(enumSetLabels, setPointValue))
		 setPointValue = null;

	     // Fire valueChanged
	     enumHelper.fireEnumScalarValueChanged(scalarValue, timeStamp);
	  }
	  catch (AttributeReadException attEx)
	  {
	      scalarValue = null;
	      setPointValue = null;
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
	  scalarValue = null;
	  setPointValue = null;

	  System.out.println("EnumScalar.dispatch() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("EnumScalar.dispatch()------------------------------------------------");
      }
  }

  public void dispatchError(DevFailed e)
  {
      // Tango error
      scalarValue = null;
      setPointValue = null;
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

  
  
  public String[] getSetEnumValues()
  {
      return enumSetLabels;
  }
 
         
  
  
  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent evt) 
  {
      periodicCount++;
      DeviceAttribute da = null;
      long t0 = System.currentTimeMillis();

      trace(DeviceFactory.TRACE_PERIODIC_EVENT, "EnumScalar.periodic method called for " + getName(), t0);

      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "EnumScalar.periodicEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "EnumScalar.periodicEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "EnumScalar.periodicEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
	      // Tango error
	      scalarValue = null;
	      setPointValue = null;
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "EnumScalar.periodicEvt.getValue(" + getName() + ") failed, got other error", t0);
	      // Tango error
	      scalarValue = null;
	      setPointValue = null;
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "EnumScalar.periodicEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  scalarValue = null;
	  setPointValue = null;

	  System.out.println("EnumScalar.periodic.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("EnumScalar.periodic.getValue()------------------------------------------------");
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
	    scalarValue = enumHelper.getEnumScalarValue(da);

	    // Retreive the set point for the attribute
	    setPointValue = enumHelper.getEnumScalarSetPoint(da);
	    if (!containsEnumLabel(enumSetLabels, setPointValue))
	       setPointValue = null;

	    // Fire valueChanged
	    enumHelper.fireEnumScalarValueChanged(scalarValue, timeStamp);

	 }
	 catch (AttributeReadException attEx)
	 {
	     scalarValue = null;
	     setPointValue = null;
	     // Fire error event
	     readAttError("Invalid enum value read.", attEx);
	 }
	 catch (DevFailed dfe)
	 {
            // Tango error
	    scalarValue = null;
	    setPointValue = null;
            // Fire error event
            readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	 }
	 catch (Exception e) // Code failure
	 {
	    scalarValue = null;
	    setPointValue = null;

            System.out.println("EnumScalar.periodic.extractString() Exception caught ------------------------------");
            e.printStackTrace();
            System.out.println("EnumScalar.periodic.extractString()------------------------------------------------");
	 } // end of catch
      }
      
  }
 
  
  
  
  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent evt) 
  {
      changeCount++;
      DeviceAttribute da = null;
      long t0 = System.currentTimeMillis();

      trace(DeviceFactory.TRACE_CHANGE_EVENT, "EnumScalar.change method called for " + getName(), t0);

      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "EnumScalar.changeEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "EnumScalar.changeEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "EnumScalar.changeEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
	      // Tango error
	      scalarValue = null;
	      setPointValue = null;
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "EnumScalar.changeEvt.getValue(" + getName() + ") failed, got other error", t0);
	      // Tango error
	      scalarValue = null;
	      setPointValue = null;
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "EnumScalar.changeEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  scalarValue = null;
	  setPointValue = null;

	  System.out.println("EnumScalar.change.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("EnumScalar.change.getValue()------------------------------------------------");
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
	    scalarValue = enumHelper.getEnumScalarValue(da);

	    // Retreive the set point for the attribute
	    setPointValue = enumHelper.getEnumScalarSetPoint(da);
	    if (!containsEnumLabel(enumSetLabels, setPointValue))
	       setPointValue = null;

	    // Fire valueChanged
	    enumHelper.fireEnumScalarValueChanged(scalarValue, timeStamp);

	 }
	 catch (AttributeReadException attEx)
	 {
	     scalarValue = null;
	     setPointValue = null;
	     // Fire error event
	     readAttError("Invalid enum value read.", attEx);
	 }
	 catch (DevFailed dfe)
	 {
            // Tango error
	    scalarValue = null;
	    setPointValue = null;
            // Fire error event
            readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	 }
	 catch (Exception e) // Code failure
	 {
	    scalarValue = null;
	    setPointValue = null;

            System.out.println("EnumScalar.change.extractString() Exception caught ------------------------------");
            e.printStackTrace();
            System.out.println("EnumScalar.change.extractString()------------------------------------------------");
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
