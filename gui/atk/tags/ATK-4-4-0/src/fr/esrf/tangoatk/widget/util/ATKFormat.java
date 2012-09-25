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
 

package fr.esrf.tangoatk.widget.util;

/**
 * A Base class for atk formatting.
 */
public class ATKFormat {

  /** Returns a String representating the given number.
   * @param number A number
   */
  public String format(Number number) {
    return number.toString();
  }

  /** Returns a String representating the given string.
   * @param s A String
   */
  public String format(String s) {
    return s;
  }

  /** Returns a String representating the given object.
   * @param obj An Object
   */
  public String format(Object obj) {
    return obj.toString();
  }

  /**
   * Construct an ATKFormat object.
   */
  public ATKFormat() {
  }
}
