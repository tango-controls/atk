// File:          IStateListener.java
// Created:       2001-12-05 11:15:07, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-04-22 17:48:13, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.util.EventListener;
public interface IStateListener extends EventListener, IErrorListener {
    public void stateChange(StateEvent e);

}
