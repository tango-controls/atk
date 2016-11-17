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
 
//
// SearchInfo.java
// Description: A Class to handle 2D graphics plot
//
// JL Pons (c)ESRF 2002
package fr.esrf.tangoatk.widget.util.chart;

/** A class to handle search result. It is used when user clicks on the graph and when
    the nearest value found is displayed */

public class SearchInfo {

  /** Value tooltip TOPLEFT placement */
   public static final int TOPLEFT     = 0;
  /** Value tooltip TOPRIGHT placement */
   public static final int TOPRIGHT    = 1;
  /** Value tooltip BOTTOMLEFT placement */
   public static final int BOTTOMLEFT  = 2;
  /** Value tooltip BOTTOMRIGHT placement */
   public static final int BOTTOMRIGHT = 3;
   
   /** True when a point has been found */
   public boolean     found;
   /** X pixel coordinates of the point found */
   public int         x;
   /** Y pixel coordinates of the point found */
   public int         y;
   /** Axis on which the view containing the point is displayed */
   public JLAxis      axis;

   /** Y DataView which contains the point */
   public JLDataView  dataView;
   
   /** Handle to the y value */
   public DataList    value;

   /** X DataView which countaing the point (XY monitoring) */
   public JLDataView  xdataView;

   /** Handle to the X value (XY monitoring)*/
   public DataList    xvalue;
   
   /** Square distance from click to point (pixel) */
   public double      dist;
   
   /** placement of the tooltip panel */
   public int         placement;   
   
   /** index in the dataView that contains the clicked point */
   public int         clickIdx;   
   
   SearchInfo(int x,int y,JLDataView  dataView,JLAxis axis,DataList value,double dist,int placement,int idx) 
   {
     this.found=true;
     this.x=x;
     this.y=y;
     this.dataView=dataView;
     this.value=value;
     this.dist=dist;
     this.placement=placement;
     this.axis=axis;
     this.xvalue=null;
     this.xdataView=null;
     this.clickIdx=idx;
   }

   public void setXValue(DataList d,JLDataView  x) {
	 this.xvalue    = d;
	 this.xdataView = x;
   }

   SearchInfo() 
   {
     this.found=false;
     this.dist=Integer.MAX_VALUE;
     this.clickIdx=-1;
   }
   
   public String toString() {
     if( found ) 
       return "SearchInfo[ Hit ("+x+","+y+") View name="+dataView.getName()+
              " Org ("+value.x+","+value.y+") Dist="+dist;
     else 
       return "SearchInfo[ No Hit]";
   }
   
}
