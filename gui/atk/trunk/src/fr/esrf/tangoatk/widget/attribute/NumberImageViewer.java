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
 
package fr.esrf.tangoatk.widget.attribute;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;

import com.braju.format.Format;

import fr.esrf.TangoDs.AttrManip;
import fr.esrf.tangoatk.core.IImageListener;
import fr.esrf.tangoatk.core.INumberImage;
import fr.esrf.tangoatk.widget.image.LineProfilerViewer;
import fr.esrf.tangoatk.widget.properties.LabelViewer;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import fr.esrf.tangoatk.widget.util.ErrorHistory;
import fr.esrf.tangoatk.widget.util.Gradient;
import fr.esrf.tangoatk.widget.util.JGradientEditor;
import fr.esrf.tangoatk.widget.util.JGradientViewer;
import fr.esrf.tangoatk.widget.util.JImage;
import fr.esrf.tangoatk.widget.util.JSmoothLabel;
import fr.esrf.tangoatk.widget.util.JTableRow;
import fr.esrf.tangoatk.widget.util.MultiExtFileFilter;
import fr.esrf.tangoatk.widget.util.chart.AxisPanel;
import fr.esrf.tangoatk.widget.util.chart.CfFileReader;
import fr.esrf.tangoatk.widget.util.chart.JLAxis;
import fr.esrf.tangoatk.widget.util.jdraw.JDrawable;


/**
 * A high level class to display a TANGO image and handle several image manipulation
 * function.
 *
 * @author  E.S.R.F
 */

public class NumberImageViewer extends JPanel implements IImageListener, MouseMotionListener, MouseListener, ActionListener, KeyListener, JDrawable {

  INumberImage model;

  // ------------------------------------------------------
  // Private data
  // ------------------------------------------------------
  protected double[][] doubleValues = null;
  private Rectangle oldSelection = null;
  protected int profileMode;
  private boolean showingMenu;
  private boolean snapToGrid;
  private boolean sigHistogram;
  private boolean rectXYmode;
  private boolean isNegative;
  private int integrationWidthH;
  private int integrationWidthV;
  protected int startHisto;
  private Gradient gColor;
  private int[] gColormap;
  private int iSz; // Image size
  private EventListenerList listenerList;  // list of Roi listeners
  protected Insets noMargin = new Insets(0,0,0,0);
  private boolean autoZoom = false;
  private boolean firstRefresh = false;
  private boolean userZoom = false;
  private int verticalExtent = 1;
  private String lastConfig = "";

  // Best fit param
  private boolean isBestFit;
  private boolean autoBestFit;
  private double bfMin = 0.0;
  private double bfMax = 65536.0;
  private double autoBfMin;
  private double autoBfMax;
  private double bfa0;
  private double bfa1;
  private double curSelMin;
  private double curSelMax;

  // ------------------------------------------------------
  // Main panel components
  // ------------------------------------------------------

  protected JImage imagePanel;
  protected JScrollPane imageView;

  // Button panel
  protected JPanel buttonPanel;
  protected JButton selectButton;
  protected JButton selectMaxButton;
  protected JButton selectColorButton;
  protected JButton fileButton;
  protected JButton zoomButton;
  protected JButton tableButton;
  protected JButton profileButton;
  protected JButton profile2Button;
  protected JButton histoButton;
  protected JButton settingsButton;
  protected JButton axisButton;
  protected JButton loadButton;
  protected JButton saveButton;
  protected JButton printButton;
  protected JScrollPane buttonView;

  // Info panel
  private Font panelFont;
  private JPanel cfgPanel;
  private JLabel statusLabel;
  private JLabel rangeLabel;
  private JLabel avgLabel;
  private JLabel selLabel;
  protected JTextField selText;

  // Popup menu
  protected JPopupMenu imgMenu;
  protected JMenuItem infoMenuItem;
  protected JCheckBoxMenuItem bestFitMenuItem;
  protected JCheckBoxMenuItem snapToGridMenuItem;
  protected JCheckBoxMenuItem negativeMenuItem;
  protected JCheckBoxMenuItem toolbarMenuItem;
  protected JCheckBoxMenuItem statusLineMenuItem;
  protected JCheckBoxMenuItem showGradMenuItem;
  protected JMenuItem selectionMenuItem;
  protected JMenuItem selectionMaxMenuItem;
  protected JMenuItem selectionColorMenuItem;
  protected JMenuItem fileMenuItem;
  protected JMenuItem zoomMenuItem;
  protected JMenuItem tableMenuItem;
  protected JMenuItem lineProfileMenuItem;
  protected JMenuItem lineProfile2MenuItem;
  protected JMenu dblProfileMenu;
  protected JCheckBoxMenuItem vLeftCheckMenuItem;
  protected JCheckBoxMenuItem vCenterCheckMenuItem;
  protected JCheckBoxMenuItem vRigthCheckMenuItem;
  protected JCheckBoxMenuItem hTopCheckMenuItem;
  protected JCheckBoxMenuItem hCenterCheckMenuItem;
  protected JCheckBoxMenuItem hBottomCheckMenuItem;
  protected JMenuItem histogramMenuItem;
  protected JMenuItem settingsMenuItem;
  protected JMenuItem loadMenuItem;
  protected JMenuItem saveMenuItem;
  protected JMenuItem saveDataFileMenuItem;
  protected JMenuItem printMenuItem;
  protected JCheckBoxMenuItem displayLogMenuItem;

  // Gradientviewer
  private JGradientViewer gradientTool;

  // ------------------------------------------------------
  // LineProfiler panel components
  // ------------------------------------------------------
  protected LineProfilerViewer lineProfiler = null;

  // ------------------------------------------------------
  // Zoom panel components
  // ------------------------------------------------------
  private JFrame zoomDialog = null;
  private JPanel zoomPanel;
  private JScrollPane zoomView;
  private JImage zoomImage;
  private JPanel zoomCfgPanel;
  private JComboBox zoomCombo;
  private int zoomFactor;
  private int zoomXOrg;
  private int zoomYOrg;
  private JLabel zoomText;

  // ------------------------------------------------------
  // Settings panel components
  // ------------------------------------------------------
  private JDialog settingsDialog = null;
  private JPanel settingsPanel;
  private LabelViewer attNameLabel;
  private JButton propButton;
  private JCheckBox sigHistogramCheck;
  private JCheckBox rectDisplayCheck;
  private JCheckBox bestFitCheck;
  private JCheckBox autoBestFitCheck;
  private JLabel minBestFitLabel;
  private JTextField minBestFitText;
  private JLabel maxBestFitLabel;
  private JTextField maxBestFitText;
  private JLabel integrationWidthHLabel;
  private JTextField integrationWidthHText;
  private JLabel integrationWidthVLabel;
  private JTextField integrationWidthVText;
  private JCheckBox snapToGridCheck;
  private JLabel snapToGridLabel;
  private JTextField snapToGridText;
  private JCheckBox negativeCheck;
  private JComboBox imageSizeCombo;
  private JLabel imageSizeLabel;
  private JGradientEditor gradViewer;
  private JButton gradButton;
  private JLabel gradLabel;
  private JButton okButton;
  private JButton cancelButton;

  // ------------------------------------------------------
  // Axis Panels
  // ------------------------------------------------------
  private JDialog     axisDialog = null;
  private JTabbedPane tabPane;
  private AxisPanel   xAxisPanel;
  private AxisPanel   yAxisPanel;
  private JButton     axisCloseButton;

  // ------------------------------------------------------
  // Table panel components
  // ------------------------------------------------------
  private JFrame tableDialog = null;
  private JTableRow tablePanel;

  // ------------------------------------------------------
  // PropertyFrame
  // ------------------------------------------------------
  SimplePropertyFrame propDialog = null;

  static String[] exts = {"toolBarVisible",
                          "statusLineVisible",
                          "gradientVisible",
                          "bestFit",
                          "xAxis",
                          "yAxis"
                          };

  // Used to open the file chooser dialog on the last saved snapshot location
  protected String lastSnapshotLocation = ".";

  // Used to open the file chooser dialog with the last used file filter
  protected FileFilter lastFileFilter = null;

  protected boolean logValues = false;

  
  private int		rgbNaN = 0; //Added by Pascal Verdier to manage a specified color for NaN values.
  private Color	        colorNaN = null; //Added by Pascal Verdier to manage a specified color for NaN values.

  /**
   * Create a new NumberImageViewer
   */
  public NumberImageViewer() {
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEtchedBorder());

    // ------------------------------------------------------
    // Main panel
    // ------------------------------------------------------
    initImagePanel();

    // ------------------------------------------------------
    // Toolbar
    // ------------------------------------------------------
    initButtonPanel();

    // ------------------------------------------------------
    // Gradient tool (Status Line)
    // ------------------------------------------------------
    initGradient();

    // ------------------------------------------------------
    // Main panel (Status Line)
    // ------------------------------------------------------
    initStatusLine();

    // ------------------------------------------------------
    // (Main Panel) Popup menu
    // ------------------------------------------------------
    initPopupMenu();

    // Private stuff
    isBestFit = true;
    rectXYmode = false;
    integrationWidthH = 1;
    integrationWidthV = 1;
    setAlignToGrid(true);
    autoBestFit = true;
    sigHistogram = false;
    isNegative = false;
    showingMenu = true;
    curSelMin = 65536.0;
    curSelMax = 0.0;
    startHisto = 0;
    zoomFactor = 0; // 100%
    iSz = 1;
    listenerList = new EventListenerList();
  }

  protected void initImagePanel() {
      imagePanel = new JImage();
      imagePanel.setBorder(null);
      imagePanel.setSnapGrid(8);
      imageView = new JScrollPane(imagePanel);
      add(imageView, BorderLayout.CENTER);
      imagePanel.addMouseMotionListener(this);
      imagePanel.addMouseListener(this);
      imagePanel.addKeyListener(this);
  }

  protected void initButtonPanel() {
      buttonPanel = new JPanel();
      buttonPanel.setLayout(null);
      buttonPanel.setMinimumSize(new Dimension(60, 575));
      buttonPanel.setPreferredSize(new Dimension(60, 575));
      buttonView = new JScrollPane(buttonPanel);
      buttonView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      add(buttonView, BorderLayout.WEST);

      selectButton = new JButton();
      selectButton.setMargin(noMargin);
      selectButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_select.gif")));
      selectButton.setBounds(2, 5, 36, 36);
      selectButton.setToolTipText("Free selection");
      selectButton.addActionListener(this);
      buttonPanel.add(selectButton);
      
      selectMaxButton = new JButton();
      selectMaxButton.setMargin(noMargin);
      selectMaxButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_selectmax.gif")));
      selectMaxButton.setBounds(2, 40, 36, 36);
      selectMaxButton.setToolTipText("Select whole image");
      selectMaxButton.addActionListener(this);
      buttonPanel.add(selectMaxButton);

      selectColorButton = new JButton();
      selectColorButton.setMargin(noMargin);
      selectColorButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_select_color.gif")));
      selectColorButton.setBounds(2, 75, 36, 36);
      selectColorButton.setToolTipText("Selection Color...");
      selectColorButton.addActionListener(this);
      buttonPanel.add(selectColorButton);

      fileButton = new JButton();
      fileButton.setMargin(noMargin);
      fileButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_snapshot.gif")));
      fileButton.setBounds(2, 115, 36, 36);
      fileButton.setToolTipText("Save snapshot");
      fileButton.addActionListener(this);
      buttonPanel.add(fileButton);

      zoomButton = new JButton();
      zoomButton.setMargin(noMargin);
      zoomButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_zoom.gif")));
      zoomButton.setBounds(2, 155, 36, 36);
      zoomButton.setToolTipText("Zoom selection");
      zoomButton.addActionListener(this);
      buttonPanel.add(zoomButton);

      tableButton = new JButton();
      tableButton.setMargin(noMargin);
      tableButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_table.gif")));
      tableButton.setBounds(2, 195, 36, 36);
      tableButton.setToolTipText("Selection to table");
      tableButton.addActionListener(this);
      buttonPanel.add(tableButton);

      profileButton = new JButton();
      profileButton.setMargin(noMargin);
      profileButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_profile.gif")));
      profileButton.setBounds(2, 240, 36, 36);
      profileButton.setToolTipText("Line profile");
      profileButton.addActionListener(this);
      buttonPanel.add(profileButton);

      profile2Button = new JButton();
      profile2Button.setMargin(noMargin);
      profile2Button.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_profile2.gif")));
      profile2Button.setBounds(2, 280, 36, 36);
      profile2Button.setToolTipText("Line profiles");
      profile2Button.addActionListener(this);
      buttonPanel.add(profile2Button);

      histoButton = new JButton();
      histoButton.setMargin(noMargin);
      histoButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_histo.gif")));
      histoButton.setBounds(2, 320, 36, 36);
      histoButton.setToolTipText("Histogram");
      histoButton.addActionListener(this);
      buttonPanel.add(histoButton);

      settingsButton = new JButton();
      settingsButton.setMargin(noMargin);
      settingsButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_option.gif")));
      settingsButton.setBounds(2, 365, 36, 36);
      settingsButton.setToolTipText("Image viewer settings");
      settingsButton.addActionListener(this);
      buttonPanel.add(settingsButton);

      axisButton = new JButton();
      axisButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_axis.gif")));
      axisButton.setMargin(noMargin);
      axisButton.setBounds(2, 405, 36, 36);
      axisButton.setToolTipText("Axis settings");
      axisButton.addActionListener(this);
      buttonPanel.add(axisButton);

      loadButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_load_settings.gif")));
      loadButton.setToolTipText("Load settings");
      loadButton.setMargin(noMargin);
      loadButton.setBounds(2, 450, 36, 36);
      loadButton.addActionListener(this);
      buttonPanel.add(loadButton);

      saveButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_save_settings.gif")));
      saveButton.setToolTipText("Save settings");
      saveButton.setMargin(noMargin);
      saveButton.setBounds(2, 490, 36, 36);
      saveButton.addActionListener(this);
      buttonPanel.add(saveButton);

      printButton = new JButton(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_print.gif")));
      printButton.setToolTipText("Print Image");
      printButton.setMargin(noMargin);
      printButton.setBounds(2, 535, 36, 36);
      printButton.addActionListener(this);
      buttonPanel.add(printButton);

  }

  protected void initGradient() {
      gradientTool = new JGradientViewer();
      gradientTool.getAxis().setMinimum(0);
      gradientTool.getAxis().setMaximum(65536);
      add(gradientTool, BorderLayout.EAST);
      gColor = new Gradient();
      gColor.buildRainbowGradient();
      gColormap = gColor.buildColorMap(65536);
      gradientTool.setGradient(gColor);
  }

  protected void initStatusLine() {
      cfgPanel = new JPanel();
      cfgPanel.setLayout(null);
      cfgPanel.setPreferredSize(new Dimension(0, 50));
      add(cfgPanel, BorderLayout.SOUTH);

      panelFont = new Font("Dialog", Font.PLAIN, 11);

      statusLabel = new JLabel("");
      statusLabel.setFont(panelFont);
      statusLabel.setBounds(5, 3, 290, 20);
      cfgPanel.add(statusLabel);

      rangeLabel = new JLabel("");
      rangeLabel.setFont(panelFont);
      rangeLabel.setBounds(305, 3, 300, 20);
      cfgPanel.add(rangeLabel);

      avgLabel = new JLabel("");
      avgLabel.setFont(panelFont);
      avgLabel.setBounds(305, 25, 300, 20);
      cfgPanel.add(avgLabel);

      selLabel = new JLabel("Selection");
      selLabel.setFont(panelFont);
      selLabel.setBounds(5, 25, 55, 20);
      cfgPanel.add(selLabel);

      selText = new JTextField("None");
      selText.setMargin(noMargin);
      selText.setFont(panelFont);
      selText.setBounds(65, 25, 230, 20);
      selText.addKeyListener(this);

      cfgPanel.add(selText);
  }

  protected void initPopupMenu() {
      imgMenu = new JPopupMenu();

      infoMenuItem = new JMenuItem("Image Viewer");
      infoMenuItem.setEnabled(false);

      bestFitMenuItem = new JCheckBoxMenuItem("Best fit");
      bestFitMenuItem.addActionListener(this);

      snapToGridMenuItem = new JCheckBoxMenuItem("Align to grid");
      snapToGridMenuItem.addActionListener(this);

      negativeMenuItem = new JCheckBoxMenuItem("Negative image");
      negativeMenuItem.addActionListener(this);

      toolbarMenuItem = new JCheckBoxMenuItem("Show toolbar");
      toolbarMenuItem.addActionListener(this);

      statusLineMenuItem = new JCheckBoxMenuItem("Show status line");
      statusLineMenuItem.addActionListener(this);

      showGradMenuItem = new JCheckBoxMenuItem("Show gradient");
      showGradMenuItem.addActionListener(this);

      selectionMenuItem = new JMenuItem("Free selection");
      selectionMenuItem.addActionListener(this);

      selectionMaxMenuItem = new JMenuItem("Select all");
      selectionMaxMenuItem.addActionListener(this);

      selectionColorMenuItem = new JMenuItem("Selection Color...");
      selectionColorMenuItem.addActionListener(this);

      lineProfileMenuItem = new JMenuItem("Line profile");
      lineProfileMenuItem.addActionListener(this);

      lineProfile2MenuItem = new JMenuItem("Line profiles");
      lineProfile2MenuItem.addActionListener(this);

      dblProfileMenu = new JMenu("Position");

      hTopCheckMenuItem = new JCheckBoxMenuItem("Horizontal Line (Top)");
      hTopCheckMenuItem.addActionListener(this);
      dblProfileMenu.add(hTopCheckMenuItem);
      hCenterCheckMenuItem = new JCheckBoxMenuItem("Horizontal Line (Center)");
      hCenterCheckMenuItem.addActionListener(this);
      dblProfileMenu.add(hCenterCheckMenuItem);
      hBottomCheckMenuItem = new JCheckBoxMenuItem("Horizontal Line (Bottom)");
      hBottomCheckMenuItem.addActionListener(this);
      dblProfileMenu.add(hBottomCheckMenuItem);
      dblProfileMenu.add(new JSeparator());
      vLeftCheckMenuItem = new JCheckBoxMenuItem("Vertical Line (Left)");
      vLeftCheckMenuItem.addActionListener(this);
      dblProfileMenu.add(vLeftCheckMenuItem);
      vCenterCheckMenuItem = new JCheckBoxMenuItem("Vertical Line (Center)");
      vCenterCheckMenuItem.addActionListener(this);
      dblProfileMenu.add(vCenterCheckMenuItem);
      vRigthCheckMenuItem = new JCheckBoxMenuItem("Vertical Line (Right)");
      vRigthCheckMenuItem.addActionListener(this);
      dblProfileMenu.add(vRigthCheckMenuItem);
      refreshDblProfileMenu();

      histogramMenuItem = new JMenuItem("Histogram");
      histogramMenuItem.addActionListener(this);

      fileMenuItem = new JMenuItem("Save selection");
      fileMenuItem.addActionListener(this);
      
      saveDataFileMenuItem = new JMenuItem("Save selection in data file");
      saveDataFileMenuItem.addActionListener(this);            

      zoomMenuItem = new JMenuItem("Zoom selection");
      zoomMenuItem.addActionListener(this);

      settingsMenuItem = new JMenuItem("Settings");
      settingsMenuItem.addActionListener(this);

      tableMenuItem = new JMenuItem("Selection to table");
      tableMenuItem.addActionListener(this);

      loadMenuItem = new JMenuItem("Load settings");
      loadMenuItem.addActionListener(this);

      saveMenuItem = new JMenuItem("Save settings");
      saveMenuItem.addActionListener(this);
      
      printMenuItem = new JMenuItem("Print image");
      printMenuItem.addActionListener(this);

      displayLogMenuItem = new JCheckBoxMenuItem("Display log values");
      displayLogMenuItem.addActionListener(this);

      imgMenu.add(infoMenuItem);
      imgMenu.add(new JSeparator());
      imgMenu.add(bestFitMenuItem);
      imgMenu.add(negativeMenuItem);
      imgMenu.add(snapToGridMenuItem);
      imgMenu.add(toolbarMenuItem);
      imgMenu.add(statusLineMenuItem);
      imgMenu.add(showGradMenuItem);
      imgMenu.add(new JSeparator());
      imgMenu.add(selectionMenuItem);
      imgMenu.add(selectionMaxMenuItem);
      imgMenu.add(selectionColorMenuItem);
      imgMenu.add(fileMenuItem);
      imgMenu.add(saveDataFileMenuItem);
      imgMenu.add(zoomMenuItem);
      imgMenu.add(tableMenuItem);
      imgMenu.add(new JSeparator());
      imgMenu.add(lineProfileMenuItem);
      imgMenu.add(lineProfile2MenuItem);
      imgMenu.add(dblProfileMenu);
      imgMenu.add(histogramMenuItem);
      imgMenu.add(new JSeparator());
      imgMenu.add(settingsMenuItem);
      imgMenu.add(new JSeparator());
      imgMenu.add(loadMenuItem);
      imgMenu.add(saveMenuItem);
      imgMenu.add(new JSeparator());
      imgMenu.add(printMenuItem);
      imgMenu.add(new JSeparator());
      imgMenu.add(displayLogMenuItem);
  }

  // ------------------------------------------------------
  // Implementation of JDrawable interface
  // ------------------------------------------------------
  public void initForEditing() {
  }

  public JComponent getComponent() {
    return this;
  }

  public String getDescription(String name) {

    if (name.equalsIgnoreCase("toolBarVisible")) {
      return "Displays the left toolbar.";
    } else if (name.equalsIgnoreCase("statusLineVisible")) {
      return "Displays the bottom status line.";
    } else if (name.equalsIgnoreCase("gradientVisible")) {
      return "Displays the right gradient scale";
    } else if (name.equalsIgnoreCase("bestFit")) {
      return "Displays the image using the whole color range";
    } else if (name.equalsIgnoreCase("xAxis")) {
      return JLAxis.getHelpString();
    } else if (name.equalsIgnoreCase("yAxis")) {
      return JLAxis.getHelpString();
    }

    return "";

  }

  public String[] getExtensionList() {
    return exts;
  }

  public boolean setExtendedParam(String name,String value,boolean popupErr) {

    if (name.equalsIgnoreCase("toolBarVisible")) {

      if(value.equalsIgnoreCase("true")) {
        setToolbarVisible(true);
        return true;
      } else if (value.equalsIgnoreCase("false")) {
        setToolbarVisible(false);
        return true;
      } else {
        showJdrawError(popupErr,"toolBarVisible","Wrong syntax: 'true' or 'false' expected.");
        return false;
      }

    } else if (name.equalsIgnoreCase("statusLineVisible")) {

      if(value.equalsIgnoreCase("true")) {
        setStatusLineVisible(true);
        return true;
      } else if (value.equalsIgnoreCase("false")) {
        setStatusLineVisible(false);
        return true;
      } else {
        showJdrawError(popupErr,"statusLineVisible","Wrong syntax: 'true' or 'false' expected.");
        return false;
      }

    } else if (name.equalsIgnoreCase("gradientVisible")) {

      if(value.equalsIgnoreCase("true")) {
        setGradientVisible(true);
        return true;
      } else if (value.equalsIgnoreCase("false")) {
        setGradientVisible(false);
        return true;
      } else {
        showJdrawError(popupErr,"gradientVisible","Wrong syntax: 'true' or 'false' expected.");
        return false;
      }

    } else if (name.equalsIgnoreCase("bestFit")) {

      if(value.equalsIgnoreCase("true")) {
        setBestFit(true);
        return true;
      } else if (value.equalsIgnoreCase("false")) {
        setBestFit(false);
        return true;
      } else {
        showJdrawError(popupErr,"bestFit","Wrong syntax: 'true' or 'false' expected.");
        return false;
      }

    } else if (name.equalsIgnoreCase("xAxis")) {

      // Handle a 'bug' in CFFileReader parsing
      if(!value.endsWith("\n")) value = value + "\n";
      CfFileReader f = new CfFileReader();
      if (!f.parseText(value)) {
        showJdrawError(popupErr, "xAxis settings", "Failed to parse given config");
        return false;
      }
      getXAxis().applyConfiguration("x",f);
      return true;

    } else if (name.equalsIgnoreCase("yAxis")) {

      // Handle a 'bug' in CFFileReader parsing
      if(!value.endsWith("\n")) value = value + "\n";
      CfFileReader f = new CfFileReader();
      if (!f.parseText(value)) {
        showJdrawError(popupErr, "yAxis settings", "Failed to parse given config");
        return false;
      }
      getYAxis().applyConfiguration("y",f);
      return true;

    }

    return false;

  }

  public String getExtendedParam(String name) {

    if(name.equalsIgnoreCase("toolBarVisible")) {
      return (isToolbarVisible())?"true":"false";
    } else if(name.equalsIgnoreCase("statusLineVisible")) {
      return (isStatusLineVisible())?"true":"false";
    } else if(name.equalsIgnoreCase("gradientVisible")) {
      return (isGradientVisible())?"true":"false";
    } else if(name.equalsIgnoreCase("bestFit")) {
      return (isBestFit())?"true":"false";
    } else if(name.equalsIgnoreCase("xAxis")) {
      return getXAxis().getConfiguration("x");
    } else if(name.equalsIgnoreCase("yAxis")) {
      return getYAxis().getConfiguration("y");
    }

    return "";

  }

  private void showJdrawError(boolean popup,String paramName,String message) {
    if(popup)
      JOptionPane.showMessageDialog(null, "NumberSpectrumViewer: "+paramName+" incorrect.\n" + message,
                                    "Error",JOptionPane.ERROR_MESSAGE);
  }


  // -----------------------------------------------------------
  // Roi listener
  // -----------------------------------------------------------

  //Add the specified WheelSwitch Listeners
  public void addRoiListener(IRoiListener l) {
    listenerList.add(IRoiListener.class, l);
  }

  //Remove the specified WheelSwitch Listeners
  public void removeRoiListener(IRoiListener l) {
    listenerList.remove(IRoiListener.class, l);
  }

  // Fire WheelSwitchEvent to all registered listeners
  public void fireRoiChange() {
    IRoiListener[] list = (IRoiListener[]) (listenerList.getListeners(IRoiListener.class));
    RoiEvent w = new RoiEvent(this,getSelection());
    for (int i = 0; i < list.length; i++) list[i].roiChange(w);
  }

  // -----------------------------------------------------------
  // Property stuff
  // -----------------------------------------------------------
  /**
   * Displays the image using the whole color range.
   * @param b Best fit toggle
   */
  public void setBestFit(boolean b) {
    isBestFit = b;

    synchronized (this) {
      convertImage();
      if (zoomDialog != null && zoomDialog.isVisible()) buildZoom();
    }

  }

  /**
   * Returns true when best fit is on
   * @return Best fit state
   */
  public boolean isBestFit() {
    return isBestFit;
  }

  /**
   * Sets the auto Best fit mode. Computes maximum and minimum value of
   * the image when enabled else uses the min and max best fit user values.
   * @param b True to enable auto best fit
   */
  public void setAutoBestFit(boolean b) {
    autoBestFit = b;

    synchronized (this) {
      convertImage();
      if (zoomDialog != null && zoomDialog.isVisible()) buildZoom();
    }
  }

  /**
   * Returns true when automatic best fit is enabled
   * @return Auto best fit state
   */
  public boolean isAutoBestFit() {
    return autoBestFit;
  }

  /**
   * Sets the value of best fit min and max when automatic
   * best fit is off.
   * @param min Minimum value
   * @param max Maximum value
   */
  public void setBestFitMinMax(double min, double max) {

    if (min < max) {
      bfMin = min;
      bfMax = max;
    }

    if (!autoBestFit) {
      synchronized (this) {
        convertImage();
        if (zoomDialog != null && zoomDialog.isVisible()) buildZoom();
      }
    }

  }

  /**
   * Returns current best fit min value (user value)
   * @return Minimum value
   */
  public double getBestFitMin() {
    return bfMin;
  }

  /**
   * Returns current best fit max value (user value)
   * @return Maximum value
   */
  public double getBestFitMax() {
    return bfMax;
  }

  /**
   * Sets the colormap
   * @param g New gradient colormap
   */
  public void setGradient(Gradient g) {
    gColor = g;
    gColormap = g.buildColorMap(65536);
    gradientTool.setGradient(gColor);
    gradientTool.repaint();
    synchronized (this) {
      convertImage();
      if (zoomDialog != null && zoomDialog.isVisible()) buildZoom();
    }
  }

  /**
   * Returns current colormap
   * @return Gradient object
   */
  public Gradient getGradient() {
    return gColor;
  }

  /**
   * Displays negative image when enabled
   * @param b True for negtive image
   */
  public void setNegative(boolean b) {
    isNegative = b;
    synchronized (this) {
      convertImage();
      if (zoomDialog != null && zoomDialog.isVisible()) buildZoom();
    }
  }

  /**
   * Returns true only if negative image is displayed
   * @return True when negative
   */
  public boolean isNegative() {
    return isNegative;
  }

  /**
   * Sets the align to grid mode for floating selection
   * @param b True to enable
   */
  public void setAlignToGrid(boolean b) {
    snapToGrid = b;
    imagePanel.setSnapToGrid(b);
  }

  /**
   * Returns true is floating selection are aligned to grid
   * @return Align to grid
   */
  public boolean isAlignToGrid() {
    return snapToGrid;
  }

  /**
   * Displays or hides the toolbar.
   * @param b True if toolbar is displayed
   */
  public void setToolbarVisible(boolean b) {
    buttonView.setVisible(b);
  }

  /**
   * Returns true when the toolbar is visible.
   * @return Toolbar visible state
   */
  public boolean isToolbarVisible() {
    return buttonView.isVisible();
  }

  /**
   * Displays or hides the status line (bottom panel).
   * @param b True if status line is displayed
   */
  public void setStatusLineVisible(boolean b) {
    cfgPanel.setVisible(b);
  }

  /**
   * Returns true when the status line is visible.
   * @return Status line visible state
   */
  public boolean isStatusLineVisible() {
    return cfgPanel.isVisible();
  }

  /**
   * Displays or hides the gradient (right panel).
   * @param b True if status line is displayed
   */
  public void setGradientVisible(boolean b) {
    gradientTool.setVisible(b);
  }

  /**
   * Returns true when the gradient is visible.
   */
  public boolean isGradientVisible() {
    return gradientTool.isVisible();
  }

  /**
   * True to enable menu displayed when clicking on right mouse button.
   * @param b True to enable the menu
   */
  public void setShowingMenu(boolean b) {
    showingMenu = b;
  }

  /** Sets the image zoom factor (does not affect the zoom dialog).
   * If you want to start you viewer with a fixed zoom factor, you
   * have to call setZoom() before setModel().
   * <pre>
   * Possible zoomIndex values are:
   *   0 : 800%
   *   1 : 400%
   *   2 : 200%
   *   3 : 100%
   *   4 : 50%
   *   5 : 25%
   *   6 : 12.5%
   * </pre>
   * @param zoomIndex ZoomFactor index (see description).
   */
  public void setZoom(int zoomIndex) {

    switch (zoomIndex) {

      case 0: // 800%
        iSz = -8;
        break;

      case 1: // 400%
        iSz = -4;
        break;

      case 2: // 200%
        iSz = -2;
        break;

      case 3: // 100%
        iSz = 1;
        break;

      case 4: // 50%
        iSz = 2;
        break;

      case 5: //25%
        iSz = 4;
        break;

      case 6: //12.5%
        iSz = 8;
        break;

      default:
        JOptionPane.showMessageDialog(this,"NumberImageViewer.setZoom():\nInvalid zoom index value. [0..5]","Error",
                                      JOptionPane.ERROR_MESSAGE);
        // Restore 100%
        iSz = 1;
    }

    if(iSz<0)
      imagePanel.setMarkerScale((double) -iSz);
    else
      imagePanel.setMarkerScale(1.0 / (double) iSz);

    // When set the zoom avoid the computeAutoZoom()
    userZoom = true;

  }

  /**
   * Return the current zoom factor index.
   * @see #setZoom
   */
  public int getZoom() {

    int s = 0;
    switch (iSz) {
      case -8:
        s = 0;
        break;
      case -4:
        s = 1;
        break;
      case -2:
        s = 2;
        break;
      case 1:
        s = 3;
        break;
      case 2:
        s = 4;
        break;
      case 4:
        s = 5;
        break;
      case 8:
        s = 6;
        break;
    }

    return s;
  }

  /**
   * Enables or disables the auto zoom. When enabled,
   * the image size (zoom) is automatically adjusted
   * according to the component size. The calcul of
   * the size is triggered by a call to setData() or
   * imageChange().
   * @param auto AutoZoom flag
   */
  public void setAutoZoom(boolean auto) {
    autoZoom = auto;
  }

  /** Determines wheter this image viewer has auto zoom enabled.
   * @see #setAutoZoom
   */
  public boolean getAutoZoom() {
    return autoZoom;
  }

  /**
   * Returns true is the image viewer menu is displayed when clicking
   * on the right mouse button.
   * @return True is menu is enabled
   */
  public boolean isShowingMenu() {
    return showingMenu;
  }

  /**
   * Returns image margin.
   * @return Mergin
   */
  public Insets getImageMargin() {
    return imagePanel.getMargin();
  }

  /**
   * Sets the image margin.
   * @param i Image margin
   */
  public void setImageMargin(Insets i) {
    imagePanel.setMargin(i);
  }

  /**
   * Returns the current image size
   * @return Current image size
   */
  public Dimension getCurrentImageSize() {

    if (doubleValues == null) {
      return new Dimension(0, 0);
    }

    int y = doubleValues.length;

    if (y == 0) {
      return new Dimension(0, 0);
    }

    return new Dimension(doubleValues[0].length, y);

  }

  /**
   * True is floating selection is enabled
   * @param b True to allow floating selection
   */
  public void setSelectionEnabled(boolean b) {
    imagePanel.setSelectionEnabled(b);
  }

  /**
   * Returns true is floating selection is enabled
   * @return  True when floating selection is allowed
   */
  public boolean isSelectionEnabled() {
    return imagePanel.isSelectionEnabled();
  }

  /**
   * Enable or disable the cross cursor.
   * @param enable True to enable cross cursor, false otherwise.
   */
  public void setCrossCursor(boolean enable) {
    imagePanel.setCrossCursor(enable);
  }

  /**
   * Set the cross cursor color.
   * @param c Cursor color
   * @see #setCrossCursor
   */
  public void setCrossCursorColor(Color c) {
    imagePanel.setCursorColor(c);
  }

  /**
   * Sets the floating selection.
   * @param r Rectangle to select
   */
  public void setSelection(Rectangle r) {
    divRect(r);
    imagePanel.setSelection(r.x, r.y, r.x + r.width, r.y + r.height);
    synchronized (this) {
      refreshComponents();
    }
  }

  /**
   * Return current floating rectangle selection
   * @return Selection rectangle, Null is returned when nothing is selected.
   */
  public Rectangle getSelection() {
    Rectangle r = imagePanel.getSelectionRect();
    if( r!=null) mulRect(r);
    return r;
  }

  /**
   * Returns the floating selection mode
   * @return 0 when Line selection, 1 when rectangle selection
   */
  public int getSelectionMode() {
    return imagePanel.getSelectionMode();
  }

  /**
   * Sets the floating selection mode
   * @param m 0 for Line selection, 1 fro rectangle selection
   */
  public void setSelectionMode(int m) {
    imagePanel.clearSelection();
    imagePanel.setSelectionMode(m);
    freePopup();
    synchronized (this) {
      refreshStatusLine();
      refreshSelectionMinMax();
    }
  }

  /**
   * Sets data to display.
   * @param v Handle to data
   */
  public void setData(double[][] v)
  {

    // Synchronise access to critic data
    synchronized (this) {
      doubleValues = v;
      computeVerticalExtent();
      computeAutoZoom();
      convertImage();
      refreshComponents();
    }

    // Nothing to display
    if (doubleValues == null) {
      imagePanel.setImage(null);
      freePopup();
    }

    // Revalidating the gray background
    imagePanel.revalidate();
    imagePanel.repaint();
    imageView.revalidate();
    imageView.repaint();

  }

  /**
   * Load an image (gif,jpg or png) into the viewer. For color image
   * the green field is taken.
   * @param fileName File to be loaded
   * @throws IOException Exception thrown in case of failure
   */
  public void loadImage(String fileName) throws IOException {

    File in = new File(fileName);
    BufferedImage img = ImageIO.read(in);
    int w = img.getWidth();
    int h = img.getHeight();
    int[] tmpArray = new int[w];
    double[][] newValues = new double[h][w];
    for(int i=0;i<h;i++) {
      img.getRGB(0,i,w,1,tmpArray,0,w);
      for(int j=0;j<w;j++)
       // Get the green field
       newValues[i][j] = (double)((tmpArray[j] & 0xFF00) >> 8);
    }
    setData(newValues);

  }

  /**
   * Adds a cross marker at the specified pos
   * @param x X coordinates
   * @param y Y coordinates
   * @param c Marker Color
   * @return Marker id
   */
  public int addCrossMarker(int x,int y,Color c) {
    return imagePanel.addCrossMarker(x,y,c);
  }

  /**
   * Adds a rectangle marker
   * @param x X topleft corner coordinate
   * @param y Y topleft corner coordinate
   * @param width Rectangle width
   * @param height Rectangle hieght
   * @param c Marker Color
   * @return Marker id
   */
  public int addRectangleMarker(int x, int y, int width,int height,Color c) {
    return imagePanel.addRectangleMarker(x, y, width , height , c);
  }

  /**
   * Adds a vertical line marker
   * @param x Horizontal position
   * @param c Marker color
   * @return Marker id
   */
  public int addVerticalLineMarker(int x,Color c) {
    return imagePanel.addVerticalLineMarker(x,c);
  }

  /**
   * Adds a horizontal line marker
   * @param y Vertical position
   * @param c Marker color
   * @return Marker id
   */
  public int addHorizontalLineMarker(int y,Color c) {
    return imagePanel.addHorizontalLineMarker(y,c);
  }

  /**
   * Sets the position of a marker
   * @param id Marker index
   * @param x X coordinate (ignored when HORIZONTAL_LINE Marker)
   * @param y Y coordinate (ignored when VERTICAL_LINE Marker)
   * @param nWidth Rectangle width (ignored when CROSS Marker or LINE Marker)
   * @param nHeight Rectangle height (ignored when CROSS Marker or LINE Marker)
   */
  public void setMarkerPos(int id, int x, int y, int nWidth, int nHeight) {
    imagePanel.setMarkerPos(id,x,y,nWidth,nHeight);
  }

  /**
   * Clears all markers
   */
  public void clearMarkers() {
    imagePanel.clearMarkers();
  }

  public boolean hasMarker() {
    return imagePanel.getMarkerNumber()>0;
  }

  /**
   * Returns a handle to the x axis.
   */
  public JLAxis getXAxis() {
    return imagePanel.getXAxis();
  }

  /**
   * Return a handle to the Y axis.
   */
  public JLAxis getYAxis() {
    return imagePanel.getYAxis();
  }

  /**
   * Returna handle to the Gradient axis.
   */
  public JLAxis getGradientAxis() {
    return gradientTool.getAxis();
  }

  /**
   * Converts the component horizontal coordinate to the image coordinate.
   * @param x Component horizontal coordinate
   */
  public int getImageXCoord(int x) {

    if(iSz<0) {
      return (x - imagePanel.getXOrigin()) / (-iSz);
    } else {
      return (x - imagePanel.getXOrigin()) * iSz;
    }

  }

  /**
   * Converts the component vertical coordinate to the image coordinate.
   * @param y Component vertical coordinate
   */
  public int getImageYCoord(int y) {

    if(iSz<0) {
      return (y - imagePanel.getYOrigin()) / (-iSz);
    } else {
      return (y - imagePanel.getYOrigin()) * iSz;
    }

  }

  /**
   * Extend verticaly the image.
   * @param ratio Vertical extent ration
   */
  public void setVerticalExtent(int ratio) {
    verticalExtent = ratio;
  }

  // ----------------------------------------------------------
  // Private stuff
  // ----------------------------------------------------------

  private void refreshDblProfileMenu() {

    dblProfileMenu.setEnabled(profileMode==3);
    if (profileMode == 3) {

      hTopCheckMenuItem.setState(false);
      hCenterCheckMenuItem.setState(false);
      hBottomCheckMenuItem.setState(false);

      switch (imagePanel.getHorizontalPosition()) {
        case JImage.HORIZONTAL_TOP:
          hTopCheckMenuItem.setState(true);
          break;
        case JImage.HORIZONTAL_CENTER:
          hCenterCheckMenuItem.setState(true);
          break;
        case JImage.HORIZONTAL_BOTTOM:
          hBottomCheckMenuItem.setState(true);
          break;
      }

      vLeftCheckMenuItem.setState(false);
      vCenterCheckMenuItem.setState(false);
      vRigthCheckMenuItem.setState(false);

      switch (imagePanel.getVerticalPosition()) {
        case JImage.VERTICAL_LEFT:
          vLeftCheckMenuItem.setState(true);
          break;
        case JImage.VERTICAL_CENTER:
          vCenterCheckMenuItem.setState(true);
          break;
        case JImage.VERTICAL_RIGHT:
          vRigthCheckMenuItem.setState(true);
          break;
      }

    }

  }


  protected void mulRect(Rectangle r) {
    if(iSz<0) {
      r.x /= (-iSz);
      r.y /= (-iSz);
      r.width /= (-iSz);
      r.height /= (-iSz);
    } else {
      r.x *= iSz;
      r.y *= iSz;
      r.width *= iSz;
      r.height *= iSz;
    }
  }

  private void divRect(Rectangle r) {
    if(iSz<0) {
      r.x *= (-iSz);
      r.y *= (-iSz);
      r.width *= (-iSz);
      r.height *= (-iSz);
    } else {
      r.x /= iSz;
      r.y /= iSz;
      r.width /= iSz;
      r.height /= iSz;
    }
  }

  protected void mulPoint(Point p) {

    boolean xOk = false;
    boolean yOk = false;
    Dimension d = getCurrentImageSize();

    if (iSz < 0) {

      p.x /= (-iSz);
      p.y /= (-iSz);

    } else {

      // Hack to handle line having a vertex on a image edgde
      if (p.x == (d.width / iSz) - 1) {
        p.x = d.width - 1;
        xOk = true;
      }
      if (p.y == (d.height / iSz) - 1) {
        p.y = d.height - 1;
        yOk = true;
      }

      if (!xOk) p.x *= iSz;
      if (!yOk) p.y *= iSz;

    }

  }

  protected void freePopup() {

    if (lineProfiler != null) {
      lineProfiler.setData(null);
    }

    if (zoomDialog != null) {
      zoomImage.setImage(null);
    }

    if (tableDialog != null) {
      tablePanel.clearData();
    }

  }

  protected double[] buildProfileData(Point p1,Point p2) {

    double[] profile;

    Dimension d = getCurrentImageSize();

    mulPoint(p1);
    mulPoint(p2);

    int dx = p2.x - p1.x;
    int dy = p2.y - p1.y;
    int adx = Math.abs(dx);
    int ady = Math.abs(dy);
    double delta;
    int i, j, xe, ye;

    if( dx==0 && dy==0 ) {
      return new double[0];
    }

    if (dx == 0 && dy != 0) {

      // Vertical profile
      profile = new double[ady + 1];
      ye = p1.y;
      xe = p1.x;
      int dxi = integrationWidthV/2;

      for (i = 0; i <= ady; i++) {

        double sum=0;
        double n=0;

        for(j=-dxi;j<=dxi;j++) {
          if ( (xe+j) >= 0 && (xe+j) < d.width && ye >= 0 && ye < d.height)
            sum += doubleValues[ye][xe+j];
          else
            sum = Double.NaN;
          n = n + 1.0;
        }
        profile[i] = sum/n;

        if (dy < 0)
          ye--;
        else
          ye++;

      }

    } else if (dx != 0 && dy == 0) {

      // Horizontal profile
      profile = new double[adx + 1];
      xe = p1.x;
      ye = p1.y;
      int dxi = integrationWidthH/2;

      for (i = 0; i <= adx; i++) {

        double sum=0;
        double n=0;

        for(j=-dxi;j<=dxi;j++) {
          if (xe >= 0 && xe < d.width && (ye+j) >= 0 && (ye+j) < d.height)
            sum += doubleValues[ye+j][xe];
          else
            sum = Double.NaN;
          n = n + 1.0;
        }
        profile[i] = sum/n;

        if (dx < 0)
          xe--;
        else
          xe++;
        
      }

    } else {

      // Skew profile
      if (adx > ady) {

        delta = (double) dy / (double) adx;
        profile = new double[adx + 1];
        xe = p1.x;
        for (i = 0; i <= adx; i++) {
          ye = p1.y + (int) (delta * (double) i);
          if (xe >= 0 && xe < d.width && ye >= 0 && ye < d.height)
            profile[i] = doubleValues[ye][xe];
          else
            profile[i] = Double.NaN;
          if (dx < 0)
            xe--;
          else
            xe++;
        }

      } else {

        delta = (double) dx / (double) ady;
        profile = new double[ady + 1];
        ye = p1.y;
        for (i = 0; i <= ady; i++) {
          xe = p1.x + (int) (delta * (double) i);
          if (xe >= 0 && xe < d.width && ye >= 0 && ye < d.height)
            profile[i] = doubleValues[ye][xe];
          else
            profile[i] = Double.NaN;
          if (dy < 0)
            ye--;
          else
            ye++;
        }

      }

    }

    return profile;

  }

  protected double[] buildHistogramData() {

    if (doubleValues == null)
      return null;

    double[] histo = new double[65536];
    startHisto = 0;

    Rectangle r = imagePanel.getSelectionRect();

    if (r != null) {

      mulRect(r);

      int i;
      for (i = 0; i < 65536; i++) histo[i] = 0.0;

      try {
        for (i = r.x; i < r.x + r.width; i++)
          for (int j = r.y; j < r.y + r.height; j++)
            histo[(int) doubleValues[j][i]] += 1.0;
      } catch(ArrayIndexOutOfBoundsException e) {
        System.out.println("NumberImageViewer.buildHistogramData() : Cannot build histogram. One or more value exceed the range [0..65535].");
        return null;
      }

      if (!sigHistogram) {

        return histo;

      } else {

        // Clip 0
        int i1 = 0;
        int i2 = 65535;
        boolean found = false;

        // Begining
        while (i1 < 65536 && !found) {
          found = (histo[i1] > 0.0);
          if (!found) i1++;
        }

        // End
        found = false;
        while (i2 >= 0 && !found) {
          found = (histo[i2] > 0.0);
          if (!found) i2--;
        }

        int nb = i2 - i1 + 1;
        double[] nhisto = new double[nb];

        for (i = 0; i < nb; i++)
          nhisto[i] = histo[i1 + i];

        startHisto = i1;
        return nhisto;

      }

    } else {

      return null;

    }

  }

  protected void refreshLineProfile() {

    Point[] p;

    if (lineProfiler != null && lineProfiler.isVisible() && profileMode > 0) {

      refreshDblProfileMenu();

      switch (profileMode) {
        case 1:
          p = imagePanel.getSelectionPoint();
          if(p!=null) lineProfiler.setData(buildProfileData(p[0],p[1]));
          else        lineProfiler.setData(null);
          break;
        case 2:
          double[] v = buildHistogramData();
          if (v != null) {
            lineProfiler.setData(v, startHisto);
          } else {
            lineProfiler.setData(null);
          }
          break;
        case 3:
          p = imagePanel.getSelectionCrossPoint();
          if(p!=null) {
            lineProfiler.setData(buildProfileData(p[0],p[1]));
            lineProfiler.setData2(buildProfileData(p[2],p[3]));
          } else {
            lineProfiler.setData(null);
            lineProfiler.setData2(null);
          }
          break;

      }

    }

  }

  private void refreshSelectionMinMax() {

    if (doubleValues == null) {
      rangeLabel.setText("");
      avgLabel.setText("");
      return;
    }

    Rectangle r = imagePanel.getSelectionRect();

    if (r == null || imagePanel.getSelectionMode() != 1) {
      rangeLabel.setText("");
      return;
    }

    mulRect(r);

    curSelMin = 65536.0;
    curSelMax = 0.0;
    double sum = 0.0;
    double sum2 = 0.0;
    double lgth = 0.0;
    double avg  = 0.0;
    double std  = 0.0;

    for (int j = r.y; j < r.y + r.height; j++)
      for (int i = r.x; i < r.x + r.width; i++) {
        double v = doubleValues[j][i];
        if (v > curSelMax) curSelMax = v;
        if (v < curSelMin) curSelMin = v;
        sum   += v;
        lgth  += 1.0;
      }
    avg = sum/lgth;

    for (int j = r.y; j < r.y + r.height; j++)
      for (int i = r.x; i < r.x + r.width; i++) {
        double v = doubleValues[j][i];
        sum2 += (v-avg)*(v-avg);
      }
    std = Math.sqrt( sum2/lgth );

    Double avgD = new Double(avg);
    Double stdD = new Double(std);
    avgLabel.setText("Average: " + Format.sprintf("%.2f",new Double[]{avgD}) +
                     "  Std deviation: " + Format.sprintf("%.2f",new Double[]{stdD}));

    if (curSelMin <= curSelMax)
      rangeLabel.setText("Range: " + Double.toString(curSelMin) +
          " , " + Double.toString(curSelMax));
    else
      rangeLabel.setText("");

  }

  private void refreshComponents() {

    //refreshStatusLine();
    refreshSelectionMinMax();
    refreshLineProfile();

    if (zoomDialog != null && zoomDialog.isVisible()) {
      buildZoom();
      zoomView.revalidate();
      zoomPanel.repaint();
    }

    if (tableDialog != null && tableDialog.isVisible()) {
      buildTable();
      tablePanel.repaint();
    }

  }

  private void selectionChanged() {
    synchronized (this) {
      refreshComponents();
    }
    fireRoiChange();
  }

  protected void refreshStatusLine() {

    int m = imagePanel.getSelectionMode();
    String selStr = "None";
    Point[] pts;

    switch (m) {
      case JImage.MODE_LINE:
        pts = imagePanel.getSelectionPoint();
        if (pts != null) {
          mulPoint(pts[0]);
          mulPoint(pts[1]);
          selStr = "Line (" + pts[0].x + "," + pts[0].y + ") - (" + pts[1].x + "," + pts[1].y + ")";
        }
        break;
      case JImage.MODE_CROSS:
        pts = imagePanel.getSelectionCrossPoint();
        if (pts != null) {
          mulPoint(pts[0]);
          mulPoint(pts[1]);
          mulPoint(pts[2]);
          mulPoint(pts[3]);
          selStr = "(" + pts[0].x + "," + pts[0].y + ")-(" + pts[1].x + "," + pts[1].y + ")";
          selStr += " (" + pts[2].x + "," + pts[2].y + ")-(" + pts[3].x + "," + pts[3].y + ")";
        }
        break;
      case JImage.MODE_RECT:
        Rectangle sel = imagePanel.getSelectionRect();
        if (sel != null) {
          mulRect(sel);
          if(rectXYmode)
            selStr = "Rect (" + sel.x + "," + sel.y + ") - (" + (sel.x + sel.width - 1) + "," + (sel.y + sel.height + -1) + ")";
          else
            selStr = "Rect (" + sel.x + "," + sel.y + ") - [" + sel.width + "," + sel.height + "]";
        }
        break;
    }

    selText.setText(selStr);


  }

  private void showZoom() {

    constructZoomPanel();

    if(zoomDialog.isVisible()) {
      // if dialog already visible simply raise the window
      zoomDialog.setVisible( true );
      return;
    }

    // Build the zoom
    synchronized (this) {
      if (!buildZoom()) return;
    }

    zoomCombo.setSelectedIndex(zoomFactor);
    zoomText.setText("");
    zoomDialog.setTitle("[zoom] ImageViewer");
    zoomDialog.pack();

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension _scr = toolkit.getScreenSize();
    Dimension _dlg = zoomDialog.getPreferredSize();
    zoomDialog.setBounds((_scr.width - _dlg.height) / 2, (_scr.height - _dlg.height) / 2,
        _dlg.width, _dlg.height);

    zoomDialog.setVisible( true );

  }

  private void applySettings() {

    String minStr = minBestFitText.getText();
    String maxStr = maxBestFitText.getText();
    String gridStr = snapToGridText.getText();

    isBestFit = bestFitCheck.isSelected();
    autoBestFit = autoBestFitCheck.isSelected();
    snapToGrid = snapToGridCheck.isSelected();
    isNegative = negativeCheck.isSelected();
    sigHistogram = sigHistogramCheck.isSelected();
    rectXYmode = rectDisplayCheck.isSelected();

    setZoom(imageSizeCombo.getSelectedIndex());

    setAlignToGrid(snapToGrid);

    try {

      int iwh = Integer.parseInt(integrationWidthHText.getText());

      if( iwh%2==0 ) {
        JOptionPane.showMessageDialog(null, "Integration width (H) must be an odd number", "Error", JOptionPane.ERROR_MESSAGE);
      } else {
        integrationWidthH = iwh;
      }

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Invalid syntax for integration width (H)", "Error", JOptionPane.ERROR_MESSAGE);
    }

    try {

      int iwv = Integer.parseInt(integrationWidthVText.getText());

      if( iwv%2==0 ) {
        JOptionPane.showMessageDialog(null, "Integration width (V) must be an odd number", "Error", JOptionPane.ERROR_MESSAGE);
      } else {
        integrationWidthV = iwv;
      }

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Invalid syntax for integration width (V)", "Error", JOptionPane.ERROR_MESSAGE);
    }

    try {

      int g = Integer.parseInt(gridStr);
      imagePanel.setSnapGrid(g);

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Invalid syntax for grid value", "Error", JOptionPane.ERROR_MESSAGE);
    }

    if (!autoBestFit) {

      try {
        bfMin = Double.parseDouble(minStr);
        bfMax = Double.parseDouble(maxStr);

        if (bfMin >= bfMax) {
          JOptionPane.showMessageDialog(null, "maximum  best fit value is lower or equal than minimum!", "Error", JOptionPane.ERROR_MESSAGE);
          autoBestFit = true;
          bfMin = 0;
          bfMax = 65536;
        }

      } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Invalid syntax for maximum or minimum best fit value", "Error", JOptionPane.ERROR_MESSAGE);
        autoBestFit = true;
        bfMin = 0;
        bfMax = 65536;
      }

    }

    settingsDialog.setVisible(false);

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
      JOptionPane.showMessageDialog(this, "Failed to write " + filename, "Error", JOptionPane.ERROR_MESSAGE);
    }

  }

  /** returns the configuration as string.
    * @see #saveSetting
    */
  public String getSettings() {
    constructSettingsPanel();
    initSettings();
    String to_write = "";

    synchronized(this)
    {
      // General settings
      to_write += "minBestFitText:\'" + minBestFitText.getText() + "\'\n";
      to_write += "maxBestFitText:\'" + maxBestFitText.getText() + "\'\n";
      to_write += "snapToGridText:\'" + snapToGridText.getText() + "\'\n";
      to_write += "isBestFit:\'" + bestFitCheck.isSelected() + "\'\n";
      to_write += "autoBestFit:\'" + autoBestFitCheck.isSelected() + "\'\n";
      to_write += "snapToGrid:\'" + snapToGridCheck.isSelected() + "\'\n";
      to_write += "isNegative:\'" + negativeCheck.isSelected() + "\'\n";
      to_write += "sigHistogram:\'" + sigHistogramCheck.isSelected() + "\'\n";
      to_write += "zoom:\'" + imageSizeCombo.getSelectedIndex() + "\'\n";
      to_write += "toolBarVisible:\'" + toolbarMenuItem.isSelected() + "\'\n";
      to_write += "statusLineVisible:\'" + statusLineMenuItem.isSelected() + "\'\n";
      int gradientCount = gColor.getEntryNumber();
      to_write += "gradientCount:\'" + gradientCount + "\'\n";
      for (int i = 0; i < gradientCount; i++)
      {
        Color gradientColor = gColor.getColorAt(i);
        to_write += "gradientColor_" + i + "_red:\'" + gradientColor.getRed() + "\'\n";
        to_write += "gradientColor_" + i + "_green:\'" + gradientColor.getGreen() + "\'\n";
        to_write += "gradientColor_" + i + "_blue:\'" + gradientColor.getBlue() + "\'\n";
        to_write += "gradientPos_" + i + ":\'" + gColor.getPosAt(i) + "\'\n";
        gradientColor = null;
      }
    }

    return to_write;
  }

  /**
   *  Load NumberImageViewer settings.
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

  private String applySettings(CfFileReader f) {
    constructSettingsPanel();

    String errBuff = "";
    synchronized(this)
    {
      // rollback variables
      String minBFText = minBestFitText.getText();
      String maxBFText = maxBestFitText.getText();
      String snapTGText = snapToGridText.getText();
      boolean isBestFit = bestFitCheck.isSelected();
      boolean autoBestFit = autoBestFitCheck.isSelected();
      boolean snapToGrid = snapToGridCheck.isSelected();
      boolean isNegative = negativeCheck.isSelected();
      boolean sigHistogram = sigHistogramCheck.isSelected();
      int zoom = imageSizeCombo.getSelectedIndex();
      boolean toolBarVisible = toolbarMenuItem.isSelected();
      boolean statusLineVisible = statusLineMenuItem.isSelected();
      int gradientCount = 0;
      Color[] gradientColor;
      double[] gradientPos;
      

      // try to apply settings
      Vector<?> param;

      param = f.getParam("minBestFitText");
      if (param == null) {
        errBuff += ("Unable to find minBestFitText");
        return errBuff;
      }
      else
      {
        minBestFitText.setText(param.get(0).toString());
      }

      param = f.getParam("maxBestFitText");
      if (param == null) {
        // error
        minBestFitText.setText(minBFText);
        errBuff += ("Unable to find maxBestFitText");
        return errBuff;
      }
      else
      {
        maxBestFitText.setText(param.get(0).toString());
      }

      param = f.getParam("snapToGridText");
      if (param == null) {
        // error : rollback
        minBestFitText.setText(minBFText);
        maxBestFitText.setText(maxBFText);
        errBuff += ("Unable to find snapToGridText");
        return errBuff;
      }
      else
      {
        snapToGridText.setText(param.get(0).toString());
      }

      param = f.getParam("isBestFit");
      if (param == null) {
        // error : rollback
        minBestFitText.setText(minBFText);
        maxBestFitText.setText(maxBFText);
        snapToGridText.setText(snapTGText);
        errBuff += ("Unable to find isBestFit");
        return errBuff;
      }
      else
      {
        bestFitCheck.setSelected("true".equalsIgnoreCase(param.get(0).toString().trim()));
      }

      param = f.getParam("autoBestFit");
      if (param == null) {
        // error : rollback
        minBestFitText.setText(minBFText);
        maxBestFitText.setText(maxBFText);
        snapToGridText.setText(snapTGText);
        bestFitCheck.setSelected(isBestFit);
        errBuff += ("Unable to find autoBestFit");
        return errBuff;
      }
      else
      {
        autoBestFitCheck.setSelected("true".equalsIgnoreCase(param.get(0).toString().trim()));
      }

      param = f.getParam("snapToGrid");
      if (param == null) {
        // error : rollback
        minBestFitText.setText(minBFText);
        maxBestFitText.setText(maxBFText);
        snapToGridText.setText(snapTGText);
        bestFitCheck.setSelected(isBestFit);
        autoBestFitCheck.setSelected(autoBestFit);
        errBuff += ("Unable to find snapToGrid");
        return errBuff;
      }
      else
      {
        snapToGridCheck.setSelected("true".equalsIgnoreCase(param.get(0).toString().trim()));
      }

      param = f.getParam("isNegative");
      if (param == null) {
        // error : rollback
        minBestFitText.setText(minBFText);
        maxBestFitText.setText(maxBFText);
        snapToGridText.setText(snapTGText);
        bestFitCheck.setSelected(isBestFit);
        autoBestFitCheck.setSelected(autoBestFit);
        snapToGridCheck.setSelected(snapToGrid);
        errBuff += ("Unable to find isNegative");
        return errBuff;
      }
      else
      {
        negativeCheck.setSelected("true".equalsIgnoreCase(param.get(0).toString().trim()));
      }

      param = f.getParam("sigHistogram");
      if (param == null) {
        // error : rollback
        minBestFitText.setText(minBFText);
        maxBestFitText.setText(maxBFText);
        snapToGridText.setText(snapTGText);
        bestFitCheck.setSelected(isBestFit);
        autoBestFitCheck.setSelected(autoBestFit);
        snapToGridCheck.setSelected(snapToGrid);
        negativeCheck.setSelected(isNegative);
        errBuff += ("Unable to find sigHistogram");
        return errBuff;
      }
      else
      {
        sigHistogramCheck.setSelected("true".equalsIgnoreCase(param.get(0).toString().trim()));
      }

      param = f.getParam("zoom");
      if (param == null) {
        // error : rollback
        minBestFitText.setText(minBFText);
        maxBestFitText.setText(maxBFText);
        snapToGridText.setText(snapTGText);
        bestFitCheck.setSelected(isBestFit);
        autoBestFitCheck.setSelected(autoBestFit);
        snapToGridCheck.setSelected(snapToGrid);
        negativeCheck.setSelected(isNegative);
        sigHistogramCheck.setSelected(sigHistogram);
        errBuff += ("Unable to find zoom");
        return errBuff;
      }
      else
      {
        try {
          int histoSelection = Integer.parseInt(param.get(0).toString());
          imageSizeCombo.setSelectedIndex(histoSelection);
        } catch (NumberFormatException e) {
          // error : rollback
          minBestFitText.setText(minBFText);
          maxBestFitText.setText(maxBFText);
          snapToGridText.setText(snapTGText);
          bestFitCheck.setSelected(isBestFit);
          autoBestFitCheck.setSelected(autoBestFit);
          snapToGridCheck.setSelected(snapToGrid);
          negativeCheck.setSelected(isNegative);
          sigHistogramCheck.setSelected(sigHistogram);
          errBuff += "zoom: invalid number\n";
          return errBuff;
        }
      }

      param = f.getParam("toolBarVisible");
      if (param == null) {
        // error : rollback
        minBestFitText.setText(minBFText);
        maxBestFitText.setText(maxBFText);
        snapToGridText.setText(snapTGText);
        bestFitCheck.setSelected(isBestFit);
        autoBestFitCheck.setSelected(autoBestFit);
        snapToGridCheck.setSelected(snapToGrid);
        negativeCheck.setSelected(isNegative);
        sigHistogramCheck.setSelected(sigHistogram);
        imageSizeCombo.setSelectedIndex(zoom);
        errBuff += ("Unable to find toolBarVisible");
        return errBuff;
      }
      else
      {
        toolbarMenuItem.setSelected("true".equalsIgnoreCase(param.get(0).toString().trim()));
      }

      param = f.getParam("statusLineVisible");
      if (param == null) {
        // error : rollback
        minBestFitText.setText(minBFText);
        maxBestFitText.setText(maxBFText);
        snapToGridText.setText(snapTGText);
        bestFitCheck.setSelected(isBestFit);
        autoBestFitCheck.setSelected(autoBestFit);
        snapToGridCheck.setSelected(snapToGrid);
        negativeCheck.setSelected(isNegative);
        sigHistogramCheck.setSelected(sigHistogram);
        imageSizeCombo.setSelectedIndex(zoom);
        toolbarMenuItem.setSelected(toolBarVisible);
        errBuff += ("Unable to find statusLineVisible");
        return errBuff;
      }
      else
      {
        statusLineMenuItem.setSelected("true".equalsIgnoreCase(param.get(0).toString().trim()));
      }

      param = f.getParam("gradientCount");
      if (param == null) {
        // error : rollback
        minBestFitText.setText(minBFText);
        maxBestFitText.setText(maxBFText);
        snapToGridText.setText(snapTGText);
        bestFitCheck.setSelected(isBestFit);
        autoBestFitCheck.setSelected(autoBestFit);
        snapToGridCheck.setSelected(snapToGrid);
        negativeCheck.setSelected(isNegative);
        sigHistogramCheck.setSelected(sigHistogram);
        imageSizeCombo.setSelectedIndex(zoom);
        toolbarMenuItem.setSelected(toolBarVisible);
        statusLineMenuItem.setSelected(statusLineVisible);
        errBuff += ("Unable to find gradientCount");
        return errBuff;
      }
      else
      {
        try
        {
          gradientCount = Integer.parseInt(param.get(0).toString());
        }
        catch (NumberFormatException nfe)
        {
          // error : rollback
          minBestFitText.setText(minBFText);
          maxBestFitText.setText(maxBFText);
          snapToGridText.setText(snapTGText);
          bestFitCheck.setSelected(isBestFit);
          autoBestFitCheck.setSelected(autoBestFit);
          snapToGridCheck.setSelected(snapToGrid);
          negativeCheck.setSelected(isNegative);
          sigHistogramCheck.setSelected(sigHistogram);
          imageSizeCombo.setSelectedIndex(zoom);
          toolbarMenuItem.setSelected(toolBarVisible);
          statusLineMenuItem.setSelected(statusLineVisible);
          errBuff += ("gradientCount: invalid number");
          return errBuff;
        }
      }
      gradientColor = new Color[gradientCount];
      gradientPos = new double[gradientCount];
      for (int i = 0; i < gradientCount; i++)
      {
        int red, green, blue;
        double pos;

        param = f.getParam("gradientColor_"+i+"_red");
        if (param == null) {
          // error : rollback
          minBestFitText.setText(minBFText);
          maxBestFitText.setText(maxBFText);
          snapToGridText.setText(snapTGText);
          bestFitCheck.setSelected(isBestFit);
          autoBestFitCheck.setSelected(autoBestFit);
          snapToGridCheck.setSelected(snapToGrid);
          negativeCheck.setSelected(isNegative);
          sigHistogramCheck.setSelected(sigHistogram);
          imageSizeCombo.setSelectedIndex(zoom);
          toolbarMenuItem.setSelected(toolBarVisible);
          statusLineMenuItem.setSelected(statusLineVisible);
          errBuff += ("Unable to find gradientColor_"+i+"_red");
          return errBuff;
        }
        else
        {
          try
          {
            red = Integer.parseInt(param.get(0).toString());
          }
          catch (NumberFormatException nfe)
          {
            // error : rollback
            minBestFitText.setText(minBFText);
            maxBestFitText.setText(maxBFText);
            snapToGridText.setText(snapTGText);
            bestFitCheck.setSelected(isBestFit);
            autoBestFitCheck.setSelected(autoBestFit);
            snapToGridCheck.setSelected(snapToGrid);
            negativeCheck.setSelected(isNegative);
            sigHistogramCheck.setSelected(sigHistogram);
            imageSizeCombo.setSelectedIndex(zoom);
            toolbarMenuItem.setSelected(toolBarVisible);
            statusLineMenuItem.setSelected(statusLineVisible);
            errBuff += ("gradientColor_"+i+"_red: invalid number");
            return errBuff;
          }
        }

        param = f.getParam("gradientColor_"+i+"_green");
        if (param == null) {
          // error : rollback
          minBestFitText.setText(minBFText);
          maxBestFitText.setText(maxBFText);
          snapToGridText.setText(snapTGText);
          bestFitCheck.setSelected(isBestFit);
          autoBestFitCheck.setSelected(autoBestFit);
          snapToGridCheck.setSelected(snapToGrid);
          negativeCheck.setSelected(isNegative);
          sigHistogramCheck.setSelected(sigHistogram);
          imageSizeCombo.setSelectedIndex(zoom);
          toolbarMenuItem.setSelected(toolBarVisible);
          statusLineMenuItem.setSelected(statusLineVisible);
          errBuff += ("Unable to find gradientColor_"+i+"_green");
          return errBuff;
        }
        else
        {
          try
          {
            green = Integer.parseInt(param.get(0).toString());
          }
          catch (NumberFormatException nfe)
          {
            // error : rollback
            minBestFitText.setText(minBFText);
            maxBestFitText.setText(maxBFText);
            snapToGridText.setText(snapTGText);
            bestFitCheck.setSelected(isBestFit);
            autoBestFitCheck.setSelected(autoBestFit);
            snapToGridCheck.setSelected(snapToGrid);
            negativeCheck.setSelected(isNegative);
            sigHistogramCheck.setSelected(sigHistogram);
            imageSizeCombo.setSelectedIndex(zoom);
            toolbarMenuItem.setSelected(toolBarVisible);
            statusLineMenuItem.setSelected(statusLineVisible);
            errBuff += ("gradientColor_"+i+"_green: invalid number");
            return errBuff;
          }
        }

        param = f.getParam("gradientColor_"+i+"_blue");
        if (param == null) {
          // error : rollback
          minBestFitText.setText(minBFText);
          maxBestFitText.setText(maxBFText);
          snapToGridText.setText(snapTGText);
          bestFitCheck.setSelected(isBestFit);
          autoBestFitCheck.setSelected(autoBestFit);
          snapToGridCheck.setSelected(snapToGrid);
          negativeCheck.setSelected(isNegative);
          sigHistogramCheck.setSelected(sigHistogram);
          imageSizeCombo.setSelectedIndex(zoom);
          toolbarMenuItem.setSelected(toolBarVisible);
          statusLineMenuItem.setSelected(statusLineVisible);
          errBuff += ("Unable to find gradientColor_"+i+"_blue");
          return errBuff;
        }
        else
        {
          try
          {
            blue = Integer.parseInt(param.get(0).toString());
          }
          catch (NumberFormatException nfe)
          {
            // error : rollback
            minBestFitText.setText(minBFText);
            maxBestFitText.setText(maxBFText);
            snapToGridText.setText(snapTGText);
            bestFitCheck.setSelected(isBestFit);
            autoBestFitCheck.setSelected(autoBestFit);
            snapToGridCheck.setSelected(snapToGrid);
            negativeCheck.setSelected(isNegative);
            sigHistogramCheck.setSelected(sigHistogram);
            imageSizeCombo.setSelectedIndex(zoom);
            toolbarMenuItem.setSelected(toolBarVisible);
            statusLineMenuItem.setSelected(statusLineVisible);
            errBuff += ("gradientColor_"+i+"_blue: invalid number");
            return errBuff;
          }
        }

        param = f.getParam("gradientPos_" + i);
        if (param == null) {
          // error : rollback
          minBestFitText.setText(minBFText);
          maxBestFitText.setText(maxBFText);
          snapToGridText.setText(snapTGText);
          bestFitCheck.setSelected(isBestFit);
          autoBestFitCheck.setSelected(autoBestFit);
          snapToGridCheck.setSelected(snapToGrid);
          negativeCheck.setSelected(isNegative);
          sigHistogramCheck.setSelected(sigHistogram);
          imageSizeCombo.setSelectedIndex(zoom);
          toolbarMenuItem.setSelected(toolBarVisible);
          statusLineMenuItem.setSelected(statusLineVisible);
          errBuff += ("Unable to find gradientPos_" + i);
          return errBuff;
        }
        else
        {
          try
          {
            pos = Double.parseDouble(param.get(0).toString());
          }
          catch (NumberFormatException nfe)
          {
            // error : rollback
            minBestFitText.setText(minBFText);
            maxBestFitText.setText(maxBFText);
            snapToGridText.setText(snapTGText);
            bestFitCheck.setSelected(isBestFit);
            autoBestFitCheck.setSelected(autoBestFit);
            snapToGridCheck.setSelected(snapToGrid);
            negativeCheck.setSelected(isNegative);
            sigHistogramCheck.setSelected(sigHistogram);
            imageSizeCombo.setSelectedIndex(zoom);
            toolbarMenuItem.setSelected(toolBarVisible);
            statusLineMenuItem.setSelected(statusLineVisible);
            errBuff += ("gradientPos_" + i + ": invalid number");
            return errBuff;
          }
        }

        gradientColor[i] = new Color(red, green, blue);
        gradientPos[i] = pos;
      }

      gColor = new Gradient(gradientPos,gradientColor);
      gColormap = gColor.buildColorMap(65536);
      gradientTool.setGradient(gColor);

      applySettings();
      setToolbarVisible(toolbarMenuItem.isSelected());
      setStatusLineVisible(statusLineMenuItem.isSelected());

      convertImage();
      refreshComponents();
    }

    return errBuff;
  }

  private void saveButtonActionPerformed() {

    int ok = JOptionPane.YES_OPTION;
    JFileChooser chooser = new JFileChooser(".");
    chooser.addChoosableFileFilter(new FileFilter() {
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = MultiExtFileFilter.getExtension(f);
            if (extension != null && extension.equals("txt"))
                return true;
            return false;
        }

        public String getDescription() {
            return "text files ";
        }
    });
    if(lastConfig.length()>0)
      chooser.setSelectedFile(new File(lastConfig));
    int returnVal = chooser.showSaveDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      if (f != null) {
        if (MultiExtFileFilter.getExtension(f) == null) {
          f = new File(f.getAbsolutePath() + ".txt");
        }
        if (f.exists())
          ok = JOptionPane.showConfirmDialog(this, "Do you want to overwrite " + f.getName() + " ?",
                  "Confirm overwrite", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
          saveSetting(f.getAbsolutePath());
        }
      }
    }

  }

  private void loadButtonActionPerformed() {

    int ok = JOptionPane.YES_OPTION;
    JFileChooser chooser = new JFileChooser();
    chooser.addChoosableFileFilter(new FileFilter() {
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = MultiExtFileFilter.getExtension(f);
            if (extension != null && extension.equals("txt"))
                return true;
            return false;
        }

        public String getDescription() {
            return "text files ";
        }
    });
    if(lastConfig.length()>0)
      chooser.setSelectedFile(new File(lastConfig));
    int returnVal = chooser.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      if (f != null) {
        if (ok == JOptionPane.YES_OPTION) {
          String err = loadSetting(f.getAbsolutePath());
          if (err.length() > 0) {
            JOptionPane.showMessageDialog(this, err, "Errors reading " + f.getName(), JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    }

  }

  private void showAxisDialog() {

    if (axisDialog == null) {

      Window parent = ATKGraphicsUtils.getWindowForComponent(this);
      if(parent instanceof Frame)
        axisDialog = new JDialog((Frame)parent, false);
      else if(parent instanceof Dialog)
        axisDialog = new JDialog((Dialog)parent, false);
      else
        axisDialog = new JDialog((Frame)null, false);

      axisDialog.setTitle("Axis settings");
      JPanel innerPanel = new JPanel(null);
      tabPane = new JTabbedPane();
      xAxisPanel = new AxisPanel(getXAxis(), AxisPanel.X_TYPE, null);
      yAxisPanel = new AxisPanel(getYAxis(), AxisPanel.Y1_TYPE, null);
      tabPane.add("X axis", xAxisPanel);
      tabPane.add("Y axis", yAxisPanel);
      tabPane.setBounds(5, 5, 300, 370);
      innerPanel.add(tabPane);
      axisCloseButton = new JButton("Close");
      axisCloseButton.setFont(ATKConstant.labelFont);
      axisCloseButton.setBounds(225, 380, 80, 25);
      innerPanel.add(axisCloseButton);
      axisCloseButton.addActionListener(this);
      innerPanel.setPreferredSize(new Dimension(310,410));
      tabPane.setBounds(5, 5, 300, 370);
      axisDialog.setContentPane(innerPanel);

    }

    ATKGraphicsUtils.centerDialog(axisDialog);
    axisDialog.setVisible(true);

  }


  private void showSettings() {

    constructSettingsPanel();
    initSettings();  

    ATKGraphicsUtils.centerDialog(settingsDialog);
    settingsDialog.setVisible(true);

    synchronized (this) {
      convertImage();
      refreshComponents();
    }

  }
  
  private void initSettings(){

      minBestFitText.setText(Double.toString(bfMin));
      maxBestFitText.setText(Double.toString(bfMax));

      integrationWidthVText.setText(Integer.toString(integrationWidthV));
      integrationWidthHText.setText(Integer.toString(integrationWidthH));

      minBestFitLabel.setEnabled(!autoBestFit);
      minBestFitText.setEnabled(!autoBestFit);
      maxBestFitLabel.setEnabled(!autoBestFit);
      maxBestFitText.setEnabled(!autoBestFit);

      bestFitCheck.setSelected(isBestFit);
      autoBestFitCheck.setSelected(autoBestFit);
      sigHistogramCheck.setSelected(sigHistogram);
      snapToGridCheck.setSelected(snapToGrid);
      negativeCheck.setSelected(isNegative);

      imageSizeCombo.setSelectedIndex(getZoom());
      snapToGridText.setText(Integer.toString(imagePanel.getSnapGrid()));
      rectDisplayCheck.setSelected(rectXYmode);

      gradViewer.setGradient(gColor);
}



  private boolean buildTable() {

    Rectangle r = imagePanel.getSelectionRect();

    if (r == null) {
      // Select the whole image
      Dimension d = imagePanel.getImageSize();
      r = new Rectangle(0,0,d.width,d.height);
    }

    mulRect(r);

    if (r.width <= 0 || r.height <= 0)
      return false;

    String attFormat = null;
    if(model!=null) attFormat = model.getFormat();

    if(attFormat!=null && attFormat.length()>0) {

      if (attFormat.indexOf('%') == -1) {

        String[][] d = new String[r.height][r.width];
        for (int j = 0; j < r.height; j++)
          for (int i = 0; i < r.width; i++)
          {
            if (Double.isNaN(doubleValues[r.y + j][r.x + i]) || Double.isInfinite(doubleValues[r.y + j][r.x + i]))
            {
              d[j][i] = Double.toString(doubleValues[r.y + j][r.x + i]);
            }
            else
            {
              d[j][i] = AttrManip.format(attFormat, doubleValues[r.y + j][r.x + i]);
            }
          }
        tablePanel.setData(d, r.x, r.y);

      } else {

        Double[] tmp = new Double[1];
        String[][] d = new String[r.height][r.width];
        for (int j = 0; j < r.height; j++)
          for (int i = 0; i < r.width; i++) {
            tmp[0] = new Double(doubleValues[r.y + j][r.x + i]);
            if (Double.isNaN(doubleValues[r.y + j][r.x + i]) || Double.isInfinite(doubleValues[r.y + j][r.x + i]))
            {
              d[j][i] = tmp[0].toString();
            }
            else
            {
              d[j][i] = Format.sprintf(attFormat, tmp);
            }
          }
        tablePanel.setData(d, r.x, r.y);

      }

    } else {

      Double[][] d = new Double[r.height][r.width];
      for (int j = 0; j < r.height; j++)
        for (int i = 0; i < r.width; i++)
          d[j][i] = new Double(doubleValues[r.y + j][r.x + i]);
      tablePanel.setData(d, r.x, r.y);

    }


    return true;

  }

  private void showTable() {

    constructTablePanel();

    if(tableDialog.isVisible()) {
      // if dialog already visible simply raise the window
      tableDialog.setVisible(true);
      return;
    }

    synchronized (this) {
      if (!buildTable()) return;
    }

    ATKGraphicsUtils.centerFrameOnScreen(tableDialog);
    tableDialog.setVisible(true);

  }
  
  //This method is used to construct table without displaying the table
  private void constructTable() {

      constructTablePanel();
      synchronized (this) {
        if (!buildTable()) return;
      }
  }
  
  //This method is used to save data file without displaying the table
  private void saveDataFile() {
      constructTable();
      if (tablePanel != null)
          tablePanel.saveDataFile();
  }

  private void showPropertyFrame() {

    if (model != null) {
      if (propDialog == null)
        propDialog = new SimplePropertyFrame(settingsDialog, true);
      propDialog.setModel(model);
      propDialog.setVisible(true);
    }

  }

  private void showGradientEditor() {

    Gradient g = JGradientEditor.showDialog(settingsDialog, gColor);
    if (g != null) {
      gColor = g;
      gColormap = g.buildColorMap(65536);
      gradViewer.setGradient(gColor);
      gradViewer.repaint();
      gradientTool.setGradient(gColor);
      gradientTool.repaint();
    }
    
    

  }

  protected void constructLineProfiler() {
    if (lineProfiler == null) {

      // -------------------------------------------------------------------
      // Profiler
      // -------------------------------------------------------------------
      lineProfiler = new LineProfilerViewer();
      lineProfiler.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          //Free data
          lineProfiler.setData(null);
          lineProfiler.dispose();
        }
      });

    }
  }

  private void constructTablePanel() {
    if (tableDialog == null) {

      // -------------------------------------------------------------------
      // Table panel
      // -------------------------------------------------------------------
      tablePanel = new JTableRow();
      tableDialog = new JFrame();
      tableDialog.setContentPane(tablePanel);
      tableDialog.setTitle("[table] ImageViewer");
      tableDialog.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          //Free table
          tablePanel.clearData();
          tableDialog.dispose();
        }
      });

    }
  }

  private void constructZoomPanel() {
    if (zoomDialog == null) {
      // ------------------------------------------------------
      //Zoom panel
      // ------------------------------------------------------

      zoomPanel = new JPanel(new BorderLayout());
      zoomImage = new JImage();
      zoomImage.setSelectionEnabled(false);
      zoomImage.setMargin(new Insets(2, 2, 2, 2));
      zoomImage.setImage(null);
      zoomView = new JScrollPane(zoomImage);
      zoomPanel.add(zoomView, BorderLayout.CENTER);

      zoomCfgPanel = new JPanel();
      zoomCfgPanel.setLayout(null);
      zoomCfgPanel.setPreferredSize(new Dimension(0, 25));

      zoomCombo = new JComboBox();
      zoomCombo.setFont(panelFont);
      zoomCombo.addItem("Zoom 100%");
      zoomCombo.addItem("Zoom 200%");
      zoomCombo.addItem("Zoom 300%");
      zoomCombo.addItem("Zoom 400%");
      zoomCombo.addItem("Zoom 500%");
      zoomCombo.addItem("Zoom 600%");
      zoomCombo.addItem("Zoom 700%");
      zoomCombo.addItem("Zoom 800%");
      zoomCombo.setEditable(false);
      zoomCombo.setBounds(5, 3, 120, 20);
      zoomCombo.addActionListener(this);
      zoomCfgPanel.add(zoomCombo);

      zoomText = new JLabel("");
      zoomText.setFont(panelFont);
      zoomText.setBounds(130, 3, 500, 20);
      zoomCfgPanel.add(zoomText);

      zoomPanel.add(zoomCfgPanel, BorderLayout.SOUTH);

      zoomDialog = new JFrame();
      zoomDialog.setContentPane(zoomPanel);
      zoomDialog.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          //Free image
          zoomImage.setImage(null);
          zoomDialog.dispose();
        }
      });
      zoomImage.addMouseMotionListener(this);

    }
  }

  private void constructSettingsPanel() {

    if (settingsDialog == null) {
      // ------------------------------------------------------
      // Settings panel
      // ------------------------------------------------------
      settingsPanel = new JPanel();
      settingsPanel.setLayout(null);
      settingsPanel.setMinimumSize(new Dimension(290, 330));
      settingsPanel.setPreferredSize(new Dimension(290, 330));

      attNameLabel = new LabelViewer();
      attNameLabel.setOpaque(false);
      attNameLabel.setFont(new Font("Dialog", Font.BOLD, 16));
      attNameLabel.setBounds(5, 5, 200, 30);
      attNameLabel.setHorizontalAlignment(JSmoothLabel.LEFT_ALIGNMENT);
      settingsPanel.add(attNameLabel);
      attNameLabel.setModel(model);

      propButton = new JButton();
      propButton.setText("?");
      propButton.setToolTipText("Edit attribute properties");
      propButton.setFont(panelFont);
      propButton.setMargin(new Insets(0, 0, 0, 0));
      propButton.setBounds(250, 5, 30, 30);
      propButton.addActionListener(this);
      settingsPanel.add(propButton);

      // ------------------------------------------------------------------------------------
      JSeparator js = new JSeparator();
      js.setBounds(0, 40, 500, 10);
      settingsPanel.add(js);

      autoBestFitCheck = new JCheckBox("Best fit");
      autoBestFitCheck.setSelected(true);
      autoBestFitCheck.setFont(panelFont);
      autoBestFitCheck.setBounds(5, 50, 100, 20);
      autoBestFitCheck.setToolTipText("Generate the best fit display using maximum and minimum value of the image");
      autoBestFitCheck.addActionListener(this);
      settingsPanel.add(autoBestFitCheck);

      bestFitCheck = new JCheckBox("Enable fitting");
      bestFitCheck.setSelected(false);
      bestFitCheck.setFont(panelFont);
      bestFitCheck.setBounds(120, 50, 105, 20);
      bestFitCheck.setToolTipText("Display the image using the whole color range");
      settingsPanel.add(bestFitCheck);

      minBestFitLabel = new JLabel("Fit Min");
      minBestFitLabel.setFont(panelFont);
      minBestFitLabel.setBounds(5, 75, 80, 20);
      settingsPanel.add(minBestFitLabel);

      minBestFitText = new JTextField("");
      minBestFitText.setMargin(noMargin);
      minBestFitText.setFont(panelFont);
      minBestFitText.setBounds(90, 75, 50, 20);
      settingsPanel.add(minBestFitText);

      maxBestFitLabel = new JLabel("Fit Max");
      maxBestFitLabel.setFont(panelFont);
      maxBestFitLabel.setBounds(145, 75, 80, 20);
      maxBestFitLabel.setHorizontalAlignment(JLabel.CENTER);
      settingsPanel.add(maxBestFitLabel);

      maxBestFitText = new JTextField("");
      maxBestFitText.setMargin(noMargin);
      maxBestFitText.setFont(panelFont);
      maxBestFitText.setBounds(230, 75, 50, 20);
      settingsPanel.add(maxBestFitText);

      gradLabel = new JLabel("Colormap");
      gradLabel.setFont(panelFont);
      gradLabel.setBounds(5, 100, 70, 20);
      settingsPanel.add(gradLabel);

      gradViewer = new JGradientEditor();
      gradViewer.setGradient(gColor);
      gradViewer.setEditable(false);
      gradViewer.setToolTipText("Display the image using this colormap");
      gradViewer.setBounds(80, 100, 180, 20);
      settingsPanel.add(gradViewer);

      gradButton = new JButton();
      gradButton.setText("...");
      gradButton.setToolTipText("Edit colormap");
      gradButton.setFont(panelFont);
      gradButton.setMargin(new Insets(0, 0, 0, 0));
      gradButton.setBounds(260, 100, 20, 20);
      gradButton.addActionListener(this);
      settingsPanel.add(gradButton);

      negativeCheck = new JCheckBox("Negative image");
      negativeCheck.setSelected(false);
      negativeCheck.setFont(panelFont);
      negativeCheck.setBounds(5, 125, 110, 20);
      negativeCheck.setToolTipText("Display the negative image");
      settingsPanel.add(negativeCheck);


      imageSizeLabel = new JLabel("Image size");
      imageSizeLabel.setFont(panelFont);
      imageSizeLabel.setBounds(115, 125, 85, 20);
      imageSizeLabel.setHorizontalAlignment(JLabel.CENTER);
      settingsPanel.add(imageSizeLabel);

      imageSizeCombo = new JComboBox();
      imageSizeCombo.setFont(panelFont);
      imageSizeCombo.addItem("800  %");
      imageSizeCombo.addItem("400  %");
      imageSizeCombo.addItem("200  %");
      imageSizeCombo.addItem("100  %");
      imageSizeCombo.addItem("50   %");
      imageSizeCombo.addItem("25   %");
      imageSizeCombo.addItem("12.5 %");
      imageSizeCombo.setBounds(200, 125, 80, 22);
      settingsPanel.add(imageSizeCombo);

      // ------------------------------------------------------------------------------------
      JSeparator js2 = new JSeparator();
      js2.setBounds(0, 153, 500, 10);
      settingsPanel.add(js2);

      integrationWidthHLabel = new JLabel("Integration width (Horizontal profile)");
      integrationWidthHLabel.setFont(panelFont);
      integrationWidthHLabel.setBounds(5,160,220,20);
      settingsPanel.add(integrationWidthHLabel);

      integrationWidthHText = new JTextField();
      integrationWidthHText.setEditable(true);
      integrationWidthHText.setBounds(230,160,50,20);
      settingsPanel.add(integrationWidthHText);

      integrationWidthVLabel = new JLabel("Integration width (Vertical profile)");
      integrationWidthVLabel.setFont(panelFont);
      integrationWidthVLabel.setBounds(5,185,220,20);
      settingsPanel.add(integrationWidthVLabel);

      integrationWidthVText = new JTextField();
      integrationWidthVText.setEditable(true);
      integrationWidthVText.setBounds(230,185,50,20);
      settingsPanel.add(integrationWidthVText);

      // ------------------------------------------------------------------------------------
      JSeparator js3 = new JSeparator();
      js3.setBounds(0, 218, 500, 10);
      settingsPanel.add(js3);

      snapToGridCheck = new JCheckBox("Align to grid");
      snapToGridCheck.setSelected(false);
      snapToGridCheck.setFont(panelFont);
      snapToGridCheck.setBounds(5, 225, 100, 20);
      snapToGridCheck.setToolTipText("Align the selection to the grid");
      settingsPanel.add(snapToGridCheck);

      snapToGridLabel = new JLabel("Grid spacing");
      snapToGridLabel.setFont(panelFont);
      snapToGridLabel.setBounds(110, 225, 90, 20);
      settingsPanel.add(snapToGridLabel);

      snapToGridText = new JTextField("");
      snapToGridText.setMargin(noMargin);
      snapToGridText.setFont(panelFont);
      snapToGridText.setBounds(205, 225, 50, 20);
      settingsPanel.add(snapToGridText);

      sigHistogramCheck = new JCheckBox("Display significant data for histogram");
      sigHistogramCheck.setSelected(false);
      sigHistogramCheck.setFont(panelFont);
      sigHistogramCheck.setBounds(5, 250, 280, 20);
      sigHistogramCheck.setToolTipText("Clip the histogram to significant data");
      settingsPanel.add(sigHistogramCheck);

      rectDisplayCheck = new JCheckBox("Display rectangle as (x1,y1) - (x2,y2)");
      rectDisplayCheck.setSelected(false);
      rectDisplayCheck.setFont(panelFont);
      rectDisplayCheck.setBounds(5, 275, 280, 20);
      rectDisplayCheck.setToolTipText("Display rectangle as (x1,y1) - (x2,y2) instead of (x1,y1) - [width,height]");
      settingsPanel.add(rectDisplayCheck);

      okButton = new JButton();
      okButton.setText("Apply");
      okButton.setFont(panelFont);
      okButton.setBounds(5, 300, 80, 25);
      okButton.addActionListener(this);
      settingsPanel.add(okButton);

      cancelButton = new JButton();
      cancelButton.setText("Dismiss");
      cancelButton.setFont(panelFont);
      cancelButton.setBounds(205, 300, 80, 25);
      cancelButton.addActionListener(this);
      settingsPanel.add(cancelButton);

      Window parent = ATKGraphicsUtils.getWindowForComponent(this);
      if(parent instanceof Frame)
        settingsDialog = new JDialog((Frame)parent, true);
      else if(parent instanceof Dialog)
        settingsDialog = new JDialog((Dialog)parent, true);
      else
        settingsDialog = new JDialog((Frame)null, true);

      settingsDialog.setResizable(false);
      settingsDialog.setContentPane(settingsPanel);
      settingsDialog.setTitle("Image viewer settings");

    }

  }


  private void refreshStatusAndProlfile() {
    synchronized (this) {
      refreshStatusLine();
      refreshLineProfile();
    }
  }

  // ----------------------------------------------------------
  // Action Listener
  // ----------------------------------------------------------
  public void actionPerformed(ActionEvent evt) {

    if (evt.getSource() == selectButton ||
        evt.getSource() == selectionMenuItem) {

      imagePanel.clearSelection();
      imagePanel.setSelectionMode(1);
      profileMode = 0;
      freePopup();

      synchronized (this) {
        refreshStatusLine();
        refreshSelectionMinMax();
      }

    } else if (evt.getSource() == selectMaxButton ||
        evt.getSource() == selectionMaxMenuItem) {

      Dimension d = imagePanel.getImageSize();
      imagePanel.setSelection(0, 0, d.width, d.height);
      selectionChanged();

    } else if (evt.getSource() == selectColorButton ||
            evt.getSource() == selectionColorMenuItem) {

      changeSelectionColor();

    } else if (evt.getSource() == fileButton ||
            evt.getSource() == fileMenuItem ) {

      saveFile();

    } else if (evt.getSource() == profileButton ||
        evt.getSource() == lineProfileMenuItem) {

      imagePanel.setSelectionMode(JImage.MODE_LINE);
      constructLineProfiler();
      lineProfiler.setLineProfileMode();
      lineProfiler.setVisible(true);
      profileMode = 1;
      refreshDblProfileMenu();
      refreshStatusAndProlfile();

    } else if (evt.getSource() == profile2Button ||
        evt.getSource() == lineProfile2MenuItem) {

      imagePanel.setSelectionMode(JImage.MODE_CROSS);
      constructLineProfiler();
      lineProfiler.setMode(LineProfilerViewer.LINE_MODE_DOUBLE);
      lineProfiler.setVisible(true);
      profileMode = 3;
      refreshDblProfileMenu();
      refreshStatusAndProlfile();

    } else if (evt.getSource() == histoButton ||
        evt.getSource() == histogramMenuItem) {

      imagePanel.setSelectionMode(JImage.MODE_RECT);
      constructLineProfiler();
      lineProfiler.setHistogramMode();
      lineProfiler.setVisible(true);
      profileMode = 2;
      refreshDblProfileMenu();
      refreshStatusAndProlfile();

    } else if (evt.getSource() == bestFitMenuItem) {
      setBestFit(!isBestFit());
    } else if (evt.getSource() == negativeMenuItem) {
      setNegative(!isNegative());
    } else if (evt.getSource() == snapToGridMenuItem) {
      setAlignToGrid(!isAlignToGrid());
    } else if (evt.getSource() == toolbarMenuItem) {
      setToolbarVisible(!isToolbarVisible());
    } else if (evt.getSource() == statusLineMenuItem) {
      setStatusLineVisible(!isStatusLineVisible());
    } else if (evt.getSource() == showGradMenuItem) {
      setGradientVisible(!isGradientVisible());
    } else if (evt.getSource() == zoomButton ||
        evt.getSource() == zoomMenuItem) {
      showZoom();
    } else if (evt.getSource() == zoomCombo) {
      zoomFactor = zoomCombo.getSelectedIndex();
      synchronized (this) {
        buildZoom();
      }
      //zoomView.revalidate();
      zoomDialog.pack();
    } else if (evt.getSource() == settingsButton ||
        evt.getSource() == settingsMenuItem) {
      showSettings();
    } else if (evt.getSource() == autoBestFitCheck) {
      autoBestFit = !autoBestFit;
      minBestFitLabel.setEnabled(!autoBestFit);
      minBestFitText.setEnabled(!autoBestFit);
      maxBestFitLabel.setEnabled(!autoBestFit);
      maxBestFitText.setEnabled(!autoBestFit);
    } else if (evt.getSource() == cancelButton) {
      settingsDialog.setVisible(false);
    } else if (evt.getSource() == axisButton) {
      showAxisDialog();
    } else if (evt.getSource() == axisCloseButton) {
      axisDialog.setVisible(false);
    } else if (evt.getSource() == okButton) {
      applySettings();
    } else if (evt.getSource() == tableButton ||
        evt.getSource() == tableMenuItem) {
      showTable();
    } else if (evt.getSource() == propButton) {
      showPropertyFrame();
    } else if (evt.getSource() == gradButton) {
      showGradientEditor();
    } else if (evt.getSource() == loadButton || evt.getSource() == loadMenuItem) {
      loadButtonActionPerformed();
    } else if (evt.getSource() == saveButton || evt.getSource() == saveMenuItem) {
      saveButtonActionPerformed();
    } else if (evt.getSource() == saveDataFileMenuItem) {
          saveDataFile();
    } else if (evt.getSource() == printButton || evt.getSource() == printMenuItem) {
      printImage();
    } else if (evt.getSource() == displayLogMenuItem) {
      setLogValues( displayLogMenuItem.isSelected() );
    } else if (evt.getSource() == vLeftCheckMenuItem) {
      imagePanel.setVerticalPosition(JImage.VERTICAL_LEFT);
      refreshStatusAndProlfile();
    } else if (evt.getSource() == vCenterCheckMenuItem) {
      imagePanel.setVerticalPosition(JImage.VERTICAL_CENTER);
      refreshStatusAndProlfile();
    } else if (evt.getSource() == vRigthCheckMenuItem) {
      imagePanel.setVerticalPosition(JImage.VERTICAL_RIGHT);
      refreshStatusAndProlfile();
    } else if (evt.getSource() == hTopCheckMenuItem) {
      imagePanel.setHorizontalPosition(JImage.HORIZONTAL_TOP);
      refreshStatusAndProlfile();
    } else if (evt.getSource() == hCenterCheckMenuItem) {
      imagePanel.setHorizontalPosition(JImage.HORIZONTAL_CENTER);
      refreshStatusAndProlfile();
    } else if (evt.getSource() == hBottomCheckMenuItem) {
      imagePanel.setHorizontalPosition(JImage.HORIZONTAL_BOTTOM);
      refreshStatusAndProlfile();
    }

  }

  // ----------------------------------------------------------
  // Mouse Listener
  // ----------------------------------------------------------

  public void mouseDragged(MouseEvent e) {
    mouseMoved(e);
  }

  synchronized public void mouseMoved(MouseEvent e) {

    Dimension imgsize = getCurrentImageSize();

    if (doubleValues != null) {

      if (e.getSource() == imagePanel) {

        int x;
        int y;
        if(iSz<0) {
          x = (e.getX() - imagePanel.getXOrigin()) / (-iSz);
          y = (e.getY() - imagePanel.getYOrigin()) / (-iSz);
        } else {
          x = (e.getX() - imagePanel.getXOrigin()) * iSz;
          y = (e.getY() - imagePanel.getYOrigin()) * iSz;
        }

        if ((x >= imgsize.width) || (y >= imgsize.height) || (y < 0) || (x < 0)) {
          statusLabel.setText(getLabelInfoString());
        } else {
          statusLabel.setText(getLabelInfoString() + " (" + x + "," + y + ")=" + Double.toString(doubleValues[y][x]));
        }


        refreshStatusLine();

      } else if (e.getSource() == zoomImage) {


        /*
        int x = e.getX() - zoomImage.getXOrigin();
        int y = e.getY() - zoomImage.getYOrigin();
        int xc = x / (zoomFactor + 1) + zoomXOrg;
        int yc = y / (zoomFactor + 1) + zoomYOrg;
        zoomText.setText("(" + x + "," + y + ") => (" + xc + "," + yc + ")  org=(" + zoomXOrg + "," + zoomYOrg + ") v=" + Double.toString(doubleValues[yc][xc]) );
        */

        int x = (e.getX() - zoomImage.getXOrigin()) / (zoomFactor + 1) + zoomXOrg;
        int y = (e.getY() - zoomImage.getYOrigin()) / (zoomFactor + 1) + zoomYOrg;

        if ((x >= imgsize.width) || (y >= imgsize.height) || (y < 0) || (x < 0)) {
          zoomText.setText("");
        } else {
          zoomText.setText("(" + x + "," + y + ") " + Double.toString(doubleValues[y][x]));
        }

      }

    } else {

      statusLabel.setText("");

    }

  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseClicked(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
    Rectangle newSelection = imagePanel.getSelectionRect();
    if (newSelection != null) {
      if (oldSelection == null) {
        selectionChanged();
      } else if (!newSelection.equals(oldSelection)) {
        selectionChanged();
      }
    }
    oldSelection = newSelection;
  }

  public void mousePressed(MouseEvent e) {
    // Right button click
    if (e.getButton() == MouseEvent.BUTTON3) {
      if (showingMenu && e.getSource()==imagePanel) {
        bestFitMenuItem.setSelected(isBestFit());
        snapToGridMenuItem.setSelected(isAlignToGrid());
        negativeMenuItem.setSelected(isNegative());
        toolbarMenuItem.setSelected(isToolbarVisible());
        statusLineMenuItem.setSelected(isStatusLineVisible());
        showGradMenuItem.setSelected(isGradientVisible());
        imgMenu.show(imagePanel, e.getX() , e.getY() );
      }
    }

  }
  
  // ----------------------------------------------------------
  // Keyboard listener
  // ----------------------------------------------------------

  public void keyPressed(KeyEvent e) {

    if (e.getSource() == selText) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        // Browse the line
        String sel = selText.getText();


        try {

          String x1Str;
          String y1Str;
          String x2Str;
          String y2Str;

          sel = sel.substring(sel.indexOf('(') + 1);
          x1Str = sel.substring(0, sel.indexOf(','));
          sel = sel.substring(sel.indexOf(',') + 1);
          y1Str = sel.substring(0, sel.indexOf(')'));


          if (imagePanel.getSelectionMode() == 0) {
            sel = sel.substring(sel.indexOf('(') + 1);
            x2Str = sel.substring(0, sel.indexOf(','));
            sel = sel.substring(sel.indexOf(',') + 1);
            y2Str = sel.substring(0, sel.indexOf(')'));
          } else {
            sel = sel.substring(sel.indexOf('[') + 1);
            x2Str = sel.substring(0, sel.indexOf(','));
            sel = sel.substring(sel.indexOf(',') + 1);
            y2Str = sel.substring(0, sel.indexOf(']'));
          }

          int x1,y1,x2,y2;
          if(iSz<0) {
            x1 = Integer.parseInt(x1Str) * (-iSz);
            y1 = Integer.parseInt(y1Str) * (-iSz);
            x2 = Integer.parseInt(x2Str) * (-iSz);
            y2 = Integer.parseInt(y2Str) * (-iSz);
          } else {
            x1 = Integer.parseInt(x1Str) / iSz;
            y1 = Integer.parseInt(y1Str) / iSz;
            x2 = Integer.parseInt(x2Str) / iSz;
            y2 = Integer.parseInt(y2Str) / iSz;
          }
          if (imagePanel.getSelectionMode() == 0) {
            imagePanel.setSelection(x1, y1, x2, y2);
          } else {
            imagePanel.setSelection(x1, y1, x1 + x2, y1 + y2);
          }


        } catch (Exception ex) {

          JOptionPane.showMessageDialog(null, "Invalid syntax for selection", "Error", JOptionPane.ERROR_MESSAGE);

        }

        Rectangle newSelection = imagePanel.getSelectionRect();
        if (newSelection != null) {
          if (oldSelection == null) {
            selectionChanged();
          } else if (!newSelection.equals(oldSelection)) {
            selectionChanged();
          }
        }
        oldSelection = newSelection;

        refreshStatusLine();
      }
    }
    else if (e.getSource() == imagePanel) {
      switch ( e.getKeyCode() ) {
        case KeyEvent.VK_UP:
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_RIGHT:
            synchronized(this) {
                refreshStatusLine();
                refreshSelectionMinMax();
                refreshLineProfile();
            }
            break;
      }
    }

  }

  public void keyTyped(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
  }

  // ----------------------------------------------------------
  // Image Listener
  // ----------------------------------------------------------

  public void errorChange(fr.esrf.tangoatk.core.ErrorEvent errorEvent) {
      setData(null);
  }

  public void stateChange(fr.esrf.tangoatk.core.AttributeStateEvent evt) {
  }

  public void imageChange(fr.esrf.tangoatk.core.NumberImageEvent evt) {
    if (logValues) {
      setData( computeLog( evt.getValue() ) );
    }
    else {
      setData(evt.getValue());
    }
  }

  // ----------------------------------------------------------
  // Model stuff
  // ----------------------------------------------------------

  /**<code>setModel</code> Set the model.
   * @param v  Value to assign to model. This image must have a height equals to 2.
   */
  public void setModel(INumberImage v) {
    // Free old model
    clearModel();

    if (settingsDialog != null)
      attNameLabel.setModel(v);

    // Init new one
    if (v != null) {

      // Reset viewer
      imagePanel.setImage(null);
      firstRefresh = !userZoom;
      freePopup();

      // Init new model
      model = v;
      model.addImageListener(this);
      // Force a reading to initialise the viewer size before
      // make it visible
      model.refresh();
    }
  }

  /**
   * Removes all  listener belonging to the viewer.
   */
  public void clearModel() {
    if (model == null) return;
    
    model.removeImageListener(this);
    model = null;
    if (imagePanel != null) imagePanel.setImage(null);
  }

  // ----------------------------------------------------------
  // Image low level routines
  // Perform transformation
  // ----------------------------------------------------------

  private void saveEdf(String filename) {

    if (doubleValues == null) {
      JOptionPane.showMessageDialog(this, "No data to save.");
      return;
    }

    Dimension d = getCurrentImageSize();
    Rectangle r = imagePanel.getSelectionRect();

    if (r == null)
      r=new Rectangle(0,0,d.width,d.height);
    else
      mulRect(r);

    if (r.width <= 0 || r.height <= 0) {
      JOptionPane.showMessageDialog(this, "No area selected.");
      return;
    }

    try {

      DataOutputStream fw = new DataOutputStream(new FileOutputStream(filename));

      StringBuffer to_write= new StringBuffer();

      to_write.append("{\n");
      to_write.append("HeaderID       = EH:000001:000000:000000 ;\n");
      to_write.append("Image          = 1 ;\n");
      to_write.append("ByteOrder      = LowByteFirst ;\n");
      to_write.append("DataType       = UnsignedShort ;\n");
      to_write.append("Dim_1          = " + r.width + " ;\n");
      to_write.append("Dim_2          = " + r.height + " ;\n");
      to_write.append("Size           = " + r.width * r.height * 2 + " ;\n");
      to_write.append("count_time     = 0 ;\n");
      to_write.append("point_no       = 0 ;\n");
      to_write.append("preset         = 1.0 ;\n");
      to_write.append("col_end        = " + (r.width-1 ) + " ;\n");
      to_write.append("row_end        = " + (r.height-1) + " ;\n");
      to_write.append("dir            = . ;\n");
      to_write.append("suffix         = edf ;\n");
      to_write.append("prefix         = ;\n");
      to_write.append("run            = 1 ;\n");
      to_write.append("description    = ;\n");
      to_write.append("title          = snapshot ;\n");
      int l = to_write.length();
      while(l<1022) { to_write.append(' ');l++; }
      to_write.append("}\n");

      fw.writeBytes(to_write.toString());

      /*
      *(short *)(edf_header + 1018) = (short) w;
      *(short *)(edf_header + 1020) = (short) h;

      *(short *)(edf_header + 1000) = (short) w;
      *(short *)(edf_header + 1002) = (short) h;
      */

      // Write data
      to_write = new StringBuffer();
      
      for (int j = r.y; j < r.y + r.height; j++) {
        for (int i = r.x; i < r.x + r.width; i++) {
          int v = (int)doubleValues[j][i];
          to_write.append((char)(v & 0xFF)); //Low bytes first
          to_write.append((char)( (v >> 8) & 0xFF ));
        }
      }

      //long t0 = System.currentTimeMillis();
      fw.writeBytes(to_write.toString());
      //long t1 = System.currentTimeMillis();
      //System.out.println("writeBytes takes " + (t1-t0) + " ms");

      fw.close();

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error during saving file.\n" + e.getMessage());
    }

  }

  private String getLabelInfoString() {
    Dimension imgsize = getCurrentImageSize();
    int percent;
    if(iSz<0)
      percent = (int)( 100.0 * (double)(-iSz) );
    else
      percent = (int)( 100.0 / (double)iSz );

    return percent + "% [" + imgsize.width + "," + imgsize.height + "]";
  }


  private void computeVerticalExtent() {

    if (doubleValues == null)
      return;

    if( verticalExtent==1 )
      return;

    int dimy = doubleValues.length;
    if(dimy == 0)
      return;

    int dimx = doubleValues[0].length;
    if(dimx == 0)
      return;

    double[][] newValues = new double[(dimy-1)*verticalExtent + 1][dimx];

    for(int j=0;j<dimy-1;j++) {
      for(int i=0;i<dimx;i++) {
        for(int k=0;k<verticalExtent;k++) {
          double yup   = doubleValues[j][i];
          double ydown = doubleValues[j+1][i];
          double r = (double)k / (double)verticalExtent;
          newValues[j*verticalExtent+k][i] = yup * (1.0-r) + ydown * r;
        }
      }
    }
    // Last line
    int k = (dimy-1)*verticalExtent;
    for(int i=0;i<dimx;i++) {
      newValues[k][i] = doubleValues[dimy-1][i];
    }

    doubleValues = newValues;

  }

  private void computeAutoZoom() {

    if (doubleValues == null)
      return;

    if (firstRefresh || autoZoom) {

      if (doubleValues.length > 0) {
        int dimy = doubleValues.length;
        if (dimy > 0) {
          int dimx = doubleValues[0].length;

          // Auto calculate best image size
          int sz = 1;

          if( dimx>800 || dimy>600 ) {

            // Search smaller size
            while ((dimx > 800 || dimy > 600) && (sz < 8) && ((dimx % 2) == 0) && ((dimy % 2) == 0)) {
              dimx = dimx / 2;
              dimy = dimy / 2;
              sz *= 2;
            }
            iSz = sz;

          } else {

            // Search bigger size
            while ((dimx < 600 && dimy < 400) && (sz < 4) ) {
              dimx = dimx * 2;
              dimy = dimy * 2;
              sz *= 2;
            }
            if(sz!=1)
             iSz = -sz;
            else
              iSz = 1;

          }

          if(iSz<0)
            imagePanel.setMarkerScale((double) -iSz);
          else
            imagePanel.setMarkerScale(1.0 / (double) iSz);
          
          firstRefresh = false;

        }
      }

    }

  }

  private void preComputeBestFit() {

    int i,j;

    if (!isBestFit) {
      gradientTool.getAxis().setMinimum(0.0);
      gradientTool.getAxis().setMaximum(65536.0);
      gradientTool.repaint();
      return;
    }

    if (doubleValues == null)
      return;

    if (autoBestFit) {

      autoBfMin = 65536.0;
      autoBfMax = 0.0;

      for (j = 0; j < doubleValues.length; j++)
        for (i = 0; i < doubleValues[j].length; i++) {
          double v = doubleValues[j][i];
          if ( (!Double.isNaN(v)) && (!Double.isInfinite(v)) )
          {
            // ignore NaN and Infinite values
            if (v > autoBfMax) autoBfMax = v;
            if (v < autoBfMin) autoBfMin = v;
          }
        }
      if (autoBfMin == 65536.0 && autoBfMax == 0.0)
      {
          autoBfMin = 0;
          autoBfMax = 0;
      }

      bfa0 = -autoBfMin;

      if (autoBfMax == autoBfMin) {
        // Uniform picture
        bfa1 = 0.0;
        gradientTool.getAxis().setMinimum(autoBfMin);
        gradientTool.getAxis().setMaximum(autoBfMax+1.0);
        gradientTool.repaint();
      } else {
        bfa1 = (65536.0) / (autoBfMax - autoBfMin);
        gradientTool.getAxis().setMinimum(autoBfMin);
        gradientTool.getAxis().setMaximum(autoBfMax);
        gradientTool.repaint();
      }

    } else {

      bfa0 = -bfMin;

      if (bfMax == bfMin)
        bfa1 = 0.0;
      else
        bfa1 = (65536.0) / (bfMax - bfMin);

      gradientTool.getAxis().setMinimum(bfMin);
      gradientTool.getAxis().setMaximum(bfMax);
      gradientTool.repaint();

    }

  }

  private int bestFit(double v) {
    int nv = (int) ((bfa0 + v) * bfa1);
    if (nv < 0) return 0;
    if (nv > 65535) return 65535;
    return nv;
  }

  private boolean buildZoom() {

    if (doubleValues == null)
      return false;

    Rectangle r = imagePanel.getSelectionRect();

    if (r == null)
      return false;

    mulRect(r);

    if (r.width <= 0 || r.height <= 0)
      return false;

    zoomXOrg = r.x;
    zoomYOrg = r.y;

    int zf = zoomFactor + 1;

    BufferedImage zoomImg = new BufferedImage(r.width * zf, r.height * zf, BufferedImage.TYPE_INT_RGB);

    int[] rgb = new int[r.width * zf];

    preComputeBestFit();

    // Fill the image
    for (int j = r.y; j < r.y + r.height; j++) {
      for (int i = r.x; i < r.x + r.width; i++) {

        if (isNegative) {
          if (isBestFit) {
            int p = gColormap[(~bestFit(doubleValues[j][i])) & 65535];
            for (int k = 0; k < zf; k++) rgb[(i - r.x) * zf + k] = p;
          } else {
            int p = gColormap[(~(int) doubleValues[j][i]) & 65535];
            for (int k = 0; k < zf; k++) rgb[(i - r.x) * zf + k] = p;
          }
        } else {
          if (isBestFit) {
            int p = gColormap[bestFit(doubleValues[j][i])];
            for (int k = 0; k < zf; k++) rgb[(i - r.x) * zf + k] = p;
          } else {
            int p = gColormap[((int) doubleValues[j][i]) & 65535];
            for (int k = 0; k < zf; k++) rgb[(i - r.x) * zf + k] = p;
          }
        }


      }

      for (int k = 0; k < zf; k++)
        zoomImg.setRGB(0, (j - r.y) * zf + k, r.width * zf, 1, rgb, 0, r.width * zf);
    }

    zoomImage.setImage(zoomImg);
    return true;
  }

  // Save a screenshot
  private void saveFile() {

    int ok = JOptionPane.YES_OPTION;

    FileFilter edfFilter = new FileFilter() {
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        }
        String extension = MultiExtFileFilter.getExtension(f);
        if (extension != null && extension.equals("edf"))
          return true;
        return false;
      }

      public String getDescription() {
        return "edf - EDF pictues (Mono 16 Bits)";
      }

      public boolean equals (Object obj) {
        if (obj == null) {
          return false;
        }
        else if (obj instanceof FileFilter) {
          return getDescription().equals(
                        ( (FileFilter) obj ).getDescription()
          );
        }
        else {
          return false;
        }
      }
    };

    FileFilter jpgFilter = new FileFilter() {
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        }
        String extension = MultiExtFileFilter.getExtension(f);
        if (extension != null && extension.equals("jpg"))
          return true;
        return false;
      }

      public String getDescription() {
        return "jpg - JPEG pictures (Color 24 Bits)";
      }

      public boolean equals (Object obj) {
        if (obj == null) {
          return false;
        }
        else if (obj instanceof FileFilter) {
          return getDescription().equals(
                        ( (FileFilter) obj ).getDescription()
          );
        }
        else {
          return false;
        }
      }
    };

    FileFilter jpg8Filter = new FileFilter() {
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        }
        String extension = MultiExtFileFilter.getExtension(f);
        if (extension != null && extension.equals("jpg"))
          return true;
        return false;
      }

      public String getDescription() {
        return "jpg - JPEG pictures (Mono 8 Bits)";
      }

      public boolean equals (Object obj) {
        if (obj == null) {
          return false;
        }
        else if (obj instanceof FileFilter) {
          return getDescription().equals(
                        ( (FileFilter) obj ).getDescription()
          );
        }
        else {
          return false;
        }
      }
    };

    FileFilter pngFilter = new FileFilter() {
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        }
        String extension = MultiExtFileFilter.getExtension(f);
        if (extension != null && extension.equals("png"))
          return true;
        return false;
      }

      public String getDescription() {
        return "png - PNG pictures (Color 24 Bits)";
      }

      public boolean equals (Object obj) {
        if (obj == null) {
          return false;
        }
        else if (obj instanceof FileFilter) {
          return getDescription().equals(
                        ( (FileFilter) obj ).getDescription()
          );
        }
        else {
          return false;
        }
      }
    };

    FileFilter png8Filter = new FileFilter() {
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        }
        String extension = MultiExtFileFilter.getExtension(f);
        if (extension != null && extension.equals("png"))
          return true;
        return false;
      }

      public String getDescription() {
        return "png - PNG pictures (Mono 8 Bits)";
      }

      public boolean equals (Object obj) {
        if (obj == null) {
          return false;
        }
        else if (obj instanceof FileFilter) {
          return getDescription().equals(
                        ( (FileFilter) obj ).getDescription()
          );
        }
        else {
          return false;
        }
      }
    };

    FileFilter[] filters = new FileFilter[5];
    filters[0] = edfFilter;
    filters[1] = png8Filter;
    filters[2] = pngFilter;
    filters[3] = jpg8Filter;
    filters[4] = jpgFilter;
    JFileChooser chooser = new JFileChooser(lastSnapshotLocation);
    for (int i = 0; i < filters.length; i++) {
      if ( filters[i].equals(lastFileFilter) ) {
        continue;
      }
      chooser.addChoosableFileFilter(filters[i]);
    }
    if ( lastFileFilter != null ) {
      chooser.addChoosableFileFilter(lastFileFilter);
    }
    chooser.setDialogTitle("Save snapshot");
    int returnVal = chooser.showSaveDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      if (f != null) {
        lastSnapshotLocation = f.getParentFile().getAbsolutePath();
        FileFilter filter = chooser.getFileFilter();

        if (edfFilter.equals(filter)) {
          if (MultiExtFileFilter.getExtension(f) == null || !MultiExtFileFilter.getExtension(f).equalsIgnoreCase("edf")) {
            f = new File(f.getAbsolutePath() + ".edf");
          }
          lastFileFilter = filter;
        } else if (jpgFilter.equals(filter) || jpg8Filter.equals(filter)) {
          if (MultiExtFileFilter.getExtension(f) == null || !MultiExtFileFilter.getExtension(f).equalsIgnoreCase("jpg")) {
            f = new File(f.getAbsolutePath() + ".jpg");
          }
          lastFileFilter = filter;
        } else if (pngFilter.equals(filter) || png8Filter.equals(filter)) {
          if (MultiExtFileFilter.getExtension(f) == null || !MultiExtFileFilter.getExtension(f).equalsIgnoreCase("png")) {
            f = new File(f.getAbsolutePath() + ".png");
          }
          lastFileFilter = filter;
        } else {
          JOptionPane.showMessageDialog(this, "Please select a valid image format", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (f.exists())
          ok = JOptionPane.showConfirmDialog(this, "Do you want to overwrite " + f.getName() + " ?", "Confirm overwrite", JOptionPane.YES_NO_OPTION);

        if (ok == JOptionPane.YES_OPTION) {

          if (edfFilter.equals(filter)) {

            saveEdf(f.getAbsolutePath());

          } else if (jpgFilter.equals(filter)) {

            try {
              ImageIO.write(getSelectionImage(), "jpg", f);
            }
            catch (IOException ioe) {
              JOptionPane.showMessageDialog(this, "File could not be saved", "Error", JOptionPane.ERROR_MESSAGE);
            }

          } else if (jpg8Filter.equals(filter)) {

            try {
              ImageIO.write(get8BitImage(), "jpg", f);
            }
            catch (IOException ioe) {
              JOptionPane.showMessageDialog(this, "File could not be saved", "Error", JOptionPane.ERROR_MESSAGE);
            }

          } else if (pngFilter.equals(filter)) {

            try {
              ImageIO.write(getSelectionImage(), "png", f);
            }
            catch (IOException ioe) {
              JOptionPane.showMessageDialog(this, "File could not be saved", "Error", JOptionPane.ERROR_MESSAGE);
            }

          } else if (png8Filter.equals(filter)) {

            try {
              ImageIO.write(get8BitImage(), "png", f);
            }
            catch (IOException ioe) {
              JOptionPane.showMessageDialog(this, "File could not be saved", "Error", JOptionPane.ERROR_MESSAGE);
            }

          } // end tests for file filter

        } // end if (ok == JOptionPane.YES_OPTION)
      } // end if (f != null)
    } // end if (returnVal == JFileChooser.APPROVE_OPTION)

    pngFilter = null;
    png8Filter = null;
    jpgFilter = null;
    jpg8Filter = null;
    edfFilter = null;
    for (int i = 0; i < filters.length; i++) {
      filters[i] = null;
    }
    filters = null;

  }

  private BufferedImage getSelectionImage() {

    Rectangle r = imagePanel.getSelectionRect();
    if( r!=null ) {

      BufferedImage newImage = new BufferedImage(r.width,r.height,BufferedImage.TYPE_INT_RGB);
      Graphics2D g2 = newImage.createGraphics();
      g2.drawImage(imagePanel.getImage(),0 ,0 ,r.width-1 ,r.height-1 ,
                                         r.x, r.y, r.x+r.width-1, r.y+r.height-1,null);
      g2.dispose();
      return newImage;

    } else {

      int w = imagePanel.getImage().getWidth();
      int h = imagePanel.getImage().getHeight();
      BufferedImage newImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
      Graphics2D g2 = newImage.createGraphics();
      g2.drawImage(imagePanel.getImage(),0,0,null);
      g2.dispose();
      return newImage;

    }

  }

  private BufferedImage get8BitImage() {

    // Convert to 8 bits
    Rectangle r = imagePanel.getSelectionRect();
    if( r!=null ) {

      BufferedImage newImage = new BufferedImage(r.width,r.height,BufferedImage.TYPE_BYTE_GRAY);
      Graphics2D g2 = newImage.createGraphics();
      g2.drawImage(imagePanel.getImage(),0 ,0 ,r.width-1 ,r.height-1 ,
                                       r.x, r.y, r.x+r.width-1, r.y+r.height-1,null);
      g2.dispose();
      return newImage;

    } else {

      int w = imagePanel.getImage().getWidth();
      int h = imagePanel.getImage().getHeight();
      BufferedImage newImage = new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
      Graphics2D g2 = newImage.createGraphics();
      g2.drawImage(imagePanel.getImage(),0,0,null);
      g2.dispose();
      return newImage;

    }

  }

 //	Added by Pascal Verdier to manage a specified color for NaN values.
  
 /**
  *	Set a specified color for NaN values.
  *
  *	@param color the specified color for NaN values (if null NaN color is not managed) 
  */
  public void setNaNcolor(Color color)
  {
       if (color==null)
           rgbNaN = 0;
       else
           rgbNaN = color.getRGB();
       colorNaN = color;
  }

  // Convert the image from TANGO format to SCREEN format according
  // to transformation
  private void convertImage() {

    if (doubleValues == null)
      return;

    if (doubleValues.length > 0) {
      int dimy = doubleValues.length;
      if (dimy > 0) {
        int dimx = doubleValues[0].length;

        //long t1 = System.currentTimeMillis();

        BufferedImage lastImg = imagePanel.getImage();

        int rdimx,rdimy;
        if(iSz<0) {
          rdimx = dimx * (-iSz);
          rdimy = dimy * (-iSz);
        } else {
          rdimx = dimx / iSz;
          rdimy = dimy / iSz;
        }

        if (lastImg == null || lastImg.getHeight() != rdimy || lastImg.getWidth() != rdimx) {
          // Recreate the image
          //System.out.println("Creating new Image:" + rdimx + "," + rdimy + " Old=" + lastImg);
          lastImg = new BufferedImage(rdimx, rdimy, BufferedImage.TYPE_INT_RGB);
          imagePanel.setImage(lastImg);
          freePopup();
          statusLabel.setText(getLabelInfoString());
          refreshComponents();
        }

        int[] rgb = new int[rdimx];
        preComputeBestFit();

        /*
        int type = model.getProperty("data_type").getIntValue();
        switch (type) {
          case fr.esrf.TangoDs.TangoConst.Tango_DEV_SHORT:
            ratio = 256.0;
            break;
          case fr.esrf.TangoDs.TangoConst.Tango_DEV_LONG:
            ratio = 65536.0 * 256.0;
            break;
        }
        */

        // Fill the image
        if (iSz < 0) {

          // Bigger image
          int sz = -iSz;

          for (int j = 0; j<dimy; j++)
          {
               if (colorNaN==null) //	NaN color not managed
               {
		     
        	 if (isBestFit)
                 {

                    if (isNegative) {
                      for (int i = 0; i < dimx; i++) {
                            int c = gColormap[(~bestFit(doubleValues[j][i])) & 65535];
                            for(int k=0;k<sz;k++)
                          rgb[i*sz+k] = c;
                      }
                    } else {
                      for (int i = 0; i < dimx; i++) {
                            int c = gColormap[bestFit(doubleValues[j][i])];
                            for(int k=0;k<sz;k++)
                          rgb[i*sz+k] = c;
                      }
                    }

                 } 
                 else // not BestFit
                 {

                    if (isNegative) {
                      for (int i = 0; i < dimx; i++) {
                            int c = gColormap[(~(int)(doubleValues[j][i])) & 65535];
                            for(int k=0;k<sz;k++)
                          rgb[i*sz+k] = c;
                      }
                    } else {
                      for (int i = 0; i < dimx; i++) {
                            int c = gColormap[((int) doubleValues[j][i]) & 65535];
                            for(int k=0;k<sz;k++)
                          rgb[i*sz+k] = c;
                      }
                    }

                 }
               }
               else //	Manage NaN NaN color
               {
		      
         	 if (isBestFit)
                 {
                    if (isNegative) {
                      for (int i = 0; i < dimx; i++) {
                            int c = (Double.isNaN(doubleValues[j][i])) ? rgbNaN :
                                                                    gColormap[(~bestFit(doubleValues[j][i])) & 65535];
                            for(int k=0;k<sz;k++)
                               rgb[i*sz+k] = c;
                      }
                    } else {
                      for (int i = 0; i < dimx; i++) {
                            int c = (Double.isNaN(doubleValues[j][i])) ? rgbNaN :
                                                                    gColormap[bestFit(doubleValues[j][i])];
                            for(int k=0;k<sz;k++)
                               rgb[i*sz+k] = c;
                      }
                    }

                 }
                 else // not BestFit
                 {
            	    if (isNegative) {
                      for (int i = 0; i < dimx; i++) {
                            int c = (Double.isNaN(doubleValues[j][i])) ? rgbNaN :
                                                                    gColormap[(~(int)(doubleValues[j][i])) & 65535];
                            for(int k=0;k<sz;k++)
                               rgb[i*sz+k] = c;
                      }
            	    } else {
                      for (int i = 0; i < dimx; i++) {
                            int c = (Double.isNaN(doubleValues[j][i])) ? rgbNaN :
                                                                    gColormap[((int) doubleValues[j][i]) & 65535];
                            for(int k=0;k<sz;k++)
                                rgb[i*sz+k] = c;
                      }
            	    }

                 }
               }
            for(int k=0;k<sz;k++)
              lastImg.setRGB(0, j*sz+k, rdimx, 1, rgb, 0, rdimx);

          }

        } 
        else //Smaller
        {
           if (colorNaN==null)
           { //	NaN color not managed
             
             for (int j = 0,l = 0; l < rdimy; j += iSz, l++) {

               if (isBestFit) {

            	 if (isNegative) {
                   for (int i = 0,k = 0; k < rdimx; i += iSz, k++)
                	 rgb[k] = gColormap[(~bestFit(doubleValues[j][i])) & 65535];
            	 } else {
                   for (int i = 0,k = 0; k < rdimx; i += iSz, k++)
                	 rgb[k] = gColormap[bestFit(doubleValues[j][i])];
            	 }

               } else {

            	 if (isNegative) {
                   for (int i = 0,k = 0; k < rdimx; i += iSz, k++)
                	 rgb[k] = gColormap[(~(int) doubleValues[j][i]) & 65535];
            	 } else {
                   for (int i = 0,k = 0; k < rdimx; i += iSz, k++)
                	 rgb[k] = gColormap[((int) doubleValues[j][i]) & 65535];
            	 }
               }
               lastImg.setRGB(0, l, rdimx, 1, rgb, 0, rdimx);
             }	//	end of for (int j = 0,l = 0; ..
           }
	   else // Manage NaN NaN color
	   {
		      
             for (int j = 0,l = 0; l < rdimy; j += iSz, l++) {

               if (isBestFit) {

            	 if (isNegative) {
                   for (int i = 0,k = 0; k < rdimx; i += iSz, k++)
                     if (Double.isNaN(doubleValues[j][i]))
		        rgb[k] = rgbNaN;
		     else
                    	rgb[k] = gColormap[(~bestFit(doubleValues[j][i])) & 65535];
            	 } else {
                   for (int i = 0,k = 0; k < rdimx; i += iSz, k++)
                     if (Double.isNaN(doubleValues[j][i]))
			rgb[k] = rgbNaN;
		     else
                    	rgb[k] = gColormap[bestFit(doubleValues[j][i])];
            	 }

               } else {

            	 if (isNegative) {
                   for (int i = 0,k = 0; k < rdimx; i += iSz, k++)
                     if (Double.isNaN(doubleValues[j][i]))
			rgb[k] = rgbNaN;
		     else
                    	rgb[k] = gColormap[(~(int) doubleValues[j][i]) & 65535];
            	 } else {
                   for (int i = 0,k = 0; k < rdimx; i += iSz, k++)
                     if (Double.isNaN(doubleValues[j][i]))
			rgb[k] = rgbNaN;
		     else
                    	rgb[k] = gColormap[((int) doubleValues[j][i]) & 65535];
            	 }
               }
               lastImg.setRGB(0, l, rdimx, 1, rgb, 0, rdimx);
             }	//	end of for (int j = 0,l = 0; ..
 	   }
        }

        imagePanel.repaint();
        imageView.revalidate();

        //long T = System.currentTimeMillis() - t1;
        //System.out.println("Image conversion:" + T + " ms.");
        return;
      } else {
        doubleValues = null;
      }
    } else {
      doubleValues = null;
    }


  }

  /**
   * Prints out this image.
   */
  public void printImage() {
    ATKGraphicsUtils.printComponent(imagePanel,"Print Image",true,0);
  }

  /**
   * @return the logValues
   */
  public boolean isLogValues () {
    return logValues;
  }

  /**
   * @param logValues the logValues to set
   */
    public void setLogValues(boolean logValues)
    {
        if (logValues != this.logValues)
        {
            synchronized (this)
            {
                if (model != null)
                {
                    double[][] values = model.getValue();
                    if (logValues)
                    {
                        setData(computeLog(values));
                    }
                    else
                    {
                        setData(values);
                    }
                }
            }
        }
        this.logValues = logValues;
    }

  protected double[][] computeLog(double[][] values) {
    if (values == null) {
      return null;
    }
    int length1 = 0, length2 = 0;
    length1 = values.length;
    if (length1 > 0) {
      length2 = values[0].length;
    }
    double[][] logs = new double[length1][length2];
//    StringBuffer testLog = new StringBuffer("\n-------------");
    for (int i = 0; i < length1; i++) {
      for (int j = 0; j < length2; j++) {
        logs[i][j] = Math.log10( values[i][j] );
//        testLog.append("\nvalues[").append(i).append("][").append(j).append("] = ").append( values[i][j] );
//        testLog.append("\nlogs[").append(i).append("][").append(j).append("] = ").append( logs[i][j] );
      }
    }
//    testLog.append("\n-------------\n");
//    System.out.println( testLog.toString() );
    return logs;
  }

  protected void changeSelectionColor() {
    Color selectionColor = JColorChooser.showDialog(
            this,
            "Choose Selection Color",
            imagePanel.getSelectionColor()
    );
    if (selectionColor != null) {
        imagePanel.setSelectionColor(selectionColor);
        imagePanel.repaint();
    }
  }

  // ----------------------------------------------------------
  // Instantiate the DualSpectrumViewer
  // ----------------------------------------------------------
  public static void main(String args[]) {

    final JFrame f = new JFrame();
    final NumberImageViewer d = new NumberImageViewer();

    //d.setShowingMenu(false);
    //d.setSelectionEnabled(false);
    //d.setBestFit(true);
    //d.setToolbarVisible(false);
    //d.setStatusLineVisible(false);
    //d.getYAxis().setVisible(true);
    //d.setImageMargin(new Insets(0,0,5,0));
    //d.setBorder(null);
    //d.setZoom(2);
    //d.setVerticalExtent(10);

    fr.esrf.tangoatk.core.AttributeList attributeList =
        new fr.esrf.tangoatk.core.AttributeList();
    final ErrorHistory errWin = new ErrorHistory();
    attributeList.addErrorListener(errWin);


    try {

//      INumberImage theAtt = (INumberImage) attributeList.add("//orion:10000/sys/machstat/tango/current_history");
      INumberImage theAtt;
      if (args.length >  0)
      {
        theAtt = (INumberImage) attributeList.add(args[0]);
      }
      else
      {
        theAtt = (INumberImage) attributeList.add("jlp/test/1/att_image");
//        theAtt = (INumberImage) attributeList.add("sy/d-tm/profile/TuneSpectra");
//        theAtt = (INumberImage) attributeList.add("//mcs-diag:10000/lego/mindstorms/tango/Vision");
//        theAtt = (INumberImage) attributeList.add("//athena:20000/id14/eh3-camera/sony/image");
      }
      d.setModel(theAtt);

    } catch (Exception e) {

      e.printStackTrace();

    }

    int mid = d.addHorizontalLineMarker(512, Color.GREEN);
    d.setCrossCursor(true);
    d.setMarkerPos(mid,0,256,0,0);
//    d.setSelectionEnabled(false);

    f.getContentPane().setLayout(new BorderLayout());
    f.getContentPane().add(d, BorderLayout.CENTER);

    JPanel panel = new JPanel();
    JButton errorBtn = new JButton("Show errors");
    errorBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        errWin.setVisible(true);
      }
    });
    panel.add(errorBtn);
    JButton diagBtn = new JButton("Show diag");
    diagBtn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fr.esrf.tangoatk.widget.util.ATKDiagnostic.showDiagnostic();
      }
    });
    panel.add(diagBtn);

    f.getContentPane().add(panel, BorderLayout.SOUTH);

    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setTitle("ImageViewer");
    f.pack();
    f.setVisible(true);

    attributeList.setRefreshInterval(1000);
    attributeList.startRefresher();


  }

}
