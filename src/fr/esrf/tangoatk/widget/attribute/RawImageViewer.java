package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.IRawImageListener;
import fr.esrf.tangoatk.core.RawImageEvent;
import fr.esrf.tangoatk.core.IRawImage;
import fr.esrf.tangoatk.widget.util.*;
import fr.esrf.tangoatk.widget.util.chart.JLAxis;
import fr.esrf.tangoatk.widget.util.chart.AxisPanel;
import fr.esrf.tangoatk.widget.image.*;
import fr.esrf.tangoatk.widget.properties.LabelViewer;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import com.braju.format.Format;


/**
 * A high level class to display a TANGO image (as defined by the CCD abstract class)
 * and handle several image manipulation function.
 */
public class RawImageViewer extends JPanel implements IRawImageListener,ActionListener,MouseListener, MouseMotionListener {

  // ------------------------------------------------------
  // Private data
  // ------------------------------------------------------
  private IRawImage model;
  private IImageFormat[] allFormats;
  private int currentFormat;
  private boolean snapToGrid;
  private boolean isNegative;
  private Gradient gColor;
  private int[] gColormap;
  private int iSz; // Image size
  private EventListenerList listenerList;  // list of Roi listeners
  private Rectangle oldSelection = null;
  private Insets noMargin = new Insets(0,0,0,0);
  private boolean isBestFit;
  private boolean autoZoom = false;
  private boolean firstRefresh = false;
  private boolean userZoom = false;
  private boolean showingMenu;
  private int profileMode;
  private double minFit;
  private double maxFit;

  // ------------------------------------------------------
  // Main panel components
  // ------------------------------------------------------
  private Font panelFont;

  protected JImage imagePanel;
  private JScrollPane imageView;

  // Popup menu
  private JPopupMenu imgMenu;
  private JCheckBoxMenuItem[] formatMenuItem;
  private JCheckBoxMenuItem bestFitMenuItem;
  private JCheckBoxMenuItem snapToGridMenuItem;
  private JCheckBoxMenuItem negativeMenuItem;
  private JCheckBoxMenuItem toolbarMenuItem;
  private JCheckBoxMenuItem statusLineMenuItem;
  private JCheckBoxMenuItem showGradMenuItem;
  private JMenuItem selectionMenuItem;
  private JMenuItem selectionMaxMenuItem;
  private JMenuItem fileMenuItem;
  private JMenuItem tableMenuItem;
  private JMenuItem lineProfileMenuItem;
  private JMenuItem histogramMenuItem;
  private JMenuItem settingsMenuItem;
  private JMenuItem statsMenuItem;

  // Button panel
  private JButton selectButton;
  private JButton selectMaxButton;
  private JButton fileButton;
  private JButton tableButton;
  private JButton profileButton;
  private JButton histoButton;
  private JButton settingsButton;
  private JButton axisButton;
  private JScrollPane buttonView;

  // Settings panel components
  private JDialog settingsDialog = null;
  private LabelViewer attNameLabel;
  private JButton propButton;
  private JComboBox formatCombo;
  private JCheckBox bestFitCheck;
  private JTextField fitMinText;
  private JTextField fitMaxText;
  private JCheckBox snapToGridCheck;
  private JTextField snapToGridText;
  private JCheckBox negativeCheck;
  private JComboBox imageSizeCombo;
  private JGradientEditor gradViewer;
  private JButton gradButton;
  private JButton okButton;
  private JButton cancelButton;

  // PropertyFrame
  SimplePropertyFrame propDialog = null;

  // Table panel components
  private JFrame tableDialog = null;
  private JTableRow tablePanel;

  // LineProfiler panel components
  private LineProfilerViewer lineProfiler = null;

  // Axis Panels
  private JDialog     axisDialog = null;
  private JButton     axisCloseButton;

  // Status line
  private JPanel cfgPanel;
  private JLabel statusLabel;
  private JLabel rangeLabel;

  // Gradientviewer
  private JGradientViewer gradientTool;


  /**
   * Create a new RawImageViewer
   */
  public RawImageViewer() {

    // ------------------------------------------------------
    // Image format initialisation
    // ------------------------------------------------------

    // !! Important, a new format must be added at the end of the list
    allFormats = new IImageFormat[3];
    allFormats[0] = new Mono8ImageFormat();
    allFormats[1] = new Mono16ImageFormat();
    allFormats[2] = new RGB24ImageFormat();
    currentFormat = 0;

    // ------------------------------------------------------
    // Graphics stuff
    // ------------------------------------------------------

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEtchedBorder());
    panelFont = new Font("Dialog", Font.PLAIN, 11);

    // Main panel
    imagePanel = new JImage();
    imagePanel.setBorder(null);
    imagePanel.setSnapGrid(8);
    imageView = new JScrollPane(imagePanel);
    add(imageView, BorderLayout.CENTER);

    // JPopupMenu
    imgMenu = new JPopupMenu();

    JMenuItem infoMenuItem = new JMenuItem("Image Viewer");
    infoMenuItem.setEnabled(false);

    JMenu formatMenu = new JMenu("Format");
    formatMenuItem = new JCheckBoxMenuItem[allFormats.length];
    for(int i=0;i<allFormats.length;i++) {
      formatMenuItem[i] = new JCheckBoxMenuItem(allFormats[i].getName());
      formatMenuItem[i].addActionListener(this);
      formatMenu.add(formatMenuItem[i]);
    }

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

    lineProfileMenuItem = new JMenuItem("Line profile");
    lineProfileMenuItem.addActionListener(this);

    histogramMenuItem = new JMenuItem("Histogram");
    histogramMenuItem.addActionListener(this);

    fileMenuItem = new JMenuItem("Save selection");
    fileMenuItem.addActionListener(this);

    settingsMenuItem = new JMenuItem("Settings");
    settingsMenuItem.addActionListener(this);

    statsMenuItem = new JMenuItem("Statistics");
    statsMenuItem.addActionListener(this);

    tableMenuItem = new JMenuItem("Selection to table");
    tableMenuItem.addActionListener(this);

    imgMenu.add(infoMenuItem);
    imgMenu.add(new JSeparator());
    imgMenu.add(formatMenu);
    imgMenu.add(bestFitMenuItem);
    imgMenu.add(negativeMenuItem);
    imgMenu.add(snapToGridMenuItem);
    imgMenu.add(toolbarMenuItem);
    imgMenu.add(statusLineMenuItem);
    imgMenu.add(showGradMenuItem);
    imgMenu.add(new JSeparator());
    imgMenu.add(selectionMenuItem);
    imgMenu.add(selectionMaxMenuItem);
    imgMenu.add(fileMenuItem);
    imgMenu.add(tableMenuItem);
    imgMenu.add(new JSeparator());
    imgMenu.add(lineProfileMenuItem);
    imgMenu.add(histogramMenuItem);
    imgMenu.add(new JSeparator());
    imgMenu.add(settingsMenuItem);
    imgMenu.add(statsMenuItem);

    // Toolbar
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(null);
    buttonPanel.setPreferredSize(new Dimension(60, 335));
    buttonView = new JScrollPane(buttonPanel);
    buttonView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    add(buttonView, BorderLayout.WEST);
    buttonView.addComponentListener(new ComponentListener(){
      public void componentResized(ComponentEvent e) { adjustToolbarSize(); }
      public void componentMoved(ComponentEvent e) {}
      public void componentShown(ComponentEvent e) { adjustToolbarSize(); }
      public void componentHidden(ComponentEvent e) { adjustToolbarSize(); }
    });

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
    selectMaxButton.setBounds(2, 45, 36, 36);
    selectMaxButton.setToolTipText("Select whole image");
    selectMaxButton.addActionListener(this);
    buttonPanel.add(selectMaxButton);

    fileButton = new JButton();
    fileButton.setMargin(noMargin);
    fileButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_snapshot.gif")));
    fileButton.setBounds(2, 85, 36, 36);
    fileButton.setToolTipText("Save snapshot");
    fileButton.addActionListener(this);
    buttonPanel.add(fileButton);

    tableButton = new JButton();
    tableButton.setMargin(noMargin);
    tableButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_table.gif")));
    tableButton.setBounds(2, 125, 36, 36);
    tableButton.setToolTipText("Selection to table");
    tableButton.addActionListener(this);
    buttonPanel.add(tableButton);

    profileButton = new JButton();
    profileButton.setMargin(noMargin);
    profileButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_profile.gif")));
    profileButton.setBounds(2, 170, 36, 36);
    profileButton.setToolTipText("Line profile");
    profileButton.addActionListener(this);
    buttonPanel.add(profileButton);

    histoButton = new JButton();
    histoButton.setMargin(noMargin);
    histoButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_histo.gif")));
    histoButton.setBounds(2, 205, 36, 36);
    histoButton.setToolTipText("Histogram");
    histoButton.addActionListener(this);
    buttonPanel.add(histoButton);

    settingsButton = new JButton();
    settingsButton.setMargin(noMargin);
    settingsButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_option.gif")));
    settingsButton.setBounds(2, 250, 36, 36);
    settingsButton.setToolTipText("Image viewer settings");
    settingsButton.addActionListener(this);
    buttonPanel.add(settingsButton);

    axisButton = new JButton();
    axisButton.setIcon(new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/image/img_axis.gif")));
    axisButton.setMargin(noMargin);
    axisButton.setBounds(2, 295, 36, 36);
    axisButton.setToolTipText("Axis settings");
    axisButton.addActionListener(this);
    buttonPanel.add(axisButton);

    // Status line
    cfgPanel = new JPanel();
    cfgPanel.setLayout(null);
    cfgPanel.setPreferredSize(new Dimension(0, 40));
    add(cfgPanel, BorderLayout.SOUTH);

    statusLabel = new JLabel("");
    statusLabel.setFont(panelFont);
    statusLabel.setBounds(5, 0, 400, 20);
    cfgPanel.add(statusLabel);

    rangeLabel = new JLabel("");
    rangeLabel.setFont(panelFont);
    rangeLabel.setBounds(5, 20, 400, 20);
    cfgPanel.add(rangeLabel);

    // Gradient tool
    gradientTool = new JGradientViewer();
    gradientTool.getAxis().setMinimum(0);
    gradientTool.getAxis().setMaximum(256);
    add(gradientTool, BorderLayout.EAST);

    // ------------------------------------------------------
    // Private initialisation
    // ------------------------------------------------------

    imagePanel.addMouseMotionListener(this);
    imagePanel.addMouseListener(this);

    isBestFit = true;
    setAlignToGrid(true);
    isNegative = false;
    gColor = new Gradient();
    gColormap = gColor.buildColorMap(65536);
    gradientTool.setGradient(gColor);
    iSz = 1;
    showingMenu = true;
    listenerList = new EventListenerList();
    profileMode = 0;

    for(int i=0;i<allFormats.length;i++) {
      allFormats[i].initDefault(isBestFit,gradientTool);
    }
    minFit = 0.0;
    maxFit = 100.0;

  }

  // ----------------------------------------------------------
  // Public stuff
  // ----------------------------------------------------------

  /**
   * Sets data to display.
   * @param image Handle to data
   */
  public void setData(byte[][] image) {

    synchronized(this) {
      for(int i=0;i<allFormats.length;i++)
        allFormats[i].setData(image);
      computeAutoZoom();
      convertImage();
      refreshComponents();
    }

    // Nothing to display
    if (image == null) {
      imagePanel.setImage(null);
      freePopup();
      imageView.revalidate();
    }


  }

  /**
   * Displays the image using the whole color range.
   * @param b Best fit toggle
   */
  public void setBestFit(boolean b) {
    isBestFit = b;
    applyFitting();
    synchronized (this) {
      convertImage();
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
   * Return the current zoom factor index.
   * @see #setZoom
   */
  public int getZoom() {

    int s = 0;
    switch (iSz) {
      case -4:
        s = 0;
        break;
      case -2:
        s = 1;
        break;
      case 1:
        s = 2;
        break;
      case 2:
        s = 3;
        break;
      case 4:
        s = 4;
        break;
      case 8:
        s = 5;
        break;
    }

    return s;
  }

  /** Sets the image zoom factor.
   * If you want to start you viewer with a fixed zoom factor, you
   * have to call setZoom() before setModel().
   * <pre>
   * Possible zoomIndex values are:
   *   0 : 400%
   *   1 : 200%
   *   2 : 100%
   *   3 : 50%
   *   4 : 25%
   *   5 : 12.5%
   * </pre>
   * @param zoomIndex ZoomFactor index (see description).
   */
  public void setZoom(int zoomIndex) {

    switch (zoomIndex) {

      case 0: // 400%
        iSz = -4;
        break;

      case 1: // 200%
        iSz = -2;
        break;

      case 2: // 100%
        iSz = 1;
        break;

      case 3: // 50%
        iSz = 2;
        break;

      case 4: // 25%
        iSz = 4;
        break;

      case 5: //12.5%
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
   * Sets the format of the image (Index in the format combobox)
   * @param format Format index
   */
  public void setFormat(int format) {

    currentFormat = format;

    // Remove the gradient when color format
    if(allFormats[currentFormat].isColorFormat())
      gradientTool.setVisible(false);

    synchronized (this) {
      convertImage();
    }

  }

  /**
   * Returns the image format.
   * #see #setFormat
   */
  public int getFormat() {
    return currentFormat;
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
   * True to enable menu displayed when clicking on right mouse button.
   * @param b True to enable the menu
   */
  public void setShowingMenu(boolean b) {
    showingMenu = b;
  }

  /**
   * Add the specified ROI Listener
   * @param l ROI listener
   */
  public void addRoiListener(IRoiListener l) {
    listenerList.add(IRoiListener.class, l);
  }

  /**
   * Remove the specified ROI Listener
   * @param l ROI listener
   */
  public void removeRoiListener(IRoiListener l) {
    listenerList.remove(IRoiListener.class, l);
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
      refreshSelectionMinMax();
    }
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
   * Returns the current image size
   * @return Current image size
   */
  public Dimension getCurrentImageSize() {

    int dimx = allFormats[currentFormat].getWidth();
    int dimy = allFormats[currentFormat].getHeight();
    return new Dimension(dimx, dimy);

  }

  // ----------------------------------------------------------
  // Action Listener
  // ----------------------------------------------------------
  public void actionPerformed(ActionEvent evt) {

    Object src = evt.getSource();

    if (src == settingsMenuItem) {
      showSettings();
    } else if (evt.getSource() == cancelButton) {
      settingsDialog.setVisible(false);
    } else if (evt.getSource() == okButton) {
      applySettings();
    } else if (evt.getSource() == propButton) {
      showPropertyFrame();
    } else if (evt.getSource() == gradButton) {
      showGradientEditor();
    } else if (src == selectButton || src == selectionMenuItem) {
      imagePanel.clearSelection();
      imagePanel.setSelectionMode(1);
      profileMode = 0;
      freePopup();
      synchronized (this) {
        refreshSelectionMinMax();
      }
    }  else if (src == selectMaxButton || src == selectionMaxMenuItem) {
      Dimension d = imagePanel.getImageSize();
      imagePanel.setSelection(0, 0, d.width, d.height);
      selectionChanged();
    } else if (src == fileButton || src == fileMenuItem) {
      saveFile();
    } else if (src == tableButton || src == tableMenuItem) {
      showTable();
    } else if (src == profileButton || src == lineProfileMenuItem) {

      imagePanel.setSelectionMode(0);
      constructLineProfiler();
      lineProfiler.setLineProfileMode();
      lineProfiler.setVisible(true);
      profileMode = 1;

      synchronized (this) {
        refreshLineProfile();
        refreshSelectionMinMax();
      }

    } else if (src == histoButton || src == histogramMenuItem) {

      imagePanel.setSelectionMode(1);
      constructLineProfiler();
      lineProfiler.setHistogramMode();
      lineProfiler.setVisible(true);
      profileMode = 2;

      synchronized (this) {
        refreshLineProfile();
      }

    } else if (src == settingsButton) {
      showSettings();
    } else if (src == axisButton) {
      showAxisDialog();
    } else if (src == axisCloseButton) {
      axisDialog.setVisible(false);
    } else if (src == bestFitMenuItem) {
      setBestFit(!isBestFit());
    } else if (src == negativeMenuItem) {
      setNegative(!isNegative());
    } else if (src == snapToGridMenuItem) {
      setAlignToGrid(!isAlignToGrid());
    } else if (src == toolbarMenuItem) {
      setToolbarVisible(!isToolbarVisible());
    } else if (src == statusLineMenuItem) {
      setStatusLineVisible(!isStatusLineVisible());
    } else if (src == showGradMenuItem) {
      setGradientVisible(!isGradientVisible());
    } else if (src == statsMenuItem) {
      JOptionPane.showMessageDialog(this,getImageInfo(),"Image Information",JOptionPane.INFORMATION_MESSAGE);
    } else if (src == bestFitCheck) {
      boolean sel = bestFitCheck.isSelected();
      fitMinText.setEnabled(!sel);
      fitMaxText.setEnabled(!sel);
    }

    // Search if a format menu item has been selected
    boolean found=false;
    int i=0;
    while(i<allFormats.length && !found) {
      found = (src == formatMenuItem[i]);
      if(!found) i++;
    }
    if(found) setFormat(i);

  }

  // ----------------------------------------------------------
  // Mouse Listener
  // ----------------------------------------------------------

  public void mouseDragged(MouseEvent e) {
    mouseMoved(e);
  }

  synchronized public void mouseMoved(MouseEvent e) {

    Dimension imgsize = getCurrentImageSize();
    int x = getImageXCoord(e.getX());
    int y = getImageYCoord(e.getY());
    if ((x >= imgsize.width) || (y >= imgsize.height) || (y < 0) || (x < 0)) {
      statusLabel.setText(getLabelInfoString());
    } else {
      statusLabel.setText(getLabelInfoString() + " (" + x + "," + y + ")="
                         + Double.toString(allFormats[currentFormat].getValue(x,y)));
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
        for(int i=0;i<allFormats.length;i++)
          formatMenuItem[i].setSelected(i == currentFormat);
        bestFitMenuItem.setSelected(isBestFit());
        snapToGridMenuItem.setSelected(isAlignToGrid());
        negativeMenuItem.setSelected(isNegative());
        toolbarMenuItem.setSelected(isToolbarVisible());
        statusLineMenuItem.setSelected(isStatusLineVisible());
        showGradMenuItem.setSelected(isGradientVisible());
        showGradMenuItem.setEnabled(!allFormats[currentFormat].isColorFormat());
        imgMenu.show(imagePanel, e.getX() , e.getY() );
      }
    }

  }

  // ----------------------------------------------------------
  // Image Listener
  // ----------------------------------------------------------

  public void errorChange(fr.esrf.tangoatk.core.ErrorEvent errorEvent) {
    setData(null);
  }

  public void stateChange(fr.esrf.tangoatk.core.AttributeStateEvent evt) {
  }

  public void rawImageChange(RawImageEvent evt) {
    setData(evt.getValue());
  }

  // ----------------------------------------------------------
  // Model stuff
  // ----------------------------------------------------------

  /**
   * Sets the model.
   * @param v  Value to assign to model.
   */
  public void setModel(IRawImage v) {

    if (settingsDialog != null)
      attNameLabel.setModel(v);

    // Free old model
    if (model != null) {
      model.removeRawImageListener(this);
      model = null;
    }

    // Init new one
    if (v != null) {

      // Reset viewer
      imagePanel.setImage(null);
      firstRefresh = !userZoom;
      freePopup();

      // Init new model
      model = v;
      model.addRawImageListener(this);

      // Force a reading to initialise the viewer size before
      // make it visible
      model.refresh();

    }

  }

  /**
   * Removes all  listener belonging to the viewer.
   */
  public void clearModel() {
    setModel(null);
  }


  // ----------------------------------------------------------
  // Private stuff
  // ----------------------------------------------------------
  private void constructSettingsPanel() {

    if (settingsDialog == null) {
      // ------------------------------------------------------
      // Settings panel
      // ------------------------------------------------------
      JPanel settingsPanel = new JPanel();
      settingsPanel.setLayout(null);
      settingsPanel.setMinimumSize(new Dimension(290, 235));
      settingsPanel.setPreferredSize(new Dimension(290, 235));

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

      bestFitCheck = new JCheckBox("Best fit");
      bestFitCheck.setSelected(false);
      bestFitCheck.setFont(panelFont);
      bestFitCheck.setBounds(5, 50, 85, 20);
      bestFitCheck.setToolTipText("Display the image using the whole color range");
      bestFitCheck.addActionListener(this);
      settingsPanel.add(bestFitCheck);

      JLabel fitMinLabel = new JLabel("Min (%)");
      fitMinLabel.setFont(panelFont);
      fitMinLabel.setBounds(90, 50, 50, 20);
      fitMinLabel.setHorizontalAlignment(JLabel.LEFT);
      settingsPanel.add(fitMinLabel);

      fitMinText = new JTextField("");
      fitMinText.setMargin(noMargin);
      fitMinText.setFont(panelFont);
      fitMinText.setBounds(140, 50, 40, 20);
      settingsPanel.add(fitMinText);

      JLabel fitMaxLabel = new JLabel("Max (%)");
      fitMaxLabel.setFont(panelFont);
      fitMaxLabel.setBounds(190, 50, 50, 20);
      fitMaxLabel.setHorizontalAlignment(JLabel.LEFT);
      settingsPanel.add(fitMaxLabel);

      fitMaxText = new JTextField("");
      fitMaxText.setMargin(noMargin);
      fitMaxText.setFont(panelFont);
      fitMaxText.setBounds(240, 50, 40, 20);
      settingsPanel.add(fitMaxText);

      // ------------------------------------------------------------------------------------
      JSeparator js1 = new JSeparator();
      js1.setBounds(0, 80, 500, 10);
      settingsPanel.add(js1);

      JLabel formatLabel = new JLabel("Format");
      formatLabel.setFont(panelFont);
      formatLabel.setBounds(5, 90, 70, 20);
      settingsPanel.add(formatLabel);

      formatCombo = new JComboBox();
      formatCombo.setFont(panelFont);
      for(int i=0;i<allFormats.length;i++)
        formatCombo.addItem(allFormats[i].getName());
      formatCombo.setBounds(80, 90, 200, 22);
      settingsPanel.add(formatCombo);

      JLabel gradLabel = new JLabel("Colormap");
      gradLabel.setFont(panelFont);
      gradLabel.setBounds(5, 115, 70, 20);
      settingsPanel.add(gradLabel);

      gradViewer = new JGradientEditor();
      gradViewer.setGradient(gColor);
      gradViewer.setEditable(false);
      gradViewer.setToolTipText("Display the image using this colormap");
      gradViewer.setBounds(80, 115, 180, 20);
      settingsPanel.add(gradViewer);

      gradButton = new JButton();
      gradButton.setText("...");
      gradButton.setToolTipText("Edit colormap");
      gradButton.setFont(panelFont);
      gradButton.setMargin(new Insets(0, 0, 0, 0));
      gradButton.setBounds(260, 115, 20, 20);
      gradButton.addActionListener(this);
      settingsPanel.add(gradButton);

      negativeCheck = new JCheckBox("Negative image");
      negativeCheck.setSelected(false);
      negativeCheck.setFont(panelFont);
      negativeCheck.setBounds(5, 140, 110, 20);
      negativeCheck.setToolTipText("Display the negative image");
      settingsPanel.add(negativeCheck);


      JLabel imageSizeLabel = new JLabel("Image size");
      imageSizeLabel.setFont(panelFont);
      imageSizeLabel.setBounds(115, 140, 85, 20);
      imageSizeLabel.setHorizontalAlignment(JLabel.CENTER);
      settingsPanel.add(imageSizeLabel);

      imageSizeCombo = new JComboBox();
      imageSizeCombo.setFont(panelFont);
      imageSizeCombo.addItem("400  %");
      imageSizeCombo.addItem("200  %");
      imageSizeCombo.addItem("100  %");
      imageSizeCombo.addItem("50   %");
      imageSizeCombo.addItem("25   %");
      imageSizeCombo.addItem("12.5 %");
      imageSizeCombo.setBounds(200, 140, 80, 22);
      settingsPanel.add(imageSizeCombo);

      // ------------------------------------------------------------------------------------
      JSeparator js2 = new JSeparator();
      js2.setBounds(0, 168, 500, 10);
      settingsPanel.add(js2);

      snapToGridCheck = new JCheckBox("Align to grid");
      snapToGridCheck.setSelected(false);
      snapToGridCheck.setFont(panelFont);
      snapToGridCheck.setBounds(5, 175, 100, 20);
      snapToGridCheck.setToolTipText("Align the selection to the grid");
      settingsPanel.add(snapToGridCheck);

      JLabel snapToGridLabel = new JLabel("Grid spacing");
      snapToGridLabel.setFont(panelFont);
      snapToGridLabel.setBounds(110, 175, 90, 20);
      settingsPanel.add(snapToGridLabel);

      snapToGridText = new JTextField("");
      snapToGridText.setMargin(noMargin);
      snapToGridText.setFont(panelFont);
      snapToGridText.setBounds(205, 175, 50, 20);
      settingsPanel.add(snapToGridText);

      okButton = new JButton();
      okButton.setText("Apply");
      okButton.setFont(panelFont);
      okButton.setBounds(5, 205, 80, 25);
      okButton.addActionListener(this);
      settingsPanel.add(okButton);

      cancelButton = new JButton();
      cancelButton.setText("Dismiss");
      cancelButton.setFont(panelFont);
      cancelButton.setBounds(205, 205, 80, 25);
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

  private void showSettings() {

    constructSettingsPanel();

    formatCombo.setSelectedIndex(currentFormat);
    bestFitCheck.setSelected(isBestFit);
    snapToGridCheck.setSelected(snapToGrid);
    negativeCheck.setSelected(isNegative);
    imageSizeCombo.setSelectedIndex(getZoom());

    fitMinText.setText(Double.toString(minFit));
    fitMaxText.setText(Double.toString(maxFit));
    fitMinText.setEnabled(!isBestFit);
    fitMaxText.setEnabled(!isBestFit);

    snapToGridText.setText(Integer.toString(imagePanel.getSnapGrid()));

    ATKGraphicsUtils.centerDialog(settingsDialog);
    settingsDialog.setVisible(true);

    // Remove the gradient when color format
    if(allFormats[currentFormat].isColorFormat())
      gradientTool.setVisible(false);

    synchronized (this) {
      convertImage();
      refreshComponents();
    }

  }

  private void applyFitting() {

    boolean ok = true;

    try {
      if(fitMinText!=null) {
        minFit = Double.parseDouble(fitMinText.getText());
        maxFit = Double.parseDouble(fitMaxText.getText());
      }
    } catch(NumberFormatException e) {
      ok = false;
    }

    for(int i=0;i<allFormats.length && ok;i++) {
      ok = ok && allFormats[i].setFitting(isBestFit,minFit,maxFit);
    }
    if( !ok ) {
      JOptionPane.showMessageDialog(null, "Invalid fitting parameters\nRestoring default", "Error", JOptionPane.ERROR_MESSAGE);
      for(int i=0;i<allFormats.length;i++)
        allFormats[i].setFitting(true,0.0,100.0);
      minFit=0.0;
      maxFit=100.0;
      isBestFit = true;
    }

  }

  private void applySettings() {

    String gridStr = snapToGridText.getText();

    currentFormat = formatCombo.getSelectedIndex();
    isBestFit = bestFitCheck.isSelected();
    applyFitting();
    snapToGrid = snapToGridCheck.isSelected();
    isNegative = negativeCheck.isSelected();

    setZoom(imageSizeCombo.getSelectedIndex());

    setAlignToGrid(snapToGrid);

    try {

      int g = Integer.parseInt(gridStr);
      imagePanel.setSnapGrid(g);

    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "Invalid syntax for grid value", "Error", JOptionPane.ERROR_MESSAGE);
    }

    settingsDialog.setVisible(false);

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

  private void computeAutoZoom() {

    if (firstRefresh || autoZoom) {

      int dimx = allFormats[currentFormat].getWidth();
      int dimy = allFormats[currentFormat].getHeight();

      if (dimy > 0 && dimx > 0) {

        // Auto calculate best image size
        int sz = 1;

        if (dimx > 800 || dimy > 600) {

          // Search smaller size
          while ((dimx > 800 || dimy > 600) && (sz < 8) && ((dimx % 2) == 0) && ((dimy % 2) == 0)) {
            dimx = dimx / 2;
            dimy = dimy / 2;
            sz *= 2;
          }
          iSz = sz;

        } else {

          // Search bigger size
          while ((dimx < 600 && dimy < 400) && (sz < 4)) {
            dimx = dimx * 2;
            dimy = dimy * 2;
            sz *= 2;
          }
          if (sz != 1)
            iSz = -sz;
          else
            iSz = 1;

        }

        if (iSz < 0)
          imagePanel.setMarkerScale((double) -iSz);
        else
          imagePanel.setMarkerScale(1.0 / (double) iSz);

        firstRefresh = false;

      }

    }

  }

  private void adjustToolbarSize() {

    if(buttonView.isVisible())
      buttonView.setPreferredSize(new Dimension(60,0));
    revalidate();

  }

  private String getExtension(File f) {

    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 && i < s.length() - 1) {
      ext = s.substring(i + 1).toLowerCase();
    }

    return ext;

  }

  // Save a screenshot
  private void saveFile() {

    int ok = JOptionPane.YES_OPTION;

    FileFilter jpgFilter = new FileFilter() {
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        }
        String extension = getExtension(f);
        if (extension != null && extension.equals("jpg"))
          return true;
        return false;
      }

      public String getDescription() {
        return "jpg - JPEG pictures (Color 24 Bits)";
      }
    };

    FileFilter jpg8Filter = new FileFilter() {
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        }
        String extension = getExtension(f);
        if (extension != null && extension.equals("jpg"))
          return true;
        return false;
      }

      public String getDescription() {
        return "jpg - JPEG pictures (Mono 8 Bits)";
      }
    };

    FileFilter pngFilter = new FileFilter() {
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        }
        String extension = getExtension(f);
        if (extension != null && extension.equals("png"))
          return true;
        return false;
      }

      public String getDescription() {
        return "png - PNG pictures (Color 24 Bits)";
      }
    };

    FileFilter png8Filter = new FileFilter() {
      public boolean accept(File f) {
        if (f.isDirectory()) {
          return true;
        }
        String extension = getExtension(f);
        if (extension != null && extension.equals("png"))
          return true;
        return false;
      }

      public String getDescription() {
        return "png - PNG pictures (Mono 8 Bits)";
      }
    };

    JFileChooser chooser = new JFileChooser(".");
    chooser.addChoosableFileFilter(pngFilter);
    chooser.addChoosableFileFilter(png8Filter);
    chooser.addChoosableFileFilter(jpgFilter);
    chooser.addChoosableFileFilter(jpg8Filter);
    chooser.setDialogTitle("Save snapshot");
    int returnVal = chooser.showSaveDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      if (f != null) {

        FileFilter filter = chooser.getFileFilter();

        if (filter == jpgFilter || filter == jpg8Filter) {
          if (getExtension(f) == null || !getExtension(f).equalsIgnoreCase("jpg")) {
            f = new File(f.getAbsolutePath() + ".jpg");
          }
        } else if (filter == pngFilter || filter == png8Filter) {
          if (getExtension(f) == null || !getExtension(f).equalsIgnoreCase("png")) {
            f = new File(f.getAbsolutePath() + ".png");
          }
        } else {
          JOptionPane.showMessageDialog(this, "Please select a valid image format", "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }

        if (f.exists())
          ok = JOptionPane.showConfirmDialog(this, "Do you want to overwrite " + f.getName() + " ?", "Confirm overwrite", JOptionPane.YES_NO_OPTION);

        if (ok == JOptionPane.YES_OPTION) {

          if (filter == jpgFilter) {

            try {
              ImageIO.write(imagePanel.getImage(), "jpg", f);
            }
            catch (IOException ioe) {
              JOptionPane.showMessageDialog(this, "File could not be saved", "Error", JOptionPane.ERROR_MESSAGE);
            }

          } else if (filter == jpg8Filter) {

            try {
              ImageIO.write(get8BitImage(), "jpg", f);
            }
            catch (IOException ioe) {
              JOptionPane.showMessageDialog(this, "File could not be saved", "Error", JOptionPane.ERROR_MESSAGE);
            }

          } else if (filter == pngFilter) {

            try {
              ImageIO.write(imagePanel.getImage(), "png", f);
            }
            catch (IOException ioe) {
              JOptionPane.showMessageDialog(this, "File could not be saved", "Error", JOptionPane.ERROR_MESSAGE);
            }

          } else if (filter == png8Filter) {

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

  }

  private BufferedImage get8BitImage() {

    // Convert to 8 bits
    int w = imagePanel.getImage().getWidth();
    int h = imagePanel.getImage().getHeight();
    BufferedImage newImage = new BufferedImage(w,h,BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D g2 = newImage.createGraphics();
    g2.drawImage(imagePanel.getImage(),0,0,null);
    g2.dispose();
    return newImage;

  }

  private void fireRoiChange() {

    IRoiListener[] list = (IRoiListener[]) (listenerList.getListeners(IRoiListener.class));
    RoiEvent w = new RoiEvent(this,getSelection());
    for (int i = 0; i < list.length; i++) list[i].roiChange(w);

  }

  private void selectionChanged() {
    synchronized (this) {
      refreshComponents();
    }
    fireRoiChange();
  }

  private void mulRect(Rectangle r) {
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

  private void mulPoint(Point p) {

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

  private void freePopup() {

    if (lineProfiler != null) {
      lineProfiler.setData(null);
    }

    if (tableDialog != null) {
      tablePanel.clearData();
    }

  }

  private void constructTablePanel() {

    if (tableDialog == null) {

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

    try {
      Double[][] d = new Double[r.height][r.width];
      for (int j = 0; j < r.height; j++)
        for (int i = 0; i < r.width; i++)
          d[j][i] = new Double(allFormats[currentFormat].getValue(r.x + i,r.y + j));
      tablePanel.setData(d, r.x, r.y);
    } catch (OutOfMemoryError e) {
      System.out.println("Out of memory, cannot build the table");
      tablePanel.clearData();
    }

    return true;

  }

  private double[] buildHistogramData() {

    double[] histo = new double[allFormats[currentFormat].getHistogramWidth()];

    Rectangle r = imagePanel.getSelectionRect();

    if (r == null) {
      // Select the whole image
      Dimension d = imagePanel.getImageSize();
      r = new Rectangle(0,0,d.width,d.height);
    }

    mulRect(r);

    int i;
    for (i = 0; i < histo.length; i++) histo[i] = 0.0;

    try {
      for (i = r.x; i < r.x + r.width; i++)
        for (int j = r.y; j < r.y + r.height; j++)
          histo[(int) allFormats[currentFormat].getValue(i,j)] += 1.0;
    } catch(ArrayIndexOutOfBoundsException e) {
      System.out.println("NumberImageViewer.buildHistogramData() : Cannot build histogram. One or more value exceed the range [0..65535].");
      return null;
    }

    return histo;

  }

  private void refreshComponents() {

    refreshLineProfile();
    refreshSelectionMinMax();

    if (tableDialog != null && tableDialog.isVisible()) {
      buildTable();
      tablePanel.repaint();
    }

  }

  private void constructLineProfiler() {

    if (lineProfiler == null) {

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

  private void refreshLineProfile() {

    if (lineProfiler != null && lineProfiler.isVisible() && profileMode > 0) {

      switch (profileMode) {
        case 1:
          lineProfiler.setData(buildProfileData());
          break;
        case 2:
          double[] v = buildHistogramData();
          if (v != null) {
            lineProfiler.setData(v, 0);
          } else {
            lineProfiler.setData(null);
          }
          break;
      }

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
            profile[i] = allFormats[currentFormat].getValue(xe,ye);
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
            profile[i] = allFormats[currentFormat].getValue(xe,ye);
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
      JTabbedPane tabPane = new JTabbedPane();
      AxisPanel xAxisPanel = new AxisPanel(getXAxis(), AxisPanel.X_TYPE, null);
      AxisPanel yAxisPanel = new AxisPanel(getYAxis(), AxisPanel.Y1_TYPE, null);
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
    imagePanel.repaint();

  }

  private void refreshSelectionMinMax() {

    Rectangle r = imagePanel.getSelectionRect();

    if (r == null) {
      // Select the whole image
      Dimension d = imagePanel.getImageSize();
      r = new Rectangle(0,0,d.width,d.height);
    }

    mulRect(r);

    double curSelMin = 65536.0;
    double curSelMax = 0.0;

    for (int j = r.y; j < r.y + r.height; j++)
      for (int i = r.x; i < r.x + r.width; i++) {
        double v = allFormats[currentFormat].getValue(i,j);
        if (v > curSelMax) curSelMax = v;
        if (v < curSelMin) curSelMin = v;
      }

    String sel = "Selection: (" + r.x + "," + r.y + ") - [" + r.width + "," + r.height + "]";

    if (curSelMin <= curSelMax)
      rangeLabel.setText(sel + " Range: " + Double.toString(curSelMin) +
          " , " + Double.toString(curSelMax));
    else
      rangeLabel.setText("");

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

  private String getImageInfo() {

    String retString = "";
    IImageFormat cur = allFormats[currentFormat];

    retString += "Format: " + cur.getName() + "\n";
    retString += "Full Width: " + cur.getWidth() + "\n";
    retString += "Full Height: " + cur.getHeight() + "\n\n";

    Rectangle r = imagePanel.getSelectionRect();
    if (r == null) {
      // Select the whole image
      Dimension d = imagePanel.getImageSize();
      r = new Rectangle(0,0,d.width,d.height);
    }
    mulRect(r);
    retString += "Selection: (" + r.x + "," + r.y + ") - [" + r.width + "," + r.height + "]\n";

    double curSelMin = 65536.0;
    double curSelMax = 0.0;
    double sum = 0.0;
    double sum2 = 0.0;
    double lgth = 0.0;
    double avg;
    double std;

    for (int j = r.y; j < r.y + r.height; j++)
      for (int i = r.x; i < r.x + r.width; i++) {
        double v = cur.getValue(i,j);
        if (v > curSelMax) curSelMax = v;
        if (v < curSelMin) curSelMin = v;
        sum   += v;
        lgth  += 1.0;
      }
    avg = sum/lgth;

    for (int j = r.y; j < r.y + r.height; j++)
      for (int i = r.x; i < r.x + r.width; i++) {
        double v = cur.getValue(i,j);
        sum2 += (v-avg)*(v-avg);
      }
    std = Math.sqrt( sum2/lgth );

    Double avgD = new Double(avg);
    Double stdD = new Double(std);

    if (curSelMin <= curSelMax) {
      retString += "Minimum: " + Double.toString(curSelMin) + "\n";
      retString += "Maximum: " + Double.toString(curSelMax) + "\n";
    }

    retString += "Average: " + Format.sprintf("%.2f",new Double[]{avgD}) + "\n";
    retString += "Std deviation: " + Format.sprintf("%.2f",new Double[]{stdD}) + "\n";


    return retString;

  }

  private void convertImage() {

    int dimx = allFormats[currentFormat].getWidth();
    int dimy = allFormats[currentFormat].getHeight();

    if(dimx==0 || dimy==0) return;

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
    allFormats[currentFormat].computeFitting();
    if(isGradientVisible()) gradientTool.repaint();

    // Fill the image
    if (iSz < 0) {

      // Bigger image
      int sz = -iSz;

      for (int j = 0; j < dimy; j++) {
        for (int i = 0; i < dimx; i++) {
          int c = allFormats[currentFormat].getRGB(isNegative, gColormap, i, j);
          for (int k = 0; k < sz; k++)
            rgb[i * sz + k] = c;
        }
        for (int k = 0; k < sz; k++)
          lastImg.setRGB(0, j * sz + k, rdimx, 1, rgb, 0, rdimx);
      }

    } else {

      //Smaller
      for (int j = 0, l = 0; l < rdimy; j += iSz, l++) {
        for (int i = 0, k = 0; k < rdimx; i += iSz, k++)
          rgb[k] = allFormats[currentFormat].getRGB(isNegative, gColormap, i, j);
        lastImg.setRGB(0, l, rdimx, 1, rgb, 0, rdimx);
      }

    }

    imagePanel.repaint();
    imageView.revalidate();

    //long T = System.currentTimeMillis() - t1;
    //System.out.println("Image conversion:" + T + " ms.");

  }


  // ----------------------------------------------------------
  // Main test function
  // ----------------------------------------------------------
  public static void main(String args[]) {

    final JFrame f = new JFrame();
    final RawImageViewer d = new RawImageViewer();

    d.setCrossCursor(true);

    fr.esrf.tangoatk.core.AttributeList attributeList =
        new fr.esrf.tangoatk.core.AttributeList();
    final ErrorHistory errWin = new ErrorHistory();
    attributeList.addErrorListener(errWin);

    try {

      IRawImage theAtt;
      theAtt = (IRawImage) attributeList.add("tango://id11:20000/id11/ccd1394/1/image");
      //theAtt = (IRawImage) attributeList.add("//mufid3:20000/id22eh2/ccd1394/1/Image");
      d.setModel(theAtt);

    } catch (Exception e) {

      e.printStackTrace();

    }

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
    f.setTitle("RawImageViewer");
    f.pack();
    f.setVisible(true);

    attributeList.setRefreshInterval(1000);
    attributeList.startRefresher();

  }



}
