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
 
package fr.esrf.tangoatk.widget.util.interlock;

import java.awt.*;

class UndoPattern {

  // Minimum NetObject property set.
  // When keeping only the reference, you must take care
  // of never changing this object during the edition.
  // To change it, you have to recreate it.

  int type;               // Object type
  int userType;           // User type
  int size;               // Object size
  int shape;              // Object shape
  boolean eshape;         // Editable shape
  int maxi;               // Maximun number of input
  int maxo;               // Maximun number of output
  int x;                  // X Origin
  int y;                  // Y Origin

  String label;           // Label
  int lx;                 // Label X offset (pixel coordinates)
  int ly;                 // Label Y offset (pixel coordinates)
  int justify;            // Label justification

  int idx;                // Graph link backup
  int[] childList;        // Graph link backup

  String[] extsV;         // BUBBLE only: extensions value
  String[] extsN;         // BUBBLE only: extensions name (reference only)

  Font     textFnt;       // TEXT only: Font text (reference only)


  /**
   * Create a backup of the given object.
   * @param o Object to be backuped.
   */
  public UndoPattern(NetObject o) {
    int i;

    // create object backup
    type = o.type;
    userType = o.userType;
    shape = o.shape;
    eshape = o.editableShape;
    size = o.bSize;
    maxi = o.maxInput;
    maxo = o.maxOutput;
    x = o.org.x;
    y = o.org.y;

    label  = o.getLabel();
    lx = o.labelOffset.x;
    ly = o.labelOffset.y;
    justify = o.justify;

    textFnt = o.getTextFont();

    idx = o.getIndex();
    childList = new int[o.getChildrenNumber()];
    for(i=0;i<childList.length;i++)
      childList[i] = o.getChildAt(i).getIndex();

    // Bubble specific
    if (type == NetObject.OBJECT_BUBBLE) {

      extsN = o.extParamName;
      if (extsN != null) {
        extsV = new String[extsN.length];
        for (i = 0; i < extsN.length; i++) extsV[i] = new String(o.extParamValue[i]);
      }

    }

  }

}
