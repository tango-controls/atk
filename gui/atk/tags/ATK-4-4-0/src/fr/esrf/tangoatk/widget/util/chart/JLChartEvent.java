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
 
// JLChartEvent.java
// 
// Description:       

package fr.esrf.tangoatk.widget.util.chart;
import java.util.EventObject;

/**   Event sent when when the user click on the graph */
public class JLChartEvent extends EventObject {

    public SearchInfo searchResult;
    
    public JLChartEvent(Object source,SearchInfo si) {
	super(source);
	searchResult = si;
    }

    public void setSource(Object source) {
	this.source = source;
    }
    
    public String getVersion() {
	return "$Id$";
    }

    public Object clone() {
	return new JLChartEvent(source, searchResult);
    }

   /**
    * Gets the x value pixel coordinates
    * @return X value
    */
    public double getX() {
      return searchResult.x;
    }

    /**
     * Gets the y value pixel coordinates
     * @return Y value
     */
    public double getY() {
      return searchResult.y;
    }

   /**
    * Gets the x value (original value)
    * @return X value
    */
    public double getXValue() {
      if( searchResult.xdataView!=null )
        return searchResult.xvalue.y;
      else
        return searchResult.value.x;
    }

    /**
     * Gets the y value (original value)
     * @return Original Y value
     */
    public double getYValue() {
      return searchResult.value.y;
    }

    /**
     * Gets the x transformed value (through the A0,A1,A2 polynomial transform)
     * Does not make conversion in normal monitoring mode (not XY)
     */
    public double getTransformedXValue() {
      if( searchResult.xdataView!=null )
        return searchResult.xdataView.getTransformedValue(searchResult.xvalue.y);
      else
        return searchResult.value.x;
    }

    /**
     * Gets the y transformed value (through the A0,A1,A2 polynomial transform)
     */
    public double getTransformedYValue() {
      return searchResult.dataView.getTransformedValue(searchResult.value.y);
    }
    
   /**
    * Gets the index of the clicked point in the dataView.
    * Works only with normal mode. (not XY)
    * @return index
    */
    public int getDataViewIndex() {
        return searchResult.clickIdx;
    }

   /**
    * Gets the dataView that contains the clicked point.
    * @return DataView
    */
    public JLDataView getDataView() {
      return searchResult.dataView;
    }

}
