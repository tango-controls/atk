// File:          ScalarListener.java
// Created:       2001-10-08 16:27:51, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-05-22 15:58:7, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.beans.*;

public interface ISpectrumListener extends IAttributeStateListener {

    public void spectrumChange(NumberSpectrumEvent e);
}
