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

/** A panel for JDTitledRect private properties */
package fr.esrf.tangoatk.widget.util.jdraw;

import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.ATKFontChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

class JDTitledRectPanel extends JPanel implements ActionListener {

  private JTextField titleText;
  private JButton applyTitleBtn;

  private JLabel fontLabel;
  private JButton fontBtn;

  private JLabel c1Label;
  private JButton c1Button;
  private JLabel c2Label;
  private JButton c2Button;
  private JCheckBox etchedCheckBox;

  private JDTitledRect allObjects[] = null;
  private JDrawEditor invoker;
  private Rectangle oldRect;
  private boolean isUpdating = false;

  public JDTitledRectPanel(JDTitledRect[] p, JDrawEditor jc) {

    invoker = jc;
    setLayout(null);
    setBorder(BorderFactory.createEtchedBorder());
    setPreferredSize(new Dimension(380, 290));

    // ------------------------------------------------------------------------------------
    JPanel namePanel = new JPanel(null);
    namePanel.setBorder(JDUtils.createTitleBorder("Title"));
    namePanel.setBounds(5, 5, 370, 55);

    titleText = new JTextField();
    titleText.setMargin(JDUtils.zMargin);
    titleText.setEditable(true);
    titleText.setFont(JDUtils.labelFont);
    titleText.setBounds(10, 20, 260, 24);
    titleText.addActionListener(this);
    namePanel.add(titleText);

    applyTitleBtn = new JButton("Apply");
    applyTitleBtn.setFont(JDUtils.labelFont);
    applyTitleBtn.setBounds(270, 20, 90, 24);
    applyTitleBtn.addActionListener(this);
    namePanel.add(applyTitleBtn);

    add(namePanel);

    // ------------------------------------------------------------------------------------
    JPanel stylePanel = new JPanel(null);
    stylePanel.setBorder(JDUtils.createTitleBorder("Text styles"));
    stylePanel.setBounds(5,60,370,55);

    fontLabel = new JLabel("Font");
    fontLabel.setFont(JDUtils.labelFont);
    fontLabel.setForeground(JDUtils.labelColor);
    fontLabel.setBounds(10, 20, 135, 24);
    stylePanel.add(fontLabel);

    fontBtn = new JButton();
    fontBtn.setText("Choose");
    fontBtn.setMargin(new Insets(0, 0, 0, 0));
    fontBtn.setFont(JDUtils.labelFont);
    fontBtn.setBounds(220, 20, 140, 24);
    fontBtn.addActionListener(this);
    stylePanel.add(fontBtn);

    add(stylePanel);

    // ------------------------------------------------------------------------------------
    JPanel borderPanel = new JPanel(null);
    borderPanel.setBorder(JDUtils.createTitleBorder("Border"));
    borderPanel.setBounds(5, 120, 370, 85);

    c1Label = JDUtils.createLabel("Color #1");
    c1Label.setBounds(10, 20, 100, 24);
    borderPanel.add(c1Label);
    c1Button = new JButton("...");
    c1Button.setMargin(new Insets(0, 0, 0, 0));
    c1Button.setForeground(Color.BLACK);
    c1Button.addActionListener(this);
    c1Button.setBounds(120, 20, 60, 24);
    borderPanel.add(c1Button);
    add(borderPanel);

    c2Label = JDUtils.createLabel("Color #2");
    c2Label.setBounds(190, 20, 100, 24);
    borderPanel.add(c2Label);
    c2Button = new JButton("");
    c2Button.setMargin(new Insets(0, 0, 0, 0));
    c2Button.setForeground(Color.BLACK);
    c2Button.addActionListener(this);
    c2Button.setBounds(300, 20, 60, 24);
    borderPanel.add(c2Button);

    etchedCheckBox = JDUtils.createCheckBox("Etched",this);
    etchedCheckBox.setBounds(10,50,150,25);
    borderPanel.add(etchedCheckBox);

    updatePanel(p);

  }

  public void updatePanel(JDTitledRect[] objs) {

    allObjects = objs;
    isUpdating = true;

    if (objs == null || objs.length <= 0) {

      titleText.setText("");
      fontLabel.setText("Font: ");
      c1Button.setBackground(JDTitledRect.color1Default);
      c2Button.setBackground(JDTitledRect.color2Default);
      etchedCheckBox.setSelected(true);

    } else {

      JDTitledRect p = objs[0];

      fontLabel.setText("Font: [" + JDUtils.buildFontName(p.getFont()) + "]");
      titleText.setText(p.getTitle());
      c1Button.setBackground(p.getColor1());
      c2Button.setBackground(p.getColor2());
      etchedCheckBox.setSelected(p.hasEtchedBorder());

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
      Font newFont = ATKFontChooser.getNewFont(this, "Choose label font", allObjects[0].getFont());
      if (newFont != null) {
        for (i = 0; i < allObjects.length; i++)
          allObjects[i].setFont(newFont);
        fontLabel.setText("Font: [" + JDUtils.buildFontName(newFont) + "]");
        invoker.setNeedToSave(true,"Change font");
      }
    } else if (src == applyTitleBtn || src == titleText) {
      for (i = 0; i < allObjects.length; i++)
        allObjects[i].setTitle(titleText.getText());
      invoker.setNeedToSave(true,"Change title");
    } else if (src == c1Button) {
      Color c = JColorChooser.showDialog(this, "Choose color #1", allObjects[0].getColor1());
      if (c != null) {
        for (i = 0; i < allObjects.length; i++)
          allObjects[i].setColor1(c);
        c1Button.setBackground(c);
        invoker.setNeedToSave(true,"Change color #1");
      }
    } else if (src == c2Button) {
      Color c = JColorChooser.showDialog(this, "Choose color #2", allObjects[0].getColor2());
      if (c != null) {
        for (i = 0; i < allObjects.length; i++)
          allObjects[i].setColor2(c);
        c2Button.setBackground(c);
        invoker.setNeedToSave(true,"Change color #2");
      }
    } else if (src == etchedCheckBox) {
       for (i = 0; i < allObjects.length; i++)
         allObjects[i].setEtchedBorder(etchedCheckBox.isSelected());
       invoker.setNeedToSave(true,"Change etched");
    }

    repaintObjects();
  }


}
