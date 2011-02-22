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
  public DataList d2;

  XYData(DataList d1, DataList d2) {
    this.d1 = d1;
    this.d2 = d2;
  }

  // Convert time to second
  private long toSecond(double t) {
    return (long) Math.floor(t / 1000.0 + 0.5);
  }

  // Find the next point for XY mode
  void toNextXYPoint() {

    if (d1.next != null && d2.next != null) {
      long lnx = toSecond(d1.next.x);
      long mnx = toSecond(d2.next.x);
      if (lnx <= mnx) d1 = d1.next;
      if (mnx <= lnx) d2 = d2.next;
    } else {
      d1 = d1.next;
      d2 = d2.next;
    }

  }

  // Go to starting time position
  void initFirstPoint() {
    while (d1 != null && toSecond(d1.x) < toSecond(d2.x)) {
      d1 = d1.next;
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
  /** Horizontal axis */
  public static final int HORIZONTAL = 1;
  /** Vertical right axis */
  public static final int VERTICAL_RIGHT = 2;
  /** Vertical left axis */
  public static final int VERTICAL_LEFT = 3;

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
  static final java.text.SimpleDateFormat genFormat = new java.text.SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
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

  static float dashDotPattern[] = {5.0f, 3.0f, 2.0f, 3.0f};
  static float dotPattern[] = {2.0f, 4.0f};
  static float dashPattern[] = {5.0f};
  static float longDashPattern[] = {10.0f};

  static double linStep[] = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
  static double logStep[] = {0.301, 0.477, 0.602, 0.699, 0.778, 0.845, 0.903, 0.954};

  /** Axis constructor (Do not use).
   * @param orientation Axis placement: JLAxis.HORIZONTAL,JLAxis.VERTICAL_RIGHT or JLAxis.VERTICAL_LEFT
   * @param parent parent chart
   */
  public JLAxis(JComponent parent, int orientation) {
    labels = new Vector();
    labelFont = new Font("Dialog", Font.BOLD, 11);
    labelColor = Color.black;
    name = null;
    this.orientation = orientation;
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

    if (orientation == HORIZONTAL) {

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
   * a dataView to X axis. Only one view is allowed on HORIZONTAL Axis
   * and you must ensure that all views in all axis have the same number
   * of point, else some views can be truncated and extra point can be added.
   * @param v The dataview to display along this axis.
   * @see JLAxis#removeDataView
   * @see JLAxis#clearDataView
   * @see JLAxis#getViews
   */
  public void addDataView(JLDataView v) {

    if (dataViews.contains(v))
      return;

    if (orientation != HORIZONTAL) {
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
    if (orientation == HORIZONTAL) {
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
  public String formatTimeValue(double vt) {
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

    if (orientation == HORIZONTAL && autoScale && sz > 0) {

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
    if (orientation == HORIZONTAL) {
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

    if (csize != null)
      switch (orientation) {

        case VERTICAL_RIGHT:
        case VERTICAL_LEFT:
          return csize.width;
        case HORIZONTAL:
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

    if (csize != null)
      switch (orientation) {

        case VERTICAL_RIGHT:
        case VERTICAL_LEFT:
          return csize.height;
        case HORIZONTAL:
          return csize.width;

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

    switch (orientation) {

      case VERTICAL_RIGHT:
      case VERTICAL_LEFT:
        computeLabels(frc, desiredHeight, true);
        break;
      case HORIZONTAL:
        computeLabels(frc, desiredWidth, false);
        break;

    }

    for (i = 0; i < labels.size(); i++) {
      LabelInfo li = (LabelInfo) labels.get(i);
      if (li.size.width > max_width)
        max_width = li.size.width;
      if (li.size.height > max_height)
        max_height = li.size.height;
    }

    switch (orientation) {

      case VERTICAL_RIGHT:
      case VERTICAL_LEFT:
        csize = new Dimension(max_width + 5, desiredHeight);
        break;
      case HORIZONTAL:
        csize = new Dimension(desiredWidth, max_height);
        break;

    }

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

    for (int i = 0; i < sz; i++) {

      JLDataView v = (JLDataView) dataViews.get(i);
      DataList e = v.getData();

      while (e != null) {

        Point p = transform(e.x, v.getTransformedValue(e.y), xAxis);

        if (boundRect.contains(p)) {
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

    for (int i = 0; i < sz; i++) {

      JLDataView v = (JLDataView) dataViews.get(i);
      XYData e = new XYData(v.getData(), w.getData());

      if (e.isValid()) e.initFirstPoint();

      while (e.isValid()) {

        Point p = transform(w.getTransformedValue(e.d2.y), v.getTransformedValue(e.d1.y), xAxis);

        if (boundRect.contains(p)) {
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
        // This has nothing to fo with TIME_FORMAT

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
        if (dataViews.size() == 0 && orientation != HORIZONTAL) return;

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
          if (lastLabel != null && orientation == HORIZONTAL) {
            // Ckech bounds
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
        BasicStroke bs = createStroke(v.getLineWidth(), v.getStyle());
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

  /** Expert usage.
   * Create a Basic stroke for the dashMode
   * @param lw Line width
   * @param style Line style
   * @return null when no stroke is needed
   */
  public static BasicStroke createStroke(int lw, int style) {

    BasicStroke bs = null;

    if (lw != 1 || style != JLDataView.STYLE_SOLID) {
      switch (style) {
        case JLDataView.STYLE_DOT:
          bs = new BasicStroke(lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dotPattern, 0.0f);
          break;
        case JLDataView.STYLE_DASH:
          bs = new BasicStroke(lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f);
          break;
        case JLDataView.STYLE_LONG_DASH:
          bs = new BasicStroke(lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, longDashPattern, 0.0f);
          break;
        case JLDataView.STYLE_DASH_DOT:
          bs = new BasicStroke(lw, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashDotPattern, 0.0f);
          break;
        default:
          bs = new BasicStroke(lw);
          break;
      }
    }

    return bs;
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
    BasicStroke bs = createStroke(v.getLineWidth(), v.getStyle());
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
    if (xAxis.isXY())
      paintDataViewsXY(g, xAxis, xOrg, yOrg);
    else
      paintDataViewsNormal(g, xAxis, xOrg, yOrg);
  }

  // Paint dataviews along the given axis
  // Expert usage
  private void paintDataViewsNormal(Graphics g, JLAxis xAxis, int xOrg, int yOrg) {

    int nbView = dataViews.size();

    int k,j;

    //Clip
    g.setClip(xOrg, yOrg - getLength(), xAxis.getLength(), getLength());

    //Draw dataView
    for (k = 0; k < nbView; k++) {

      JLDataView v = (JLDataView) dataViews.get(k);
      DataList l = v.getData();
      int lw = v.getLineWidth();

      if (l != null) {

        int nbPoint = v.getDataLength();
        int mType = v.getMarker();
        int mSize = v.getMarkerSize();
        Color mColor = v.getMarkerColor();

        int pointX[] = new int[nbPoint];
        int pointY[] = new int[nbPoint];

        // Transform points

        double minx,maxx,lx;
        double miny,maxy,ly;
        double xratio;
        double yratio;
        double vt;
        double A0 = v.getA0();
        double A1 = v.getA1();
        double A2 = v.getA2();

        minx = xAxis.getMin();
        maxx = xAxis.getMax();
        lx = xAxis.getLength();
        int sx = xAxis.getScale();

        miny = min;
        maxy = max;
        ly = getLength();

        j = 0;
        boolean valid = true;

        // Set the stroke mode for dashed line
        Graphics2D g2 = (Graphics2D) g;
        Stroke old = g2.getStroke();
        BasicStroke bs = createStroke(v.getLineWidth(), v.getStyle());


        while (l != null) {

          g.setColor(mColor);


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
              if (mType > JLDataView.MARKER_NONE)
                paintMarker(g, mType, mSize, pointX[j], pointY[j]);
              l = l.next;
              j++;
            }

          }

          if (bs != null) g2.setStroke(bs);

          // Draw the polyline
          g.setColor(v.getColor());

          if (j > 1 && lw > 0)
            g.drawPolyline(pointX, pointY, j);

          //restore default stroke
          g2.setStroke(old);

          j = 0;
          if (!valid) {
            l = l.next;
            valid = true;
          }

        } // End (while l!=null)

      } // End (if l!=null)

    } // End (for k<nbView)

    //Restore clip
    Dimension d = parent.getSize();
    g.setClip(0, 0, d.width, d.height);

  }

  // Paint dataviews along the given axis in XY mode
  // Expert usage
  private void paintDataViewsXY(Graphics g, JLAxis xAxis, int xOrg, int yOrg) {

    int nbView = dataViews.size();

    int k,j;

    //Clip
    g.setClip(xOrg, yOrg - getLength(), xAxis.getLength(), getLength());

    //Draw dataView
    for (k = 0; k < nbView; k++) {

      JLDataView v = (JLDataView) dataViews.get(k);
      JLDataView w = (JLDataView) xAxis.getViews().get(0);
      XYData l = new XYData(v.getData(), w.getData());
      int lw = v.getLineWidth();

      if (l.isValid()) {

        int nbPoint = v.getDataLength() + w.getDataLength(); // Max number of point
        int mType = v.getMarker();
        int mSize = v.getMarkerSize();
        Color mColor = v.getMarkerColor();

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

        j = 0;
        boolean valid = true;

        // Set the stroke mode for dashed line
        Graphics2D g2 = (Graphics2D) g;
        Stroke old = g2.getStroke();
        BasicStroke bs = createStroke(v.getLineWidth(), v.getStyle());

        while (l.isValid()) {

          g.setColor(mColor);

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
              if (mType > JLDataView.MARKER_NONE)
                paintMarker(g, mType, mSize, pointX[j], pointY[j]);

              // Go to next pos
              l.toNextXYPoint();
              j++;
            }

          }


          if (bs != null) g2.setStroke(bs);

          // Draw the polyline
          g.setColor(v.getColor());

          if (j > 1 && lw > 0) {
            g.drawPolyline(pointX, pointY, j);

            // Draw surface
            if( v.isFill() ) {
              int[] Xs = new int[j + 2];
              int[] Ys = new int[j + 2];
              for (int i = 0; i < j; i++) {
                Xs[i + 1] = pointX[i];
                Ys[i + 1] = pointY[i];
              }
              Xs[0] = Xs[1];
              Ys[0] = yOrg;
              Xs[j + 1] = Xs[j];
              Ys[j + 1] = yOrg;
              g.fillPolygon(Xs, Ys, j + 2);
            }
          }

          //restore default stroke
          g2.setStroke(old);

          j = 0;
          if (!valid) {
            // Go to next pos
            l.toNextXYPoint();
            valid = true;
          }

        } // End (while l!=null)

      } // End (if l!=null)

    } // End (for k<nbView)

    //Restore clip
    Dimension d = parent.getSize();
    g.setClip(0, 0, d.width, d.height);

  }

  // Paint sub tick outside label limit
  // Expert usage
  private int paintExtraYSubTicks(Graphics g, int x0, int ys, int length, int y0, int la, BasicStroke bs, int step) {

    int j,h;
    Graphics2D g2 = (Graphics2D) g;
    Stroke old = g2.getStroke();

    if (subtickVisible) {

      if (step == -1) {

        for (j = 0; j < logStep.length; j++) {
          h = ys + (int) (length * logStep[j]);
          if (h > y0 && h < (y0 + csize.height)) {
            g.drawLine(x0 - 1, h, x0 + 2, h);
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
            g.drawLine(x0 - 1, h, x0 + 2, h);
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
  private int paintExtraXSubTicks(Graphics g, int y0, int xs, int length, int x0, int la, BasicStroke bs, int step) {

    int j,w;
    Graphics2D g2 = (Graphics2D) g;
    Stroke old = g2.getStroke();

    if (subtickVisible) {

      if (step == -1) {

        for (j = 0; j < logStep.length; j++) {
          w = xs + (int) (length * logStep[j]);
          if (w > x0 && w < (x0 + csize.width)) {
            g.drawLine(w, y0 - 1, w, y0 + 2);
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
            g.drawLine(w, y0 - 1, w, y0 + 2);
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
  private int paintYSubTicks(Graphics g, int i, int x0, int y, int length, int la, BasicStroke bs) {

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
          g.drawLine(x0 - 1, h, x0 + 2, h);
          if (gridVisible && subGridVisible) {
            if (bs != null) g2.setStroke(bs);
            g.drawLine(x0, h, x0 + la, h);
            g2.setStroke(old);
          }
        }


      } else if (step > 0) {  // Linear step

        for (j = 0; j < linStep.length; j += (10 / step)) {
          h = y + (int) (length * linStep[j]);
          g.drawLine(x0 - 1, h, x0 + 2, h);
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
  private int paintXSubTicks(Graphics g, int i, int y0, int x, int length, int la, BasicStroke bs) {

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
          g.drawLine(w, y0 - 1, w, y0 + 2);
          if (gridVisible && subGridVisible) {
            if (bs != null) g2.setStroke(bs);
            g.drawLine(w, y0, w, y0 + la);
            g2.setStroke(old);
          }
        }


      } else if (step > 0) {  // Linear step

        for (j = 0; j < linStep.length; j += (10 / step)) {
          w = x + (int) (length * linStep[j]);
          g.drawLine(w, y0 - 1, w, y0 + 2);
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
   */
  public void paintAxis(Graphics g, FontRenderContext frc, int x0, int y0, JLAxis xAxis, int xOrg, int yOrg, Color back) {

    //Do not draw vertical axis without data
    if (orientation != HORIZONTAL && dataViews.size() == 0) return;

    int i,j,x,y,la = 0;
    BasicStroke bs = null;
    Graphics2D g2 = (Graphics2D) g;
    int tickStep = 0;
    Color subgridColor = computeMediumColor(labelColor, back);

    g.setFont(labelFont);

    // stroke for the grid
    if (gridVisible) bs = createStroke(1, gridStyle);
    la = xAxis.getLength() - 2;

    switch (orientation) {

      case VERTICAL_LEFT:

        for (i = 0; i < labels.size(); i++) {

          // Draw labels
          g.setColor(labelColor);
          LabelInfo li = (LabelInfo) labels.get(i);

          x = x0 + (csize.width - 4) - li.size.width;
          y = li.pos + y0;
          g.drawString(li.value, x, y + li.size.height / 3);

          //Draw tick
          g.drawLine(x0 + (csize.width - 2), y, x0 + (csize.width + 3), y);

          //Draw the grid
          if (gridVisible) {
            Stroke old = g2.getStroke();
            if (bs != null) g2.setStroke(bs);
            g.drawLine(x0 + (csize.width + 2), y, x0 + (csize.width + 2) + la, y);
            g2.setStroke(old);
          }

          //Draw sub tick
          g.setColor(subgridColor);
          int ts = paintYSubTicks(g, i, x0 + csize.width, y, -li.pos, la, bs);
          if (ts != 0 && tickStep == 0) tickStep = ts;

        }

        //Draw extra sub ticks (outside labels limit)
        if (tickStep != 0) {
          LabelInfo lis = (LabelInfo) labels.get(0);
          LabelInfo lie = (LabelInfo) labels.get(labels.size() - 1);

          paintExtraYSubTicks(g, x0 + csize.width, y0 + lis.pos - tickStep, tickStep, y0, la, bs, lis.subtick_step);
          paintExtraYSubTicks(g, x0 + csize.width, y0 + lie.pos, tickStep, y0, la, bs, lis.subtick_step);
        }

        // Draw Axe
        g.setColor(labelColor);
        g.drawLine(x0 + csize.width, y0, x0 + csize.width, y0 + csize.height);

        if (name != null) {
          Rectangle2D bounds = labelFont.getStringBounds(name, frc);
          g.drawString(name, (x0 + csize.width) - (int) bounds.getWidth() / 2, y0 - (int) (bounds.getHeight() / 2));
        }

        boundRect.setRect(x0 + csize.width, y0, la, csize.height);
        break;


      case VERTICAL_RIGHT:

        for (i = 0; i < labels.size(); i++) {

          // Draw labels
          g.setColor(labelColor);
          LabelInfo li = (LabelInfo) labels.get(i);

          y = li.pos + y0;
          g.drawString(li.value, x0 + 6, y + li.size.height / 3);

          //Draw tick
          g.drawLine(x0 - 2, y, x0 + 2, y);

          //Draw the grid
          if (gridVisible) {
            Stroke old = g2.getStroke();
            if (bs != null) g2.setStroke(bs);
            g.drawLine(x0 - 2, y, x0 - 2 - la, y);
            g2.setStroke(old);
          }

          //Draw sub tick
          g.setColor(subgridColor);
          int ts = paintYSubTicks(g, i, x0, y, -li.pos, -la, bs);
          if (ts != 0 && tickStep == 0) tickStep = ts;

        }

        //Draw extra sub ticks (outside labels limit)
        if (tickStep != 0) {
          LabelInfo lis = (LabelInfo) labels.get(0);
          LabelInfo lie = (LabelInfo) labels.get(labels.size() - 1);

          paintExtraYSubTicks(g, x0, y0 + lis.pos - tickStep, tickStep, y0, -la, bs, lis.subtick_step);
          paintExtraYSubTicks(g, x0, y0 + lie.pos, tickStep, y0, -la, bs, lis.subtick_step);
        }

        // Draw Axe
        g.setColor(labelColor);
        g.drawLine(x0, y0, x0, y0 + csize.height);

        if (name != null) {
          Rectangle2D bounds = labelFont.getStringBounds(name, frc);
          g.drawString(name, x0 - (int) bounds.getWidth() / 2, y0 - (int) (bounds.getHeight() / 2));
        }

        boundRect.setRect(x0 - la - 1, y0, la, csize.height);
        break;

      case HORIZONTAL:

        for (i = 0; i < labels.size(); i++) {

          // Draw labels
          g.setColor(labelColor);
          LabelInfo li = (LabelInfo) labels.get(i);

          x = li.pos + x0;
          y = y0;
          if (li.isVisible)
            g.drawString(li.value, x - li.size.width / 2, y + li.size.height + 2);

          //Draw tick
          g.drawLine(x, y0 - 2, x, y0 + 3);

          //Draw the grid
          if (gridVisible) {
            Stroke old = g2.getStroke();
            if (bs != null) g2.setStroke(bs);
            g.drawLine(x, y0 - 2, x, y0 - 2 - la);
            g2.setStroke(old);
          }

          //Draw sub tick
          g.setColor(subgridColor);
          int ts = paintXSubTicks(g, i, y, x, -li.pos, -la, bs);
          if (ts != 0 && tickStep == 0) tickStep = ts;

        }

        //Draw extra sub ticks (outside labels limit)
        if (tickStep != 0) {
          LabelInfo lis = (LabelInfo) labels.get(0);
          LabelInfo lie = (LabelInfo) labels.get(labels.size() - 1);

          paintExtraXSubTicks(g, y0, x0 + lis.pos - tickStep, tickStep, x0, -la, bs, lis.subtick_step);
          paintExtraXSubTicks(g, y0, x0 + lie.pos, tickStep, x0, -la, bs, lis.subtick_step);
        }

        // Draw Axe
        g.setColor(labelColor);
        g.drawLine(x0, y0, x0 + csize.width, y0);

        if (name != null) {
          Rectangle2D bounds = labelFont.getStringBounds(name, frc);
          g.drawString(name, x0 + ((csize.width) - (int) bounds.getWidth()) / 2,
              y0 + 2 * (int) bounds.getHeight());
        }

        boundRect.setRect(x0, y0 - la, csize.width, la);
        break;

    }

  }

}
