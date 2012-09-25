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

import java.util.Vector;

class UndoBuffer  {

  private UndoPattern[] undoBuffer;
  private String name;

  /**
   * Create a backup of the given set of ItlkObjects.
   * @param objects ItlkObjects to be backuped
   * @param n backup's name
   */
  UndoBuffer(Vector objects,String n) {

    int i,sz = objects.size();

    // Reset index
    for(i=0;i<sz;i++)
      ((NetObject)objects.get(i)).setIndex(i);

    // Create the undo pattern
    undoBuffer = new UndoPattern[objects.size()];
    for(i=0;i<objects.size();i++)
      undoBuffer[i] = ((NetObject)objects.get(i)).getUndoPattern();
    name=n;
    
  }

  /**
   * Rebuild the set of NetObject backuped.
   * @return Reconstructed array of ItlkObjects
   */
  NetObject[] rebuild() {

    int i,j;
    int sz = undoBuffer.length;
    NetObject[] objects=new NetObject[sz];
    NetObject pObj,cObj;

    // Rebuild objects
    for(i=0;i<sz;i++) objects[i]=new NetObject(undoBuffer[i]);

    // Rebuild link
    for(i=0;i<sz;i++) {
      for(j=0;j<undoBuffer[i].childList.length;j++) {
        pObj = objects[i];
        cObj = objects[undoBuffer[i].childList[j]];
        pObj.addChild(cObj);
      }
    }

    return objects;
  }

  /**
   * Return the name of this backup
   * @return Name
   */
  String getName() {
    return name;
  }

}
