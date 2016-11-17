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

import javax.swing.tree.DefaultMutableTreeNode;

class JDTreeNode extends DefaultMutableTreeNode {

  private boolean areChildrenDefined = false;
  JDObject        theObject;

  public JDTreeNode(JDObject obj) {
    // Object node
    theObject = obj;
  }

  public JDTreeNode() {
    /// Root node
    theObject=null;
  }

  public int getChildCount() {
    if (!areChildrenDefined)
      defineChildNodes();
    return (super.getChildCount());
  }

  // *****************************************************************************************************************
  // Add dinamycaly nodes in the tree when the user open a branch.
  private void defineChildNodes() {

    int i;
    areChildrenDefined = true;

    // Root node
    if( theObject==null )
      return;

    // Add children of group
    if( theObject instanceof JDGroup ) {

      JDGroup g = (JDGroup)theObject;
      for(i=0;i<g.getChildrenNumber();i++)
        add(new JDTreeNode(g.getChildAt(i)));

    } else if ( theObject instanceof JDSlider ) {

      add(new JDTreeNode(((JDSlider)theObject).getCursor()));

    }


  }

  public JDObject getObject() {
    return theObject;
  }

  public String toString() {
     return "Selection";
  }

}
