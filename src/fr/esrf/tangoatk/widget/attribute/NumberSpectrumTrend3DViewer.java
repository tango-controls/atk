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

import fr.esrf.tangoatk.core.ISpectrumListener;
import fr.esrf.tangoatk.core.INumberSpectrum;
import fr.esrf.tangoatk.core.INumberSpectrumHistory;
import fr.esrf.tangoatk.widget.util.*;
import fr.esrf.tangoatk.widget.util.chart.JLAxis;
import fr.esrf.tangoatk.widget.util.chart.JLChart;
import fr.esrf.tangoatk.widget.util.chart.JLDataView;
import fr.esrf.tangoatk.widget.properties.LabelViewer;
import fr.esrf.tangoatk.widget.image.LineProfilerViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Date;


/**
 * Spectrum history
 */

class TrendData {
  double[] values;
  long     time;
}

/**
 * A class to monitor a spectrum as a function of time using colormap for intensity.
 */
public class NumberSpectrumTrend3DViewer extends JComponent implements ISpectrumListener, ActionListener, MouseListener, J3DTrendListener  {

  static final java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
  static final java.text.SimpleDateFormat genFormat = new java.text.SimpleDateFormat("dd/MM/yy HH:mm:ss.SSS");

  protected INumberSpectrum model = null;
  private TrendData[]       data;
  private J3DTrend          trend;
  private JScrollPane       trendView;
  private int               historyLength;
  private int[]             gColormap;
  private Gradient          gColor;
  private double            zMin;
  private double            zMax;
  private boolean           zAutoScale;
  private JGradientViewer   gradientViewer;
  private JLabel            statusLabel;
  private Color             NaNColor = new Color(128,128,128);
  private int               rdimx;
  private int               rdimy;
  private int               vZoom;
  private int               hZoom;

  // Contextual menu
  private boolean           showingMenu;
  protected JPopupMenu      popupMenu;
  protected JMenuItem       settingsMenuItem;
  protected JMenuItem       hProfileMenuItem;
  protected JMenuItem       vProfileMenuItem;
  protected JMenuItem       hZoomInMenuItem;
  protected JMenuItem       hZoomOutMenuItem;
  protected JMenuItem       vZoomInMenuItem;
  protected JMenuItem       vZoomOutMenuItem;

  // Settings panel
  protected JFrame        settingsFrame = null;
  private JPanel          settingsPanel;
  private LabelViewer     attNameLabel = null;
  private JButton         propButton;
  private JCheckBox       autoScaleCheck;
  private JTextField      minText;
  private JTextField      maxText;
  private JGradientEditor gradEditor;
  private JButton         gradButton;
  private JTextField      hLengthText;
  private JComboBox       hZoomCombo = null;
  private JComboBox       vZoomCombo = null;
  private JButton         okButton;
  private JButton         cancelButton;

  protected SimplePropertyFrame propDialog = null;

  // Profile plotter
  protected LineProfilerViewer  vProfiler = null;
  protected JFrame              hProfiler = null;
  protected JLChart             hProfilerGraph;
  protected JLDataView          hProfilerData;


  /**
   * Construct a number specturm 3D viewer
   */
  public NumberSpectrumTrend3DViewer()
  {

    setLayout(new BorderLayout());

    // Initialise default parameters
    historyLength = 0;
    setHistoryLength(800);
    zAutoScale = true;
    zMin = 0.0;
    zMax = 100.0;
    rdimx = historyLength;
    rdimy = 300;
    hZoom = 1;
    vZoom = 1;
    gColor = new Gradient();
    gColor.buildRainbowGradient();
    gColormap = gColor.buildColorMap(65536);

    // Main panel components
    trend = new J3DTrend();
    trend.setParent(this);
    trend.addMouseListener(this);
    trendView = new JScrollPane(trend);
    add(trendView, BorderLayout.CENTER);
    gradientViewer = new JGradientViewer();
    gradientViewer.setGradient(gColor);
    add(gradientViewer, BorderLayout.EAST);
    statusLabel = new JLabel(" ");
    statusLabel.setFont(ATKConstant.labelFont);
    add(statusLabel,BorderLayout.SOUTH);

    // Contextual menu
    showingMenu = true;
    popupMenu = new JPopupMenu();

    hZoomInMenuItem = new JMenuItem("Horz. ZoomIn");
    hZoomInMenuItem.addActionListener(this);
    popupMenu.add(hZoomInMenuItem);
    hZoomOutMenuItem = new JMenuItem("Horz. ZoomOut");
    hZoomOutMenuItem.addActionListener(this);
    popupMenu.add(hZoomOutMenuItem);
    vZoomInMenuItem = new JMenuItem("Vert. ZoomIn");
    vZoomInMenuItem.addActionListener(this);
    popupMenu.add(vZoomInMenuItem);
    vZoomOutMenuItem = new JMenuItem("Vert. ZoomOut");
    vZoomOutMenuItem.addActionListener(this);
    popupMenu.add(vZoomOutMenuItem);
    popupMenu.add(new JSeparator());
    hProfileMenuItem = new JMenuItem("Horz. profile");
    hProfileMenuItem.addActionListener(this);
    popupMenu.add(hProfileMenuItem);
    vProfileMenuItem = new JMenuItem("Vert. profile");
    vProfileMenuItem.addActionListener(this);
    popupMenu.add(vProfileMenuItem);
    popupMenu.add(new JSeparator());
    settingsMenuItem = new JMenuItem("Settings");
    settingsMenuItem.addActionListener(this);
    popupMenu.add(settingsMenuItem);


  }

  /**
   * Sets the horizontal axis length in pixel.
   * @param length Horizontal axis length
   */
  public void setHistoryLength(int length) {

    synchronized (this) {
      TrendData[] newData = new TrendData[length];
      int i;
      for (i = 0; i < historyLength && i < length; i++)
        newData[i] = data[i];
      for (; i < length; i++) {
        newData[i] = null;
      }
      data = newData;
      historyLength = length;
    }

  }

  /**
   * Sets the minimum of the z axis (color)
   * @param min Minimum value
   */
  public void setZMinimum(double min) {

    synchronized (this) {
      zMin = min;
    }

  }

  /**
   * Returns the minimum of the of the z axis (color)
   * @return minimum value
   */
  public double getZMinimum() {
    return zMin;
  }

  /**
   * Sets the maximum of the z axis (color)
   * @param max Maximum value
   */
  public void setZMaximum(double max) {

    synchronized (this) {
      zMax = max;
    }

  }

  /**
   * Returns maximum value of the z axis (color)
   */
  public double getZMaximum() {
    return zMax;
  }

  public boolean isZAutoScale() {
    return zAutoScale;
  }

  public void setZAutoScale(boolean autoScale) {

    synchronized (this) {
      zAutoScale = autoScale;
    }

  }

  /**
   * Displays or hides the gradient (right panel).
   * @param b True if status line is displayed
   */
  public void setGradientVisible(boolean b) {
    gradientViewer.setVisible(b);
  }

  /**
   * Returns true when the gradient is visible.
   */
  public boolean isGradientVisible() {
    return gradientViewer.isVisible();
  }

  /**
   * Sets the color for the NaN values
   * @param nanColor NaN color
   */
  public void setNaNColor(Color nanColor) {
    NaNColor = nanColor;
  }

  /**
   * Returns a handle to the horizontal axis
   */
  public JLAxis getXAxis() {
    return trend.getXAxis();
  }

  /**
   * Returns a handle to the vertical axis
   */
  public JLAxis getYAxis() {
    return trend.getYAxis();
  }

  /**
   * Sets the horizontal zoom factor
   * @param zoom zoom factor (between 1 and 8)
   */
  public void setHorizontalZoom(int zoom) {
    synchronized (this) {
      hZoom=zoom;
    }
  }

  /**
   * Sets the vertical zoom factor
   * @param zoom zoom factor (between 1 and 8)
   */
  public void setVerticalZoom(int zoom) {
    synchronized (this) {
      vZoom=zoom;
    }
  }

  /**
   * Sets the model of this viewer
   * @param v NumberSpectrum model
   */
  public void setModel(INumberSpectrum v) {

    clearModel();
    if (v == null) {
      repaint();
      return;
    }
    model = v;
    readHistory();
    statusLabel.setText(model.getName());
    model.addSpectrumListener(this);
    synchronized (this) {
      buildImage();
    }
    repaint();

  }

  /**
   *  removes the model.
   */
  public void clearModel()
  {
    if (model != null) {
      model.removeSpectrumListener(this);
      if(attNameLabel!=null) attNameLabel.setModel(null);
    }
    model = null;
  }

  /**
   * Returns the timestamp of the values at the coordinates x. Returns 0 if
   * no data is present at this place. x is in image coordinates.
   * @param x X coordinates (in image coordinates)
   */
  public long getTimeAt(int x) {

    int xData = historyLength - x / hZoom - 1;
    if (xData >= 0 && xData < historyLength) {
      if (data[xData] != null) {
        return data[xData].time;
      } else {
        return 0;
      }
    } else {
      return 0;
    }

  }

  /**
   * Return the value at (x,y) position. NaN is returned if no data.
   * @param x X coordinates (in image coordinates)
   * @param y Y coordinates (in image coordinates)
   */
  public double getValueAt(int x,int y) {

    int xData = historyLength - x/hZoom - 1;
    if(xData>=0 && xData<historyLength) {
      if( data[xData] != null ) {
        int yData = rdimy - y/vZoom - 1;
        if( yData>=0 && yData<data[xData].values.length ) {
          return data[xData].values[yData];
        } else {
          return Double.NaN;
        }
      } else {
        return Double.NaN;
      }
    } else {
      return Double.NaN;
    }

  }

  /**
   * Enable cross cursor on mouse click
   * @param enable Enable cursor
   */
  public void setCursorEnabled(boolean enable) {
    trend.setCursorEnabled(enable);
  }

  /**
   * Update cursor information. This function is trigerred when the user
   * click on the image.
   * @param xCursor x coordinates (referenced by the image)
   * @param yCursor y coordinates (referenced by the image)
   */
  public void updateCursor(int xCursor,int yCursor) {

    if(model!=null) {

      synchronized (this) {

        if (xCursor < 0) {
          statusLabel.setText(model.getName());
        } else {
          long time = getTimeAt(xCursor);
          if (time == 0) {
            statusLabel.setText(model.getName() + "  | no data at marker position");
          } else {
            calendar.setTimeInMillis(time);
            Date date = calendar.getTime();
            String timeStr = genFormat.format(date);
            double val = getValueAt(xCursor, yCursor);
            if (Double.isNaN(val)) {
              statusLabel.setText(model.getName() + "  | " + timeStr);
            } else {
              statusLabel.setText(model.getName() + "  | " + timeStr + " , " + val + " " + model.getUnit());
            }
          }
        }

        // Refresh profile
        if (vProfiler != null)
          if (vProfiler.isVisible())
            buildVerticalProfile();

        if (hProfiler != null)
          if (hProfiler.isVisible())
            buildHorizontalProfile();

      }

    } else {
      statusLabel.setText(" ");
    }

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

  // ------------------------------------------------------------------------
  // Action listener
  // ------------------------------------------------------------------------

  public void actionPerformed(ActionEvent e) {

    Object src = e.getSource();

    if( src==settingsMenuItem ) {
      showSettings();
    } else if ( src==propButton ) {
      showPropertyFrame();
    } else if ( src==autoScaleCheck ) {
      applyAutoScale();
    } else if ( src==cancelButton ) {
      settingsFrame.setVisible(false);
    } else if ( src==okButton ) {
      applySettings();
    } else if ( src==minText ) {
      applyMinMaxAndBuild();
    } else if ( src==maxText ) {
      applyMinMaxAndBuild();
    } else if ( src==gradButton ) {
      showGradientEditor();
    } else if ( src==hLengthText ) {
      applyHistoryLengthAndBuild();
    } else if ( src==hZoomCombo ) {
      applyHorizontalZoom();
    } else if ( src==vZoomCombo ) {
      applyVerticalZoom();
    } else if ( src==vProfileMenuItem ) {
      showVerticalProfile();
    } else if ( src==hProfileMenuItem ) {
      showHorizontalProfile();
    } else if ( src==hZoomInMenuItem ) {
      applyHorizontalZoomIn();
    } else if ( src==hZoomOutMenuItem ) {
      applyHorizontalZoomOut();
    } else if ( src==vZoomInMenuItem ) {
      applyVerticalZoomIn();
    } else if ( src==vZoomOutMenuItem ) {
      applyVerticalZoomOut();
    }

  }

  // ------------------------------------------------------------------------
  // MouseListener listener
  // ------------------------------------------------------------------------

  public void mouseClicked(MouseEvent e) {}

  public void mousePressed(MouseEvent e) {

    if(e.getButton()==MouseEvent.BUTTON3) {
      if (showingMenu && e.getSource()==trend) {
        hProfileMenuItem.setEnabled(trend.isCursorInside());
        vProfileMenuItem.setEnabled(trend.isCursorInside());
        hZoomInMenuItem.setEnabled(hZoom<8);
        hZoomOutMenuItem.setEnabled(hZoom>1);
        vZoomInMenuItem.setEnabled(vZoom<8);
        vZoomOutMenuItem.setEnabled(vZoom>1);
        popupMenu.show(trend, e.getX() , e.getY());
      }
    }

  }

  public void mouseReleased(MouseEvent e) {}

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {}

  // ------------------------------------------------------------------------
  // Spectrum listener
  // ------------------------------------------------------------------------

  public void spectrumChange(fr.esrf.tangoatk.core.NumberSpectrumEvent evt) {

    synchronized(this) {
      TrendData vals = new TrendData();
      vals.values = evt.getValue();
      vals.time = evt.getTimeStamp();
      shiftData();
      trend.shiftCursorX(-hZoom);
      data[0] = vals;
      buildImage();
      repaint();

      if(hProfiler!=null)
        if(hProfiler.isVisible())
          buildHorizontalProfile();
    }

  }

  public void stateChange(fr.esrf.tangoatk.core.AttributeStateEvent evt) {

  }

  public void errorChange(fr.esrf.tangoatk.core.ErrorEvent evt)
  {

    synchronized (this) {
      TrendData vals = new TrendData();
      vals.values = new double[0];
      vals.time = evt.getTimeStamp();
      shiftData();
      trend.shiftCursorX(-hZoom);
      data[0] = vals;
      buildImage();
      repaint();

      if (hProfiler != null)
        if (hProfiler.isVisible())
          buildHorizontalProfile();
    }

  }

  // ------------------------------------------------------------------
  // private stuff
  // ------------------------------------------------------------------


  // -- Profile plotter -----------------------------------------------

  private void showVerticalProfile() {

    constructVerticalProfiler();
    vProfiler.setMode(LineProfilerViewer.LINE_MODE_SINGLE);
    synchronized (this) {
      buildVerticalProfile();
    }
    if(!vProfiler.isVisible())
      ATKGraphicsUtils.centerFrameOnScreen(vProfiler);
    vProfiler.setVisible(true);

  }

  private void buildVerticalProfile() {

    int x = trend.getXCursor();
    double[] vals = new double[rdimy];
    for(int i=0;i<rdimy;i++) vals[i]=Double.NaN;
    String title = "Vertical profile";

    int xData = historyLength - x/hZoom - 1;
    if(xData>=0 && xData<historyLength) {
      if( data[xData] != null ) {

        calendar.setTimeInMillis(data[xData].time);
        Date date = calendar.getTime();
        String timeStr = genFormat.format(date);
        title += " at " + timeStr;

        for(int i=0;i<rdimy;i++) {
          if( i<data[xData].values.length ) {
            vals[i] = data[xData].values[i];
          } else {
            vals[i] = Double.NaN;
          }
        }

      }
    }

    vProfiler.setData(vals);
    vProfiler.setTitle("[profile]");
    vProfiler.getProfile1().getChart().setHeader(title);
    vProfiler.getProfile1().getChart().setLabelVisible(false);

  }

  private void showHorizontalProfile() {

    constructHorizontalProfiler();
    synchronized (this) {
      buildHorizontalProfile();
    }
    if(!hProfiler.isVisible())
      ATKGraphicsUtils.centerFrameOnScreen(hProfiler);
    hProfiler.setVisible(true);

  }

  private void buildHorizontalProfile() {

    int y = trend.getYCursor();
    int yData = rdimy - y/vZoom - 1;
    hProfilerData.reset();

    for(int i=0;i<rdimx;i++) {
      if(data[i]!=null) {
        if(data[i].time>0) {
          if( yData>=0 && yData<data[i].values.length ) {
            hProfilerData.add((double)data[i].time, data[i].values[yData]);
          } else {
            hProfilerData.add((double)data[i].time, Double.NaN);
          }
        }
      }
    }

    hProfilerGraph.repaint();

  }

  private void constructHorizontalProfiler() {

    if (hProfiler == null) {

      JPanel innerPanel = new JPanel(new BorderLayout());

      hProfilerGraph = new JLChart();
      hProfilerGraph.setBorder(new javax.swing.border.EtchedBorder());
      hProfilerGraph.getXAxis().setAutoScale(true);
      hProfilerGraph.getXAxis().setAnnotation(JLAxis.TIME_ANNO);
      hProfilerGraph.getXAxis().setGridVisible(true);
      hProfilerGraph.getXAxis().setName("Time");
      hProfilerGraph.getY1Axis().setAutoScale(true);
      hProfilerGraph.getY1Axis().setGridVisible(true);
      hProfilerGraph.getY1Axis().setName("Value");
      hProfilerGraph.setPreferredSize(new Dimension(600, 400));
      hProfilerGraph.setMinimumSize(new Dimension(600, 400));
      hProfilerGraph.setHeaderFont(new Font("Dialog",Font.BOLD,18));
      hProfilerGraph.setHeader("Horizontal profile");
      hProfilerGraph.setLabelVisible(false);
      innerPanel.add(hProfilerGraph, BorderLayout.CENTER);

      hProfilerData = new JLDataView();
      hProfilerGraph.getY1Axis().addDataView(hProfilerData);

      hProfiler = new JFrame();
      hProfiler.setTitle("[profile]");
      hProfiler.setContentPane(innerPanel);

    }

  }

  private void constructVerticalProfiler() {

    if (vProfiler == null) {

      vProfiler = new LineProfilerViewer();
      vProfiler.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          //Free data
          vProfiler.setData(null);
          vProfiler.dispose();
        }
      });

    }

  }

  // -- Settings panel ------------------------------------------------------------------

  private void showSettings() {

    constructSettingsPanel();
    initSettings();
    if( !settingsFrame.isVisible() ) {
      ATKGraphicsUtils.centerFrame(this,settingsFrame);
    }
    settingsFrame.setVisible(true);

  }

  private void initSettings(){

    autoScaleCheck.setSelected(zAutoScale);
    minText.setEnabled(!zAutoScale);
    minText.setText(Double.toString(zMin));
    maxText.setEnabled(!zAutoScale);
    maxText.setText(Double.toString(zMax));
    gradEditor.setGradient(gColor);
    hLengthText.setText(Integer.toString(historyLength));
    vZoomCombo.setSelectedIndex(vZoom-1);
    hZoomCombo.setSelectedIndex(hZoom-1);

  }

  private void applySettings() {

    applyMinMax();
    applyHistoryLength();
    synchronized (this) {
      buildImage();
    }
    repaint();

  }

  private void applyMinMax() {

    double min,max;

    try {
      min = Double.parseDouble(minText.getText());
    } catch (NumberFormatException e1) {
      JOptionPane.showMessageDialog(this,"Invalid entry for min\n"+e1.getMessage());
      return;
    }

    try {
      max = Double.parseDouble(maxText.getText());
    } catch (NumberFormatException e2) {
      JOptionPane.showMessageDialog(this,"Invalid entry for max\n"+e2.getMessage());
      return;
    }

    if(min>=max) {
      JOptionPane.showMessageDialog(this,"min must be lower than max\n");
      return;
    }

    synchronized (this) {
      zMin = min;
      zMax = max;
    }

  }

  private void applyMinMaxAndBuild() {

    applyMinMax();
    synchronized (this) {
      buildImage();
    }
    repaint();

  }

  private void applyHistoryLength() {

    int hLength;

    try {
      hLength = Integer.parseInt(hLengthText.getText());
    } catch (NumberFormatException e1) {
      JOptionPane.showMessageDialog(this,"Invalid entry for history length\n"+e1.getMessage());
      return;
    }

    setHistoryLength(hLength);

  }

  private void applyHistoryLengthAndBuild() {

    applyHistoryLength();
    synchronized (this) {
      buildImage();
    }
    repaint();

  }

  private void applyAutoScale() {

    synchronized (this) {
      zAutoScale = autoScaleCheck.isSelected();
      minText.setEnabled(!zAutoScale);
      maxText.setEnabled(!zAutoScale);
      buildImage();
    }
    repaint();

  }

  private void applyHorizontalZoom() {
    setHorizontalZoom(hZoomCombo.getSelectedIndex()+1);
    synchronized (this) {
      buildImage();
    }
    repaint();
  }

  private void applyVerticalZoom() {
    setVerticalZoom(vZoomCombo.getSelectedIndex()+1);
    synchronized (this) {
      buildImage();
    }
    repaint();
  }

  private void applyHorizontalZoomIn() {
    if(hZoom<8) {
      synchronized (this) {
        hZoom++;
        if(hZoomCombo!=null) hZoomCombo.setSelectedIndex(hZoom-1);
        buildImage();
      }
      repaint();
    }
  }

  private void applyHorizontalZoomOut() {
    if(hZoom>1) {
      synchronized (this) {
        hZoom--;
        if(hZoomCombo!=null) hZoomCombo.setSelectedIndex(hZoom-1);
        buildImage();
      }
      repaint();
    }
  }

  private void applyVerticalZoomIn() {
    if(vZoom<8) {
      synchronized (this) {
        vZoom++;
        if(vZoomCombo!=null) vZoomCombo.setSelectedIndex(vZoom-1);
        buildImage();
      }
      repaint();
    }
  }

  private void applyVerticalZoomOut() {
    if(vZoom>1) {
      synchronized (this) {
        vZoom--;
        if(vZoomCombo!=null) vZoomCombo.setSelectedIndex(vZoom-1);
        buildImage();
      }
      repaint();
    }
  }

  private void showGradientEditor() {

    Gradient g = JGradientEditor.showDialog(settingsFrame, gColor);
    if (g != null) {
      gColor = g;
      gColormap = g.buildColorMap(65536);
      gradEditor.setGradient(gColor);
      gradEditor.repaint();
      gradientViewer.setGradient(gColor);
      gradientViewer.repaint();
    }

  }

  private void constructSettingsPanel() {

    if (settingsFrame == null) {

      // ------------------------------------------------------
      // Settings panel
      // ------------------------------------------------------
      settingsPanel = new JPanel();
      settingsPanel.setLayout(null);
      settingsPanel.setMinimumSize(new Dimension(290, 230));
      settingsPanel.setPreferredSize(new Dimension(290, 230));

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
      propButton.setFont(ATKConstant.labelFont);
      propButton.setMargin(new Insets(0, 0, 0, 0));
      propButton.setBounds(250, 5, 30, 30);
      propButton.addActionListener(this);
      settingsPanel.add(propButton);

      // ------------------------------------------------------------------------------------
      JSeparator js = new JSeparator();
      js.setBounds(0, 40, 500, 10);
      settingsPanel.add(js);

      autoScaleCheck = new JCheckBox("Auto scale");
      autoScaleCheck.setFont(ATKConstant.labelFont);
      autoScaleCheck.setBounds(5, 52, 100, 20);
      autoScaleCheck.setToolTipText("Auto scale colormap");
      autoScaleCheck.addActionListener(this);
      settingsPanel.add(autoScaleCheck);

      JLabel minLabel = new JLabel("Min");
      minLabel.setFont(ATKConstant.labelFont);
      minLabel.setBounds(110,50,30,25);
      settingsPanel.add(minLabel);

      minText = new JTextField();
      minText.setBounds(145,50,50,25);
      minText.addActionListener(this);
      settingsPanel.add(minText);

      JLabel maxLabel = new JLabel("Max");
      maxLabel.setFont(ATKConstant.labelFont);
      maxLabel.setBounds(200,50,30,25);
      settingsPanel.add(maxLabel);

      maxText = new JTextField();
      maxText.setBounds(235,50,50,25);
      maxText.addActionListener(this);
      settingsPanel.add(maxText);

      JLabel gradLabel = new JLabel("Colormap");
      gradLabel.setFont(ATKConstant.labelFont);
      gradLabel.setBounds(5, 80, 70, 20);
      settingsPanel.add(gradLabel);

      gradEditor = new JGradientEditor();
      gradEditor.setEditable(false);
      gradEditor.setToolTipText("Display the image using this colormap");
      gradEditor.setBounds(80, 80, 180, 20);
      settingsPanel.add(gradEditor);

      gradButton = new JButton();
      gradButton.setText("...");
      gradButton.setToolTipText("Edit colormap");
      gradButton.setFont(ATKConstant.labelFont);
      gradButton.setMargin(new Insets(0, 0, 0, 0));
      gradButton.setBounds(260, 80, 25, 20);
      gradButton.addActionListener(this);
      settingsPanel.add(gradButton);

      // ------------------------------------------------------------------------------------
      JSeparator js2 = new JSeparator();
      js2.setBounds(0, 110, 500, 10);
      settingsPanel.add(js2);

      JLabel hLengthLabel = new JLabel("History length");
      hLengthLabel.setFont(ATKConstant.labelFont);
      hLengthLabel.setBounds(5,120,90,25);
      settingsPanel.add(hLengthLabel);

      hLengthText = new JTextField();
      hLengthText.setBounds(100,120,185,25);
      hLengthText.addActionListener(this);
      settingsPanel.add(hLengthText);

      // ------------------------------------------------------------------------------------
      JSeparator js3 = new JSeparator();
      js3.setBounds(0, 150, 500, 10);
      settingsPanel.add(js3);

      JLabel hZoomLabel = new JLabel("Horz. zoom");
      hZoomLabel.setFont(ATKConstant.labelFont);
      hZoomLabel.setBounds(5, 160, 70, 20);
      settingsPanel.add(hZoomLabel);

      hZoomCombo = new JComboBox();
      hZoomCombo.setFont(ATKConstant.labelFont);
      hZoomCombo.addItem("100%");
      hZoomCombo.addItem("200%");
      hZoomCombo.addItem("300%");
      hZoomCombo.addItem("400%");
      hZoomCombo.addItem("500%");
      hZoomCombo.addItem("600%");
      hZoomCombo.addItem("700%");
      hZoomCombo.addItem("800%");
      hZoomCombo.setBounds(80, 160, 60, 22);
      hZoomCombo.addActionListener(this);
      settingsPanel.add(hZoomCombo);

      JLabel vZoomLabel = new JLabel("Vert. zoom");
      vZoomLabel.setFont(ATKConstant.labelFont);
      vZoomLabel.setBounds(150, 160, 70, 20);
      settingsPanel.add(vZoomLabel);

      vZoomCombo = new JComboBox();
      vZoomCombo.setFont(ATKConstant.labelFont);
      vZoomCombo.addItem("100%");
      vZoomCombo.addItem("200%");
      vZoomCombo.addItem("300%");
      vZoomCombo.addItem("400%");
      vZoomCombo.addItem("500%");
      vZoomCombo.addItem("600%");
      vZoomCombo.addItem("700%");
      vZoomCombo.addItem("800%");
      vZoomCombo.setBounds(225, 160, 60, 22);
      vZoomCombo.addActionListener(this);
      settingsPanel.add(vZoomCombo);

      // ------------------------------------------------------------------------------------

      okButton = new JButton();
      okButton.setText("Apply");
      okButton.setFont(ATKConstant.labelFont);
      okButton.setBounds(5, 200, 90, 25);
      okButton.addActionListener(this);
      settingsPanel.add(okButton);

      cancelButton = new JButton();
      cancelButton.setText("Dismiss");
      cancelButton.setFont(ATKConstant.labelFont);
      cancelButton.setBounds(195, 200, 90, 25);
      cancelButton.addActionListener(this);
      settingsPanel.add(cancelButton);

      settingsFrame = new JFrame();
      settingsFrame.setTitle("NumberSpectrumTrend Options");
      settingsFrame.setContentPane(settingsPanel);

    }

  }

  private void showPropertyFrame() {

    if (model != null) {
      if (propDialog == null)
        propDialog = new SimplePropertyFrame(settingsFrame, true);
      propDialog.setModel(model);
      propDialog.setVisible(true);
    }

  }

  private void readHistory() {

    // Retrieve attribute history
    INumberSpectrumHistory[] history = model.getNumberSpectrumHistory();
    if (history != null) {
      for (int i = 0; i < history.length; i++) {
        TrendData vals = new TrendData();
        vals.values = history[i].getValue();
        vals.time = history[i].getTimestamp();
        shiftData();
        data[0] = vals;
      }
    }

  }

  private void shiftData() {

    for(int i=historyLength-1;i>0;i--)
      data[i] = data[i-1];

  }

  private void buildImage() {

    // Comput ymax, zmax and zmin
    int ymax=0;
    boolean zRangeOK = false;
    double min = Double.MAX_VALUE;
    double max = -Double.MAX_VALUE;
    if(!zAutoScale) {
      max = zMax;
      min = zMin;
    }
    for(int i=0;i<historyLength;i++) {
      if(data[i]!=null && data[i].values!=null) {

        if(data[i].values.length>ymax) ymax=data[i].values.length;

        if( zAutoScale ) {
          for(int j=0;j<data[i].values.length;j++) {
            double v = data[i].values[j];
            if(!Double.isNaN(v)) {
              if(v<min) min=v;
              if(v>max) max=v;
              zRangeOK = true;
            }
          }
        }

      }
    }

    if( zRangeOK ) {
      if ((max - min) < 1e-100) {
        max += 0.999;
        min -= 0.999;
      }
    } else {
      // Only Nan or invalid data
      min = zMin;
      max = zMax;
    }

    // Update gradient viewer
    gradientViewer.getAxis().setMinimum(min);
    gradientViewer.getAxis().setMaximum(max);

    // Update image
    BufferedImage lastImg = trend.getImage();

    rdimx = historyLength;
    rdimy = (ymax==0)?rdimy:ymax;

    int dimx = rdimx*hZoom;
    int dimy = rdimy*vZoom;

    if (lastImg == null || lastImg.getHeight() != dimy || lastImg.getWidth() != dimx) {
      // Recreate the image
      lastImg = new BufferedImage(dimx, dimy, BufferedImage.TYPE_INT_RGB);
      trend.setImage(lastImg,hZoom,vZoom);
    }

    if(ymax==0 || (zAutoScale && !zRangeOK)) {

      // Only error or not initialized data
      Graphics2D g = (Graphics2D)lastImg.getGraphics();
      g.setColor(NaNColor);
      g.fillRect(0,0,rdimx,rdimy);

    } else {

      if(max-min<1e-20) max+=1.0;

      int rgbNaN = NaNColor.getRGB();
      int[] rgb = new int[dimx];
      for (int j = 0; j < rdimy; j++) {
        int ypos = rdimy - j - 1;

        for (int i = 0; i < rdimx; i++) {
          int xpos = (rdimx - i - 1);
          if (data[i] == null) {
            for (int i2 = 0; i2 < hZoom; i2++) rgb[hZoom * xpos + i2] = rgbNaN;
          } else {
            if (j >= data[i].values.length) {
              for (int i2 = 0; i2 < hZoom; i2++) rgb[hZoom * xpos + i2] = rgbNaN;
            } else {
              if (Double.isNaN(data[i].values[j])) {
                for (int i2 = 0; i2 < hZoom; i2++) rgb[hZoom * xpos + i2] = rgbNaN;
              } else {
                double c = ((data[i].values[j] - min) / (max - min)) * 65536.0;
                if (c < 0.0) c = 0.0;
                if (c > 65535.0) c = 65535.0;
                for (int i2 = 0; i2 < hZoom; i2++) rgb[hZoom * xpos + i2] = gColormap[(int) c];
              }
            }
          }
        }

        for (int j2 = 0; j2 < vZoom; j2++)
          lastImg.setRGB(0, vZoom*ypos+j2 , dimx, 1, rgb, 0, dimx);
      }
    }

    trend.setImage(lastImg,hZoom,vZoom);
    trendView.revalidate();
    revalidate();
  }


  public static void main(String[] args) {

    try {
      fr.esrf.tangoatk.core.AttributeList attributeList = new
        fr.esrf.tangoatk.core.AttributeList();

      NumberSpectrumTrend3DViewer nstv = new NumberSpectrumTrend3DViewer();
      nstv.setModel((INumberSpectrum) attributeList.add("jlp/test/1/att_spectrum"));
      nstv.setZAutoScale(false);
      nstv.setZMinimum(-0.5);
      nstv.setZMaximum(0.5);
      JFrame f = new JFrame();
      attributeList.startRefresher();
      f.setContentPane(nstv);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      ATKGraphicsUtils.centerFrameOnScreen(f);
      f.setVisible( true );
      attributeList.startRefresher();
    } catch (Exception e) {
      e.printStackTrace();
    }

  } // end of main ()



}
