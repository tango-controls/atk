package fr.esrf.tangoatk.widget.util.jdraw;

import fr.esrf.Tango.DevFailed;
import fr.esrf.tangoatk.widget.util.ErrorPane;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *  Class used by the JDDeviceTree
 */
abstract class JDNode extends DefaultMutableTreeNode {

  private boolean areChildrenDefined = false;

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
