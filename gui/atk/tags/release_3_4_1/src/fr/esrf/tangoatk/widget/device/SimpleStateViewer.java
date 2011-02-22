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
 
package fr.esrf.tangoatk.widget.device;

import fr.esrf.tangoatk.core.Device;
import fr.esrf.tangoatk.core.IDevice;
import fr.esrf.tangoatk.core.IDeviceApplication;
import fr.esrf.tangoatk.core.StateEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.IStateListener;
import fr.esrf.tangoatk.core.IErrorListener;

import fr.esrf.tangoatk.widget.util.*;

/**
 * <code>SimpleStateViewer</code> is a viewer to surveil the state of a
 * {@link fr.esrf.tangoatk.core.Device}. Background color are defined in
 * {@link fr.esrf.tangoatk.widget.util.ATKConstant} . SimpleStateViewer offer
 * the possibility to use Antialiased font for better rendering.
 * SimpleStateViewer has no label. Normally one connects the device
 * with the viewer like this:
 * <p>
 * <pre>
 * Device device = DeviceFactory.getInstance().getDevice("my_device");
 * SimpleStateViewer state = new SimpleStateViewer();
 * state.setModel(device);
 * </pre>
 * @version $Revision$
 */
public class SimpleStateViewer extends JSmoothLabel
        implements IStateListener,IErrorListener {

  private Device device;
  String state = "UNKNOWN";
  boolean externalSetText = false;
  boolean stateClickable = true;
  IDeviceApplication application;

  IDevicePopUp popUp = SingletonStatusViewer.getInstance();

  /**
   * Contructs a SimpleStateViewer.
   */
  public SimpleStateViewer() {

    setFont(ATKConstant.labelFont);
    setPreferredSize(new java.awt.Dimension(40, 14));
    setOpaque(true);
    addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        valueLabelMouseClicked(evt);
      }
    });

  }

  private void valueLabelMouseClicked(java.awt.event.MouseEvent evt) {

    if ((evt.getModifiers() &
            java.awt.event.InputEvent.BUTTON3_MASK) != 0) {
      if (application == null) return;
      application.setModel(device);
      application.run();
      return;
    }

    if (stateClickable && device != null) {
      popUp.setModel(device);
      popUp.setVisible(true);
    }

  }

  /**
   * <code>setModel</code> sets the model of this viewer.
   * If the textLabel property is not set, the name of the device is
   * shown on the textLabel.
   * @param device a <code>Device</code> to surveil
   */
  public void setModel(Device devModel)
  {
     if (device != null)
         clearModel();
     
     if (devModel == null)
         return;

     device = devModel;
     device.addStateListener(this);
     device.addErrorListener(this);
     setState(device.getState());
     setToolTipText(device.getName());
  }

  public void clearModel()
  {
     if (device != null)
     {
        device.removeStateListener(this);
        device.removeErrorListener(this);
        device = null;
        setState(IDevice.UNKNOWN);
        setToolTipText("no device");        
     }
  }

  /**
   * <code>getModel</code> gets the model of this stateviewer.
   *
   * @return a <code>Device</code> value
   */
  public Device getModel() {
    return device;
  }

  /**
   * <code>setState</code>
   *
   * @param state a <code>String</code> value
   */
  private void setState(String state) {
    this.state = state;
    if (device != null)
        setBackground(ATKConstant.getColor4State(state, device.getInvertedOpenClose(), device.getInvertedInsertExtract()));
    else
        setBackground(ATKConstant.getColor4State(state));
  }

  /**
   * <code>getState</code>
   *
   * @return a <code>String</code> value presenting the state of the device
   */
  public String getState() {
    return state;
  }

  // --------------------------------------------------
  // State listener
  // --------------------------------------------------
  public void stateChange(StateEvent evt) {
    setState(evt.getState());
  }

  public void errorChange(ErrorEvent evt) {
    setState("UNKNOWN");
  }

  /**
   * <code>setStateClickable</code> will the state be clickable?
   *
   * @param clickable a <code>boolean</code> value
   */
  public void setStateClickable(boolean clickable) {
    stateClickable = clickable;
  }

  /**
   * <code>isStateClickable</code> returns if the state is clickable or not.
   *
   * @return a <code>boolean</code> value
   */
  public boolean isStateClickable() {
    return stateClickable;
  }

  /**
   * Set the application which will be displayed on right mouse click.
   * @param runnable Application to be launched
   */
  public void setApplication(IDeviceApplication runnable) {
    application = runnable;
  }

  /**
   * Gets the application attached to this state viewer.
   * @see #setApplication
   */
  public IDeviceApplication getApplication() {
    return application;
  }

  /**
   * Get the value of popUp.
   * @return value of popUp.
   * @see #setPopUp
   */
  public IDevicePopUp getPopUp() {
    return popUp;
  }

  /**
   * Set the popup which will be displayed on left mouse click.
   * @param v  Value to assign to popUp.
   */
  public void setPopUp(IDevicePopUp v) {
    this.popUp = v;
  }

}
