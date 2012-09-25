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
 
package fr.esrf.tangoatk.widget.util.jdraw;

import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class JDExtensionEditor extends JDialog implements ActionListener {

  private boolean modified = false;

  private JTextArea textArea;
  private JScrollPane textView;
  private JButton cancelBtn;
  private JButton applyBtn;

  JDExtensionEditor(Dialog parent) {
    super(parent,true);
    initComponents();
  }

  JDExtensionEditor() {
    super((Frame)null,true);
    initComponents();
  }

  private void initComponents() {

    JPanel innerPane = new JPanel();
    innerPane.setLayout(new BorderLayout());

    // ---------------------------------------------
    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    applyBtn = new JButton("Apply change");
    applyBtn.setFont(JDUtils.labelFont);
    applyBtn.addActionListener(this);
    btnPanel.add(applyBtn);

    cancelBtn = new JButton("Cancel");
    cancelBtn.setFont(JDUtils.labelFont);
    cancelBtn.addActionListener(this);
    btnPanel.add(cancelBtn);

    innerPane.add(btnPanel,BorderLayout.SOUTH);

    textArea = new JTextArea();
    textArea.setEditable(true);
    textView = new JScrollPane(textArea);
    innerPane.add(textView,BorderLayout.CENTER);

    setContentPane(innerPane);

  }

  public void actionPerformed(ActionEvent e) {

    Object src = e.getSource();

    if(src==cancelBtn) {
      setVisible(false);
      dispose();
    } else if(src==applyBtn) {
      modified = true;
      setVisible(false);
      dispose();
    }

  }

  boolean getModified() {
    return modified;
  }

  String getValue() {
    return textArea.getText();
  }

  void setValue(String value) {
    textArea.setText(value);
  }

  static String showExtensionEditor(JComponent parent,String title,String defaultValue) {

    JDExtensionEditor dlg;

    Window peer = ATKGraphicsUtils.getWindowForComponent(parent);
    if(peer instanceof Dialog) {
      dlg = new JDExtensionEditor((Dialog)peer);
    } else {
      System.out.println("jdraw.JDExtensionEditor() : Warning null parent");
      dlg = new JDExtensionEditor();
    }

    dlg.setTitle(title);
    // Hack to enlarge a bit the dialog
    dlg.setValue(defaultValue+"____\n_");
    ATKGraphicsUtils.centerDialog(dlg);
    dlg.setValue(defaultValue);
    dlg.setVisible(true);
    if(dlg.getModified()) {
      return dlg.getValue();
    } else {
      return null;
    }

  }


}
