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

import fr.esrf.tangoatk.widget.util.interlock.NetEditor;
import fr.esrf.tangoatk.widget.util.interlock.NetObject;
import fr.esrf.tangoatk.widget.util.interlock.NetEditorListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.io.IOException;

public class DjNetViewer extends NetEditor implements NetEditorListener {

  /** An inner class to handle special data needed to evaluate the Dijkstra algorithm */
  class DijkstraInfo {
    double     dist;  // Distance to source
    NetObject  pred;  // Previous bubble (Used to draw the shortest path)
  }

  NetObject[]  bubbles;    // Graph node
  int          nbBubble;   // Node number
  NetObject    theSource=null;  // Source bubble
  NetObject    theDest=null;    // Destination bubble

  DjNetViewer(JFrame parent) {

    super(parent);
    setEditable(false);
    setMoveableBubble(true);
    addEditorListener(this);

  }

  public void loadFile(String fileName) throws IOException {

    super.loadFile(fileName);

    // Init Graph structure
    bubbles = new NetObject[getNetObjectNumber()];
    nbBubble = 0;
    // Extract bubble object from the net file
    for (int i = 0; i < getNetObjectNumber(); i++) {
      if (getNetObjectAt(i).getType() == NetObject.OBJECT_BUBBLE) {
        bubbles[nbBubble] = getNetObjectAt(i);
        // We use a DijkstraInfo object as userValue to compute shortest way
        bubbles[nbBubble].setUserValue(new DijkstraInfo());
        nbBubble++;
      }
    }
    theSource = bubbles[0];
    theDest = bubbles[nbBubble - 1];
    computeDijkstra();

  }

  /** Customized link paiting */
  public void paintLinks(Graphics2D g2) {

    // Paint all links
    super.paintLinks(g2);

    // Paint the shortest path
    if (theDest != null) {
      NetObject dst = theDest;
      g2.setColor(Color.ORANGE);
      while (getDI(dst).pred != null) {
        getDI(dst).pred.paintLink(g2, dst, true);
        dst = getDI(dst).pred;
      }
    }

  }

  /** Returns the distance in pixel between 2 NetObjects */
  private double distance(NetObject n1,NetObject n2) {
    double x = ( n2.getXOrigin() - n1.getXOrigin() );
    double y = ( n2.getYOrigin() - n1.getYOrigin() );
    return Math.sqrt(x*x+y*y);
  }

  /** Helper function to retreive the DijkstraInfo struct of the specified NetObject */
  private DijkstraInfo getDI(NetObject o) {
    return (DijkstraInfo)o.getUserValue();
  }

  /** Initialise direct distance, predecessor and the start set S */
  private Vector initDijkstra() {

    Vector S = new Vector();

    for(int i=0;i<nbBubble;i++) {
      if( theSource.isParentOf(bubbles[i]) ) {
        getDI(bubbles[i]).dist = distance(theSource,bubbles[i]);
        getDI(bubbles[i]).pred = theSource;
      } else {
        getDI(bubbles[i]).dist = Double.POSITIVE_INFINITY;
        getDI(bubbles[i]).pred = null;
      }
      S.add(bubbles[i]);
    }

    getDI(theSource).dist = 0.0;
    S.remove(theSource);

    return S;

  }

  /** Compute Dijkstra minimun distance from the source */
  private void computeDijkstra() {

    int i;
    NetObject s;
    double minDist;

    Vector S = initDijkstra();

    do {

      // Select the minimun distance whithin undone node (S set)
      for( minDist = Double.POSITIVE_INFINITY , s = null , i=0 ; i < S.size() ; i++ ) {
        NetObject toDo = (NetObject)S.get(i);
        if(getDI(toDo).dist < minDist) {
          s = toDo;
          minDist = getDI(toDo).dist;
        }
      }

      // Update the S set and outgoing link distance
      if(s!=null) {
        S.remove(s);
        for(i=0;i < s.getChildrenNumber();i++) {
          NetObject t = s.getChildAt(i);
          double d = distance(s,t);
          if( getDI(s).dist + d < getDI(t).dist ) {
            getDI(t).dist = getDI(s).dist + d;
            getDI(t).pred = s;
          }
        }
      }

    } while(s!=null);

    updateBubble();

  }

  /** Update bubble accroding to Dijkstra result */
  private void updateBubble() {

    for(int i=0;i < nbBubble;i++) {

      if( bubbles[i].equals(theSource) )
        bubbles[i].setColor(Color.BLUE);
      else if ( bubbles[i].equals(theDest) )
        bubbles[i].setColor(Color.CYAN);
      else
        bubbles[i].setColor(Color.GRAY);

      double d = getDI(bubbles[i]).dist;

      if( d==Double.POSITIVE_INFINITY ) {
        // Non reachable node
        bubbles[i].setColor(Color.RED);
        bubbles[i].setCenterLabel("No");
      } else {
        bubbles[i].setCenterLabel(Integer.toString((int)d));
      }

    }
    repaint();

  }

  // -------------------------------------------------------------------
  // The Editor listener
  // -------------------------------------------------------------------
  public void valueChanged(NetEditor src) {

    if( nbBubble>0 ) {
      // Update graph when moving a bubble
      computeDijkstra();
    }

  }

  public void objectClicked(NetEditor src,NetObject obj,MouseEvent e) {

    // Filter bubble
    if( obj.getType()!=NetObject.OBJECT_BUBBLE )
      return;

    // Set source
    if( e.getButton()==MouseEvent.BUTTON1 ) {
      theSource = obj;
      computeDijkstra();
    }

    // Set destination
    if( e.getButton()==MouseEvent.BUTTON3 ) {
      theDest = obj;
      computeDijkstra();
    }

  }

  public void linkClicked(NetEditor src,NetObject obj,int childIdx,MouseEvent e) {}

  public void sizeChanged(NetEditor src,Dimension d) {}

  public void cancelCreate(NetEditor src) {}

}
