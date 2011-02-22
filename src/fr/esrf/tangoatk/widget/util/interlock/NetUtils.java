/**
 * A set of class to handle a network editor and its viewer.
 *
 * Author: Jean Luc PONS
 * Date: Jul 1, 2004
 * (c) ESRF 2004
 */

package fr.esrf.tangoatk.widget.util.interlock;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import java.awt.*;

/** Utility class. */
public class NetUtils {

  final static String[] fntList = {"Serif" , "SansSerif" , "Monospaced" , "Dialog" , "DialogInput"};
  final static Color fColor    = new Color(99, 97, 156);
  final static Font labelFont  = new Font("Dialog", Font.PLAIN, 12);
  final static Font labelbFont = new Font("Dialog", Font.BOLD , 12);
  final static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

  /**
   * Create a JMenuItem.
   * @param name Name
   * @param key Accelerator key ,0 for none
   * @param modifier Key modifier ,0 for none
   * @param a Action listener
   * @return Created JMenuItem
   */
  public static JMenuItem createMenuItem(String name, int key, int modifier,ActionListener a) {
    JMenuItem m = new JMenuItem();
    m.setText(name);
    if (key != 0)
      m.setAccelerator(KeyStroke.getKeyStroke(key, modifier));
    m.addActionListener(a);
    return m;
  }

/**
 * Create a named title border.
 * @param name Border name
 * @return Border object
 */
  public static Border createTitleBorder(String name) {
    return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), name, TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, labelbFont, fColor);
  }

  /**
   * Center the given dialog according to its parent. If the dialog is not parented
   * (null parent), It will appear at the center of the screen. The dialog is
   * not displayed.
   * <p>Note: This function has been designed to work with 'heavyWeight' system dependant
   * awt window which doesn't use a layout manager (null layout).
   * @param dlg the dialog.
   * @param dlgWidth desired width of the JDialog content pane.
   * @param dlgHeight desired height of the JDialog content pane.
   */
  public static void centerDialog(Dialog dlg,int dlgWidth,int dlgHeight) {

    // Get the parent rectangle
    Rectangle r = new Rectangle(0,0,0,0);
    if (dlg.getParent() != null)
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
   * Center the given frame on screen. Call it before showing this frame.
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

  // -----------------------------------------------------------------------------------
  
  static JLabel createLabel(String msg) {
    JLabel label = new JLabel(msg);
    label.setFont(labelFont);
    label.setForeground(fColor);
    return label;
  }

  static boolean fontEquals(Font f1,Font f2) {
    return (f1.getName().equalsIgnoreCase(f2.getName())) &&
           (f1.getSize() == f2.getSize() ) &&
           (f1.getStyle() == f2.getStyle());
  }

  static JComboBox createFontCombo() {

    JComboBox ret = new JComboBox();
    ret.setEditable(false);
    ret.setFont(labelFont);
    for(int i=0;i<fntList.length;i++)
      ret.addItem(fntList[i]);
    return ret;

  }

  static int getIdx(String fntName) {

    int i=0;
    boolean found=false;

    while(i<fntList.length && !found) {
      found = fntName.equalsIgnoreCase(fntList[i]);
      if(!found) i++;
    }

    if( found )
      return i;
    else
      return -1;

  }

}
