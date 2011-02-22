// File:          IAttributeStateListener.java
// Created:       2002-02-04 16:44:36, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-03-13 11:20:43, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import java.util.EventListener;

public interface IAttributeStateListener extends IErrorListener
{

    public void stateChange(AttributeStateEvent e);

}
