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
 
// File:          DeviceTreeCellRenderer.java<2>
// Created:       2002-09-18 16:34:33, erik
// By:            <erik@skiinfo.fr>
// Time-stamp:    <2002-09-18 17:30:42, erik>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.device.tree;

import javax.swing.tree.*;
import javax.swing.*;
import java.awt.*;
import fr.esrf.tangoatk.widget.dnd.*;

public class DeviceTreeCellRenderer implements TreeCellRenderer {
    TreeCellRenderer defaultRenderer;

    public DeviceTreeCellRenderer() {


    }

    public DeviceTreeCellRenderer(TreeCellRenderer defaultRenderer) {
	this.defaultRenderer = defaultRenderer;
    }

    public Component getTreeCellRendererComponent(JTree tree,
						  Object value,
						  boolean sel,
						  boolean expanded,
						  boolean leaf,
						  int row,
						  boolean hasFocus) {

	defaultRenderer.getTreeCellRendererComponent(tree, value, sel,
						     expanded, leaf, row,
						     hasFocus);
		
	Object o = ((DefaultMutableTreeNode)value).getUserObject();

	if (o == null || !(o instanceof Node)) 
	    return (java.awt.Component)defaultRenderer;
	Node node = (Node)o;
	
	if (node instanceof NumberScalarNode) {
	    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	    renderer.getTreeCellRendererComponent(tree, value, sel,
						  expanded, leaf, row,
						  hasFocus);
	    renderer.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/icons/numberscalar.gif")));
	    return renderer;
	}

	if (node instanceof StringScalarNode) {
	    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	    renderer.getTreeCellRendererComponent(tree, value, sel,
						  expanded, leaf, row,
						  hasFocus);
	    renderer.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/icons/stringscalar.gif")));
	    return renderer;
	}


	if (node instanceof NumberSpectrumNode) {
	    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	    renderer.getTreeCellRendererComponent(tree, value, sel,
						  expanded, leaf, row,
						  hasFocus);
	    renderer.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/icons/numberspectrum.gif")));
	    return renderer;
	}

	if (node instanceof NumberImageNode) {
	    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	    renderer.getTreeCellRendererComponent(tree, value, sel,
						  expanded, leaf, row,
						  hasFocus);
	    renderer.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/icons/numberimage.gif")));
	    return renderer;
	}

	return (java.awt.Component)defaultRenderer;
    }
}
		

