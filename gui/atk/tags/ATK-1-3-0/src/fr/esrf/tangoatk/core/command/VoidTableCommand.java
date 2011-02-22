// File:          VoidTableCommand.java
// Created:       2002-07-08 18:13:26, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-08 18:13:49, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.command;


public class VoidTableCommand extends ACommand {

    public VoidTableCommand() {
	inputHelper = new VoidCommandHelper(this);
	outputHelper = new TableCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }

}
