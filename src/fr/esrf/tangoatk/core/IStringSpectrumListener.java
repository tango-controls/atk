// File:          IStringSpectrumListener.java
// Created:       2003-12-11 18:00:00, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.beans.*;

public interface IStringSpectrumListener extends IAttributeStateListener
{
    public void stringSpectrumChange(StringSpectrumEvent e);
}
