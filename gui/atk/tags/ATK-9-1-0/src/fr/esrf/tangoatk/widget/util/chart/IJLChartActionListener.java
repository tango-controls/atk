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
 
// File:          JLChartListener.java
//

package fr.esrf.tangoatk.widget.util.chart;
import java.util.EventListener;

/**  An interface to handle some user defined action available from
 * the chart contextual menu
 */
public interface IJLChartActionListener extends EventListener, java.io.Serializable {

    /**
      * Called when the user select a user action (available from
      * contextual chart menu)
      * @param evt Event object (containing acion name and state)
      * @see JLChart#addUserAction
      */
    public void actionPerformed(JLChartActionEvent evt);

   /**
    * Called when the the action name starting with 'chk'
    * (displayed as check box menu item) and each time the chart menu
    * is shown.
    * if several listener handle the same action, the result will be a
    * logical and of all results.
    * @param evt Event object (containing acion name)
    * @see JLChart#addUserAction
    */
    public boolean getActionState(JLChartActionEvent evt);

}
