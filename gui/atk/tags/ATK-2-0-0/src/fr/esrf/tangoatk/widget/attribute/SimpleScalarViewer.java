package fr.esrf.tangoatk.widget.attribute;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import fr.esrf.tangoatk.widget.util.*;
import fr.esrf.tangoatk.core.*;
import com.braju.format.Format;
import fr.esrf.TangoDs.AttrManip;

/** A light weigth viewer which display a scalar attribute (String or Number) and its unit.
 * Here is an example of use:
 * <p>
 * <pre>
 * fr.esrf.tangoatk.core.AttributeList attributeList = new
 *    fr.esrf.tangoatk.core.AttributeList();
 * SimpleScalarViewer snv = new SimpleScalarViewer();
 * INumberScalar model = (INumberScalar) attributeList.add("jlp/test/1/att_quatre");
 * snv.setModel(model);
 * attributeList.startRefresher();
 * </pre>
 */

public class SimpleScalarViewer extends JAutoScrolledText
       implements INumberScalarListener, IStringScalarListener, PropertyChangeListener, IErrorListener {

  INumberScalar numberModel = null;
  IStringScalar stringModel = null;
  boolean alarmEnabled = true;
  ATKFormat userFormat;
  String format = "";
  String error = "-----";
  boolean unitVisible = true;
  Color backgroundColor;

  /**
   * Contructs a SimpleScalar viewer. Display a scalar atribute and its unit.
   */
  public SimpleScalarViewer() {
    backgroundColor = ATKConstant.getColor4Quality("VALID");
    setOpaque(true);
  }

  /** Returns the current background color. Color used for the VALID attribute quality state */
  public Color getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * Sets the background color of this viewer. Color used for the VALID attribute quality state.
   * @param bg Background color.
   */
  public void setBackgroundColor(Color bg) {
    backgroundColor = bg;
  }

  // -------------------------------------------------------------
  // String scalar listener
  // -------------------------------------------------------------
  public void stringScalarChange(StringScalarEvent evt) {

    String val;

    val = getDisplayString(evt);

    String oldVal=getText();
    if(!val.equals(oldVal)) setText(val);

  }

  // -------------------------------------------------------------
  // Number scalar listener
  // -------------------------------------------------------------
  public void numberScalarChange(NumberScalarEvent evt) {

    String val;

    val = getDisplayString(evt);

    if (unitVisible) {
      setText(val + " " + numberModel.getUnit());
    } else {
      setText(val);
    }

  }


  private String getDisplayString(StringScalarEvent evt) {

    return evt.getValue();

  }

  private String getDisplayString(NumberScalarEvent evt) {

    Double attDouble = new Double(evt.getValue());
    String dispStr;

    try {
      if (userFormat != null) {
        dispStr = userFormat.format(attDouble);
      } else if (format.indexOf('%') == -1) {
        dispStr = AttrManip.format(format, evt.getValue());
      } else {
        Object[] o = {attDouble};
        dispStr = Format.sprintf(format, o);
      } // end of else
    } catch (Exception e) {
      return "Exception while formating";
    }

    return dispStr;
  }

  /**
   * Overides the format property of the attribute.
   * @param format Format
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
   * @param b True to enable alarm color.
   */
  public void setAlarmEnabled(boolean b) {
    alarmEnabled = b;
  }

  public void stateChange(AttributeStateEvent evt)
  {

    String state = evt.getState();

    if(state.equals("INVALID"))
      setText(error);

    if (!alarmEnabled) return;

    if (state.equals("VALID")) {
      setBackground(backgroundColor);
      return;
    }

    setBackground(ATKConstant.getColor4Quality(state));

  }

  public void errorChange(ErrorEvent evt) {

    setText(error);
    if (!alarmEnabled) return;
    setBackground(ATKConstant.getColor4Quality("UNKNOWN"));

  }

  public void propertyChange(PropertyChangeEvent evt) {

    Property src = (Property) evt.getSource();

    if (numberModel != null) {
      if (src.getName().equalsIgnoreCase("format")) {
        format = src.getValue().toString();
      }
      numberModel.refresh();
    }

    if (stringModel != null) {
      if (src.getName().equalsIgnoreCase("format")) {
        format = src.getValue().toString();
      }
      stringModel.refresh();
    }

  }

  /**
   * Sets the model for this viewer.
   * @param scalar scalar model
   */
  public void setModel(INumberScalar scalar) {

    clearModel();

    if (scalar != null) {
      format = scalar.getProperty("format").getPresentation();
      numberModel = scalar;
      numberModel.addNumberScalarListener(this);
      numberModel.getProperty("format").addPresentationListener(this);
      numberModel.getProperty("unit").addPresentationListener(this);
    }

  }

  /**
   * Sets the model for this viewer.
   * @param scalar model
   */
  public void setModel(IStringScalar scalar) {

    clearModel();

    if (scalar != null) {
      format = scalar.getProperty("format").getPresentation();
      stringModel = scalar;
      stringModel.addStringScalarListener(this);
      stringModel.getProperty("format").addPresentationListener(this);
    }

  }

  /**
   * Clears all model and listener attached to the components
   */
  public void clearModel() {

    if (stringModel != null) {
      stringModel.removeStringScalarListener(this);
      stringModel.getProperty("format").removePresentationListener(this);
      stringModel = null;
    }

    if (numberModel != null) {
      numberModel.removeNumberScalarListener(this);
      numberModel.getProperty("format").removePresentationListener(this);
      numberModel.getProperty("unit").removePresentationListener(this);
      numberModel = null;
    }

  }

  /**
   * Set the text which will be displayed in case of error or INVALID quality.
   * @param s Text to be displayed.
   */
  public void setInvalidText(String s) {
    error = s;
  }

  /**
   * Returns the current text which is displayed in case of error.
   * @see #setInvalidText
   */
  public String getInvalidText() {
    return error;
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
    INumberScalar model = (INumberScalar) attributeList.add("jlp/test/1/att_quatre");
    snv.setModel(model);
    snv.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
    snv.setBackgroundColor(java.awt.Color.WHITE);
    snv.setForeground(java.awt.Color.BLACK);
    snv.setFont(new java.awt.Font("Dialog", Font.BOLD, 30));
    snv.setValueOffsets(0,-5);
    model.refresh();
    JFrame f = new JFrame();
    f.setContentPane(snv);
    f.pack();
    f.show();
    attributeList.startRefresher();

  } // end of main ()

}
