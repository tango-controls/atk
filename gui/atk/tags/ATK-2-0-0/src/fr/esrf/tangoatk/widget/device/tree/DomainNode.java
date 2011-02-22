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
