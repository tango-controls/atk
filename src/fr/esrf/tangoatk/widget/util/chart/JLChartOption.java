//
// JLChartOption.java
// Description: A Class to handle 2D graphics plot
//
// JL Pons (c)ESRF 2002

package fr.esrf.tangoatk.widget.util.chart;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

class AxisPanel extends JPanel implements ActionListener, KeyListener {

  private JLAxis  pAxis;
  private JLChart pChart;
  int     type;

  private JPanel scalePanel;
  private JPanel settingPanel;

  private JLabel MinLabel;
  private JTextField MinText;
  private JLabel MaxLabel;
  private JTextField MaxText;
  private JCheckBox AutoScaleCheck;

  private JLabel ScaleLabel;
  private JComboBox ScaleCombo;
  private JCheckBox SubGridCheck;
  private JCheckBox VisibleCheck;

  private JComboBox FormatCombo;
  private JLabel FormatLabel;

  private JLabel TitleLabel;
  private JTextField TitleText;

  private JLabel ColorLabel;
  private JLabel ColorView;
  private JButton ColorBtn;

  private JLabel PositionLabel;
  private JComboBox PositionCombo;

  final static int Y1_TYPE = 1;
  final static int Y2_TYPE = 2;
  final static int X_TYPE  = 3;

  AxisPanel(JLAxis a,int axisType,JLChart parentChart)  {

    pAxis  = a;
    pChart = parentChart;
    type   = axisType;
    setLayout(null);

    scalePanel = new JPanel();
    scalePanel.setLayout(null);
    scalePanel.setBorder(GraphicsUtils.createTitleBorder("Scale"));

    settingPanel = new JPanel();
    settingPanel.setLayout(null);
    settingPanel.setBorder(GraphicsUtils.createTitleBorder("Axis settings"));

    MinLabel = new JLabel("Min");
    MinLabel.setFont(GraphicsUtils.labelFont);
    MinText = new JTextField();
    MinLabel.setForeground(GraphicsUtils.fColor);
    MinLabel.setEnabled(!a.isAutoScale());
    MinText.setText(Double.toString(a.getMinimum()));
    MinText.setEditable(true);
    MinText.setEnabled(!a.isAutoScale());
    MinText.addKeyListener(this);

    MaxLabel = new JLabel("Max");
    MaxLabel.setFont(GraphicsUtils.labelFont);
    MaxText = new JTextField();
    MaxLabel.setForeground(GraphicsUtils.fColor);
    MaxLabel.setHorizontalAlignment(JLabel.RIGHT);
    MaxLabel.setEnabled(!a.isAutoScale());
    MaxText.setText(Double.toString(a.getMaximum()));
    MaxText.setEditable(true);
    MaxText.setEnabled(!a.isAutoScale());
    MaxText.addKeyListener(this);

    AutoScaleCheck = new JCheckBox("Auto scale");
    AutoScaleCheck.setFont(GraphicsUtils.labelFont);
    AutoScaleCheck.setForeground(GraphicsUtils.fColor);
    AutoScaleCheck.setSelected(a.isAutoScale());
    AutoScaleCheck.addActionListener(this);

    ScaleLabel = new JLabel("Mode");
    ScaleLabel.setFont(GraphicsUtils.labelFont);
    ScaleLabel.setForeground(GraphicsUtils.fColor);
    ScaleCombo = new JComboBox();
    ScaleCombo.setFont(GraphicsUtils.labelFont);
    ScaleCombo.addItem("Linear");
    ScaleCombo.addItem("Logarithmic");
    ScaleCombo.setSelectedIndex(a.getScale());
    ScaleCombo.addActionListener(this);

    SubGridCheck = new JCheckBox("Show sub grid");
    SubGridCheck.setFont(GraphicsUtils.labelFont);
    SubGridCheck.setForeground(GraphicsUtils.fColor);
    SubGridCheck.setSelected(a.isSubGridVisible());
    SubGridCheck.setToolTipText("You have to select the grid in the general option panel");
    SubGridCheck.addActionListener(this);

    VisibleCheck = new JCheckBox("Visible");
    VisibleCheck.setFont(GraphicsUtils.labelFont);
    VisibleCheck.setForeground(GraphicsUtils.fColor);
    VisibleCheck.setSelected(a.isVisible());
    VisibleCheck.setToolTipText("Display/Hide the axis");
    VisibleCheck.addActionListener(this);

    FormatCombo = new JComboBox();
    FormatCombo.setFont(GraphicsUtils.labelFont);
    FormatCombo.addItem("Automatic");
    FormatCombo.addItem("Scientific");
    FormatCombo.addItem("Time (hh:mm:ss)");
    FormatCombo.addItem("Decimal int");
    FormatCombo.addItem("Hexadecimal int");
    FormatCombo.addItem("Binary int");
    FormatCombo.setSelectedIndex(a.getLabelFormat());
    FormatCombo.addActionListener(this);

    FormatLabel = new JLabel("Label format");
    FormatLabel.setFont(GraphicsUtils.labelFont);
    FormatLabel.setForeground(GraphicsUtils.fColor);

    TitleLabel = new JLabel("Title");
    TitleLabel.setFont(GraphicsUtils.labelFont);
    TitleLabel.setForeground(GraphicsUtils.fColor);
    TitleText = new JTextField();
    TitleText.setEditable(true);
    TitleText.setText(a.getName());
    TitleText.addKeyListener(this);

    ColorLabel = new JLabel("Color");
    ColorLabel.setFont(GraphicsUtils.labelFont);
    ColorLabel.setForeground(GraphicsUtils.fColor);
    ColorView = new JLabel("");
    ColorView.setOpaque(true);
    ColorView.setBorder(BorderFactory.createLineBorder(Color.black));
    ColorView.setBackground(a.getAxisColor());
    ColorBtn = new JButton("...");
    ColorBtn.addActionListener(this);
    ColorBtn.setMargin(GraphicsUtils.zInset);

    PositionLabel = new JLabel("Position");
    PositionLabel.setFont(GraphicsUtils.labelFont);
    PositionLabel.setForeground(GraphicsUtils.fColor);
    PositionCombo = new JComboBox();
    PositionCombo.setFont(GraphicsUtils.labelFont);
    switch(type) {

      case X_TYPE:
        PositionCombo.addItem("Down");
        PositionCombo.addItem("Up");
        PositionCombo.addItem("Y1 Origin");
        PositionCombo.addItem("Y2 Origin");
        PositionCombo.setSelectedIndex(a.getPosition()-1);
        break;

      case Y1_TYPE:
        PositionCombo.addItem("Left");
        PositionCombo.addItem("X Origin");
        PositionCombo.setSelectedIndex((a.getPosition() == JLAxis.VERTICAL_ORG) ? 1 : 0);
        break;

      case Y2_TYPE:
        PositionCombo.addItem("Right");
        PositionCombo.addItem("X Origin");
        PositionCombo.setSelectedIndex((a.getPosition() == JLAxis.VERTICAL_ORG) ? 1 : 0);
        break;

    }
    PositionCombo.addActionListener(this);

    scalePanel.add(MinLabel);
    scalePanel.add(MinText);
    scalePanel.add(MaxLabel);
    scalePanel.add(MaxText);
    scalePanel.add(AutoScaleCheck);
    scalePanel.add(ScaleLabel);
    scalePanel.add(ScaleCombo);
    add(scalePanel);

    settingPanel.add(SubGridCheck);
    settingPanel.add(VisibleCheck);
    settingPanel.add(FormatCombo);
    settingPanel.add(FormatLabel);
    settingPanel.add(TitleLabel);
    settingPanel.add(TitleText);
    settingPanel.add(ColorLabel);
    settingPanel.add(ColorView);
    settingPanel.add(ColorBtn);
    settingPanel.add(PositionLabel);
    settingPanel.add(PositionCombo);
    add(settingPanel);

    MinLabel.setBounds(10, 20, 35, 25);
    MinText.setBounds(50, 20, 90, 25);
    MaxLabel.setBounds(145, 20, 40, 25);
    MaxText.setBounds(190, 20, 90, 25);
    ScaleLabel.setBounds(10, 50, 100, 25);
    ScaleCombo.setBounds(115, 50, 165, 25);
    AutoScaleCheck.setBounds(5, 80, 275, 25);
    scalePanel.setBounds(5,10,290,115);

    FormatLabel.setBounds(10, 20, 100, 25);
    FormatCombo.setBounds(115, 20, 165, 25);
    TitleLabel.setBounds(10, 50, 100, 25);
    TitleText.setBounds(115, 50, 165, 25);
    ColorLabel.setBounds(10, 80, 100, 25);
    ColorView.setBounds(115, 80, 130, 25);
    ColorBtn.setBounds(250, 80, 30, 25);
    PositionLabel.setBounds(10, 110, 100, 25);
    PositionCombo.setBounds(115, 110, 165, 25);
    SubGridCheck.setBounds(5, 140, 280, 25);
    VisibleCheck.setBounds(5, 170, 280, 25);
    settingPanel.setBounds(5,130,290,205);
  }

  public void actionPerformed(ActionEvent e) {

    if (e.getSource() == AutoScaleCheck) {

      boolean b = AutoScaleCheck.isSelected();

      pAxis.setAutoScale(b);

      if (!b) {
        try {

          double min = Double.parseDouble(MinText.getText());
          double max = Double.parseDouble(MaxText.getText());

          if (max > min) {
             pAxis.setMinimum(min);
             pAxis.setMaximum(max);
          }

        } catch (NumberFormatException err) {

        }
      }

      MinLabel.setEnabled(!b);
      MinText.setEnabled(!b);
      MaxLabel.setEnabled(!b);
      MaxText.setEnabled(!b);

      Commit();

      // ------------------------------------------------------------
    } else if (e.getSource() == FormatCombo) {

      int s = FormatCombo.getSelectedIndex();
      pAxis.setLabelFormat(s);
      Commit();

      // ------------------------------------------------------------
    } else if (e.getSource() == PositionCombo) {
      int s = PositionCombo.getSelectedIndex();
      switch(type) {

        case X_TYPE:
          pAxis.setPosition(s+1);
          break;

        case Y1_TYPE:
          switch(s) {
            case 0:
              pAxis.setPosition(JLAxis.VERTICAL_LEFT);
              break;
            case 1:
              pAxis.setPosition(JLAxis.VERTICAL_ORG);
              break;
          }
          break;

        case Y2_TYPE:
          switch (s) {
            case 0:
              pAxis.setPosition(JLAxis.VERTICAL_RIGHT);
              break;
            case 1:
              pAxis.setPosition(JLAxis.VERTICAL_ORG);
              break;
          }
          break;

      }
      Commit();

    } else if (e.getSource() == ScaleCombo) {

      int s = ScaleCombo.getSelectedIndex();
      pAxis.setScale(s);
      Commit();

      // ------------------------------------------------------------
    } else if (e.getSource() == SubGridCheck) {

      pAxis.setSubGridVisible(SubGridCheck.isSelected());
      Commit();

    } else if (e.getSource() == VisibleCheck) {

      pAxis.setVisible(VisibleCheck.isSelected());
      Commit();

    } else if (e.getSource() == ColorBtn) {

      Color c = JColorChooser.showDialog(this, "Choose axis Color", pAxis.getAxisColor());
      if (c != null) {
        pAxis.setAxisColor(c);
        ColorView.setBackground(c);
        Commit();
      }

    }

  }

  public void keyPressed(KeyEvent e) {}

  public void keyTyped(KeyEvent e) {}

  public void keyReleased(KeyEvent e) {

   if ((e.getSource() == MinText || e.getSource() == MaxText) && !pAxis.isAutoScale()) {

      if (e.getKeyCode() == KeyEvent.VK_ENTER) {

        try {

          double min = Double.parseDouble(MinText.getText());
          double max = Double.parseDouble(MaxText.getText());

          if (max <= min) {
            error("Min must be strictly lower than max.");
            return;
          }

          if (pAxis.getScale() == JLAxis.LOG_SCALE) {
            if (min <= 0 || max <= 0) {
              error("Min and max must be strictly positive with logarithmic scale.");
              return;
            }
          }

          pAxis.setMinimum(min);
          pAxis.setMaximum(max);
          Commit();

        } catch (NumberFormatException err) {
          error("Min or Max: malformed number.");
        }

      }

      // ------------------------------------------------------------
    } else if (e.getSource() == TitleText) {

      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        pAxis.setName(TitleText.getText());
        Commit();
      }

    }

  }

  private void Commit() {
    if (pChart != null) pChart.repaint();
  }

  private void error(String m) {
    JOptionPane.showMessageDialog(this, m, "Chart options error",
      JOptionPane.ERROR_MESSAGE);
  }

}

/**
 * A class to display global graph settings dialog.
 * @author JL Pons
 */
public class JLChartOption extends JDialog implements ActionListener, MouseListener, ChangeListener, KeyListener {

  // Local declaration
  private JLChart chart;
  private JTabbedPane tabPane;
  private JButton closeBtn;

  // general panel
  private JPanel generalPanel;

  private JPanel gLegendPanel;

  private JLabel generalLegendLabel;
  private JTextField generalLegendText;

  private JCheckBox generalLabelVisibleCheck;

  private JPanel gColorFontPanel;

  private JLabel generalFontHeaderLabel;
  private JALabel generalFontHeaderSampleLabel;
  private JButton generalFontHeaderBtn;

  private JLabel generalFontLabelLabel;
  private JALabel generalFontLabelSampleLabel;
  private JButton generalFontLabelBtn;

  private JLabel generalBackColorLabel;
  private JLabel generalBackColorView;
  private JButton generalBackColorBtn;

  private JPanel gGridPanel;

  private JComboBox generalGridCombo;

  private JComboBox generalLabelPCombo;
  private JLabel generalLabelPLabel;

  private JComboBox generalGridStyleCombo;
  private JLabel generalGridStyleLabel;

  private JPanel gMiscPanel;

  private JLabel generalDurationLabel;
  private JTextField generalDurationText;


  // Axis panel
  private AxisPanel y1Panel;
  private AxisPanel y2Panel;
  private AxisPanel xPanel;

  //
  // parent: parent frame
  // chart:  Chart to edit
  /**
   * JLChartOption constructor.
   * @param parent Parent dialog
   * @param chart Chart to be edited.
   */
  public JLChartOption(JDialog parent, JLChart chart) {
    super(parent, false);
    this.chart = chart;
    initComponents();
  }

  /**
   * JLChartOption constructor.
   * @param parent Parent frame
   * @param chart Chart to be edited.
   */
  public JLChartOption(JFrame parent, JLChart chart) {
    super(parent, false);
    this.chart = chart;
    initComponents();
  }


  private void initComponents() {

    getContentPane().setLayout(null);


    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        hide();
        dispose();
      }
    });

    setTitle("Chart properties");

    tabPane = new JTabbedPane();

    // **********************************************
    // General panel construction
    // **********************************************

    generalPanel = new JPanel();
    generalPanel.setLayout(null);

    gLegendPanel = new JPanel();
    gLegendPanel.setLayout(null);
    gLegendPanel.setBorder(GraphicsUtils.createTitleBorder("Legends"));

    gColorFontPanel= new JPanel();
    gColorFontPanel.setLayout(null);
    gColorFontPanel.setBorder(GraphicsUtils.createTitleBorder("Colors & Fonts"));

    gGridPanel= new JPanel();
    gGridPanel.setLayout(null);
    gGridPanel.setBorder(GraphicsUtils.createTitleBorder("Axis grid"));

    gMiscPanel= new JPanel();
    gMiscPanel.setLayout(null);
    gMiscPanel.setBorder(GraphicsUtils.createTitleBorder("Misc"));

    generalLegendLabel = new JLabel("Chart title");
    generalLegendLabel.setFont(GraphicsUtils.labelFont);
    generalLegendLabel.setForeground(GraphicsUtils.fColor);
    generalLegendText = new JTextField();
    generalLegendText.setEditable(true);
    generalLegendText.setText(chart.getHeader());
    generalLegendText.addKeyListener(this);

    generalLabelVisibleCheck = new JCheckBox();
    generalLabelVisibleCheck.setFont(GraphicsUtils.labelFont);
    generalLabelVisibleCheck.setForeground(GraphicsUtils.fColor);
    generalLabelVisibleCheck.setText("Visible");
    generalLabelVisibleCheck.setSelected(chart.isLabelVisible());
    generalLabelVisibleCheck.addActionListener(this);

    generalBackColorLabel = new JLabel("Chart background");
    generalBackColorLabel.setFont(GraphicsUtils.labelFont);
    generalBackColorLabel.setForeground(GraphicsUtils.fColor);
    generalBackColorView = new JLabel("");
    generalBackColorView.setOpaque(true);
    generalBackColorView.setBorder(BorderFactory.createLineBorder(Color.black));
    generalBackColorView.setBackground(chart.getChartBackground());
    generalBackColorBtn = new JButton("...");
    generalBackColorBtn.addMouseListener(this);
    generalBackColorBtn.setMargin(GraphicsUtils.zInset);

    generalLabelPLabel = new JLabel("Placement");
    generalLabelPLabel.setHorizontalAlignment(JLabel.RIGHT);
    generalLabelPLabel.setFont(GraphicsUtils.labelFont);
    generalLabelPLabel.setForeground(GraphicsUtils.fColor);

    generalLabelPCombo = new JComboBox();
    generalLabelPCombo.setFont(GraphicsUtils.labelFont);
    generalLabelPCombo.addItem("Bottom");
    generalLabelPCombo.addItem("Top");
    generalLabelPCombo.addItem("Right");
    generalLabelPCombo.addItem("Left");
    generalLabelPCombo.setSelectedIndex(chart.getLabelPlacement());
    generalLabelPCombo.addActionListener(this);

    generalGridCombo = new JComboBox();
    generalGridCombo.setFont(GraphicsUtils.labelFont);
    generalGridCombo.addItem("None");
    generalGridCombo.addItem("On X");
    generalGridCombo.addItem("On Y1");
    generalGridCombo.addItem("On Y2");
    generalGridCombo.addItem("On X and Y1");
    generalGridCombo.addItem("On X and Y2");

    boolean vx = chart.getXAxis().isGridVisible();
    boolean vy1 = chart.getY1Axis().isGridVisible();
    boolean vy2 = chart.getY2Axis().isGridVisible();

    int sel = 0;
    if (vx && !vy1 && !vy2) sel = 1;
    if (!vx && vy1 && !vy2) sel = 2;
    if (!vx && !vy1 && vy2) sel = 3;
    if (vx && vy1 && !vy2) sel = 4;
    if (vx && !vy1 && vy2) sel = 5;

    generalGridCombo.setSelectedIndex(sel);
    generalGridCombo.addActionListener(this);

    generalGridStyleLabel = new JLabel("Style");
    generalGridStyleLabel.setFont(GraphicsUtils.labelFont);
    generalGridStyleLabel.setHorizontalAlignment(JLabel.RIGHT);
    generalGridStyleLabel.setForeground(GraphicsUtils.fColor);

    generalGridStyleCombo = new JComboBox();
    generalGridStyleCombo.setFont(GraphicsUtils.labelFont);
    generalGridStyleCombo.addItem("Solid");
    generalGridStyleCombo.addItem("Point dash");
    generalGridStyleCombo.addItem("Short dash");
    generalGridStyleCombo.addItem("Long dash");
    generalGridStyleCombo.addItem("Dot dash");
    generalGridStyleCombo.setSelectedIndex(chart.getY1Axis().getGridStyle());
    generalGridStyleCombo.addActionListener(this);

    generalDurationLabel = new JLabel("Display duration (s)");
    generalDurationLabel.setFont(GraphicsUtils.labelFont);
    generalDurationLabel.setForeground(GraphicsUtils.fColor);
    generalDurationText = new JTextField();
    generalDurationText.setEditable(true);
    generalDurationText.setToolTipText("Type Infinity to disable");
    generalDurationText.setText(Double.toString(chart.getDisplayDuration() / 1000.0));
    generalDurationText.addKeyListener(this);

    generalFontHeaderLabel = new JLabel("Header font");
    generalFontHeaderLabel.setFont(GraphicsUtils.labelFont);
    generalFontHeaderLabel.setForeground(GraphicsUtils.fColor);
    generalFontHeaderSampleLabel = new JALabel("Sample text");
    generalFontHeaderSampleLabel.setForeground(GraphicsUtils.fColor);
    generalFontHeaderSampleLabel.setOpaque(false);
    generalFontHeaderSampleLabel.setValueOffsets(0,-3);
    generalFontHeaderSampleLabel.setFont(chart.getHeaderFont());
    generalFontHeaderBtn = new JButton("...");
    generalFontHeaderBtn.addMouseListener(this);
    generalFontHeaderBtn.setMargin(GraphicsUtils.zInset);

    generalFontLabelLabel = new JLabel("Label font");
    generalFontLabelLabel.setFont(GraphicsUtils.labelFont);
    generalFontLabelLabel.setForeground(GraphicsUtils.fColor);
    generalFontLabelSampleLabel = new JALabel("Sample 0123456789");
    generalFontLabelSampleLabel.setValueOffsets(0,-3);
    generalFontLabelSampleLabel.setForeground(GraphicsUtils.fColor);
    generalFontLabelSampleLabel.setOpaque(false);
    generalFontLabelSampleLabel.setFont(chart.getXAxis().getFont());
    generalFontLabelBtn = new JButton("...");
    generalFontLabelBtn.addMouseListener(this);
    generalFontHeaderBtn.setMargin(GraphicsUtils.zInset);

    gLegendPanel.add(generalLabelVisibleCheck);
    gLegendPanel.add(generalLabelPLabel);
    gLegendPanel.add(generalLabelPCombo);
    generalPanel.add(gLegendPanel);

    gGridPanel.add(generalGridCombo);
    gGridPanel.add(generalGridStyleLabel);
    gGridPanel.add(generalGridStyleCombo);
    generalPanel.add(gGridPanel);

    gColorFontPanel.add(generalBackColorLabel);
    gColorFontPanel.add(generalBackColorView);
    gColorFontPanel.add(generalBackColorBtn);
    gColorFontPanel.add(generalFontHeaderLabel);
    gColorFontPanel.add(generalFontHeaderSampleLabel);
    gColorFontPanel.add(generalFontHeaderBtn);
    gColorFontPanel.add(generalFontLabelLabel);
    gColorFontPanel.add(generalFontLabelSampleLabel);
    gColorFontPanel.add(generalFontLabelBtn);
    generalPanel.add(gColorFontPanel);

    gMiscPanel.add(generalLegendLabel);
    gMiscPanel.add(generalLegendText);
    gMiscPanel.add(generalDurationLabel);
    gMiscPanel.add(generalDurationText);
    generalPanel.add(gMiscPanel);

    generalLabelVisibleCheck.setBounds(5, 20, 80, 25);
    generalLabelPLabel.setBounds(90, 20, 95, 25);
    generalLabelPCombo.setBounds(190, 20, 95, 25);
    gLegendPanel.setBounds(5,10,290,55);

    generalBackColorLabel.setBounds(10, 20, 140, 25);
    generalBackColorView.setBounds(155, 20, 95, 25);
    generalBackColorBtn.setBounds(255, 20, 30, 25);
    generalFontHeaderLabel.setBounds(10, 50, 90, 25);
    generalFontHeaderSampleLabel.setBounds(105, 50, 145, 25);
    generalFontHeaderBtn.setBounds(255, 50, 30, 25);
    generalFontLabelLabel.setBounds(10, 80, 90, 25);
    generalFontLabelSampleLabel.setBounds(105, 80, 145, 25);
    generalFontLabelBtn.setBounds(255, 80, 30, 25);
    gColorFontPanel.setBounds(5,70,290,115);

    generalGridCombo.setBounds(10, 20, 120, 25);
    generalGridStyleLabel.setBounds(135, 20, 45, 25);
    generalGridStyleCombo.setBounds(185, 20, 100, 25);
    gGridPanel.setBounds(5,190,290,55);

    generalLegendLabel.setBounds(10, 20, 70, 25);
    generalLegendText.setBounds(85, 20, 200, 25);
    generalDurationLabel.setBounds(10, 50, 120, 25);
    generalDurationText.setBounds(135, 50, 150, 25);
    gMiscPanel.setBounds(5,250,290,85);

    // **********************************************
    // Axis panel construction
    // **********************************************
    y1Panel = new AxisPanel(chart.getY1Axis(),AxisPanel.Y1_TYPE,chart);
    y2Panel = new AxisPanel(chart.getY2Axis(),AxisPanel.Y2_TYPE,chart);
    xPanel  = new AxisPanel(chart.getXAxis() ,AxisPanel.X_TYPE ,chart);

    // Global frame construction

    tabPane.add("General", generalPanel);
    if(chart.getXAxis().getAnnotation()!=JLAxis.TIME_ANNO) tabPane.add("X axis", xPanel);
    tabPane.add("Y1 axis", y1Panel);
    tabPane.add("Y2 axis", y2Panel);

    getContentPane().add(tabPane);

    closeBtn = new JButton();
    closeBtn.setText("Close");
    getContentPane().add(closeBtn);

    tabPane.setBounds(5, 5, 300, 370);
    closeBtn.setBounds(225, 380, 80, 25);

    closeBtn.addMouseListener(this);

    Rectangle r;
    if (getParent() != null) {
      r = getParent().getBounds();
    } else {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Dimension d = toolkit.getScreenSize();
      r = new Rectangle(0, 0, d.width, d.height);
    }

    int xe = r.x + (r.width - 320) / 2;
    int y = r.y + (r.height - 440) / 2;
    setBounds(xe, y, 320, 440);
    setResizable(false);

  }

  private void Commit() {
    if (chart != null) chart.repaint();
  }

  // Mouse Listener
  public void mouseClicked(MouseEvent e) {
    // ------------------------------
    if (e.getSource() == closeBtn) {
      hide();
      dispose();
    } else if (e.getSource() == generalBackColorBtn) {
      Color c = JColorChooser.showDialog(this, "Choose background Color", chart.getChartBackground());
      if (c != null) {
        chart.setChartBackground(c);
        generalBackColorView.setBackground(c);
        Commit();
      }
    } else if (e.getSource() == generalFontHeaderBtn) {

      JFontChooser fc = new JFontChooser("Choose Header Font", chart.getHeaderFont());
      Font f = fc.getNewFont();
      if (f != null) {
        chart.setHeaderFont(f);
        generalFontHeaderSampleLabel.setFont(f);
        Commit();
      }

    } else if (e.getSource() == generalFontLabelBtn) {

      JFontChooser fc = new JFontChooser("Choose label Font", chart.getXAxis().getFont());
      Font f = fc.getNewFont();
      if (f != null) {
        chart.getXAxis().setFont(f);
        chart.getY1Axis().setFont(f);
        chart.getY2Axis().setFont(f);
        chart.setLabelFont(f);
        generalFontLabelSampleLabel.setFont(f);
        Commit();
      }

    }

  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
  }

  //***************************************************************
  //Action listener
  //***************************************************************
  public void actionPerformed(ActionEvent e) {

    // General ----------------------------------------------------
    if (e.getSource() == generalLabelVisibleCheck) {

      chart.setLabelVisible(generalLabelVisibleCheck.isSelected());
      Commit();

      // ------------------------------------------------------------
    } else if (e.getSource() == generalGridCombo) {

      int sel = generalGridCombo.getSelectedIndex();

      switch (sel) {
        case 1: // On X
          chart.getXAxis().setGridVisible(true);
          chart.getY1Axis().setGridVisible(false);
          chart.getY2Axis().setGridVisible(false);
          break;
        case 2: // On Y1
          chart.getXAxis().setGridVisible(false);
          chart.getY1Axis().setGridVisible(true);
          chart.getY2Axis().setGridVisible(false);
          break;
        case 3: // On Y2
          chart.getXAxis().setGridVisible(false);
          chart.getY1Axis().setGridVisible(false);
          chart.getY2Axis().setGridVisible(true);
          break;
        case 4: // On X,Y1
          chart.getXAxis().setGridVisible(true);
          chart.getY1Axis().setGridVisible(true);
          chart.getY2Axis().setGridVisible(false);
          break;
        case 5: // On X,Y2
          chart.getXAxis().setGridVisible(true);
          chart.getY1Axis().setGridVisible(false);
          chart.getY2Axis().setGridVisible(true);
          break;
        default: // None
          chart.getXAxis().setGridVisible(false);
          chart.getY1Axis().setGridVisible(false);
          chart.getY2Axis().setGridVisible(false);
          break;
      }
      Commit();

      // ------------------------------------------------------------
    } else if (e.getSource() == generalGridStyleCombo) {

      int s = generalGridStyleCombo.getSelectedIndex();
      chart.getXAxis().setGridStyle(s);
      chart.getY1Axis().setGridStyle(s);
      chart.getY2Axis().setGridStyle(s);
      Commit();

      // ------------------------------------------------------------
    } else if (e.getSource() == generalLabelPCombo) {

      int s = generalLabelPCombo.getSelectedIndex();
      chart.setLabelPlacement(s);
      Commit();

    }
  }

  //***************************************************************
  //Change listener
  //***************************************************************
  public void stateChanged(ChangeEvent e) {
  }

  //***************************************************************
  //Key listener
  //***************************************************************
  public void keyPressed(KeyEvent e) {
  }

  public void keyTyped(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {

    // General ------------------------------------------------------------
    if (e.getSource() == generalLegendText) {

      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        chart.setHeader(generalLegendText.getText());
        Commit();
      }

      if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
        generalLegendText.setText(chart.getHeader());
      }

    } else if (e.getSource() == generalDurationText) {

      if (e.getKeyCode() == KeyEvent.VK_ENTER) {


        if (generalDurationText.getText().equalsIgnoreCase("infinty")) {
          chart.setDisplayDuration(Double.POSITIVE_INFINITY);
          return;
        }

        try {

          double d = Double.parseDouble(generalDurationText.getText());
          chart.setDisplayDuration(d * 1000);
          Commit();

        } catch (NumberFormatException err) {
          error("Display duration: malformed number.");
        }
        Commit();
      }

      if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
        generalLegendText.setText(Double.toString(chart.getDisplayDuration() / 1000.0));
      }

    }

  } // End keyReleased

  // Error message
  private void error(String m) {
    JOptionPane.showMessageDialog(this, m, "Chart options error",
      JOptionPane.ERROR_MESSAGE);
  }


}
