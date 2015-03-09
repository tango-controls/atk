package fr.esrf.tangoatk.widget.util.jdraw;

import fr.esrf.Tango.DevFailed;

import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Class used for drag and drop operation on the jdraw device tree
 */

class JDEntityNode extends JDNode implements Transferable {

  private String devName;
  private String entitytName;
  private String info;
  int type;

  final public static DataFlavor JDENTITY_NODE_FLAVOR = new DataFlavor(
      DefaultMutableTreeNode.class, "JDENTITYNODE Tree Node");

  JDEntityNode(int type,String devName,String info,String entitytName) {
    this.entitytName = entitytName;
    this.type = type;
    this.devName = devName;
    this.info = info;
  }

  void populateNode() throws DevFailed {}

  public boolean isLeaf() {
    return true;
  }

  public String toString() {
    return entitytName;
  }

  public String getName() {
    return devName + "/" + entitytName;
  }

  public int getType() {
    return type;
  }

  public String getInfo() {
    return info;
  }

  public DataFlavor[] getTransferDataFlavors() {
    return null;
  }

  public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException, IOException {
    return this;
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return flavor.equals(JDENTITY_NODE_FLAVOR);
  }

}
