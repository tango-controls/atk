// File:          IStringSpectrum.java
// Created:       2003-12-11 18:00:00, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;

public interface IStringSpectrum extends IAttribute {

    public void addListener(IStringSpectrumListener l) ;

    public void removeListener(IStringSpectrumListener l);

    public String[] getStringSpectrumValue();
    
    public void setStringSpectrumValue(String[] strSpect);

}
