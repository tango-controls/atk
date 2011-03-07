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

import com.braju.format.Format;
import fr.esrf.tangoatk.core.ISpectrumListener;
import fr.esrf.tangoatk.core.INumberSpectrum;
import fr.esrf.tangoatk.core.INumberSpectrumHistory;
import fr.esrf.tangoatk.widget.util.*;
import fr.esrf.tangoatk.widget.util.chart.*;
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
public class NumberSpectrumTrend3DViewer extends JComponent implements ISpectrumListener, ActionListener, MouseListener, J3DTrendListener, IJLChartListener {

  static final java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
  static final java.text.SimpleDateFormat genFormat = new java.text.SimpleDateFormat("dd/MM/yy HH:mm:ss");

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
  private boolean           readPollingHistory=true;
  private String            valueName="value";
  private String            yName="Y";
  private String[]          yIndexName=new String[0];
  private String            yUnit="";
  private double            yGain=1.0;
  private double            yOffset=0.0;
  private String            generalName=" ";
  private String            unitName="";
  private boolean           logScale=false;
  private int               zoomScroll;
  private String            format;

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
  private JCheckBox       logScaleCheck;
  private JTextField      formatText;

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
    rdimy = 256;
    hZoom = 1;
    vZoom = 1;
    zoomScroll = 0;
    gColor = new Gradient();
    gColor.buildRainbowGradient();
    gColormap = gColor.buildColorMap(65536);
    format = "";

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
    add(statusLabel,BorderLayout.NORTH);

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
    buildImage();

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
   * Returns the horizontal axis length in pixel.
   * @return Horizontal axis length
   */
  public int getHistoryLength() {
    return historyLength;
  }

  /**
   * Clear the viewer
   */
  public void clearData() {
    setData(null,null);
  }

  /**
   * Fill the viewer with arbitrary data
   * @param dates Time stamps
   * @param data Data
   */
  public void setData(long[] dates,double[][] data) {

    if( dates==null || data==null ) {

      synchronized (this) {

        // Clear the data
        TrendData[] newData = new TrendData[historyLength];
        for (int i=0; i < historyLength; i++) {
          newData[i] = null;
        }
        this.data = newData;
        buildImage();
        return;

      }

    }

    if(dates.length!=data.length) {
      System.out.println("Invalid data: date length and data length differ");
      return;
    }

    synchronized (this) {

      int nbData = dates.length;
      TrendData[] newData = new TrendData[nbData];
      for(int i=0;i<nbData;i++) {
        newData[nbData-i-1] = new TrendData();
        newData[nbData-i-1].time = dates[i];
        newData[nbData-i-1].values = data[i];
      }
      this.data = newData;
      historyLength = nbData;
      buildImage();

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

  /**
   * Returns true if the viewer is in autoscale mode for the colormap, false otherwise
   */
  public boolean isZAutoScale() {
    return zAutoScale;
  }

  /**
   * Sets the viewer in autoscale mode for the colormap when true, use min and max otherwise
   * @param autoScale AutoScale flag
   */
  public void setZAutoScale(boolean autoScale) {

    synchronized (this) {
      zAutoScale = autoScale;
    }

  }

  /**
   * Return true if the viewer is in log scale for the colormap, false otherwise
   */
  public boolean isLogScale() {
    return logScale;
  }

  /**
   * Set the viewer in linear or log scale for the colormap
   * @param logScale LogScale flag
   */
  public void setLogScale(boolean logScale) {

    synchronized (this) {
      this.logScale = logScale;
    }

  }

  /**
   * Sets the colormap.
   * @param g Gradient colormap
   */
  public void setGradient(Gradient g) {

    synchronized (this) {
      gColor = g;
      gColormap = g.buildColorMap(65536);
      gradientViewer.setGradient(gColor);
      gradientViewer.repaint();
    }

  }

  /**
   * Returns the gradient used by the viewer.
   */
  public Gradient getGradient() {
    return gColor;
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
   * -10 = 10%
   * -9 = 11%
   * -8 = 12%
   * -7 = 14%
   * -6 = 16%
   * -5 = 20%
   * -4 = 25%
   * -3 = 33%
   * -2 = 50%
   * -1 = 100%
   *  0 = Not allowed
   *  1 = 100%
   *  2 = 200%
   *  ...
   *  8 = 800%
   * @param zoom zoom factor (between -4 and 8)
   */
  public void setHorizontalZoom(int zoom) {
    synchronized (this) {
      zoomScroll=0;
      hZoom=zoom;
    }
  }

  /**
   * Return the horizontal zoom factor.
   */
  public int getHorizontalZoom() {
    return hZoom;
  }

  /**
   * Sets the vertical zoom factor
   * -10 = 10%
   * -9 = 11%
   * -8 = 12%
   * -7 = 14%
   * -6 = 16%
   * -5 = 20%
   * -4 = 25%
   * -3 = 33%
   * -2 = 50%
   * -1 = 100%
   *  0 = Not allowed
   *  1 = 100%
   *  2 = 200%
   *  ...
   *  8 = 800%
   * @param zoom zoom factor (between -4 and 8)
   */
  public void setVerticalZoom(int zoom) {
    synchronized (this) {
      vZoom=zoom;
    }
  }

  /**
   * Return the vertical zoom factor
   */
  public int getVerticalZoom() {
    return vZoom;
  }
  
  public void setFormat(String f) {
    format = f;
  }

  public String getFormat() {
    return format;
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
    if(readPollingHistory) readHistory();
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

    int xData;

    if (hZoom >= 1) {
      // Zoom
      xData = historyLength - x / hZoom - 1;
    } else {
      // UnZoom
      xData = historyLength - x * (-hZoom) - 1;
    }

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

    int xData;
    int yData;

    if(hZoom>=1) {
      xData = historyLength - x/hZoom - 1;
    } else {
      xData = historyLength - x*(-hZoom) - 1;
    }

    if(xData>=0 && xData<historyLength) {
      if( data[xData] != null ) {
        if( vZoom>=1 ) {
          yData = y/vZoom;
        } else {
          yData = y*(-vZoom);
        }
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
   * Clear the values in the status label, only name is printed until the next click.
   */
  public void clearStatusLabel() {

    trend.clearCursor();

  }

  /**
   * Update cursor information. This function is trigerred when the user
   * click on the image.
   * @param xCursor x coordinates (referenced by the image)
   * @param yCursor y coordinates (referenced by the image)
   */
  public void updateCursor(int xCursor, int yCursor) {

    String modelName = generalName;
    String modelUnit = unitName;

    synchronized (this) {

      if (model != null) {
        modelName = model.getName();
        modelUnit = model.getUnit();
      }

      if (xCursor < 0) {
        statusLabel.setText(modelName);
      } else {
        long time = getTimeAt(xCursor);
        if (time == 0) {
          statusLabel.setText(modelName + "  | no data at marker position");
        } else {
          calendar.setTimeInMillis(time);
          Date date = calendar.getTime();
          String timeStr = genFormat.format(date);
          double val = getValueAt(xCursor, yCursor);

          if (Double.isNaN(val)) {

            if (vZoom >= 1) {

              int yIndex = (int) ((yCursor / vZoom) * yGain + yOffset + 0.5);
              if (yIndex >= 0 && yIndex < yIndexName.length) {
                statusLabel.setText(modelName + "  | " + timeStr + " " +
                        valueName + "=NaN at " + yName + "=" +
                        yIndexName[yIndex] + " " + yUnit);
              } else {
                statusLabel.setText(modelName + "  | " + timeStr + " " +
                        valueName + "=NaN at " + yName + "=" +
                        ((yCursor / vZoom) * yGain + yOffset) + " " + yUnit);
              }

            } else {

              int yIndex = (int) ((yCursor * (-vZoom)) * yGain + yOffset + 0.5);
              if (yIndex >= 0 && yIndex < yIndexName.length) {

                statusLabel.setText(modelName + "  | " + timeStr + " " +
                        valueName + "=NaN at " + yName + "=" +
                        yIndexName[yIndex] + " " + yUnit);

              } else {
                statusLabel.setText(modelName + "  | " + timeStr + " " +
                        valueName + "=NaN at " + yName + "=" +
                        ((yCursor * (-vZoom)) * yGain + yOffset) + " " + yUnit);
              }

            }

          } else {

            String value;
            if (format.length() > 0) {
              Object[] o = {val};
              value = Format.sprintf(format, o);
            } else {
              value = Double.toString(val);
            }

            if (vZoom >= 1) {

              int yIndex = (int) ((yCursor / vZoom) * yGain + yOffset + 0.5);
              if (yIndex >= 0 && yIndex < yIndexName.length) {
                statusLabel.setText(modelName + "  | " + timeStr + " " +
                        valueName + "=" + value + " " + modelUnit + " at " + yName + "=" +
                        yIndexName[yIndex] + " " + yUnit);

              } else {
                statusLabel.setText(modelName + "  | " + timeStr + " " +
                        valueName + "=" + value + " " + modelUnit + " at " + yName + "=" +
                        ((yCursor / vZoom) * yGain + yOffset) + " " + yUnit);

              }

            } else {

              int yIndex = (int) ((yCursor * (-vZoom)) * yGain + yOffset + 0.5);
              if (yIndex >= 0 && yIndex < yIndexName.length) {

                statusLabel.setText(modelName + "  | " + timeStr + " " +
                        valueName + "=" + value + " " + modelUnit + " at " + yName + "=" +
                        yIndexName[yIndex] + " " + yUnit);

              } else {

                statusLabel.setText(modelName + "  | " + timeStr + " " +
                        valueName + "=" + value + " " + modelUnit + " at " + yName + "=" +
                        ((yCursor * (-vZoom)) * yGain + yOffset) + " " + yUnit);

              }

            }

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
   * Reads the polling history when setting the model
   * @param readPolling true to enable history reading, false otherwise
   */
  public void readPollingHistory(boolean readPolling) {
    readPollingHistory = readPolling;
  }

  /**
   * Sets the name wich is displayed in the status line for the value.
   * @param vName value name
   */
  public void setValueName(String vName) {
    if( vName.length()==0 ) {
      valueName = "value";
    } else {
      valueName = vName;
    }
  }

  /**
   * Sets the name which is displayed in the status line for the y value.
   * @param yName y axis name
   */
  public void setYName(String yName) {
    if( yName.length()==0 ) {
      this.yName = "Y";
    } else {
      this.yName = yName;
    }
  }

  /**
   * Sets the names of Y index
   * @param idxName Array of string containing vertical index name
   */
  public void setYIndexName(String[] idxName) {

    if( idxName==null ) {
      yIndexName = new String[0];
    } else {
      yIndexName = idxName;
    }

  }

  /**
   * Sets the unit of the y axis
   * @param unit Y axis unit
   */
  public void setYUnit(String unit) {
    this.yUnit = unit;
  }

  /**
   * Sets the Y axis coordinates transformation
   * @param gain Y gain
   * @param offset Y offset
   */
  public void setYTransfom(double gain,double offset) {
    yGain = gain;
    yOffset = offset;
  }

  /**
   * Sets the name which is displayed in the status line for the unit.
   * @param unit Unit
   */
  public void setUnit(String unit) {
    unitName = unit;
  }

  /**
   * Sets the main name which is displayed in the status line.
   * @param name Name
   */
  public void setName(String name) {
    if(name.length()==0) {
      generalName=" ";
    } else {
      generalName=name;
    }
  }

  /**
   * Rebuild image when some settings has been changed.
   */
  public void commit() {

    synchronized (this) {
      buildImage();
    }
    repaint();

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
    } else if ( src==logScaleCheck ) {
      applyLogScale();
    } else if ( src==formatText ) {
      applyFormat();
    }
    
  }

  // ------------------------------------------------------------------------
  // Chart listener
  // ------------------------------------------------------------------------

  public String[] clickOnChart(JLChartEvent evt) {

    String[] ret = new String[0];
    String modelUnit = unitName;
    if (model != null) modelUnit = model.getUnit();

    Object src = evt.getSource();

    if( src==hProfilerGraph ) {

      ret = new String[2];

      long time = (long)evt.getTransformedXValue();
      calendar.setTimeInMillis(time);
      Date date = calendar.getTime();
      ret[0] = genFormat.format(date);

      double val = evt.getTransformedYValue();
      String value;
      if (format.length() > 0) {
        Object[] o = {val};
        value = Format.sprintf(format, o);
      } else {
        value = Double.toString(val);
      }
      ret[1] = valueName + "=" + value + " " + modelUnit;

    }

    return ret;

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
        hZoomOutMenuItem.setEnabled(hZoom>-10);
        vZoomInMenuItem.setEnabled(vZoom<8);
        vZoomOutMenuItem.setEnabled(vZoom>-10);
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
      if( hZoom>=1 ) {
        trend.shiftCursorX(-hZoom);
      } else {
        zoomScroll++;
        if(zoomScroll>=-hZoom) {
          trend.shiftCursorX(-1);
          zoomScroll=0;
        }
      }
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
      if( hZoom>=1 ) {
        trend.shiftCursorX(-hZoom);
      } else {
        zoomScroll++;
        if(zoomScroll>=-hZoom) {
          trend.shiftCursorX(-1);
          zoomScroll=0;
        }
      }
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

    String modelUnit = unitName;
    if (model != null) modelUnit = model.getUnit();

    constructVerticalProfiler();
    vProfiler.setMode(LineProfilerViewer.LINE_MODE_SINGLE);
    vProfiler.setXAxisName(yName + " [" + yUnit + "]");
    vProfiler.setYAxisName(valueName + " [" + modelUnit + "]");
    synchronized (this) {
      buildVerticalProfile();
    }
    if(!vProfiler.isVisible())
      ATKGraphicsUtils.centerFrameOnScreen(vProfiler);
    vProfiler.setVisible(true);

  }

  private void buildVerticalProfile() {

    int xData;
    int x = trend.getXCursor();
    double[] vals = new double[rdimy];
    for(int i=0;i<rdimy;i++) vals[i]=Double.NaN;
    String title = "Vertical profile";

    if (hZoom >= 1) {
      // Zoom
      xData = historyLength - x / hZoom - 1;
    } else {
      // UnZoom
      xData = historyLength - x * (-hZoom) - 1;
    }
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

    vProfiler.setData(vals,yGain,yOffset);
    vProfiler.setTitle("[profile]");
    vProfiler.getProfile1().getChart().setHeader(title);
    vProfiler.getProfile1().getChart().setLabelVisible(false);
    vProfiler.setFormat(format);

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
    int yData;
    if(vZoom>=1) {
      yData = y/vZoom;
    } else {
      yData = y*(-vZoom);
    }
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

    if(yData>=0 && yData<rdimy) {
      int idx = (int)(yData*yGain+yOffset+0.5);
      if( idx>=0 && idx<yIndexName.length) {
        hProfilerGraph.setHeader("Horizontal profile at " + yName + "=" + yIndexName[idx]);
      } else {
        hProfilerGraph.setHeader("Horizontal profile at " + yName + "=" + (yData*yGain + yOffset));
      }
    } else {
      hProfilerGraph.setHeader("Horizontal profile");      
    }
    hProfilerGraph.repaint();

  }

  private void constructHorizontalProfiler() {

    if (hProfiler == null) {

      String modelUnit = unitName;
      if (model != null) modelUnit = model.getUnit();

      JPanel innerPanel = new JPanel(new BorderLayout());

      hProfilerGraph = new JLChart();
      hProfilerGraph.setBorder(new javax.swing.border.EtchedBorder());
      hProfilerGraph.getXAxis().setAutoScale(true);
      hProfilerGraph.getXAxis().setAnnotation(JLAxis.TIME_ANNO);
      hProfilerGraph.getXAxis().setGridVisible(true);
      hProfilerGraph.getXAxis().setName("Time");
      hProfilerGraph.getY1Axis().setAutoScale(true);
      hProfilerGraph.getY1Axis().setGridVisible(true);
      hProfilerGraph.getY1Axis().setName(valueName + " [" + modelUnit + "]");
      hProfilerGraph.setPreferredSize(new Dimension(600, 400));
      hProfilerGraph.setMinimumSize(new Dimension(600, 400));
      hProfilerGraph.setHeaderFont(new Font("Dialog",Font.BOLD,18));
      hProfilerGraph.setHeader("Horizontal profile");
      hProfilerGraph.setLabelVisible(false);
      hProfilerGraph.setJLChartListener(this);
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

  public void showSettings() {

    constructSettingsPanel();
    initSettings();
    if( !settingsFrame.isVisible() ) {
      ATKGraphicsUtils.centerFrame(this,settingsFrame);
    }
    settingsFrame.setVisible(true);

  }

  private void setZoomCombo(JComboBox combo,int zoom) {

    switch(zoom) {
      case -10: // 10%
        combo.setSelectedIndex(0);
        break;
      case -9: // 11%
        combo.setSelectedIndex(1);
        break;
      case -8: // 12%
        combo.setSelectedIndex(2);
        break;
      case -7: // 14%
        combo.setSelectedIndex(3);
        break;
      case -6: // 16%
        combo.setSelectedIndex(4);
        break;
      case -5: // 25%
        combo.setSelectedIndex(5);
        break;
      case -4: // 25%
        combo.setSelectedIndex(6);
        break;
      case -3: // 33%
        combo.setSelectedIndex(7);
        break;
      case -2: // 50%
        combo.setSelectedIndex(8);
        break;
      case 1:  // 100%
        combo.setSelectedIndex(9);
        break;
      case 2:  // 200%
        combo.setSelectedIndex(10);
        break;
      case 3:  // 300%
        combo.setSelectedIndex(11);
        break;
      case 4:  // 400%
        combo.setSelectedIndex(12);
        break;
      case 5:  // 500%
        combo.setSelectedIndex(13);
        break;
      case 6:  // 600%
        combo.setSelectedIndex(14);
        break;
      case 7:  // 700%
        combo.setSelectedIndex(15);
        break;
      case 8:  // 800%
        combo.setSelectedIndex(16);
        break;
    }

  }

  private int getZoomCombo(JComboBox combo) {

    int s = combo.getSelectedIndex();
    switch(s) {
      case 0:
        return -10;
      case 1:
        return -9;
      case 2:
        return -8;
      case 3:
        return -7;
      case 4:
        return -6;
      case 5:
        return -5;
      case 6:
        return -4;
      case 7:
        return -3;
      case 8:
        return -2;
      case 9:
        return 1;
      case 10:
        return 2;
      case 11:
        return 3;
      case 12:
        return 4;
      case 13:
        return 5;
      case 14:
        return 6;
      case 15:
        return 7;
      case 16:
        return 8;
    }

    return 1;

  }

  private void initSettings(){

    autoScaleCheck.setSelected(zAutoScale);
    minText.setEnabled(!zAutoScale);
    minText.setText(Double.toString(zMin));
    maxText.setEnabled(!zAutoScale);
    maxText.setText(Double.toString(zMax));
    gradEditor.setGradient(gColor);
    hLengthText.setText(Integer.toString(historyLength));
    setZoomCombo(vZoomCombo,vZoom);
    setZoomCombo(hZoomCombo,hZoom);
    logScaleCheck.setSelected(logScale);
    formatText.setText(format);

  }

  private void applySettings() {

    applyMinMax();
    applyHistoryLength();
    applyFormat();
    synchronized (this) {
      buildImage();
    }
    repaint();

  }

  private void applyFormat() {

    format = formatText.getText();
    // Update format in the label string
    trend.cursorMove();

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

    if( logScale && !zAutoScale ) {
      if(min<=0 || max<=0) {
        JOptionPane.showMessageDialog(this,"min and max must be strictly positive in log scale\n");
        return;
      }
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

  private void applyLogScale() {

    if(!zAutoScale) {
      if(zMin<=0 || zMax<=0) {
        JOptionPane.showMessageDialog(this,"min and max must be strictly positive in log scale\n");
        logScaleCheck.setSelected(false);
        logScale = false;
        return;
      }
    }

    synchronized (this) {
      logScale = logScaleCheck.isSelected();
      buildImage();
    }
    repaint();

  }

  private void applyAutoScale() {

    synchronized (this) {
      zAutoScale = autoScaleCheck.isSelected();
      if(!zAutoScale) {
        if (logScale) {
          if (zMin <= 0 || zMax <= 0) {
            JOptionPane.showMessageDialog(this, "min and max must be strictly positive in log scale\n");
            zAutoScale = true;
            autoScaleCheck.setSelected(true);
            return;
          }
        }
      }
      minText.setEnabled(!zAutoScale);
      maxText.setEnabled(!zAutoScale);
      buildImage();
    }
    repaint();

  }

  private void applyHorizontalZoom() {
    setHorizontalZoom(getZoomCombo(hZoomCombo));
    synchronized (this) {
      buildImage();
    }
    repaint();
  }

  private void applyVerticalZoom() {
    setVerticalZoom(getZoomCombo(vZoomCombo));
    synchronized (this) {
      buildImage();
    }
    repaint();
  }

  private void applyHorizontalZoomIn() {
    if(hZoom<8) {
      synchronized (this) {
        hZoom++;
        if(hZoom==-1) hZoom=1;
        if(hZoomCombo!=null) setZoomCombo(hZoomCombo,hZoom);
        zoomScroll=0;
        buildImage();
      }
      repaint();
    }
  }

  private void applyHorizontalZoomOut() {
    if(hZoom>-10) {
      synchronized (this) {
        hZoom--;
        if(hZoom==0) hZoom=-2;
        if(hZoomCombo!=null) setZoomCombo(hZoomCombo,hZoom);
        zoomScroll=0;
        buildImage();
      }
      repaint();
    }
  }

  private void applyVerticalZoomIn() {
    if(vZoom<8) {
      synchronized (this) {
        vZoom++;
        if(vZoom==-1) vZoom=1;
        if(vZoomCombo!=null) setZoomCombo(vZoomCombo,vZoom);
        buildImage();
      }
      repaint();
    }
  }

  private void applyVerticalZoomOut() {
    if(vZoom>-10) {
      synchronized (this) {
        vZoom--;
        if(vZoom==0) vZoom=-2;
        if(vZoomCombo!=null) setZoomCombo(vZoomCombo,vZoom-1);
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
      settingsPanel.setMinimumSize(new Dimension(290, 290));
      settingsPanel.setPreferredSize(new Dimension(290, 290));

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
      /*
      * -10 = 10%
      * -9 = 11%
      * -8 = 12%
      * -7 = 14%
      * -6 = 16%
      * -5 = 20%
      * -4 = 25%
      * -3 = 33%
      * -2 = 50%
      * -1 = 100%
      *  0 = Not allowed
      *  1 = 100%
      *  2 = 200%
      *  ...
      *  8 = 800%
      */
      hZoomCombo.addItem("10%");
      hZoomCombo.addItem("11%");
      hZoomCombo.addItem("12%");
      hZoomCombo.addItem("14%");
      hZoomCombo.addItem("16%");
      hZoomCombo.addItem("20%");
      hZoomCombo.addItem("25%");
      hZoomCombo.addItem("33%");
      hZoomCombo.addItem("50%");
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
      vZoomCombo.addItem("10%");
      vZoomCombo.addItem("11%");
      vZoomCombo.addItem("12%");
      vZoomCombo.addItem("14%");
      vZoomCombo.addItem("16%");
      vZoomCombo.addItem("20%");
      vZoomCombo.addItem("25%");
      vZoomCombo.addItem("33%");
      vZoomCombo.addItem("50%");
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
      JSeparator js4 = new JSeparator();
      js4.setBounds(0, 190, 500, 10);
      settingsPanel.add(js4);

      logScaleCheck = new JCheckBox("Display log values");
      logScaleCheck.setFont(ATKConstant.labelFont);
      logScaleCheck.setBounds(5, 200, 200, 20);
      logScaleCheck.setToolTipText("Display log values");
      logScaleCheck.addActionListener(this);
      settingsPanel.add(logScaleCheck);

      JLabel formatLabel = new JLabel("Format");
      formatLabel.setFont(ATKConstant.labelFont);
      formatLabel.setBounds(5,230,100,25);
      settingsPanel.add(formatLabel);
      formatText = new JTextField();
      formatText.addActionListener(this);
      formatText.setEditable(true);
      formatText.setBounds(110,230,175,25);
      settingsPanel.add(formatText);
      
      // ------------------------------------------------------------------------------------

      okButton = new JButton();
      okButton.setText("Apply");
      okButton.setFont(ATKConstant.labelFont);
      okButton.setBounds(5, 260, 90, 25);
      okButton.addActionListener(this);
      settingsPanel.add(okButton);

      cancelButton = new JButton();
      cancelButton.setText("Dismiss");
      cancelButton.setFont(ATKConstant.labelFont);
      cancelButton.setBounds(195, 260, 90, 25);
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

  private double computeHighTen(double d) {
    int p = (int)Math.log10(d);
    return Math.pow(10.0, p + 1);
  }

  private double computeLowTen(double d) {
    int p = (int)Math.log10(d);
    return Math.pow(10.0, p);
  }

  private void buildImage() {

    // Compute ymax, zmax and zmin
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
            if (logScale) {

              if(!Double.isNaN(data[i].values[j]) && data[i].values[j]>0) {
                double v = data[i].values[j];
                if (v < min) min = v;
                if (v > max) max = v;
                zRangeOK = true;
              }

            } else {

              double v = data[i].values[j];
              if (!Double.isNaN(v)) {
                if (v < min) min = v;
                if (v > max) max = v;
                zRangeOK = true;
              }

            }
          }
        }

      }
    }

    if( zRangeOK ) {
      if( logScale ) {
        if ((max - min) < 1e-100) {
          max = computeHighTen(max);
          min = computeLowTen(min);
        }
      } else {
        if ((max - min) < 1e-100) {
          max += 0.999;
          min -= 0.999;
        }
      }
    } else {
      // Only Nan or invalid data
      min = zMin;
      max = zMax;
    }

    // Update gradient viewer
    gradientViewer.getAxis().setMinimum(min);
    gradientViewer.getAxis().setMaximum(max);
    if( logScale ) {
      gradientViewer.getAxis().setScale(JLAxis.LOG_SCALE);
    } else {
      gradientViewer.getAxis().setScale(JLAxis.LINEAR_SCALE);
    }

    // Update image
    BufferedImage lastImg = trend.getImage();

    rdimx = historyLength;
    rdimy = (ymax==0)?rdimy:ymax;

    int dimx;
    if(hZoom>=1) {
      dimx = rdimx*hZoom;
    } else {
      dimx = rdimx/(-hZoom);
    }
    int dimy;
    if(vZoom>=1) {
      dimy = rdimy*vZoom;
    } else {
      dimy = rdimy/(-vZoom);
    }

    if (lastImg == null || lastImg.getHeight() != dimy || lastImg.getWidth() != dimx) {
      // Recreate the image
      lastImg = new BufferedImage(dimx, dimy, BufferedImage.TYPE_INT_RGB);
      trend.setImage(lastImg,hZoom,vZoom);
    }

    if (ymax == 0 || (zAutoScale && !zRangeOK)) {

      // Only error or not initialized data
      Graphics2D g = (Graphics2D) lastImg.getGraphics();
      g.setColor(NaNColor);
      g.fillRect(0, 0, dimx, dimy);

    } else {

      if (max - min < 1e-20) max += 1.0;
      if (logScale) {
        min = Math.log10(min);
        max = Math.log10(max);
      }

      int rgbNaN = NaNColor.getRGB();
      int[] rgb = new int[dimx];

      if (vZoom >= 1 && hZoom >= 1) {

        // X zoom, Y zoom --------------------------------------------------------------------------
        for (int j = 0; j < rdimy; j++) {

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
                  double c;
                  if (logScale)
                    c = ((Math.log10(data[i].values[j]) - min) / (max - min)) * 65536.0;
                  else
                    c = ((data[i].values[j] - min) / (max - min)) * 65536.0;
                  if (c < 0.0) c = 0.0;
                  if (c > 65535.0) c = 65535.0;
                  for (int i2 = 0; i2 < hZoom; i2++) rgb[hZoom * xpos + i2] = gColormap[(int) c];
                }
              }
            }
          }

          for (int j2 = 0; j2 < vZoom; j2++)
            lastImg.setRGB(0, vZoom * j + j2, dimx, 1, rgb, 0, dimx);
        }

      } else if (vZoom >= 1 && hZoom < 0) {

        // X unzoom, Y zoom ----------------------------------------------------------------------------
        for (int j = 0; j < rdimy; j++) {

          for (int i = 0; i < rdimx; i+=(-hZoom)) {
            int xpos = (rdimx - i - 1)/(-hZoom);
            if (data[i] == null) {
              if(xpos<dimx) rgb[xpos] = rgbNaN;
            } else {
              if (j >= data[i].values.length) {
                if(xpos<dimx) rgb[xpos] = rgbNaN;
              } else {
                if (Double.isNaN(data[i].values[j])) {
                  if(xpos<dimx) rgb[xpos] = rgbNaN;
                } else {
                  double c;
                  if (logScale)
                    c = ((Math.log10(data[i].values[j]) - min) / (max - min)) * 65536.0;
                  else
                    c = ((data[i].values[j] - min) / (max - min)) * 65536.0;
                  if (c < 0.0) c = 0.0;
                  if (c > 65535.0) c = 65535.0;
                  if(xpos<dimx) rgb[xpos] = gColormap[(int) c];
                }
              }
            }
          }

          for (int j2 = 0; j2 < vZoom; j2++)
            lastImg.setRGB(0, vZoom * j + j2, dimx, 1, rgb, 0, dimx);
        }

      } else if (vZoom < 0 && hZoom >= 1) {

        // X zoom, Y unzoom --------------------------------------------------------------------------
        for (int j = 0; j < rdimy; j+=(-vZoom)) {

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
                  double c;
                  if (logScale)
                    c = ((Math.log10(data[i].values[j]) - min) / (max - min)) * 65536.0;
                  else
                    c = ((data[i].values[j] - min) / (max - min)) * 65536.0;
                  if (c < 0.0) c = 0.0;
                  if (c > 65535.0) c = 65535.0;
                  for (int i2 = 0; i2 < hZoom; i2++) rgb[hZoom * xpos + i2] = gColormap[(int) c];
                }
              }
            }
          }

          if((j/(-vZoom))<dimy) lastImg.setRGB(0,  j/(-vZoom) , dimx, 1, rgb, 0, dimx);
        }

      } else if (vZoom < 0 && hZoom < 0) {

        // X unzoom, Y unzoom ---------------------------------------------------------------------
        for (int j = 0; j < rdimy; j+=(-vZoom)) {

          for (int i = 0; i < rdimx; i+=(-hZoom)) {
            int xpos = (rdimx - i - 1)/(-hZoom);
            if (data[i] == null) {
              if(xpos<dimx) rgb[xpos] = rgbNaN;
            } else {
              if (j >= data[i].values.length) {
                if(xpos<dimx) rgb[xpos] = rgbNaN;
              } else {
                if (Double.isNaN(data[i].values[j])) {
                  if(xpos<dimx) rgb[xpos] = rgbNaN;
                } else {
                  double c;
                  if (logScale)
                    c = ((Math.log10(data[i].values[j]) - min) / (max - min)) * 65536.0;
                  else
                    c = ((data[i].values[j] - min) / (max - min)) * 65536.0;
                  if (c < 0.0) c = 0.0;
                  if (c > 65535.0) c = 65535.0;
                  if(xpos<dimx) rgb[xpos] = gColormap[(int) c];
                }
              }
            }
          }

          if((j/(-vZoom))<dimy) lastImg.setRGB(0,  j/(-vZoom) , dimx, 1, rgb, 0, dimx);
        }

      }

    }

    trend.setImage(lastImg,rdimx,rdimy);
    trendView.getViewport().revalidate();
    revalidate();

  }

  public static void main(String[] args) {

    try {
      fr.esrf.tangoatk.core.AttributeList attributeList = new
        fr.esrf.tangoatk.core.AttributeList();

      final NumberSpectrumTrend3DViewer nstv = new NumberSpectrumTrend3DViewer();

      nstv.readPollingHistory(true);

      /*
      int nbData = 1000;
      long dates[] = new long[nbData];
      double[][] data = new double[nbData][256];
      long startDate = System.currentTimeMillis()-nbData*1000;
      for(int i=0;i<nbData;i++) {
        dates[i] = startDate + i*1000;
        for(int j=0;j<256;j++) {
          data[i][j] = (double)j/256.0;
        }
      }
      nstv.setData(dates,data);
      */

      //nstv.getXAxis().setVisible(false);
      //nstv.getYAxis().setVisible(false);
      nstv.setName("Data test");
      nstv.setUnit("au");
      nstv.setYName("Height");
      nstv.setValueName("Strength");
      nstv.setYUnit("yu");
      nstv.setFormat("%.3f");
      String[] idxName = new String[200];
      for(int i=0;i<idxName.length;i++) {
        idxName[i] = new String("I"+i);
      }
      nstv.setYIndexName(idxName);
      //nstv.setYTransfom(2,10);

      nstv.setModel((INumberSpectrum) attributeList.add("jlp/test/1/att_spectrum"));
      nstv.setZAutoScale(true);
      nstv.setZMinimum(-0.5);
      nstv.setZMaximum(0.5);
      attributeList.startRefresher();
      
      JFrame f = new JFrame();
      JMenuBar menuBar = new JMenuBar();
      JMenu menu = new JMenu("Test");
      menuBar.add(menu);
      JMenuItem clearMenuItem = new JMenuItem("Clear");
      clearMenuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          nstv.clearData();
        }
      });
      JMenuItem cursorMenuItem = new JMenuItem("Remove cursor");
      cursorMenuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          nstv.clearStatusLabel();
        }
      });
      menu.add(clearMenuItem);
      menu.add(cursorMenuItem);
      f.setJMenuBar(menuBar);

      f.setContentPane(nstv);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      ATKGraphicsUtils.centerFrameOnScreen(f);
      f.setVisible( true );

    } catch (Exception e) {
      e.printStackTrace();
    }

  } // end of main ()

}
