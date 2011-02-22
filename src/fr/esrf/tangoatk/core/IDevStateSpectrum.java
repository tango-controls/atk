// File:          IDevStateSpectrum.java
// Created:       2008-07-07 15:23:16, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;

public interface IDevStateSpectrum extends IAttribute
{    
    public static final String STATE_LABELS = "StateLabels";
    
    public void addDevStateSpectrumListener(IDevStateSpectrumListener l) ;
    
    public void removeDevStateSpectrumListener(IDevStateSpectrumListener l) ;

    public String[] getValue();
    
    public String[] getDeviceValue();
    
    public String[] getStateLabels();
    
    public boolean getInvertedOpenCloseForElement(int elemIndex);
    
    public boolean getInvertedInsertExtractForElement(int elemIndex);
    
    public void setValue(String[] states) throws AttributeSetException;
    
}
