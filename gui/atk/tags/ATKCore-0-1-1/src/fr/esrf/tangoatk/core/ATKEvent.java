// File:          ATKEvent.java
// Created:       2002-02-06 16:23:28, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-27 11:37:36, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import java.util.EventObject;

public abstract class ATKEvent extends EventObject {
    long timeStamp;


    public ATKEvent(Object source, long timeStamp) {
	super(source);
	setTimeStamp(timeStamp);
    }
    
    public void setTimeStamp(long ms) {
	    timeStamp = ms;
    }

    public long getTimeStamp() {
	return timeStamp;
    }

    public String getVersion() {
	return "$Id$";
    }

}
