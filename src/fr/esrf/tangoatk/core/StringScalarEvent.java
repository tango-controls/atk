// File:          StringScalarEvent.java
// Created:       2002-03-21 13:26:42, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-03-21 14:11:46, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import java.util.EventObject;

public class StringScalarEvent extends ATKEvent {
    String value;
    long timeStamp;
    public StringScalarEvent(IStringScalar source, String value,
			 long timeStamp) {
	super(source, timeStamp);
	setValue(value);
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public void setSource(IStringScalar source) {
	this.source = source;
    }

    public String getVersion() {
	return "$Id$";
    }
}
