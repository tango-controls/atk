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
 * StateViewer.java
 *
 * Created on February 09, 2005, 14:07
 */

package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.ATKConstant;

import javax.swing.JFrame;


/**
 * <code>State</code>
 * State is a viewer to surveil and attribute of type DevState.
 * Normally one connects the device with the viewer like this:<br>
 * <code>
 * IEntity ie =attributeList.add("mydeviceName/StateAtt"); // some method to obtain the state attribute.
 * DevStateScalar stateAtt = (DevStateScalar) stateAtt;
 * fr.esrf.tangoatk.widget.attribute.StateViewer statev = new StateViewer();
 * statev.setModel(stateAtt);
 * </code>
 */
public class StateViewer extends javax.swing.JPanel
       implements fr.esrf.tangoatk.core.IDevStateScalarListener
{


  private javax.swing.JLabel     textLabel;
  private javax.swing.JLabel     valueLabel;
  
  
  private IDevStateScalar  model = null;
  private boolean          useDeviceAlias = true;
  private String           currentState = IDevice.UNKNOWN;
  private boolean          externalSetText = false;
  private boolean          stateInTooltip = false;
  

  public StateViewer()
  {
    initComponents();
   // UIManagerHelper.setAll("StateViewer.Label", textLabel);
  }

  private void initComponents()
  {
    textLabel = new javax.swing.JLabel();
    valueLabel = new javax.swing.JLabel();

    setLayout(new java.awt.GridBagLayout());
    java.awt.GridBagConstraints gridBagConstraints1;

    textLabel.setText("Not Connected");
    textLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    gridBagConstraints1 = new java.awt.GridBagConstraints();
    gridBagConstraints1.gridx = 1;
    gridBagConstraints1.gridy = 0;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints1.insets = new java.awt.Insets(0, 4, 0, 3);
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints1.weightx = 0.1;
    gridBagConstraints1.weighty = 0.1;
    add(textLabel, gridBagConstraints1);

    valueLabel.setBackground(java.awt.Color.red);
    valueLabel.setFont(new java.awt.Font("Dialog", 0, 12));
    valueLabel.setPreferredSize(new java.awt.Dimension(40, 14));
    valueLabel.setOpaque(true);

    gridBagConstraints1 = new java.awt.GridBagConstraints();
    gridBagConstraints1.gridx = 0;
    gridBagConstraints1.gridy = 0;
    gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints1.insets = new java.awt.Insets(0, 3, 0, 4);
    gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints1.weightx = 0.2;
    gridBagConstraints1.weighty = 0.2;
    add(valueLabel, gridBagConstraints1);

  }
  
  
  public void clearModel()
  {
      if (model != null)
      {
	 model.removeDevStateScalarListener(this);
	 model = null;
      }
  }


   /**
   * <code>setModel</code> sets the model of this viewer.
   * If the textLabel property is not set, the name of the devState attribute is
   * shown on the textLabel.
   * @param stateAtt a <code>DevStateScalar</code> to surveil
   */
  public void setModel(IDevStateScalar stateAtt)
  {
      if (model != null)
	 clearModel();

      if (stateAtt == null) 
	 return;

      this.model = stateAtt;
      stateAtt.addDevStateScalarListener(this);
      
      if (!externalSetText)
        if (useDeviceAlias)
	{
	   if (model.getDevice().getAlias() != null)
	      textLabel.setText(model.getDevice().getAlias());
	   else
	      textLabel.setText(model.getDevice().getName());
	}
	else
	   textLabel.setText(model.getDevice().getName());

      valueLabel.setToolTipText(model.getDevice().getName());
      //stateAtt.refresh();
      setCurrentState(model.getDeviceValue());
  }

  /**
   * <code>getModel</code> gets the model of this stateviewer.
   *
   * @return a <code>DevStateScalar</code> value
   */
  public IDevStateScalar getModel() {
    return model;
  }

  /**
   * <code>setLabel</code> set the text of the label. The
   * default value is to show the name of the devState attribute.
   * @param label a <code>String</code> value
   */
  public void setLabel(String label) {
    externalSetText = true;
    textLabel.setText(label);
  }

  public String getLabel() {
    return textLabel.getText();
  }

  /**
   * <code>setLabelVisisble</code> makes the label visible or not.
   *
   * @param visible a <code>boolean</code> value
   */
  public void setLabelVisible(boolean visible) {
    textLabel.setVisible(visible);
  }

  /**
   * <code>isLabelVisible</code> returns the visibility of the label
   *
   * @return a <code>boolean</code> value
   */
  public boolean isLabelVisible() {
    return textLabel.isVisible();
  }

  /**
   * <code>getUseDeviceAlias</code> returns true if the device alias is displayed instead of device name
   *
   * @return a <code>boolean</code> value
   */
  public boolean getUseDeviceAlias() {
    return useDeviceAlias;
  }

  /**
   * <code>setUseDeviceAlias</code> use or not use device alias
   *
   * @param b True to enable the usage of device alias.
   */
  public void setUseDeviceAlias(boolean b) {
    useDeviceAlias=b;
  }

  /**
   * <code>setStateVisible</code> makes the state value lable visible or not.
   *
   * @param visible a <code>boolean</code> value
   */
  public void setStateVisible(boolean visible) {
    valueLabel.setVisible(visible);
  }

  /**
   * <code>isStateVisible</code> returns the visibility of the
   * state value label
   * @return a <code>boolean</code> value
   */
  public boolean isStateVisible() {
    return valueLabel.isVisible();
  }
  
  /**
   * <code>getStateInTooltip</code> returns true if the device state is displayed inside the viewer's tooltip
   *
   * @return a <code>boolean</code> value
   */
  public boolean getStateInTooltip() {
    return stateInTooltip;
  }

  /**
   * <code>setStateInTooltip</code> display or not the device state inside the tooltip
   *
   * @param b If True the device state will be displayed inside the tooltip.
   */
  public void setStateInTooltip(boolean b) {
    if (stateInTooltip != b)
    {
       if (b == false)
          if (model != null)
              valueLabel.setToolTipText(model.getDevice().getName());
       stateInTooltip=b;
    }
  }


  /**
   * <code>stateChange</code> inherited from IAttributeStateListener called when the
   * attribute quality factor changes.
   *
   * @param e A <code>AttributeStateEvent</code> value
   */
  public void stateChange(AttributeStateEvent e)
  {
  }


  public void devStateScalarChange(DevStateScalarEvent evt)
  {
      setCurrentState(evt.getValue());
  }

  public void errorChange(ErrorEvent evt)
  {
      setCurrentState(IDevice.UNKNOWN);
  }


    /**
   * <code>setCurrentState</code>
   *
   * @param stateStr a <code>String</code> value
   */
  private void setCurrentState(String stateStr)
  {
    this.currentState = stateStr;
    valueLabel.setBackground(ATKConstant.getColor4State(currentState, model.getInvertedOpenClose(), model.getInvertedInsertExtract()));
    if (stateInTooltip)
       valueLabel.setToolTipText(model.getDevice().getName() + " : " + currentState);
  }

  /**
   * <code>getCurrentState</code>
   *
   * @return a <code>String</code> value presenting the current value of the DevStateScalar attribute
   */
  public String getCurrentState()
  {
    return currentState;
  }

  /**
   * <code>setStateText</code> sets the text on the colored state box
   *
   * @param text a <code>String</code> value
   */
  public void setStateText(String text) {
    valueLabel.setText(text);
  }

  /**
   * <code>getStateText</code> gets the text that is on the
   * colored state box
   * @return a <code>String</code> value
   */
  public String getStateText() {
    return valueLabel.getText();
  }


  public void setStateFont(java.awt.Font font) {
    valueLabel.setFont(font);
  }

  public void setFont(java.awt.Font font) {
    if (valueLabel != null) {
      valueLabel.setFont(font);
    }
    if (textLabel != null) {
      textLabel.setFont(font);
    }

    super.setFont(font);
  }

  public java.awt.Font getStateFont() {
    return valueLabel.getFont();
  }

  public void setStateForeground(java.awt.Color color) {
    valueLabel.setForeground(color);
  }


  public java.awt.Color getStateForeground() {
    return valueLabel.getForeground();
  }


  public void setForeground(java.awt.Color color) {
    if (valueLabel != null) {
      valueLabel.setForeground(color);
    }
    if (textLabel != null) {
      textLabel.setForeground(color);
    }
    super.setForeground(color);
  }


  /**
   * <code>setStateHorizontalAlignement</code>
   * @see javax.swing.SwingConstants
   * @param i an <code>int</code> value
   */
  public void setStateHorizontalAlignment(int i)
  {
    valueLabel.setHorizontalAlignment(i);
  }

  public int getStateHorizontalAlignment() {
    return valueLabel.getHorizontalAlignment();
  }

  public void setStatePreferredSize(java.awt.Dimension dimension) {
    valueLabel.setPreferredSize(dimension);
  }

  public java.awt.Dimension getStatePreferredSize() {
    return valueLabel.getPreferredSize();
  }

  public void setStateBorder(javax.swing.border.Border border) {
    if (valueLabel == null) return;

    valueLabel.setBorder(border);
  }

  public javax.swing.border.Border getStateBorder() {
    if (valueLabel == null) return null;

    return valueLabel.getBorder();
  }



    
    public static void main(String[] args)
    {
       final fr.esrf.tangoatk.core.AttributeList  attList = new fr.esrf.tangoatk.core.AttributeList();
       IDevStateScalar                            attState;
       JFrame                                     mainFrame;
       
       StateViewer                                stv = new StateViewer();

       //nslv.setBackground(java.awt.Color.white);
       //nslv.setForeground(java.awt.Color.black);

       // Connect to a list of number scalar attributes
       try
       {
          attState = (IDevStateScalar) attList.add("jlp/test/1/State");
	  stv.setModel(attState);
	  stv.setLabel("jlp status");
       }
       catch (Exception ex)
       {
          System.out.println("caught exception : "+ ex.getMessage());
	  System.exit(-1);
       }
       
       mainFrame = new JFrame();
       
       mainFrame.addWindowListener(
	       new java.awt.event.WindowAdapter()
			  {
			      public void windowActivated(java.awt.event.WindowEvent evt)
			      {
				 // To be sure that the refresher (an independente thread)
				 // will begin when the the layout manager has finished
				 // to size and position all the components of the window
				 attList.startRefresher();
			      }
			  }
                                     );
				     

       mainFrame.setContentPane(stv);
       mainFrame.pack();

       mainFrame.setVisible(true);

    } // end of main ()


}
