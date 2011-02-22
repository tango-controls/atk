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
 
package fr.esrf.tangoatk.widget.image;

import fr.esrf.tangoatk.widget.util.JGradientViewer;

import java.io.IOException;

/**
 * An abstract class for image format (Used by the RawImageViewer).
 */
public abstract class IImageFormat {

  final static byte[][] zImg = new byte[0][0];

  byte[][] data = zImg;  // Handle to data
  double bfA0;           // Fitting offset
  double bfA1;           // Fitting factor
  boolean bestFit;       // Automatic best fit enabled
  JGradientViewer tool;  // Gradient tool

  /**
   * Sets and decode the data.
   * @param rawData Pointer to image data
   */
  public abstract void setData(byte[] rawData) throws IOException;

  /**
   * Init static variable.
   * @param gradTool Gradient viewer
   */
  public void initDefault(JGradientViewer gradTool) {

    tool = gradTool;
    bestFit = false;
    bfA0 = 0.0;
    bfA1 = 1.0;

  }

  /**
   * Returns true if the image has a null size, false otherwise
   */
  public boolean isNull() {
    int s = getWidth()*getHeight();
    return (s==0);
  }

  /**
   * Set manual fitting parameters
   * @param bestFit Enable automatic fitting
   * @param min minumun fitting value (0..100%)
   * @param max maximum fitting value (0..100%)
   */
  public boolean setFitting(boolean bestFit,double min,double max) {

    // Check bounds
    if(min>=max || min<0.0 || min>100.0 || max<0.0 || max>100.0) {
      // invalid values
      return false;
    }

    this.bestFit = bestFit;

    // Compute fitting parameters (overrided when bestFit enabled)
    double scale = (double)(getHistogramWidth());
    double mi    = min/100.0;
    double ma    = max/100.0;

    // We always scale to a 16Bit colormap
    bfA0 = - mi * scale;
    bfA1 = 65536.0 / (scale * (ma-mi));

    return true;

  }

  /**
   * Returns the width of the image depending on the format.
   */
  public abstract int getWidth();

  /**
   * Returns the height of the image depending on the format.
   */
  public int getHeight() {
    return data.length;
  }

  /**
   * Returns true if this format is a color format, false otherwise.
   */
  public abstract boolean isColorFormat();

  /**
   * Returns the format name.
   */
  public abstract String getName();

  /**
   * Gets the pixel value as double (Used for table and profile display).
   * @param x horizontal coordinate
   * @param y vertical coordinate
   */
  public abstract double getValue(int x,int y);

  /**
   * Gets the pixel value as string (Used for pixel info display).
   * @param x horizontal coordinate
   * @param y vertical coordinate
   */
  public abstract String getValueStr(int x,int y);

  /**
   * Returns the histogram width.
   * (The number of possible value for a pixel)
   */
  public abstract int getHistogramWidth();

  /**
   * Compute the fitting bounds (Monochrome only)
   */
  public abstract void computeFitting();

  /**
   * Returns the pixel at the specifed pos
   * @param x horizontal coordinate
   * @param y vertical coordinate
   * @param negative Negative flag
   * @param colormap16 16Bit colormap (Monochrome only)
   */
  public abstract int getRGB(boolean negative,int[] colormap16,int x,int y);


}
