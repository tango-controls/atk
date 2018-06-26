package fr.esrf.tangoatk.widget.util.jdraw;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/** JDraw Title rectangle graphic object.
 */
public class JDTitledRect extends JDRectangular {

  // Default properties
  static final String textDefault = "Title";
  static final Font fontDefault = new Font("Dialog", Font.BOLD, 14);
  static final int rMargin = 10;
  static final Color color1Default = Color.BLACK;
  static final Color color2Default = Color.WHITE;

  // Vars
  static BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
  private String theTitle;
  private Font theFont;
  private Color c1;
  private Color c2;
  private Dimension preferredSize = null;
  private int wTitle = 0;
  private int hTitle = 0;

  /**
   * Construcxt a JDTitledRect.
   * @param objectName Object name
   * @param title Title
   * @param x Up left corner x coordinate
   * @param y Up left corner y coordinate
   */
  public JDTitledRect(String objectName, String title, int x, int y) {
    initDefault();
    setOrigin(new Point.Double(0.0, 0.0));
    summit = new Point.Double[8];
    name = objectName;
    theTitle = title;
    Dimension d = getMinSize();
    createSummit();
    computeSummitCoordinates(x,y,d.width, d.height);
    updateShape();
    centerOrigin();
  }

  JDTitledRect(JDTitledRect e, int x, int y) {
    cloneObject(e, x, y);
    theTitle = new String(e.theTitle);
    theFont = new Font(e.theFont.getName(), e.theFont.getStyle(), e.theFont.getSize());
    c1 = new Color(e.c1.getRGB());
    c2 = new Color(e.c2.getRGB());
    updateShape();
  }

  // -----------------------------------------------------------
  // Overrides
  // -----------------------------------------------------------
  void initDefault() {
    super.initDefault();
    theTitle = textDefault;
    theFont = fontDefault;
    c1 = color1Default;
    c2 = color2Default;
  }

  public JDTitledRect copy(int x, int y) {
    return new JDTitledRect(this, x, y);
  }

  public void paint(JDrawEditor parent,Graphics g) {

    if (!visible) return;
    Graphics2D g2 = (Graphics2D) g;

    // G2 initilialisation ----------------------------------

    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
        RenderingHints.VALUE_FRACTIONALMETRICS_ON);

    g2.setFont(theFont);
    FontRenderContext frc = g2.getFontRenderContext();
    int fa = (int) Math.ceil(g.getFont().getLineMetrics("ABC", frc).getAscent());

    // Draw title if visible
    Rectangle2D bounds = g.getFont().getStringBounds(theTitle, frc);
    wTitle = (int) Math.ceil(bounds.getWidth());
    hTitle = (int) Math.ceil(bounds.getHeight());

    // Draw border
    g2.setColor(foreground);
    int x1 = boundRect.x;
    int y1 = boundRect.y + hTitle/2;
    int x2 = boundRect.x + boundRect.width - lineWidth;
    int y2 = boundRect.y + boundRect.height - lineWidth;
    int h1 = x1 + rMargin;
    int h2 = x1 + 2*rMargin + wTitle;


    if(boundRect.height>hTitle/2) {

      if(fillStyle!=JDObject.FILL_STYLE_NONE) {
        Paint p = GraphicsUtils.createPatternForFilling(this);
        if(p!=null) g2.setPaint(p);
        g.fillRect(x1,y1,x2-x1,y2-y1);
      }

      if( boundRect.width> 2*rMargin + wTitle &&
          boundRect.height > hTitle+2 ) {

        if(fillStyle!=JDObject.FILL_STYLE_NONE) {

          Paint p = GraphicsUtils.createPatternForFilling(this);
          if(p!=null) g2.setPaint(p);
          g.fillRect(boundRect.x+rMargin,boundRect.y,wTitle+rMargin,hTitle);
          paintRect(g2,foreground,boundRect.x+rMargin,boundRect.y,wTitle+rMargin,hTitle);

        }

        int xpos = boundRect.x + rMargin + rMargin/2;
        int ypos = boundRect.y + fa;
        g2.setColor(foreground);
        g2.drawString(theTitle, xpos, ypos);

      }

      paintRect(g2,c1,x1,y1,x2,y2,h1,h2);
      paintRect(g2,c2,x1+lineWidth,y1+lineWidth,x2+lineWidth,y2+lineWidth,h1,h2);

    }


  }

  private void paintRect(Graphics2D g2,Color c,int x,int y,int w,int h) {

    if(lineWidth>0) {

      BasicStroke bs = GraphicsUtils.createStrokeForLine(lineWidth, lineStyle);
      Stroke old = null;

      if (bs != null) {
        old = g2.getStroke();
        g2.setStroke(bs);
      }

      g2.setColor(foreground);
      g2.drawLine(x, y, x + w, y);
      g2.drawLine(x + w, y, x + w, y + h);
      g2.drawLine(x + w, y + h, x, y + h);
      g2.drawLine(x,y+h,x,y);

      if (old != null)
        g2.setStroke(old);

    }

  }

  private void paintRect(Graphics2D g2,Color c,int x1,int y1,int x2,int y2,int h1,int h2) {

    if(lineWidth<1)
      return;

    g2.setColor(c);

    BasicStroke bs = GraphicsUtils.createStrokeForLine(lineWidth, lineStyle);
    Stroke old = null;

    if (bs != null) {
      old = g2.getStroke();
      g2.setStroke(bs);
    }

    if(h1<x2) {
      g2.drawLine(x1,y1,h1,y1);
      if( h2<x2 )
        g2.drawLine(h2,y1,x2,y1);
    }

    g2.drawLine(x2,y1,x2,y2);
    g2.drawLine(x2,y2,x1,y2);
    g2.drawLine(x1,y2,x1,y1);

    if (old != null)
      g2.setStroke(old);

  }

  public boolean isInsideObject(int x, int y) {

    if(!super.isInsideObject(x,y)) return false;

    if (fillStyle != FILL_STYLE_NONE) {
      return boundRect.contains(x, y);
    } else {
      int x1 = boundRect.x;
      int x2 = boundRect.x + boundRect.width;
      int y1 = boundRect.y + hTitle/2;
      int y2 = boundRect.y + boundRect.height;

      return isPointOnLine(x, y, x1, y1, x2, y1) ||
          isPointOnLine(x, y, x2, y1, x2, y2) ||
          isPointOnLine(x, y, x2, y2, x1, y2) ||
          isPointOnLine(x, y, x1, y2, x1, y1);
    }

  }

  void updateShape() {

    computeBoundRect();

  }

  // -----------------------------------------------------------
  // Property stuff
  // -----------------------------------------------------------

  /**
   * Sets the font of this label and resize it if needed and specified.
   * @param f Font
   */
  public void setFont(Font f) {
    theFont = f;
  }

  /**
   * Returns the current font of this label.
   */
  public Font getFont() {
    return theFont;
  }
  /**
   * Sets the title of this titled rectangle.
   * @param s Title
   */
  public void setTitle(String s) {
    theTitle = s;
  }

  /**
   * Returns the current label text.
   */
  public String getTitle() {
    return theTitle;
  }

  /**
   * Sets the line color
   * @param c Line color
   */
  public void setColor1(Color c) {
    c1 = c;
  }

  /**
   * Returns the line color
   */
  public Color getColor1() {
    return c1;
  }

  /**
   * Sets the line color
   * @param c Line color
   */
  public void setColor2(Color c) {
    c2 = c;
  }

  /**
   * Returns the line color
   */
  public Color getColor2() {
    return c2;
  }

  // -----------------------------------------------------------
  // File management
  // -----------------------------------------------------------
  void recordObject(StringBuffer to_write, int level) {

    StringBuffer decal = recordObjectHeader(to_write, level);

    if (theFont.getName() != fontDefault.getName() ||
        theFont.getStyle() != fontDefault.getStyle() ||
        theFont.getSize() != fontDefault.getSize()) {
      to_write.append(decal).append("font:\"");
      to_write.append(theFont.getName()).append("\",");
      to_write.append(theFont.getStyle()).append(",");
      to_write.append(theFont.getSize()).append("\n");
    }

    if (!theTitle.equals(textDefault)) {
      to_write.append(decal).append("title:");
      to_write.append("\"").append(theTitle).append("\"\n");
    }

    if (c1.getRGB() != color1Default.getRGB()) {
      to_write.append(decal).append("color1:");
      to_write.append(c1.getRed()).append(",");
      to_write.append(c1.getGreen()).append(",");
      to_write.append(c1.getBlue());
      if(c1.getAlpha()!=255)
        to_write.append(",").append(foreground.getAlpha());
      to_write.append("\n");
    }

    if (c2.getRGB() != color2Default.getRGB()) {
      to_write.append(decal).append("color2:");
      to_write.append(c2.getRed()).append(",");
      to_write.append(c2.getGreen()).append(",");
      to_write.append(c2.getBlue());
      if(c2.getAlpha()!=255)
        to_write.append(",").append(foreground.getAlpha());
      to_write.append("\n");
    }

    closeObjectHeader(to_write, level);

  }

  JDTitledRect(JDFileLoader f) throws IOException {

    initDefault();
    f.startBlock();
    summit = f.parseRectangularSummitArray();

    while (!f.isEndBlock()) {
      String propName = f.parseProperyName();
      if (propName.equals("title")) {
        theTitle = f.parseString();
      } else if (propName.equals("font")) {
        theFont = f.parseFont();
      } else if (propName.equals("color1")) {
        c1 = f.parseColor();
      } else if (propName.equals("color2")) {
        c2 = f.parseColor();
      } else
        loadDefaultPropery(f, propName);
    }

    f.endBlock();

    updateShape();
  }

  // -----------------------------------------------------------
  // Undo buffer
  // -----------------------------------------------------------
  UndoPattern getUndoPattern() {

    UndoPattern u = new UndoPattern(UndoPattern._JDTitledRect);
    fillUndoPattern(u);
    u.fName = theFont.getName();
    u.fStyle = theFont.getStyle();
    u.fSize = theFont.getSize();
    u.text = new String(theTitle);
    u.c1 = c1.getRGB();
    u.c2 = c2.getRGB();

    return u;
  }

  JDTitledRect(UndoPattern e) {
    initDefault();
    applyUndoPattern(e);
    theFont = new Font(e.fName,e.fStyle,e.fSize);
    theTitle = e.text;
    c1 = new Color(e.c1);
    c2 = new Color(e.c2);
    updateShape();
  }

  // -----------------------------------------------------------
  // Private stuff
  // -----------------------------------------------------------

  // Compute summit coordinates from width, height
  // 0 1 2
  // 7   3
  // 6 5 4
  private void computeSummitCoordinates(int x,int y,int width, int height) {

    // Compute summit

    summit[0].x = x;
    summit[0].y = y;

    summit[2].x = x + width;
    summit[2].y = y;

    summit[4].x = x + width;
    summit[4].y = y + height;

    summit[6].x = x;
    summit[6].y = y + height;

    centerSummit();

  }

  private Dimension getMinSize() {

    if (preferredSize == null) {

      Graphics g = img.getGraphics();
      g.setFont(theFont);
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
          RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      FontRenderContext frc = g2.getFontRenderContext();

      Rectangle2D bounds = g.getFont().getStringBounds(theTitle, frc);
      int w = (int) Math.ceil(bounds.getWidth());
      int h = (int) Math.ceil(bounds.getHeight());
      g.dispose();
      preferredSize = new Dimension(w+2*rMargin, h + 2);
    }

    return preferredSize;

  }


}
