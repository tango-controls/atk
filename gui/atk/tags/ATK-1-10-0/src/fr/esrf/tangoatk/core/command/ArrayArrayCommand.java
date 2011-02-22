// File:          ArrayArrayCommand.java
// Created:       2002-06-24 14:51:00, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:16:32, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.command;


public class ArrayArrayCommand extends ACommand {

    public ArrayArrayCommand() {
	inputHelper = new ArrayCommandHelper(this);
	outputHelper = new ArrayCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }

}
