// File:          VoidCommandHelper.java
// Created:       2001-12-21 14:03:23, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-24 18:14:56, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.command;

import fr.esrf.TangoApi.DeviceData;

class VoidCommandHelper extends ACommandHelper {

    VoidCommandHelper(ACommand command) {
	super(command);
    }

    protected DeviceData setInput(java.util.List l) {
	return setInput();
    }

    protected DeviceData setInput() {
	data.insert();
	return data;
    }

    protected java.util.List extractOutput(DeviceData d) {
	return null;
    }

    public String getVersion() {
	return "$Id$";
    }

    private void readObject(java.io.ObjectInputStream in)
	throws java.io.IOException, ClassNotFoundException {
	in.defaultReadObject();
	try {
	    serializeInit();
	} catch (Exception e) {
	    throw new java.io.IOException(e.getMessage());
	}
    }

}
