/**
 * JDraw Group graphic object
 */

package fr.esrf.tangoatk.widget.util.jdraw;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

/** JDraw Group graphic object */
public class JDGroup extends JDRectangular {

  // Vars
  Vector children;

  double x1;
  double y1;
  double x2;
  double y2;

  // -----------------------------------------------------------
  // Construction
  // -----------------------------------------------------------
  public JDGroup(String objectName, Vector o) {
    initDefault();
    children = new Vector();
    for (int i = 0; i < o.size(); i++) children.add(o.get(i));
    summit = new Point.Double[8];
    name = objectName;
    createSummit();
    computeSummitCoordinates();
    updateShape();
  }

  public JDGroup(JDGroup e, int x, int y) {
    cloneObject(e, x, y);

    // Clone child
    children = new Vector();
    for (int i = 0; i < e.children.size(); i++)
      children.add(((JDObject) e.children.get(i)).copy(x, y));

    computeGroupBoundRect();
    updateShape();
  }

  public JDObject copy(int x, int y) {
    return new JDGroup(this, x, y);
  }

  JDGroup(JLXObject jlxObj, Vector o) {
    initDefault();
    loadObject(jlxObj);
    children = o;
    summit = new Point.Double[8];
    createSummit();
    computeSummitCoordinates();
    updateShape();
  }

  // -----------------------------------------------------------
  // Overrides
  // -----------------------------------------------------------

  public void paint(Graphics g) {
    if (!visible) return;

    if (boundRect.width <= 1 || boundRect.height <= 1) {
      if( Math.abs(summit[4].x-summit[0].x)<0.5 ||
          Math.abs(summit[4].y-summit[0].y)<0.5  )
      return;
    }

    for (int i = 0; i < children.size(); i++)
      ((JDObject) children.get(i)).paint(g);
  }

  void paintShadows(Graphics g) {}

  public boolean isInsideObject(int x, int y) {
    if (!super.isInsideObject(x, y)) return false;

    boolean found = false;

    for (int i = children.size() - 1; i >= 0 && !found; i--)
      found = ((JDObject) children.get(i)).isInsideObject(x, y);

    return found;
  }

  public void updateShape() {

    int i;

    // Does not compute anything for null width group
    if( Math.abs(summit[4].x-summit[0].x)<0.5 ||
        Math.abs(summit[4].y-summit[0].y)<0.5  )
      return;

    //Compute transformation for chidlren

    double nx1 = summit[0].x;
    double ny1 = summit[0].y;
    double nx2 = summit[4].x;
    double ny2 = summit[4].y;

    // Translation
    double tx = nx1 - x1;
    double ty = ny1 - y1;

    // Scale
    double sx = (nx2 - nx1) / (x2 - x1);
    double sy = (ny2 - ny1) / (y2 - y1);

    if (sx == 1.0 && sy == 1.0) {
      // Translation
      for (i = 0; i < children.size(); i++)
        ((JDObject) children.get(i)).translate(tx, ty);
    } else {
      // Scaling
      for (i = 0; i < children.size(); i++)
        ((JDObject) children.get(i)).scaleTranslate(x1, y1, sx, sy, tx, ty);
    }


    // Compute boundrects
    computeGroupBoundRect();
    computeBoundRect();

  }

  public void restoreTransform() {

    for(int i=0;i<children.size();i++)
      ((JDObject)children.get(i)).restoreTransform();
    computeGroupBoundRect();
    super.restoreTransform();

  }

  public void saveTransform() {

    for(int i=0;i<children.size();i++)
      ((JDObject)children.get(i)).saveTransform();
    super.saveTransform();

  }

  public void rotate90(double x,double y) {

    for(int i=0;i<getChildrenNumber();i++)
      getChildAt(i).rotate90(x,y);
    computeSummitCoordinates();
    updateShape();
  
  }

  // -----------------------------------------------------------
  // Overrided Property
  // -----------------------------------------------------------
  public Rectangle getRepaintRect() {

    // Compute repaint rectangle
    Rectangle r = null;
    for(int i=0;i<children.size();i++) {
      if(i==0) r=((JDObject)children.get(i)).getRepaintRect();
      else r = r.union(((JDObject)children.get(i)).getRepaintRect());
    }

    if( r==null ) {
      int sw = (lineWidth + 1);
      return new Rectangle(boundRect.x - sw, boundRect.y - sw,
            boundRect.width + sw * 2, boundRect.height + sw * 2);

    } else
      return  r;

  }

  public void setBackground(Color c) {
    background = c;
    for (int i = 0; i < children.size(); i++)
      ((JDObject) children.get(i)).setBackground(c);
  }

  public void setForeground(Color c) {
    foreground = c;
    for (int i = 0; i < children.size(); i++)
      ((JDObject) children.get(i)).setForeground(c);
  }

  public void setFillStyle(int s) {
    fillStyle = s;
    for (int i = 0; i < children.size(); i++)
      ((JDObject) children.get(i)).setFillStyle(s);
  }

  public void setLineStyle(int s) {
    lineStyle = s;
    for (int i = 0; i < children.size(); i++)
      ((JDObject) children.get(i)).setLineStyle(s);
  }

  public void setLineWidth(int w) {
    lineWidth = w;
    for (int i = 0; i < children.size(); i++)
      ((JDObject) children.get(i)).setLineWidth(w);
  }

  public void setShadow(boolean b) {
    isShadowed = b;
    for (int i = 0; i < children.size(); i++)
      ((JDObject) children.get(i)).setShadow(b);
  }

  public void setInverseShadow(boolean b) {
    invertShadow = b;
    for (int i = 0; i < children.size(); i++)
      ((JDObject) children.get(i)).setInverseShadow(b);
  }

  public void setShadowWidth(int w) {
    shadowThickness = w;
    for (int i = 0; i < children.size(); i++)
      ((JDObject) children.get(i)).setShadowWidth(w);
  }

  void setVal(int v,JDObject master) {
    // First spread the value over children
    for (int i = 0; i < children.size(); i++)
      ((JDObject) children.get(i)).setVal(v,master);

    super.setVal(v,master);
  }

  public void initVal(JDObject master) {
    JDObject m = isInteractive()?this:master;
    super.initVal(m);
    for (int i=0;i<children.size();i++)
      getChildAt(i).initVal(m);
  }

  public void setMinValue(int v) {
    for (int i=0;i<children.size();i++)
      getChildAt(i).setMinValue(v);
    super.setMinValue(v);
  }

  public void setMaxValue(int v) {
    for (int i=0;i<children.size();i++)
      getChildAt(i).setMaxValue(v);
    super.setMaxValue(v);
  }

  public void setInitValue(int v) {
    for (int i=0;i<children.size();i++)
      getChildAt(i).setInitValue(v);
    super.setInitValue(v);
  }

  void findObjectsAt(int x,int y,Vector result) {

    if( isInteractive() ) {
      super.findObjectsAt(x,y,result);
      return;
    }

    for (int i=0;i<children.size();i++)
      getChildAt(i).findObjectsAt(x,y,result);

  }
  
  void getUserValueList(Vector result) {
    if(isInteractive()) {
      result.add(this);
    } else {
      for (int i=0;i<children.size();i++)
        getChildAt(i).getUserValueList(result);
    }
  }

  public boolean isProgrammed() {
    boolean ret = super.isProgrammed();
    for (int i=0;i<children.size();i++)
      ret |= getChildAt(i).isProgrammed();
    return ret;
  }

  // -----------------------------------------------------------
  // Children management
  // -----------------------------------------------------------

  public JDObject getChildAt(int idx) {
    return (JDObject)children.get(idx);
  }

  public int getChildrenNumber() {
    return children.size();
  }

  public void setChildrenList(Vector o) {
    children = new Vector();
    for (int i = 0; i < o.size(); i++) children.add(o.get(i));
    computeSummitCoordinates();
    updateShape();
  }

  public Vector getChildren() {
    return children;
  }

  /**
   * Generates a Java class capable to paint this object using only 
   * java awt functions. It supports only Line and Polyline (or
   * shape that can be converted to polyline) and not all
   * functionality of the JDObject are suppported. Nevertheless,
   * the code keeps the vectorial sizing behavior.
   * <strong>Note</strong>: All objects named 'body' will have their background
   * color overrided by the color passed to the generated paint()
   * function.
   * @param f file to be saved
   */
  public void generateJavaClass(FileWriter f) throws IOException {

    int i,j,k;
    int xOrg =  boundRect.x + boundRect.width/2;
    int yOrg =  boundRect.y + boundRect.height/2;

    // An inner class to handle color factorisation
    class ColorManager {
      Vector colors;
      ColorManager() {colors = new Vector();}
      int addColor(Color c) {
        if(c.equals(Color.black))
          return -1;
        if(c.equals(Color.white))
          return -2;
        int i=0;
        boolean found=false;
        while(i<colors.size() && !found) {
          found = ((Color)colors.get(i)).getRGB() == c.getRGB();
          if(!found) i++;
        }
        if(!found) colors.add(c);
        return i;
      }
      Color get(int i) {
        return (Color)colors.get(i);
      }
      String getName(int i) {
        if( i==-1 ) {
          return "Color.black";
        } else if (i==-2) {
          return "Color.white";
        } else if (i==-3) {
          return "backColor";
        } else {
          return "sColor" + i;
        }
      }
    }

    // An inner class to handle object conversion
    class ObjInfo {
      int     type;
      boolean closed;
      int[]   xPolys;
      int[]   yPolys;
      int     backgroundId;
      int     foregroundId;
      void setValue(JDPolyline p,int xOrg,int yOrg,ColorManager c) {
        type = 1;
        xPolys = new int[p.getSummitNumber()];
        yPolys = new int[p.getSummitNumber()];
        for(int j=0;j<p.getSummitNumber();j++) {
          xPolys[j] = (int)p.getSummit(j).x - xOrg;
          yPolys[j] = (int)p.getSummit(j).y - yOrg;
        }
        if( p.getName().equalsIgnoreCase("body") ) {
          backgroundId = -3;
          foregroundId = -1;
        } else {
          backgroundId = c.addColor(p.getBackground());
          foregroundId = c.addColor(p.getForeground());
        }
        closed = p.isClosed();
      }
    }


    // Build polygons array
    ObjInfo[] objInfo = new ObjInfo[getChildrenNumber()];
    ColorManager colorManager = new ColorManager();

    for(i=0;i<getChildrenNumber();i++) {

      objInfo[i] = new ObjInfo();

      if( getChildAt(i) instanceof JDPolyConvert ) {

        JDPolyline p = ((JDPolyConvert)getChildAt(i)).convertToPolyline();
        objInfo[i].setValue(p,xOrg,yOrg,colorManager);

      } else if( getChildAt(i) instanceof JDPolyline ) {

        JDPolyline p = (JDPolyline)getChildAt(i);
        objInfo[i].setValue(p,xOrg,yOrg,colorManager);

      } else if ( getChildAt(i) instanceof JDLine ) {

        JDLine p = (JDLine)getChildAt(i);
        objInfo[i].type = 2;
        objInfo[i].xPolys = new int[2];
        objInfo[i].yPolys = new int[2];
        objInfo[i].xPolys[0] = (int)p.getSummit(0).x - xOrg;
        objInfo[i].yPolys[0] = (int)p.getSummit(0).y - yOrg;
        objInfo[i].xPolys[1] = (int)p.getSummit(1).x - xOrg;
        objInfo[i].yPolys[1] = (int)p.getSummit(1).y - yOrg;
        objInfo[i].foregroundId = colorManager.addColor(p.getForeground());

      } else {

        throw new IOException("generateJavaClass() supports only Line or Polyline");

      }

    }

    // Write class

    f.write("/** ---------- " + this.getName() + " class ---------- */\n");
    f.write("public class " + this.getName() + " {\n\n");
    f.write("  private static int[][] xPolys = null;\n");
    f.write("  private static int[][] yPolys = null;\n\n");

    // Write colors
    for(i=0;i<colorManager.colors.size();i++) {
      Color c = colorManager.get(i);
      f.write("  private static Color sColor" + i + " = new Color("+c.getRed()+","+c.getGreen()+","+c.getBlue()+");\n");
    }
    f.write("\n");

    f.write("  private static int[][] xOrgPolys = {\n");
    for(i=0;i<getChildrenNumber();i++) {
      if(objInfo[i].type == 1) {
        f.write("    {");
        for(j=0;j<objInfo[i].xPolys.length;j++) {
          f.write(Integer.toString(objInfo[i].xPolys[j]));
          if(j<objInfo[i].xPolys.length-1) f.write(",");
        }
        f.write("},\n");
      }
    }
    f.write("  };\n\n");

    f.write("  private static int[][] yOrgPolys = {\n");
    for(i=0;i<getChildrenNumber();i++) {
      if(objInfo[i].type == 1) {
        f.write("    {");
        for(j=0;j<objInfo[i].yPolys.length;j++) {
          f.write(Integer.toString(objInfo[i].yPolys[j]));
          if(j<objInfo[i].yPolys.length-1) f.write(",");
        }
        f.write("},\n");
      }
    }
    f.write("  };\n\n");

    f.write("  static public void paint(Graphics g,Color backColor,int x,int y,double size) {\n\n");
    f.write("    // Allocate array once\n");
    f.write("    if( xPolys == null ) {\n");
    f.write("      xPolys = new int [xOrgPolys.length][];\n");
    f.write("      yPolys = new int [yOrgPolys.length][];\n");
    f.write("      for( int i=0 ; i<xOrgPolys.length ; i++ ) {\n");
    f.write("        xPolys[i] = new int [xOrgPolys[i].length];\n");
    f.write("        yPolys[i] = new int [yOrgPolys[i].length];\n");
    f.write("      }\n");
    f.write("    }\n\n");

    f.write("    // Scale and translate poly\n");
    f.write("    for( int i=0 ; i<xOrgPolys.length ; i++ ) {\n");
    f.write("      for( int j=0 ; j<xOrgPolys[i].length ; j++ ) {\n");
    f.write("        xPolys[i][j] = (int)((double)xOrgPolys[i][j]*size+0.5) + x;\n");
    f.write("        yPolys[i][j] = (int)((double)yOrgPolys[i][j]*size+0.5) + y;\n");
    f.write("      }\n");
    f.write("    }\n\n");

    f.write("    // Paint object\n");
    for(k=0,i=0;i<getChildrenNumber();i++) {
      if( objInfo[i].type==1 ) {
        if(getChildAt(i).getFillStyle()!=JDObject.FILL_STYLE_NONE)
          f.write("    g.setColor(" + colorManager.getName(objInfo[i].backgroundId) + ");g.fillPolygon(xPolys["+k+"],yPolys["+k+"],xPolys["+k+"].length);\n");
        if(objInfo[i].closed)
          f.write("    g.setColor(" + colorManager.getName(objInfo[i].foregroundId) + ");g.drawPolygon(xPolys["+k+"],yPolys["+k+"],xPolys["+k+"].length);\n");
        else
          f.write("    g.setColor(" + colorManager.getName(objInfo[i].foregroundId) + ");g.drawPolyline(xPolys["+k+"],yPolys["+k+"],xPolys["+k+"].length);\n");
        k++;
      } else {
        f.write("    g.setColor(" + colorManager.getName(objInfo[i].foregroundId) + ");\n");
        f.write("    g.drawLine((int)(" + objInfo[i].xPolys[0] + ".0*size+0.5)+x, (int)("
                                       + objInfo[i].yPolys[0] + ".0*size+0.5)+y, (int)("
                                       + objInfo[i].xPolys[1] + ".0*size+0.5)+x, (int)("
                                       + objInfo[i].yPolys[1] + ".0*size+0.5)+y);\n");

      }
    }
    f.write("\n  }\n\n");

    f.write("  static public void setBoundRect(int x,int y,double size,Rectangle bound) {\n");
    f.write("    bound.setRect((int)(" + (boundRect.x-xOrg) + ".0*size+0.5)+x,(int)("
                                        + (boundRect.y-yOrg) + ".0*size+0.5)+y,(int)("
                                        + boundRect.width+  ".0*size+0.5),(int)("
                                        + boundRect.height+ ".0*size+0.5));");
    f.write("\n  }\n\n");

    f.write("}\n\n");
  }

  // -----------------------------------------------------------
  // File management
  // -----------------------------------------------------------
  public void saveObject(FileWriter f, int level) throws IOException {

    String decal = saveObjectHeader(f, level);

    String to_write = decal + "children: {\n";
    f.write(to_write, 0, to_write.length());

    for (int i = 0; i < children.size(); i++)
      ((JDObject) children.get(i)).saveObject(f,level+2);

    to_write = decal + "}\n";
    f.write(to_write, 0, to_write.length());

    closeObjectHeader(f, level);

  }

  public JDGroup(JDFileLoader f) throws IOException {

    children = new Vector();

    initDefault();
    f.startBlock();
    summit = f.parseRectangularSummitArray();

    while(!f.isEndBlock()) {
      String propName = f.parseProperyName();
      if( propName.equals("children") ) {
        f.startBlock();
        // Build the children list
        while(!f.isEndBlock()) {
          children.add(f.parseObject());
        }
        f.endBlock();
      } else
        loadDefaultPropery(f,propName);
    }

    f.endBlock();

    computeGroupBoundRect();
    updateShape();
  }

  // -----------------------------------------------------------
  // Undo buffer
  // -----------------------------------------------------------
  UndoPattern getUndoPattern() {
    UndoPattern u = new UndoPattern(UndoPattern._JDGroup);
    fillUndoPattern(u);
    u.gChildren = new Vector();
    for(int i=0;i<children.size();i++) {
      u.gChildren.add( ((JDObject)children.get(i)).getUndoPattern() );
    }
    return u;
  }

  JDGroup(UndoPattern e) {
    initDefault();
    applyUndoPattern(e);

    children=new Vector();
    for(int i=0;i<e.gChildren.size();i++) {
      UndoPattern u = (UndoPattern)e.gChildren.get(i);
      switch(u.JDclass) {
        case UndoPattern._JDEllipse:
          children.add(new JDEllipse(u));
          break;
        case UndoPattern._JDGroup:
          children.add(new JDGroup(u));
          break;
        case UndoPattern._JDLabel:
          children.add(new JDLabel(u));
          break;
        case UndoPattern._JDLine:
          children.add(new JDLine(u));
          break;
        case UndoPattern._JDPolyline:
          children.add(new JDPolyline(u));
          break;
        case UndoPattern._JDRectangle:
          children.add(new JDRectangle(u));
          break;
        case UndoPattern._JDRoundRectangle:
          children.add(new JDRoundRectangle(u));
          break;
        case UndoPattern._JDSpline:
          children.add(new JDSpline(u));
          break;
        case UndoPattern._JDImage:
          children.add(new JDImage(u));
          break;
        default:
          System.out.println("!!! WARNING Undo failure !!!");
      }
    }

    computeGroupBoundRect();
    updateShape();
  }

  // -----------------------------------------------------------
  // Private stuff
  // -----------------------------------------------------------
  private void computeGroupBoundRect() {
    x1 = 65536;
    y1 = 65536;
    x2 = -65536;
    y2 = -65536;

    int i,j;
    double t;

    for (i = 0; i < children.size(); i++) {
      JDObject p = (JDObject) children.get(i);
      for (j = 0; j < p.summit.length; j++) {
        if (p.summit[j].x < x1) x1 = p.summit[j].x;
        if (p.summit[j].y < y1) y1 = p.summit[j].y;
        if (p.summit[j].x > x2) x2 = p.summit[j].x;
        if (p.summit[j].y > y2) y2 = p.summit[j].y;
      }
    }

    //Swap min and max according to summit
    if(summit[0].x>summit[4].x) {
      t=x1;x1=x2;x2=t;
    }
    if(summit[0].y>summit[4].y) {
      t=y1;y1=y2;y2=t;
    }

  }

  // Compute summit coordinates from children
  // 0 1 2
  // 7   3
  // 6 5 4
  private void computeSummitCoordinates() {

    computeGroupBoundRect();

    if (children.size() >= 0) {
      summit[0].x = x1;
      summit[0].y = y1;
      summit[2].x = x2;
      summit[2].y = y1;
      summit[4].x = x2;
      summit[4].y = y2;
      summit[6].x = x1;
      summit[6].y = y2;
      centerSummit();
    }

    setOrigin(new Point.Double((x2 - x1) / 2.0, (y2 - y1) / 2.0));

  }

}