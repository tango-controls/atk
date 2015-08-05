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
 
package fr.esrf.tangoatk.widget.command;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import javax.swing.*;
import javax.swing.event.*;

public class CommandList extends JList {

    private void initComponents()
    {
	setLayout(new BorderLayout());
	addMouseListener(new _cls1());
    }

    private void formMouseClicked(MouseEvent mouseevent) {
	int i = locationToIndex(mouseevent.getPoint());
	System.out.println("asking viewerList(" + i + ") to show...");
	fireSelectionValueChanged(i, i, false);
    }

    public CommandList() {
	initComponents();
    }


    private class _cls1 extends MouseAdapter {

	public void mouseClicked(MouseEvent mouseevent) {
	    formMouseClicked(mouseevent);
	}

	private final void constructor$0(CommandList commandlist) {
	}

	_cls1() {
	    constructor$0(CommandList.this);
	}
    }

}
