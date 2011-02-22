/*
 * JSmoothProgressBar.java
 * Author: Jean-Luc PONS
 */

package fr.esrf.tangoatk.widget.util;

import java.awt.event.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

public class JSmoothProgressBar extends JComponent {

    // Local declarations
    public int     off_x;
    public int     off_y;
    public float   currentValue;
    public float   maxValue;
    public boolean stringPaint;
    
    // General constructor
    public JSmoothProgressBar() {
    
      off_x=0;
      off_y=0;
      currentValue=0;
      maxValue=100;
      setBackground(new Color(206,206,206));
      setForeground(new Color(156,154,206));
      setBorder( BorderFactory.createLoweredBevelBorder() );
      setFont( new Font("Dialog",1,12) );
      stringPaint = false;
      setOpaque( true );
     
    }
    
    // Set value (current progress)
    public void setValue(int v) {
      if( (float)v>=maxValue ) currentValue = maxValue;
      else                     currentValue = (float)v;
      repaint();
    }

    // Get value
    public int getValue() {
      return (int)currentValue;
    }
    
    // Set maximun value
    public void setMaximum(int v) {
      maxValue = (float)v;
      if( maxValue<=currentValue ) currentValue=maxValue;
      repaint();
    }

    // Get maximun value
    public int getMaximum() {
      return (int)maxValue;
    }    

    // Set indeterminate process (Not used)
    public void setIndeterminate(boolean b) {    
    }    
    
    // Enable/Disable the string showing the progress
    public void setStringPainted(boolean b) {
      stringPaint = b;
      repaint();
    }
    
    // Set an offset (in pixels) for drawing the string
    public void setValueOffsets(int x,int y) {
      off_x = x;
      off_y = y;
      repaint();
    }

    // Paint the component 
    protected void paintComponent(Graphics g) {
	
	int w = getWidth();
	int h = getHeight();
	float ratio = currentValue/maxValue;
	int xpos;
	String text; 
	
	// Draw the background
        g.setColor(getBackground());
	g.fillRect(0,0,w,h);
	
	// Draw the progress bar
        xpos = (int)( w * ratio );
        g.setColor(getForeground());
	g.fillRect(0,0,xpos,h);
			
	// Draw the string
	if( stringPaint ) {
	  Insets insets = getInsets();
	  Graphics2D g2 = (Graphics2D) g;
	  g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,			
	                    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);		
	  g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,			
	                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);		
	  FontRenderContext frc = g2.getFontRenderContext();		
	  xpos = (int)( 100.0 * ratio );
	  text = xpos + " %";
	  Rectangle2D bounds = g.getFont().getStringBounds(text, frc);		
	  int y = (int)(bounds.getHeight());

          xpos = (int)( (w-bounds.getWidth())/2 );
	  g.setXORMode( getBackground() );
	  g.drawString(text , xpos , off_y + y);
	  g.setPaintMode();
	}
    }

}
