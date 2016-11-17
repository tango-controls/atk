/*
 *  Copyright (C) :     2002,2003,2004,2005,2006,2007,2008,2009
 *                      European Synchrotron Radiation Facility
 *                      BP 220, Grenoble 38043
 *                      FRANCE
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
package fr.esrf.tangoatk.widget.util.jgl3dchart;

import java.awt.font.FontRenderContext;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Class which handle 2d label positioning
 */
public class LabelInfo {

  private final static double cos27 = 0.891;
  private final static double cos63 = 0.454;

  // 2D tick coordinates
  double x1;
  double y1;
  double x2;
  double y2;

  // 3D tick coordinates
  VERTEX3D p1;
  VERTEX3D p2;

  Font labelFont;
  Color labelColor;
  String value;
  int width;
  int height;
  double x;
  double y;
  int ascent;

  void measureLabel(FontRenderContext frc) {

    Rectangle2D bounds = labelFont.getStringBounds(value, frc);
    width  = (int)(bounds.getWidth()+0.5);
    height = (int)(bounds.getHeight()+0.5);
    ascent = (int)(labelFont.getLineMetrics("0",frc).getAscent()+0.5f);

  }

  void computePosition() {

    double vx = x2-x1;
    double vy = y2-y1;
    double n = Math.sqrt( (vx*vx) + (vy*vy) );
    double cs = (vx)/n;

    if( vy>0 ) {
      if( cs>0 ) {
        if( cs<cos63 ) {
          x = x2-(double)width/2.0;
          y = y2;
        } else if( cs>cos63 && cs<cos27 ) {
          x = x2;
          y = y2;
        } else {
          x = x2;
          y = y2-(double)height/2.0;
        }
      } else {
        if( -cs<cos63 ) {
          x = x2-(double)width/2.0;
          y = y2;
        } else if( -cs>cos63 && -cs<cos27 ) {
          x = x2-width;
          y = y2;
        } else {
          x = x2-width;
          y = y2-(double)height/2.0;
        }
      }
    } else {
      if( cs>0 ) {
        if( cs<cos63 ) {
          x = x2-(double)width/2.0;
          y = y2-height;
        } else if( cs>cos63 && cs<cos27 ) {
          x = x2;
          y = y2-height;
        } else {
          x = x2;
          y = y2-(double)height/2.0;
        }
      } else {
        if( -cs<cos63 ) {
          x = x2-(double)width/2.0;
          y = y2-height;
        } else if( -cs>cos63 && -cs<cos27 ) {
          x = x2-width;
          y = y2-height;
        } else {
          x = x2-width;
          y = y2-(double)height/2.0;
        }
      }
    }

  }

  void paint(Graphics g) {
    g.setColor(labelColor);
    g.drawString(value,(int)(x+0.5),(int)(y+0.5)+ascent);
  }

}
