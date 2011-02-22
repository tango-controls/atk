// File:          IRawImage.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <pons@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import fr.esrf.Tango.*;

public interface IRawImage extends IAttribute {

    public void addRawImageListener(IRawImageListener l) ;
    
    public void removeRawImageListener(IRawImageListener l) ;

    public byte[][] getValue() throws DevFailed;

    public void setValue(byte[][] d) throws AttributeSetException;

}
