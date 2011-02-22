// File:          ErrorListener.java
// Created:       2001-09-25 09:43:57, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-04-05 10:55:14, assum>
// 
// $Id$
// 
// Description:

package fr.esrf.tangoatk.core;

import java.util.EventListener;

public interface IErrorListener extends EventListener, java.io.Serializable {

    public void errorChange(ErrorEvent evt);

}
