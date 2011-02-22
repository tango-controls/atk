// File:          VoidVoidCommand.java
// Created:       2001-12-21 14:18:42, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-24 18:15:14, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.command;

public class VoidVoidCommand extends ACommand {

    public VoidVoidCommand() {
	inputHelper = new VoidCommandHelper(this);
	outputHelper = new VoidCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }

}
