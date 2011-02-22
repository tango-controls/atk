/** A panel for JDLabel private properties */
package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class JDLabelPanel extends JPanel implements ActionListener, DocumentListener {

  private JScrollPane textView;
  private JTextArea textText;

  private JLabel fontLabel;
  private JButton fontBtn;

  private JLabel alignmentLabel;
  private JComboBox alignmentCombo;

  private JLabel alignment2Label;
  private JComboBox alignment2Combo;

  private JLabel orientationLabel;
  private JComboBox orientationCombo;

  JDLabel allObjects[];
  JComponent invoker;
  Rectangle oldRect;

  public JDLabelPanel(JDLabel[] p, JComponent jc) {

    allObjects = p;
    invoker = jc;
    setLayout(null);
    setBorder(BorderFactory.createEtchedBorder());
    setPreferredSize(new Dimension(380, 210));

    // ------------------------------------------------------------------------------------
    JPanel namePanel = new JPanel(null);
    namePanel.setBorder(JDUtils.createTitleBorder("Text"));
    namePanel.setBounds(5,5,370,120);

    textText = new JTextArea();
    textText.setEditable(true);
    textText.setFont(JDUtils.labelFont);
    textText.setText(p[0].getText());
    textText.getDocument().addDocumentListener(this);
    textView = new JScrollPane(textText);
    textView.setBounds(10, 25, 350, 80);
    namePanel.add(textView);
    add(namePanel);

    // ------------------------------------------------------------------------------------
    JPanel stylePanel = new JPanel(null);
    stylePanel.setBorder(JDUtils.createTitleBorder("Text styles"));
    stylePanel.setBounds(5,130,370,150);

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

    alignmentLabel = new JLabel("Horizontal alignment");
    alignmentLabel.setFont(JDUtils.labelFont);
    alignmentLabel.setForeground(JDUtils.labelColor);
    alignmentLabel.setBounds(10, 50, 125, 25);
    stylePanel.add(alignmentLabel);

    alignmentCombo = new JComboBox();
    alignmentCombo.setFont(JDUtils.labelFont);
    alignmentCombo.addItem("Center");
    alignmentCombo.addItem("Left");
    alignmentCombo.addItem("Right");
    alignmentCombo.setSelectedIndex(p[0].getHorizontalAlignment());
    alignmentCombo.addActionListener(this);
    alignmentCombo.setBounds(220, 50, 140, 25);
    stylePanel.add(alignmentCombo);

    alignment2Label = new JLabel("Vertical alignment");
    alignment2Label.setFont(JDUtils.labelFont);
    alignment2Label.setForeground(JDUtils.labelColor);
    alignment2Label.setBounds(10, 80, 125, 25);
    stylePanel.add(alignment2Label);

    alignment2Combo = new JComboBox();
    alignment2Combo.setFont(JDUtils.labelFont);
    alignment2Combo.addItem("Center");
    alignment2Combo.addItem("Up");
    alignment2Combo.addItem("Down");
    alignment2Combo.setSelectedIndex(p[0].getHorizontalAlignment());
    alignment2Combo.addActionListener(this);
    alignment2Combo.setBounds(220, 80, 140, 25);
    stylePanel.add(alignment2Combo);

    orientationLabel = new JLabel("Text orientation");
    orientationLabel.setFont(JDUtils.labelFont);
    orientationLabel.setForeground(JDUtils.labelColor);
    orientationLabel.setBounds(10, 110, 100, 25);
    stylePanel.add(orientationLabel);

    orientationCombo = new JComboBox();
    orientationCombo.setFont(JDUtils.labelFont);
    orientationCombo.addItem("Left to right");
    orientationCombo.addItem("Bottom to top");
    orientationCombo.addItem("Left to right");
    orientationCombo.addItem("Top to bottom");
    orientationCombo.setSelectedIndex(p[0].getOrientation());
    orientationCombo.addActionListener(this);
    orientationCombo.setBounds(220, 110, 140, 25);
    stylePanel.add(orientationCombo);
    add(stylePanel);

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
    if (src == fontBtn) {
      JDFontChooser jf = new JDFontChooser((JDialog) getRootPane().getParent(), "Choose label font", allObjects[0].getFont());
      Font newFont = jf.getNewFont();
      if (newFont != null) {
        for (i = 0; i < allObjects.length; i++)
          allObjects[i].setFont(newFont);
        JDUtils.modified=true;
      }
    } else if (src == alignmentCombo) {
      for (i = 0; i < allObjects.length; i++)
        allObjects[i].setHorizontalAlignment(alignmentCombo.getSelectedIndex());
      JDUtils.modified=true;
    } else if (src == alignment2Combo) {
      for (i = 0; i < allObjects.length; i++)
        allObjects[i].setVerticalAlignment(alignment2Combo.getSelectedIndex());
      JDUtils.modified=true;
    } else if (src == orientationCombo) {
      for (i = 0; i < allObjects.length; i++)
        allObjects[i].setOrientation(orientationCombo.getSelectedIndex());
      JDUtils.modified=true;
    }
    repaintObjects();
  }

  // ---------------------------------------------------------
  // Documents listener
  // ---------------------------------------------------------
  private void updateText() {
    int i;
    initRepaint();
    for (i = 0; i < allObjects.length; i++)
      allObjects[i].setText(textText.getText());
    repaintObjects();
    JDUtils.modified=true;
  }

  public void changedUpdate(DocumentEvent e) {
    updateText();
  }

  public void insertUpdate(DocumentEvent e) {
    updateText();
  }

  public void removeUpdate(DocumentEvent e) {
    updateText();
  }


}
