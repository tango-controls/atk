package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


class JDTransformPanel extends JPanel implements ActionListener, ChangeListener {

  JButton upBtn;
  JButton downBtn;
  JButton leftBtn;
  JButton rightBtn;

  JLabel transXLabel;
  JTextField transXText;

  JLabel transYLabel;
  JTextField transYText;

  JDObject[] allObjects;
  JComponent invoker;
  Rectangle oldRect;

  JSlider scaleXSlider;
  JSlider scaleYSlider;

  JLabel scaleXLabel;
  JTextField scaleXText;

  JLabel scaleYLabel;
  JTextField scaleYText;

  JCheckBox rot90CheckBox;
  JCheckBox rot180CheckBox;
  JCheckBox rot270CheckBox;

  JCheckBox scaleRatioCheckBox;

  JButton resetTransformBtn;

  int transX;
  int transY;
  int scaleX;
  int scaleY;
  int angle;
  boolean transformInited = false;
  Point org;

  public JDTransformPanel(JDObject[] p, JComponent jc) {

    allObjects = p;
    invoker = jc;

    setLayout(null);
    setBorder(BorderFactory.createEtchedBorder());
    setPreferredSize(new Dimension(280, 360));

    // ------------------------------------------------------------------------------------
    JPanel translationPanel = new JPanel(null);
    translationPanel.setBorder(JDUtils.createTitleBorder("Translation"));
    translationPanel.setBounds(5, 5, 270, 90);

    transX = 0;
    transY = 0;

    upBtn = new JButton();
    upBtn.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/jdraw/gif/up_button.gif")));
    upBtn.setBounds(30, 20, 20, 20);
    upBtn.addActionListener(this);
    translationPanel.add(upBtn);

    downBtn = new JButton();
    downBtn.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/jdraw/gif/down_button.gif")));
    downBtn.setBounds(30, 60, 20, 20);
    downBtn.addActionListener(this);
    translationPanel.add(downBtn);

    leftBtn = new JButton();
    leftBtn.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/jdraw/gif/left_button.gif")));
    leftBtn.setBounds(10, 40, 20, 20);
    leftBtn.addActionListener(this);
    translationPanel.add(leftBtn);

    rightBtn = new JButton();
    rightBtn.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/jdraw/gif/right_button.gif")));
    rightBtn.setBounds(50, 40, 20, 20);
    rightBtn.addActionListener(this);
    translationPanel.add(rightBtn);

    transXLabel = new JLabel("Horizontal translation");
    transXLabel.setHorizontalAlignment(JLabel.RIGHT);
    transXLabel.setFont(JDUtils.labelFont);
    transXLabel.setForeground(JDUtils.labelColor);
    transXLabel.setBounds(80, 20, 135, 24);
    translationPanel.add(transXLabel);

    transXText = new JTextField();
    transXText.setEditable(true);
    transXText.setFont(JDUtils.labelFont);
    transXText.setBounds(220, 20, 40, 24);
    transXText.addActionListener(this);
    translationPanel.add(transXText);

    transYLabel = new JLabel("Vertical translation");
    transYLabel.setHorizontalAlignment(JLabel.RIGHT);
    transYLabel.setFont(JDUtils.labelFont);
    transYLabel.setForeground(JDUtils.labelColor);
    transYLabel.setBounds(80, 50, 135, 24);
    translationPanel.add(transYLabel);

    transYText = new JTextField();
    transYText.setEditable(true);
    transYText.setFont(JDUtils.labelFont);
    transYText.setBounds(220, 50, 40, 24);
    transYText.addActionListener(this);
    translationPanel.add(transYText);
    add(translationPanel);

    // ------------------------------------------------------------------------------------
    JPanel scalePanel = new JPanel(null);
    scalePanel.setBorder(JDUtils.createTitleBorder("Scaling"));
    scalePanel.setBounds(5, 100, 270, 150);

    scaleX = 100;
    scaleY = 100;
    angle=0;
    org = JDUtils.getCenter(allObjects);

    scaleXSlider = new JSlider(10, 500, scaleX);
    scaleXSlider.setMinorTickSpacing(20);
    scaleXSlider.setMajorTickSpacing(100);
    scaleXSlider.setPaintTicks(true);
    scaleXSlider.setPaintLabels(false);
    scaleXSlider.addChangeListener(this);
    scaleXSlider.setBounds(5, 20, 135, 50);
    scalePanel.add(scaleXSlider);

    scaleYSlider = new JSlider(10, 500, scaleY);
    scaleYSlider.setFont(JDUtils.labelFont);
    scaleYSlider.setMinorTickSpacing(20);
    scaleYSlider.setMajorTickSpacing(100);
    scaleYSlider.setPaintTicks(true);
    scaleYSlider.setPaintLabels(false);
    scaleYSlider.addChangeListener(this);
    scaleYSlider.setBounds(5, 70, 135, 50);
    scalePanel.add(scaleYSlider);

    scaleXLabel = new JLabel("H scale [%]");
    scaleXLabel.setFont(JDUtils.labelFont);
    scaleXLabel.setHorizontalAlignment(JLabel.RIGHT);
    scaleXLabel.setForeground(JDUtils.labelColor);
    scaleXLabel.setBounds(140, 30, 75, 24);
    scalePanel.add(scaleXLabel);

    scaleXText = new JTextField();
    scaleXText.setEditable(true);
    scaleXText.setFont(JDUtils.labelFont);
    scaleXText.setBounds(220, 30, 40, 24);
    scaleXText.addActionListener(this);
    scalePanel.add(scaleXText);

    scaleYLabel = new JLabel("V scale [%]");
    scaleYLabel.setHorizontalAlignment(JLabel.RIGHT);
    scaleYLabel.setFont(JDUtils.labelFont);
    scaleYLabel.setForeground(JDUtils.labelColor);
    scaleYLabel.setBounds(140, 80, 75, 24);
    scalePanel.add(scaleYLabel);

    scaleYText = new JTextField();
    scaleYText.setEditable(true);
    scaleYText.setFont(JDUtils.labelFont);
    scaleYText.setBounds(220, 80, 40, 24);
    scaleYText.addActionListener(this);
    scalePanel.add(scaleYText);

    scaleRatioCheckBox = new JCheckBox("Preserve ratio");
    scaleRatioCheckBox.setFont(JDUtils.labelFont);
    scaleRatioCheckBox.setForeground(JDUtils.labelColor);
    scaleRatioCheckBox.setBounds(5, 120, 150, 25);
    scaleRatioCheckBox.setSelected(true);
    scaleRatioCheckBox.addActionListener(this);
    scalePanel.add(scaleRatioCheckBox);

    add(scalePanel);

    // ------------------------------------------------------------------------------------
    JPanel rotatePanel = new JPanel(null);
    rotatePanel.setBorder(JDUtils.createTitleBorder("Rotate"));
    rotatePanel.setBounds(5, 260, 270, 60);

    rot90CheckBox = new JCheckBox("90deg");
    rot90CheckBox.setFont(JDUtils.labelFont);
    rot90CheckBox.setForeground(JDUtils.labelColor);
    rot90CheckBox.setBounds(5,20,80,24);
    rot90CheckBox.addActionListener(this);
    rotatePanel.add(rot90CheckBox);

    rot180CheckBox = new JCheckBox("180deg");
    rot180CheckBox.setFont(JDUtils.labelFont);
    rot180CheckBox.setForeground(JDUtils.labelColor);
    rot180CheckBox.setBounds(85,20,90,24);
    rot180CheckBox.addActionListener(this);
    rotatePanel.add(rot180CheckBox);

    rot270CheckBox = new JCheckBox("270deg");
    rot270CheckBox.setFont(JDUtils.labelFont);
    rot270CheckBox.setForeground(JDUtils.labelColor);
    rot270CheckBox.setBounds(175,20,88,24);
    rot270CheckBox.addActionListener(this);
    rotatePanel.add(rot270CheckBox);

    add(rotatePanel);

    resetTransformBtn = new JButton("Reset transfom");
    resetTransformBtn.setMargin(new Insets(0, 0, 0, 0));
    resetTransformBtn.setFont(JDUtils.labelFont);
    resetTransformBtn.addActionListener(this);
    resetTransformBtn.setBounds(7, 325, 120, 24);
    add(resetTransformBtn);

    updateControls();
  }

  private void updateControls() {
    transXText.setText(Integer.toString(transX));
    transYText.setText(Integer.toString(transY));
    scaleXText.setText(Integer.toString(scaleX));
    scaleYText.setText(Integer.toString(scaleY));
    scaleXSlider.setValue(scaleX);
    scaleYSlider.setValue(scaleY);
    rot90CheckBox.setSelected(angle==90);
    rot180CheckBox.setSelected(angle==180);
    rot270CheckBox.setSelected(angle==270);
  }

  private void initTransform() {
    if (!transformInited) {
      for(int i=0;i<allObjects.length;i++) allObjects[i].saveTransform();
      transformInited = true;
    }
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

  private void updateTransform() {

    int i;
    for (i = 0; i < allObjects.length; i++) {
      allObjects[i].restoreTransform();
      allObjects[i].scaleTranslate(org.x, org.y, (double) (scaleX) / 100.0, (double) (scaleY) / 100.0, transX, transY);
      for(int a=0;a<angle;a+=90)
        allObjects[i].rotate90(org.x, org.y);
    }

  }

  // ---------------------------------------------------------
  // Action listener
  // ---------------------------------------------------------
  public void actionPerformed(ActionEvent e) {
    initRepaint();
    initTransform();
    Object src = e.getSource();

    if (src == upBtn) {
      transY--;
      JDUtils.modified = true;
    } else if (src == downBtn) {
      transY++;
      JDUtils.modified = true;
    } else if (src == leftBtn) {
      transX--;
      JDUtils.modified = true;
    } else if (src == rightBtn) {
      transX++;
      JDUtils.modified = true;
    } else if (src == transXText) {
      try {
        transX = Integer.parseInt(transXText.getText());
        JDUtils.modified = true;
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Invalid number", "Error", JOptionPane.ERROR_MESSAGE);
      }
    } else if (src == transYText) {
      try {
        transY = Integer.parseInt(transYText.getText());
        JDUtils.modified = true;
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Invalid number", "Error", JOptionPane.ERROR_MESSAGE);
      }
    } else if (src == scaleXText) {
      try {
        scaleX = Integer.parseInt(scaleXText.getText());
        JDUtils.modified = true;
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Invalid number", "Error", JOptionPane.ERROR_MESSAGE);
      }
    } else if (src == scaleYText) {
      try {
        scaleY = Integer.parseInt(scaleYText.getText());
        JDUtils.modified = true;
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Invalid number", "Error", JOptionPane.ERROR_MESSAGE);
      }
    } else if (src == rot90CheckBox) {
      angle=90;
      JDUtils.modified = true;
    } else if (src == rot180CheckBox) {
      angle=180;
      JDUtils.modified = true;
    } else if (src == rot270CheckBox) {
      angle=270;
      JDUtils.modified = true;
    } else if (src == resetTransformBtn) {
      scaleX = 100;
      scaleY = 100;
      transX = 0;
      transY = 0;
      angle=0;
    }

    updateTransform();
    repaintObjects();
    updateControls();
  }

  // ---------------------------------------------------------
  //Change listener
  // ---------------------------------------------------------
  public void stateChanged(ChangeEvent e) {

    initRepaint();
    initTransform();
    Object src = e.getSource();

    if (src == scaleXSlider) {
      scaleX = scaleXSlider.getValue();
      if (scaleRatioCheckBox.isSelected()) scaleY = scaleX;
      JDUtils.modified = true;
    } else if (src == scaleYSlider) {
      scaleY = scaleYSlider.getValue();
      if (scaleRatioCheckBox.isSelected()) scaleX = scaleY;
      JDUtils.modified = true;
    }

    updateTransform();
    repaintObjects();
    updateControls();
  }


}
