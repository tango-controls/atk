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

import java.util.Vector;
import java.io.IOException;


class JLXPath {

  static int ToJDLineArrow[] = {
    JDLine.ARROW_NONE,    // 0
    JDLine.ARROW2_LEFT,   // 1
    JDLine.ARROW2_RIGHT,  // 2
    JDLine.ARROW2_BOTH,   // 3
    JDLine.ARROW_NONE,    // 4
    JDLine.ARROW1_LEFT,   // 5
    JDLine.ARROW1_RIGHT,  // 6
    JDLine.ARROW1_BOTH,   // 7
    JDLine.ARROW2_CENTER, // 8
    JDLine.ARROW_NONE,    // 9
    JDLine.ARROW_NONE,    // 10
    JDLine.ARROW_NONE,    // 11
    JDLine.ARROW1_CENTER, // 12
    JDLine.ARROW_NONE,    // 13
    JDLine.ARROW_NONE,    // 14
    JDLine.ARROW_NONE     // 15
  };

  Vector  path;
  int     arrow;
  boolean closed;
  int     pathType;

  JLXPath() {
    path = new Vector();
    arrow = JDLine.ARROW_NONE;
    closed = false;
    pathType = 1; // 0,1 Polyline 2,3 Spline
  }


  void parse(JLXFileLoader f,boolean hasShadow,boolean hasArrow) throws IOException {

    int nbItem = f.read_int();
    double[] vals;

    for(int k = 0; k < nbItem; k++)
    {
        int type = -1;
        vals = null;
        if(f.version.compareTo("2.0.0") >= 0)
            type = f.read_int();
        if(type != 4) {
          vals = f.parseDouleArray();
        } else {
          System.out.println("JLXPath.parse() Unsuported type at line " + f.getCurrentLine());
          continue;
        }

        path.add(vals);

        if(k==1) pathType = type;
    }

    closed = (f.read_int()==1);
    if(hasArrow)
      arrow = ToJDLineArrow[f.read_int()];
    if(hasShadow)
      f.read_int(); // Jump shadow path

  }

  private void cleanPath() {

    if (path.size() > 1) {
      // Remove duplicate entry
      double[] pt;
      double[] opt = (double[]) path.firstElement();
      for (int i = 1; i < path.size(); i++) {
        pt = (double[]) path.get(i);
        if ((pt[0]==opt[0]) && (pt[1]==opt[1])) {
          // remove entry
          path.remove(i);
          i--;
        }
        opt = pt;
      }
    }

    // Check last entry
    if( path.size()>1 ) {
      double[] pt  = (double[]) path.firstElement();
      double[] opt = (double[]) path.lastElement();
      if ((pt[0]==opt[0]) && (pt[1]==opt[1]))
        path.remove(0);
    }

  }

  private void createPolyline(JLXObject parent,Vector objs,boolean closed) {


    if (path.size()>1) {
      if (objs != null) {

        // Size the path
        double[] pts;
        double w = parent.boundRect.getWidth();
        double h = parent.boundRect.getHeight();

        for(int i=0;i<path.size();i++) {
          pts = (double[])path.get(i);
          pts[0] = pts[0]*w;
          pts[1] = pts[1]*h;
        }

        // Create the polyline
        this.closed = closed;
        cleanPath();
        objs.add(new JDPolyline(parent, this));

      }
    }
    path.removeAllElements();

  }

  void parseCustom(JLXFileLoader f,JLXObject parent,Vector objs) throws IOException {

    int nbItem = f.read_int();
    double[] ctrlPt;
    double[] lastPt;

    for(int i=0;i<nbItem;i++) {

      int action = f.read_int();
      switch(action) {

        case 0: // MoveTo
          //We restart a new object
          createPolyline(parent,objs,false);
          path.add(f.parseDouleArray());
          break;

        case 1: // LineTo
          path.add(f.parseDouleArray());
          break;

        case 2: // QuadTo (Emulate quadratic with spline)
          ctrlPt = f.parseDouleArray();
          lastPt = (double[])path.lastElement();
          JDUtils.computeSpline(lastPt[0],lastPt[1],ctrlPt[0],ctrlPt[1],ctrlPt[0],ctrlPt[1],ctrlPt[2],ctrlPt[3],
                                10,true,0,path,null,null);
          break;

        case 3: // SplineTo
          ctrlPt = f.parseDouleArray();
          lastPt = (double[])path.lastElement();
          JDUtils.computeSpline(lastPt[0],lastPt[1],ctrlPt[0],ctrlPt[1],ctrlPt[2],ctrlPt[3],ctrlPt[4],ctrlPt[5],
                                10,true,0,path,null,null);
          break;

        case 4: // Close
          createPolyline(parent,objs,true);
          break;
      }

    }

    // Don't forget unclosed shape
    createPolyline(parent,objs,false);

  }

}
