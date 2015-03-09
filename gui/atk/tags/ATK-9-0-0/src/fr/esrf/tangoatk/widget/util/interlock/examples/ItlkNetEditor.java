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
 
/** An application example that uses the NetEditor */

import fr.esrf.tangoatk.widget.util.interlock.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/** A class which override the NetEditor to build an Interlock Simulator editor */
public class ItlkNetEditor extends NetEditor {

  // Interlock Bubble Type
  final static int ITLK_BUBBLE   = 1;  // Represents a physical interlock (as a switch)
  final static int SENSOR_BUBBLE = 2;  // Represents a logical sensor
  final static int VCC_BUBBLE    = 3;  // Represents a VCC object
  final static int GROUND_BUBBLE = 4;  // Represents a ground object
  final static int JOIN_BUBBLE   = 5;  // Represents an intersection point

  private int createMode;
  private NetObject theRoot;  // root VCC object (only one allowed)

  /** Inner class to handle interlock evaluation algorithm */
  class ItlkInfo {
    boolean state;        // false=OPEN true=CLOSE
    int     nbHit;        // mark for graph browsing
    boolean storedResult; // Temporary result for node with multiple input
  }

  // Set of ITLK_BUBBLE extensions
  final static String[] bubbleExt = { "Type","Address" };

  /** Construction */
  public ItlkNetEditor(JFrame parent) {

    super(parent);
    createMode = 0;
    theRoot=null;

  }

  /** Override loadFile to reinitialise object struct after a load */
  public void loadFile(String fileName) throws IOException {

    super.loadFile(fileName);
    if(!isEditable()) prepareObjects();

  }

  /** Sets the editor in EDIT/TRACE mode */
  public void setEditable(boolean b) {

    if(!b) prepareObjects();
    super.setEditable(b);

  }

  /** Returns the root of the scheme (The only VCC object) or null */
  public NetObject getRoot() {
    return theRoot;
  }

  /** Prepare object for logical evaluation, must be called once before playing logic */
  public void prepareObjects() {

    for (int i = 0; i < getNetObjectNumber(); i++) {
      NetObject o = getNetObjectAt(i);
      if (o.getType() != NetObject.OBJECT_TEXT) {
        o.setUserValue(new ItlkInfo());
        getII(o).state = true; /* Initial state to CLOSE(green) */
        o.setColor(Color.GREEN);
      }
    }

  }

  /** Reset algorithm variable */
  private void resetLogic() {

    for (int i = 0; i < getNetObjectNumber(); i++) {
      NetObject o = getNetObjectAt(i);
      if (o.getType() != NetObject.OBJECT_TEXT) {
        getII(o).nbHit = o.getParentNumber();
        getII(o).storedResult = false;
      }
    }

  }

  /** Compute state of link and output (sensor), paint links */
  private void computeState(Graphics2D g,NetObject o,boolean curState) {

    if (getII(o).nbHit > 1) {

      // Multiple input
      // Wait for the end of the branch
      getII(o).storedResult = getII(o).storedResult || curState;
      getII(o).nbHit--;

    } else {

      switch (o.getUserType()) {
        // Logic for a bubble (seen as a switch)
        case ITLK_BUBBLE:
          if (getII(o).state)
            propagateState(g, o, curState);
          else
            propagateState(g, o, false);
          break;

        case VCC_BUBBLE:
          propagateState(g, o, curState);
          break;

        case SENSOR_BUBBLE:
          if (!curState)
            o.setColor(Color.RED);
          else
            o.setColor(Color.GREEN);
          propagateState(g, o, curState);
          break;

        // Logic for an intersection
        case JOIN_BUBBLE:
          if (o.getParentNumber() > 1) {
            getII(o).storedResult |= curState;
            propagateState(g, o, getII(o).storedResult);
          } else {
            propagateState(g, o, curState);
          }
          break;
      }

    }

  }

  /** propagate state on children */
  private void propagateState(Graphics2D g, NetObject o,boolean curState) {

    for (int i = 0; i < o.getChildrenNumber(); i++) {
      NetObject c = o.getChildAt(i);
      if( curState ) {
        g.setColor(Color.GREEN);
      } else {
        g.setColor(Color.RED);
      }
      o.paintLink(g,c,false);
      computeState(g, c,curState);
    }

  }

  /** Swap an iterlock state */
  public void swapItlkState(NetObject o) {

    getII(o).state = !getII(o).state;
    if( getII(o).state ) {
      o.setColor(Color.GREEN);
    } else {
      o.setColor(Color.RED);
    }
    repaint();

  }

  /** Helper function to retreive the ItlkInfo */
  private ItlkInfo getII(NetObject o) {
    return (ItlkInfo)o.getUserValue();
  }

  /** Sets the editor in creation mode */
  public void setCreateMode(int type,int userType) {
    createMode =  userType;
    setCreateMode(type);
  }

  /** Overriding createBubbleObject to create our specific NetObject */
  public NetObject createBubbleObject(int x,int y) {

    NetObject ret = null;

    switch(createMode) {

      case ITLK_BUBBLE:
        ret = new NetObject(NetObject.OBJECT_BUBBLE,ITLK_BUBBLE,1,1,x,y);
        ret.setShape(NetShape.SHAPE_CIRCLE);
        ret.setExtensionList(bubbleExt);
        ret.setEditableShape(false);
        break;
      case SENSOR_BUBBLE:
        ret = new NetObject(NetObject.OBJECT_BUBBLE,SENSOR_BUBBLE,1,1,x,y);
        ret.setShape(NetShape.SHAPE_SQUARE);
        ret.setEditableShape(false);
        ret.setSize(9);
        break;
      case VCC_BUBBLE:
        ret = new NetObject(NetObject.OBJECT_BUBBLE,VCC_BUBBLE,0,1,x,y);
        ret.setShape(NetShape.SHAPE_VCC);
        ret.setEditableShape(false);
        ret.setLabel("Vcc");
        break;
      case GROUND_BUBBLE:
        ret = new NetObject(NetObject.OBJECT_BUBBLE,GROUND_BUBBLE,1,0,x,y);
        ret.setShape(NetShape.SHAPE_GROUND);
        ret.setEditableShape(false);
        break;
      case JOIN_BUBBLE:
        ret = new NetObject(NetObject.OBJECT_BUBBLE,JOIN_BUBBLE,10,10,x,y);
        ret.setShape(NetShape.SHAPE_DOT);
        ret.setEditableShape(false);
        break;

    }

    return ret;

  }

  /** Override addObject to ensure that there is always at most one VCC object */
  public boolean addObject(NetObject o) {

    if (o == null)
      return false;

    if (o.getUserType() == VCC_BUBBLE) {
      if (theRoot != null) {
        JOptionPane.showMessageDialog(getParentFrame(), "Cannot add a new VCC object. Only 1 accepted",
                "Error", JOptionPane.ERROR_MESSAGE);
        return false;
      } else {
        theRoot = o;
      }
    }

    return super.addObject(o);

  }

  /** Override removeObject to ensure that there is always at most one VCC object */
  public void removeObject(NetObject o) {

    if(theRoot==o) theRoot=null;
    super.removeObject(o);

  }

  /** Override clearObjects to ensure that there is always at most one VCC object */
  public void clearObjects() {

    theRoot=null;
    super.clearObjects();

  }

  /** Override paintLink to paint link state */
  public void paintLinks(Graphics2D g2) {

    if(!isEditable()) {
      resetLogic();
      if(theRoot!=null) computeState(g2,theRoot,true);
    } else {
      super.paintLinks(g2);
    }

  }

  /** Override paintObject to map the type and address extension to small label before objects are painted */
  public void paintObjects(Graphics2D g2) {

    for(int i=0;i < getNetObjectNumber();i++) {
      NetObject o = getNetObjectAt(i);
      if(o.getUserType()==ITLK_BUBBLE) {
        o.setCenterLabel(o.getExtendedParam(0 /* Type extensions */ ));
        o.setBottomLabel(o.getExtendedParam(1 /* Address extensions */));
      }
    }
    super.paintObjects(g2);

  }

}
