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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Global option dialog. */
public class NetEditorDlg extends JDialog implements ActionListener {

  private NetEditor parentEditor;

  JPanel innerPanel;

  JPanel     fontPanel;
  JLabel     fNameLabel;
  JLabel     fSizeLabel;
  JLabel     fBoldLabel;
  JLabel     labelFontLabel;
  JLabel     smallFontLabel;
  JComboBox  labelFontCombo;
  JComboBox  smallFontCombo;
  JTextField labelFontSize;
  JTextField smallFontSize;
  JCheckBox  labelFontBold;
  JCheckBox  smallFontBold;

  JPanel     miscPanel;
  JCheckBox  useAAFont;
  JCheckBox  drawArrow;

  JButton    applyBtn;
  JButton    closeBtn;

  /**
   * Construct an NetEditorDlg.
   * @param parent Parent dialog
   * @param iE Parent editor
   */
  public NetEditorDlg(JDialog parent, NetEditor iE) {
    super(parent, true);
    parentEditor = iE;
    initComponents();
  }

  /**
   * Construct an NetEditorDlg.
   * @param parent Parent frame
   * @param iE Parent editor
   */
  public NetEditorDlg(JFrame parent, NetEditor iE) {
    super(parent, true);
    parentEditor = iE;
    initComponents();
  }

  /** Display the global option dialog. */
  public void showOption() {

    labelFontCombo.setSelectedIndex(NetUtils.getIdx(parentEditor.labelFont.getName()));
    labelFontSize.setText(Integer.toString(parentEditor.labelFont.getSize()));
    labelFontBold.setSelected(parentEditor.labelFont.isBold());

    smallFontCombo.setSelectedIndex(NetUtils.getIdx(parentEditor.smallFont.getName()));
    smallFontSize.setText(Integer.toString(parentEditor.smallFont.getSize()));
    smallFontBold.setSelected(parentEditor.smallFont.isBold());

    useAAFont.setSelected(parentEditor.getAntialiasFont());
    drawArrow.setSelected(parentEditor.isShowingArrow());

    NetUtils.centerDialog(this,310,235);
    setVisible(true);
  }

  private void initComponents() {

    innerPanel = new JPanel();
    innerPanel.setLayout(null);
    setContentPane(innerPanel);
    setTitle("Editor Preferences");

    fontPanel = new JPanel();
    fontPanel.setLayout(null);
    fontPanel.setBorder(NetUtils.createTitleBorder("Bubble Fonts"));

    fNameLabel = NetUtils.createLabel("Name");
    fNameLabel.setHorizontalAlignment(JLabel.CENTER);
    fNameLabel.setBounds(100,10,100,25);
    fontPanel.add(fNameLabel);

    fSizeLabel = NetUtils.createLabel("Size");
    fSizeLabel.setHorizontalAlignment(JLabel.CENTER);
    fSizeLabel.setBounds(200,10,50,25);
    fontPanel.add(fSizeLabel);

    fBoldLabel = NetUtils.createLabel("Bold");
    fBoldLabel.setHorizontalAlignment(JLabel.CENTER);
    fBoldLabel.setBounds(250,10,40,25);
    fontPanel.add(fBoldLabel);

    // -------------------------------------
    labelFontLabel = NetUtils.createLabel("Label font");
    labelFontLabel.setBounds(10,30,100,25);
    fontPanel.add(labelFontLabel);

    labelFontCombo = NetUtils.createFontCombo();
    labelFontCombo.setBounds(100,30,100,25);
    fontPanel.add(labelFontCombo);

    labelFontSize = new JTextField();
    labelFontSize.setEditable(true);
    labelFontSize.setBounds(200,30,50,25);
    fontPanel.add(labelFontSize);

    labelFontBold = new JCheckBox();
    labelFontBold.setText("");
    labelFontBold.setBounds(260,30,20,25);
    fontPanel.add(labelFontBold);

    // -------------------------------------
    smallFontLabel = NetUtils.createLabel("Small font");
    smallFontLabel.setBounds(10,60,100,25);
    fontPanel.add(smallFontLabel);

    smallFontCombo = NetUtils.createFontCombo();
    smallFontCombo.setBounds(100,60,100,25);
    fontPanel.add(smallFontCombo);

    smallFontSize = new JTextField();
    smallFontSize.setEditable(true);
    smallFontSize.setBounds(200,60,50,25);
    fontPanel.add(smallFontSize);

    smallFontBold = new JCheckBox();
    smallFontBold.setText("");
    smallFontBold.setBounds(260,60,20,25);
    fontPanel.add(smallFontBold);

    miscPanel = new JPanel();
    miscPanel.setLayout(null);
    miscPanel.setBorder(NetUtils.createTitleBorder("Miscellaneous"));

    useAAFont = new JCheckBox();
    useAAFont.setFont(NetUtils.labelFont);
    useAAFont.setForeground(NetUtils.fColor);
    useAAFont.setText("Use Anti-Aliased fonts");
    useAAFont.setBounds(10,20,260,25);
    miscPanel.add(useAAFont);

    drawArrow = new JCheckBox();
    drawArrow.setFont(NetUtils.labelFont);
    drawArrow.setForeground(NetUtils.fColor);
    drawArrow.setText("Draw arrow with link");
    drawArrow.setBounds(10,50,260,25);
    miscPanel.add(drawArrow);

    applyBtn = new JButton("Apply");
    applyBtn.setBounds(7,205,100,25);
    applyBtn.setFont(NetUtils.labelFont);
    applyBtn.addActionListener(this);
    innerPanel.add(applyBtn);

    closeBtn = new JButton("Dismiss");
    closeBtn.setBounds(203,205,100,25);
    closeBtn.setFont(NetUtils.labelFont);
    closeBtn.addActionListener(this);
    innerPanel.add(closeBtn);

    fontPanel.setBounds(5,10,300,100);
    innerPanel.add(fontPanel);
    miscPanel.setBounds(5,115,300,85);
    innerPanel.add(miscPanel);

  }

  public void actionPerformed(ActionEvent e) {

    Object src = e.getSource();

    if (src == closeBtn) {
      hide();
    } else if (src == applyBtn) {

      parentEditor.labelFont = new Font( labelFontCombo.getSelectedItem().toString() ,
                                         labelFontBold.isSelected()?Font.BOLD:Font.PLAIN ,
                                         Integer.parseInt(labelFontSize.getText()) );

      parentEditor.smallFont = new Font( smallFontCombo.getSelectedItem().toString() ,
                                         smallFontBold.isSelected()?Font.BOLD:Font.PLAIN ,
                                         Integer.parseInt(smallFontSize.getText()) );

      parentEditor.setAntialiasFont(useAAFont.isSelected());
      parentEditor.setShowArrow(drawArrow.isSelected());

      parentEditor.repaint();

    }

  }


}
