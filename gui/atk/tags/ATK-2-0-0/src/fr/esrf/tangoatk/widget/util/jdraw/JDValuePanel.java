/** A panel to control the user interaction */
package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class JDValuePanel extends JPanel implements ActionListener {

  private JCheckBox userValueCheckBox;

  private JLabel     initValueLabel;
  private JTextField initValueText;
  private JLabel     minValueLabel;
  private JTextField minValueText;
  private JLabel     maxValueLabel;
  private JTextField maxValueText;

  private JLabel    userBehaviorLabel;
  private JComboBox userBehaviorCombo;

  private JCheckBox affectBackgroundCheckBox;
  private JButton   affectBackgroundBtn;
  private JCheckBox affectForegroundCheckBox;
  private JButton   affectForegroundBtn;
  private JCheckBox affectVisibleCheckBox;
  private JButton   affectVisibleBtn;
  private JCheckBox affectInvertShadowCheckBox;
  private JButton   affectInvertShadowBtn;
  private JCheckBox affectXPosCheckBox;
  private JButton   affectXPosBtn;
  private JCheckBox affectYPosCheckBox;
  private JButton   affectYPosBtn;
  private JCheckBox affectXScaleCheckBox;
  private JButton   affectXScaleBtn;
  private JCheckBox affectYScaleCheckBox;
  private JButton   affectYScaleBtn;

  JDObject[] allObjects;
  JComponent invoker;
  Rectangle oldRect;

  public JDValuePanel(JDObject[] p, JComponent jc) {

    allObjects = p;
    invoker = jc;

    setForeground(JDUtils.labelColor);
    setFont(JDUtils.labelFont);
    setLayout(null);
    setBorder(BorderFactory.createEtchedBorder());
    setPreferredSize(new Dimension(380, 280));

    // ----- User value panel

    JPanel userPanel = new JPanel(null);
    userPanel.setBorder(JDUtils.createTitleBorder("Object value"));
    userPanel.setBounds(5, 10, 370, 140);

    userValueCheckBox = JDUtils.createCheckBox("Enable user interaction (Play mode)",this);
    userValueCheckBox.setBounds(5, 20, 330, 25);
    userPanel.add(userValueCheckBox);

    initValueLabel = JDUtils.createLabel("Init");
    initValueLabel.setBounds(10, 50, 35, 25);
    userPanel.add(initValueLabel);

    initValueText = new JTextField();
    initValueText.setEditable(true);
    initValueText.setFont(JDUtils.labelFont);
    initValueText.setBounds(50, 50, 40, 24);
    initValueText.addActionListener(this);
    userPanel.add(initValueText);

    minValueLabel =JDUtils.createLabel("Minimum");
    minValueLabel.setBounds(95, 50, 70, 25);
    userPanel.add(minValueLabel);

    minValueText = new JTextField();
    minValueText.setEditable(true);
    minValueText.setFont(JDUtils.labelFont);
    minValueText.setBounds(165, 50, 40, 24);
    minValueText.addActionListener(this);
    userPanel.add(minValueText);

    maxValueLabel = JDUtils.createLabel("Maximum");
    maxValueLabel.setBounds(210, 50, 70, 25);
    userPanel.add(maxValueLabel);

    maxValueText = new JTextField();
    maxValueText.setEditable(true);
    maxValueText.setFont(JDUtils.labelFont);
    maxValueText.setBounds(280, 50, 40, 24);
    maxValueText.addActionListener(this);
    userPanel.add(maxValueText);

    userBehaviorLabel = JDUtils.createLabel("Object value change when nouse");
    userBehaviorLabel.setBounds(10, 83, 300, 20);
    userPanel.add(userBehaviorLabel);

    userBehaviorCombo = new JComboBox();
    userBehaviorCombo.setFont(JDUtils.labelFont);
    userBehaviorCombo.addItem("Clicked (value=value+1)");
    userBehaviorCombo.addItem("Pressed,Released (value=value+1)");
    userBehaviorCombo.addItem("XDragged (value=vMin,X=0 to vMax,X=W)");
    userBehaviorCombo.addItem("XDragged (value=vMax,X=0 to vMin,X=W)");
    userBehaviorCombo.addItem("YDragged (value=vMin,Y=0 to vMax,Y=H)");
    userBehaviorCombo.addItem("YDragged (value=vMax,Y=0 to vMin,Y=H)");
    userBehaviorCombo.addActionListener(this);
    userBehaviorCombo.setBounds(10,105, 350, 25);
    userPanel.add(userBehaviorCombo);

    add(userPanel);

    // ------------------------------------------------------------------------------------
    JPanel dynaPanel = new JPanel(null);
    dynaPanel.setBorder(JDUtils.createTitleBorder("Object value affetcs"));
    int curY=20;

    affectBackgroundCheckBox = JDUtils.createCheckBox("Backgound color",this);
    affectBackgroundCheckBox.setBounds(5, curY, 140, 25);
    dynaPanel.add(affectBackgroundCheckBox);
    affectBackgroundBtn = JDUtils.createSetButton(this);
    affectBackgroundBtn.setBounds(150, curY, 25, 25);
    dynaPanel.add(affectBackgroundBtn);
    curY+=25;

    affectForegroundCheckBox = JDUtils.createCheckBox("Foregound color",this);
    affectForegroundCheckBox.setBounds(5, curY, 140, 25);
    dynaPanel.add(affectForegroundCheckBox);
    affectForegroundBtn = JDUtils.createSetButton(this);
    affectForegroundBtn.setBounds(150, curY, 25, 25);
    dynaPanel.add(affectForegroundBtn);
    curY+=25;

    affectVisibleCheckBox = JDUtils.createCheckBox("Visibilty",this);
    affectVisibleCheckBox.setBounds(5, curY, 140, 25);
    dynaPanel.add(affectVisibleCheckBox);
    affectVisibleBtn = JDUtils.createSetButton(this);
    affectVisibleBtn.setBounds(150, curY, 25, 25);
    dynaPanel.add(affectVisibleBtn);
    curY+=25;

    affectInvertShadowCheckBox = JDUtils.createCheckBox("Inverse shadow",this);
    affectInvertShadowCheckBox.setBounds(5, curY, 140, 25);
    dynaPanel.add(affectInvertShadowCheckBox);
    affectInvertShadowBtn = JDUtils.createSetButton(this);
    affectInvertShadowBtn.setBounds(150, curY, 25, 25);
    dynaPanel.add(affectInvertShadowBtn);
    curY=20;

    affectXPosCheckBox = JDUtils.createCheckBox("Horizontal position",this);
    affectXPosCheckBox.setBounds(190, curY, 140, 25);
    dynaPanel.add(affectXPosCheckBox);
    affectXPosBtn = JDUtils.createSetButton(this);
    affectXPosBtn.setBounds(335, curY, 25, 25);
    dynaPanel.add(affectXPosBtn);
    curY+=25;

    affectYPosCheckBox = JDUtils.createCheckBox("Vertical position",this);
    affectYPosCheckBox.setBounds(190, curY, 140, 25);
    dynaPanel.add(affectYPosCheckBox);
    affectYPosBtn = JDUtils.createSetButton(this);
    affectYPosBtn.setBounds(335, curY, 25, 25);
    dynaPanel.add(affectYPosBtn);
    curY+=25;

    affectXScaleCheckBox = JDUtils.createCheckBox("Horizontal scale",this);
    affectXScaleCheckBox.setBounds(190, curY, 140, 25);
    dynaPanel.add(affectXScaleCheckBox);
    affectXScaleBtn = JDUtils.createSetButton(this);
    affectXScaleBtn.setBounds(335, curY, 25, 25);
    dynaPanel.add(affectXScaleBtn);
    curY+=25;

    affectYScaleCheckBox = JDUtils.createCheckBox("Vertical scale",this);
    affectYScaleCheckBox.setBounds(190, curY, 140, 25);
    dynaPanel.add(affectYScaleCheckBox);
    affectYScaleBtn = JDUtils.createSetButton(this);
    affectYScaleBtn.setBounds(335, curY, 25, 25);
    dynaPanel.add(affectYScaleBtn);
    curY+=25;

    add(dynaPanel);
    dynaPanel.setBounds(5, 155, 370, curY+10);

    refreshControls();

  }

  private void refreshControls() {
    minValueText.setText(Integer.toString(allObjects[0].getMinValue()));
    minValueText.setCaretPosition(0);
    maxValueText.setText(Integer.toString(allObjects[0].getMaxValue()));
    maxValueText.setCaretPosition(0);
    initValueText.setText(Integer.toString(allObjects[0].getInitValue()));
    initValueText.setCaretPosition(0);
    boolean isEnabled = allObjects[0].isInteractive();
    userValueCheckBox.setSelected(isEnabled);
    userBehaviorCombo.setSelectedIndex(allObjects[0].getValueChangeMode());
    userBehaviorCombo.setEnabled(isEnabled);

    // Mappers
    affectBackgroundCheckBox.setSelected(allObjects[0].hasBackgroundMapper());
    affectBackgroundBtn.setEnabled(allObjects[0].hasBackgroundMapper());
    affectForegroundCheckBox.setSelected(allObjects[0].hasForegroundMapper());
    affectForegroundBtn.setEnabled(allObjects[0].hasForegroundMapper());
    affectVisibleCheckBox.setSelected(allObjects[0].hasVisibilityMapper());
    affectVisibleBtn.setEnabled(allObjects[0].hasVisibilityMapper());
    affectInvertShadowCheckBox.setSelected(allObjects[0].hasInvertShadowMapper());
    affectInvertShadowBtn.setEnabled(allObjects[0].hasInvertShadowMapper());
    affectInvertShadowCheckBox.setSelected(allObjects[0].hasInvertShadowMapper());
    affectInvertShadowBtn.setEnabled(allObjects[0].hasInvertShadowMapper());
    affectXPosCheckBox.setSelected(allObjects[0].hasHTranslationMapper());
    affectXPosBtn.setEnabled(allObjects[0].hasHTranslationMapper());
    affectYPosCheckBox.setSelected(allObjects[0].hasVTranslationMapper());
    affectYPosBtn.setEnabled(allObjects[0].hasVTranslationMapper());

    // Not yet used
    affectXScaleCheckBox.setEnabled(false);
    affectXScaleBtn.setEnabled(false);
    affectYScaleCheckBox.setEnabled(false);
    affectYScaleBtn.setEnabled(false);

  }
  // --------------------------------------------------------
  private void editBackgroundMapper() {
    JDValueProgram bm = JDUtils.showValueMappingDialog(this,allObjects,"Background color",
                                                      JDValueProgram.COLOR_TYPE,allObjects[0].getBackgroundMapper());
    if(bm!=null) setBackgroundMapper(bm);
    else         refreshControls();
  }

  private void setBackgroundMapper(JDValueProgram bm) {
    int i;
    if(bm==null)
      for(i=0;i<allObjects.length;i++) allObjects[i].setBackgroundMapper(null);
    else
      for(i=0;i<allObjects.length;i++) allObjects[i].setBackgroundMapper(bm.copy());
    JDUtils.modified=true;
    refreshControls();
  }
  // --------------------------------------------------------
  private void editForegroundMapper() {
    JDValueProgram bm = JDUtils.showValueMappingDialog(this,allObjects,"Foreground color",
                                                      JDValueProgram.COLOR_TYPE,allObjects[0].getForegroundMapper());
    if(bm!=null) setForegroundMapper(bm);
    else         refreshControls();
  }

  private void setForegroundMapper(JDValueProgram bm) {
    int i;
    if(bm==null)
      for(i=0;i<allObjects.length;i++) allObjects[i].setForegroundMapper(null);
    else
      for(i=0;i<allObjects.length;i++) allObjects[i].setForegroundMapper(bm.copy());
    JDUtils.modified=true;
    refreshControls();
  }
  // --------------------------------------------------------
  private void editVisibilityMapper() {
    JDValueProgram bm = JDUtils.showValueMappingDialog(this,allObjects,"Visibility",
                                                      JDValueProgram.BOOLEAN_TYPE,allObjects[0].getVisibilityMapper());
    if(bm!=null) setVisibilityMapper(bm);
    else         refreshControls();
  }

  private void setVisibilityMapper(JDValueProgram bm) {
    int i;
    if(bm==null)
      for(i=0;i<allObjects.length;i++) allObjects[i].setVisibilityMapper(null);
    else
      for(i=0;i<allObjects.length;i++) allObjects[i].setVisibilityMapper(bm.copy());
    JDUtils.modified=true;
    refreshControls();
  }
  // --------------------------------------------------------
  private void editInvertShadowMapper() {
    JDValueProgram bm = JDUtils.showValueMappingDialog(this,allObjects,"Invert shadow",
                                                      JDValueProgram.BOOLEAN_TYPE,allObjects[0].getInvertShadowMapper());
    if(bm!=null) setInvertShadowMapper(bm);
    else         refreshControls();
  }

  private void setInvertShadowMapper(JDValueProgram bm) {
    int i;
    if(bm==null)
      for(i=0;i<allObjects.length;i++) allObjects[i].setInvertShadowMapper(null);
    else
      for(i=0;i<allObjects.length;i++) allObjects[i].setInvertShadowMapper(bm.copy());
    JDUtils.modified=true;
    refreshControls();
  }
  // --------------------------------------------------------
  private void editHTranslationMapper() {
    JDValueProgram bm = JDUtils.showValueMappingDialog(this,allObjects,"Horizontal Translation",
                                                      JDValueProgram.INTEGER_TYPE,allObjects[0].getHTranslationMapper());
    if(bm!=null) setHTranslationMapper(bm);
    else         refreshControls();
  }

  private void setHTranslationMapper(JDValueProgram bm) {
    int i;
    if(bm==null)
      for(i=0;i<allObjects.length;i++) allObjects[i].setHTranslationMapper(null);
    else
      for(i=0;i<allObjects.length;i++) allObjects[i].setHTranslationMapper(bm.copy());
    JDUtils.modified=true;
    refreshControls();
  }
  // --------------------------------------------------------
  private void editVTranslationMapper() {
    JDValueProgram bm = JDUtils.showValueMappingDialog(this,allObjects,"Vertical Translation",
                                                      JDValueProgram.INTEGER_TYPE,allObjects[0].getVTranslationMapper());
    if(bm!=null) setVTranslationMapper(bm);
    else         refreshControls();
  }

  private void setVTranslationMapper(JDValueProgram bm) {
    int i;
    if(bm==null)
      for(i=0;i<allObjects.length;i++) allObjects[i].setVTranslationMapper(null);
    else
      for(i=0;i<allObjects.length;i++) allObjects[i].setVTranslationMapper(bm.copy());
    JDUtils.modified=true;
    refreshControls();
  }

  // ---------------------------------------------------------
  // Action listener
  // ---------------------------------------------------------
  public void actionPerformed(ActionEvent e) {
    int i,m;
    Object src = e.getSource();
    if( src == minValueText ) {
      try {
        m = Integer.parseInt( minValueText.getText() );
        for(i=0;i<allObjects.length;i++) allObjects[i].setMinValue(m);
        JDUtils.modified=true;
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,"Invalid syntax for min value");
      }
      refreshControls();
    } else if( src == maxValueText ) {
      try {
        m = Integer.parseInt( maxValueText.getText() );
        for(i=0;i<allObjects.length;i++) allObjects[i].setMaxValue(m);
        JDUtils.modified=true;
        refreshControls();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,"Invalid syntax for max value");
      }
      refreshControls();
    } else if( src == initValueText ) {
      try {
        m = Integer.parseInt( initValueText.getText() );
        for(i=0;i<allObjects.length;i++) allObjects[i].setInitValue(m);
        JDUtils.modified=true;
        refreshControls();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,"Invalid syntax for init value");
      }
      refreshControls();
    } else  if( src==userValueCheckBox ) {
      for(i=0;i<allObjects.length;i++) allObjects[i].setInteractive(userValueCheckBox.isSelected());
      refreshControls();
      JDUtils.modified = true;
    } else if ( src==userBehaviorCombo ) {
      int s = userBehaviorCombo.getSelectedIndex();
      if(s>=0) {
        for(i=0;i<allObjects.length;i++) allObjects[i].setValueChangeMode(s);
        JDUtils.modified = true;
      }
    } else if( src == affectBackgroundBtn ) {

      editBackgroundMapper();

    } else if( src==affectBackgroundCheckBox ) {

      if(allObjects[0].hasBackgroundMapper())
        setBackgroundMapper(null);
      else
        editBackgroundMapper();

    } else if( src == affectForegroundBtn ) {

      editForegroundMapper();

    } else if( src==affectForegroundCheckBox ) {

      if(allObjects[0].hasForegroundMapper())
        setForegroundMapper(null);
      else
        editForegroundMapper();

    } else if( src == affectVisibleBtn ) {

      editVisibilityMapper();

    } else if( src==affectVisibleCheckBox ) {

      if(allObjects[0].hasVisibilityMapper())
        setVisibilityMapper(null);
      else
        editVisibilityMapper();

    } else if( src == affectInvertShadowBtn ) {

      editInvertShadowMapper();

    } else if( src==affectInvertShadowCheckBox ) {

      if(allObjects[0].hasInvertShadowMapper())
        setInvertShadowMapper(null);
      else
        editInvertShadowMapper();

    } else if( src == affectXPosBtn ) {

      editHTranslationMapper();

    } else if( src==affectXPosCheckBox ) {

      if(allObjects[0].hasHTranslationMapper())
        setHTranslationMapper(null);
      else
        editHTranslationMapper();

    } else if( src == affectYPosBtn ) {

      editVTranslationMapper();

    } else if( src==affectYPosCheckBox ) {

      if(allObjects[0].hasVTranslationMapper())
        setVTranslationMapper(null);
      else
        editVTranslationMapper();

    }


  }



}
