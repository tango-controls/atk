// File:          IBooleanScalar.java
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

public interface IBooleanScalar extends IAttribute
{

    public void addBooleanScalarListener(IBooleanScalarListener l) ;
    
    public void removeBooleanScalarListener(IBooleanScalarListener l) ;

    public boolean getValue();
    
    public boolean getDeviceValue();
    
    public boolean getSetPoint();

    public boolean getDeviceSetPoint();

    public void setValue(boolean  b);

}
