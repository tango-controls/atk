// File:          StringScalarNode.java
// Created:       2002-09-16 20:23:52, erik
// By:            <erik@skiinfo.fr>
// Time-stamp:    <2002-09-23 16:48:7, erik>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.dnd;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import fr.esrf.tangoatk.core.*;

public class StringScalarNode extends AttributeNode {

    public StringScalarNode(IStringScalar value) {
	super(value);
    }

    protected String[] getMimeTypes() {
	String types[] = super.getMimeTypes();
	String tmp[] = new String[types.length + 1];
	System.arraycopy(types, 0, tmp, 0, types.length);
	tmp[types.length] = NodeFactory.MIME_STRINGSCALAR; 
	return tmp;
    }
}
