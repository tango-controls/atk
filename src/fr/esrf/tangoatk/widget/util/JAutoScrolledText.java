/*
 * JAutoScrolledText.java
 * Author: Jean-Luc PONS
 */

package fr.esrf.tangoatk.widget.util;

import java.awt.event.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

// Thread to handle auto scrolling

class ScrollRun implements Runnable {
        
   JAutoScrolledText p;

   ScrollRun(JAutoScrolledText parent) {
       this.p = parent;
   }
 
   public void run() {

    while( !p.stopDemand ) {	     
      try {
	if( p.endFlag ) {
	  p.endFlag = false;
          Thread.sleep(3000);
	} else {
          Thread.sleep(p.sleepTime);
	}
        p.scrollText();
      } catch (InterruptedException e) {
        System.out.println(e);
      }
    }
    // System.out.println("Stopping scroll");
    p.currentPos = 0;
    p.repaint();

   }

}
 
public class JAutoScrolledText extends JComponent {

    // Static constant
    static public int CENTER_ALIGNMENT=1;
    static public int LEFT_ALIGNMENT=2;
    static public int RIGHT_ALIGNMENT=3;
    
    // Local declarations
    public int     currentPos=0;
    public int     maxPos;
    public boolean scrollNeeded=false;
    public boolean lastScroll=false;
    public String  text;
    public int     off_x;
    public int     off_y;
    public int     sleepTime;
    public int     align;
    public boolean endFlag;
    public boolean stopDemand;

    // General constructor
    public JAutoScrolledText() {
    
      currentPos=0;
      maxPos=0;
      off_x=0;
      off_y=0;
      sleepTime = 0;
      setBackground(Color.white);
      setForeground(Color.black);
      align = CENTER_ALIGNMENT;
      stopDemand = false;
      endFlag = false;
      text = "";

    }
    
    // Enable scrolling
    public void setAutoScroll(int time) {
      sleepTime = time;
    }

    // Set text
    public void setText(String txt) {
      if( txt==null ) text = "";
      else            text = txt;
      currentPos = 0;
      repaint();
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

    // Scroll the text if needed        
    public void scrollText() {      
      if( scrollNeeded ) {
        currentPos++;
	if(currentPos>maxPos) {
	  currentPos=0;
	  endFlag = true;
        }
        repaint();
      }
    }
    
    // Paint the component 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
	
	// Prepare rendering environement

	int w = getWidth();
	int h = getHeight();
	g.setColor(getBackground());
	g.fillRect(0,0,w,h);
	Insets insets = getInsets();
	g.setColor(getForeground());
	Graphics2D g2 = (Graphics2D) g;
	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,			
	                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);		
	g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,			
	                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);		
	FontRenderContext frc = g2.getFontRenderContext();		
	Rectangle2D bounds = g.getFont().getStringBounds(text, frc);		
	maxPos = (int)(bounds.getWidth() - (w-(insets.left+insets.right)));
	int y = (int)((bounds.getHeight() + h) / 2);
	scrollNeeded = maxPos > 0;

        // Trigger the scrolling ON/OFF
	if( lastScroll != scrollNeeded ) {
          if( sleepTime!=0 ) {
	    if( scrollNeeded ) {
	      // System.out.println("Starting scroll");
	      currentPos = 0;
	      stopDemand = false;
	      endFlag = true;
	      ScrollRun p = new ScrollRun(this);
              new Thread(p).start();
	    } else {
	      stopDemand =true;
	    }
	  }
	}
	lastScroll = scrollNeeded;
	
	if( scrollNeeded ) {
	
	  g.drawString(text, off_x-currentPos , off_y + y);
	
	} else {
	
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

}
