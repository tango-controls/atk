/**
 *  A font selection dialog box. Support Anti-Aliased font.
 */
package fr.esrf.tangoatk.widget.util.jdraw;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;


class JDFontChooser extends JDialog implements ActionListener,ListSelectionListener,ChangeListener {

    private	Font	  currentFont;	
	private int       fontSize;
    private	Font	  result;		
    private	String[]  allFamily;
	
    private DefaultListModel	listModel;
	private JScrollPane         familyView;
	private JList               familyList;

    private JPanel    infoPanel;
		
	private JCheckBox  plainCheck;
	private JCheckBox  boldCheck;
	private JCheckBox  italicCheck;
	private JCheckBox  italicboldCheck;
	
	private JTextField sizeText;
	private JLabel     sizeLabel;
	private JSlider    sizeSlider;
		
	private JALabel   sampleLabel;
	
	private JButton   okBtn;
	private JButton   cancelBtn;

    /**
    * JFontChooser constructor.
    * @param title The dialog title
    *        initialFont initial font
    */
    public JDFontChooser(JFrame parent, String title, Font initialFont ) {
      super(parent,true);
      initComponents(title,initialFont);
    }

    public JDFontChooser(JDialog parent, String title, Font initialFont ) {
      super(parent,true);
      initComponents(title,initialFont);
    }

    private void initComponents(String title, Font initialFont) {

        Color fColor = new Color( 99,97,156 );

		if( initialFont==null )
		  currentFont = new Font("Dialog",Font.PLAIN,12);
		else
		  currentFont = initialFont;
		
		fontSize = currentFont.getSize();
	    result   = currentFont;
		
		// Get all available family font	
		allFamily = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		
		// Create Gui
		getContentPane().setLayout(null);
		
		listModel  = new DefaultListModel();
		familyList = new JList(listModel);
	    familyList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		
		for(int i=0;i<allFamily.length;i++)
		  listModel.addElement( allFamily[i] );
		  
		familyView = new JScrollPane( familyList );
		familyView.setBorder( BorderFactory.createLoweredBevelBorder() );
		  
	    infoPanel = new JPanel();
		infoPanel.setLayout(null);
		infoPanel.setBorder( BorderFactory.createTitledBorder("Size and Style") );
		
		plainCheck  = new JCheckBox("Plain");
		plainCheck.setForeground( fColor );
		plainCheck.addActionListener( this );

		
		boldCheck   = new JCheckBox("Bold");
		boldCheck.setForeground( fColor );
		boldCheck.addActionListener( this );

		
		italicCheck = new JCheckBox("Italic");
		italicCheck.setForeground( fColor );
		italicCheck.addActionListener( this );

		italicboldCheck = new JCheckBox("Bold italic");
		italicboldCheck.setForeground( fColor );
		italicboldCheck.addActionListener( this );
		
		sizeText = new JTextField();
		sizeText.setEditable(true);

		sizeLabel  = new JLabel("Size");
		sizeLabel.setForeground( fColor );
		
	    sizeSlider = new JSlider( 5, 72, fontSize );
	    sizeSlider.setMinorTickSpacing( 1 );
	    sizeSlider.setMajorTickSpacing( 5 );
	    sizeSlider.setPaintTicks( true );
	    sizeSlider.setPaintLabels( true );
		sizeSlider.addChangeListener(this);
		
		infoPanel.add( plainCheck );
		infoPanel.add( italicCheck );
		infoPanel.add( italicboldCheck );
		infoPanel.add( boldCheck );
		infoPanel.add( sizeLabel );
		infoPanel.add( sizeText );
		infoPanel.add( sizeSlider );
				
		plainCheck.setBounds(  5 , 20 , 100 , 25);
		italicCheck.setBounds( 5 , 45 , 100 , 25);
		boldCheck.setBounds(   5 , 70 , 100 , 25);
		italicboldCheck.setBounds(   5 , 95 , 100 , 25);
		sizeLabel.setBounds(   130 , 35 , 80 , 25 );
		sizeText.setBounds(    130 , 60 , 80 , 25);
		sizeSlider.setBounds(  5 , 125 , 240 , 45);					
		
		okBtn = new JButton( "Apply" );
		okBtn.addActionListener(this);

		cancelBtn = new JButton( "Cancel" );
		cancelBtn.addActionListener(this);
		
        sampleLabel = new JALabel("Sample 12.34");
		sampleLabel.setBorder( BorderFactory.createLoweredBevelBorder() );
		sampleLabel.setBackground( new Color(210,210,210) );

		
		// Add and size
		getContentPane().add( familyView );
		getContentPane().add( infoPanel );
		getContentPane().add( okBtn );
		getContentPane().add( cancelBtn );
		getContentPane().add( sampleLabel );
		  
		infoPanel.setBounds( 150 , 5 , 250 , 175 );
		familyView.setBounds( 5,5,140,300 );
		okBtn.setBounds( 150 , 280 , 80 , 25 );
		cancelBtn.setBounds( 320 , 280 , 80 , 25 );
		sampleLabel.setBounds( 150 , 185 , 250 , 90 );
		
		setTitle( title );
		updateControl();

		//Select the currentFont
	    int selid = familyList.getNextMatch( currentFont.getFamily(),0,
											 Position.Bias.Forward);

		if( selid != -1 ) {
	      familyList.setSelectedIndex( selid );
	      familyList.ensureIndexIsVisible( selid );
		}

		familyList.addListSelectionListener(this);

    } // JFontChooser constructor
	
	// Control update
	private void updateControl() {
	  
		plainCheck.setSelected( currentFont.isPlain() );
		boldCheck.setSelected( currentFont.isBold() && !currentFont.isItalic() );
		italicCheck.setSelected( currentFont.isItalic() && !currentFont.isBold() );
		italicboldCheck.setSelected( currentFont.isBold() && currentFont.isItalic() );

		sampleLabel.setFont( currentFont );
  	    sizeText.setText( Integer.toString(fontSize) );

	}

    // ActionListener ----------------------------------------------------------
	public void actionPerformed( ActionEvent e){
	  if( e.getSource() == cancelBtn ) {
	    result = null;
		hide();
		dispose();
	  } else if ( e.getSource() == okBtn ) {
	    result = currentFont;
		hide();
		dispose();
	  } else if ( e.getSource() == plainCheck ) {
		Font newFont = new Font( currentFont.getFamily() , Font.PLAIN , fontSize );
		if( newFont!=null ) {
		  currentFont=newFont;
          updateControl();
		}
	  } else if ( e.getSource() == boldCheck ) {
		Font newFont = new Font( currentFont.getFamily() , Font.BOLD , fontSize );
		if( newFont!=null ) {
		  currentFont=newFont;
          updateControl();
		}

	  } else if ( e.getSource() == italicCheck ) {
		Font newFont = new Font( currentFont.getFamily() , Font.ITALIC , fontSize );
		if( newFont!=null ) {
		  currentFont=newFont;
          updateControl();
		}
	  } else if ( e.getSource() == italicboldCheck) {
		Font newFont = new Font( currentFont.getFamily() , Font.ITALIC + Font.BOLD , fontSize );
		if( newFont!=null ) {
		  currentFont=newFont;
          updateControl();
		}
	  }

	}
	
    // SelectionListListener ---------------------------------------------------
    public void valueChanged(ListSelectionEvent e)  {
		if( e.getSource()==familyList ) {

		  String fName = (String)listModel.get( familyList.getSelectedIndex() );

		  Font newFont = new Font( fName , Font.PLAIN , fontSize );
		  if( newFont!=null ) {
		    currentFont=newFont;
            updateControl();
		  }

		}
	}

    // Change listener ---------------------------------------------------------
    public void stateChanged(ChangeEvent e) {
	  if( e.getSource() == sizeSlider ) {
	    fontSize = sizeSlider.getValue();
		Font newFont = currentFont.deriveFont( (float)fontSize );
		if( newFont!=null ) {
		   currentFont=newFont;
           updateControl();
		}
	  }
	}


	/**
     * Display the Font chooser dialog.
     * @return A handle to the selected font
     */

	public Font getNewFont() {
      Rectangle r;
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      Dimension d = toolkit.getScreenSize();
      r=new Rectangle(0,0,d.width,d.height);

      int x = r.x + (r.width-420)/2;
      int y = r.y + (r.height-345)/2;
      setBounds(x,y,420,345);
	  setResizable(false);
	  setVisible(true);
	  return result;
	}


}// JFontChooser class