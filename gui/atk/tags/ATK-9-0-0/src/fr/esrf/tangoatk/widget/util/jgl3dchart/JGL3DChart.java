/*
 *  Copyright (C) :     2002,2003,2004,2005,2006,2007,2008,2009
 *                      European Synchrotron Radiation Facility
 *                      BP 220, Grenoble 38043
 *                      FRANCE
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
package fr.esrf.tangoatk.widget.util.jgl3dchart;

import fr.esrf.tangoatk.widget.util.Gradient;

import fr.esrf.tangoatk.widget.util.JGradientViewer;
//import fr.esrf.TangoApi.DeviceProxy;
//import fr.esrf.TangoApi.DeviceAttribute;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * A class to handle 3D surface plot
 */
public class JGL3DChart extends JPanel implements ActionListener {

  private JGL3DView       j3dView;
  private Gradient        gColor;
  JGradientViewer         gradientViewer;

  private JButton resetRotBtn;
  private JButton rotationYXBtn;
  private JButton rotationYZBtn;
  private JButton rotationXZBtn;
  private JButton autoScaleCameraBtn;
  private JButton zoomBackBtn;
  private JButton settingsBtn;

  private SettingsFrame settingsFrame;

  public JGL3DChart() {

    // JOGL view
    setLayout(new BorderLayout());
    j3dView = new JGL3DView(this);
    add(j3dView,BorderLayout.CENTER);

    // Gradient
    gColor = new Gradient();
    gColor.buildRainbowGradient();
    int[] gColormap = gColor.buildColorMap(65536);
    j3dView.setColorMap(gColormap);
    gradientViewer = new JGradientViewer();
    gradientViewer.setGradient(gColor);
    add(gradientViewer, BorderLayout.EAST);

    // Toolbar
    JToolBar toolbarPanel = new JToolBar();
    toolbarPanel.setOrientation(JToolBar.HORIZONTAL);

    resetRotBtn = Utils.createIconButton("jgl3dchart_resetrot",false,"Reset rotation",this);
    toolbarPanel.add(resetRotBtn);
    rotationYXBtn = Utils.createIconButton("jgl3dchart_rotYX",false,"Align to YX plane",this);
    toolbarPanel.add(rotationYXBtn);
    rotationYZBtn = Utils.createIconButton("jgl3dchart_rotYZ",false,"Align to YZ plane",this);
    toolbarPanel.add(rotationYZBtn);
    rotationXZBtn = Utils.createIconButton("jgl3dchart_rotXZ",false,"Align to XZ plane",this);
    toolbarPanel.add(rotationXZBtn);
    autoScaleCameraBtn = Utils.createIconButton("jgl3dchart_autosccam",false,"Auto scale camera",this);
    toolbarPanel.add(autoScaleCameraBtn);
    zoomBackBtn = Utils.createIconButton("jgl3dchart_zoomback",false,"Zoom back",this);
    toolbarPanel.add(zoomBackBtn);
    settingsBtn = Utils.createIconButton("jgl3dchart_settings",false,"Show settings",this);
    toolbarPanel.add(settingsBtn);
    add(toolbarPanel,BorderLayout.NORTH);

  }

  /**
   * Sets the data (The value is mapped to the y axis)
   * The fisrt coordinates data[x][] is mapped to the x axis
   * The second coordinates data[][y] is mapped the the y axis
   * All y line must have the same lenght
   * @param data Data to be displayed
   */
  public void setData(double[][] data) {
    j3dView.setData(data);
  }

  /**
   * Reset rotation to default
   */
  public void resetRotation() {
    j3dView.resetRotation();
  }

  /**
   * Align viewer to YX plane
   */
  public void rotateYX() {
    j3dView.rotateYX();
  }

  /**
   * Align viewer to YZ plane
   */
  public void rotateYZ() {
    j3dView.rotateYZ();
  }

  /**
   * Align viewer to XZ plane
   */
  public void rotateXZ() {
    j3dView.rotateXZ();
  }

  /**
   * Free rotate the viewer
   * @param ox Angle around X axis
   * @param oy Angle around Y aixs
   */
  public void rotate(double ox,double oy) {
    j3dView.rotate(ox,oy);
  }

  /**
   * Autoscale camera (fit the graph to the display)
   */
  public void autoScaleCamera() {
    j3dView.autoScaleCameraRequest();
    j3dView.display();
  }

  /**
   * Sets the gradient for the YAxis data colormap
   * @param g Gradient
   */
  public void setGradient(Gradient g) {

    gColor = g;
    int[] gColorMap = g.buildColorMap(65536);
    gradientViewer.setGradient(g);
    j3dView.setColorMap(gColorMap);
    j3dView.computeScale();

  }

  /**
   * Returns the current gradient for Y axis colormap.
   */
  public Gradient getGradient() {
    return gColor;
  }

  /**
   * Returns a handle to the X axis
   */
  public JGL3DAxis getXAxis() {
    return j3dView.getXAxis();
  }

  /**
   * Returns a handle to the Y axis
   */
  public JGL3DAxis getYAxis() {
    return j3dView.getYAxis();
  }

  /**
   * Returns a handle to the Z axis
   */
  public JGL3DAxis getZAxis() {
    return j3dView.getZAxis();
  }

  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();

    if(src==resetRotBtn) {
      resetRotation();
    } else if (src==rotationYXBtn) {
      rotateYX();
    } else if (src==rotationYZBtn) {
      rotateYZ();
    } else if (src==rotationXZBtn) {
      rotateXZ();
    } else if (src==autoScaleCameraBtn) {
      autoScaleCamera();
    } else if (src==zoomBackBtn) {
      j3dView.zoomBack();
    } else if (src==settingsBtn) {
      if( settingsFrame==null )
        settingsFrame = new SettingsFrame(this);
      settingsFrame.setVisible(true);
    }

  }

  public static void main(String[] args) {

    final JGL3DChart joglChart = new JGL3DChart();

    // Build test data

    double[][] data = new double[1000][1000];
    for(int x=-500;x<500;x++) {
      for(int y=-500;y<500;y++) {
        double e1 = (x-100)*(x-100)/100.0 + (y-5)*(y-5)/100.0;
        double e2 = (x+100)*(x+100)/100.0 + (y+5)*(y+5)/100.0;
        data[x+500][y+500] = 150.0*Math.exp(-e1) - 150.0*Math.exp(-e2);
      }
    }
    joglChart.setData(data);

/*
    try {
      DeviceProxy ds = new DeviceProxy("sy/d-tm/profile");
      DeviceAttribute da = ds.read_attribute("TuneSpectra");
      double[] rawData = da.extractDoubleArray();
      int imgWidth = da.getDimX();
      int imgHeight = da.getDimY();
      double[][] data = new double[imgWidth][imgHeight];
      for(int x=0;x<imgWidth;x++) {
        for(int z=0;z<imgHeight;z++) {
          data[x][z] = rawData[x+z*imgWidth];
        }
      }
      joglChart.setData(data);
      joglChart.getXAxis().setOffsetTransform(0.5);
      joglChart.getXAxis().setGainTransform(0.0009765625);
      joglChart.getYAxis().setOffsetTransform(0.0);
      joglChart.getYAxis().setGainTransform(0.005);
      joglChart.getZAxis().setOffsetTransform(0.0);
      joglChart.getZAxis().setGainTransform(0.01);
      joglChart.rotate(Math.PI/2.0,Math.PI);

    } catch (Exception e) {
      System.exit(0);
    }
*/

    JFrame fr = new JFrame();
    fr.setTitle("JOGL Chart");
    fr.setContentPane(joglChart);
    fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    fr.pack();
    fr.setVisible(true);

  }


}
