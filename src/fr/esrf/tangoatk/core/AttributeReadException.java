// File:          ConnectionException.java
// Created:       2001-10-26 13:09:40, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-10 14:9:32, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import fr.esrf.Tango.*;

public class AttributeReadException extends ATKException {

    public AttributeReadException(DevFailed e) {
	super(e);
    }

    public AttributeReadException(String s) {
	super(s);
    }

    public AttributeReadException() {

    }
    
    public String getVersion() {
	return "$Id$";
    }

}
