// File:          ISetErrorListener.java
// Created:       2002-12-18 17:00:00, poncet
// By:            <poncet@esrf.fr>
// Time-stamp:    <2002-12-18 17:00:00, poncet>
// 
// $Id$
// 
// Description:

package fr.esrf.tangoatk.core;

import java.util.EventListener;

public interface ISetErrorListener extends EventListener, java.io.Serializable {

    public void setErrorOccured(ErrorEvent evt);

}
