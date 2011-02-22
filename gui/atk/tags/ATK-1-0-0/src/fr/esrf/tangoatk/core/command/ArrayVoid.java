// File:          ArrayVoid.java
// Created:       2002-06-24 15:24:41, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 15:24:58, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.command;

import fr.esrf.tangoatk.core.ACommand;

public class ArrayVoidCommand extends ACommand {

    public ArrayVoidCommand() {
	inputHelper = new ArrayCommandHelper(this);
	outputHelper = new VoidCommandHelper(this);
    }

    public String getVersion() {
	return "$Id$";
    }

}
