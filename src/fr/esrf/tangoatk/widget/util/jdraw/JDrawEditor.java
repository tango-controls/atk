/**
 * A set of class to handle a graphical synoptic viewer (vector drawing) and its editor.
 */
package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.Vector;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

/** The graph editor/viewer component */
public class JDrawEditor extends JComponent implements MouseMotionListener, MouseListener, ActionListener, KeyListener {

  // Mode of the editor
  /** Editor is in classic edition mode */
  final static public int MODE_EDIT  = 1;
  /** Group edition mode, this is a restricted edition mode (no undo possible) */
  final static public int MODE_EDIT_GROUP = 2;
  /** Play mode, play object according to their value , in this mode no contextual menu is displayed */
  final static public int MODE_PLAY = 3;

  // Creation mode
  final static public int RECTANGLE = 1;
  final static public int LINE = 2;
  final static public int ELLIPSE = 3;
  final static public int POLYLINE = 4;
  final static public int LABEL = 5;
  final static public int SPLINE = 6;
  final static public int CLIPBOARD = 7;
  final static public int RRECTANGLE = 8;
  final static public int IMAGE = 9;

  final static private int undoLength=20;

  // Private declaration
  private Vector objects;
  private Vector clipboard;
  private Vector undo;
  private int undoPos;
  private int selSummit;
  private int curObject;
  private Vector selObjects;
  private boolean isDraggingSummit;
  private boolean isDraggingObject;
  private boolean isDraggingSelection;
  private boolean hasMoved;
  private int lastX;
  private int lastY;
  private int selX1;
  private int selY1;
  private int selX2;
  private int selY2;
  private int creationMode;
  private JDObject lastCreatedObject=null;
  private Vector tmpPoints;
  private JPopupMenu objMenu;
  private JDPolyline editedPolyline;
  private int zoomFactor = 0;
  private int sizeX;
  private int sizeY;
  private String lastFileName = "";
  private boolean needToSave = false;
  Vector listeners;
  private int mode;
  private int transx;
  private int transy;
  private JDObject pressedObject=null;
  private JDObject motionObject=null;
  private boolean alignToGrid=false;
  private int GRID_SIZE=16;
  private boolean gridVisible=false;

  // ------- Contextual menu ----------------
  private JSeparator sep1;
  private JSeparator sep2;
  private JSeparator sep3;
  private JSeparator sep4;
  private JSeparator sep5;

  private JMenuItem infoMenuItem;

  private JMenuItem cutMenuItem;
  private JMenuItem copyMenuItem;
  private JMenuItem pasteMenuItem;
  private JMenuItem deleteMenuItem;

  private JMenuItem zoomInMenuItem;
  private JMenuItem zoomOutMenuItem;

  private JMenuItem groupMenuItem;
  private JMenuItem ungroupMenuItem;

  private JMenuItem deleteSummitMenuItem;
  private JMenuItem breakShapeMenuItem;

  private JMenuItem raiseMenuItem;
  private JMenuItem lowerMenuItem;
  private JMenuItem frontMenuItem;
  private JMenuItem backMenuItem;


  static private Cursor hCursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
  static private Cursor vCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
  static private Cursor nwCursor = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
  static private Cursor neCursor = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
  static private Cursor seCursor = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
  static private Cursor swCursor = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
  static private Cursor bCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
  static private Cursor dCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

  final static Color defaultBackground = new Color(230, 230, 230);

  // -----------------------------------------------------
  // Construction
  // -----------------------------------------------------

  /**
   * Contruct a JDraw editor in the specified mode.
   * @param mode Mode of the editor
   * @see JDrawEditor#MODE_EDIT
   * @see JDrawEditor#MODE_EDIT_GROUP
   * @see JDrawEditor#MODE_PLAY
   */
  public JDrawEditor(int mode) {
    setLayout(null);
    this.mode = mode;
    initComponents();
  }

  private void initComponents() {

    objects = new Vector();
    selObjects = new Vector();

    // Tests ----------------------------------------------------------

    /*
    Point[] ptl = {new Point(340, 120), new Point(350, 240), new Point(434, 320), new Point(482, 228)};

    objects.add(new JDRectangle("Rectangle1", 100, 100, 80, 50));
    objects.add(new JDRectangle("Rectangle2", 200, 120, 180, 70));
    objects.add(new JDRectangle("Rectangle3", 50, 300, 80, 150));
    objects.add(new JDPolyline("Poly1", ptl));
    objects.add(new JDEllipse("Ellipse1", 150, 300, 80, 150));
    JDLabel jl = new JDLabel("Label", "Jean-Luc\nLigne 2\nLigne 3", 230, 178);
    objects.add(jl);

    ((JDObject) objects.get(1)).setBackground(Color.cyan);
    ((JDObject) objects.get(3)).setBackground(Color.red);
    ((JDObject) objects.get(4)).setBackground(Color.blue);
    */
    //jl.setOrientation(JDLabel.BOTTOM_TO_TOP);

    //--------------------------------------------------------------------

    sizeX = 800;
    sizeY = 600;
    setBackground(defaultBackground);
    setOpaque(true);
    isDraggingSummit = false;
    isDraggingObject = false;
    isDraggingSelection = false;
    hasMoved=false;
    selX1 = selX2 = selY1 = selY2 = 0;
    creationMode = 0;
    transx = transy = 0;
    listeners = new Vector();
    needToSave = false;

    switch(mode) {
      case MODE_EDIT:
        clipboard = new Vector();
        tmpPoints = new Vector();
        undo = new Vector();
        clearUndo();
        createContextualMenu();
        break;
      case MODE_EDIT_GROUP:
        tmpPoints = new Vector();
        clipboard = new Vector();
        createContextualMenu();
        break;
      case MODE_PLAY:
        break;
    }


    addKeyListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);

  }

  // -----------------------------------------------------
  // Editing stuff
  // -----------------------------------------------------

  public void setGridVisible(boolean b) {
    gridVisible=b;
    repaint();
  }

  public boolean isGridVisible() {
    return gridVisible;
  }

  public void setGridSize(int size) {
    if(size>1) {
      GRID_SIZE = size;
      repaint();
    }
  }

  public int getGridSize() {
    return GRID_SIZE;
  }

  public void setAlignToGrid(boolean b) {
    alignToGrid = b;
  }

  public boolean isAlignToGrid() {
    return alignToGrid;
  }

  /** Returns the mode of the editor */
  public int getMode() {
    return mode;
  }

  public void selectObject(JDObject obj) {
    if (obj != null && mode!=MODE_PLAY) {
      if (!isSelected(obj))
        selObjects.add(obj);
      repaint(obj.getRepaintRect());
    }
  }

  public void unselectObject(JDObject obj) {
    if (obj != null && mode!=MODE_PLAY) {
      selObjects.remove(obj);
      repaint(obj.getRepaintRect());
    }
  }

  public boolean isSelected(JDObject obj) {
    if( mode==MODE_PLAY ) return false;
    return selObjects.contains(obj);
  }

  public void selectObjects(JDObject[] objs) {
    if (objs.length>0 && mode!=MODE_PLAY && selObjects.size()==0) {
      for (int i = 0; i < objs.length; i++)
        selObjects.add(objs[i]);
      repaint(buildRepaintRect(selObjects));
    }
  }

  /** Get number of object */
  public int getObjectNumber() {
    return objects.size();
  }

  /** Get the JDObject at the specified position */
  public JDObject getObjectAt(int idx) {
    return (JDObject)objects.get(idx);
  }

  /* Used for read only purpose , vector should not be modified by this way */
  public Vector getObjects() {
    return objects;
  }

  /** Unselect all object */
  public void unselectAll() {
    if(mode==MODE_PLAY) return;
    repaint(buildRepaintRect(selObjects));
    selObjects.clear();
  }

  /** Select all object */
  public void selectAll() {
    if(mode==MODE_PLAY) return;
    selObjects.clear();
    selObjects.addAll(objects);
    repaint(buildRepaintRect(objects));
  }

  /** Sets the editor in creation mode */
  public void create(int what) {
    if(mode==MODE_PLAY) return;
    creationMode = what;
  }

  /** Get number of selected object */
  public int getSelectionLength() {
    if(mode==MODE_PLAY) return 0;
    return selObjects.size();
  }

  /** Get number of object inside the clipboard */
  public int getClipboardLength() {
    if(mode==MODE_PLAY) return 0;
    return clipboard.size();
  }

  /** Shows the property window */
  public void showPropertyWindow() {
    if(mode==MODE_PLAY) return;
    boolean m = JDUtils.showPropertyDialog(this, selObjects,0);
    if (m) setNeedToSave(true,"Property change");
  }

  /** Shows the property window */
  public void showTransformWindow() {
    if(mode==MODE_PLAY) return;
    boolean m = JDUtils.showTransformDialog(this, selObjects);
    if (m) setNeedToSave(true,"transform");
  }

  /** Shows the object browser */
  public void showBrowserWindow() {
    if(mode==MODE_PLAY) return;
    boolean m = JDUtils.showBrowserDialog(this, selObjects);
    if (m) setNeedToSave(true,"Property change");
  }

  /** Shows the group editor dialog */
  public void showGroupEditorWindow() {
    if(mode==MODE_PLAY) return;
    if (selObjects.size() == 1) {
      JDObject p =(JDObject)selObjects.get(0);
      if (p instanceof JDGroup) {
        // Init the modified variable at the root level of group hiearchy
        if(mode==MODE_EDIT) JDUtils.modified = false;
        boolean m = JDUtils.showGroupEditorDialog(this, (JDGroup) p);
        if (m) setNeedToSave(true, "Group edit");
      }
    }
  }

  /** Generates java classes from the selection.
   * @see JDGroup#generateJavaClass
   */
  public void generateJavaClasses(String dirName) throws IOException {

    int i;

    String msgInfo="Destination directory: " + dirName + "\n\n";

    for (i = 0; i < selObjects.size(); i++) {
      JDObject p = (JDObject) selObjects.get(i);
      String fileName = dirName + "\\" + p.getName() + ".java";
      FileWriter f = new FileWriter(fileName);
      f.write("/* Class generated by JDraw */\n\n");
      f.write("import java.awt.*;\n\n");
      if (p instanceof JDGroup) {
        ((JDGroup) p).generateJavaClass(f);
        msgInfo += "   " + p.getName()+".java" + " : OK\n";
      } else {
        msgInfo += "   " + p.getName()+".java" + " : generation failed (Invalid object type)\n";
      }
      f.close();
    }

    JOptionPane.showMessageDialog(this,msgInfo,"Message",JOptionPane.INFORMATION_MESSAGE);

  }

  /** Shows the java generation file selection box */
  public void showGroupJavaWindow() {
    if (mode == MODE_PLAY) return;
    if (selObjects.size() >= 1) {
      JFileChooser jf = new JFileChooser(".");
      jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      jf.setDialogTitle("Choose directory for java classes generation");
      if (jf.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        try {
          generateJavaClasses(jf.getSelectedFile().getAbsolutePath());
        } catch (IOException e) {
          JOptionPane.showMessageDialog(this, "Error during java code generation.\n" + e.getMessage());
        }
      }
    }
  }

  /** Copy selection to clipboard */
  public void copySelection() {
    if(mode==MODE_PLAY) return;
    if(selObjects.size()==0) return;

    clipboard.clear();
    for (int i = 0; i < selObjects.size(); i++)
      clipboard.add(((JDObject)selObjects.get(i)).copy(0,0));
    fireClipboardChange();
  }

  /** Paste the selection at the specified pos */
  public void pasteClipboard(int x, int y) {
    if(mode==MODE_PLAY) return;
    if(clipboard.size()==0) return;

    unselectAll();
    Point org = JDUtils.getTopLeftCorner(clipboard);

    for (int i = 0; i < clipboard.size(); i++) {
      JDObject n = ((JDObject) clipboard.get(i)).copy(x - org.x, y - org.y);
      objects.add(n);
      selObjects.add(n);
    }

    setNeedToSave(true,"Paste");
    repaint(buildRepaintRect(selObjects));
    fireSelectionChange();
  }

  /** Scale selection */
  public void scaleSelection(double rx, double ry) {
    if(mode==MODE_PLAY) return;

    // Repaint old rectangle
    repaint(buildRepaintRect(selObjects));

    Point org = JDUtils.getCenter(selObjects);

    for (int i = 0; i < selObjects.size(); i++)
      ((JDObject) selObjects.get(i)).scale(org.x, org.y, rx, ry);

    setNeedToSave(true,"Scale");
    repaint(buildRepaintRect(selObjects));
  }

  /** Move the selection to clipboard */
  public void cutSelection() {
    if(mode==MODE_PLAY) return;
    if(selObjects.size()==0) return;
    clipboard.clear();
    objects.removeAll(selObjects);
    clipboard.addAll(selObjects);
    repaint(buildRepaintRect(selObjects));
    selObjects.clear();
    setNeedToSave(true,"Cut");
    fireSelectionChange();
    fireClipboardChange();

  }

  /** Delete selection from the draw */
  public void deleteSelection() {
    if(mode==MODE_PLAY) return;
    if(selObjects.size()==0) return;
    objects.removeAll(selObjects);
    repaint(buildRepaintRect(selObjects));
    selObjects.clear();
    setNeedToSave(true,"Delete");
    fireSelectionChange();
  }

  /**
   * Show the file selection box and call saveFile if a file is selected.
   * Trigger valueChanged() if a file is selected to be saved.
   * @param defaultDir default directory
   * @see JDrawEditor#saveFile
   */
  public void showSaveDialog(String defaultDir) {

    int ok = JOptionPane.YES_OPTION;
    JFileChooser chooser = new JFileChooser(defaultDir);
    if(lastFileName.length()>0)
      chooser.setSelectedFile(new File(lastFileName));
    int returnVal = chooser.showSaveDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      if (f != null) {
        if (f.exists()) ok = JOptionPane.showConfirmDialog(this, "Do you want to overwrite " + f.getName() + " ?", "Confirm overwrite", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
          try {
            saveFile(f.getAbsolutePath());
          } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error during saving file.\n" + e.getMessage());
          }
        }
      }
    }

  }

  /**
   * Save the current drawing to a file.
   * @param fileName File name
   * @throws IOException Exception containing error message when failed.
   */
  public void saveFile(String fileName) throws IOException {
    
    if(fileName.endsWith(".jlx")) {
      String fName = fileName.substring(0,fileName.lastIndexOf('.'));
      if( JOptionPane.showConfirmDialog(this,"Cannot save to jlx format , save to jdw format ?\n"+fName + ".jdw",
                                        "Save confirmation",JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
        return;
      fileName = fName + ".jdw";
    }

    FileWriter fw = new FileWriter(fileName);
    try {
      fw.write("JDFile v11 {\n");
      fw.write("  Global {\n");
      if(getBackground().getRGB()!=defaultBackground.getRGB())
        fw.write("background:" + getBackground().getRed() + "," + getBackground().getGreen() + "," + getBackground().getBlue());
      fw.write("  }\n");
      for (int i = 0; i < objects.size(); i++)
        ((JDObject) objects.get(i)).saveObject(fw, 1);
      fw.write("}\n");
      fw.close();
    } catch (IOException e) {
      fw.close();
      throw e;
    }
    lastFileName = fileName;
    setNeedToSave(false,"Save");

  }

  /** Save the current drawing to the file (Ask for filename if no filename has been previously set) */
  public void instantSave(String defaultDir) {

    if (lastFileName.length()>0) {
      try {
        saveFile(lastFileName);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error during saving file.\n" + e.getMessage());
      }
    } else {
      showSaveDialog(defaultDir);
    }

  }

  /** Load a jdraw grpahics file into the editor
   * Trigger valueChanged() if a file is selected to be loaded.
   * @param fileName File name
   * @throws IOException Exception containing error message when failed.
   * @see JDrawEditorListener#valueChanged
   */
  public void loadFile(String fileName) throws IOException {

    Vector objs;


    FileReader fr = new FileReader(fileName);

    if (fileName.endsWith("jlx")) {

      // JLOOX files
      JLXFileLoader fl = new JLXFileLoader(fr);
      try {
        objs = fl.parseFile();
        fr.close();
      } catch (IOException e) {
        fr.close();
        throw e;
      }

    } else {

      //JDRAW files
      JDFileLoader fl = new JDFileLoader(fr);
      try {
        objs = fl.parseFile();
        fr.close();
      } catch (IOException e) {
        fr.close();
        throw e;
      }

      applyGlobalOption(fl);

    }

    // Load success
    clearObjects();
    objects = objs;
    for(int i=0;i<objects.size();i++)
      ((JDObject)objects.get(i)).setParent(this);
    lastFileName = fileName;

    if(mode!=MODE_PLAY) {
      clearUndo();
      setNeedToSave(false,"Load");
      fireSelectionChange();
    } else {
      initPlayer();
    }

    computePreferredSize();
    repaint();

  }

  /**
   * Show the file selection box and call loadFile if a file is selected.
   * Trigger valueChanged() if a file is selected to be loaded.
   * @param defaultDir default directory
   * @see JDrawEditorListener#valueChanged
   * @see JDrawEditor#loadFile
   */
  public void showOpenDialog(String defaultDir) {

    int ok = JOptionPane.NO_OPTION;
    if (needToSave) ok = JOptionPane.showConfirmDialog(this ,"Your changes will be lost , save before opening a new file ?",
                                        "Open confirmation",JOptionPane.YES_NO_CANCEL_OPTION);

    if(ok == JOptionPane.YES_OPTION)
      instantSave(".");

    if (ok == JOptionPane.YES_OPTION || ok == JOptionPane.NO_OPTION) {

      JFileChooser chooser = new JFileChooser(defaultDir);
      if(lastFileName.length()>0)
        chooser.setSelectedFile(new File(lastFileName));
      JDFileFilter jlxFilter = new JDFileFilter("JLoox vectorial draw",new String[]{"jlx"});
      chooser.addChoosableFileFilter(jlxFilter);
      JDFileFilter jdwFilter = new JDFileFilter("JDraw graphics program",new String[]{"jdw"});
      chooser.addChoosableFileFilter(jdwFilter);

      int returnVal = chooser.showOpenDialog(this);
      File f = chooser.getSelectedFile();

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        try {
          loadFile(f.getAbsolutePath());
        } catch (IOException ex) {
          JOptionPane.showMessageDialog(this, "Error during reading file: " + f.getName() + "\n" + ex.getMessage());
        }
      }

    }

  }

  /** bring selected object to foreground */
  public void frontSelection() {
    if(mode==MODE_PLAY) return;

    Vector nObjects = new Vector();

    // Get ordered list of selected object
    for (int i = 0; i < objects.size(); i++) {
      JDObject p = (JDObject)objects.get(i);
      if (isSelected(p)) nObjects.add(p);
    }

    // Remove them then readd them at the end
    objects.removeAll(nObjects);
    objects.addAll(nObjects);

    setNeedToSave(true,"Bring to front");
    repaint(buildRepaintRect(selObjects));
  }

  /** send selected object to background */
  public void backSelection() {
    if(mode==MODE_PLAY) return;

    Vector nObjects = new Vector();

    // Get ordered list of selected object
    for (int i = 0; i < objects.size(); i++) {
      JDObject p = (JDObject)objects.get(i);
      if (isSelected(p)) nObjects.add(p);
    }

    // Remove them then readd them at the begining
    objects.removeAll(nObjects);
    objects.addAll(0,nObjects);

    setNeedToSave(true,"Send to back");
    repaint(buildRepaintRect(selObjects));
  }

  /** group selected objects */
  public void groupSelection() {
    if(mode==MODE_PLAY) return;

    Vector nObjects = new Vector();

    // Get ordered list of selected object
    for (int i = 0; i < objects.size(); i++) {
      JDObject p = (JDObject) objects.get(i);
      if (isSelected(p)) nObjects.add(p);
    }

    if (nObjects.size() > 0) {
      selObjects.clear();
      // Remove them
      objects.removeAll(nObjects);

      //Create the group
      JDGroup g = new JDGroup("JDGroup", nObjects);
      selObjects.add(g);
      objects.add(g);
      fireSelectionChange();
      setNeedToSave(true,"Group");
      repaint(g.getRepaintRect());
    }

  }

  /** ungroup selected object */
  public void ungroupSelection() {
    if(mode==MODE_PLAY) return;

    if (selObjects.size() == 1) {
      JDObject p = (JDObject) selObjects.get(0);
      if (p instanceof JDGroup) {
        JDGroup g = (JDGroup) p;
        repaint(g.getRepaintRect());
        int id = objects.indexOf(g);
        objects.remove(g);
        selObjects.clear();
        // Readd objects
        selObjects.addAll(g.getChildren());
        objects.addAll(id,g.getChildren());
        fireSelectionChange();        
        setNeedToSave(true,"Ungroup");
      }
    }

  }

  /** Zoom In the graph */
  public void zoomIn() {

    zoomFactor++;
    invalidate();
    fireSizeChanged();
    focusZoomSelection();

  }

  /** Zoom Out the graph */
  public void zoomOut() {

    zoomFactor--;
    invalidate();
    fireSizeChanged();
    focusZoomSelection();

  }

  private void focusZoomSelection() {

    if (selObjects.size() > 0) {
      Object p = getParent();
      if (p instanceof JViewport) {

        JViewport vp = (JViewport) p;
        Rectangle r = buildRepaintRect(selObjects);
        Rectangle zr = new Rectangle(zbconvert(r.x, transx), zbconvert(r.y, transy),
                                     zbconvert(r.width, 0), zbconvert(r.height, 0));
        vp.validate();
        Dimension vr = vp.getSize();
        Rectangle nr = new Rectangle(zr.x - (vr.width-zr.width)/2,zr.y - (vr.height-zr.height)/2,
                                     vr.width,vr.height);

        // This to avoid a JViewport.scrollRectToVisible() bug
        // Unfortunaly this generates jirky sometimes
        vp.setViewPosition(new Point(0,0));

        vp.scrollRectToVisible(nr);

      }
    }

  }

  /** Get the zoom factor in percent */
  public int getZoomFactorPercent() {
    return zbconvert(100,0);
  }

  /**
   * Returns the zoom factor value.
   * @see #getZoomFactorPercent
   */
  public int getZoomFactor() {
    return zoomFactor;
  }

  /** Sets the zoom factor */
  public void setZoomFactor(int z) {
    zoomFactor=z;
    invalidate();
    fireSizeChanged();
  }

  /** Translate selected Object */
  public void translateSelection(int x, int y) {
    if(mode==MODE_PLAY) return;

    if (selObjects.size() > 0 && (x!=0 || y!=0)) {

      Rectangle oldRect = buildRepaintRect(selObjects);
      repaint(oldRect);
      for (int i = 0; i < selObjects.size(); i++)
        ((JDObject)selObjects.get(i)).translate(x, y);
      oldRect.translate(x,y);
      repaint(oldRect);

      if(isDraggingObject)  hasMoved=true;

    }

  }

  /** Get undo state */
  public boolean canUndo() {
    return mode==MODE_EDIT && undoPos>=2;
  }

  /** Get redo state */
  public boolean canRedo() {
    return mode==MODE_EDIT && undoPos<undo.size();
  }

  /** Get name of the last action */
  public String getLastActionName() {
    if( canUndo() ) return ((UndoBuffer)undo.get(undoPos-1)).getName();
    else return "";
  }

  /** Get name of the action that can be redone */
  public String getNextActionName() {
    if( canRedo() ) return ((UndoBuffer)undo.get(undoPos)).getName();
    else return "";
  }

  /** Undo the last action */
  public void undo() {
    if( canUndo() ) {
      undoPos--;
      rebuildBackup(undoPos-1);
    }
  }

  /** Redo last canceled action */
  public void redo() {
    if( canRedo() ) {
      undoPos++;
      rebuildBackup(undoPos-1);
    }
  }

  /** Clear the undo buffer */
  public void clearUndo() {
    if (mode == MODE_EDIT) {
      undo.clear();
      undo.add(new UndoBuffer(objects, "Init"));
      undoPos = 1;
    }
  }

  /** Align selection to top */
  public void aligntopSelection() {
    if(mode==MODE_PLAY) return;

    // repaint old rectangle
    repaint(buildRepaintRect(selObjects));

    Point org = JDUtils.getTopLeftCorner(selObjects);

    for (int i = 0; i < selObjects.size(); i++) {
      JDObject n = (JDObject) selObjects.get(i);
      double y = n.boundRect.y;
      n.translate(0.0,org.y-y);
    }

    setNeedToSave(true,"Align");
    repaint(buildRepaintRect(selObjects));
  }

  /** Align selection to left */
  public void alignleftSelection() {
    if(mode==MODE_PLAY) return;

    // repaint old rectangle
    repaint(buildRepaintRect(selObjects));

    Point org = JDUtils.getTopLeftCorner(selObjects);

    for (int i = 0; i < selObjects.size(); i++) {
      JDObject n = (JDObject) selObjects.get(i);
      double x = n.boundRect.x;
      n.translate(org.x-x,0.0);
    }

    setNeedToSave(true,"Align");
    repaint(buildRepaintRect(selObjects));
  }

  /** Align selection to bottom */
  public void alignbottomSelection() {
    if(mode==MODE_PLAY) return;

    // repaint old rectangle
    repaint(buildRepaintRect(selObjects));

    Point org = JDUtils.getBottomRightCorner(selObjects);

    for (int i = 0; i < selObjects.size(); i++) {
      JDObject n = (JDObject) selObjects.get(i);
      double y = n.boundRect.y+n.boundRect.height;
      n.translate(0.0,org.y-y);
    }

    setNeedToSave(true,"Align");
    repaint(buildRepaintRect(selObjects));
  }

  /** Align selection to right */
  public void alignrightSelection() {
    if(mode==MODE_PLAY) return;

    // repaint old rectangle
    repaint(buildRepaintRect(selObjects));

    Point org = JDUtils.getBottomRightCorner(selObjects);

    for (int i = 0; i < selObjects.size(); i++) {
      JDObject n = (JDObject) selObjects.get(i);
      double x = n.boundRect.x+n.boundRect.width;
      n.translate(org.x-x,0.0);
    }

    setNeedToSave(true,"Align");
    repaint(buildRepaintRect(selObjects));
  }

  /**
   * Add an JDrawEditor listener.
   * @param l Editor listener.
   * @see JDrawEditorListener
   */
  public void addEditorListener(JDrawEditorListener l) {
    listeners.add(l);
  }

  /**
   * Remove an JDrawEditor listener.
   * @param l Editor listener.
   * @see JDrawEditorListener
   */
  public void removeEditorListener(JDrawEditorListener l) {
    listeners.remove(l);
  }

  /**
   * Clears the JDrawEditor listener list.
   * @see JDrawEditorListener
   */
  public void clearEditorListener() {
    listeners.clear();
  }

  /** Returns true if the drawing has been modofied and need to be saved */
  public boolean getNeedToSaveState() {
    return needToSave;
  }

  /** Gets the name of the last loaded file */
  public String getFileName() {
    return lastFileName;
  }

  /** Add an object to the drawing. If you want to add dynamcaly object to this
   * editor (in PLAY_MODE) , You should call initPlayer() after all objects
   * are inserted.
   * @see #initPlayer
   */
  public void addObject(JDObject o) {
    objects.add(o);
    o.setParent(this);
  }

  /** Clear all object */
  public void clearObjects() {
    objects.clear();
    if(mode==MODE_PLAY) return;
    selObjects.clear();
  }

  /** Set a global translation for the drawing area */
  public void setTranslation(int x,int y) {
    transx = x;
    transy = y;
  }

  /** Compute the optimal size of the components and trigger sizeChanged() */
  public void computePreferredSize() {

    Dimension d = new Dimension();
    Rectangle old=null;
    for(int i=0;i<objects.size();i++) {
      if(old==null)
        old = getObjectAt(i).getBoundRect();
      else
        old = old.union(getObjectAt(i).getBoundRect());
    }

    if( old==null ) {
      d.width  = 320;
      d.height = 200;
    } else {
      d.width   = old.x + old.width + 5;
      d.height  = old.y + old.height + 5;
    }

    setPreferredSize(d);
    fireSizeChanged();

  }

  public void setPreferredSize(Dimension d) {
    sizeX = d.width;
    sizeY = d.height;
  }

  public Dimension getPreferredSize() {
    return new Dimension(zbconvert(sizeX,0), zbconvert(sizeY,0));
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  /** Inits the player, This function should be called only if you want
   * to build dynamicaly a graph with addObject(). Call it after all
   * objects are inserted in the Editor.
   * @see #addObject
   */
  public void initPlayer() {

    for(int i=0;i<objects.size();i++) {
      getObjectAt(i).saveTransform();
      getObjectAt(i).initValue();
    }

  }

  /** Convert the selected objects to JDPolyline. */
  public void convertToPolyline() {

    for(int i=0;i<selObjects.size();i++) {
      JDObject o = (JDObject)selObjects.get(i);
      if( o instanceof JDPolyConvert ) {
        JDObject n = ((JDPolyConvert)o).convertToPolyline();
        int pos = objects.indexOf(o);
        objects.remove(pos);
        selObjects.clear();
        objects.add(pos,n);
        selObjects.add(n);
        repaint(o.getRepaintRect());
        repaint(n.getRepaintRect());
      }
      setNeedToSave(true,"Convert to polyline");
    }

  }

  boolean canConvertToPolyline() {
    boolean ret = selObjects.size()>0;
    for(int i=0;i<selObjects.size();i++)
      ret = ret && (selObjects.get(i) instanceof JDPolyConvert);
    return ret;
  }

  boolean canEditGroup() {
    return (selObjects.size()==1) && (selObjects.get(0) instanceof JDGroup);
  }

  /** Raise selected object. */
  public void raiseObject() {

    if (selObjects.size() == 1) {
      JDObject n = (JDObject) selObjects.get(0);
      int pos = objects.indexOf(n);
      if (pos < objects.size() - 1) {
        objects.remove(pos);
        objects.add(pos + 1, n);
        setNeedToSave(true, "Raise");
        repaint(n.getRepaintRect());
      }
    }

  }

  /** Move down selected object. */
  public void lowerObject() {

    if (selObjects.size() == 1) {
      JDObject n = (JDObject) selObjects.get(0);
      int pos = objects.indexOf(n);
      if (pos > 0) {
        objects.remove(pos);
        objects.add(pos - 1, n);
        setNeedToSave(true, "Lower");
        repaint(n.getRepaintRect());
      }
    }

  }

  /** Return all object that have the "User interaction" flag enabled. */
  public Vector getInteractiveObjects() {
    Vector ret = new Vector();
    for(int i=0;i<objects.size();i++)
      ((JDObject)objects.get(i)).getUserValueList(ret);
    return ret;
  }

// -----------------------------------------------------
// Key listener
// -----------------------------------------------------
  public void keyPressed(KeyEvent e) {
    if(mode==MODE_PLAY) return;
    int t = (alignToGrid)?GRID_SIZE:1;

    switch (e.getKeyCode()) {
      case KeyEvent.VK_UP:
        translateSelection(0, -t);
        setNeedToSave(true,"Translate");
         // consume event (does not allow the parent ScrollPane to get the keyEvent)
         e.consume();
        break;
      case KeyEvent.VK_DOWN:
        translateSelection(0, t);
        setNeedToSave(true,"Translate");
        e.consume();
        break;
      case KeyEvent.VK_LEFT:
        translateSelection(-t, 0);
        setNeedToSave(true,"Translate");
        e.consume();
        break;
      case KeyEvent.VK_RIGHT:
        translateSelection(t, 0);
        setNeedToSave(true,"Translate");
        e.consume();
        break;
    }

  }

  public void keyReleased(KeyEvent e) {

  }

  public void keyTyped(KeyEvent e) {

  }

// -----------------------------------------------------
// Mouse listener
// -----------------------------------------------------
  public void mouseDragged(MouseEvent e) {
    if(mode==MODE_PLAY) {
      relayPlayerMouseMoveEvent(e);
      mouseDraggedPlayer(e);
    } else {
      mouseDraggedEditor(e);
    }
  }

  private void mouseDraggedPlayer(MouseEvent e) {

    int ex = zconvert(e.getX(), transx);
    int ey = zconvert(e.getY(), transy);

    if (selObjects.size() > 0) {
      // repaint old rect
      repaint(buildRepaintRect(selObjects));

      // Forward the event to dragged objects
      for (int i = 0; i < selObjects.size(); i++)
        ((JDObject) selObjects.get(i)).processValue(JDObject.MDRAGGED,ex,ey);

      repaint(buildRepaintRect(selObjects));
    }

  }

  private void mouseDraggedEditor(MouseEvent e) {

    int ex = zconvert(e.getX(),transx);
    int ey = zconvert(e.getY(),transy);

    //---------------------------------------------------
    if (isDraggingSelection) {

      Rectangle old = buildSelectionRect();
      selX2 = ex;
      selY2 = ey;
      repaint(old.union(buildSelectionRect()));
      return;

    }

    //---------------------------------------------------
    if (isDraggingSummit) {

      if( alignToGrid ) {
        ex = ((ex + GRID_SIZE / 2) / GRID_SIZE) * GRID_SIZE;
        ey = ((ey + GRID_SIZE / 2) / GRID_SIZE) * GRID_SIZE;
      }

      JDObject p = (JDObject) selObjects.get(curObject);
      Rectangle old = p.getRepaintRect();

      switch (p.getSummitMotion(selSummit)) {
        case JDObject.BOTH:
          p.moveSummit(selSummit, ex, ey);
          break;
        case JDObject.HORIZONTAL:
          p.moveSummit(selSummit, ex, p.getSummit(selSummit).y);
          break;
        case JDObject.VERTICAL:
          p.moveSummit(selSummit, p.getSummit(selSummit).x, ey);
          break;
      }
      hasMoved=true;
      repaint(old.union(p.getRepaintRect()));

    }

    //---------------------------------------------------
    if (isDraggingObject) {

      if( alignToGrid ) {
        ex = ((ex + GRID_SIZE / 2) / GRID_SIZE) * GRID_SIZE;
        ey = ((ey + GRID_SIZE / 2) / GRID_SIZE) * GRID_SIZE;
      }

      translateSelection(ex - lastX, ey - lastY);
      lastX = ex;
      lastY = ey;

    }


  }

  public void mouseMoved(MouseEvent e) {
    if(mode==MODE_PLAY) {
      relayPlayerMouseMoveEvent(e);
      return;
    }

    int ex = zconvert(e.getX(),transx);
    int ey = zconvert(e.getY(),transy);

    if ((creationMode == POLYLINE || creationMode == SPLINE) && tmpPoints.size() > 0) {
      if( alignToGrid ) {
        ex = ((ex + GRID_SIZE / 2) / GRID_SIZE) * GRID_SIZE;
        ey = ((ey + GRID_SIZE / 2) / GRID_SIZE) * GRID_SIZE;
      }
      int s = tmpPoints.size();
      Rectangle old = buildRectFromLine((Point) tmpPoints.get(s - 2), (Point) tmpPoints.get(s - 1));
      ((Point) tmpPoints.get(s - 1)).x = ex;
      ((Point) tmpPoints.get(s - 1)).y = ey;
      repaint(old.union(buildRectFromLine((Point) tmpPoints.get(s - 2), (Point) tmpPoints.get(s - 1))));
      return;
    }

    if (!isDraggingSummit && !isDraggingObject && !isDraggingSelection)
      findSummit(ex, ey);
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
    if(mode==MODE_PLAY) {
      if(motionObject!=null) {
        motionObject.fireMouseEvent(MouseEvent.MOUSE_EXITED,e);
        motionObject=null;
      }
    }
  }

  public void mouseClicked(MouseEvent e) {
    if(mode==MODE_PLAY) return;

    int ex = zconvert(e.getX(),transx);
    int ey = zconvert(e.getY(),transy);

    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {

      boolean found = false;
      int i = selObjects.size() - 1;
      while (!found && i >= 0) {
        found = ((JDObject) selObjects.get(i)).isInsideObject(ex, ey);
        if (!found) i--;
      }

      if (found) {
        boolean m = JDUtils.showPropertyDialog(this, selObjects,0);
        if (m) setNeedToSave(true,"Property change");
      }

    }

  }

  public void mouseReleased(MouseEvent e) {
    if(mode==MODE_PLAY) {
      relayPlayerMouseReleasedEvent(e);
      mouseReleasedPlayer(e);
    } else {
      mouseReleasedEditor(e);
    }

  }

  private void mouseReleasedPlayer(MouseEvent e) {

    int ex = zconvert(e.getX(), transx);
    int ey = zconvert(e.getY(), transy);

    if (selObjects.size() > 0) {
      // repaint old rect
      repaint(buildRepaintRect(selObjects));

      // Forward the event to clicked objects
      for (int i = 0; i < selObjects.size(); i++)
        ((JDObject) selObjects.get(i)).processValue(JDObject.MRELEASED,ex,ey);

      repaint(buildRepaintRect(selObjects));
      selObjects.clear();
    }

  }

  private void mouseReleasedEditor(MouseEvent e) {

    if((isDraggingSummit || isDraggingObject) && hasMoved) {
      if(isDraggingSummit) {
        if( lastCreatedObject==null ) {
          setNeedToSave(true,"Shape edit");
        } else {
          // Creation done
          setNeedToSave(true,"Object creation");
          fireCreationDone();
          lastCreatedObject.centerOrigin();
          lastCreatedObject=null;
        }
      }
      if(isDraggingObject) {
        setNeedToSave(true,"Translate");
        repaint(buildRepaintRect(selObjects));
      }
    }

    isDraggingSummit = false;
    isDraggingObject = false;
    hasMoved=false;

    if (isDraggingSelection) {
      isDraggingSelection = false;
      Rectangle sr = buildSelectionRect();
      Rectangle rep = null;
      selX1 = selX2 = selY1 = selY2 = 0;
      // Look for all object int the selection rectangle
      for (int i = 0; i < objects.size(); i++) {
        JDObject p = (JDObject) objects.get(i);
        if (p.isVisible() && sr.contains(p.getBoundRect())) {
          if( !isSelected(p) ) {
            selObjects.add(p);
          } else {
            if( e.isControlDown() ) {
              // Invert selection
              selObjects.remove(p);
            }
          }

          if( rep==null ) rep = p.getRepaintRect();
          else rep = rep.union(p.getRepaintRect());
        }
      }
      fireSelectionChange();

      if( rep!=null ) repaint(sr.union(rep));
      else            repaint(sr);
    }

    setCursor(dCursor);
  }

  public void mousePressedEditorB1(MouseEvent e) {

    int ex = zconvert(e.getX(),transx);
    int ey = zconvert(e.getY(),transy);

    // -----------------------------------------------------------------------
    if( createObject(ex,ey) )
      return;

    // -----------------------------------------------------------------------
    // Starts by looking if a summit has been hit
    if (findSummit(ex, ey)) {
      isDraggingSummit = true;
      return;
    }

    JDObject p = findObject(ex,ey);

    if (p!=null) {
      //Select it if not
      if (!isSelected(p)) {
        if (!e.isControlDown()) unselectAll();
        selObjects.add(p);
        repaint(p.getRepaintRect());
        fireSelectionChange();
      } else {
        if( e.isControlDown() ) {
          // Unselect it
          selObjects.remove(p);
          repaint(p.getRepaintRect());
          fireSelectionChange();
          return;
        }
      }
      curObject = selObjects.indexOf(p);
      isDraggingObject = true;
      if( alignToGrid ) {
        ex = ((ex + GRID_SIZE / 2) / GRID_SIZE) * GRID_SIZE;
        ey = ((ey + GRID_SIZE / 2) / GRID_SIZE) * GRID_SIZE;
      }
      lastX = ex;
      lastY = ey;
      setCursor(bCursor);
    } else {
      // Starting rectangle selection
      if (!e.isControlDown()) {
        unselectAll();
        fireSelectionChange();
      }
      selX1 = ex;
      selY1 = ey;
      selX2 = ex;
      selY2 = ey;
      isDraggingSelection = true;
    }

  }

  public void mousePressedEditorB3(MouseEvent e) {
    int i;

    // --------------------------------------- Creation mode
    if (creationMode == POLYLINE) {

      if (tmpPoints.size() != 0) {
        // Create the Polyline
        int s = tmpPoints.size();
        Point[] pts = new Point[s];
        for (i = 0; i < s; i++) pts[i] = (Point) tmpPoints.get(i);
        JDObject p = new JDPolyline("Polyline", pts);
        selObjects.add(p);
        objects.add(p);
        repaint(p.getRepaintRect());
        fireCreationDone();
        fireSelectionChange();
        setNeedToSave(true,"Object creation");
      } else {
        // Canceling
        fireCreationDone();
      }
      tmpPoints.clear();
      creationMode = 0;
      return;

    } else if (creationMode == SPLINE) {

      // Create the Spline
      int s = tmpPoints.size();
      s = ((s - 1) / 3) * 3 + 1;
      if (s >= 4) {
        Point[] pts = new Point[s];
        for (i = 0; i < s; i++) pts[i] = (Point) tmpPoints.get(i);
        JDObject p = new JDSpline("Spline", pts);
        selObjects.add(p);
        objects.add(p);
        repaint(p.getRepaintRect());
        fireCreationDone();
        fireSelectionChange();
        setNeedToSave(true,"Object creation");
      } else {
        // Not enough point canceling
        fireCreationDone();
        repaint();
      }

      tmpPoints.clear();
      creationMode = 0;
      return;

    } else if (creationMode != 0) {

      // Canceling
      tmpPoints.clear();
      creationMode = 0;
      fireCreationDone();
      repaint();
      return;

    }

    showMenu(e);
  }

  public void mousePressedPlayerB1(MouseEvent e) {

    int ex = zconvert(e.getX(), transx);
    int ey = zconvert(e.getY(), transy);
    int i;

    // Build list of object to be modified
    selObjects.clear();
    selectObjects(ex, ey, selObjects);
    if (selObjects.size() > 0) {

      // repaint old rect
      repaint(buildRepaintRect(selObjects));

      // Execute value program
      for (i = 0; i < selObjects.size(); i++)
        ((JDObject) selObjects.get(i)).processValue(JDObject.MPRESSED,ex,ey);

      repaint(buildRepaintRect(selObjects));

    }

  }

  public void mousePressedPlayerB3(MouseEvent e) {}

  public void mousePressed(MouseEvent e) {

    grabFocus();
    if( mode==MODE_PLAY ) {
      relayPlayerMousePressedEvent(e);
      if (e.getButton() == MouseEvent.BUTTON1) mousePressedPlayerB1(e);
      if (e.getButton() == MouseEvent.BUTTON3) mousePressedPlayerB3(e);
    } else {
      if (e.getButton() == MouseEvent.BUTTON1) mousePressedEditorB1(e);
      if (e.getButton() == MouseEvent.BUTTON3) mousePressedEditorB3(e);
    }

  }

  private void relayPlayerMouseReleasedEvent(MouseEvent e) {

    int ex = zconvert(e.getX(),transx);
    int ey = zconvert(e.getY(),transy);

    if(pressedObject!=null) {
      if(pressedObject.isInsideObject(ex,ey)) {
        pressedObject.fireMouseEvent(MouseEvent.MOUSE_RELEASED,e);
        pressedObject.fireMouseEvent(MouseEvent.MOUSE_CLICKED,e);
      }
      pressedObject=null;
    }

  }

  private void relayPlayerMousePressedEvent(MouseEvent e) {

    int ex = zconvert(e.getX(),transx);
    int ey = zconvert(e.getY(),transy);
    pressedObject = findMouseListenerObject(ex,ey);
    if(pressedObject!=null)
      pressedObject.fireMouseEvent(MouseEvent.MOUSE_PRESSED,e);

  }

  private void relayPlayerMouseMoveEvent(MouseEvent e) {

    int ex = zconvert(e.getX(),transx);
    int ey = zconvert(e.getY(),transy);
    JDObject p = findMouseListenerObject(ex,ey);
    if(motionObject==null) {
      if( p==null ) {
        // Nothing
      } else {
        // Enter object
        motionObject = p;
        motionObject.fireMouseEvent(MouseEvent.MOUSE_ENTERED,e);
      }
    } else {
      if( p==null ) {
        // Leave object
        motionObject.fireMouseEvent(MouseEvent.MOUSE_EXITED,e);
        motionObject=null;
      } else {
        if(p==motionObject) {
          // Still the same object
        } else {
          // Move from motionObject to P
          motionObject.fireMouseEvent(MouseEvent.MOUSE_EXITED,e);
          motionObject = p;
          motionObject.fireMouseEvent(MouseEvent.MOUSE_ENTERED,e);
        }
      }
    }

  }

// -----------------------------------------------------
// Action listener
// -----------------------------------------------------
  public void actionPerformed(ActionEvent e) {
    if(mode==MODE_PLAY) return;

    Object src = e.getSource();
    Rectangle rep = null;

    if (src == deleteSummitMenuItem) {
      rep = editedPolyline.getRepaintRect();
      editedPolyline.deleteSummit();
      setNeedToSave(true,"Shape edit");
      repaint(rep.union(editedPolyline.getRepaintRect()));
    } else if (src == breakShapeMenuItem) {
      rep = editedPolyline.getRepaintRect();
      setNeedToSave(true,"Shape edit");
      editedPolyline.breakShape();
      repaint(rep.union(editedPolyline.getRepaintRect()));
    } else if (src == copyMenuItem) {
      copySelection();
    } else if (src == pasteMenuItem) {
      pasteClipboard(lastX, lastY);
    } else if (src == cutMenuItem) {
      cutSelection();
    } else if (src == deleteMenuItem) {
      deleteSelection();
    } else if (src == raiseMenuItem) {
      raiseObject();
    } else if (src == lowerMenuItem) {
      lowerObject();
    } else if (src == frontMenuItem) {
      frontSelection();
    } else if (src == backMenuItem) {
      backSelection();
    } else if (src == groupMenuItem) {
      groupSelection();
    } else if (src == ungroupMenuItem) {
      ungroupSelection();
    } else if (src == zoomInMenuItem) {
      zoomIn();
    } else if (src == zoomOutMenuItem) {
      zoomOut();
    }

  }

  public void paintObjects(Graphics g) {
    for (int i = 0; i < objects.size(); i++) {
      ((JDObject) objects.get(i)).paint(g);
      //((JDObject) objects.get(i)).paintOrigin(g);
    }
  }

  public void paintSelection(Graphics g) {

    if (mode != MODE_PLAY) {

      int i;

      // Paint selection summit
      if( !isDraggingObject || !hasMoved )
        for (i = 0; i < selObjects.size(); i++)
          ((JDObject) selObjects.get(i)).paintSummit(g, zdconvert(6.0, 0.0));

      // Paint selection rectangle
      if (selX1 != selX2 && selY1 != selY2) {
        g.setColor(Color.darkGray);
        g.drawLine(selX1, selY1, selX2, selY1);
        g.drawLine(selX2, selY1, selX2, selY2);
        g.drawLine(selX2, selY2, selX1, selY2);
        g.drawLine(selX1, selY2, selX1, selY1);
      }

      // Paint creation polyline
      for (i = 1; i < tmpPoints.size(); i++) {
        g.setColor(Color.darkGray);
        Point p1 = (Point) tmpPoints.get(i - 1);
        Point p2 = (Point) tmpPoints.get(i);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
      }

    }

  }

  private void paintGrid(Graphics gr) {

    if (gridVisible && mode!=MODE_PLAY) {
      Dimension d = getSize();
      double gs = zbdconvert((double)GRID_SIZE,0.0);
      int r,g,b,x,y;

      if(getBackground().getRed()<128)
        r = getBackground().getRed() + 64;
      else
        r = getBackground().getRed() - 64;

      if(getBackground().getGreen()<128)
        g = getBackground().getGreen() + 64;
      else
        g = getBackground().getGreen() - 64;

      if(getBackground().getBlue()<128)
        b = getBackground().getBlue() + 64;
      else
        b = getBackground().getBlue() - 64;

      Color gColor = new Color( r , g , b );
      gr.setColor(gColor);

      for (double i = 0.0; i<=d.width; i += gs)
        for (double j = 0.0; j<=d.height; j += gs) {
          x = (int)(i+0.5);
          y = (int)(j+0.5);
          gr.drawLine(x, y, x, y);
        }

    }

  }

// -----------------------------------------------------
// Painting stuff
// -----------------------------------------------------
  public void paint(Graphics g) {

    Dimension d = getSize();
    Graphics2D g2 = (Graphics2D) g;

    // Paint Background
    if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, d.width, d.height);
    }

    // Paint the grid
    paintGrid(g);

    // build the transformation according to the zoom and translation
    AffineTransform oldT = g2.getTransform();
    AffineTransform nT = new AffineTransform(oldT);
    double r = getZoomScaleRatio();
    nT.scale(r,r);
    nT.translate((double)transx,(double)transy);
    g2.setTransform(nT);

    // Paint
    paintObjects(g);
    paintSelection(g);

    // Rectore transform
    g2.setTransform(oldT);

    // Paint swing stuff
    paintComponents(g);
    paintBorder(g);

  }

  public void repaint(Rectangle r) {
    if (r != null) {
      int m = (int) (zdconvert(3.0, 0.0) + 0.5) + 1; // summitWidth
      Rectangle zr = new Rectangle(zbconvert(r.x, transx) - m, zbconvert(r.y, transy) - m,
              zbconvert(r.width, 0) + 2 * m, zbconvert(r.height, 0) + 2 * m);
      super.repaint(zr);
    }
  }

// -----------------------------------------------------
// Private stuff
// -----------------------------------------------------
  private void setNeedToSave(boolean b,String s) {

    //System.out.println("Need to save:(" + s + ")" + b);

    needToSave = b;

    if( needToSave && mode==MODE_EDIT ) {
      // Clear upper undo buffer
      for(int i=undo.size()-1;i>=undoPos;i--) {
        undo.removeElementAt(i);
        //System.out.println("Clearing backup #" + i);
      }
      undo.add(new UndoBuffer(objects,s));
      if( undo.size()>=undoLength ) {
        undo.removeElementAt(0);
        //System.out.println("Full!! Clearing backup #0");
      }
      // Reset redo
      undoPos=undo.size();
      //System.out.println("UndoLength=" + undo.size());
    }

    fireValueChanged();
  }

  private double getZoomScaleRatio() {

    double r = 1.0;
    if (zoomFactor != 0) {
      if (zoomFactor >= 0)
        r = (double) (zoomFactor + 1);
      else
        r = 1.0 / (double) (-zoomFactor + 1);
    }
    return r;

  }

  private int zconvert(int x,int t) {

    if (zoomFactor == 0) {
      return x-t;
    } else if (zoomFactor > 0) {
      return (int) ((double) (x) / (double) (zoomFactor + 1)) - t;
    } else {
      return (x) * (-zoomFactor + 1) - t;
    }

  }

  private int zbconvert(int x,int t) {

    if (zoomFactor == 0) {
      return x+t;
    } else if (zoomFactor > 0) {
      return (x+t) * (zoomFactor + 1);
    } else {
      return (int) ((double) (x+t) / (double) (-zoomFactor + 1));
    }

  }

  private double zdconvert(double x,double tx) {

    if (zoomFactor == 0) {
      return x-tx;
    } else if (zoomFactor > 0) {
      return (x-tx)/(double)(zoomFactor + 1);
    } else {
      return (x-tx)*(-zoomFactor + 1);
    }

  }

  private double zbdconvert(double x,double tx) {

    if (zoomFactor == 0) {
      return x+tx;
    } else if (zoomFactor > 0) {
      return (x+tx) * (double)(zoomFactor + 1);
    } else {
      return (x+tx)/(double)(-zoomFactor + 1);
    }

  }

  private void showMenu(MouseEvent e) {

    int ex = zconvert(e.getX(),transx);
    int ey = zconvert(e.getY(),transy);

    // -------------- Contextual menu
    JDObject p = findObject(ex,ey);

    if (p!=null) {
      //Select it if not
      if (!isSelected(p)) {
        unselectAll();
        selObjects.add(p);
        repaint(p.getRepaintRect());
        fireSelectionChange();
      }
    }

    breakShapeMenuItem.setVisible(false);
    deleteSummitMenuItem.setVisible(false);
    editedPolyline = null;
    int sz = selObjects.size();
    p = null;

    if (sz > 0) p = (JDObject) selObjects.get(0);

    infoMenuItem.setVisible(sz >= 1);

    if (sz == 1)
      infoMenuItem.setText(p.getName() + " [" + p.toString() + "]");
    else
      infoMenuItem.setText("Multiple selection");

    cutMenuItem.setEnabled((sz>0));
    copyMenuItem.setEnabled((sz>0));
    pasteMenuItem.setEnabled((clipboard.size()>0));
    deleteMenuItem.setEnabled((sz>0));

    groupMenuItem.setVisible((sz > 0));
    if (sz==1) {
      ungroupMenuItem.setVisible(p instanceof JDGroup);
    } else {
      ungroupMenuItem.setVisible(false);
    }

    raiseMenuItem.setVisible((sz == 1));
    lowerMenuItem.setVisible((sz == 1));
    frontMenuItem.setVisible((sz >= 1));
    backMenuItem.setVisible((sz >= 1));

    if (p != null) {
      int pos = objects.indexOf(p);
      raiseMenuItem.setEnabled(pos < objects.size() - 1);
      lowerMenuItem.setEnabled(pos > 0);
    }

    // Special polyline edition
    findSummit(ex, ey);
    if (selSummit != -1) {
      if (selObjects.get(curObject) instanceof JDPolyline) {
        editedPolyline = (JDPolyline) selObjects.get(curObject);
        deleteSummitMenuItem.setVisible(editedPolyline.canDeleteSummit(selSummit));
      }
    } else {
      if (p instanceof JDPolyline) {
        editedPolyline = (JDPolyline) p;
        breakShapeMenuItem.setVisible(editedPolyline.canBreakShape(ex, ey));
      }
    }

    //Separator
    sep1.setVisible(infoMenuItem.isVisible());
    sep2.setVisible(deleteSummitMenuItem.isVisible() || breakShapeMenuItem.isVisible());
    sep3.setVisible(groupMenuItem.isVisible() || ungroupMenuItem.isVisible());
    sep5.setVisible(raiseMenuItem.isVisible() || frontMenuItem.isVisible());

    // Popup the menu
    lastX = ex;
    lastY = ey;
    objMenu.show(this, e.getX(), e.getY());

  }

  private Cursor getCursorForMotion(JDObject p,int summit) {

    switch (p.getSummitMotion(summit)) {
      case JDObject.BOTH:
        if (p instanceof JDRectangular) {
          double x = p.getSummit(summit).getX();
          double y = p.getSummit(summit).getY();
          double xc = p.getBoundRect().x + (double) p.getBoundRect().width / 2.0;
          double yc = p.getBoundRect().y + (double) p.getBoundRect().height / 2.0;
          if (x < xc) {
            if (y < yc)
              return nwCursor;
            else
              return swCursor;
          } else {
            if (y < yc)
              return neCursor;
            else
              return seCursor;
          }
        } else {
          return bCursor;
        }
      case JDObject.HORIZONTAL:
        return hCursor;
      case JDObject.VERTICAL:
        return vCursor;
    }

    return dCursor;
  }

  private Rectangle buildSelectionRect() {
    Point p1 = new Point(selX1, selY1);
    Point p2 = new Point(selX2, selY2);
    return buildRectFromLine(p1, p2);
  }

  private Rectangle buildRectFromLine(Point p1, Point p2) {

    Rectangle r = new Rectangle();

    int m = zconvert(1,0);
    if( m<1 ) m = 1;

    if (p1.x < p2.x) {
      if (p1.y < p2.y) {
        r.setRect(p1.x - m, p1.y - m, p2.x - p1.x + 2*m, p2.y - p1.y + 2*m);
      } else {
        r.setRect(p1.x - m, p2.y - m, p2.x - p1.x + 2*m, p1.y - p2.y + 2*m);
      }
    } else {
      if (p1.y < p2.y) {
        r.setRect(p2.x - m, p1.y - m, p1.x - p2.x + 2*m, p2.y - p1.y + 2*m);
      } else {
        r.setRect(p2.x - m, p2.y - m, p1.x - p2.x + 2*m, p1.y - p2.y + 2*m);
      }
    }

    return r;
  }

  private boolean findSummit(int x, int y) {

    boolean found = false;
    int i;
    curObject = -1;
    selSummit = -1;

    if (selObjects.size() != 0) {

      i = 0;
      found = false;
      JDObject p = null;
      while (i < selObjects.size() && !found) {
        p = (JDObject) selObjects.get(i);
        selSummit = p.getSummit(x, y,zdconvert(6.0,0.0));
        found = (selSummit != -1);
        if (!found) i++;
      }

      if (found) {
        curObject = i;
        setCursor(getCursorForMotion(p,selSummit));
      } else {
        setCursor(dCursor);
      }

    }

    return found;

  }

  private JDObject findObject(int x, int y) {

    boolean found = false;
    int i;
    // start looking at front
    i = objects.size() - 1;
    while (!found && i >= 0) {
      found = ((JDObject) objects.get(i)).isInsideObject(x, y);
      if (!found) i--;
    }

    if (found) {
      return (JDObject) objects.get(i);
    } else {
      return null;
    }

  }

  private JDObject findMouseListenerObject(int x, int y) {

    boolean found = false;
    int i;
    JDObject o;
    // start looking at front
    i = objects.size() - 1;
    while (!found && i >= 0) {
      o = ((JDObject) objects.get(i));
      if(o.hasMouseListener())
        found = ((JDObject) objects.get(i)).isInsideObject(x, y);
      if (!found) i--;
    }

    if (found) {
      return (JDObject) objects.get(i);
    } else {
      return null;
    }

  }

  private void selectObjects(int x, int y,Vector found) {
    // Find objects under x,y coordinates
    for(int i=0;i<objects.size();i++)
      getObjectAt(i).findObjectsAt(x,y,found);
  }

  private void createObject(JDObject p, int summit) {
    unselectAll();
    lastCreatedObject=p;
    objects.add(p);
    creationMode = 0;
    selObjects.add(p);
    isDraggingSummit = (summit >= 0);
    curObject = 0;
    selSummit = summit;
    repaint(p.getRepaintRect());
    fireSelectionChange();

    if (!isDraggingSummit) {
      // Creation done
      setNeedToSave(true, "Object creation");
      fireCreationDone();
      lastCreatedObject = null;
    }

  }

  private void rebuildBackup(int pos) {

    // Free All
    clearObjects();

    //Rebuild form backup
    objects = ((UndoBuffer)undo.get(pos)).rebuild();
    //System.out.println("Rebuild backup #" + pos);

    //Repaint
    repaint();
    fireValueChanged();
    fireSelectionChange();
  }

  private void fireSelectionChange() {
    for(int i=0;i<listeners.size();i++)
      ((JDrawEditorListener)listeners.get(i)).selectionChanged();
  }

  private void fireCreationDone() {
    for(int i=0;i<listeners.size();i++)
      ((JDrawEditorListener)listeners.get(i)).creationDone();
  }

  private void fireClipboardChange() {
    for(int i=0;i<listeners.size();i++)
      ((JDrawEditorListener)listeners.get(i)).clipboardChanged();
  }

  void fireValueChanged() {
    for(int i=0;i<listeners.size();i++)
      ((JDrawEditorListener)listeners.get(i)).valueChanged();
  }

  private void fireSizeChanged() {
    for(int i=0;i<listeners.size();i++)
      ((JDrawEditorListener)listeners.get(i)).sizeChanged();
  }

  private void applyGlobalOption(JDFileLoader f) {
    setBackground(f.globalBackground);
  }

  private void createContextualMenu() {
    objMenu = new JPopupMenu();

    infoMenuItem = new JMenuItem();
    infoMenuItem.setEnabled(false);

    cutMenuItem = new JMenuItem("Cut");
    cutMenuItem.addActionListener(this);

    copyMenuItem = new JMenuItem("Copy");
    copyMenuItem.addActionListener(this);

    pasteMenuItem = new JMenuItem("Paste");
    pasteMenuItem.addActionListener(this);

    deleteMenuItem = new JMenuItem("Delete");
    deleteMenuItem.addActionListener(this);

    deleteSummitMenuItem = new JMenuItem("Delete control point");
    deleteSummitMenuItem.addActionListener(this);

    breakShapeMenuItem = new JMenuItem("Add a control point here");
    breakShapeMenuItem.addActionListener(this);

    raiseMenuItem = new JMenuItem("Raise");
    raiseMenuItem.addActionListener(this);

    lowerMenuItem = new JMenuItem("Lower");
    lowerMenuItem.addActionListener(this);

    frontMenuItem = new JMenuItem("Bring to front");
    frontMenuItem.addActionListener(this);

    backMenuItem = new JMenuItem("Send to back");
    backMenuItem.addActionListener(this);

    groupMenuItem = new JMenuItem("Group");
    groupMenuItem.addActionListener(this);

    ungroupMenuItem = new JMenuItem("Ungroup");
    ungroupMenuItem.addActionListener(this);


    zoomInMenuItem = new JMenuItem("Zoom in");
    zoomInMenuItem.addActionListener(this);
    zoomOutMenuItem = new JMenuItem("Zoom out");
    zoomOutMenuItem.addActionListener(this);

    objMenu.add(infoMenuItem);
    sep1 = new JSeparator();
    objMenu.add(sep1);
    objMenu.add(cutMenuItem);
    objMenu.add(copyMenuItem);
    objMenu.add(pasteMenuItem);
    objMenu.add(deleteMenuItem);
    sep4 = new JSeparator();
    objMenu.add(sep4);
    objMenu.add(groupMenuItem);
    objMenu.add(ungroupMenuItem);
    sep3 = new JSeparator();
    objMenu.add(sep3);
    objMenu.add(deleteSummitMenuItem);
    objMenu.add(breakShapeMenuItem);
    sep2 = new JSeparator();
    objMenu.add(sep2);
    objMenu.add(raiseMenuItem);
    objMenu.add(lowerMenuItem);
    objMenu.add(frontMenuItem);
    objMenu.add(backMenuItem);
    sep5 = new JSeparator();
    objMenu.add(sep5);
    objMenu.add(zoomInMenuItem);
    objMenu.add(zoomOutMenuItem);

  }

  private Rectangle buildRepaintRect(Vector obs) {

    Rectangle oldRect = null;

    // Build the rectangle to repaint
    for (int i = 0; i < obs.size(); i++) {
      JDObject p = (JDObject) obs.get(i);
      if (oldRect == null)
        oldRect = p.getRepaintRect();
      else
        oldRect = oldRect.union(p.getRepaintRect());
    }

    return oldRect;

  }

  private boolean createObject(int ex,int ey) {

    if( alignToGrid ) {
      ex = ((ex + GRID_SIZE / 2) / GRID_SIZE) * GRID_SIZE;
      ey = ((ey + GRID_SIZE / 2) / GRID_SIZE) * GRID_SIZE;
    }

    switch (creationMode) {
      case RECTANGLE:
        createObject(new JDRectangle("Rectangle", ex, ey, 4, 4), 4);
        return true;
      case RRECTANGLE:
        createObject(new JDRoundRectangle("RoundRectangle", ex, ey, 4, 4), 4);
        return true;
      case LINE:
        createObject(new JDLine("Line", ex, ey, ex, ey), 1);
        return true;
      case ELLIPSE:
        createObject(new JDEllipse("Ellipse", ex, ey, 4, 4), 4);
        return true;
      case POLYLINE:
      case SPLINE:
        int s = tmpPoints.size();
        if (s == 0) {
          unselectAll();
          tmpPoints.clear();
          tmpPoints.add(new Point(ex, ey));
          tmpPoints.add(new Point(ex + 4, ey + 4));
        } else {
          Rectangle old = buildRectFromLine((Point) tmpPoints.get(s - 2), (Point) tmpPoints.get(s - 1));
          tmpPoints.add(new Point(ex, ey));
          repaint(old.union(buildRectFromLine((Point) tmpPoints.get(s - 1), (Point) tmpPoints.get(s))));
        }
        fireSelectionChange();
        return true;
      case LABEL:
        String str = JOptionPane.showInputDialog(this, "Enter a text", "Create label", JOptionPane.INFORMATION_MESSAGE);
        if (str != null)
          createObject(new JDLabel("Label", str, ex, ey), -1);
        else {
          //Canceling
          creationMode = 0;
          fireCreationDone();
        }
        return true;
      case IMAGE:
        JFileChooser chooser = new JFileChooser(".");
        String[] exts={"gif","png","jpg"};
        chooser.addChoosableFileFilter(new JDFileFilter("Image file",exts));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
          createObject(new JDImage("Image", chooser.getSelectedFile().getAbsolutePath(),ex, ey), -1);
          return true;
        }
        break;
      case CLIPBOARD:
        creationMode = 0;
        this.pasteClipboard(ex, ey);
        fireCreationDone();
        return true;
    }

    return false;
  }

}
