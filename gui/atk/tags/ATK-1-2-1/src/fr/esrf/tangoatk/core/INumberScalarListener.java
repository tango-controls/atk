// File:          NumberListener.java
// Created:       2001-10-08 16:36:12, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-04-12 16:34:57, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import java.beans.*;

public interface INumberScalarListener extends IAttributeStateListener {

    public void numberScalarChange(NumberScalarEvent evt);

}
