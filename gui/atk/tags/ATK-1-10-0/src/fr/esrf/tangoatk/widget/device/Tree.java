// File:          Tree.java
// Created:       2002-09-17 11:53:27, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-11-26 14:57:59, erik>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.device;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import fr.esrf.tangoatk.widget.device.tree.*;
import fr.esrf.tangoatk.widget.dnd.*;
import fr.esrf.tangoatk.core.*;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.Database;

public class Tree extends JTree {
    DomainNode[] domains;
    EventSupport propChanges;
    
    DefaultMutableTreeNode top;

    public Tree() {
	initComponents();

    }

    public void refresh() {
	top.removeAllChildren();
	importFromDb();
    }

    protected void error(Exception e) {
	propChanges.fireReadErrorEvent(this, e);
    }

    public void importFromDb() {
	try {
	    propChanges.fireStatusEvent(this, "Importing from database...");
	    Database db = new Database();
	    String [] dms = db.get_device_domain("*");
	    addDomains(top, db, dms);
	} catch (DevFailed e) {
	    error(new ATKException(e));
	}
	propChanges.fireStatusEvent(this, "Importing from database...Done");
	expandRow(0);
    }

    public void addErrorListener(IErrorListener l) {
	propChanges.addErrorListener(l);
    }

    public void removeErrorListener(IErrorListener l) {
	propChanges.removeErrorListener(l);
    }

    public void addStatusListener(IStatusListener l) {
	propChanges.addStatusListener(l);
    }

    public void removeStatusListener(IStatusListener l) {
	propChanges.removeStatusListener(l);
    }

    protected void initComponents() {
	propChanges = new EventSupport();
	
	top = new DefaultMutableTreeNode("Devices");
	setTransferHandler(new fr.esrf.tangoatk.widget.dnd.TransferHandler());
	setCellRenderer(new DeviceTreeCellRenderer(getCellRenderer()));	
	addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
		    if (!e.isPopupTrigger())
			return;

		    if (getPathForLocation(e.getX(),e.getY()) != null) {
			clearSelection();
			setSelectionPath(getPathForLocation
					 (e.getX(),e.getY() ));
		    }
		}
	    });
	((DefaultTreeModel)getModel()).setRoot(top);
    }

    protected void addDomains(DefaultMutableTreeNode top, Database db,
			    String[] dms) throws DevFailed {

	domains = new DomainNode[dms.length];

	for (int i = 0; i < dms.length; i++) {
	    String name = dms[i];
	    domains[i] = new DomainNode(name, db);
	    addFamilies(domains[i], db, db.get_device_family(name + "/*"));

	    top.add(domains[i]);
	} // end of for ()
    }

    protected void addFamilies(DomainNode top, Database db, String [] fms)
	throws DevFailed {

	for (int i = 0; i < fms.length; i++) {
	    String name = fms[i];

	    String []members = db.get_device_member(top.getName() + "/" + 
						     name + "/*");

	    FamilyNode family = new FamilyNode(top, name, db);

	    initialAddMembers(family, db, members);
	    top.add(family);
	} // end of for ()
    }

    protected void initialAddMembers(FamilyNode top, Database db,
				   String [] members) throws DevFailed {

	DefaultMutableTreeNode memberNode = null;
	for (int i = 0; i < members.length; i++) {
	    String m = members[i];

	    MemberNode member = new MemberNode(top, m, db);
	    top.add(member);
	} // end of for ()
    }


    protected void addAttributes(DefaultMutableTreeNode top,
				 AttributeList attributes) {
	NodeFactory nodeFactory = NodeFactory.getInstance();
	IAttribute attribute;
	DefaultMutableTreeNode node;
	for (int j = 0; j < attributes.size(); j++) {
	    attribute = (IAttribute)attributes.get(j);
	    node = new DefaultMutableTreeNode
		(nodeFactory.getNode4Entity(attribute));
	    top.add(node);
	} // end of for ()
    }

    protected void addCommands(DefaultMutableTreeNode top,
			     CommandList commands) {
	NodeFactory nodeFactory = NodeFactory.getInstance();
	ICommand command;
	DefaultMutableTreeNode node;
	for (int j = 0; j < commands.size(); j++) {
	    command = (ICommand)commands.get(j);
	    node = new DefaultMutableTreeNode
		(nodeFactory.getNode4Entity(command));
	    top.add(node);
	} // end of for ()
    }

    protected void addMembers(FamilyNode family) {
	if (family.isFilled()) return;
	propChanges.fireStatusEvent(this, "Importing devices on " + family + "...");
	family.setFilled(true);
	List  members = family.getChildren();
	List devices = new Vector();
	DeviceFactory factory = DeviceFactory.getInstance();

	for (int i = 0; i < members.size(); i++) {

	    String fqName = ((MemberNode)members.get(i)).getName();
	    try {
		devices.add(factory.getDevice(fqName));
	    } catch (Exception e) {
		family.setFilled(false);
		error(new ConnectionException(e));
	    } // end of try-catch
	} // end of for ()
	addDevices(family, devices);
	propChanges.fireStatusEvent(this, "Importing devices on " + family + "..." +
				    "done");
    }	

    protected void addDevices(FamilyNode family,
			    java.util.List devices) {
        MemberNode device = null;
	DefaultMutableTreeNode attributes = null;
	DefaultMutableTreeNode commands = null;
	IDevice d;
	
	for (int i = 0; i < devices.size(); i++) {
	    AttributeList al = new AttributeList();
	    CommandList cl = new CommandList();
	
	    d = (IDevice)devices.get(i);

	    device = family.getChild(d.getName());
	    device.setAttributeList(al);
	    device.setCommandList(cl);
	    device.setDevice(d);

	    IAttribute a;
	    try {
		al.add(d.getName() + "/*");
	    } catch (ATKException e) {
		error(e);
	    } 

	    if (al.size() > 0) {
		attributes = new DefaultMutableTreeNode("Attributes");

		device.add(attributes);
		addAttributes(attributes, al);
	    }
	    
	    ICommand c;
	    try {
		cl.add(d.getName() + "/*");
	    } catch (ConnectionException e) {
		error(e);
	    } // end of try-catch
	    if (cl.size() > 0) {
		commands = new DefaultMutableTreeNode("commands");
		device.add(commands);
		addCommands(commands, cl);
	    }
	} 
    }

    public void setShowEntities(boolean b) {
	if (!b) return;
	addTreeWillExpandListener(new TreeWillExpandListener() {

 		public void treeWillCollapse(TreeExpansionEvent event)
 		    throws ExpandVetoException {
 		}

 		public void treeWillExpand(TreeExpansionEvent event)
 		    throws ExpandVetoException {
 		    TreePath tp = event.getPath();
 		    Object[] path = tp.getPath();

 		    if (path.length == 3) {
 			DefaultMutableTreeNode node =
			    (DefaultMutableTreeNode)path[2];
 			if (!(node instanceof FamilyNode)) 
 			    return;
			addMembers((FamilyNode)node);
 		    }
		}
	    });
    }
    
    public static void main(String [] args) {
	JFrame frame = new JFrame();
	Tree tree = new Tree();
	tree.setShowEntities(true);
	frame.setContentPane(tree);
	frame.pack();
	frame.show();

    }
	
}
