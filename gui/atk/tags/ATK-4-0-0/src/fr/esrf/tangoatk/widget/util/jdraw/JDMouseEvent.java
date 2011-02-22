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
 
package fr.esrf.tangoatk.widget.util.jdraw;

import java.awt.event.MouseEvent;
import java.util.EventObject;

/** JDraw MouseEvent */
public class JDMouseEvent extends EventObject {

  private MouseEvent realSource;

  /**
   * Construct a JDMouseEvent
   * @param source JDObject source
   * @param e0 Initial mouse event.
   */
  public JDMouseEvent(JDObject source,MouseEvent e0) {

    super(source);
    realSource = e0;

  }
  /**
   * Returns the horizontal x position of the intial MouseEvent.
   */
  public int getX() {
      return realSource.getX();
  }

  /**
   * Returns the vetical y position of the intial MouseEvent.
   */
  public int getY() {
    return realSource.getY();
  }

  /**
   * Indicates the number of quick consecutive clicks of
   * a mouse button.
   */
  public int getClickCount() {
    return realSource.getClickCount();
  }

  /**
   * Returns which, if any, of the mouse buttons has changed state.
   * @see MouseEvent#BUTTON1
   * @see MouseEvent#BUTTON2
   * @see MouseEvent#BUTTON3
   */
  public int getButton() {
    return realSource.getButton();
  }

  /**
   * Returns the timestamp of when this event occurred.
   */
  public long getWhen() {
    return realSource.getWhen();
  }

  /**
   * Returns whether or not the Shift modifier is down on this event.
   */
  public boolean isShiftDown() {
    return realSource.isShiftDown();
  }

  /**
   * Returns whether or not the Control modifier is down on this event.
   */
  public boolean isControlDown() {
    return realSource.isControlDown();
  }

  /**
   * Returns whether or not the Meta modifier is down on this event.
   */
  public boolean isMetaDown() {
    return realSource.isMetaDown();
  }

  /**
   * Returns whether or not the Alt modifier is down on this event.
   */
  public boolean isAltDown() {
      return realSource.isAltDown();
  }

  /**
   * Returns whether or not the AltGraph modifier is down on this event.
   */
  public boolean isAltGraphDown() {
      return realSource.isAltGraphDown();
  }

  /**
   * Returns the modifier mask for this event.
   */
  public int getModifiers() {
      return realSource.getModifiers();
  }

}
