//
// JLAxis.java
// Description: A Class to handle 2D graphics plot
//
// JL Pons (c)ESRF 2002

package fr.esrf.tangoatk.widget.util.chart;


import java.awt.event.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import com.braju.format.Format;

// Inner class to handle label info

class LabelInfo {

  String value;
  boolean isVisible;
  Dimension size;
  int pos;
  int subtick_step;  // -1 for logarithmic step, 0 None

  LabelInfo(String lab, boolean v, int w, int h, int d, int sub) {
    value = lab;
    size = new Dimension(w, h);
    pos = d;
    subtick_step = sub;
    isVisible = v;
  }

}

// Inner class to handle XY correlation

class XYData {

  public DataList d1;
  public DataList d2;  // View plotted on the xAxis

  XYData(DataList d1, DataList d2) {
    this.d1 = d1;
    this.d2 = d2;
  }

  // Find the next point for XY mode
  void toNextXYPoint() {

    // Correlation mode
    d1 = d1.next;
    while (d1 != null && d2 != null && d2.next != null && d2.next.x <= d1.x) d2 = d2.next;

  }

  // Go to starting time position
  void initFirstPoint() {
    if (d1.x < d2.x) {
      while (d1 != null && d1.x < d2.x) d1 = d1.next;
    } else {
      while (d2 != null && d2.next != null && d2.next.x < d1.x) d2 = d2.next;
    }
  }


  boolean isValid() {
    return (d1 != null && d2 != null);
  }

}

/**
 * Class which handles chart axis.
 * @author JL Pons
 */

public class JLAxis {

  // constant
  /** Horizontal axis at bottom of the chart */
  public static final int HORIZONTAL_DOWN = 1;
  /** Horizontal axis at top of the chart */
  public static final int HORIZONTAL_UP = 2;
  /** Horizontal axis at 0 position (on Y1) */
  public static final int HORIZONTAL_ORG1 = 3;
  /** Horizontal axis at 0 position (on Y2) */
  public static final int HORIZONTAL_ORG2 = 4;
  /** Vertical right axis */
  public static final int VERTICAL_RIGHT = 5;
  /** Vertical left axis */
  public static final int VERTICAL_LEFT = 6;
  /** Vertical axis at X=0 */
  public static final int VERTICAL_ORG = 7;

  /** Draw time annotation for x axis. */
  public static final int TIME_ANNO = 1;
  /** Draw formated annotation  */
  public static final int VALUE_ANNO = 2;

  /** Use linear scale for this axis  */
  public static final int LINEAR_SCALE = 0;
  /** Use logarithmic scale for this axis  */
  public static final int LOG_SCALE = 1;

  /** Use default compiler format to display double */
  public static final int AUTO_FORMAT = 0;
  /** Display value using exponential representation (x.xxEyy) */
  public static final int SCIENTIFIC_FORMAT = 1;
  /** Display number of second as HH:MM:SS */
  public static final int TIME_FORMAT = 2;
  /** Display integer using decimal format */
  public static final int DECINT_FORMAT = 3;
  /** Display integer using haxadecimal format */
  public static final int HEXINT_FORMAT = 4;
  /** Display integer using binary format */
  public static final int BININT_FORMAT = 5;

  static final double YEAR = 31536000000.0;
  static final double MONTH = 2592000000.0;
  static final double DAY = 86400000.0;
  static final double HOUR = 3600000.0;
  static final double MINU = 60000.0;
  static final double SECO = 1000.0;

  //Local declaration
  private double min = 0.0;
  private double max = 100.0;
  private double minimum = 0.0;
  private double maximum = 100.0;
  private boolean autoScale = false;
  private int scale = LINEAR_SCALE;
  private Color labelColor;
  private Font labelFont;
  private int labelFormat;
  private Vector labels;
  private int orientation;
  private int dOrientation;
  private int tick = 10;  // label precision
  private boolean subtickVisible;
  private Dimension csize = null;
  private String name;
  private int annotation = VALUE_ANNO;
  private Vector dataViews;
  private JComponent parent;
  private double ln10;
  private boolean gridVisible;
  private boolean subGridVisible;
  private int gridStyle;
  private Rectangle boundRect;
  private boolean lastAutoScate;
  private boolean isZoomed;
  private double percentScrollback;
  private String axeName;
  private java.text.SimpleDateFormat useFormat;
  private double desiredPrec;

  //Global
  static final java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
  static final java.text.SimpleDateFormat genFormat = new java.text.SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");
  static final java.text.SimpleDateFormat yearFormat = new java.text.SimpleDateFormat("yyyy");
  static final java.text.SimpleDateFormat monthFormat = new java.text.SimpleDateFormat("MMMMM yy");
  static final java.text.SimpleDateFormat weekFormat = new java.text.SimpleDateFormat("dd/MM/yy");
  static final java.text.SimpleDateFormat dayFormat = new java.text.SimpleDateFormat("EEE dd");
  static final java.text.SimpleDateFormat hour12Format = new java.text.SimpleDateFormat("EEE HH:mm");
  static final java.text.SimpleDateFormat hourFormat = new java.text.SimpleDateFormat("HH:mm");
  static final java.text.SimpleDateFormat secFormat = new java.text.SimpleDateFormat("HH:mm:ss");

  static final double[] timePrecs = {
    1 * SECO, 5 * SECO, 10 * SECO, 30 * SECO,
    1 * MINU, 5 * MINU, 10 * MINU, 30 * MINU,
    1 * HOUR, 3 * HOUR, 6 * HOUR, 12 * HOUR,
    1 * DAY, 7 * DAY, 1 * MONTH, 1 * YEAR, 5 * YEAR,
    10 * YEAR
  };

  static final java.text.SimpleDateFormat timeFormats[] = {
    secFormat, secFormat, secFormat, secFormat,
    secFormat, secFormat, secFormat, hourFormat,
    hourFormat, hourFormat, hourFormat, hour12Format,
    dayFormat, weekFormat, monthFormat, yearFormat,
    yearFormat, yearFormat};

  static final String labelFomats[] = {"%g", "", "%02d:%02d:%02d", "%d", "%X", "%b"};

  static final int triangleX[] = {0, 4, -4};
  static final int triangleY[] = {-3, 3, 3};
  static final Polygon triangleShape = new Polygon(triangleX, triangleY, 3);

  static final int diamondX[] = {0, 4, 0, -4};
  static final int diamondY[] = {4, 0, -4, 0};
  static final Polygon diamondShape = new Polygon(diamondX, diamondY, 4);

  static double linStep[] = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
  static double logStep[] = {0.301, 0.477, 0.602, 0.699, 0.778, 0.845, 0.903, 0.954};

  /** Axis constructor (Expert usage).
   * @param orientation Default Axis placement (cannot be ..._ORG).
   * @param parent parent chart
   * @see JLAxis#HORIZONTAL_DOWN
   * @see JLAxis#HORIZONTAL_UP
   * @see JLAxis#VERTICAL_LEFT
   * @see JLAxis#VERTICAL_RIGHT
   * @see JLAxis#setPosition
   */
  public JLAxis(JComponent parent, int orientation) {
    labels = new Vector();
    labelFont = new Font("Dialog", Font.BOLD, 11);
    labelColor = Color.black;
    name = null;
    this.orientation = orientation;
    dOrientation = orientation;
    dataViews = new Vector();
    this.parent = parent;
    ln10 = Math.log(10);
    gridVisible = false;
    subGridVisible = false;
    gridStyle = JLDataView.STYLE_DOT;
    labelFormat = AUTO_FORMAT;
    subtickVisible = true;
    boundRect = new Rectangle(0, 0, 0, 0);
    isZoomed = false;
    percentScrollback = 0.025;
    axeName = "";
  }


  /**
   * Sets the percent scrollback. When using {@link JLChart#addData(JLDataView , double , double ) JLChart.addData}
   * and TIME_ANNO mode for the horizontal axis this property allows to avoid a full graph repaint
   * for every new data entered.
   * @param d Scrollback percent [0..100]
   */
  public void setPercentScrollback(double d) {
    percentScrollback = d / 100;
  }

  /**
   * Gets the percent scrollback
   * @return scrollback percent
   */
  public double getPercentScrollback() {
    return percentScrollback;
  }

  /**
   * Sets the axis color.
   * @param c Axis color
   * @see JLAxis#getAxisColor
   */
  public void setAxisColor(Color c) {
    labelColor = c;
  }

  /**
   * Returns the axis color.
   * @return Axis color
   * @see JLAxis#setAxisColor
   */
  public Color getAxisColor() {
    return labelColor;
  }

  /**
   * Sets the axis label format.
   * @param l Format of values displayed on axis and in tooltips.
   * @see  JLAxis#AUTO_FORMAT
   * @see  JLAxis#SCIENTIFIC_FORMAT
   * @see  JLAxis#TIME_FORMAT
   * @see  JLAxis#DECINT_FORMAT
   * @see  JLAxis#HEXINT_FORMAT
   * @see  JLAxis#BININT_FORMAT
   * @see  JLAxis#getLabelFormat
   */
  public void setLabelFormat(int l) {
    labelFormat = l;
  }

  /**
   * Returns the axis label format.
   * @return Axis value format
   * @see  JLAxis#setLabelFormat
   */
  public int getLabelFormat() {
    return labelFormat;
  }

  /**
   * Shows the grid.
   * @param b true to make the grid visible; false to hide it
   * @see  JLAxis#isGridVisible
   */
  public void setGridVisible(boolean b) {
    gridVisible = b;
  }

  /**
   * Determines whether the axis is showing the grid
   * @return true if the grid is visible, false otherwise
   * @see  JLAxis#setGridVisible
   */
  public boolean isGridVisible() {
    return gridVisible;
  }

  /**
   * Shows the sub grid. More accurate grid displayed with a soft color.
   * @param b true to make the subgrid visible; false to hide it
   * @see  JLAxis#isSubGridVisible
   */
  public void setSubGridVisible(boolean b) {
    subGridVisible = b;
  }

  /** Determines whether the axis is showing the sub grid
   *  @return true if the subgrid is visible, false otherwise
   *  @see JLAxis#setSubGridVisible
   */
  public boolean isSubGridVisible() {
    return subGridVisible;
  }

  /** Sets the grid style.
   * @param s Style of the grid. Can be one of the following:
   * @see JLDataView#STYLE_SOLID
   * @see JLDataView#STYLE_DOT
   * @see JLDataView#STYLE_DASH
   * @see JLDataView#STYLE_LONG_DASH
   * @see JLDataView#STYLE_DASH_DOT
   * @see JLAxis#getGridStyle
   */
  public void setGridStyle(int s) {
    gridStyle = s;
  }

  /**
   * Returns the current grid style.
   * @return the current grid style
   * @see JLAxis#setGridStyle
   */
  public int getGridStyle() {
    return gridStyle;
  }

  /**
   * Sets the label font
   * @param f Sets the font for this components
   * @see JLAxis#getFont
   */
  public void setFont(Font f) {
    labelFont = f;
  }

  /**
   * Gets the label font
   * @return The current label font
   * @see JLAxis#setFont
   */
  public Font getFont() {
    return labelFont;
  }

  /**
   * Set the annotation method
   * @param a Annotation for this axis
   * @see JLAxis#TIME_ANNO
   * @see JLAxis#VALUE_ANNO
   */
  public void setAnnotation(int a) {
    annotation = a;
  }

  public int getAnnotation() {
    return annotation;
  }

  /** Determines whether the axis is zoomed.
   * @return true if the axis is zoomed, false otherwise
   * @see JLAxis#zoom
   */
  public boolean isZoomed() {
    return isZoomed;
  }

  /** Determines whether the axis is in XY mode. Use only with HORIZONTAL axis.
   *  @return true if the axis is in XY mode, false otherwise
   *  @see JLAxis#addDataView
   */
  public boolean isXY() {
    return (dataViews.size() > 0);
  }

  /**
   * Sets minimum axis value. This value is ignored when using autoscale.
   * @param d Minimum value for this axis. Must be strictly positive for LOG_SCALE.
   * @see JLAxis#getMinimum
   */
  public void setMinimum(double d) {

    minimum = d;

    if (!autoScale) {
      if (scale == LOG_SCALE) {
        if (d <= 0) d = 1;
        min = Math.log(d) / ln10;
      } else
        min = d;
    }

  }

  /**
   * Gets minimum axis value
   * @return  The minimum value for this axis.
   * @see JLAxis#setMinimum
   */
  public double getMinimum() {
    return minimum;
  }

  /**
   * Sets maximum axis value. This value is ignored when using autoscale.
   * @param d Maximum value for this axis. Must be strictly positive for LOG_SCALE.
   * @see JLAxis#getMaximum
   */
  public void setMaximum(double d) {

    maximum = d;

    if (!autoScale) {
      if (scale == LOG_SCALE) {
        if (max <= 0) max = min * 10.0;
        max = Math.log(d) / ln10;
      } else
        max = d;
    }

  }

  /**
   * Gets maximum axis value
   * @return  The maximum value for this axis.
   * @see JLAxis#setMaximum
   */
  public double getMaximum() {
    return maximum;
  }

  /**
   * Expert usage. Get minimum axis value (according to auto scale transformation).
   * @return  The minimum value for this axis.
   */
  public double getMin() {
    return min;
  }

  /**
   * Expert usage. Get maximum axis value (according to auto scale transformation).
   * @return  The maximum value for this axis.
   */
  public double getMax() {
    return max;
  }

  /** Determines whether the axis is autoscaled.
   * @return true if the axis is autoscaled, false otherwise
   * @see JLAxis#setAutoScale
   */
  public boolean isAutoScale() {
    return autoScale;
  }

  /**
   * Sets the autoscale mode for this axis.
   * @param b true if the axis is autoscaled, false otherwise
   * @see JLAxis#isAutoScale
   */
  public void setAutoScale(boolean b) {
    autoScale = b;
  }

  /** Gets the scale mdoe for this axis.
   * @return scale mdoe
   * @see JLAxis#setScale
   */
  public int getScale() {
    return scale;
  }

  /** Sets scale mode
   * @param s Scale mode for this axis
   * @see JLAxis#LINEAR_SCALE
   * @see JLAxis#LOG_SCALE
   * @see JLAxis#getScale
   */
  public void setScale(int s) {

    scale = s;

    if (scale == LOG_SCALE) {
      // Check min and max
      if (minimum <= 0 || maximum <= 0) {
        minimum = 1;
        maximum = 10;
      }
    }

    if (scale == LOG_SCALE) {
      min = Math.log(minimum) / ln10;
      max = Math.log(maximum) / ln10;
    } else {
      min = minimum;
      max = maximum;
    }

  }

  /** Zoom axis.
   * @param x1 New minimum value for this axis
   * @param x2 New maximum value for this axis
   * @see JLAxis#isZoomed
   * @see JLAxis#unzoom
   */
  public void zoom(int x1, int x2) {

    if (!isZoomed) lastAutoScate = autoScale;

    if (isHorizontal()) {

      // Clip
      if (x1 < boundRect.x) x1 = boundRect.x;
      if (x2 > (boundRect.x + boundRect.width)) x2 = boundRect.x + boundRect.width;

      // Too small zoom
      if ((x2 - x1) < 10) return;

      // Compute new min and max
      double xr1 = (double) (x1 - boundRect.x) / (double) (boundRect.width);
      double xr2 = (double) (x2 - boundRect.x) / (double) (boundRect.width);
      double nmin = min + (max - min) * xr1;
      double nmax = min + (max - min) * xr2;
      min = nmin;
      max = nmax;

    } else {

      // Clip
      if (x1 < boundRect.y) x1 = boundRect.y;
      if (x2 > (boundRect.y + boundRect.height)) x2 = boundRect.y + boundRect.height;

      // Too small zoom
      if ((x2 - x1) < 10) return;

      // Compute new min and max
      double yr1 = (double) (boundRect.y + boundRect.height - x2) / (double) (boundRect.height);
      double yr2 = (double) (boundRect.y + boundRect.height - x1) / (double) (boundRect.height);
      double nmin = min + (max - min) * yr1;
      double nmax = min + (max - min) * yr2;
      min = nmin;
      max = nmax;

    }

    autoScale = false;
    isZoomed = true;

  }

  /** Unzoom the axis and restores last state.
   * @see JLAxis#isZoomed
   * @see JLAxis#unzoom
   */
  public void unzoom() {
    autoScale = lastAutoScate;
    if (!lastAutoScate) {
      setMinimum(getMinimum());
      setMaximum(getMaximum());
    }
    isZoomed = false;
  }

  /** Get the labels interval ( tick is the desired number of label on the axis ).
   *  if font overlap happens, you may get less labels.
   * @return Tick number.
   * @see JLAxis#setTick
   */
  public int getTick() {
    return tick;
  }

  /** Sets the label interval.
   * @param s Desired number of label for this axis.
   * @see JLAxis#getTick
   */
  public void setTick(int s) {
    tick = s;
  }

  /** Gets the axis label.
   * @return Axis name.
   * @see JLAxis#setName
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the axis label.
   * Label is displayed along or above the axis.
   * @param s Name of this axis.
   * @see JLAxis#getName
   */
  public void setName(String s) {

    int z = 0;
    if (s != null) z = s.length();

    if (z > 0)
      name = s;
    else
      name = null;

  }

  /**
   * Sets the axis position
   * @param o Axis position
   * @see JLAxis#VERTICAL_LEFT
   * @see JLAxis#VERTICAL_RIGHT
   * @see JLAxis#VERTICAL_ORG
   * @see JLAxis#HORIZONTAL_DOWN
   * @see JLAxis#HORIZONTAL_UP
   * @see JLAxis#HORIZONTAL_ORG1
   * @see JLAxis#HORIZONTAL_ORG2
   */
  public void setPosition(int o) {
    if (isHorizontal()) {
      if (o >= JLAxis.HORIZONTAL_DOWN && o <= JLAxis.HORIZONTAL_ORG2)
        orientation = o;
    } else {
      if (o >= JLAxis.VERTICAL_RIGHT && o <= JLAxis.VERTICAL_ORG)
        orientation = o;
    }
  }

  /**
   * Returns the axis position
   * @return Axis position
   * @see JLAxis#setPosition
   */
  int getPosition() {
    return orientation;
  }

  /** Gets the axis label.
   * @return Axis name.
   * @see JLAxis#setAxeName
   */
  public String getAxeName() {
    return axeName;
  }

  /** Sets the axis name. Name is displayed in tooltips when clicking on the graph.
   * @param s Name of this axis.
   * @see JLAxis#getName
   */
  public void setAxeName(String s) {
    axeName = s;
  }

  /** Displays a DataView on this axis.
   * The graph switches in XY monitoring mode when adding
   * a dataView to X axis. Only one view is allowed on HORIZONTAL Axis.
   * In case of a view plotted along this horizontal axis doesn't have
   * the same number of point as this x view, points are correlated according to
   * their x values.
   * @param v The dataview to map along this axis.
   * @see JLAxis#removeDataView
   * @see JLAxis#clearDataView
   * @see JLAxis#getViews
   */
  public void addDataView(JLDataView v) {

    if (dataViews.contains(v))
      return;

    if (!isHorizontal()) {
      dataViews.add(v);
      v.setAxis(this);
    } else {
      // Switch to XY mode
      // Only one view on X
      dataViews.clear();
      dataViews.add(v);
      v.setAxis(this);
      setAnnotation(VALUE_ANNO);
    }
  }

  /** Removes dataview from this axis
   * @param v dataView to remove from this axis.
   * @see JLAxis#addDataView
   * @see JLAxis#clearDataView
   * @see JLAxis#getViews
   */
  public void removeDataView(JLDataView v) {
    dataViews.remove(v);
    v.setAxis(null);
    if (isHorizontal()) {
      // Restore TIME_ANNO and Liner scale
      setAnnotation(TIME_ANNO);
      if (scale != LINEAR_SCALE) setScale(LINEAR_SCALE);
    }
  }

  /** Clear all dataview from this axis
   * @see JLAxis#removeDataView
   * @see JLAxis#addDataView
   * @see JLAxis#getViews
   */
  public void clearDataView() {
    int sz = dataViews.size();
    JLDataView v;
    for (int i = 0; i < sz; i++) {
      v = (JLDataView) dataViews.get(i);
      v.setAxis(null);
    }
    dataViews.clear();
  }

  /** Gets all dataViews displayed on this axis.
   * Do not modify the returned vector (Use as read only).
   * @return Vector of JLDataView.
   * @see JLAxis#addDataView
   * @see JLAxis#removeDataView
   * @see JLAxis#clearDataView
   */
  public Vector getViews() {
    return dataViews;
  }

  /**
   * Returns the bouding rectangle of this axis.
   * @return The bounding rectangle
   */
  public Rectangle getBoundRect() {
    return boundRect;
  }

  /** Return a scientific (exponential) representation of the double.
   * @param a souble to convert
   * @return A string continaing a scientific representation of the given double.
   */
  public String toScientific(double d) {

    double a = Math.abs(d);
    int e = 0;
    String f = "%.2fE%d";

    if (a != 0) {
      if (a < 1) {
        while (a < 1) {
          a = a * 10;
          e--;
        }
      } else {
        while (a >= 10) {
          a = a / 10;
          e++;
        }
      }
    }

    if (a >= 9.999999999) {
      a = a / 10;
      e++;
    }

    if (d < 0) a = -a;

    Object o[] = {new Double(a), new Integer(e)};

    return Format.sprintf(f, o);

  }

  /**
   * Returns a representation of the double in time format "EEE, d MMM yyyy HH:mm:ss".
   * @param vt number of millisec since epoch
   * @return A string continaing a time representation of the given double.
   */
  public static String formatTimeValue(double vt) {
    java.util.Date date;
    calendar.setTimeInMillis((long) vt);
    date = calendar.getTime();
    return genFormat.format(date);
  }


  /**
   * Sets the appropriate time format for the range to display
   * @return Time format
   */
  private void computeDateformat() {

    //find optimal precision
    boolean found = false;
    int i = 0;
    while (i < timePrecs.length && !found) {
      int n = (int) ((max - min) / timePrecs[i]);
      found = (n <= tick);
      if (!found) i++;
    }

    if (!found) {
      // TODO Year Linear scale
      i--;
      desiredPrec = 10 * YEAR;
      useFormat = yearFormat;
    } else {
      desiredPrec = timePrecs[i];
      useFormat = timeFormats[i];
    }

  }


  /**
   * Returns a representation of the double acording to the format
   * @param vt double to convert
   * @param prec Desired precision (Pass 0 to not perform prec rounding).
   * @return A string continaing a formated representation of the given double.
   */
  public String formatValue(double vt, double prec) {

    // Round value according to desired prec
    // TODO: rounding in LOG_SCALE
    if (prec != 0 && scale == LINEAR_SCALE) {
      long r;
      if (vt >= 0) {
        vt = vt / prec * 1e5;
        r = (long) (vt + 0.5);
        vt = (r * prec) / 1e5;
      } else {
        vt = -vt / prec * 1e5;
        r = (long) (vt + 0.5);
        vt = -(r * prec) / 1e5;
      }
    }

    switch (labelFormat) {
      case SCIENTIFIC_FORMAT:
        return toScientific(vt);

      case DECINT_FORMAT:
      case HEXINT_FORMAT:
      case BININT_FORMAT:
        Object[] o2 = {new Integer((int) (Math.abs(vt)))};
        if (vt < 0.0)
          return "-" + Format.sprintf(labelFomats[labelFormat], o2);
        else
          return Format.sprintf(labelFomats[labelFormat], o2);

      case TIME_FORMAT:

        int sec = (int) (Math.abs(vt));
        Object[] o3 = {
          new Integer(sec / 3600),
          new Integer((sec % 3600) / 60),
          new Integer(sec % 60)};

        if (vt < 0.0)
          return "-" + Format.sprintf(labelFomats[labelFormat], o3);
        else
          return Format.sprintf(labelFomats[labelFormat], o3);

      default:

        return Double.toString(vt);

    }

  }

  private boolean isHorizontal() {
    return (dOrientation == HORIZONTAL_DOWN) ||
      (dOrientation == HORIZONTAL_UP);
  }

  // *****************************************************
  // AutoScaling stuff
  // Expert usage

  // log10(x) = ln(x)/ln(10);
  private double computeHighTen(double d) {
    int p = (int) (Math.log(d) / ln10);
    return Math.pow(10.0, p + 1);
  }

  private double computeLowTen(double d) {
    int p = (int) (Math.log(d) / ln10);
    return Math.pow(10.0, p);
  }

  private void computeAutoScale() {

    int i = 0;
    int sz = dataViews.size();
    double mi = 0,ma = 0;

    if (autoScale && sz > 0) {

      JLDataView v;
      min = Double.MAX_VALUE;
      max = -Double.MAX_VALUE;

      for (i = 0; i < sz; i++) {

        v = (JLDataView) dataViews.get(i);

        if (v.hasTransform()) {
          double[] mm = v.computeTransformedMinMax();
          mi = mm[0];
          ma = mm[1];
        } else {
          mi = v.getMinimum();
          ma = v.getMaximum();
        }

        if (scale == LOG_SCALE) {

          if (mi <= 0) mi = v.computePositiveMin();
          if (mi != Double.MAX_VALUE) mi = Math.log(mi) / ln10;

          if (ma <= 0)
            ma = -Double.MAX_VALUE;
          else
            ma = Math.log(ma) / ln10;
        }

        if (ma > max) max = ma;
        if (mi < min) min = mi;

      }

      // Check max and min
      if (min == Double.MAX_VALUE && max == -Double.MAX_VALUE) {

        // Only invalid data !!
        if (scale == LOG_SCALE) {
          min = 0;
          max = 1;
        } else {
          min = 0;
          max = 99.99;
        }

      }

      if ((max - min) < 1e-100) {
        max += 0.999;
        min -= 0.999;
      }

      double prec = computeLowTen(max - min);

      //System.out.println("ComputeAutoScale: Prec= " + prec );

      if (min < 0)
        min = ((int) (min / prec) - 1) * prec;
      else
        min = (int) (min / prec) * prec;


      if (max < 0)
        max = (int) (max / prec) * prec;
      else
        max = ((int) (max / prec) + 1) * prec;

      //System.out.println("ComputeAutoScale: " + min + "," + max );

    } // end ( if autoScale )

  }

  /**
   * Expert usage. Compute X auto scale (HORIZONTAL axis only)
   * @param views All views displayed along all Y axis.
   */
  public void computeXScale(Vector views) {

    int i = 0;
    int sz = views.size();
    double t;
    double mi,ma;

    if (isHorizontal() && autoScale && sz > 0) {

      if (!isXY()) {

        //******************************************************
        // Classic monitoring

        JLDataView v;
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;

// Horizontal autoScale

        for (i = 0; i < sz; i++) {

          v = (JLDataView) views.get(i);

          ma = v.getMaxTime();
          mi = v.getMinTime();

          if (scale == LOG_SCALE) {
            if (mi <= 0) mi = v.getPositiveMinTime();
            if (mi != Double.MAX_VALUE) mi = Math.log(mi) / ln10;

            if (ma <= 0)
              ma = -Double.MAX_VALUE;
            else
              ma = Math.log(ma) / ln10;
          }

          if (ma > max) max = ma;
          if (mi < min) min = mi;

        }


        if (min == Double.MAX_VALUE && max == -Double.MAX_VALUE) {

          // Only empty views !

          if (scale == LOG_SCALE) {

            min = 0;
            max = 1;

          } else {

            if (annotation == TIME_ANNO) {
              min = System.currentTimeMillis() - HOUR;
              max = System.currentTimeMillis();
            } else {
              min = 0;
              max = 99.99;
            }

          }

        }

        if (annotation == TIME_ANNO) {
          // percent scrollBack
          max += (max - min) * percentScrollback;
        }

        if ((max - min) < 1e-100) {
          max += 0.999;
          min -= 0.999;
        }

      } else {

        //******************************************************
        // XY monitoring
        computeAutoScale();

      }

    }


  }

  // *****************************************************
  // Measurements stuff

  /**
   * Expert usage.
   * @param g Graphics object
   * @return Axis font height.
   */
  public int getFontHeight(Graphics g) {
    if (isHorizontal()) {
      if (name != null)
        return 2 * g.getFontMetrics(labelFont).getHeight();
      else
        return g.getFontMetrics(labelFont).getHeight();
    } else {
      if (name != null)
        return g.getFontMetrics(labelFont).getHeight();
      else
        return 5;
    }
  }

  /**
   * Expert usage.
   * Returns axis tichkness in pixel ( shorter side )
   * @return Axis tichkness
   * @see JLAxis#getLength
   */
  public int getThickness() {
    if (csize != null) {
      if (!isHorizontal())
        return csize.width;
      else
        return csize.height;
    }
    return 0;
  }

  /**
   * Expert usage.
   * Returns axis lenght in pixel ( larger side ).
   * @return Axis lenght.
   * @see JLAxis#getThickness
   */
  public int getLength() {
    if (csize != null) {
      if (isHorizontal())
        return csize.width;
      else
        return csize.height;
    }
    return 0;
  }

  /**
   * Expert usage.
   * Computes labels and measures axis dimension.
   * @param g Grpahics object
   * @param frc Font render context
   * @param desiredWidth Desired width
   * @param desiredHeight Desired height
   */
  public void measureAxis(Graphics g, FontRenderContext frc, int desiredWidth, int desiredHeight) {

    int i;
    int max_width = 10; // Minimun width
    int max_height = 0;


    g.setFont(labelFont);

    computeAutoScale();

    if (!isHorizontal())
      computeLabels(frc, desiredHeight, true);
    else
      computeLabels(frc, desiredWidth, false);

    for (i = 0; i < labels.size(); i++) {
      LabelInfo li = (LabelInfo) labels.get(i);
      if (li.size.width > max_width)
        max_width = li.size.width;
      if (li.size.height > max_height)
        max_height = li.size.height;
    }

    if (!isHorizontal())
      csize = new Dimension(max_width + 5, desiredHeight);
    else
      csize = new Dimension(desiredWidth, max_height);

  }

  // ****************************************************************
  //	search nearest point stuff

  /**
   * Expert usage.
   * Transfrom given coordinates (real space) into pixel coordinates
   * @param x The x coordinates (Real space)
   * @param y The y coordinates (Real space)
   * @param xAxis The axis corresponding to x coordinates.
   * @return Point(-100,-100) when cannot transform
   */
  public Point transform(double x, double y, JLAxis xAxis) {

    // The graph must have been measured before
    // we can transform
    if (csize == null) return new Point(-100, -100);

    double xlength = (xAxis.getMax() - xAxis.getMin());
    int xOrg = boundRect.x;
    int yOrg = boundRect.y + getLength();
    double vx,vy;

    // Check validity
    if (Double.isNaN(y) || Double.isNaN(x))
      return new Point(-100, -100);

    if (xAxis.getScale() == LOG_SCALE) {
      if (x <= 0)
        return new Point(-100, -100);
      else
        vx = Math.log(x) / ln10;
    } else
      vx = x;

    if (scale == LOG_SCALE) {
      if (y <= 0)
        return new Point(-100, -100);
      else
        vy = Math.log(y) / ln10;

    } else
      vy = y;

    double xratio = (vx - xAxis.getMin()) / (xlength) * (xAxis.getLength());
    double yratio = -(vy - min) / (max - min) * csize.height;

    // Saturate
    if (xratio < -32000) xratio = -32000;
    if (xratio > 32000) xratio = 32000;
    if (yratio < -32000) yratio = -32000;
    if (yratio > 32000) yratio = 32000;

    return new Point((int) (xratio) + xOrg, (int) (yratio) + yOrg);

  }

  //Return the square distance
  private int distance2(int x1, int y1, int x2, int y2) {
    return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
  }

  /** Expert usage.
   * Search the nearest point in the dataViews in normal monitoring mode
   * @param x The x coordinates (Real space)
   * @param y The y coorsinates (Real space)
   * @param xAxis The axis corresponding to x coordinates.
   * @return A structure containing search result.
   * @see JLAxis#searchNearestXY
   */
  public SearchInfo searchNearestNormal(int x, int y, JLAxis xAxis) {

    int sz = dataViews.size();
    int norme2;
    DataList minP = null;
    Point minPt = null;
    int minNorme = Integer.MAX_VALUE;
    JLDataView minDataView = null;
    int minPl = 0;

    Rectangle boundRect2 = new Rectangle();
    boundRect2.setBounds(boundRect.x - 2, boundRect.y - 2, boundRect.width + 4, boundRect.height + 4);

    for (int i = 0; i < sz; i++) {

      JLDataView v = (JLDataView) dataViews.get(i);
      DataList e = v.getData();

      while (e != null) {

        Point p = transform(e.x, v.getTransformedValue(e.y), xAxis);

        if (boundRect2.contains(p)) {
          norme2 = distance2(x, y, p.x, p.y);
          if (norme2 < minNorme) {

            minNorme = norme2;
            minP = e;
            minDataView = v;
            minPt = p;

            // Compute placement for the value info window
            if (p.x < (boundRect.x + boundRect.width / 2)) {
              if (p.y < (boundRect.y + boundRect.height / 2)) {
                minPl = SearchInfo.BOTTOMRIGHT;
              } else {
                minPl = SearchInfo.TOPRIGHT;
              }
            } else {
              if (p.y < (boundRect.y + boundRect.height / 2)) {
                minPl = SearchInfo.BOTTOMLEFT;
              } else {
                minPl = SearchInfo.TOPLEFT;
              }
            }
          }
        }

        e = e.next;

      }

    }

    if (minNorme == Integer.MAX_VALUE)
      return new SearchInfo(); //No item found
    else
      return new SearchInfo(minPt.x, minPt.y, minDataView, this, minP, minNorme, minPl); //No item found

  }

  /** Expert usage.
   * Search the nearest point in the dataViews in XY monitoring mode
   * @param x The x coordinates (Real space)
   * @param y The y coorsinates (Real space)
   * @param xAxis The axis corresponding to x coordinates.
   * @return A structure containing search result.
   * @see JLAxis#searchNearestNormal
   */

  public SearchInfo searchNearestXY(int x, int y, JLAxis xAxis) {

    int sz = dataViews.size();
    int norme2;
    DataList minP = null;
    DataList minXP = null;
    Point minPt = null;
    int minNorme = Integer.MAX_VALUE;
    JLDataView minDataView = null;
    int minPl = 0;

    JLDataView w = (JLDataView) xAxis.getViews().get(0);

    Rectangle boundRect2 = new Rectangle();
    boundRect2.setBounds(boundRect.x - 2, boundRect.y - 2, boundRect.width + 4, boundRect.height + 4);

    for (int i = 0; i < sz; i++) {

      JLDataView v = (JLDataView) dataViews.get(i);
      XYData e = new XYData(v.getData(), w.getData());

      if (e.isValid()) e.initFirstPoint();

      while (e.isValid()) {

        Point p = transform(w.getTransformedValue(e.d2.y), v.getTransformedValue(e.d1.y), xAxis);

        if (boundRect2.contains(p)) {
          norme2 = distance2(x, y, p.x, p.y);
          if (norme2 < minNorme) {

            minNorme = norme2;
            minP = e.d1;
            minXP = e.d2;
            minDataView = v;
            minPt = p;

            // Compute placement for the value info window
            if (p.x < (boundRect.x + boundRect.width / 2)) {
              if (p.y < (boundRect.y + boundRect.height / 2)) {
                minPl = SearchInfo.BOTTOMRIGHT;
              } else {
                minPl = SearchInfo.TOPRIGHT;
              }
            } else {
              if (p.y < (boundRect.y + boundRect.height / 2)) {
                minPl = SearchInfo.BOTTOMLEFT;
              } else {
                minPl = SearchInfo.TOPLEFT;
              }
            }
          }
        }

        e.toNextXYPoint();

      }

    }

    if (minNorme == Integer.MAX_VALUE)
      return new SearchInfo(); //No item found
    else {
      SearchInfo si = new SearchInfo(minPt.x, minPt.y, minDataView, this, minP, minNorme, minPl);
      si.setXValue(minXP, w);
      return si;
    }

  }

  /**
   * Search the nearest point in the dataViews.
   * @param x The x coordinates (Real space)
   * @param y The y coordinates (Real space)
   * @param xAxis The axis corresponding to x coordinates.
   * @return A structure containing search result.
   */
  public SearchInfo searchNearest(int x, int y, JLAxis xAxis) {

    int sz = dataViews.size();
    int norme2;
    DataList minP = null;
    Point minPt = null;
    int minNorme = Integer.MAX_VALUE;
    JLDataView minDataView = null;
    int minPl = 0;

    //Search only in graph area
    if (!boundRect.contains(x, y)) return new SearchInfo();

    if (xAxis.isXY()) {
      return searchNearestXY(x, y, xAxis);
    } else {
      return searchNearestNormal(x, y, xAxis);
    }


  }

  // ****************************************************************
  // Compute labels
  // Expert usage
  private void computeLabels(FontRenderContext frc, double length, boolean invert) {

    double sz = max - min;
    int pos,w,h,i;
    int lgth = (int) length;
    java.util.Date date;
    String s;
    double startx;
    double prec;
    LabelInfo lastLabel = null;

    labels.clear();
    Rectangle2D bounds;

    switch (annotation) {

      case TIME_ANNO:

        // Only for HORINZONTAL axis !
        // This has nothing to do with TIME_FORMAT

        computeDateformat();

        // round to multiple of prec
        int round;
        round = (int) (min / desiredPrec);
        startx = (round + 1) * desiredPrec;

        if (invert)
          pos = (int) (length * (1.0 - (startx - min) / sz));
        else
          pos = (int) (length * ((startx - min) / sz));

        calendar.setTimeInMillis((long) startx);
        date = calendar.getTime();
        s = useFormat.format(date);
        bounds = labelFont.getStringBounds(s, frc);
        w = (int) bounds.getWidth();
        h = (int) bounds.getHeight();
        lastLabel = new LabelInfo(s, true, w, h, pos, 0);
        labels.add(lastLabel);

        double minPrec = (((double) w * 1.3) / length) * sz;

        // Correct to avoid label overlap
        prec = desiredPrec;
        while (prec < minPrec) prec += desiredPrec;

        startx += prec;

        // Build labels
        while (startx <= max) {

          if (invert)
            pos = (int) (length * (1.0 - (startx - min) / sz));
          else
            pos = (int) (length * ((startx - min) / sz));

          calendar.setTimeInMillis((long) startx);
          date = calendar.getTime();
          s = useFormat.format(date);
          bounds = labelFont.getStringBounds(s, frc);

          // Check limit
          if (pos > 0 && pos < lgth) {
            w = (int) bounds.getWidth();
            h = (int) bounds.getHeight();
            lastLabel = new LabelInfo(s, true, w, h, pos, 0);
            labels.add(lastLabel);
          }

          startx += prec;

        }
        break;

      case VALUE_ANNO:

        //Do not compute labels on vertical axis if no data displayed
        if ((dataViews.size() == 0) && !isHorizontal()) return;

        double fontAscent = (double) parent.getFontMetrics(labelFont).getAscent();
        int nbMaxLab = (int) (length / fontAscent);
        int n;
        int step = 0;


        if (nbMaxLab > tick) nbMaxLab = tick;

        // Find the best precision

        if (scale == LOG_SCALE) {

          prec = 1;   // Decade
          step = -1;  // Logarithm subgrid

          startx = Math.rint(min);

          n = (int) ((max - min) / prec);

          while (n > nbMaxLab) {
            prec = prec * 2;
            step = 2;
            n = (int) ((max - min) / prec);
            if (n > nbMaxLab) {
              prec = prec * 5;
              step = 10;
              n = (int) ((max - min) / prec);
            }
          }

        } else {

          prec = computeLowTen(max - min);
          step = 10;

          n = (int) ((max - min) / (prec / 2.0));

          while (n <= nbMaxLab) {
            prec = prec / 2.0;
            step = 5;
            n = (int) ((max - min) / (prec / 5.0));
            if (n <= nbMaxLab) {
              prec = prec / 5.0;
              step = 10;
              n = (int) ((max - min) / (prec / 2.0));
            }
          }

          // round to multiple of prec
          round = (int) (min / prec);
          startx = (round - 1) * prec;

        }

        //Build labels

        while (startx <= max) {

          if (invert)
            pos = (int) (length * (1.0 - (startx - min) / sz));
          else
            pos = (int) (length * ((startx - min) / sz));

          double vt;
          if (scale == LOG_SCALE)
            vt = Math.pow(10.0, startx);
          else
            vt = startx;

          s = formatValue(vt, prec);
          bounds = labelFont.getStringBounds(s, frc);

          // Check overlap
          boolean visible = true;
          if (lastLabel != null && isHorizontal()) {
            // Check bounds on horizontal axis
            visible = (lastLabel.pos + lastLabel.size.width / 2) <
              (pos - ((int) bounds.getWidth()) / 2);
          }

          if (startx >= (min - 1e-12)) {
            LabelInfo li = new LabelInfo(s, visible, (int) bounds.getWidth(), (int) fontAscent, pos, step);
            if (visible) lastLabel = li;
            labels.add(li);
          }

          startx += prec;

        }
        break;

    }

  }

  // ****************************************************************
  // Painting stuff

  /** Expert Usage.
   * Paint last point of a dataView.
   * @param g Graphics object
   * @param lp last point
   * @param p new point
   * @param v view containing the lp and p.
   */
  public void drawFast(Graphics g, Point lp, Point p, JLDataView v) {

    if (lp != null) {
      if (boundRect.contains(lp)) {

        Graphics2D g2 = (Graphics2D) g;
        Stroke old = g2.getStroke();
        BasicStroke bs = GraphicsUtils.createStrokeForLine(v.getLineWidth(), v.getStyle());
        if (bs != null) g2.setStroke(bs);

        // Draw
        g.setColor(v.getColor());
        g.drawLine(lp.x, lp.y, p.x, p.y);

        //restore default stroke
        g2.setStroke(old);
      }
    }

    //Paint marker
    Color oc = g.getColor();
    g.setColor(v.getMarkerColor());
    paintMarker(g, v.getMarker(), v.getMarkerSize(), p.x, p.y);
    g.setColor(oc);

  }

  /** Expert usage.
   * Paint a marker a the specified position
   * @param g Graphics object
   * @param mType Marker type
   * @param mSize Marker size
   * @param x x coordinates (pixel space)
   * @param y y coordinates (pixel space)
   */
  public static void paintMarker(Graphics g, int mType, int mSize, int x, int y) {

    int mSize2 = mSize / 2;
    int mSize21 = mSize / 2 + 1;

    switch (mType) {
      case JLDataView.MARKER_DOT:
        g.fillOval(x - mSize2, y - mSize2, mSize, mSize);
        break;
      case JLDataView.MARKER_BOX:
        g.fillRect(x - mSize2, y - mSize2, mSize, mSize);
        break;
      case JLDataView.MARKER_TRIANGLE:
        triangleShape.translate(x, y);
        g.fillPolygon(triangleShape);
        triangleShape.translate(-x, -y);
        break;
      case JLDataView.MARKER_DIAMOND:
        diamondShape.translate(x, y);
        g.fillPolygon(diamondShape);
        diamondShape.translate(-x, -y);
        break;
      case JLDataView.MARKER_STAR:
        g.drawLine(x - mSize2, y + mSize2, x + mSize21, y - mSize21);
        g.drawLine(x + mSize2, y + mSize2, x - mSize21, y - mSize21);
        g.drawLine(x, y - mSize2, x, y + mSize21);
        g.drawLine(x - mSize2, y, x + mSize21, y);
        break;
      case JLDataView.MARKER_VERT_LINE:
        g.drawLine(x, y - mSize2, x, y + mSize21);
        break;
      case JLDataView.MARKER_HORIZ_LINE:
        g.drawLine(x - mSize2, y, x + mSize21, y);
        break;
      case JLDataView.MARKER_CROSS:
        g.drawLine(x, y - mSize2, x, y + mSize21);
        g.drawLine(x - mSize2, y, x + mSize21, y);
        break;
      case JLDataView.MARKER_CIRCLE:
        g.drawOval(x - mSize2, y - mSize2, mSize + 1, mSize + 1);
        break;
      case JLDataView.MARKER_SQUARE:
        g.drawRect(x - mSize2, y - mSize2, mSize, mSize);
        break;
    }

  }

  private void paintBarBorder(Graphics g, int barWidth, int y0, int x, int y) {
    g.drawLine(x - barWidth / 2, y, x + barWidth / 2, y);
    g.drawLine(x + barWidth / 2, y, x + barWidth / 2, y0);
    g.drawLine(x + barWidth / 2, y0, x - barWidth / 2, y0);
    g.drawLine(x - barWidth / 2, y0, x - barWidth / 2, y);
  }

  private void paintBar(Graphics g, Paint pattern, int barWidth, Color background, Color foreground, int fillStyle, int y0, int x, int y) {

    Graphics2D g2 = (Graphics2D) g;

    if (fillStyle != JLDataView.FILL_STYLE_NONE) {
      if (pattern != null) g2.setPaint(pattern);
      if (y > y0) {
        g.fillRect(x - barWidth / 2, y0, barWidth, (y - y0));
      } else {
        g.fillRect(x - barWidth / 2, y, barWidth, (y0 - y));
      }
    }

  }

  /** Expert usage.
   * Draw a sample line of a dataview
   * @param g Graphics object
   * @param x x coordinates (pixel space)
   * @param y y coordinates (pixel space)
   * @param v dataview
   */
  public static void drawSampleLine(Graphics g, int x, int y, JLDataView v) {

    Graphics2D g2 = (Graphics2D) g;
    Stroke old = g2.getStroke();
    BasicStroke bs = GraphicsUtils.createStrokeForLine(v.getLineWidth(), v.getStyle());
    if (bs != null) g2.setStroke(bs);

    // Draw
    g.drawLine(x, y, x + 40, y);

    //restore default stroke
    g2.setStroke(old);

    //Paint marker
    Color oc = g.getColor();
    g.setColor(v.getMarkerColor());
    paintMarker(g, v.getMarker(), v.getMarkerSize(), x + 20, y);
    g.setColor(oc);

  }

  //   Expert usage
  //  Paint dataviews along the given axis
  //  xAxis horizonbtal axis of the graph
  //  xOrg x origin (pixel space)
  //  yOrg y origin (pixel space)
  void paintDataViews(Graphics g, JLAxis xAxis, int xOrg, int yOrg) {

    int nbView = dataViews.size();
    int k;
    boolean isXY = xAxis.isXY();
    JLDataView vx = null;

    //-------- Clipping

    int xClip = xOrg - 1;
    int yClip = yOrg - getLength() - 1;
    int wClip = xAxis.getLength() + 2;
    int hClip = getLength() + 2;
    if (wClip <= 1 || hClip <= 1) return;
    g.setClip(xClip, yClip, wClip, hClip);

    //-------- Draw dataView
    if (isXY) vx = (JLDataView) (xAxis.getViews().get(0));

    for (k = 0; k < nbView; k++) {

      JLDataView v = (JLDataView) dataViews.get(k);

      if (isXY)
        paintDataViewXY(g, v, vx, xAxis, xOrg, yOrg);
      else
        paintDataViewNormal(g, v, xAxis, xOrg, yOrg);

    } // End (for k<nbView)

    //Restore clip
    Dimension d = parent.getSize();
    g.setClip(0, 0, d.width, d.height);

  }

  // Paint dataviews along the given axis
  // Expert usage

  private int computeBarWidth(JLDataView v, JLAxis xAxis) {

    int defaultWidth = 20;
    double minx = xAxis.getMin();
    double maxx = xAxis.getMax();
    int bw = v.getBarWidth();
    double minI = Double.MAX_VALUE;


    // No auto scale
    if (bw > 0)
      return bw;

    // No autoScale when horizontal axis is logarithmic
    if (xAxis.getScale() == LOG_SCALE)
      return defaultWidth;


    if (xAxis.isXY()) {

      JLDataView vx = (JLDataView) (xAxis.getViews().get(0));

      // Look for the minimun interval
      DataList d = vx.getData();
      if (d != null) {
        double x = d.y;
        d = d.next;
        while (d != null) {
          double diff = Math.abs(d.y - x);
          if (diff < minI) minI = diff;
          x = d.y;
          d = d.next;
        }
      }

    } else {

      // Look for the minimun interval
      DataList d = v.getData();
      if (d != null) {
        double x = d.x;
        d = d.next;
        while (d != null) {
          double diff = Math.abs(d.x - x);
          if (diff < minI) minI = diff;
          x = d.x;
          d = d.next;
        }
      }

    }

    if (minI == Double.MAX_VALUE)
      return defaultWidth;

    bw = (int) Math.floor(minI / (maxx - minx) * xAxis.getLength()) - 2;

    // Make width multiple of 2 and saturate
    bw = bw / 2;
    bw = bw * 2;
    if (bw < 0) bw = 0;

    return bw;
  }

  private void paintDataViewBar(Graphics2D g2,
                                JLDataView v,
                                int barWidth,
                                BasicStroke bs,
                                Paint fPattern,
                                int y0,
                                int x,
                                int y) {

    if (v.getViewType() == JLDataView.TYPE_BAR) {

      paintBar((Graphics) g2,
        fPattern,
        barWidth,
        v.getFillColor(),
        v.getColor(),
        v.getFillStyle(),
        y0,
        x,
        y);

      // Draw bar border
      if (v.getLineWidth() > 0) {
        Stroke old = g2.getStroke();
        if (bs != null) g2.setStroke(bs);
        g2.setColor(v.getColor());
        paintBarBorder((Graphics) g2, barWidth, y0, x, y);
        g2.setStroke(old);
      }

    }

  }

  private void paintDataViewPolyline(Graphics2D g2,
                                     JLDataView v,
                                     BasicStroke bs,
                                     Paint fPattern,
                                     int nb,
                                     int yOrg,
                                     int[] pointX,
                                     int[] pointY) {

    if (nb > 1 && v.getViewType() == JLDataView.TYPE_LINE) {

      // Draw surface
      if (v.getFillStyle() != JLDataView.FILL_STYLE_NONE) {
        int[] Xs = new int[nb + 2];
        int[] Ys = new int[nb + 2];
        for (int i = 0; i < nb; i++) {
          Xs[i + 1] = pointX[i];
          Ys[i + 1] = pointY[i];
        }
        Xs[0] = Xs[1];
        Ys[0] = yOrg;
        Xs[nb + 1] = Xs[nb];
        Ys[nb + 1] = yOrg;
        if (fPattern != null) g2.setPaint(fPattern);
        g2.fillPolygon(Xs, Ys, nb + 2);
      }

      if (v.getLineWidth() > 0) {
        Stroke old = g2.getStroke();
        if (bs != null) g2.setStroke(bs);
        g2.setColor(v.getColor());
        g2.drawPolyline(pointX, pointY, nb);
        g2.setStroke(old);
      }
    }

  }

  private void paintDataViewNormal(Graphics g, JLDataView v, JLAxis xAxis, int xOrg, int yOrg) {

    DataList l = v.getData();

    if (l != null) {

      // Get some variables

      int nbPoint = v.getDataLength() + 1;
      int pointX[] = new int[nbPoint];
      int pointY[] = new int[nbPoint];

      double minx,maxx,lx;
      double miny,maxy,ly;
      double xratio;
      double yratio;
      double vt;
      double A0 = v.getA0();
      double A1 = v.getA1();
      double A2 = v.getA2();
      int y0;

      minx = xAxis.getMin();
      maxx = xAxis.getMax();
      lx = xAxis.getLength();
      int sx = xAxis.getScale();

      miny = min;
      maxy = max;
      ly = getLength();

      int j = 0;
      boolean valid = true;

      // Set the stroke mode for dashed line

      Graphics2D g2 = (Graphics2D) g;
      BasicStroke bs = GraphicsUtils.createStrokeForLine(v.getLineWidth(), v.getStyle());

      // Create the filling pattern
      Paint fPattern = GraphicsUtils.createPatternForFilling(v.getFillStyle(), v.getFillColor(), v.getColor());

      // Compute zero vertical offset
      switch (v.getFillMethod()) {
        case JLDataView.METHOD_FILL_FROM_TOP:
          y0 = yOrg - (int) ly;
          break;
        case JLDataView.METHOD_FILL_FROM_ZERO:
          if (scale == LOG_SCALE)
            y0 = yOrg;
          else
            y0 = (int) (miny / (maxy - miny) * ly) + yOrg;
          break;
        default:
          y0 = yOrg;
          break;
      }

      int barWidth = computeBarWidth(v, xAxis);

      while (l != null) {

        while (valid && l != null) {

          // Compute transform here for performance
          vt = A0 + A1 * l.y + A2 * l.y * l.y;
          valid = !Double.isNaN(vt) && (sx != LOG_SCALE || l.x > 1e-100)
            && (scale != LOG_SCALE || vt > 1e-100);

          if (valid) {

            if (sx == LOG_SCALE)
              xratio = (Math.log(l.x) / ln10 - minx) / (maxx - minx) * lx;
            else
              xratio = (l.x - minx) / (maxx - minx) * lx;

            if (scale == LOG_SCALE)
              yratio = -(Math.log(vt) / ln10 - miny) / (maxy - miny) * ly;
            else
              yratio = -(vt - miny) / (maxy - miny) * ly;

            // Saturate
            if (xratio < -32000) xratio = -32000;
            if (xratio > 32000) xratio = 32000;
            if (yratio < -32000) yratio = -32000;
            if (yratio > 32000) yratio = 32000;
            pointX[j] = (int) (xratio) + xOrg;
            pointY[j] = (int) (yratio) + yOrg;

            // Draw marker
            if (v.getMarker() > JLDataView.MARKER_NONE) {
              g.setColor(v.getMarkerColor());
              paintMarker(g, v.getMarker(), v.getMarkerSize(), pointX[j], pointY[j]);
            }

            // Draw bar
            paintDataViewBar(g2, v, barWidth, bs, fPattern, y0, pointX[j], pointY[j]);

            l = l.next;
            j++;
          }

        }

        // Draw the polyline
        paintDataViewPolyline(g2, v, bs, fPattern, j, y0, pointX, pointY);

        j = 0;
        if (!valid) {
          l = l.next;
          valid = true;
        }

      } // End (while l!=null)

    } // End (if l!=null)

  }

  // Paint dataviews along the given axis in XY mode
  // Expert usage
  private void paintDataViewXY(Graphics g, JLDataView v, JLDataView w, JLAxis xAxis, int xOrg, int yOrg) {

    XYData l = new XYData(v.getData(), w.getData());
    int lw = v.getLineWidth();

    if (l.isValid()) {

      int nbPoint = v.getDataLength() + w.getDataLength() + 2; // Max number of point

      int pointX[] = new int[nbPoint];
      int pointY[] = new int[nbPoint];

      // Transform points

      double minx,maxx,lx;
      double miny,maxy,ly;
      double xratio;
      double yratio;
      double vtx;
      double vty;
      double A0y = v.getA0();
      double A1y = v.getA1();
      double A2y = v.getA2();
      double A0x = w.getA0();
      double A1x = w.getA1();
      double A2x = w.getA2();

      minx = xAxis.getMin();
      maxx = xAxis.getMax();
      lx = xAxis.getLength();
      int sx = xAxis.getScale();

      miny = min;
      maxy = max;
      ly = getLength();
      int y0;

      int j = 0;
      boolean valid = true;

      // Compute zero vertical offset
      switch (v.getFillMethod()) {
        case JLDataView.METHOD_FILL_FROM_TOP:
          y0 = yOrg - (int) ly;
          break;
        case JLDataView.METHOD_FILL_FROM_ZERO:
          if (scale == LOG_SCALE)
            y0 = yOrg;
          else
            y0 = (int) (miny / (maxy - miny) * ly) + yOrg;
          break;
        default:
          y0 = yOrg;
          break;
      }

      // Set the stroke mode for dashed line
      Graphics2D g2 = (Graphics2D) g;
      Stroke old = g2.getStroke();
      BasicStroke bs = GraphicsUtils.createStrokeForLine(v.getLineWidth(), v.getStyle());

      // Create the filling pattern
      Paint fPattern = GraphicsUtils.createPatternForFilling(v.getFillStyle(), v.getFillColor(), v.getColor());

      int barWidth = computeBarWidth(v, xAxis);

      while (l.isValid()) {

        // Go to starting time position
        l.initFirstPoint();

        while (valid && l.isValid()) {

          // Compute transform here for performance
          vty = A0y + A1y * l.d1.y + A2y * l.d1.y * l.d1.y;
          vtx = A0x + A1x * l.d2.y + A2x * l.d2.y * l.d2.y;

          valid = !Double.isNaN(vtx) && !Double.isNaN(vty) &&
            (sx != LOG_SCALE || vtx > 1e-100) &&
            (scale != LOG_SCALE || vty > 1e-100);

          if (valid) {

            if (sx == LOG_SCALE)
              xratio = (Math.log(vtx) / ln10 - minx) / (maxx - minx) * lx;
            else
              xratio = (vtx - minx) / (maxx - minx) * lx;

            if (scale == LOG_SCALE)
              yratio = -(Math.log(vty) / ln10 - miny) / (maxy - miny) * ly;
            else
              yratio = -(vty - miny) / (maxy - miny) * ly;

            // Saturate
            if (xratio < -32000) xratio = -32000;
            if (xratio > 32000) xratio = 32000;
            if (yratio < -32000) yratio = -32000;
            if (yratio > 32000) yratio = 32000;
            pointX[j] = (int) (xratio) + xOrg;
            pointY[j] = (int) (yratio) + yOrg;

            // Draw marker
            if (v.getMarker() > JLDataView.MARKER_NONE) {
              g.setColor(v.getMarkerColor());
              paintMarker(g, v.getMarker(), v.getMarkerSize(), pointX[j], pointY[j]);
            }

            // Draw bar
            paintDataViewBar(g2, v, barWidth, bs, fPattern, y0, pointX[j], pointY[j]);

            // Go to next pos
            l.toNextXYPoint();
            j++;
          }

        }

        // Draw the polyline
        paintDataViewPolyline(g2, v, bs, fPattern, j, y0, pointX, pointY);

        j = 0;
        if (!valid) {
          // Go to next pos
          l.toNextXYPoint();
          valid = true;
        }

      } // End (while l!=null)

    } // End (if l!=null)

  }

  // Paint sub tick outside label limit
  // Expert usage
  private int paintExtraYSubTicks(Graphics g, int x0, int ys, int length, int y0, int la, BasicStroke bs, int step, int tr) {

    int j,h;
    Graphics2D g2 = (Graphics2D) g;
    Stroke old = g2.getStroke();

    if (subtickVisible) {

      if (step == -1) {

        for (j = 0; j < logStep.length; j++) {
          h = ys + (int) (length * logStep[j]);
          if (h > y0 && h < (y0 + csize.height)) {
            g.drawLine(x0 + tr - 1, h, x0 + tr + 2, h);
            if (gridVisible && subGridVisible) {
              if (bs != null) g2.setStroke(bs);
              g.drawLine(x0, h, x0 + la, h);
              g2.setStroke(old);
            }
          }
        }

      } else if (step > 0) {

        for (j = 0; j < linStep.length; j += (10 / step)) {
          h = ys + (int) (length * linStep[j]);
          if (h > y0 && h < (y0 + csize.height)) {
            g.drawLine(x0 + tr - 1, h, x0 + tr + 2, h);
            if ((j > 0) && gridVisible && subGridVisible) {
              if (bs != null) g2.setStroke(bs);
              g.drawLine(x0, h, x0 + la, h);
              g2.setStroke(old);
            }
          }
        }

      }

      return length;

    } else {

      return 0;

    }

  }

  // Paint sub tick outside label limit
  // Expert usage
  private int paintExtraXSubTicks(Graphics g, int y0, int xs, int length, int x0, int la, BasicStroke bs, int step, int tr) {

    int j,w;
    Graphics2D g2 = (Graphics2D) g;
    Stroke old = g2.getStroke();

    if (subtickVisible) {

      if (step == -1) {

        for (j = 0; j < logStep.length; j++) {
          w = xs + (int) (length * logStep[j]);
          if (w > x0 && w < (x0 + csize.width)) {
            g.drawLine(w, y0 + tr - 1, w, y0 + tr + 2);
            if (gridVisible && subGridVisible) {
              if (bs != null) g2.setStroke(bs);
              g.drawLine(w, y0, w, y0 + la);
              g2.setStroke(old);
            }
          }
        }

      } else if (step > 0) {

        for (j = 0; j < linStep.length; j += (10 / step)) {
          w = xs + (int) (length * linStep[j]);
          if (w > x0 && w < (x0 + csize.width)) {
            g.drawLine(w, y0 + tr - 1, w, y0 + tr + 2);
            if ((j > 0) && gridVisible && subGridVisible) {
              if (bs != null) g2.setStroke(bs);
              g.drawLine(w, y0, w, y0 + la);
              g2.setStroke(old);
            }
          }
        }

      }

      return length;

    } else {

      return 0;

    }

  }

  // Paint Y sub tick and return tick spacing
  // Expert usage
  private int paintYSubTicks(Graphics g, int i, int x0, int y, int length, int la, BasicStroke bs, int tr) {

    int j,h;
    Graphics2D g2 = (Graphics2D) g;
    Stroke old = g2.getStroke();

    if (subtickVisible && i < (labels.size() - 1)) {

      LabelInfo li = (LabelInfo) labels.get(i + 1);
      length += li.pos;
      int step = li.subtick_step;

      if (step == -1) {  // Logarithmic step

        for (j = 0; j < logStep.length; j++) {
          h = y + (int) (length * logStep[j]);
          g.drawLine(x0 + tr - 1, h, x0 + tr + 2, h);
          if (gridVisible && subGridVisible) {
            if (bs != null) g2.setStroke(bs);
            g.drawLine(x0, h, x0 + la, h);
            g2.setStroke(old);
          }
        }

      } else if (step > 0) {  // Linear step

        for (j = 0; j < linStep.length; j += (10 / step)) {
          h = y + (int) (length * linStep[j]);
          g.drawLine(x0 + tr - 1, h, x0 + tr + 2, h);
          if ((j > 0) && gridVisible && subGridVisible) {
            if (bs != null) g2.setStroke(bs);
            g.drawLine(x0, h, x0 + la, h);
            g2.setStroke(old);
          }
        }

      }

      return length;

    } else {

      return 0;

    }

  }

  // Paint X sub tick and return tick spacing
  // Expert usage
  private int paintXSubTicks(Graphics g, int i, int y0, int x, int length, int la, BasicStroke bs, int tr) {

    int j,w;
    Graphics2D g2 = (Graphics2D) g;
    Stroke old = g2.getStroke();

    if (subtickVisible && i < (labels.size() - 1)) {

      LabelInfo li = (LabelInfo) labels.get(i + 1);
      length += li.pos;
      int step = li.subtick_step;

      if (step == -1) {  // Logarithmic step

        for (j = 0; j < logStep.length; j++) {
          w = x + (int) (length * logStep[j]);
          g.drawLine(w, y0 + tr - 1, w, y0 + tr + 2);
          if (gridVisible && subGridVisible) {
            if (bs != null) g2.setStroke(bs);
            g.drawLine(w, y0, w, y0 + la);
            g2.setStroke(old);
          }
        }


      } else if (step > 0) {  // Linear step

        for (j = 0; j < linStep.length; j += (10 / step)) {
          w = x + (int) (length * linStep[j]);
          g.drawLine(w, y0 + tr - 1, w, y0 + tr + 2);
          if ((j > 0) && gridVisible && subGridVisible) {
            if (bs != null) g2.setStroke(bs);
            g.drawLine(w, y0, w, y0 + la);
            g2.setStroke(old);
          }
        }

      }

      return length;

    } else {

      return 0;

    }

  }

  /**
   * Compute the medium color of c1,c2
   * @param c1 Color 1
   * @param c2 Color 2
   * @return Averaged color.
   */
  public Color computeMediumColor(Color c1, Color c2) {
    return new Color((c1.getRed() + 3 * c2.getRed()) / 4,
      (c1.getGreen() + 3 * c2.getGreen()) / 4,
      (c1.getBlue() + 3 * c2.getBlue()) / 4);
  }

  /** Expert usage.
   * Paint the axis and its DataView at the specified position along the given axis.
   * @param g Graphics object
   * @param frc Font render context
   * @param x0 Axis x position (pixel space)
   * @param y0 Axis y position (pixel space)
   * @param xAxis Horizontal axis of the graph
   * @param xOrg X origin for transformation (pixel space)
   * @param yOrg Y origin for transformation (pixel space)
   * @param back Background color
   * @param dOrientation Default orientation
   */
  public void paintAxis(Graphics g, FontRenderContext frc, int x0, int y0, JLAxis xAxis, int xOrg, int yOrg, Color back) {

    //Do not draw vertical axis without data
    if (!isHorizontal() && dataViews.size() == 0) return;

    //Do not paint when too small
    if (getLength() <= 1) return;

    int i,j,x,y,la = 0;
    BasicStroke bs = null;
    Graphics2D g2 = (Graphics2D) g;
    int tickStep = 0;
    int tr = 0;
    Point p0 = null;

    Color subgridColor = computeMediumColor(labelColor, back);

    g.setFont(labelFont);

    // stroke for the grid

    if (gridVisible) bs = GraphicsUtils.createStrokeForLine(1, gridStyle);
    la = xAxis.getLength() - 2;

    // Update bounding rectangle

    switch (dOrientation) {

      case VERTICAL_LEFT:
        boundRect.setRect(x0 + csize.width, y0, la, csize.height);

        if (orientation == VERTICAL_ORG) {
          p0 = transform(0.0, 1.0, xAxis);
          if ((p0.x >= (x0 + csize.width)) && (p0.x <= (x0 + csize.width + la)))
            tr = p0.x - (x0 + csize.width);
          else
          // Do not display axe ot of bounds !!
            return;
        }
        break;

      case VERTICAL_RIGHT:
        boundRect.setRect(x0 - la - 1, y0, la, csize.height);

        if (orientation == VERTICAL_ORG) {
          p0 = transform(0.0, 1.0, xAxis);
          if ((p0.x >= (x0 - la - 1)) && (p0.x <= x0))
            tr = p0.x - x0;
          else
          // Do not display axe ot of bounds !!
            return;
        }
        break;

      case HORIZONTAL_DOWN:
      case HORIZONTAL_UP:
        boundRect.setRect(x0, y0 - la, csize.width, la);

        if (orientation == HORIZONTAL_ORG1 || orientation == HORIZONTAL_ORG2) {

          p0 = xAxis.transform(1.0, 0.0, this);
          if ((p0.y >= (y0 - la)) && (p0.y <= y0))
            tr = p0.y - y0;
          else
          // Do not display axe ot of bounds !!
            return;

        }

        break;

      default:
        System.out.println("JLChart warning: Wrong axis position");
        break;

    }


    switch (dOrientation) {

      case VERTICAL_LEFT:

        for (i = 0; i < labels.size(); i++) {

          // Draw labels
          g.setColor(labelColor);
          LabelInfo li = (LabelInfo) labels.get(i);

          x = x0 + (csize.width - 4) - li.size.width;
          y = li.pos + y0;
          g.drawString(li.value, x + tr, y + li.size.height / 3);

          //Draw tick
          g.drawLine(x0 + tr + (csize.width - 2), y, x0 + tr + (csize.width + 3), y);

          //Draw the grid
          if (gridVisible) {
            Stroke old = g2.getStroke();
            if (bs != null) g2.setStroke(bs);
            g.drawLine(x0 + (csize.width + 2), y, x0 + (csize.width + 2) + la, y);
            g2.setStroke(old);
          }

          //Draw sub tick
          g.setColor(subgridColor);
          int ts = paintYSubTicks(g, i, x0 + csize.width, y, -li.pos, la, bs, tr);
          if (ts != 0 && tickStep == 0) tickStep = ts;

        }

        //Draw extra sub ticks (outside labels limit)
        if (tickStep != 0) {
          LabelInfo lis = (LabelInfo) labels.get(0);
          LabelInfo lie = (LabelInfo) labels.get(labels.size() - 1);

          paintExtraYSubTicks(g, x0 + csize.width, y0 + lis.pos - tickStep, tickStep, y0, la, bs, lis.subtick_step, tr);
          paintExtraYSubTicks(g, x0 + csize.width, y0 + lie.pos, tickStep, y0, la, bs, lis.subtick_step, tr);
        }

        // Draw Axe
        g.setColor(labelColor);
        g.drawLine(x0 + tr + csize.width, y0, x0 + tr + csize.width, y0 + csize.height);

        if (name != null) {
          Rectangle2D bounds = labelFont.getStringBounds(name, frc);
          g.drawString(name, (x0 + tr + csize.width) - (int) bounds.getWidth() / 2, y0 - (int) (bounds.getHeight() / 2) - 2);
        }
        break;

      case VERTICAL_RIGHT:

        for (i = 0; i < labels.size(); i++) {

          // Draw labels
          g.setColor(labelColor);
          LabelInfo li = (LabelInfo) labels.get(i);

          y = li.pos + y0;
          g.drawString(li.value, x0 + tr + 6, y + li.size.height / 3);

          //Draw tick
          g.drawLine(x0 + tr - 2, y, x0 + tr + 2, y);

          //Draw the grid
          if (gridVisible) {
            Stroke old = g2.getStroke();
            if (bs != null) g2.setStroke(bs);
            g.drawLine(x0 - 2, y, x0 - 2 - la, y);
            g2.setStroke(old);
          }

          //Draw sub tick
          g.setColor(subgridColor);
          int ts = paintYSubTicks(g, i, x0, y, -li.pos, -la, bs, tr);
          if (ts != 0 && tickStep == 0) tickStep = ts;

        }

        //Draw extra sub ticks (outside labels limit)
        if (tickStep != 0) {
          LabelInfo lis = (LabelInfo) labels.get(0);
          LabelInfo lie = (LabelInfo) labels.get(labels.size() - 1);

          paintExtraYSubTicks(g, x0, y0 + lis.pos - tickStep, tickStep, y0, -la, bs, lis.subtick_step, tr);
          paintExtraYSubTicks(g, x0, y0 + lie.pos, tickStep, y0, -la, bs, lis.subtick_step, tr);
        }

        // Draw Axe
        g.setColor(labelColor);
        g.drawLine(x0 + tr, y0, x0 + tr, y0 + csize.height);

        if (name != null) {
          Rectangle2D bounds = labelFont.getStringBounds(name, frc);
          g.drawString(name, x0 + tr - (int) bounds.getWidth() / 2, y0 - (int) (bounds.getHeight() / 2) - 2);
        }

        break;

      case HORIZONTAL_UP:
      case HORIZONTAL_DOWN:
      case HORIZONTAL_ORG1:
      case HORIZONTAL_ORG2:

        for (i = 0; i < labels.size(); i++) {

          // Draw labels
          g.setColor(labelColor);
          LabelInfo li = (LabelInfo) labels.get(i);

          x = li.pos + x0;
          y = y0;
          if (li.isVisible)
            g.drawString(li.value, x - li.size.width / 2, y + tr + li.size.height + 2);

          //Draw tick
          g.drawLine(x, y0 + tr - 2, x, y0 + tr + 3);

          //Draw the grid
          if (gridVisible) {
            Stroke old = g2.getStroke();
            if (bs != null) g2.setStroke(bs);
            g.drawLine(x, y0 - 2, x, y0 - 2 - la);
            g2.setStroke(old);
          }

          //Draw sub tick
          g.setColor(subgridColor);
          int ts = paintXSubTicks(g, i, y, x, -li.pos, -la, bs, tr);
          if (ts != 0 && tickStep == 0) tickStep = ts;

        }

        //Draw extra sub ticks (outside labels limit)
        if (tickStep != 0) {
          LabelInfo lis = (LabelInfo) labels.get(0);
          LabelInfo lie = (LabelInfo) labels.get(labels.size() - 1);

          paintExtraXSubTicks(g, y0, x0 + lis.pos - tickStep, tickStep, x0, -la, bs, lis.subtick_step, tr);
          paintExtraXSubTicks(g, y0, x0 + lie.pos, tickStep, x0, -la, bs, lis.subtick_step, tr);
        }

        // Draw Axe
        g.setColor(labelColor);
        g.drawLine(x0, y0 + tr, x0 + csize.width, y0 + tr);

        if (name != null) {
          Rectangle2D bounds = labelFont.getStringBounds(name, frc);
          g.drawString(name, x0 + ((csize.width) - (int) bounds.getWidth()) / 2,
            y0 + 2 * (int) bounds.getHeight());
        }

        break;

    }

  }


  /**
   * Apply axis configuration.
   * @param prefix Axis settings prefix
   * @param f CfFileReader object wich contains axis parametters
   * @see JLChart#applyConfiguration
   * @see JLDataView#applyConfiguration
   */
  public void applyConfiguration(String prefix, CfFileReader f) {

    Vector p;
    p = f.getParam(prefix + "grid");
    if (p != null) setGridVisible(OFormat.getBoolean(p.get(0).toString()));
    p = f.getParam(prefix + "subgrid");
    if (p != null) setSubGridVisible(OFormat.getBoolean(p.get(0).toString()));
    p = f.getParam(prefix + "grid_style");
    if (p != null) setGridStyle(OFormat.getInt(p.get(0).toString()));
    p = f.getParam(prefix + "min");
    if (p != null) setMinimum(OFormat.getDouble(p.get(0).toString()));
    p = f.getParam(prefix + "max");
    if (p != null) setMaximum(OFormat.getDouble(p.get(0).toString()));
    p = f.getParam(prefix + "autoscale");
    if (p != null) setAutoScale(OFormat.getBoolean(p.get(0).toString()));
    p = f.getParam(prefix + "scale");
    if (p != null) {
      setScale(OFormat.getInt(p.get(0).toString()));
    } else {
      // To handle a bug in older version
      p = f.getParam(prefix + "cale");
      if (p != null) setScale(OFormat.getInt(p.get(0).toString()));
    }
    p = f.getParam(prefix + "format");
    if (p != null) setLabelFormat(OFormat.getInt(p.get(0).toString()));
    p = f.getParam(prefix + "title");
    if (p != null) setName(OFormat.getName(p.get(0).toString()));
    p = f.getParam(prefix + "color");
    if (p != null) setAxisColor(OFormat.getColor(p));
    p = f.getParam(prefix + "label_font");
    if (p != null) setFont(OFormat.getFont(p));

  }

  /**
   * Builds a configuration string that can be write into a file and is compatible
   * with CfFileReader.
   * @param prefix Axis settings prefix
   * @return A string containing param
   * @see JLAxis#applyConfiguration
   * @see JLChart#getConfiguration
   * @see JLDataView#getConfiguration
   */
  public String getConfiguration(String prefix) {

    String to_write="";

    to_write += prefix + "grid:" + isGridVisible() + "\n";
    to_write += prefix + "subgrid:" + isSubGridVisible() + "\n";
    to_write += prefix + "grid_style:" + getGridStyle() + "\n";
    to_write += prefix + "min:" + getMinimum() + "\n";
    to_write += prefix + "max:" + getMaximum() + "\n";
    to_write += prefix + "autoscale:" + isAutoScale() + "\n";
    to_write += prefix + "scale:" + getScale() + "\n";
    to_write += prefix + "format:" + getLabelFormat() + "\n";
    to_write += prefix + "title:\'" + getName() + "\'\n";
    to_write += prefix + "color:" + OFormat.color(getAxisColor()) + "\n";
    to_write += prefix + "label_font:" + OFormat.font(getFont()) + "\n";

    return to_write;
  }

}