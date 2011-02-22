// File:          ScalarVoidCommand.java
// Created:       2001-12-21 14:34:53, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-24 18:16:32, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.command;

public class ScalarVoidCommand extends ACommand {

    public ScalarVoidCommand() {
	inputHelper = new ScalarCommandHelper(this);
	outputHelper = new VoidCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }

}


