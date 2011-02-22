// File:          NumberSpectrumEvent.java
// Created:       2002-01-31 17:49:20, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-27 11:46:21, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import java.util.EventObject;

public class NumberSpectrumEvent extends ATKEvent {
    double []value;

    public NumberSpectrumEvent(INumberSpectrum source, double[] value,
			       long timeStamp) {
	super(source, timeStamp);
	setValue(value);
    }

    public double[] getValue() {
	return value;
    }

    public void setValue(double []value) {
	this.value = value;
    }

    public void setSource(INumberSpectrum source) {
	this.source = source;
    }

    
    public String getVersion() {
	return "$Id$";
    }
}
