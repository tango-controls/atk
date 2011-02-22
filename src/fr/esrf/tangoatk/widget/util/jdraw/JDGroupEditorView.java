/** A JDGroup editor */
package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

class JDGroupEditor extends JDrawEditor {

  JDGroupEditor(int mode) {
    super(mode);
  }

  // Handle Cut,Copy paste and delete shortcut here (not done by the menu here)
  public void keyPressed(KeyEvent e) {

    switch (e.getKeyCode()) {
      case KeyEvent.VK_C:
        if (e.isControlDown())
          copySelection();
        break;
      case KeyEvent.VK_X:
        if (e.isControlDown())
          cutSelection();
        break;
      case KeyEvent.VK_V:
        if (e.isControlDown())
          create(JDrawEditor.CLIPBOARD);
        break;
      case KeyEvent.VK_DELETE:
        deleteSelection();
        break;
      case KeyEvent.VK_G:
        if (e.isControlDown())
          showGroupEditorWindow();
        break;
      default:
        super.keyPressed(e);
    }

  }

}

class JDGroupEditorView extends JComponent implements JDrawEditorListener,ActionListener {

  JDGroup       theGroup;
  JDrawEditor   invoker;
  JDGroupEditor theEditor;
  JScrollPane   theEditorView;
  Rectangle     oldRect;

  JToolBar      toolBar;
  JButton       lineBtn;
  JButton       rectangleBtn;
  JButton       roundRectBtn;
  JButton       circleBtn;
  JButton       polylineBtn;
  JButton       labelBtn;
  JButton       splineBtn;
  JButton       imageBtn;

  public JDGroupEditorView(JDGroup g, JDrawEditor jc) {

    int margin=20;
    int i;
    theGroup = g;
    invoker = jc;
    oldRect = g.getRepaintRect();
    setLayout(new BorderLayout());

    // Create the editor
    theEditor = new JDGroupEditor(JDrawEditor.MODE_EDIT_GROUP);
    Rectangle r = g.getBoundRect();
    theEditor.addEditorListener(this);
    theEditor.setTranslation(-r.x + margin ,-r.y + margin);
    theEditor.setPreferredSize(new Dimension(r.width+2*margin,r.height+2*margin));

    for(i=0;i<g.getChildrenNumber();i++)
      theEditor.addObject(g.getChildAt(i));

    theEditorView = new JScrollPane(theEditor);
    add(theEditorView,BorderLayout.CENTER);

    theEditor.setZoomFactor(invoker.getZoomFactor());

    // --- toolbar

    toolBar = new JToolBar();
    lineBtn = JDUtils.createIconButton("jdraw_line",false,"Create a line",this);
    rectangleBtn = JDUtils.createIconButton("jdraw_rectangle",false,"Create a rectangle",this);
    roundRectBtn = JDUtils.createIconButton("jdraw_roundrect",false,"Create a rounded rectangle",this);
    circleBtn = JDUtils.createIconButton("jdraw_circle",false,"Create an ellipse",this);
    polylineBtn = JDUtils.createIconButton("jdraw_polyline",false,"Create a polyline",this);
    labelBtn = JDUtils.createIconButton("jdraw_label",false,"Create a label",this);
    splineBtn = JDUtils.createIconButton("jdraw_spline",false,"Create a spline",this);
    imageBtn = JDUtils.createIconButton("jdraw_image",false,"Create an image",this);
    toolBar.add(lineBtn);
    toolBar.add(rectangleBtn);
    toolBar.add(roundRectBtn);
    toolBar.add(circleBtn);
    toolBar.add(labelBtn);
    toolBar.add(polylineBtn);    
    toolBar.add(splineBtn);
    toolBar.add(imageBtn);
    toolBar.setOrientation(JToolBar.VERTICAL);
    add(toolBar,BorderLayout.WEST);

  }


  // ---------------------------------------------------------

  public void creationDone() {}
  public void selectionChanged() {}
  public void clipboardChanged() {}
  public void valueChanged() {
    rebuildGroup();
  }
  public void sizeChanged() {
    theEditorView.revalidate();
    repaint();
  }

  // ---------------------------------------------------------

  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src==rectangleBtn) {
      theEditor.create(JDrawEditor.RECTANGLE);
    } else if (src==roundRectBtn) {
      theEditor.create(JDrawEditor.RRECTANGLE);
    } else if (src==lineBtn) {
      theEditor.create(JDrawEditor.LINE);
    } else if (src==circleBtn) {
      theEditor.create(JDrawEditor.ELLIPSE);
    } else if (src==polylineBtn) {
      theEditor.create(JDrawEditor.POLYLINE);
    } else if (src==labelBtn) {
      theEditor.create(JDrawEditor.LABEL);
    } else if (src==splineBtn) {
      theEditor.create(JDrawEditor.SPLINE);
    } else if (src==imageBtn) {
      theEditor.create(JDrawEditor.IMAGE);
    }
  }

  // ---------------------------------------------------------
  private void rebuildGroup() {
     // Rebuild the group
     theGroup.setChildrenList(theEditor.getObjects());
     Rectangle r = theGroup.getRepaintRect();
     invoker.repaint(oldRect.union(r));
     // Trigger the parent valueChanged
     invoker.fireValueChanged();
     oldRect=r;
     JDUtils.modified=true;
  }

}
