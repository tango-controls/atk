package fr.esrf.tangoatk.widget.image;

import fr.esrf.tangoatk.widget.util.JGradientViewer;

/**
 * An abstract class for image format (Used by the RawImageViewer).
 */
public abstract class IImageFormat {

  byte[][] data = new byte[0][0];  // Handle to data
  double bfA0;                     // Best fit offset
  double bfA1;                     // Best fit factor

  /**
   * Sets the data as a byte buffer (as defined by the Tango CCD abstract class).
   * @param data Pointer to image data
   */
  public void setData(byte[][] data) {
    if(data==null)
      this.data = new byte[0][0];
    else
      this.data = data;
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
   * Returns the histogram width.
   * (The number of possible value for a pixel)
   */
  public abstract int getHistogramWidth();

  /**
   * Compute the best fit bounds (Monochrome only)
   * @param bestFit Bestfit enabled flag
   * @param tool Gradient displayed within the image viewer
   */
  public abstract void preComputeBestFit(boolean bestFit, JGradientViewer tool);

  /**
   * Returns the pixel at the specifed pos
   * @param x horizontal coordinate
   * @param y vertical coordinate
   * @param negative Negative flag
   * @param colormap16 16Bit colormap (Monochrome only)
   */
  public abstract int getRGB(boolean negative,int[] colormap16,int x,int y);


}
