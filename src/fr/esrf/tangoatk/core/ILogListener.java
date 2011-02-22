// File:          ILogListener.java
// Created:       2002-02-08 13:07:57, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-08 13:12:3, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import java.util.EventListener;

public interface ILogListener extends EventListener {

    public void append(String message);

    public void close();

}
