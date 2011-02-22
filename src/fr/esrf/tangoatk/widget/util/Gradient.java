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
 
package fr.esrf.tangoatk.widget.util;

import java.awt.*;

/**
 * Gradient class
 */
public class Gradient {

  private Color[]  colorVal=null;
  private double[] colorPos=null;

  /**
   * Construct a default gradient black to white
   */
  public Gradient() {

    colorVal = new Color[2];
    colorPos = new double[2];
    colorVal[0] = new Color(0,0,0);
    colorVal[1] = new Color(255,255,255);
    colorPos[0] = 0.0;
    colorPos[1] = 1.0;

  }

  /**
   * Please use buildRainbowGradient() instead.
   * @deprecated
   */
  public void buidColorGradient() {
    buildRainbowGradient();
  }

  /**
   * Construct a rainbow gradient.
   */
  public void buildRainbowGradient() {

    colorVal = new Color[5];
    colorPos = new double[5];

    colorVal[0] = new Color(200,0,250); //Purple
    colorVal[1] = new Color(40,40,255);   //Blue
    colorVal[2] = new Color(40,255,40);   //Green
    colorVal[3] = new Color(250,250,0); //Yellow
    colorVal[4] = new Color(255,0,0);   //Red

    colorPos[0] = 0.0;
    colorPos[1] = 0.25;
    colorPos[2] = 0.50;
    colorPos[3] = 0.75;
    colorPos[4] = 1.0;

  }

  /**
   * Invert color entries.
   */
  public void invertGradient() {

    Color[] nColorVal = new Color[colorVal.length];
    for(int i=0;i<colorVal.length;i++)
      nColorVal[i] = colorVal[colorVal.length-i-1];
    System.arraycopy(nColorVal, 0, colorVal, 0, colorVal.length);
    
  }

/**
 * Returns a copy of this gradient
 * @return New gradient object
 */
  public Gradient cloneMe() {

    Gradient nG = new Gradient();

    nG.colorPos = new double[getEntryNumber()];
    nG.colorVal = new Color[getEntryNumber()];

    for(int i=0;i<getEntryNumber();i++) {
      nG.colorPos[i] = colorPos[i];
      nG.colorVal[i] = new Color(colorVal[i].getRGB());
    }

    return nG;
  }
  /**
   * Build a color map for this gradient.
   * @param nb Number of color for the colormap
   * @return a nb 32Bit [ARGB] array, null if fails
   */
  public int[] buildColorMap(int nb) {

    if( colorVal==null )
      return null;

    if( colorVal.length<=1 )
      return null;

    int    colId;

    colId=0;
    int[] ret = new int[nb];

    for(int i=0;i<nb;i++) {

      double r1,g1,b1;
      double r2,g2,b2;

      double r = (double)i / (double)nb;
      if(colId<(colorPos.length-2) && r>=colorPos[colId+1] ) colId++;

      r1 = (double) colorVal[colId].getRed();
      g1 = (double) colorVal[colId].getGreen();
      b1 = (double) colorVal[colId].getBlue();

      r2 = (double) colorVal[colId+1].getRed();
      g2 = (double) colorVal[colId+1].getGreen();
      b2 = (double) colorVal[colId+1].getBlue();

      double rr = (r-colorPos[colId])/(colorPos[colId+1]-colorPos[colId]);
      if(rr<0.0) rr=0.0;
      if(rr>1.0) rr=1.0;

      ret[i] = (int)( r1 + (r2-r1)*rr ) * 65536 +
               (int)( g1 + (g2-g1)*rr ) * 256 +
               (int)( b1 + (b2-b1)*rr );

    }

    return ret;

  }

  /**
   * Returns number of color entry.
   * @return Number of entry
   */
  public int getEntryNumber() {
    return colorVal.length;
  }

  /**
   * Returns color information for the specified entry
   * @param id Entry id
   * @return Color value
   */
  public Color getColorAt(int id) {
    return colorVal[id];
  }

  /**
   * Returns the specified pos for the specified entry
   * @param id Entry id
   * @return A floating point between 0.0 and 1.0
   */
  public double getPosAt(int id) {
    return colorPos[id];
  }

  /**
   * Sets the color for a specified entry.
   * @param id Entry id
   * @param c New color value
   */
  public void setColorAt(int id,Color c) {
    if( id>=0 && id<colorVal.length ) {
      colorVal[id]=c;
    }
  }

  /**
   * Sets the position for a specified id.
   * Position value must be greater that the previous pos and
   * lower than the next pos. It must also be in the
   * range 0.0 => 1.0. If those conditions are
   * not validated , no change happens.
   * @param id Entry id
   * @param pos New position value
   */
  public void setPosAt(int id,double pos) {
    if( id>0 && id<(colorVal.length-1) ) {
      if( pos>=0.0 && pos<=1.0 ) {
        if(id==0) {
          if( pos<colorPos[id+1] )
            colorPos[id]=pos;
        } else if (id==colorVal.length-1) {
          if( pos>colorPos[id-1] )
            colorPos[id]=pos;
        } else {
          if( pos<colorPos[id+1] && pos>colorPos[id-1] )
            colorPos[id]=pos;
        }
      }
    }
  }

  /**
   * Adds a color,pos entry to the gradient
   * Note that you have by default 2 entries at 0.0 and 1.0
   * @param c Color value
   * @param pos Position [0.0 => 1.0]
   * @return Entry id (-1 when fails)
   */
  public int addEntry(Color c,double pos) {

    boolean found;
    int i;

    if( pos<=0.0 || pos>=1.0 )
      return -1;

    found = false;
    i=0;

    while(i<(colorPos.length) && !found) {
      found = pos<colorPos[i];
      if(!found) i++;
    }

    if( found ) {
      //Check validity
      if( Math.abs(colorPos[i]-pos)<1e-2 )
        return -1;

      //Oki :)
      Color[] oldColor = colorVal;
      double[] oldPos  = colorPos;

      colorVal = new Color[colorPos.length+1];
      colorPos = new double[colorVal.length];

      System.arraycopy(oldColor, 0, colorVal, 0, i);
      System.arraycopy(oldPos, 0, colorPos, 0, i);
      colorVal[i] = c;
      colorPos[i] = pos;
      System.arraycopy(oldColor, i, colorVal, i+1, oldColor.length-i);
      System.arraycopy(oldPos, i, colorPos, i+1, oldColor.length-i);

      return i;

    }

    // Not found
    return -1;

  }

  /**
   * Removes a color entry
   * @param id Entry id (Cannot be fisrt or last value)
   */
  public void removeEntry(int id) {

    if( id>0 && id<(colorVal.length-1)) {

      Color[] oldColor = colorVal;
      double[] oldPos  = colorPos;

      colorVal = new Color[colorPos.length-1];
      colorPos = new double[colorVal.length];

      System.arraycopy(oldColor, 0, colorVal, 0, id);
      System.arraycopy(oldColor, id+1, colorVal, id, oldColor.length-id-1);

      System.arraycopy(oldPos, 0, colorPos, 0, id);
      System.arraycopy(oldPos, id+1, colorPos, id, oldPos.length-id-1);
    }

  }

}
