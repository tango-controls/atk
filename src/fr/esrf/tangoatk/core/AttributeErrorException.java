// File:          ConnectionException.java
// Created:       2001-10-26 13:09:40, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-27 11:38:9, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import fr.esrf.Tango.*;

class AttributeErrorException extends ATKException {

    public AttributeErrorException(DevFailed e) {
	super(e);
    }

    public AttributeErrorException(String s) {
	super(s);
    }

    public String getVersion() {
	return "$Id$";
    }
    
}
