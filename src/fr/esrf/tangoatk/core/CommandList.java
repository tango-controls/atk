// File:          CommandList.java
// Created:       2001-09-28 10:46:00, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-01 16:53:5, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.util.*;
import fr.esrf.tangoatk.core.command.CommandFactory;

/**
 * The CommandList keeps all the commands we're working with.
 * Most of the functionality is handled by its superclass
 * {@link AEntityList}
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version 1.0
 */
public class CommandList extends AEntityList {

    /**
     * Creates a new <code>CommandList</code> instance, and 
     * instanciates its command factory.
     */
    public CommandList() {
	factory = CommandFactory.getInstance();
    }

    public String getVersion() {
	return "$Id$";
    }

}
