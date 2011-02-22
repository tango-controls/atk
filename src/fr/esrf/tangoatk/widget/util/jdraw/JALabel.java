/*
 * JALabel.java
 * Author: Jean-Luc PONS
 * Java anti-aliased label
 */

package fr.esrf.tangoatk.widget.util.jdraw;


import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * A Class for Anti-Aliased label.
  * @author JL Pons
 */

class JALabel extends JComponent {

    // Static constant
    static public int CENTER_ALIGNMENT=1;
    static public int LEFT_ALIGNMENT=2;
    static public int RIGHT_ALIGNMENT=3;
    
    // Local declarations
    public String  text;
    public int     off_x;
    public int     off_y;
    public int     align;

    // General constructor
    public JALabel() {
    
      off_x=0;
      off_y=0;
      setBackground(Color.white);
      setForeground(Color.black);
      setOpaque( true );
      align = CENTER_ALIGNMENT;
      text = "";

    }
    
    public JALabel(String s) {
      off_x=0;
      off_y=0;
      setBackground(Color.white);
      setForeground(Color.black);
      setOpaque( true );
      align = CENTER_ALIGNMENT;
      text = s;
    }

    // Set text
    public void setText(String txt) {
      if( txt==null ) text="";
      else            text = txt;
      repaint();
    }

    // Get text
    public String getText() {
      return text;
    }
    
    // Set an offset (in pixels) for drawing the string
    public void setValueOffsets(int x,int y) {
      off_x = x;
      off_y = y;
      repaint();
    }

    // Set aligmenet policiy (when no scroll)
    public void setHorizontalAlignment(int a) {
      align = a;
    }

    public int getHorizontalAlignment() {
      return align;
    }

    // Paint the component 
    protected void paintComponent(Graphics g) {
	
	// Prepare rendering environement

	int w = getWidth();
	int h = getHeight();
	
	if( isOpaque() ) {
	  g.setColor(getBackground());
	  g.fillRect(0,0,w,h);
	}
	
	g.setColor(getForeground());
	Graphics2D g2 = (Graphics2D) g;
	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,			
	                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);		
	g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,			
	                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);		
	FontRenderContext frc = g2.getFontRenderContext();		
	Rectangle2D bounds = g.getFont().getStringBounds(text, frc);		
	int y = (int)((bounds.getHeight() + h) / 2);

	int xpos = 0;
	switch( align ) {
          case 1: //CENTER_ALIGNMENT
	    xpos = (int)( (w-bounds.getWidth())/2 );
	    break;
          case 2: //LEFT_ALIGNMENT
	    xpos = 0;
	    break;
          case 3: //RIGHT_ALIGNMENT
	    xpos = (int)(w - bounds.getWidth());
	    break;
	}
	g.drawString(text , xpos , off_y + y);
	 	
    }

}
