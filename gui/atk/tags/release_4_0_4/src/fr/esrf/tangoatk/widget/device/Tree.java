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
 
// File:          Tree.java
// Created:       2002-09-17 11:53:27, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-11-26 14:57:59, erik>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.device;

import java.awt.Frame;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.Database;
import fr.esrf.tangoatk.core.ATKException;
import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.CommandList;
import fr.esrf.tangoatk.core.ConnectionException;
import fr.esrf.tangoatk.core.DeviceFactory;
import fr.esrf.tangoatk.core.EventSupport;
import fr.esrf.tangoatk.core.IAttribute;
import fr.esrf.tangoatk.core.ICommand;
import fr.esrf.tangoatk.core.IDevice;
import fr.esrf.tangoatk.core.IErrorListener;
import fr.esrf.tangoatk.core.IStatusListener;
import fr.esrf.tangoatk.widget.device.tree.DeviceTreeCellRenderer;
import fr.esrf.tangoatk.widget.device.tree.DomainNode;
import fr.esrf.tangoatk.widget.device.tree.FamilyNode;
import fr.esrf.tangoatk.widget.device.tree.MemberNode;
import fr.esrf.tangoatk.widget.dnd.NodeFactory;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;

public class Tree extends JTree {
    DomainNode[] domains;
    EventSupport propChanges;
    
    DefaultMutableTreeNode top;
    

    public Tree() {
	initComponents();

    }

    /*
     * Not really efficient. You'd better re initialize the tree
     * and its listeners
     */
    public void refresh() {
	top.removeAllChildren();
	importFromDb();
    }
    
    public synchronized void removeListeners(){
        propChanges.removeAtkEventListeners();

        ComponentListener[] CL = getComponentListeners();
        for (int i=0; i<CL.length; i++){
            removeComponentListener(CL[i]);
        }
        
        FocusListener[] FL = getFocusListeners();
        for (int i=0; i<FL.length; i++){
            removeFocusListener(FL[i]);
        }
        
        HierarchyBoundsListener[] HBL = getHierarchyBoundsListeners();
        for (int i=0; i<HBL.length; i++){
            removeHierarchyBoundsListener(HBL[i]);
        }
        
        HierarchyListener[] HL = getHierarchyListeners();
        for (int i=0; i<HL.length; i++){
            removeHierarchyListener(HL[i]);
        }
        
        InputMethodListener[] IML = getInputMethodListeners();
        for (int i=0; i<IML.length; i++){
            removeInputMethodListener(IML[i]);
        }
        
        KeyListener[] KL = getKeyListeners();
        for (int i=0; i<KL.length; i++){
            removeKeyListener(KL[i]);
        }
        
        MouseListener[] ML = getMouseListeners();
        for (int i=0; i<ML.length; i++){
            removeMouseListener(ML[i]);
        }
        
        MouseMotionListener[] MML = getMouseMotionListeners();
        for (int i=0; i<MML.length; i++){
            removeMouseMotionListener(MML[i]);
        }
        
        MouseWheelListener[] MWL = getMouseWheelListeners();
        for (int i=0; i<MWL.length; i++){
            removeMouseWheelListener(MWL[i]);
        }
        
        java.beans.PropertyChangeListener[] PCL = getPropertyChangeListeners();
        for (int i=0; i<PCL.length; i++){
            removePropertyChangeListener(PCL[i]);
        }
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
		    /*if (!e.isPopupTrigger())
			return;*/ //removed because it avoided selection by rightclicking
            if (getPathForLocation(e.getX(),e.getY()) != null)
            {
              clearSelection();
			  setSelectionPath( getPathForLocation(e.getX(),e.getY()) );
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

	//DefaultMutableTreeNode memberNode = null;
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
	
	JDialog waitingDialog = new JDialog((Frame)null,"Importing devices on " + family + "...");
	waitingDialog.setVisible(true);
	ATKGraphicsUtils.centerDialog(waitingDialog,400,0);
	
	propChanges.fireStatusEvent(this, "Importing devices on " + family + "...");
	family.setFilled(true);
	List  members = family.getChildren();
	List<IDevice> devices = new Vector<IDevice> ();
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
	waitingDialog.setVisible(false);
	waitingDialog=null;
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

	    //IAttribute a;
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
	    
	    //ICommand c;
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
	frame.setVisible(true);

    }
	
}
