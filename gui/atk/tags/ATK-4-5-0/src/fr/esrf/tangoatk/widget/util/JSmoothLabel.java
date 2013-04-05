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
 * JSmoothLabel.java
 * Author: Jean-Luc PONS
 */

package fr.esrf.tangoatk.widget.util;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

/** A Label that have anti-aliased font and that supports Matrix positionning. */
public class JSmoothLabel extends JComponent {

  /** Text is centered */
  static public int CENTER_ALIGNMENT = 1;
  /** Text is left aligned */
  static public int LEFT_ALIGNMENT = 2;
  /** Text is right aligned */
  static public int RIGHT_ALIGNMENT = 3;

  /** Computes font size and return the minimun/preferred size to the layout manager */
  static public int CLASSIC_BEHAVIOR = 2;
  /** Does not compute font size and let the layout manager size the component.Usefull with GridLayout. */
  static public int MATRIX_BEHAVIOR = 1;

  // Local declarations
  private String text;
  private int    off_y;
  private int    align;
  private int    sizingBehavior;

  // General constructor
  public JSmoothLabel() {

    off_y = 0;
    setBackground(Color.white);
    setForeground(Color.black);
    setOpaque(true);
    align = CENTER_ALIGNMENT;
    text = "";
    sizingBehavior = CLASSIC_BEHAVIOR;

  }

  /** Sets the text. */
  public void setText(String txt) {
    if (txt == null)
      text = "";
    else
      text = txt;
    repaint();
  }

  /** Gets the text. */
  public String getText() {
    return text;
  }

  /**
   * Sets the text vertical offset in pixel.
   * @param y Offset value
   */
  public void setVerticalOffset(int y) {
    off_y = y;
    repaint();
  }

  /**
   * Returns the current text vertical offset.
   */
  public int getVerticalOffset() {
    return off_y;
  }

  /**
   * Has no longer effects.
   * @see #setVerticalOffset
   * @deprecated
   */
  public void setValueOffsets(int x, int y) {
    System.out.println("JSmoothLabel.setValueOffsets() is deprecated and has no effects.");
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

    if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, w, h);
    }

    g.setColor(getForeground());
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                        RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    FontRenderContext frc = g2.getFontRenderContext();
    Rectangle2D bounds = g.getFont().getStringBounds(text, frc);
    //int y = (int) ((bounds.getHeight() + h) / 2);
    double a = getFont().getLineMetrics(text,frc).getAscent();
    int y = (int) ( (h-bounds.getHeight())/ 2.0 + a );

    int xpos = 0;
    switch (align) {
      case 1: //CENTER_ALIGNMENT
        xpos = (w - (int) bounds.getWidth()) / 2;
        break;
      case 2: //LEFT_ALIGNMENT
        xpos = 3;
        break;
      case 3: //RIGHT_ALIGNMENT
        xpos = w - (int) bounds.getWidth() - 3;
        break;
    }
    g.drawString(text, xpos, off_y + y);

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
    return getPreferredSize();
  }

}
