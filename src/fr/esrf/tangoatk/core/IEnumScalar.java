// File:          IEnumScalar.java
// Created:       2007-02-08 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;

public interface IEnumScalar extends IAttribute
{

    public void addEnumScalarListener(IEnumScalarListener l) ;
    
    public void removeEnumScalarListener(IEnumScalarListener l) ;

    public String getEnumScalarValue();
        
    public String getEnumScalarSetPoint();
        
    public String getEnumScalarSetPointFromDevice();

    public void setEnumScalarValue(String s);
  
    public String[] getEnumValues();

}
