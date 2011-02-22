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
import javax.swing.*;
import java.io.IOException;
import java.awt.*;

public class Dijkstra extends JFrame  {

  DjNetViewer  netViewer;   // The viewer

  /** Main constructor */
  public Dijkstra() {

    // Construct the viewer
    netViewer = new DjNetViewer(this);

    // Load the network file
    try {
      netViewer.loadFile("dijkstra.net");
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Cannot load dijkstra.net\n" + ex.getMessage() , "Error", JOptionPane.ERROR_MESSAGE);
    }

    setTitle("Dijkstra");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setContentPane(netViewer);
    pack();

  }

  /** Main function */
  public static void main(String[] args) {

    final Dijkstra f = new Dijkstra();
    f.setVisible(true);

  }

}