// File:          DeviceException.java
// Created:       2001-12-14 16:57:45, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-27 11:42:29, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;
import fr.esrf.Tango.*;

public class DeviceException extends ATKException {

    public DeviceException(DevFailed e) {
	super(e);
    }

    public DeviceException(String s) {
	super(s);
    }

    public String getVersion() {
	return "$Id$";
    }

}
