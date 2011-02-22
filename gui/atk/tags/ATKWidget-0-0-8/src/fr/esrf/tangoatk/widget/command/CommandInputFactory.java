// File:          CommandInputFactory.java
// Created:       2002-06-03 16:38:46, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-03 17:21:51, assum>
// 
// $Id$
// 
// Description:       


package fr.esrf.tangoatk.widget.command;

import fr.esrf.tangoatk.core.ICommand;
public class CommandInputFactory {

    private CommandInputFactory self;

    private CommandInputFactory() {
    }

    public CommandInputFactory getInstance() {
	if (self == null) self = new CommandInputFactory();

	return self;
    }


    public IInput getInputter4Command(ICommand command) {
	if (command.takesScalarInput()) 
	    return new ScalarCommandInput(command);

	if (command.takesArrayInput()) {
	    return new ArrayInput(command);
	}

	return new NoInput(command);
    }
}
