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
 
// File:          InvalidCommand.java
// Created:       2002-02-21 20:29:01, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:17:16, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.command;
import fr.esrf.tangoatk.core.CommandExecuteException;
import java.util.List;

public class InvalidCommand extends ACommand {

    public InvalidCommand() {
    }

    public void execute(List l) {
	cmdError("execution of commands which take arrayinput is " +
		 "not supported",
		 new CommandExecuteException(new IllegalArgumentException()));
    }
    
    public String getVersion() {
	return "$Id$";
    }
    
}
