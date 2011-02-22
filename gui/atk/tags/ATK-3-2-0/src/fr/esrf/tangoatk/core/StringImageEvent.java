// File:          StringImageEvent.java
// Created:       2007-05-03 10:46:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.util.EventObject;

public class StringImageEvent extends ATKEvent
{
    String[][]  value;
    long         timeStamp;
    
    public StringImageEvent(IStringImage source, String[][] value, long timeStamp)
    {
	super(source, timeStamp);
	setValue(value);
    }

    public String[][] getValue()
    {
	return value;
    }

    public void setValue(String[][] value)
    {
	this.value = value;
    }

    public void setSource(IStringImage source) {
	this.source = source;
    }

    public String getVersion() {
	return "$Id$";
    }
}
