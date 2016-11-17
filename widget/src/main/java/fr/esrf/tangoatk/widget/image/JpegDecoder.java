package fr.esrf.tangoatk.widget.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.io.InputStream;

/**
 * A class to decode jpeg image
 * Author: JL Pons
 */

public class JpegDecoder extends InputStream {

  /** 32 Bits RGB */
  public final static int RGB24 = 1;
  /** 8 Bits Gray scale */
  public final static int GRAY8 = 2;

  private byte[] cache;
  private int pos;
  private int type;

  /**
   * Contructs a JPEG decoder
   * @param jpgBuffer buffer of encoded data
   */
  public JpegDecoder(byte[] jpgBuffer) {
    cache = jpgBuffer;
    pos = 0;
    type = 0;
  }

  /**
   * Returns type (0 is returned if no image has been decoded)
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

    byte[][] img = null;
    DataBufferByte data;

    pos = 0;
    BufferedImage ret = ImageIO.read(this);
    if (ret == null)
      throw new IOException("Jpeg decoding error");

    int width  = ret.getWidth();
    int height = ret.getHeight();
    int type   = ret.getType();
    int startLine = 0;

    switch(type) {

      case BufferedImage.TYPE_3BYTE_BGR:

        type = RGB24;
        img  = new byte[height][width*3];
        data = (DataBufferByte)ret.getRaster().getDataBuffer();
        for(int i=0;i<height;i++) {
          System.arraycopy(data.getData(),startLine,img[i],0,width*3);
          startLine += width*3;
        }
        break;

      case BufferedImage.TYPE_BYTE_GRAY:

        type = GRAY8;
        img = new byte[height][width];
        data = (DataBufferByte)ret.getRaster().getDataBuffer();
        for(int i=0;i<height;i++) {
          System.arraycopy(data.getData(),startLine,img[i],0,width);
          startLine += width;
        }

      break;
      default:
        throw new IOException("Unsupported jpeg format");
    }

    return img;
  }

  public int read() throws IOException {

    if (pos >= cache.length)
      return -1;
    int i = (int)cache[pos];
    i = i & 0xFF;
    pos++;
    return i;

  }

  public int available() throws IOException {

    return cache.length - pos;

  }

}
