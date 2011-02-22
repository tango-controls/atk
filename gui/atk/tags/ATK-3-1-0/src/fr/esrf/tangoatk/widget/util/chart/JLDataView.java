//
// JLDataView.java
// Description: A Class to handle 2D graphics plot
//
// JL Pons (c)ESRF 2002

package fr.esrf.tangoatk.widget.util.chart;


import com.braju.format.Format;

import java.util.*;
import java.awt.*;

/**
 * A class to handle data view. It handles data and all graphics stuff related to a serie of data.
 * @author JL Pons
 */
public class JLDataView implements java.io.Serializable {

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

  /**NaN value used with y to represent a null value*/
  public static final double NAN_FOR_NULL = Double.longBitsToDouble( 0x7ff0000bad0000ffL );
  /**NaN value used with y to represent a positive infinity value*/
  public static final double NAN_FOR_POSITIVE_INFINITY = Double.longBitsToDouble( 0xfffbad00000000ffL );
  /**NaN value used with y to represent a negative infinity value*/
  public static final double NAN_FOR_NEGATIVE_INFINITY = Double.longBitsToDouble( 0xfff00000000badffL );

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
  private double maxXValue;
  private double minXValue;
  private String name;
  private String unit;
  private boolean clickable;
  private boolean labelVisible;
  private String  userFormat;
  private Color labelColor = Color.BLACK;
  // A boolean to know whether data is supposed to be sorted on x
  protected boolean xDataSorted = true;

 /**
  * Returns a string containing the configuration file help.
  */
  public static String getHelpString() {

   return
   "-- Dataview settings --\n  Parameter name is preceded by the dataview name.\n\n" +
   "linecolor:r,g,b  Curve color\n" +
   "linewidth:width  Curve width\n" +
   "linestyle:style  Line style (0 Solid,1 Dot, 2 Dash, 3 Long Dash,...)\n" +
   "fillcolor:r,g,b  Curve fill color\n" +
   "fillmethod:m   Bar filling method (0 Top,1 Zero,2 Bottom)\n" +
   "fillstyle:style  Curve filling style (0 No fill,1 Solid,...)\n" +
   "viewtype:type   Type of plot (0 Line, 1 Bar)\n" +
   "barwidth:width   Bar width in pixel (0 autoscale)\n" +
   "markercolor:r,g,b   Marker color\n" +
   "markersize:size   Marker size\n" +
   "markerstyle:style  Marker style (0 No marker,1 Dot,2 Box,...)\n" +
   "A0,A1,A2:value   Vertical transfrom Y = A0 + A1*y + A2*y*y\n" +
   "A0,A1,A2:value   Vertical transfrom Y = A0 + A1*y + A2*y*y\n" +
   "labelvisible:true or false   Displays legend of this view\n" +
   "clickable:true or false  Shows tooltip on mouse click\n";

  }

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
    minXValue = Double.MAX_VALUE;
    maxXValue = -Double.MAX_VALUE;
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
    clickable=true;
    labelVisible=true;
    userFormat = null;
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
   * @param b Filling style
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

  /**
   * Sets this view clickable or not. When set to false, this view
   * is excluded from the search that occurs when the user click on the
   * chart. Default is true.
   * @param b Clickable state
   */
  public void setClickable(boolean b) {
    clickable=b;
  }

  /**
   * Returns the clickable state.
   * @see JLDataView#setClickable
   */
  public boolean isClickable() {
    return clickable;
  }
  /**
   * Display the label of this view when true.
   * This has effects only if the parent chart has visible labels.
   * @param b visible state
   * @see JLChart#setLabelVisible
   */
  public void setLabelVisible(boolean b) {
    labelVisible=b;
  }

  /** Returns true when the label is visible.
   * @see JLDataView#setLabelVisible
   */
  public boolean isLabelVisible() {
    return labelVisible;
  }

  public Color getLabelColor () {
    return labelColor;
  }

  public void setLabelColor (Color labelColor) {
    this.labelColor = labelColor;
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
   * Gets the minimum on X axis.
   * @return Minimum x value
   */
  public double getMinXValue() {
    if (isXDataSorted()) {
      return getMinTime();
    }
    return minXValue;
  }

  /** Expert usage.
   * Get the positive minimum on X axis.
   * @return Minimum value strictly positive
   */
  public double getPositiveMinXValue() {
    double mi = Double.MAX_VALUE;
    DataList e = theData;
    while (e != null) {
      if (e.x > 0 && e.x < mi) mi = e.x;
      e = e.next;
    }
    return mi;
  }

  /** Expert usage.
   * Get the positive minimum on X axis.
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
   * Get the maximum on X axis
   * @return Maximum x value
   */
  public double getMaxXValue() {
    if (isXDataSorted()) {
      return getMaxTime();
    }
    return maxXValue;
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

  /** Return a handle on the data.
   * If you modify data, call commitChange() after the update.
   * Expert usage.
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
    computeDataBounds();
  }

  /**
   * Add datum to the dataview. If you call this routine directly the graph will be updated only after a repaint
   * and your data won't be garbaged (if a display duration is specified). You should use JLChart.addData instead.
   * @param x x coordinates (real space)
   * @param y y coordinates (real space)
   * @see JLChart#addData
   * @see JLChart#setDisplayDuration
   */
  public synchronized void add(double x, double y) {

    // Convert infinite value to NaN
    if( Double.isInfinite(y) ) {
        if (y > 0) {
            y = NAN_FOR_POSITIVE_INFINITY;
        }
        else {
            y = NAN_FOR_NEGATIVE_INFINITY;
        }
    }

    DataList newData = new DataList(x, y);

    if (newData == null) {
      System.out.println("Warning: Cannot add data to the chart (may be a memory problem...)");
      return;
    }

    if (theData == null) {
      theData = newData;
      theDataEnd = theData;
    } else {
      theDataEnd.next = newData;
      theDataEnd = theDataEnd.next;
    }

    if (y < min) min = y;
    if (y > max) max = y;

    if (x < minXValue) minXValue = x;
    if (x > maxXValue) maxXValue = x;

    dataLength++;

  }

  /**
   * Set data of this dataview.
   * @param x x values
   * @param y y values
   * @see JLDataView#add
   */
  public void setData(double[] x,double[] y) {

    if(x.length != y.length) {
      System.out.println("Warning: Cannot set data, x vector and y vector have not the same length");
      return;
    }
    reset();
    for(int i=0;i<x.length;i++)
      add(x[i],y[i]);

  }

  /**
   * Garbage old data according to time.
   * @param garbageLimit Limit time (in millisec)
   * @return Number of deleted point.
   */
  public synchronized int garbagePointTime(double garbageLimit) {

    boolean need_to_recompute_max = false;
    boolean need_to_recompute_min = false;
    boolean need_to_recompute_max_x_value = false;
    boolean need_to_recompute_min_x_value = false;
    boolean found = false;
    int nbr = 0;
    DataList old;

    // Garbage old data

    if (theData != null) {

      while (theData != null && !found) {
        // Keep 3 seconds more to avoid complete curve
        found = (theData.x > (maxXValue - garbageLimit - 3000));
        if (!found) {
          // Remove first element
          need_to_recompute_max = need_to_recompute_max || (theData.y == max);
          need_to_recompute_min = need_to_recompute_min || (theData.y == min);
          need_to_recompute_max_x_value = need_to_recompute_max_x_value || (theData.x == maxXValue);
          need_to_recompute_min_x_value = need_to_recompute_min_x_value || (theData.x == minXValue);
          old = theData;
          theData = theData.next;
          old.next = null;
          //To be sure that the JVM will clean the data list
          old = null;
          dataLength--;
          nbr++;
        }
      }
    }

    if ( theData==null ) {

      // All points has been removed
      reset();

    } else {

      need_to_recompute_min_x_value = need_to_recompute_min_x_value && !isXDataSorted();
      need_to_recompute_max_x_value = need_to_recompute_max_x_value && !isXDataSorted();

      if (need_to_recompute_max) {
        if(need_to_recompute_min || need_to_recompute_min_x_value || need_to_recompute_max_x_value) {
          computeDataBounds();
        }
        else {
          computeMax();
        }
      }
      else if (need_to_recompute_min) {
        if(need_to_recompute_min_x_value || need_to_recompute_max_x_value) {
          computeDataBounds();
        }
        else {
          computeMin();
        }
      }
      else if (need_to_recompute_max_x_value) {
        if(need_to_recompute_min_x_value) {
          computeDataBounds();
        }
        else {
          computeMaxXValue();
        }
      }
      else if (need_to_recompute_min_x_value){
        computeMinXValue();
      }

    }

    return nbr;
  }

  /**
   * Garbage old data according to data length.
   * It will remove the (dataLength-garbageLimit) first points.
   * @param garbageLimit Index limit
   */
  public synchronized void garbagePointLimit(int garbageLimit) {

    boolean need_to_recompute_max = false;
    boolean need_to_recompute_min = false;
    boolean need_to_recompute_max_x_value = false;
    boolean need_to_recompute_min_x_value = false;
    DataList old;

    // Garbage old data
    int nb = dataLength - garbageLimit;
    for (int i = 0; i < nb; i++) {
      need_to_recompute_max = need_to_recompute_max || (theData.y == max);
      need_to_recompute_min = need_to_recompute_min || (theData.y == min);
      need_to_recompute_max_x_value = need_to_recompute_max_x_value || (theData.x == maxXValue);
      need_to_recompute_min_x_value = need_to_recompute_min_x_value || (theData.x == minXValue);
      old = theData;
      theData = theData.next;
      old.next = null;
      dataLength--;
    }

    need_to_recompute_min_x_value = need_to_recompute_min_x_value && !isXDataSorted();
    need_to_recompute_max_x_value = need_to_recompute_max_x_value && !isXDataSorted();

    if (need_to_recompute_max) {
      if(need_to_recompute_min || need_to_recompute_min_x_value || need_to_recompute_max_x_value) {
        computeDataBounds();
      }
      else {
        computeMax();
      }
    }
    else if (need_to_recompute_min) {
      if(need_to_recompute_min_x_value || need_to_recompute_max_x_value) {
        computeDataBounds();
      }
      else {
        computeMin();
      }
    }
    else if (need_to_recompute_max_x_value) {
      if(need_to_recompute_min_x_value) {
        computeDataBounds();
      }
      else {
        computeMaxXValue();
      }
    }
    else if (need_to_recompute_min_x_value){
      computeMinXValue();
    }

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

  //Compute minXValue
  private void computeMinXValue() {
    minXValue = Double.MAX_VALUE;
    DataList e = theData;
    while (e != null) {
      if (e.x < minXValue) minXValue = e.x;
      e = e.next;
    }
    //System.out.println("JLDataView.computeMinTime() done.");
  }

  //Compute maxXValue
  private void computeMaxXValue() {
    maxXValue = -Double.MAX_VALUE;
    DataList e = theData;
    while (e != null) {
      if (e.x > maxXValue) maxXValue = e.x;
      e = e.next;
    }
    //System.out.println("JLDataView.computeMaxTime() done.");
  }

  /**
   * Computes and stores min and max on x and y
   */
  public void computeDataBounds() {
      minXValue = Double.MAX_VALUE;
      maxXValue = -Double.MAX_VALUE;
      min = Double.MAX_VALUE;
      max = -Double.MAX_VALUE;
      DataList e = theData;
      while (e != null) {
        if (e.x < minXValue) minXValue = e.x;
        if (e.x > maxXValue) maxXValue = e.x;
        if (e.y < min) min = e.y;
        if (e.y > max) max = e.y;
        e = e.next;
      }
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
  public synchronized void reset() {
    theData = null;
    theDataEnd = null;
    dataLength = 0;
    computeDataBounds();
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
    p = f.getParam(prefix + "_labelvisible");
    if (p != null) setLabelVisible(OFormat.getBoolean(p.get(0).toString()));
    p = f.getParam(prefix + "_clickable");
    if (p != null) setClickable(OFormat.getBoolean(p.get(0).toString()));
    p = f.getParam(prefix + "_labelColor");
    if (p != null) setLabelColor(OFormat.getColor(p));

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
    to_write += prefix + "_labelvisible:" + isLabelVisible() + "\n";
    to_write += prefix + "_clickable:" + isClickable() + "\n";
    to_write += prefix + "_labelColor:" + OFormat.color(getLabelColor()) + "\n";
    return to_write;

  }
  
  
  /**
   * Returns Y value according to index.
   * @param idx Value index
   * @return Y value
   */
  public double getYValueByIndex(int idx)
  {
     if (idx<0 || idx>=getDataLength() )
     {
       return Double.NaN;
     }
     
     int i=0;
     DataList e = theData;
     
     while (e != null && i<idx)
     {       
       e=e.next;
       i++;
     }
     
     if( e!=null )
     {
       return e.y;
     }
     else
     {
       return Double.NaN;
     }
         
  }
  
  /**
   * Returns X value according to index.
   * @param idx Value index
   * @return X value
   */
  public double getXValueByIndex(int idx)
  {
     if (idx<0 || idx>=getDataLength() )
     {
       return Double.NaN;
     }
     
     int i=0;
     DataList e = theData;
     
     while (e != null && i<idx)
     {       
       e=e.next;
       i++;
     }
     
     if( e!=null )
     {
       return e.x;
     }
     else
     {
       return Double.NaN;
     }
         
  }

  /**
   * Sets the format property for this dataview (C format).
   * By default, it uses the Axis format.
   * @param format Format (C style)
   */
  public void setUserFormat(String format) {
    if( format != null && format.length() > 0 && isValidFormat(format) ) {
      userFormat = format;
    }
    else {
      StringBuffer errorBuffer = new StringBuffer();
      errorBuffer.append("JLDataView.setUserFormat(String format): ");
      errorBuffer.append(format);
      errorBuffer.append(" is not a valid format !");
      System.err.println( errorBuffer.toString() );
      errorBuffer = null;

      userFormat = null;
    }
  }

  /**
   * Tests whether a given format is valid or not
   * 
   * @param format
   *            the format to tests
   * @return a boolean value : <code>true</code> if the format is valid,
   *         <code>false</code> otherwise.
   */
  public boolean isValidFormat (String format) {
    if ( format.indexOf("%") == 0
         && format.lastIndexOf("%") == format.indexOf("%") ) {
      if ( format.indexOf(".") == -1 ) {
        // case %xd
        try {
          int x = Integer.parseInt(
                  format.substring( 1, format.length() - 1 )
          );
          if (x > 0) {
            return true;
          }
          else return false;
        }
        catch (Exception e) {
          return false;
        }
      }
      else {
        if ( format.indexOf(".") == format.lastIndexOf(".") ) {
          if ( ( (format.toLowerCase().indexOf("f") == format.length() - 1)
                 && (format.toLowerCase().indexOf("f") > 0)
               )
               || ( (format.toLowerCase().indexOf("e") == format.length() - 1)
                    && (format.toLowerCase().indexOf("e") > 0)
                  )
             ) {
            // case %x.yf, %x.ye
            try {
              int x = Integer.parseInt(
                      format.substring( 1, format.indexOf(".") )
              );
              int y = Integer.parseInt(
                      format.substring(
                              format.indexOf(".") + 1,
                              format.length() - 1
                      )
              );
              if ( x > y && x > 0 && y >= 0 ) {
                return true;
              }
              else return false;
            }
            catch (Exception e) {
              return false;
            }
          }
          else return false;
        }
        else return false;
      }
    }
    else return false;
  }

  /**
   * Returns the current user format (null when none).
   */
  public String getUserFormat() {
    return userFormat;
  }

  /**
   * Format the given value according the userFormat or
   * to the Axis format.
   * @param v Value to be formated
   */
  public String formatValue(double v) {

    if(Double.isNaN(v)) {
        long l = Double.doubleToRawLongBits( v );
        if ( l == Double.doubleToRawLongBits(NAN_FOR_NULL) ) {
            return "null";
        }
        if ( l == Double.doubleToRawLongBits(NAN_FOR_NEGATIVE_INFINITY) ) {
            return "-Infinity";
        }
        if ( l == Double.doubleToRawLongBits(NAN_FOR_POSITIVE_INFINITY) ) {
            return "+Infinity";
        }
        return "NaN";
    }

    if(userFormat != null) {

      Object o[] = { new Double(v) };
      String value = Double.toString(v);
      try
      {
          value = Format.sprintf(userFormat, o);
      }
      catch(Exception e)
      {
          value = Double.toString(v);
      }
      return value;

    } else if (parentAxis==null) {

      return Double.toString(v);

    } else {

      return parentAxis.formatValue(v,0);

    }

  }

  /**
   * Returns whether data is supposed to be sorted on x or not
   * @return a boolean value
   */
  public boolean isXDataSorted () {
      return xDataSorted;
  }

  /**
   * Set whether data is supposed to be sorted on x or not.
   * <code>false</code> by default
   * @param dataSorted a boolean value
   */
  public void setXDataSorted (boolean dataSorted) {
    if (xDataSorted && !dataSorted) computeDataBounds();
    xDataSorted = dataSorted;
  }

  public synchronized double[] getSortedTimes() {
    double[] time = new double[dataLength];
    DataList currentData = theData;
    int i = 0;
    while (i < dataLength) {
      time[i] = currentData.x;
      currentData = currentData.next;
    }
    mergeSort( time, null );
    return time;
  }

  public synchronized double[] getSortedValues() {
    double[] value = new double[dataLength];
    DataList currentData = theData;
    int i = 0;
    while (i < dataLength) {
      value[i] = currentData.y;
      currentData = currentData.next;
    }
    mergeSort( value, null );
    return value;
  }

  public synchronized double[][] getDataSortedByTimes() {
    double[][] result = new double[2][dataLength];
    double[] time = new double[dataLength];
    double[] value = new double[dataLength];
    DataList currentData = theData;
    int i = 0;
    while (i < dataLength) {
      time[i] = currentData.x;
      value[i] = currentData.y;
      currentData = currentData.next;
    }
    mergeSort( time, value );
    result[0] = time;
    result[1] = value;
    return result;
  }

  public synchronized double[][] getDataSortedByValues() {
    double[][] result = new double[2][dataLength];
    double[] time = new double[dataLength];
    double[] value = new double[dataLength];
    DataList currentData = theData;
    int i = 0;
    while (i < dataLength) {
      time[i] = currentData.x;
      value[i] = currentData.y;
      currentData = currentData.next;
    }
    mergeSort( value, time );
    result[0] = time;
    result[1] = value;
    return result;
  }

  /**
   * Applies merge sort on an array of double. If an associated array is given,
   * its elements are moved the same way as the array to sort.
   * 
   * @param array
   *            The array to sort
   * @param associated
   *            The associated array. Must be null or of the same length of
   *            <code>array</code>
   */
  public static void mergeSort (double[] array, double[] associated) {
    int length = array.length;
    if ( length > 0 ) {
      mergeSort( array, associated, 0, length - 1 );
    }
  }

  private static void mergeSort (double[] array, double[] associated, int start, int end) {
    if ( start != end ) {
      int middle = ( end + start ) / 2;
      mergeSort( array, associated, start, middle );
      mergeSort( array, associated, middle + 1, end );
      merge( array, associated, start, middle, end );
    }
  }

  private static void merge (double[] array, double[] associated, int start1, int end1, int end2) {
    int deb2 = end1 + 1;
    double[] tempArray = new double[end1 - start1 + 1];
    double[] tempAsso = (associated == null ? null : new double[end1 - start1 + 1]);
    for (int i = start1; i <= end1; i++) {
      tempArray[i - start1] = array[i];
      if (associated != null) {
        tempAsso[i - start1] = associated[i];
      }
    }
    int index1 = start1;
    int index2 = deb2;
    for (int i = start1; i <= end2; i++) {
      if ( index1 == deb2 ) {
        break;
      }
      else if ( index2 == ( end2 + 1 ) ) {
        array[i] = tempArray[index1 - start1];
        if (associated != null) {
          associated[i] = tempAsso[index1 - start1];
        }
        index1++;
      }
      else if ( tempArray[index1 - start1] < array[index2] ) {
        array[i] = tempArray[index1 - start1];
        if (associated != null) {
          associated[i] = tempAsso[index1 - start1];
        }
        index1++;
      }
      else {
        array[i] = array[index2];
        if (associated != null) {
          associated[i] = associated[index2];
        }
        index2++;
      }
    }
  }

}