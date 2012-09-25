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
 
// File:          FamilyNode.java
// Created:       2002-09-17 12:38:56, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-10-02 11:20:27, erik>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.device.tree;
import fr.esrf.TangoApi.Database;
import java.util.*;

public class FamilyNode extends DomainNode {
    DomainNode parent;
    private List members = new Vector(); 
    private boolean filled = false;
    
    public FamilyNode(String name, Database db) {
	super(name, db);
    }
    
    public FamilyNode(DomainNode parent, String name, Database db) {
	this(name, db);
	parent.addChild(this);
	this.parent = parent;
    }


    public List getChildren() {
	return members;
    }

    public MemberNode getChild(String name) {
	for (int i = 0; i < members.size(); i++) {
	    MemberNode node = (MemberNode)members.get(i);
	    if (node.getName().equals(name))
		return node;
	}
	return null;
    }

    public boolean isFilled() {
	return filled;
    }

    public void setFilled(boolean b) {
	filled = b;
    }
    
    public String getName() {
	return parent.getName() + "/" + name;
    }
   
}
