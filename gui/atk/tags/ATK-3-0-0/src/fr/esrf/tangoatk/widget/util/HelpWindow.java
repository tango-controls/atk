// File:          HelpWindow.java
// Created:       2002-09-24 14:51:03, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-10-03 18:51:4, erik>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import java.util.*;

import fr.esrf.tangoatk.widget.util.*;

public class HelpWindow extends JFrame implements IControlee {
    protected static HelpWindow instance;
    JSplitPane mainSplit;
    JTree topics;
    JEditorPane htmlView;
    HelpNode top;
    Map nodes;
    JButton back;
    JButton forward;
    java.util.List views;
    JTextField location;
    boolean noHistory = false;
    int currentView = 0;
    ButtonBar bb;
    
    protected HelpWindow() {
	initComponents();

    }

    public void ok() {
	this.setVisible(false);
    }

    public static HelpWindow getInstance() {
	if (instance == null) {
	    instance = new HelpWindow();
	}
	return instance;
    }


    protected void initComponents() {
	bb = new ButtonBar();
	top = new HelpNode();
	nodes = new HashMap();
	views = new Vector();
	JLabel label = new JLabel("Location: ");
	back = new JButton();
	forward = new JButton();
	location = new JTextField();
	location.setEditable(false);
	location.setBackground(Color.white);
	htmlView = new JEditorPane();
	setTitle("Help");
	bb.setControlee(this);
	htmlView.setEditable(false);
	back.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    goBack();
		}
	    });

	forward.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    goForward();
		}
	    });

	back.setToolTipText("Back");
	back.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/Back16.gif")));
	forward.setToolTipText("Forward");
	forward.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/Forward16.gif")));
	htmlView.addHyperlinkListener(new HyperlinkListener() {

		public void hyperlinkUpdate(HyperlinkEvent e) {
		    if (e.getEventType() ==
			HyperlinkEvent.EventType.ACTIVATED) {
			if (e instanceof HTMLFrameHyperlinkEvent) {
			    HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
			    HTMLDocument doc = (HTMLDocument)htmlView.getDocument();
			    doc.processHTMLFrameHyperlinkEvent(evt);
			} else {
			    try {
				showUrl(e.getURL());
			    } catch (Throwable t) {
				t.printStackTrace();
			    }
			}
		    }
		}
	    });
	topics = new JTree(top);

	topics.getSelectionModel().setSelectionMode
	    (TreeSelectionModel.SINGLE_TREE_SELECTION);

	topics.addTreeSelectionListener(new TreeSelectionListener() {

		public void valueChanged(TreeSelectionEvent e) {

		    DefaultMutableTreeNode n =
			(DefaultMutableTreeNode)
			topics.getLastSelectedPathComponent();

		    if (n == null || !(n instanceof HelpNode)) return;
		    HelpNode node = (HelpNode)n;
		    showPage(node.getUrl());

		    if (noHistory) {
			noHistory = false;
			return;
		    }
		    views.add(node);
		    currentView++;
		}
	    });
	
	JScrollPane treeScroll = new JScrollPane(topics);
	JScrollPane htmlScroll = new JScrollPane(htmlView);
	mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				   treeScroll, htmlScroll);
	mainSplit.setDividerSize(9);
	mainSplit.setOneTouchExpandable(true);
	mainSplit.setDividerLocation(150);
	getContentPane().setLayout(new GridBagLayout());
	GridBagConstraints constraints = new GridBagConstraints();
	constraints.gridx = 0;
	constraints.gridy = 0;
	constraints.fill = constraints.BOTH;
	constraints.insets = new java.awt.Insets(10, 10, 5, 0);
	getContentPane().add(label, constraints);
	constraints.gridx = 1;
	constraints.weightx = 1;
	constraints.insets = new java.awt.Insets(10, 0, 5, 0);
	getContentPane().add(location, constraints);
	constraints.weightx = 0;
	constraints.gridx = 2;
	constraints.insets = new java.awt.Insets(10, 0, 5, 0);
	getContentPane().add(back, constraints);
	constraints.gridx = 3;
	constraints.insets = new java.awt.Insets(10, 0, 5, 10);
	getContentPane().add(forward, constraints);
	constraints.weighty = 1;
	constraints.gridx = 0;
	constraints.gridy = 1;
	constraints.insets = new java.awt.Insets(0, 10, 0, 10);
	constraints.gridwidth = constraints.REMAINDER;
	treeScroll.setPreferredSize(new Dimension(150, 50));
	htmlScroll.setPreferredSize(new Dimension(400, 300));
	getContentPane().add(mainSplit, constraints);
	constraints.weighty = 0;
	constraints.gridy = 2;
	constraints.insets = new java.awt.Insets(0, 0, 0, 0);
	getContentPane().add(bb, constraints);
	pack();

    }

    public void showUrl(URL url) {
	String urlName = url.toString();
	int index;

	showPage(url);	

	if ((index = urlName.indexOf('#')) != -1) {
	    try {
		url = new URL(urlName.substring(0, index));
	    } catch (MalformedURLException e) {
		System.out.println("Can't display url: " + e.getMessage());
	    }
	}

	HelpNode node = (HelpNode)nodes.get(url);

	if (node == null) return;

	topics.setSelectionPath(new TreePath(node.getPath()));
	showHelpWindow();
    }

    public void goBack() {
	if (views.size() > 1 && currentView > 0) {
	    noHistory = true;
	    showNode((HelpNode)views.get(--currentView));
	}
    }

    public void goForward() {
	if (currentView < (views.size() - 1)) {
	    noHistory = true;
	    showNode((HelpNode)views.get(++currentView));
	}
    }
	
    protected void showPage(URL url) {
	try {
	    htmlView.setPage(url);
	    location.setText(url.toString());
	} catch (IOException e) {
	    System.out.println("Can't display url: " + e.getMessage());
	}

    }

    public void showNode(HelpNode node) {
	topics.setSelectionPath(new TreePath(node.getPath()));
	showPage(node.getUrl());

    }
	
    public void addCategory(String category, String name,
			    URL url) {
	CategoryNode node = null;
	
	Enumeration i = top.children();

	boolean found = false;
	
	while (i.hasMoreElements()) {
	    DefaultMutableTreeNode n =
		(DefaultMutableTreeNode)i.nextElement();
	    if (n instanceof HelpNode) continue;
	    
	    node = (CategoryNode)n;
	    if (node.getCategory().equals(category)) {
		found = true;
		break;
	    }
	}
	if (!found) {
	    node = new CategoryNode(category, name, url);
	    top.add(node);
	} else {
	    if (nodes.containsKey(url)) return;
	    node.add(name, url);
	}

	//	topics.expandRow(0);
	pack();
    }

    public void setTop(String name, URL url) {
	top.setName(name);
	top.setUrl(url);
	nodes.put(url, top);
	views.add(top);
	showPage(url);
    }
	
    public void addTop(String name, URL url) {
	HelpNode node = null;
	node = new HelpNode(name, url);
	top.add(node);
	pack();
    }

    public void showHelpWindow() {
	topics.expandRow(0);
	super.setVisible(true);
    }

    public static void main(String [] args) throws Exception {

	HelpWindow.getInstance().
	    setTop("Urk", new URL("http://www.dagbladet.no"));

	HelpWindow.getInstance().
	    addTop("first", new URL("http://www.skiinfo.no"));
	HelpWindow.getInstance().
	    addCategory("Skiinfo", "second", new URL("http://www.skiinfo.no"));
	HelpWindow.getInstance().showHelpWindow();

    }

    class CategoryNode extends DefaultMutableTreeNode {
	String category;

	public CategoryNode(String category, String name, URL url) {
	    this.category = category;
	    add(name, url);
	}

	public String getCategory() {
	    return category;
	}
	    
	public String toString() {
	    return category;
	}

	public void add(String name, URL url) {
	    this.add(new HelpNode(name, url));
	}
    }


    class HelpNode extends DefaultMutableTreeNode {
	String name;
	URL url;

	public HelpNode() {
	}
	
	public HelpNode(String name, URL url) {
	    this.name = name;
	    this.url = url;
	    nodes.put(url, this);
	}

	public String toString() {
	    return name;
	}

	public URL getUrl() {
	    return url;
	}

	public void setUrl(URL url) {
	    this.url = url;
	}

	public void setName(String name) {
	    this.name = name;
	}
    }
}
