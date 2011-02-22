// File:          StringSpectrumEvent.java
// Created:       2003-12-15 11:02:42, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:  

     
package fr.esrf.tangoatk.core;

import java.util.EventObject;

public class StringSpectrumEvent extends ATKEvent
{
    String[] value;
    long timeStamp;
    
    public StringSpectrumEvent(IStringSpectrum source, String[] value, long timeStamp)
    {
	super(source, timeStamp);
	setValue(value);
    }

    public String[] getValue()
    {
	return value;
    }

    public void setValue(String[] value)
    {
	this.value = value;
    }

    public void setSource(IStringSpectrum source)
    {
	this.source = source;
    }

    public String getVersion()
    {
	return "$Id$";
    }
}
