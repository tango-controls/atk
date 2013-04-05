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
 
//+======================================================================
// File: GrayscaleColorConverter.java
//
// Project:   Tango
//
// Description:  java source code for image processing.
//
// $Author$
//
// $Revision$
//
// $Log$
// Revision 1.4  2003/01/29 18:32:08  poncet
// Removed all references to the cvs command from Makefiles.
// Removed the file Makefile~ from src/fr/esrf/tangoatk/widget/dnd/.
//
// Revision 1.3  2002/07/03 14:35:10  assum
// Added new buttonbar framework.
//
// Changed layout of ConvolveFilter
//
// Erik.
//
// Revision 1.2  2002/06/13 11:32:32  assum
// changed a wee bug
//
//
// Copyright 1995 by European Synchrotron Radiation Facility, Grenoble, France
//               All Rights Reversed
//-======================================================================
 
package fr.esrf.tangoatk.widget.image;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import fr.esrf.Tango.*;
import fr.esrf.TangoDs.*;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.image.*;



/**
 *
 * @author  root
 */
public class GrayscaleColorConverter implements IRasterConverter {

    final int	IMAGE_DEPTH = 256;
    final int	INCREASE = 0;
    final int	DECREASE = 1;




    public static final int		_256_COLORS      = 0;
    final int		GRAY_SCALE  = 1;
    final int		CONTRASTED_COLOR = 2;

    public int[][]			raster = null;
    private int[]			colormap;
    private BufferedImage	image;
    private	fr.esrf.tangoatk.widget.image.IImageViewer imgviewer;
	
    private final int	NB_MAIN_COLORS	= 8;
    private final int	BLACK	= 0;
    private final int	BLUE	= 1*IMAGE_DEPTH / NB_MAIN_COLORS;
    private final int	CYAN	= 2*IMAGE_DEPTH / NB_MAIN_COLORS;
    private final int	GREEN	= 3*IMAGE_DEPTH / NB_MAIN_COLORS;
    private final int	YELLOW	= 4*IMAGE_DEPTH / NB_MAIN_COLORS;
    private final int	WHITE	= 5*IMAGE_DEPTH / NB_MAIN_COLORS;
    private final int	MAGENTA	= 6*IMAGE_DEPTH / NB_MAIN_COLORS;
    private final int	RED		= 7*IMAGE_DEPTH / NB_MAIN_COLORS;
    private final int	BG_MAX  = IMAGE_DEPTH/4;	//	Maximum background

    //===========================================================
    //===========================================================
    public GrayscaleColorConverter(int lut_num) {
	colormap = new int[IMAGE_DEPTH];
	switch (lut_num) {
	case _256_COLORS:
	    set256ColorsLookUpTable();
	    break;
	case GRAY_SCALE:
	    setGrayScaleLookUpTable();
	    break;
	}
    }
    //===========================================================
    //===========================================================
    public void changeLookUpTable(int lut_num) {
	switch (lut_num) {
	case _256_COLORS:
	    set256ColorsLookUpTable();
	    break;
	case GRAY_SCALE:
	    setGrayScaleLookUpTable();
	    break;
	}
		
	//	if image already create set it with new LUT
	if (image==null)
	    return;

	for (int y=0 ; y<image.getHeight() ; y++)
	    for (int x=0 ; x<image.getWidth() ; x++)
		image.setRGB(x, y, colormap[raster[x][y]]);
	imgviewer.repaint();
    }
    //===========================================================
    //===========================================================
    private void setGrayScaleLookUpTable() {	
	Color	color;
	for (int i=0 ; i<IMAGE_DEPTH ; i++) {
	    color = new Color(i, i, i);
	    colormap[i] = color.getRGB();
	}
    }
    //===========================================================
    //===========================================================
    private void set256ColorsLookUpTable() {	
	Color	color;
	int		r=0, g=0, b=0;
	//int		colorInc = IMAGE_DEPTH / (BLUE-BLACK);
	int		colorInc = IMAGE_DEPTH / (BLUE-BLACK)/2;
	for (int i=0 ; i<IMAGE_DEPTH ; i++) {
	    if (i==BLACK) {
		r = g = b = 0 ;
	    } else if (i==BLUE) {
		r=0x00 ;
		g=0x7f;
		b=0xff;
	    } else if (i==CYAN) {
		r=0x00 ;
		g=0xff ;
		b=0xff ;
	    } else if (i==GREEN) {
		r=0x7f ;
		g=0xff ;
		b=0x00 ;
	    } else if (i==YELLOW) {
		r=0xff ;
		g=0xff ;
		b=0x7f ;
	    } else if (i==WHITE) {
		r=0xff ;
		g=0xff ;
		b=0xff ;
	    } else if (i==MAGENTA) {
		r=0xff ;
		g=0x00 ;
		b=0xff ;
	    } else if (i==RED) {
		r=0xff ;
		g=0x00 ;
		b=0x00 ;
	    } else if (i<BLUE)	/*  Black -> Blue   */
		b+=colorInc;
	    else if (i<CYAN)	/*  Blue -> Cyan   */
		g+=colorInc ;
	    else if (i<GREEN)	/*  Cyan -> Green */
		b-=colorInc ;
	    else if (i<YELLOW)	/*  Green -> Yellow */
		r+=colorInc ;
	    else if (i<WHITE)	/*  Yellow -> White */
		b+=colorInc ;
	    else if (i<MAGENTA)	/*  White -> Magenta */
		g-=colorInc ;
	    else if (i<RED)	/*  Magenta -> Red  */
		b-=colorInc ;
	    else
		r-=colorInc ; /*  Red -> Black    */
	    //System.out.println(i + ": " + r + ", " + g + ", " + b );
	    color = new Color(r, g, b);
	    colormap[i] = color.getRGB();
	}
    }

    //===========================================================
    //===========================================================
    public void createColormapImage() {
	int width = 100, height = 256;
	raster = new int[width][height];
	image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	for (int y = 0 ; y < height ; y++)
	    for (int x = 0 ; x < width ; x++) {
		raster[x][y] = height - y - 1;
		image.setRGB(x, y, colormap[raster[x][y]]);
	    }
    }
    //===========================================================
    /**
     *	IRasterControl setModel abstract method
     */
    //===========================================================
    public void setModel(IImageViewer iv) {
	imgviewer = iv;
    }

    public void setImageViewer(IImageViewer iv) {
	imgviewer = iv;
    }
    //===========================================================
    /**
     *	IRasterControl rasterChanged abstract method
     */
    //===========================================================

    public BufferedImage convertRaster(double[][] double_raster) {
	createImage(double_raster);
	return image;
    }

    public void rasterChanged(double[][] double_raster) {
	BufferedImage image = convertRaster(double_raster);
	imgviewer.setImage(image);
	imgviewer.setSize(new Dimension(image.getWidth(),
					image.getHeight())); 
    }
    //===========================================================
    /**
     *	Raster already known.
     */
    //===========================================================
    public void rasterChanged() {
	imgviewer.setImage(image);
	imgviewer.setSize(new Dimension(image.getWidth(),
					image.getHeight())); 
    }

    //===========================================================
    //===========================================================
    public void createImage(double[][] double_raster) {
	int	width  = double_raster[0].length;
	int	height = double_raster.length;

	raster = new int[width][height];
	image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	for (int y = 0 ; y < height; y++)
	    for (int x = 0 ; x < width; x++) {
		raster[x][y] = 0xff & (int)(double_raster[y][x]);
		image.setRGB(x, y, colormap[raster[x][y]]);
	    }
    }
    //===========================================================
    //===========================================================
    public void createImage(int[][] int_raster) {
	raster = int_raster;
	int	width  = raster.length;
	int	height = raster[0].length;
	image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	for (int y=0 ; y<height ; y++)
	    for (int x=0 ; x<width ; x++)
		image.setRGB(x, y, colormap[raster[x][y]]);
    }

    private Rectangle	roi = null;
    //===========================================================
    //===========================================================
    private Rectangle calculateROI() {
	int		width  = image.getWidth();
	int		height = image.getHeight();
	Point	p0 = new Point(0, 0);
	Point	p1 = new Point(width-1, height-1);
		
	//	Search max pixel
	int	max  = 0;
	int     xmax = 0;
	int     ymax = 0;
	int	bg   = 0;
	int	idx  = 0;
	int	z = 0;
	for (int y=5 ; y < height ; y++)
	    for (int x=5 ; x < width ; x++) {
		z = 0xff & (int)raster[x][y];
		if (z > max) {
		    max  = z;
		    xmax = x;
		    ymax = y;
		}
		//	get max of background in first and last line
		if (y == 0 || y == height - 1 || x == 0 || x == width - 1)
		    if (z > bg && z < BG_MAX)
			bg = z;
	    }

	System.out.println(xmax + ", " + ymax + " -> " + max + " Bg = " + bg);
	p0.x = p0.y - 0;
	p1.x = width-1;
	p1.y = height -1;

	//	Search in each direction, whre back ground begins
	for (int x=xmax ; x>0 ; x--)
	    if ((z=0xff & raster[x][ymax]) <= bg) {
		p0.x = x;
		break;
	    }

	for (int x=xmax ; x<width ; x++)
	    if ((z=0xff & raster[x][ymax]) <= bg) {
		p1.x = x;
		break;
	    }

	for (int y=ymax ; y>0 ; y--)
	    if ((z=0xff & raster[xmax][y]) <= bg) {
		p0.y = y;
		break;
	    }
	for (int y=ymax ; y<height ; y++)
	    if ((z=0xff & raster[xmax][y]) <= bg) {
		p1.y = y;
		break;
	    }
	return new Rectangle(p0, new Dimension(	p1.x -p0.x, p1.y-p0.y));
    }
	
	
    //===========================================================
    /**
     *	Draw rectangle
     */
    //===========================================================
    public void drawROI(boolean calculate)
    {
	if (calculate)
	    roi = calculateROI();

	//	Draw ROI	
	Graphics	g = image.getGraphics();
	g.setXORMode(Color.blue);
	g.drawRect(roi.x, roi.y, roi.width, roi.height);
    }
}
