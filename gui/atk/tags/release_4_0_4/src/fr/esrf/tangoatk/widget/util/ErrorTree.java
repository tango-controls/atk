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
 
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) space 
// Source File Name:   ErrorTree.java

package fr.esrf.tangoatk.widget.util;

import fr.esrf.Tango.DevError;
import fr.esrf.tangoatk.core.ATKException;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/** An error stack trace viewer (using a JTree). */
public class ErrorTree extends JPanel {

    class ErrorNode {

	public DevError getError() {
	    return error;
	}

	public String toString() {
	    return error.reason.trim();
	}

	DevError error;

	ErrorNode(DevError deverror) {
	    error = deverror;
	}
    }

    private void initComponents() {
	errorTree = new JTree(top);
	setLayout(new BorderLayout());
	add(errorTree);
    }

    void populateErrorNode(DefaultMutableTreeNode node, ErrorNode errornode)
    {
	DevError deverror = errornode.getError();
	node.add(new DefaultMutableTreeNode("Severity - " +
					    ATKException.severity[deverror.severity.value()]));
	node.add(new DefaultMutableTreeNode("Origin - " +
					    deverror.origin));
	node.add(new DefaultMutableTreeNode("Description - " +
					    deverror.desc));
	node.add(new DefaultMutableTreeNode("Reason - " +
					    deverror.reason));

    }

    public void addError(DevError deverror) {
	ErrorNode errornode = new ErrorNode(deverror);
	DefaultMutableTreeNode node = new DefaultMutableTreeNode(errornode);
	top.add(node);
	populateErrorNode(node, errornode);
    }

    public void addErrors(DevError adeverror[]) {
	remove(errorTree);
	top = new DefaultMutableTreeNode("Stack");
	initComponents();
	errorTree.setRootVisible(true);

	for (int i = 0; i < adeverror.length; i++) {
	    //System.out.println("Adding " + adeverror[i].desc);
	    addError(adeverror[i]);
	}

	for (int j = 0; j < errorTree.getRowCount(); j++)
	    errorTree.expandRow(j);

    }

    public ErrorTree() {
	top = new DefaultMutableTreeNode("Stack");
	initComponents();
	errorTree.setRootVisible(false);
    }

    DefaultMutableTreeNode top;
    private JTree errorTree;
}
