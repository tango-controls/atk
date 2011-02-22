// File:          NumberImageEvent.java
// Created:       2002-01-31 17:50:00, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-27 11:45:44, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;


import java.util.EventObject;

public class NumberImageEvent extends ATKEvent {
    double[][]  value;
    double[][]  devValue;
    
    
    public NumberImageEvent(INumberImage source, double[][] value, long timeStamp)
    {
	super(source, timeStamp);
	setValue(value);
	devValue = new double[1][1];
    }

    // Return the value in the display unit
    public double[][] getValue() {
	return value;
    }

    // Return the value in the device server unit
    public double[][] getDeviceValue()
    {
	double      dispUnitFactor = 1.0;
	
	
	if (source != null)
	   if (source instanceof INumberImage)
	      dispUnitFactor = ((INumberImage) source).getDisplayUnitFactor();
	      
	if (dispUnitFactor <= 0)
	   dispUnitFactor = 1.0;
	   
	   
	if (dispUnitFactor == 1.0)
	   return value;


	int ydim = value.length;
	int xdim = value[0].length;
	if (ydim != devValue.length || devValue.length == 0 || xdim != devValue[0].length)
	{
	    devValue = new double[ydim][xdim];
	}

	for (int i = 0; i < ydim; i++)
	  for (int j = 0; j < xdim; j++)
	  {
             devValue[i][j] = value[i][j] / dispUnitFactor; //return the value in the device server unit
	  }
	return devValue;
    }

    // Return the value in the standard unit
    public double[][] getStandardValue()
    {
	double[][]  devVal;
        double[][]  stdVal;
        double      stdUnitFactor = 1.0;
	
        devVal = getDeviceValue(); // First get the value in the device server unit
	
	if (source != null)
	   if (source instanceof INumberImage)
	      stdUnitFactor = ((INumberImage) source).getStandardUnitFactor();
	      
	if (stdUnitFactor <= 0)
	   stdUnitFactor = 1.0;
	   
	   
	if (stdUnitFactor == 1.0)
	   return devVal;


	int ydim = devVal.length;
	int xdim = devVal[0].length;
        stdVal = new double[ydim][xdim];

	for (int i = 0; i < ydim; i++)
	  for (int j = 0; j < xdim; j++)
	  {
             stdVal[i][j] = devVal[i][j] * stdUnitFactor; //return the value in the standard unit
	  }
	return stdVal;
    }


    public void setValue(double [][]value) {
	this.value = value;
    }

    public void setSource(INumberImage source) {
	this.source = source;
    }

    public String getVersion() {
	return "$Id$";
    }
}
