// File:          TableTableCommand.java
// Created:       2002-07-08 18:10:07, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-08 18:11:18, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.command;


public class TableTableCommand extends ACommand {

    public TableTableCommand() {
	inputHelper = new TableCommandHelper(this);
	outputHelper = new TableCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }

}
