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

import fr.esrf.tangoatk.widget.util.interlock.NetEditorListener;
import fr.esrf.tangoatk.widget.util.interlock.NetEditor;
import fr.esrf.tangoatk.widget.util.interlock.NetObject;

import javax.swing.*;
import java.io.IOException;
import java.awt.event.MouseEvent;
import java.awt.*;

public class ItlkViewer extends JFrame implements NetEditorListener {

  ItlkNetViewer  itlkNetViewer;   // The viewer

  /** Main constructor */
  public ItlkViewer() {

    // Construct the viewer
    itlkNetViewer = new ItlkNetViewer(this);
    itlkNetViewer.addEditorListener(this);

    // Load the network file
    try {
      itlkNetViewer.loadFile("interlock.net");
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(this, "Cannot load interlock.net\n" + ex.getMessage() , "Error", JOptionPane.ERROR_MESSAGE);
    }

    setTitle("Interlock Simulator");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setContentPane(itlkNetViewer);
    pack();

  }
  
  // -------------------------------------------------------------------
  // The Editor listener
  // -------------------------------------------------------------------
  public void valueChanged(NetEditor src) {}
  public void sizeChanged(NetEditor src,Dimension d) {}
  public void cancelCreate(NetEditor src) {}
  public void linkClicked(NetEditor src,NetObject obj,int childIdx,MouseEvent e) {}
  public void objectClicked(NetEditor src,NetObject obj,MouseEvent e) {
    itlkNetViewer.swapItlkState(obj);
  }

  /** Main function */
  public static void main(String[] args) {

    final ItlkViewer f = new ItlkViewer();
    f.setVisible(true);

  }

}