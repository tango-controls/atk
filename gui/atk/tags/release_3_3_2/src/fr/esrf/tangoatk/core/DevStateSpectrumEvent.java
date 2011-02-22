// File:          DevStateSpectrumEvent.java
// Created:       2008-07-07 15:23:16, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;


public class DevStateSpectrumEvent extends ATKEvent
{
    String[]     value;
    long         timeStamp;
    
    public DevStateSpectrumEvent(IDevStateSpectrum source, String[]  value, long timeStamp)
    {
	super(source, timeStamp);
	setValue(value);
    }

    public String[]  getValue()
    {
	return value;
    }

    public void setValue(String[]   value)
    {
	this.value = value;
    }

    public void setSource(IDevStateSpectrum source)
    {
	this.source = source;
    }

    public String getVersion()
    {
	return "$Id$";
    }
}
