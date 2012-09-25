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
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.*;

/**
 *  A class to handle a 3D spectrum image viewer using colormap
 *  This class should be used with NumberSpectrumTrend3DViewer
 */
public class J3DTrend extends JComponent implements MouseListener, MouseMotionListener, KeyListener  {

  protected BufferedImage theImage = null;
  protected Insets margin;
  protected int xOrg;
  protected int yOrg;

  // Axis
  protected JLAxis  xAxis;
  protected int     xAxisHeight;
  protected int     xAxisUpMargin;
  protected JLAxis  yAxis;
  protected int     yAxisWidth;
  protected int     yAxisRightMargin;

  // Cursor
  private boolean cursorEnabled;
  private int     xCursor;
  private int     yCursor;
  private int     xCursorInc;
  private int     yCursorInc;

  // Listener
  J3DTrendListener parent;

  /**
   * Construction
   */

  public J3DTrend() {

    setLayout(null);
    setBorder(null);
    setBackground(new Color(180, 180, 200));
    setOpaque(true);
    setMargin(new Insets(5, 5, 5, 5));

    yAxis = new JLAxis(this,JLAxis.VERTICAL_LEFT);
    yAxis.setAxisColor(Color.BLACK);
    yAxis.setFont(ATKConstant.labelFont);
    yAxis.setAutoScale(false);
    yAxis.setVisible(true);
    yAxis.setInverted( false );

    xAxis = new JLAxis(this,JLAxis.HORIZONTAL_DOWN);
    xAxis.setAxisColor(Color.BLACK);
    xAxis.setFont(ATKConstant.labelFont);
    xAxis.setAutoScale(false);
    xAxis.setVisible(true);
    xAxis.setAnnotation(JLAxis.VALUE_ANNO);

    cursorEnabled = true;
    xCursorInc=1;
    yCursorInc=1;
    addMouseListener(this);
    addMouseMotionListener(this);
    addKeyListener(this);

    parent = null;

  }

  /**
   * Return handle to the y axis
   */
  public JLAxis getYAxis() {
    return yAxis;
  }

  /**
   * Return handle to the x axis
   */
  public JLAxis getXAxis() {
    return xAxis;
  }

  /**
   * Sets the margin of the JImage
   * @param i Image margin
   */
  public void setMargin(Insets i) {
      margin = i;
  }

  /**
   * Enable cross cursor on mouse click
   * @param enable Enable cursor
   */
  public void setCursorEnabled(boolean enable) {
    cursorEnabled = enable;
    repaint();
  }

  /**
   * Sets the parent of this component
   * @param p NumberSpectrumTrend3DViewer parent
   */
  public void setParent(J3DTrendListener p) {
    parent=p;
  }

  protected void measureAxis() {

    xAxisHeight = 0;
    yAxisWidth  = 0;
    xAxisUpMargin = 0;
    yAxisRightMargin = 0;
    if(xAxis.isVisible()) {
      xAxis.measureAxis(ATKGraphicsUtils.getDefaultRenderContext(), theImage.getWidth() , 0);
      if(xAxis.getOrientation() == JLAxis.HORIZONTAL_UP) {
        xAxisHeight = 7;
        xAxisUpMargin =  xAxis.getThickness();
      } else {
        xAxisHeight = xAxis.getThickness();
        xAxisUpMargin = 7;
      }
    }
    if(yAxis.isVisible()) {
      yAxis.measureAxis(ATKGraphicsUtils.getDefaultRenderContext(), 0, theImage.getHeight());
      yAxisWidth = yAxis.getThickness();
      yAxisRightMargin = 15;
    }

  }

  public Dimension getMinimumSize() {

    if (theImage == null) {
      return new Dimension(320, 200);
    } else {
      measureAxis();
      int xDim =  theImage.getWidth() + margin.right + margin.left + yAxisWidth + yAxisRightMargin;
      int yDim =  theImage.getHeight() + margin.top + margin.bottom + xAxisHeight + xAxisUpMargin;
      return new Dimension(xDim,yDim);
    }

  }

  public Dimension getPreferredSize() {
    return getMinimumSize();
  }

  /**
   * Sets the image
   * @param img image
   */
  public void setImage(BufferedImage img,int maxX,int maxY) {

    BufferedImage lastImage = theImage;
    theImage = img;
    repaint();
    if(lastImage != null)
      lastImage.flush();
    lastImage = null;

    yAxis.setMinimum(0);
    yAxis.setMaximum(maxY);
    xAxis.setMinimum(-maxX);
    xAxis.setMaximum(0);

  }

  /**
   * Returns a handle to the image displayed
   * @return Image handle
   */
  public BufferedImage getImage() {
    return theImage;
  }

  /**
   * Shift the cursor by the specified delta
   * @param delta
   */
  public void shiftCursorX(int delta) {
    xCursor+=delta;
  }

  /**
   * Returns X coordinates of the cursor
   */
  public int getXCursor() {
    return xCursor;
  }

  /**
   * Returns Y coordinates of the cursor
   */
  public int getYCursor() {
    return yCursor;
  }

  /**
   * Returns true if the cursor is  inside the image.
   */
  public boolean isCursorInside() {
    if( theImage==null ) return false;
    return (yCursor>=0 && yCursor<theImage.getHeight() &&
            xCursor>=0 && xCursor<theImage.getWidth() );
  }

  /**
   * Remove the cursor
   */
  public void clearCursor() {
    xCursor = -1;
    yCursor = -1;
    cursorMove();
  }

  protected void paintAxis(Graphics g) {

    if(yAxis.isVisible()) {
        yAxis.paintAxisDirect(g,ATKGraphicsUtils.getDefaultRenderContext(),
                  -yAxis.getThickness(),0,Color.BLACK,0,0);
        if(yAxis.isDrawOpposite())
          yAxis.paintAxisOpposite(g,ATKGraphicsUtils.getDefaultRenderContext(),
                     theImage.getWidth()-yAxis.getThickness(),0,Color.BLACK,0,0);
    }

    if(xAxis.isVisible()) {
        xAxis.paintAxisDirect(g,ATKGraphicsUtils.getDefaultRenderContext(),
                  0,theImage.getHeight(),Color.BLACK,0,0);
        if(xAxis.isDrawOpposite())
          xAxis.paintAxisOpposite(g,ATKGraphicsUtils.getDefaultRenderContext(),
                      0,0,Color.BLACK,0,0);
    }

  }

  public void paint(Graphics g) {

    Dimension d = getSize();

    if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, d.width, d.height);
    }

    if(theImage == null)
      return;

    measureAxis();
    xOrg = (d.width - (theImage.getWidth() + yAxisWidth + yAxisRightMargin)) / 2 ;
    yOrg = (d.height - (theImage.getHeight() + xAxisHeight + xAxisUpMargin)) / 2;
    g.translate(xOrg + yAxisWidth , yOrg + xAxisUpMargin);
    g.drawImage(theImage, 0 , 0, null);
    paintAxis(g);

    if(cursorEnabled) {
      if( isCursorInside() ) {
        g.setColor(Color.WHITE);
        g.drawLine(0,yCursor,theImage.getWidth(),yCursor);
        g.drawLine(xCursor,0,xCursor,theImage.getHeight());
      }
    }

  }

  public void cursorMove() {

    if (parent != null) {
      if (isCursorInside()) parent.updateCursor(xCursor, yCursor);
      else parent.updateCursor(-1, -1);
    }
    repaint();

  }

  // --------------------------------------------------------------
  // Mouse listener
  // --------------------------------------------------------------

  public void mouseDragged(MouseEvent e) {

    if (cursorEnabled) {
      xCursor = e.getX() - xOrg - yAxisWidth;
      yCursor = e.getY() - yOrg - xAxisUpMargin;
      cursorMove();
    }

  }

  public void mouseMoved(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {

    grabFocus();
    if(cursorEnabled) {
      xCursor = e.getX() - xOrg - yAxisWidth;
      yCursor = e.getY() - yOrg - xAxisUpMargin;
      cursorMove();
    }

  }

  public void keyPressed(KeyEvent e) {

    switch (e.getKeyCode()) {
      case KeyEvent.VK_UP:
        if(cursorEnabled) {
          yCursor-=yCursorInc;
          cursorMove();
        }
        break;
      case KeyEvent.VK_DOWN:
        if(cursorEnabled) {
          yCursor+=yCursorInc;
          cursorMove();
        }
        break;
      case KeyEvent.VK_LEFT:
        if(cursorEnabled) {
          xCursor-=xCursorInc;
          cursorMove();
        }
        break;
      case KeyEvent.VK_RIGHT:
        if(cursorEnabled) {
          xCursor+=xCursorInc;
          cursorMove();
        }
        break;
    }

  }

  public void keyReleased (KeyEvent e) {
  }

  public void keyTyped (KeyEvent e) {
  }

}
