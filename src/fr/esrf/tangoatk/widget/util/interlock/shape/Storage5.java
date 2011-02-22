package fr.esrf.tangoatk.widget.util.interlock.shape;
/* Class generated by JDraw */

import java.awt.*;

/** ---------- Storage5 class ---------- */
public class Storage5 {

  private static int[][] xPolys = null;
  private static int[][] yPolys = null;

  private static Color sColor0 = new Color(153,153,153);

  private static int[][] xOrgPolys = {
    {-33,-33,14,14},
    {-33,-8,32,14},
    {14,14,32,32},
    {8,8,12,12},
    {8,8,12,12},
  };

  private static int[][] yOrgPolys = {
    {-1,13,13,-2},
    {-1,-13,-13,-1},
    {-1,13,-2,-13},
    {2,6,6,2},
    {9,11,11,9},
  };

  static public void paint(Graphics g,Color backColor,int x,int y,double size) {

    // Allocate array once
    if( xPolys == null ) {
      xPolys = new int [xOrgPolys.length][];
      yPolys = new int [yOrgPolys.length][];
      for( int i=0 ; i<xOrgPolys.length ; i++ ) {
        xPolys[i] = new int [xOrgPolys[i].length];
        yPolys[i] = new int [yOrgPolys[i].length];
      }
    }

    // Scale and translate poly
    for( int i=0 ; i<xOrgPolys.length ; i++ ) {
      for( int j=0 ; j<xOrgPolys[i].length ; j++ ) {
        xPolys[i][j] = (int)((double)xOrgPolys[i][j]*size+0.5) + x;
        yPolys[i][j] = (int)((double)yOrgPolys[i][j]*size+0.5) + y;
      }
    }

    // Paint object
    g.setColor(backColor);g.fillPolygon(xPolys[0],yPolys[0],xPolys[0].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[0],yPolys[0],xPolys[0].length);
    g.setColor(backColor);g.fillPolygon(xPolys[1],yPolys[1],xPolys[1].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[1],yPolys[1],xPolys[1].length);
    g.setColor(backColor);g.fillPolygon(xPolys[2],yPolys[2],xPolys[2].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[2],yPolys[2],xPolys[2].length);
    g.setColor(Color.black);
    g.drawLine((int)(-31.0*size+0.5)+x, (int)(11.0*size+0.5)+y, (int)(6.0*size+0.5)+x, (int)(11.0*size+0.5)+y);
    g.setColor(Color.black);
    g.drawLine((int)(-31.0*size+0.5)+x, (int)(2.0*size+0.5)+y, (int)(-31.0*size+0.5)+x, (int)(11.0*size+0.5)+y);
    g.setColor(Color.black);
    g.drawLine((int)(-31.0*size+0.5)+x, (int)(2.0*size+0.5)+y, (int)(6.0*size+0.5)+x, (int)(2.0*size+0.5)+y);
    g.setColor(Color.black);
    g.drawLine((int)(6.0*size+0.5)+x, (int)(2.0*size+0.5)+y, (int)(6.0*size+0.5)+x, (int)(11.0*size+0.5)+y);
    g.setColor(sColor0);g.fillPolygon(xPolys[3],yPolys[3],xPolys[3].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[3],yPolys[3],xPolys[3].length);
    g.setColor(Color.black);
    g.drawLine((int)(-29.0*size+0.5)+x, (int)(4.0*size+0.5)+y, (int)(4.0*size+0.5)+x, (int)(4.0*size+0.5)+y);
    g.setColor(sColor0);g.fillPolygon(xPolys[4],yPolys[4],xPolys[4].length);
    g.setColor(Color.black);g.drawPolygon(xPolys[4],yPolys[4],xPolys[4].length);
    g.setColor(Color.black);
    g.drawLine((int)(-29.0*size+0.5)+x, (int)(8.0*size+0.5)+y, (int)(4.0*size+0.5)+x, (int)(8.0*size+0.5)+y);
    g.setColor(Color.black);
    g.drawLine((int)(-29.0*size+0.5)+x, (int)(4.0*size+0.5)+y, (int)(-29.0*size+0.5)+x, (int)(8.0*size+0.5)+y);
    g.setColor(Color.black);
    g.drawLine((int)(4.0*size+0.5)+x, (int)(4.0*size+0.5)+y, (int)(4.0*size+0.5)+x, (int)(8.0*size+0.5)+y);
    g.setColor(Color.black);
    g.drawLine((int)(2.0*size+0.5)+x, (int)(10.0*size+0.5)+y, (int)(0.0*size+0.5)+x, (int)(10.0*size+0.5)+y);

  }

  static public void setBoundRect(int x,int y,double size,Rectangle bound) {
    bound.setRect((int)(-33.0*size+0.5)+x,(int)(-13.0*size+0.5)+y,(int)(66.0*size+0.5),(int)(27.0*size+0.5));
  }

}

