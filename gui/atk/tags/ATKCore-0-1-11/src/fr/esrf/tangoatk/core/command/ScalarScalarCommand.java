// File:          ScalarScalarCommand.java
// Created:       2001-09-28 08:30:44, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-24 18:15:3, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.command;

public class ScalarScalarCommand extends ACommand {

    public ScalarScalarCommand() {
	inputHelper = new ScalarCommandHelper(this);
	outputHelper = new ScalarCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }

}
