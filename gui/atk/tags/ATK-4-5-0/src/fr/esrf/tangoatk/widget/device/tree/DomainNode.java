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
 
// File:          DomainNode.java
// Created:       2002-09-17 12:38:23, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-09-17 13:13:54, erik>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.device.tree;

import java.util.*;

import javax.swing.tree.*;
import fr.esrf.TangoApi.Database;

public class DomainNode extends DefaultMutableTreeNode {

    String name;
    Database db;
    private List families = new Vector();
    
    public DomainNode(String name, Database db) {
	this.name = name;
	this.db = db;
	
    }

    public String getName() {
	return name;
    }

    public void addChild(Object node) {
	getChildren().add(node);
    }

    public List getChildren() {
	return families;
    }

    public String toString() {
	return name;
    }
    
}
