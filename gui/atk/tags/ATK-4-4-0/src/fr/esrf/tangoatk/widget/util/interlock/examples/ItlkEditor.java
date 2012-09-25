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
import java.awt.event.*;

/** The main Interlock editor frame */
public class ItlkEditor extends NetEditorFrame {

  // The Interlock editor component
  ItlkNetEditor  itlkEditor;

  // New menu items
  JMenuItem traceMode;
  JMenuItem editMode;

  // New toolbar items
  JButton bubbleBtn;
  JButton permitBtn;
  JButton joinBtn;
  JButton vccBtn;
  JButton groundBtn;

  // ---------------------------------------------------------------
  // Construction
  // ---------------------------------------------------------------
  public ItlkEditor() {

    setAppTitle("Interlock Simulator Editor");

    // Create the editor -------------------------------------------
    itlkEditor = new ItlkNetEditor(this);
    setEditor(itlkEditor);

    // Customize options menu ---------------------------------------
    traceMode = NetUtils.createMenuItem("Trace mode",0,0,this);
    editMode = NetUtils.createMenuItem("Edit mode",0,0,this);
    getOptionMenu().add(traceMode,0);
    getOptionMenu().add(editMode,1);
    getOptionMenu().add(new JSeparator(),2);

    // Cutomize toollbar --------------------------------------------
    JToolBar tb = getToolbar();
    tb.remove(getToobarButton(NetEditorFrame.TOOL_BUBBLE));
    String rPth = "/fr/esrf/tangoatk/widget/util/interlock/gif/";
    bubbleBtn = createIconButton(rPth,"bubble","Create an interlock object (physical switch)",this);
    tb.add(bubbleBtn,0);
    permitBtn = createIconButton(rPth,"permit","Create a permit object (logical sensor)",this);
    tb.add(permitBtn,1);
    joinBtn = createIconButton(rPth,"join","Create a join object (intersection point)",this);
    tb.add(joinBtn,2);
    vccBtn = createIconButton(rPth,"start","Create a VCC object",this);
    tb.add(vccBtn,3);
    groundBtn = createIconButton(rPth,"end","Create a Ground object",this);
    tb.add(groundBtn,4);

  }

  public void objectClicked(NetEditor src,NetObject obj,MouseEvent e) {

    itlkEditor.swapItlkState(obj);

  }

  public void actionPerformed(ActionEvent e) {

    Object src = e.getSource();
    if( src == bubbleBtn ) {
      itlkEditor.setCreateMode(NetEditor.CREATE_BUBBLE,ItlkNetEditor.ITLK_BUBBLE);
      getHelpLabel().setText("Interlock creation: Left click to create a interlock object, right click to cancel.");
    } else if (src == permitBtn ) {
      itlkEditor.setCreateMode(NetEditor.CREATE_BUBBLE,ItlkNetEditor.SENSOR_BUBBLE);
      getHelpLabel().setText("Sensor creation: Left click to create a sensor object, right click to cancel.");
    } else if (src == joinBtn ) {
      itlkEditor.setCreateMode(NetEditor.CREATE_BUBBLE,ItlkNetEditor.JOIN_BUBBLE);
      getHelpLabel().setText("Join creation: Left click to create a join object, right click to cancel.");
    } else if (src == vccBtn ) {
      itlkEditor.setCreateMode(NetEditor.CREATE_BUBBLE,ItlkNetEditor.VCC_BUBBLE);
      getHelpLabel().setText("VCC creation: Left click to create a VCC object, right click to cancel.");
    } else if (src == groundBtn ) {
      itlkEditor.setCreateMode(NetEditor.CREATE_BUBBLE,ItlkNetEditor.GROUND_BUBBLE);
      getHelpLabel().setText("Ground creation: Left click to create a ground object, right click to cancel.");
    } else if (src == traceMode) {
      itlkEditor.setEditable(false);
    } else if (src == editMode) {
      itlkEditor.setEditable(true);
    } else {
      super.actionPerformed(e);
    }

  }

  // -----------------------------------------------------
  // Main function
  // -----------------------------------------------------
  public static void main(String[] args) {

    ItlkEditor iE = new ItlkEditor();
    iE.pack();
    iE.setVisible(true);

  }

}
