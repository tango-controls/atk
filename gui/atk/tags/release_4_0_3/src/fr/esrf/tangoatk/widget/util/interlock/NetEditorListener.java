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
 
/**
 * A set of class to handle a network editor and its viewer.
 *
 * Author: Jean Luc PONS
 * Date: Jul 1, 2004
 * (c) ESRF 2004
 */

package fr.esrf.tangoatk.widget.util.interlock;

import java.awt.Dimension;
import java.awt.event.MouseEvent;

/**
 * An interface to handle the interaction between the editor and the main program.
 */
public interface NetEditorListener {

  /**
   * Trigerred when the scheme change, it means that when the user
   * do something that affects the scheme currently edited, this function is called.
   * This is also true when load/saveFile or showOpen/SaveFileDialg are called.
   * @param src NetEditor that has trigerred the event.
   * @see NetEditor#loadFile
   * @see NetEditor#showOpenFileDialog
   * @see NetEditor#showSaveFileDialog
   */
  public void valueChanged(NetEditor src);

  /**
   * Trigerred when the user click on an object only
   * if the editor is not editable.
   * @param src NetEditor that has trigerred the event.
   * @param obj Clicked object.
   * @param e Original MouseEvent
   * @see NetEditor#setEditable
   */
  public void objectClicked(NetEditor src,NetObject obj,MouseEvent e);

  /**
   * Trigerred when the user click on a link if the editor is not editable.
   * @param src NetEditor that has trigerred the event.
   * @param obj Clicked object.
   * @param childIdx Child index.
   * @param e Original MouseEvent
   * @see NetEditor#setEditable
   */
  public void linkClicked(NetEditor src,NetObject obj,int childIdx,MouseEvent e);

  /**
   * Trigerred when the scheme size change, after a load or a call to computePreferredSize.
   * It the editor component is within a JSCrollPane, a call to revalidate on this scrollPane
   * may be needed. This is the main purpose of this function.
   * @param src NetEditor that has trigerred the event.
   * @param d New dimension (in pixel coordinates)
   * @see NetEditor#computePreferredSize
   * @see NetEditor#loadFile
   */
  public void sizeChanged(NetEditor src,Dimension d);

  /**
   * Triggered when the creation mode is canceled
   * only if the editor is editable.
   * @param src NetEditor that has trigerred the event.
   * @see NetEditor#setEditable
   */
  public void cancelCreate(NetEditor src);

}
