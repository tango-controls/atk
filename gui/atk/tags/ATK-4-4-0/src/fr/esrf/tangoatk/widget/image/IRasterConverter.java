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
 
 // File:          IRasterConverter.java
// Created:       2002-06-12 13:21:43, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-08 16:42:31, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.image;
import java.awt.image.BufferedImage;

/**
 * <code>IRasterConverter</code> is an interface which specifies the
 * methods to be used to convert a <code>double [][]</code> to a
 * <code>BufferedImage</code>. A class implementing this interface is
 * given to an IImageViewer through the <code>setController</code>
 * method in that interface. Each time the IImageViewer
 * <code>setRaster</code> is called, the <code>convertRaster</code>
 * method in <code>IRasterConverter</code> is called so that it can
 * convert the <code>double[][]</code> to a <code>BufferdImage</code>
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Version$
 */
public interface IRasterConverter {
	
    /**
     * <code>convertRaster</code> is called from the IImageViewer. It
     * is responsible for changing a <code>double[][]</code> into a
     * <code>BufferedImage</code>
     * Oh, and by the way, the first dimension is the y-axis, and
     * the second is the x-axis:
     * <code>
     * double point;<br>
     * for (int y = 0; y < raster.length; y++)<br>
     *    for (int x = 0; x < raster[y].length; x++)<br>
     *        point = raster[y][x]; <br>
     * </code>
     * @param raster a <code>double[][]</code> value
     */
    public BufferedImage convertRaster(double [][] raster);

    /**
     * <code>setImageViewer</code> is called by the IImageViewer that
     * this IRasterConverter controls. It is called so that the class
     * implementing IRasterConverter can gain access to its
     * IImageViewer.
     *
     * @param viewer an <code>IImageViewer</code> value
     */
    public void setImageViewer(IImageViewer viewer);

}
