// File:          AttributeNode.java
// Created:       2002-09-17 10:42:07, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-09-17 10:44:50, erik>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.dnd;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import fr.esrf.tangoatk.core.*;

public class AttributeNode extends EntityNode {
    
    public AttributeNode(IAttribute value) {
	super(value);
    }

    protected String[] getMimeTypes() {
	String types[] = super.getMimeTypes();
	String tmp[] = new String[types.length + 1];
	System.arraycopy(types, 0, tmp, 0, types.length);
	tmp[types.length] = NodeFactory.MIME_ATTRIBUTE; 
	return tmp;
    }

}
