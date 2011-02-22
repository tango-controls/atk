package fr.esrf.tangoatk.widget.util;

import fr.esrf.tangoatk.widget.util.chart.JLAxis;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * A class to handle an image viewer with seleclection , marker and axis capability.
 */
public class JImage extends JComponent implements MouseMotionListener, MouseListener, KeyListener {

  // Selection modes
  public final static int MODE_LINE   = 0;
  public final static int MODE_RECT   = 1;
  public final static int MODE_CROSS  = 2;

  // Cross selection position (Vertical arrow)
  public final static int VERTICAL_LEFT     = 0;
  public final static int VERTICAL_CENTER   = 1;
  public final static int VERTICAL_RIGHT    = 2;

  // Cross selection position (Horizontal arrow)
  public final static int HORIZONTAL_TOP    = 0;
  public final static int HORIZONTAL_CENTER = 1;
  public final static int HORIZONTAL_BOTTOM = 2;

  // Markers
  protected final static int MARKER_CROSS = 1;
  protected final static int MARKER_RECT  = 2;
  protected final static int MARKER_VLINE = 3;
  protected final static int MARKER_HLINE = 4;

  protected BufferedImage theImage = null;
  protected Insets margin;
  protected int xOrg;
  protected int yOrg;

  // Selection
  protected boolean selectionEnabled;
  protected int mode;             // 0=Line 1=Rectangle
  protected boolean isDragging;
  protected int dragCorner;       // 0 = Top,Left 1 = Top,Right 3 = Bottom,Right 4 = Bottom,left
  protected int cornerWidth = 10; // Must be multiple of 2
  protected int x1 = -1;          // Current Image selection
  protected int y1 = -1;
  protected int x2 = -1;
  protected int y2 = -1;

  protected boolean snapToGrid;
  protected int grid = 16;

  // Marker
  protected Vector<Marker> markers = null;
  protected double markerScaleFactor = 1.0;

  // Axis
  protected JLAxis  xAxis;
  protected int     xAxisHeight;
  protected int     xAxisUpMargin;
  protected JLAxis  yAxis;
  protected int     yAxisWidth;
  protected int     yAxisRightMargin;

  // Cursor
  protected boolean crossCursor = false;
  protected Color cursorColor = Color.WHITE;
  protected int xCursor = -1;
  protected int yCursor = -1;

  protected Color selectionColor = Color.RED;

  protected int horizontalPosition;
  protected int verticalPosition;

  /**
   * Construction
   */
  public JImage() {

    setLayout(null);
    setBorder(null);
    setBackground(new Color(180, 180, 200));
    setOpaque(true);
    setMargin(new Insets(5, 5, 5, 5));
    xOrg = 5;
    yOrg = 5;
    mode = MODE_RECT;
    verticalPosition   = VERTICAL_CENTER;
    horizontalPosition = HORIZONTAL_CENTER;
    isDragging = false;
    selectionEnabled = true;
    addMouseMotionListener(this);
    addMouseListener(this);
    addKeyListener(this);
    snapToGrid = false;

    yAxis = new JLAxis(this,JLAxis.VERTICAL_LEFT);
    yAxis.setAxisColor(Color.BLACK);
    yAxis.setFont(ATKConstant.labelFont);
    yAxis.setAutoScale(false);
    yAxis.setMinimum(0.0);
    yAxis.setMaximum(100.0);
    yAxis.setVisible(false);
    // 0 is on top, not on bottom
    yAxis.setInverted( false );

    xAxis = new JLAxis(this,JLAxis.HORIZONTAL_UP);
    xAxis.setAxisColor(Color.BLACK);
    xAxis.setFont(ATKConstant.labelFont);
    xAxis.setAutoScale(false);
    xAxis.setMinimum(0.0);
    xAxis.setMaximum(100.0);
    xAxis.setVisible(false);

  }

  /**
   * Enable or disable the cross cursor.
   * @param enable True to enable cross cursor, false otherwise.
   */
  public void setCrossCursor(boolean enable) {
    crossCursor = enable;
    if(enable) {
      setCursor(null);
    } else {
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
  }

  /**
   * Set the cross cursor color.
   * @param c Cursor color
   * @see #setCrossCursor
   */
  public void setCursorColor(Color c) {
    cursorColor = c;
  }

  /**
   * Returns true if cross curosr is enabled.
   */
  public boolean isCrossCursor() {
    return crossCursor;
  }

  /**
   * Enable or disable the selection. When enabled, A region can be dragged
   * and sized inside the image.
   * @param b Selection mode
   * @see JImage#isSelectionEnabled
   */
  public void setSelectionEnabled(boolean b) {
    selectionEnabled = b;
    repaint();
  }

  /**
   * Returns true only if the selection is enabled.
   * @return Selection mode
   */
  public boolean isSelectionEnabled() {
    return selectionEnabled;
  }

  /**
   * Returns the current rectangle selected.
   * @return null when no selection or a Rectangle
   */
  public Rectangle getSelectionRect() {
    if (hasSelection()) {
      return buildSelectionRect();
    } else
      return null;
  }

  /**
   * Returns the current line/rectangle selected.
   * @return null when no selection or a 2 points array
   */
  public Point[] getSelectionPoint() {
    if (hasSelection()) {
      Point[] ret = new Point[2];
      ret[0] = new Point(x1, y1);
      ret[1] = new Point(x2, y2);
      return ret;
    } else
      return null;
  }

  /**
   * Returns the current lines selected (MODE_CROSS).
   * @return null when no selection or a 4 points array
   */
  public Point[] getSelectionCrossPoint() {

    if (hasSelection() && mode==MODE_CROSS) {

      Point[] pts = new Point[4];
      Point p1=new Point();
      Point p2=new Point();
      Point p3=new Point();
      Point p4=new Point();
      pts[0] = p1;
      pts[1] = p2;
      pts[2] = p3;
      pts[3] = p4;

      switch(getHorizontalPosition()) {
        case HORIZONTAL_TOP:
          p1.x = x1; p2.x = x2;
          p1.y = y1; p2.y = y1;
          break;
        case HORIZONTAL_CENTER:
          p1.x = x1; p2.x = x2;
          p1.y = (y1+y2)/2; p2.y = (y1+y2)/2;
          break;
        case HORIZONTAL_BOTTOM:
          p1.x = x1; p2.x = x2;
          p1.y = y2; p2.y = y2;
          break;
      }

      switch(getVerticalPosition()) {
        case VERTICAL_LEFT:
          p3.x = x1; p4.x = x1;
          p3.y = y1; p4.y = y2;
          break;
        case VERTICAL_CENTER:
          p3.x = (x1+x2)/2; p4.x = (x1+x2)/2;
          p3.y = y1; p4.y = y2;
          break;
        case VERTICAL_RIGHT:
          p3.x = x2; p4.x = x2;
          p3.y = y1; p4.y = y2;
          break;
      }

      return pts;

    } else {
      return null;
    }

  }

  /**
   * Sets the selection mode
   * @param m 0 for Line selection , 1 for Rectangle selection
   */
  public void setSelectionMode(int m) {
    mode = m;
    clipSelection();
    repaint();
  }

  /**
   * Returns the selection mode
   * @return 0 for Line selection , 1 for Rectangle selection
   */
  public int getSelectionMode() {
    return mode;
  }

  /**
   * Returns true if a selection exists
   * @return true is a selection is visible on the image
   */
  public boolean hasSelection() {
    return selectionEnabled && (x1 >= 0) && (x2 >= 0) && (y1 >= 0) && (y2 >= 0);
  }

  /**
   * Sets the current selection.
   * @param _x1 Top left corner x coordinate
   * @param _y1 Top left corner y coordinate
   * @param _x2 Bottom right corner x coordinate
   * @param _y2 Bottom right corner y coordinate
   */
  public void setSelection(int _x1, int _y1, int _x2, int _y2) {

    if (selectionEnabled) {

      x1 = _x1;
      y1 = _y1;
      x2 = _x2;
      y2 = _y2;

      clipSelection();
      isDragging = false;
      repaint();

    }

  }

  /**
   * Clears the current selection.
   */
  public void clearSelection() {
    x1 = -1;
    y1 = -1;
    x2 = -1;
    y2 = -1;
    isDragging = false;
    repaint();
  }

  /**
   * Sets the margin of the JImage
   * @param i Image margin
   */
  public void setMargin(Insets i) {
      margin = i;
  }

  /**
   * Returns margin of the image
   * @return Image margin
   */
  public Insets getMargin() {
    return margin;
  }

  /**
   * Return origin of the image within the component
   * @return X origin in pixel
   */
  public int getXOrigin() {
    return xOrg + yAxis.getThickness();
  }

  /**
   * Return origin of the image within the component
   * @return Y origin in pixel
   */
  public int getYOrigin() {
    return yOrg + xAxisUpMargin;
  }

  /**
   * Sets the image to be displayed
   * @param i Image
   */
  public void setImage(BufferedImage i) {
    BufferedImage formerImage = theImage;
    theImage = i;
    clearSelection();
    repaint();
    if(formerImage != null) {
      formerImage.flush();
    }
    formerImage = null;
  }

  /**
   * Returns a handle of the X axis.
   */
  public JLAxis getXAxis() {
    return xAxis;
  }

  /**
   * Returns a handle of the Y axis.
   */
  public JLAxis getYAxis() {
    return yAxis;
  }

  /**
   * Returns a handle to the image displayed
   * @return Image handle
   */
  public BufferedImage getImage() {
    return theImage;
  }

  /**
   * Returns size of the image (does not include margin)
   * @return Image size
   */
  public Dimension getImageSize() {
    if (theImage != null)
      return new Dimension(theImage.getWidth(), theImage.getHeight());
    else
      return new Dimension(0, 0);
  }

  /**
   * Return true when snap to grid mode is enable
   * @return AlignToGrid state
   * @see JImage#setSnapToGrid
   */
  public boolean isSnapToGrid() {
    return snapToGrid;
  }

  /**
   * Enables the grid mode. All selection point are rounded to
   * grid multiple.
   * @param b
   * @see JImage#isSnapToGrid
   * @see JImage#getSnapGrid
   */
  public void setSnapToGrid(boolean b) {
    snapToGrid = b;
  }

  /**
   * Returns the grid spacing (Pixel)
   * @return Grid width in pixels
   */
  public int getSnapGrid() {
    return grid;
  }

  /**
   * Sets the grid spacing (Pixel)
   * @param b Grid width in pixels
   */
  public void setSnapGrid(int b) {
    grid = b;
  }

  /**
   * Adds a cross marker at the specified pos
   * @param x X coordinates
   * @param y Y coordinates
   * @param c Marker Color
   * @return Marker id
   */
  public int addCrossMarker(int x, int y, Color c) {
    if (markers == null) markers = new Vector<Marker>();
    markers.add(new Marker(MARKER_CROSS, x, y, c));
    repaint();
    return markers.size() - 1;
  }

  /**
   * Adds a rectangle marker
   * @param x X topleft corner coordinate
   * @param y Y topleft corner coordinate
   * @param width Rectangle width
   * @param height Rectangle hieght
   * @param c Marker Color
   * @return Marker id
   */
  public int addRectangleMarker(int x, int y, int width, int height, Color c) {
    if (markers == null) markers = new Vector<Marker>();
    markers.add(new Marker(MARKER_RECT, new Rectangle(x, y, width, height), c));
    repaint();
    return markers.size() - 1;
  }

  /**
   * Adds a vertical line marker
   * @param x Horizontal position
   * @param c Marker color
   * @return Marker id
   */
  public int addVerticalLineMarker(int x,Color c) {
    if (markers == null) markers = new Vector<Marker>();
    markers.add(new Marker(MARKER_VLINE, x , 0, c));
    repaint();
    return markers.size() - 1;
  }

  /**
   * Adds a horizontal line marker
   * @param y Vertical position
   * @param c Marker color
   * @return Marker id
   */
  public int addHorizontalLineMarker(int y,Color c) {
    if (markers == null) markers = new Vector<Marker>();
    markers.add(new Marker(MARKER_HLINE, 0 , y, c));
    repaint();
    return markers.size() - 1;
  }

  /**
   * Sets the position of a marker
   * @param id Marker index
   * @param x X coordinate (ignored when HORIZONTAL_LINE Marker)
   * @param y Y coordinate (ignored when VERTICAL_LINE Marker)
   * @param nWidth Rectangle width (ignored when CROSS Marker, LINE Marker)
   * @param nHeight Rectangle height (ignored when CROSS Marker, LINE Marker)
   */
  public void setMarkerPos(int id, int x, int y, int nWidth, int nHeight) {
    if (markers != null) {
      if (id >= 0 && id < markers.size()) {
        Marker m = (Marker) markers.get(id);
        m.markerRect.setBounds(x, y, nWidth, nHeight);
      }
      repaint();
    }
  }

  /**
   * Clears all markers
   */
  public void clearMarkers() {
    if (markers != null) {
      markers.clear();
      markers = null;
      repaint();
    }
  }

  /**
   * @return The number of marker
   */
  public int getMarkerNumber() {
    if (markers == null)
      return 0;
    else
      return markers.size();
  }

  /**
   * Sets the scale factor for markers
   * @param s Scale factor
   */
  public void setMarkerScale(double s) {
    markerScaleFactor = s;
  }

  /**
   * Returns the horizontal selection arrow position (MODE_CROSS selection)
   */
  public int getHorizontalPosition () {
    return horizontalPosition;
  }

  /**
   * Sets the horizontal selection arrow position (MODE_CROSS selection)
   * @param horizontalPosition Horizontal position
   */
  public void setHorizontalPosition (int horizontalPosition) {
    this.horizontalPosition = horizontalPosition;
    repaint();
  }

  /**
   * Returns the vertical selection arrow position (MODE_CROSS selection)
   */
  public int getVerticalPosition () {
    return verticalPosition;
  }

  /**
   * Sets the vertical selection arrow position (MODE_CROSS selection)
   * @param verticalPosition Vertical position
   */
  public void setVerticalPosition (int verticalPosition) {
    this.verticalPosition = verticalPosition;
    repaint();
  }

  /**
   * Returns the color of the selection area.
   */
  public Color getSelectionColor () {
    return selectionColor;
  }

  /**
   * Sets the color of the selection area
   * @param selectionColor Selection color
   */
  public void setSelectionColor (Color selectionColor) {
    this.selectionColor = selectionColor;
    repaint();
  }

  // -------------------------------------------------------
  // Painting stuff
  // -------------------------------------------------------
  protected void paintCursor(Graphics g) {

    Dimension d = getImageSize();

    if(crossCursor && xCursor>=0 && yCursor>=0 && xCursor<=d.width && yCursor<=d.height) {
      g.setColor(cursorColor);
      g.drawLine(0,yCursor,d.width,yCursor);
      g.drawLine(xCursor,0,xCursor,d.height);
    }

  }

  protected void paintSelection(Graphics g) {

    if (hasSelection()) {
      Graphics2D g2 = (Graphics2D) g;

      Color oldColor = g2.getColor();
      if (selectionColor != null) {
        g2.setColor(selectionColor);
      }
      else {
        g2.setColor(Color.RED); 
      }
      Stroke old = g2.getStroke();
      BasicStroke bs = new BasicStroke(2);
      g2.setStroke(bs);

      Rectangle r = buildSelectionRect();
      Rectangle c = new Rectangle(0, 0, cornerWidth, cornerWidth);

      switch (mode) {

        case MODE_LINE: // Line
          g2.drawLine(x1, y1, x2, y2);

          g2.setStroke(old);

          // Draw arrow
          int xc = (x1 + x2) / 2;
          int yc = (y1 + y2) / 2;

          double xn = (y2 - y1);
          double yn = -(x2 - x1);
          double n = Math.sqrt(xn * xn + yn * yn);
          int vxn = (int) (8.0 * (xn / n));
          int vyn = (int) (8.0 * (yn / n));

          xn = (x2 - x1);
          yn = (y2 - y1);
          n = Math.sqrt(xn * xn + yn * yn);
          int vx = (int) (10.0 * (xn / n));
          int vy = (int) (10.0 * (yn / n));

          g2.drawLine(xc - vxn, yc - vyn, xc + vxn, yc + vyn);
          g2.drawLine(xc + vxn, yc + vyn, xc + vx, yc + vy);
          g2.drawLine(xc + vx, yc + vy, xc - vxn, yc - vyn);

          // Draw corners
          c.translate(x1 - cornerWidth / 2, y1 - cornerWidth / 2);
          g2.drawRect(c.x, c.y, c.width, c.height);
          c.translate(x2 - x1, y2 - y1);
          g2.drawRect(c.x, c.y, c.width, c.height);
          break;

        case MODE_RECT: // Rectangle

          g2.drawRect(r.x, r.y, r.width, r.height);

          // Draw corners
          g2.setStroke(old);
          c.translate(r.x - cornerWidth / 2, r.y - cornerWidth / 2);
          g2.drawRect(c.x, c.y, c.width, c.height);
          c.translate(r.width, 0);
          g2.drawRect(c.x, c.y, c.width, c.height);
          c.translate(0, r.height);
          g2.drawRect(c.x, c.y, c.width, c.height);
          c.translate(-r.width, 0);
          g2.drawRect(c.x, c.y, c.width, c.height);
          c.translate(r.width / 2, -r.height / 2);
          g2.drawRect(c.x, c.y, c.width, c.height);
          break;

        case MODE_CROSS:

          int xv, yh;
          switch (horizontalPosition) {
              case HORIZONTAL_TOP:
                  yh = (y1<y2?y1:y2);
                  break;
              case HORIZONTAL_CENTER:
                  yh = (y1+y2)/2;
                  break;
              case HORIZONTAL_BOTTOM:
                  yh = (y1<y2?y2:y1);
                  break;
              default:
                  yh = -1;
          }
          switch (verticalPosition) {
              case VERTICAL_LEFT:
                  xv = (x1<x2?x1:x2);
                  break;
              case VERTICAL_CENTER:
                  xv = (x1+x2)/2;
                  break;
              case VERTICAL_RIGHT:
                  xv = (x1<x2?x2:x1);
                  break;
              default:
                  xv = -1;
          }
          if ( xv >= 0 && yh >= 0 ) {
              // Draw Lines
              g2.drawLine( x1, yh, x2, yh );
              g2.drawLine( xv, y1, xv, y2 );
              // Draw arrow
              // horizontal
              if ( x1 < x2 ) {
                  g2.drawLine( x2, yh, x2 - 8, yh - 8 );
                  g2.drawLine( x2 - 8, yh - 8, x2 - 8, yh + 8 );
                  g2.drawLine( x2 - 8, yh + 8, x2, yh );
              }
              else {
                  g2.drawLine( x2, yh, x2 + 8, yh - 8 );
                  g2.drawLine( x2 + 8, yh - 8, x2 + 8, yh + 8 );
                  g2.drawLine( x2 + 8, yh + 8, x2, yh );
              }
              // vertical
              if ( y1 < y2 ) {
                  g2.drawLine( xv, y2, xv - 8, y2 - 8 );
                  g2.drawLine( xv - 8, y2 - 8, xv + 8, y2 - 8 );
                  g2.drawLine( xv + 8, y2 - 8, xv, y2 );
              }
              else {
                  g2.drawLine( xv, y2, xv - 8, y2 + 8 );
                  g2.drawLine( xv - 8, y2 + 8, xv + 8, y2 + 8 );
                  g2.drawLine( xv + 8, y2 + 8, xv, y2 );
              }
              // Draw corners
              g2.setStroke( old );
              c.translate( r.x - cornerWidth / 2, r.y - cornerWidth / 2 );
              g2.drawRect( c.x, c.y, c.width, c.height );
              c.translate( r.width, 0 );
              g2.drawRect( c.x, c.y, c.width, c.height );
              c.translate( 0, r.height );
              g2.drawRect( c.x, c.y, c.width, c.height );
              c.translate( -r.width, 0 );
              g2.drawRect( c.x, c.y, c.width, c.height );
              c.translate( r.width / 2, -r.height / 2 );
              g2.drawRect( c.x, c.y, c.width, c.height );
          }
          break;
      }

      //restore default
      g2.setColor(oldColor);
      g2.setPaintMode();

    }

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

  protected void paintAxis(Graphics g) {

    if(yAxis.isVisible()) {
      if (yAxis.getOrientation() == JLAxis.VERTICAL_RIGHT) {
        yAxis.paintAxisDirect(g,ATKGraphicsUtils.getDefaultRenderContext(),
                theImage.getWidth()-yAxis.getThickness(),0,Color.BLACK,0,0);
        if(yAxis.isDrawOpposite())
          yAxis.paintAxisOpposite(g,ATKGraphicsUtils.getDefaultRenderContext(),
                     -yAxis.getThickness(),0,Color.BLACK,0,0);
      }
      else {
        yAxis.paintAxisDirect(g,ATKGraphicsUtils.getDefaultRenderContext(),
                  -yAxis.getThickness(),0,Color.BLACK,0,0);
        if(yAxis.isDrawOpposite())
          yAxis.paintAxisOpposite(g,ATKGraphicsUtils.getDefaultRenderContext(),
                     theImage.getWidth()-yAxis.getThickness(),0,Color.BLACK,0,0);
      }
    }

    if(xAxis.isVisible()) {
      if (xAxis.getOrientation() == JLAxis.HORIZONTAL_UP) {
        xAxis.paintAxisDirect(g,ATKGraphicsUtils.getDefaultRenderContext(),
                  0,0,Color.BLACK,0,0);
        if(xAxis.isDrawOpposite())
          xAxis.paintAxisOpposite(g,ATKGraphicsUtils.getDefaultRenderContext(),
                      0,theImage.getHeight(),Color.BLACK,0,0);
      }
      else {
        xAxis.paintAxisDirect(g,ATKGraphicsUtils.getDefaultRenderContext(),
                  0,theImage.getHeight(),Color.BLACK,0,0);
        if(xAxis.isDrawOpposite())
          xAxis.paintAxisOpposite(g,ATKGraphicsUtils.getDefaultRenderContext(),
                      0,0,Color.BLACK,0,0);
      }
    }

  }

  public void paint(Graphics g) {

    Dimension d = getSize();

    //long t1 = System.currentTimeMillis();

    if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, d.width, d.height);
    }

    if(theImage == null)
      return;

    if ( xAxis.isAutoScale() ) {
      xAxis.setAutoScale(false);
      xAxis.setMinimum(0);
      xAxis.setMaximum( theImage.getWidth()/markerScaleFactor );
      xAxis.setAutoScale(true);
    }
    if ( yAxis.isAutoScale() ) {
      yAxis.setAutoScale(false);
      yAxis.setMinimum(0);
      yAxis.setMaximum( theImage.getHeight()/markerScaleFactor );
      yAxis.setAutoScale(true);
    }

    measureAxis();

    xOrg = (d.width - (theImage.getWidth() + yAxisWidth + yAxisRightMargin)) / 2 ;
    yOrg = (d.height - (theImage.getHeight() + xAxisHeight + xAxisUpMargin)) / 2;
    g.translate(xOrg + yAxisWidth , yOrg + xAxisUpMargin);
    g.drawImage(theImage, 0 , 0, null);
    paintAxis(g);
    paintSelection(g);
    paintCursor(g);

    if (markers != null) {
      int i;
      for (i = 0; i < markers.size(); i++) {
        Marker m = (Marker) markers.get(i);
        g.setColor(m.markerColor);
        int x = (int) ((double) m.markerRect.x * markerScaleFactor);
        int y = (int) ((double) m.markerRect.y * markerScaleFactor);
        int w = (int) ((double) m.markerRect.width * markerScaleFactor);
        int h = (int) ((double) m.markerRect.height * markerScaleFactor);

        Rectangle br = new Rectangle(0, 0, theImage.getWidth() + 1, theImage.getHeight() + 1);
        Rectangle mr;
        if(w==0)  mr = new Rectangle(x, y, 1, 1);
        else      mr = new Rectangle(x, y, w, h);

        if (br.contains(mr)) {
          switch (m.type) {

            case MARKER_CROSS:
              g.drawLine(x, 0, x, theImage.getHeight());
              g.drawLine(0, y, theImage.getWidth(), y);
              g.drawRect(x - 2, y - 2, 5, 5);
              break;

            case MARKER_RECT:
              g.drawRect(x, y, w, h);
              break;

            case MARKER_VLINE:
              g.drawLine(x, 0, x, theImage.getHeight());
              break;

            case MARKER_HLINE:
              g.drawLine(0, y, theImage.getWidth(), y);
              break;

          }
        }

      }
    }

    //long T = System.currentTimeMillis() - t1;
    //System.out.println("Image repaint:" + T + " ms.");

  }

  // --------------------------------------------------------
  // Measurements stuff
  // --------------------------------------------------------

  public Dimension getMinimumSize() {

    if (theImage == null) {
      return new Dimension(320, 200);
    } else {
      measureAxis();
      return new Dimension(
        theImage.getWidth() + margin.right + margin.left + yAxisWidth + yAxisRightMargin ,
        theImage.getHeight() + margin.top + margin.bottom + xAxisHeight + xAxisUpMargin );
    }

  }

  public Dimension getPreferredSize() {
    return getMinimumSize();
  }

  // --------------------------------------------------------
  // Private stuff
  // --------------------------------------------------------

  protected boolean cornerMatch(int x, int y, int xc, int yc) {
    int cw = cornerWidth / 2;
    return (x >= xc - cw) && (x <= xc + cw) && (y >= yc - cw) && (y <= yc + cw);
  }

  protected int findCorner(int x, int y) {
    if (hasSelection()) {
      int xc = (x2 + x1) / 2;
      int yc = (y2 + y1) / 2;
      if (cornerMatch(x, y, x1, y1)) return 1;
      if (cornerMatch(x, y, x2, y1) && mode != MODE_LINE) return 2;
      if (cornerMatch(x, y, x2, y2)) return 3;
      if (cornerMatch(x, y, x1, y2) && mode != MODE_LINE) return 4;
      if (cornerMatch(x, y, xc, yc)) return 5;
    }
    return 0;
  }

  protected Rectangle buildSelectionRect() {

    Rectangle r = new Rectangle();

    if (x1 < x2) {
      if (y1 < y2) {
        r.setRect(x1, y1, x2 - x1, y2 - y1);
      } else {
        r.setRect(x1, y2, x2 - x1, y1 - y2);
      }
    } else {
      if (y1 < y2) {
        r.setRect(x2, y1, x1 - x2, y2 - y1);
      } else {
        r.setRect(x2, y2, x1 - x2, y1 - y2);
      }
    }

    return r;
  }

  protected void repaintBoundingRect(Rectangle oldSel) {

    // Create bounding rect and repaint
    // Repaint only if selection has changed
    Rectangle newSel = buildSelectionRect();
    if (!newSel.equals(oldSel)) {

      int bx1,by1,bx2,by2;
      bx1 = oldSel.x;
      by1 = oldSel.y;
      bx2 = oldSel.x + oldSel.width;
      by2 = oldSel.y + oldSel.height;

      if (newSel.x < bx1) bx1 = newSel.x;
      if (newSel.y < by1) by1 = newSel.y;

      if ((newSel.width + newSel.x) > bx2)
        bx2 = newSel.width + newSel.x;

      if ((newSel.height + newSel.y) > by2)
        by2 = (newSel.height + newSel.y);

      int cw = cornerWidth / 2;

      if ( mode == MODE_LINE || mode == MODE_CROSS ) {
        repaint(0, (bx1 - cw) + xOrg + yAxisWidth - 4, (by1 - cw) + yOrg + xAxisUpMargin - 4, (bx2 - bx1) + cornerWidth + 8, (by2 - by1) + cornerWidth + 8);
      } else {
        repaint(0, (bx1 - cw) + xOrg + yAxisWidth, (by1 - cw) + yOrg + xAxisUpMargin, (bx2 - bx1) + cornerWidth + 1, (by2 - by1) + cornerWidth + 1);
      }

    }

  }

  //Reclip
  protected void clipSelection() {

    if (hasSelection()) {

      Dimension d = getImageSize();
      if (mode == MODE_LINE) {
        if (x1 >= d.width) x1 = d.width - 1;
        if (y1 >= d.height) y1 = d.height - 1;
        if (x2 >= d.width) x2 = d.width - 1;
        if (y2 >= d.height) y2 = d.height - 1;
        if (x1 < 0) x1 = 0;
        if (y1 < 0) y1 = 0;
        if (x2 < 0) x2 = 0;
        if (y2 < 0) y2 = 0;
      } else {
        if (x1 == x2) x2 += grid;
        if (y1 == y2) y2 += grid;
        // -1 here to avoid a selection mode swapping side fx
        // when a line 0,0 - 511,511 on a 512*512image
        // is converted to a rectangle
        if( d.width>1 ) {
          if (x1 >= d.width - 1) x1 = d.width;
          if (x2 >= d.width - 1) x2 = d.width;
        } else {
          x1=0;
          x2=1;
        }
        if( d.height>1 ) {
          if (y1 >= d.height - 1) y1 = d.height;
          if (y2 >= d.height - 1) y2 = d.height;
        } else {
          y1=0;
          y2=1;
        }
        if (x1 < 0) x1 = 0;
        if (y1 < 0) y1 = 0;
        if (x2 < 0) x2 = 0;
        if (y2 < 0) y2 = 0;
      }

    }

  }

  protected void alignSelection() {

    if (snapToGrid && hasSelection()) {

      if (mode == MODE_RECT) {

        x1 = x1 / grid * grid;
        x2 = x2 / grid * grid;
        y1 = y1 / grid * grid;
        y2 = y2 / grid * grid;

      } else {
        Dimension d = getImageSize();

        if (x1 >= d.width - 1)
          x1 = d.width - 1;
        else
          x1 = x1 / grid * grid;
        if (x2 >= d.width - 1)
          x2 = d.width - 1;
        else
          x2 = x2 / grid * grid;
        if (y1 >= d.height - 1)
          y1 = d.height - 1;
        else
          y1 = y1 / grid * grid;
        if (y2 >= d.height - 1)
          y2 = d.height - 1;
        else
          y2 = y2 / grid * grid;

      }

    }

  }


  // ----------------------------------------------------------
  // Mouse Listeners
  // ----------------------------------------------------------

  public void mouseDragged(MouseEvent e) {

    if(crossCursor) {
      xCursor = e.getX() - xOrg - yAxisWidth;
      yCursor = e.getY() - yOrg - xAxisUpMargin;
      repaint();
    }

    if (isDragging) {

      Rectangle oldSel = buildSelectionRect();
      int nx = e.getX() - xOrg - yAxisWidth;
      int ny = e.getY() - yOrg - xAxisUpMargin;

      // Clip
      Dimension d = getImageSize();
      if (nx < 0) nx = 0;
      if (ny < 0) ny = 0;

      if (mode == MODE_LINE) {
        if (nx >= d.width) nx = d.width - 1;
        if (ny >= d.height) ny = d.height - 1;
      } else {
        if (nx >= d.width) nx = d.width;
        if (ny >= d.height) ny = d.height;
      }

      switch (dragCorner) {

        case 1:
          x1 = nx;
          y1 = ny;
          break;

        case 2:
          x2 = nx;
          y1 = ny;
          break;

        case 3:
          x2 = nx;
          y2 = ny;
          break;

        case 4:
          x1 = nx;
          y2 = ny;
          break;

        case 5:

          //Clip
          Rectangle r = buildSelectionRect();
          int xc = r.x + r.width / 2;
          int yc = r.y + r.height / 2;
          int tx = nx - xc;
          int ty = ny - yc;

          if ((x1 + tx) < 0) tx = -x1;
          if ((x2 + tx) < 0) tx = -x2;
          if ((y1 + ty) < 0) ty = -y1;
          if ((y2 + ty) < 0) ty = -y2;

          if (mode == MODE_LINE) {
            if ((x1 + tx) >= d.width) tx = d.width - 1 - x1;
            if ((x2 + tx) >= d.width) tx = d.width - 1 - x2;
            if ((y1 + ty) >= d.height) ty = d.height - 1 - y1;
            if ((y2 + ty) >= d.height) ty = d.height - 1 - y2;
          } else {
            if ((x1 + tx) >= d.width) tx = d.width - x1;
            if ((x2 + tx) >= d.width) tx = d.width - x2;
            if ((y1 + ty) >= d.height) ty = d.height - y1;
            if ((y2 + ty) >= d.height) ty = d.height - y2;
          }

          x1 += tx;
          x2 += tx;
          y1 += ty;
          y2 += ty;
          break;
      }

      alignSelection();
      repaintBoundingRect(oldSel);

    }

  }

  public void mouseMoved(MouseEvent e) {
    if(crossCursor) {
      xCursor = e.getX() - xOrg - yAxisWidth;
      yCursor = e.getY() - yOrg - xAxisUpMargin;
      repaint();
    }
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
    if(crossCursor) {
      xCursor = -1;
      yCursor = -1;
      repaint();
    }
  }

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
    isDragging = false;
    repaint();
  }

  public void mousePressed(MouseEvent e) {

    if (e.getButton() == MouseEvent.BUTTON1) {
      grabFocus();

      int x = e.getX() - xOrg - yAxisWidth;
      int y = e.getY() - yOrg - xAxisUpMargin;

      if (selectionEnabled) {

        dragCorner = findCorner(x, y);

        if (dragCorner == 0) {

          if (!hasSelection()) {

            // New selection
            // Clip
            Dimension d = getImageSize();
            if (x > d.width || y > d.height)
              return;

            if (x < 0) x = 0;
            if (y < 0) y = 0;

            //Set new roi
            x1 = x2 = x;
            y1 = y2 = y;
            dragCorner = 3;
            repaint(0, x1 - cornerWidth, y1 - cornerWidth, x1 + cornerWidth, y1 + cornerWidth);

          }

        }

        Rectangle oldSel = buildSelectionRect();
        alignSelection();
        repaintBoundingRect(oldSel);
        isDragging = true;

      }

    }
  }

  public void keyPressed (KeyEvent e) {
    // if a selection exists, and the key is an arrow, move selection
    if (hasSelection()) {

      if (e.isShiftDown()) {

        switch (e.getKeyCode()) {
          case KeyEvent.VK_UP:
            if (horizontalPosition == HORIZONTAL_BOTTOM) {
              setHorizontalPosition(HORIZONTAL_CENTER);
            } else {
              setHorizontalPosition(HORIZONTAL_TOP);
            }
            break;
          case KeyEvent.VK_DOWN:
            if (horizontalPosition == HORIZONTAL_TOP) {
              setHorizontalPosition(HORIZONTAL_CENTER);
            } else {
              setHorizontalPosition(HORIZONTAL_BOTTOM);
            }
            break;
          case KeyEvent.VK_LEFT:
            if (verticalPosition == VERTICAL_RIGHT) {
              setVerticalPosition(VERTICAL_CENTER);
            } else {
              setVerticalPosition(VERTICAL_LEFT);
            }
            break;
          case KeyEvent.VK_RIGHT:
            if (verticalPosition == VERTICAL_LEFT) {
              setVerticalPosition(VERTICAL_CENTER);
            } else {
              setVerticalPosition(VERTICAL_RIGHT);
            }
            break;
        }

      } else {

        Dimension d = getImageSize();
        Rectangle oldSel = buildSelectionRect();
        int step = isSnapToGrid() ? grid : 1;
        switch (e.getKeyCode()) {
          case KeyEvent.VK_UP:
            if (y1 >= step && y2 >= step) {
              y1 -= step;
              y2 -= step;
            }
            break;
          case KeyEvent.VK_DOWN:
            if (y2 < d.getHeight() - step && y1 < d.getHeight() - step) {
              y1 += step;
              y2 += step;
            }
            break;
          case KeyEvent.VK_LEFT:
            if (x1 >= step && x2 >= step) {
              x1 -= step;
              x2 -= step;
            }
            break;
          case KeyEvent.VK_RIGHT:
            if (x2 < d.getWidth() - step && x1 < d.getWidth() - step) {
              x1 += step;
              x2 += step;
            }
            break;
        }
        alignSelection();
        repaintBoundingRect(oldSel);

      }
    }
  }

  public void keyReleased (KeyEvent e) {
      // nothing to do
  }

  public void keyTyped (KeyEvent e) {
      // nothing to do
  }

  public static void main (String[] args) {
      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      JImage image = new JImage();
      BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
      image.setImage( img );
      image.mode = MODE_CROSS;
      image.setSelectionColor( Color.GREEN );
      frame.setContentPane( image );
      frame.pack();
      frame.setVisible( true );
  }

}

class Marker {

  int type;
  Rectangle markerRect;
  Color markerColor;

  Marker(int _type, int x, int y, Color _markerColor) {
    type = _type;
    markerRect = new Rectangle(x, y, 0, 0);
    markerColor = _markerColor;
  }

  Marker(int _type, Rectangle rect, Color _markerColor) {
    type = _type;
    markerRect = rect;
    markerColor = _markerColor;
  }

}
