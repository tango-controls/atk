package fr.esrf.tangoatk.widget.util.chart;

import java.awt.*;

/**
 * A class to handle bar chart fill color.
 */
public class ColorItem {

  public int   idx;
  public Color fillColor;

  //Construct an item
  ColorItem(int idx,Color fillColor) {
    this.idx = idx;
    this.fillColor = fillColor;
  }

}
