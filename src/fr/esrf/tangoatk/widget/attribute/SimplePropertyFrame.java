package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.attribute.NumberImage;
import fr.esrf.tangoatk.widget.util.JAutoScrolledText;
import fr.esrf.Tango.AttrWriteType;
import fr.esrf.Tango.DispLevel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;
import java.util.Map;

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

  private SimpleScalarViewer theValue = null;
  private NumberScalarWheelEditor theSetter = null;

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

    theValue = new SimpleScalarViewer();
    theValue.setFont(new Font("Dialog", Font.BOLD, 30));
    theValue.setBorder(BorderFactory.createLoweredBevelBorder());
    theValue.setSizingBehavior(JAutoScrolledText.MATRIX_BEHAVIOR);
    pane.add(theValue);

    theSetter = new NumberScalarWheelEditor();
    theSetter.setFont(new Font("Dialog", Font.BOLD, 20));
    theSetter.setVisible(false);
    pane.add(theSetter);

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
        hide();
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

  // Apply resource change
  public void applyChange() {
    Map pmap = model.getPropertyMap();
    Property p;

    // Update label
    p = (Property) pmap.get("label");
    p.setValue(nameText.getText());
    p.refresh();

    if (model instanceof INumberImage) {
      // Update min
      p = (Property) pmap.get("min_value");
      String v = minText.getText();
      if (v.equalsIgnoreCase("Not specified")) {
        p.setValue(v);
      } else {
        try {
          Double d = new Double(v);
          p.setValue(d);
          p.refresh();
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(this, "Invalid minimum value\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }

      // Update max
      p = (Property) pmap.get("max_value");
      v = maxText.getText();
      if (v.equalsIgnoreCase("Not specified")) {
        p.setValue(v);
      } else {
        try {
          Double d = new Double(v);
          p.setValue(d);
          p.refresh();
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(this, "Invalid maximum value\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }

      // Update almin
      p = (Property) pmap.get("min_alarm");
      v = alminText.getText();
      if (v.equalsIgnoreCase("Not specified")) {
        p.setValue(v);
      } else {
        try {
          Double d = new Double(v);
          p.setValue(d);
          p.refresh();
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(this, "Invalid minimum alarm\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }

      // Update almax
      p = (Property) pmap.get("max_alarm");
      v = almaxText.getText();
      if (v.equalsIgnoreCase("Not specified")) {
        p.setValue(v);
      } else {
        try {
          Double d = new Double(v);
          p.setValue(d);
          p.refresh();
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(this, "Invalid maximum alarm\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
    placeComponents();

  }

  // Update components according to the model
  public void updateComponents() {

    if (model == null)
      return;

    deviceText.setText(model.getDevice().getName());
    attText.setText(model.getNameSansDevice());

    if (model instanceof INumberScalar) {
      theValue.setModel((INumberScalar) model);
      theValue.setVisible(true);
    } else if (model instanceof IStringScalar) {
      theValue.setModel((IStringScalar) model);
      theValue.setVisible(true);
    } else {
      theValue.clearModel();
      theValue.setText("");
      theValue.setVisible(false);
    }

    if (model.isWritable() && model instanceof INumberScalar) {
      theSetter.setModel((INumberScalar) model);
      theSetter.setVisible(true);
    } else {
      theSetter.setModel(null);
      theSetter.setVisible(false);
    }

    nameText.setText(model.getLabel());

    if (model instanceof NumberImage) {
      double v;
      NumberImage m = (NumberImage) model;

      v = m.getMinValue();
      if( Double.isNaN(v) ) minText.setText("Not specified");
      else                  minText.setText(Double.toString(v));

      v = m.getMaxValue();
      if (Double.isNaN(v))   maxText.setText("Not specified");
      else                   maxText.setText(Double.toString(v));

      v = m.getMinAlarm();
      if (Double.isNaN(v))   alminText.setText("Not specified");
      else                   alminText.setText(Double.toString(v));

      v = m.getMaxAlarm();
      if (Double.isNaN(v))   almaxText.setText("Not specified");
      else                   almaxText.setText(Double.toString(v));

    } else {
      minText.setText("None");
      maxText.setText("None");
      alminText.setText("None");
      almaxText.setText("None");
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
    if (theValue.isVisible()) {
      if (!theSetter.isVisible()) {
        theValue.setBounds(5, 87, 440, 45);
      } else {
        int w = (int) theSetter.getPreferredSize().getWidth();
        theValue.setBounds(5, 87, 435 - w, 45);
        theSetter.setBounds(5 + 435 - w, 87, w, 45);
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

    textView.setBounds(10, 135, 425, 140);


    // Buttons
    if (theValue.isVisible()) {
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

    msg += "Standart unit factor: " + model.getStandardUnit() + "\n";
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
      pf.setModel((IAttribute) attributeList.add("//orion:10000/sys/machstat/tango/sig_current"));
    } catch (Exception e) {
      System.out.println("attributeList.add() failed with " + e.getMessage());
      e.printStackTrace();
    }

    pf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pf.setVisible(true);
  }

}
