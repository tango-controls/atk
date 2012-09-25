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
 * 24 Bit RGB image format
 */
public class RGB24ImageFormat extends IImageFormat {

  public RGB24ImageFormat() {}
  
  public void setData(byte[] rawData) throws IOException {

    // Get width and height
    int wh = (rawData[0] & 0xFF);
    int wl = (rawData[1] & 0xFF);
    wh = wh << 8;
    int width = wh | wl;

    int hh = (rawData[2] & 0xFF);
    int hl = (rawData[3] & 0xFF);
    hh = hh << 8;
    int height = hh | hl;

    // Convert data
    int idx = 4;
    data = new byte[height][width*3];
    for(int j=0;j<height;j++) {
      for(int i=0;i<width;i++) {
        data[j][i*3+2] = rawData[idx++];
        data[j][i*3+1] = rawData[idx++];
        data[j][i*3+0] = rawData[idx++];
      }
    }

  }

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

  public String getValueStr(int x,int y) {

    int r = data[y][x*3+2] & 0xFF;
    int g = data[y][x*3+1] & 0xFF;
    int b = data[y][x*3+0] & 0xFF;
    return "(" + r + "," + g + "," + b + ")";

  }

  public void computeFitting() {
    // Does not compute best fit for color image
  }

  public int getRGB(boolean negative,int[] colormap16,int x,int y) {
    if(!negative) {
      int r = data[y][x*3+2] & 0xFF;
      int g = data[y][x*3+1] & 0xFF;
      int b = data[y][x*3+0] & 0xFF;
      return (r << 16) + (g << 8) + b;
    } else {
      int r = (~data[y][x*3+2]) & 0xFF;
      int g = (~data[y][x*3+1]) & 0xFF;
      int b = (~data[y][x*3+0]) & 0xFF;
      return (r << 16) + (g << 8) + b;
    }
  }

}
