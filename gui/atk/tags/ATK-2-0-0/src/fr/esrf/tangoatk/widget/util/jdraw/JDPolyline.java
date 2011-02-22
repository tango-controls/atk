/**
 * JDraw Polyline graphic object
 */
package fr.esrf.tangoatk.widget.util.jdraw;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;

/** JDraw Polyline graphic object */
public class JDPolyline extends JDObject {

  // Default
  static final boolean isClosedDefault = true;
  static final int stepDefault = 1;

  // Vars
  boolean isClosed;
  int step;

  int breakId = -1;
  int breakX;
  int breakY;

  // -----------------------------------------------------------
  // Construction
  // -----------------------------------------------------------
  public JDPolyline() {
  }

  public JDPolyline(String objectName, Point[] p) {
    initDefault();
    summit = new Point.Double[p.length];
    for (int i = 0; i < p.length; i++) summit[i] = new Point.Double(p[i].x, p[i].y);
    name = objectName;
    updateShape();
    Point.Double org = new Point.Double(boundRect.x + boundRect.width / 2, boundRect.y + boundRect.height / 2);
    setOrigin(org);
  }

  public JDPolyline(JDPolyline e, int x, int y) {
    cloneObject(e, x, y);
    isClosed = e.isClosed;
    step = e.step;
    updateShape();
  }

  JDPolyline(JLXObject jlxObj,JLXPath p) {

    initDefault();
    loadObject(jlxObj);

    double x = jlxObj.boundRect.getX();
    double y = jlxObj.boundRect.getY();
    double w = jlxObj.boundRect.getWidth();
    double h = jlxObj.boundRect.getHeight();
    int nbp = p.path.size();

    setOrigin(new Point2D.Double(x+w/2.0, y+h/2.0));
    summit = new Point2D.Double[nbp];
    createSummit();

    for(int i=0;i<nbp;i++) {
      double[] pts = (double[])p.path.get(i);
      summit[i].x = origin.x + pts[0];
      summit[i].y = origin.y + pts[1];
    }

    isClosed = p.closed;
    step = stepDefault;

    updateShape();

  }

  // -----------------------------------------------------------
  // Overrides
  // -----------------------------------------------------------
  public void initDefault() {
    super.initDefault();
    step = stepDefault;
    isClosed = isClosedDefault;
  }

  public JDObject copy(int x, int y) {
    return new JDPolyline(this, x, y);
  }

  public void paint(Graphics g) {
    if (!visible) return;

    Graphics2D g2 = (Graphics2D) g;

    if (!isClosed) {

      if (lineWidth > 0) {
        g.setColor(foreground);
        BasicStroke bs = GraphicsUtils.createStrokeForLine(lineWidth, lineStyle);

        if (bs != null) {
          Stroke old = g2.getStroke();
          g2.setStroke(bs);
          g.drawPolyline(ptsx, ptsy, ptsx.length);
          g2.setStroke(old);
        } else {
          g.drawPolyline(ptsx, ptsy, ptsx.length);
        }
      }

    } else {

      if (fillStyle != FILL_STYLE_NONE) {
        Paint p = GraphicsUtils.createPatternForFilling(this);
        if (p != null) g2.setPaint(p);
        g.fillPolygon(ptsx, ptsy, ptsx.length);
      }

      if (lineWidth > 0) {
        g.setColor(foreground);
        BasicStroke bs = GraphicsUtils.createStrokeForLine(lineWidth, lineStyle);

        if (bs != null) {
          Stroke old = g2.getStroke();
          g2.setStroke(bs);
          g.drawPolygon(ptsx, ptsy, ptsx.length);
          g2.setStroke(old);
        } else {
          g.drawPolygon(ptsx, ptsy, ptsx.length);
        }
      }

    }

    paintShadows(g);

  }

  public int getSummitMotion(int id) {
    return JDObject.BOTH;
  }

  public void moveSummit(int id, double x, double y) {

    summit[id].x = x;
    summit[id].y = y;
    updateShape();

  }

  public void deleteSummit() {
    if (breakId < 0) return;
    Point.Double[] nSummit = new Point.Double[summit.length - 1];
    for (int i = 0,nb = 0; i < summit.length; i++)
      if (i != breakId) nSummit[nb++] = summit[i];
    summit = nSummit;
    updateShape();
  }

  public boolean canDeleteSummit(int id) {
    if (!visible) return false;
    breakId = -1;
    if ((id < 0) || (id >= summit.length) || summit.length <= 2)
      return false;
    breakId = id;
    return true;
  }

  public boolean canBreakShape(int x, int y) {
    if (!visible) return false;
    Line2D.Double l = new Line2D.Double();
    int inext;
    breakId = -1;
    for (int i = 0; i < ptsx.length; i++) {
      inext = i + 1;
      if (inext >= ptsx.length) inext -= ptsx.length;
      l.setLine(ptsx[i], ptsy[i], ptsx[inext], ptsy[inext]);
      if (l.intersects(x - 3, y - 3, 6, 6)) {
        breakId = i;
        breakX = x;
        breakY = y;
        return true;
      }
    }

    return false;
  }

  public void breakShape() {
    if (breakId < 0) return;
    // Add a summit
    Point.Double[] nSummit = new Point.Double[summit.length + 1];
    for (int i = 0,nb = 0; i < summit.length; i++) {
      nSummit[nb++] = summit[i];
      if (i == breakId) nSummit[nb++] = new Point.Double(breakX, breakY);
    }
    summit = nSummit;
    updateShape();
  }

  public boolean isInsideObject(int x, int y) {
    if (!super.isInsideObject(x, y)) return false;

    boolean found = false;
    int i = 0;

    if (fillStyle != FILL_STYLE_NONE && isClosed) {

      Polygon p = new Polygon(ptsx, ptsy, ptsx.length);
      found = p.contains(x, y);

    } else {

      while (i < (ptsx.length - 1) && !found) {
        found = isPointOnLine(x, y, ptsx[i], ptsy[i], ptsx[i + 1], ptsy[i + 1]);
        if (!found) i++;
      }

      if (!found && isClosed) {
        // Check last line
        found = isPointOnLine(x, y, ptsx[i], ptsy[i], ptsx[0], ptsy[0]);
      }

    }

    return found;
  }

  // -----------------------------------------------------------
  // Property
  // -----------------------------------------------------------
  public boolean isClosed() {
    return isClosed;
  }

  public void setClosed(boolean b) {
    isClosed = b;
    updateShape();
  }

  public int getStep() {
    return step;
  }

  public void setStep(int s) {
    step = 1;
  }

  // -----------------------------------------------------------
  // File management
  // -----------------------------------------------------------
  public void saveObject(FileWriter f, int level) throws IOException {

    String decal = saveObjectHeader(f, level);

    String to_write;

    if (step != stepDefault) {
      to_write = decal + "step:" + step + "\n";
      f.write(to_write, 0, to_write.length());
    }

    if (isClosed != isClosedDefault) {
      to_write = decal + "isClosed:" + isClosed + "\n";
      f.write(to_write, 0, to_write.length());
    }

    closeObjectHeader(f, level);

  }

  public JDPolyline(JDFileLoader f) throws IOException {
    initDefault();
    int l = f.getCurrentLine();

    f.startBlock();
    summit = f.parseSummitArray();
    if (summit.length < 2)
      throw new IOException("Invalid summit number for JDPolyline at line " + l);

    while (!f.isEndBlock()) {
      String propName = f.parseProperyName();
      if (propName.equals("isClosed")) {
        isClosed = f.parseBoolean();
      } else if (propName.equals("step")) {
        step = (int) f.parseDouble();
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

    UndoPattern u = new UndoPattern(UndoPattern._JDPolyline);
    fillUndoPattern(u);
    u.step = step;
    u.isClosed = isClosed;

    return u;
  }

  JDPolyline(UndoPattern e) {
     initDefault();
     applyUndoPattern(e);
     step = e.step;
     isClosed = e.isClosed;
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

    if (hasShadow()) {
      computeShadow(true);
      computeShadowColors();
    }

  }

}