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
 
// JLChartActionEvent.java
//
// Description:

package fr.esrf.tangoatk.widget.util.chart;

import java.util.EventObject;

/** Event sent when when the user select a user action from
  * the contextual menu
  */
public class JLChartActionEvent extends EventObject {

  private String  actionName;
  private boolean state;

  public JLChartActionEvent(Object source, String name) {
    super(source);
    actionName = name;
    state=false;
  }

  public JLChartActionEvent(Object source, String name, boolean s) {
    super(source);
    actionName = name;
    state=s;
  }

  public void setSource(Object source) {
    this.source = source;
  }

  public String getVersion() {
    return "$Id$";
  }

  public Object clone() {
    return new JLChartActionEvent(source, actionName);
  }


  /**
   * Gets the action name
   */
  public String getName() {
    return actionName;
  }

  /**
   * Gets the action state
   */
  public boolean getState() {
    return state;
  }

}
