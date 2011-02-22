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
 
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;
import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import fr.esrf.TangoApi.events.*;

public class NumberSpectrum extends ANumber  implements INumberSpectrum
{
  double[] spectrumValue;
  double[] spectrumSetPointValue;
  double[] devSpectrumValue;
  double[] devSpectrumSetPointValue;
  boolean  hasXminmaxProperties = false;
  boolean  hasXminmaxAttributes = false;
  String   XminAttName = null;
  String   XmaxAttName = null;
  double   xminValue = -1.0;
  double   xmaxValue = -1.0;

  // TODO better solution : TEMP
  public ANumberSpectrumHelper getNumberSpectrumHelper()
  {
	  return (ANumberSpectrumHelper)getNumberHelper();
  }
  
  // ----------------------------------
  
  public void addSpectrumListener(ISpectrumListener l) {
    propChanges.addSpectrumListener(l);
  }

  public void removeSpectrumListener(ISpectrumListener l) {
    propChanges.removeSpectrumListener(l);
  }


/* Replaced by F. Poncet on 06/jan/2003
    public void setValue(double[][] d) throws AttributeSetException {
	setValue(d[0]);
    }
*/

  public void setValue(double[][] d) {
    setValue(d[0]);
  }

  public double[][] getValue() {
    double[][] val = new double[1][];
    val[0] = getSpectrumValue();
    return val;
  }


  public void setValue(double[] d) {
    try {
      insert(d);
      writeAtt();
      refresh();
    } catch (DevFailed df) {
      setAttError("Couldn't set value", new AttributeSetException(df));
    }
  }

  protected void checkDimensions(double[] o) {
    if (o.length > getMaxXDimension()) {
      throw new IllegalStateException();
    }
  }


  protected void insert(double[] d) {
    checkDimensions(d);
    getNumberSpectrumHelper().insert(d);
  }

  public void refresh() {

    if (skippingRefresh) return;
    refreshCount++;
    try {

      try {

	// Retreive the value from the device
        DeviceAttribute     da = readValueFromNetwork();
	spectrumValue = getNumberSpectrumHelper().getNumberSpectrumDisplayValue(da); //convert to display unit
	spectrumSetPointValue = getNumberSpectrumHelper().getNumberSpectrumDisplaySetPoint(da); //convert to display unit
	devSpectrumValue = getNumberSpectrumHelper().getNumberSpectrumValue(da); 
	devSpectrumSetPointValue = getNumberSpectrumHelper().getNumberSpectrumSetPoint(da);
        timeStamp = da.getTimeValMillisSec();
        // Fire valueChanged
        getNumberSpectrumHelper().fireSpectrumValueChanged(spectrumValue, timeStamp);

      } catch (DevFailed e) {

        // Tango error
        spectrumValue = null;
        spectrumSetPointValue = null;
        devSpectrumValue = null;
        devSpectrumSetPointValue = null;

        // Fire error event
        readAttError(e.getMessage(), new AttributeReadException(e));

      }

    } catch (Exception e) {

      // Code failure
      spectrumValue = null;
      spectrumSetPointValue = null;
      devSpectrumValue = null;
      devSpectrumSetPointValue = null;

      System.out.println("NumberSpectrum.refresh() Exception caught ------------------------------");
      e.printStackTrace();
      System.out.println("NumberSpectrum.refresh()------------------------------------------------");

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

        // Retreive the value from the device
        spectrumValue = getNumberSpectrumHelper().getNumberSpectrumDisplayValue(attValue); //convert to display unit
	spectrumSetPointValue = getNumberSpectrumHelper().getNumberSpectrumDisplaySetPoint(attValue); //convert to display unit
	devSpectrumValue = getNumberSpectrumHelper().getNumberSpectrumValue(attValue); 
	devSpectrumSetPointValue = getNumberSpectrumHelper().getNumberSpectrumSetPoint(attValue);

        // Fire valueChanged
        getNumberSpectrumHelper().fireSpectrumValueChanged(spectrumValue, timeStamp);

      } catch (DevFailed e) {

        dispatchError(e);

      }

    } catch (Exception e) {

      // Code failure
      spectrumValue = null;
      spectrumSetPointValue = null;
      devSpectrumValue = null;
      devSpectrumSetPointValue = null;

      System.out.println("NumberSpectrum.dispatch() Exception caught ------------------------------");
      e.printStackTrace();
      System.out.println("NumberSpectrum.dispatch()------------------------------------------------");

    }

  }

  public void dispatchError(DevFailed e) {

    // Tango error
    spectrumValue = null;
    spectrumSetPointValue = null;
    devSpectrumValue = null;
    devSpectrumSetPointValue = null;

    // Fire error event
    readAttError(e.getMessage(), new AttributeReadException(e));

  }



  // getSpectrumValue returns the attribute value in display_unit
  public double[] getSpectrumValue()
  {
      return spectrumValue;
  }
  
  // getSpectrumDeviceValue returns the attribute value in the device server unit
  public double[] getSpectrumDeviceValue()
  {
      return devSpectrumValue;      
  }
  
  // getSpectrumStandardValue returns the attribute value in the standard unit
  public double[] getSpectrumStandardValue()
  {
        double[]  stdVal;
        double    stdUnitFactor = 1.0;
        	
	stdUnitFactor = getStandardUnitFactor();
	      
	if (stdUnitFactor <= 0)
	   stdUnitFactor = 1.0;
	   	   
	if (stdUnitFactor == 1.0)
	   return devSpectrumValue;
        
        if (devSpectrumValue == null) return null;

        stdVal = new double[devSpectrumValue.length];
        for (int i=0; i < devSpectrumValue.length; i++)
            stdVal[i] = devSpectrumValue[i] * stdUnitFactor; //return the value in the standard unit
	return stdVal;      
  }
    
  // getSpectrumSetPoint returns the attribute's setpoint value in display_unit
  public double[] getSpectrumSetPoint()
  {
      return spectrumSetPointValue;
  }
  
  // getSpectrumDeviceSetPoint returns the attribute's setpoint value in the device server unit
  public double[] getSpectrumDeviceSetPoint()
  {
      return devSpectrumSetPointValue;
  }
  
  // getSpectrumStandardSetPoint returns the attribute's setpoint value in the standard unit
  public double[] getSpectrumStandardSetPoint()
  {
        double[]  stdVal;
        double    stdUnitFactor = 1.0;
	
	stdUnitFactor = getStandardUnitFactor();
	      
	if (stdUnitFactor <= 0)
	   stdUnitFactor = 1.0;
	   	   
	if (stdUnitFactor == 1.0)
	   return devSpectrumSetPointValue;
        
        if (devSpectrumSetPointValue == null) return null;

        stdVal = new double[devSpectrumSetPointValue.length];
        for (int i=0; i < devSpectrumSetPointValue.length; i++)
            stdVal[i] = devSpectrumSetPointValue[i] * stdUnitFactor; //return the value in the standard unit
	return stdVal;            
  }

  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent evt) 
  {
      periodicCount++;
      DeviceAttribute     da=null;
      long t0 = System.currentTimeMillis();
      
      trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberSpectrum.periodic method called for " + getName(), t0);
      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberSpectrum.periodicEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
	  trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberSpectrum.periodicEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberSpectrum.periodicEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
              // Tango error
              spectrumValue = null;
              spectrumSetPointValue = null;
              devSpectrumValue = null;
              devSpectrumSetPointValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberSpectrum.periodicEvt.getValue(" + getName() + ") failed, got other error", t0);
              // Tango error
              spectrumValue = null;
              spectrumSetPointValue = null;
              devSpectrumValue = null;
              devSpectrumSetPointValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "NumberSpectrum.periodicEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
          spectrumValue = null;
          spectrumSetPointValue = null;
          devSpectrumValue = null;
          devSpectrumSetPointValue = null;

	  System.out.println("NumberSpectrum.periodic.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("NumberSpectrum.periodic.getValue()------------------------------------------------");
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
            // Retreive the value from the device
            spectrumValue = getNumberSpectrumHelper().getNumberSpectrumDisplayValue(da); //convert to display unit
            spectrumSetPointValue = getNumberSpectrumHelper().getNumberSpectrumDisplaySetPoint(da); //convert to display unit
            devSpectrumValue = getNumberSpectrumHelper().getNumberSpectrumValue(da); 
            devSpectrumSetPointValue = getNumberSpectrumHelper().getNumberSpectrumSetPoint(da);

            // Fire valueChanged
            getNumberSpectrumHelper().fireSpectrumValueChanged(spectrumValue, timeStamp);
	 }
	 catch (DevFailed dfe)
	 {
            spectrumValue = null;
            spectrumSetPointValue = null;
            devSpectrumValue = null;
            devSpectrumSetPointValue = null;
            // Fire error event
            readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	 }
	 catch (Exception e) // Code failure
	 {
            spectrumValue = null;
            spectrumSetPointValue = null;
            devSpectrumValue = null;
            devSpectrumSetPointValue = null;
            System.out.println("NumberSpectrum.periodic.getNumberSpectrumDisplayValue() Exception caught ------------------------------");
            e.printStackTrace();
            System.out.println("NumberSpectrum.periodic.getNumberSpectrumDisplayValue()------------------------------------------------");
	 } // end of catch
      }
      
  }

  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent evt) 
  {
      changeCount++;
      DeviceAttribute     da=null;
      long t0 = System.currentTimeMillis();
      
      trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberSpectrum.change method called for " + getName(), t0);
      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberSpectrum.changeEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
	  trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberSpectrum.changeEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberSpectrum.changeEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
              // Tango error
              spectrumValue = null;
              spectrumSetPointValue = null;
              devSpectrumValue = null;
              devSpectrumSetPointValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberSpectrum.changeEvt.getValue(" + getName() + ") failed, got other error", t0);
              // Tango error
              spectrumValue = null;
              spectrumSetPointValue = null;
              devSpectrumValue = null;
              devSpectrumSetPointValue = null;

              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "NumberSpectrum.changeEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
          spectrumValue = null;
          spectrumSetPointValue = null;
          devSpectrumValue = null;
          devSpectrumSetPointValue = null;

	  System.out.println("NumberSpectrum.change.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("NumberSpectrum.change.getValue()------------------------------------------------");
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
            // Retreive the value from the device
            spectrumValue = getNumberSpectrumHelper().getNumberSpectrumDisplayValue(da); //convert to display unit
            spectrumSetPointValue = getNumberSpectrumHelper().getNumberSpectrumDisplaySetPoint(da); //convert to display unit
            devSpectrumValue = getNumberSpectrumHelper().getNumberSpectrumValue(da); 
            devSpectrumSetPointValue = getNumberSpectrumHelper().getNumberSpectrumSetPoint(da);

            // Fire valueChanged
            getNumberSpectrumHelper().fireSpectrumValueChanged(spectrumValue, timeStamp);
	 }
	 catch (DevFailed dfe)
	 {
            spectrumValue = null;
            spectrumSetPointValue = null;
            devSpectrumValue = null;
            devSpectrumSetPointValue = null;
            // Fire error event
            readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	 }
	 catch (Exception e) // Code failure
	 {
            spectrumValue = null;
            spectrumSetPointValue = null;
            devSpectrumValue = null;
            devSpectrumSetPointValue = null;
            System.out.println("NumberSpectrum.change.getNumberSpectrumDisplayValue() Exception caught ------------------------------");
            e.printStackTrace();
            System.out.println("NumberSpectrum.change.getNumberSpectrumDisplayValue()------------------------------------------------");
	 } // end of catch
      }
  }
  
  public void freeInternalData()
  {
     super.freeInternalData();
     spectrumValue = null;
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

  public int getYDimension() {
    return 1;
  }

  public int getMaxYDimension() {
    return 1;
  }

  public boolean hasMinxMaxxAttributes()
  {
     return hasXminmaxAttributes;
  }

  public boolean hasMinxMaxxProperties()
  {
     return hasXminmaxProperties;
  }
  
  public String getMinxAttName()
  {
     return XminAttName;
  }
   
  public String getMaxxAttName()
  {
     return XmaxAttName;
  }
  
  public double getMinx()
  {
     return xminValue;
  }
  
  public double getMaxx()
  {
     return xmaxValue;
  }
 
  
  @Override
  public void loadAttProperties()
  {
     DbAttribute    dbAtt=null;
     DbDatum        propDbDatum=null;
     String         xmin_att_name=null, xmax_att_name=null;
     double         xmin_prop_val=-1.0, xmax_prop_val=-1.0;
     boolean        hasXmin=false, hasXmax=false;
 
     try
     {
         attPropertiesLoaded = true;
         dbAtt = this.getDevice().get_attribute_property(this.getNameSansDevice());
         if (dbAtt== null) return;
         
         if (!dbAtt.is_empty(INumberSpectrum.XMIN_ATT_PROP))
         {
             propDbDatum = dbAtt.datum(INumberSpectrum.XMIN_ATT_PROP);
             if (propDbDatum != null)
                if (!propDbDatum.is_empty())
                       xmin_att_name = propDbDatum.extractString();
         }
          
         if (!dbAtt.is_empty(INumberSpectrum.XMAX_ATT_PROP))
         {
             propDbDatum = dbAtt.datum(INumberSpectrum.XMAX_ATT_PROP);
             if (propDbDatum != null)
                if (!propDbDatum.is_empty())
                       xmax_att_name = propDbDatum.extractString();
         }
         
         if ((xmin_att_name != null) && (xmax_att_name != null))
            if ((xmin_att_name.length() > 0) && (xmax_att_name.length() > 0))
               hasXminmaxAttributes = true;
         
         if (hasXminmaxAttributes)
         {
            XminAttName = xmin_att_name;
            XmaxAttName = xmax_att_name;
            return;
         }
         
         // If the Xmin and Xmax att names are not found perhaps we can find Xmin and Xmax constant properties
         if (!dbAtt.is_empty(INumberSpectrum.XMIN_PROP))
         {
             propDbDatum = dbAtt.datum(INumberSpectrum.XMIN_PROP);
             if (propDbDatum != null)
                if (!propDbDatum.is_empty())
                {
                   hasXmin = true;
                   xmin_prop_val = propDbDatum.extractDouble();
                }
         }
          
         if (!dbAtt.is_empty(INumberSpectrum.XMAX_PROP))
         {
             propDbDatum = dbAtt.datum(INumberSpectrum.XMAX_PROP);
             if (propDbDatum != null)
                if (!propDbDatum.is_empty())
                {
                   hasXmax = true;
                   xmax_prop_val = propDbDatum.extractDouble();
                }
         }
         
         if (hasXmin && hasXmax)
         {
             hasXminmaxProperties = true;
             xminValue = xmin_prop_val;
             xmaxValue = xmax_prop_val;
         }
     }
     catch (Exception ex)
     {
         System.out.println("get_attribute_property("+this.getName()+") thrown exception");
         ex.printStackTrace();
     }
     
  }



}
