// File:          ACommandHelper.java
// Created:       2001-12-21 13:36:53, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-24 18:12:42, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.command;

import fr.esrf.TangoDs.TangoConst;
import fr.esrf.TangoApi.DeviceData;
import fr.esrf.Tango.DevFailed;

public abstract class ACommandHelper
    implements TangoConst, java.io.Serializable {

    transient protected DeviceData data;
    protected ACommand command;

    public ACommandHelper(ACommand command) {
	this.command = command;
	try {
	    data = new DeviceData();	     
	} catch (DevFailed e) {
	    command.setError("Could not initialize DeviceData", e);
	} // end of try-catch
    }
    
    protected abstract DeviceData setInput(java.util.List input);

    protected abstract java.util.List extractOutput(DeviceData output);


    private static String VERSION = "$Id$";

    public String getVersion() {
	return VERSION;
    }

    protected void setError(String s, Throwable e) {
	command.setError(s, e);
    }
    
    protected int getInType() {
	return command.getInType();
    }

    protected int getOutType() {
	return command.getOutType();
    }
    
    protected void serializeInit() throws Exception {
	data = new DeviceData();
    };

}
