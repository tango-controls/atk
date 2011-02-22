// File:          ArrayScalarCommand.java
// Created:       2002-06-24 15:23:34, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:18:57, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.command;


public class ArrayScalarCommand extends ACommand {

    public ArrayScalarCommand() {
	inputHelper = new ArrayCommandHelper(this);
	outputHelper = new ScalarCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }

}
