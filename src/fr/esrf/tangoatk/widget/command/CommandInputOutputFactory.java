// File:          CommandInputOutputFactory.java
// Created:       2002-06-03 16:38:46, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-03 17:21:51, assum>
// 
// $Id$
// 
// Description:       
// Renamed from CommandInputFactory to CommandInputOutputFactory
// By F. Poncet on 10 july 2002

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
