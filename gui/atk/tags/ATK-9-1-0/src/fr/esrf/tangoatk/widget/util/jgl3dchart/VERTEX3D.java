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

/**
 * VERTEX3D class
 */
class VERTEX3D {

  VERTEX3D() {
    x=0;
    y=0;
    z=0;
  }

  VERTEX3D(double x,double y,double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public void normalize() {
    double n = norme();
    if(n!=0) {
      x=x/n;
      y=y/n;
      z=z/n;
    }
  }

  public double norme() {
    return Math.sqrt( x*x + y*y + z*z );
  }

  double x;
  double y;
  double z;

}
