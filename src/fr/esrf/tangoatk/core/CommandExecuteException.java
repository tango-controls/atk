// File:          ConnectionException.java
// Created:       2001-10-26 13:09:40, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-03-28 14:8:43, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import fr.esrf.Tango.*;

public class CommandExecuteException extends ATKException {

    public CommandExecuteException(DevFailed e) {
	super(e);
    }

    public CommandExecuteException(Exception e) {
	super(e.getMessage());
    }
    
    public CommandExecuteException(String s) {
	super(s);
    }

    public String getVersion() {
	return "$Id$";
    }
    
}
