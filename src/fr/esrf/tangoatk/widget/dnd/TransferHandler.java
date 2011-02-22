// File:          TransferHandler.java
// Created:       2002-09-12 11:58:33, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-10-18 15:5:1, erik>
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

public class TransferHandler extends javax.swing.TransferHandler {

    protected Transferable createTransferable(JComponent comp) {
	if (!(comp instanceof JTree)) return null;
	
	JTree tree = (JTree) comp;

	DefaultMutableTreeNode node = (DefaultMutableTreeNode)
	    tree.getLastSelectedPathComponent();
	
	if (node == null) return null;
	
	Object nodeInfo = node.getUserObject();
	if (!(nodeInfo instanceof Node)) return null;

	Node n = (Node)nodeInfo;

	return n;
    }

    public int getSourceActions(JComponent c) {
	return COPY;
    }
}
