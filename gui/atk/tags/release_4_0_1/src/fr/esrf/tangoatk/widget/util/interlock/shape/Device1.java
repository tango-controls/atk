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

/** ---------- Device1 class ---------- */
public class Device1 {

  private static int[][] xPolys = null;
  private static int[][] yPolys = null;

  private static Color sColor0 = new Color(204,204,204);

  private static int[][] xOrgPolys = {
    {-3,-5,-6,-6,-3,0,3,6,6,5,3},
    {-3,-3,3,3},
    {-2,2,1,-1},
  };

  private static int[][] yOrgPolys = {
    {5,2,-2,-10,-14,-15,-14,-10,-3,2,5},
    {5,12,12,5},
    {12,12,15,15},
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
    g.setColor(sColor0);g.fillPolygon(xPolys[1],yPolys[1],xPolys[1].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[1],yPolys[1],xPolys[1].length);
    g.setColor(Color.black);
    g.drawLine((int)(-3.0*size+0.5)+x, (int)(6.0*size+0.5)+y, (int)(3.0*size+0.5)+x, (int)(7.0*size+0.5)+y);
    g.setColor(Color.black);
    g.drawLine((int)(-3.0*size+0.5)+x, (int)(8.0*size+0.5)+y, (int)(3.0*size+0.5)+x, (int)(9.0*size+0.5)+y);
    g.setColor(Color.black);
    g.drawLine((int)(-3.0*size+0.5)+x, (int)(10.0*size+0.5)+y, (int)(3.0*size+0.5)+x, (int)(11.0*size+0.5)+y);
    g.setColor(sColor0);g.fillPolygon(xPolys[2],yPolys[2],xPolys[2].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[2],yPolys[2],xPolys[2].length);

  }

  static public void setBoundRect(int x,int y,double size,Rectangle bound) {
    bound.setRect((int)(-6.0*size+0.5)+x,(int)(-15.0*size+0.5)+y,(int)(13.0*size+0.5),(int)(31.0*size+0.5));
  }

}

