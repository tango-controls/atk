/**
 * JDraw Line graphic object
 */
package fr.esrf.tangoatk.widget.util.jdraw;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;

/** JDraw Line graphic object */
public class JDLine extends JDObject {

  // Global vars
  final public static int ARROW_NONE = 0;
  final public static int ARROW1_LEFT = 1;
  final public static int ARROW1_RIGHT = 2;
  final public static int ARROW1_BOTH = 3;
  final public static int ARROW1_CENTER = 4;
  final public static int ARROW2_LEFT = 5;
  final public static int ARROW2_RIGHT = 6;
  final public static int ARROW2_BOTH = 7;
  final public static int ARROW2_CENTER = 8;

  // Default
  final static int arrowModeDefault = ARROW_NONE;
  final static int arrowWidthDefault = 7;

  // Vars
  private int arrowMode = ARROW_NONE;
  Polygon[] arrows = null;
  int arrowWidth;
  int[] ashx;
  int[] ashy;

  // -----------------------------------------------------------
  // Construction
  // -----------------------------------------------------------
  public JDLine(String objectName, int x1, int y1, int x2, int y2) {
    initDefault();
    summit = new Point.Double[2];
    summit[0] = new Point.Double(x1, y1);
    summit[1] = new Point.Double(x2, y2);
    name = objectName;
    updateShape();
    Point.Double org = new Point.Double((x1+x2)/2,(y1+y2)/2);
    setOrigin(org);
  }

  public JDLine(JDLine e, int x, int y) {
    cloneObject(e, x, y);
    arrowMode = e.arrowMode;
    arrowWidth = e.arrowWidth;
    ashx = new int[4];
    ashy = new int[4];
    updateShape();
  }

  public JDObject copy(int x, int y) {
    return new JDLine(this, x, y);
  }

  JDLine(JLXObject jlxObj,JLXPath p) {

    initDefault();
    loadObject(jlxObj);

    double x = jlxObj.boundRect.getX();
    double y = jlxObj.boundRect.getY();
    double w = jlxObj.boundRect.getWidth();
    double h = jlxObj.boundRect.getHeight();

    setOrigin(new Point2D.Double(x+w/2.0, y+h/2.0));
    summit = new Point2D.Double[2];
    createSummit();
    
    for(int i=0;i<2;i++) {
      double[] pts = (double[])p.path.get(i);
      summit[i].x = origin.x + pts[0];
      summit[i].y = origin.y + pts[1];
    }

    arrowMode = p.arrow;

    updateShape();

  }

  // -----------------------------------------------------------
  // Ovverides
  // -----------------------------------------------------------
  public void initDefault() {
    super.initDefault();
    ashx = new int[4];
    ashy = new int[4];
    arrowMode = arrowModeDefault;
    arrowWidth = arrowWidthDefault;
  }

  public void setArrow(int arrow) {
    arrowMode = arrow;
    updateShape();
  }

  public int getArrow() {
    return arrowMode;
  }

  public void setArrowSize(int s) {
    arrowWidth = s;
    updateShape();
  }

  public int getArrowSize() {
    return arrowWidth;
  }

  public void paint(Graphics g) {
    if (!visible) return;

    Graphics2D g2 = (Graphics2D) g;

    if (lineWidth > 0) {
      g.setColor(foreground);
      BasicStroke bs = GraphicsUtils.createStrokeForLine(lineWidth, lineStyle);

      if (bs != null) {
        Stroke old = g2.getStroke();
        g2.setStroke(bs);
        g2.drawPolyline(ptsx, ptsy, summit.length);
        g2.setStroke(old);
      } else {
        g.drawPolyline(ptsx, ptsy, summit.length);
      }
    }

    // Paint arrow
    if (arrows != null) {

      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);

      for (int i = 0; i < arrows.length; i++) {
        g2.setColor(foreground);
        g2.fillPolygon(arrows[i]);
      }

      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_OFF);
    }

  }

  public boolean hasShadow() {
    return false;
  }

  public Rectangle getShadowBoundRect() {
    return null;
  }

  public int getSummitMotion(int id) {
    return JDObject.BOTH;
  }

  public void moveSummit(int id, double x, double y) {

    summit[id].x = x;
    summit[id].y = y;
    updateShape();

  }

  public boolean isInsideObject(int x, int y) {
    if (!super.isInsideObject(x, y)) return false;
    return isPointOnLine(x, y, (int) summit[0].x, (int) summit[0].y, (int) summit[1].x, (int) summit[1].y);
  }

  public Rectangle getRepaintRect() {

    Rectangle r = super.getRepaintRect();

    if (arrowMode != ARROW_NONE) {
      r.x -= arrowWidth;
      r.y -= arrowWidth;
      r.width += 2 * arrowWidth;
      r.height += 2 * arrowWidth;
    }

    return r;

  }

  public void setLineWidth(int width) {
    lineWidth = width;
    updateShape();
  }

  // -----------------------------------------------------------
  // File management
  // -----------------------------------------------------------
  public void saveObject(FileWriter f, int level) throws IOException {

    String decal = saveObjectHeader(f, level);

    String to_write;

    if (arrowWidth != arrowWidthDefault) {
      to_write = decal + "arrowWidth:" + arrowWidth + "\n";
      f.write(to_write, 0, to_write.length());
    }

    if (arrowMode != arrowModeDefault) {
      to_write = decal + "arrowMode:" + arrowMode + "\n";
      f.write(to_write, 0, to_write.length());
    }

    closeObjectHeader(f, level);

  }

  public JDLine(JDFileLoader f) throws IOException {
    initDefault();
    int l = f.getCurrentLine();

    f.startBlock();
    summit = f.parseSummitArray();
    if (summit.length != 2)
      throw new IOException("Invalid summit number for JDLine at line " + l);

    while (!f.isEndBlock()) {
      String propName = f.parseProperyName();
      if (propName.equals("arrowMode")) {
        arrowMode = (int) f.parseDouble();
      } else if (propName.equals("arrowWidth")) {
        arrowWidth = (int) f.parseDouble();
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

    UndoPattern u = new UndoPattern(UndoPattern._JDLine);
    fillUndoPattern(u);
    u.arrowMode = arrowMode;
    u.arrowWidth = arrowWidth;

    return u;
  }

  JDLine(UndoPattern e) {
     initDefault();
     applyUndoPattern(e);
     arrowMode = e.arrowMode;
     arrowWidth = e.arrowWidth;
     updateShape();
   }

  // -----------------------------------------------------------
  // Private stuff
  // -----------------------------------------------------------
  // Compute pts
  void updateShape() {

    computeBoundRect();

    ptsx = new int[summit.length];
    ptsy = new int[summit.length];
    for (int i = 0; i < summit.length; i++) {
      ptsx[i] = (int) summit[i].x;
      ptsy[i] = (int) summit[i].y;
    }

    if (arrowMode != ARROW_NONE) {

      // Compute arrow polygon
      double nx = -(ptsy[1] - ptsy[0]);
      double ny = (ptsx[1] - ptsx[0]);
      double n = Math.sqrt(nx * nx + ny * ny);
      double aw = (double) arrowWidth;
      double lw = (double) (lineWidth-2)/2.0;
      if(lw<0.0) lw=0.0;

      if (n < 1.0) {
        // Cannot build arrow for null line
        arrows = null;
      } else {

        int i = 0;
        double dx = (double)(ptsx[1] - ptsx[0])  * aw/n;
        double dy = (double)(ptsy[1] - ptsy[0])  * aw/n;
        double dpx = (double)(ptsx[1] - ptsx[0]) * lw/n;
        double dpy = (double)(ptsy[1] - ptsy[0]) * lw/n;

        if (arrowMode == ARROW1_BOTH || arrowMode == ARROW2_BOTH)
          arrows = new Polygon[2];
        else
          arrows = new Polygon[1];

        if (arrowMode == ARROW1_BOTH || arrowMode == ARROW1_LEFT) {

          ashx[0] = (int)( ptsx[0] + dx + (-nx / n * aw) + 0.5);
          ashy[0] = (int)( ptsy[0] + dy + (-ny / n * aw) + 0.5);
          ashx[1] = (int)( ptsx[0] + dx + (nx / n * aw) + 0.5);
          ashy[1] = (int)( ptsy[0] + dy + (ny / n * aw) + 0.5);
          ashx[2] = ptsx[0];
          ashy[2] = ptsy[0];

          ptsx[0] += (int)(dx+dpx+0.5);
          ptsy[0] += (int)(dy+dpy+0.5);

          arrows[i] = new Polygon(ashx, ashy, 3);
          i++;
        }

        if (arrowMode == ARROW1_BOTH || arrowMode == ARROW1_RIGHT) {

          ashx[0] = (int) (ptsx[1] - dx + (-nx/n * aw) + 0.5);
          ashy[0] = (int) (ptsy[1] - dy + (-ny/n * aw) + 0.5);
          ashx[1] = (int) (ptsx[1] - dx + (nx/n * aw) + 0.5);
          ashy[1] = (int) (ptsy[1] - dy + (ny/n * aw) + 0.5);
          ashx[2] = ptsx[1];
          ashy[2] = ptsy[1];

          ptsx[1] -= (int)(dx+dpx+0.5);
          ptsy[1] -= (int)(dy+dpy+0.5);

          arrows[i] = new Polygon(ashx, ashy, 3);
        }

        if (arrowMode == ARROW1_CENTER) {

          double xc = (double)(ptsx[1] + ptsx[0]) / 2.0;
          double yc = (double)(ptsy[1] + ptsy[0]) / 2.0;

          ashx[0] = (int)(0.5 + xc + (-nx / n * aw));
          ashy[0] = (int)(0.5 + yc + (-ny / n * aw));
          ashx[1] = (int)(0.5 + xc + (nx / n * aw));
          ashy[1] = (int)(0.5 + yc + (ny / n * aw));
          ashx[2] = (int)(0.5 + xc + (dx));
          ashy[2] = (int)(0.5 + yc + (dy));

          arrows[i] = new Polygon(ashx, ashy, 3);
        }

        if (arrowMode == ARROW2_BOTH || arrowMode == ARROW2_LEFT) {

          ashx[0] = (int)( ptsx[0] + dx + (-nx / n * aw) + (dx / 2.0) + 0.5);
          ashy[0] = (int)( ptsy[0] + dy + (-ny / n * aw) + (dy / 2.0) + 0.5);
          ashx[1] = (int)( ptsx[0] + dx + 0.5);
          ashy[1] = (int)( ptsy[0] + dy + 0.5);
          ashx[2] = (int)( ptsx[0] + dx + (nx / n * aw) + (dx / 2.0) + 0.5);
          ashy[2] = (int)( ptsy[0] + dy + (ny / n * aw) + (dy / 2.0) + 0.5);
          ashx[3] = ptsx[0];
          ashy[3] = ptsy[0];

          arrows[i] = new Polygon(ashx, ashy, 4);
          i++;

          ptsx[0] += (int)(dx+dpx+0.5);
          ptsy[0] += (int)(dy+dpy+0.5);

        }

        if (arrowMode == ARROW2_BOTH || arrowMode == ARROW2_RIGHT) {

          ashx[0] = (int)( ptsx[1] - dx + (-nx / n * aw) - (dx / 2.0) + 0.5);
          ashy[0] = (int)( ptsy[1] - dy + (-ny / n * aw) - (dy / 2.0) + 0.5);
          ashx[1] = (int)( ptsx[1] - dx + 0.5);
          ashy[1] = (int)( ptsy[1] - dy + 0.5);
          ashx[2] = (int)( ptsx[1] - dx + (nx / n * aw) - (dx / 2.0) + 0.5);
          ashy[2] = (int)( ptsy[1] - dy + (ny / n * aw) - (dy / 2.0) + 0.5);
          ashx[3] = ptsx[1];
          ashy[3] = ptsy[1];

          ptsx[1] -= (int)(dx+dpx+0.5);
          ptsy[1] -= (int)(dy+dpy+0.5);

          arrows[i] = new Polygon(ashx, ashy, 4);
        }

        if (arrowMode == ARROW2_CENTER) {

          int xc = (ptsx[1] + ptsx[0]) / 2;
          int yc = (ptsy[1] + ptsy[0]) / 2;

          ashx[0] = xc + (int) (-nx / n * aw) - (int) (dx / 2.0);
          ashy[0] = yc + (int) (-ny / n * aw) - (int) (dy / 2.0);
          ashx[1] = xc;
          ashy[1] = yc;
          ashx[2] = xc + (int) (nx / n * aw) - (int) (dx / 2.0);
          ashy[2] = yc + (int) (ny / n * aw) - (int) (dy / 2.0);
          ashx[3] = xc + (int) (dx);
          ashy[3] = yc + (int) (dy);

          arrows[i] = new Polygon(ashx, ashy, 4);

        }

      }

    } else {
      arrows = null;
    }

  }

}