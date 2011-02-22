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
 
/**
 * A set of class to handle a network editor and its viewer.
 *
 * Author: Jean Luc PONS
 * Date: Jul 1, 2004
 * (c) ESRF 2004
 */

package fr.esrf.tangoatk.widget.util.interlock;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.font.FontRenderContext;
import java.util.Vector;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class which allow to control and display an object of the network. It exists 2 types of object :
 * <p>
 * <li> The <strong>bubble object</strong> which is the main object . A bubble accept a free label that can be moved
 * by the user , 2 small fixed labels (one centered and one at the bottom left) and can have extensions :
 * A list of named parameters associated to a string value. You can also specifie the maximum number of
 * incoming/outgoing links and its shape. The coordinates of a bubble are in GRID unit.
 * <p>
 * <li> The <strong>text object</strong> which is a free label moveable by the user. You can also specify a font for
 * this object. The text object does not accept links , extensions , small fixed labels and shape. The coordinates
 * for a text object a in pixel.
 * <p>
 * <strong>Important</strong>: It is not recommended to subclass this NetObject class because
 * it would be too complex to provide a full overridable interface including file,undo and
 * clipboard management. Nevertheless, when using the NetEditor in non editable mode, it is
 * possible either to override NetEditor.loadFile() or NetEditor.addObject() to convert
 * loaded NetObject into a new type. As there is no undo or clipboard operation, your
 * objects will not be affected.
 *
 * <p>Here are the possible bubble shapes:<p>
 * <img src="all_shapes.gif" border="0" alt="The NetEditor shapes"></img>
 */

public class NetObject {

  // ------------------------------------------------
  // Constants
  // ------------------------------------------------

  /** Bubble pbject */
  public static final int OBJECT_BUBBLE = 1;
  /** Free label object */
  public static final int OBJECT_TEXT   = 2;

  /** Left label justification */
  public static final int JUSTIFY_LEFT   = 0;
  /** Right label justification */
  public static final int JUSTIFY_RIGHT  = 1;
  /** Center label justification */
  public static final int JUSTIFY_CENTER = 2;

  // ------------------------------------------------
  // Param for base object
  // ------------------------------------------------

  int type;                    // Object type
  int userType;                // Object user type (for external usage)
  int bSize;                   // Object size
  int maxInput;                // maximun number of incoming link
  int maxOutput;               // maximun number of outgoing link
  int shape;                   // Object shape
  boolean editableShape;       // Editable shape
  Point org;                   // Origin
  private String[] labels;     // Free label
  Point labelOffset;           // Free label offset (pixel coordinates)
  int justify;                 // Free label justification
  String smallCenterLabel;     // centered label
  String smallBottomLabel;     // Bottom left label
  Color backColor;             // Bubble background
  String[] extParamValue;      // Extension param values
  String[] extParamName;       // Extension param names

  // ------------------------------------------------
  // Editing stuff
  // ------------------------------------------------

  Point dragStart;
  int   selSet;
  int   labelAscent;       // Label ascent

  // Selection mode
  static final int SEL_NONE   = 0;
  static final int SEL_OBJECT = 1;
  static final int SEL_LABEL  = 2;
  static final int SEL_LINK   = 10;

  // ------------------------------------------------
  // Private variable
  // ------------------------------------------------

  private Vector    children;     // Vector of NetObject children
  private Vector    parents;      // Vector of parent NetObjects
  private int[]     childrenIds;  // Used to build the tree structure
  private int       idx;          // Used to build the tree structure
  private boolean   selected;     // selection state
  private Rectangle repaintRect;  // Reapaint rectangle
  private Rectangle boundRect;    // Bounding box
  private Rectangle labelRect;    // Label Bounding box
  private int[]     labelWidth;   // Label width
  private int       labelHeight;  // Label height
  private int       maxLabelWidth;// Max label height
  private Font      textFont;     // Font fot OBJECT_TEXT

  private Object    userValue;    // User value (Reserved for external usage)

  private int[]        ashx;
  private int[]        ashy;
  private StringBuffer to_write;
  private NetEditor   parentEditor; // parent editor (Used for painting and font purpose)

  // ------------------------------------------------
  // Static
  // ------------------------------------------------

  private static float dashPattern[] = {3.0f};
  private static BasicStroke dashStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f);

  private static BufferedImage     fntImg=null;
  private static FontRenderContext frc;
  private static Point pt0 = new Point(0,0);

  // --------------------------------------------------------------------
  // Construction
  // --------------------------------------------------------------------

  /**
   * Construct a NetObject.
   * @param type Type of this NetObject
   * @param userType User defined type of this NetObject
   * @param maxInput Maximum number of incoming link for this bubble
   * @param maxOutput Maximum number of outgoing link for this bubble
   * @param x x coordinates (GRID coordinates for BUBBLE , pixel for TEXT)
   * @param y y coordinates ...
   * @see NetObject#OBJECT_BUBBLE
   * @see NetObject#OBJECT_TEXT
   */
  public NetObject(int type,int userType,int maxInput,int maxOutput,int x,int y) {

    this.type = type;
    this.userType = userType;
    if( type==OBJECT_BUBBLE ) {
      this.maxInput = maxInput;
      this.maxOutput = maxOutput;
    } else {
      this.maxInput = 0;
      this.maxOutput = 0;
    }
    shape = NetShape.SHAPE_CIRCLE;
    org = new Point(x,y);
    labelOffset = new Point(0, 0);
    boundRect = new Rectangle();
    labelRect = new Rectangle();
    repaintRect = new Rectangle();
    children = new Vector();
    parents = new Vector();
    childrenIds = null;
    idx = 0;
    selected = false;
    ashx = new int[4];
    ashy = new int[4];
    labels = new String[0];
    labelWidth = new int[0];
    smallCenterLabel = "";
    smallBottomLabel = "";
    justify = JUSTIFY_CENTER;
    dragStart  = new Point();
    parentEditor = null;
    userValue  = null;
    extParamValue = null;
    extParamName  = null;
    bSize = 10;
    backColor = Color.black;
    editableShape = true;

    if( type==OBJECT_TEXT )
      textFont = NetEditor.defaultLabelFont;
    else
      textFont = null;

    // Init static
    if( fntImg==null ) {
      fntImg = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
      Graphics2D g = (Graphics2D)fntImg.getGraphics();
      g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      frc = g.getFontRenderContext();
    }

  }

  /**
   * Build an NetObject from an UndoPattern.
   * Does not conserve children.
   * The extensions param are backuped by the base class.
   * @param e Undo pattern
   */
  NetObject(UndoPattern e) {

    type = e.type;
    userType = e.userType;
    org = new Point(e.x,e.y);
    shape = e.shape;
    editableShape = e.eshape;
    labelOffset = new Point(e.lx, e.ly);
    boundRect = new Rectangle();
    labelRect = new Rectangle();
    repaintRect = new Rectangle();
    children = new Vector();
    parents = new Vector();
    childrenIds = null;
    idx = e.idx;
    selected = false;
    ashx = new int[4];
    ashy = new int[4];
    setLabel(e.label);
    smallCenterLabel = "";
    smallBottomLabel = "";
    justify = e.justify;
    dragStart = new Point();
    parentEditor = null;
    userValue=null;
    maxInput  = e.maxi;
    maxOutput = e.maxo;
    bSize = e.size;
    textFont = e.textFnt;

    // Bubble extensions

    extParamName  = null;
    extParamValue = null;

    if (type == OBJECT_BUBBLE) {

      extParamName = e.extsN;
      if (e.extsN != null) {
        extParamValue = new String[e.extsN.length];
        for (int i = 0; i < e.extsN.length; i++)
          extParamValue[i] = new String(e.extsV[i]);
      }

    }

  }

  // --------------------------------------------------------------------
  // Tree building,browsing functions
  // --------------------------------------------------------------------

  /**
   * Add the given object to the children list.
   * @param o Child object
   */
  public void addChild(NetObject o) {
    children.add(o);
    o.parents.add(this);
  }

  /**
   * Remove the specified child from the children list.
   * @param o Child object to remove
   */
  public void removeChild(NetObject o) {
    children.remove(o);
    o.parents.remove(this);
  }

  /**
   * Remove the specified child from the children list.
   * @param i Child index
   */
  public void removeChild(int i) {
    getChildAt(i).parents.remove(this);
    children.remove(i);
  }

  /** Returns the number of children. */
  public int getChildrenNumber() {
    return children.size();
  }

  /**
   * Returns child at the specified index.
   * @param idx Child index
   * @return Child NetObject
   */
  public NetObject getChildAt(int idx) {
    return (NetObject)children.get(idx);
  }

  /** Clear the children list */
  public void clearChildren() {
    int sz = getChildrenNumber();
    for(int i=0;i<sz;i++) getChildAt(i).parents.remove(this);
    children.clear();
  }

  /** Returns the number of parent */
  public int getParentNumber() {
    return parents.size();
  }

  /**
   * Returns the parent object at the specified index.
   * @param idx Parent index
   * @return Parent NetObject
   */
  public NetObject getParentAt(int idx) {
    return (NetObject)parents.get(idx);
  }

  /** Returns true if this object is parent of o */
  public boolean isParentOf(NetObject o) {
    return children.contains(o);
  }

  /** Returns true if this object is child of o */
  public boolean isChildOf(NetObject o) {
    return parents.contains(o);
  }

  // --------------------------------------------------------------------

  /** Internal usage */
  void setChildrenIdList(int[] lst) {
    childrenIds = lst;
  }

  /** Internal usage */
  int[] getChildrenIdList() {
    return childrenIds;
  }

  /** Internal usage */
  int getIndex() {
    return idx;
  }

  /** Internal usage */
  void setIndex(int i) {
    idx=i;
  }

  /** Internal usage */
  void setParent(NetEditor iE) {
    parentEditor=iE;
  }

  // --------------------------------------------------------------------
  // Property stuff
  // --------------------------------------------------------------------

  /** Returns the label */
  public String getLabel() {

    StringBuffer tmpStr = new StringBuffer();
    int sz = labels.length;

    for (int i = 0; i < sz; i++) {
      tmpStr.append(labels[i]);
      if (i < sz - 1) tmpStr.append('\n');
    }

    return tmpStr.toString();
  }

  /**
   * Sets the free label of this object.
   * @param value Label
   */
  public void setLabel(String value) {

    // Check null value
    if( value==null ) {
      labels = new String[0];
      labelWidth = new int[labels.length];
      return;
    }

    // Remove extra \n at the end of the string (not handled by split)
    while (value.endsWith("\n")) value = value.substring(0, value.length() - 1);

    // Replace " by ' within label
    StringBuffer newValue = new StringBuffer();
    char c;
    for(int i=0;i<value.length();i++ ) {
      c = value.charAt(i);
      if( c == '"' ) newValue.append('\'');
      else           newValue.append(c);
    }

    if (newValue.length() == 0)
      labels = new String[0];
    else
      labels = newValue.toString().split("\n");

    labelWidth = new int[labels.length];

  }

  /** Sets the free lablel */
  public void setLabel(String[] value) {
    labels = value;
    labelWidth = new int[labels.length];
  }

  /**
   * Get object name.
   * @return  'Bubble' or 'Text'
   */
  public String getName() {

    switch(type) {
      case OBJECT_BUBBLE:
        return "Bubble";
      case OBJECT_TEXT:
        return "Text";
    }

    return "Unknown";

  }

  /**
   * Return the type of this NetObject.
   * @see NetObject#OBJECT_BUBBLE
   * @see NetObject#OBJECT_TEXT
   */
  public int getType() {
    return type;
  }

  /** Returns the user defined type. */
  public int getUserType() {
    return userType;
  }

  /**
   * Sets the shape of this object
   * @param s Shape
   * @see NetShape
   */
  public void setShape(int s) {
    shape = s;
  }

  /** Returns shape of this object */
  public int getShape() {
    return shape;
  }

  /**
   * Set the editable shape properties.
   * @param b True to make the shape editable (via the NetObjectDlg), false otherwise.
   */
  public void setEditableShape(boolean b) {
    editableShape=b;
  }

  /**
   * Sets the label justification of this object.
   * @see NetObject#JUSTIFY_LEFT
   * @see NetObject#JUSTIFY_RIGHT
   * @see NetObject#JUSTIFY_CENTER
   */
  public void setJustify(int i) {
    justify = i;
  }

  /** Returns current justification
   * @see NetObject#setJustify
   */
  public int getJustify() {
    return justify;
  }

  /** Returns maximum number of incoming link */
  public int getMaxInput() {
    return maxInput;
  }

  /** Returns maximum number of outgoing link */
  public int getMaxOutput() {
    return maxOutput;
  }

  /**
   * Sets the label displayed at the center of this object.
   * It uses the NetEditor small font.
   */
  public void setCenterLabel(String s) {
    smallCenterLabel = s;
  }

  /**
   * Sets the label displayed at the bottom left of this object.
   * It uses the NetEditor small font.
   */
  public void setBottomLabel(String s) {
    smallBottomLabel = s;
  }

  /** Sets the background (fill color) of this object (when parent NetEditor is not editable) */
  public void setColor(Color c) {
    backColor = c;
  }

  /** Gets the background (fill color) of this object (when parent NetEditor is not editable) */
  public Color getColor() {
    return backColor;
  }

  /** Sets the object size */
  public void setSize(int size) {
    bSize = size;
  }

  /** Gets the object size */
  public int getSize() {
    return bSize;
  }

  /** Sets the user value for external usage */
  public void setUserValue(Object v) {
    userValue = v;
  }

  /** Returns the user value */
  public Object getUserValue() {
    return userValue;
  }

  /**
   * Sets the list of extended parameter name for this bubble object.
   * Text does not accept extensions.
   * @param names List of names
   */
  public void setExtensionList(String[] names) {
    if(type!=OBJECT_BUBBLE) {
      System.out.println("NetObject.setExtensionList() : Only bubble object accept extensions.");
      return;
    }
    extParamName  = names;
    extParamValue = new String[names.length];
    for(int i=0;i<extParamValue.length;i++)
      extParamValue[i] = "";
  }

  /**
   * Sets the extended param value.
   * @param name Param name
   * @param value Param value
   * @see NetObject#setExtensionList
   */
  public void setExtendedParam(String name,String value) {

    int i = getExtendedParamIndex(name);
    if(i!=-1) extParamValue[i] = name;
    else System.out.println("NetObject.setExtendedParam() : " + name + " does not exist.");

  }

  /**
   * Sets the extended param value.
   * @param extIdx Index of the extensions.
   * @param value param value
   * @see NetObject#setExtensionList
   */
  public void setExtendedParam(int extIdx,String value) {
    int n = getExtendedParamNumber();
    if( extIdx<0 || extIdx>=n ) {
      System.out.println("NetObject.setExtendedParam() : index of of bounds.");
      return;
    }
    extParamValue[extIdx] = value;
  }

  /**
   * Returns the value of the specified extended param, an empty string if not found.
   * @param name Param name
   * @return param value
   * @see NetObject#setExtensionList
   */
  public String getExtendedParam(String name) {
    int i = getExtendedParamIndex(name);
    if(i!=-1) return extParamValue[i];
    else {
      System.out.println("NetObject.getExtendedParam() : " + name + " does not exist.");
      return "";
    }
  }

  /**
   * Returns the value of the specified extended param, an empty string if not found.
   * @param extIdx Index of the extensions.
   * @return param value
   * @see NetObject#setExtensionList
   */
  public String getExtendedParam(int extIdx) {
    int n = getExtendedParamNumber();
    if( extIdx<0 || extIdx>=n ) {
      System.out.println("NetObject.getExtendedParam() : index of of bounds.");
      return "";
    }
    return extParamValue[extIdx];
  }

  /** Returns the number of extensions */
  public int getExtendedParamNumber() {
   if( extParamValue == null )
     return 0;
    else
     return extParamValue.length;
  }

  /** Returns the index of the specified extended param , -1 when not found */
  public int getExtendedParamIndex(String name) {

    if( extParamName==null )
      return -1;

    boolean found = false;
    int i=0;
    while(i<extParamName.length && !found) {
      found = name.equalsIgnoreCase(extParamName[i]);
      if(!found) i++;
    }

    if( found )
      return i;
    else
      return -1;

  }

  /**
   * Set the font of this object. Only TEXT object accept font
   * @param f Font
   */
  public void setTextFont(Font f) {
    if( type==OBJECT_TEXT )
      textFont = f;
  }

  /** Get the font of this object */
  public Font getTextFont() {
    return textFont;
  }

  /**
   * Returns X pixel coordinates.Returns a valid value only if
   * this object has been inserted in a NetEditor.
   */
  public int getXOrigin() {
    if(type==OBJECT_TEXT) {
      return org.x;
    } else {
      return org.x*parentEditor.XGRID_SIZE;
    }
  }

  /**
   * Returns Y pixel coordinates.Returns a valid value only if
   * this object has been inserted in a NetEditor.
   */
  public int getYOrigin() {
    if(type==OBJECT_TEXT) {
      return org.y;
    } else {
      return org.y*parentEditor.YGRID_SIZE;
    }
  }

  // --------------------------------------------------------------------
  // Editing stuff
  // --------------------------------------------------------------------

  boolean acceptInput() {
    switch(type) {
      case OBJECT_BUBBLE:
        return getParentNumber()<maxInput;
    }
    return false;
  }

  boolean acceptOutput() {
    switch(type) {
      case OBJECT_BUBBLE:
        return getChildrenNumber()<maxOutput;
    }
    return false;
  }


  /**
   * Select or not this NetObject within the editor.
   * @param s True to select, false otherwise
   */
  public void setSelected(boolean s) {
    selected = s;
  }

  /**
   * @return True if this NetObject is selected.
   */ 
  public boolean getSelected() {
    return selected;
  }

  // --------------------------------------------------------------------

  boolean contains(int x, int y) {
    return boundRect.contains(x, y);
  }

  boolean labelContains(int x, int y) {
    return labelRect.contains(x, y);
  }

  /** Handle double link, decal them when they overlap */
  private Point getLinkTranslation(int x1,int y1,int x2,int y2,NetObject child) {

    // Detect double link
    if( isChildOf(child) ) {

      double nx = (double)(y2 - y1);
      double ny = (double)(x1 - x2);
      double n = Math.sqrt( nx*nx + ny*ny );
      int tx = (int)((nx/n)*3.0+0.5);
      int ty = (int)((ny/n)*3.0+0.5);
      return new Point(tx,ty);

    } else {

      return pt0;

    }

  }

  int childContains(int x, int y) {

    NetObject child;
    int sz = children.size();
    int i=0;
    boolean found=false;
    Line2D l  = new Line2D.Double();
    Rectangle2D r = new Rectangle2D.Double((double) (x - 4), (double) (y - 4), 8.0, 8.0);

    int x2 , x1=org.x * parentEditor.XGRID_SIZE;
    int y2 , y1=org.y * parentEditor.YGRID_SIZE;

    while(i<sz && !found) {

      child=getChildAt(i);
      x2 = child.org.x*parentEditor.XGRID_SIZE;
      y2 = child.org.y*parentEditor.YGRID_SIZE;
      Point t = getLinkTranslation(x1,y1,x2,y2,child);
      l.setLine((double)(x1+t.x), (double)(y1+t.y),
                (double)(x2+t.x), (double)(y2+t.y));
      found = l.intersects(r);

      if(!found) i++;

    }

    // Return child index (-1 if not found)
    if( found )
      return i;
    else
      return -1;

  }

  boolean inside(Rectangle r) {
    return r.contains(boundRect);
  }

  boolean hasProperties() {

    switch (type) {

      case OBJECT_BUBBLE:
      case OBJECT_TEXT:
        return true;

      default:
        return false;

    }

  }

  void resetDrag() {
    dragStart.x = org.x;
    dragStart.y = org.y;
  }

  Rectangle getBoundRect() {
    return boundRect;
  }

  Rectangle getRepaintRect() {
    return repaintRect;
  }

  NetObject getCopyAt(int newX,int newY) {

    int i;

    NetObject n = new NetObject(type,userType,maxInput,maxOutput,newX,newY);
    n.shape = shape;
    n.editableShape = editableShape;
    n.bSize = bSize;
    n.labels = new String[labels.length];
    n.labelWidth = new int[labels.length];
    for(i=0;i<labels.length;i++)
      n.labels[i] = new String(labels[i]);
    n.justify = justify;
    n.textFont = textFont;
    n.labelOffset.x = labelOffset.x;
    n.labelOffset.y = labelOffset.y;
    n.extParamName = extParamName;

    if( extParamName!=null ) {
      n.extParamValue = new String[extParamValue.length];
      for(i=0;i<extParamValue.length;i++)
        n.extParamValue[i] = new String(extParamValue[i]);
    } else {
      n.extParamValue = null;
    }

    return n;

  }

  UndoPattern getUndoPattern() {
    return new UndoPattern(this);
  }

  // --------------------------------------------------------------------
  // Painting stuff
  // --------------------------------------------------------------------
  private void paintArrows(Graphics2D g,int x1,int y1,int x2,int y2,boolean doubleLink) {

    double xc,yc;
    double dx,dy;
    double nx = -(y2 - y1);
    double ny =  (x2 - x1);
    double n = Math.sqrt(nx * nx + ny * ny);
    double aw = 5.0;

    // Cannot build arrow for null line
    if (n >= 1.0) {

      dx = ((x2 - x1) * aw*1.3) / n;
      dy = ((y2 - y1) * aw*1.3) / n;
      if( doubleLink ) {
        // Shift arrow when double link
        xc = (double)x1 + (double)(x2 - x1) * 0.51;
        yc = (double)y1 + (double)(y2 - y1) * 0.51;
      } else {
        xc = (double)x1 + (double)(x2 - x1) * 0.5;
        yc = (double)y1 + (double)(y2 - y1) * 0.5;
      }

      // Compute arrow polygon
      ashx[0] = (int)Math.round(xc + (-nx / n * aw));
      ashy[0] = (int)Math.round(yc + (-ny / n * aw));
      ashx[1] = (int)Math.round(xc + (nx / n * aw));
      ashy[1] = (int)Math.round(yc + (ny / n * aw));
      ashx[2] = (int)Math.round(xc+dx);
      ashy[2] = (int)Math.round(yc+dy);

      // Anti alias arrow
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);
      g.fillPolygon(ashx, ashy, 3);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_OFF);

    }

  }

  void paintLinks(Graphics2D g, boolean drawArrow,boolean isSelected) {

    int i;

    for (i = 0; i < children.size(); i++) {

      if(isSelected && (i==selSet-SEL_LINK))
        g.setColor(NetShape.selColor);
      else
        g.setColor(Color.black);

      paintLink(g,getChildAt(i),drawArrow);

    }

  }

  private void paintLabels(Graphics2D g) {

    int i,y;

    y = labelRect.y + labelAscent;

    switch (justify) {

      case JUSTIFY_LEFT:
        for (i = 0; i < labels.length; i++) {
          g.drawString(labels[i], labelRect.x, y);
          y += labelHeight;
        }
        break;

      case JUSTIFY_RIGHT:
        for (i = 0; i < labels.length; i++) {
          g.drawString(labels[i], labelRect.x + (maxLabelWidth-labelWidth[i]), y);
          y += labelHeight;
        }
        break;

      case JUSTIFY_CENTER:
        for (i = 0; i < labels.length; i++) {
          g.drawString(labels[i], labelRect.x + (maxLabelWidth - labelWidth[i])/2, y);
          y += labelHeight;
        }
        break;

    }

    if (selected && parentEditor.isEditable() && labelRect.width>0) {
      g.setColor(Color.black);
      Stroke old = g.getStroke();
      g.setStroke(dashStroke);
      g.drawRect(labelRect.x, labelRect.y, labelRect.width, labelRect.height);
      g.setStroke(old);
    }

  }

  private void paintSmallCenterLabel(Graphics2D g) {

    Rectangle2D bounds;

    if (smallCenterLabel.length()>0) {

      g.setFont(parentEditor.smallFont);
      g.setColor(Color.black);
      bounds = parentEditor.smallFont.getStringBounds(smallCenterLabel, frc);
      g.drawString(smallCenterLabel, org.x * parentEditor.XGRID_SIZE - (int) (bounds.getWidth() / 2.0),
              org.y * parentEditor.YGRID_SIZE + 4);

    }

  }

  private void paintSmallBottomLabel(Graphics2D g) {

    Rectangle2D bounds;

    if (smallBottomLabel.length() > 0) {

      g.setFont(parentEditor.smallFont);
      bounds = parentEditor.smallFont.getStringBounds(smallBottomLabel, frc);
      g.drawString(smallBottomLabel, org.x * parentEditor.XGRID_SIZE - (bSize + (int) bounds.getWidth() + 2),
              org.y * parentEditor.YGRID_SIZE + 15);

    }

  }

  void paintBubble(Graphics2D g) {

    // Paint shape
    Color bckColor = (parentEditor.isEditable()) ? Color.yellow : backColor;
    boolean sel = (parentEditor.isEditable()) ? selected : false;
    NetShape.paintShape(g,shape,sel,bckColor,
                        org.x*parentEditor.XGRID_SIZE,org.y*parentEditor.YGRID_SIZE,
                        bSize);

    // Paint labels
    g.setFont(parentEditor.labelFont);
    g.setColor(Color.black);
    paintLabels(g);
    paintSmallBottomLabel(g);
    paintSmallCenterLabel(g);

  }

  void paintJoin(Graphics2D g) {

    if (parentEditor.isEditable()) {

      if (selected)
        g.setColor(NetShape.selColor);
      else
        g.setColor(Color.BLACK);

      g.fillRect(org.x * parentEditor.XGRID_SIZE - 3, org.y * parentEditor.YGRID_SIZE - 3, 6, 6);

    } else {

      if(bSize==0) return;
      g.setColor(Color.BLACK);
      g.fillRect(org.x * parentEditor.XGRID_SIZE - 2, org.y * parentEditor.YGRID_SIZE - 2, 4, 4);

    }

  }

  void paintText(Graphics2D g) {

    g.setColor(backColor);
    g.setFont(textFont);
    paintLabels(g);

  }

  /**
   * Paint the link beetween this object and child.
   * @param g Graphics object
   * @param child child object
   * @param drawArrow true to draw arrow
   */
  public void paintLink(Graphics2D g, NetObject child,boolean drawArrow) {

    int x1,y1,x2,y2;
    x1 = org.x * parentEditor.XGRID_SIZE;
    y1 = org.y * parentEditor.YGRID_SIZE;
    x2 = child.org.x * parentEditor.XGRID_SIZE;
    y2 = child.org.y * parentEditor.YGRID_SIZE;

    Point t = getLinkTranslation(x1,y1,x2,y2,child);

    g.drawLine(x1+t.x, y1+t.y, x2+t.x, y2+t.y);
    if (drawArrow) paintArrows(g , x1+t.x , y1+t.y , x2+t.x , y2+t.y , t!=pt0);

  }

  /**
   * Paint this NetObject.
   * @param g Graphics object
   */
  public void paint(Graphics2D g) {

    if( parentEditor==null )
      return;

    updateBoundRect();

    switch (type) {

      case OBJECT_BUBBLE:
        paintBubble(g);
        break;

      case OBJECT_TEXT:
        paintText(g);
        break;

    }

  }

  // --------------------------------------------------------------------

  private void updateLabelBoundRect() {

    Rectangle2D bounds;
    int i,xOrg,yOrg;
    Font font;

    if(type==OBJECT_TEXT) {
      xOrg = org.x;
      yOrg = org.y;
      font = textFont;
    } else {
      xOrg = org.x*parentEditor.XGRID_SIZE;
      yOrg = org.y*parentEditor.YGRID_SIZE- 15;
      font = parentEditor.labelFont;
    }

    if(labels.length==0) {
      //labelRect.setRect(xOrg-6+labelOffset.x,yOrg-6+labelOffset.y,12,12);
      labelRect.setRect(0,0,0,0);
      return;
    }

    // Compute global width
    maxLabelWidth=0;
    for (i = 0; i < labels.length; i++) {
      bounds = font.getStringBounds(labels[i], frc);
      labelHeight  =(int)bounds.getHeight();
      labelWidth[i]=(int)bounds.getWidth();
      if( labelWidth[i]>maxLabelWidth ) maxLabelWidth=labelWidth[i];
    }
    // estimate ascent
    labelAscent = (int)((double)labelHeight*0.8);

    switch (justify) {

      case JUSTIFY_LEFT:
        labelRect.setRect( xOrg + labelOffset.x , yOrg + labelOffset.y - labelAscent,
                           maxLabelWidth , labelHeight*labels.length );
        break;

      case JUSTIFY_RIGHT:
        labelRect.setRect( xOrg - maxLabelWidth + labelOffset.x , yOrg + labelOffset.y - labelAscent,
                           maxLabelWidth , labelHeight*labels.length );
        break;

      case JUSTIFY_CENTER:
        labelRect.setRect( xOrg - maxLabelWidth/2 + labelOffset.x , yOrg + labelOffset.y - labelAscent,
                           maxLabelWidth , labelHeight*labels.length );
        break;

    }


  }

  private void updateBoundRect() {

    switch (type) {

      case OBJECT_BUBBLE:
        updateLabelBoundRect();
        NetShape.setBoundRect(shape,org.x * parentEditor.XGRID_SIZE,org.y * parentEditor.YGRID_SIZE,
                              bSize,boundRect);
        break;

      case OBJECT_TEXT:
        updateLabelBoundRect();
        boundRect.setRect(labelRect.x,labelRect.y,labelRect.width,labelRect.height);
        break;

    }


  }

  void updateRepaintRect() {
    if( parentEditor!=null ) {
      updateBoundRect();
      if( labelRect.width>0 )
        repaintRect = boundRect.union(labelRect);
      else
        repaintRect = new Rectangle(boundRect);
    }
  }


  // --------------------------------------------------------------------
  // File management
  // --------------------------------------------------------------------
  private void saveLabels() {

    if( labels.length>0 ) {

      to_write.append("    label:");
      for(int i=0;i<labels.length;i++) {
        to_write.append('"');
        to_write.append(labels[i]);
        to_write.append('"');
        if(i<labels.length-1) to_write.append(",\n          ");
      }
      to_write.append('\n');

    }

    if( labelOffset.x!=0 || labelOffset.y!=0 ) {
      to_write.append("    label_offset:");
      to_write.append(Integer.toString(labelOffset.x));
      to_write.append(',');
      to_write.append(Integer.toString(labelOffset.y));
      to_write.append('\n');
    }

    if( justify!=NetObject.JUSTIFY_CENTER ) {
      to_write.append("    justify:");
      to_write.append(Integer.toString(justify));
      to_write.append('\n');
    }

  }

  private void saveHeader() {

    to_write.append("  ");
    to_write.append(getName());
    to_write.append(' ');
    to_write.append(Integer.toString(userType));
    to_write.append(" (");
    to_write.append(Integer.toString(org.x));
    to_write.append(',');
    to_write.append(Integer.toString(org.y));
    to_write.append(") ");
    if(type==OBJECT_BUBBLE) {
      to_write.append(Integer.toString(maxInput));
      to_write.append(',');
      to_write.append(Integer.toString(maxOutput));
      to_write.append(' ');
    }
    to_write.append("{\n");

  }

  private void saveChildrenList() {

    int sz = children.size();
    NetObject child;
    int i;

    if( sz>0 ) {
      to_write.append("    children:");
      for(i=0;i<sz;i++) {
        child = (NetObject)children.get(i);
        to_write.append(Integer.toString(child.idx));
        if( i<sz-1 ) to_write.append(',');
      }
      to_write.append('\n');
    }

  }

  private void saveExtensions() {

    int sz = getExtendedParamNumber();
    if( sz>0 ) {
      int i;

      to_write.append("    extensions {\n");
      for (i = 0; i < sz; i++) {
        to_write.append("      ");
        if( extParamName[i].indexOf(' ')>0 ) {
          to_write.append('"');
          to_write.append(extParamName[i]);
          to_write.append('"');
        } else {
          to_write.append(extParamName[i]);
        }
        to_write.append(":\"");
        to_write.append(extParamValue[i]);
        to_write.append("\"\n");
      }
      to_write.append("    }\n");

    }

  }

  private void saveShape() {

    if( shape != NetShape.SHAPE_CIRCLE ) {
      to_write.append("    shape:");
      to_write.append(Integer.toString(shape));
      to_write.append("\n");
    }

    if( !editableShape )
      to_write.append("    editable_shape:0\n");

  }

  private void saveSize() {

    if( bSize!=10 ) {
      to_write.append("    size:");
      to_write.append(Integer.toString(bSize));
      to_write.append("\n");
    }

  }

  private void saveFont() {

    if( !NetUtils.fontEquals(textFont,NetEditor.defaultLabelFont) ) {
      to_write.append("    font:\"");
      to_write.append(textFont.getName());
      to_write.append("\",");
      to_write.append(Integer.toString(textFont.getStyle()));
      to_write.append(',');
      to_write.append(Integer.toString(textFont.getSize()));
      to_write.append("\n");
    }

  }

  void saveObject(FileWriter f) throws IOException {

    to_write=new StringBuffer();

    // Save object properties

    switch(type) {

      case OBJECT_BUBBLE:
        saveHeader();
        saveShape();
        saveSize();
        saveLabels();
        saveChildrenList();
        saveExtensions();
        to_write.append("  }\n");
        break;

      case OBJECT_TEXT:
        saveHeader();
        saveLabels();
        saveFont();
        saveChildrenList();
        to_write.append("  }\n");
        break;

    }

    f.write(to_write.toString());

  }



}
