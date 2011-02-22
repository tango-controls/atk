// File:          AttributeStateEvent.java
// Created:       2002-02-04 16:43:06, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-02-27 11:39:39, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import java.util.EventObject;

public class AttributeStateEvent extends ATKEvent {
    String state;
    
    public AttributeStateEvent(IAttribute source, String state,
			       long timeStamp) {
	super(source, timeStamp);
	setState(state);
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public void setSource(IAttribute source) {
	this.source = source;
    }

    public String getVersion() {
	return "$Id$";
    }
    
}
