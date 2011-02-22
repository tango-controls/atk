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
