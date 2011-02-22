// File:          IImageViewer.java
// Created:       2002-05-31 15:28:14, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-17 11:30:11, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.image;

import java.awt.image.BufferedImage;

public interface IImageViewer {

    public javax.swing.JPanel getTopLayer();

    /**
     * <code>setRasterControl</code> sets the class which is responsible of
     * changing a <code>double [][]</code> into a 
     * @param control an <code>IRasterControl</code> value
     * @deprecated use setRasterConverter(IRasterConverter converter)
     */
    public void setRasterControl(IRasterControl control);

    /**
     * <code>setRasterConverter</code> sets the object that is
     * responsible for transforming a <code>double [][]</code> into a
     * <code>BufferdImage</code>
     *
     * @param converter an <code>IRasterConverter</code> value
     */
    public void setRasterConverter(IRasterConverter converter);

    public IRasterConverter getRasterConverter();

    /**
     * <code>getRasterControl</code>
     *
     * @return an <code>IRasterControl</code> value
     * @deprecated you should use the IRasterConverter methods instead.
     */
    public IRasterControl getRasterControl();

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
