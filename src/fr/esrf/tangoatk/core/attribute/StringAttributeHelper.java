// File:          StringAttributeHelper.java
// Created:       2001-12-04 14:39:50, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-10 17:31:19, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;


import java.util.*;

import fr.esrf.tangoatk.core.*;

import fr.esrf.TangoApi.*;
import fr.esrf.Tango.AttrQuality;
import fr.esrf.Tango.DevFailed;

class StringAttributeHelper extends ANumberScalarHelper {

    StringAttributeHelper(IAttribute attribute) {
	setAttribute(attribute);
    }

    void setMinAlarm(double d, boolean writable) {
	setProperty("min_alarm", new Double(Double.NaN), writable);
    }
    
    void setMaxAlarm(double d, boolean writable) {
	setProperty("max_alarm", new Double(Double.NaN), writable);
    }

    void setMinValue(double d, boolean writable) {
	setProperty("min_value", new Double(Double.NaN), writable);
    }
    
    void setMaxValue(double d, boolean writable) {
	setProperty("max_value", new Double(Double.NaN), writable);
    }

    void setMinAlarm(double d) {
	setProperty("min_alarm", new Double(Double.NaN));
    }
    
    void setMaxAlarm(double d) {
	setProperty("max_alarm", new Double(Double.NaN));
    }

    void setMinValue(double d) {
	setProperty("min_value", new Double(Double.NaN));
    }
    
    void setMaxValue(double d) {
	setProperty("max_value", new Double(Double.NaN));
    }

    void insert(double d) {
	attribute.getAttribute().insert(new Double(d).toString());
    }

    double getNumberScalarValue(DeviceAttribute attribute) {
	return Double.NaN;
    }

    double getNumberScalarSetPoint(DeviceAttribute attribute) {
	return Double.NaN;
    }
    
    protected StringScalarHistory[] getStringScalarAttHistory(DeviceDataHistory[] attPollHist)
    {
    
       List  hist;
       StringScalarHistory   histElem;
       fr.esrf.Tango.AttrQuality attq;
       int                       i;
       
       if (attPollHist.length <= 0)
         return null;
	 
       hist = new Vector();
	        
       for (i=0; i<attPollHist.length; i++)
       {
            histElem = new StringScalarHistory();


            try
	    {
		histElem.setTimestamp(attPollHist[i].getTime());
	    }
	    catch (Exception ex)
	    {
		histElem.setTimestamp(0);
	    }


            try
	    {
		attq = attPollHist[i].getAttrQuality();

		if (AttrQuality._ATTR_VALID   == attq.value() )
		{
		   histElem.setState(IAttribute.VALID);
		}
		else
		{
		   if (AttrQuality._ATTR_INVALID == attq.value())
		   {
		       histElem.setState(IAttribute.INVALID);
		   }
		   else
		   {
		      if (AttrQuality._ATTR_ALARM   == attq.value())
		      {
			  histElem.setState(IAttribute.ALARM);
	              }
		      else
			  histElem.setState(IAttribute.UNKNOWN);
		   }
		}	    
	    }
	    catch (Exception ex)
	    {
		histElem.setState(IAttribute.UNKNOWN);
	    }


            try
	    {
		histElem.setValue(attPollHist[i].extractString());
	    }
	    catch (Exception ex)
	    {
	        histElem.setValue(null);
	    }
	    
	    hist.add(i, histElem);
       }
       
       StringScalarHistory[]  histArray;
       
       histArray = (StringScalarHistory[]) hist.toArray(new StringScalarHistory[0]);
       
       return histArray;
    }

    protected IAttributeScalarHistory[] getScalarAttHistory(DeviceDataHistory[] attPollHist)
    {
      return (getStringScalarAttHistory(attPollHist));
    }


    public String getStringScalarSetPoint(DeviceAttribute attribute)
    {

	String[]  str_arr=null;
    try {
	  str_arr = attribute.extractStringArray();
    } catch( DevFailed e ) {}
      
	if (str_arr == null)
	   return "???";
	
	if (str_arr.length < 1)
	   return "???";
	   
	if (str_arr.length > 1)
	   return str_arr[1];
	else // The attribute WRITE (WRITE ONLY) return their setPoint in the first element
	   return str_arr[0];
    }



    public String getVersion() {
	return "$Id$";
    }
}
