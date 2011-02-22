/*
 * NumberSpectrumViewer.java
 *
 * Author:JL Pons 2003
 */

package fr.esrf.tangoatk.widget.attribute;

import javax.swing.*;
import java.util.*;

import fr.esrf.tangoatk.widget.util.chart.*;
import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.INumberSpectrum;

/**
 * A class to display a scalar spectrum attribute . The NumberSpectrumViewer
 * displays the spectrum according to index value (x axis shows value index).
 *
 * @author  E.S.R.F
 */
public class NumberSpectrumViewer extends JLChart implements fr.esrf.tangoatk.core.ISpectrumListener, IJLChartActionListener, IJLChartListener {

  // Configuration dialog
  class OptionDlg extends JDialog {

    private JLabel     xUnitLabel;
    private JTextField xUnitText;

    public OptionDlg() {
    }

  }

  INumberSpectrum model = null;
  JLDataView dvy;
  SimplePropertyFrame pf = null;
  double A0=0.0;
  double A1=1.0;
  String xUnit="";

  /** Creates new fNumberSpectrumViewer */
  public NumberSpectrumViewer() {

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

  /**
   * Sets an affine tranform to the X axis. This allows to transform
   * spectra index displayed on X axis.
   * @param a0
   * @param a1
   */
  public void setXAxisAffineTransform(double a0,double a1) {
    A0 = a0;
    A1 = a1;
  }

  /**
   * Sets the x Axis unit.
   * @param u
   */
  public void setXAxisUnit(String u) {
    xUnit = u;
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

  // -------------------------------------------------------------
  // Spectrum listener
  // -------------------------------------------------------------

  public void errorChange(fr.esrf.tangoatk.core.ErrorEvent errorEvent) {
    // Clear the graph
    dvy.reset();
    repaint();
  }

  public void stateChange(fr.esrf.tangoatk.core.AttributeStateEvent evt) {
  }

  public void spectrumChange(fr.esrf.tangoatk.core.NumberSpectrumEvent numberSpectrumEvent) {

    double[] value = numberSpectrumEvent.getValue();
    int length = value.length;
    dvy.reset();

    for (int i = 0; i < length; i++) {
      dvy.add(A0 + A1 * (double) i, value[i]);
    }

    // Commit change
    repaint();
  }

  public String[] clickOnChart(JLChartEvent e) {
    String[] ret = new String[3];
    ret[0] = model.getName();

    if( xUnit.length()>0 )
      ret[1] = "X=" + e.getTransformedXValue() + " " + xUnit;
    else
      ret[1] = "X=" + e.getTransformedXValue();

    ret[2] = "Y=" + e.getTransformedYValue() + " " + model.getUnit();
    return ret;
  }

  /**<code>setModel</code> Set the model.
   * @param v  Value to assign to model.
   */
  public void setModel(INumberSpectrum v) {

    if (model != null) {
      model.removeSpectrumListener(this);
      if (pf != null) pf.setModel(null);
    }

    this.model = v;

    if (v != null) {
      dvy.setUnit(v.getUnit());
      dvy.setName(v.getName());
      model.addSpectrumListener(this);
      if (pf != null) pf.setModel(model);
    }

  }

  /**
   * Apply configuration.
   * @param cfg String containing configuration
   * @return error string when failure or an empty string when succesfull
   */

  public String setSettings(String cfg) {

    CfFileReader f = new CfFileReader();
    Vector p;

    if (!f.parseText(cfg)) {
      return "NumberSpectrumViewer.setSettings: Failed to parse given config";
    }

    // General settings
    applyConfiguration(f);


    // Local settings
    p = f.getParam("xaxis_transform");
    if (p != null)
      setXAxisAffineTransform(OFormat.getDouble(p.get(0).toString()),
                              OFormat.getDouble(p.get(1).toString()));
    p = f.getParam("xaxis_unit");
    if (p != null) xUnit = p.get(0).toString();

    // Axis
    getXAxis().applyConfiguration("x",f);
    getY1Axis().applyConfiguration("y1", f);

    // Dataview options
    dvy.applyConfiguration("dvy",f);

    return "";
  }

  /**
   * Return configuration.
   * @return current chart configuration as string
   */
  public String getSettings() {

    String to_write = "";

    // General settings
    to_write += getConfiguration();

    // Local settings
    to_write += "xaxis_transform:" + A0 + "," + A1 + "\n";
    to_write += "xaxis_unit:\'" + xUnit + "\'\n";

    // xAxis
    to_write += getXAxis().getConfiguration("x");
    to_write += getY1Axis().getConfiguration("y1");

    // Dataview
    to_write += dvy.getConfiguration("dvy");

    return to_write;
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables

  public static void main(String[] args) {

    try {
      fr.esrf.tangoatk.core.AttributeList attributeList = new
        fr.esrf.tangoatk.core.AttributeList();

      NumberSpectrumViewer nsv = new NumberSpectrumViewer();
      nsv.setModel((INumberSpectrum) attributeList.add("jlp/test/1/att_spectrum"));
      nsv.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
      nsv.setFont(new java.awt.Font("Dialog", 0, 12));
      nsv.setXAxisAffineTransform(0.0,1000);
      nsv.setXAxisUnit("mA");
      JFrame f = new JFrame();
      attributeList.startRefresher();
      f.setContentPane(nsv);
      f.setSize(640,480);
      f.show();
    } catch (Exception e) {
      e.printStackTrace();
    }

  } // end of main ()


}
