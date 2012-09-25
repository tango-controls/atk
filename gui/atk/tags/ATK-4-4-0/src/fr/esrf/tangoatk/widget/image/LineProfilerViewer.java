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
 
package fr.esrf.tangoatk.widget.image;

import fr.esrf.tangoatk.widget.util.chart.*;
import fr.esrf.tangoatk.widget.util.JTableRow;
import com.braju.format.Format;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A Class to display a line profile
 * @author pons
 */

public class LineProfilerViewer extends JFrame {

  public final static int LINE_MODE_SINGLE  = 1;
  public final static int LINE_MODE_DOUBLE  = 2;
  public final static int HISTOGRAM_MODE    = 3;

  private JSplitPane splitPane;
  private LineProfilerPanel profile1;
  private LineProfilerPanel profile2;
  private int mode;

  public LineProfilerViewer() {
    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    profile1  = new LineProfilerPanel();
    profile2  = null; // Does not construct the second profiler yet
    splitPane.setLeftComponent(profile1);
    splitPane.setRightComponent(null);
    mode = LINE_MODE_SINGLE;
    setContentPane(splitPane);
    pack();
  }

  public void setXAxisName(String name) {
    profile1.getChart().getXAxis().setName(name);
  }

  public void setYAxisName(String name) {
    profile1.getChart().getY1Axis().setName(name);
  }

  public void setFormat(String format) {
    profile1.setFormat(format);
    if(profile2!=null) profile2.setFormat(format);
  }

  public void setMode(int mode) {

    this.mode = mode;

    switch(this.mode) {

      case LINE_MODE_SINGLE:
        setTitle("[profile] ImageViewer");
        profile1.getChart().setHeader("Line profile");
        profile1.getChart().getXAxis().setName("Pixel index");
        profile1.getChart().getY1Axis().setName("Value");
        profile1.getChart().setName("Pixel value");
        if(profile1.getChart().isZoomed()) profile1.getChart().exitZoom();
        if(profile2!=null) {
          splitPane.setRightComponent(null);
          profile2.setVisible(false);
        }
        break;

      case LINE_MODE_DOUBLE:
        setTitle("[profile] ImageViewer");
        if(profile2==null) profile2 = new LineProfilerPanel();

        profile1.getChart().setHeader("Line profile (Horizontal)");
        profile1.getChart().getXAxis().setName("Pixel index");
        profile1.getChart().getY1Axis().setName("Value");
        profile1.getChart().setName("Pixel value");
        if(profile1.getChart().isZoomed()) profile1.getChart().exitZoom();
        profile2.getChart().setHeader("Line profile (Vertical)");
        profile2.getChart().getXAxis().setName("Pixel index");
        profile2.getChart().getY1Axis().setName("Value");
        profile2.getChart().setName("Pixel value");
        if(profile2.getChart().isZoomed()) profile2.getChart().exitZoom();
        splitPane.setRightComponent(profile2);
        profile2.setVisible(true);
        break;

      case HISTOGRAM_MODE:
        setTitle("[Histogram] ImageViewer");
        profile1.getChart().setHeader("Histogram");
        profile1.getChart().getXAxis().setName("Pixel value");
        profile1.getChart().getY1Axis().setName("Number");
        profile1.getChart().setName("pixel number");
        if(profile1.getChart().isZoomed()) profile1.getChart().exitZoom();
        if(profile2!=null) {
          splitPane.setRightComponent(null);
          profile2.setVisible(false);
        }
        break;

    }
    pack();

  }

  public void setLineProfileMode() {
    setMode(LINE_MODE_SINGLE);
  }

  public void setHistogramMode() {
    setMode(HISTOGRAM_MODE);
  }

  public void setData(double[] v) {
    setData(v,0);
  }

  public void setData(double[] v,double xgain,double xoffset) {
    profile1.setData(v,xgain,xoffset);
  }

  public void setData(double[] v, int startIndexing) {
    profile1.setData(v,startIndexing);
  }

  public void setData2(double[] v) {
    setData2(v,0);
  }

  public void setData2(double[] v, int startIndexing) {
    if(profile2!=null) profile2.setData(v,startIndexing);
  }

  /**
   * @return the profile1
   */
  public LineProfilerPanel getProfile1 () {
    return profile1;
  }

  /**
   * @return the profile2
   */
  public LineProfilerPanel getProfile2 () {
    return profile2;
  }

  public static void main(String[] args) {
    final LineProfilerViewer l = new LineProfilerViewer();
    l.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    l.setVisible(true);
  }

}

