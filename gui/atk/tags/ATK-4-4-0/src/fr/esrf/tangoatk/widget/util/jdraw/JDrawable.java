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

import javax.swing.*;

/** An interface that must be implemented by a JComponent which can be edited with JDrawEditor.
 * @see JDrawableList#addClass
 */
public interface JDrawable {

  /** Call after a component of a JDSwingObject is created, this give a default look
   * and feel for editing. */
  public void initForEditing();

  /** Returns the JComponent that implements this interface. */
  public JComponent getComponent();

  /** Returns list of extension name for this objects (Empty array for none). */
  public String[] getExtensionList();

  /** Sets the specified param.
   * @param name Parameter name (Case unsensitive).
   * @param value Parameter value.
   * @param popupAllowed true when the JDrawable should display a popup if
   * the parameter value is incorrect, false otherwise. Note that the JDrawable
   * must not display an error message if the parameter does not exists even
   * if popupAllowed is true.
   * @return true if parameters has been succesfully applied, false otherwise.
   */
  public boolean setExtendedParam(String name,String value,boolean popupAllowed);

  /**
   * Returns the specified parameter value.
   * @param name Param name (Case unsensitive).
   * @return Empty string if not exists, the value otherwise.
   */
  public String getExtendedParam(String name);

  /**
   * Get a description of this extensions.
   * @param extName Extension name
   * @return Empty string for no description.
   */ 
  public String getDescription(String extName);

}
