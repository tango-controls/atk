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
 
package fr.esrf.tangoatk.widget.util.interlock.shape;
/* Class generated by JDraw */

import java.awt.*;

/** ---------- Storage3 class ---------- */
public class Storage3 {

  private static int[][] xPolys = null;
  private static int[][] yPolys = null;

  private static Color sColor0 = new Color(204,204,204);

  private static int[][] xOrgPolys = {
    {-25,-23,-19,-11,-6,2,9,15,19,21,20,16,10,4,-7,-13,-19,-23},
    {23,25,4},
    {-25,-23,-19,-11,-6,2,9,15,19,21,20,16,10,4,-7,-13,-19,-23},
    {23,25,4},
    {-25,-23,-19,-11,-6,2,9,15,19,21,20,16,10,4,-7,-13,-19,-23},
    {0,23,25},
    {-5,-3,1,3,3,1,-3,-5},
  };

  private static int[][] yOrgPolys = {
    {3,6,9,11,12,12,11,9,7,4,1,-2,-4,-5,-5,-4,-2,1},
    {0,3,8},
    {-1,2,5,7,8,8,7,5,3,0,-3,-6,-8,-9,-9,-8,-6,-3},
    {-4,-1,4},
    {-5,-2,1,3,4,4,3,1,-1,-4,-7,-10,-12,-13,-13,-12,-10,-7},
    {0,-8,-5},
    {-6,-7,-7,-6,-4,-3,-3,-4},
  };

  static public void paint(Graphics g,Color backColor,int x,int y,double size) {

    // Allocate array once
    if( xPolys == null ) {
      xPolys = new int [xOrgPolys.length][];
      yPolys = new int [yOrgPolys.length][];
      for( int i=0 ; i<xOrgPolys.length ; i++ ) {
        xPolys[i] = new int [xOrgPolys[i].length];
        yPolys[i] = new int [yOrgPolys[i].length];
      }
    }

    // Scale and translate poly
    for( int i=0 ; i<xOrgPolys.length ; i++ ) {
      for( int j=0 ; j<xOrgPolys[i].length ; j++ ) {
        xPolys[i][j] = (int)((double)xOrgPolys[i][j]*size+0.5) + x;
        yPolys[i][j] = (int)((double)yOrgPolys[i][j]*size+0.5) + y;
      }
    }

    // Paint object
    g.setColor(backColor);g.fillPolygon(xPolys[0],yPolys[0],xPolys[0].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[0],yPolys[0],xPolys[0].length);
    g.setColor(Color.black);g.fillPolygon(xPolys[1],yPolys[1],xPolys[1].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[1],yPolys[1],xPolys[1].length);
    g.setColor(backColor);g.fillPolygon(xPolys[2],yPolys[2],xPolys[2].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[2],yPolys[2],xPolys[2].length);
    g.setColor(Color.black);g.fillPolygon(xPolys[3],yPolys[3],xPolys[3].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[3],yPolys[3],xPolys[3].length);
    g.setColor(backColor);g.fillPolygon(xPolys[4],yPolys[4],xPolys[4].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[4],yPolys[4],xPolys[4].length);
    g.setColor(Color.black);g.fillPolygon(xPolys[5],yPolys[5],xPolys[5].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[5],yPolys[5],xPolys[5].length);
    g.setColor(sColor0);g.fillPolygon(xPolys[6],yPolys[6],xPolys[6].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[6],yPolys[6],xPolys[6].length);

  }

  static public void setBoundRect(int x,int y,double size,Rectangle bound) {
    bound.setRect((int)(-25.0*size+0.5)+x,(int)(-13.0*size+0.5)+y,(int)(51.0*size+0.5),(int)(26.0*size+0.5));
  }

}

