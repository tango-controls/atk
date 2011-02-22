/*
 * Trend.java
 *
 * Created on May 13, 2002, 4:28 PM
 */

package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.widget.util.IControlee;
import fr.esrf.tangoatk.widget.util.chart.*;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.widget.attribute.TrendSelectionNode;
import fr.esrf.tangoatk.core.ConnectionException;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author  pons
 */


public class Trend extends JPanel implements IControlee, ActionListener, IJLChartActionListener {

  // Constant

  // Selection type
  public static final int SEL_NONE = 0;
  public static final int SEL_X = 1;
  public static final int SEL_Y1 = 2;
  public static final int SEL_Y2 = 3;

  //Default Color
  public static final Color[] defaultColor = {
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
  JFrame parent = null;

  // Toolbar stuff
  JToolBar theToolBar;
  JPopupMenu toolMenu;

  JButton optionButton;
  JMenuItem optionMenuI;
  JButton stopButton;
  JMenuItem stopMenuI;
  JButton startButton;
  JMenuItem startMenuI;
  JButton loadButton;
  JMenuItem loadMenuI;
  JButton saveButton;
  JMenuItem saveMenuI;
  JButton zoomButton;
  JMenuItem zoomMenuI;
  JButton timeButton;
  JMenuItem timeMenuI;

  JMenuItem showtoolMenuI;

  JPanel innerPanel;


  // Selection tree stuff
  JScrollPane treeView = null;
  JTree mainTree = null;
  DefaultTreeModel mainTreeModel = null;
  TrendSelectionNode rootNode = null;
  JPopupMenu treeMenu;
  JMenuItem addXMenuItem;
  JMenuItem addY1MenuItem;
  JMenuItem addY2MenuItem;
  JMenuItem removeMenuItem;
  JMenuItem optionMenuItem;

  // Chart stuff
  JLChart theGraph;

  // The models
  fr.esrf.tangoatk.core.AttributeList attList = null;
  private TrendSelectionNode lastAdded;

  // Trend constructor
  public Trend(JFrame parent) {
    this();
    this.parent = parent;
  }

  public Trend() {

    theToolBar = new JToolBar();
    toolMenu = new JPopupMenu();

    optionButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_settings.gif")));
    optionButton.setToolTipText("Global settings");
    optionMenuI = new JMenuItem("Global settings");

    stopButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_stop.gif")));
    stopButton.setToolTipText("Stop monitoring");
    stopMenuI = new JMenuItem("Stop monitoring");

    startButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_start.gif")));
    startButton.setToolTipText("Start monitoring");
    startMenuI = new JMenuItem("Start monitoring");

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

    showtoolMenuI = new JMenuItem("Hide toolbar");

    theToolBar.setFloatable(true);

    loadButton.addActionListener(this);
    loadMenuI.addActionListener(this);
    saveButton.addActionListener(this);
    saveMenuI.addActionListener(this);
    optionButton.addActionListener(this);
    optionMenuI.addActionListener(this);
    zoomButton.addActionListener(this);
    zoomMenuI.addActionListener(this);
    stopButton.addActionListener(this);
    stopMenuI.addActionListener(this);
    startButton.addActionListener(this);
    startMenuI.addActionListener(this);
    timeButton.addActionListener(this);
    timeMenuI.addActionListener(this);
    showtoolMenuI.addActionListener(this);

    theToolBar.add(loadButton);
    theToolBar.add(saveButton);
    theToolBar.add(optionButton);
    theToolBar.add(zoomButton);
    theToolBar.add(startButton);
    theToolBar.add(stopButton);
    theToolBar.add(timeButton);

    toolMenu.add(loadMenuI);
    toolMenu.add(saveMenuI);
    toolMenu.add(optionMenuI);
    toolMenu.add(zoomMenuI);
    toolMenu.add(startMenuI);
    toolMenu.add(stopMenuI);
    toolMenu.add(timeMenuI);
    toolMenu.add(showtoolMenuI);

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
    theGraph.addJLChartActionListener(this);

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
    optionMenuItem = new JMenuItem("Options");
    treeMenu.add(addXMenuItem);
    treeMenu.add(addY1MenuItem);
    treeMenu.add(addY2MenuItem);
    treeMenu.add(removeMenuItem);
    treeMenu.add(optionMenuItem);

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
        TrendSelectionNode selNode = (TrendSelectionNode) mainTree.getSelectionPath().getLastPathComponent();
        INumberScalar m = selNode.getModel();
        if (m != null) {
          selNode.setSelected(SEL_Y1);
          mainTree.repaint();
          theGraph.repaint();
        }
      }
    });

    addY2MenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        TrendSelectionNode selNode = (TrendSelectionNode) mainTree.getSelectionPath().getLastPathComponent();
        INumberScalar m = selNode.getModel();
        if (m != null) {
          selNode.setSelected(SEL_Y2);
          mainTree.repaint();
          theGraph.repaint();
        }
      }
    });

    removeMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        TrendSelectionNode selNode = (TrendSelectionNode) mainTree.getSelectionPath().getLastPathComponent();
        INumberScalar m = selNode.getModel();
        if (m != null) {
          selNode.setSelected(SEL_NONE);
          mainTree.repaint();
          theGraph.repaint();
        }
      }
    });

    optionMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        TrendSelectionNode selNode = (TrendSelectionNode) mainTree.getSelectionPath().getLastPathComponent();
        INumberScalar m = selNode.getModel();
        if (m != null) {
          selNode.showOptions();
        }
      }
    });

  }

  // -------------------------------------------------------------
  // Action listener
  // -------------------------------------------------------------
  public void actionPerformed(ActionEvent evt) {

    Object o = evt.getSource();
    if (o == optionButton || o == optionMenuI) {
      optionButtonActionPerformed(evt);
    } else if (o == stopButton || o == stopMenuI) {
      attList.stopRefresher();
    } else if (o == startButton || o == startMenuI) {
      attList.startRefresher();
    } else if (o == loadButton || o == loadMenuI) {
      loadButtonActionPerformed(evt);
    } else if (o == saveButton || o == saveMenuI) {
      saveButtonActionPerformed(evt);
    } else if (o == zoomButton || o == zoomMenuI) {
      if (!theGraph.isZoomed())
        theGraph.enterZoom();
      else
        theGraph.exitZoom();
    } else if (o == timeButton || o == timeMenuI) {
      setRefreshInterval();
    } else if (o == showtoolMenuI) {
      boolean b = isButtonBarVisible();
      b = !b;
      setButtonBarVisible(b);
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
    }
  }

  public boolean getActionState(JLChartActionEvent evt) {

    if(evt.getName().equals("Show toolbar")) {
      return isButtonBarVisible();
    } else if (evt.getName().equals("Show selection tree")) {
      return isSelectionTreeVisible();
    }

    return false;
  }

  private void setRefreshInterval() {

    int old_it = (int) attList.getRefreshInterval();
    String i = JOptionPane.showInputDialog(this, "Enter refresh interval (ms)", (Object) new Integer(old_it));
    if (i != null) {
      try {
        int it = Integer.parseInt(i);
        attList.setRefreshInterval(it);
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(parent, "Invalid number !", "Error", JOptionPane.ERROR_MESSAGE);
      }
    }

  }

  /**
   * This <code>setModel</code> which takes an AttributeList as a
   * parameter, will just add the attributes in the list to the list
   * viewer in the Trend. It will not add any of the attributes to the
   * Trend
   * @param list a <code>fr.esrf.tangoatk.core.AttributeList</code> value
   */
  public void setModel(fr.esrf.tangoatk.core.AttributeList list) {

    // Create the selection tree
    rootNode = new TrendSelectionNode(theGraph);

    for (int i = 0; i < list.size(); i++) {
      lastAdded = rootNode.addItem(theGraph, (INumberScalar) list.get(i), defaultColor[i % defaultColor.length]);
    }

    TrendRenderer renderer = new TrendRenderer();

    mainTreeModel = new DefaultTreeModel(rootNode);
    mainTree = new JTree(mainTreeModel);
    mainTree.setCellRenderer(renderer);
    mainTree.setEditable(false);
    mainTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    mainTree.setShowsRootHandles(true);
    mainTree.setRootVisible(true);
    mainTree.setBorder(BorderFactory.createLoweredBevelBorder());
    treeView = new JScrollPane(mainTree);
    mainTree.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        revalidate();
        int selRow = mainTree.getRowForLocation(e.getX(), e.getY());
        TreePath selPath = mainTree.getPathForLocation(e.getX(), e.getY());
        if (selRow != -1) {
          if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
            if (selPath != null) {
              mainTree.setSelectionPath(selPath);
              TrendSelectionNode selNode = (TrendSelectionNode) selPath.getLastPathComponent();
              if (selNode.getModel() != null) {
                addXMenuItem.setEnabled(selNode.getSelected() != SEL_X);
                addY1MenuItem.setEnabled(selNode.getSelected() != SEL_Y1);
                addY2MenuItem.setEnabled(selNode.getSelected() != SEL_Y2);
                removeMenuItem.setEnabled(selNode.getSelected() != SEL_NONE);
                treeMenu.show(mainTree, e.getX(), e.getY());
              } else if (selNode == rootNode) {

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
    });

    //mainTree.addTreeSelectionListener(treeSelectionlistemner);

    attList = list;
    innerPanel.add(treeView, BorderLayout.WEST);
  }

  /**
   * <code>addAttribute</code> will add the INumberScalar to the
   * Trend. Additional calls to addAttribute will add more INumberScalars
   * to the trend.
   * @param name Attribute name
   */

  public void addAttribute(String name) {
    INumberScalar scalar;

    // Add the attribute in the list
    try {

      if (attList == null) {
        attList = new fr.esrf.tangoatk.core.AttributeList();
        attList.add(name);
        setModel(attList);
        attList.setRefreshInterval(1000);
        attList.startRefresher();
      } else {
        int i = attList.size();
        scalar = (INumberScalar) attList.add(name);
        lastAdded = rootNode.addItem(theGraph, scalar, defaultColor[i % defaultColor.length]);
        mainTreeModel = new DefaultTreeModel(rootNode);
        mainTree.setModel(mainTreeModel);
      }

      TreePath np = new TreePath(lastAdded.getPath());
      mainTree.setSelectionPath(np);
      mainTree.expandPath(np);
      mainTree.makeVisible(np);

    } catch (ConnectionException e) {
      ;
    }


    innerPanel.revalidate();

  }

  public void addAttribute(INumberScalar scalar) {

    // Add the attribute in the list
    System.out.println(" Adding" + scalar.getName());
    if (attList == null) {
      attList = new fr.esrf.tangoatk.core.AttributeList();
      attList.add(scalar);
      setModel(attList);
      attList.setRefreshInterval(1000);
      attList.startRefresher();
    } else {
      if (!attList.contains(scalar)) {
        attList.add(scalar);
        int i = attList.size();
        lastAdded = rootNode.addItem(theGraph, scalar, defaultColor[i % defaultColor.length]);
        mainTreeModel = new DefaultTreeModel(rootNode);
        mainTree.setModel(mainTreeModel);
      }
    }

    TreePath np = new TreePath(lastAdded.getPath());
    mainTree.setSelectionPath(np);
    mainTree.expandPath(np);
    mainTree.makeVisible(np);
    innerPanel.revalidate();

  }

  public void removeAttribute(INumberScalar scalar) {

    lastAdded = null;
    if (attList.contains(scalar)) {
      System.out.println("Removing " + scalar.getName());
      rootNode.delItem(scalar);
      attList.removeElement(scalar);
      mainTreeModel = new DefaultTreeModel(rootNode);
      mainTree.setModel(mainTreeModel);
      innerPanel.revalidate();
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

  public fr.esrf.tangoatk.core.AttributeList getModel() {
    return attList;
  }


  private void optionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionButtonActionPerformed
    theGraph.showOptionDialog();
  }

  private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionButtonActionPerformed

    int ok = JOptionPane.YES_OPTION;
    JFileChooser chooser = new JFileChooser();
    int returnVal = chooser.showSaveDialog(parent);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      if (f != null) {
        if (f.exists())
          ok = JOptionPane.showConfirmDialog(parent, "Do you want to overwrite " + f.getName() + " ?",
                  "Confirm overwrite", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
          saveSetting(f.getAbsolutePath());
        }
      }
    }

  }

  private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionButtonActionPerformed

    int ok = JOptionPane.YES_OPTION;
    JFileChooser chooser = new JFileChooser();
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

  // ****************************************************************
  // Return graph configuration as String
  // ****************************************************************
  public String getSettings() {

    int i;
    String to_write = "";

    // General settings
    to_write += theGraph.getConfiguration();
    to_write += "toolbar_visible:" + isButtonBarVisible() + "\n";
    to_write += "tree_visible:" + isSelectionTreeVisible() + "\n";
    if (attList != null) to_write += "refresh_time:" + attList.getRefreshInterval() + "\n";

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
      to_write += n.getData().getConfiguration("dv" + i);
    }

    return to_write;
  }

  // ****************************************************************
  // Apply settings
  // Return error string
  // ****************************************************************
  private String applySettings(CfFileReader f) {

    String errBuff = "";
    Vector p;
    int i,nbDv;

    // Reset display duration (to avoid history reading side FX)
    theGraph.setDisplayDuration(Double.POSITIVE_INFINITY);

    //Create a new Attribute List
    fr.esrf.tangoatk.core.AttributeList alist = new fr.esrf.tangoatk.core.AttributeList();
    alist.setFilter(new fr.esrf.tangoatk.core.IEntityFilter() {
      public boolean keep(fr.esrf.tangoatk.core.IEntity entity) {
        if (entity instanceof fr.esrf.tangoatk.core.INumberScalar) {
          return true;
        }
        System.out.println(entity.getName() + "not imported (only NumberScalar!)");
        return false;
      }
    });

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

      //We have the attList
      //Set the model
      if (nbDv > 0) {
        if (attList != null) {
          innerPanel.remove(treeView);
          treeView = null;
          mainTree = null;
        }

        p = f.getParam("refresh_time");
        if (p != null)
          alist.setRefreshInterval(OFormat.getInt(p.get(0).toString()));
        else
          alist.setRefreshInterval(1000);

        alist.startRefresher();
        setModel(alist);
      }

    } else {
      nbDv = 0;
    }

    innerPanel.revalidate();

    // Now we can set up the graph
    // General settings
    theGraph.applyConfiguration(f);
    p = f.getParam("toolbar_visible");
    if (p != null) setButtonBarVisible(OFormat.getBoolean(p.get(0).toString()));
    p = f.getParam("tree_visible");
    if (p != null) setSelectionTreeVisible(OFormat.getBoolean(p.get(0).toString()));

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
          n = (TrendSelectionNode) dv.get(i);
          found = n.getModelName().equals(attName);
          if (!found) i++;
        }
        if (found) {

          if (s > 0) n.setSelected(s);
          JLDataView d = n.getData();

          // Dataview options
          d.applyConfiguration(pref,f);
        }
      }
    }

    return errBuff;
  }

  // ****************************************************************
  // Apply a block settings
  // ****************************************************************
  public String setSetting(String txt) {

    CfFileReader f = new CfFileReader();

    // Read and browse the file
    if (!f.parseText(txt)) {
      return "Trend.setSettings: Failed to parse given text";
    }

    return applySettings(f);
  }

  // ****************************************************************
  // Save the whole graph setting
  // ****************************************************************
  public void saveSetting(String filename) {
    int i;

    try {
      FileWriter f = new FileWriter(filename);
      String s = getSettings();
      f.write(s, 0, s.length());
      f.close();
    } catch (Exception e) {
      JOptionPane.showMessageDialog(parent, "Failed to write " + filename, "Error", JOptionPane.ERROR_MESSAGE);
    }

  }

  // ****************************************************************
  // Load graph setting
  // Retrun error string (zero length when succes)
  // ****************************************************************
  public String loadSetting(String filename) {

    CfFileReader f = new CfFileReader();

    // Read and browse the file
    if (!f.readFile(filename)) {
      return "Failed to read " + filename;
    }

    return applySettings(f);
  }


  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    d.height = 0;
    return d;
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
  public void setShowingDeviceNames(boolean b) {
  }

  /**
   * @deprecated no longer used (has no FX)
   */
  public boolean isShowingDeviceNames() {
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
   * @param b Visible flag
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

  // End of variables declaration//GEN-END:variables

  public static void main(String[] args) throws Exception {

    final JFrame f = new JFrame();
    final Trend t = new Trend();

    if (args.length > 0) {
      String err = t.loadSetting(args[0]);
      if (err.length() > 0) {
        JOptionPane.showMessageDialog(null, err, "Errors reading " + f.getName(), JOptionPane.ERROR_MESSAGE);
      }
    } else {
      // Create an empty tree
      fr.esrf.tangoatk.core.AttributeList lst =
              new fr.esrf.tangoatk.core.AttributeList();
      t.setModel(lst);
    }

    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setTitle("Trends");
    f.setContentPane(t);
    Image image = Toolkit.getDefaultToolkit().getImage(t.getClass().getResource("/fr/esrf/tangoatk/widget/attribute/trend_icon.gif"));
    if (image != null) f.setIconImage(image);
    f.pack();
    f.setSize(640, 480);
    f.show();

  } // end of main ()

}
