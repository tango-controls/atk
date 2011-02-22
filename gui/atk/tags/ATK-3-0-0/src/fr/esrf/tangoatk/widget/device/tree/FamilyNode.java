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
