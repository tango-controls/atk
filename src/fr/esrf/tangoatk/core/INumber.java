// File:          INumber.java
// Created:       2001-11-26 10:14:10, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-01-31 15:50:29, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

public interface INumber extends IAttribute{

    public double getMaxValue();

    public double getMinValue();

    public double getMinAlarm();

    public double getMaxAlarm();

    public double getMinWarning();

    public double getMaxWarning();

    public double getDeltaT();

    public double getDeltaVal();

    public void setMaxValue(double d);

    public void setMinValue(double d);

    public void setMinAlarm(double d);

    public void setMaxAlarm(double d);

    public void setMinWarning(double d);

    public void setMaxWarning(double d);

    public void setDeltaT(double d);

    public void setDeltaVal(double d);

}
