// File:          SimpleScalarViewer.java
// Created:       2002-06-27 13:02:31, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-01 17:37:9, assum>
//
// $Id$
//
// Description:
package fr.esrf.tangoatk.widget.attribute;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import fr.esrf.tangoatk.widget.util.*;
import fr.esrf.tangoatk.core.*;
import com.braju.format.Format;
import fr.esrf.TangoDs.AttrManip;

/** A light weigth viewer which display a scalar attribute and its unit */
public class SimpleScalarViewer
    extends JAutoScrolledText
    implements INumberScalarListener,
    IStringScalarListener,
    IErrorListener {

  INumberScalar numberModel = null;
  IStringScalar stringModel = null;
  boolean alarmEnabled = true;
  ATKFormat userFormat;
  String format = "";
  String error = "-------";
  boolean unitVisible = true;

  public SimpleScalarViewer() {
    setOpaque(true);
  }

  public void stringScalarChange(StringScalarEvent evt) {
    String val;
    String s = evt.getValue();
    if (userFormat != null) {
      val = userFormat.format(s);
    } else {
      Object[] o = {s};
      val = Format.sprintf(format, o);
    } // end of else

    if (unitVisible) {
      setText(val + " " + numberModel.getUnit());
    } else {
      setText(val);
    }
  }

  public void numberScalarChange(NumberScalarEvent evt) {
    Double d = new Double(evt.getValue());
    String val;

    if (userFormat != null) {
      val = userFormat.format(d);
    } else if (format.indexOf('%') == -1) {
      val = AttrManip.format(format, evt.getValue());
    } else {
      Object[] o = {d};
      val = Format.sprintf(format, o);
    } // end of else

    if (unitVisible) {
      setText(val + " " + numberModel.getUnit());
    } else {
      setText(val);
    }

  }

 /**
   * Overides the format property of the attribute.
   * @param format
   */
  public void setUserFormat(ATKFormat format) {
    userFormat = format;
  }

 /**
   * Returns user format properties.
   * @return User format
   */
  public ATKFormat getUserFormat() {
    return userFormat;
  }

  /**
   * Displays or hides the unit.
   * @param b true to display the unit, false otherwise
   */
  public void setUnitVisible(boolean b) {
    unitVisible = b;
  }

  /**
   * Detemines wether the unit is visible
   * @return true if unit is visible
   */
  public boolean getUnitVisible() {
    return unitVisible;
  }

  /**
   * Enables or disables alarm background (shows quality factor of the attribute).
   * @param b
   */
  public void setAlarmEnabled(boolean b) {
    alarmEnabled = b;
  }

  public void stateChange(AttributeStateEvent evt) {
    String state = evt.getState();
    if (!alarmEnabled) return;
    setBackground(AttributeStateViewer.getColor4State(state));
  }

  public void errorChange(ErrorEvent evt) {
    setText(error);
    if (!alarmEnabled) return;
    setBackground(AttributeStateViewer.getColor4State("UNKNOWN"));
  }

  /**
   * Sets the model for this viewer.
   * @param Number scalar model
   */
  public void setModel(INumberScalar scalar) {
    if (numberModel != null)
      numberModel.removeNumberScalarListener(this);
    format = scalar.getProperty("format").getPresentation();
    numberModel = scalar;
    numberModel.addNumberScalarListener(this);
  }

  /**
   * Sets the model for this viewer.
   * @param String model
   */
  public void setModel(IStringScalar scalar) {
    if (stringModel != null)
      stringModel.removeStringScalarListener(this);

    format = scalar.getProperty("format").getPresentation();
    stringModel = scalar;
    stringModel.addStringScalarListener(this);
  }

  /**
   * Test function
   * @param args Not used
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    fr.esrf.tangoatk.core.AttributeList attributeList = new
        fr.esrf.tangoatk.core.AttributeList();
    SimpleScalarViewer snv = new SimpleScalarViewer();
    snv.setModel((INumberScalar) attributeList.add("jlp/test/1/att_un"));
    snv.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
    snv.setBackground(java.awt.Color.blue);
    snv.setForeground(java.awt.Color.yellow);
    snv.setFont(new java.awt.Font("Dialog", 0, 12));
    JFrame f = new JFrame();
    attributeList.startRefresher();
    f.setContentPane(snv);
    f.pack();
    f.show();

  } // end of main ()

}
