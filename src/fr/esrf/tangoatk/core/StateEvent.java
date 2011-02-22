// File:          StateChangeEvent.java
// Created:       2002-02-01 15:23:46, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-27 11:49:15, assum>
// 
// $Id$
// 
// Description:       


package fr.esrf.tangoatk.core;

import java.util.EventObject;

public class StateEvent extends ATKEvent {
    String state;
    
    public StateEvent(Device source, String state, long timeStamp) {
	super(source, timeStamp);
	setState(state);
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public void setSource(Device source) {
	this.source = source;
    }
    
    public String getVersion() {
	return "$Id$";
    }
}
