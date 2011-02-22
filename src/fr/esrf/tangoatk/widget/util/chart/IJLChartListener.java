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

/**   An interface to handle some event comming from the chart */
public interface IJLChartListener extends EventListener, java.io.Serializable {

    /**
      * Called when the user click on the chart
      * @param evt Event object (containing click inforamtion)
      * @return A set of string to display in the value tooltip. Does not
      *         display the tooltip if an empty array is returned.
      *         Keep default behavior when null is returned
      */		
    public String[] clickOnChart(JLChartEvent evt);

}
