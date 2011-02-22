package fr.esrf.tangoatk.widget.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.*;


/**
 *  A font chooser dialog box.<p>
 * <pre>
 *  Font defFont = new Font("Dialog", Font.BOLD, 12);
 *  Font newFont = ATKFontChooser.getNewFont(this,"Choose font",defFont);
 *  if (newFont != null) {
 *     ....
 *  }
 * </pre>
 */
public class ATKFontChooser extends JDialog implements ActionListener, ListSelectionListener, ChangeListener {

  private JPanel innerPanel;
  private Font currentFont;
  private int fontSize;
  Font result;
  private String[] allFamily;

  private DefaultListModel listModel;
  private JScrollPane familyView;
  private JList familyList;

  private JPanel infoPanel;

  private JCheckBox plainCheck;
  private JCheckBox boldCheck;
  private JCheckBox italicCheck;
  private JCheckBox italicboldCheck;

  private JTextField sizeText;
  private JLabel sizeLabel;
  private JSlider sizeSlider;

  private JSmoothLabel sampleLabel;

  private JButton okBtn;
  private JButton cancelBtn;

  private static Font  labelFontBold  = new Font("Dialog", Font.BOLD, 12);
  private static Color labelColor = new Color(85, 87, 140);


  // -----------------------------------------------
  // Contruction
  // -----------------------------------------------
  ATKFontChooser(Frame parent, String title, Font initialFont) {
    super(parent, true);
    initComponents(title, initialFont);
  }

  ATKFontChooser(Dialog parent, String title, Font initialFont) {
    super(parent, true);
    initComponents(title, initialFont);
  }

  private void initComponents(String title, Font initialFont) {

    innerPanel = new JPanel();
    innerPanel.setLayout(null);

    if (initialFont == null)
      currentFont = ATKConstant.labelFont;
    else
      currentFont = initialFont;

    fontSize = currentFont.getSize();
    result = currentFont;

    // Get all available family font
    allFamily = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

    // Create Gui

    listModel = new DefaultListModel();
    familyList = new JList(listModel);
    familyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    familyList.setFont(ATKConstant.labelFont);

    for (int i = 0; i < allFamily.length; i++)
      listModel.addElement(allFamily[i]);

    familyView = new JScrollPane(familyList);
    familyView.setBorder(BorderFactory.createLoweredBevelBorder());

    infoPanel = new JPanel();
    infoPanel.setLayout(null);
    infoPanel.setBorder(createTitleBorder("Size and Style"));

    plainCheck = new JCheckBox("Plain");
    plainCheck.setForeground(labelColor);
    plainCheck.setFont(ATKConstant.labelFont);
    plainCheck.addActionListener(this);


    boldCheck = new JCheckBox("Bold");
    boldCheck.setForeground(labelColor);
    boldCheck.setFont(ATKConstant.labelFont);
    boldCheck.addActionListener(this);


    italicCheck = new JCheckBox("Italic");
    italicCheck.setForeground(labelColor);
    italicCheck.setFont(ATKConstant.labelFont);
    italicCheck.addActionListener(this);

    italicboldCheck = new JCheckBox("Bold italic");
    italicboldCheck.setForeground(labelColor);
    italicboldCheck.setFont(ATKConstant.labelFont);
    italicboldCheck.addActionListener(this);

    sizeText = new JTextField();
    sizeText.addActionListener(this);
    sizeText.setEditable(true);

    sizeLabel = new JLabel("Size");
    sizeLabel.setFont(ATKConstant.labelFont);
    sizeLabel.setForeground(labelColor);

    sizeSlider = new JSlider(5, 72, fontSize);
    sizeSlider.setMinorTickSpacing(1);
    sizeSlider.setMajorTickSpacing(5);
    sizeSlider.setPaintTicks(true);
    sizeSlider.setPaintLabels(true);

    sizeSlider.addChangeListener(this);

    infoPanel.add(plainCheck);
    infoPanel.add(italicCheck);
    infoPanel.add(italicboldCheck);
    infoPanel.add(boldCheck);
    infoPanel.add(sizeLabel);
    infoPanel.add(sizeText);
    infoPanel.add(sizeSlider);

    plainCheck.setBounds(5, 20, 100, 25);
    italicCheck.setBounds(5, 45, 100, 25);
    boldCheck.setBounds(5, 70, 100, 25);
    italicboldCheck.setBounds(5, 95, 100, 25);
    sizeLabel.setBounds(130, 35, 80, 25);
    sizeText.setBounds(130, 60, 80, 25);
    sizeSlider.setBounds(5, 125, 240, 45);

    okBtn = new JButton("Apply");
    okBtn.setFont(ATKConstant.labelFont);
    okBtn.addActionListener(this);

    cancelBtn = new JButton("Cancel");
    cancelBtn.setFont(ATKConstant.labelFont);
    cancelBtn.addActionListener(this);

    sampleLabel = new JSmoothLabel();
    sampleLabel.setText("Sample 12.34");
    sampleLabel.setBorder(BorderFactory.createLoweredBevelBorder());
    sampleLabel.setBackground(new Color(220, 220, 220));

    // Add and size
    innerPanel.add(familyView);
    innerPanel.add(infoPanel);
    innerPanel.add(okBtn);
    innerPanel.add(cancelBtn);
    innerPanel.add(sampleLabel);

    infoPanel.setBounds(178, 5, 254, 175);
    familyView.setBounds(5, 5, 170, 300);
    okBtn.setBounds(180, 280, 80, 25);
    cancelBtn.setBounds(350, 280, 80, 25);
    sampleLabel.setBounds(180, 185, 250, 90);

    setTitle(title);
    updateControl();

    //Select the currentFont
    int selid = familyList.getNextMatch(currentFont.getFamily(), 0,
                                        Position.Bias.Forward);

    if (selid != -1) {
      familyList.setSelectedIndex(selid);
      familyList.ensureIndexIsVisible(selid);
    }

    familyList.addListSelectionListener(this);

    innerPanel.setPreferredSize(new Dimension(435, 312));
    setContentPane(innerPanel);
    setResizable(false);

  }

  // Update control according to the current font.
  private void updateControl() {

    plainCheck.setSelected(currentFont.isPlain());
    boldCheck.setSelected(currentFont.isBold() && !currentFont.isItalic());
    italicCheck.setSelected(currentFont.isItalic() && !currentFont.isBold());
    italicboldCheck.setSelected(currentFont.isBold() && currentFont.isItalic());

    sampleLabel.setFont(currentFont);
    sizeText.setText(Integer.toString(fontSize));

  }

  // -----------------------------------------------
  // ActionListener
  // -----------------------------------------------
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == cancelBtn) {
      result = null;
      setVisible(false);
      dispose();
    } else if (e.getSource() == okBtn) {
      result = currentFont;
      setVisible(false);
      dispose();
    } else if (e.getSource() == plainCheck) {
      Font newFont = new Font(currentFont.getFamily(), Font.PLAIN, fontSize);
      if (newFont != null) {
        currentFont = newFont;
        updateControl();
      }
    } else if (e.getSource() == boldCheck) {
      Font newFont = new Font(currentFont.getFamily(), Font.BOLD, fontSize);
      if (newFont != null) {
        currentFont = newFont;
        updateControl();
      }

    } else if (e.getSource() == italicCheck) {
      Font newFont = new Font(currentFont.getFamily(), Font.ITALIC, fontSize);
      if (newFont != null) {
        currentFont = newFont;
        updateControl();
      }
    } else if (e.getSource() == italicboldCheck) {
      Font newFont = new Font(currentFont.getFamily(), Font.ITALIC + Font.BOLD, fontSize);
      if (newFont != null) {
        currentFont = newFont;
        updateControl();
      }
    } else if (e.getSource()==sizeText) {
      try {
        int nSize = Integer.parseInt(sizeText.getText());
        sizeSlider.setValue(nSize);
      } catch (NumberFormatException ex) {
        JOptionPane.showConfirmDialog(this,"Wrong integer format\n" + ex.getMessage());
      }
    }

  }

  // -----------------------------------------------
  // SelectionListListener
  // -----------------------------------------------
  public void valueChanged(ListSelectionEvent e) {

    if (e.getSource() == familyList) {

      String fName = (String) listModel.get(familyList.getSelectedIndex());

      Font newFont = new Font(fName, Font.PLAIN, fontSize);
      if (newFont != null) {
        currentFont = newFont;
        updateControl();
      }

    }

  }

  // -----------------------------------------------
  // Change listener
  // -----------------------------------------------
  public void stateChanged(ChangeEvent e) {

    if (e.getSource() == sizeSlider) {
      fontSize = sizeSlider.getValue();
      Font newFont = currentFont.deriveFont((float) fontSize);
      if (newFont != null) {
        currentFont = newFont;
        updateControl();
      }
    }

  }

  /**
   * Display the Font chooser dialog.
   * @param parent Parent component
   * @param dlgTitle Dialog title
   * @param defaultFont Default font (can be null)
   * @return A handle to a new Font, null when canceled.
    */
  static public Font getNewFont(Component parent,String dlgTitle,Font defaultFont) {

    Window pWindow = ATKGraphicsUtils.getWindowForComponent(parent);
    ATKFontChooser dlg;
    if(pWindow instanceof Dialog) {
      dlg = new ATKFontChooser((Dialog)pWindow,dlgTitle,defaultFont);
    } else if(pWindow instanceof Frame) {
      dlg = new ATKFontChooser((Frame)pWindow,dlgTitle,defaultFont);
    } else {
      dlg = new ATKFontChooser((Frame)null,dlgTitle,defaultFont);
    }
    ATKGraphicsUtils.centerDialog(dlg);
    dlg.setVisible(true);
    return dlg.result;

  }

  private Border createTitleBorder(String name) {
    return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), name,
                                            TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION,
                                            labelFontBold, labelColor);
  }

  static public void main(String[] args) {
    getNewFont(null,"Choose font",null);
    System.exit(0);
  }


}