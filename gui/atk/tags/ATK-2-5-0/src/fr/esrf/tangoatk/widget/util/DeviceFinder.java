/*
 * DeviceFinder.java
 *
 * Created on June 18, 2002, 10:28 AM
 */

package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import javax.swing.tree.*;
import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.AttrDataFormat;
import fr.esrf.TangoApi.Database;
import fr.esrf.TangoApi.ApiUtil;
import fr.esrf.TangoApi.CommandInfo;
import fr.esrf.TangoApi.AttributeInfo;
import fr.esrf.tangoatk.core.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;


/**
 * A panel for selecting device , attribute or command.
 */
public class DeviceFinder extends JPanel {

  /** Select device */
  public final static int MODE_DEVICE    = 0;
  /** Select all attributes */
  public final static int MODE_ATTRIBUTE = 1;
  /** Select command */
  public final static int MODE_COMMAND   = 2;
  /** Select all scalar attributes */
  public final static int MODE_ATTRIBUTE_SCALAR = 3;

  static Database  db;
  JTree            tree;
  JScrollPane      treeView;
  DefaultTreeModel treeModel;
  int              mode;

  /**
   * Construct a DeviceFinder panel using the given mode.
   * @param mode Mode
   * @see #MODE_DEVICE
   * @see #MODE_ATTRIBUTE
   * @see #MODE_COMMAND
   */
  public DeviceFinder(int mode) {

    this.mode = mode;
    try {
      db = ApiUtil.get_db_obj();
    } catch (DevFailed e) {
      ErrorPane.showErrorMessage(null,"Database",e);
      return;
    }
    setLayout(new BorderLayout());
    createTree();
    add(treeView,BorderLayout.CENTER);

  }

  /**
   * Returns the list of selected entities.
   */
  public String[] getSelectedNames() {

    TreePath[] p = tree.getSelectionPaths();
    Vector completePath = new Vector();

    if (p != null) {
      for (int i = 0; i < p.length; i++) {

        Object[] pth = p[i].getPath();
        String name = "";

        switch (mode) {
          case MODE_DEVICE:
            if (pth.length == 4) {
              name = pth[1] + "/" + pth[2] + "/" + pth[3];
              completePath.add(name);
            }
            break;
          case MODE_ATTRIBUTE:
          case MODE_ATTRIBUTE_SCALAR:
          case MODE_COMMAND:
            if (pth.length == 5) {
              name = pth[1] + "/" + pth[2] + "/" + pth[3] + "/" + pth[4];
              completePath.add(name);
            }
            break;
        }

      }
    }

    String[] ret = new String[completePath.size()];
    for(int i=0;i<completePath.size();i++)
      ret[i] = (String)completePath.get(i);
    return ret;

  }

  private void createTree() {

    treeModel = new DefaultTreeModel(new RootNode(mode));
    tree = new JTree(treeModel);
    tree.setEditable(false);
    tree.setCellRenderer(new TreeNodeRenderer());
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    tree.setBorder(BorderFactory.createLoweredBevelBorder());
    treeView = new JScrollPane(tree);

  }

  /** test function */
  public static void main(String[] args) {

    final DeviceFinder df = new DeviceFinder(MODE_ATTRIBUTE_SCALAR);

    JFrame f = new JFrame();
    JPanel p = new JPanel();
    p.setLayout(new BorderLayout());
    JButton selButton = new JButton("Select");
    selButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        String[] names = df.getSelectedNames();
        System.out.println("-------------------------");
        for(int i=0;i<names.length;i++)
          System.out.println(names[i]);
      }
    });
    p.add(selButton,BorderLayout.SOUTH);
    p.add(df,BorderLayout.CENTER);
    f.setContentPane(p);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ATKGraphicsUtils.centerFrameOnScreen(f);
    f.setVisible(true);

  }

}

// ---------------------------------------------------------------

abstract class Node extends DefaultMutableTreeNode {

  private boolean areChildrenDefined = false;
  int mode;

  public int getChildCount() {
    try {
      if(!areChildrenDefined) {
        areChildrenDefined = true;
        populateNode();
      }
    } catch (DevFailed e) {
      TreeNode[] pth = getPath();
      String nodeName = "";
      for(int i=1;i<pth.length;i++) {
        if(i<pth.length-1)
          nodeName += (pth[i].toString()+"/");
        else
          nodeName += pth[i].toString();
      }
      ErrorPane.showErrorMessage(null,nodeName,e);
    }
    return super.getChildCount();
  }

  // Fill children
  abstract void populateNode() throws DevFailed;

  public boolean isLeaf() {
    return false;
  }

}

// ---------------------------------------------------------------

class RootNode extends Node {

  RootNode(int mode) {
    this.mode = mode;
  }

  void populateNode() throws DevFailed {
    String[] list = DeviceFinder.db.get_device_domain("*");
    for(int i=0;i<list.length;i++)
      add(new DomainNode(mode,list[i]));
  }

  public String toString() {
    return "RootNode";
  }

}

// ---------------------------------------------------------------

class DomainNode extends Node {

  private String domain;

  DomainNode(int mode,String domain) {
    this.domain = domain;
    this.mode = mode;
  }

  void populateNode() throws DevFailed {
    String[] list = DeviceFinder.db.get_device_family(domain+"/*");
    for(int i=0;i<list.length;i++)
      add(new FamilyNode(mode,domain,list[i]));
  }

  public String toString() {
    return domain;
  }

}

// ---------------------------------------------------------------

class FamilyNode extends Node {

  private String domain;
  private String family;

  FamilyNode(int mode,String domain,String family) {
    this.mode = mode;
    this.domain = domain;
    this.family = family;
  }

  void populateNode() throws DevFailed {
    String[] list = DeviceFinder.db.get_device_member(domain+"/"+family+"/*");
    for(int i=0;i<list.length;i++)
      add(new MemberNode(mode,domain,family,list[i]));
  }

  public String toString() {
    return family;
  }

}

// ---------------------------------------------------------------

class MemberNode extends Node {

  private String domain;
  private String family;
  private String member;

  MemberNode(int mode,String domain,String family,String member) {
    this.mode = mode;
    this.domain = domain;
    this.family = family;
    this.member = member;
  }

  void populateNode() throws DevFailed {

    if(!isLeaf()) {
      String devName = domain + "/" + family + "/" + member;
      try {
        Device ds = DeviceFactory.getInstance().getDevice(devName);
        switch(mode) {
          case DeviceFinder.MODE_ATTRIBUTE:
            String[] attList = ds.get_attribute_list();
            for(int i=0;i<attList.length;i++)
              add(new EntityNode(mode,attList[i]));
            break;
          case DeviceFinder.MODE_ATTRIBUTE_SCALAR:
            AttributeInfo[] ai = ds.get_attribute_config();
            for(int i=0;i<ai.length;i++) {
              if(ai[i].data_format.value() == AttrDataFormat._SCALAR)
                add(new EntityNode(mode,ai[i].name));
            }
            break;
          case DeviceFinder.MODE_COMMAND:
            CommandInfo[] cmdList = ds.command_list_query();
            for(int i=0;i<cmdList.length;i++)
              add(new EntityNode(mode,cmdList[i].cmd_name));
            break;
        }
      } catch (ConnectionException e) {
        ErrorPane.showErrorMessage(null,devName,e);
      }
    }

  }

  public boolean isLeaf() {
    return (mode == DeviceFinder.MODE_DEVICE);
  }

  public String toString() {
    return member;
  }

}

// ---------------------------------------------------------------

class EntityNode extends Node {

  private String entitytName;

  EntityNode(int mode,String entitytName) {
    this.entitytName = entitytName;
    this.mode = mode;
  }

  void populateNode() throws DevFailed {}

  public boolean isLeaf() {
    return true;
  }

  public String toString() {
    return entitytName;
  }

}

// ---------------------------------------------------------------

class TreeNodeRenderer extends DefaultTreeCellRenderer {

  ImageIcon devicon;
  ImageIcon cmdicon;
  ImageIcon atticon;

  public TreeNodeRenderer() {
    devicon = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/device.gif"));
    cmdicon = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/command.gif"));
    atticon = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/attribute.gif"));
  }

  public Component getTreeCellRendererComponent(
      JTree tree,
      Object value,
      boolean sel,
      boolean expanded,
      boolean leaf,
      int row,
      boolean hasFocus) {

    super.getTreeCellRendererComponent(
        tree, value, sel,
        expanded, leaf, row,
        hasFocus);

    // Device Icon
    if (value instanceof MemberNode) {
      setIcon(devicon);
      return this;
    }

    if (value instanceof EntityNode) {
      switch (((Node) value).mode) {
        case DeviceFinder.MODE_COMMAND:
          setIcon(cmdicon);
          break;
        case DeviceFinder.MODE_ATTRIBUTE:
        case DeviceFinder.MODE_ATTRIBUTE_SCALAR:
          setIcon(atticon);
          break;
      }
    }

    return this;
  }

}
