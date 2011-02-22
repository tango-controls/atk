// File:          ShortSpectrum.java
// Created:       2001-10-10 13:50:57, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-05-22 15:31:41, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;

public interface INumberImage extends IAttribute {

    public void addImageListener(IImageListener l) ;
    
    public void removeImageListener(IImageListener l) ;

    public double[][] getValue() throws DevFailed;

    public void setValue(double [][] d) throws AttributeSetException;

    public double getMaxValue();

    public double getMinValue();

    public double getMinAlarm();

    public double getMaxAlarm();

    public void setMaxValue(double d);

    public void setMinValue(double d);

    public void setMinAlarm(double d);

    public void setMaxAlarm(double d);


}
