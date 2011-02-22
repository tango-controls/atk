// File:          NumberArrayAttribute.java
// Created:       2001-10-10 10:41:58, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-05-22 15:31:14, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import org.apache.log4j.Logger;
import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;

public interface INumberSpectrum extends INumberImage {

    public void addSpectrumListener(ISpectrumListener l) ;

    public void removeSpectrumListener(ISpectrumListener l);

    public double[] getSpectrumValue();

    public double[] getStandardSpectrumValue();
    
    public void setValue(double[] d) throws AttributeSetException;

}
