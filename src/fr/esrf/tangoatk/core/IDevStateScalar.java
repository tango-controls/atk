// File:          IDevStateScalar.java
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

public interface IDevStateScalar extends IAttribute
{

    public void addDevStateScalarListener(IDevStateScalarListener l) ;
    
    public void removeDevStateScalarListener(IDevStateScalarListener l) ;

    public String getValue() throws DevFailed;
    
    public String getDeviceValue();
}
