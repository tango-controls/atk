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
 
// File:          RoiControlPanel.java
// Created:       2002-06-06 11:08:19, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-05 16:5:35, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.image;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
public class RoiControlPanel extends JPanel implements IImageManipulator, IImagePanel {

    int sx = 0, sy = 0, ex = 0, ey = 0;

    int i = 0;
    IImageViewer model;
    JPanel top;
    Graphics graphics;

    public void setModel(IImageViewer viewer) {
	setImageViewer(viewer);
    }

    public void setImageViewer(IImageViewer viewer) {
	viewer.addImageManipulator(this);
	model = viewer;
	top = model.getTopLayer();

    }
    
    public String getName() {
	return "Roi Control";
    }

    public JComponent getComponent() {
	return this;
    }

    public BufferedImage filter(BufferedImage i) {
	graphics = i.getGraphics();
	graphics.setColor(Color.red);
	graphics.drawRect(sx, sy, ex - sx, ey - sy);
	return i;
    }

    public void ok() {
	getRootPane().getParent().setVisible(false);
    }
    
    public void roiChanged(int startx, int endx, int starty, int endy) {

	
	sx = startx;
	ex = endx;
	sy = starty;
	ey = endy;

	if (++i > 20 ) {
	    i = 0;
	    top.repaint();
	}

    }
}
