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
 
// 
// $Id$
// 

package fr.esrf.tangoatk.widget.command;

import fr.esrf.tangoatk.core.ICommand;
public class CommandInputOutputFactory {

    private static CommandInputOutputFactory self;

    private CommandInputOutputFactory() {
    }

    public static CommandInputOutputFactory getInstance() {
	if (self == null) self = new CommandInputOutputFactory();

	return self;
    }


    public IInput getInputter4Command(ICommand command)
    {
    
        if (!command.takesInput())
	   return null;
	   
	if (command.takesScalarInput()) 
	    return new ScalarCommandInput(command);

	if (command.takesArrayInput())
	    return new ArrayCommandInput(command);

	if (command.takesTableInput())
	    return new TableCommandInput(command);

        return new ScalarCommandInput();
//	return new NoInput(command);
    }
}
