package fr.esrf.tangoatk.widget.image;

import java.io.IOException;

/**
 * Monochrome 8bits image format (JPEG)
 */
public class Jpeg8ImageFormat extends Mono8ImageFormat {

  JpegDecoder dec;

  public Jpeg8ImageFormat() {
    dec = new JpegDecoder();
  }

  public String getName() {
    return "MONO8 (JPEG)";
  }

  public void setData(byte[] rawData) throws IOException {

    data = zImg;
    if(rawData!=null) {
      dec.setBuffer(rawData);
      byte[][] d = dec.decode();
      if( dec.GetType()!=JpegDecoder.GRAY8 )
        new IOException("Jpeg8ImageFormat: Unexpected jpeg format");
      data = d;
    }

  }

}
