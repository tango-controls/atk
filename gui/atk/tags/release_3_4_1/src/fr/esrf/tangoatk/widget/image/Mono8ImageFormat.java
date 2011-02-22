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
 * Monochrome 8bits image format
 */
public class Mono8ImageFormat extends IImageFormat {

  public Mono8ImageFormat() {}

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
