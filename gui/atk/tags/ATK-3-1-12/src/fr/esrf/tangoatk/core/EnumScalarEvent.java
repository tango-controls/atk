// File:          EnumScalarEvent.java
// Created:       2007-02-08 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.util.EventObject;

public class EnumScalarEvent extends ATKEvent
{
    String       value;
    long         timeStamp;
    
    public EnumScalarEvent(IEnumScalar source, String  value, long timeStamp)
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

    public void setSource(IEnumScalar source)
    {
	this.source = source;
    }

    public String getVersion()
    {
	return "$Id$";
    }
}
