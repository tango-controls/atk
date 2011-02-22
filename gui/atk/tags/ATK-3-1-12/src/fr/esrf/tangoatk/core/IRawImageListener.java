// File:          IRawImageListener.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <pons@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;


public interface IRawImageListener extends IAttributeStateListener
{

    public void rawImageChange(RawImageEvent e);

}
