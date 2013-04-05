/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
/*
 * JSmoothProgressBar.java
 * Author: Jean-Luc PONS
 */

package fr.esrf.tangoatk.widget.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/** A progress bar using anti-aliased font */
public class JSmoothProgressBar extends JComponent {

  static private Color darkProgressDefault = new Color(110,110,160);
  static private Color backProgressDefault = new Color(156, 154, 206);
  static private Color lightProgressDefault = new Color(196,200,240);

  // Local declarations
  private int off_x;
  private int off_y;
  private float currentValue;
  private float maxValue;
  private boolean stringPaint;
  private Color darkProgress = darkProgressDefault;
  private Color backProgress = backProgressDefault;
  private Color lightProgress = lightProgressDefault;
  private Insets borderMargin;
  private int ascent;
  private int[] dgSize;
  private int prSize;
  private int totalSize;
  private int[] digit;
  private double ratio;

  /**
   * Construct a progress bar.
   */
  public JSmoothProgressBar() {

    off_x = 0;
    off_y = 0;
    currentValue = 0;
    maxValue = 100;
    setBackground(new Color(206, 206, 206));
    setForeground(backProgressDefault);
    setFont(new Font("Dialog", Font.BOLD, 12));
    stringPaint = false;
    setOpaque(true);
    setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    updateDigit();

  }

  /**
   * Sets the shadow and background colors used to paint the progress bar.
   * @param back Background color
   * @param light Light color used for shadow
   * @param dark Dark color used for shadow
   */
  public void setProgressBarColors(Color back,Color light,Color dark) {

    darkProgress = dark;
    lightProgress = light;
    backProgress = back;

  }

  public void setBorder(Border b) {

    super.setBorder(b);
    if(b==null) {
      borderMargin = new Insets(0,0,0,0);
    } else {
      borderMargin = getBorder().getBorderInsets(this);
    }

  }

  public void setFont(Font f) {

    super.setFont(f);

    // Measures digit of this font.
    dgSize = new int[10];
    for(int i=0;i<10;i++)
      dgSize[i] = ATKGraphicsUtils.measureString(Integer.toString(i),f).width + 1;
    prSize = ATKGraphicsUtils.measureString("%",f).width;
    ascent = (int)(ATKGraphicsUtils.getLineMetrics("100%", f).getAscent()+0.5);

  }

  /**
   * Sets the progress value.
   * @param v Progress value
   */
  public void setValue(int v) {
    if ((float) v >= maxValue)
      currentValue = maxValue;
    else
      currentValue = (float) v;
    updateDigit();
    repaint();
  }

  /**
   * Returns the current progress value.
   * @see #setValue
   */
  public int getValue() {
    return (int) currentValue;
  }

  /**
   * Sets the maximum progress value.
   * @param v Maximum
   */
  public void setMaximum(int v) {
    maxValue = (float) v;
    if (maxValue <= currentValue) currentValue = maxValue;
    updateDigit();
    repaint();
  }

  /**
   * Returns the maximum progress value.
   */
  public int getMaximum() {
    return (int) maxValue;
  }

  /** @deprecated */
  public void setIndeterminate(boolean b) {
  }

/**
 * Enable/Disable the string showing the progress
 * @param b True to display the progress string
 */
  public void setStringPainted(boolean b) {
    stringPaint = b;
    repaint();
  }

/**
 * Sets an offset (in pixels) for drawing the progress string.
 * @param x Horizontal offset
 * @param y Vertical offset
 */
  public void setValueOffsets(int x, int y) {
    off_x = x;
    off_y = y;
    repaint();
  }

  // Paint the component
  public void paint(Graphics g) {

    int    w     = getWidth();
    int    h     = getHeight();
    int    wr    = w - (borderMargin.left + borderMargin.right + 1);
    int    hr    = h - (borderMargin.bottom + borderMargin.top + 1);

    // Draw the background
    g.setColor(getBackground());
    g.fillRect(0, 0, w, h);

    // Draw the progress bar
    int bpos = (int)Math.rint((double)wr * ratio);

    g.setColor(backProgress);
    g.fillRect(borderMargin.left, borderMargin.top, bpos, hr);

    g.setColor(lightProgress);
    g.drawLine(borderMargin.left, borderMargin.top, borderMargin.left + bpos, borderMargin.top);
    g.drawLine(borderMargin.left, borderMargin.top, borderMargin.left, borderMargin.top + hr);
    g.setColor(darkProgress);
    g.drawLine(borderMargin.left + bpos, borderMargin.top, borderMargin.left + bpos, borderMargin.top+hr);
    g.drawLine(borderMargin.left + bpos, borderMargin.top+hr, borderMargin.left, borderMargin.top + hr);

    // Draw the string
    if (stringPaint) {

      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      int xpos = ((w - totalSize) / 2) + off_x;
      int ypos = ((hr + ascent ) / 2) + off_y + borderMargin.top;
      int sum = 0;

      g.setFont(getFont());
      for(int i=0;i<digit.length;i++) {
        g.setColor( selectColor((xpos+sum+dgSize[digit[i]]/3)>bpos) );
        g.drawString(Integer.toString(digit[i]), xpos+sum, ypos);
        sum += dgSize[digit[i]];
      }
      g.setColor( selectColor((xpos+sum+prSize/3)>bpos) );
      g.drawString("%", xpos+sum, ypos);

    }

    // Draw the border
    paintBorder(g);

  }

  private Color selectColor(boolean getFg) {

    if(getFg)
      return getForeground();
    else
      return getBackground();

  }

  private void updateDigit() {

    ratio = currentValue / maxValue + 1e-4;

    // Check limits
    if(ratio<0.0) ratio = 0.0;
    if(ratio>1.0) ratio = 1.0;
    if(Double.isNaN(ratio)) ratio = 0.0;

    // ratio is in [0,1]
    int c = (int)ratio;
    int d = (int)(ratio * 10.0)  % 10;
    int u = (int)(ratio * 100.0) % 10;

    if(c==0) {
      if(d==0) {
        digit = new int[1];
        digit[0] = u;
      } else {
        digit = new int[2];
        digit[0] = d;
        digit[1] = u;
      }
    } else {
      digit = new int[3];
      digit[0] = c;
      digit[1] = d;
      digit[2] = u;
    }

    totalSize = prSize;
    for(int i=0;i<digit.length;i++)
      totalSize += dgSize[ digit[i] ];

  }



}
