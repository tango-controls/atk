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

import java.io.IOException;
import java.awt.geom.Rectangle2D;

class JLXObject {

  String name;
  boolean visible;
  Rectangle2D boundRect;
  JLXStyle style;
  int shadowWidth;

  JLXObject(String name) {
    this.name = name;
    visible = true;
    shadowWidth = 0;
  }

  void correct(double x,double y) {
    boundRect.setRect(boundRect.getX()+x,boundRect.getY()+y,boundRect.getWidth(),boundRect.getHeight());
  }

  void parse(JLXFileLoader f,boolean isGroup) throws IOException {

    String s = f.read_string();
    if(!s.equals("!") && !s.equals("$"))
      name = s;

    String flags = f.read_safe_word();
    visible = flags.charAt(0)=='1';
    //flags[1] = Zoomable
    //flags[2] = UserResizable
    //flags[3] = UserMovable
    //flags[4] = Locked
    //flags[5] = Sensitive
    //flags[6] = Selectable
    //flags[7] = BlinkingEnabled
    //flags[8] = Opaque

    double x = f.read_double();
    f.jump_colon();
    double y = f.read_double();
    f.jump_colon();
    double w = f.read_double();
    f.jump_colon();
    double h = f.read_double();

    boundRect = new Rectangle2D.Double(x,y,w,h);

    f.read_double();         // Trajectory inc
    f.read_double();         // Trajectory step
    f.read_safe_full_word(); // Trajectory offset

    f.read_safe_word(); // reference 1
    if(f.version.compareTo("1.1.1") >= 0)
      f.read_safe_word(); // reference 2

    if(f.version.compareTo("2.0.0") >= 0)
      f.read_int(); // Ghost flag

    style = new JLXStyle();
    if(!isGroup) {
      style.parse(f);
      shadowWidth = (int)f.read_double();
    } else {
      style.fillStyle = JDObject.FILL_STYLE_NONE;
      style.lineWidth = 0;
    }

  }


}
