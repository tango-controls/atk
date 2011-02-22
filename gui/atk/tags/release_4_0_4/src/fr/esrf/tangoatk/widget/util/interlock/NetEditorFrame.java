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
 
/**
 * A set of class to handle a network editor and its viewer.
 *
 * Author: Jean Luc PONS
 * Date: Jul 1, 2004
 * (c) ESRF 2004
 */
package fr.esrf.tangoatk.widget.util.interlock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** A Frame for the NetEditor. This class is a good base class to create
 * specific network editor. By default, It constructs all menus needed by
 * the NetEditor and its toolbar.
 * <p>All actions are handled by this frame. So you can override
 * actionPerformed() to get the control on a menu item or
 * a button. Ex: overriding the 'load' menu item action
 * <pre>
 * public void <strong>actionPerformed</strong>(ActionEvent evt) {
 *
 *   if( evt.getSource() == getFileMenuItem(NetEditorFrame.FILE_LOAD) ) {
 *     ...
 *   } else {
 *     super.actionPerformed(evt);
 *   }
 *
 * }
 * </pre>
 * <p>You can also add or remove menu items or toolbar buttons. Here is an example of code
 * that customize the option menu :
 * <pre>
 *  traceMode = NetUtils.createMenuItem("Trace mode",0,0,this);
 *  editMode = NetUtils.createMenuItem("Edit mode",0,0,this);
 *  getOptionMenu().add(traceMode,0);
 *  getOptionMenu().add(editMode,1);
 *  getOptionMenu().add(new JSeparator(),2);
 *</pre>
 */
public class NetEditorFrame extends JFrame implements NetEditorListener,ActionListener {

  // Public constant
  public final static int FILE_NEW = 1;
  public final static int FILE_LOAD = 2;
  public final static int FILE_SAVE = 3;
  public final static int FILE_SAVEAS = 4;
  public final static int FILE_EXIT = 5;

  public final static int EDIT_UNDO = 1;
  public final static int EDIT_REDO = 2;
  public final static int EDIT_CUT = 3;
  public final static int EDIT_COPY = 4;
  public final static int EDIT_PASTE = 5;
  public final static int EDIT_DELETE = 6;
  public final static int EDIT_SELECT_ALL = 7;
  public final static int EDIT_SELECT_NONE = 8;

  public final static int OPTION_FIT  = 1;
  public final static int OPTION_PREF = 2;

  public final static int TOOL_BUBBLE = 1;
  public final static int TOOL_LABEL = 2;
  public final static int TOOL_LINK = 3;
  public final static int TOOL_UNDO = 4;
  public final static int TOOL_REDO = 5;

  // ----------------------------------------------
  // private declaration
  // ----------------------------------------------
  private String appRelease = "Network editor";

  // The editor view
  private JScrollPane netView;
  private NetEditor   netEditor;

  // Menu
  private JMenu     jMenuFile;
  private JMenuItem jMenuItemNew;
  private JMenuItem jMenuItemLoad;
  private JMenuItem jMenuItemSave;
  private JMenuItem jMenuItemSaveAs;
  private JMenuItem jMenuItemExit;

  private JMenu     jMenuEdit;
  private JMenuItem jMenuItemUndo;
  private JMenuItem jMenuItemRedo;
  private JMenuItem jMenuItemCut;
  private JMenuItem jMenuItemCopy;
  private JMenuItem jMenuItemPaste;
  private JMenuItem jMenuItemDelete;
  private JMenuItem jMenuItemSelectAll;
  private JMenuItem jMenuItemSelectNone;

  private JMenu       jMenuOptions;
  private JMenuItem   jMenuItemFit;
  private JMenuItem   jMenuItemPref;

  // Toolbar
  private JToolBar toolBar;
  private JButton  bubbleBtn;
  private JButton  labelBtn;
  private JButton  linkBtn;
  private JButton  undoBtn;
  private JButton  redoBtn;

  // Help label
  private JLabel helpLabel;

  final static Insets bMargin = new Insets(3, 3, 3, 3);

  /**
   * Construct a frame for the NetEditor.
   * @see NetEditorFrame#setEditor
   */
  public NetEditorFrame() {

    getContentPane().setLayout(new BorderLayout());
    setTitle(appRelease);
    netEditor = null;
    netView = null;

    // File menu --------------------------------------------------------------------------

    jMenuFile = new JMenu();
    jMenuFile.setText("File");
    jMenuFile.setMnemonic('F');
    jMenuItemNew    = NetUtils.createMenuItem("New",0,0,this);
    jMenuItemLoad   = NetUtils.createMenuItem("Load file...",0,0,this);
    jMenuItemSave   = NetUtils.createMenuItem("Save",KeyEvent.VK_S, InputEvent.CTRL_MASK,this);
    jMenuItemSaveAs = NetUtils.createMenuItem("Save as...",0,0,this);
    jMenuItemExit = NetUtils.createMenuItem("Exit",0,0,this);

    jMenuFile.add(jMenuItemNew);
    jMenuFile.add(jMenuItemLoad);
    jMenuFile.add(jMenuItemSave);
    jMenuFile.add(jMenuItemSaveAs);
    jMenuFile.add(new JSeparator());
    jMenuFile.add(jMenuItemExit);

    // Edit menu --------------------------------------------------------------------------

    jMenuEdit = new JMenu();
    jMenuEdit.setText("Edit");
    jMenuEdit.setMnemonic('E');

    jMenuItemUndo       = NetUtils.createMenuItem("Undo",KeyEvent.VK_Z, InputEvent.CTRL_MASK,this);
    jMenuItemRedo       = NetUtils.createMenuItem("Redo",KeyEvent.VK_Z, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK,this);
    jMenuItemCut        = NetUtils.createMenuItem("Cut",KeyEvent.VK_X, InputEvent.CTRL_MASK,this);
    jMenuItemCopy       = NetUtils.createMenuItem("Copy",KeyEvent.VK_C, InputEvent.CTRL_MASK,this);
    jMenuItemPaste      = NetUtils.createMenuItem("Paste",KeyEvent.VK_V, InputEvent.CTRL_MASK,this);
    jMenuItemDelete     = NetUtils.createMenuItem("Delete",KeyEvent.VK_DELETE, 0,this);
    jMenuItemSelectAll  = NetUtils.createMenuItem("Select All",KeyEvent.VK_A, InputEvent.CTRL_MASK,this);
    jMenuItemSelectNone = NetUtils.createMenuItem("Clear selection",KeyEvent.VK_N, InputEvent.CTRL_MASK,this);

    jMenuEdit.add(jMenuItemUndo);
    jMenuEdit.add(jMenuItemRedo);
    jMenuEdit.add(new JSeparator());
    jMenuEdit.add(jMenuItemCut);
    jMenuEdit.add(jMenuItemCopy);
    jMenuEdit.add(jMenuItemPaste);
    jMenuEdit.add(jMenuItemDelete);
    jMenuEdit.add(new JSeparator());
    jMenuEdit.add(jMenuItemSelectAll);
    jMenuEdit.add(jMenuItemSelectNone);


    // Options menu ----------------------------------------------------------------------

    jMenuOptions = new JMenu();

    jMenuOptions.setText("Options");
    jMenuOptions.setMnemonic('O');
    jMenuItemFit = NetUtils.createMenuItem("Fit drawing area to scheme",0, 0,this);
    jMenuItemPref = NetUtils.createMenuItem("Edit preferences ...",0, 0,this);

    jMenuOptions.add(jMenuItemFit);
    jMenuOptions.add(jMenuItemPref);

    // Tool bar --------------------------------------------------------------------------

    toolBar = new JToolBar();

    String rPth = "/fr/esrf/tangoatk/widget/util/interlock/gif/";

    bubbleBtn = createIconButton(rPth,"bubble","Create a bubble object",this);
    toolBar.add(bubbleBtn);
    labelBtn = createIconButton(rPth,"label","Create a label object",this);
    toolBar.add(labelBtn);
    toolBar.add(new JToolBar.Separator());
    linkBtn = createIconButton(rPth,"link","Link an object to an other",this);
    toolBar.add(linkBtn);
    toolBar.add(new JToolBar.Separator());
    undoBtn = createIconButton(rPth,"undo","Undo last action",this);
    toolBar.add(undoBtn);
    redoBtn = createIconButton(rPth,"redo","Redo last action",this);
    toolBar.add(redoBtn);

    toolBar.setOrientation(JToolBar.VERTICAL);

    // Help label --------------------------------------------------------------------------

    helpLabel = new JLabel();
    helpLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
    helpLabel.setText("");
    helpLabel.setPreferredSize(new Dimension(0, 16));

    JMenuBar mainMenu = new JMenuBar();
    mainMenu.add(jMenuFile);
    mainMenu.add(jMenuEdit);
    mainMenu.add(jMenuOptions);
    setJMenuBar(mainMenu);
    getContentPane().add(toolBar, BorderLayout.WEST);
    getContentPane().add(helpLabel, BorderLayout.SOUTH);

    // Window listener ---------------------------------------------
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {exitApp();}
    }
    );

  }

  /**
   * Sets the NetEditor.
   * @param editor NetEditor object
   */
  public void setEditor(NetEditor editor) {
    netEditor=editor;
    netEditor.addEditorListener(this);
    netView = new JScrollPane(netEditor);
    netView.setPreferredSize(new Dimension(800, 600));
    getContentPane().add(netView, BorderLayout.CENTER);
    // Update components
    valueChanged(netEditor);
  }
  /**
   * Sets the app name used to build the frame title.
   * @param appName application name
   * @see NetEditorFrame#valueChanged
   */
  public void setAppTitle(String appName) {
    appRelease = appName;
  }

  /** Returns the file menu. */
  public JMenu getFileMenu() {
    return jMenuFile;
  }

/**
 * Returns a reference to the specified menu item of the file menu.
 * @param which Menu identifier
 * @see NetEditorFrame#FILE_NEW
 * @see NetEditorFrame#FILE_LOAD
 * @see NetEditorFrame#FILE_SAVE
 * @see NetEditorFrame#FILE_SAVEAS
 * @see NetEditorFrame#FILE_EXIT
 */
  public JMenuItem getFileMenuItem(int which) {

    switch(which) {
      case FILE_NEW:
        return jMenuItemNew;
      case FILE_LOAD:
        return jMenuItemLoad;
      case FILE_SAVE:
        return jMenuItemSave;
      case FILE_SAVEAS:
        return jMenuItemSaveAs;
      case FILE_EXIT:
        return jMenuItemExit;
    }

    return null;
  }

  /** Returns the edit menu. */
  public JMenu getEditMenu() {
    return jMenuEdit;
  }

  /**
   * Returns a reference to the specified menu item of the edit menu.
   * @param which Menu identifier
   * @see NetEditorFrame#EDIT_UNDO
   * @see NetEditorFrame#EDIT_REDO
   * @see NetEditorFrame#EDIT_CUT
   * @see NetEditorFrame#EDIT_COPY
   * @see NetEditorFrame#EDIT_PASTE
   * @see NetEditorFrame#EDIT_DELETE
   * @see NetEditorFrame#EDIT_SELECT_ALL
   * @see NetEditorFrame#EDIT_SELECT_NONE
   */
  public JMenuItem getEditMenuItem(int which) {

    switch (which) {
      case EDIT_UNDO:
        return jMenuItemUndo;
      case EDIT_REDO:
        return jMenuItemRedo;
      case EDIT_CUT:
        return jMenuItemCut;
      case EDIT_COPY:
        return jMenuItemCopy;
      case EDIT_PASTE:
        return jMenuItemPaste;
      case EDIT_DELETE:
        return jMenuItemDelete;
      case EDIT_SELECT_ALL:
        return jMenuItemSelectAll;
      case EDIT_SELECT_NONE:
        return jMenuItemSelectNone;
    }

    return null;

  }


  /** Returns the option menu. */
  public JMenu getOptionMenu() {
    return jMenuOptions;
  }

  /**
   * Returns a reference to the specified menu item of the option menu.
   * @param which Menu identifier
   * @see NetEditorFrame#OPTION_FIT
   * @see NetEditorFrame#OPTION_PREF
   */
  public JMenuItem getOptionMenuItem(int which) {

    switch (which) {
      case OPTION_FIT:
        return jMenuItemFit;
      case OPTION_PREF:
        return jMenuItemPref;
    }

    return null;

  }

  /** Returns the default toolbar */
  public JToolBar getToolbar() {
    return toolBar;
  }

  /**
   * Returns a reference to the specified button of the toolbar.
   * @param which Button identifier
   * @see NetEditorFrame#TOOL_BUBBLE
   * @see NetEditorFrame#TOOL_LABEL
   * @see NetEditorFrame#TOOL_LINK
   * @see NetEditorFrame#TOOL_UNDO
   * @see NetEditorFrame#TOOL_REDO
   */
  public JButton getToobarButton(int which) {

    switch (which) {
      case TOOL_BUBBLE:
        return bubbleBtn;
      case TOOL_LABEL:
        return labelBtn;
      case TOOL_LINK:
        return linkBtn;
      case TOOL_UNDO:
        return undoBtn;
      case TOOL_REDO:
        return redoBtn;
    }

    return null;

  }

  /** Returns a reference to the help label */
  public JLabel getHelpLabel() {
    return helpLabel;
  }

  /** Return the JScrollPane that contains the NetEditor object */
  public JScrollPane getScrollPane() {
    return netView;
  }

  /** Ask to save if the current scheme has unsaved modification before exiting. */
  public void exitApp() {

    if(netEditor.getNeedToSaveState())
      if( JOptionPane.showConfirmDialog(this, "Save before closing ?","Exiting",
                                        JOptionPane.YES_NO_OPTION)== JOptionPane.YES_OPTION )
        netEditor.saveCurrent(".");

    System.exit(0);

  }

  /**
   * Create an icon button. The function laods 3 gif files from the
   * resource. They must be named "preffix_btn_up.gif" , "preffix_btn_down.gif"
   * and "preffix_btn_dis.gif".
   * @param resPath Resource path (ended with a '/')
   * @param preffix Preffix of gif files
   * @param toolTip Tooltip text
   * @param l ActionListener
   * @return an icon JButton
   */
  public JButton createIconButton(String resPath,String preffix,String toolTip,ActionListener l) {
    JButton b = new JButton();
    b.setIcon(new ImageIcon(getClass().getResource(resPath+preffix+"_btn_up.gif")));
    b.setPressedIcon(new ImageIcon(getClass().getResource(resPath+preffix+"_btn_down.gif")));
    b.setDisabledIcon(new ImageIcon(getClass().getResource(resPath+preffix+"_btn_dis.gif")));
    b.setMargin(bMargin);
    b.setBorder(null);
    b.setToolTipText(toolTip);
    b.addActionListener(l);
    return b;
  }

  /**
   * Update the title bar with the filename and the apptitle when
   * the scheme change . Update also the state of menu and icon
   * according to the editor (clipboard,selection,...)
   * @param src NetEditor
   * @see NetEditorFrame#setAppTitle
   */
  public void valueChanged(NetEditor src) {

    String title = appRelease;
    if(src.getFileName().length()>0) {
      title += " [" + src.getFileName() + "]";
    }
    if( src.getNeedToSaveState() )  setTitle(title+"*");
    else                            setTitle(title);

    jMenuItemUndo.setEnabled(src.canUndo());
    undoBtn.setEnabled(src.canUndo());
    jMenuItemUndo.setText("Undo " + src.getUndoActionName());
    jMenuItemRedo.setEnabled(src.canRedo());
    redoBtn.setEnabled(src.canRedo());
    jMenuItemRedo.setText("Redo " + src.getRedoActionName());

  }

  public void objectClicked(NetEditor src,NetObject obj,MouseEvent e) {}

  public void linkClicked(NetEditor src,NetObject obj,int childIdx,MouseEvent e) {}

  /** Revalidate the inner ScrollPane */
  public void sizeChanged(NetEditor src,Dimension d) {
    netView.revalidate();
    repaint();
  }

  /** Clear the help label when the creation mode is aborted */
  public void cancelCreate(NetEditor src) {
    helpLabel.setText("");
  }

  // -----------------------------------------------------
  // Action Listener
  // -----------------------------------------------------
  public void actionPerformed(ActionEvent evt) {

    Object src = evt.getSource();

    if (src == jMenuItemNew) {

      netEditor.newAll();

    } else if (src == jMenuItemLoad) {

      netEditor.showOpenFileDialog(".",null);

    } else if (src == jMenuItemSave) {

      netEditor.saveCurrent(".");

    } else if (src == jMenuItemSaveAs) {

      netEditor.showSaveFileDialog(".",null);

    } else if (src == bubbleBtn) {

      netEditor.setCreateMode(NetEditor.CREATE_BUBBLE);
      helpLabel.setText("Bubble creation: Left click to create a bubble object, right click to cancel.");

    } else if (src == labelBtn) {

      netEditor.setCreateMode(NetEditor.CREATE_TEXT);
      helpLabel.setText("Label creation: Left click to create a label object, right click to cancel.");

    } else if (src == linkBtn) {

      netEditor.setCreateMode(NetEditor.CREATE_LINK);
      helpLabel.setText("Link creation: Left click on fisrt object and drag the cursor up to the second object, right click to cancel.");

    } else if (src == jMenuItemDelete) {

      netEditor.deleteSelection();

    } else if (src == jMenuItemCopy) {

      netEditor.copySelection();

    } else if (src == jMenuItemPaste) {

      netEditor.pasteSelection();

    } else if (src == jMenuItemCut) {

      netEditor.cutSelection();

    } else if (src == jMenuItemSelectAll) {

      netEditor.selectAll();
      netEditor.repaint();

    } else if (src == jMenuItemSelectNone) {

      netEditor.unselectAll();
      netEditor.repaint();

    } else if (src == jMenuItemUndo || src ==undoBtn ) {

      netEditor.undo();

    } else if (src == jMenuItemRedo || src ==redoBtn) {

      netEditor.redo();

    } else if (src == jMenuItemFit ) {

      netEditor.computePreferredSize();

    } else if (src == jMenuItemPref ) {

      netEditor.showOptionDialog();

    } else if (src == jMenuItemExit) {

      exitApp();

    }

  }

  /** Main function which launch the default NerEditor . */
  public static void main(String[] args) {

    NetEditorFrame frame = new NetEditorFrame();
    NetEditor   netEditor = new NetEditor(frame);
    frame.setEditor(netEditor);
    NetUtils.centerFrameOnScreen(frame);
    frame.setVisible(true);

  }

}
