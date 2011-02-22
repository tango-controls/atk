// File:          IStringImageListener.java
// Created:       2007-05-03 10:46:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.beans.*;

public interface IStringImageListener extends IAttributeStateListener
{

    public void stringImageChange(StringImageEvent e);

}
