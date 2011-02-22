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

/**
 * A class to display global graph settings dialog.
 * @author JL Pons
 */
public class JLChartOption extends JDialog implements ActionListener,MouseListener,ChangeListener,KeyListener {

  // Local declaration    
  private JLChart     chart;
  private JTabbedPane tabPane;
  private JButton     closeBtn;
  private Frame       fParent;
    
  // general panel
  private JPanel      generalPanel;
  
  private JLabel      generalLegendLabel;
  private JTextField  generalLegendText;
    
  private JCheckBox   generalLabelVisibleCheck;

  private JLabel      generalBackColorLabel;
  private JLabel      generalBackColorView;
  private JButton     generalBackColorBtn;
  
  private JComboBox   generalGridCombo;
  private JLabel      generalGridLabel;

  private JComboBox   generalGridStyleCombo;
  private JLabel      generalGridStyleLabel;

  private JLabel      generalDurationLabel;
  private JTextField  generalDurationText;

  private JLabel      generalFontHeaderLabel;
  private JALabel     generalFontHeaderSampleLabel;
  private JButton     generalFontHeaderBtn;

  private JLabel      generalFontLabelLabel;
  private JALabel     generalFontLabelSampleLabel;
  private JButton     generalFontLabelBtn;
  
  // Y1Axis panel
  private JPanel      y1Panel;

  private JLabel      y1MinLabel;
  private JTextField  y1MinText;
  private JLabel      y1MaxLabel;
  private JTextField  y1MaxText;
  private JCheckBox   y1AutoScaleCheck;

  private JLabel      y1ScaleLabel;
  private JComboBox   y1ScaleCombo;
  private JCheckBox   y1SubGridCheck;

  private JComboBox   y1FormatCombo;
  private JLabel      y1FormatLabel;

  private JLabel      y1TitleLabel;
  private JTextField  y1TitleText;

  private JLabel      y1ColorLabel;
  private JLabel      y1ColorView;
  private JButton     y1ColorBtn;

    
  // Y2Axis panel
  private JPanel      y2Panel;

  private JLabel      y2MinLabel;
  private JTextField  y2MinText;
  private JLabel      y2MaxLabel;
  private JTextField  y2MaxText;
  private JCheckBox   y2AutoScaleCheck;

  private JLabel      y2ScaleLabel;
  private JComboBox   y2ScaleCombo;
  private JCheckBox   y2SubGridCheck;

  private JComboBox   y2FormatCombo;
  private JLabel      y2FormatLabel;

  private JLabel      y2TitleLabel;
  private JTextField  y2TitleText;

  private JLabel      y2ColorLabel;
  private JLabel      y2ColorView;
  private JButton     y2ColorBtn;
  
  // XAxis panel
  private JPanel      xPanel;

  private JLabel      xMinLabel;
  private JTextField  xMinText;
  private JLabel      xMaxLabel;
  private JTextField  xMaxText;
  private JCheckBox   xAutoScaleCheck;

  private JLabel      xScaleLabel;
  private JComboBox   xScaleCombo;
  private JCheckBox   xSubGridCheck;

  private JComboBox   xFormatCombo;
  private JLabel      xFormatLabel;

  private JLabel      xTitleLabel;
  private JTextField  xTitleText;

  private JLabel      xColorLabel;
  private JLabel      xColorView;
  private JButton     xColorBtn;
  
  //
  // parent: parent frame
  // chart:  Chart to edit
  /**
   * Dialog constructor.
   * @param parent Parent frame.
   * @param chart Chart to be edited.
   */
  public JLChartOption(Frame parent,JLChart chart) {
  
     super(parent,true);
     getContentPane().setLayout(null);
	 fParent=parent;

     this.chart=chart;

     addWindowListener(new WindowAdapter() {
       public void windowClosing(WindowEvent evt) {
	     hide();
	     dispose();
       }
     });

     setTitle("Graph options");
     
     tabPane = new JTabbedPane();
     
	 // **********************************************
     // General panel construction
	 // **********************************************
     Color fColor = new Color( 99,97,156 );
     
     generalPanel = new JPanel();
     generalPanel.setLayout( null );
	 
     generalLegendLabel = new JLabel("Graph title");
	 generalLegendLabel.setForeground( fColor );
     generalLegendText = new JTextField();
     generalLegendText.setEditable(true);
     generalLegendText.setText(chart.getHeader());
     generalLegendText.addKeyListener(this);

     generalLabelVisibleCheck = new JCheckBox();
	 generalLabelVisibleCheck.setForeground( fColor );
	 generalLabelVisibleCheck.setText("Legend visible");
	 generalLabelVisibleCheck.setSelected( chart.isLabelVisible() );
	 generalLabelVisibleCheck.addActionListener(this);

     generalBackColorLabel = new JLabel("Background");
	 generalBackColorLabel.setForeground( fColor );
     generalBackColorView = new JLabel("");
	 generalBackColorView.setOpaque(true);
	 generalBackColorView.setBorder( BorderFactory.createLineBorder(Color.black) );
	 generalBackColorView.setBackground( chart.getBackground() );
	 generalBackColorBtn=new JButton("...");
	 generalBackColorBtn.addMouseListener(this);
	 
     generalGridLabel = new JLabel("Grid");
	 generalGridLabel.setForeground( fColor );
	 
     generalGridCombo = new JComboBox();
	 generalGridCombo.addItem("None");
     generalGridCombo.addItem("On X");
     generalGridCombo.addItem("On Y1");
     generalGridCombo.addItem("On Y2");
     generalGridCombo.addItem("On X and Y1");
     generalGridCombo.addItem("On X and Y2");

	 boolean vx  = chart.getXAxis().isGridVisible();
	 boolean vy1 = chart.getY1Axis().isGridVisible();
	 boolean vy2 = chart.getY2Axis().isGridVisible();

	 int     sel=0;
	 if( vx && !vy1 && !vy2 ) sel=1;
	 if(!vx &&  vy1 && !vy2 ) sel=2;
	 if(!vx && !vy1 &&  vy2 ) sel=3;
	 if( vx &&  vy1 && !vy2 ) sel=4;
	 if( vx && !vy1 &&  vy2 ) sel=5;

	 generalGridCombo.setSelectedIndex(sel);
	 generalGridCombo.addActionListener(this);

     generalGridStyleLabel = new JLabel("Style");
	 generalGridStyleLabel.setForeground( fColor );
	 
     generalGridStyleCombo = new JComboBox();
     generalGridStyleCombo.addItem("Solid");
     generalGridStyleCombo.addItem("Point dash");
     generalGridStyleCombo.addItem("Short dash");
     generalGridStyleCombo.addItem("Long dash");
     generalGridStyleCombo.addItem("Dot dash");     
	 generalGridStyleCombo.setSelectedIndex( chart.getY1Axis().getGridStyle() );
	 generalGridStyleCombo.addActionListener(this);

     generalDurationLabel = new JLabel("Display duration (s)");
	 generalDurationLabel.setForeground( fColor );
     generalDurationText = new JTextField();
     generalDurationText.setEditable(true);
     generalDurationText.setToolTipText("Type Infinity to disable");
     generalDurationText.setText(Double.toString(chart.getDisplayDuration()/1000.0));
	 generalDurationText.addKeyListener(this);
	
     generalFontHeaderLabel = new JLabel("Header font");
	 generalFontHeaderLabel.setForeground( fColor );	
     generalFontHeaderSampleLabel = new JALabel("Sample text");
	 generalFontHeaderSampleLabel.setForeground( fColor );
	 generalFontHeaderSampleLabel.setOpaque(false);
	 generalFontHeaderSampleLabel.setFont( chart.getHeaderFont() );
     generalFontHeaderBtn = new JButton("...");
	 generalFontHeaderBtn.addMouseListener(this);

     generalFontLabelLabel = new JLabel("Label font");
	 generalFontLabelLabel.setForeground( fColor );	
     generalFontLabelSampleLabel = new JALabel("Sample 0123456789");
	 generalFontLabelSampleLabel.setForeground( fColor );
	 generalFontLabelSampleLabel.setOpaque(false);
	 generalFontLabelSampleLabel.setFont( chart.getXAxis().getFont() );
     generalFontLabelBtn = new JButton("...");
	 generalFontLabelBtn.addMouseListener(this);

	 generalPanel.add(generalLegendLabel);
	 generalPanel.add(generalLegendText);
	 generalPanel.add(generalLabelVisibleCheck);
	 generalPanel.add(generalGridLabel);
	 generalPanel.add(generalGridCombo);
	 generalPanel.add(generalGridStyleLabel);
	 generalPanel.add(generalGridStyleCombo);
	 generalPanel.add(generalDurationLabel);
	 generalPanel.add(generalDurationText);
	 generalPanel.add(generalBackColorLabel);
	 generalPanel.add(generalBackColorView);
	 generalPanel.add(generalBackColorBtn);
     generalPanel.add(generalFontHeaderLabel);
     generalPanel.add(generalFontHeaderSampleLabel);
     generalPanel.add(generalFontHeaderBtn);
     generalPanel.add(generalFontLabelLabel);
     generalPanel.add(generalFontLabelSampleLabel);
     generalPanel.add(generalFontLabelBtn);	 
	 
	 generalLegendLabel.setBounds( 10,10,70,25 );
     generalLegendText.setBounds( 85,10,200,25 );
     generalLabelVisibleCheck.setBounds(5,40,110,25);
	 generalBackColorLabel.setBounds(125,40,70,25);
	 generalBackColorView.setBounds(200,40,40,25);
	 generalBackColorBtn.setBounds(245,40,40,25);
  	 generalGridLabel.setBounds( 10,70,30,25 );
	 generalGridCombo.setBounds( 45,70,100,25);
  	 generalGridStyleLabel.setBounds( 150,70,40,25 );
	 generalGridStyleCombo.setBounds( 195,70,90,25);
	 generalDurationLabel.setBounds( 10,100,120,25 );
     generalDurationText.setBounds( 135,100,150,25 );

     generalFontHeaderLabel.setBounds( 10,130,80,25 );
     generalFontHeaderSampleLabel.setBounds( 95,130,145,25 );
     generalFontHeaderBtn.setBounds( 245,130,40,25 );

	 generalFontLabelLabel.setBounds( 10,160,80,25 );
     generalFontLabelSampleLabel.setBounds( 95,160,145,25 );
     generalFontLabelBtn.setBounds( 245,160,40,25 );

	 // **********************************************
     // Y1 Axis panel construction
	 // **********************************************
     y1Panel = new JPanel();
	 y1Panel.setLayout(null);

     JLAxis y1 = chart.getY1Axis();

	 y1MinLabel = new JLabel("Min");
     y1MinText = new JTextField();
	 y1MinLabel.setForeground( fColor );
	 y1MinLabel.setEnabled( !y1.isAutoScale() );
	 y1MinText.setText( Double.toString(y1.getMinimum()) );
	 y1MinText.setEditable( true );
	 y1MinText.setEnabled( !y1.isAutoScale() );
	 y1MinText.addKeyListener(this);

	 y1MaxLabel = new JLabel("Max");
     y1MaxText = new JTextField();
	 y1MaxLabel.setForeground( fColor );
	 y1MaxLabel.setEnabled( !y1.isAutoScale() );
	 y1MaxText.setText( Double.toString(y1.getMaximum()) );
	 y1MaxText.setEditable( true );
	 y1MaxText.setEnabled( !y1.isAutoScale() );
	 y1MaxText.addKeyListener(this);

     y1AutoScaleCheck = new JCheckBox("Auto scale");
	 y1AutoScaleCheck.setForeground( fColor );
	 y1AutoScaleCheck.setSelected( y1.isAutoScale() );
	 y1AutoScaleCheck.addActionListener( this );

     y1ScaleLabel = new JLabel("Scale");
	 y1ScaleLabel.setForeground( fColor );
     y1ScaleCombo = new JComboBox();
	 y1ScaleCombo.addItem("Linear");
	 y1ScaleCombo.addItem("Logarithmic");
	 y1ScaleCombo.setSelectedIndex( y1.getScale() );
	 y1ScaleCombo.addActionListener(this);

     y1SubGridCheck = new JCheckBox("Show sub grid");
	 y1SubGridCheck.setForeground( fColor );
	 y1SubGridCheck.setSelected( y1.isSubGridVisible() );
	 y1SubGridCheck.setToolTipText("You have to select the grid in the general option panel");
	 y1SubGridCheck.addActionListener( this );

     y1FormatCombo = new JComboBox();
	 y1FormatCombo.addItem("Automatic");
	 y1FormatCombo.addItem("Scientific");
	 y1FormatCombo.addItem("Time (hh:mm:ss)");
	 y1FormatCombo.addItem("Decimal int");
	 y1FormatCombo.addItem("Hexadecimal int");
	 y1FormatCombo.addItem("Binary int");
	 y1FormatCombo.setSelectedIndex(y1.getLabelFormat());
	 y1FormatCombo.addActionListener(this);
	 
     y1FormatLabel = new JLabel("Format");
	 y1FormatLabel.setForeground( fColor );	 

     y1TitleLabel = new JLabel("Axis title");
	 y1TitleLabel.setForeground( fColor );
     y1TitleText = new JTextField();
	 y1TitleText.setEditable(true);
	 y1TitleText.setText( y1.getName() );
	 y1TitleText.addKeyListener( this );

     y1ColorLabel = new JLabel("Axis color");
	 y1ColorLabel.setForeground( fColor );
     y1ColorView = new JLabel("");
	 y1ColorView.setOpaque(true);
	 y1ColorView.setBorder( BorderFactory.createLineBorder(Color.black) );
	 y1ColorView.setBackground( chart.getY1Axis().getAxisColor() );
	 y1ColorBtn=new JButton("...");
	 y1ColorBtn.addMouseListener(this);

	 y1Panel.add(y1MinLabel);
	 y1Panel.add(y1MinText);
	 y1Panel.add(y1MaxLabel);
	 y1Panel.add(y1MaxText);
	 y1Panel.add(y1AutoScaleCheck);
	 y1Panel.add(y1ScaleLabel);
	 y1Panel.add(y1SubGridCheck);
	 y1Panel.add(y1ScaleCombo);
	 y1Panel.add(y1FormatCombo);
	 y1Panel.add(y1FormatLabel);
	 y1Panel.add(y1TitleLabel);
	 y1Panel.add(y1TitleText);
	 y1Panel.add(y1ColorLabel);
	 y1Panel.add(y1ColorView);
	 y1Panel.add(y1ColorBtn);

	 y1MinLabel.setBounds(10,10,30,25);
	 y1MinText.setBounds(35,10,80,25);
	 y1MaxLabel.setBounds(120,10,30,25);
	 y1MaxText.setBounds(155,10,80,25);

	 y1AutoScaleCheck.setBounds(5,40,110,25);
	 y1FormatLabel.setBounds(120,40,40,25);
	 y1FormatCombo.setBounds(165,40,125,25);

	 y1SubGridCheck.setBounds(5,70,110,25);
	 y1ScaleLabel.setBounds(120,70,40,25);
	 y1ScaleCombo.setBounds(165,70,125,25);
	 
	 y1TitleLabel.setBounds(10,100,70,25);
	 y1TitleText.setBounds(85,100,205,25);

	 y1ColorLabel.setBounds(10,130,170,25);
	 y1ColorView.setBounds(185,130,55,25);
	 y1ColorBtn.setBounds(245,130,40,25);

	 // **********************************************
     // Y2 Axis panel construction
	 // **********************************************
     y2Panel = new JPanel();
	 y2Panel.setLayout(null);

     JLAxis y2 = chart.getY2Axis();

	 y2MinLabel = new JLabel("Min");
     y2MinText = new JTextField();
	 y2MinLabel.setForeground( fColor );
	 y2MinLabel.setEnabled( !y2.isAutoScale() );
	 y2MinText.setText( Double.toString(y2.getMinimum()) );
	 y2MinText.setEditable( true );
	 y2MinText.setEnabled( !y2.isAutoScale() );
	 y2MinText.addKeyListener(this);

	 y2MaxLabel = new JLabel("Max");
     y2MaxText = new JTextField();
	 y2MaxLabel.setForeground( fColor );
	 y2MaxLabel.setEnabled( !y2.isAutoScale() );
	 y2MaxText.setText( Double.toString(y2.getMaximum()) );
	 y2MaxText.setEditable( true );
	 y2MaxText.setEnabled( !y2.isAutoScale() );
	 y2MaxText.addKeyListener(this);

     y2AutoScaleCheck = new JCheckBox("Auto scale");
	 y2AutoScaleCheck.setForeground( fColor );
	 y2AutoScaleCheck.setSelected( y2.isAutoScale() );
	 y2AutoScaleCheck.addActionListener( this );

     y2ScaleLabel = new JLabel("Scale");
	 y2ScaleLabel.setForeground( fColor );
     y2ScaleCombo = new JComboBox();
	 y2ScaleCombo.addItem("Linear");
	 y2ScaleCombo.addItem("Logarithmic");
	 y2ScaleCombo.setSelectedIndex( y2.getScale() );
	 y2ScaleCombo.addActionListener(this);

     y2SubGridCheck = new JCheckBox("Show sub grid");
	 y2SubGridCheck.setForeground( fColor );
	 y2SubGridCheck.setSelected( y2.isSubGridVisible() );
	 y2SubGridCheck.setToolTipText("You have to select the grid in the general option panel");
	 y2SubGridCheck.addActionListener( this );

     y2FormatCombo = new JComboBox();
	 y2FormatCombo.addItem("Automatic");
	 y2FormatCombo.addItem("Scientific");
	 y2FormatCombo.addItem("Time (hh:mm:ss)");
	 y2FormatCombo.addItem("Decimal int");
	 y2FormatCombo.addItem("Hexadecimal int");
	 y2FormatCombo.addItem("Binary int");
	 y2FormatCombo.setSelectedIndex(y2.getLabelFormat());
	 y2FormatCombo.addActionListener(this);
	 
     y2FormatLabel = new JLabel("Format");
	 y2FormatLabel.setForeground( fColor );	 

     y2TitleLabel = new JLabel("Axis title");
	 y2TitleLabel.setForeground( fColor );
     y2TitleText = new JTextField();
	 y2TitleText.setEditable(true);
	 y2TitleText.setText( y2.getName() );
	 y2TitleText.addKeyListener( this );

     y2ColorLabel = new JLabel("Axis color");
	 y2ColorLabel.setForeground( fColor );
     y2ColorView = new JLabel("");
	 y2ColorView.setOpaque(true);
	 y2ColorView.setBorder( BorderFactory.createLineBorder(Color.black) );
	 y2ColorView.setBackground( chart.getY2Axis().getAxisColor() );
	 y2ColorBtn=new JButton("...");
	 y2ColorBtn.addMouseListener(this);

	 y2Panel.add(y2MinLabel);
	 y2Panel.add(y2MinText);
	 y2Panel.add(y2MaxLabel);
	 y2Panel.add(y2MaxText);
	 y2Panel.add(y2AutoScaleCheck);
	 y2Panel.add(y2ScaleLabel);
	 y2Panel.add(y2SubGridCheck);
	 y2Panel.add(y2ScaleCombo);
	 y2Panel.add(y2FormatCombo);
	 y2Panel.add(y2FormatLabel);
	 y2Panel.add(y2TitleLabel);
	 y2Panel.add(y2TitleText);
	 y2Panel.add(y2ColorLabel);
	 y2Panel.add(y2ColorView);
	 y2Panel.add(y2ColorBtn);

	 y2MinLabel.setBounds(10,10,30,25);
	 y2MinText.setBounds(35,10,80,25);
	 y2MaxLabel.setBounds(120,10,30,25);
	 y2MaxText.setBounds(155,10,80,25);

	 y2AutoScaleCheck.setBounds(5,40,110,25);
	 y2FormatLabel.setBounds(120,40,40,25);
	 y2FormatCombo.setBounds(165,40,125,25);

	 y2SubGridCheck.setBounds(5,70,110,25);
	 y2ScaleLabel.setBounds(120,70,40,25);
	 y2ScaleCombo.setBounds(165,70,125,25);
	 
	 y2TitleLabel.setBounds(10,100,70,25);
	 y2TitleText.setBounds(85,100,205,25);
     
	 y2ColorLabel.setBounds(10,130,170,25);
	 y2ColorView.setBounds(185,130,55,25);
	 y2ColorBtn.setBounds(245,130,40,25);
	 
	 // **********************************************
     // X Axis panel construction
	 // **********************************************
     xPanel = new JPanel();
	 xPanel.setLayout(null);

     JLAxis x = chart.getXAxis();

	 xMinLabel = new JLabel("Min");
     xMinText = new JTextField();
	 xMinLabel.setForeground( fColor );
	 xMinLabel.setEnabled( !x.isAutoScale() );
	 xMinText.setText( Double.toString(x.getMinimum()) );
	 xMinText.setEditable( true );
	 xMinText.setEnabled( !x.isAutoScale() );
	 xMinText.addKeyListener(this);

	 xMaxLabel = new JLabel("Max");
     xMaxText = new JTextField();
	 xMaxLabel.setForeground( fColor );
	 xMaxLabel.setEnabled( !x.isAutoScale() );
	 xMaxText.setText( Double.toString(x.getMaximum()) );
	 xMaxText.setEditable( true );
	 xMaxText.setEnabled( !x.isAutoScale() );
	 xMaxText.addKeyListener(this);

     xAutoScaleCheck = new JCheckBox("Auto scale");
	 xAutoScaleCheck.setForeground( fColor );
	 xAutoScaleCheck.setSelected( x.isAutoScale() );
	 xAutoScaleCheck.addActionListener( this );

     xScaleLabel = new JLabel("Scale");
	 xScaleLabel.setForeground( fColor );
     xScaleCombo = new JComboBox();
	 xScaleCombo.addItem("Linear");
	 xScaleCombo.addItem("Logarithmic");
	 xScaleCombo.setSelectedIndex( x.getScale() );
	 xScaleCombo.addActionListener(this);

     xSubGridCheck = new JCheckBox("Show sub grid");
	 xSubGridCheck.setForeground( fColor );
	 xSubGridCheck.setSelected( x.isSubGridVisible() );
	 xSubGridCheck.addActionListener( this );

     xFormatCombo = new JComboBox();
	 xFormatCombo.addItem("Automatic");
	 xFormatCombo.addItem("Scientific");
	 xFormatCombo.addItem("Time (hh:mm:ss)");
	 xFormatCombo.addItem("Decimal int");
	 xFormatCombo.addItem("Hexadecimal int");
	 xFormatCombo.addItem("Binary int");
	 xFormatCombo.setSelectedIndex(x.getLabelFormat());
	 xFormatCombo.addActionListener(this);
	 
     xFormatLabel = new JLabel("Format");
	 xFormatLabel.setForeground( fColor );	 

     xTitleLabel = new JLabel("Axis title");
	 xTitleLabel.setForeground( fColor );
     xTitleText = new JTextField();
	 xTitleText.setEditable(true);
	 xTitleText.setText( x.getName() );
	 xTitleText.addKeyListener( this );

     xColorLabel = new JLabel("Axis color");
	 xColorLabel.setForeground( fColor );
     xColorView = new JLabel("");
	 xColorView.setOpaque(true);
	 xColorView.setBorder( BorderFactory.createLineBorder(Color.black) );
	 xColorView.setBackground( chart.getXAxis().getAxisColor() );
	 xColorBtn=new JButton("...");
	 xColorBtn.addMouseListener(this);

	 xPanel.add(xMinLabel);
	 xPanel.add(xMinText);
	 xPanel.add(xMaxLabel);
	 xPanel.add(xMaxText);
	 xPanel.add(xAutoScaleCheck);
	 xPanel.add(xScaleLabel);
	 xPanel.add(xSubGridCheck);
	 xPanel.add(xScaleCombo);
	 xPanel.add(xFormatCombo);
	 xPanel.add(xFormatLabel);
	 xPanel.add(xTitleLabel);
	 xPanel.add(xTitleText);
	 xPanel.add(xColorLabel);
	 xPanel.add(xColorView);
	 xPanel.add(xColorBtn);

	 xMinLabel.setBounds(10,10,30,25);
	 xMinText.setBounds(35,10,80,25);
	 xMaxLabel.setBounds(120,10,30,25);
	 xMaxText.setBounds(155,10,80,25);

	 xAutoScaleCheck.setBounds(5,40,110,25);
	 xFormatLabel.setBounds(120,40,40,25);
	 xFormatCombo.setBounds(165,40,125,25);

	 xSubGridCheck.setBounds(5,70,110,25);
	 xScaleLabel.setBounds(120,70,40,25);
	 xScaleCombo.setBounds(165,70,125,25);
	 
	 xTitleLabel.setBounds(10,100,70,25);
	 xTitleText.setBounds(85,100,205,25);

	 xColorLabel.setBounds(10,130,170,25);
	 xColorView.setBounds(185,130,55,25);
	 xColorBtn.setBounds(245,130,40,25);
      
     // Global frame construction
     
     tabPane.add("General" ,generalPanel);
     if(x.isXY()) tabPane.add("X axis"  ,xPanel);
     tabPane.add("Y1 axis" ,y1Panel);
     tabPane.add("Y2 axis" ,y2Panel);
     
     getContentPane().add(tabPane);
               
     closeBtn = new JButton();
     closeBtn.setText("Close");
     getContentPane().add(closeBtn);
               
     tabPane.setBounds(  5,5,300,220 );     
     closeBtn.setBounds( 225,230,80,25 );
     
     closeBtn.addMouseListener(this);

     Rectangle r;
	
     if( parent!=null ) {
       r = parent.getBounds();
     } else {
	   Toolkit toolkit = Toolkit.getDefaultToolkit();
	   Dimension d = toolkit.getScreenSize();
	   r=new Rectangle(0,0,d.width,d.height);
	 }

     int xe = r.x + (r.width-320)/2;
     int y = r.y + (r.height-290)/2;
     setBounds(xe,y,320,290);    
	 setResizable(false); 
  
  }
  
  /**
   * Force graph to repainted.
   */
  public void Commit() {
    if(chart!=null) chart.repaint();	   
  }
  
  // Mouse Listener
  public void mouseClicked(MouseEvent e) {
    // ------------------------------
    if( e.getSource() == closeBtn ) {    
      hide();
      dispose();	 
    } else if ( e.getSource() == generalBackColorBtn ) {
      Color c = JColorChooser.showDialog(this,"Choose background Color",chart.getBackground());
      if (c != null) {
        chart.setBackground(c);
        generalBackColorView.setBackground( c );
	    Commit();
	  }
	} else if ( e.getSource() == generalFontHeaderBtn ) {
	  
	  JFontChooser fc = new JFontChooser("Choose Header Font",chart.getHeaderFont());
	  Font f = fc.getNewFont();
	  if( f!=null ) {
		chart.setHeaderFont(f);
	    generalFontHeaderSampleLabel.setFont( f );
		Commit();
	  }

	} else if ( e.getSource() == generalFontLabelBtn ) {
	  
	  JFontChooser fc = new JFontChooser("Choose label Font",chart.getXAxis().getFont());
	  Font f = fc.getNewFont();
	  if( f!=null ) {
  		chart.getXAxis().setFont(f);
		chart.getY1Axis().setFont(f);
		chart.getY2Axis().setFont(f);
		chart.setLabelFont(f);
	    generalFontLabelSampleLabel.setFont( f );
		Commit();
	  }

	} else if ( e.getSource() == y1ColorBtn ) {
      Color c = JColorChooser.showDialog(this,"Choose Y1 axis Color",chart.getY1Axis().getAxisColor());
      if (c != null) {
        chart.getY1Axis().setAxisColor(c);
        y1ColorView.setBackground( c );
	    Commit();
	  }
	} else if ( e.getSource() == y2ColorBtn ) {
      Color c = JColorChooser.showDialog(this,"Choose Y2 axis Color",chart.getY2Axis().getAxisColor());
      if (c != null) {
        chart.getY2Axis().setAxisColor(c);
        y2ColorView.setBackground( c );
	    Commit();
	  }
	} else if ( e.getSource() == xColorBtn ) {
      Color c = JColorChooser.showDialog(this,"Choose X axis Color",chart.getXAxis().getAxisColor());
      if (c != null) {
        chart.getXAxis().setAxisColor(c);
        xColorView.setBackground( c );
	    Commit();
	  }
	}
	

  }
  
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}

  //***************************************************************
  //Action listener
  //***************************************************************
  public void actionPerformed(ActionEvent e) {

    // General ----------------------------------------------------
    if( e.getSource() == generalLabelVisibleCheck ) {

      chart.setLabelVisible(generalLabelVisibleCheck.isSelected());
	  Commit();

    // ------------------------------------------------------------
    } else if( e.getSource() == generalGridCombo ) {

	  int sel = generalGridCombo.getSelectedIndex();
	 
	  switch(sel) {
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
	} else if ( e.getSource()==generalGridStyleCombo ) {

	  int s = generalGridStyleCombo.getSelectedIndex();
      chart.getXAxis().setGridStyle(s);
      chart.getY1Axis().setGridStyle(s);
      chart.getY2Axis().setGridStyle(s);
	  Commit();

    // Y1 ------------------------------------------------------------
    } else if ( e.getSource()==y1AutoScaleCheck) {
	  
	  boolean b = y1AutoScaleCheck.isSelected();
	  
	  chart.getY1Axis().setAutoScale(b);

	  if(!b) {
	    try {

	      double min = Double.parseDouble( y1MinText.getText() );
	      double max = Double.parseDouble( y1MaxText.getText() );
		
		  if( max>min ) {
		    chart.getY1Axis().setMinimum(min);
	        chart.getY1Axis().setMaximum(max);		
		  }		
	  
		} catch (NumberFormatException err) {

		}
	  }

	  y1MinLabel.setEnabled( !b );
	  y1MinText.setEnabled( !b );
	  y1MaxLabel.setEnabled( !b );
	  y1MaxText.setEnabled( !b );

	  Commit();
	
    // ------------------------------------------------------------
	} else if ( e.getSource()==y1FormatCombo ) {
	
	  int s = y1FormatCombo.getSelectedIndex();
	  chart.getY1Axis().setLabelFormat(s);
	  Commit();
	
    // ------------------------------------------------------------
	} else if ( e.getSource()==y1ScaleCombo ) {
	
	  int s = y1ScaleCombo.getSelectedIndex();
	  chart.getY1Axis().setScale(s);
	  Commit();
	  
    // ------------------------------------------------------------	
	} else if ( e.getSource()==y1SubGridCheck ) {
	
	  chart.getY1Axis().setSubGridVisible(y1SubGridCheck.isSelected());
	  Commit();
	
    // Y2 ------------------------------------------------------------	
	} else if ( e.getSource()==y2AutoScaleCheck) {
	  
	  boolean b = y2AutoScaleCheck.isSelected();
	  
	  chart.getY2Axis().setAutoScale(b);

	  if(!b) {
	    try {

	      double min = Double.parseDouble( y2MinText.getText() );
	      double max = Double.parseDouble( y2MaxText.getText() );
		
		  if( max>min ) {
		    chart.getY2Axis().setMinimum(min);
	        chart.getY2Axis().setMaximum(max);		
		  }		
	  
		} catch (NumberFormatException err) {

		}
	  }

	  y2MinLabel.setEnabled( !b );
	  y2MinText.setEnabled( !b );
	  y2MaxLabel.setEnabled( !b );
	  y2MaxText.setEnabled( !b );

	  Commit();
	
    // ------------------------------------------------------------
	} else if ( e.getSource()==y2FormatCombo ) {
	
	  int s = y2FormatCombo.getSelectedIndex();
	  chart.getY2Axis().setLabelFormat(s);
	  Commit();
	
    // ------------------------------------------------------------
	} else if ( e.getSource()==y2ScaleCombo ) {
	
	  int s = y2ScaleCombo.getSelectedIndex();
	  chart.getY2Axis().setScale(s);
	  Commit();
	  
    // ------------------------------------------------------------	
	} else if ( e.getSource()==y2SubGridCheck ) {
	
	  chart.getY2Axis().setSubGridVisible(y2SubGridCheck.isSelected());
	  Commit();
	
	// X ------------------------------------------------------------	
	} else if ( e.getSource()==xAutoScaleCheck) {
	  
	  boolean b = xAutoScaleCheck.isSelected();
	  
	  chart.getXAxis().setAutoScale(b);

	  if(!b) {
	    try {

	      double min = Double.parseDouble( xMinText.getText() );
	      double max = Double.parseDouble( xMaxText.getText() );
		
		  if( max>min ) {
		    chart.getXAxis().setMinimum(min);
	        chart.getXAxis().setMaximum(max);		
		  }		
	  
		} catch (NumberFormatException err) {

		}
	  }

	  xMinLabel.setEnabled( !b );
	  xMinText.setEnabled( !b );
	  xMaxLabel.setEnabled( !b );
	  xMaxText.setEnabled( !b );

	  Commit();
	
    // ------------------------------------------------------------
	} else if ( e.getSource()==xFormatCombo ) {
	
	  int s = xFormatCombo.getSelectedIndex();
	  chart.getXAxis().setLabelFormat(s);
	  Commit();
	
    // ------------------------------------------------------------
	} else if ( e.getSource()==xScaleCombo ) {
	
	  int s = xScaleCombo.getSelectedIndex();
	  chart.getXAxis().setScale(s);
	  Commit();
	  
    // ------------------------------------------------------------	
	} else if ( e.getSource()==xSubGridCheck ) {
	
	  chart.getXAxis().setSubGridVisible(xSubGridCheck.isSelected());
	  Commit();
	
	}
	
  }
  
  //***************************************************************
  //Change listener
  //***************************************************************
  public void stateChanged(ChangeEvent e) {}
    
  //***************************************************************
  //Key listener
  //***************************************************************
  public void keyPressed(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {
	  
    // General ------------------------------------------------------------
    if( e.getSource() == generalLegendText ) {
    
      if( e.getKeyCode() == KeyEvent.VK_ENTER ) {	   
        chart.setHeader( generalLegendText.getText() );
        Commit();
      }
       
      if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
        generalLegendText.setText(chart.getHeader());
      }

	} else if( e.getSource() == generalDurationText ) {
    
      if( e.getKeyCode() == KeyEvent.VK_ENTER ) {


		if( generalDurationText.getText().equalsIgnoreCase("infinty") ) {
		  chart.setDisplayDuration(Double.POSITIVE_INFINITY);
		  return;
		}
		  
		try {

	      double d = Double.parseDouble( generalDurationText.getText() );		  
	      chart.setDisplayDuration(d*1000);
		  Commit();
		  
		} catch (NumberFormatException err) {
		    error("Display duration: malformed number.");
		}
        Commit();
      }
       
      if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
        generalLegendText.setText(Double.toString(chart.getDisplayDuration()/1000.0));
      }
            
    // Y1 ------------------------------------------------------------
	} else if ( (e.getSource()==y1MinText || e.getSource()==y1MaxText) && !chart.getY1Axis().isAutoScale() ) {

      if( e.getKeyCode() == KeyEvent.VK_ENTER ) {	   
        
		try {

	      double min = Double.parseDouble( y1MinText.getText() );
	      double max = Double.parseDouble( y1MaxText.getText() );
		  
		  if( max<=min ) {
		    error("Min must be strictly lower than max.");
			return;
		  }
		
	      if( chart.getY1Axis().getScale()==JLAxis.LOG_SCALE ) {
		    if( min<=0 || max<=0 ) {
			  error("Min and max must be strictly positive with logarithmic scale.");
			  return;
			}		  
		  }
		  
	      chart.getY1Axis().setMinimum(min);
	      chart.getY1Axis().setMaximum(max);
		  Commit();
		  
		} catch (NumberFormatException err) {
		    error("Min or Max: malformed number.");
		}

	  }
		
    // ------------------------------------------------------------
	} else if (e.getSource()==y1TitleText)  {

      if( e.getKeyCode() == KeyEvent.VK_ENTER ) {	  
	    chart.getY1Axis().setName(y1TitleText.getText());
		Commit();
	  }
		
	// Y2 ------------------------------------------------------------
    } else if ( (e.getSource()==y2MinText || e.getSource()==y2MaxText) && !chart.getY2Axis().isAutoScale() ) {

      if( e.getKeyCode() == KeyEvent.VK_ENTER ) {	   
        
		try {

	      double min = Double.parseDouble( y2MinText.getText() );
	      double max = Double.parseDouble( y2MaxText.getText() );
		  
		  if( max<=min ) {
		    error("Min must be strictly lower than max.");
			return;
		  }
		
	      if( chart.getY2Axis().getScale()==JLAxis.LOG_SCALE ) {
		    if( min<=0 || max<=0 ) {
			  error("Min and max must be strictly positive with logarithmic scale.");
			  return;
			}		  
		  }
		  
	      chart.getY2Axis().setMinimum(min);
	      chart.getY2Axis().setMaximum(max);
		  Commit();
		  
		} catch (NumberFormatException err) {
		    error("Min or Max: malformed number.");
		}

	  }
		
    // ------------------------------------------------------------
	} else if (e.getSource()==y2TitleText)  {

      if( e.getKeyCode() == KeyEvent.VK_ENTER ) {	  
	    chart.getY2Axis().setName(y2TitleText.getText());
		Commit();
	  }
		
	// X ------------------------------------------------------------
    } else if ( (e.getSource()==xMinText || e.getSource()==xMaxText) && !chart.getXAxis().isAutoScale() ) {

      if( e.getKeyCode() == KeyEvent.VK_ENTER ) {	   
        
		try {

	      double min = Double.parseDouble( xMinText.getText() );
	      double max = Double.parseDouble( xMaxText.getText() );
		  
		  if( max<=min ) {
		    error("Min must be strictly lower than max.");
			return;
		  }
		
	      if( chart.getXAxis().getScale()==JLAxis.LOG_SCALE ) {
		    if( min<=0 || max<=0 ) {
			  error("Min and max must be strictly positive with logarithmic scale.");
			  return;
			}		  
		  }
		  
	      chart.getXAxis().setMinimum(min);
	      chart.getXAxis().setMaximum(max);
		  Commit();
		  
		} catch (NumberFormatException err) {
		    error("Min or Max: malformed number.");
		}

	  }
		
    // ------------------------------------------------------------
	} else if (e.getSource()==xTitleText)  {

      if( e.getKeyCode() == KeyEvent.VK_ENTER ) {	  
	    chart.getXAxis().setName(xTitleText.getText());
		Commit();
	  }
		
	}// end elseifs

  } // End keyReleased

  // Error message
  private void error(String m) {    
      JOptionPane.showMessageDialog(this, m , "Graph options error", 
	                                JOptionPane.ERROR_MESSAGE);         
  }
  
}
