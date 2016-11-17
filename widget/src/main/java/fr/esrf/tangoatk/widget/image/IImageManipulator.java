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
 
// File:          IImageManipulator.java
// Created:       2002-06-10 15:31:35, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-08 16:36:21, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.image;
import java.awt.image.BufferedImage;

/**
 * <code>IImageManipulator</code> is an interface which specifies the behaviour of objects used to manipulate <code>BufferedImage</code>s. Normally a <code>IImageManipulator</code>s <code>draw</code> method is called each time a <code>IImageViewer.repaint</code> is called. The <code>draw</code> method is passed an <code>BufferdImage</code> which it can manipulate in whatever way it wants.
 *
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Version$
 */
public interface IImageManipulator extends java.util.EventListener {

    /**
     * <code>setModel</code>
     *
     * @param viewer an <code>IImageViewer</code> value
     * @deprecated please use setImageViewer instead
     */
    public void setModel(IImageViewer viewer);

    /**
     * <code>setImageViewer</code> is called from an IImageViewer when 
     * the IImageViewer receives an <code>addImageManipulator</code> call.
     * This gives you access to the IImageViewer this controller is
     * controlling.
     * @param viewer an <code>IImageViewer</code> value
     */
    public void setImageViewer(IImageViewer viewer);

    /**
     * <code>filter</code> is called on each repaint from the IImageViewer.
     * You can manipulate the image in any way you want.
     * @param image a <code>BufferedImage</code> value
     */
    public BufferedImage filter(BufferedImage image);

    /**
     * <code>roiChanged</code> is called each time the roi of the image
     * is called.
     * @param startx an <code>int</code> value
     * @param endx an <code>int</code> value
     * @param starty an <code>int</code> value
     * @param endy an <code>int</code> value
     */
    public void roiChanged(int startx, int endx, int starty, int endy);
}
