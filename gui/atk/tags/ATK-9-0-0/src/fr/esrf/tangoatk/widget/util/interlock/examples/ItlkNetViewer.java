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
 

import fr.esrf.tangoatk.widget.util.interlock.NetEditor;
import javax.swing.*;
import java.io.IOException;

/** A class which override the ItlkNetEditor to build an Interlock Simulator viewer */
public class ItlkNetViewer extends ItlkNetEditor {

  public ItlkNetViewer(JFrame parent) {

    super(parent);
    setEditable(false);

  }

  /** Overload load file to check the root */
  public void loadFile(String fileName) throws IOException {

    super.loadFile(fileName);
    if( getRoot()==null ) {
      clearObjects();
      throw new IOException("No VCC found in this net file.");
    }

  }

}
