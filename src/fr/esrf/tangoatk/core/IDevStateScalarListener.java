// File:          IDevStateScalarListener.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.beans.*;

public interface IDevStateScalarListener extends IAttributeStateListener
{

    public void devStateScalarChange(DevStateScalarEvent e);

}
