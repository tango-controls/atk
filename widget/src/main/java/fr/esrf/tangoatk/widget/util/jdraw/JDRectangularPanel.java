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

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class JDRectangularPanel extends JPanel implements ActionListener,ChangeListener {


  private JLabel   leftLabel;
  private JSpinner leftSpinner;
  private JLabel   rightLabel;
  private JSpinner rightSpinner;
  private JLabel   bottomLabel;
  private JSpinner bottomSpinner;
  private JLabel   topLabel;
  private JSpinner topSpinner;
  private JLabel   widthLabel;
  private JSpinner widthSpinner;
  private JLabel   heightLabel;
  private JSpinner heightSpinner;

  private JDRectangular[] allObjects = null;
  private JDrawEditor invoker;
  private Rectangle oldRect;
  private boolean isUpdating = false;

  public JDRectangularPanel(JDRectangular[] p, JDrawEditor jc) {

    invoker = jc;

    setLayout(null);
    setBorder(BorderFactory.createEtchedBorder());
    setPreferredSize(new Dimension(380, 290));

    // ------------------------------------------------------------------------------------
    JPanel posPanel = new JPanel(null);
    posPanel.setBorder(JDUtils.createTitleBorder("Positioning"));
    posPanel.setBounds(5, 5, 370, 85);

    leftLabel = new JLabel("Left");
    leftLabel.setFont(JDUtils.labelFont);
    leftLabel.setForeground(JDUtils.labelColor);
    leftLabel.setBounds(10, 20, 100, 25);
    posPanel.add(leftLabel);

    leftSpinner = new JSpinner();
    leftSpinner.addChangeListener(this);
    leftSpinner.setBounds(115, 20, 60, 25);
    posPanel.add(leftSpinner);

    rightLabel = new JLabel("Right");
    rightLabel.setFont(JDUtils.labelFont);
    rightLabel.setForeground(JDUtils.labelColor);
    rightLabel.setBounds(195, 20, 100, 25);
    posPanel.add(rightLabel);

    rightSpinner = new JSpinner();
    rightSpinner.addChangeListener(this);
    rightSpinner.setBounds(295, 20, 60, 25);
    posPanel.add(rightSpinner);

    topLabel = new JLabel("Top");
    topLabel.setFont(JDUtils.labelFont);
    topLabel.setForeground(JDUtils.labelColor);
    topLabel.setBounds(10, 50, 100, 25);
    posPanel.add(topLabel);

    topSpinner = new JSpinner();
    topSpinner.addChangeListener(this);
    topSpinner.setBounds(115, 50, 60, 25);
    posPanel.add(topSpinner);

    bottomLabel = new JLabel("Bottom");
    bottomLabel.setFont(JDUtils.labelFont);
    bottomLabel.setForeground(JDUtils.labelColor);
    bottomLabel.setBounds(195, 50, 100, 25);
    posPanel.add(bottomLabel);

    bottomSpinner = new JSpinner();
    bottomSpinner.addChangeListener(this);
    bottomSpinner.setBounds(295, 50, 60, 25);
    posPanel.add(bottomSpinner);

    add(posPanel);

    JPanel dimPanel = new JPanel(null);
    dimPanel.setBorder(JDUtils.createTitleBorder("Dimension"));
    dimPanel.setBounds(5, 90, 370, 55);

    widthLabel = new JLabel("Width");
    widthLabel.setFont(JDUtils.labelFont);
    widthLabel.setForeground(JDUtils.labelColor);
    widthLabel.setBounds(10, 20, 100, 25);
    dimPanel.add(widthLabel);

    widthSpinner = new JSpinner();
    widthSpinner.addChangeListener(this);
    widthSpinner.setBounds(115, 20, 60, 25);
    dimPanel.add(widthSpinner);

    heightLabel = new JLabel("Height");
    heightLabel.setFont(JDUtils.labelFont);
    heightLabel.setForeground(JDUtils.labelColor);
    heightLabel.setBounds(195, 20, 100, 25);
    dimPanel.add(heightLabel);

    heightSpinner = new JSpinner();
    heightSpinner.addChangeListener(this);
    heightSpinner.setBounds(295, 20, 60, 25);
    dimPanel.add(heightSpinner);

    add(dimPanel);

    updatePanel(p);

  }

  public void updatePanel(JDRectangular[] objs) {

    allObjects = objs;
    isUpdating = true;

    if (objs == null || objs.length <= 0) {

      SpinnerNumberModel nullModel = new SpinnerNumberModel(0, 0, 0, 0);
      leftSpinner.setModel(nullModel);

    } else {

      JDRectangular p = objs[0];
      Integer min = -4096;
      Integer max = 4096;
      Integer step = 1;

      leftSpinner.setModel(new SpinnerNumberModel(new Integer(p.getLeft()), min, max, step));
      rightSpinner.setModel(new SpinnerNumberModel(new Integer(p.getRight()), min, max, step));
      topSpinner.setModel(new SpinnerNumberModel(new Integer(p.getTop()), min, max, step));
      bottomSpinner.setModel(new SpinnerNumberModel(new Integer(p.getBottom()), min, max, step));
      widthSpinner.setModel(new SpinnerNumberModel(new Integer(p.getWidth()), min, max, step));
      heightSpinner.setModel(new SpinnerNumberModel(new Integer(p.getHeight()), min, max, step));

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

  }

  // ---------------------------------------------------------
  //Change listener
  // ---------------------------------------------------------
  public void stateChanged(ChangeEvent e) {

    if(allObjects==null || isUpdating) return;

    int i;
    initRepaint();
    Object src = e.getSource();
    Integer v;

    if (src == leftSpinner) {
      v = (Integer) leftSpinner.getValue();
      for (i = 0; i < allObjects.length; i++)
        allObjects[i].setLeft(v.intValue());
      invoker.setNeedToSave(true, "Change left");
    } else if (src == rightSpinner) {
      v = (Integer) rightSpinner.getValue();
      for (i = 0; i < allObjects.length; i++)
        allObjects[i].setRight(v.intValue());
      invoker.setNeedToSave(true,"Change right");
    } else if (src == topSpinner) {
      v = (Integer) topSpinner.getValue();
      for (i = 0; i < allObjects.length; i++)
        allObjects[i].setTop(v.intValue());
      invoker.setNeedToSave(true,"Change top");
    } else if (src == bottomSpinner) {
      v = (Integer) bottomSpinner.getValue();
      for (i = 0; i < allObjects.length; i++)
        allObjects[i].setBottom(v.intValue());
      invoker.setNeedToSave(true,"Change bottom");
    } else if (src == widthSpinner) {
      v = (Integer) widthSpinner.getValue();
      for (i = 0; i < allObjects.length; i++)
        allObjects[i].setWidth(v.intValue());
      invoker.setNeedToSave(true,"Change width");
    } else if (src == heightSpinner) {
      v = (Integer) heightSpinner.getValue();
      for (i = 0; i < allObjects.length; i++)
        allObjects[i].setHeight(v.intValue());
      invoker.setNeedToSave(true,"Change height");
    }
    updatePanel(allObjects);
    repaintObjects();

  }

}
