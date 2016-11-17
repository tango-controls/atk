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
 
package fr.esrf.tangoatk.widget.util.chart;

import fr.esrf.tangoatk.widget.util.JTableRow;

import javax.swing.*;
import java.awt.*;

/**
 * Table dialog.
 */
public class JLTable extends JFrame {

  JTableRow theTable;

  /**
   * Construction
   */
  public JLTable() {

    theTable = new JTableRow();
    setContentPane(theTable);
    setTitle("Graph data");

  }

  /**
   * Sets the data.
   * @param data Handle to data array.
   * @param colNames Name of columns
   */
  public void setData(Object[][] data, String[] colNames) {
    theTable.setData(data,colNames);
  }

  /**
   * Clear the table
   */
  public void clearData() {
    theTable.clearData();
  }

  // Center the window
  public void centerWindow() {

    theTable.adjustColumnSize();
    theTable.adjustSize();

    // Center the frame and saturate to 800*600
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    Dimension scrsize = toolkit.getScreenSize();
    pack();
    Dimension appsize = getPreferredSize();
    if( appsize.height>600 ) {
      appsize.height=600;
      if(appsize.width<800) {
        // When we saturate the height
        // it is better to reserver space for
        // the vertical scrollbar
        appsize.width += 16;
      }
    }
    if( appsize.width>800 ) appsize.width=800;

    int x = (scrsize.width - appsize.width) / 2;
    int y = (scrsize.height - appsize.height) / 2;
    setBounds(x, y, appsize.width, appsize.height);

  }

}