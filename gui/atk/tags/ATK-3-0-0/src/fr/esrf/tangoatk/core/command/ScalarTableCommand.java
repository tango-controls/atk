// File:          ScalarTableCommand.java
// Created:       2002-07-08 18:12:58, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-08 18:13:21, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.command;


public class ScalarTableCommand extends ACommand {

    public ScalarTableCommand() {
	inputHelper = new ScalarCommandHelper(this);
	outputHelper = new TableCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }

}
