// File:          TableVoidCommand.java
// Created:       2002-06-24 15:24:14, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:16:32, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.command;

public class TableVoidCommand extends ACommand {

    public TableVoidCommand() {
	inputHelper = new TableCommandHelper(this);
	outputHelper = new VoidCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }

}
