// File:          ATKTransferHandler.java
// Created:       2002-09-12 11:58:33, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-09-13 16:29:54, erik>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.dnd;

import java.awt.dnd.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import fr.esrf.tangoatk.core.*;
import javax.swing.tree.*;

public class ATKTransferHandler extends TransferHandler {

    protected Transferable createTransferable(JComponent comp) {
	if (!(comp instanceof JTree)) return null;

	JTree tree = (JTree) comp;

	DefaultMutableTreeNode node = (DefaultMutableTreeNode)
	    tree.getLastSelectedPathComponent();
	
	if (node == null) return null;
	
	Object nodeInfo = node.getUserObject();

	if (!(nodeInfo instanceof ATKNode)) return null;

	ATKNode n = (ATKNode)nodeInfo;

	return n;
    }
			

    public int getSourceActions(JComponent c) {
	return COPY;
    }
}
