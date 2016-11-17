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
 
package fr.esrf.tangoatk.widget.util;

import fr.esrf.tangoatk.widget.util.chart.JLAxis;
import javax.swing.*;
import java.awt.*;

/** A labeled color gradient viewer (vertical orientation only) */
public class JGradientViewer extends JComponent {

  private Gradient gradient;
  private int[] colorMap;
  private JLAxis axis;

  private int barWidth=20;

  /**
   * Construct a default black and white gradient.
   */
  public JGradientViewer() {
    setLayout(null);
    setBorder(null);
    setOpaque(true);
    gradient = new Gradient();
    colorMap = gradient.buildColorMap(256);
    axis = new JLAxis(this,JLAxis.VERTICAL_RIGHT);
    axis.setAutoScale(false);
    axis.setAnnotation(JLAxis.VALUE_ANNO);
    axis.setMinimum(0.0);
    axis.setMaximum(100.0);
  }

  /**
   * Return the current gradient.
   * @see #setGradient
   */
  public Gradient getGradient() {
    return gradient;
  }

  /**
   * Sets the gradient to be displayed.
   * @param gradient Gradient object
   */
  public void setGradient(Gradient gradient) {
    this.gradient = gradient;
    colorMap = this.gradient.buildColorMap(256);
  }

  /**
   * Returns a Handle to the axis.
   */
  public JLAxis getAxis() {
    return axis;
  }

  /** Returns the current bar width */
  public int getBarWidth() {
    return barWidth;
  }

  /** Sets the bar thickness. */
  public void setBarWidth(int barWidth) {
    this.barWidth = barWidth;
  }

  public void paint(Graphics g) {

    Dimension d = getSize();

    if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, d.width, d.height);
    }

    int bw,startX = 0;
    boolean axisVisible = false;

    if (d.height <= 20 || d.width <= 0)
      return;

    axis.measureAxis(ATKGraphicsUtils.getDefaultRenderContext(), 0, d.height - 21);

    if (d.width < barWidth) {
      bw = d.width;
      startX = 0;
    } else {
      bw = barWidth;
      if (d.width < barWidth + axis.getThickness()) {
        startX = (d.width - barWidth) / 2;
      } else {
        startX = (d.width - (barWidth + axis.getThickness())) / 2;
        axisVisible = true;
      }
    }

    double r = 256.0 / (double) (d.height - 20);
    for (int i = 10; i < d.height - 10; i++) {
      int id = (int) (r * (double) (i - 10));

      if (id <= 255)
        g.setColor(new Color(colorMap[255 - id]));
      else
        g.setColor(new Color(colorMap[0]));

      g.drawLine(startX, i, startX + bw, i);
    }

    if (axisVisible)
      axis.paintAxisDirect(g, ATKGraphicsUtils.getDefaultRenderContext(), startX + bw, 10, Color.BLACK, 0, 0);

  }

  public Dimension getPreferredSize() {
    return getMinimumSize();
  }

  public Dimension getMinimumSize() {
    axis.measureAxis(ATKGraphicsUtils.getDefaultRenderContext(), 0, getHeight() - 21);
    return new Dimension(axis.getThickness()+barWidth+4,20);
  }

  public static void main(String args[]) {
    final JFrame f = new JFrame();
    final JGradientViewer gv = new JGradientViewer();
    gv.setPreferredSize(new Dimension(50,200));
    gv.getAxis().setMinimum(1e-9);
    gv.getAxis().setMaximum(1e-6);
    gv.getAxis().setScale(JLAxis.LOG_SCALE);
    f.setContentPane(gv);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ATKGraphicsUtils.centerFrameOnScreen(f);
    f.setVisible(true);
  }

}
