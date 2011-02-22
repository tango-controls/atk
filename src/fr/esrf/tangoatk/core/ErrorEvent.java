// File:          SetErrorEvent.java
// Created:       2002-12-18 17:00:00, poncet
// By:            <poncet@esrf.fr>
// Time-stamp:    <2002-12-18 17:00:00, poncet>
// 
// $Id$
// 
// Description:       


package fr.esrf.tangoatk.core;

import java.util.EventObject;

public class ErrorEvent extends ATKEvent {
    Throwable error;
    
    public ErrorEvent(Object source, Throwable error, long timeStamp)
    {
	super(source, timeStamp);
	setError(error);
    }

    public Throwable getError()
    {
	return error;
    }

    public void setError(Throwable error)
    {
	this.error = error;
    }

    public void setSource(Object source)
    {
	this.source = source;
    }
    
    public String getVersion()
    {
	return "$Id$";
    }

    public Object clone()
    {
	return new ErrorEvent(source, error, timeStamp);
    }
}
