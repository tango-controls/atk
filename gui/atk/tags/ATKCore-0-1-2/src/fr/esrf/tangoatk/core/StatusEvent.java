// File:          StatusChangeEvent.java
// Created:       2002-02-01 15:25:50, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-27 11:49:36, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import java.util.EventObject;

public class StatusEvent extends ATKEvent {
    String status;
    
    public StatusEvent(Device source, String status, long timeStamp) {
	super(source, timeStamp);
	setStatus(status);
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public void setSource(Device source) {
	this.source = source;
    }
    
    public String getVersion() {
	return "$Id$";
    }

}
