// File:          BooleanScalarEvent.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.util.EventObject;

public class BooleanScalarEvent extends ATKEvent
{
    boolean      value;
    long         timeStamp;
    
    public BooleanScalarEvent(IBooleanScalar source, boolean  value, long timeStamp)
    {
	super(source, timeStamp);
	setValue(value);
    }

    public boolean  getValue()
    {
	return value;
    }

    public void setValue(boolean   value)
    {
	this.value = value;
    }

    public void setSource(IBooleanScalar source)
    {
	this.source = source;
    }

    public String getVersion()
    {
	return "$Id$";
    }
}
