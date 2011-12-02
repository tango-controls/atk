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
 
/*
 * Trend.java
 *
 * Created on May 13, 2002, 4:28 PM
 */

package fr.esrf.tangoatk.widget.attribute;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.*;
import fr.esrf.tangoatk.widget.util.chart.*;

/**
 *
 * @author  pons
 */

/** A class to monitor multiple scalar attributes. */
public class Trend extends JPanel implements IControlee, ActionListener, IJLChartActionListener, IRefresherListener, IListStateListener {

  // Constant

  /** Not selected */
  public static final int SEL_NONE = 0;
  /** Selected on X */
  public static final int SEL_X = 1;
  /** Selected on Y1 */
  public static final int SEL_Y1 = 2;
  /** Selected on Y2 */
  public static final int SEL_Y2 = 3;

  /** Device name are displayed whithin chart label when needed */
  public final static int DEVICE_LABEL_AUTO=0;
  /** Device name are never displayed whithin chart label */
  public final static int DEVICE_LABEL_NEVER=1;
  /** Device name are always displayed whithin chart label */
  public final static int DEVICE_LABEL_ALWAYS=2;

  //Default Color
  protected static Color[] defaultColor = {
    Color.red,
    Color.blue,
    Color.cyan,
    Color.green,
    Color.magenta,
    Color.orange,
    Color.pink,
    Color.yellow,
    Color.black};

  // Local declaration
  protected JFrame parent = null;

  // Toolbar stuff
  protected JToolBar theToolBar;
  protected JPopupMenu toolMenu;

  protected JButton optionButton;
  protected JMenuItem optionMenuI;
  protected JButton startStopButton;
  protected ImageIcon startIcon;
  protected ImageIcon stopIcon;
  protected JMenuItem startStopMenuI;
  protected JButton loadButton;
  protected JMenuItem loadMenuI;
  protected JButton saveButton;
  protected JMenuItem saveMenuI;
  protected JButton zoomButton;
  protected JMenuItem zoomMenuI;
  protected JButton timeButton;
  protected JMenuItem timeMenuI;
  protected JButton cfgButton;
  protected JMenuItem cfgMenuI;
  protected JButton resetButton;
  protected JMenuItem resetMenuI;
  protected JCheckBoxMenuItem offLineButton;
  protected JMenuItem showErrorMenuI;
  protected JMenuItem showDiagMenuI;

  protected JMenuItem showtoolMenuI;

  protected JPanel innerPanel;

  protected JLabel dateLabel;

  // Selection tree stuff
  protected JScrollPane treeView = null;
  protected JTree mainTree = null;
  protected DefaultTreeModel mainTreeModel = null;
  protected TrendSelectionNode rootNode = null;
  protected JPopupMenu treeMenu;
  protected JMenuItem addXMenuItem;
  protected JMenuItem addY1MenuItem;
  protected JMenuItem addY2MenuItem;
  protected JCheckBoxMenuItem showMinAlarmMenuItem;
  protected JCheckBoxMenuItem showMaxAlarmMenuItem;
  protected JMenuItem removeMenuItem;
  protected JMenuItem optionMenuItem;
  protected JMenuItem attOptionMenuItem;


  // Chart stuff
  protected JLChart theGraph;
  private String graphTitle="";
  private ConfigPanel cfgPanel = null;
  static private Point framePos=new Point(0,0);
  static private Point frameDimension=new Point(640,480);


  // The models
  protected AttributePolledList attList = null;
  private TrendSelectionNode lastAdded = null;
  private AttributePolledList lastCreatedList = null;

  private SimplePropertyFrame propFrame=null;

  private String lastConfig = "";
  private boolean singleDevice=true;
  private int isShowingDeviceName=DEVICE_LABEL_AUTO;

  static final java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
  static final java.text.SimpleDateFormat genFormat = new java.text.SimpleDateFormat("EEE dd/MM/yy HH:mm:ss");

  protected Map buttonMap;
  
  /**
   * Corresponds to the button "start/stop monitoring"
   */
  public static final String startStop  = "STARTSTOP";

  /**
   * Corresponds to the button "Save configuration"
   */
  public static final String save   = "SAVE";
  
  
  /**
   * Corresponds to the button "Load configuration"
   */
  public static final String load   = "LOAD";
  
  
  /**
   * Corresponds to the button "Zoom"
   */
  public static final String zoom   = "ZOOM";
  
  
  /**
   * Corresponds to the button "Set rfresh interval"
   */
  public static final String time   = "TIME";
  
  
  /**
   * Corresponds to the button "Global settings"
   */
  public static final String option = "OPTION";

  /**
   * Corresponds to the button "Add new attribute"
   */
  public static final String config = "CONFIG";

  /**
   * Corresponds to the button "Reset trend"
   */
  public static final String reset = "RESET";
  
  private int timePrecision = 0;

  protected int minRefreshInterval = 0;
  
  //Seperate Trend management   
  private boolean manageIntervalTrend = false;
  
  private long currentTime = 0;
  private long oldCurrentTime = 0; 
 
  protected JToolBar panelToolBarTrend;
  protected JToolBar panelToolBar;    
  protected JButton timeButtonTrend;
  protected JButton refreshButton;
  protected JMenuItem timeMenuTrendI; 
  protected JMenuItem refreshMenuI;
  
  protected int minRefreshTrendInterval = 0;  
  private int refreshIntervalTrend = 1000;

  protected boolean offLineMode = false;

  protected ErrorHistory errWin;


  /**
   * Trend constructor.
   * @param parent Parent frame
   */
  public Trend(JFrame parent) {
    this();
    this.parent = parent;
    theGraph.setFrameParent(parent);
  }

  /**
   * Default constructor.
   */
  public Trend() {

    buttonMap = new HashMap();
      
    theToolBar = new JToolBar();
    toolMenu = new JPopupMenu();

    optionButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_settings.gif")));
    optionButton.setToolTipText("Global settings");
    optionMenuI = new JMenuItem("Global settings");

    stopIcon = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_stop.gif"));
    startIcon = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_start.gif"));

    startStopButton = new JButton(startIcon);
    startStopButton.setToolTipText("Start monitoring");
    startStopMenuI = new JMenuItem("Start monitoring");
    startStopMenuI.addActionListener(this);

    loadButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_load.gif")));
    loadButton.setToolTipText("Load configuration");
    loadMenuI = new JMenuItem("Load configuration");

    saveButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_save.gif")));
    saveButton.setToolTipText("Save configuration");
    saveMenuI = new JMenuItem("Save configuration");

    zoomButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_zoom.gif")));
    zoomButton.setToolTipText("Zoom");
    zoomMenuI = new JMenuItem("Zoom");

    timeButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_time.gif")));
    timeButton.setToolTipText("Set refresh interval");
    timeMenuI = new JMenuItem("Set refresh interval");

    cfgButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_config.gif")));
    cfgButton.setToolTipText("Add new attribute");
    cfgMenuI = new JMenuItem("Add new attribute");

    resetButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_reset.gif")));
    resetButton.setToolTipText("Reset trend");
    resetMenuI = new JMenuItem("Reset trend");

    showtoolMenuI = new JMenuItem("Hide toolbar");

    offLineButton = new JCheckBoxMenuItem("Off line mode");

    showErrorMenuI = new JMenuItem("View errors");
    showDiagMenuI = new JMenuItem("Diagnostic");

    theToolBar.setFloatable(true);

    loadButton.addActionListener(this);
    loadMenuI.addActionListener(this);
    saveButton.addActionListener(this);
    saveMenuI.addActionListener(this);
    optionButton.addActionListener(this);
    optionMenuI.addActionListener(this);
    zoomButton.addActionListener(this);
    zoomMenuI.addActionListener(this);
    startStopButton.addActionListener(this);
    timeButton.addActionListener(this);
    timeMenuI.addActionListener(this);
    cfgButton.addActionListener(this);
    cfgMenuI.addActionListener(this);
    resetButton.addActionListener(this);
    resetMenuI.addActionListener(this);
    showtoolMenuI.addActionListener(this);
    offLineButton.addActionListener(this);
    showErrorMenuI.addActionListener(this);
    showDiagMenuI.addActionListener(this);

    theToolBar.add(loadButton);
    theToolBar.add(saveButton);
    theToolBar.add(optionButton);
    theToolBar.add(zoomButton);
    theToolBar.add(startStopButton);
    theToolBar.add(timeButton);
    theToolBar.add(cfgButton);
    theToolBar.add(resetButton);

    buttonMap.put(load,loadButton);
    buttonMap.put(save,saveButton);
    buttonMap.put(option,optionButton);
    buttonMap.put(zoom,zoomButton);
    buttonMap.put(startStop,startStopButton);
    buttonMap.put(time,timeButton);
    buttonMap.put(config,cfgButton);
    buttonMap.put(reset,resetButton);

    toolMenu.add(loadMenuI);
    toolMenu.add(saveMenuI);
    toolMenu.add(optionMenuI);
    toolMenu.add(zoomMenuI);
    toolMenu.add(startStopMenuI);
    toolMenu.add(timeMenuI);
    toolMenu.add(cfgMenuI);
    toolMenu.add(resetMenuI);
    toolMenu.add(showtoolMenuI);
    toolMenu.add(offLineButton);
    toolMenu.add(showErrorMenuI);
    toolMenu.add(showDiagMenuI);

    // Create the graph
    theGraph = new JLChart();
    theGraph.setBorder(new javax.swing.border.EtchedBorder());
    theGraph.setBackground(new java.awt.Color(180, 180, 180));
    theGraph.getY1Axis().setAutoScale(true);
    theGraph.getY2Axis().setAutoScale(true);
    theGraph.getXAxis().setAutoScale(true);
    theGraph.setDisplayDuration(300000.0); // 5min
    theGraph.addUserAction("chkShow toolbar");
    theGraph.addUserAction("chkShow selection tree");
    theGraph.addUserAction("chkShow date");
    theGraph.addUserAction("Load configuration");
    theGraph.addUserAction("Save configuration");
    theGraph.addUserAction("View errors");
    theGraph.addUserAction("Diagnostic");
    theGraph.addJLChartActionListener(this);
    // Commented revision 1.43 modifications :
    // refuse displayDuration greater than 1 day, in order to limit memory use
    //theGraph.setMaxDisplayDuration(24 * 60 * 60 * 1000);

    innerPanel = new JPanel();
    innerPanel.setLayout(new BorderLayout());

    setLayout(new BorderLayout());
    add(theToolBar, BorderLayout.NORTH);
    innerPanel.add(theGraph, BorderLayout.CENTER);
    add(innerPanel, BorderLayout.CENTER);

    // Create the tree popup menu
    treeMenu = new JPopupMenu();
    addXMenuItem = new JMenuItem("Set to X");
    addY1MenuItem = new JMenuItem("Add to Y1");
    addY2MenuItem = new JMenuItem("Add to Y2");
    removeMenuItem = new JMenuItem("Remove");
    showMinAlarmMenuItem = new JCheckBoxMenuItem("Show min alarm");
    showMaxAlarmMenuItem = new JCheckBoxMenuItem("Show max alarm");
    optionMenuItem = new JMenuItem("Graphic properties");
    attOptionMenuItem = new JMenuItem("Attribute properties");
    treeMenu.add(addXMenuItem);
    treeMenu.add(addY1MenuItem);
    treeMenu.add(addY2MenuItem);
    treeMenu.add(removeMenuItem);
    treeMenu.add(showMinAlarmMenuItem);
    treeMenu.add(showMaxAlarmMenuItem);
    treeMenu.add(optionMenuItem);
    treeMenu.add(attOptionMenuItem);
    
    setManageIntervalTrend();
    if(manageIntervalTrend)
        initTrendRefresher();

    addXMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        TrendSelectionNode selNode = (TrendSelectionNode) mainTree.getSelectionPath().getLastPathComponent();
        INumberScalar m = selNode.getModel();
        if (m != null) {

          // Remove X view (Only one view on X)
          int i = 0;
          boolean found = false;
          Vector dv = rootNode.getSelectableItems();
          TrendSelectionNode n = null;
          while (!found && i < dv.size()) {
            n = (TrendSelectionNode) dv.get(i);
            found = (n.getSelected() == SEL_X);
            if (!found) i++;
          }
          if (found) n.setSelected(SEL_NONE);

          // Select new view
          selNode.setSelected(SEL_X);
          mainTree.repaint();
          theGraph.repaint();
        }
      }
    });


    addY1MenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        TreePath[] selPaths = mainTree.getSelectionPaths();
        for (int i = 0; i < selPaths.length; i++) {
          TrendSelectionNode selNode = (TrendSelectionNode) selPaths[i].getLastPathComponent();
          if (selNode.hasModel() && selNode.getSelected() != SEL_Y1) {
            selNode.setSelected(SEL_Y1);
          }
        }
        mainTree.repaint();
        theGraph.repaint();
      }
    });

    addY2MenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        TreePath[] selPaths = mainTree.getSelectionPaths();
        for (int i = 0; i < selPaths.length; i++) {
          TrendSelectionNode selNode = (TrendSelectionNode) selPaths[i].getLastPathComponent();
          if (selNode.hasModel() && selNode.getSelected() != SEL_Y2) {
            selNode.setSelected(SEL_Y2);
          }
        }
        mainTree.repaint();
        theGraph.repaint();
      }
    });

    removeMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        TreePath[] selPaths = mainTree.getSelectionPaths();
        for (int i = 0; i < selPaths.length; i++) {
          TrendSelectionNode selNode = (TrendSelectionNode) selPaths[i].getLastPathComponent();
          if (selNode.hasModel() && selNode.getSelected() != SEL_NONE) {
            selNode.setSelected(SEL_NONE);
          }
        }
        mainTree.repaint();
        theGraph.repaint();
      }
    });

    showMinAlarmMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        TreePath[] selPaths = mainTree.getSelectionPaths();
        for (int i = 0; i < selPaths.length; i++) {
          TrendSelectionNode selNode = (TrendSelectionNode) selPaths[i].getLastPathComponent();
          if (selNode.getModel() != null) {
            if(showMinAlarmMenuItem.isSelected())
              selNode.showMinAlarm();
            else
              selNode.hideMinAlarm();
          }
        }
        mainTree.repaint();
        theGraph.repaint();
      }
    });

    showMaxAlarmMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        TreePath[] selPaths = mainTree.getSelectionPaths();
        for (int i = 0; i < selPaths.length; i++) {
          TrendSelectionNode selNode = (TrendSelectionNode) selPaths[i].getLastPathComponent();
          if (selNode.getModel() != null) {
            if(showMaxAlarmMenuItem.isSelected())
              selNode.showMaxAlarm();
            else
              selNode.hideMaxAlarm();
          }
        }
        mainTree.repaint();
        theGraph.repaint();
      }
    });

    optionMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        TrendSelectionNode selNode = (TrendSelectionNode) mainTree.getSelectionPath().getLastPathComponent();
        if (selNode.hasModel()) {
          selNode.showOptions();
        }
      }
    });

    attOptionMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        TrendSelectionNode selNode = (TrendSelectionNode) mainTree.getSelectionPath().getLastPathComponent();
        IAttribute m = selNode.getModel();
        if(m==null) m = selNode.getBooleanModel();
        if (m != null) {
          if(propFrame==null)
            propFrame = new SimplePropertyFrame();
          propFrame.setModel(m);
          propFrame.setVisible(true);
        }
      }
    });

    calendar.setTimeInMillis(System.currentTimeMillis());
    dateLabel = new JLabel();
    dateLabel.setText(genFormat.format(calendar.getTime()));
    dateLabel.setHorizontalAlignment(JLabel.CENTER);
    dateLabel.setVisible(false);
    add(dateLabel,BorderLayout.SOUTH);

    errWin = new ErrorHistory();

  }

  void refreshNode(TrendSelectionNode n) {
    theGraph.repaint();
    mainTreeModel.nodeChanged(n);
  }
  
  private void initTrendRefresher()
  {
  	panelToolBar = new JToolBar();
    panelToolBarTrend = new JToolBar();
    
    timeButtonTrend = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_refresh_time.png")));
    timeButtonTrend.setToolTipText("Set refresh interval Trend");
    timeMenuTrendI = new JMenuItem("Set refresh interval Trend");
    	
    refreshButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_refresh.png")));
    refreshButton.setToolTipText("Refresh trend");
    refreshMenuI = new JMenuItem("Refresh trend");
    	
    timeButtonTrend.addActionListener(this);
    timeMenuTrendI.addActionListener(this);    	
    refreshButton.addActionListener(this);
    	
    panelToolBar.setFloatable(false);
    panelToolBarTrend.setFloatable(false);
    
    panelToolBar.add(loadButton);
    panelToolBar.add(saveButton);
    panelToolBar.add(optionButton);
    panelToolBar.add(zoomButton);
    panelToolBar.add(startStopButton);
    panelToolBar.add(timeButton);
    panelToolBar.add(cfgButton);
    panelToolBar.add(resetButton);
    panelToolBarTrend.add(timeButtonTrend);
    panelToolBarTrend.add(refreshButton);
          
    theToolBar.setLayout(new BorderLayout());
    theToolBar.add(panelToolBar,BorderLayout.WEST);
    
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new BorderLayout());
    jPanel.add(panelToolBarTrend,BorderLayout.CENTER);
    theToolBar.add(jPanel,BorderLayout.EAST);
    
    buttonMap.put(reset,refreshButton);
    toolMenu.add(timeMenuTrendI);
    
  }

  // -------------------------------------------------------------
  // Action listener
  // -------------------------------------------------------------
  public void actionPerformed(ActionEvent evt) {

    Object o = evt.getSource();
    if (o == optionButton || o == optionMenuI) {
      optionButtonActionPerformed();
    } else if (o == startStopButton || o == startStopMenuI) {
      if( attList!=null ) {
        if( attList.isRefresherStarted() ) {
          attList.stopRefresher();
        } else {
          attList.startRefresher();
        }
      }
    } else if (o == loadButton || o == loadMenuI) {
      loadButtonActionPerformed();
    } else if (o == saveButton || o == saveMenuI) {
      saveButtonActionPerformed();
    } else if (o == zoomButton || o == zoomMenuI) {
      if (!theGraph.isZoomed())
        theGraph.enterZoom();
      else
        theGraph.exitZoom();
    } else if (o == timeButton || o == timeMenuI) {
      setRefreshInterval();
    } else if (o == cfgButton || o == cfgMenuI) {
      if(cfgPanel == null) {
        Window w = ATKGraphicsUtils.getWindowForComponent(this);
        if(w instanceof Frame)
          cfgPanel = new ConfigPanel((Frame)w,this);
        else if (w instanceof Dialog)
          cfgPanel = new ConfigPanel((Dialog)w,this);
        else
          cfgPanel = new ConfigPanel((Frame)null,this);
      }
      cfgPanel.showPanel();
    } else if (o == showtoolMenuI) {
      boolean b = isButtonBarVisible();
      b = !b;
      setButtonBarVisible(b);
    } else if (o == resetButton || o == resetMenuI) {
      resetTrend();
    } else if (o == refreshButton || o == refreshButton) {
      refreshTrend();
    } else if (o == timeButtonTrend || o == timeMenuTrendI) {
      setRefreshIntervalTrend();
    } else if (o == offLineButton) {
      setOffLineMode(offLineButton.isSelected());
    } else if (o == showErrorMenuI) {
      ATKGraphicsUtils.centerFrameOnScreen(errWin);
      errWin.setVisible(true);      
    } else if (o == showDiagMenuI) {
      fr.esrf.tangoatk.widget.util.ATKDiagnostic.showDiagnostic();
    }

  }

  // -------------------------------------------------------------
  // JLChart action listener
  // -------------------------------------------------------------
  public void actionPerformed(JLChartActionEvent evt) {

    if(evt.getName().equals("Show toolbar")) {
      setButtonBarVisible(evt.getState());
    } else if (evt.getName().equals("Show selection tree")) {
      setSelectionTreeVisible(evt.getState());
    } else if (evt.getName().equals("Show date")) {
      setDateVisible(evt.getState());
    } else if (evt.getName().equalsIgnoreCase("Load configuration")) {
      loadButtonActionPerformed();
    } else if (evt.getName().equalsIgnoreCase("Save configuration")) {
      saveButtonActionPerformed();
    } else if (evt.getName().equalsIgnoreCase("View errors")) {
      ATKGraphicsUtils.centerFrameOnScreen(errWin);
      errWin.setVisible(true);
    } else if (evt.getName().equalsIgnoreCase("Diagnostic")) {
      fr.esrf.tangoatk.widget.util.ATKDiagnostic.showDiagnostic();
    }

  }

  public boolean getActionState(JLChartActionEvent evt) {

    if(evt.getName().equals("Show toolbar")) {
      return isButtonBarVisible();
    } else if (evt.getName().equals("Show selection tree")) {
      return isSelectionTreeVisible();
    } else if (evt.getName().equals("Show date")) {
      return isDateVisible();
    }

    return false;
  }

public int getTimePrecision() {
    return timePrecision;
}
public void setTimePrecision(int timePrecision) {
    this.timePrecision = timePrecision;
    if(theGraph != null)
        theGraph.setTimePrecision(timePrecision);
}
  // -------------------------------------------------------------
  // Refresher listener
  // -------------------------------------------------------------
  public void refreshStep() {

    if (isDateVisible()) {
      calendar.setTimeInMillis(System.currentTimeMillis());
      dateLabel.setText(genFormat.format(calendar.getTime()));
    }

    if (theGraph.getXAxis().getPercentScrollback() == 0.0) {

      if (!offLineMode) {
        // All attribute has been read, we can repaint the graph
        if (!manageIntervalTrend)
          theGraph.repaint();
        else {
          // repaint Trend after IntervalTrend time
          currentTime = System.currentTimeMillis();
          if (currentTime - oldCurrentTime > getRefreshIntervalTrend()) {
            theGraph.repaint();
            oldCurrentTime = currentTime;
          }
        }
      }

    }

  }

  // -------------------------------------------------------------
  // ListState listener
  // -------------------------------------------------------------
  public void stateChange(int state) {

    updateStartStopButton();

  }

  /**
   * Sets or unset the offline mode (data are updated but not painted)
   * @param mode Offline mode
   */
  public void setOffLineMode(boolean mode) {
    offLineMode = mode;
  }

  private void setRefreshInterval() {

    int old_it = attList.getRefreshInterval();
    String i = JOptionPane.showInputDialog(this, "Enter refresh interval (ms)", new Integer(old_it));
    if (i != null) {
      try {
        int it = Integer.parseInt(i);
        if (it < getMinRefreshInterval()) {
          JOptionPane.showMessageDialog(
                  parent,
                  "Invalid number ! Can not be less than "
                    + getMinRefreshInterval(),
                  "Error",
                  JOptionPane.ERROR_MESSAGE
          );
          return;
        }
        attList.setRefreshInterval(it);
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(parent, "Invalid number !", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }

  }
  
  private void setRefreshIntervalTrend() {
        int old_it = getRefreshIntervalTrend();
        String i = JOptionPane.showInputDialog(this, "Enter refresh interval Trend (ms)", new Integer(old_it));
        if (i != null) {
          try {
            int it = Integer.parseInt(i);
            if (it < getMinRefreshInterval()) {
              JOptionPane.showMessageDialog(
                      parent,
                      "Invalid number ! Can not be less than " + getMinRefreshInterval(),
                      "Error",
                      JOptionPane.ERROR_MESSAGE
              );
              return;
            }
            if (it < attList.getRefreshInterval() || it % attList.getRefreshInterval() != 0) {
                JOptionPane.showMessageDialog(parent, "Invalid values !" +
                        "\nThe value must be greater and multiple than the refresh interval Values " +
                        attList.getRefreshInterval(), "Error", JOptionPane.ERROR_MESSAGE);
            }else{
                setRefreshIntervalTrend(it);               
            }
          } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent, "Invalid number !", "Error", JOptionPane.ERROR_MESSAGE);
          }
        }
      }

  private void updateModel() {
    singleDevice = true;
    if( attList!=null ) {
      int i;

      // Check if we have a single device
      int sz = attList.size();
      if( sz>1 ) {
        IAttribute s = (IAttribute)attList.get(0);
        String dName = s.getDevice().getName();
        for(i=1;i<sz && singleDevice;i++) {
          s = (IAttribute)attList.get(i);
          singleDevice=dName.equalsIgnoreCase(s.getDevice().getName());
        }
      }

      // Update nodes
      Vector v = rootNode.getSelectableItems();
      for(i=0;i<v.size();i++) ((TrendSelectionNode)v.get(i)).refreshNode();

    }
  }

  private void updateStartStopButton() {

    if( attList.isRefresherStarted() ) {
      startStopButton.setIcon(stopIcon);
      startStopButton.setToolTipText("Stop monitoring");
      startStopMenuI.setText("Stop monitoring");
    } else {
      startStopButton.setIcon(startIcon);
      startStopButton.setToolTipText("Start monitoring");
      startStopMenuI.setText("Start monitoring");
    }

  }

  /**
   * Free any allocated resource and stop refreshing.
   * The internal attribute list is released if it has
   * been created using addAttribute() or setSettings().
   */
  public void clearModel() {
    setModel((AttributePolledList)null);
  }

  /**
   * This <code>setModel</code> which takes an AttributePolledList as a
   * parameter, will just add the attributes in the list to the list
   * viewer in the Trend. It will not add any of the attributes to the
   * Trend
   * @param list a <code>AttributePolledList</code> value
   */
  public void setModel(AttributePolledList list) {
    int i;

    // Free old allocated resource ----------------------------------------------------

    theGraph.unselectAll();

    // Remove old listeners and clean former list
    if (attList != null)
    {
        attList.removeErrorListener(errWin);
        attList.removeRefresherListener(this);
        attList.removeListStateListener(this);
    }

    if( rootNode!=null ) {
      Vector dv = rootNode.getSelectableItems();
      TrendSelectionNode n;
      for(i=0;i<dv.size();i++) {
        n = (TrendSelectionNode)dv.get(i);
        n.clearModel();
      }
    }

    // Stop refresher on list created by the Trend and clean this list
    if (lastCreatedList != null) {
      lastCreatedList.stopRefresher();
      lastCreatedList.clear();
      lastCreatedList=null;
    }
    
    if( treeView != null ) {
      innerPanel.remove(treeView);
      treeView=null;
    }

    // Create the selection tree -------------------------------------------------------
    rootNode = new TrendSelectionNode(this);
    int j;
    if (list != null) {
      for (i = 0; i < list.size(); i++) {

        if ( list.get(i) instanceof INumberScalar ) {
          j = i;
          lastAdded = rootNode.addItem( this, (INumberScalar) list.get(j), defaultColor[j % defaultColor.length] );
        }

        if( list.get(i) instanceof IBooleanScalar ) {
          j = i;
          lastAdded = rootNode.addItem( this, (IBooleanScalar) list.get(j), defaultColor[j % defaultColor.length] );
        }

      }
    }

    TrendRenderer renderer = new TrendRenderer();

    mainTreeModel = new DefaultTreeModel(rootNode);
    mainTree = new JTree(mainTreeModel);
    mainTree.setCellRenderer(renderer);
    mainTree.setEditable(false);
    mainTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    mainTree.setShowsRootHandles(true);
    mainTree.setRootVisible(true);
    mainTree.setBorder(BorderFactory.createLoweredBevelBorder());
    treeView = new JScrollPane(mainTree);
    mainTree.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        revalidate();
        int selRow = mainTree.getRowForLocation(e.getX(), e.getY());
        TreePath[] selPaths = mainTree.getSelectionPaths();
        if (selRow != -1) {
          if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
            if (selPaths != null && selPaths.length > 0) {
              if (selPaths.length == 1) {
                TrendSelectionNode selNode = (TrendSelectionNode) selPaths[0].getLastPathComponent();
                if (selNode.hasModel()) {
                  addXMenuItem.setEnabled(selNode.getSelected() != SEL_X);
                  addY1MenuItem.setEnabled(selNode.getSelected() != SEL_Y1);
                  addY2MenuItem.setEnabled(selNode.getSelected() != SEL_Y2);
                  removeMenuItem.setEnabled(selNode.getSelected() != SEL_NONE);
                  showMinAlarmMenuItem.setSelected(selNode.isShowingMinAlarm());
                  showMaxAlarmMenuItem.setSelected(selNode.isShowingMaxAlarm());
                  treeMenu.show(mainTree, e.getX(), e.getY());
                } else if (selNode == rootNode) {

                  if (isButtonBarVisible())
                    showtoolMenuI.setText("Hide toolbar");
                  else
                    showtoolMenuI.setText("Show toolbar");

                  toolMenu.show(mainTree, e.getX(), e.getY());
                }
              }
              else {
                TrendSelectionNode lastAttributeNode = null;
                int attributeCount = 0;
                boolean containsRootNode = false;
                for (int i = 0; i < selPaths.length; i++) {
                  TrendSelectionNode selNode = (TrendSelectionNode) selPaths[i].getLastPathComponent();
                  if (selNode.getModel() != null) {
                    attributeCount++;
                    lastAttributeNode = selNode;
                  }
                  else if (selNode == rootNode) {
                      containsRootNode = true;
                  }
                }
                if (attributeCount > 0) {
                  if (attributeCount == 1) {
                    addXMenuItem.setEnabled(lastAttributeNode.getSelected() != SEL_X);
                    addY1MenuItem.setEnabled(lastAttributeNode.getSelected() != SEL_Y1);
                    addY2MenuItem.setEnabled(lastAttributeNode.getSelected() != SEL_Y2);
                    removeMenuItem.setEnabled(lastAttributeNode.getSelected() != SEL_NONE);
                    showMinAlarmMenuItem.setSelected(lastAttributeNode.isShowingMinAlarm());
                    showMaxAlarmMenuItem.setSelected(lastAttributeNode.isShowingMaxAlarm());
                  } else {
                    addXMenuItem.setEnabled(false);
                    addY1MenuItem.setEnabled(true);
                    addY2MenuItem.setEnabled(true);
                    removeMenuItem.setEnabled(true);
                    showMinAlarmMenuItem.setSelected(false);
                    showMaxAlarmMenuItem.setSelected(false);
                  }
                  treeMenu.show(mainTree, e.getX(), e.getY());
                  lastAttributeNode = null;
                }
                else if (containsRootNode){
                  if (isButtonBarVisible())
                    showtoolMenuI.setText("Hide toolbar");
                  else
                    showtoolMenuI.setText("Show toolbar");

                  toolMenu.show(mainTree, e.getX(), e.getY());
                }
              }
            }
          }
        }
      }
    });

    //mainTree.addTreeSelectionListener(treeSelectionlistemner);
    innerPanel.add(treeView, BorderLayout.WEST);
    innerPanel.revalidate();

    attList = list;
    if(attList!=null) {
      attList.addRefresherListener(this);
      attList.addListStateListener(this);
      attList.addErrorListener(errWin);
    }

    updateStartStopButton();
    updateModel();
  }

  /**
   * <code>addAttribute</code> will add the INumberScalar to the
   * Trend. Additional calls to addAttribute will add more INumberScalars
   * to the trend.
   * @param name Attribute name
   */
  public void addAttribute(String name) {
    INumberScalar scalar;
    IBooleanScalar bscalar;
    AttributePolledList alist;

    // Add the attribute in the list
    try {

      if (attList == null) {
        alist = new AttributePolledList();
        alist.add(name);
        setModel(alist);
        lastCreatedList = alist;
        alist.setRefreshInterval(1000);
        alist.startRefresher();
      } else {
        if (attList.get(name)==null) {
          IAttribute att = (IAttribute) attList.add(name);
          int i = attList.size();

          if( att instanceof IBooleanScalar ) {
	          bscalar = (IBooleanScalar) attList.add(name);
	          lastAdded = rootNode.addItem(this, bscalar, defaultColor[i % defaultColor.length]);
          } else {
            scalar = (INumberScalar) attList.add(name);
            lastAdded = rootNode.addItem(this, scalar, defaultColor[i % defaultColor.length]);
          }

	        mainTreeModel = new DefaultTreeModel(rootNode);
	        mainTree.setModel(mainTreeModel);
        }
      }

      TreePath np = new TreePath(lastAdded.getPath());
      mainTree.setSelectionPath(np);
      mainTree.expandPath(np);
      mainTree.makeVisible(np);

    } catch (ConnectionException e) {
      ;
    }

    updateModel();
    theGraph.repaint();
    innerPanel.revalidate();

  }

  public void addAttribute(INumberScalar scalar) {

    AttributePolledList alist;

    // Add the attribute in the list
    if (attList == null) {
      alist = new AttributePolledList();
      alist.add(scalar);
      setModel(alist);
      lastCreatedList = alist;
      alist.setRefreshInterval(1000);
      alist.startRefresher();
    } else {
      if (!attList.contains(scalar)) {
        attList.add(scalar);
        int i = attList.size();
        lastAdded = rootNode.addItem(this, scalar, defaultColor[i % defaultColor.length]);
        mainTreeModel = new DefaultTreeModel(rootNode);
        mainTree.setModel(mainTreeModel);
      }
    }

    TreePath np = new TreePath(lastAdded.getPath());
    mainTree.setSelectionPath(np);
    mainTree.expandPath(np);
    mainTree.makeVisible(np);
    updateModel();// RG comment : I made this addition to correct the Mantis bug 495
    theGraph.repaint();
    innerPanel.revalidate();

  }

  public void removeAttribute(INumberScalar scalar) {

    lastAdded = null;
    if (attList.contains(scalar)) {
      System.out.println("Removing " + scalar.getName());
      rootNode.delItem(scalar);
      attList.remove(scalar.getName());
      mainTreeModel = new DefaultTreeModel(rootNode);
      mainTree.setModel(mainTreeModel);
      innerPanel.revalidate();
      updateModel();
    }

  }


  /**
   * <code>setModel</code>
   *
   * @param scalar a <code>fr.esrf.tangoatk.core.INumberScalar</code> value
   * @deprecated use addAttribute instead.
   */
  public void setModel(INumberScalar scalar) {
    throw new IllegalStateException
            ("Please use addAttribute() instead of setModel() ");
  }

  public AttributePolledList getModel() {
    return attList;
  }

  private void optionButtonActionPerformed() {
    theGraph.showOptionDialog();
  }

  private void saveButtonActionPerformed() {

    int ok = JOptionPane.YES_OPTION;
    JFileChooser chooser = new JFileChooser(".");
    chooser.addChoosableFileFilter(new MultiExtFileFilter("Text files", "txt"));
    if(lastConfig.length()>0)
      chooser.setSelectedFile(new File(lastConfig));
    int returnVal = chooser.showSaveDialog(parent);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      if (f != null) {
        if (MultiExtFileFilter.getExtension(f) == null) {
  		  f = new File(f.getAbsolutePath() + ".txt");
        }
        if (f.exists())
          ok = JOptionPane.showConfirmDialog(parent, "Do you want to overwrite " + f.getName() + " ?",
                  "Confirm overwrite", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
          saveSetting(f.getAbsolutePath());
        }
      }
    }

  }

  private void loadButtonActionPerformed() {

    int ok = JOptionPane.YES_OPTION;
    JFileChooser chooser = new JFileChooser(".");
    chooser.addChoosableFileFilter(new MultiExtFileFilter("Text files", "txt"));
    if(lastConfig.length()>0)
      chooser.setSelectedFile(new File(lastConfig));
    int returnVal = chooser.showOpenDialog(parent);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      if (f != null) {
        if (ok == JOptionPane.YES_OPTION) {
          String err = loadSetting(f.getAbsolutePath());
          if (err.length() > 0) {
            JOptionPane.showMessageDialog(parent, err, "Errors reading " + f.getName(), JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    }

  }

  /** returns the configuration as string.
    * @see #setSetting
    */
  public String getSettings() {

    int i;
    String to_write = "";

    // General settings
    to_write += theGraph.getConfiguration();
    to_write += "toolbar_visible:" + isButtonBarVisible() + "\n";
    to_write += "tree_visible:" + isSelectionTreeVisible() + "\n";
    to_write += "date_visible:" + isDateVisible() + "\n";
    if (graphTitle.length()>0) to_write += "frame_title:'" + graphTitle + "'\n";
    to_write += "window_pos:" + getLocationOnScreen().x + "," + getLocationOnScreen().y + "\n";
    to_write += "window_size:" + getSize().width + "," + getSize().height + "\n";
    to_write += "show_device_name:" + isShowingDeviceNames() + "\n";

    if (attList != null) to_write += "refresh_time:" + attList.getRefreshInterval() + "\n";
    to_write += "min_refresh_time:" + getMinRefreshInterval() + "\n";

    // Axis
    to_write += theGraph.getXAxis().getConfiguration("x");
    to_write += theGraph.getY1Axis().getConfiguration("y1");
    to_write += theGraph.getY2Axis().getConfiguration("y2");

    // dataViews
    if (rootNode == null) return to_write;

    Vector dv = rootNode.getSelectableItems();
    TrendSelectionNode n;

    to_write += "dv_number:" + dv.size() + "\n";

    for (i = 0; i < dv.size(); i++) {
      n = (TrendSelectionNode) dv.get(i);
      to_write += "dv" + i + "_name:\'" + n.getModelName() + "\'\n";
      to_write += "dv" + i + "_selected:" + n.getSelected() + "\n";
      to_write += "dv" + i + "_showminalarm:" + n.isShowingMinAlarm() + "\n";
      to_write += "dv" + i + "_showmaxalarm:" + n.isShowingMaxAlarm() + "\n";
      to_write += n.getData().getConfiguration("dv" + i);
      if( n.isShowingMinAlarm() ) to_write += n.getMinAlarmData().getConfiguration("dv_min_alarm" + i);
      if( n.isShowingMaxAlarm() ) to_write += n.getMaxAlarmData().getConfiguration("dv_max_alarm" + i);
    }

    return to_write;
  }

  private String applySettings(CfFileReader f) {

    String errBuff = "";
    Vector p;
    int i,nbDv;
    // Commented revision 1.46 modifications :
    //theGraph.setMaxDisplayDuration(Double.POSITIVE_INFINITY);

    // Reset display duration (to avoid history reading side FX)
    theGraph.setDisplayDuration(Double.POSITIVE_INFINITY);

    // Load isShowingDeviceName parameter before creating dataview
    p = f.getParam("show_device_name");
    if (p != null) setShowingDeviceNames(OFormat.getInt(p.get(0).toString()));

    //Create a new Attribute List
    AttributePolledList alist = new AttributePolledList();
    alist.setFilter(new fr.esrf.tangoatk.core.IEntityFilter() {
      public boolean keep(fr.esrf.tangoatk.core.IEntity entity) {
        if (entity instanceof fr.esrf.tangoatk.core.INumberScalar) {
          return true;
        }
        if (entity instanceof fr.esrf.tangoatk.core.IBooleanScalar) {
          return true;
        }        
        System.out.println(entity.getName() + " not imported (only NumberScalar or BooleanScalar!)");
        return false;
      }
    });
    alist.addErrorListener(errWin);

    // Get all dataviews
    p = f.getParam("dv_number");
    if (p != null) {

      try {
        nbDv = Integer.parseInt(p.get(0).toString());
      } catch (NumberFormatException e) {
        errBuff += "dv_number: invalid number\n";
        return errBuff;
      }

      // Build attribute list
      for (i = 0; i < nbDv; i++) {

        p = f.getParam("dv" + i + "_name");
        if (p == null) {
          errBuff += ("Unable to find dv" + i + "_name param\n");
          return errBuff;
        }

        try {
          alist.add(p.get(0).toString());
        } catch (Exception e) {
          errBuff += (e.getMessage() + "\n");
        }

      }

      p = f.getParam("min_refresh_time");
      if (p != null) {
        setMinRefreshInterval( OFormat.getInt(p.get(0).toString()) );
      }
      if (alist.getRefreshInterval() < getMinRefreshInterval()) {
        alist.setRefreshInterval( getMinRefreshInterval() );
      }
      //We have the attList
      //Set the devicePropertyModel
      if (nbDv > 0) {
        if (attList != null) {
          innerPanel.remove(treeView);
          treeView = null;
          mainTree = null;
        }

        p = f.getParam("refresh_time");
        int refreshInterval = 1000;
        if (p != null) {
          refreshInterval = OFormat.getInt(p.get(0).toString());
        }
        if (refreshInterval < getMinRefreshInterval()) {
          refreshInterval = getMinRefreshInterval();
        }
        alist.setRefreshInterval(refreshInterval);

        alist.startRefresher();
        setModel(alist);
        lastCreatedList = alist;

      }

    } else {
      nbDv = 0;
    }

    innerPanel.revalidate();

    // Now we can set up the graph
    // General settings
    theGraph.applyConfiguration(f);
    // Commented revision 1.46 modifications :
    //theGraph.setMaxDisplayDuration(24 * 60 * 60 * 1000);
    p = f.getParam("toolbar_visible");
    if (p != null) setButtonBarVisible(OFormat.getBoolean(p.get(0).toString()));
    p = f.getParam("tree_visible");
    if (p != null) setSelectionTreeVisible(OFormat.getBoolean(p.get(0).toString()));
    p = f.getParam("date_visible");
    if (p != null) setDateVisible(OFormat.getBoolean(p.get(0).toString()));
    p = f.getParam("frame_title");
    if (p != null) {
      graphTitle = p.get(0).toString();
      if(parent!=null) parent.setTitle(graphTitle);
    }
    p = f.getParam("window_pos");
    if( p != null ) framePos=OFormat.getPoint(p);
    p = f.getParam("window_size");
    if( p != null ) frameDimension=OFormat.getPoint(p);

    // Axis
    theGraph.getXAxis().applyConfiguration("x",f);
    theGraph.getY1Axis().applyConfiguration("y1", f);
    theGraph.getY2Axis().applyConfiguration("y2", f);

    // Select signal and apply dataView options
    if (rootNode == null) return errBuff;
    Vector dv = rootNode.getSelectableItems();
    TrendSelectionNode n = null;

    for (i = 0; i < nbDv; i++) {
      String attName;
      String pref = "dv" + i;
      p = f.getParam(pref + "_name");
      attName = p.get(0).toString();

      p = f.getParam(pref + "_selected");
      if (p != null) {
        int s = OFormat.getInt(p.get(0).toString());

        // Find to node to select
        int j = 0;
        boolean found = false;
        while (!found && j < dv.size()) {
          n = (TrendSelectionNode) dv.get(j);
          found = n.getModelName().equals(attName);
          if (!found) j++;
        }
        if (found) {

          if (s > 0) n.setSelected(s);
          JLDataView d = n.getData();

          // Dataview options
          d.applyConfiguration(pref,f);

          // Min alarm
          p=f.getParam(pref + "_showminalarm");
          if(p!=null) {
            boolean showMinAlarm = OFormat.getBoolean(p.get(0).toString());
            if( showMinAlarm ) {
              n.showMinAlarm();
              String prefMinAlarm = "dv_min_alarm" + i;
              n.getMinAlarmData().applyConfiguration(prefMinAlarm,f);
            } else {
              n.hideMinAlarm();
            }
          }

          // Max alarm
          p=f.getParam(pref + "_showmaxalarm");
          if(p!=null) {
            boolean showMaxAlarm = OFormat.getBoolean(p.get(0).toString());
            if( showMaxAlarm ) {
              n.showMaxAlarm();
              String prefMaxAlarm = "dv_max_alarm" + i;
              n.getMaxAlarmData().applyConfiguration(prefMaxAlarm,f);
            } else {
              n.hideMaxAlarm();
            }
          }

        }
      }
    }

    return errBuff;
  }

  /** Apply a configuration.
   *
   * @param txt Configuration text.
   * @return An error string or An empty string when succes
   * @see #getSettings
   */
  public String setSetting(String txt) {

    CfFileReader f = new CfFileReader();

    // Read and browse the file
    if (!f.parseText(txt)) {
      return "Trend.setSettings: Failed to parse given text";
    }

    return applySettings(f);
  }

  /**
   * Save settings.
   * @param filename file to be saved.
   */
  public void saveSetting(String filename) {

    try {
      FileWriter f = new FileWriter(filename);
      String s = getSettings();
      f.write(s, 0, s.length());
      f.close();
      lastConfig = filename;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(parent, "Failed to write " + filename, "Error", JOptionPane.ERROR_MESSAGE);
    }

  }

  /**
   *  Load graph settings.
   * @param filename file to be read
   * @return An error string or An empty string when succes
   */
  public String loadSetting(String filename) {

    CfFileReader f = new CfFileReader();

    // Read and browse the file
    if (!f.readFile(filename)) {
      return "Failed to read " + filename;
    }
    lastConfig = filename;

    return applySettings(f);
  }

  /** Returns the frame_title field read in the config file. */
  public String getTitle() {
    return graphTitle;
  }

  // ************************************************
  // Option fonction
  // ************************************************
  /**
   * @deprecated use getChart()
   */
  public void setLegendVisible(boolean b) {
  }

  /**
   * @deprecated use getChart()
   */
  public boolean isLegendVisible() {
    return false;
  }

  /**
   * @deprecated no longer used (has no FX)
   */
  public void setSamplingRate(double rate) {
  }

  /**
   * @deprecated no longer used (has no FX)
   */
  public double getSamplingRate() {
    return 0.0;
  }

  /**
   * @deprecated no longer used (has no FX)
   */
  public void setXAxisLength(int length) {
  }

  /**
   * @deprecated no longer used (has no FX)
   */
  public int getXAxisLength() {
    return 0;
  }

  /**
   * @deprecated use getChart()
   */
  public void setLogarithmicScale(boolean logarithmic) {
  }

  /**
   * @deprecated use getChart()
   */
  public boolean isLogarithmicScale() {
    return false;
  }

  /**
   * @deprecated no longer used (has no FX)
   */
  public void setListVisible(boolean b) {
  }

  /**
   * @deprecated no longer used (has no FX)
   */
  public boolean isListVisible() {
    return false;
  }

  /**
   * @deprecated no longer used (has no FX)
   */
  public void setShowingNames(boolean b) {
  }

  /**
   * @deprecated no longer used (has no FX)
   */
  public boolean isShowingNames() {
    return false;
  }

  /**
   * Determines whether or not device names (within chart labels) are visible.
   * @param mode Device name display mode
   * @see #DEVICE_LABEL_AUTO
   * @see #DEVICE_LABEL_NEVER
   * @see #DEVICE_LABEL_ALWAYS
   * @see #isShowingDeviceNames
   */
  public void setShowingDeviceNames(int mode) {
    isShowingDeviceName = mode;
  }

  /**
   * Determines whether or not device names (within chart labels) are visible.
   * @see #setShowingDeviceNames
   */
  public int isShowingDeviceNames() {
    return isShowingDeviceName;
  }

  boolean displayDeviceNames() {
    switch(isShowingDeviceName) {
      case DEVICE_LABEL_ALWAYS:
        return true;
      case DEVICE_LABEL_NEVER:
        return false;
    }
    return !singleDevice;
  }

  /**
   * Displays or hides the toolbar
   * @param b Visible flag
   */
  public void setButtonBarVisible(boolean b) {
    if (theToolBar != null) theToolBar.setVisible(b);
  }

  /**
   * Returns true only if toolbar is visible
   * @return Visible flag
   */
  public boolean isButtonBarVisible() {
    if (theToolBar != null)
      return theToolBar.isVisible();
    else
      return false;
  }

  /**
   * Displays or hides the date label
   * @param b Visible flag
   */
  public void setDateVisible(boolean b) {
    if (dateLabel != null) dateLabel.setVisible(b);
  }

  /**
   * Returns true only if datelabel is visible
   * @return Visible flag
   */
  public boolean isDateVisible() {
    if (dateLabel != null)
      return dateLabel.isVisible();
    else
      return false;
  }

  /**
   * Displays or hides the selection tree
   * @param b Visible flag
   */
  public void setSelectionTreeVisible(boolean b) {
    if (treeView != null) {
      treeView.setVisible(b);
      revalidate();
    }

  }

  /**
   * Returns true only if the selection tree is visible
   */
  public boolean isSelectionTreeVisible() {
    if (treeView != null)
      return treeView.isVisible();
    else
      return false;
  }

  /**
   * Returns a handle to the chart
   * @return Chart handle
   */
  public JLChart getChart() {
    return theGraph;
  }

  public void ok() {
    getRootPane().getParent().setVisible(false);
  }
  
  /**
   * Disables the button corresponding to the string
   */
  public void disableButton(String buttonName){
      JButton b = (JButton)buttonMap.get(buttonName);
      if (b!=null) b.setEnabled(false);
  }
  
  /**
   * Enables the button corresponding to the string
   */
  public void enableButton(String buttonName){
      JButton b = (JButton)buttonMap.get(buttonName);
      if (b!=null) b.setEnabled(true);
  }

  /**
   * Adds an attribute to an axis of this trend (or removes the attribute from
   * axis).
   * 
   * @param attributeName
   *            The name of the attribute
   * @param axisSelection
   *            The axis. Can be SEL_X (x axis), SEL_Y1 (y1 axis), SEL_Y2 (y2
   *            axis) or SEL_NONE (removes attribute from axis)
   * @param addToModel
   *            A boolean to say wheather the attribute has to be added in
   *            model or not. If <code>true</code> and the attribute is not
   *            in model, the attribute is added in the trend model.
   * @see #SEL_X
   * @see #SEL_Y1
   * @see #SEL_Y2
   * @see #SEL_NONE
   */
  public void addToAxis(String attributeName, int axisSelection, boolean addToModel)
  {
      if (addToModel)
      {
          try
          {
              if (getModel() == null)
              {
                 addAttribute(attributeName); 
              }
              else if (getModel().get(attributeName) == null)
              {
                  addAttribute(attributeName); 
              }
          }
          catch(ClassCastException e)
          {
              return;
          }
      }
      if (rootNode == null) return;
      int i = 0;
      Vector dv = rootNode.getSelectableItems();
      TrendSelectionNode attributeNode = null;
      boolean present = false;
      while (!present && i < dv.size())
      {
          attributeNode = (TrendSelectionNode) dv.get( i );
          present = ( (attributeNode != null) 
                      && (attributeNode.getModel() != null)
                      && (attributeNode.getModel().getName().equals(attributeName))
                );
          if (!present) i++;
      }
      if (present)
      {
          switch(axisSelection)
          {
              case SEL_X:
                  int j = 0;
                  boolean found = false;
                  TrendSelectionNode nodeSetToX = null;
                  while (!found && j < dv.size())
                    {
                        nodeSetToX = (TrendSelectionNode) dv.get( j );
                        found = ( (nodeSetToX != null) 
                                  && (nodeSetToX.getSelected() == SEL_X)
                                );
                        if (!found) j++;
                    }
                  if (found) nodeSetToX.setSelected(SEL_NONE);
              case SEL_Y1:
              case SEL_Y2:
              case SEL_NONE:
                  attributeNode.setSelected(axisSelection);
                  mainTree.repaint();
                  theGraph.repaint();
              default:
                  return;
          }
      }
  }

  /**
   * Adds an attribute to an axis of this trend (or removes the attribute from
   * axis).
   * 
   * @param attribute
   *            The attribute
   * @param axisSelection
   *            The axis. Can be SEL_X (x axis), SEL_Y1 (y1 axis), SEL_Y2 (y2
   *            axis) or SEL_NONE (removes attribute from axis)
   * @param addToModel
   *            A boolean to say wheather the attribute has to be added in
   *            model or not. If <code>true</code> and the attribute is not
   *            in model, the attribute is added in the trend model.
   * @see #SEL_X
   * @see #SEL_Y1
   * @see #SEL_Y2
   * @see #SEL_NONE
   */
  public void addToAxis(INumberScalar attribute, int axisSelection, boolean addToModel)
  {
      if (addToModel)
      {
          if (getModel() == null)
          {
              addAttribute(attribute);
          }
          else if (getModel().get(attribute.getName()) == null)
          {
              addAttribute(attribute);
          }
      }
      if (rootNode == null) return;
      int i = 0;
      Vector dv = rootNode.getSelectableItems();
      TrendSelectionNode attributeNode = null;
      boolean present = false;
      while (!present && i < dv.size())
      {
          attributeNode = (TrendSelectionNode) dv.get( i );
          present = ( (attributeNode != null) 
                      && (attributeNode.getModel() != null)
                      && (attributeNode.getModel().getName().equals(attribute.getName()))
                );
          if (!present) i++;
      }
      if (present)
      {
          switch(axisSelection)
          {
              case SEL_X:
                  int j = 0;
                  boolean found = false;
                  TrendSelectionNode nodeSetToX = null;
                  while (!found && j < dv.size())
                    {
                        nodeSetToX = (TrendSelectionNode) dv.get( j );
                        found = ( (nodeSetToX != null) 
                                  && (nodeSetToX.getSelected() == SEL_X)
                                );
                        if (!found) j++;
                    }
                  if (found) nodeSetToX.setSelected(SEL_NONE);
              case SEL_Y1:
              case SEL_Y2:
              case SEL_NONE:
                  attributeNode.setSelected(axisSelection);
                  mainTree.repaint();
                  theGraph.repaint();
              default:
                  return;
          }
      }
  }

  /**
   * Returns the axis associated with an attribute
   * @param attributeName The name of the attribute
   * @return The axis associated with the attribute
   * the value can be:<br>
   * <ul>
   *   <li>SEL_X (x axis)</li>
   *   <li>SEL_Y1 (y1 axis)</li>
   *   <li>SEL_Y2 (y2 axis)</li>
   *   <li>SEL_NONE (no axis, default value)</li>
   * </ul>
   * @see #SEL_X
   * @see #SEL_Y1
   * @see #SEL_Y2
   * @see #SEL_NONE
   */
  public int getAxisForAttribute(String attributeName)
  {
      int selectedAxis = SEL_NONE;
      if (rootNode == null) return selectedAxis;
      int i = 0;
      Vector dv = rootNode.getSelectableItems();
      TrendSelectionNode attributeNode = null;
      boolean present = false;
      while (!present && i < dv.size())
      {
          attributeNode = (TrendSelectionNode) dv.get( i );
          present = ( (attributeNode != null) 
                      && (attributeNode.getModel() != null)
                      && (attributeNode.getModel().getName().equals(attributeName))
                    );
          if (!present) i++;
      }
      if (present)
      {
          selectedAxis = attributeNode.getSelected();
      }
      return selectedAxis;
  }

  /**
   * Returns the dataview associated with an attribute (null if no dataview is associated with the attribute)
   * @param attributeName The name of the attribute
   * @return The dataview associated with the attribute (null if no dataview is associated with the attribute)
   */
  public JLDataView getDataViewForAttribute(String attributeName)
  {
      JLDataView selectedData = null;
      if (rootNode == null) return selectedData;
      int i = 0;
      Vector dv = rootNode.getSelectableItems();
      TrendSelectionNode attributeNode = null;
      boolean present = false;
      while (!present && i < dv.size())
      {
          attributeNode = (TrendSelectionNode) dv.get( i );
          present = ( (attributeNode != null) 
                      && (attributeNode.getModel() != null)
                      && (attributeNode.getModel().getName().equals(attributeName))
                    );
          if (!present) i++;
      }
      if (present)
      {
          selectedData = attributeNode.getData();
      }
      return selectedData;
  }

  public void setMaxDisplayDuration (double maxDisplay)
  {
      theGraph.setMaxDisplayDuration(maxDisplay);
  }

  public double getMaxDisplayDuration ()
  {
      return theGraph.getMaxDisplayDuration();
  }

  protected int getMinRefreshInterval () {
    return minRefreshInterval;
  }

  protected void setMinRefreshInterval (int minRefreshInterval) {
    this.minRefreshInterval = minRefreshInterval;
    if (attList != null && attList.getRefreshInterval() < minRefreshInterval) {
      attList.setRefreshInterval( minRefreshInterval );
    }
  }

  public void resetTrend() {

    if( rootNode!=null ) {
      Vector dv = rootNode.getSelectableItems();
      TrendSelectionNode n;
      for(int i = 0;i < dv.size(); i++) {
        n = (TrendSelectionNode)dv.get(i);
        if (n != null && n.getData() != null) {
          n.getData().reset();
        }
      }
      theGraph.repaint();
    }

  }
  
    protected int getMinRefreshTrendInterval() {
        return minRefreshTrendInterval;
      }

      protected void setMinRefreshTrendInterval (int minRefreshTrendInterval) {
        this.minRefreshTrendInterval = minRefreshTrendInterval;
        if (attList != null && getRefreshIntervalTrend() < minRefreshTrendInterval) {
          setRefreshIntervalTrend(minRefreshTrendInterval);
        }
      }     
     
  	public int getRefreshIntervalTrend() {
        return refreshIntervalTrend;
    }

    public void setRefreshIntervalTrend(int refreshIntervalTrend) {
        this.refreshIntervalTrend = refreshIntervalTrend;
    }

	public boolean isManageIntervalTrend() {
        return manageIntervalTrend;
    }

    public void setManageIntervalTrend() {
            URL tmpUrl = getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend.properties");
            if(tmpUrl != null)
                manageIntervalTrend = true;
    }
    
  	public void refreshTrend() {
          theGraph.repaint();
    }

  // End of variables declaration//GEN-END:variables

  public static void main(String[] args) throws Exception {

    final JFrame f = new JFrame();
    final Trend t = new Trend(f);

    //DeviceFactory.getInstance().setTraceMode(DeviceFactory.TRACE_ALL);
    /*
    t.setSetting("toolbar_visible:false "+
                 "tree_visible:false "+
                 "dv_number:2 "+
                 "dv0_name:'jlp/test/1/att_un' "+
                 "dv0_selected:2 "+
                 "dv0_linecolor:255,0,0 "+
                 "dv1_name:'jlp/test/1/att_deux' "+
                 "dv1_selected:3 "+
                 "dv1_linecolor:0,0,255 ");
     */

    // Default title
    f.setTitle("Trends");

    if (args.length > 0) {
      String err = t.loadSetting(args[0]);
      if (err.length() > 0) {
        JOptionPane.showMessageDialog(null, err, "Errors reading " + f.getName(), JOptionPane.ERROR_MESSAGE);
      }
    } else {
      // Create an empty tree
      AttributePolledList lst = new AttributePolledList();
      t.setModel(lst);
    }

    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    if( t.getTitle().length()>0 )
      f.setTitle(t.getTitle());

    f.setContentPane(t);
    Image image = Toolkit.getDefaultToolkit().getImage(t.getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_icon.gif"));
    if (image != null) f.setIconImage(image);
    f.pack();
    f.setBounds(framePos.x, framePos.y, frameDimension.x, frameDimension.y);
    f.setVisible(true);

  } // end of main ()

}

class ConfigPanel extends JDialog implements ActionListener {

  private JButton      addBtn;
  private JButton      closeBtn;
  private Trend        trend;
  private DeviceFinder finder;

  ConfigPanel(Frame parent,Trend trend) {
    super(parent,true);
    initComponents();
    this.trend = trend;
  }

  ConfigPanel(Dialog parent,Trend trend) {
    super(parent,true);
    initComponents();
    this.trend = trend;
  }

  private void initComponents() {

    setTitle("Add new attribute");
    JPanel innerPanel = new JPanel();
    innerPanel.setLayout(new BorderLayout());
    finder = new DeviceFinder(DeviceFinder.MODE_ATTRIBUTE_NUMBER_BOOLEAN_SCALAR);
    innerPanel.add(finder,BorderLayout.CENTER);

    addBtn = new JButton("Add selected attribute(s)");
    addBtn.setFont(ATKConstant.labelFont);
    addBtn.addActionListener(this);
    closeBtn = new JButton("Dismiss");
    closeBtn.setFont(ATKConstant.labelFont);
    closeBtn.addActionListener(this);
    JPanel btnPanel = new JPanel(new FlowLayout());
    btnPanel.add(addBtn);
    btnPanel.add(closeBtn);
    innerPanel.add(btnPanel,BorderLayout.SOUTH);
    setContentPane(innerPanel);

  }

  public void actionPerformed(ActionEvent e) {

    Object src = e.getSource();

    if(src == addBtn) {
      String[] list = finder.getSelectedNames();
      for(int i=0;i<list.length;i++)
        trend.addAttribute(list[i]);
    } else if (src == closeBtn ) {
      setVisible(false);
    }

  }

  void showPanel() {
    ATKGraphicsUtils.centerDialog(this);
    setVisible(true);
  }
  
}
