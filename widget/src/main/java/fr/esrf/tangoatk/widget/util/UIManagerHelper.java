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
 
// File:          UIManagerHelper.java<2>
// Created:       2002-02-12 13:52:20, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-19 11:30:16, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import java.awt.*;
public class UIManagerHelper {


    public static void setFont(String thingyName, JComponent thingy) {
	Font f = UIManager.getFont(thingyName + ".Font");
	if (f == null) return;

	thingy.setFont(f);
    }

    public static void setForeground(String thingyName, JComponent thingy) {
	Color c = UIManager.getColor(thingyName + ".Foreground");
	if (c == null) return;

	thingy.setForeground(c);
    }

    public static void setBackground(String thingyName, JComponent thingy) {
	Color c = UIManager.getColor(thingyName + ".Background");
	if (c == null) return;

	thingy.setBackground(c);
    }

    public static void setSize(String thingyName, JComponent thingy) {
	Dimension d = UIManager.getDimension(thingyName + ".Size");
	if (d == null) return;

	thingy.setPreferredSize(d);
    }

    public static void setAll(String thingyName, JComponent thingy) {
	setSize(thingyName, thingy);
	setFont(thingyName, thingy);
	setForeground(thingyName, thingy);
	setBackground(thingyName, thingy);
    }

    public static Dimension getRequiredSize(FontMetrics fm, int characters) {
	int advance = fm.getMaxAdvance();
	Dimension dim;
	if (advance != -1) {
	    dim = new Dimension(advance * characters, fm.getHeight());
	    return dim;
	}
	
	int [] widths = fm.getWidths();
	int max = 0;

	for (int i = 0; i < widths.length; i++) {
	    if (widths[i] < max) continue;
	    max = widths[i];
	} 

	dim = new Dimension(max * characters, fm.getHeight());
	    
	return dim;
    }
}
