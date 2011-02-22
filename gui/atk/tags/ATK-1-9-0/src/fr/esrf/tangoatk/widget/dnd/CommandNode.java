// File:          CommandNode.java
// Created:       2002-09-13 13:12:29, erik
// By:            <erik@skiinfo.fr>
// Time-stamp:    <2002-09-17 10:46:6, erik>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.dnd;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import fr.esrf.tangoatk.core.*;

public class CommandNode extends EntityNode {

    public CommandNode(ICommand value) {
	super(value);
    }

    protected String[] getMimeTypes() {
	String types[] = super.getMimeTypes();
	String tmp[] = new String[types.length + 1];
	System.arraycopy(types, 0, tmp, 0, types.length);
	tmp[types.length] = NodeFactory.MIME_COMMAND; 
	return tmp;
    }
}
