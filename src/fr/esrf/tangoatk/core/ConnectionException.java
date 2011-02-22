// File:          ConnectionException.java
// Created:       2001-10-26 13:09:40, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-03-01 15:9:17, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import fr.esrf.Tango.*;

public class ConnectionException extends ATKException {

    public ConnectionException(DevFailed e) {
	super(e);
    }

    public ConnectionException(Exception e) {
	super(e.toString());
    }
    
    public ConnectionException(String s) {
	super(s);
    }

    public String getVersion() {
	return "$Id$";
    }

}
