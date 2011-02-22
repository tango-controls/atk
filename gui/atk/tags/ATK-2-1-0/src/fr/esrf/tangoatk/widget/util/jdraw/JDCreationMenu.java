package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** A convenience class to handle object creation Menu and Toolbar.
 * @see JDrawEditorFrame
 */
public class JDCreationMenu implements ActionListener {

  private JDrawEditor invoker;

  /** Create menu */
  private JMenu      createMenu;
  private JMenuItem  createLineMenuItem;
  private JMenuItem  createRectangleMenuItem;
  private JMenuItem  createRoundRectMenuItem;
  private JMenuItem  createEllipseMenuItem;
  private JMenuItem  createPolylineMenuItem;
  private JMenuItem  createLabelMenuItem;
  private JMenuItem  createSplineMenuItem;
  private JMenuItem  createImageMenuItem;
  private JMenuItem  createAxisMenuItem;

  /** JDrawable Swing object */
  private JMenu       swingMenu;
  private JMenuItem[] swingMenuItem;
  private JPopupMenu  swingPopupMenu;
  private JMenuItem[] swingPopupMenuItem;
  private String[] drawableItem = new String[0];

  /** The JDObject toolbar */
  private JToolBar objectToolBar;
  private JButton  objectToolLineBtn;
  private JButton  objectToolRectangleBtn;
  private JButton  objectToolRoundRectBtn;
  private JButton  objectToolEllipseBtn;
  private JButton  objectToolPolylineBtn;
  private JButton  objectToolLabelBtn;
  private JButton  objectToolSplineBtn;
  private JButton  objectToolImageBtn;
  private JButton  objectToolAxisBtn;
  private JButton  objectToolSwingBtn;

  private JLabel statusLabel;

  /**
   * Constructs menu and toolbar for JDObject creation.
   */
  JDCreationMenu() {

    invoker = null;
    statusLabel = null;

    // Create Menu -------------------------------------

    createLineMenuItem = new JMenuItem("Line");
    createLineMenuItem.addActionListener(this);
    createRectangleMenuItem = new JMenuItem("Rectangle");
    createRectangleMenuItem.addActionListener(this);
    createRoundRectMenuItem = new JMenuItem("Round Rectangle");
    createRoundRectMenuItem.addActionListener(this);
    createEllipseMenuItem = new JMenuItem("Ellipse");
    createEllipseMenuItem.addActionListener(this);
    createPolylineMenuItem = new JMenuItem("Polyline");
    createPolylineMenuItem.addActionListener(this);
    createLabelMenuItem = new JMenuItem("Label");
    createLabelMenuItem.addActionListener(this);
    createSplineMenuItem = new JMenuItem("Spline");
    createSplineMenuItem.addActionListener(this);
    createImageMenuItem = new JMenuItem("Image");
    createImageMenuItem.addActionListener(this);
    createAxisMenuItem = new JMenuItem("Axis");
    createAxisMenuItem.addActionListener(this);

    // JDrawable Swing object menu --------------------

    swingMenu = new JMenu("ATK Swing");
    swingPopupMenu = new JPopupMenu();
    drawableItem = JDrawableList.getDrawalbeList();
    swingMenuItem = new JMenuItem[drawableItem.length];
    swingPopupMenuItem = new JMenuItem[drawableItem.length];
    for(int i=0;i<drawableItem.length;i++) {
      swingMenuItem[i] = new JMenuItem(JDUtils.buildShortClassName(drawableItem[i]));
      swingPopupMenuItem[i] = new JMenuItem(JDUtils.buildShortClassName(drawableItem[i]));
      swingMenuItem[i].addActionListener(this);
      swingPopupMenuItem[i].addActionListener(this);
      swingMenu.add(swingMenuItem[i]);
      swingPopupMenu.add(swingPopupMenuItem[i]);
    }

    createMenu = new JMenu("Create");
    createMenu.setMnemonic('C');
    createMenu.add(createLineMenuItem);
    createMenu.add(createRectangleMenuItem);
    createMenu.add(createRoundRectMenuItem);
    createMenu.add(createEllipseMenuItem);
    createMenu.add(createLabelMenuItem);
    createMenu.add(createSplineMenuItem);
    createMenu.add(createPolylineMenuItem);
    createMenu.add(createImageMenuItem);
    createMenu.add(createAxisMenuItem);
    createMenu.add(swingMenu);

    // The toolbal -----------------------------------

    objectToolBar = new JToolBar();
    objectToolLineBtn = JDUtils.createIconButton("jdraw_line",false,"Create a line",this);
    objectToolRectangleBtn = JDUtils.createIconButton("jdraw_rectangle",false,"Create a rectangle",this);
    objectToolRoundRectBtn = JDUtils.createIconButton("jdraw_roundrect",false,"Create a rounded rectangle",this);
    objectToolEllipseBtn = JDUtils.createIconButton("jdraw_circle",false,"Create an ellipse",this);
    objectToolPolylineBtn = JDUtils.createIconButton("jdraw_polyline",false,"Create a polyline",this);
    objectToolLabelBtn = JDUtils.createIconButton("jdraw_label",false,"Create a label",this);
    objectToolSplineBtn = JDUtils.createIconButton("jdraw_spline",false,"Create a spline",this);
    objectToolImageBtn = JDUtils.createIconButton("jdraw_image",false,"Create an image",this);
    objectToolAxisBtn = JDUtils.createIconButton("jdraw_axis",false,"Create an axis",this);
    objectToolSwingBtn = JDUtils.createIconButton("jdraw_swing",false,"Create a swing object",this);
    objectToolBar.add(objectToolLineBtn);
    objectToolBar.add(objectToolRectangleBtn);
    objectToolBar.add(objectToolRoundRectBtn);
    objectToolBar.add(objectToolEllipseBtn);
    objectToolBar.add(objectToolLabelBtn);
    objectToolBar.add(objectToolPolylineBtn);
    objectToolBar.add(objectToolSplineBtn);
    objectToolBar.add(objectToolImageBtn);
    objectToolBar.add(objectToolAxisBtn);
    objectToolBar.add(objectToolSwingBtn);
    objectToolBar.setOrientation(JToolBar.VERTICAL);

  }


  /**
   * Returns the JDObject creation toolbar.
   */
  public JToolBar getToolbar() {
    return objectToolBar;
  }

  /**
   * Returns the JDObject creation menu.
   */
  public JMenu getMenu() {
    return createMenu;
  }

  /**
   * Sets the editor where object will be created.
   * @param editor Parent editor.
   */
  public void setEditor(JDrawEditor editor) {
    invoker = editor;
  }

  /**
   * Sets the status label where are printed creation information.
   * @param label Label
   */
  public void setStatusLabel(JLabel label) {
    statusLabel = label;
  }
  
  private void setStatus(String s) {
    if(statusLabel!=null)
      statusLabel.setText(s);
  }

  private void invoke(int mode) {
    if(invoker!=null)
      invoker.create(mode);
  }

  private void invoke(int mode,String param) {
    if(invoker!=null)
      invoker.create(mode,param);
  }

  public void actionPerformed(ActionEvent e) {

    Object src = e.getSource();
    if (src == objectToolRectangleBtn || src == createRectangleMenuItem) {
      invoke(JDrawEditor.CREATE_RECTANGLE);
      setStatus("Left click and drag to create a rectangle");
    } else if (src == objectToolRoundRectBtn || src == createRoundRectMenuItem) {
      invoke(JDrawEditor.CREATE_RRECTANGLE);
      setStatus("Left click and drag to create a rounded rectangle");
    } else if (src == objectToolLineBtn || src == createLineMenuItem) {
      invoke(JDrawEditor.CREATE_LINE);
      setStatus("Left click and drag to create a line");
    } else if (src == objectToolEllipseBtn || src == createEllipseMenuItem) {
      invoke(JDrawEditor.CREATE_ELLIPSE);
      setStatus("Left click and drag to create an ellipse");
    } else if (src == objectToolPolylineBtn || src == createPolylineMenuItem) {
      invoke(JDrawEditor.CREATE_POLYLINE);
      setStatus("Left click to create a new point and right click to create the last point");
    } else if (src == objectToolLabelBtn || src == createLabelMenuItem) {
      invoke(JDrawEditor.CREATE_LABEL);
      setStatus("Left click to create the label");
    } else if (src == objectToolSplineBtn || src == createSplineMenuItem) {
      invoke(JDrawEditor.CREATE_SPLINE);
      setStatus("Left click to create a new point and right click to create the last point");
    } else if (src == objectToolImageBtn || src == createImageMenuItem) {
      invoke(JDrawEditor.CREATE_IMAGE);
      setStatus("Left click to create an image");
    } else if (src == objectToolAxisBtn || src == createAxisMenuItem) {
      invoke(JDrawEditor.CREATE_AXIS);
      setStatus("Left click to create an axis");
    } else if (src == objectToolSwingBtn) {
      swingPopupMenu.show(objectToolSwingBtn,32,0);
    } else {

      // Now search within Jdrawable Swing Object list
      boolean found = false;
      int i = 0;
      while(!found && i<drawableItem.length) {
        found = (src == swingMenuItem[i]) ||
                (src == swingPopupMenuItem[i]);
        if(!found) i++;
      }
      if( found ) {
        // We create a Swing object
        invoke(JDrawEditor.CREATE_SWINGOBJECT,drawableItem[i]);
        setStatus("Left click to create a JDSwingObject : " +
                            JDUtils.buildShortClassName(drawableItem[i]));
      }

    }

  }

}
