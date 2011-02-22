/*
 * JSmoothLabel.java
 * Author: Jean-Luc PONS
 */

package fr.esrf.tangoatk.widget.util;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

public class JSmoothLabel extends JComponent {

    // Static constant
    static public int CENTER_ALIGNMENT=1;
    static public int LEFT_ALIGNMENT=2;
    static public int RIGHT_ALIGNMENT=3;
    
    /** Computes font size and return the minimun size to the layout manager */
    static public int CLASSIC_BEHAVIOR = 2;
    /** Does not compute font size and let the layout manager size the component */
    static public int MATRIX_BEHAVIOR = 1;

    // Local declarations
    private String  text;
    private int    off_x;
    private int    off_y;
    private int    align;
    private int    sizingBehavior;

    // General constructor
    public JSmoothLabel() {
    
      off_x=0;
      off_y=0;
      setBackground(Color.white);
      setForeground(Color.black);
      setOpaque( true );
      align = CENTER_ALIGNMENT;
      text = "";
      sizingBehavior = CLASSIC_BEHAVIOR;

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
    
   /**
    * Sets the sizing behavior.
    * @param s Sizing behavior
    * @see JSmoothLabel#CLASSIC_BEHAVIOR
    * @see JSmoothLabel#MATRIX_BEHAVIOR
    */
    public void setSizingBehavior(int s) {
      sizingBehavior = s;
    }
    
    /**
     * Gets the sizing behavior.
     * @return Actual sizing behavior
     * @see JSmoothLabel#setSizingBehavior
     */
    public int getSizingBehavior() {
      return sizingBehavior;
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
	    xpos = (w-(int)bounds.getWidth())/2;
	    break;
          case 2: //LEFT_ALIGNMENT
	    xpos = 3;
	    break;
          case 3: //RIGHT_ALIGNMENT
	    xpos = w - (int)bounds.getWidth() - 3;
	    break;
	}
	g.drawString(text , xpos , off_y + y);
	 	
  }
    
  public Dimension getPreferredSize() {

    if (sizingBehavior == MATRIX_BEHAVIOR) {

      return super.getPreferredSize();

    } else {

      Dimension d = ATKGraphicsUtils.measureString(text, getFont());
      d.width += 6;
      d.height += 4;
      return d;

    }

  }

  public Dimension getMinimumSize() {
    return  getPreferredSize();
  }

}
