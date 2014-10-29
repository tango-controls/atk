/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

/** A class containing various low level graphics utils. */
public class ATKGraphicsUtils {

  // Create a Buffered image to get high precision font metrics
  private static FontRenderContext frc=null;
  final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

  /**
   * Measure a string (using AA font , zoom and translation of Graphics2D are not supported)
   * @param s String to be measured
   * @param f Font used
   * @return Dimesion of the string (in pixel)
   */
  static public Dimension measureString(String s,Font f) {

    init();
    if( f!=null && s!=null ) {
      Rectangle2D bounds = f.getStringBounds(s, frc);
      int w = (int)(bounds.getWidth()+0.5);
      int h = (int)(bounds.getHeight()+0.5);
      return new Dimension(w,h);
    } else {
      return new Dimension(0,0);
    }

  }
  
  /**
   * Retreive the default render context.
   */
  static public FontRenderContext getDefaultRenderContext() {
    init();
    return frc;
  }

  /**
   * Returns the line metrics for the given font.
   * @param s String to be measured
   * @param f Font object
   * @return LineMetrics
   */
  static public LineMetrics getLineMetrics(String s,Font f) {
    init();
    return f.getLineMetrics(s,frc);
  }

  /**
   * Center the given dialog according to its parent. If the dialog is not parented
   * (null parent), It will appear at the center of the screen. The dialog is
   * not displayed after a call to this function, a call to setVisible() is needed.
   * <p>Note: This function has been designed to work with 'heavyWeight' system dependant
   * awt window which doesn't use a layout manager (null layout).
   * @param dlg the dialog.
   * @param dlgWidth desired width of the JDialog content pane.
   * @param dlgHeight desired height of the JDialog content pane.
   */
  public static void centerDialog(Dialog dlg,int dlgWidth,int dlgHeight) {

    // Get the parent rectangle
    Rectangle r = new Rectangle(0,0,0,0);
    if (dlg.getParent()!=null && dlg.getParent().isVisible())
      r = dlg.getParent().getBounds();

    // Check rectangle validity
    if(r.width==0 || r.height==0) {
      r.x = 0;
      r.y = 0;
      r.width  = screenSize.width;
      r.height = screenSize.height;
    }

    // Get the window insets.
    dlg.pack();
    Insets insets = dlg.getInsets();

    // Center
    int xe,ye,wx,wy;
    wx = dlgWidth  + (insets.right + insets.left);
    wy = dlgHeight + (insets.bottom + insets.top);
    xe = r.x + (r.width - wx) / 2;
    ye = r.y + (r.height - wy) / 2;

    // Saturate
    if( (xe+wx) > screenSize.width )
      xe = screenSize.width - wx;
    if( (ye+wy) > screenSize.height )
      ye = screenSize.height - wy;
    if( xe<0 ) xe=0;
    if( ye<0 ) ye=0;

    // Set bounds
    dlg.setBounds(xe, ye, wx, wy);

  }

  /**
   * Center the given dialog according to its parent and its preferredSize.
   * @param dlg the dialog.
   */
  public static void centerDialog(Dialog dlg) {

    dlg.pack();

    // Get the parent rectangle
    Rectangle r = new Rectangle(0,0,0,0);
    if (dlg.getParent()!=null && dlg.getParent().isVisible())
      r = dlg.getParent().getBounds();

    // Check rectangle validity
    if(r.width==0 || r.height==0) {
      r.x = 0;
      r.y = 0;
      r.width  = screenSize.width;
      r.height = screenSize.height;
    }

    // Center
    int xe,ye,wx,wy;
    wx = dlg.getPreferredSize().width;
    wy = dlg.getPreferredSize().height;
    xe = r.x + (r.width - wx) / 2;
    ye = r.y + (r.height - wy) / 2;

    // Saturate
    if( (xe+wx) > screenSize.width )
      xe = screenSize.width - wx;
    if( (ye+wy) > screenSize.height )
      ye = screenSize.height - wy;
    if( xe<0 ) xe=0;
    if( ye<0 ) ye=0;

    // Set bounds
    dlg.setBounds(xe, ye, wx, wy);

  }

  /**
   * Center the given frame on screen. The frame is not displayed
   * after a call to this function, a call to setVisible() is needed.
   * @param fr Frame to be centered.
   */
  public static void centerFrameOnScreen(Frame fr) {

    Rectangle r = new Rectangle(0,0,screenSize.width,screenSize.height);
    fr.pack();

    // Center
    int xe,ye,wx,wy;
    wx = fr.getPreferredSize().width;
    if(wx>screenSize.width) wx = screenSize.width;
    wy = fr.getPreferredSize().height;
    if(wy>screenSize.height) wy = screenSize.height;
    xe = r.x + (r.width - wx) / 2;
    ye = r.y + (r.height - wy) / 2;

    // Set bounds
    fr.setBounds(xe, ye, wx, wy);

  }

  /**
   * Center the given frame according to the given componenent. The frame is
   * not displayed after a call to this function, a call to setVisible()
   * is needed.
   * @param parent Parent component
   * @param fr Frame to be centered.
   */
  public static void centerFrame(JComponent parent,Frame fr) {

    fr.pack();
    Window parentWin = getWindowForComponent(parent);

    // Get the parent rectangle
    Rectangle r = new Rectangle(0,0,0,0);
    if (parentWin!=null && parentWin.isVisible())
      r = parentWin.getBounds();

    // Check rectangle validity
    if(r.width==0 || r.height==0) {
      r.x = 0;
      r.y = 0;
      r.width  = screenSize.width;
      r.height = screenSize.height;
    }

    // Center
    int xe,ye,wx,wy;
    wx = fr.getPreferredSize().width;
    wy = fr.getPreferredSize().height;
    xe = r.x + (r.width - wx) / 2;
    ye = r.y + (r.height - wy) / 2;

    // Saturate
    if( (xe+wx) > screenSize.width )
      xe = screenSize.width - wx;
    if( (ye+wy) > screenSize.height )
      ye = screenSize.height - wy;
    if( xe<0 ) xe=0;
    if( ye<0 ) ye=0;

    // Set bounds
    fr.setBounds(xe, ye, wx, wy);


  }

  /**
   * Return the parent Window of the given component.
   * @param aComponent Child componenent
   * @return A handle to the parent window.
   */
  public static Window getWindowForComponent(Component aComponent) {
    if (aComponent == null)
      return null;
    if (aComponent instanceof Frame || aComponent instanceof Dialog)
      return (Window) aComponent;
    return getWindowForComponent(aComponent.getParent());
  }

  /**
   * Displays the print dialog and sends a component snapshot to the printer.
   * Using the printerResolution can be usefull to print your component bigger
   * or smaller. A screen typicaly has a resolution of ~100dpi. This method does
   * not support multiple page documents.
   * @param comp Component to be printed out.
   * @param title Title of the print dialog.
   * @param fitToPage True to fit the component to the page (printerResolution ignored).
   * @param printerResolution Printer resolution when fitToPage is not enabled.
   */
  public static void printComponent(JComponent comp,String title,boolean fitToPage,int printerResolution) {

    // Default

    PageAttributes pa = new PageAttributes();
    JobAttributes ja = new JobAttributes();
    pa.setPrintQuality(PageAttributes.PrintQualityType.HIGH);
    pa.setColor(PageAttributes.ColorType.COLOR);
    pa.setMedia(PageAttributes.MediaType.A4);
    if(fitToPage) {
      // Default resolution
      pa.setPrinterResolution(72);
    } else {
      pa.setPrinterResolution(printerResolution);
    }
    ja.setMaxPage(1);
    ja.setMinPage(1);
    ja.setDialog(JobAttributes.DialogType.NATIVE);

    // Displays print window

    Window parent = getWindowForComponent(comp);
    PrintJob printJob;
    if(parent instanceof Frame) {
      printJob = java.awt.Toolkit.getDefaultToolkit().getPrintJob((Frame)parent, title, ja, pa);
    } else {
      Frame dummy = new Frame();
      printJob = java.awt.Toolkit.getDefaultToolkit().getPrintJob(dummy, title, ja, pa);
    }

    if (printJob != null) {

      // Get image dimension
      int w = comp.getSize().width;
      int h = comp.getSize().height;
      int tx,ty;

      // Make a screenshot of the graph
      BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
      Color oldBackground = comp.getBackground();
      comp.setBackground(Color.WHITE);
      comp.paint(img.getGraphics());
      comp.setBackground(oldBackground);

      try {

        // Fit the draw to the page by changing the printer resolution
        if (fitToPage) {

          // Get page dimension (should be given for 72dpi resolution)
          int wp = printJob.getPageDimension().width - 72; // 0.5inch margin
          int hp = printJob.getPageDimension().height - 72; // 0.5inch margin

          // Fit the graph to the page
          double ratioW = (double) w / (double) wp;
          double ratioH = (double) h / (double) hp;
          double nResolution;

          if (ratioW > ratioH) {

            // We get ratioW
            // We center verticaly
            nResolution = 72.0 * ratioW;
            tx = (int) (nResolution * 0.5);
            double cH = nResolution / 72.0 * (double) hp - (double) h;
            ty = (int) (0.5 * (nResolution + cH));

          } else {

            // We get ratioH
            // We center horizontaly
            nResolution = 72.0 * ratioH;
            double cW = nResolution / 72.0 * (double) wp - (double) w;
            tx = (int) (0.5 * (nResolution + cW));
            ty = (int) (nResolution * 0.5);

          }

          pa.setPrinterResolution((int) (nResolution + 0.5));

        } else {

          // 0.5 inch margin
          tx = printerResolution / 2;
          ty = printerResolution / 2;

        }

        // Print it
        java.awt.Graphics g = printJob.getGraphics();
        if( g!=null ) {
          g.translate(tx, ty);
          g.setClip(0, 0, w, h);
          g.drawImage(img, 0, 0, null);
          g.dispose();
        } else {
          JOptionPane.showMessageDialog(parent, "Unexpected error while printing.\nCheck you printer",
                                        title, JOptionPane.ERROR_MESSAGE);
        }
        printJob.end();

      } catch (Exception e) {

        e.printStackTrace();
        JOptionPane.showMessageDialog(parent, "Exception occured while printing\n" + e.getMessage(),
                                      title, JOptionPane.ERROR_MESSAGE);

      }

    }

  }

  
  /**
   * Position the given frame at the requested location  on screen. The frame is not displayed
   * after a call to this function, a call to setVisible() is needed.
   * @param fr Frame to be positioned.
   */
  public static void positionFrameOnScreen(Frame fr, int posx, int posy) {

    Rectangle r = new Rectangle(0,0,screenSize.width,screenSize.height);
    fr.pack();

    // Center
    int xe,ye,wx,wy;
    wx = fr.getPreferredSize().width;
    if(wx>screenSize.width) wx = screenSize.width;
    wy = fr.getPreferredSize().height;
    if(wy>screenSize.height) wy = screenSize.height;
    
    if ((posx+wx) > screenSize.width)
    {
        xe = screenSize.width - wx;
        if (xe < 0) xe = 0;
    }
    else
        xe = posx;
    
    if ((posy+wy) > screenSize.height)
    {
        ye = screenSize.height - wy;
        if (ye < 0) ye = 0;
    }
    else
        ye = posy;

    
    // Set bounds
    fr.setBounds(xe, ye, wx, wy);

  }

  private static int[] parseWindowPosition(String str)
  {
      if (str == null) return null;
      if (str.length() == 0) return null;
      
      String[] posStrs = str.split(",");
      if (posStrs == null) return null;
      if (posStrs.length != 2) return null;
      
      int posX = -1;
      int posY = -1;      
      try
      {
          posX = Integer.parseInt(posStrs[0]);
      }
      catch (NumberFormatException nfe)
      {
          return null;
      }
      try
      {
          posY = Integer.parseInt(posStrs[1]);
      }
      catch (NumberFormatException nfe)
      {
          return null;
      }
      
      if ((posX >= 0) && (posY >= 0))
      {
          int[]  wPos = new int[2];
          wPos[0] = posX;
          wPos[1] = posY;
          return wPos;
      }
      
      return null;
  }
  
  public static int[] getWindowPosFromArgs(String args[])
  {
        if (args.length <= 0) return null;

        for (int i=0; i<args.length; i++)
        {
            if (args[i].equalsIgnoreCase("-wpos"))
            {
                if (i == args.length-1) return null;
                i++;
                String wposStr = args[i];
                int[]   windowPos = parseWindowPosition(wposStr);
                return windowPos;
            }
        }        
        return null;      
  }
  
  
  static private void init() {

    // Init static
    if( frc==null ) {
      BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
      Graphics2D g = (Graphics2D)img.getGraphics();
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      frc = g.getFontRenderContext();
      g.dispose();
    }

  }

}
