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
 
package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.JAutoScrolledText;
import fr.esrf.Tango.AttrWriteType;
import fr.esrf.Tango.DispLevel;

import fr.esrf.tangoatk.core.attribute.ANumber;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class for display/edit attribute property
 *
 * @author  pons
 */

public class SimplePropertyFrame extends JDialog {

  // Frame components
  private JPanel identPanel;
  private JLabel deviceLabel;
  private JTextField deviceText;
  private JLabel attLabel;
  private JTextField attText;

  private SimpleScalarViewer          numberAndStringValue = null;
  private BooleanScalarCheckBoxViewer booleanValue         = null;
  private NumberScalarWheelEditor     numberSetter         = null;
  private StringScalarEditor          stringSetter         = null;
  private BooleanScalarComboEditor    booleanSetter        = null;

  private JPanel propPanel;

  private JLabel nameLabel;
  private JTextField nameText;

  private JLabel minLabel;
  private JTextField minText;

  private JLabel maxLabel;
  private JTextField maxText;

  private JLabel alminLabel;
  private JTextField alminText;

  private JLabel almaxLabel;
  private JTextField almaxText;

  private JLabel minWarningLabel;
  private JTextField minWarningText;

  private JLabel maxWarningLabel;
  private JTextField maxWarningText;

  private JLabel deltaTLabel;
  private JTextField deltaTText;

  private JLabel deltaValLabel;
  private JTextField deltaValText;

  private JLabel formatLabel;
  private JTextField formatText;

  private JLabel unitLabel;
  private JTextField unitText;

  private JScrollPane textView;
  private JTextArea descText;

  private JButton okButton;
  private JButton applyButton;
  private JButton infoButton;

  private boolean editable = true;
  private IAttribute model;  // Handle to the property map

  Dimension appsize = new Dimension(460, 490);

  // Construction
  public SimplePropertyFrame() {
    super((JFrame) null, false);
    initComponents();
  }

  public SimplePropertyFrame(JDialog parent, boolean modal) {
    super(parent, modal);
    initComponents();
  }

  public SimplePropertyFrame(JFrame parent, boolean modal) {
    super(parent, modal);
    initComponents();
  }

  public void pack() {
    // Do nothing on pack
  }

  private void initComponents() {

    Container pane = getContentPane();
    pane.setLayout(null);

    addComponentListener(new ComponentAdapter() {
      public void componentHidden(ComponentEvent evt) {
        System.out.println("Free ref");
        setModel(null);
        dispose();
      }
    });

    Font labelFont = new Font("Dialog", Font.PLAIN, 11);
    Insets noMargin = new Insets(0, 0, 0, 0);

    identPanel = new JPanel();
    identPanel.setLayout(null);
    identPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Identification", TitledBorder.LEFT, TitledBorder.TOP, labelFont));

    deviceLabel = new JLabel("Device");
    identPanel.add(deviceLabel);
    deviceText = new JTextField();
    deviceText.setMargin(noMargin);
    deviceText.setEditable(false);
    identPanel.add(deviceText);

    attLabel = new JLabel("Attribute");
    identPanel.add(attLabel);
    attText = new JTextField();
    attText.setMargin(noMargin);
    attText.setEditable(false);
    identPanel.add(attText);
    pane.add(identPanel);

    numberAndStringValue = new SimpleScalarViewer();
    numberAndStringValue.setFont(new Font("Dialog", Font.BOLD, 30));
    numberAndStringValue.setBorder(BorderFactory.createLoweredBevelBorder());
    numberAndStringValue.setSizingBehavior(JAutoScrolledText.MATRIX_BEHAVIOR);

    booleanValue = new BooleanScalarCheckBoxViewer();
    booleanValue.setQualityEnabled(true);
    booleanValue.setTrueLabel("True");
    booleanValue.setFalseLabel("False");
    booleanValue.setOpaque(true);
    booleanValue.setFont(new Font("Dialog", Font.BOLD, 30));
    booleanValue.setBorder(BorderFactory.createLoweredBevelBorder());

    pane.add(numberAndStringValue);

    numberSetter = new NumberScalarWheelEditor();
    numberSetter.setFont(new Font("Dialog", Font.BOLD, 20));
    numberSetter.setVisible(false);

    stringSetter = new StringScalarEditor();
    stringSetter.setFont(new Font("Dialog", java.awt.Font.PLAIN, 14));
    stringSetter.setVisible(false);

    booleanSetter = new BooleanScalarComboEditor();
    booleanSetter.setFont(new Font("Dialog", java.awt.Font.PLAIN, 14));
    booleanSetter.setVisible(false);

    pane.add(numberSetter);

    propPanel = new JPanel();
    propPanel.setLayout(null);
    propPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Properties", TitledBorder.LEFT, TitledBorder.TOP, labelFont));

    nameLabel = new JLabel("Label");
    propPanel.add(nameLabel);
    nameText = new JTextField();
    nameText.setMargin(noMargin);
    nameText.setEditable(true);
    propPanel.add(nameText);

    minLabel = new JLabel("Minimum value");
    propPanel.add(minLabel);
    minText = new JTextField();
    minText.setMargin(noMargin);
    minText.setEditable(true);
    propPanel.add(minText);

    maxLabel = new JLabel("Maximum value");
    propPanel.add(maxLabel);
    maxText = new JTextField();
    maxText.setMargin(noMargin);
    maxText.setEditable(true);
    propPanel.add(maxText);

    alminLabel = new JLabel("Minimum alarm");
    propPanel.add(alminLabel);
    alminText = new JTextField();
    alminText.setMargin(noMargin);
    alminText.setEditable(true);
    propPanel.add(alminText);

    almaxLabel = new JLabel("Maximum alarm");
    propPanel.add(almaxLabel);
    almaxText = new JTextField();
    almaxText.setMargin(noMargin);
    almaxText.setEditable(true);
    propPanel.add(almaxText);

    minWarningLabel = new JLabel("Min. warning");
    propPanel.add(minWarningLabel);
    minWarningText = new JTextField();
    minWarningText.setMargin(noMargin);
    minWarningText.setEditable(true);
    minWarningText.setEnabled(false);
    propPanel.add(minWarningText);

    maxWarningLabel = new JLabel("Max. warning");
    propPanel.add(maxWarningLabel);
    maxWarningText = new JTextField();
    maxWarningText.setMargin(noMargin);
    maxWarningText.setEditable(true);
    maxWarningText.setEnabled(false);
    propPanel.add(maxWarningText);

    deltaTLabel = new JLabel("Delta t(ms)");
    propPanel.add(deltaTLabel);
    deltaTText = new JTextField();
    deltaTText.setMargin(noMargin);
    deltaTText.setEditable(true);
    deltaTText.setEnabled(false);
    propPanel.add(deltaTText);

    deltaValLabel = new JLabel("Delta Val");
    propPanel.add(deltaValLabel);
    deltaValText = new JTextField();
    deltaValText.setMargin(noMargin);
    deltaValText.setEditable(true);
    deltaValText.setEnabled(false);
    propPanel.add(deltaValText);

    formatLabel = new JLabel("Format");
    propPanel.add(formatLabel);
    formatText = new JTextField();
    formatText.setMargin(noMargin);
    formatText.setEditable(true);
    propPanel.add(formatText);

    unitLabel = new JLabel("Unit");
    propPanel.add(unitLabel);
    unitText = new JTextField();
    unitText.setMargin(noMargin);
    unitText.setEditable(true);
    propPanel.add(unitText);

    descText = new JTextArea();
    descText.setEditable(true);
    textView = new JScrollPane(descText);
    propPanel.add(textView);

    pane.add(propPanel);

    okButton = new JButton("Dismiss");
    okButton.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        setVisible(false);
        dispose();
      }
    });

    infoButton = new JButton("Information");
    infoButton.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        showInformation();
      }
    });

    applyButton = new JButton("Apply change");
    applyButton.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        // Apply resources
        applyChange();
      }
    });

    pane.add(applyButton);
    pane.add(infoButton);
    pane.add(okButton);

    setTitle("Attribute property editor");
    placeComponents();

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension scrsize = toolkit.getScreenSize();
    int x = (scrsize.width - appsize.width) / 2;
    int y = (scrsize.height - appsize.height) / 2;
    setBounds(x, y, appsize.width, appsize.height);

  }

  public boolean propertyReset(String s) {
    return s.equalsIgnoreCase("Not specified") || s.equals("") || s.equalsIgnoreCase("NaN");
  }

  // Apply resource change
  public void applyChange() {
    Map pmap = model.getPropertyMap();
    Property p;

    // Update label
    p = (Property) pmap.get("label");
    p.setValue(nameText.getText());
    p.refresh();

    if (model instanceof ANumber)
    {
    	ANumber aNbModel = (ANumber) model;
    	java.util.HashMap<String, JTextField> properties = new java.util.HashMap<String, JTextField>();

    	properties.put("min_value", minText);
    	properties.put("max_value", maxText);
    	properties.put("min_alarm", alminText);
    	properties.put("max_alarm", almaxText);
    	properties.put("min_warning", minWarningText);
    	properties.put("max_warning", maxWarningText);
    	properties.put("delta_t", deltaTText);
    	properties.put("delta_val", deltaValText);

    	for(Entry<String, JTextField> entry : properties.entrySet()) {
    		p = (Property) pmap.get(entry.getKey());

    		//if the property is really in the property map we update it
    		if( p != null){
    			String v = entry.getValue().getText();
    			updateProperty(p, aNbModel, v, entry.getKey());
    		}
    	}
    }

    // Update format
    p = (Property) pmap.get("format");
    p.setValue(formatText.getText());
    p.refresh();

    // Update unit
    p = (Property) pmap.get("unit");
    p.setValue(unitText.getText());
    p.refresh();

    // Update description
    p = (Property) pmap.get("description");
    p.setValue(descText.getText());
    p.refresh();

    //Commit change
    model.storeConfig();
    updateComponents();

  }

private void updateProperty(Property p, ANumber aNbModel, String v, String desc) {
	if (propertyReset(v)) {
        p.setValue("NaN");
      } else {
        try {
          Double d = new Double( aNbModel.getValueInDeviceUnit(Double.parseDouble(v)) );
          p.setValue(d);
          p.refresh();
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(this, "Invalid "+desc+" value\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
}


  // Update components according to the model
  public void updateComponents() {

    if (model == null)
      return;

    Container pane = getContentPane();
    pane.remove(1);
    deviceText.setText(model.getDevice().getName());
    attText.setText(model.getNameSansDevice());

    if (model instanceof INumberScalar) {
      numberAndStringValue.setModel((INumberScalar) model);
      numberAndStringValue.setVisible(true);
      booleanValue.clearModel();
      booleanValue.setVisible(false);
      pane.add(numberAndStringValue, 1);
    } else if (model instanceof IStringScalar) {
      numberAndStringValue.setModel((IStringScalar) model);
      numberAndStringValue.setVisible(true);
      booleanValue.clearModel();
      booleanValue.setVisible(false);
      pane.add(numberAndStringValue, 1);
    } else {
      numberAndStringValue.clearModel();
      numberAndStringValue.setText("");
      numberAndStringValue.setVisible(false);
      if (model instanceof IBooleanScalar) {
        booleanValue.setAttModel((IBooleanScalar)model);
        booleanValue.setVisible(true);
        pane.add(booleanValue, 1);
      }
      else {
        booleanValue.clearModel();
        booleanValue.setVisible(false);
        pane.add(numberAndStringValue, 1);
      }
    }
    if ( pane.getComponent(2) == numberSetter
            || pane.getComponent(2) == stringSetter
            || pane.getComponent(2) == booleanSetter) {
        pane.remove(2);
    }

    if ( model.isWritable() ) {
      if ( model instanceof INumberScalar ) {
        numberSetter.setModel((INumberScalar) model);
        numberSetter.setVisible(true);
        stringSetter.setModel(null);
        stringSetter.setVisible(false);
        booleanSetter.setAttModel(null);
        booleanSetter.setVisible(false);
        pane.add( numberSetter, 2 );
      }
      else if ( model instanceof IStringScalar ) {
        numberSetter.setModel(null);
        numberSetter.setVisible(false);
        stringSetter.setModel((IStringScalar) model);
        stringSetter.setVisible(true);
        booleanSetter.setAttModel(null);
        booleanSetter.setVisible(false);
        pane.add( stringSetter, 2 );
      }
      else if ( model instanceof IBooleanScalar ) {
        numberSetter.setModel(null);
        numberSetter.setVisible(false);
        stringSetter.setModel(null);
        stringSetter.setVisible(false);
        booleanSetter.setAttModel((IBooleanScalar) model);
        booleanSetter.setVisible(true);
        pane.add( booleanSetter, 2 );
      }
      else {
        numberSetter.setModel(null);
        numberSetter.setVisible(false);
        stringSetter.setModel(null);
        stringSetter.setVisible(false);
        booleanSetter.setAttModel(null);
        booleanSetter.setVisible(false);
      }
    } else {
      numberSetter.setModel(null);
      numberSetter.setVisible(false);
      stringSetter.setModel(null);
      stringSetter.setVisible(false);
      booleanSetter.setAttModel(null);
      booleanSetter.setVisible(false);
    }
    pane.repaint();

    nameText.setText(model.getLabel());

    if (model instanceof ANumber) {
      double v;
      ANumber m = (ANumber) model;

      v = m.getMinValue();
      if( Double.isNaN(v) ) minText.setText("Not specified");
      else                  minText.setText(Double.toString(m.getValueInDisplayUnit(v)));

      v = m.getMaxValue();
      if (Double.isNaN(v))   maxText.setText("Not specified");
      else                   maxText.setText(Double.toString(m.getValueInDisplayUnit(v)));

      v = m.getMinAlarm();
      if (Double.isNaN(v))   alminText.setText("Not specified");
      else                   alminText.setText(Double.toString(m.getValueInDisplayUnit(v)));

      v = m.getMaxAlarm();
      if (Double.isNaN(v))   almaxText.setText("Not specified");
      else                   almaxText.setText(Double.toString(m.getValueInDisplayUnit(v)));

      v = m.getMinWarning();
      if (Double.isNaN(v))   minWarningText.setText("Not specified");
      else                   minWarningText.setText(Double.toString(m.getValueInDisplayUnit(v)));

      v = m.getMaxWarning();
      if (Double.isNaN(v))   maxWarningText.setText("Not specified");
      else                   maxWarningText.setText(Double.toString(m.getValueInDisplayUnit(v)));

      v = m.getDeltaT();
      if (Double.isNaN(v))   deltaTText.setText("Not specified");
      else                   deltaTText.setText(Double.toString(v));

      v = m.getDeltaVal();
      if (Double.isNaN(v))   deltaValText.setText("Not specified");
      else                   deltaValText.setText(Double.toString(m.getValueInDisplayUnit(v)));

    } else {
      minText.setText("None");
      maxText.setText("None");
      alminText.setText("None");
      almaxText.setText("None");
      minWarningText.setText("None");
      maxWarningText.setText("None");
      deltaTText.setText("None");
      deltaValText.setText("None");
    }

    formatText.setText(model.getFormat());
    unitText.setText(model.getUnit());
    descText.setText(model.getDescription());

  }

  // Places the components
  public void placeComponents() {

    // Id Panel
    identPanel.setBounds(5, 5, 440, 80);
    deviceLabel.setBounds(10, 15, 65, 25);
    deviceText.setBounds(80, 15, 345, 25);
    attLabel.setBounds(10, 45, 65, 25);
    attText.setBounds(80, 45, 345, 25);

    // Value
    if (numberAndStringValue.isVisible()) {
      if ( (!numberSetter.isVisible())
              && (!stringSetter.isVisible()) ) {
        numberAndStringValue.setBounds(5, 87, 440, 45);
      } else {
        int w = (int) numberSetter.getPreferredSize().getWidth();
        numberAndStringValue.setBounds(5, 87, 435 - w, 45);
        numberSetter.setBounds(5 + 435 - w, 87, w, 45);
        stringSetter.setBounds(5 + 435 - w, 87, w, 45);
      }
      propPanel.setBounds(5, 135, 440, 280);

    } else if (booleanValue.isVisible()) {
      if ( (!booleanSetter.isVisible()) ) {
          booleanValue.setBounds(5, 87, 440, 45);
      } else {
        int w = (int) booleanSetter.getPreferredSize().getWidth();
        booleanValue.setBounds(5, 87, 435 - w, 45);
        booleanSetter.setBounds(5 + 435 - w, 87, w, 45);
      }
      propPanel.setBounds(5, 135, 440, 280);
    } else {

      propPanel.setBounds(5, 85, 440, 280);

    }


    // Prop panel
    nameLabel.setBounds(10, 15, 110, 25);
    nameText.setBounds(120, 15, 310, 25);

    minLabel.setBounds(10, 45, 110, 25);
    minText.setBounds(120, 45, 100, 25);

    maxLabel.setBounds(225, 45, 110, 25);
    maxText.setBounds(330, 45, 100, 25);

    alminLabel.setBounds(10, 75, 110, 25);
    alminText.setBounds(120, 75, 100, 25);

    almaxLabel.setBounds(225, 75, 110, 25);
    almaxText.setBounds(330, 75, 100, 25);

    formatLabel.setBounds(10, 105, 110, 25);
    formatText.setBounds(120, 105, 100, 25);

    unitLabel.setBounds(225, 105, 110, 25);
    unitText.setBounds(330, 105, 100, 25);

    minWarningLabel.setBounds(10, 135, 110, 25);
    minWarningText.setBounds(120, 135, 100, 25);

    maxWarningLabel.setBounds(225, 135, 110, 25);
    maxWarningText.setBounds(330, 135, 100, 25);

    deltaTLabel.setBounds(10, 165, 110, 25);
    deltaTText.setBounds(120, 165, 100, 25);

    deltaValLabel.setBounds(225, 165, 110, 25);
    deltaValText.setBounds(330, 165, 100, 25);

    textView.setBounds(10, 195, 425, 80);


    // Buttons
    if (numberAndStringValue.isVisible() || booleanValue.isVisible()) {
      applyButton.setBounds(5, 420, 120, 30);
      infoButton.setBounds(160, 420, 120, 30);
      okButton.setBounds(325, 420, 120, 30);
      setSize(appsize);
    } else {
      applyButton.setBounds(5, 370, 120, 30);
      infoButton.setBounds(160, 370, 120, 30);
      okButton.setBounds(325, 370, 120, 30);
      Dimension d = new Dimension(appsize);
      d.height -= 50;
      setSize(d);
    }

    textView.revalidate();
  }

  // Sets the model
  public void setModel(IAttribute m) {
    //System.out.println("setModel( " + model.getName() + ")");
    model = m;
    if (model != null) {
      updateComponents();
      placeComponents();
      model.refresh();
    }
  }

  // Makes all properties editable or not
  public void setEditable(boolean b) {
    editable = b;
    nameText.setEditable(b);
    minText.setEditable(b);
    maxText.setEditable(b);
    alminText.setEditable(b);
    almaxText.setEditable(b);
    formatText.setEditable(b);
    unitText.setEditable(b);
    descText.setEditable(b);
    applyButton.setEnabled(b);
  }

  // Returns if the property frame is editable
  public boolean isEditable() {
    return editable;
  }

  // Display the information text
  private void showInformation() {

    String msg = "Attribute: " + model.getName() + "\n\n";
    msg += "Type: " + model.getType() + "\n";

    AttrWriteType wt = (AttrWriteType) model.getProperty("writable").getValue();
    switch (wt.value()) {
      case AttrWriteType._READ:
        msg += "Writable: READ\n";
        break;
      case AttrWriteType._READ_WITH_WRITE:
        msg += "Writable: READ_WITH_WRITE\n";
        break;
      case AttrWriteType._READ_WRITE:
        msg += "Writable: READ_WRITE\n";
        break;
      case AttrWriteType._WRITE:
        msg += "Writable: WRITE\n";
        break;
    }

    DispLevel dl = (DispLevel) model.getProperty("level").getValue();
    switch (dl.value()) {
      case DispLevel._EXPERT:
        msg += "Display level: EXPERT\n";
        break;
      case DispLevel._OPERATOR:
        msg += "Display level: OPERATOR\n";
        break;
    }

    msg += "Display unit factor: " + model.getDisplayUnitFactor() + "\n";
    msg += "Standart unit factor: " + model.getStandardUnitFactor() + "\n";
    msg += "Writable attribute: " + model.getProperty("writable_attr_name").getValue().toString() + "\n";
    msg += "Max X dimension: " + model.getMaxXDimension() + "\n";
    msg += "Max Y dimension: " + model.getMaxYDimension() + "\n";
    JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);

  }

  // Main function
  static public void main(String[] args) {
    final SimplePropertyFrame pf = new SimplePropertyFrame();

    fr.esrf.tangoatk.core.AttributeList attributeList = new
      fr.esrf.tangoatk.core.AttributeList();

    try {
      pf.setModel((IAttribute) attributeList.add("jlp/test/1/att_un"));
    } catch (Exception e) {
      System.out.println("attributeList.add() failed with " + e.getMessage());
      e.printStackTrace();
    }

    pf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pf.setVisible(true);
  }

}
