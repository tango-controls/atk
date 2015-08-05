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
 
import fr.esrf.tangoatk.widget.util.jdraw.JDrawEditor;
import fr.esrf.tangoatk.widget.util.jdraw.JDLabel;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;

import javax.swing.*;

public class HelloWorld extends JFrame {

  JDrawEditor theGraph;
  JDLabel     label;

  public HelloWorld() {
    // Creates a JDrawEditor in MODE_PLAY.
    theGraph = new JDrawEditor(JDrawEditor.MODE_PLAY);
    // Creates a JDLabel
    label = new JDLabel("myLabel","Hello World",5,5);
    // Adds the label to the editor.
    theGraph.addObject(label);
    // Sizes the editor according to the size of the drawing.
    theGraph.computePreferredSize();

    setContentPane(theGraph);
  }

  public static void main(String[] args) {
    final HelloWorld hw = new HelloWorld();
    ATKGraphicsUtils.centerFrameOnScreen(hw);
    hw.setVisible(true);
  }

}
