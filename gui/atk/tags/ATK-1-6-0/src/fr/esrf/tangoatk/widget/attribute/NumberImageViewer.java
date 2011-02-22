package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.IImageListener;
import fr.esrf.tangoatk.core.INumberImage;
import fr.esrf.tangoatk.widget.image.LineProfilerViewer;
import fr.esrf.tangoatk.widget.properties.LabelViewer;
import fr.esrf.tangoatk.widget.util.*;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.EventListener;
import java.util.Vector;
import java.io.FileWriter;
import java.io.File;

/**
 * A high level class to display a TANGO image and handle several image manipulation
 * function.
 *
 * @author  E.S.R.F
 */

public class NumberImageViewer extends JPanel implements IImageListener, MouseMotionListener, MouseListener, ActionListener, KeyListener {

  INumberImage model;

  // ------------------------------------------------------
  // Private data
  // ------------------------------------------------------
  private double[][] doubleValues = null;
  private Rectangle oldSelection = null;
  private int profileMode;
  private boolean showingMenu;
  private boolean snapToGrid;
  private boolean sigHistogram;
  private boolean isNegative;
  private int startHisto;
  private Gradient gColor;
  private int[] gColormap;
  private int iSz; // Image size
  private EventListenerList listenerList;  // list of Roi listeners
  private Insets noMargin = new Insets(0,0,0,0);

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

  private JImage imagePanel;
  private JScrollPane imageView;

  // Button panel
  private JPanel buttonPanel;
  private JButton selectButton;
  private JButton selectMaxButton;
  private JButton fileButton;
  private JButton zoomButton;
  private JButton tableButton;
  private JButton profileButton;
  private JButton histoButton;
  private JButton settingsButton;

  // Info panel
  private Font panelFont;
  private JPanel cfgPanel;
  private JLabel statusLabel;
  private JLabel minMaxLabel;
  private JLabel selLabel;
  private JTextField selText;

  // Popup menu

  private JPopupMenu imgMenu;
  private JMenuItem infoMenuItem;
  private JCheckBoxMenuItem bestFitMenuItem;
  private JCheckBoxMenuItem snapToGridMenuItem;
  private JCheckBoxMenuItem negativeMenuItem;
  private JCheckBoxMenuItem toolbarMenuItem;
  private JCheckBoxMenuItem statusLineMenuItem;
  private JMenuItem selectionMenuItem;
  private JMenuItem selectionMaxMenuItem;
  private JMenuItem fileMenuItem;
  private JMenuItem zoomMenuItem;
  private JMenuItem tableMenuItem;
  private JMenuItem lineProfileMenuItem;
  private JMenuItem histogramMenuItem;
  private JMenuItem settingsMenuItem;

  // ------------------------------------------------------
  // LineProfiler panel components
  // ------------------------------------------------------
  private LineProfilerViewer lineProfiler = null;


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
  private JCheckBox bestFitCheck;
  private JCheckBox autoBestFitCheck;
  private JLabel minBestFitLabel;
  private JTextField minBestFitText;
  private JLabel maxBestFitLabel;
  private JTextField maxBestFitText;
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
  // Table panel components
  // ------------------------------------------------------
  private JFrame tableDialog = null;
  private JTableRow tablePanel;

  // ------------------------------------------------------
  // PropertyFrame
  // ------------------------------------------------------
  SimplePropertyFrame propDialog = null;

  /**
   * Create a new NumberImageViewer
   */
  public NumberImageViewer() {

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEtchedBorder());

    // ------------------------------------------------------
    // Main panel
    // ------------------------------------------------------
    imagePanel = new JImage();
    imagePanel.setBorder(null);
    imagePanel.setSnapGrid(8);
    imageView = new JScrollPane(imagePanel);
    add(imageView, BorderLayout.CENTER);

    // ------------------------------------------------------
    // Toolbar
    // ------------------------------------------------------
    buttonPanel = new JPanel();
    buttonPanel.setLayout(null);
    buttonPanel.setPreferredSize(new Dimension(40, 0));
    add(buttonPanel, BorderLayout.WEST);

    selectButton = new JButton();
    selectButton.setMargin(null);
    selectButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_select.gif")));
    selectButton.setBounds(2, 5, 36, 36);
    selectButton.setToolTipText("Free selection");
    selectButton.addActionListener(this);
    buttonPanel.add(selectButton);

    selectMaxButton = new JButton();
    selectMaxButton.setMargin(null);
    selectMaxButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_selectmax.gif")));
    selectMaxButton.setBounds(2, 45, 36, 36);
    selectMaxButton.setToolTipText("Select whole image");
    selectMaxButton.addActionListener(this);
    buttonPanel.add(selectMaxButton);

    fileButton = new JButton();
    fileButton.setMargin(null);
    fileButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_snapshot.gif")));
    fileButton.setBounds(2, 85, 36, 36);
    fileButton.setToolTipText("Save snapshot");
    fileButton.addActionListener(this);
    buttonPanel.add(fileButton);

    zoomButton = new JButton();
    zoomButton.setMargin(null);
    zoomButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_zoom.gif")));
    zoomButton.setBounds(2, 125, 36, 36);
    zoomButton.setToolTipText("Zoom selection");
    zoomButton.addActionListener(this);
    buttonPanel.add(zoomButton);

    tableButton = new JButton();
    tableButton.setMargin(null);
    tableButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_table.gif")));
    tableButton.setBounds(2, 165, 36, 36);
    tableButton.setToolTipText("Selection to table");
    tableButton.addActionListener(this);
    buttonPanel.add(tableButton);

    profileButton = new JButton();
    profileButton.setMargin(null);
    profileButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_profile.gif")));
    profileButton.setBounds(2, 210, 36, 36);
    profileButton.setToolTipText("Line profile");
    profileButton.addActionListener(this);
    buttonPanel.add(profileButton);

    histoButton = new JButton();
    histoButton.setMargin(null);
    histoButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_histo.gif")));
    histoButton.setBounds(2, 245, 36, 36);
    histoButton.setToolTipText("Histogram");
    histoButton.addActionListener(this);
    buttonPanel.add(histoButton);

    settingsButton = new JButton();
    settingsButton.setMargin(null);
    settingsButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_option.gif")));
    settingsButton.setBounds(2, 290, 36, 36);
    settingsButton.setToolTipText("Image viewer settings");
    settingsButton.addActionListener(this);
    buttonPanel.add(settingsButton);

    // ------------------------------------------------------
    // Main panel (Status Line)
    // ------------------------------------------------------

    cfgPanel = new JPanel();
    cfgPanel.setLayout(null);
    cfgPanel.setPreferredSize(new Dimension(0, 25));
    add(cfgPanel, BorderLayout.SOUTH);

    panelFont = new Font("Dialog", Font.PLAIN, 11);

    statusLabel = new JLabel("");
    statusLabel.setFont(panelFont);
    statusLabel.setBounds(5, 3, 220, 20);
    cfgPanel.add(statusLabel);

    minMaxLabel = new JLabel("");
    minMaxLabel.setFont(panelFont);
    minMaxLabel.setBounds(230, 3, 160, 20);
    cfgPanel.add(minMaxLabel);

    selLabel = new JLabel("Selection");
    selLabel.setFont(panelFont);
    selLabel.setBounds(390, 3, 55, 20);
    cfgPanel.add(selLabel);

    selText = new JTextField("None");
    selText.setMargin(noMargin);
    selText.setFont(panelFont);
    selText.setBounds(450, 3, 160, 20);
    selText.addKeyListener(this);

    cfgPanel.add(selText);

    // ------------------------------------------------------
    // (Main Panel) Popup menu
    // ------------------------------------------------------

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

    selectionMenuItem = new JMenuItem("Free selection");
    selectionMenuItem.addActionListener(this);

    selectionMaxMenuItem = new JMenuItem("Select all");
    selectionMaxMenuItem.addActionListener(this);

    lineProfileMenuItem = new JMenuItem("Line profile");
    lineProfileMenuItem.addActionListener(this);

    histogramMenuItem = new JMenuItem("Histogram");
    histogramMenuItem.addActionListener(this);

    fileMenuItem = new JMenuItem("Save selection");
    fileMenuItem.addActionListener(this);

    zoomMenuItem = new JMenuItem("Zoom selection");
    zoomMenuItem.addActionListener(this);

    settingsMenuItem = new JMenuItem("Settings");
    settingsMenuItem.addActionListener(this);

    tableMenuItem = new JMenuItem("Selection to table");
    tableMenuItem.addActionListener(this);

    imgMenu.add(infoMenuItem);
    imgMenu.add(new JSeparator());
    imgMenu.add(bestFitMenuItem);
    imgMenu.add(negativeMenuItem);
    imgMenu.add(snapToGridMenuItem);
    imgMenu.add(toolbarMenuItem);
    imgMenu.add(statusLineMenuItem);
    imgMenu.add(new JSeparator());
    imgMenu.add(selectionMenuItem);
    imgMenu.add(selectionMaxMenuItem);
    imgMenu.add(fileMenuItem);
    imgMenu.add(zoomMenuItem);
    imgMenu.add(tableMenuItem);
    imgMenu.add(new JSeparator());
    imgMenu.add(lineProfileMenuItem);
    imgMenu.add(histogramMenuItem);
    imgMenu.add(new JSeparator());
    imgMenu.add(settingsMenuItem);

    // Private stuff
    imagePanel.addMouseMotionListener(this);
    imagePanel.addMouseListener(this);

    isBestFit = false;
    setAlignToGrid(true);
    autoBestFit = true;
    sigHistogram = false;
    isNegative = false;
    showingMenu = true;
    curSelMin = 65536.0;
    curSelMax = 0.0;
    startHisto = 0;
    zoomFactor = 0; // 100%
    gColor = new Gradient();
    gColormap = gColor.buildColorMap(65536);
    iSz = 1;
    listenerList = new EventListenerList();


  }

  // -----------------------------------------------------------
  // Roi listener
  // -----------------------------------------------------------

  //Add the specified WheelSwitch Listeners
  public void addRoiListener(IRoiListener l) {
    listenerList.add(IRoiListener.class, (EventListener) l);
  }

  //Remove the specified WheelSwitch Listeners
  public void removeRoiListener(IRoiListener l) {
    listenerList.remove(IRoiListener.class, (EventListener) l);
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
    buttonPanel.setVisible(b);
  }

  /**
   * Returns true when the toolbar is visible.
   * @return Toolbar visible state
   */
  public boolean isToolbarVisible() {
    return buttonPanel.isVisible();
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
   * True to enable menu displayed when clicking on right mouse button.
   * @param b True to enable the menu
   */
  public void setShowingMenu(boolean b) {
    showingMenu = b;
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
  public void setData(double[][] v) {

    // Synchronise access to critic data
    synchronized (this) {
      doubleValues = v;
      convertImage();
      refreshComponents();
    }

    // Nothing to display
    if (doubleValues == null) {
      imagePanel.setImage(null);
      freePopup();
      imageView.revalidate();
    }

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
   * @param widht Rectangle width
   * @param height Rectangle hieght
   * @param c Marker Color
   * @return Marker id
   */
  public int addRectangleMarker(int x, int y, int width,int height,Color c) {
    return imagePanel.addRectangleMarker(x, y, width , height , c);
  }

  /**
   * Sets the position of a marker
   * @param id Marker index
   * @param x X coordinate
   * @param y Y coordinate
   * @param nWidth Rectangle width (ignored when CROSS Marker)
   * @param nHeight Rectangle height (ignored when CROSS Marker)
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

  // ----------------------------------------------------------
  // Analysing stuff
  // ----------------------------------------------------------
  private void mulRect(Rectangle r) {
    r.x *= iSz;
    r.y *= iSz;
    r.width *= iSz;
    r.height *= iSz;
  }

  private void divRect(Rectangle r) {
    r.x /= iSz;
    r.y /= iSz;
    r.width /= iSz;
    r.height /= iSz;
  }

  private void mulPoint(Point p) {

    // Hack to handle line having a vertex on a image edgde
    boolean xOk = false;
    boolean yOk = false;
    Dimension d = getCurrentImageSize();
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

/*
  private void divPoint(Point p) {
    p.x /= iSz;
    p.y /= iSz;
  }

  private void mulDimension(Dimension p) {
    p.width *= iSz;
    p.height *= iSz;
  }

  private void divDimension(Dimension p) {
    p.width /= iSz;
    p.height /= iSz;
  }
*/

  private void freePopup() {

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

  private double[] buildProfileData() {

    double[] profile;

    Dimension d = getCurrentImageSize();

    Point[] p = imagePanel.getSelectionPoint();

    if (p != null) {

      mulPoint(p[0]);
      mulPoint(p[1]);

      int dx = p[1].x - p[0].x;
      int dy = p[1].y - p[0].y;
      int adx = Math.abs(dx);
      int ady = Math.abs(dy);
      double delta;
      int i,xe,ye;

      if (adx > ady) {

        delta = (double) dy / (double) adx;
        profile = new double[adx + 1];
        xe = p[0].x;
        for (i = 0; i <= adx; i++) {
          ye = p[0].y + (int) (delta * (double) i);
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
        ye = p[0].y;
        for (i = 0; i <= ady; i++) {
          xe = p[0].x + (int) (delta * (double) i);
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

      return profile;

    } else {

      return null;

    }

  }

  private double[] buildHistogramData() {

    if (doubleValues == null)
      return null;

    double[] histo = new double[65536];
    startHisto = 0;

    Rectangle r = imagePanel.getSelectionRect();

    if (r != null) {

      mulRect(r);

      int i;
      for (i = 0; i < 65536; i++) histo[i] = 0.0;

      for (i = r.x; i < r.x + r.width; i++)
        for (int j = r.y; j < r.y + r.height; j++)
          histo[(int) doubleValues[j][i]] += 1.0;

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

        histo = null;

        startHisto = i1;
        return nhisto;

      }

    } else {

      return null;

    }

  }

  private void refreshLineProfile() {

    if (lineProfiler != null && lineProfiler.isVisible() && profileMode > 0) {

      switch (profileMode) {
        case 1:
          lineProfiler.setData(buildProfileData());
          break;
        case 2:
          double[] v = buildHistogramData();
          if (v != null) {
            lineProfiler.setData(v, startHisto);
          } else {
            lineProfiler.setData(null);
          }
          break;
      }

    }

  }

  private void refreshSelectionMinMax() {

    if (doubleValues == null) {
      minMaxLabel.setText("");
      return;
    }

    Rectangle r = imagePanel.getSelectionRect();

    if (r == null || imagePanel.getSelectionMode() != 1) {
      minMaxLabel.setText("");
      return;
    }

    mulRect(r);

    curSelMin = 65536.0;
    curSelMax = 0.0;

    for (int j = r.y; j < r.y + r.height; j++)
      for (int i = r.x; i < r.x + r.width; i++) {
        double v = doubleValues[j][i];
        if (v > curSelMax) curSelMax = v;
        if (v < curSelMin) curSelMin = v;
      }

    if (curSelMin <= curSelMax)
      minMaxLabel.setText("Range[" + Double.toString(curSelMin) +
          "," + Double.toString(curSelMax) + "]");
    else
      minMaxLabel.setText("");

  }

  private void refreshComponents() {

    refreshStatusLine();
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

  private void refreshStatusLine() {

    int m = imagePanel.getSelectionMode();
    String selStr = "None";

    switch (m) {
      case 0: // Line
        Point[] pts = imagePanel.getSelectionPoint();
        if (pts != null) {
          mulPoint(pts[0]);
          mulPoint(pts[1]);
          selStr = "Line (" + pts[0].x + "," + pts[0].y + ") - (" + pts[1].x + "," + pts[1].y + ")";
        }
        break;
      case 1:
        Rectangle sel = imagePanel.getSelectionRect();
        if (sel != null) {
          mulRect(sel);
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
      zoomDialog.show();
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

    zoomDialog.show();

  }

  private void applySettings() {

    Dimension d = getCurrentImageSize();

    String minStr = minBestFitText.getText();
    String maxStr = maxBestFitText.getText();
    String gridStr = snapToGridText.getText();

    isBestFit = bestFitCheck.isSelected();
    autoBestFit = autoBestFitCheck.isSelected();
    snapToGrid = snapToGridCheck.isSelected();
    isNegative = negativeCheck.isSelected();
    sigHistogram = sigHistogramCheck.isSelected();

    int s = imageSizeCombo.getSelectedIndex();
    switch (s) {
      case 0:
        iSz = 1;
        break;
      case 1:
        if ((d.width % 2) == 0 && (d.height % 2) == 0) {
          iSz = 2;
        } else {
          int ret = JOptionPane.showConfirmDialog(this, "The image dimensions are not multiple of 2. You may get unwanted result.\nProceed anyway ?");
          if (ret == JOptionPane.YES_OPTION) iSz = 2;
        }
        break;
      case 2:
        if ((d.width % 4) == 0 && (d.height % 4) == 0) {
          iSz = 4;
        } else {
          int ret = JOptionPane.showConfirmDialog(this, "The image dimensions are not multiple of 4. You may get unwanted result.\nProceed anyway ?");
          if (ret == JOptionPane.YES_OPTION) iSz = 4;
        }
        break;
      case 3:
        if ((d.width % 8) == 0 && (d.height % 8) == 0) {
          iSz = 8;
        } else {
          int ret = JOptionPane.showConfirmDialog(this, "The dimensions are not multiple of 8. You may get unwanted result.\nProceed anyway ?");
          if (ret == JOptionPane.YES_OPTION) iSz = 8;
        }
        break;
    }

    imagePanel.setMarkerScale(1.0 / (double) iSz);

    setAlignToGrid(snapToGrid);

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

  private void showSettings() {

    constructSettingsPanel();

    minBestFitText.setText(Double.toString(bfMin));
    maxBestFitText.setText(Double.toString(bfMax));

    minBestFitLabel.setEnabled(!autoBestFit);
    minBestFitText.setEnabled(!autoBestFit);
    maxBestFitLabel.setEnabled(!autoBestFit);
    maxBestFitText.setEnabled(!autoBestFit);

    bestFitCheck.setSelected(isBestFit);
    autoBestFitCheck.setSelected(autoBestFit);
    sigHistogramCheck.setSelected(sigHistogram);
    snapToGridCheck.setSelected(snapToGrid);
    negativeCheck.setSelected(isNegative);

    int s = 0;
    switch (iSz) {
      case 1:
        s = 0;
        break;
      case 2:
        s = 1;
        break;
      case 4:
        s = 2;
        break;
      case 8:
        s = 3;
        break;
    }

    imageSizeCombo.setSelectedIndex(s);

    snapToGridText.setText(Integer.toString(imagePanel.getSnapGrid()));

    settingsDialog.pack();
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension _scr = toolkit.getScreenSize();
    Dimension _dlg = settingsDialog.getPreferredSize();
    settingsDialog.setBounds((_scr.width - _dlg.height) / 2, (_scr.height - _dlg.height) / 2,
        _dlg.width, _dlg.height);
    settingsDialog.setVisible(true);
    settingsDialog.dispose();

    synchronized (this) {
      convertImage();
      refreshComponents();
    }

  }

  private boolean buildTable() {

    Rectangle r = imagePanel.getSelectionRect();

    if (r == null)
      return false;

    mulRect(r);

    if (r.width <= 0 || r.height <= 0)
      return false;

    Double[][] d = new Double[r.height][r.width];
    for (int j = 0; j < r.height; j++)
      for (int i = 0; i < r.width; i++)
        d[j][i] = new Double(doubleValues[r.y + j][r.x + i]);

    tablePanel.setData(d, r.x, r.y);

    return true;

  }

  private void showTable() {

    constructTablePanel();

    if(tableDialog.isVisible()) {
      // if dialog already visible simply raise the window
      tableDialog.show();
      return;
    }

    synchronized (this) {
      if (!buildTable()) return;
    }

    tableDialog.pack();
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension _scr = toolkit.getScreenSize();
    Dimension _dlg = tableDialog.getPreferredSize();
    tableDialog.setBounds((_scr.width - _dlg.height) / 2, (_scr.height - _dlg.height) / 2,
        _dlg.width, _dlg.height);
    tableDialog.show();

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
    }

  }

  private void constructLineProfiler() {
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
      settingsPanel.setMinimumSize(new Dimension(290, 245));
      settingsPanel.setPreferredSize(new Dimension(290, 245));

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

      autoBestFitCheck = new JCheckBox("Automatic best fit");
      autoBestFitCheck.setSelected(true);
      autoBestFitCheck.setFont(panelFont);
      autoBestFitCheck.setBounds(5, 50, 150, 20);
      autoBestFitCheck.setToolTipText("Generate the best fit display using maximum and minimum value of the image");
      autoBestFitCheck.addActionListener(this);
      settingsPanel.add(autoBestFitCheck);

      bestFitCheck = new JCheckBox("Best fit");
      bestFitCheck.setSelected(false);
      bestFitCheck.setFont(panelFont);
      bestFitCheck.setBounds(160, 50, 65, 20);
      bestFitCheck.setToolTipText("Display the image using the whole color range");
      settingsPanel.add(bestFitCheck);

      minBestFitLabel = new JLabel("Best fit Min");
      minBestFitLabel.setFont(panelFont);
      minBestFitLabel.setBounds(5, 75, 80, 20);
      settingsPanel.add(minBestFitLabel);

      minBestFitText = new JTextField("");
      minBestFitText.setMargin(noMargin);
      minBestFitText.setFont(panelFont);
      minBestFitText.setBounds(90, 75, 50, 20);
      settingsPanel.add(minBestFitText);

      maxBestFitLabel = new JLabel("Best fit Max");
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

      snapToGridCheck = new JCheckBox("Align to grid");
      snapToGridCheck.setSelected(false);
      snapToGridCheck.setFont(panelFont);
      snapToGridCheck.setBounds(5, 160, 100, 20);
      snapToGridCheck.setToolTipText("Align the selection to the grid");
      settingsPanel.add(snapToGridCheck);

      snapToGridLabel = new JLabel("Grid spacing");
      snapToGridLabel.setFont(panelFont);
      snapToGridLabel.setBounds(110, 160, 90, 20);
      settingsPanel.add(snapToGridLabel);

      snapToGridText = new JTextField("");
      snapToGridText.setMargin(noMargin);
      snapToGridText.setFont(panelFont);
      snapToGridText.setBounds(205, 160, 50, 20);
      settingsPanel.add(snapToGridText);

      sigHistogramCheck = new JCheckBox("Display significative data for histogram");
      sigHistogramCheck.setSelected(false);
      sigHistogramCheck.setFont(panelFont);
      sigHistogramCheck.setBounds(5, 185, 280, 20);
      sigHistogramCheck.setToolTipText("Clip the histogram to significative data");
      settingsPanel.add(sigHistogramCheck);

      okButton = new JButton();
      okButton.setText("Apply");
      okButton.setFont(panelFont);
      okButton.setBounds(5, 215, 80, 25);
      okButton.addActionListener(this);
      settingsPanel.add(okButton);

      cancelButton = new JButton();
      cancelButton.setText("Dismiss");
      cancelButton.setFont(panelFont);
      cancelButton.setBounds(205, 215, 80, 25);
      cancelButton.addActionListener(this);
      settingsPanel.add(cancelButton);

      settingsDialog = new JDialog((JFrame) null, true);
      settingsDialog.setResizable(false);
      settingsDialog.setContentPane(settingsPanel);
      settingsDialog.setTitle("Image viewer settings");
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

    } else if (evt.getSource() == fileButton) {

      int ok = JOptionPane.YES_OPTION;
      JFileChooser chooser = new JFileChooser(".");
      chooser.setDialogTitle("Save EDF 16Bits snapshot");
      int returnVal = chooser.showSaveDialog(this);

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File f = chooser.getSelectedFile();
        if (f != null) {
          if (f.exists()) ok = JOptionPane.showConfirmDialog(this, "Do you want to overwrite " + f.getName() + " ?", "Confirm overwrite", JOptionPane.YES_NO_OPTION);
          if (ok == JOptionPane.YES_OPTION)
            saveEdf(f.getAbsolutePath());
        }
      }

    } else if (evt.getSource() == profileButton ||
        evt.getSource() == lineProfileMenuItem) {

      imagePanel.setSelectionMode(0);
      constructLineProfiler();
      lineProfiler.setLineProfileMode();
      lineProfiler.setVisible(true);
      profileMode = 1;

      synchronized (this) {
        refreshStatusLine();
        refreshLineProfile();
      }

    } else if (evt.getSource() == histoButton ||
        evt.getSource() == histogramMenuItem) {

      imagePanel.setSelectionMode(1);
      constructLineProfiler();
      lineProfiler.setHistogramMode();
      lineProfiler.setVisible(true);
      profileMode = 2;

      synchronized (this) {
        refreshStatusLine();
        refreshLineProfile();
      }

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
    } else if (evt.getSource() == okButton) {
      applySettings();
    } else if (evt.getSource() == tableButton ||
        evt.getSource() == tableMenuItem) {
      showTable();
    } else if (evt.getSource() == propButton) {
      showPropertyFrame();
    } else if (evt.getSource() == gradButton) {
      showGradientEditor();
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

        int x = (e.getX() - imagePanel.getXOrigin()) * iSz;
        int y = (e.getY() - imagePanel.getYOrigin()) * iSz;

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
      if (showingMenu) {
        bestFitMenuItem.setSelected(isBestFit());
        snapToGridMenuItem.setSelected(isAlignToGrid());
        negativeMenuItem.setSelected(isNegative());
        toolbarMenuItem.setSelected(isToolbarVisible());
        statusLineMenuItem.setSelected(isStatusLineVisible());
        imgMenu.show(this, e.getX(), e.getY());
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

          int x1 = Integer.parseInt(x1Str) / iSz;
          int y1 = Integer.parseInt(y1Str) / iSz;
          int x2 = Integer.parseInt(x2Str) / iSz;
          int y2 = Integer.parseInt(y2Str) / iSz;

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

  }

  public void keyTyped(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
  }

  // ----------------------------------------------------------
  // Image Listener
  // ----------------------------------------------------------

  public void errorChange(fr.esrf.tangoatk.core.ErrorEvent errorEvent) {
  }

  public void stateChange(fr.esrf.tangoatk.core.AttributeStateEvent evt) {
  }

  public void imageChange(fr.esrf.tangoatk.core.NumberImageEvent evt) {
    setData(evt.getValue());
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

      FileWriter fw = new FileWriter(filename);

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

      //System.out.println("Writting EDF header " + to_write.length() + " bytes");
      fw.write(to_write.toString());

      /*
      *(short *)(edf_header + 1018) = (short) w;
      *(short *)(edf_header + 1020) = (short) h;

      *(short *)(edf_header + 1000) = (short) w;
      *(short *)(edf_header + 1002) = (short) h;
      */

      // Write data
      char[] bytes = new char[2];
      for (int j = r.y; j < r.y + r.height; j++) {
        for (int i = r.x; i < r.x + r.width; i++) {
          int v = (int)doubleValues[j][i];
          bytes[0] = (char)(  v & 0xFF       );  //Low bytes first
          bytes[1] = (char)( (v >> 8) & 0xFF );
          fw.write(bytes);
        }
      }

      fw.close();

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error during saving file.\n" + e.getMessage());
    }

  }

  private String getLabelInfoString() {
    Dimension imgsize = getCurrentImageSize();
    int percent = (int)( 100.0 / (double)iSz );
    return percent + "% [" + imgsize.width + "," + imgsize.height + "]";  
  }
  
  private void preComputeBestFit() {

    int i,j;

    if (!isBestFit)
      return;

    if (doubleValues == null)
      return;

    if (autoBestFit) {

      autoBfMin = 65536.0;
      autoBfMax = 0.0;

      for (j = 0; j < doubleValues.length; j++)
        for (i = 0; i < doubleValues[j].length; i++) {
          double v = doubleValues[j][i];
          if (v > autoBfMax) autoBfMax = v;
          if (v < autoBfMin) autoBfMin = v;
        }

      bfa0 = -autoBfMin;

      if ((autoBfMax - autoBfMin) < 1.0)
        bfa1 = 0.0;
      else
        bfa1 = (65536.0) / (autoBfMax - autoBfMin);

    } else {

      bfa0 = -bfMin;

      if ((bfMax - bfMin) < 1.0)
        bfa1 = 0.0;
      else
        bfa1 = (65536.0) / (bfMax - bfMin);

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
    zoomImage.setImage(zoomImg);

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

    return true;
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

        long t1 = System.currentTimeMillis();

        BufferedImage lastImg = imagePanel.getImage();

        int rdimx = dimx / iSz;
        int rdimy = dimy / iSz;

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
        for (int j = 0,l = 0; j < dimy; j += iSz, l++) {

          if (isBestFit) {

            if (isNegative) {
              for (int i = 0,k = 0; i < dimx; i += iSz, k++)
                rgb[k] = gColormap[(~bestFit(doubleValues[j][i])) & 65535];
            } else {
              for (int i = 0,k = 0; i < dimx; i += iSz, k++)
                rgb[k] = gColormap[bestFit(doubleValues[j][i])];
            }

          } else {

            if (isNegative) {
              for (int i = 0,k = 0; i < dimx; i += iSz, k++)
                rgb[k] = gColormap[(~(int) doubleValues[j][i]) & 65535];
            } else {
              for (int i = 0,k = 0; i < dimx; i += iSz, k++)
                rgb[k] = gColormap[((int) doubleValues[j][i]) & 65535];
            }

          }

          lastImg.setRGB(0, l, rdimx, 1, rgb, 0, rdimx);
        }


        imagePanel.repaint();
        imageView.revalidate();

        long T = System.currentTimeMillis() - t1;

        //System.out.println("Image conversion:" + T + " ms.");
        return;
      } else {
        doubleValues = null;
      }
    } else {
      doubleValues = null;
    }


  }

  /**<code>setModel</code> Set the model.
   * @param v  Value to assign to model. This image must have a height equals to 2.
   */
  public void setModel(INumberImage v) {

    if (settingsDialog != null)
      attNameLabel.setModel(v);

    // Free old model
    if (model != null) {
      model.removeImageListener(this);
      model = null;
    }

    // Init new one
    if (v != null) {
      iSz = 1;

      model = v;
      model.addImageListener(this);
      // Get the max dimension and create an image
      int dimx = model.getMaxXDimension();
      int dimy = model.getMaxYDimension();

      // Auto calculate best image size
      while ((dimx > 800 || dimy > 600) && (iSz < 8) && ((dimx % 2) == 0) && ((dimy % 2) == 0)) {
        dimx = dimx / 2;
        dimy = dimy / 2;
        iSz *= 2;
      }
      imagePanel.setMarkerScale(1.0 / (double) iSz);

      BufferedImage img = new BufferedImage(dimx, dimy, BufferedImage.TYPE_INT_RGB);
      imagePanel.setImage(img);
      freePopup();
      refreshComponents();
    }

  }

  /**
   * Removes all  listener belonging to the viewer.
   */
  public void clearModel() {
    setModel(null);
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
    //d.setImageMargin(new Insets(0,0,0,0));
    //d.setBorder(null);

    try {

      fr.esrf.tangoatk.core.AttributeList attributeList =
          new fr.esrf.tangoatk.core.AttributeList();
      INumberImage theAtt = (INumberImage) attributeList.add("jlp/test/1/att_image");
//      INumberImage theAtt = (INumberImage) attributeList.add("//mcs-diag:10000/lego/mindstorms/tango/Vision");
//      INumberImage theAtt = (INumberImage) attributeList.add("//athena:20000/id14/eh3-camera/sony/image");
      d.setModel(theAtt);


      // Build test values
      /*
      double[][] myvalues = new double[512][512];
      for (int i = 0; i < 512; i++)
        for (int j = 0; j < 512; j++)
          myvalues[i][j] = (double) ((i * j) % 8192) + 200;
      d.setData(myvalues);
      */

      d.setSelection(new Rectangle(0, 0, 1024, 1024));

      f.getContentPane().setLayout(new java.awt.GridLayout(1, 1));
      f.getContentPane().add(d);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setTitle("ImageViewer");
      f.pack();
      f.setVisible(true);

      attributeList.setRefreshInterval(1000);
      attributeList.startRefresher();


    } catch (Exception e) {

      e.printStackTrace();

    }

  }

}

