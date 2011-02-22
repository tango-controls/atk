package fr.esrf.tangoatk.widget.util;

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
    Rectangle2D bounds = f.getStringBounds(s, frc);
    int w = (int)bounds.getWidth();
    int h = (int)bounds.getHeight();
    return new Dimension(w,h);

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
    if( xe<0 ) xe=0;
    if( ye<0 ) ye=0;
    if( (xe+wx) > screenSize.width )
      xe = screenSize.width - wx;
    if( (ye+wy) > screenSize.height )
      ye = screenSize.height - wy;

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
    if( xe<0 ) xe=0;
    if( ye<0 ) ye=0;
    if( (xe+wx) > screenSize.width )
      xe = screenSize.width - wx;
    if( (ye+wy) > screenSize.height )
      ye = screenSize.height - wy;

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
    wy = fr.getPreferredSize().height;
    xe = r.x + (r.width - wx) / 2;
    ye = r.y + (r.height - wy) / 2;

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
