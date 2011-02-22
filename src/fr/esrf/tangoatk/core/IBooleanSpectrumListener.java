// File:          IBooleanSpectrumListener.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.beans.*;

public interface IBooleanSpectrumListener extends IAttributeStateListener
{

    public void booleanSpectrumChange(BooleanSpectrumEvent e);

}
