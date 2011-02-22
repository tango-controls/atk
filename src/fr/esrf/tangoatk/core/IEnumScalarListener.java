// File:          IEnumScalarListener.java
// Created:       2007-02-08 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.beans.*;

public interface IEnumScalarListener extends IAttributeStateListener
{

    public void enumScalarChange(EnumScalarEvent e);

}
