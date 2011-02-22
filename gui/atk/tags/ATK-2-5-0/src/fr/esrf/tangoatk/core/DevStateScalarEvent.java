// File:          DevStateScalarEvent.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.util.EventObject;

public class DevStateScalarEvent extends ATKEvent
{
    String       value;
    long         timeStamp;
    
    public DevStateScalarEvent(IDevStateScalar source, String  value, long timeStamp)
    {
	super(source, timeStamp);
	setValue(value);
    }

    public String  getValue()
    {
	return value;
    }

    public void setValue(String   value)
    {
	this.value = value;
    }

    public void setSource(IDevStateScalar source)
    {
	this.source = source;
    }

    public String getVersion()
    {
	return "$Id$";
    }
}
