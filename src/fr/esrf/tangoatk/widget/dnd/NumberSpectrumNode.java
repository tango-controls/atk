// File:          NumberSpectrumNode.java
// Created:       2002-09-12 12:07:01, erik
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

public class NumberSpectrumNode extends AttributeNode {
    
    public NumberSpectrumNode(INumberSpectrum value) {
	super(value);
    }


    protected String[] getMimeTypes() {
	String types[] = super.getMimeTypes();
	String tmp[] = new String[types.length + 1];
	System.arraycopy(types, 0, tmp, 0, types.length);
	tmp[types.length] = NodeFactory.MIME_NUMBERSPECTRUM; 
	return tmp;
    }
}
