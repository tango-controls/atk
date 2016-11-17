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

class UndoBuffer  {

  UndoPattern[] undoBuffer;
  String name;

  UndoBuffer(Vector objects,String n) {
    undoBuffer = new UndoPattern[objects.size()];
    for(int i=0;i<objects.size();i++)
      undoBuffer[i] = ((JDObject)objects.get(i)).getUndoPattern();
    name=n;
  }

  UndoBuffer(JDObject[] objects,String n) {
    undoBuffer = new UndoPattern[objects.length];
    for(int i=0;i<objects.length;i++)
      undoBuffer[i] = objects[i].getUndoPattern();
    name=n;
  }

  static void rebuildObject(UndoPattern u,Vector children) {

    switch(u.JDclass) {
      case UndoPattern._JDEllipse:
        children.add(new JDEllipse(u));
        break;
      case UndoPattern._JDGroup:
        children.add(new JDGroup(u));
        break;
      case UndoPattern._JDLabel:
        children.add(new JDLabel(u));
        break;
      case UndoPattern._JDLine:
        children.add(new JDLine(u));
        break;
      case UndoPattern._JDPolyline:
        children.add(new JDPolyline(u));
        break;
      case UndoPattern._JDRectangle:
        children.add(new JDRectangle(u));
        break;
      case UndoPattern._JDRoundRectangle:
        children.add(new JDRoundRectangle(u));
        break;
      case UndoPattern._JDSpline:
        children.add(new JDSpline(u));
        break;
      case UndoPattern._JDImage:
        children.add(new JDImage(u));
        break;
      case UndoPattern._JDSwingObject:
        children.add(new JDSwingObject(u));
        break;
      case UndoPattern._JDAxis:
        children.add(new JDAxis(u));
        break;
      case UndoPattern._JDBar:
        children.add(new JDBar(u));
        break;
      case UndoPattern._JDSlider:
        children.add(new JDSlider(u));
        break;
      default:
        System.out.println("!!! UndoBuffer.rebuildObject() : WARNING Undo failure !!!");
    }

  }

  Vector rebuild() {

    Vector children=new Vector();
    for(int i=0;i<undoBuffer.length;i++)
      rebuildObject(undoBuffer[i],children);
    return children;

  }

  UndoPattern get(int id) {
    return undoBuffer[id];
  }

  String getName() {
    return name;
  }


}
