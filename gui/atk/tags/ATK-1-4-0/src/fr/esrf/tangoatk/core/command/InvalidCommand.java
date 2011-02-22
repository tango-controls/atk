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
