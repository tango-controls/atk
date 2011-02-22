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
 
/** A panel for JDPolyline private properties */
package fr.esrf.tangoatk.widget.util.jdraw;

import fr.esrf.tangoatk.widget.util.ATKFontChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class JDSwingPanel extends JPanel implements ActionListener {

  private JTextField classnameLabel;

  private JLabel fontLabel;
  private JButton fontBtn;

  private JLabel borderLabel;
  private JComboBox borderCombo;

  private JDSwingObject[] allObjects = null;
  private JDrawEditor invoker;
  private Rectangle oldRect;
  private boolean isUpdating = false;

  public JDSwingPanel(JDSwingObject[] p, JDrawEditor jc) {

    invoker = jc;

    setLayout(null);
    setBorder(BorderFactory.createEtchedBorder());
    setPreferredSize(new Dimension(380, 290));

    // ------------------------------------------------------------------------------------
    JPanel classPanel = new JPanel(null);
    classPanel.setBorder(JDUtils.createTitleBorder("Object Class"));
    classPanel.setBounds(5,5,370,60);

    classnameLabel = new JTextField();
    classnameLabel.setMargin(JDUtils.zMargin);
    classnameLabel.setFont(JDUtils.labelFont);
    classnameLabel.setEditable(false);
    classnameLabel.setBorder(null);
    classnameLabel.setForeground(JDUtils.labelColor);
    classnameLabel.setBounds(10, 20, 350, 25);
    classPanel.add(classnameLabel);
    add(classPanel);

    // ------------------------------------------------------------------------------------
    JPanel propPanel = new JPanel(null);
    propPanel.setBorder(JDUtils.createTitleBorder("Component properties"));
    propPanel.setBounds(5,65,370,85);

    fontLabel = new JLabel("Font");
    fontLabel.setFont(JDUtils.labelFont);
    fontLabel.setForeground(JDUtils.labelColor);
    fontLabel.setBounds(10, 20, 135, 24);
    propPanel.add(fontLabel);

    fontBtn = new JButton();
    fontBtn.setText("Choose");
    fontBtn.setMargin(new Insets(0, 0, 0, 0));
    fontBtn.setFont(JDUtils.labelFont);
    fontBtn.setBounds(220, 20, 140, 24);
    fontBtn.addActionListener(this);
    propPanel.add(fontBtn);

    borderLabel = new JLabel("Border");
    borderLabel.setFont(JDUtils.labelFont);
    borderLabel.setForeground(JDUtils.labelColor);
    borderLabel.setBounds(10, 50, 125, 25);
    propPanel.add(borderLabel);

    borderCombo = new JComboBox();
    borderCombo.setFont(JDUtils.labelFont);
    borderCombo.addItem("No border");
    borderCombo.addItem("Lowered");
    borderCombo.addItem("Raised");
    borderCombo.addItem("Etched");
    borderCombo.addActionListener(this);
    borderCombo.setBounds(220, 50, 140, 25);
    propPanel.add(borderCombo);

    add(propPanel);

    updatePanel(p);

  }

  public void updatePanel(JDSwingObject[] objs) {

    allObjects = objs;
    isUpdating = true;

    if (objs == null || objs.length <= 0) {

      borderCombo.setSelectedIndex(-1);
      classnameLabel.setText("");
      fontLabel.setText("Font: ");

    } else {

      JDSwingObject p = objs[0];
      fontLabel.setText("Font: [" + JDUtils.buildFontName(p.getFont())+ "]");
      classnameLabel.setText(p.getClassName());
      borderCombo.setSelectedIndex(p.getBorder());

    }

    isUpdating = false;

  }

  private void initRepaint() {
    if(allObjects==null) return;
    oldRect = allObjects[0].getRepaintRect();
    for (int i = 1; i < allObjects.length; i++)
      oldRect = oldRect.union(allObjects[i].getRepaintRect());
  }

  private void repaintObjects() {
    if(allObjects==null) return;
    Rectangle newRect = allObjects[0].getRepaintRect();
    for (int i = 1; i < allObjects.length; i++)
      newRect = newRect.union(allObjects[i].getRepaintRect());
    invoker.repaint(newRect.union(oldRect));
  }


  // ---------------------------------------------------------
  // Action listener
  // ---------------------------------------------------------
  public void actionPerformed(ActionEvent e) {

    if(allObjects==null || isUpdating) return;

    int i;
    initRepaint();
    Object src = e.getSource();

    if (src == fontBtn) {
      Font newFont = ATKFontChooser.getNewFont(this,"Choose component font",allObjects[0].getFont());
      if (newFont != null) {
        for (i = 0; i < allObjects.length; i++)
          allObjects[i].setFont(newFont,invoker.resizeLabelOnFontChange);
        fontLabel.setText("Font: [" + JDUtils.buildFontName(newFont)+ "]");
        invoker.setNeedToSave(true,"Change font");
      }
    } else if (src == borderCombo) {
      for (i = 0; i < allObjects.length; i++)
        allObjects[i].setBorder(borderCombo.getSelectedIndex());
      invoker.setNeedToSave(true,"Change border");
    }

    repaintObjects();
  }



}
