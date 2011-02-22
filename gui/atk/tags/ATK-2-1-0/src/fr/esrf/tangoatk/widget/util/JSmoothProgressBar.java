/*
 * JSmoothProgressBar.java
 * Author: Jean-Luc PONS
 */

package fr.esrf.tangoatk.widget.util;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;

/** A progress bar using anti-aliased font */
public class JSmoothProgressBar extends JComponent {

  // Local declarations
  public int off_x;
  public int off_y;
  public float currentValue;
  public float maxValue;
  public boolean stringPaint;

  static private Color darkBackground  = new Color(160,160,160);
  static private Color lightBackground = new Color(240,240,240);

  static private Color darkProgress = new Color(110,110,160);
  static private Color lightProgress = new Color(196,200,240);

  // General constructor
  public JSmoothProgressBar() {

    off_x = 0;
    off_y = 1;
    currentValue = 0;
    maxValue = 100;
    setBackground(new Color(206, 206, 206));
    setForeground(new Color(156, 154, 206));
    setFont(new Font("Dialog", 1, 12));
    stringPaint = false;
    setOpaque(true);

  }

  // Set value (current progress)
  public void setValue(int v) {
    if ((float) v >= maxValue)
      currentValue = maxValue;
    else
      currentValue = (float) v;
    repaint();
  }

  // Get value
  public int getValue() {
    return (int) currentValue;
  }

  // Set maximun value
  public void setMaximum(int v) {
    maxValue = (float) v;
    if (maxValue <= currentValue) currentValue = maxValue;
    repaint();
  }

  // Get maximun value
  public int getMaximum() {
    return (int) maxValue;
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
  public void setValueOffsets(int x, int y) {
    off_x = x;
    off_y = y;
    repaint();
  }

  // Paint the component
  public void paint(Graphics g) {

    int w = getWidth();
    int h = getHeight();
    float ratio = currentValue / maxValue;
    int xpos;
    String text;

    // Draw the background
    g.setColor(getBackground());
    g.fillRect(0, 0, w, h);

    // Draw the progress bar
    xpos = (int) (w * ratio);
    g.setColor(getForeground());
    g.fillRect(0, 0, xpos, h);
    g.setColor(lightProgress);
    g.drawLine(2,2,xpos-1,2);
    g.drawLine(2,2,2,h-3);
    g.setColor(darkProgress);
    g.drawLine(xpos-1,2,xpos-1,h-3);
    g.drawLine(2,h-3,xpos-1,h-3);

    // Draw the string
    if (stringPaint) {
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
              RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
              RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      FontRenderContext frc = g2.getFontRenderContext();
      xpos = (int) (100.0 * ratio);
      text = xpos + " %";
      Rectangle2D bounds = g.getFont().getStringBounds(text, frc);
      int y = (int) (bounds.getHeight());

      xpos = (int) ((w - bounds.getWidth()) / 2);
      g.setXORMode(getBackground());
      g.setColor(getForeground());
      g.drawString(text, xpos, off_y + y);
      g.setPaintMode();
    }

    // Draw the border
    g.setColor(darkBackground);
    g.drawLine(0,0,w-1,0);
    g.drawLine(0,1,w-1,1);
    g.drawLine(0,0,0,h-1);
    g.drawLine(1,0,1,h-1);
    g.setColor(lightBackground);
    g.drawLine(w-2,1,w-2,h-1);
    g.drawLine(w-1,0,w-1,h-1);
    g.drawLine(1,h-2,w-1,h-2);
    g.drawLine(0,h-1,w-1,h-1);

  }

}
