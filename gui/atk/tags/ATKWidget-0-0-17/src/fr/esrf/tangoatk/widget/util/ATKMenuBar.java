// File:          ATKMenuBar.java
// Created:       2002-07-15 14:59:14, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-15 16:29:9, assum>
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
    GridBagConstraints constraints;
    
    public ATKMenuBar() {
	constraints = new GridBagConstraints();

	setLayout(new GridBagLayout());
	constraints.gridx = 0;
	add(file = new JMenu("File"), constraints);
	constraints.gridx = 1;
	add(edit = new JMenu("Edit"), constraints);
	constraints.gridx = 2;
	add(view = new JMenu("View"), constraints);

	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.weightx = 0.1;
	constraints.gridx = 200;
	add(new JLabel(""), constraints);
	constraints.fill = GridBagConstraints.NONE;
	constraints.gridx = 201;
	constraints.weightx = 0;
	add(help = new JMenu("Help"), constraints);
	constraints.gridx = 2;

	exitItem = new JMenuItem("Quit");
	aboutItem = new JMenuItem("About...");
	
	file.setMnemonic('F');
	view.setMnemonic('V');
	edit.setMnemonic('E');
	help.setMnemonic('H');

	exitItem.setAccelerator(KeyStroke.getKeyStroke('Q', KeyEvent.CTRL_MASK));
	file.add(new JSeparator());
	file.add(exitItem);

	help.add(aboutItem);
    }

    public void setFont(Font f) {
	super.setFont(f);
	if (file == null) return;
	
	file.setFont(f);
	view.setFont(f);
	edit.setFont(f);
	help.setFont(f);
	exitItem.setFont(f);
	aboutItem.setFont(f);
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

    public void add2EditMenu(JComponent item, int i) {
	item.setFont(getFont());
	edit.add(item, i);
    }

    public void add2HelpMenu(JComponent item, int i) {
	item.setFont(getFont());
	help.add(item, i);
    }

    public void add2FileMenu(JComponent item, int i) {
	item.setFont(getFont());
	file.add(item, i);
    }
	
    public void addMenu(JMenu menu) {
	constraints.gridx++;
	System.out.println("gridx = " + constraints.gridx);
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
	f.show();
    } // end of main ()
    


}
