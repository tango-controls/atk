package fr.esrf.tangoatk.widget.image;

import com.sun.imageio.plugins.jpeg.JPEGImageReader;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.ImageInputStreamImpl;
import javax.swing.*;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.IOException;
import java.util.Iterator;

/**
 * A class to decode jpeg image
 * Author: JL Pons
 */

public class JpegDecoder extends ImageInputStreamImpl {

  /** 32 Bits RGB */
  public final static int RGB24 = 1;
  /** 8 Bits Gray scale */
  public final static int GRAY8 = 2;

  private byte[] cache;
  private int type;
  JPEGImageReader imgReader;

  /**
   * Contructs a JPEG decoder
   */
  public JpegDecoder() {

    Iterator imgIt = ImageIO.getImageReadersByFormatName("jpeg");
    if (!imgIt.hasNext()) {
      JOptionPane.showMessageDialog(null, "Jpeg decoder not found", "Error", JOptionPane.ERROR_MESSAGE);
      imgReader = null;
    } else {
      imgReader = (JPEGImageReader)imgIt.next();
    }

  }

  /**
   * Set data
   * @param jpgBuffer buffer of encoded data
   */
  public void setBuffer(byte[] jpgBuffer) {

    cache = jpgBuffer;
    streamPos = 0;
    bitOffset = 0;
    type = 0;

  }

  /**
   * @return type (0 is returned if no image has been decoded)
   * @see #RGB24
   * @see #GRAY8
   */
  public int GetType() {
    return type;
  }

  /**
   * Decode the image
   * @return An array of 8Bits graysscale or 32Bits RGB
   * @throws IOException in case of failure
   */
  public byte[][] decode() throws IOException {

    if(imgReader==null)
      throw new IOException("No native JPEG decoder available");

    byte[][] img = null;
    DataBufferByte data;

    streamPos = 0;

    imgReader.setInput(this, true, true);
    ImageReadParam param = imgReader.getDefaultReadParam();
    Raster r = imgReader.readRaster(0,param);

    int width  = r.getWidth();
    int height = r.getHeight();
    int colors = imgReader.getRawImageType(0).getColorModel().getNumColorComponents();
    int startLine = 0;

    switch(colors) {

      case 1:
        type = GRAY8;
        data = (DataBufferByte)r.getDataBuffer();
        img = new byte[height][width];
        for(int i=0;i<height;i++) {
          System.arraycopy(data.getData(),startLine,img[i],0,width);
          startLine += width;
        }
        break;

      case 3:
        type = RGB24;
        data = (DataBufferByte)r.getDataBuffer();
        img = new byte[height][width*3];
        for(int i=0;i<height;i++) {
          System.arraycopy(data.getData(),startLine,img[i],0,width*3);
          startLine += width*3;
        }
        break;

      default:
        throw new IOException("Unexpected JPEG color format");
    }

    return img;
  }


  public int read() throws IOException {

    bitOffset = 0;
    if (streamPos >= cache.length)
      return -1;
    int i = (int)cache[(int)streamPos];
    i = i & 0xFF;
    streamPos++;
    return i;

  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {

    bitOffset = 0;

    int remainingByte = (int) (cache.length-streamPos);
    int byteToCopy = (len<=remainingByte)?len:remainingByte;

    if(byteToCopy>0) {
      System.arraycopy(cache,(int)streamPos,b,off,byteToCopy);
      streamPos += byteToCopy;
      return byteToCopy;
    } else {
      return -1;
    }

  }

}
