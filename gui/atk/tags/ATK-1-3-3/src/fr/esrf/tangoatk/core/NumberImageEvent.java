// File:          NumberImageEvent.java
// Created:       2002-01-31 17:50:00, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-27 11:45:44, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import java.util.EventObject;

public class NumberImageEvent extends ATKEvent {
    double [][]value;
    long timeStamp;
    public NumberImageEvent(INumberImage source, double[][] value,
			    long timeStamp) {
	super(source, timeStamp);
	setValue(value);
    }

    public double[][] getValue() {
	return value;
    }

    public void setValue(double [][]value) {
	this.value = value;
    }

    public void setSource(INumberImage source) {
	this.source = source;
    }

    public String getVersion() {
	return "$Id$";
    }
}
