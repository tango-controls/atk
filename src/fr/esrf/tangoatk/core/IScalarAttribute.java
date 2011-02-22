// File:          IScalarAttribute.java
// Created:       2002-01-24 13:05:36, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-05-23 11:28:6, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

public interface IScalarAttribute extends IAttribute {

    public IScalarAttribute getWritableAttribute();

    public IScalarAttribute getReadableAttribute();
}
