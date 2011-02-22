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
 * Monochrome 8bits image format
 */
public class Mono8ImageFormat extends IImageFormat {

  public Mono8ImageFormat() {}

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
    data = new byte[height][width];
    for(int j=0;j<height;j++) {
      for(int i=0;i<width;i++) {
        data[j][i] = rawData[idx++];
      }
    }

  }

  public int getWidth() {
    if(getHeight()==0) return 0;
    else               return data[0].length;
  }

  public String getName() {
    return "MONO8";
  }

  public boolean isColorFormat() {
    return false;
  }

  public int getHistogramWidth() {
    // Return 8bit histogram width because we plot only the green field
    return 256;
  }

  public double getValue(int x,int y) {

    int v = data[y][x] & 0xFF;
    return (double)v;

  }

  public String getValueStr(int x,int y) {
    return Double.toString(getValue(x,y));
  }

  public void computeFitting() {

    // Scale to 16bit for the colormap

    if (!bestFit) {

      tool.getAxis().setMinimum(-bfA0);
      tool.getAxis().setMaximum(-bfA0 + (1.0/bfA1)*65536.0);

    } else {

      int i, j;
      double autoBfMin = 65536.0;
      double autoBfMax = 0.0;

      for (j = 0; j < data.length; j++)
        for (i = 0; i < data[j].length; i++) {
          int uc = data[j][i] & 0xFF;
          double v = (double)uc;
          if (v > autoBfMax) autoBfMax = v;
          if (v < autoBfMin) autoBfMin = v;
        }

      bfA0 = -autoBfMin;

      if (autoBfMax == autoBfMin) {
        // Uniform picture
        bfA1 = 0.0;
        tool.getAxis().setMinimum(autoBfMin);
        tool.getAxis().setMaximum(autoBfMax+1.0);
      } else {
        bfA1 = (65536.0) / (autoBfMax - autoBfMin);
        tool.getAxis().setMinimum(autoBfMin);
        tool.getAxis().setMaximum(autoBfMax);
      }

    }

  }

  private int bestFit(byte b) {

    int v = b & 0xFF; // Unsigned
    int nv = (int) ((bfA0 + v) * bfA1);
    if (nv < 0) return 0;
    if (nv > 65535) return 65535;
    return nv;

  }

  public int getRGB(boolean negative,int[] colormap16,int x,int y) {

    if(negative) {
      return colormap16[(~bestFit(data[y][x])) & 65535];
    } else {
      return colormap16[bestFit(data[y][x])];
    }

  }

}
