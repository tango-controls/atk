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
 
// File:          MemberNode.java
// Created:       2002-09-17 12:39:15, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-09-17 13:49:46, erik>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.device.tree;
import fr.esrf.TangoApi.Database;

import fr.esrf.tangoatk.core.*;


public class MemberNode extends FamilyNode {
    FamilyNode parent;
    AttributeList attributes;
    CommandList commands;
    IDevice device;
    
    public MemberNode(FamilyNode family, String name, Database db) {
	super(name, db);
	this.parent = family;
	parent.addChild(this);
    }

    public void setAttributeList(AttributeList list) {
	attributes = list;
    }

    public void setCommandList(CommandList list) {
	commands = list;
    }

    public void setDevice(IDevice device) {
	this.device = device;
    }

    public String getName() {
	return parent.getName() + "/" + name;
    }
}
