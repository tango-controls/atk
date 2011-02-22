//
// JLDataView.java
// Description: A Class to handle 2D graphics plot
//
// JL Pons (c)ESRF 2002

package fr.esrf.tangoatk.widget.util.chart;


import java.util.*;
import java.awt.*;
import javax.swing.*;

/**
 * A class to handle data view. It handles data and all graphics stuff related to a serie of data.
 * @author JL Pons
 */
public class JLDataView {

  //Static declaration

  /** No marker displayed */
  public static final int MARKER_NONE = 0;
  /** Display a dot for each point of the view */
  public static final int MARKER_DOT = 1;
  /** Display a box for each point of the view */
  public static final int MARKER_BOX = 2;
  /** Display a triangle for each point of the view */
  public static final int MARKER_TRIANGLE = 3;
  /** Display a diamond for each point of the view */
  public static final int MARKER_DIAMOND = 4;
  /** Display a start for each point of the view */
  public static final int MARKER_STAR = 5;
  /** Display a vertical line for each point of the view */
  public static final int MARKER_VERT_LINE = 6;
  /** Display an horizontal line for each point of the view */
  public static final int MARKER_HORIZ_LINE = 7;
  /** Display a cross for each point of the view */
  public static final int MARKER_CROSS = 8;
  /** Display a circle for each point of the view */
  public static final int MARKER_CIRCLE = 9;
  /** Display a square for each point of the view */
  public static final int MARKER_SQUARE = 10;

  /** Solid line style */
  public static final int STYLE_SOLID = 0;
  /** Dot line style */
  public static final int STYLE_DOT = 1;
  /** Dash line style */
  public static final int STYLE_DASH = 2;
  /** Long Dash line style */
  public static final int STYLE_LONG_DASH = 3;
  /** Dash + Dot line style */
  public static final int STYLE_DASH_DOT = 4;

  /** Line style */
  public static final int TYPE_LINE = 0;

  /** BarGraph style */
  public static final int TYPE_BAR  = 1;

  /** Fill curve and bar from the top of the graph */
  public static final int METHOD_FILL_FROM_TOP = 0;

  /** Fill curve and bar from zero position (on Yaxis) */
  public static final int METHOD_FILL_FROM_ZERO = 1;

  /** Fill curve and bar from the bottom of the graph */
  public static final int METHOD_FILL_FROM_BOTTOM = 2;

  /** No filling */
  public static final int FILL_STYLE_NONE = 0;
  /** Solid fill style */
  public static final int FILL_STYLE_SOLID = 1;
  /** Hatch fill style */
  public static final int FILL_STYLE_LARGE_RIGHT_HATCH = 2;
  /** Hatch fill style */
  public static final int FILL_STYLE_LARGE_LEFT_HATCH = 3;
  /** Hatch fill style */
  public static final int FILL_STYLE_LARGE_CROSS_HATCH = 4;
  /** Hatch fill style */
  public static final int FILL_STYLE_SMALL_RIGHT_HATCH = 5;
  /** Hatch fill style */
  public static final int FILL_STYLE_SMALL_LEFT_HATCH = 6;
  /** Hatch fill style */
  public static final int FILL_STYLE_SMALL_CROSS_HATCH = 7;
  /** Hatch fill style */
  public static final int FILL_STYLE_DOT_PATTERN_1 = 8;
  /** Hatch fill style */
  public static final int FILL_STYLE_DOT_PATTERN_2 = 9;
  /** Hatch fill style */
  public static final int FILL_STYLE_DOT_PATTERN_3 = 10;

  //Local declaration
  private JLAxis parentAxis;
  private Color lineColor;
  private Color fillColor;
  private Color markerColor;
  private int lineStyle;
  private int lineWidth;
  private int markerType;
  private int markerSize;
  private int barWidth;
  private int fillStyle;
  private int fillMethod;
  private int type;
  private double A0;
  private double A1;
  private double A2;
  private DataList theData;
  private int dataLength;
  private DataList theDataEnd;
  private double max;
  private double min;
  private String name;
  private String unit;

  /**
   * DataView constructor.
   */
  public JLDataView() {
    theData = null;
    theDataEnd = null;
    dataLength = 0;
    name = "";
    unit = "";
    lineColor = Color.red;
    fillColor = Color.lightGray;
    markerColor = Color.red;
    min = Double.MAX_VALUE;
    max = -Double.MAX_VALUE;
    markerType = MARKER_NONE;
    lineStyle = STYLE_SOLID;
    type = TYPE_LINE;
    fillStyle = FILL_STYLE_NONE;
    fillMethod = METHOD_FILL_FROM_BOTTOM;
    lineWidth = 1;
    barWidth = 10;
    markerSize = 6;
    A0 = 0;
    A1 = 1;
    A2 = 0;
    parentAxis = null;
  }

  /* ----------------------------- Global config --------------------------------- */

  /**
   * Sets the graph type (Line or BarGraph).
   * @param s Type of graph
   * @see JLDataView#TYPE_LINE
   * @see JLDataView#TYPE_BAR
   */
  public void setViewType(int s) {
    type = s;
  }

  /**
   * Gets view type.
   * @return View type
   * @see JLDataView#setViewType
   */
  public int getViewType() {
    return type;
  }

  /**
   * Sets the filling style of this view.
   * @param s Filling style
   * @see JLDataView#FILL_STYLE_NONE
   * @see JLDataView#FILL_STYLE_SOLID
   * @see JLDataView#FILL_STYLE_LARGE_RIGHT_HATCH
   * @see JLDataView#FILL_STYLE_LARGE_LEFT_HATCH
   * @see JLDataView#FILL_STYLE_LARGE_CROSS_HATCH
   * @see JLDataView#FILL_STYLE_SMALL_RIGHT_HATCH
   * @see JLDataView#FILL_STYLE_SMALL_LEFT_HATCH
   * @see JLDataView#FILL_STYLE_SMALL_CROSS_HATCH
   * @see JLDataView#FILL_STYLE_DOT_PATTERN_1
   * @see JLDataView#FILL_STYLE_DOT_PATTERN_2
   * @see JLDataView#FILL_STYLE_DOT_PATTERN_3
   */

  public void setFillStyle(int b) {
    fillStyle = b;
  }

  /**
   * Gets the current filling style.
   * @return Filling style
   * @see JLDataView#setFillStyle
   */
  public int getFillStyle() {
    return fillStyle;
  }

  /**
   * Sets the filling method for this view.
   * @param m Filling method
   * @see JLDataView#METHOD_FILL_FROM_TOP
   * @see JLDataView#METHOD_FILL_FROM_ZERO
   * @see JLDataView#METHOD_FILL_FROM_BOTTOM
   */

  public void setFillMethod(int m) {
    fillMethod = m;
  }

  /**
   * Gets the current filling style.
   * @return Filling method
   * @see JLDataView#setFillMethod
   */
  public int getFillMethod() {
    return fillMethod;
  }

  /**
   * Sets the filling color of this dataView.
   * @param c Filling color
   * @see JLDataView#getFillColor
   */
  public void setFillColor(Color c) {
    fillColor = c;
  }

  /**
   * Gets the filling color.
   * @return Filling color
   * @see JLDataView#setFillColor
   */
  public Color getFillColor() {
    return fillColor;
  }

  /* ----------------------------- Line config --------------------------------- */

  /**
   * Sets the color of the curve.
   * @param c Curve color
   * @see JLDataView#getColor
   */
  public void setColor(Color c) {
    lineColor = c;
  }

  /**
   * Gets the curve color.
   * @return Curve color
   * @see JLDataView#setColor
   */
  public Color getColor() {
    return lineColor;
  }


  /**
   * Provided for backward compatibility.
   * @see JLDataView#setFillStyle
   * @return true if the view is filled, false otherwise
   */
  public boolean isFill() {
    return fillStyle!=FILL_STYLE_NONE;
  }

  /**
   * Provided for backward compatibility.
   * @param b true if the view is filled, false otherwise
   * @see JLDataView#setFillStyle
   */
  public void setFill(boolean b) {
    if( !b ) {
      setFillStyle(FILL_STYLE_NONE);
    } else {
      setFillStyle(FILL_STYLE_SOLID);
    }
  }


  /* ----------------------------- BAR config --------------------------------- */

  /**
   * Sets the width of the bar in pixel (Bar graph mode).
   * Pass 0 to have bar auto scaling.
   * @param w Bar width (pixel)
   * @see JLDataView#getBarWidth
   */
  public void setBarWidth(int w) {
    barWidth = w;
  }

  /**
   * Gets the bar width.
   * @return Bar width (pixel)
   * @see JLDataView#setBarWidth
   */
  public int getBarWidth() {
    return barWidth;
  }

  /**
   * Sets the marker color.
   * @param c Marker color
   * @see JLDataView#getMarkerColor
   */
  public void setMarkerColor(Color c) {
    markerColor = c;
  }

  /**
   * Gets the marker color.
   * @return Marker color
   * @see JLDataView#setMarkerColor
   */
  public Color getMarkerColor() {
    return markerColor;
  }

  /**
   * Set the plot line style.
   * @param c Line style
   * @see JLDataView#STYLE_SOLID
   * @see JLDataView#STYLE_DOT
   * @see JLDataView#STYLE_DASH
   * @see JLDataView#STYLE_LONG_DASH
   * @see JLDataView#STYLE_DASH_DOT
   * @see JLDataView#getStyle
   */
  public void setStyle(int c) {
    lineStyle = c;
  }

  /**
   * Gets the marker size.
   * @return Marker size (pixel)
   * @see JLDataView#setMarkerSize
   */
  public int getMarkerSize() {
    return markerSize;
  }

  /**
   * Sets the marker size (pixel).
   * @param c Marker size (pixel)
   * @see JLDataView#getMarkerSize
   */
  public void setMarkerSize(int c) {
    markerSize = c;
  }

  /**
   * Gets the line style.
   * @return Line style
   * @see JLDataView#setStyle
   */
  public int getStyle() {
    return lineStyle;
  }

  /**
   * Gets the line width.
   * @return Line width
   * @see JLDataView#setLineWidth
   */
  public int getLineWidth() {
    return lineWidth;
  }

  /**
   * Sets the line width (pixel).
   * @param c Line width (pixel)
   * @see JLDataView#getLineWidth
   */
  public void setLineWidth(int c) {
    lineWidth = c;
  }

  /**
   * Sets the view name.
   * @param s Name of this view
   * @see JLDataView#getName
   */
  public void setName(String s) {
    name = s;
  }

  /**
   * Gets the view name.
   * @return Dataview name
   * @see JLDataView#setName
   */
  public String getName() {
    return name;
  }

  /**
   * Set the dataView unit. (Used only for display)
   * @param s Dataview unit.
   * @see JLDataView#getUnit
   */
  public void setUnit(String s) {
    unit = s;
  }

  /**
   * Gets the dataView unit.
   * @return Dataview unit
   * @see JLDataView#setUnit
   */
  public String getUnit() {
    return unit;
  }

  /**
   * Gets the extended name. (including transform description when used)
   * @return Extended name of this view.
   */
  public String getExtendedName() {

    String r;
    String t = "";

    if (hasTransform()) {
      r = name + " [";

      if (A0 != 0.0) {
        r += Double.toString(A0);
        t = " + ";
      }
      if (A1 != 0.0) {
        r = r + t + Double.toString(A1) + "*y";
        t = " + ";
      }
      if (A2 != 0.0) {
        r = r + t + Double.toString(A2) + "*y^2";
      }
      r += "]";

      return r;

    } else
      return name;
  }

  /**
   * Gets the marker type.
   * @return Marker type
   * @see JLDataView#setMarker
   */
  public int getMarker() {
    return markerType;
  }

  /**
   * Sets the marker type.
   * @param m Marker type
   * @see JLDataView#MARKER_NONE
   * @see JLDataView#MARKER_DOT
   * @see JLDataView#MARKER_BOX
   * @see JLDataView#MARKER_TRIANGLE
   * @see JLDataView#MARKER_DIAMOND
   * @see JLDataView#MARKER_STAR
   * @see JLDataView#MARKER_VERT_LINE
   * @see JLDataView#MARKER_HORIZ_LINE
   * @see JLDataView#MARKER_CROSS
   * @see JLDataView#MARKER_CIRCLE
   * @see JLDataView#MARKER_SQUARE
   */
  public void setMarker(int m) {
    markerType = m;
  }

  /**
   * Gets the A0 transformation coeficient.
   * @return A0 value
   * @see JLDataView#setA0
   */
  public double getA0() {
    return A0;
  }

  /**
   * Gets the A1 transformation coeficient.
   * @return A1 value
   * @see JLDataView#setA1
   */
  public double getA1() {
    return A1;
  }

  /**
   * Gets the A2 transformation coeficient.
   * @return A2 value
   * @see JLDataView#setA2
   */
  public double getA2() {
    return A2;
  }

  /**
   * Set A0 transformation coeficient. The transformation computes
   * new value = A0 + A1*v + A2*v*v.
   * Transformation is disabled when A0=A2=0 and A1=1.
   * @param d A0 value
   */
  public void setA0(double d) {
    A0 = d;
  }

  /**
   * Set A1 transformation coeficient. The transformation computes
   * new value = A0 + A1*v + A2*v*v.
   * Transformation is disabled when A0=A2=0 and A1=1.
   * @param d A1 value
   */
  public void setA1(double d) {
    A1 = d;
  }

  /**
   * Set A2 transformation coeficient. The transformation computes
   * new value = A0 + A1*v + A2*v*v.
   * Transformation is disabled when A0=A2=0 and A1=1.
   * @param d A2 value
   */
  public void setA2(double d) {
    A2 = d;
  }

  /**
   * Determines wether this views has a transformation.
   * @return false when A0=A2=0 and A1=1, true otherwise
   */
  public boolean hasTransform() {
    return !(A0 == 0 && A1 == 1 && A2 == 0);
  }

  /** Expert usage.
   * Sets the parent axis.
   * ( Used by JLAxis.addDataView() )
   * @param a Parent axis
   */
  public void setAxis(JLAxis a) {
    parentAxis = a;
  }

  /** Expert usage.
   * Gets the parent axis.
   * @return Parent axis
   */
  public JLAxis getAxis() {
    return parentAxis;
  }

  /** Expert usage.
   * Gets the minimum (Y axis).
   * @return Minimum value
   */
  public double getMinimum() {
    return min;
  }

  /** Expert usage.
   * Gets the maximum (Y axis).
   * @return Maximun value
   */
  public double getMaximum() {
    return max;
  }

  /** Expert usage.
   * Gets the minimun on X axis (with TIME_ANNO).
   * @return Minimum time
   */
  public double getMinTime() {
    if (theData != null)
      return theData.x;
    else
      return Double.MAX_VALUE;
  }

  /** Expert usage.
   * Get the positive minimun on X axis (with TIME_ANNO).
   * @return Minimum value strictly positive
   */
  public double getPositiveMinTime() {
    DataList e = theData;
    boolean found = false;
    while (e != null && !found) {
      found = (e.x > 0);
      if (!found) e = e.next;
    }

    if (e != null)
      return e.x;
    else
      return Double.MAX_VALUE;
  }

  /** Expert usage.
   * Get the maxinmun on X axis (with TIME_ANNO)
   * @return Maximum value
   */
  public double getMaxTime() {
    if (theDataEnd != null)
      return theDataEnd.x;
    else
      return -Double.MAX_VALUE;
  }

  /**
   * Gets the number of data in this view.
   * @return Data length
   */
  public int getDataLength() {
    return dataLength;
  }

  /** Expert usage.
   * Return a handle on DATA.
   * !! If you modifie data, call commitChange() after the update !!
   * @return A handle to the data.
   * @see JLDataView#commitChange()
   */
  public DataList getData() {
    return theData;
  }

  /**
   * Commit change when some data has been in modified in the DataList (via getData())
   * @see JLDataView#getData()
   */
  public void commitChange() {
    this.computeMin();
    this.computeMax();
  }

  /**
   * Add datum to the dataview. If you call this routine directly the graph will be updated only after a repaint
   * and your data won't be garbaged (if a display duration is specified). You should use JLChart.addData instead.
   * @param x x coordinates (real space)
   * @param y y coordinates (real space)
   * @see JLChart#addData
   * @see JLChart#setDisplayDuration
   */
  public void add(double x, double y) {

    if (theData == null) {
      theData = new DataList(x, y);
      theDataEnd = theData;
    } else {
      theDataEnd.next = new DataList(x, y);
      theDataEnd = theDataEnd.next;
    }

    if (y < min) min = y;
    if (y > max) max = y;

    dataLength++;

  }

  /**
   * Garbage old data according to time.
   * @param garbageLimit Limit time (in millisec)
   * @return Number of deleted point.
   */
  public int garbagePointTime(double garbageLimit) {

    boolean need_to_recompute_max = false;
    boolean need_to_recompute_min = false;
    boolean found = false;
    int nbr = 0;

    // Garbage old data

    if (theData != null) {
      double xmax = theDataEnd.x;

      while (theData != null && !found) {
        found = (theData.x > (xmax - garbageLimit));
        if (!found) {
          // Remve first element
          need_to_recompute_max = need_to_recompute_max || (theData.y == max);
          need_to_recompute_min = need_to_recompute_min || (theData.y == min);
          theData = theData.next;
          dataLength--;
          nbr++;
        }
      }
    }

    if (need_to_recompute_max) computeMax();
    if (need_to_recompute_min) computeMin();

    return nbr;
  }

  /**
   * Garbage old data according to data length.
   * It will remove the (dataLength-garbageLimit) first points.
   * @param garbageLimit Index limit
   */
  public void garbagePointLimit(int garbageLimit) {

    boolean need_to_recompute_max = false;
    boolean need_to_recompute_min = false;
    boolean found = false;

    // Garbage old data
    int nb = dataLength - garbageLimit;
    for (int i = 0; i < nb; i++) {
      need_to_recompute_max = need_to_recompute_max || (theData.y == max);
      need_to_recompute_min = need_to_recompute_min || (theData.y == min);
      theData = theData.next;
      dataLength--;
    }

    if (need_to_recompute_max) computeMax();
    if (need_to_recompute_min) computeMin();

  }

  //Compute min
  private void computeMin() {
    min = Double.MAX_VALUE;
    DataList e = theData;
    while (e != null) {
      if (e.y < min) min = e.y;
      e = e.next;
    }
    //System.out.println("JLDataView.computeMin() done.");
  }

  //Compute max
  private void computeMax() {
    max = -Double.MAX_VALUE;
    DataList e = theData;
    while (e != null) {
      if (e.y > max) max = e.y;
      e = e.next;
    }
    //System.out.println("JLDataView.computeMax() done.");
  }

  /** Expert usage.
   * Compute transformed min and max.
   * @return Transformed min and max
   */
  public double[] computeTransformedMinMax() {

    double[] ret = new double[2];

    double mi = Double.MAX_VALUE;
    double ma = -Double.MAX_VALUE;

    DataList e = theData;

    while (e != null) {
      double v = A0 + A1 * e.y + A2 * e.y * e.y;
      if (v < mi) mi = v;
      if (v > ma) ma = v;
      e = e.next;
    }

    if (mi == Double.MAX_VALUE) mi = 0;
    if (ma == -Double.MAX_VALUE) ma = 99;

    ret[0] = mi;
    ret[1] = ma;

    return ret;
  }

  /**
   * Compute minimun of positive value.
   * @return Double.MAX_VALUE when no positive value are found
   */
  public double computePositiveMin() {

    double mi = Double.MAX_VALUE;
    DataList e = theData;
    while (e != null) {
      if (e.y > 0 && e.y < mi) mi = e.y;
      e = e.next;
    }
    return mi;

  }

  /**
   *  Compute transformed value of x.
   * @param x Value to transform
   * @return transformed value (through A0,A1,A2 transformation)
   */
  public double getTransformedValue(double x) {
    return A0 + A1 * x + A2 * x * x;
  }

  /**
   * Get last value.
   * @return Last value
   */
  public DataList getLastValue() {
    return theDataEnd;
  }

  /**
   * Clear all data in this view.
   */
  public void reset() {
    theData = null;
    theDataEnd = null;
    dataLength = 0;
    computeMin();
    computeMax();
  }

  /**
   * Apply dataview configuration.
   * @param prefix settings prefix
   * @param f CfFileReader object wich contains dataview parametters
   * @see JLChart#applyConfiguration
   * @see JLAxis#applyConfiguration
   */

  public void applyConfiguration(String prefix, CfFileReader f) {
    Vector p;

    // Dataview options
    p = f.getParam(prefix + "_linecolor");
    if (p != null) setColor(OFormat.getColor(p));
    p = f.getParam(prefix + "_linewidth");
    if (p != null) setLineWidth(OFormat.getInt(p.get(0).toString()));
    p = f.getParam(prefix + "_linestyle");
    if (p != null) setStyle(OFormat.getInt(p.get(0).toString()));
    p = f.getParam(prefix + "_fillcolor");
    if (p != null) setFillColor(OFormat.getColor(p));
    p = f.getParam(prefix + "_fillmethod");
    if (p != null) setFillMethod(OFormat.getInt(p.get(0).toString()));
    p = f.getParam(prefix + "_fillstyle");
    if (p != null) setFillStyle(OFormat.getInt(p.get(0).toString()));
    p = f.getParam(prefix + "_viewtype");
    if (p != null) setViewType(OFormat.getInt(p.get(0).toString()));
    p = f.getParam(prefix + "_barwidth");
    if (p != null) setBarWidth(OFormat.getInt(p.get(0).toString()));
    p = f.getParam(prefix + "_markercolor");
    if (p != null) setMarkerColor(OFormat.getColor(p));
    p = f.getParam(prefix + "_markersize");
    if (p != null) setMarkerSize(OFormat.getInt(p.get(0).toString()));
    p = f.getParam(prefix + "_markerstyle");
    if (p != null) setMarker(OFormat.getInt(p.get(0).toString()));
    p = f.getParam(prefix + "_A0");
    if (p != null) setA0(OFormat.getDouble(p.get(0).toString()));
    p = f.getParam(prefix + "_A1");
    if (p != null) setA1(OFormat.getDouble(p.get(0).toString()));
    p = f.getParam(prefix + "_A2");
    if (p != null) setA2(OFormat.getDouble(p.get(0).toString()));

  }

  /**
   * Build a configuration string that can be write into a file and is compatible
   * with CfFileReader.
   * @param prefix DataView prefix
   * @return A string containing param.
   * @see JLDataView#applyConfiguration
   */
  public String getConfiguration(String prefix) {

    String to_write="";

    to_write += prefix + "_linecolor:" + OFormat.color(getColor()) + "\n";
    to_write += prefix + "_linewidth:" + getLineWidth() + "\n";
    to_write += prefix + "_linestyle:" + getStyle() + "\n";
    to_write += prefix + "_fillcolor:" + OFormat.color(getFillColor()) + "\n";
    to_write += prefix + "_fillmethod:" + getFillMethod() + "\n";
    to_write += prefix + "_fillstyle:" + getFillStyle() + "\n";
    to_write += prefix + "_viewtype:" + getViewType() + "\n";
    to_write += prefix + "_barwidth:" + getBarWidth() + "\n";
    to_write += prefix + "_markercolor:" + OFormat.color(getMarkerColor()) + "\n";
    to_write += prefix + "_markersize:" + getMarkerSize() + "\n";
    to_write += prefix + "_markerstyle:" + getMarker() + "\n";
    to_write += prefix + "_A0:" + getA0() + "\n";
    to_write += prefix + "_A1:" + getA1() + "\n";
    to_write += prefix + "_A2:" + getA2() + "\n";
    return to_write;

  }

}