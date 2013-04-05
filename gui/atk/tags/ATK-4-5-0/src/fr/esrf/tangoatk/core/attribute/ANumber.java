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
 
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.TangoApi.AttributeInfoEx;

public abstract class ANumber extends AAttribute
{
    private NumberAttributeHelper numberHelper = null; 

    public double getMinValue() {
	    return getPropertyStorage().getNumberProperty("min_value");
    }

    public double getMaxValue() {
	    return getPropertyStorage().getNumberProperty("max_value");
    }

    public double getMinAlarm() {
	    return getPropertyStorage().getNumberProperty("min_alarm");
    }

    public double getMaxAlarm() {
	    return getPropertyStorage().getNumberProperty("max_alarm");
    }

    public double getMinWarning() {
	    return getPropertyStorage().getNumberProperty("min_warning");
    }

    public double getMaxWarning() {
	    return getPropertyStorage().getNumberProperty("max_warning");
    }

    public double getDeltaT() {
	    return getPropertyStorage().getNumberProperty("delta_t");
    }

    public double getDeltaVal() {
	    return getPropertyStorage().getNumberProperty("delta_val");
    }

    public void setMinValue(double d) {
	    numberHelper.setMinValue(d);
    }

    public void setMaxValue(double d) {
	    numberHelper.setMaxValue(d);
    }

    public void setMinAlarm(double d) {
	    numberHelper.setMinAlarm(d);
    }

    public void setMaxAlarm(double d) {
	    numberHelper.setMaxAlarm(d);
    }

    public void setMinWarning(double d) {
	    numberHelper.setMinWarning(d);
    }

    public void setMaxWarning(double d) {
	    numberHelper.setMaxWarning(d);
    }

    public void setDeltaT(double d) {
	    numberHelper.setDeltaT(d);
    }

    public void setDeltaVal(double d) {
	    numberHelper.setDeltaVal(d);
    }

    public void setMinValue(double d, boolean writable) {
	    numberHelper.setMinValue(d, writable);
    }

    public void setMaxValue(double d, boolean writable) {
	    numberHelper.setMaxValue(d, writable);
    }

    public void setMinAlarm(double d, boolean writable) {
	    numberHelper.setMinAlarm(d, writable);
    }

    public void setMaxAlarm(double d, boolean writable) {
	    numberHelper.setMaxAlarm(d, writable);
    }

    public void setMinWarning(double d, boolean writable) {
	    numberHelper.setMinWarning(d, writable);
    }

    public void setMaxWarning(double d, boolean writable) {
	    numberHelper.setMaxWarning(d, writable);
    }

    public void setDeltaT(double d, boolean writable) {
	    numberHelper.setDeltaT(d, writable);
    }

    public void setDeltaVal(double d, boolean writable) {
	    numberHelper.setDeltaVal(d, writable);
    }

    public NumberAttributeHelper getNumberHelper() {
	    return numberHelper;
    }

    public void setNumberHelper(NumberAttributeHelper numberHelper) {
	    this.numberHelper = numberHelper;
    }

    public void setConfiguration(AttributeInfoEx c)
    {
	super.setConfiguration(c);

	try 
	{
     double d = new Double(config.min_value).doubleValue();
	   setMinValue(d, true);
     if(Double.isNaN(d)) getProperty("min_value").setSpecified(false);
	} 
	catch (NumberFormatException e)
	{
	   setMinValue(Double.NaN, true);
	   getProperty("min_value").setSpecified(false);
	} // end of try-catch

	try
	{
     double d = new Double(config.max_value).doubleValue();
	   setMaxValue(d, true);
     if(Double.isNaN(d)) getProperty("max_value").setSpecified(false);
	} 
	catch (NumberFormatException e)
	{
	   setMaxValue(Double.NaN, true);
	   getProperty("max_value").setSpecified(false);
	} // end of try-catch

	try
	{
     double d;
	   if(config.alarms!=null) {
       d = new Double(config.alarms.min_alarm).doubleValue();
     } else {
       d = new Double(config.min_alarm).doubleValue();
     }
     setMinAlarm(d, true);
     if(Double.isNaN(d)) getProperty("min_alarm").setSpecified(false);
	}
	catch (NumberFormatException e)
	{
	   setMinAlarm(Double.NaN, true);
	   getProperty("min_alarm").setSpecified(false);
	} // end of try-catch

	try
	{
     double d;
	   if(config.alarms!=null) {
       d = new Double(config.alarms.max_alarm).doubleValue();
     } else {
       d = new Double(config.max_alarm).doubleValue();
     }
     setMaxAlarm(d, true);
     if(Double.isNaN(d)) getProperty("max_alarm").setSpecified(false);
	}
	catch (NumberFormatException e)
	{
	   setMaxAlarm(Double.NaN, true);
	   getProperty("max_alarm").setSpecified(false);
	} // end of try-catch

	try
	{
	   if(config.alarms!=null) {
       double d = new Double(config.alarms.min_warning).doubleValue();
       setMinWarning(d, true);
       if(Double.isNaN(d)) getProperty("min_warning").setSpecified(false);
     }
	}
	catch (NumberFormatException e)
	{
	   setMinWarning(Double.NaN, true);
	   getProperty("min_warning").setSpecified(false);
	} // end of try-catch

	try
	{
	   if(config.alarms!=null) {
       double d = new Double(config.alarms.max_warning).doubleValue();
       setMaxWarning(d, true);
       if(Double.isNaN(d)) getProperty("max_warning").setSpecified(false);
     }
	}
	catch (NumberFormatException e)
	{
	   setMaxWarning(Double.NaN, true);
	   getProperty("max_warning").setSpecified(false);
	} // end of try-catch

	try
	{
	   if(config.alarms!=null) {
       double d = new Double(config.alarms.delta_t).doubleValue();
       setDeltaT(d, true);
       if(Double.isNaN(d)) getProperty("delta_t").setSpecified(false);
     }
	}
	catch (NumberFormatException e)
	{
	   setDeltaT(Double.NaN, true);
	   getProperty("delta_t").setSpecified(false);
	} // end of try-catch

	try
	{
	   if(config.alarms!=null) {
       double d = new Double(config.alarms.delta_val).doubleValue();
       setDeltaVal(d, true);
       if(Double.isNaN(d)) getProperty("delta_val").setSpecified(false);
     }
	}
	catch (NumberFormatException e)
	{
	   setDeltaVal(Double.NaN, true);
	   getProperty("delta_val").setSpecified(false);
	} // end of try-catch
    }
    
    
    
    /**
     * <code>getValueInDeviceUnit</code> converts the value of a numeric property expressed in display unit to the value in device unit
     *
     * @param dispPval a property value expressed in display unit
     */
    public double getValueInDeviceUnit(double dispPval)
    {
 	double    dUnitFactor=1.0;
        
        dUnitFactor = getDisplayUnitFactor();
        if (dUnitFactor <= 0)
	    dUnitFactor = 1.0;
        
        double devPval = dispPval / dUnitFactor;
        return devPval;
    }
    
    
    
    /**
     * <code>getValueInDisplayUnit</code> converts the value of a numeric property expressed in device unit to the value in display unit
     *
     * @param devPval a property value expressed in device unit
     */
    public double getValueInDisplayUnit(double devPval)
    {
 	double    dUnitFactor=1.0;
        
        dUnitFactor = getDisplayUnitFactor();
        if (dUnitFactor <= 0)
	    dUnitFactor = 1.0;
        
        double dispPval = devPval * dUnitFactor;
        return dispPval;
    }

    
}
