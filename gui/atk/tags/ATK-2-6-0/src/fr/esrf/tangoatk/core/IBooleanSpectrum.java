// File:          IBooleanSpectrum.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;

public interface IBooleanSpectrum extends IAttribute {

    public void addBooleanSpectrumListener(IBooleanSpectrumListener l) ;
    
    public void removeBooleanSpectrumListener(IBooleanSpectrumListener l) ;

    public boolean[] getValue() throws DevFailed;

    public void setValue(boolean[] b) throws AttributeSetException;

}
