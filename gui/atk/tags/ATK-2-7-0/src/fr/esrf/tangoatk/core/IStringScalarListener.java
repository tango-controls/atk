// File:          IStringScalarListener.java
// Created:       2002-03-21 13:25:17, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-03-21 13:28:34, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

public interface IStringScalarListener extends IAttributeStateListener {

    public void stringScalarChange(StringScalarEvent e);

}
