// File:          TableArrayCommand.java<2>
// Created:       2002-06-24 14:50:13, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:16:32, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.command;


public class TableArrayCommand extends ACommand {

    public TableArrayCommand() {
	inputHelper = new TableCommandHelper(this);
	outputHelper = new ArrayCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }

}
