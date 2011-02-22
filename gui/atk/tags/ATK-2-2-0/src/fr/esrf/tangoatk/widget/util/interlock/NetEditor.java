/**
 * A set of class to handle a network editor and its viewer.
 *
 * Author: Jean Luc PONS
 * Date: Jul 1, 2004
 * (c) ESRF 2004
 */
package fr.esrf.tangoatk.widget.util.interlock;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.*;
import java.util.Vector;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
/**
 * Network Editor component class. This class can be subclassed to create specific editor
 * or viewer (not editable) component.
 */
public class NetEditor extends JComponent implements MouseListener, MouseMotionListener {

  // Dragging mode
  final static int DRAG_NONE = 0;
  final static int DRAG_OBJECT = 1;
  final static int DRAG_SELECTION = 2;
  final static int DRAG_LABEL = 3;
  final static int DRAG_LINK = 4;
  final static int DRAG_LINKMOVE = 5;

  /** Selection mode (no creation)  */
  public static final int CREATE_NONE   = 0;
  /** Editor is in bubble creation mode  */
  public static final int CREATE_BUBBLE = 1;
  /** Editor is in text creation mode (Free label) */
  public static final int CREATE_TEXT   = 3;
  /** Editor is in link creation mode  */
  public static final int CREATE_LINK   = 4;

  // ------------------------------------------------
  // Global param
  // ------------------------------------------------

  Font    smallFont        = defaultSmallFont;
  Font    labelFont        = defaultLabelFont;
  boolean useAAFont        = false;
  boolean showArrow        = true;
  boolean isEditable       = true;
  String  defaultExtension = "net";
  int     XGRID_SIZE       = 20; // Horizontal grid size
  int     YGRID_SIZE       = 16; // Vertical grid size

  // ------------------------------------------------

  private Vector objects;     // All objects in the graph
  private Vector clipboard;   // Clipboard
  private Vector undo;        // Undo vector
  private JFrame pFrame;      // parent frame
  private boolean needToSave; // need to save flag
  private boolean moveBubble; // Moveable bubble when not editable

  private int dragMode;          // Dragging mode
  private int createMode;        // Creation mode
  private Point dragStart;       // Drag origin
  private Rectangle selDrag;     // selection area
  private boolean onlyTextSelected;
  private NetObject selObject; // selection object (for sub-selection)
  private NetObject lnkObject; // link object
  private NetObjectDlg dlgProp;
  private NetEditorDlg dlgOpt;
  private final static int undoLength=15;
  private int undoPos;
  private Vector listeners;
  private String currentFileName;

  // Static constant
  private final static float dashPattern[] = {2.0f};
  private final static BasicStroke dashStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f);
  static  final Font defaultSmallFont     = new Font("Dialog", Font.PLAIN, 9);
  static  final Font defaultLabelFont     = new Font("Dialog", Font.PLAIN, 11);

  // -----------------------------------------------------------------
  // Construction
  // -----------------------------------------------------------------

  /**
   * Contruct a Network Editor. It is preferable to use the constructor that gets the parent Frame
   * else all dialogs will appear at the center of screen instead of the center of the parent component.
   */
  public NetEditor() {
    pFrame = null;
    initComponents();
  }

  /**
   * Contruct a Network Editor.
   * @param parent Parent frame
   */
  public NetEditor(JFrame parent) {
    pFrame = parent;
    initComponents();
  }

  private void initComponents() {

    setBackground(new Color(220, 220, 220));
    setLayout(null);
    objects = new Vector();
    clipboard = new Vector();
    dragMode = DRAG_NONE;
    dragStart = new Point();
    selDrag = new Rectangle();
    selObject = null;
    createMode = CREATE_NONE;
    dlgProp = new NetObjectDlg(pFrame, this);
    dlgOpt = new NetEditorDlg(pFrame, this);
    useAAFont = false;
    undo = new Vector();
    resetUndo();
    needToSave=false;
    listeners=new Vector();
    addMouseListener(this);
    addMouseMotionListener(this);
    currentFileName = "";
    moveBubble=false;
    setPreferredSize(new Dimension(640, 480));

  }

  // -----------------------------------------------------------------
  // Settings And Editing control stuff
  // -----------------------------------------------------------------

  /**
   * Sets the specified boolean to indicate whether or not this
   * NetEditor should be editable. When set to false, it paints
   * objects according to their color and they cannot be selected
   * ,copied, deleted or moved.
   * @param b the boolean to be set
   * @see NetEditor#isEditable
   */
  public void setEditable(boolean b) {
    unselectAll();
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    dragMode = DRAG_NONE;
    if( createMode!=CREATE_NONE ) {
      createMode = CREATE_NONE;
      fireCancelCreate();
    }
    isEditable = b;
    repaint();
  }

  /**
   * Returns true is this NetEditor is editable.
   * @return Mode value
   * @see NetEditor#setEditable
   */
  public boolean isEditable() {
    return isEditable;
  }

  /**
   * Sets the file extension for this editor.
   * @param ext File extension
   */
  public void setFileExtension(String ext) {
    defaultExtension = ext;
  }

  /** Returns the file extension of this editor */
  public String getFileExtension() {
    return defaultExtension;
  }

  /**
   * Load a Network file (net or xpss format) into the editor.
   * Trigger sizeChanged() and valueChanged() on success.
   * @param fileName filename to load
   * @throws IOException in case of failure (Contains the error message)
   * @see NetEditorListener#sizeChanged
   * @see NetEditorListener#valueChanged
   */
  public void loadFile(String fileName) throws IOException {

    int i;
    char[] header_buff = new char[5];
    String header;

    /* Determine the file format */
    FileReader f = new FileReader(fileName);
    f.read(header_buff,0,5);
    f.close();
    f = new FileReader(fileName);
    header = new String(header_buff);

    /* Load the net file */
    Vector objs = null;

    if(header.equalsIgnoreCase("Begin")) {
      XpssFileLoader fl = new XpssFileLoader(f);
      objs = fl.parseXpssFile(new Dimension(XGRID_SIZE,YGRID_SIZE));
    } else {
      NetFileLoader fl = new NetFileLoader(f,fileName);
      objs = fl.parseNetFile();
      // Get back global param
      smallFont    = fl.getSmallFont();
      labelFont    = fl.getLabelFont();
      useAAFont    = fl.getUseAAFont();
      showArrow    = fl.getDrawArrow();
    }

    f.close();

    // Load success
    clearObjects();
    currentFileName = fileName;
    for(i=0;i<objs.size();i++)
      addObject((NetObject)objs.get(i));

    resetUndo();
    computePreferredSize();
    setNeedToSave(false,"Load");
    repaint();

  }

  /**
   * Show the file selection box and call loadFile if
   * a file is selected.
   * Trigger valueChanged() if a file is selected to be loaded.
   * @param defaultDir default directory
   * @param filter File filter, if null is specified a file filter is created with the file extension.
   * @see NetEditor#loadFile
   * @see NetEditor#setFileExtension
   * @see NetEditorListener#valueChanged
   * @see NetEditorListener#sizeChanged
   * @see NetFileFilter#NetFileFilter
   */
  public void showOpenFileDialog(String defaultDir,NetFileFilter filter) {

    int ok = JOptionPane.YES_OPTION;
    JFileChooser chooser = new JFileChooser(defaultDir);
    if(currentFileName.length()>0)
      chooser.setSelectedFile(new File(currentFileName));
    
    if( filter!=null ) {
      chooser.addChoosableFileFilter(filter);
    } else {
      String[] ext    = {defaultExtension};
      String[] extPss = {"xpss","pss"};
      chooser.addChoosableFileFilter(new NetFileFilter("PSS Network description file",extPss));
      chooser.addChoosableFileFilter(new NetFileFilter("Network description file",ext));
    }

    int returnVal = chooser.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      if (f != null) {
        if (ok == JOptionPane.YES_OPTION) {
          String fileName = f.getAbsolutePath();
          try {
            loadFile(fileName);
          } catch (IOException ex) {
            error("Error during reading file:" + fileName + "\n" + ex.getMessage());
            fireValueChanged();
          }
        }
      }
    }

  }

  /**
   * Save the editor content to a net file.
   * @param fileName filename to save
   * @throws IOException in case of failure
   */
  public void saveNetFile(String fileName) throws IOException {

    int i,sz = objects.size();
    if (sz == 0) return;

    if(!fileName.endsWith(defaultExtension))
      if( JOptionPane.showConfirmDialog(this, "Do you really want to save " + fileName
                                        + "\nwith an extension different from the standard (."
                                        + defaultExtension+") ?", "Confirm",
                                        JOptionPane.YES_NO_OPTION)== JOptionPane.NO_OPTION )
        return;


    FileWriter f = new FileWriter(fileName);

    // Save header
    f.write("NetFile v10 {\n");

    // Save global param section
    f.write("  GlobalParam {\n");
    if(!NetUtils.fontEquals(labelFont,defaultLabelFont))
      f.write("    label_font:\"" + labelFont.getName() + "\"," + labelFont.getStyle() + "," + labelFont.getSize() + "\n");
    if(!NetUtils.fontEquals(smallFont,defaultSmallFont))
      f.write("    small_font:\"" + smallFont.getName() + "\"," + smallFont.getStyle() + "," + smallFont.getSize() + "\n");
    if( useAAFont )
      f.write("    use_aa_font:1\n");
    if( !showArrow )
      f.write("    draw_arrow:0\n");
    f.write("  }\n");

    // Reset index
    for (i = 0; i < sz; i++)
      ((NetObject) objects.get(i)).setIndex(i);

    // Save object
    for (i = 0; i < objects.size(); i++)
      ((NetObject) objects.get(i)).saveObject(f);

    f.write("}\n");

    //close
    f.close();
    currentFileName = fileName;
    setNeedToSave(false, "Save");

  }

  /**
   * Show the file selection box and call saveNetFile if
   * a file is selected.
   * Trigger valueChanged() if a file is selected to be saved.
   * @param defaultDir default directory
   * @param filter File filter, if null is specified a file filter is created with the default file extension.
   * @see NetEditorListener#valueChanged
   * @see NetEditor#saveNetFile
   * @see NetEditor#setFileExtension
   */
  public void showSaveFileDialog(String defaultDir,NetFileFilter filter) {

    int ok = JOptionPane.YES_OPTION;
    JFileChooser chooser = new JFileChooser(defaultDir);
    if(currentFileName.length()>0)
      chooser.setSelectedFile(new File(currentFileName));

    if( filter!=null ) {
      chooser.addChoosableFileFilter(filter);
    } else {
      String[] ext = {defaultExtension};
      chooser.addChoosableFileFilter(new NetFileFilter("Network description file",ext));
    }

    int returnVal = chooser.showSaveDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File f = chooser.getSelectedFile();
      if (f != null) {
        if (f.exists()) ok = JOptionPane.showConfirmDialog(this, "Do you want to overwrite " + f.getName() + " ?", "Confirm overwrite", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
          String fileName = f.getAbsolutePath();
          try {
            saveNetFile(fileName);
          } catch (IOException ex) {
            error("Error during saving file:" + fileName + "\n" + ex.getMessage());
          }
        }
      }
    }

  }

  /**
   * Save the current scheme.
   * @param defaultDir default directory for filebox if no filename has been set.
   * @see NetEditorListener#valueChanged
   * @see NetEditor#saveNetFile
   */
  public void saveCurrent(String defaultDir) {
    if (currentFileName.length() == 0) {
      showSaveFileDialog(defaultDir,null);
    } else {
      try {
        saveNetFile(currentFileName);
      } catch (IOException ex) {
        error("Error during saving file:" + currentFileName + "\n" + ex.getMessage());
      }
    }
  }

  /**
   * Sets anti aliased font usage. Performance can be lower
   * on certain system when using AA fonts.
   * @param b True to use Anti-Aliased font
   */
  public void setAntialiasFont(boolean b) {
    useAAFont = b;
    repaint();
  }

  /**
   * Returns true if Anti-Aliased fonts are used, false otherwise
   * @return Anti-Aliased fonts usage
   */
  public boolean getAntialiasFont() {
    return useAAFont;
  }

  /**
   * Unselect all objects in the editor. Does not repaint.
   */
  public void unselectAll() {
    int sz = objects.size();
    for (int i = 0; i < sz; i++)
      ((NetObject) objects.get(i)).setSelected(false);
    selObject = null;
    onlyTextSelected = false;
  }

  /**
   * Select all objects in the editor. Does not repaint.
   */
  public void selectAll() {
    int sz = objects.size();
    NetObject o;
    onlyTextSelected = true;
    for (int i = 0; i < sz; i++) {
      o = (NetObject) objects.get(i);
      o.setSelected(true);
      onlyTextSelected |= (o.type == NetObject.OBJECT_TEXT);
    }
    selObject = null;
  }

  /**
   * Clear the whole editor.Clipbaord remains unchanged.
   */
  public void newAll() {

    int ok = JOptionPane.YES_OPTION;

    if (objects.size() > 0) {
      ok = JOptionPane.showConfirmDialog(pFrame, "Do you want to clear all objects. ?", "Confirm new", JOptionPane.YES_NO_OPTION);
      if (ok == JOptionPane.YES_OPTION) {
        clearObjects();
        setNeedToSave(true,"New all");
        repaint();
      }
    }

  }

  /**
   * Draws arrow with link.
   * @param b True to enable arrow.
   */
  public void setShowArrow(boolean b) {
    showArrow = b;
    repaint();
  }

  /**
   * Returns true if the editor is displaying arrow with link.
   * @return arrow mode
   */
  public boolean isShowingArrow() {
    return showArrow;
  }

  /**
   * Sets editor in creation mode. Does not have any effects if the editor
   * is not editable. To create bubble object, createBubbleObject() is called.
   * @param type Type of object to be created
   * @see NetEditor#CREATE_NONE
   * @see NetEditor#CREATE_BUBBLE
   * @see NetEditor#CREATE_TEXT
   * @see NetEditor#CREATE_LINK
   * @see NetEditor#createBubbleObject
   */
  public void setCreateMode(int type) {

    if (!isEditable) {
      return;
    }

    createMode = type;

    switch (type) {
      case CREATE_BUBBLE:
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        break;
      case CREATE_TEXT:
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        break;
      case CREATE_LINK:
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        unselectAll();
        repaint();
        break;
    }

  }

  /**
   * Called when the editor adds a bubble object to the network.
   * It you override this function , Do not return an Object which
   * override NetObject, else the editor will reconvert them to NetObject
   * during clipboard , undo or file loading operation.
   * This function is provided to customize the editor and create bubbles
   * with a set of extensions and various default value. If null is returned
   * no object is added to the network.
   * @param x X coordinates (GRID coordinates)
   * @param y Y coordinates (GRID coordinates)
   * @return Created NetObject or null to ignore
   * @see NetEditor#setCreateMode
   */
  public NetObject createBubbleObject(int x,int y) {
    return new NetObject(NetObject.OBJECT_BUBBLE,0,10,10,x,y);
  }

  /** move selection to clipbaord */
  public void cutSelection() {
    copySelection();
    deleteSelection();
  }

  /** copy current selection to the clipboard */
  public void copySelection() {

    NetObject no,o;
    int i,j;
    int[] nIdx = new int[objects.size()];
    int[] oIdx = new int[objects.size()];

    if (!isEditable) {
      return;
    }

    clipboard.clear();

    // Initialise the 'index to new index' relation in order to recreate link in the clipboard ---

    for (i = 0; i < objects.size(); i++) {
      nIdx[i] = -1;
      oIdx[i] = -1;
    }

    // Create new objects into the clipboard -----------------------------------------------------

    for (j = 0, i = 0; i < objects.size(); i++) {

      o = (NetObject) objects.get(i);

      if (o.getSelected()) {

        // Translate new objects to avoid paste overlap
        if (o.type == NetObject.OBJECT_TEXT)
          no = o.getCopyAt(o.org.x + 2 * XGRID_SIZE, o.org.y + 2 * YGRID_SIZE);
        else
          no = o.getCopyAt(o.org.x + 2, o.org.y + 2);

        // add to the clipboard
        clipboard.add(no);

        // Update the idx <=> nIdx relation
        nIdx[i] = j;
        oIdx[j] = i;
        j++;

      }

    }

    // Check clipboard size ---------------------------------------------------------------------

    if (clipboard.size() == 0) {
      error("Nothing to copy, empty selection.");
      return;
    }

    // Rebuild link in the clipboard ------------------------------------------------------------

    for (i = 0; i < clipboard.size(); i++) {

      // Get the cloned object and the source object
      no = (NetObject) clipboard.get(i);
      o = (NetObject) objects.get(oIdx[i]);

      for (j = 0; j < o.getChildrenNumber(); j++) {

        // Get the corresponding child id and map it to clipboard object index
        int childIdx = nIdx[objects.indexOf(o.getChildAt(j))];
        // If the child is also in the clipbaord, create the link
        if (childIdx != -1) no.addChild((NetObject) clipboard.get(childIdx));

      }

    }

  }

  /** paste clipboard */
  public void pasteSelection() {

    NetObject o;
    int i;

    if (!isEditable) {
      return;
    }

    if (clipboard.size() == 0) {
      error("Nothing to paste, clipboard empty.");
      return;
    }

    unselectAll();

    // Add clipboard to the current net
    for (i = 0; i < clipboard.size(); i++) {
      o = (NetObject) clipboard.get(i);
      if( addObject(o) )
        o.setSelected(true);
    }

    // Recopy current selection into the clipbaord
    copySelection();

    setNeedToSave(true,"Paste");
    repaint();

  }

  /** delete current selection */
  public void deleteSelection() {

    Vector sel = new Vector();
    int i,toDel;
    NetObject o;

    if (!isEditable) {
      return;
    }

    // Delete link -------------------------------------------------------------

    if (selObject != null) {

      if (selObject.selSet >= NetObject.SEL_LINK) {
        toDel = selObject.selSet - NetObject.SEL_LINK;
        selObject.removeChild(toDel);
        unselectAll();
        setNeedToSave(true,"Delete link");
        repaint();
        return;
      }

    }

    // Delete object selection -------------------------------------------------

    // Build a list of object to delete
    for (i = 0; i < objects.size(); i++) {
      o = (NetObject) objects.get(i);
      if (o.getSelected()) sel.add(o);
    }

    // Check empty selection
    if (sel.size() == 0) {
      error("Nothing to delete, empty selection.");
      return;
    }

    // Now remove
    for (i = 0; i < sel.size(); i++)
      removeObject(((NetObject) sel.get(i)));

    sel.clear();
    unselectAll();

    setNeedToSave(true,"Cut/Delete");
    repaint();

  }

  /** Get undo state */
  public boolean canUndo() {
    return (isEditable && undoPos>=2);
  }

  /** Get the name of the last action performed */
  public String getUndoActionName() {
    if( canUndo() ) {
      return ((UndoBuffer)undo.get(undoPos-1)).getName();
    } else {
      return "";
    }
  }

  /** Get the name of the last action undone */
  public String getRedoActionName() {
    if( canRedo() ) {
      return ((UndoBuffer)undo.get(undoPos)).getName();
    } else {
      return "";
    }
  }

  /** Get redo state */
  public boolean canRedo() {
    return (isEditable && undoPos<undo.size());
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

  /**
   * Add a NetEditor listener.
   * @param l Editor listener.
   * @see NetEditorListener
   */
  public void addEditorListener(NetEditorListener l) {
    listeners.add(l);
  }

  /**
   * Remove a NetEditor listener.
   * @param l Editor listener.
   * @see NetEditorListener
   */
  public void removeEditorListener(NetEditorListener l) {
    listeners.remove(l);
  }

  /**
   * Clears the NetEditor listener list.
   * @see NetEditorListener
   */
  public void clearEditorListener() {
    listeners.clear();
  }

  /** Returns true if the scheme has been modified and need
      to be saved */
  public boolean getNeedToSaveState() {
    return needToSave;
  }

  /** Ask the editor to recompute it size.
   * @see NetEditorListener#sizeChanged */
  public void computePreferredSize() {

    // min size
    int maxx = 320;
    int maxy = 200;
    for (int i = 0; i < objects.size(); i++) {
      ((NetObject) objects.get(i)).updateRepaintRect();
      Rectangle r = ((NetObject) objects.get(i)).getRepaintRect();
      if (r.x+r.width > maxx)  maxx = r.x+r.width;
      if (r.y+r.height> maxy ) maxy = r.y+r.height;
    }

    // margin
    maxx += 10;
    maxy += 10;

    Dimension d = new Dimension(maxx, maxy);
    setPreferredSize(d);
    setMaximumSize(d);
    invalidate();

    // fire sizeChanged
    for(int i=0;i<listeners.size();i++)
     ((NetEditorListener)listeners.get(i)).sizeChanged(this,d);

  }

  /** Returns the file name of the last laoded/saved net/xpss file */
  public String getFileName() {
    return currentFileName;
  }

  /**
   * Returns the NetObject at the specified index.
   * @param i Object index
   * @return NetObject
   */
  public NetObject getNetObjectAt(int i) {
    return (NetObject)objects.get(i);
  }

  /**
   * Remove a NetObject from the editor.
   * @param obj Object to be removed
   */
  public void removeObject(NetObject obj) {

    int i;
    int sz =  objects.size();;
    NetObject o;

    // Remove input of all childrem
    obj.clearChildren();

    // Remove output of all parent
    for (i = 0; i < sz; i++) {
      o = (NetObject) objects.get(i);
      if (o.isParentOf(obj)) o.removeChild(obj);
    }

    //finally remove the object
    objects.remove(obj);
    obj.setParent(null);

  }

  /**
   * Add a NetObject to the editor.
   * @param obj Object to be added
   * @return true if the object has been succesfully added.
   */
  public boolean addObject(NetObject obj) {

    if(obj==null) return false;

    obj.setParent(this);
    objects.add(obj);
    return true;

  }

  /**
   * Clear all object in the editor.
   */
  public void clearObjects() {

    unselectAll();
    objects.clear();

  }

  /** Returns the number of NetObject in the editor */
  public int getNetObjectNumber() {
    return objects.size();
  }

  /** Display the global option dialog */
  public void showOptionDialog() {
    dlgOpt.showOption();
  }

  /** Sets the global option dialog. Allows to build a customized Editor option dialog. */
  public void setNetEditorDialog(NetEditorDlg dlg) {
    dlgOpt = dlg;
  }

  /** Sets the object properties dialog. Allows to build a custoimized Object editon dialog. */
  public void setNetObjectDialog(NetObjectDlg dlg) {
    dlgProp = dlg;
  }

  /** Allow the user to move bubble object even when the editor is not editable */
  public void setMoveableBubble(boolean b) {
    moveBubble=b;
  }

  /** Returns the parent frame or null */
  public JFrame getParentFrame() {
    return pFrame;
  }

  /**
   * Sets the grid size of this NetEditor.
   * @param d Grid dimension
   */
  public void setGridSize(Dimension d) {
    XGRID_SIZE = d.width;
    YGRID_SIZE = d.height;
    repaint();
  }

  /** Returns current grid size. */
  public Dimension getGridSize() {
    return new Dimension(XGRID_SIZE,YGRID_SIZE);
  }

  // -----------------------------------------------------
  // Dragging stuff
  // -----------------------------------------------------

  private NetObject findObject(MouseEvent e) {
    int i;
    boolean found = false;
    int foundC = -1;
    NetObject o = null;
    int sz = objects.size();
    int ex = e.getX();
    int ey = e.getY();

    // Search label
    i = 0;
    while (i < sz && !found) {
      o = (NetObject) objects.get(i);
      found = o.labelContains(ex, ey);
      if (!found) i++;
    }
    if (found) {
      o.selSet = NetObject.SEL_LABEL;
      return o;
    }

    // Search object
    i = 0;
    while (i < sz && !found) {
      o = (NetObject) objects.get(i);
      found = o.contains(ex, ey);
      if (!found) i++;
    }

    if (found) {
      o.selSet = NetObject.SEL_OBJECT;
      return o;
    }

    // Searck link
    i = 0;
    while (i < sz && (foundC == -1)) {
      o = (NetObject) objects.get(i);
      foundC = o.childContains(ex, ey);
      if (foundC == -1) i++;
    }
    if (foundC != -1) {
      o.selSet = NetObject.SEL_LINK + foundC;
      return o;
    }

    return null;

  }

  private void initDragObject(MouseEvent e) {

    int sz = objects.size();

    dragMode = DRAG_OBJECT;
    onlyTextSelected = true;
    for (int i = 0; i < sz; i++) {
      NetObject o = (NetObject) objects.get(i);
      if (o.getSelected()) {
        o.resetDrag();
        onlyTextSelected = onlyTextSelected && (o.type == NetObject.OBJECT_TEXT);
      }
    }
    dragStart.x = e.getX();
    dragStart.y = e.getY();
    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

  }

  private void initDragSelection(MouseEvent e) {

    dragMode = DRAG_SELECTION;
    dragStart.x = e.getX();
    dragStart.y = e.getY();
    selDrag.setRect(e.getX(), e.getY(), 0, 0);

  }

  private void initDragLabel(MouseEvent e, NetObject o) {

    dragMode = DRAG_LABEL;
    dragStart.x = e.getX();
    dragStart.y = e.getY();
    selObject = o;
    selObject.dragStart.x = o.labelOffset.x;
    selObject.dragStart.y = o.labelOffset.y;
    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

  }

  private void initDragLink(MouseEvent e, NetObject o) {

    dragStart.x = o.org.x * XGRID_SIZE;
    dragStart.y = o.org.y * YGRID_SIZE;
    selDrag.setRect(e.getX(), e.getY(), e.getX(), e.getY());
    lnkObject = o;
    dragMode = DRAG_LINK;

  }

  private void initDragMoveLink(MouseEvent e, NetObject o) {

    unselectAll();
    dragStart.x = e.getX();
    dragStart.y = e.getY();
    selObject = o;
    lnkObject = o.getChildAt(o.selSet-NetObject.SEL_LINK);
    selObject.resetDrag();
    lnkObject.resetDrag();
    dragMode = DRAG_LINKMOVE;
    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

  }

  private void createObject(MouseEvent e) {

    int x=0,y=0;
    NetObject o=null;

    switch (createMode) {
      case CREATE_BUBBLE:
        x = (e.getX() + XGRID_SIZE / 2) / XGRID_SIZE;
        y = (e.getY() + YGRID_SIZE / 2) / YGRID_SIZE;
        o = createBubbleObject(x,y);
        break;
      case CREATE_TEXT:
        o = new NetObject(NetObject.OBJECT_TEXT,0,0,0,e.getX(),e.getY());
        o.setLabel("label");
        break;
    }

    if( addObject(o) )
      setNeedToSave(true,"Create " + o.getName());

    o=null;
    repaint();

  }

  private void buildSelRect(int x1, int y1, int x2, int y2) {


    if (x1 < x2) {
      if (y1 < y2) {
        selDrag.setRect(x1, y1, x2 - x1, y2 - y1);
      } else {
        selDrag.setRect(x1, y2, x2 - x1, y1 - y2);
      }
    } else {
      if (y1 < y2) {
        selDrag.setRect(x2, y1, x1 - x2, y2 - y1);
      } else {
        selDrag.setRect(x2, y2, x1 - x2, y1 - y2);
      }
    }

  }

  private void dragObjects(MouseEvent e) {

    int tx = 0,ty = 0;
    int i;
    int sz = objects.size();

    for (i = 0; i < sz; i++) {
      NetObject o = (NetObject) objects.get(i);
      if (o.getSelected()) {

        if (onlyTextSelected) {

          // Follow cursor

          tx = e.getX() - dragStart.x;
          ty = e.getY() - dragStart.y;

        } else {

          // Map cursor coordinates to the nearest grid point.

          tx = (e.getX() - dragStart.x);

          if (tx < 0) {
            if (o.type == NetObject.OBJECT_TEXT) {
              tx = ((e.getX() - dragStart.x - XGRID_SIZE / 2) / XGRID_SIZE) * XGRID_SIZE;
            } else {
              tx = (e.getX() - dragStart.x - XGRID_SIZE / 2) / XGRID_SIZE;
            }
          } else {
            if (o.type == NetObject.OBJECT_TEXT) {
              tx = ((e.getX() - dragStart.x + XGRID_SIZE / 2) / XGRID_SIZE) * XGRID_SIZE;
            } else {
              tx = (e.getX() - dragStart.x + XGRID_SIZE / 2) / XGRID_SIZE;
            }
          }

          ty = (e.getY() - dragStart.y);

          if (ty < 0) {
            if (o.type == NetObject.OBJECT_TEXT) {
              ty = ((e.getY() - dragStart.y - YGRID_SIZE / 2) / YGRID_SIZE) * YGRID_SIZE;
            } else {
              ty = (e.getY() - dragStart.y - YGRID_SIZE / 2) / YGRID_SIZE;
            }
          } else {
            if (o.type == NetObject.OBJECT_TEXT) {
              ty = ((e.getY() - dragStart.y + YGRID_SIZE / 2) / YGRID_SIZE) * YGRID_SIZE;
            } else {
              ty = (e.getY() - dragStart.y + YGRID_SIZE / 2) / YGRID_SIZE;
            }
          }

        }

        // Saturate

        if (o.type == NetObject.OBJECT_TEXT) {


          switch (o.justify) {
            case NetObject.JUSTIFY_CENTER:
              o.org.x = saturateLow(o.dragStart.x + tx, o.getBoundRect().width / 2);
              o.org.y = saturateLow(o.dragStart.y + ty, o.labelAscent);
              break;
            case NetObject.JUSTIFY_RIGHT:
              o.org.x = saturateLow(o.dragStart.x + tx, o.getBoundRect().width);
              o.org.y = saturateLow(o.dragStart.y + ty, o.labelAscent);
              break;
            case NetObject.JUSTIFY_LEFT:
              o.org.x = saturateLow(o.dragStart.x + tx, 0);
              o.org.y = saturateLow(o.dragStart.y + ty, o.labelAscent);
              break;
          }

        } else {

          o.org.x = saturateLow(o.dragStart.x + tx, 1);
          o.org.y = saturateLow(o.dragStart.y + ty, 1);

        }

      }
    }

  }

  private void dragLink(MouseEvent e) {

    int tx = 0,ty = 0;

    // Map cursor coordinates to the nearest grid point.
    tx = (e.getX() - dragStart.x);

    if (tx < 0)
      tx = (e.getX() - dragStart.x - XGRID_SIZE / 2) / XGRID_SIZE;
    else
      tx = (e.getX() - dragStart.x + XGRID_SIZE / 2) / XGRID_SIZE;

    ty = (e.getY() - dragStart.y);

    if (ty < 0)
      ty = (e.getY() - dragStart.y - YGRID_SIZE / 2) / YGRID_SIZE;
    else
      ty = (e.getY() - dragStart.y + YGRID_SIZE / 2) / YGRID_SIZE;

    // Saturate

    selObject.org.x = saturateLow(selObject.dragStart.x + tx, 1);
    selObject.org.y = saturateLow(selObject.dragStart.y + ty, 1);

    lnkObject.org.x = saturateLow(lnkObject.dragStart.x + tx, 1);
    lnkObject.org.y = saturateLow(lnkObject.dragStart.y + ty, 1);

  }

  // -----------------------------------------------------
  // Mouse listener
  // -----------------------------------------------------
  
  public void mouseDragged(MouseEvent e) {

    switch (dragMode) {

      case DRAG_OBJECT:
        dragObjects(e);
        repaint();
        break;

      case DRAG_LINKMOVE:
        dragLink(e);
        repaint();
        break;

      case DRAG_SELECTION:
        buildSelRect(dragStart.x, dragStart.y, e.getX(), e.getY());
        repaint();
        break;

      case DRAG_LINK:
        selDrag.setRect(dragStart.x, dragStart.y, e.getX(), e.getY());
        repaint();
        break;

      case DRAG_LABEL:
        selObject.labelOffset.x = selObject.dragStart.x + (e.getX() - dragStart.x);
        selObject.labelOffset.y = selObject.dragStart.y + (e.getY() - dragStart.y);
        repaint();
        break;

    }


  }

  public void mouseMoved(MouseEvent e) {

  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mouseClickedB3(MouseEvent e) {
  }

  public void mouseClickedB1(MouseEvent e) {

    if (e.getClickCount() == 2) {
      NetObject o = findObject(e);
      if (o != null) {
        if (o.selSet == NetObject.SEL_OBJECT || o.selSet == NetObject.SEL_LABEL) {
          if (o.hasProperties()) {
            dlgProp.editObject(o);
            if (dlgProp.getModified())
              setNeedToSave(true, "Change properties");
          }
        }
      }
    }

  }

  public void mouseClicked(MouseEvent e) {

    if (isEditable) {

      if (e.getButton() == MouseEvent.BUTTON1) mouseClickedB1(e);
      if (e.getButton() == MouseEvent.BUTTON3) mouseClickedB3(e);

    } else {

      NetObject o = findObject(e);
      if (o != null) {
        if (o.selSet == NetObject.SEL_OBJECT) {
          fireObjectClicked(o, e);
        } else if (o.selSet >= NetObject.SEL_LINK ) {
          fireLinkClicked(o,o.selSet-NetObject.SEL_LINK,e);
        }
      }

    }

  }

  public void mouseReleased(MouseEvent e) {

    int i = 0;
    int sz = objects.size();

    switch (dragMode) {

      case DRAG_OBJECT:
      case DRAG_LABEL:

        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if( dragStart.x != e.getX() || dragStart.y != e.getY() )
          setNeedToSave(true,"Move object");
        dragMode = DRAG_NONE;
        break;

      case DRAG_LINKMOVE:

        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if( dragStart.x != e.getX() || dragStart.y != e.getY() )
          setNeedToSave(true,"Move link");
        dragMode = DRAG_NONE;
        break;

      case DRAG_SELECTION:

        dragMode = DRAG_NONE;
        for (i = 0; i < sz; i++) {
          NetObject o = (NetObject) objects.get(i);
          onlyTextSelected = true;
          if (o.inside(selDrag)) {
            o.setSelected(true);
            onlyTextSelected |= (o.type == NetObject.OBJECT_TEXT);
          }
        }
        repaint();
        break;

      case DRAG_LINK:

        NetObject o = findObject(e);
        if (o != null) {
          if (o != lnkObject) {
            if (o.acceptInput()) {
              if (!lnkObject.isParentOf(o)) {
                lnkObject.addChild(o);
                setNeedToSave(true, "Create link");
              } else {
                error("Link already exists.");
              }
            } else {
              error("This object does not accept more incoming link.");
            }
          } else {
            error("Cannot link to itself.\nHint: to return to selection mode, right click");
          }
        }
        dragMode = DRAG_NONE;
        repaint();
        break;

      default:
        dragMode = DRAG_NONE;

    }

  }

  public void mousePressedB1(MouseEvent e) {

    if (createMode != CREATE_NONE && createMode != CREATE_LINK) {
      createObject(e);
      return;
    }

    NetObject o = findObject(e);

    if (o != null) {

      if (isEditable) {

        if (createMode == CREATE_LINK) {
          if (o.acceptOutput()) {
            unselectAll();
            o.setSelected(true);
            initDragLink(e, o);
            repaint();
          } else {
            error("This object does not accept more outgoing link.");
          }
          return;
        }

        if (o.selSet >= NetObject.SEL_LINK) {

          // Click on a link
          initDragMoveLink(e, o);

        } else {

          if (e.isControlDown()) {
            o.setSelected(!o.getSelected());
          } else {
            if (!o.getSelected()) {
              unselectAll();
              o.setSelected(true);
            }
          }

          if (!e.isControlDown()) {
            switch (o.selSet) {
              case NetObject.SEL_OBJECT:
                initDragObject(e);
                break;
              case NetObject.SEL_LABEL:
                initDragLabel(e, o);
                break;
            }
          }

        }

        repaint();

      } else {

        // Not editable
        if (o.selSet == NetObject.SEL_OBJECT) {
          if (moveBubble && o.type == NetObject.OBJECT_BUBBLE) {
            unselectAll();
            o.setSelected(true);
            initDragObject(e);
          }
        }

      }


    } else {

      if (!e.isControlDown() && isEditable && createMode == CREATE_NONE) {
        unselectAll();
        initDragSelection(e);
        repaint();
      }

    }


  }

  public void mousePressedB3(MouseEvent e) {

    if (createMode != CREATE_NONE) {
      createMode = CREATE_NONE;
      fireCancelCreate();
    }
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

  }

  public void mousePressed(MouseEvent e) {

    grabFocus();
    if (e.getButton() == MouseEvent.BUTTON1) mousePressedB1(e);
    if (e.getButton() == MouseEvent.BUTTON3) mousePressedB3(e);

  }

  // -----------------------------------------------------------------
  // painting stuff
  // -----------------------------------------------------------------

  /**
   * Paint link of the scheme. Called by paint() before paintObjects().
   * You can override it to custom the link painting.
   * Here is an example code for painting all links (Note that this code not
   * handle link selection when the editor is editable):
   * <p>
   * <pre>
   *  public void <strong>paintLinks</strong>(Graphics2D g2) {
   *    g2.setColor(Color.black);
   *    for (int i = 0; i < getNetObjectNumber() ; i++) {
   *      NetObject o = getNetObjectAt(i);
   *      for (int j = 0; j < o.getChildrenNumber() ; j++ ) {
   *        o.paintLink(g2,o.getChildAt(j),true);
   *      }
   *    }
   *  }
   *</pre>
   * @param g2 Graphics object
   * @see NetEditor#paintObjects
   */
  public void paintLinks(Graphics2D g2) {

    for (int i = 0; i < objects.size(); i++) {
      NetObject o = (NetObject) objects.get(i);
      o.paintLinks(g2, showArrow, (isEditable)?(selObject == o):false);
    }

  }

  /**
   * Paint object of the scheme. Called by paint() after paintLinks().
   * You can override it to custom the object painting.
   * <p>
   * <pre>
   *  public void <strong>paintObjects</strong>(Graphics2D g2) {
   *    for (int i = 0; i < getNetObjectNumber() ; i++)
   *      getNetObjectAt(i).paint(g2);
   *  }
   *</pre>
   * @param g2 Graphics object
   * @see NetEditor#paintLinks
   */
  public void paintObjects(Graphics2D g2) {

    int i,sz;
    sz = objects.size();
    for (i = 0; i < sz; i++)
      ((NetObject) objects.get(i)).paint(g2);

  }


  /**
   * Paint the component. It is not recommended to override paint(). If you want
   * to custom link and object painting , it is preferable to override
   * paintLinks() and paintObjects().
   * @param g Graphics object
   * @see NetEditor#paintLinks
   * @see NetEditor#paintObjects
   */
  public void paint(Graphics g) {

    //long t0 = System.currentTimeMillis();

    Dimension d = getSize();
    Stroke old;

    Graphics2D g2 = (Graphics2D) g;

    // Paint Background
    g.setColor(getBackground());
    g.fillRect(0, 0, d.width, d.height);

    // Paint the grid
    paintGrid(g2);

    // Paint links
    paintLinks(g2);

    // Set up high quality font
    if (useAAFont) {
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
              RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
            RenderingHints.VALUE_FRACTIONALMETRICS_ON);

    // Paint objects
    paintObjects(g2);

    // Paint selection
    if (dragMode == DRAG_SELECTION) {
      old = g2.getStroke();
      g2.setStroke(dashStroke);
      g2.setColor(Color.DARK_GRAY);
      g2.drawRect(selDrag.x, selDrag.y, selDrag.width, selDrag.height);
      g2.setStroke(old);
    }

    if (dragMode == DRAG_LINK) {
      old = g2.getStroke();
      g2.setStroke(dashStroke);
      g2.setColor(Color.DARK_GRAY);
      g2.drawLine(selDrag.x, selDrag.y, selDrag.width, selDrag.height);
      g2.setStroke(old);
    }

    //long t1 = System.currentTimeMillis();
    //System.out.println("Painting time: " + (t1 - t0) + " ms");

  }

  private void paintGrid(Graphics2D g) {

    int i,j;
    Dimension d = getSize();
    if (isEditable) {
      g.setColor(Color.BLACK);
      for (i = 0; i < d.width; i += XGRID_SIZE)
        for (j = 0; j < d.height; j += YGRID_SIZE)
          g.drawLine(i, j, i, j);
    }

  }

  // ---------------------------------------------------------------
  // Miscelaneous stuff
  // ---------------------------------------------------------------

  private void error(String m) {
    JOptionPane.showMessageDialog(pFrame, m, "Error",
            JOptionPane.ERROR_MESSAGE);
  }

  private void fireValueChanged() {
      for(int i=0;i<listeners.size();i++)
       ((NetEditorListener)listeners.get(i)).valueChanged(this);
  }

  private void fireCancelCreate() {
    if(isEditable)
      for(int i=0;i<listeners.size();i++)
       ((NetEditorListener)listeners.get(i)).cancelCreate(this);
  }

  private void fireObjectClicked(NetObject o,MouseEvent e) {
    if(!isEditable) {
      for(int i=0;i<listeners.size();i++)
       ((NetEditorListener)listeners.get(i)).objectClicked(this,o,e);
    }
  }

  private void fireLinkClicked(NetObject o,int id,MouseEvent e) {
    if(!isEditable) {
      for(int i=0;i<listeners.size();i++)
       ((NetEditorListener)listeners.get(i)).linkClicked(this,o,id,e);
    }
  }

  private int saturateLow(int x, int min) {
    if (x <= min) return min;
    else          return x;
  }

  private void setNeedToSave(boolean b,String s) {

    //System.out.println("Need to save:(" + s + ")" + b);

    needToSave = b;

    if( needToSave && isEditable ) {
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

  private void resetUndo() {
    undo.clear();
    undo.add(new UndoBuffer(objects,"Init"));
    undoPos=1;
  }

  private void rebuildBackup(int pos) {

    // Free All
    clearObjects();

    //Rebuild form backup
    NetObject[] objs = ((UndoBuffer)undo.get(pos)).rebuild();

    // Add object
    for(int i=0;i<objs.length;i++) addObject(objs[i]);

    fireValueChanged();
    repaint();
  }

}
