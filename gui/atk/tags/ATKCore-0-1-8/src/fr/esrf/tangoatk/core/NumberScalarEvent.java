// File:          NumberScalarEvent.java
// Created:       2002-01-31 17:40:42, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-27 11:46:1, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import java.util.EventObject;
public class NumberScalarEvent extends ATKEvent {
    double value;
    long timeStamp;
    INumberScalar _source;

    public NumberScalarEvent(Object o) {
	super(o, System.currentTimeMillis());
    }
    
    public NumberScalarEvent(INumberScalar source, double value,
			     long timeStamp) {
	super(source, timeStamp);
	setValue(value);
    }

    public double getValue() {
	return value;
    }

    public void setValue(double value) {
	this.value = value;
    }

    public void setSource(INumberScalar source) {
	this.source = source;
	_source = source;
    }

    public INumberScalar getNumberSource() {
	return _source;
    }

    public String getVersion() {
	return "$Id$";
    }
}
