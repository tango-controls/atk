// File:          VoidScalarCommand.java
// Created:       2001-12-21 14:33:03, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-24 18:16:32, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.command;

public class VoidScalarCommand extends ACommand {

    public VoidScalarCommand() {
	inputHelper = new VoidCommandHelper(this);
	outputHelper = new ScalarCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }
}
