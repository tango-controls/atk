// File:          EntityNode.java
// Created:       2002-09-17 10:42:45, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-09-17 10:44:24, erik>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.dnd;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import fr.esrf.tangoatk.core.*;

public class EntityNode extends Node {
    
    public EntityNode(IEntity value) {
	this.value = value;
    }

    protected String[] getMimeTypes() {
	String types[] = super.getMimeTypes();
	String tmp[] = new String[types.length + 1];
	System.arraycopy(types, 0, tmp, 0, types.length);
	tmp[types.length] = NodeFactory.MIME_ENTITY; 
	return tmp;
    }

}
