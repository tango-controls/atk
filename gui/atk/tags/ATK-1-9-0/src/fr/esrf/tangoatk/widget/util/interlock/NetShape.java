/**
 * A set of class to handle a network editor and its viewer.
 *
 * Author: Jean Luc PONS
 * Date: Jul 1, 2004
 * (c) ESRF 2004
 */
package fr.esrf.tangoatk.widget.util.interlock;

import java.awt.*;
import fr.esrf.tangoatk.widget.util.interlock.shape.*;

/** A class to manage the shape of a bubble object. */
public class NetShape {

  public static final int SHAPE_CIRCLE     = 0;
  public static final int SHAPE_SQUARE     = 1;
  public static final int SHAPE_HEXAGON    = 2;
  public static final int SHAPE_VCC        = 3;
  public static final int SHAPE_GROUND     = 4;
  public static final int SHAPE_DOT        = 5;
  public static final int SHAPE_COMPUTER1  = 6;
  public static final int SHAPE_DEVICE1    = 7;
  public static final int SHAPE_DEVICE2    = 8;
  public static final int SHAPE_DEVICE3    = 9;
  public static final int SHAPE_NETDEVICE1 = 10;
  public static final int SHAPE_NETDEVICE2 = 11;
  public static final int SHAPE_NETDEVICE3 = 12;
  public static final int SHAPE_PRINTER1   = 13;
  public static final int SHAPE_PRINTER2   = 14;
  public static final int SHAPE_PRINTER3   = 15;
  public static final int SHAPE_SERVER1    = 16;
  public static final int SHAPE_SERVER2    = 17;
  public static final int SHAPE_STORAGE1   = 18;
  public static final int SHAPE_STORAGE2   = 19;
  public static final int SHAPE_STORAGE3   = 20;
  public static final int SHAPE_STORAGE4   = 21;
  public static final int SHAPE_STORAGE5   = 22;
  public static final int SHAPE_XTERM      = 23;

  // Default selection color
  static Color selColor  = new Color(160, 160, 80);

  private static int[] xHexagonI = new int[8];
  private static int[] yHexagonI = new int[8];
  private static Polygon hexagonPoly = new Polygon();


  static void paintShape(Graphics2D g, int shape, boolean isSelected,
                         Color color, int x, int y, int bSize) {

    if(bSize==0)
     return;

    switch (shape) {

      case SHAPE_CIRCLE:
        setFillColor(g,color,isSelected);
        g.fillOval(x - bSize - 2, y - bSize - 2, bSize * 2 + 5, bSize * 2 + 5);
        g.setColor(Color.black);
        g.drawOval(x - bSize - 2, y - bSize - 2, bSize * 2 + 4, bSize * 2 + 4);
        break;

      case SHAPE_SQUARE:
        setFillColor(g,color,isSelected);
        g.fillRect(x - bSize - 5, y - bSize - 4, bSize * 2 + 10, bSize * 2 + 8);
        g.setColor(Color.black);
        g.drawRect(x - bSize - 5, y - bSize - 4, bSize * 2 + 10, bSize * 2 + 8);
        break;

      case SHAPE_HEXAGON:
        placeHexagon(bSize,x,y);
        setFillColor(g,color,isSelected);
        g.fillPolygon(hexagonPoly);
        g.setColor(Color.black);
        g.drawPolygon(hexagonPoly);
        break;

      case SHAPE_VCC:
        setFillColor(g, Color.GREEN,isSelected);
        g.fillRect(x - 10, y - 2, 20, 4);
        setFillColor(g, Color.BLACK,isSelected);
        g.fillRect(x - 3, y - 3, 6, 6);
        break;

      case SHAPE_GROUND:
        setFillColor(g, Color.BLACK,isSelected);
        g.drawLine(x - 10, y, x + 10, y);
        for (int i = -10; i <= 10; i += 4)
          g.drawLine(x + i, y, x + (i - 4), y + 5);
        g.fillRect(x - 3, y - 3, 6, 6);
        break;

      case SHAPE_DOT:
        setFillColor(g, Color.BLACK,isSelected);
        g.fillRect(x - 3, y - 3, 6, 6);
        break;

      case SHAPE_COMPUTER1:
        Computer1.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_DEVICE1:
        Device1.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_DEVICE2:
        Device2.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_DEVICE3:
        Device3.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_NETDEVICE1:
        NetDevice1.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_NETDEVICE2:
        NetDevice2.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_NETDEVICE3:
        NetDevice3.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_PRINTER1:
        Printer1.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_PRINTER2:
        Printer2.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_PRINTER3:
        Printer3.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_SERVER1:
        Server1.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_SERVER2:
        Server2.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_STORAGE1:
        Storage1.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_STORAGE2:
        Storage2.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_STORAGE3:
        Storage3.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_STORAGE4:
        Storage4.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_STORAGE5:
        Storage5.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;
      case SHAPE_XTERM:
        XTerm.paint(g,(isSelected)?selColor:color,x,y,(double)bSize/10.0);
        break;

    }

  }

  static void setBoundRect(int shape, int x, int y, int bSize,Rectangle bound) {

    switch (shape) {
      case SHAPE_CIRCLE:
      case SHAPE_SQUARE:
      case SHAPE_HEXAGON:
        bound.setRect(x - bSize - 2, y - bSize - 2, bSize * 2 + 4, bSize * 2 + 4);
        break;
      case SHAPE_VCC:
        bound.setRect(x - 10, y - 4, 20, 8);
        break;
      case SHAPE_GROUND:
        bound.setRect(x - 10, y - 4, 20, 8);
        break;
      case SHAPE_DOT:
        bound.setRect(x - 5, y - 5, 10, 10);
        break;
      case SHAPE_COMPUTER1:
        Computer1.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_DEVICE1:
        Device1.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_DEVICE2:
        Device2.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_DEVICE3:
        Device3.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_NETDEVICE1:
        NetDevice1.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_NETDEVICE2:
        NetDevice2.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_NETDEVICE3:
        NetDevice3.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_PRINTER1:
        Printer1.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_PRINTER2:
        Printer2.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_PRINTER3:
        Printer3.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_SERVER1:
        Server1.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_SERVER2:
        Server2.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_STORAGE1:
        Storage1.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_STORAGE2:
        Storage2.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_STORAGE3:
        Storage3.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_STORAGE4:
        Storage4.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_STORAGE5:
        Storage5.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;
      case SHAPE_XTERM:
        XTerm.setBoundRect(x,y,(double)bSize/10.0,bound);
        break;

    }

  }

  private static void placeHexagon(int bSize,int x,int y) {

    double size = bSize + 2;
    xHexagonI[0] = x - (int)(size);
    xHexagonI[1] = x - (int)(size*0.38+0.5);
    xHexagonI[2] = x + (int)(size*0.38+0.5);
    xHexagonI[3] = x + (int)(size);
    xHexagonI[4] = x + (int)(size);
    xHexagonI[5] = x + (int)(size*0.38+0.5);
    xHexagonI[6] = x - (int)(size*0.38+0.5);
    xHexagonI[7] = x - (int)(size);
    yHexagonI[0] = y - (int)(size*0.38+0.5);
    yHexagonI[1] = y - (int)(size);
    yHexagonI[2] = y - (int)(size);
    yHexagonI[3] = y - (int)(size*0.38+0.5);
    yHexagonI[4] = y + (int)(size*0.38+0.5);
    yHexagonI[5] = y + (int)(size);
    yHexagonI[6] = y + (int)(size);
    yHexagonI[7] = y + (int)(size*0.38+0.5);
    hexagonPoly.xpoints = xHexagonI;
    hexagonPoly.ypoints = yHexagonI;
    hexagonPoly.npoints = 8;
    hexagonPoly.invalidate();

  }

  private static void setFillColor(Graphics2D g,Color defaultColor,boolean selected) {

    if (selected)
      g.setColor(selColor);
    else
      g.setColor(defaultColor);

  }

}
