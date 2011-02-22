//
// JLChart.java
// Description: A Class to handle 2D graphics plot.
//
// JL Pons (c)ESRF 2002


package fr.esrf.tangoatk.widget.util.chart;


import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;

import java.awt.event.*;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.File;
import java.io.FileWriter;

class LabelRect {
  Rectangle rect;
  JLDataView view;

  LabelRect(int x, int y, int w, int h, JLDataView v) {
    rect = new Rectangle(x, y, w, h);
    view = v;
  }
}

class TabbedLine {

  JLDataView[] dv;
  DataList[] dl;
  int anno;
  int sIndex;

  TabbedLine(int nb) {
    dv = new JLDataView[nb];
    dl = new DataList[nb];
  }

  void add(int id, JLDataView v) {
    dv[id] = v;
    dl[id] = v.getData();
  }

  double getMinTime() {
    double r = Double.MAX_VALUE;

    for (int i = 0; i < dl.length; i++) {
      if (dl[i] != null) {
        if (dl[i].x < r) r = dl[i].x;
      }
    }

    return r;
  }

  String getFirstLine(int annotation) {

    StringBuffer ret = new StringBuffer();
    anno = annotation;

    if (annotation == JLAxis.TIME_ANNO) {
      ret.append("Time (s)\t");
    } else {
      ret.append("Index\t");
    }

    for (int i = 0; i < dv.length; i++)
      ret.append(dv[i].getName() + "\t");

    ret.append("\n");

    return ret.toString();

  }

  String getNextLine() {

    double t0 = getMinTime();

    // Test end of data
    if (t0 == Double.MAX_VALUE)
      return null;

    StringBuffer ret = new StringBuffer();

    if (anno == JLAxis.TIME_ANNO) {
      long t = (long) t0;
      long ts = t / 1000;
      long ms = t % 1000;

      if (ms == 0)
        ret.append(ts + "\t");
      else if (ms < 10)
        ret.append(ts + ".00" + ms + "\t");
      else if (ms < 100)
        ret.append(ts + ".0" + ms + "\t");
      else
        ret.append(ts + "." + ms + "\t");

    } else {
      ret.append(Double.toString(t0) + "\t");
    }

    for (int i = 0; i < dl.length; i++) {
      if (dl[i] != null) {
        if (dl[i].x == t0) {
          ret.append(Double.toString(dl[i].y) + "\t");
          dl[i] = dl[i].next;
        } else {
          ret.append("*\t");
        }
      } else {
        ret.append("*\t");
      }
    }


    ret.append("\n");
    return ret.toString();

  }

  String[] getFirstFields(int annotation, boolean showIndex) {

    anno = annotation;
    sIndex = (showIndex)?1:0;
    String[] ret = new String[dv.length + sIndex];

    if (sIndex>0) {
      if (annotation == JLAxis.TIME_ANNO) {
        ret[0] = "Time (s)";
      } else {
        ret[0] = "Index";
      }
    }

    for (int i = 0; i < dv.length; i++)
      ret[i + sIndex] = dv[i].getName();

    return ret;

  }

  String[] getNextFields() {

    double t0 = getMinTime();

    // Test end of data
    if (t0 == Double.MAX_VALUE)
      return null;

    String[] ret = new String[dv.length + sIndex];

    if (sIndex > 0) {
      if (anno == JLAxis.TIME_ANNO) {
        ret[0] = JLAxis.formatTimeValue(t0);
      } else {
        ret[0] = Double.toString(t0);
      }
    }

    for (int i = 0; i < dl.length; i++) {
      if (dl[i] != null) {
        if (dl[i].x == t0) {
          ret[i + sIndex] = dv[i].formatValue(dl[i].y);
          dl[i] = dl[i].next;
        } else {
          ret[i + sIndex] = "";
        }
      } else {
        ret[i + sIndex] = "";
      }
    }


    return ret;

  }

}

/**
 * A Class to handle 2D graphics plot.
 * @author JL Pons
 */

public class JLChart extends JComponent implements MouseListener, MouseMotionListener, ActionListener {

  // constant
  /** Place label at the bottom of the chart */
  public static final int LABEL_DOWN = 0;
  /** Place label at the top of the chart */
  public static final int LABEL_UP = 1;
  /** Place label at the right of the chart */
  public static final int LABEL_RIGHT = 2;
  /** Place label at the left of the chart */
  public static final int LABEL_LEFT = 3;

  /* Chart properties menu item */
  public static final int MENU_CHARTPROP = 0;
  /* Data view properties menu item */
  public static final int MENU_DVPROP    = 1;
  /* Show table menu item */
  public static final int MENU_TABLE     = 2;
  /* Save data file menu item */
  public static final int MENU_DATASAVE  = 3;
  /* print graph menu item */
  public static final int MENU_PRINT     = 4;

  // Global graph options
  private String header = null;
  private boolean headerVisible = false;
  private Font headerFont;
  private Color headerColor;

  private boolean labelVisible = true;
  private int labelMode = LABEL_DOWN;
  private Font labelFont;
  private Vector labelRect;

  private boolean ipanelVisible = false;
  private boolean paintAxisFirst = true;
  private Color chartBackground;

  private double displayDuration;

  private JPopupMenu chartMenu;
  private JMenuItem optionMenuItem;
  private JMenuItem fileMenuItem;
  private JMenuItem zoomBackMenuItem;
  private JMenuItem printMenuItem;
  private JSeparator sepMenuItem;

  private JMenu tableMenu;
  private JMenuItem tableAllMenuItem;
  private JMenuItem[] tableSingleY1MenuItem = new JMenuItem[0];
  private JMenuItem[] tableSingleY2MenuItem = new JMenuItem[0];

  private JMenu dvMenu;
  private JMenuItem[] dvY1MenuItem = new JMenuItem[0];
  private JMenuItem[] dvY2MenuItem = new JMenuItem[0];

  private JMenuItem[] userActionMenuItem;
  private String[] userAction;

  private boolean zoomDrag;
  private boolean zoomDragAllowed;
  private int zoomX;
  private int zoomY;
  private JButton zoomButton;

  private int lastX;
  private int lastY;
  private SearchInfo lastSearch;

  // Measurements stuff
  private Rectangle headerR;
  private Rectangle labelR;
  private Rectangle viewR;
  private Dimension margin;
  private int labelSHeight;
  private int labelWidth;
  private int labelHeight;
  private int headerWidth;
  private int axisHeight;
  private int axisWidth;
  private int axisFontUp;
  private int axisFontDown;
  private int y1AxisThickness;
  private int y2AxisThickness;

  // Axis
  private JLAxis xAxis;
  private JLAxis y1Axis;
  private JLAxis y2Axis;
  
  private boolean xAxisOnBottom=true;

  // Listeners
  private IJLChartListener listener;  // JLChart listener

  // Table
  private JLTable theTable = null;


  /**
   * Graph constructor.
   */
  public JLChart() {
    Color defColor = new Color(180, 180, 180);
    setBackground(defColor);
    setChartBackground(defColor);
    setForeground(Color.black);
    setOpaque(true);
    setFont(new Font("Dialog", Font.PLAIN, 12));
    headerFont = getFont();
    headerColor = getForeground();
    labelFont = getFont();

    margin = new Dimension(10,10);
    headerR = new Rectangle(0,0,0,0);
    viewR = new Rectangle(0,0,0,0);
    labelR = new Rectangle(0,0,0,0);

    xAxis = new JLAxis(this, JLAxis.HORIZONTAL_DOWN);
    xAxis.setAnnotation(JLAxis.TIME_ANNO);
    xAxis.setAutoScale(true);
    xAxis.setAxeName("(X)");
    y1Axis = new JLAxis(this, JLAxis.VERTICAL_LEFT);
    y1Axis.setAxeName("(Y1)");
    y2Axis = new JLAxis(this, JLAxis.VERTICAL_RIGHT);
    y2Axis.setAxeName("(Y2)");
    displayDuration = Double.POSITIVE_INFINITY;

    labelRect = new Vector();
    zoomDrag = false;
    zoomDragAllowed = false;

    chartMenu = new JPopupMenu();

    optionMenuItem = new JMenuItem("Chart properties");
    optionMenuItem.addActionListener(this);

    fileMenuItem = new JMenuItem("Save data File");
    fileMenuItem.addActionListener(this);

    tableMenu = new JMenu("Show table");
    tableAllMenuItem = new JMenuItem("All");
    tableAllMenuItem.addActionListener(this);

    zoomBackMenuItem = new JMenuItem("Zoom back");
    zoomBackMenuItem.addActionListener(this);

    printMenuItem = new JMenuItem("Print graph");
    printMenuItem.addActionListener(this);

    dvMenu = new JMenu("Data View properties");

    /*
    infoMenuItem = new JMenuItem("Chart menu");
    infoMenuItem.setEnabled(false);
    chartMenu.add(infoMenuItem);
    chartMenu.add(new JSeparator());
    */

    chartMenu.add(zoomBackMenuItem);
    chartMenu.add(new JSeparator());
    chartMenu.add(optionMenuItem);
    chartMenu.add(dvMenu);
    chartMenu.add(tableMenu);
    chartMenu.add(new JSeparator());
    chartMenu.add(fileMenuItem);
    chartMenu.add(printMenuItem);

    sepMenuItem = new JSeparator();
    chartMenu.add(sepMenuItem);

    userActionMenuItem = new JMenuItem[0];
    userAction = new String[0];
    sepMenuItem.setVisible(false);

    //Set up listeners
    addMouseListener(this);
    addMouseMotionListener(this);

    listener = null;
    listenerList = new EventListenerList();

    zoomButton = new JButton("Zoom back");
    zoomButton.setFont(labelFont);
    zoomButton.setMargin(new Insets(2,2,1,1));
    zoomButton.setVisible(false);
    zoomButton.addActionListener(this);
    add(zoomButton);

  }

  /**
   * Return a handle to the x axis
   * @return Axis handle
   */
  public JLAxis getXAxis() {
    return xAxis;
  }

  /**
   * Return a handle to the left y axis
   * @return Axis handle
   */
  public JLAxis getY1Axis() {
    return y1Axis;
  }

  /**
   * Return a handle to the right y axis
   * @return Axis handle
   */
  public JLAxis getY2Axis() {
    return y2Axis;
  }

  /**
   * Sets  weather x Axis is on bottom of screen or not
   * @param b boolean to know weather x Axis is on bottom of screen or not
   */
  public void setXAxisOnBottom(boolean b){
      xAxisOnBottom = b;
      getXAxis().setXAxisOnBottom(b);
      getY1Axis().setXAxisOnBottom(b);
      getY2Axis().setXAxisOnBottom(b);
  }

  /**
   * tells  weather x Axis is on bottom of screen or not
   * @return [code]true[/code] if x Axis is on bottom of screen, [code]false[/code] otherwise
   */
  public boolean isXAxisOnBottom(){
      return xAxisOnBottom;
  }
  
  /**
   * Sets header font
   * @param f Header font
   * @see JLChart#getHeaderFont
   */
  public void setHeaderFont(Font f) {
    headerFont = f;
  }

  /**
   * Gets the header font
   * @return Header font
   * @see JLChart#setHeaderFont
   */
  public Font getHeaderFont() {
    return headerFont;
  }

  /**
   * Sets component margin
   * @param d Margin
   * @see JLChart#getMargin
   */
  public void setMargin(Dimension d) {
    margin  = d;
  }

  /**
   * Gets the current margin
   * @return Margin
   * @see JLChart#setMargin
   */
  public Dimension getMargin() {
    return margin;
  }

  public void setBackground(Color c) {
    super.setBackground(c);
    //setChartBackground(c);
  }

  /**
   * Sets the chart background (curve area)
   * @param c Background color
   */
  public void setChartBackground(Color c) {
    chartBackground = c;
  }

  /**
   *
   * Gets the chart background (curve area)
   * @return Background color
   */
  public Color getChartBackground() {
    return chartBackground;
  }

  /**
   * Paints axis under curve when true
   * @param b Painting order
   */
  public void setPaintAxisFirst(boolean b) {
    //paintAxisFirst = true;
    paintAxisFirst = b;
  }

  /**
   * Return painting order between axis and curve
   * @return true if axis are painted under curve
   */
  public boolean isPaintAxisFirst() {
    return paintAxisFirst;
  }

  /**
   * Displays or hides header.
   * @param b true if the header is visible, false otherwise
   * @see JLChart#setHeader
   */
  public void setHeaderVisible(boolean b) {
    headerVisible = b;
  }

  /**
   * Sets the header and displays it.
   * @param s Graph header
   * @see JLChart#getHeader
   */
  public void setHeader(String s) {
    header = s;
    if (s != null)
      if (s.length() == 0)
        header = null;
    setHeaderVisible(header != null);
  }

  /**
   * Gets the current header
   * @return Graph header
   * @see JLChart#setHeader
   */
  public String getHeader() {
    return header;
  }


  /**
   * Sets the display duration.This will garbage old data in all displayed data views.
   * Garbaging occurs when addData is called.
   * @param v Displauy duration (millisec). Pass Double.POSITIVE_INFINITY to disable.
   * @see JLChart#addData
   */
  public void setDisplayDuration(double v) {
    displayDuration = v;
    getXAxis().setAxisDuration(v);
  }

  /**
   * Gets the display duration.
   * @return Display duration
   * @see JLChart#setDisplayDuration
   */
  public double getDisplayDuration() {
    return displayDuration;
  }

  /**
   * Sets the header color
   * @param c Header color
   */
  public void setHeaderColor(Color c) {
    headerColor = c;
    setHeaderVisible(true);
  }

  /**
   * Displays or hide labels.
   * @param b true if labels are visible, false otherwise
   * @see JLChart#isLabelVisible
   */
  public void setLabelVisible(boolean b) {
    labelVisible = b;
  }

  /**
   * Determines wether labels are visivle or not.
   * @return true if labels are visible, false otherwise
   */
  public boolean isLabelVisible() {
    return labelVisible;
  }

  /**
   * Set the label placement.
   * @param p Placement
   * @see JLChart#LABEL_UP
   * @see JLChart#LABEL_DOWN
   * @see JLChart#LABEL_LEFT
   * @see JLChart#LABEL_RIGHT
   */
  public void setLabelPlacement(int p) {
    labelMode = p;
  }

  /**
   * Returns the current label placement.
   * @return Label placement
   * @see JLChart#setLabelPlacement
   */
  public int getLabelPlacement() {
    return labelMode;
  }

  /**
   * Sets the label font
   * @param f
   */
  public void setLabelFont(Font f) {
    labelFont = f;
  }
  
  /**
   * Returns the label font
   * @see #setLabelFont
   */
  public Font getLabelFont() {
    return labelFont;
  }

  /**
   * Display the global graph option dialog.
   */
  public void showOptionDialog() {

    Object dlgParent = getRootPane().getParent();
    JLChartOption optionDlg;

    if (dlgParent instanceof JDialog) {
      optionDlg = new JLChartOption((JDialog) dlgParent, this);
    } else if (dlgParent instanceof JFrame) {
      optionDlg = new JLChartOption((JFrame) dlgParent, this);
    } else {
      optionDlg = new JLChartOption((JFrame) null, this);
    }

    ATKGraphicsUtils.centerDialog(optionDlg);
    optionDlg.setVisible(true);

  }

  /**
   * Display the data view option dialog.
   */
  public void showDataOptionDialog(JLDataView v) {

    Object dlgParent = getRootPane().getParent();
    JLDataViewOption optionDlg;

    if (dlgParent instanceof JDialog) {
      optionDlg = new JLDataViewOption((JDialog) dlgParent, this, v);
    } else if (dlgParent instanceof JFrame) {
      optionDlg = new JLDataViewOption((JFrame) dlgParent, this, v);
    } else {
      optionDlg = new JLDataViewOption((JFrame) null, this, v);
    }

    ATKGraphicsUtils.centerDialog(optionDlg);
    optionDlg.setVisible(true);

  }

  /**
   * Determines wether the graph is zoomed.
   * @return true if the , false otherwise
   */
  public boolean isZoomed() {
    return xAxis.isZoomed() || y1Axis.isZoomed() || y2Axis.isZoomed();
  }

  /**
   * Enter zoom mode. This happens when you hold the left mouse button down
   * and drag the mouse.
   */
  public void enterZoom() {
    if (!zoomDragAllowed) {
      zoomDragAllowed = true;
      setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
    }
  }

  /**
   * Set the specified JLChart Listener
   * @param l JLChart listener. If set to null the listener will be removed.
   */
  public void setJLChartListener(IJLChartListener l) {
    listener = l;
  }

  /**
   * Adds a user action. It will be available from the contextual
   * chart menu. All JLChartActionListener are triggered when
   * a user action is executed.
   * Hint: If the action name starts with 'chk' , it will be
   * displayed as check box menu item. Each time the chart menu
   * is shown, a getActionState() is executed on all listener,
   * if several listener handle the same action, the result will be a
   * logical and of all results.
   * @param name Action name
   */
  public void addUserAction(String name) {
    int i;

    String[] action = new String[userAction.length + 1];
    for (i = 0; i < userAction.length; i++) action[i] = userAction[i];
    action[i] = name;

    // Build the menu
    for (i = 0; i < userActionMenuItem.length; i++) {
      chartMenu.remove(userActionMenuItem[i]);
      userActionMenuItem[i].removeActionListener(this);
      userActionMenuItem[i] = null;
    }

    JMenuItem[] actionMenu = new JMenuItem[action.length];
    for (i = 0; i < action.length; i++) {
      if (action[i].startsWith("chk")) {
        actionMenu[i] = new JCheckBoxMenuItem(action[i].substring(3));
      } else {
        actionMenu[i] = new JMenuItem(action[i]);
      }
      actionMenu[i].addActionListener(this);
      chartMenu.add(actionMenu[i]);
    }

    userActionMenuItem = actionMenu;
    userAction = action;
    sepMenuItem.setVisible(true);

  }

  /**
   * Add the specified JLChartAction listener to the list
   * @param l Listener to add
   */
  public void addJLChartActionListener(IJLChartActionListener l) {
    listenerList.add(IJLChartActionListener.class,  l);
  }

  /**
   * Exit zoom mode.
   */
  public void exitZoom() {
    xAxis.unzoom();
    y1Axis.unzoom();
    y2Axis.unzoom();
    zoomDragAllowed = false;
    setCursor(Cursor.getDefaultCursor());
    repaint();
  }

  /**
   * Method to remove item of the contextual menu.
   * @param menu Item to remove
   * @see #MENU_CHARTPROP
   * @see #MENU_DVPROP
   * @see #MENU_TABLE
   * @see #MENU_DATASAVE
   * @see #MENU_PRINT
   */
  public void removeMenuItem(int menu) {

    switch(menu) {
      /* Chart properties menu item */
      case MENU_CHARTPROP:
        chartMenu.remove(optionMenuItem);
        break;
      /* Data view properties menu item */
      case MENU_DVPROP:
        chartMenu.remove(dvMenu);
        break;
      /* Show table menu item */
      case MENU_TABLE:
        chartMenu.remove(tableMenu);
        break;
      /* Save data file menu item */
      case MENU_DATASAVE:
        chartMenu.remove(fileMenuItem);
        break;
      /* print graph menu item */
     case MENU_PRINT:
        chartMenu.remove(printMenuItem);
        break;

    }
  }
  
  /**
   * Method to add item to the contextual menu.
   * @param menu MenuItem to add
   */
  public void addMenuItem(JMenuItem menu) {
    chartMenu.add(menu);
  }

  /**
   * Method to add a separator to the contextual menu.
   */
  public void addSeparator() {
    chartMenu.addSeparator();
  }
  
  /**
   * Remove the specified JLChartAction Listener
   * @param l Listener to remove
   */
  public void removeJLChartActionListener(IJLChartActionListener l) {
    listenerList.remove(IJLChartActionListener.class, l);
  }

  /**
   * Apply graph configuration. This includes all global settings.
   * The CfFileReader object must have been filled by the caller.
   * @param f Handle to CfFileReader object that contains global graph param
   * @see CfFileReader#parseText
   * @see CfFileReader#readFile
   * @see JLAxis#applyConfiguration
   * @see JLDataView#applyConfiguration
   */
  public void applyConfiguration(CfFileReader f) {

    Vector p;

    // General settings
    p = f.getParam("graph_title");
    if (p != null) setHeader(OFormat.getName(p.get(0).toString()));
    p = f.getParam("label_visible");
    if (p != null) setLabelVisible(OFormat.getBoolean(p.get(0).toString()));
    p = f.getParam("label_placement");
    if (p != null) setLabelPlacement(OFormat.getInt(p.get(0).toString()));
    p = f.getParam("label_font");
    if (p != null) setLabelFont(OFormat.getFont(p));
    p = f.getParam("graph_background");
    if (p != null) setBackground(OFormat.getColor(p));
    p = f.getParam("chart_background");
    if (p != null) setChartBackground(OFormat.getColor(p));
    p = f.getParam("title_font");
    if (p != null) setHeaderFont(OFormat.getFont(p));
    p = f.getParam("display_duration");
    if (p != null) setDisplayDuration(OFormat.getDouble(p.get(0).toString()));

  }

  /**
   * Build a configuration string that can be write into a file and is compatible
   * with CfFileReader.
   * @return A string containing param.
   * @see JLChart#applyConfiguration
   * @see JLDataView#getConfiguration
   * @see JLAxis#getConfiguration
   */
  public String getConfiguration() {

    String to_write = "";

    to_write += "graph_title:\'" + getHeader() + "\'\n";
    to_write += "label_visible:" + isLabelVisible() + "\n";
    to_write += "label_placement:" + getLabelPlacement() + "\n";
    to_write += "label_font:" + OFormat.font(getLabelFont()) + "\n";
    to_write += "graph_background:" + OFormat.color(getBackground()) + "\n";
    to_write += "chart_background:" + OFormat.color(getChartBackground()) + "\n";
    to_write += "title_font:" + OFormat.font(getHeaderFont()) + "\n";
    to_write += "display_duration:" + getDisplayDuration() + "\n";

    return to_write;
  }

  /**
   * Returns a string containing the configuration file help.
   */
  public String getHelpString() {

    return "-- Global chart settings --\n\n" +
           "graph_title:'title'   Chart title ('null' to disable)\n" +
           "label_visible:true or false  Show legend\n" +
           "label_placement:value   (0 Down,1 Up,2 Right, 3 Left)\n" +
           "label_font:name,style(0 Plain,1 Bold,2 italic),size \n" +
           "graph_background:r,g,b   Component background \n" +
           "chart_background:r,g,b   Graph area background \n" +
           "title_font:name,style(0 Plain,1 Bold,2 italic),size\n" +
           "display_duration:milliSec   X axis duration (time monitoring)\n\n" +
           JLAxis.getHelpString() + "\n" +
           JLDataView.getHelpString();

  }

  /**
   * Remove all dataview from the graph.
   */
  public void unselectAll() {
    getY1Axis().clearDataView();
    getY2Axis().clearDataView();
    getXAxis().clearDataView();
  }

  /**
   * Prints out this graph.
   */
  public void printGraph() {

    ATKGraphicsUtils.printComponent(this,"Print Graph",true,0);

  }

  // -----------------------------------------------------

  // Fire JLChartActionEvent to all registered IJLChartActionListener
  private void fireActionPerfromed(String name, boolean state) {
    IJLChartActionListener[] list = (IJLChartActionListener[]) (listenerList.getListeners(IJLChartActionListener.class));
    JLChartActionEvent w = new JLChartActionEvent(this, name, state);
    for (int i = 0; i < list.length; i++) list[i].actionPerformed(w);
  }

  // Fire JLChartActionEvent to all registered IJLChartActionListener
  private boolean fireGetActionState(String name) {
    IJLChartActionListener[] list = (IJLChartActionListener[]) (listenerList.getListeners(IJLChartActionListener.class));
    JLChartActionEvent w = new JLChartActionEvent(this, name);
    boolean ret = true;
    for (int i = 0; i < list.length; i++)
      ret = list[i].getActionState(w) && ret;

    return ret;
  }

  // Make a snapshot of data in a TAB seperated field
  private void saveDataFile(String fileName) {

    try {

      FileWriter fw = new FileWriter(fileName);
      TabbedLine tl;
      String s;

      Vector views = new Vector();
      if (xAxis.isXY()) views.addAll(xAxis.getViews());
      views.addAll(y1Axis.getViews());
      views.addAll(y2Axis.getViews());

      tl = new TabbedLine(views.size());
      for (int v = 0; v < views.size(); v++) tl.add(v, (JLDataView) views.get(v));

      s = tl.getFirstLine(xAxis.getAnnotation());
      while (s != null) {
        fw.write(s);
        s = tl.getNextLine();
      }

      fw.close();

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error during saving file.\n" + e.getMessage());
    }

  }

  // Display a JTable containing data of a single dataView
  private void showTableSingle(JLDataView v) {

    TabbedLine tl;

    tl = new TabbedLine(1);
    tl.add(0, v);

    if (theTable == null)
      theTable = new JLTable();

    // Build data
    Vector data = new Vector();
    String[] cols = tl.getFirstFields(xAxis.getAnnotation(),!xAxis.isXY());
    Object s = tl.getNextFields();
    while (s != null) {
      data.add(s);
      s = tl.getNextFields();
    }

    int y = data.size();
    int x = cols.length;
    Object[][] dv = new Object[y][x];
    for (int j = 0; j < y; j++) {
      Object[] ln = (Object[]) data.get(j);
      for (int i = 0; i < x; i++) {
        dv[j][i] = ln[i];
      }
    }

    theTable.setData(dv, cols);
    if (!theTable.isVisible()) 
      theTable.centerWindow();
    theTable.setVisible(true);

  }

  // Display a JTable containing all data of the chart
  private void showTableAll() {

    TabbedLine tl;

    Vector views = new Vector();
    if (xAxis.isXY()) views.addAll(xAxis.getViews());
    views.addAll(y1Axis.getViews());
    views.addAll(y2Axis.getViews());

    tl = new TabbedLine(views.size());
    for (int v = 0; v < views.size(); v++) tl.add(v, (JLDataView) views.get(v));

    if (theTable == null)
      theTable = new JLTable();

    // Build data
    Vector data = new Vector();
    String[] cols = tl.getFirstFields(xAxis.getAnnotation(),!xAxis.isXY());
    Object s = tl.getNextFields();
    while (s != null) {
      data.add(s);
      s = tl.getNextFields();
    }

    int y = data.size();
    int x = cols.length;
    Object[][] dv = new Object[y][x];
    for (int j = 0; j < y; j++) {
      Object[] ln = (Object[]) data.get(j);
      for (int i = 0; i < x; i++) {
        dv[j][i] = ln[i];
      }
    }

    theTable.setData(dv, cols);
    if (!theTable.isVisible())
      theTable.centerWindow();
    theTable.setVisible(true);

  }

  // -----------------------------------------------------
  // Action listener
  // -----------------------------------------------------
  public void actionPerformed(ActionEvent evt) {

    Object src= evt.getSource();

    if (src == optionMenuItem) {
      showOptionDialog();
    } else if (src == zoomBackMenuItem || src == zoomButton) {
      exitZoom();
    } else if (src == printMenuItem ) {
      printGraph();
    } else if (src == tableAllMenuItem) {
      showTableAll();
    } else if (src == fileMenuItem) {

      int ok = JOptionPane.YES_OPTION;
      JFileChooser chooser = new JFileChooser(".");
      chooser.setDialogTitle("Save Graph Data (Text file with TAB separated fields)");
      int returnVal = chooser.showSaveDialog(this);

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File f = chooser.getSelectedFile();
        if (f != null) {
          if (f.exists()) ok = JOptionPane.showConfirmDialog(this, "Do you want to overwrite " + f.getName() + " ?", "Confirm overwrite", JOptionPane.YES_NO_OPTION);
          if (ok == JOptionPane.YES_OPTION)
            saveDataFile(f.getAbsolutePath());
        }
      }

    } else {

      // Search in user action
      boolean found = false;
      int i = 0;
      while (i < userActionMenuItem.length && !found) {
        found = userActionMenuItem[i] == evt.getSource();
        if (!found) i++;
      }

      if (found) {
        if (userActionMenuItem[i] instanceof JCheckBoxMenuItem) {
          JCheckBoxMenuItem c = (JCheckBoxMenuItem) userActionMenuItem[i];
          fireActionPerfromed(c.getText(), c.getState());
        } else {
          fireActionPerfromed(userActionMenuItem[i].getText(), false);
        }
        return;
      }

      // Search in show Data View option menu
      i = 0;
      while (i < dvY1MenuItem.length && !found) {
        found = dvY1MenuItem[i] == evt.getSource();
        if (!found) i++;
      }

      if(found) {
        showDataOptionDialog(y1Axis.getDataView(i));
        return;
      }

      i = 0;
      while (i < dvY2MenuItem.length && !found) {
        found = dvY2MenuItem[i] == evt.getSource();
        if (!found) i++;
      }

      if(found) {
        showDataOptionDialog(y2Axis.getDataView(i));
        return;
      }

      // Search in show table single menu iten
      i = 0;
      while (i < tableSingleY1MenuItem.length && !found) {
        found = tableSingleY1MenuItem[i] == evt.getSource();
        if (!found) i++;
      }

      if(found) {
        showTableSingle(y1Axis.getDataView(i));
        return;
      }

      i = 0;
      while (i < tableSingleY2MenuItem.length && !found) {
        found = tableSingleY2MenuItem[i] == evt.getSource();
        if (!found) i++;
      }

      if(found) {
        showTableSingle(y2Axis.getDataView(i));
        return;
      }

    }

  }

  // paint Label and header
  private void paintLabelAndHeader(Graphics2D g) {

    int nbv1 = y1Axis.getViews().size();
    int nbv2 = y2Axis.getViews().size();
    int ypos,xpos;

    // Draw header
    if (headerR.width>0) {
      g.setFont(headerFont);
      xpos = ((headerR.width - headerWidth) / 2);
      g.setColor(headerColor);
      g.drawString(header, xpos, headerR.y + g.getFontMetrics(headerFont).getAscent() - 2);
    }

    // Draw labels
    labelRect.clear();
    if (labelR.width>0) {

      g.setFont(labelFont);
      JLDataView v;
      int a = g.getFontMetrics(labelFont).getAscent();
      int i,k = 0;
      int y;

      // Center
      xpos = labelR.x + (labelR.width - labelWidth) / 2 + 2;
      ypos = labelR.y + (labelR.height - labelHeight) / 2 + 2;

      // Draw labels
      for (i = 0; i < nbv1; i++) {
        v = (JLDataView) y1Axis.getViews().get(i);
        if (v.isLabelVisible()) {
          g.setColor(v.getColor());
          y = ypos + labelSHeight * k + labelSHeight / 2;
          JLAxis.drawSampleLine(g, xpos, y - 2, v);
          g.setColor(Color.BLACK);
          g.drawString(v.getExtendedName() + " " + y1Axis.getAxeName(), xpos + 44, y + labelSHeight - a);
          labelRect.add(new LabelRect(xpos, y - a, labelWidth + 44, labelSHeight, v));
          k++;
        }
      }

      for (i = 0; i < nbv2; i++) {
        v = (JLDataView) y2Axis.getViews().get(i);
        if (v.isLabelVisible()) {
          g.setColor(v.getColor());
          y = ypos + labelSHeight * k + labelSHeight / 2;
          JLAxis.drawSampleLine(g, xpos, y - 2, v);
          g.setColor(Color.BLACK);
          g.drawString(v.getExtendedName() + " " + y2Axis.getAxeName(), xpos + 44, y + labelSHeight - a);
          labelRect.add(new LabelRect(xpos, y - a, labelWidth + 44, labelSHeight, v));
          k++;
        }
      }

    }

  }

  // Compute size of graph items (Axe,label,header,....
  private void measureGraphItems(Graphics2D g, FontRenderContext frc, int w, int h, Vector views) {

    Rectangle2D bounds = null;
    int i;
    int MX = margin.width;
    int MY = margin.height;

    // Reset sizes ------------------------------------------------------
    headerR.setBounds(0,0,0,0);
    viewR.setBounds(0, 0, 0, 0);
    labelR.setBounds(0, 0, 0, 0);
    labelWidth = 0;
    labelHeight = 0;
    headerWidth = 0;
    axisWidth = 0;
    axisHeight = 0;
    axisFontUp = 0;
    axisFontDown = 0;
    y1AxisThickness = 0;
    y2AxisThickness = 0;

    // Measure header ------------------------------------------------------
    if (headerVisible && (header!=null) && (headerFont!=null)) {
      bounds = headerFont.getStringBounds(header, frc);
      headerWidth = (int) bounds.getWidth();
      headerR.setBounds(MX , MY , w-2*MX , (int)bounds.getHeight());
    }

    // Compute label number ------------------------------------------------------
    int nbLab=0;
    for (i = 0; i < y1Axis.getViews().size(); i++)
      if( ((JLDataView) y1Axis.getViews().get(i)).isLabelVisible() )
        nbLab++;
    for (i = 0; i < y2Axis.getViews().size(); i++)
      if( ((JLDataView) y2Axis.getViews().get(i)).isLabelVisible() )
        nbLab++;

    // Measure labels ------------------------------------------------------
    if (labelVisible && (nbLab>0) && (labelFont!=null)) {

      JLDataView v;
      i = 0;

      double maxLength = 0;
      for (i = 0; i < y1Axis.getViews().size(); i++) {
        v = (JLDataView) y1Axis.getViews().get(i);
        if (v.isLabelVisible()) {
          bounds = labelFont.getStringBounds(v.getExtendedName() + " " + y1Axis.getAxeName(), frc);
          if (bounds.getWidth() > maxLength)
            maxLength = bounds.getWidth();
        }
      }
      for (i = 0; i < y2Axis.getViews().size(); i++) {
        v = (JLDataView) y2Axis.getViews().get(i);
        if (v.isLabelVisible()) {
          bounds = labelFont.getStringBounds(v.getExtendedName() + " " + y2Axis.getAxeName(), frc);
          if (bounds.getWidth() > maxLength)
            maxLength = bounds.getWidth();
        }
      }

      labelSHeight = (int) bounds.getHeight() + 2;
      labelHeight = (labelSHeight * nbLab);
      labelWidth = (int) (maxLength + 45); // sample line width

      switch( labelMode ) {
        case LABEL_UP:
          labelR.setBounds(MX ,MY + headerR.height ,w-2*MX ,labelHeight);
          break;
        case LABEL_DOWN:
          labelR.setBounds(MX ,h-MY-labelHeight, w-2*MX, labelHeight);
          break;
        case LABEL_RIGHT:
          labelR.setBounds(w-MX-labelWidth, MY + headerR.height, labelWidth, h-2*MY-headerR.height);
          break;
        case LABEL_LEFT:
          labelR.setBounds(MX, MY + headerR.height, labelWidth, h - 2 * MY - headerR.height);
          break;
      }

    }

    // Measure view Rectangle --------------------------------------------
    switch (labelMode) {
      case LABEL_UP:
        viewR.setBounds(MX, MY + headerR.height + labelR.height , w - 2 * MX, h - 2*MY - headerR.height - labelR.height);
        break;
      case LABEL_DOWN:
        viewR.setBounds(MX, MY + headerR.height , w - 2 * MX, h - 2 * MY - headerR.height - labelR.height);
        break;
      case LABEL_RIGHT:
        viewR.setBounds(MX, MY + headerR.height , w - 2 * MX - labelR.width , h - 2 * MY - headerR.height);
        break;
      case LABEL_LEFT:
        viewR.setBounds(MX + labelR.width, MY + headerR.height, w - 2 * MX - labelR.width, h - 2 * MY - headerR.height);
        break;
    }

    // Measure Axis ------------------------------------------------------
    xAxis.computeXScale(views);

    if ( y1Axis.isVisible() && (y1Axis.getViews().size() > 0) ) {
      axisFontUp = y1Axis.getFontHeight(g);
    } else if (y2Axis.isVisible() && (y2Axis.getViews().size() > 0)) {
      axisFontUp = y2Axis.getFontHeight(g);
    }

    axisFontDown = xAxis.getFontHeight(g);

    axisHeight = viewR.height - (axisFontUp + axisFontDown);

    y1Axis.measureAxis(frc, 0, axisHeight);
    y2Axis.measureAxis(frc, 0, axisHeight);

    y1AxisThickness = y1Axis.getThickness();
    y2AxisThickness = y2Axis.getThickness();

    axisWidth = viewR.width - (y1AxisThickness+y2AxisThickness);

    xAxis.measureAxis(frc, axisWidth, 0);

 }

  // Paint the zoom mode label
  private void paintZoomButton(int x,int y) {

    if( isZoomed() ) {
      int w = zoomButton.getPreferredSize().width;
      int h = zoomButton.getPreferredSize().height;
      zoomButton.setBounds(x+7,y+5,w,h);
      zoomButton.setVisible(w<(axisWidth-7) && h<(axisHeight-5));
    } else {
      zoomButton.setVisible(false);
    }

  }

  // Paint the zoom rectangle
  private void paintZoomSelection(Graphics g) {

    if (zoomDrag) {
      g.setColor(Color.black);
      // Draw rectangle
      Rectangle r = buildRect(zoomX, zoomY, lastX, lastY);
      g.drawRect(r.x, r.y, r.width, r.height);
    }

  }

  /**
   * Paint the components. Use the repaint method to repaint the graph.
   * @param g Graphics object.
   */
  public void paint(Graphics g) {

    int w = getWidth();
    int h = getHeight();

    // Create a vector containing all views
    Vector views = new Vector(y1Axis.getViews());
    views.addAll(y2Axis.getViews());

    // Avoid a partial repaint that can make bad looking fx
    // if some dataViews has changed without a repaint().
//  Rectangle cr = g.getClipBounds();
//  Rectangle mr = new Rectangle(0, 0, w, h);
//  if (!cr.equals(mr)) {
//     // Ask full repaint
//     repaint();
//     return;
//  }

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
      RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    FontRenderContext frc = g2.getFontRenderContext();

    g.setPaintMode();

    if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, w, h);
    }

    // Compute bounds of label and graph
    measureGraphItems(g2,frc,w,h,views);

    // Draw label and header
    paintLabelAndHeader(g2);

    // Paint chart background
    int xOrg = viewR.x + y1AxisThickness;
    int yOrg;
    if (isXAxisOnBottom() || getY1Axis().getScale() == JLAxis.LOG_SCALE){
        yOrg = viewR.y + axisFontUp + axisHeight;
    }
    else {
        yOrg = (int)getY1Axis().transform(0, 0, getXAxis()).getY();//RG comment : I changed this line to aline X Axis whith 0 on Y1 Axis      
    }

    int xOrgY1 = viewR.x;
    int xOrgY2 = viewR.x + y1AxisThickness + axisWidth;
    int yOrgY  = viewR.y + axisFontUp;

    if (!chartBackground.equals(getBackground()) && axisWidth > 0 && axisHeight > 0) {
      g.setColor(chartBackground);
      g.fillRect(xOrg, yOrg - axisHeight, axisWidth, axisHeight);
    }

    // Paint zoom stuff
    paintZoomSelection(g);
    paintZoomButton(xOrg,yOrgY);

    if (paintAxisFirst) {

      //Draw axes
      y1Axis.paintAxis(g, frc, xOrgY1, yOrgY, xAxis, xOrg, yOrg, getBackground(),!y2Axis.isVisible() || y2Axis.getViewNumber()==0);
      y2Axis.paintAxis(g, frc, xOrgY2, yOrgY, xAxis, xOrg, yOrg, getBackground(),!y1Axis.isVisible() || y1Axis.getViewNumber()==0);
      if( xAxis.getPosition()==JLAxis.HORIZONTAL_ORG2)
        xAxis.paintAxis(g, frc, xOrg, yOrg, y2Axis, 0, 0, getBackground(),true);
      else
        xAxis.paintAxis(g, frc, xOrg, yOrg, y1Axis, 0, 0, getBackground(),true);

      //Draw data
      y1Axis.paintDataViews(g, xAxis, xOrg, yOrg);
      y2Axis.paintDataViews(g, xAxis, xOrg, yOrg);

    } else {

      //Draw data
      y1Axis.paintDataViews(g, xAxis, xOrg, yOrg);
      y2Axis.paintDataViews(g, xAxis, xOrg, yOrg);

      //Draw axes
      y1Axis.paintAxis(g, frc, xOrgY1, yOrgY, xAxis, xOrg, yOrg, getBackground(),!y2Axis.isVisible() || y2Axis.getViewNumber()==0);
      y2Axis.paintAxis(g, frc, xOrgY2, yOrgY, xAxis, xOrg, yOrg, getBackground(),!y1Axis.isVisible() || y1Axis.getViewNumber()==0);
      if (xAxis.getPosition() == JLAxis.HORIZONTAL_ORG2)
        xAxis.paintAxis(g, frc, xOrg, yOrg, y2Axis, 0, 0, getBackground(),true);
      else
        xAxis.paintAxis(g, frc, xOrg, yOrg, y1Axis, 0, 0, getBackground(),true);

    }

    redrawPanel(g);

    // Paint swing stuff
    paintComponents(g);
    paintBorder(g);
  }

  // Build a valid rectangle with the given coordinates
  private Rectangle buildRect(int x1, int y1, int x2, int y2) {

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

  // ************************************************************************
  // Mouse Listener
  public void mouseClicked(MouseEvent e) {
  }

  public void mouseDragged(MouseEvent e) {
    if (zoomDrag) {

      // Clear old rectangle
      Rectangle r = buildRect(zoomX, zoomY, lastX, lastY);
      r.width+=1;
      r.height+=1;
      repaint(r);

      // Draw new one
      lastX = e.getX();
      lastY = e.getY();
      r = buildRect(zoomX, zoomY, lastX, lastY);
      r.width+=1;
      r.height+=1;
      repaint(r);

    }
  }

  public void mouseMoved(MouseEvent e) {
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
    if (zoomDrag) {
      Rectangle r = buildRect(zoomX, zoomY, e.getX(), e.getY());
      zoomDrag = false;
      xAxis.zoom(r.x, r.x + r.width);
      y1Axis.zoom(r.y, r.y + r.height);
      y2Axis.zoom(r.y, r.y + r.height);
    }
    ipanelVisible = false;
    repaint();
  }

  public void mousePressed(MouseEvent e) {

    // Left button click
    if (e.getButton() == MouseEvent.BUTTON1) {

      // Zoom management
      if (e.isControlDown() || zoomDragAllowed) {
        zoomDrag = true;
        zoomX = e.getX();
        zoomY = e.getY();
        lastX = e.getX();
        lastY = e.getY();
        return;
      }

      SearchInfo si;
      SearchInfo msi = null;

      // Look for the nearest value on each dataView
      msi = y1Axis.searchNearest(e.getX(), e.getY(), xAxis);
      si = y2Axis.searchNearest(e.getX(), e.getY(), xAxis);
      if (si.found && si.dist < msi.dist) msi = si;

      if (msi.found) {
        Graphics g = getGraphics();
        showPanel(g, msi);
        g.dispose();
        return;
      }

      // Click on label
      int i = 0;
      boolean found = false;
      while (i < labelRect.size() && !found) {
        LabelRect r = (LabelRect) labelRect.get(i);
        found = r.rect.contains(e.getX(), e.getY());
        if (found) {
          //Display the Dataview options
          showDataOptionDialog(r.view);
        }
        i++;
      }

    }

    // Right button click
    if (e.getButton() == MouseEvent.BUTTON3) {
      int i;

      zoomBackMenuItem.setEnabled(isZoomed());
      // Gets user action state
      for (i = 0; i < userActionMenuItem.length; i++) {
        if (userActionMenuItem[i] instanceof JCheckBoxMenuItem) {
          JCheckBoxMenuItem b = (JCheckBoxMenuItem) userActionMenuItem[i];
          b.setSelected(fireGetActionState(b.getText()));
        }
      }

      // Add dataView table item
      tableMenu.removeAll();
      tableMenu.add(tableAllMenuItem);

      // --------
      if (y1Axis.getViewNumber() > 0) tableMenu.add(new JSeparator());
      for (i = 0; i < tableSingleY1MenuItem.length; i++)
        tableSingleY1MenuItem[i].removeActionListener(this);
      tableSingleY1MenuItem = new JMenuItem[y1Axis.getViewNumber()];
      for (i = 0; i < y1Axis.getViewNumber(); i++) {
        tableSingleY1MenuItem[i] = new JMenuItem(y1Axis.getDataView(i).getName());
        tableSingleY1MenuItem[i].addActionListener(this);
        tableMenu.add(tableSingleY1MenuItem[i]);
      }

      // --------
      if (y1Axis.getViewNumber() > 0 && y2Axis.getViewNumber() > 0) tableMenu.add(new JSeparator());
      for (i = 0; i < tableSingleY2MenuItem.length; i++)
        tableSingleY2MenuItem[i].removeActionListener(this);
      tableSingleY2MenuItem = new JMenuItem[y2Axis.getViewNumber()];
      for (i = 0; i < y2Axis.getViewNumber(); i++) {
        tableSingleY2MenuItem[i] = new JMenuItem(y2Axis.getDataView(i).getName());
        tableSingleY2MenuItem[i].addActionListener(this);
        tableMenu.add(tableSingleY2MenuItem[i]);
      }

      // Add dataView option menu
      dvMenu.removeAll();
      for (i = 0; i < dvY1MenuItem.length; i++)
        dvY1MenuItem[i].removeActionListener(this);
      for (i = 0; i < dvY2MenuItem.length; i++)
        dvY2MenuItem[i].removeActionListener(this);

      dvY1MenuItem = new JMenuItem[y1Axis.getViewNumber()];
      dvY2MenuItem = new JMenuItem[y2Axis.getViewNumber()];

      for(i = 0; i<y1Axis.getViewNumber(); i++ ) {
        dvY1MenuItem[i] = new JMenuItem(y1Axis.getDataView(i).getName());
        dvY1MenuItem[i].addActionListener(this);
        dvMenu.add(dvY1MenuItem[i]);
      }
      for(i = 0; i<y2Axis.getViewNumber(); i++ ) {
        dvY2MenuItem[i] = new JMenuItem(y2Axis.getDataView(i).getName());
        dvY2MenuItem[i].addActionListener(this);
        dvMenu.add(dvY2MenuItem[i]);
      }

      chartMenu.show(this, e.getX(), e.getY());
    }

  }

  //****************************************
  // redraw the panel
  private void redrawPanel(Graphics g) {

    if (!ipanelVisible) return;

    // Udpate serachInfo
    Point p;
    JLDataView vy = lastSearch.dataView;
    JLDataView vx = lastSearch.xdataView;
    DataList dy = lastSearch.value;
    DataList dx = lastSearch.xvalue;
    JLAxis yaxis = lastSearch.axis;

    if (xAxis.isXY()) {
      p = yaxis.transform(vx.getTransformedValue(dx.y),
        vy.getTransformedValue(dy.y),
        xAxis);
    } else {
      p = yaxis.transform(dy.x,
        vy.getTransformedValue(dy.y),
        xAxis);
    }

    lastSearch.x = p.x;
    lastSearch.y = p.y;

    showPanel(g, lastSearch);
  }

  private String[] buildPanelString(SearchInfo si) {

    String[] str = null;

    if (xAxis.isXY()) {
      str = new String[4];
      str[0] = si.dataView.getExtendedName() + " " + si.axis.getAxeName();
      str[1] = "Time= " + JLAxis.formatTimeValue(si.value.x);
      str[2] = "X= " + si.xdataView.formatValue(si.xdataView.getTransformedValue(si.xvalue.y));
      str[3] = "Y= " + si.dataView.formatValue(si.dataView.getTransformedValue(si.value.y)) + " " + si.dataView.getUnit();
    } else {
      str = new String[3];
      str[0] = si.dataView.getExtendedName() + " " + si.axis.getAxeName();
      str[1] = "Time= " + JLAxis.formatTimeValue(si.value.x);
      str[2] = "Y= " + si.dataView.formatValue(si.dataView.getTransformedValue(si.value.y)) + " " + si.dataView.getUnit();
    }

    return str;

  }

  /**
   * Display the value tooltip.
   * @param g Graphics object
   * @param si SearchInfo structure.
   * @see JLAxis#searchNearest
   */
  public void showPanel(Graphics g, SearchInfo si) {

    Graphics2D g2 = (Graphics2D) g;
    Rectangle2D bounds;
    int maxh = 0;
    int h = 0;
    int maxw = 0;
    int x0 = 0,y0 = 0;
    String[] str = null;


    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
      RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    FontRenderContext frc = g2.getFontRenderContext();

    g.setPaintMode();
    g.setFont(labelFont);

    if (listener != null) {

      // Call user listener
      JLChartEvent w = new JLChartEvent(this, si);
      str = listener.clickOnChart(w);

    }

    // Default behavior
    if (str == null) str = buildPanelString(si);

    // Do not show panel if no text
    if (str.length <= 0) return;

    // Compute panel size

    bounds = g.getFont().getStringBounds(str[0], frc);
    maxw = (int) bounds.getWidth();
    h = maxh = (int) bounds.getHeight();

    for (int i = 1; i < str.length; i++) {
      bounds = g.getFont().getStringBounds(str[i], frc);
      if ((int) bounds.getWidth() > maxw) maxw = (int) bounds.getWidth();
      maxh += bounds.getHeight();
    }

    maxw += 10;
    maxh += 10;

    g.setColor(Color.black);

    switch (si.placement) {
      case SearchInfo.BOTTOMRIGHT:
        x0 = si.x + 10;
        y0 = si.y + 10;
        g.drawLine(si.x, si.y, si.x + 10, si.y + 10);
        break;
      case SearchInfo.BOTTOMLEFT:
        x0 = si.x - 10 - maxw;
        y0 = si.y + 10;
        g.drawLine(si.x, si.y, si.x - 10, si.y + 10);
        break;
      case SearchInfo.TOPRIGHT:
        x0 = si.x + 10;
        y0 = si.y - 10 - maxh;
        g.drawLine(si.x, si.y, si.x + 10, si.y - 10);
        break;
      case SearchInfo.TOPLEFT:
        x0 = si.x - 10 - maxw;
        y0 = si.y - 10 - maxh;
        g.drawLine(si.x, si.y, si.x - 10, si.y - 10);
        break;
    }

    // Draw panel
    g.setColor(Color.white);
    g.fillRect(x0, y0, maxw, maxh);
    g.setColor(Color.black);
    g.drawRect(x0, y0, maxw, maxh);

    //Draw info
    g.setColor(Color.black);
    for (int i = 0; i < str.length; i++) {
      g.drawString(str[i], x0 + 3, y0 + 3 + (i + 1) * h);
    }

    lastSearch = si;
    ipanelVisible = true;

  }

  //**************************************************
  //

  /**
   * Remove points that exceed displayDuration.
   * @param v DataView containing points
   * @return Number of deleted points
   */
  public int garbageData(JLDataView v) {

    int nb = 0;

    if (displayDuration != Double.POSITIVE_INFINITY) {
      nb = v.garbagePointTime(displayDuration);
    }

    return nb;
  }

  /**
   * Add data to dataview , perform fast update when possible and garbage old data
   * (if a display duration is specified).
   * @param v The dataview
   * @param x x coordinates (real space)
   * @param y y coordinates (real space)
   * @see JLChart#setDisplayDuration
   */
  public void addData(JLDataView v, double x, double y) {

    DataList lv = null;
    boolean need_repaint = false;

    //Get the last value
    if (v.getDataLength() > 0) lv = v.getLastValue();

    //Add data
    v.add(x, y);

    // Garbage
    int nb = garbageData(v);
    if (nb > 0 && v.getAxis() != null) need_repaint = true;

    // Does not repaint if zoom drag
    if (zoomDrag) return;

    if (xAxis.isXY()) {
      // Perform fullupate in XY
      repaint();
      return;
    }

    // Compute update
    JLAxis yaxis = v.getAxis();

    if (yaxis != null) {

      Point lp = null;
      Point p = yaxis.transform(x, v.getTransformedValue(y), xAxis);
      if (lv != null) lp = yaxis.transform(lv.x, v.getTransformedValue(lv.y), xAxis);

      if (yaxis.getBoundRect().contains(p) && !need_repaint) {
        // We can perform fast update
        yaxis.drawFast(getGraphics(), lp, p, v);
      } else {
        // Full update needed
        repaint();
      }

    }

  }

  //****************************************
  // Debug stuff

  public static void main(String args[]) {

    final JFrame f = new JFrame();
    final JLChart chart = new JLChart();
    final JLDataView v1 = new JLDataView();
    final JLDataView v2 = new JLDataView();
    double startTime = (double) ((System.currentTimeMillis() / 1000) * 1000);

    // Initialise chart properties
    chart.setHeaderFont(new Font("Times", Font.BOLD, 18));
    chart.setLabelFont(new Font("Times", Font.BOLD, 12));
    chart.setHeader("Test DataView");

    // Initialise axis properties
    chart.getY1Axis().setName("mA");
    chart.getY1Axis().setAutoScale(true);
    chart.getY2Axis().setName("unit");

    chart.getXAxis().setAutoScale(true);
    chart.getXAxis().setName("Value");
    chart.getXAxis().setGridVisible(true);
    chart.getXAxis().setSubGridVisible(true);
    chart.getXAxis().setAnnotation(JLAxis.VALUE_ANNO);
    chart.getY1Axis().setGridVisible(true);
    chart.getY1Axis().setSubGridVisible(true);
    chart.getY2Axis().setVisible(false);

    // Build dataview
    /*
    v1.add(startTime, -10.0);
    v1.add(startTime + 30007, -15.0);
    v1.add(startTime + 60008, 17.0);
    v1.add(startTime + 90010, 21.0);
    v1.add(startTime + 120147, 22.0);
    v1.add(startTime + 150000, 24.0);
    v1.add(startTime + 180000, 98.0);
    v1.add(startTime + 210000, Double.NaN);
    v1.add(startTime + 240000, 21.0);
    v1.add(startTime + 270000, 99.0);
    v1.add(startTime + 300000, 50.0);
    v1.add(startTime + 330000, 40.0);
    v1.add(startTime + 360000, 30.0);
    v1.add(startTime + 390000, 20.0);
    */

    v1.add(-6, -10.0);
    v1.add(-5, -15.0);
    v1.add(-4, 17.0);
    v1.add(-3, 21.0);
    v1.add(-2, 22.0);
    v1.add(-1, 24.0);
    v1.add(0, 98.0);
    v1.add(1, Double.NaN);
    v1.add(2, 21.0);
    v1.add(3, 99.0);
    v1.add(4, 50.0);
    v1.add(5, 40.0);
    v1.add(6, 30.0);
    v1.add(7, 20.0);

    v1.setMarker(JLDataView.MARKER_CIRCLE);
    v1.setStyle(JLDataView.STYLE_DASH);
    v1.setName("Le signal 1");
    v1.setUnit("std");
    v1.setClickable(true);
    v1.setUserFormat("%5.2f");

    // Add the dataview to the chart
    chart.getY1Axis().addDataView(v1);

    // Build a second dataview
    /*
    v2.add(startTime, -10.0);
    v2.add(startTime + 30000, -5.0);
    v2.add(startTime + 60000, 7.0);
    v2.add(startTime + 90000, 11.0);
    v2.add(startTime + 120000, 12.0);
    v2.add(startTime + 150000, 14.0);
    v2.add(startTime + 180000, 78.0);
    v2.add(startTime + 210000, Double.NaN);
    v2.add(startTime + 240000, 22.0);
    v2.add(startTime + 270000, 55.0);
    v2.add(startTime + 300000, 42.0);
    v2.add(startTime + 330000, 11.0);
    v2.add(startTime + 360000, 47.0);
    v2.add(startTime + 390000, 12.0);
*/
    v2.add(-6, -10.0);
    v2.add(-5, -5.0);
    v2.add(-4, 7.0);
    v2.add(-3, 11.0);
    v2.add(-2, 12.0);
    v2.add(-1, 14.0);
    v2.add(0, 78.0);
    v2.add(1, Double.NaN);
    v2.add(2, 22.0);
    v2.add(3, 55.0);
    v2.add(4, 42.0);
    v2.add(5, 11.0);
    v2.add(6, 47.0);
    v2.add(7, 12.0);

    v2.setName("Le signal 2");
    v2.setUnit("std");
    v2.setColor(Color.blue);
    v2.setLineWidth(3);
    v2.setFillColor(Color.orange);
    v2.setFillStyle(JLDataView.FILL_STYLE_SOLID);
    v2.setViewType(JLDataView.TYPE_BAR);

    // Add it to the chart
    chart.getY2Axis().addDataView(v2);

    JPanel bot = new JPanel();
    bot.setLayout(new FlowLayout());

    JButton b = new JButton("Exit");
    b.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        System.exit(0);
      }
    });

    bot.add(b);

    JButton c = new JButton("Options");
    c.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        chart.showOptionDialog();
      }
    });

    bot.add(c);

    f.getContentPane().setLayout(new BorderLayout());
    f.getContentPane().add(chart, BorderLayout.CENTER);
    f.getContentPane().add(bot, BorderLayout.SOUTH);
    f.setSize(800, 600);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);

  }

}
