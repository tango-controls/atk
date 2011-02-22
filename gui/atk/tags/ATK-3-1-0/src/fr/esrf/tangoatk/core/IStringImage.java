// File:          IStringImage.java
// Created:       2007-05-03 10:46:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;

public interface IStringImage extends IAttribute
{

    public void addStringImageListener(IStringImageListener l) ;
    
    public void removeStringImageListener(IStringImageListener l) ;

    public String[][] getValue();

    public void setValue(String[][] si);

}
