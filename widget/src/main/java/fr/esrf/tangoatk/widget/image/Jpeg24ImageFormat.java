package fr.esrf.tangoatk.widget.image;

import java.io.IOException;

/**
 * Color 24bits image format (JPEG)
 */
public class Jpeg24ImageFormat extends RGB24ImageFormat {

  JpegDecoder dec;

  public Jpeg24ImageFormat() {
    dec = new JpegDecoder();
  }

  public String getName() {
    return "RGB24 (JPEG)";
  }

  public void setData(byte[] rawData) throws IOException {

    data = zImg;
    if(rawData!=null) {
      dec.setBuffer(rawData);
      byte[][] d = dec.decode();
      if( dec.GetType()!=JpegDecoder.RGB24 )
        new IOException("Jpeg24ImageFormat: Unexpected jpeg format");
      data = d;
    }

  }

}
