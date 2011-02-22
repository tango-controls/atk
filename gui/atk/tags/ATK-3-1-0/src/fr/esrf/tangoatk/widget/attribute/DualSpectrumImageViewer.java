/*
 * DualSpectrumImageViewer.java
 */

package fr.esrf.tangoatk.widget.attribute;

import javax.swing.*;
import java.util.*;

import fr.esrf.tangoatk.widget.util.chart.*;
import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.INumberImage;

/**
 * A class to display a scalar spectra attribute according to
 * an other spectra attribute. The 2 spectrum must be stored
 * in a Image. The Image object must have a height equals to 2.
 * The first line of the image is displayed on the X axis and
 * the second on the Y axis. For displaying time label, timestamps
 * are in millisec since epoch.
 *
 * @author  E.S.R.F
 */

public class DualSpectrumImageViewer extends JLChart implements fr.esrf.tangoatk.core.IImageListener, IJLChartActionListener , IJLChartListener {

  INumberImage model;
  JLDataView dvy;
  SimplePropertyFrame pf = null;

  /**
   * Create a new DualSpectrumImageViewer
   */
  public DualSpectrumImageViewer() {

    // Create the graph
    super();

    setBorder(new javax.swing.border.EtchedBorder());
    setBackground(new java.awt.Color(180, 180, 180));
    getY1Axis().setAutoScale(true);
    getXAxis().setAutoScale(true);
    getXAxis().setAnnotation(JLAxis.VALUE_ANNO);

    dvy = new JLDataView();
    getY1Axis().addDataView(dvy);

    addUserAction("Attribute properties");
    addJLChartActionListener(this);
    setJLChartListener(this);

  }

  // -------------------------------------------------------------
  // JLChart action listener
  // -------------------------------------------------------------
  public void actionPerformed(JLChartActionEvent evt) {
    if (evt.getName().equals("Attribute properties")) {
      if (pf == null) {
        pf = new SimplePropertyFrame();
        pf.setModel(model);
      }
      pf.setVisible(true);
    }
  }

  public boolean getActionState(JLChartActionEvent evt) {
    return (model != null);
  }

  public String[] clickOnChart(JLChartEvent e) {
    String[] ret = new String[3];
    ret[0] = model.getName();
    ret[1] = "X=" + e.getTransformedXValue();
    ret[2] = "Y=" + e.getTransformedYValue() + " " + model.getUnit();
    return ret;
  }

  public void errorChange(fr.esrf.tangoatk.core.ErrorEvent errorEvent) {
  }

  public void stateChange(fr.esrf.tangoatk.core.AttributeStateEvent evt) {
  }

  public void imageChange(fr.esrf.tangoatk.core.NumberImageEvent evt) {

    double[][] values = evt.getValue();
    if (values.length == 2) {
      int length = values[0].length;
      dvy.reset();
      for (int i = 0; i < length; i++)
        dvy.add(values[0][i], values[1][i]);

      // Commit change
      repaint();
    }

  }

  /**<code>getYView</code> Return a handle to the y view.
   * @return Return a handle to the y view
   */
  public JLDataView getYView() {
    return dvy;
  }

  /**<code>setModel</code> Set the model.
   * @param v  Value to assign to model. This image must have a height equals to 2.
   */
  public void setModel(INumberImage v) {

    if (model != null) {
      model.removeImageListener(this);
      if (pf != null) pf.setModel(null);
    }

    model = v;

    if (model != null) {
      dvy.setUnit(v.getUnit());
      dvy.setName(v.getName());
      model.addImageListener(this);
      if (pf != null) pf.setModel(model);
    }
  }

  /**
   * Apply configuration
   * @param cfg String containing configuration
   * @return error string when failure or an empty string when succesfull
   */

  public String setSettings(String cfg) {

    CfFileReader f = new CfFileReader();
    Vector p;

    if (!f.parseText(cfg)) {
      return "DualSpectrumImageViewer.setSettings: Failed to parse given config";
    }

    applyConfiguration(f);

    // Axis
    getXAxis().applyConfiguration("x", f);
    getY1Axis().applyConfiguration("y1", f);

    // Dataview options
    dvy.applyConfiguration("dvy", f);

    return "";
  }

  /**
   * Return configuration
   * @return current chart configuration as string
   */
  public String getSettings() {

    String to_write = "";

    // General settings
    to_write += getConfiguration();

    // xAxis
    to_write += getXAxis().getConfiguration("x");
    to_write += getY1Axis().getConfiguration("y1");

    // Dataview
    to_write += dvy.getConfiguration("dvy");

    return to_write;
  }

  // Instantiate the DualSpectrumViewer

  public static void main(String args[]) {

    final JFrame f = new JFrame();
    final DualSpectrumImageViewer d = new DualSpectrumImageViewer();

    if (args.length != 1) {
      JOptionPane.showMessageDialog(null, "Invalid parameters:\nUsage: DualSpectrumImageViewer full_att_name (where full_att_name is a 2 lines image attribue)", "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(0);
    }

    try {

      fr.esrf.tangoatk.core.AttributeList attributeList =
        new fr.esrf.tangoatk.core.AttributeList();
      INumberImage theAtt = (INumberImage) attributeList.add(args[0]);
      d.setModel(theAtt);

      f.getContentPane().setLayout(new java.awt.GridLayout(1, 1));
      f.getContentPane().add(d);
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setTitle("DualSpectrumViewer:" + args[0]);
      f.setSize(640, 480);
      f.setVisible(true);

      attributeList.setRefreshInterval(3000);
      attributeList.startRefresher();


    } catch (Exception e) {

      e.printStackTrace();

    }

  }


  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables

}
