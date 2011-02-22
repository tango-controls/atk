/**
 * A set of class to handle a network editor and its viewer.
 *
 * Author: Jean Luc PONS
 * Date: Jul 1, 2004
 * (c) ESRF 2004
 */

package fr.esrf.tangoatk.widget.util.interlock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/** NetObject edition dialog. */
public class NetObjectDlg extends JDialog implements ActionListener {

  // Local declaration
  private JPanel generalPanel;

  private JPanel extPanel;
  private JLabel[] extLabel;
  private JTextField[] extText;

  private JPanel labelPanel;
  private JScrollPane labelScroll;
  private JTextArea labelText;
  private JLabel justifyLabel;
  private JComboBox justifyCombo;
  private JLabel textFontLabel;
  private JComboBox textFontCombo;
  private JCheckBox textFontBold;
  private JTextField textFontSize;

  private JPanel     propPanel;
  private JLabel     shapeLabel;
  private JComboBox  shapeCombo;
  private JLabel     sizeLabel;
  private JTextField sizeText;

  private JButton closeBtn;
  private JButton applyBtn;

  private NetObject theObject;
  private NetEditor parentEditor;
  private int curY;
  private boolean modified;

  /**
   * Construct an NetObject edition dialog.
   * @param parent Parent dialog
   * @param iE Parent editor
   */
  public NetObjectDlg(JDialog parent, NetEditor iE) {
    super(parent, true);
    parentEditor = iE;
    initComponents();
  }

  /**
   * Construct an NetObject edition dialog.
   * @param parent Parent frame
   * @param iE Parent editor
   */
  public NetObjectDlg(JFrame parent, NetEditor iE) {
    super(parent, true);
    parentEditor = iE;
    initComponents();
  }

  /**
   * Display the edition dialog.
   * @param obj Object to be edited
   */
  public void editObject(NetObject obj) {

    theObject = obj;

    curY = 0;
    int sz = theObject.getExtendedParamNumber();
    extPanel.removeAll();
    if (sz > 0) {

      extLabel = new JLabel[sz];
      extText  = new JTextField[sz];
      for(int i=0;i<sz;i++) {
        extLabel[i] = NetUtils.createLabel(theObject.extParamName[i]);
        extLabel[i].setBounds(10, curY+20, 90, 25);
        extText[i] = new JTextField();
        extText[i].setEditable(true);
        extText[i].setBounds(105, curY+20, 190, 25);
        extText[i].setText(theObject.getExtendedParam(i));
        extPanel.add(extLabel[i]);
        extPanel.add(extText[i]);
        curY += 30;
      }

      curY += 30;
      extPanel.setBounds(5, 5, 305, curY);
      extPanel.setVisible(true);

    } else {

      extPanel.setVisible(false);

    }

    if( theObject.type==NetObject.OBJECT_BUBBLE ) {

      sizeText.setText(Integer.toString(theObject.getSize()));
      shapeCombo.setSelectedIndex(theObject.getShape());
      shapeCombo.setEnabled(theObject.editableShape);
      propPanel.setBounds(5, 5+curY, 305, 90);
      propPanel.setVisible(true);
      curY += 90;

    } else {

      propPanel.setVisible(false);

    }

    justifyCombo.setSelectedIndex(theObject.getJustify());
    labelText.setText(theObject.getLabel());

    if( theObject.type == NetObject.OBJECT_TEXT ) {

      labelPanel.setBounds(5, curY + 5, 305, 165);
      textFontLabel.setVisible(true);
      textFontCombo.setVisible(true);
      textFontBold.setVisible(true);
      textFontSize.setVisible(true);
      textFontCombo.setSelectedIndex(NetUtils.getIdx(theObject.getTextFont().getName()));
      textFontSize.setText(Integer.toString(theObject.getTextFont().getSize()));
      textFontBold.setSelected(theObject.getTextFont().isBold());
      curY+=30;

    } else {

      labelPanel.setBounds(5, curY + 5, 305, 135);
      textFontLabel.setVisible(false);
      textFontCombo.setVisible(false);
      textFontBold.setVisible(false);
      textFontSize.setVisible(false);

    }

    closeBtn.setBounds(208, curY + 145, 100, 25);
    applyBtn.setBounds(7, curY + 145, 100, 25);

    setTitle(theObject.getName() + " options");
    modified = false;

    NetUtils.centerDialog(this,315,175 + curY);
    setVisible(true);
  }

  /** Returns true if one or more properties have changed. */
  public boolean getModified() {
    return modified;
  }

  private void initComponents() {

    getContentPane().setLayout(null);

    // **********************************************
    // Properties panel construction
    // **********************************************

    generalPanel = new JPanel();
    generalPanel.setLayout(null);

    // ------------------------------- Extensions panel

    extPanel = new JPanel();
    extPanel.setLayout(null);
    extPanel.setBorder(NetUtils.createTitleBorder("Bubble parameters"));

    generalPanel.add(extPanel);

    // ------------------------------- Bubble Properites panel

    propPanel = new JPanel();
    propPanel.setLayout(null);
    propPanel.setBorder(NetUtils.createTitleBorder("Bubble shape"));;

    shapeLabel = NetUtils.createLabel("Shape");
    shapeLabel.setBounds(10, 20, 90, 25);
    shapeCombo = new JComboBox();
    shapeCombo.addItem("Circle");
    shapeCombo.addItem("Square");
    shapeCombo.addItem("Hexagon");
    shapeCombo.addItem("VCC symbol");
    shapeCombo.addItem("Ground symbol");
    shapeCombo.addItem("Dot");
    shapeCombo.addItem("Computer1");
    shapeCombo.addItem("Device1");
    shapeCombo.addItem("Device2");
    shapeCombo.addItem("Device3");
    shapeCombo.addItem("NetDevice1");
    shapeCombo.addItem("NetDevice2");
    shapeCombo.addItem("NetDevice3");
    shapeCombo.addItem("Printer1");
    shapeCombo.addItem("Printer2");
    shapeCombo.addItem("Printer3");
    shapeCombo.addItem("Server1");
    shapeCombo.addItem("Server2");
    shapeCombo.addItem("Storage1");
    shapeCombo.addItem("Storage2");
    shapeCombo.addItem("Storage3");
    shapeCombo.addItem("Storage4");
    shapeCombo.addItem("Storage5");
    shapeCombo.addItem("XTerm");
    shapeCombo.addActionListener(this);
    shapeCombo.setBounds(105, 20, 190, 25);

    sizeLabel = NetUtils.createLabel("Bubble size");
    sizeLabel.setBounds(10, 50, 90, 25);
    sizeText = new JTextField();
    sizeText.setEditable(true);
    sizeText.setBounds(105, 50, 190, 25);

    propPanel.add(shapeLabel);
    propPanel.add(shapeCombo);
    propPanel.add(sizeLabel);
    propPanel.add(sizeText);

    generalPanel.add(propPanel);

    // ------------------------------- label panel
    labelPanel = new JPanel();
    labelPanel.setLayout(null);
    labelPanel.setBorder(NetUtils.createTitleBorder("Label"));

    justifyLabel = NetUtils.createLabel("Justify");
    justifyLabel.setBounds(10, 100, 90, 25);
    justifyCombo = new JComboBox();
    justifyCombo.addItem("Left");
    justifyCombo.addItem("Right");
    justifyCombo.addItem("Center");
    justifyCombo.setBounds(105, 100, 190, 25);

    labelText = new JTextArea();
    labelText.setEditable(true);
    labelScroll = new JScrollPane(labelText);
    labelScroll.setBounds(10, 20, 285, 75);

    textFontLabel = NetUtils.createLabel("Text font");
    textFontLabel.setBounds(10,130,90,25);

    textFontCombo = NetUtils.createFontCombo();
    textFontCombo.setBounds(105,130,95,25);

    textFontSize = new JTextField();
    textFontSize.setEditable(true);
    textFontSize.setBounds(200,130,35,25);

    textFontBold = new JCheckBox();
    textFontBold.setFont(NetUtils.labelFont);
    textFontBold.setText("Bold");
    textFontBold.setBounds(235,130,50,25);

    labelPanel.add(labelScroll);
    labelPanel.add(justifyLabel);
    labelPanel.add(justifyCombo);
    labelPanel.add(textFontLabel);
    labelPanel.add(textFontCombo);
    labelPanel.add(textFontSize);
    labelPanel.add(textFontBold);

    generalPanel.add(labelPanel);

    // ------------------------------- general panel

    closeBtn = new JButton();
    closeBtn.setText("Dismiss");
    closeBtn.setFont(NetUtils.labelFont);
    closeBtn.addActionListener(this);
    applyBtn = new JButton();
    applyBtn.setText("Apply");
    applyBtn.setFont(NetUtils.labelFont);
    applyBtn.addActionListener(this);
    generalPanel.add(closeBtn);
    generalPanel.add(applyBtn);

    setContentPane(generalPanel);

    setResizable(false);

  }

  public void actionPerformed(ActionEvent e) {

    if (e.getSource() == closeBtn) {
      hide();
    } else if (e.getSource() == applyBtn) {

      // Extended param
      if( extPanel.isVisible() ) {
        for(int i=0;i<extLabel.length;i++)
          theObject.setExtendedParam(i,extText[i].getText());
      }

      // Label properties
      theObject.setLabel(labelText.getText());
      theObject.setJustify(justifyCombo.getSelectedIndex());

      if( theObject.type == NetObject.OBJECT_TEXT ) {

        theObject.setTextFont( new Font( textFontCombo.getSelectedItem().toString() ,
                                         textFontBold.isSelected()?Font.BOLD:Font.PLAIN ,
                                         Integer.parseInt(textFontSize.getText()) ) );

      }

      // bubble properties
      if( propPanel.isVisible() ) {
        theObject.setShape(shapeCombo.getSelectedIndex());
        try {
          theObject.setSize(Integer.parseInt(sizeText.getText()));
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(this,"Invalid size value\n" + ex.getMessage());
        }
      }

      modified = true;
      parentEditor.repaint();

    }

  }


}
