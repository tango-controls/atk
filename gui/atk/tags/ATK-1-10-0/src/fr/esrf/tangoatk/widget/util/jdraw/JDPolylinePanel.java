/** A panel for JDPolyline private properties */
package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class JDPolylinePanel extends JPanel implements ActionListener,ChangeListener {

  JCheckBox closedCheckBox;

  private JLabel stepLabel;
  private JSpinner stepSpinner;

  JDPolyline[] allObjects;
  JComponent invoker;
  Rectangle oldRect;

  public JDPolylinePanel(JDPolyline[] p, JComponent jc) {

    allObjects = p;
    invoker = jc;

    setLayout(null);
    setBorder(BorderFactory.createEtchedBorder());
    setPreferredSize(new Dimension(380, 300));

    // ------------------------------------------------------------------------------------
    JPanel polyPanel = new JPanel(null);
    polyPanel.setBorder(JDUtils.createTitleBorder("Polyline"));
    polyPanel.setBounds(5,5,370,85);

    closedCheckBox = new JCheckBox("Closed");
    closedCheckBox.setFont(JDUtils.labelFont);
    closedCheckBox.setSelected(p[0].isClosed());
    closedCheckBox.setBounds(5, 20, 100, 24);
    closedCheckBox.addActionListener(this);
    polyPanel.add(closedCheckBox);

    stepLabel = new JLabel("Interpolation step");
    stepLabel.setFont(JDUtils.labelFont);
    stepLabel.setForeground(JDUtils.labelColor);
    stepLabel.setBounds(10, 50, 100, 25);
    polyPanel.add(stepLabel);

    stepSpinner = new JSpinner();
    Integer value = new Integer(p[0].getStep());
    Integer min = new Integer(1);
    Integer max = new Integer(256);
    Integer step = new Integer(1);
    SpinnerNumberModel spModel = new SpinnerNumberModel(value, min, max, step);
    stepSpinner.setModel(spModel);
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
    int i;
    initRepaint();
    Object src = e.getSource();
    if (src == closedCheckBox) {
      for (i = 0; i < allObjects.length; i++)
        allObjects[i].setClosed(closedCheckBox.isSelected());
      JDUtils.modified=true;
    }
    repaintObjects();
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
    repaintObjects();

  }

}
