// File:          IStatusListener.java
// Created:       2002-02-01 15:22:08, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-01 15:42:46, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;
import java.util.EventListener;

public interface IStatusListener extends EventListener {

    public void statusChange(StatusEvent e);
}
