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
 
// File:          RasterControl.java
// Created:       2002-06-06 12:45:08, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-12 15:14:50, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.image;
import java.awt.image.*;
import java.awt.Color;

public class BWRaster implements IRasterConverter  {


    IImageViewer model;

    public void setImageViewer(IImageViewer viewer) {
	model = viewer;
    }

    public BufferedImage convertRaster(double [][] raster) {
	int width, height;
	if (raster == null || raster[0] == null) return null;
	width = raster[0].length;
	height = raster.length;
	BufferedImage image =
	    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	model.setSize(new java.awt.Dimension(width, height));

	int color, val;
	for (int y = 0; y < height; y++) {
	    for (int x = 0; x < width; x++ ) {
		val = (int)raster[y][x];
		image.setRGB(x, y, new Color(val, val, val).getRGB());
	    } // end of for ()
	} // end of for ()
	model.setSize(new java.awt.Dimension(width, height));

	return image;
    }
	
    public void roiChanged(int startx, int endx, int starty, int endy) {

    }

}
