package fr.esrf.tangoatk.widget.image;

import fr.esrf.tangoatk.widget.util.JGradientViewer;

/**
 * Monochrome 16bits image format
 */
public class Mono16ImageFormat extends IImageFormat {

  public Mono16ImageFormat() {}

  public int getWidth() {
    if(getHeight()==0) return 0;
    else               return data[0].length/2;
  }

  public String getName() {
    return "MONO16";
  }

  public boolean isColorFormat() {
    return false;
  }

  public int getHistogramWidth() {
    return 65536;
  }

  public double getValue(int x,int y) {

    int uc1 = data[y][2*x  ] & 0xFF;
    int uc2 = data[y][2*x+1] & 0xFF;
    return (double)((uc1 << 8) + uc2);

  }

  public void preComputeBestFit(boolean bestFit,JGradientViewer tool) {

    if (!bestFit) {

      bfA0 = 0.0;
      bfA1 = 1.0;
      tool.getAxis().setMinimum(0.0);
      tool.getAxis().setMaximum(65536.0);

    } else {

      int i, j;
      double autoBfMin = 65536.0;
      double autoBfMax = 0.0;

      for (j = 0; j < data.length; j++)
        for (i = 0; i < data[j].length/2; i++) {
          int uc1 = data[j][2*i  ] & 0xFF;
          int uc2 = data[j][2*i+1] & 0xFF;
          double v = (double)((uc1 << 8) + uc2);
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

  private int bestFit(int v) {

    int nv = (int) ((bfA0 + v) * bfA1);
    if (nv < 0) return 0;
    if (nv > 65535) return 65535;
    return nv;

  }

  public int getRGB(boolean negative,int[] colormap16,int x,int y) {

    int uc1 = data[y][2*x  ] & 0xFF;
    int uc2 = data[y][2*x+1] & 0xFF;
    int v = ((uc1 << 8) + uc2);

    if(negative) {
      return colormap16[(~bestFit(v)) & 65535];
    } else {
      return colormap16[bestFit(v)];
    }

  }

}
