/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
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
