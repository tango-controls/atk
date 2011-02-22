// File:          NumberAttribute.java
// Created:       2001-10-08 16:35:01, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 17:0:4, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

public interface INumberScalar extends IScalarAttribute, INumberSpectrum, INumber {

    public void addNumberScalarListener(INumberScalarListener l);

    public void removeNumberScalarListener(INumberScalarListener l);

    public double getNumberScalarValue();

    public double getStandardNumberScalarValue();
    
    public double getNumberScalarSetpoint();

    public double getStandardNumberScalarSetpoint();

    public void setValue (double d);

    public double getMaxValue();

    public double getMinValue();

    public double getMinAlarm();

    public double getMaxAlarm();

    public void setMaxValue(double d);

    public void setMinValue(double d);

    public void setMinAlarm(double d);

    public void setMaxAlarm(double d);

}


