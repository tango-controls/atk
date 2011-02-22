// File:          ScalarArrayCommand.java
// Created:       2002-01-22 13:00:59, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:16:32, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.command;

public class ScalarArrayCommand extends ACommand {

    public ScalarArrayCommand() {
	inputHelper = new ScalarCommandHelper(this);
	outputHelper = new ArrayCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }
}
