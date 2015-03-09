/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
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
	    command.cmdError("Could not initialize DeviceData", e);
	} // end of try-catch
    }
    
    protected abstract DeviceData setInput(java.util.List input);

    protected abstract java.util.List extractOutput(DeviceData output);


    private static String VERSION = "$Id$";

    public String getVersion() {
	return VERSION;
    }

    protected void cmdError(String s, Throwable e) {
	command.cmdError(s, e);
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
