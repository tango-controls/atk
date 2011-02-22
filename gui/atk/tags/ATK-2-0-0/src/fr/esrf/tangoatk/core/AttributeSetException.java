// File:          ConnectionException.java
// Created:       2001-10-26 13:09:40, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-27 11:39:30, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import fr.esrf.Tango.*;

public class AttributeSetException extends ATKException {

    public AttributeSetException(DevFailed e) {
	super(e);
    }

    public AttributeSetException(String s) {
	super(s);
    }

    public String getVersion() {
	return "$Id$";
    }
    
}
