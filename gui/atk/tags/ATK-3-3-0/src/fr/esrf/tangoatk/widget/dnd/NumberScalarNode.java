// File:          NumberScalarNode.java<2>
// Created:       2002-09-12 11:50:33, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-09-17 10:36:32, erik>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.dnd;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import fr.esrf.tangoatk.core.*;

public class NumberScalarNode extends AttributeNode {

    public NumberScalarNode(INumberScalar value) {
	super(value);
    }

    protected String[] getMimeTypes() {
	String types[] = super.getMimeTypes();
	String tmp[] = new String[types.length + 1];
	System.arraycopy(types, 0, tmp, 0, types.length);
	tmp[types.length] = NodeFactory.MIME_NUMBERSCALAR; 
	return tmp;
    }
}
