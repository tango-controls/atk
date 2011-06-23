package fr.esrf.tangoatk.widget.util;

import com.braju.format.Format;

import javax.swing.*;
import java.awt.*;


/**
 * A class to display a number using large digit (clock like)
 */
public class DigitalNumberViewer extends JComponent {

  private String format = "%5.2f";
  private double value  = 0.0;
  private int    fontSize = 30;
  private int    hOffset = 3;
  private int    vOffset = 3;
  private int[]  cX = new int[6];
  private int[]  cY = new int[6];

  /**
   * Construct a DigitalNumberViewer
   */
  public DigitalNumberViewer() {

    setOpaque(true);
    setLayout(null);
    setBorder(null);
    setForeground(Color.black);
    setBackground(Color.white);

  }

  /**
   * Sets the format (C like)
   * @param format
   */
  public void setFormat(String format) {
    this.format = format;
    repaint();
  }

  /**
   * Returns the current format (C like)
   */
  public String getFormat() {
    return format;
  }

  /**
   * Sets the value
   * @param value
   */
  public void setValue(double value) {
    this.value = value;
    repaint();
  }

  /**
   * Set the font size
   * @param size Font size
   */
  public void setFontSize(int size) {
    fontSize = size;
    repaint();
  }
  
  public int getFontSize() {
    return fontSize;
  }

  /**
   * Sets the vertical offset of the value
   * @param offset Vertical offset (pixel)
   */
  public void setVerticalOffset(int offset) {
    vOffset = offset;
  }

  /**
   * Sets the horizontal offset of the value
   * @param offset Horizontal offset
   */
  public void setHorizontalOffset(int offset) {
    hOffset = offset;
  }

  public Dimension getPreferredSize() {

    String v = getFormattedValue();
    int lgth = v.length();
    int pointWidth = 0;

    if(v.indexOf('.')!=-1) {
      lgth = lgth-1;
      pointWidth = 5;
    }

    Insets h = getInsets();

    return new Dimension((fontSize+5)*lgth + pointWidth + 2*hOffset + h.left + h.right,
                          fontSize*2 + 2*vOffset + h.top + h.bottom);

  }

  private void drawASegment(Graphics g,int xPos,int yPos) {

    int w = fontSize/6;

    //g.drawLine(xPos+1,yPos,xPos+fontSize-1,yPos);
    //g.drawLine(xPos+fontSize-1,yPos,xPos+fontSize-1-w,yPos+w);
    //g.drawLine(xPos+fontSize-1-w,yPos+w,xPos+w+1,yPos+w);
    //g.drawLine(xPos+w+1,yPos+w,xPos+1,yPos);

    cX[0] = xPos+1;
    cX[1] = xPos+fontSize-1;
    cX[2] = xPos+fontSize-1-w;
    cX[3] = xPos+w+1;

    cY[0] = yPos;
    cY[1] = yPos;
    cY[2] = yPos+w;
    cY[3] = yPos+w;

    g.fillPolygon(cX,cY,4);

  }

  private void drawBSegment(Graphics g,int xPos,int yPos) {

    int w = fontSize/6;

    //g.drawLine(xPos+fontSize,yPos+2,xPos+fontSize,yPos+fontSize-2);
    //g.drawLine(xPos+fontSize,yPos+fontSize-2,xPos+fontSize-w,yPos+fontSize-2-w);
    //g.drawLine(xPos+fontSize-w,yPos+fontSize-2-w,xPos+fontSize-w,yPos+w+2);
    //g.drawLine(xPos+fontSize-w,yPos+w+2,xPos+fontSize,yPos+2);

    cX[0] = xPos+fontSize;
    cX[1] = xPos+fontSize;
    cX[2] = xPos+fontSize-w;
    cX[3] = xPos+fontSize-w;

    cY[0] = yPos+2;
    cY[1] = yPos+fontSize-2;
    cY[2] = yPos+fontSize-2-w;
    cY[3] = yPos+w+2;

    g.fillPolygon(cX,cY,4);

  }

  private void drawCSegment(Graphics g,int xPos,int yPos) {

    int w = fontSize/6;

    //g.drawLine(xPos+fontSize,yPos+2+fontSize,xPos+fontSize,yPos+2*fontSize-2);
    //g.drawLine(xPos+fontSize,yPos+2*fontSize-2,xPos+fontSize-w,yPos+2*fontSize-2-w);
    //g.drawLine(xPos+fontSize-w,yPos+2*fontSize-2-w,xPos+fontSize-w,yPos+fontSize+w+2);
    //g.drawLine(xPos+fontSize-w,yPos+fontSize+w+2,xPos+fontSize,yPos+fontSize+2);

    cX[0] = xPos+fontSize;
    cX[1] = xPos+fontSize;
    cX[2] = xPos+fontSize-w;
    cX[3] = xPos+fontSize-w;

    cY[0] = yPos+2+fontSize;
    cY[1] = yPos+2*fontSize-2;
    cY[2] = yPos+2*fontSize-2-w;
    cY[3] = yPos+fontSize+w+2;

    g.fillPolygon(cX,cY,4);

  }

  private void drawDSegment(Graphics g,int xPos,int yPos) {

    int w = fontSize/6;

    //g.drawLine(xPos+1,yPos+2*fontSize,xPos+fontSize-1,yPos+2*fontSize);
    //g.drawLine(xPos+fontSize-1,yPos+2*fontSize,xPos+fontSize-1-w,yPos+2*fontSize-w);
    //g.drawLine(xPos+fontSize-1-w,yPos+2*fontSize-w,xPos+w+1,yPos+2*fontSize-w);
    //g.drawLine(xPos+w+1,yPos+2*fontSize-w,xPos+1,yPos+2*fontSize);

    cX[0] = xPos+1;
    cX[1] = xPos+fontSize-1;
    cX[2] = xPos+fontSize-1-w;
    cX[3] = xPos+w+1;

    cY[0] = yPos+2*fontSize;
    cY[1] = yPos+2*fontSize;
    cY[2] = yPos+2*fontSize-w;
    cY[3] = yPos+2*fontSize-w;

    g.fillPolygon(cX,cY,4);

  }

  private void drawESegment(Graphics g,int xPos,int yPos) {

    int w = fontSize/6;

    //g.drawLine(xPos,yPos+2+fontSize,xPos,yPos+2*fontSize-2);
    //g.drawLine(xPos,yPos+2*fontSize-2,xPos+w,yPos+2*fontSize-2-w);
    //g.drawLine(xPos+w,yPos+2*fontSize-2-w,xPos+w,yPos+fontSize+w+2);
    //g.drawLine(xPos+w,yPos+fontSize+w+2,xPos,yPos+fontSize+2);

    cX[0] = xPos;
    cX[1] = xPos;
    cX[2] = xPos+w;
    cX[3] = xPos+w;

    cY[0] = yPos+2+fontSize;
    cY[1] = yPos+2*fontSize-2;
    cY[2] = yPos+2*fontSize-2-w;
    cY[3] = yPos+fontSize+w+2;

    g.fillPolygon(cX,cY,4);

  }

  private void drawFSegment(Graphics g,int xPos,int yPos) {

    int w = fontSize/6;

    //g.drawLine(xPos,yPos+2,xPos,yPos+fontSize-2);
    //g.drawLine(xPos,yPos+fontSize-2,xPos+w,yPos+fontSize-2-w);
    //g.drawLine(xPos+w,yPos+fontSize-2-w,xPos+w,yPos+w+2);
    //g.drawLine(xPos+w,yPos+w+2,xPos,yPos+2);

    cX[0] = xPos;
    cX[1] = xPos;
    cX[2] = xPos+w;
    cX[3] = xPos+w;

    cY[0] = yPos+2;
    cY[1] = yPos+fontSize-2;
    cY[2] = yPos+fontSize-2-w;
    cY[3] = yPos+w+2;

    g.fillPolygon(cX,cY,4);

  }

  private void drawGSegment(Graphics g,int xPos,int yPos) {

    int w = fontSize/6;
    int o = w/2;

    //g.drawLine(xPos+o,yPos+fontSize,xPos+w,yPos+fontSize-w/2);
    //g.drawLine(xPos+w,yPos+fontSize-w/2,xPos+fontSize-w,yPos+fontSize-w/2);
    //g.drawLine(xPos+fontSize-w,yPos+fontSize-w/2,xPos+fontSize-o,yPos+fontSize);
    //g.drawLine(xPos+fontSize-o,yPos+fontSize,xPos+fontSize-w,yPos+fontSize+w/2);
    //g.drawLine(xPos+fontSize-w,yPos+fontSize+w/2,xPos+w,yPos+fontSize+w/2);
    //g.drawLine(xPos+w,yPos+fontSize+w/2,xPos+o,yPos+fontSize);

    cX[0] = xPos+o;
    cX[1] = xPos+w;
    cX[2] = xPos+fontSize-w;
    cX[3] = xPos+fontSize-o;
    cX[4] = xPos+fontSize-w;
    cX[5] = xPos+w;

    cY[0] = yPos+fontSize;
    cY[1] = yPos+fontSize-w/2;
    cY[2] = yPos+fontSize-w/2;
    cY[3] = yPos+fontSize;
    cY[4] = yPos+fontSize+w/2+1;
    cY[5] = yPos+fontSize+w/2+1;

    g.fillPolygon(cX,cY,6);

  }

  public void paint(Graphics g) {

    Dimension d = getSize();
    if( isOpaque() ) {
      g.setColor(getBackground());
      g.fillRect(0,0,d.width,d.height);
    }

    g.setColor(getForeground());
    String v = getFormattedValue();
    Insets h = getInsets();
    int xPos = h.left+hOffset;
    int yPos = h.top+vOffset;

    for(int i=0;i<v.length();i++) {
      switch(v.charAt(i)) {

        case '0':
          drawASegment(g,xPos,yPos);
          drawBSegment(g,xPos,yPos);
          drawCSegment(g,xPos,yPos);
          drawDSegment(g,xPos,yPos);
          drawESegment(g,xPos,yPos);
          drawFSegment(g,xPos,yPos);
          xPos+=(fontSize+5);
          break;

        case '1':
          drawBSegment(g,xPos,yPos);
          drawCSegment(g,xPos,yPos);
          xPos+=(fontSize+5);
          break;

        case '2':
          drawASegment(g,xPos,yPos);
          drawBSegment(g,xPos,yPos);
          drawGSegment(g,xPos,yPos);
          drawESegment(g,xPos,yPos);
          drawDSegment(g,xPos,yPos);
          xPos+=(fontSize+5);
          break;

        case '3':
          drawASegment(g,xPos,yPos);
          drawBSegment(g,xPos,yPos);
          drawGSegment(g,xPos,yPos);
          drawCSegment(g,xPos,yPos);
          drawDSegment(g,xPos,yPos);
          xPos+=(fontSize+5);
          break;

        case '4':
          drawFSegment(g,xPos,yPos);
          drawGSegment(g,xPos,yPos);
          drawBSegment(g,xPos,yPos);
          drawCSegment(g,xPos,yPos);
          xPos+=(fontSize+5);
          break;

        case '5':
          drawASegment(g,xPos,yPos);
          drawFSegment(g,xPos,yPos);
          drawGSegment(g,xPos,yPos);
          drawCSegment(g,xPos,yPos);
          drawDSegment(g,xPos,yPos);
          xPos+=(fontSize+5);
          break;

        case '6':
          drawASegment(g,xPos,yPos);
          drawFSegment(g,xPos,yPos);
          drawGSegment(g,xPos,yPos);
          drawCSegment(g,xPos,yPos);
          drawDSegment(g,xPos,yPos);
          drawESegment(g,xPos,yPos);
          xPos+=(fontSize+5);
          break;

        case '7':
          drawASegment(g,xPos,yPos);
          drawBSegment(g,xPos,yPos);
          drawCSegment(g,xPos,yPos);
          xPos+=(fontSize+5);
          break;

        case '8':
          drawASegment(g,xPos,yPos);
          drawBSegment(g,xPos,yPos);
          drawCSegment(g,xPos,yPos);
          drawDSegment(g,xPos,yPos);
          drawESegment(g,xPos,yPos);
          drawFSegment(g,xPos,yPos);
          drawGSegment(g,xPos,yPos);
          xPos+=(fontSize+5);
          break;

        case '9':
          drawASegment(g,xPos,yPos);
          drawBSegment(g,xPos,yPos);
          drawCSegment(g,xPos,yPos);
          drawDSegment(g,xPos,yPos);
          drawFSegment(g,xPos,yPos);
          drawGSegment(g,xPos,yPos);
          xPos+=(fontSize+5);
          break;

        case '-':
          drawGSegment(g,xPos,yPos);
          xPos+=(fontSize+5);
          break;

        case ' ':
          xPos+=(fontSize+5);
          break;

        case '.':
          g.fillRect(xPos,yPos+2*fontSize-5,5,5);
          xPos+=8;
          break;
      }
    }

  }

  private String getFormattedValue() {
    if( Double.isNaN(value) ) {
      return "-----";
    } else {
      return Format.sprintf(format, new Object[] { new Double(value) });
    }
  }

  public static void main(String[] args) {

    JFrame f = new JFrame();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    final DigitalNumberViewer d = new DigitalNumberViewer();
    d.setBorder(BorderFactory.createLoweredBevelBorder());
    d.setValue(-Math.PI);
    d.setFormat("%.6f");
    f.setContentPane(d);
    ATKGraphicsUtils.centerFrameOnScreen(f);
    f.setVisible(true);

  }

}
