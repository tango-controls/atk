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

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import fr.esrf.tangoatk.widget.util.*;
import fr.esrf.tangoatk.widget.util.jdraw.JDrawable;
import fr.esrf.tangoatk.core.*;

/** A class which displays a number scalar attribute.
 * Here is an example of use:
 * <p>
 * <pre>
 * fr.esrf.tangoatk.core.AttributeList attributeList = new
 *    fr.esrf.tangoatk.core.AttributeList();
 * DigitalNumberScalarViewer dnsv = new DigitalNumberScalarViewer();
 * INumberScalar model = (INumberScalar) attributeList.add("jlp/test/1/att_quatre");
 * dnsv.setModel(model);
 * attributeList.startRefresher();
 * </pre>
 */

public class DigitalNumberScalarViewer extends DigitalNumberViewer
       implements INumberScalarListener, PropertyChangeListener, IErrorListener, JDrawable {

  INumberScalar model = null;
  boolean alarmEnabled = true;
  Color backgroundColor;
  String userFormat = null;

  private boolean          hasToolTip=false;
  private boolean          qualityInTooltip=false;

  static String[] exts = {"userFormat","alarmEnabled","validBackground","fontSize"};

  /**
   * Contructs a SimpleScalar viewer. Display a scalar atribute and its unit.
   */
  public DigitalNumberScalarViewer() {
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

  /**
   * Overrides the format property of the attribute.
   * @param format C like Format (ex: %5.2f) , null or "" to disable.
   */
  public void setUserFormat(String format) {

    if(format==null || format.equals("")) {
      userFormat = null;
    } else {
      userFormat = format;
      setFormat(userFormat);
    }

  }

  /**
   * Returns the user format.
   * @return User format
   * @see #setUserFormat
   */
  public String getUserFormat() {

    return userFormat;

  }

  // ------------------------------------------------------
  // Implementation of JDrawable interface
  // ------------------------------------------------------
  public void initForEditing() {
  }

  public JComponent getComponent() {
    return this;
  }

  public String getDescription(String name) {

    if (name.equalsIgnoreCase("userFormat")) {
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
    } else if (name.equalsIgnoreCase("validBackground")) {
      return "Sets the background color (r,g,b) for the VALID quality factor for this viewer.\n" +
             "Has effect only if alarmEnabled is true.";
    } else if (name.equalsIgnoreCase("fontSize")) {
      return "Sets the digit size.";
    }

    return "";
  }

  public String[] getExtensionList() {
    return exts;
  }

  public boolean setExtendedParam(String name,String value,boolean popupErr) {

    if (name.equalsIgnoreCase("userFormat")) {

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

    } else if (name.equalsIgnoreCase("fontSize")) {

      try {
        int s = Integer.parseInt(value);
        if(s<=10) {
          showJdrawError(popupErr,"fontSize", "Parameter must be greater than 10");
          return false;
        }
        setFontSize(s);
        return true;

      } catch (NumberFormatException e) {
        showJdrawError(popupErr,"fontSize", "Wrong integer syntax.");
        return false;
      }

    }

    return false;

  }

  public String getExtendedParam(String name) {

    if (name.equalsIgnoreCase("userFormat")) {
      if(userFormat==null) {
        return "";
      } else {
        return userFormat;
      }
    } else if(name.equals("alarmEnabled")) {
      return (isAlarmEnabled())?"true":"false";
    } else if(name.equalsIgnoreCase("validBackground")) {
      Color c = backgroundColor;
      return c.getRed() + "," + c.getGreen() + "," + c.getBlue();
    } else if(name.equalsIgnoreCase("fontSize")) {
      return Integer.toString(getFontSize());
    }
    return "";

  }

  private void showJdrawError(boolean popup,String paramName,String message) {
    if(popup)
      JOptionPane.showMessageDialog(null, "DigitalNumberScalarViewer: "+paramName+" incorrect.\n" + message,
                                    "Error",JOptionPane.ERROR_MESSAGE);
  }

  // -------------------------------------------------------------
  // Number scalar listener
  // -------------------------------------------------------------
  public void numberScalarChange(NumberScalarEvent evt) {

    setValue(evt.getValue());
    
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

    if (hasToolTip)
    {
       if (qualityInTooltip)
       { //set a ToolTip attributename + quality
	  IAttribute attSource = (IAttribute)evt.getSource();
	  setToolTipText(attSource.getName() + " : " + state);
       }
    }

    if(state.equals(IAttribute.INVALID))
      setValue(Double.NaN);

    if (!alarmEnabled) return;

    if (state.equals(IAttribute.VALID)) {
      setBackground(backgroundColor);
      repaint();
      return;
    }

    setBackground(ATKConstant.getColor4Quality(state));

  }

  public void errorChange(ErrorEvent evt) {

    setValue(Double.NaN);
    if (!alarmEnabled) return;
    setBackground(ATKConstant.getColor4Quality(IAttribute.UNKNOWN));

  }

  public void propertyChange(PropertyChangeEvent evt) {

    Property src = (Property) evt.getSource();

    if (model != null) {
      if (src.getName().equalsIgnoreCase("format")) {
        if(userFormat==null)
          setFormat(src.getValue().toString());
      }
      model.refresh();
    }

  }

  /**
   * Sets the model for this viewer.
   * @param scalar scalar model
   */
  public void setModel(INumberScalar scalar) {

    clearModel();

    if (scalar != null) {
      if(userFormat==null) setFormat(scalar.getProperty("format").getPresentation());
      model = scalar;
      model.addNumberScalarListener(this);
      model.getProperty("format").addPresentationListener(this);
      if (hasToolTip)
    	  setToolTipText(scalar.getName());
      model.refresh();
    }

  }

  /**
   * Clears all model and listener attached to the components
   */
  public void clearModel() {

    if (hasToolTip) setToolTipText(null);

    if (model != null) {
      model.removeNumberScalarListener(this);
      model.getProperty("format").removePresentationListener(this);
      model = null;
    }

  }

  public INumberScalar getModel()
  {
      return model;
  }

  /**
   * <code>getHasToolTip</code> returns true if the viewer has a tooltip (attribute full name)
   *
   * @return a <code>boolean</code> value
   */
  public boolean getHasToolTip() {
    return hasToolTip;
  }

  /**
   * <code>setHasToolTip</code> display or not a tooltip for this viewer
   *
   * @param b If True the attribute full name will be displayed as tooltip for the viewer
   */
  public void setHasToolTip(boolean b)
  {

    if (hasToolTip == b) return;

    hasToolTip = b;

    if (!hasToolTip)
    {
       setToolTipText(null);
       return;
    }

    if (model != null)
    {
      setToolTipText(model.getName());
    }

  }


  /**
   * <code>getQualityInTooltip</code> returns true if the attribute quality factor is displayed inside the viewer's tooltip
   *
   * @return a <code>boolean</code> value
   */
  public boolean getQualityInTooltip() {
    return qualityInTooltip;
  }

  /**
   * <code>setQualityInTooltip</code> display or not the attribute quality factor inside the tooltip
   *
   * @param b If True the attribute quality factor will be displayed inside the tooltip.
   */
  public void setQualityInTooltip(boolean b)
  {
    IAttribute    attModel = null;

    if (!hasToolTip)
    {
       qualityInTooltip = b;
       return;
    }

    if (qualityInTooltip != b)
    {
       if (model != null)
	     {
         attModel = (IAttribute) model;
       }

       if (b == false)
          if (attModel != null)
              setToolTipText(attModel.getName());
       qualityInTooltip=b;
    }

  }

  /**
   * Test function
   * @param args Not used
   */
  public static void main(String[] args) {

    fr.esrf.tangoatk.core.AttributeList attributeList = new
        fr.esrf.tangoatk.core.AttributeList();
    DigitalNumberScalarViewer snv = new DigitalNumberScalarViewer();
    String attributeName = "jlp/test/1/att_un";
    IAttribute attribute = null;
    try {
        attribute = (IAttribute) attributeList.add(attributeName);
    }
    catch(Exception e) {
      ErrorPane.showErrorMessage(null,"jlp/test/1",e);
      attribute = null;
    }
    snv.setModel( (INumberScalar)attribute );
    snv.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
    snv.setBackgroundColor(java.awt.Color.WHITE);
    snv.setForeground(java.awt.Color.BLACK);
    snv.setHasToolTip(true);
    if(attribute!=null) attribute.refresh();
    JFrame f = new JFrame();
    f.setContentPane(snv);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.pack();
    f.setVisible(true);
    attributeList.startRefresher();

  } // end of main ()

}

