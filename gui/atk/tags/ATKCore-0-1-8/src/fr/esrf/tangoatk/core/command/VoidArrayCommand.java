// File:          VoidArrayCommand.java
// Created:       2002-01-22 13:46:05, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-24 18:16:32, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.command;

public class VoidArrayCommand extends ACommand {

    public VoidArrayCommand() {
	inputHelper = new VoidCommandHelper(this);
	outputHelper = new ArrayCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }
}
