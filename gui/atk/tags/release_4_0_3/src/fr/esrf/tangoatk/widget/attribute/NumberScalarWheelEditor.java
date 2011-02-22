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
 
/*
 * NumberScalarWheelEditor.java
 *
 */

package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.attribute.ANumber;
import fr.esrf.tangoatk.widget.util.*;
import fr.esrf.tangoatk.widget.util.jdraw.JDrawable;

import java.beans.*;
import java.awt.*;
import javax.swing.*;

/**
 * A Tango Number WheelSwitch editor.
 * @author  pons
 */
public class NumberScalarWheelEditor extends WheelSwitch
    implements INumberScalarListener, IWheelSwitchListener, PropertyChangeListener, JDrawable {

  static String[] exts = {"arrowColor","arrowSelColor"};

  private INumberScalar model;

  // General constructor
  public NumberScalarWheelEditor() {
    model = null;
    addWheelSwitchListener(this);
  }

  public INumberScalar getModel() {
    return model;
  }

  public void setModel(INumberScalar m) {

    // Remove old registered listener
    if (model != null) {
      model.removeNumberScalarListener(this);
      model.getProperty("format").removePresentationListener(this);
      model = null;
    }

    if( m==null ) return;

    if (!m.isWritable())
      throw new IllegalArgumentException("NumberScalarWheelEditor: Only accept writeable attribute.");


    model = m;

    // Register new listener
    model.addNumberScalarListener(this);
    model.getProperty("format").addPresentationListener(this);

    setFormat(model.getProperty("format").getPresentation());
    
    ANumber   an=null;
    if (model instanceof ANumber)
        an = (ANumber) model;

    // Set max and min
    double max = model.getMaxValue();
    if (an != null) max = an.getValueInDisplayUnit(max);
    if (!Double.isNaN(max)) setMaxValue(max);
    double min = model.getMinValue();
    if (an != null) min = an.getValueInDisplayUnit(min);
    if (!Double.isNaN(min)) setMinValue(min);

    model.refresh();
  }

  // ------------------------------------------------------
  // Implementation of JDrawable interface
  // ------------------------------------------------------
  public void initForEditing() {
    // Do nothing here
    // Default is ok.
  }

  public JComponent getComponent() {
    return this;
  }
  
  public String getDescription(String extName) {
    if (extName.equalsIgnoreCase("arrowColor")) {
      return "Sets the arrow button color (r,g,b).";
    } else if (extName.equalsIgnoreCase("arrowSelColor")) {
      return "Sets the color of selected arrow buttons.(r,g,b).";
    }
    return "";
  }

  public String[] getExtensionList() {
    return exts;
  }

  public boolean setExtendedParam(String name,String value,boolean popupErr) {

    if (name.equalsIgnoreCase("arrowColor")) {

      String[] c = value.split(",");
      if (c.length != 3) {
        showJdrawError(popupErr,"arrowColor","Integer list expected: r,g,b");
        return false;
      }

      try {
        int r = Integer.parseInt(c[0]);
        int g = Integer.parseInt(c[1]);
        int b = Integer.parseInt(c[2]);
        if(r<0 || r>255 || g<0 || g>255 || b<0 || b>255) {
          showJdrawError(popupErr,"arrowColor", "Parameter out of bounds. [0..255]");
          return false;
        }
        setButtonColor(new Color(r, g, b));
        return true;

      } catch (NumberFormatException e) {
        showJdrawError(popupErr,"arrowColor", "Wrong integer syntax.");
        return false;
      }

    } else if (name.equalsIgnoreCase("arrowSelColor")) {

      String[] c = value.split(",");
      if (c.length != 3) {
        showJdrawError(popupErr,"arrowSelColor","Integer list expected: r,g,b");
        return false;
      }

      try {
        int r = Integer.parseInt(c[0]);
        int g = Integer.parseInt(c[1]);
        int b = Integer.parseInt(c[2]);
        if(r<0 || r>255 || g<0 || g>255 || b<0 || b>255) {
          showJdrawError(popupErr,"arrowSelColor", "Parameter out of bounds. [0..255]");
          return false;
        }
        setSelButtonColor(new Color(r, g, b));
        return true;

      } catch (NumberFormatException e) {
        showJdrawError(popupErr,"arrowSelColor", "Wrong integer syntax.");
        return false;
      }

    }

    return false;

  }

  public String getExtendedParam(String name) {

    if(name.equalsIgnoreCase("arrowColor")) {
      Color c = getButtonColor();
      return c.getRed() + "," + c.getGreen() + "," + c.getBlue();
    } else if(name.equalsIgnoreCase("arrowSelColor")) {
      Color c = getSelButtonColor();
      return c.getRed() + "," + c.getGreen() + "," + c.getBlue();
    }
    return "";

  }

  private void showJdrawError(boolean popup,String paramName,String message) {
    if(popup)
      JOptionPane.showMessageDialog(null, "NumberScalarWheelEditor: "+paramName+" incorrect.\n" + message,
                                    "Error",JOptionPane.ERROR_MESSAGE);
  }

  // ------------------------------------------------------
  // Listen on "setpoint" change
  // this is not clean yet as there is no setpointChangeListener
  // Listen on valueChange and readSetpoint
  // ------------------------------------------------------
  public void numberScalarChange(NumberScalarEvent evt)
  {
      double set = Double.NaN;

      if(hasFocus())
	  set = model.getNumberScalarSetPointFromDevice();
      else
	  set = model.getNumberScalarSetPoint();

      if (getValue() != set) setValue(set);
  }

  public void errorChange(ErrorEvent e) {
    setValue(Double.NaN);
  }

  public void stateChange(AttributeStateEvent e) {
  }

  // Listen change on the WheelSwitch
  public void valueChange(WheelSwitchEvent e) {
    if (model != null) model.setValue(e.getValue());
  }

  public void propertyChange(PropertyChangeEvent evt) {

    Property src = (Property) evt.getSource();
    if (model != null) {
      if (src.getName().equalsIgnoreCase("format")) {
        setFormat(src.getValue().toString());
        model.refresh();
      }
    }

  }

  public static void main(String[] args) {

    fr.esrf.tangoatk.core.AttributeList attributeList = new
        fr.esrf.tangoatk.core.AttributeList();
    java.awt.GridBagConstraints gridBagConstraints;

    final NumberScalarWheelEditor nsv = new NumberScalarWheelEditor();
    final NumberScalarWheelEditor nsv2 = new NumberScalarWheelEditor();

    javax.swing.JFrame f = new javax.swing.JFrame();

    try {

      nsv.setFont(new java.awt.Font("Lucida Bright", java.awt.Font.BOLD, 22));
      final INumberScalar attr = (INumberScalar) attributeList.add("jlp/test/1/att_trois");
      nsv.setModel(attr);
      nsv2.setModel(attr);
      attributeList.setRefreshInterval(10000);
      attributeList.startRefresher();

    } catch (ConnectionException e) {
      ErrorPane.showErrorMessage(f,"jlp/test/1/att_trois",e);
    } // end of try-catch



    f.getContentPane().setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    f.getContentPane().add(nsv, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    f.getContentPane().add(nsv2, gridBagConstraints);

    f.pack();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);
  }


}
