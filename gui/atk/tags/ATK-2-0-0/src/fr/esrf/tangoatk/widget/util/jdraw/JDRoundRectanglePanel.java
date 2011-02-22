package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.TitledBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class JDRoundRectanglePanel extends JPanel implements ActionListener,ChangeListener {

  private JLabel stepLabel;
  private JSpinner stepSpinner;

  private JLabel cornerWidthLabel;
  private JSpinner cornerWidthSpinner;

  JDRoundRectangle[] allObjects;
  JComponent invoker;
  Rectangle oldRect;

  public JDRoundRectanglePanel(JDRoundRectangle[] p, JComponent jc) {

    allObjects = p;
    invoker = jc;

    setLayout(null);
    setBorder(BorderFactory.createEtchedBorder());
    setPreferredSize(new Dimension(380, 290));

    // ------------------------------------------------------------------------------------
    JPanel polyPanel = new JPanel(null);
    polyPanel.setBorder(JDUtils.createTitleBorder("Corner"));
    polyPanel.setBounds(5,5,370,85);

    cornerWidthLabel = new JLabel("Corner width");
    cornerWidthLabel.setFont(JDUtils.labelFont);
    cornerWidthLabel.setForeground(JDUtils.labelColor);
    cornerWidthLabel.setBounds(10, 20, 100, 25);
    polyPanel.add(cornerWidthLabel);

    cornerWidthSpinner = new JSpinner();
    Integer value = new Integer(p[0].getCornerWidth());
    Integer min = new Integer(1);
    Integer max = new Integer(256);
    Integer step = new Integer(1);
    SpinnerNumberModel spModel = new SpinnerNumberModel(value, min, max, step);
    cornerWidthSpinner.setModel(spModel);
    cornerWidthSpinner.addChangeListener(this);
    cornerWidthSpinner.setBounds(115, 20, 60, 25);
    polyPanel.add(cornerWidthSpinner);

    stepLabel = new JLabel("Interpolation step");
    stepLabel.setFont(JDUtils.labelFont);
    stepLabel.setForeground(JDUtils.labelColor);
    stepLabel.setBounds(10, 50, 100, 25);
    polyPanel.add(stepLabel);

    stepSpinner = new JSpinner();
    Integer value2 = new Integer(p[0].getStep());
    Integer min2 = new Integer(1);
    Integer max2 = new Integer(256);
    Integer step2 = new Integer(1);
    SpinnerNumberModel spModel2 = new SpinnerNumberModel(value2, min2, max2, step2);
    stepSpinner.setModel(spModel2);
    stepSpinner.addChangeListener(this);
    stepSpinner.setBounds(115, 50, 60, 25);
    polyPanel.add(stepSpinner);

    add(polyPanel);

  }

  private void initRepaint() {
    oldRect = allObjects[0].getRepaintRect();
    for (int i = 1; i < allObjects.length; i++)
      oldRect = oldRect.union(allObjects[i].getRepaintRect());
  }

  private void repaintObjects() {
    Rectangle newRect = allObjects[0].getRepaintRect();
    for (int i = 1; i < allObjects.length; i++)
      newRect = newRect.union(allObjects[i].getRepaintRect());
    invoker.repaint(newRect.union(oldRect));
  }


  // ---------------------------------------------------------
  // Action listener
  // ---------------------------------------------------------
  public void actionPerformed(ActionEvent e) {
  }

  // ---------------------------------------------------------
  //Change listener
  // ---------------------------------------------------------
  public void stateChanged(ChangeEvent e) {

    int i;
    initRepaint();
    Object src = e.getSource();
    Integer v;

    if (src == stepSpinner) {
      v = (Integer) stepSpinner.getValue();
      for (i = 0; i < allObjects.length; i++)
        allObjects[i].setStep(v.intValue());
      JDUtils.modified=true;
    }
    if (src == cornerWidthSpinner) {
      v = (Integer) cornerWidthSpinner.getValue();
      for (i = 0; i < allObjects.length; i++)
        allObjects[i].setCornerWidth(v.intValue());
      JDUtils.modified=true;
    }
    repaintObjects();

  }

}
