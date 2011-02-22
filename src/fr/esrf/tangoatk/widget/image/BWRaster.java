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
