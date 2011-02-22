// File:          IImageControl.java
// Created:       2002-06-05 10:19:28, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-10 15:31:28, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.image;
import java.awt.image.BufferedImage;

/**
 * <code>IImageControl</code>
 *
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version 1.0
 * @deprecated use IImageManipulator instead!
 */
public interface IImageControl extends java.util.EventListener {

    /**
     * <code>setModel</code>
     *
     * @param viewer an <code>IImageViewer</code> value
     * @deprecated please use setImageViewer instead
     */
    public void setModel(IImageViewer viewer);

    public void setImageViewer(IImageViewer viewer);
    
    public void draw(BufferedImage image);

    public void roiChanged(int startx, int endx, int starty, int endy);
}
