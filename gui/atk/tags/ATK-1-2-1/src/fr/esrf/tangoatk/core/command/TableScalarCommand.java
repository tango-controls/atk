// File:          TableAcalarCommand.java
// Created:       2002-06-24 15:22:15, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:16:9, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.command;

public class TableScalarCommand extends ACommand {

    public TableScalarCommand() {
	inputHelper = new TableCommandHelper(this);
	outputHelper = new ScalarCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }

}
