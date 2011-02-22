// File:          IDevStateSpectrumListener.java
// Created:       2008-07-07 15:23:16, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.beans.*;

public interface IDevStateSpectrumListener extends IAttributeStateListener
{

    public void devStateSpectrumChange(DevStateSpectrumEvent e);

}
