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
 
// File:          ATKMenuBar.java
// Created:       2002-07-15 14:59:14, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-10-17 10:25:54, erik>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


public class ATKMenuBar extends JMenuBar {
    JMenu file;
    JMenu view;
    JMenu edit;
    JMenu help;
    JMenuItem exitItem;
    JMenuItem aboutItem;
    JMenuItem errorItem;
    JMenuItem helpItem;
    
    GridBagConstraints constraints;
    ErrorHistory errorHistory;
    
    public ATKMenuBar(ErrorHistory errorHistory) {
	this();
	setErrorHistory(errorHistory);
    }

    public void setErrorHistory(ErrorHistory errorHistory) {
	this.errorHistory = errorHistory;
    }

    public ErrorHistory getErrorHistory() {
	return errorHistory;
    }

    protected void showErrorHistory() {
	if (errorHistory == null) return;
	errorHistory.setVisible(true);
    }
	
    protected void showHelpWindow() {
	HelpWindow.getInstance().setVisible(true);
    }

    
    public ATKMenuBar() {
	constraints = new GridBagConstraints();
	constraints.gridx = 0;
	setLayout(new GridBagLayout());
	
	add(file = new JMenu("File"), constraints);
	constraints.gridx++;
	add(edit = new JMenu("Edit"), constraints);
	constraints.gridx++;
	add(view = new JMenu("View"), constraints);

	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.weightx = 0.1;
	constraints.gridx = 200;
	add(new JLabel(""), constraints);
	constraints.fill = GridBagConstraints.NONE;
	constraints.gridx = 201;
	constraints.weightx = 0;
	add(help = new JMenu("Help"), constraints);
	constraints.gridx = 3;

	exitItem = new JMenuItem("Quit");
	aboutItem = new JMenuItem("About...");
	helpItem = new JMenuItem("Help");
	
	file.setMnemonic('F');
	view.setMnemonic('V');
	edit.setMnemonic('E');
	help.setMnemonic('H');

	exitItem.setAccelerator(KeyStroke.getKeyStroke('Q', KeyEvent.CTRL_MASK));
	helpItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
	helpItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    showHelpWindow();
		}
	    });
				
	errorItem = new JMenuItem("Error history...");
	errorItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    showErrorHistory();
		}
	    });
	errorItem.setAccelerator(KeyStroke.getKeyStroke('E', KeyEvent.CTRL_MASK));
	add2ViewMenu(errorItem, 0);

	file.add(new JSeparator());
	file.add(exitItem);

	help.add(aboutItem);
	help.add(helpItem);
    }

    public void setFont(Font f) {
	super.setFont(f);
	if (file == null) return;
	
	file.setFont(f);
	view.setFont(f);
	edit.setFont(f);
	help.setFont(f);
	exitItem.setFont(f);
	errorItem.setFont(f);	    
	aboutItem.setFont(f);
	helpItem.setFont(f);
    }


    public void setQuitHandler(ActionListener listener) {
	exitItem.addActionListener(listener);
    }

    public void setAboutHandler(ActionListener listener) {
	aboutItem.addActionListener(listener);
    }

    public void add2ViewMenu(JComponent item, int i) {
	item.setFont(getFont());
	view.add(item, i);
    }

    public void add2ViewMenu(JComponent item) {
	item.setFont(getFont());
	view.add(item);
    }

    public void add2EditMenu(JComponent item, int i) {
	item.setFont(getFont());
	edit.add(item, i);
    }

    public void add2EditMenu(JComponent item) {
	item.setFont(getFont());
	edit.add(item);
    }

    public void add2HelpMenu(JComponent item, int i) {
	item.setFont(getFont());
	help.add(item, i);
    }

    public void add2HelpMenu(JComponent item) {
	item.setFont(getFont());
	help.add(item);
    }

    public void add2FileMenu(JComponent item, int i) {
	item.setFont(getFont());
	file.add(item, i);
    }

    public void add2FileMenu(JComponent item) {
	item.setFont(getFont());
	file.add(item);
    }
	
    public void addMenu(JMenu menu) {
	constraints.gridx++;
	menu.setFont(getFont());
	add(menu, constraints);
    }
    // #### BUG fix traversal policy!!

    
    public static void main (String[] args) {
	JFrame f = new JFrame();

	ATKMenuBar mb = new ATKMenuBar();
	mb.setFont(new java.awt.Font("Dialog", 0, 12));
	mb.add2FileMenu(new JMenuItem("foo"), 0);
	JMenu bar = new JMenu("bar");
	bar.setMnemonic('b');
	bar.add(new JMenuItem("baz"));
	mb.addMenu(bar);
	
	mb.setQuitHandler(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    System.exit(1);
		}
	    });
	f.setJMenuBar(mb);
	f.pack();
	f.setVisible(true);
    } // end of main ()
    


}
