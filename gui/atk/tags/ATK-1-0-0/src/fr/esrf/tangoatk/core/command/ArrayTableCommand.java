// File:          ArrayTableCommand.java
// Created:       2002-07-08 18:12:17, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-08 18:12:49, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.command;


public class ArrayTableCommand extends ACommand {

    public ArrayTableCommand() {
	inputHelper = new ArrayCommandHelper(this);
	outputHelper = new TableCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }

}
