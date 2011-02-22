package fr.esrf.tangoatk.widget.attribute;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import fr.esrf.tangoatk.widget.util.*;
import fr.esrf.tangoatk.widget.util.jdraw.JDrawable;
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
       implements INumberScalarListener, IStringScalarListener, PropertyChangeListener, IErrorListener, JDrawable {

  INumberScalar numberModel = null;
  IStringScalar stringModel = null;
  boolean alarmEnabled = true;
  String userFormat = "";
  ATKFormat atkUserFormat = null;
  String format = "";
  String error = "-----";
  boolean unitVisible = true;
  Color backgroundColor;

  static String[] exts = {"unitVisible","userFormat","alarmEnabled","validBackground","invalidText"};

  /**
   * Contructs a SimpleScalar viewer. Display a scalar atribute and its unit.
   */
  public SimpleScalarViewer() {
    backgroundColor = ATKConstant.getColor4Quality(IAttribute.VALID);
    setOpaque(true);
  }

  /** Returns the current background color of this viewer. Color used for the VALID attribute quality state */
  public Color getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * Sets the 'VALID' background color of this viewer.
   * Color used for the VALID attribute quality state.
   * @param bg Background color.
   * @see #setAlarmEnabled
   */
  public void setBackgroundColor(Color bg) {
    backgroundColor = bg;
  }

  // ------------------------------------------------------
  // Implementation of JDrawable interface
  // ------------------------------------------------------
  public void initForEditing() {
    setText("000.00 unit");
  }

  public JComponent getComponent() {
    return this;
  }
  
  public String getDescription(String name) {

    if(name.equals("unitVisible")) {
      return "Display the unit of the tango attribute when enabled.\n"+
             "Possible values are: true, false.";
    } else if (name.equalsIgnoreCase("userFormat")) {
      return "Overrides the tango attribute format property when specified.\n"+
             "Supports C format, ex:%5.2f .";
    } else if (name.equalsIgnoreCase("alarmEnabled")) {
      return "When enabled, the background color change with the\n"+
             "Tango attribute quality factor.\n" +
             "Default colors are: ( unless they have been changed with\n"+
             "ATKConstant.setColor4Quality() )\n" +
             " VALID   => Green\n" +
             " INVALID => Grey\n" +
             " ALARM   => Orange\n" +
             " WARNING => Orange\n" +
             " CHANGING => Blue\n" +
             " UNKNOWN => Grey\n" +
             "Possible values are: true, false.";
    } else if (name.equalsIgnoreCase("invalidText")) {
      return "Text displayed when the qulaity factor is INVALID\n" +
             "or when the connection is lost.";
    } else if (name.equalsIgnoreCase("validBackground")) {
      return "Sets the background color (r,g,b) for the VALID quality factor for this viewer.\n" +
             "Has effect only if alarmEnabled is true.";
    }

    return "";
  }

  public String[] getExtensionList() {
    return exts;
  }

  public boolean setExtendedParam(String name,String value,boolean popupErr) {

    if(name.equalsIgnoreCase("unitVisible")) {

      if(value.equalsIgnoreCase("true")) {
        setUnitVisible(true);
        setText("000.00 unit");
        return true;
      } else if (value.equalsIgnoreCase("false")) {
        setUnitVisible(false);
        setText("000.00");
        return true;
      } else {
        showJdrawError(popupErr,"unitVisible","Wrong syntax: 'true' or 'false' expected.");
        return false;
      }

    } else if (name.equalsIgnoreCase("userFormat")) {

      setUserFormat(value);
      return true;

    } else if (name.equalsIgnoreCase("alarmEnabled")) {

      if(value.equalsIgnoreCase("true")) {
        setAlarmEnabled(true);
        return true;
      } else if (value.equalsIgnoreCase("false")) {
        setAlarmEnabled(false);
        return true;
      } else {
        showJdrawError(popupErr,"alarmEnabled","Wrong syntax: 'true' or 'false' expected.");
        return false;
      }

    } else if (name.equalsIgnoreCase("invalidText")) {

      setInvalidText(value);
      return true;

    } else if (name.equalsIgnoreCase("validBackground")) {

      String[] c = value.split(",");
      if (c.length != 3) {
        showJdrawError(popupErr,"validBackground","Integer list expected: r,g,b");
        return false;
      }

      try {
        int r = Integer.parseInt(c[0]);
        int g = Integer.parseInt(c[1]);
        int b = Integer.parseInt(c[2]);
        if(r<0 || r>255 || g<0 || g>255 || b<0 || b>255) {
          showJdrawError(popupErr,"validBackground", "Parameter out of bounds. [0..255]");
          return false;
        }
        setBackgroundColor(new Color(r, g, b));
        return true;

      } catch (NumberFormatException e) {
        showJdrawError(popupErr,"validBackground", "Wrong integer syntax.");
        return false;
      }

    }

    return false;

  }

  public String getExtendedParam(String name) {

    if(name.equals("unitVisible")) {
      return (getUnitVisible())?"true":"false";
    } else if (name.equalsIgnoreCase("userFormat")) {
      return getUserFormat();
    } else if(name.equals("alarmEnabled")) {
      return (isAlarmEnabled())?"true":"false";
    } else if(name.equals("invalidText")) {
      return getInvalidText();
    } else if(name.equalsIgnoreCase("validBackground")) {
      Color c = backgroundColor;
      return c.getRed() + "," + c.getGreen() + "," + c.getBlue();
    }
    return "";

  }

  private void showJdrawError(boolean popup,String paramName,String message) {
    if(popup)
      JOptionPane.showMessageDialog(null, "SimpleScalarViewer: "+paramName+" incorrect.\n" + message,
                                    "Error",JOptionPane.ERROR_MESSAGE);
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

    if( atkUserFormat!=null )
      return atkUserFormat.format(evt.getValue());
    else
      return evt.getValue();

  }

  private String getDisplayString(NumberScalarEvent evt) {

    Double attDouble = new Double(evt.getValue());
    String dispStr;

    if (atkUserFormat != null) {
      dispStr = atkUserFormat.format(new Double(evt.getValue()));
    } else {
      try {
        if (userFormat.length() > 0) {
          Object[] o = {attDouble};
          dispStr = Format.sprintf(userFormat, o);
        } else if (format.indexOf('%') == -1) {
          dispStr = AttrManip.format(format, evt.getValue());
        } else {
          Object[] o = {attDouble};
          dispStr = Format.sprintf(format, o);
        }
      } catch (Exception e) {
        return "Exception while formating";
      }
    }

    return dispStr;
  }

  /**
   * Overrides the format property of the attribute.
   * @param format C like Format (ex: %5.2f) , null or "" to disable.
   */
  public void setUserFormat(String format) {
    if(format==null)
      userFormat = "";
    else
      userFormat = format;
  }

  /**
   * Sets the ATK user format of this viewer.
   * It allows more specific formating than String format.
   * <pre>
   * Ex of use:
   *   time_format = new ATKFormat() {
   *     public String format(Number n) {
   *       int d = n.intValue() / 60;
   *       Object[] o = {new Integer(d / 60), new Integer(d % 60)};
   *       return Format.sprintf("%02dh %02dmn", o);
   *     }
   *   };
   *   myViewer.setUserFormat(time_format);
   * </pre>
   * @param format ATKFormat object or null to disable.
   */
   public void setUserFormat(ATKFormat format) {
    atkUserFormat = format;
  }

  /**
   * Returns the user format.
   * @return User format
   * @see #setUserFormat
   */
  public String getUserFormat() {
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
   * Enables or disables alarm background (represents the attribute quality factor).
   * @param b True to enable alarm.
   * @see #setBackgroundColor
   */
  public void setAlarmEnabled(boolean b) {
    alarmEnabled = b;
  }

  /**
   * Determines whether the background color is overrided by the quality factor.
   * @see #setAlarmEnabled
   * @see #setBackgroundColor
   */
  public boolean isAlarmEnabled() {
    return alarmEnabled;
  }

  public void stateChange(AttributeStateEvent evt)
  {

    String state = evt.getState();

    if(state.equals(IAttribute.INVALID))
      setText(error);

    if (!alarmEnabled) return;

    if (state.equals(IAttribute.VALID)) {
      setBackground(backgroundColor);
      return;
    }

    setBackground(ATKConstant.getColor4Quality(state));

  }

  public void errorChange(ErrorEvent evt) {

    setText(error);
    if (!alarmEnabled) return;
    setBackground(ATKConstant.getColor4Quality(IAttribute.UNKNOWN));

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
    model.refresh();
    JFrame f = new JFrame();
    f.setContentPane(snv);
    f.pack();
    f.show();
    attributeList.startRefresher();

  } // end of main ()

}
