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

  public void preComputeBestFit(boolean bestFit, JGradientViewer tool) {

    // Scale to 16bit for the colormap

    if (!bestFit) {

      bfA0 = 0.0;
      bfA1 = 256.0;
      // Scale the gradient
      tool.getAxis().setMinimum(0.0);
      tool.getAxis().setMaximum(256.0);

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
