/*
 * WheelSwitch.java
 * Author: JL Pons 2002 E.S.R.F.
 */

package fr.esrf.tangoatk.widget.util;

import java.awt.event.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.EventListener;

public class WheelSwitch extends JComponent {

    int       off_x;                 // Postion horz offset
    int       off_y;                 // Postion vert offset
    int       intNumber;             // Number of integer digit
    int       fracNumber;            // Number of decimal digit
    double    value;                 // Current value
    double    maxValue;              // Maximun value
    Dimension dz;                    // digit size
    boolean   editMode;              // edition mode
    String    editValue;             // value entered by keyboard
    EventListenerList listenerList;  // list of WheelSwitch listeners

    // Arrow buttons
    JArrowButton buttons_up[];
    JArrowButton buttons_down[];
    int          nbButton;
    Color        buttonBackground;
    
    // General constructor
    public WheelSwitch() {
       
       setLayout(null);
       
       setForeground( Color.black );
       setBackground( new Color( 200,200,200 ) );
       buttonBackground = getBackground();
       setOpaque( true );
       setFont( new Font("Dialog" , 0 , 16) );

       nbButton=0;
       value=0.0;
       editMode=false;
       editValue="";
       setPrecision(3,2);
       setPreferredSize( new Dimension(80,40) );
       
       listenerList = new EventListenerList();
       
       addComponentListener( new ComponentListener() {
          public void componentHidden(ComponentEvent e) {}
          public void componentMoved(ComponentEvent e) {}

          public void componentResized(ComponentEvent e) {
            placeComponents();
          }
          public void componentShown(ComponentEvent e) {
            placeComponents();
          }
       });
              
       addKeyListener( new KeyListener() { 
          public void keyPressed(KeyEvent e) {	    
	    processKey(e);
	  }
          public void keyReleased(KeyEvent e) {}
          public void keyTyped(KeyEvent e) {}
       });
       
       addMouseListener( new MouseAdapter() {
	  public void mouseClicked(MouseEvent e) {
	    grabFocus();
	  } 
       });

    }
    
    // Place the components
    private void placeComponents() {
    
      if( dz!=null ) {
     
        // Place buttons 
	Dimension sz = getSize();
	int total_width =dz.width*(nbButton+2);
	int total_height=dz.height+2*dz.width;
	int i;
	
        off_x = (sz.width - total_width) / 2 ;
	off_y = (sz.height-total_height) / 2 ;
           
        for( i=0;i<nbButton;i++ ) {      
           int xpos;	 
	   if( i<intNumber )
	     xpos = off_x + (i+1)*dz.width + 1;
	   else
	     xpos = off_x + (i+2)*dz.width + 1;
	 
           // Top buttons
           buttons_up[i].setBounds( xpos , off_y + 2 , dz.width-2 , dz.width-2 );
	 
           // Bottom buttons
	   int h = dz.width + dz.height + off_y;
           buttons_down[i].setBounds( xpos , h , dz.width-2 , dz.width-2 );
        }
	
      }
         
    }
    
    // Process Key Envent
    private void processKey(KeyEvent e) {
      
      char c   = e.getKeyChar();
      int code = e.getKeyCode();
      
      if( (c>='0' && c<='9') || c=='.' || c=='-' ) {
	  editValue += c;      
	  editMode = true;
	  repaint();
      }
      
      if( code==KeyEvent.VK_CLEAR || code==KeyEvent.VK_CANCEL ||
          code==KeyEvent.VK_ESCAPE ) {
	  editValue = "";      
	  editMode = false;
	  repaint();
      }

      if( editMode && ( code==KeyEvent.VK_BACK_SPACE ||
          code==KeyEvent.VK_DELETE )) {
	  editValue = editValue.substring(0,editValue.length()-1);      
	  repaint();
      }
     
      if( editMode && code==KeyEvent.VK_ENTER ) {
          try {
	    double newValue = near( Double.parseDouble(editValue) );      
	    if( Math.abs(newValue)<maxValue ) {
	      value = newValue;
	      fireValueChange();
	      editValue = "";
	      editMode = false;
	      repaint();
	    }
	  } catch (NumberFormatException n) {
	  }
      }
      
    }
    
    // Set up the Value
    public void setValue(double v) {
      value = v;
      repaint();
    }

    // Return the whellswitch value    
    public double getValue() {
      return value;
    }
    
    // Set the Font of the wheelSwitch
    public void setFont(Font f) {
      // reset digit size
      dz = null;
      super.setFont(f);
    }
    
    // Call when user click on top button
    private void clickUp(MouseEvent evt) {
    
      int     i=0;
      boolean found=false;
      
      if( Double.isNaN(value) )
	return;
	      
      while(i<nbButton && !found) {
        found = (evt.getSource() == buttons_up[i]);
	if( !found ) i++;
      }
      
      if( !editMode && found ) {
	double newValue= near( value + Math.pow(10,(intNumber-i-1)) );
	if( Math.abs(newValue)<maxValue ) value = newValue;
	fireValueChange();
	repaint();
      }
      grabFocus();
    }
    
    // Call when user click on bottom buttons
    private void clickDown(MouseEvent evt) {
    
      int     i=0;
      boolean found=false;
      
      if( Double.isNaN(value) )
	return;
      
      while(i<nbButton && !found) {
        found = (evt.getSource() == buttons_down[i]);
	if( !found ) i++;
      }
      
      if( !editMode && found ) {
	double newValue= near( value - Math.pow(10,(intNumber-i-1)) );
	if( Math.abs(newValue)<maxValue ) value = newValue;
	fireValueChange();
	repaint();
      }
      grabFocus();      
    }
    
    // Set the color of buttons
    public void setButtonColor(Color c) {

      int i;
      
      buttonBackground=c;
      
      for(i=0;i<nbButton;i++) {
	 buttons_up[i].setBackground(buttonBackground);
	 buttons_down[i].setBackground(buttonBackground);
      }
      
    }
   
    // Set the format as C format (only "%x.yf" or "%xd" is supported)
    public void setFormat(String format) {
   
      if( format.length()<=2 ) {
	 System.out.println("WheelSwitch: Invalid format use %x.yf or %xd");
	 return;
      }

      String f=format.replace('.','_');
      f=f.replace('%','0');
      f=f.substring(0,f.length()-1);
      
      String[] s = f.split("_");    
        
      try {
     
        if( s.length == 2 ) {
	  int a = Integer.parseInt(s[0]);
	  int b = Integer.parseInt(s[1]);
	  setPrecision(a,b);
        } else if( s.length == 1 ) {
	  int a = Integer.parseInt(s[0]);
	  setPrecision(a,0); 	  
	}
      
      } catch (NumberFormatException n) {
	 System.out.println("WheelSwitch: Invalid format use %x.yf or %xd");
      }
            
    }

    // Set the precision of the wheelswitch
    // inb = number of digit for the integer part
    // fnb = number of digit for the decimal part
    public void setPrecision(int inb,int fnb) {
       
       int i;
       
       // Remove old button
       for( i=0 ; i<nbButton ; i++ )
       {
         remove(buttons_up[i]);
         remove(buttons_down[i]);
	 buttons_up[i]=null;
	 buttons_down[i]=null;
       }
              
       // Create new buttons	 
           
       intNumber    = inb;
       if(intNumber<1) intNumber=1;
       fracNumber   = fnb;
       if(fracNumber<0) fracNumber=0;
       
       nbButton     = intNumber+fracNumber;
       buttons_up   = new JArrowButton[nbButton];
       buttons_down = new JArrowButton[nbButton];
       
       maxValue = Math.pow( 10 , intNumber );
       
       for( i=0;i<nbButton;i++ ) {
       
         // Top buttons
	 buttons_up[i] = new JArrowButton();
         add( buttons_up[i] );
	 buttons_up[i].addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                clickUp(evt);
            }
         });
	 buttons_up[i].setBackground(buttonBackground);
	 
         // Bottom buttons
         buttons_down[i] = new JArrowButton();
         add( buttons_down[i] );
	 buttons_down[i].setOrientation( JArrowButton.DOWN );
	 buttons_down[i].addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                clickDown(evt);
            }
         });
	 buttons_down[i].setBackground(buttonBackground);
	 
       }
       
    }
    
    // Compute minimun size for the digit
    // according to current font
    private void computeDigitSize(Graphics g) {
    
      int max_width=0;
      int max_height=0;
      int i;

      if( dz==null ) {
            
        FontMetrics fm = getFontMetrics(getFont());
      
	max_height = fm.getAscent();
        
	for(i=0;i<10;i++) {
      
          Integer is = new Integer(i);
	  Rectangle2D b=fm.getStringBounds(is.toString(),g);
	  int w = (int)b.getWidth();
	  if( w > max_width )  max_width  = w;
	
        }
      
        dz = new Dimension( max_width , max_height );
	
	placeComponents();
      
      }
      
    }
        
    // Round according to desired precision (fracNumber)
    private double near(double d) {
	double r = Math.pow(10,fracNumber+1);
	return Math.rint( d * r ) / r;    
    }
    
    //Return the digit at the specified position
    //Negative position returns decimal digits
    private String getDigit(int pos) {
     
      if( Double.isNaN(value) )
	return "X";

      int    i;
      double tmp=value;
      if( tmp<0 ) tmp=-tmp;
       
      if( pos >= 0 ) {
      
        // Integer part
        for(  i=0;i<pos;i++ ) tmp = tmp/10.0;
	
      } else {
      
        // Decimal part
	tmp += ( 0.5/Math.pow(10.0,fracNumber) );
	int f=(int)tmp;
	tmp = tmp-f;
	tmp = tmp * Math.pow(10,-pos);
	
      }
     
      if( tmp>1e9 ) {
	int m = (int)(tmp / 1e7);
	tmp = tmp - m*1e7;
      } 
      Integer is = new Integer((int)(tmp) % 10);
      return is.toString();
                  
    }
    
    // Paint the component 
    protected void paintComponent(Graphics g) {
	
	int w = getWidth();
	int h = getHeight();

	Graphics2D g2 = (Graphics2D) g;
	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,			
	                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);		
	g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,			
	                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);		

	g.setPaintMode();
        
	computeDigitSize(g);
	
	
	if( isOpaque() ) {
	  g.setColor(getBackground());
	  g.fillRect(0,0,w,h);
	}
	
	g.setColor(getForeground());
	g.setFont(getFont());
	
	if( editMode ) {
	  
          FontMetrics fm = getFontMetrics(getFont());
          Rectangle2D b=fm.getStringBounds(editValue,g);
	  int xpos = ( w - (int)b.getWidth() ) / 2;
	  g.drawString( editValue , xpos , off_y + dz.width + dz.height - 2 );
	
	} else {
	
	  if( value<0.0 ) 
	    g.drawString( "-" , off_x , off_y + dz.width + dz.height - 2 );
	
	  if( fracNumber>0 )
	    g.drawString( "." , off_x + (intNumber+1)*dz.width + 2 , off_y + dz.width + dz.height - 2 );	
	
	  for(int i=0;i<nbButton;i++) {
            int xpos;	 
	    if( i<intNumber )
	      xpos = off_x + (i+1)*dz.width;
	    else
	      xpos = off_x + (i+2)*dz.width;
	    
	    g.drawString( getDigit(intNumber-i-1) , xpos , off_y + dz.width + dz.height - 2 );
	  }
	  
	} 
	
    }
    
    //Add the specified WheelSwitch Listeners
    public void addWheelSwitchListener( IWheelSwitchListener l ) {
	listenerList.add(IWheelSwitchListener.class , (EventListener)l);      
    }

    //Remove the specified WheelSwitch Listeners
    public void removeWheelSwitchListener( IWheelSwitchListener l ) {
	listenerList.remove(IWheelSwitchListener.class , (EventListener)l);      
    }
    
    // Fire WheelSwitchEvent to all registered listeners
    public void fireValueChange() {
    	IWheelSwitchListener[] list = (IWheelSwitchListener[])(listenerList.getListeners(IWheelSwitchListener.class));
	WheelSwitchEvent w = new WheelSwitchEvent( this , value );
	for(int  i=0;i<list.length;i++) list[i].valueChange(w);
    }
    
    /* main: Test the wheel switch */
    public static void main(String args[]) {
    
    	JFrame f = new JFrame();
        
	WheelSwitch ws = new WheelSwitch();
	ws.setFormat("%5.2f");
	ws.setButtonColor(new Color(100,200,160) );
	ws.addWheelSwitchListener(new IWheelSwitchListener() {
	  public void valueChange(WheelSwitchEvent e) {
	    System.out.println("Value changed:" + e.getValue());
	  }
	});
	
	JButton     b  = new JButton("OK");
	b.addMouseListener( new MouseAdapter() {
	  public void mouseClicked(MouseEvent e) {
	    System.exit(0);
	  } 
	});
	
	f.getContentPane().setLayout(new GridLayout(2,1));
	f.getContentPane().add(ws);
	f.getContentPane().add(b);
	f.setSize(200,150);
	f.setVisible(true);
	
    }
    
}
