// File:          IRasterControl.java
// Created:       2002-06-06 13:12:50, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-12 13:23:2, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.image;
import java.awt.image.BufferedImage;

/**
 * <code>IRasterControl</code> is an interface which specifies the
 * methods to be used to convert a <code>double [][]</code> to a
 * <code>BufferedImage</code>. A class implementing this interface is
 * given to an IImageViewer through the <code>setController</code>
 * method in that interface. Each time the IImageViewer
 * <code>setRaster</code> is called, the <code>convertRaster</code>
 * method in <code>IRasterControl</code> is called so that it can convert the <code>double[][]</code> to a <code>BufferdImage</code>
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Version$
 * @deprecated use IRasterConverter instead
 */
public interface IRasterControl {


    /**
     * <code>rasterChanged</code>
     *
     * @param raster a <code>double[][]</code> value
     * @deprecated use convertRaster instead.
     */
    public void rasterChanged(double [][] raster);
	
    /**
     * <code>convertRaster</code> is called from the IImageViewer. It
     * is responsible for changing a <code>double[][]</code> into a
     * <code>BufferedImage</code>
     *
     * @param raster a <code>double[][]</code> value
     */
    public BufferedImage convertRaster(double [][] raster);

    /**
     * <code>setModel</code>
     *
     * @param model an <code>IImageViewer</code> value
     * @deprecated use setImageViewer instead;
     */
    public void setModel(IImageViewer model);

    /**
     * <code>setImageViewer</code> is called by the IImageViewer that
     * this IRasterControl controls. It is called so that the class
     * implementing IRasterControl can gain access to its
     * IImageViewer.
     *
     * @param viewer an <code>IImageViewer</code> value
     */
    public void setImageViewer(IImageViewer viewer);

}
