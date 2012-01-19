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
 
// File:          IImageViewer.java
// Created:       2002-05-31 15:28:14, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-05 16:5:35, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.image;

import java.awt.image.BufferedImage;

public interface IImageViewer {

    public javax.swing.JPanel getTopLayer();

    /**
     * <code>setRasterConverter</code> sets the object that is
     * responsible for transforming a <code>double [][]</code> into a
     * <code>BufferdImage</code>
     *
     * @param converter an <code>IRasterConverter</code> value
     */
    public void setRasterConverter(IRasterConverter converter);

    public IRasterConverter getRasterConverter();

    public BufferedImage getImage();

    public void setImage(BufferedImage image);

    /**
     * <code>repaint</code> this method causes the Image to be repainted
     *
     */
    public void repaint();

    /**
     * <code>setSize</code> sets the size of the image.
     *
     * @param size a <code>java.awt.Dimension</code> value
     */
    public void setSize(java.awt.Dimension size);

    public java.awt.Dimension getSize();


    public double[][] getRaster();
    
    public boolean isRasterChanged();

    public void setRaster(double[][] raster);


    public void addImagePanel(IImagePanel panel);

    /**
     * <code>addImageManipulator</code> adds a manipulator to this
     * IImageViewer. The manipulators <code>draw(BufferdImage image>
     * is called each time the Image is being repainted, so that they
     * can do whatever imagemanipulation they want.
     *
     * @param listener an <code>IImageManipulator</code> value
     * @see IImageManipulator
     */
    public void addImageManipulator(IImageManipulator listener);

    
    /**
     * <code>removeImageManipulator</code> removes a given
     * <code>IImageManipulator</code> from this IImageViewer
     *
     * @param listener an <code>IImageManipulator</code> value
     * @see IImageManipulator
     */
    public void removeImageManipulator(IImageManipulator listener);

}
