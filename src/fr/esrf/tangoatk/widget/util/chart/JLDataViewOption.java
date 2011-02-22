//
// JLDataViewOption.java
// Description: A Class to handle 2D graphics plot
//
// JL Pons (c)ESRF 2002

package fr.esrf.tangoatk.widget.util.chart;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class JLDataViewOption extends JDialog implements ActionListener,MouseListener,ChangeListener,KeyListener {


  // Local declaration    
  private JLDataView  dataView;
  private JLChart     chart;
  private JLabel      nameLabel;
  private JTabbedPane tabPane;
  private JButton     closeBtn;
  
  // Line option panel
  private JPanel      linePanel;
  
  private JLabel      lineColorView;
  private JButton     lineColorBtn;
  private JLabel      lineColorLabel;
  
  private JLabel      lineWidthLabel;
  private JSpinner    lineWidthSpinner;
  
  private JLabel      lineDashLabel;
  private JComboBox   lineDashCombo;

  // marker option panel
  private JPanel      markerPanel;
  
  private JLabel      markerColorView;
  private JButton     markerColorBtn;
  private JLabel      markerColorLabel;
  
  private JLabel      markerSizeLabel;
  private JSpinner    markerSizeSpinner;
  
  private JLabel      markerStyleLabel;
  private JComboBox   markerStyleCombo;

  //transformation panel  
  private JPanel      transformPanel;
  
  private JTextArea   transformHelpLabel;

  private JLabel      transformA0Label;
  private JTextField  transformA0Text;

  private JLabel      transformA1Label;
  private JTextField  transformA1Text;

  private JLabel      transformA2Label;
  private JTextField  transformA2Text;
  

  
  // Construction
  // parent: parent frame
  // chart:  Chart used to commit change (can be null)
  // v:      DataView to edit
  
  public JLDataViewOption(Frame parent,JLChart chart,JLDataView v) {
  
     super(parent,true);
     getContentPane().setLayout(null);
     dataView = v;
     this.chart=chart;

     addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent evt) {
	    hide();
	    dispose();
          }
     });

     setTitle("Data view options");
     
     tabPane = new JTabbedPane();
     
     // Line panel construction
     Color fColor = new Color( 99,97,156 );
     
     linePanel = new JPanel();
     linePanel.setLayout( null );
     
     lineColorView = new JLabel("");
     lineColorView.setBackground( dataView.getColor() );
     lineColorView.setOpaque(true);

     lineColorView.setBorder( BorderFactory.createLineBorder(Color.black) ); 

     lineColorBtn   = new JButton("...");
     lineColorBtn.addMouseListener(this);

     lineColorLabel = new JLabel("Color");
     lineColorLabel.setForeground( fColor );
          
     lineWidthLabel = new JLabel("Width");
     lineWidthLabel.setForeground( fColor );
          
     lineWidthSpinner = new JSpinner();
     Integer value = new Integer(dataView.getLineWidth()); 
     Integer min = new Integer(0);
     Integer max = new Integer(10); 
     Integer step = new Integer(1); 
     SpinnerNumberModel spModel = new SpinnerNumberModel(value, min, max, step); 
     lineWidthSpinner.setModel(spModel);      
     lineWidthSpinner.addChangeListener(this);
  
     lineDashLabel = new JLabel("Dashed style");
     lineDashLabel.setForeground( fColor );
     
     lineDashCombo = new JComboBox();
     lineDashCombo.addItem("Solid");
     lineDashCombo.addItem("Point dash");
     lineDashCombo.addItem("Short dash");
     lineDashCombo.addItem("Long dash");
     lineDashCombo.addItem("Dot dash");     
     lineDashCombo.setSelectedIndex(dataView.getStyle());
     lineDashCombo.addActionListener(this);
     
     linePanel.add(lineColorLabel);
     linePanel.add(lineColorView);
     linePanel.add(lineColorBtn);
     linePanel.add(lineWidthLabel);
     linePanel.add(lineWidthSpinner);
     linePanel.add(lineDashLabel);
     linePanel.add(lineDashCombo);
     
     lineColorLabel.setBounds( 10,10,100,25 );
     lineColorView.setBounds( 115,10,80,25 );
     lineColorBtn.setBounds(  200,10,40,27 );
          
     lineWidthLabel.setBounds( 10,50,100,25 );
     lineWidthSpinner.setBounds(  115,50,125,25 );
     
     lineDashLabel.setBounds( 10,90,100,25 );
     lineDashCombo.setBounds(  115,90,125,25 );

     // Marker panel construction
     
     markerPanel = new JPanel();
     markerPanel.setLayout( null );
     
     markerColorView = new JLabel("");
     markerColorView.setBackground( dataView.getMarkerColor() );
     markerColorView.setOpaque(true);

     markerColorView.setBorder( BorderFactory.createLineBorder(Color.black) ); 

     markerColorBtn   = new JButton("...");
     markerColorBtn.addMouseListener(this);

     markerColorLabel = new JLabel("Color");
     markerColorLabel.setForeground( fColor );
          
     markerSizeLabel = new JLabel("Size");
     markerSizeLabel.setForeground( fColor );
          
     markerSizeSpinner = new JSpinner();
     value = new Integer(dataView.getMarkerSize()); 
     spModel = new SpinnerNumberModel(value, min, max, step); 
     markerSizeSpinner.setModel(spModel);      
     markerSizeSpinner.addChangeListener(this);
  
     markerStyleLabel = new JLabel("Marker style");
     markerStyleLabel.setForeground( fColor );
     
     markerStyleCombo = new JComboBox();
     markerStyleCombo.addItem("None");
     markerStyleCombo.addItem("Dot");
     markerStyleCombo.addItem("Box");
     markerStyleCombo.addItem("triangle");
     markerStyleCombo.addItem("Diamond");
     markerStyleCombo.addItem("Star");
     markerStyleCombo.addItem("Vert. line");
     markerStyleCombo.addItem("Horz. line");
     markerStyleCombo.addItem("Cross");
     markerStyleCombo.addItem("Circle");
     markerStyleCombo.addItem("Sqaure");
     markerStyleCombo.setSelectedIndex(dataView.getMarker());
     markerStyleCombo.addActionListener(this);
     
     markerPanel.add(markerColorLabel);
     markerPanel.add(markerColorView);
     markerPanel.add(markerColorBtn);
     markerPanel.add(markerSizeLabel);
     markerPanel.add(markerSizeSpinner);
     markerPanel.add(markerStyleLabel);
     markerPanel.add(markerStyleCombo);
     
     markerColorLabel.setBounds( 10,10,100,25 );
     markerColorView.setBounds( 115,10,80,25 );
     markerColorBtn.setBounds(  200,10,40,27 );
          
     markerSizeLabel.setBounds( 10,50,100,25 );
     markerSizeSpinner.setBounds( 115,50,125,25 );
     
     markerStyleLabel.setBounds( 10,90,100,25 );
     markerStyleCombo.setBounds(  115,90,125,25 );
     
     // Transform panel construction
     transformPanel = new JPanel();
     transformPanel.setLayout( null );
     
     transformHelpLabel = new JTextArea("This apply a polynomial transform\nto the data view:\n y' = A0 + A1*y + A2*y^2");
     transformHelpLabel.setForeground( fColor );     
     transformHelpLabel.setFont( markerStyleLabel.getFont() );
     transformHelpLabel.setEditable(false);     
     transformHelpLabel.setBackground( markerStyleLabel.getBackground() );

     transformA0Label = new JLabel("A0");
     transformA0Label.setForeground( fColor );     
     transformA0Text = new JTextField();
     transformA0Text.setEditable(true);
     transformA0Text.setText( Double.toString( dataView.getA0() ) );
     transformA0Text.addKeyListener(this);
     
     transformA1Label = new JLabel("A1");
     transformA1Label.setForeground( fColor );     
     transformA1Text = new JTextField();
     transformA1Text.setEditable(true);
     transformA1Text.setText( Double.toString( dataView.getA1() ) );
     transformA1Text.addKeyListener(this);

     transformA2Label = new JLabel("A2");
     transformA2Label.setForeground( fColor );     
     transformA2Text = new JTextField();
     transformA2Text.setEditable(true);
     transformA2Text.setText( Double.toString( dataView.getA2() ) );
     transformA2Text.addKeyListener(this);
     
     transformPanel.add( transformHelpLabel );
     transformPanel.add( transformA0Label );
     transformPanel.add( transformA0Text );
     transformPanel.add( transformA1Label );
     transformPanel.add( transformA1Text );
     transformPanel.add( transformA2Label );
     transformPanel.add( transformA2Text );

     transformHelpLabel.setBounds(10,100,240,100);
     
     transformA0Label.setBounds(60,10,30,25);
     transformA0Text.setBounds( 95,10,100,25);
     transformA1Label.setBounds(60,40,30,25);
     transformA1Text.setBounds( 95,40,100,25);
     transformA2Label.setBounds(60,70,30,25);
     transformA2Text.setBounds( 95,70,100,25);
      
     // Global frame construction
     nameLabel = new JLabel();
     nameLabel.setText(dataView.getName());
     
     tabPane.add("Line",linePanel);
     tabPane.add("Marker",markerPanel);
     tabPane.add("Transform",transformPanel);
     
     getContentPane().add(tabPane);
     getContentPane().add(nameLabel);
               
     closeBtn = new JButton();
     closeBtn.setText("Close");
     getContentPane().add(closeBtn);
               
     tabPane.setBounds(  5,5,260,180 );     
     closeBtn.setBounds( 185,190,80,25 );
     nameLabel.setBounds( 10,190,170,25 );
     
     closeBtn.addMouseListener(this);
          	
	 Rectangle r;
	 if( parent!=null ) {
       r = parent.getBounds();
     } else {
	   Toolkit toolkit = Toolkit.getDefaultToolkit();
	   Dimension d = toolkit.getScreenSize();
	   r=new Rectangle(0,0,d.width,d.height);
	 }
     
	 int x = r.x + (r.width-280)/2;
     int y = r.y + (r.height-250)/2;
     setBounds(x,y,280,250);     

  }
  
  // Commit Change
  public void Commit() {
    if(chart!=null) chart.repaint();	   
  }
  
  // Mouse Listener
  public void mouseClicked(MouseEvent e) {
    if( e.getSource() == closeBtn ) {    
      hide();
      dispose();	 
    } else if( e.getSource() == lineColorBtn ) {
      Color c = JColorChooser.showDialog(this,"Choose Line Color",dataView.getColor());
      if (c != null) {
        dataView.setColor(c);
        lineColorView.setBackground( c );
	Commit();
      }      
    } else if( e.getSource() == markerColorBtn ) {
      Color c = JColorChooser.showDialog(this,"Choose marker Color",dataView.getMarkerColor());
      if (c != null) {
        dataView.setMarkerColor(c);
        markerColorView.setBackground( c );
	Commit();
      }      
    }
  }
  
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}

  //Action listener
  public void actionPerformed(ActionEvent e) {
  
    if( e.getSource()==lineDashCombo ) {
      dataView.setStyle( lineDashCombo.getSelectedIndex() );
      Commit();
    } else if( e.getSource()==markerStyleCombo ) {
      dataView.setMarker( markerStyleCombo.getSelectedIndex() );
      Commit();
    }
    
  }
  
  //Change listener
  public void stateChanged(ChangeEvent e) {
    
    Integer v;
    
    if( e.getSource()==lineWidthSpinner ) {
      v = (Integer)lineWidthSpinner.getValue();
      dataView.setLineWidth( v.intValue() );
      Commit();
    } else if( e.getSource()==markerSizeSpinner ) {
      v = (Integer)markerSizeSpinner.getValue();
      dataView.setMarkerSize( v.intValue() );
      Commit();
    }
    
  }
    
  //Key listener
  public void keyPressed(KeyEvent e) {}
  public void keyTyped(KeyEvent e) {}
  
  public void keyReleased(KeyEvent e) {
  
    if( e.getSource() == transformA0Text ) {
    
       if( e.getKeyCode() == KeyEvent.VK_ENTER ) {	   
         String s = transformA0Text.getText();
	 try {
	   double d = Double.parseDouble(s);
	   dataView.setA0(d);
           Commit();
	 } catch (NumberFormatException err) {
           transformA0Text.setText(Double.toString( dataView.getA0() ));	 
	 }
       }
       
       if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
         transformA0Text.setText(Double.toString( dataView.getA0() ));
       }
            
    } else if( e.getSource() == transformA1Text ) {
    
       if( e.getKeyCode() == KeyEvent.VK_ENTER ) {	   
         String s = transformA1Text.getText();
	 try {
	   double d = Double.parseDouble(s);
	   dataView.setA1(d);
           Commit();
	 } catch (NumberFormatException err) {
           transformA1Text.setText(Double.toString( dataView.getA1() ));	 
	 }
       }
       
       if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
         transformA1Text.setText(Double.toString( dataView.getA1() ));
       }
            
    } else if( e.getSource() == transformA2Text ) {
    
       if( e.getKeyCode() == KeyEvent.VK_ENTER ) {	   
         String s = transformA2Text.getText();
	 try {
	   double d = Double.parseDouble(s);
	   dataView.setA2(d);
           Commit();
	 } catch (NumberFormatException err) {
           transformA2Text.setText(Double.toString( dataView.getA2() ));	 
	 }
       }
       
       if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
         transformA2Text.setText(Double.toString( dataView.getA2() ));
       }
       
     
    }

  }    
}
