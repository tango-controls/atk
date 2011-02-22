package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.util.Vector;
import java.awt.*;
import java.awt.event.ActionListener;

class JDUtils {

  static boolean modified;
  private static Insets bMargin = new Insets(3,3,3,3);
  private static Insets zMargin = new Insets(0, 0, 0, 0);
  private static Class theClass=null;
  static Font  labelFont  = new Font("Dialog", Font.PLAIN, 12);
  static Font  labelFontBold  = new Font("Dialog", Font.BOLD, 12);
  static Color labelColor = new Color(85, 87, 140);
  private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

  static private void init() {
    if( theClass==null ) {
      String className = "jdraw.JDUtils";
      try {
        theClass = Class.forName(className);
      } catch (Exception e) {
        System.out.println("JDUtils.init() Class not found: " + className);
      }
    }
  }

  static private JDialog buildModalDialog(JComponent invoker) {

    Object parent = invoker.getRootPane().getParent();
    JDialog dlg;

    if (parent instanceof JDialog) {
      dlg = new JDialog((JDialog) parent, true);
    } else if (parent instanceof JFrame) {
      dlg = new JDialog((JFrame) parent, true);
    } else {
      dlg = new JDialog((JFrame) null, true);
    }

    return dlg;

  }

  static public boolean showPropertyDialog(JComponent invoker, Vector objects,int panel) {

    if (objects.size() == 0)
      return false;

    // Get the parent

    JDialog propDlg = buildModalDialog(invoker);

    // Check object instance and make object array
    JDObject[] objs = new JDObject[objects.size()];
    boolean sameClass = true;
    int i = 1;
    objs[0] = (JDObject) objects.get(0);
    Class firstClass = objs[0].getClass();
    for (i = 1; i < objs.length; i++) {
      objs[i] = (JDObject) objects.get(i);
      sameClass &= firstClass.equals(objs[i].getClass());
    }

    // Create panel
    JPanel innerPanel = new JPanel();
    JTabbedPane innerPane = new JTabbedPane();

    // Common properties
    JPanel p0 = new JDObjectPanel(objs, invoker);
    innerPane.add(p0,"Graphics");

    // Specific properties
    if (sameClass && objs[0] instanceof JDLabel) {
      JDLabel[] objs2 = new JDLabel[objs.length];
      for (i = 0; i < objs.length; i++) objs2[i] = (JDLabel) objs[i];
      innerPane.add(new JDLabelPanel(objs2, invoker), "Text");
    }

    if (sameClass && objs[0] instanceof JDLine) {
      JDLine[] objs2 = new JDLine[objs.length];
      for (i = 0; i < objs.length; i++) objs2[i] = (JDLine) objs[i];
      innerPane.add(new JDLinePanel(objs2, invoker), "Line");
    }

    if (sameClass && objs[0] instanceof JDPolyline) {
      JDPolyline[] objs2 = new JDPolyline[objs.length];
      for (i = 0; i < objs.length; i++) objs2[i] = (JDPolyline) objs[i];
      innerPane.add(new JDPolylinePanel(objs2, invoker), "Polyline");
    }

    if (sameClass && objs[0] instanceof JDEllipse) {
      JDEllipse[] objs2 = new JDEllipse[objs.length];
      for (i = 0; i < objs.length; i++) objs2[i] = (JDEllipse) objs[i];
      innerPane.add(new JDEllipsePanel(objs2, invoker), "Ellipse");
    }

    if (sameClass && objs[0] instanceof JDRoundRectangle) {
      JDRoundRectangle[] objs2 = new JDRoundRectangle[objs.length];
      for (i = 0; i < objs.length; i++) objs2[i] = (JDRoundRectangle) objs[i];
      innerPane.add(new JDRoundRectanglePanel(objs2, invoker), "Corner");
    }

    if (sameClass && objs[0] instanceof JDImage) {
      JDImage[] objs2 = new JDImage[objs.length];
      for (i = 0; i < objs.length; i++) objs2[i] = (JDImage) objs[i];
      innerPane.add(new JDImagePanel(objs2, invoker), "Image");
    }

    // Dynamic properties
    JPanel p1 = new JDValuePanel(objs, invoker);
    innerPane.add(p1, "Value");
    JPanel p2 = new JDExtensionPanel(objs, invoker);
    innerPane.add(p2, "Extensions");

    innerPanel.add(innerPane);
    propDlg.setContentPane(innerPanel);

    String title = "Properties";
    JDObject p = (JDObject) objects.get(0);
    if (sameClass) title += " [" + objects.size() + " " + p.toString() + " selected]";
    else           title += " [" + objects.size() + " objects selected]";
    propDlg.setTitle(title);
    propDlg.setResizable(false);
    centerDialog(propDlg);

    switch(panel) {
      case 0:
        innerPane.setSelectedComponent(p0);
        break;
      case 1:
        innerPane.setSelectedComponent(p1);
        break;
    }
    modified=false;
    propDlg.setVisible(true);
    return modified;
  }

  static public boolean showBrowserDialog(JDrawEditor invoker, Vector objects) {

    if (objects.size() == 0)
      return false;

    JDialog propDlg = buildModalDialog(invoker);

    // Set the browser panel
    JDObject[] objs = new JDObject[objects.size()];
    for(int i=0;i<objs.length;i++) objs[i]=(JDObject)objects.get(i);
    propDlg.setContentPane(new JDBrowserPanel(objs, invoker));
    propDlg.setTitle("Object browser");
    centerDialog(propDlg);

    modified=false;
    propDlg.setVisible(true);

    // Rebuild old selection
    invoker.unselectAll();
    invoker.selectObjects(objs);

    return modified;

  }

  static public boolean showGroupEditorDialog(JDrawEditor invoker, JDGroup g) {

    JDialog propDlg = buildModalDialog(invoker);
    JDGroupEditorView gEdit = new JDGroupEditorView(g, invoker);
    propDlg.setContentPane(gEdit);
    propDlg.setTitle("Group Editor [" + g.getName() + "]");
    propDlg.setResizable(true);
    centerDialog(propDlg);
    propDlg.setVisible(true);
    return modified;

  }

  static public boolean showTransformDialog(JComponent invoker, Vector objects) {

    if (objects.size() == 0)
      return false;

    JDialog propDlg = buildModalDialog(invoker);

    JDObject[] objs = new JDObject[objects.size()];
    for (int i = 0; i < objs.length; i++)
      objs[i] = (JDObject) objects.get(i);
    // Transform properties
    propDlg.setContentPane(new JDTransformPanel(objs, invoker));

    String title = "Transformation";
    JDObject p = (JDObject) objects.get(0);
    if (objects.size() == 1) title += ": " + p.getName();
    propDlg.setTitle(title);
    centerDialog(propDlg);
    propDlg.setResizable(false);

    modified=false;
    propDlg.setVisible(true);
    return modified;

  }

  static public boolean showGlobalDialog(JDrawEditor invoker) {

    JDialog propDlg = buildModalDialog(invoker);

    // Transform properties
    propDlg.setContentPane(new JDGlobalPanel(invoker));

    String title = "Global graph properties";
    propDlg.setTitle(title);
    centerDialog(propDlg);
    propDlg.setResizable(false);

    modified=false;
    propDlg.setVisible(true);
    return modified;

  }

  static public JDValueProgram showValueMappingDialog(JComponent invoker, JDObject[] objs,String desc,int type,JDValueProgram defMapper) {

    if (objs.length == 0)
      return null;

    JDialog propDlg = buildModalDialog(invoker);
    JDValueMappingPanel vp = new JDValueMappingPanel(objs, invoker,desc,type,defMapper);

    // Transform properties
    propDlg.setContentPane(vp);

    String title = "Mapping for " + desc;
    title += " [" + objs.length + " objects selected]";
    propDlg.setTitle(title);
    centerDialog(propDlg);
    propDlg.setResizable(false);

    propDlg.setVisible(true);
    if(vp.hasChanged())
      return vp.getMapper();
    else
      return null;
  }

  static boolean showBooleanDialog(JComponent invoker,String name,boolean defaultValue) {

    JDialog propDlg = buildModalDialog(invoker);
    JPanel panel = new JPanel();
    panel.setLayout(null);
    JComboBox boolCombo = new JComboBox();
    boolCombo.setFont(labelFont);
    boolCombo.addItem("False");
    boolCombo.addItem("True");
    boolCombo.setSelectedIndex(defaultValue?1:0);
    panel.add(boolCombo);
    boolCombo.setBounds(10,10,150,25);
    panel.setPreferredSize(new Dimension(170,40));
    propDlg.setContentPane(panel);
    propDlg.setTitle(name);
    centerDialog(propDlg);
    propDlg.setVisible(true);
    return (boolCombo.getSelectedIndex()==1);

  }

  static int showIntegerDialog(JComponent invoker,String name,int defaultValue) {
    String str = JOptionPane.showInputDialog(invoker, "Integer value", name, JOptionPane.INFORMATION_MESSAGE);
    int ret = defaultValue;
    if (str != null) {
      try {
        ret = Integer.parseInt(str);
      } catch (Exception e) {
      }
    }
    return ret;
  }

  static public Point getTopLeftCorner(JDObject[] list) {
    // Compute scaling origin
    int xOrg = 65536;
    int yOrg = 65536;
    Rectangle r;
    for (int i = 0; i < list.length; i++) {
      r = list[i].getBoundRect();
      if (r.x < xOrg) xOrg = r.x;
      if (r.y < yOrg) yOrg = r.y;
    }
    return new Point(xOrg, yOrg);
  }

  static public Point getTopLeftCorner(Vector list) {
    // Compute scaling origin
    int xOrg = 65536;
    int yOrg = 65536;
    Rectangle r;
    for (int i = 0; i < list.size(); i++) {
      r = ((JDObject) list.get(i)).getBoundRect();
      if (r.x < xOrg) xOrg = r.x;
      if (r.y < yOrg) yOrg = r.y;
    }
    return new Point(xOrg, yOrg);
  }

  static public Point getCenter(Vector list) {
    // Compute scaling origin
    Rectangle r;
    int x1 = 65536;
    int y1 = 65536;
    int x2 = -65536;
    int y2 = -65536;
    for (int i = 0; i < list.size(); i++) {
      r = ((JDObject) list.get(i)).getBoundRect();
      if (r.x < x1) x1 = r.x;
      if (r.x + r.width > x2) x2 = r.x + r.width;
      if (r.y < y1) y1 = r.y;
      if (r.y + r.height > y2) y2 = r.y + r.height;
    }
    return new Point((x1 + x2) / 2, (y1 + y2) / 2);
  }

  static public Point getCenter(JDObject[] list) {
    // Compute scaling origin
    Rectangle r;
    int x1 = 65536;
    int y1 = 65536;
    int x2 = -65536;
    int y2 = -65536;
    for (int i = 0; i < list.length; i++) {
      r = list[i].getBoundRect();
      if (r.x < x1) x1 = r.x;
      if (r.x + r.width > x2) x2 = r.x + r.width;
      if (r.y < y1) y1 = r.y;
      if (r.y + r.height > y2) y2 = r.y + r.height;
    }
    return new Point((x1 + x2) / 2, (y1 + y2) / 2);
  }

  static public Point getBottomRightCorner(Vector list) {
    // Compute origin
    int xOrg = -65536;
    int yOrg = -65536;
    Rectangle r;
    for (int i = 0; i < list.size(); i++) {
      r = ((JDObject) list.get(i)).getBoundRect();
      if (r.x+r.width  > xOrg) xOrg = r.x+r.width;
      if (r.y+r.height > yOrg) yOrg = r.y+r.height;
    }
    return new Point(xOrg, yOrg);

  }

  static public JButton createIconButton(String name,boolean hasDisa,String tipText,ActionListener l) {
    init();
    if( theClass!=null ) {
      JButton nB = new JButton(new ImageIcon(theClass.getResource("/fr/esrf/tangoatk/widget/util/jdraw/gif/" + name + ".gif")));
      nB.setPressedIcon(new ImageIcon(theClass.getResource("/fr/esrf/tangoatk/widget/util/jdraw/gif/" + name + "_push.gif")));
      if (hasDisa)
        nB.setDisabledIcon(new ImageIcon(theClass.getResource("/fr/esrf/tangoatk/widget/util/jdraw/gif/" + name + "_disa.gif")));
      nB.setToolTipText(tipText);
      nB.setMargin(bMargin);
      nB.setBorder(null);
      nB.addActionListener(l);
      return nB;
    } else {
      return new JButton(name);
    }
  }

  static public JButton createSetButton(ActionListener l) {
    JButton b = new JButton("...");
    b.setMargin(zMargin);
    b.setForeground(Color.BLACK);
    if(l!=null) b.addActionListener(l);
    return b;
  }

  static public JLabel createLabel(String name) {
    JLabel ret = new JLabel(name);
    ret.setFont(labelFont);
    ret.setForeground(labelColor);
    return ret;
  }

  static public Border createTitleBorder(String name) {
    return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), name,
                                            TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION,
                                            labelFontBold, labelColor);
  }

  static public JCheckBox createCheckBox(String name,ActionListener l) {
    JCheckBox cb = new JCheckBox(name);
    cb.setFont(labelFont);
    cb.setForeground(labelColor);
    if(l!=null) cb.addActionListener(l);
    return cb;
  }

  static void centerDialog(Dialog dlg) {
    centerDialog(dlg,dlg.getPreferredSize().width,dlg.getPreferredSize().height);
  }

  static void centerDialog(Dialog dlg,int dlgWidth,int dlgHeight) {

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
    // Saturate
    if(wx>screenSize.width)  wx = screenSize.width;
    if(wy>screenSize.height) wy = screenSize.height;
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
    //System.out.println("Centering dialog to :"+xe+","+ye+","+wx+","+wy);
    dlg.setBounds(xe, ye, wx, wy);

  }

  static void centerFrameOnScreen(Frame fr) {

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

  static void computeSpline(double x1,double y1,double x2,double y2,
                            double x3,double y3,double x4,double y4,
                            int step,boolean full,int start,
                            Vector pts,int[] ptsx,int[] ptsy) {

    double k,ks,kc;
    int j;
    double x,y;

    //************************
    // Compute the spline
    //************************

    double stp = 1.0 / (double) step;
    k = 0;
    j = 0;

    while (j <= step) {
      ks = k * k;
      kc = ks * k;

      x = (1.0 - 3.0 * k + 3.0 * ks - kc) * x1
              + 3.0 * (k - 2.0 * ks + kc) * x2
              + 3.0 * (ks - kc) * x3
              + kc * x4;

      y = (1.0 - 3.0 * k + 3.0 * ks - kc) * y1
              + 3.0 * (k - 2.0 * ks + kc) * y2
              + 3.0 * (ks - kc) * y3
              + kc * y4;

      // Don't forget the last point
      if((full) || (j < step)) {
        if(pts!=null) {
          double[] pt = new double[2];
          pt[0] = x;
          pt[1] = y;
          pts.add(pt);
        } else {
          ptsx[start+j] = (int)(x+0.5);
          ptsy[start+j] = (int)(y+0.5);
        }
      }

      k = k + stp;
      j++;
    }

  }

}
