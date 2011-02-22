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

/**
 * 24 Bit RGB image format
 */
public class RGB24ImageFormat extends IImageFormat {

  public RGB24ImageFormat() {}

  public int getWidth() {
    if(getHeight()==0) return 0;
    else               return data[0].length/3;
  }

  public String getName() {
    return "RGB24";
  }

  public boolean isColorFormat() {
    return true;
  }

  public int getHistogramWidth() {
    // Return 8bit histogram width because we plot only the green field
    return 256;
  }

  public double getValue(int x,int y) {

    // Return the green field
    int g = data[y][x*3+1] & 0xFF;
    return (double)g;

  }

  public void computeFitting() {
    // Does not compute best fit for color image
  }

  public int getRGB(boolean negative,int[] colormap16,int x,int y) {
    if(!negative) {
      int r = data[y][x*3] & 0xFF;
      int g = data[y][x*3+1] & 0xFF;
      int b = data[y][x*3+2] & 0xFF;
      return (r << 16) + (g << 8) + b;
    } else {
      int r = (~data[y][x*3]) & 0xFF;
      int g = (~data[y][x*3+1]) & 0xFF;
      int b = (~data[y][x*3+2]) & 0xFF;
      return (r << 16) + (g << 8) + b;
    }
  }

  /*
  int srgb = imageData[j][2*i] * 256 + imageData[j][2*i+1];
  int r = (srgb & 0xF800) >> 8;
  int g = (srgb & 0x07E0) >> 3;
  int b = (srgb & 0x001F) << 3;
  rgb[i] = r * 65536 +
           g * 256 +
           b;
  */

}
