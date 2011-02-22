/** A JDObject browser panel */
package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

class JDBrowserPanel extends JPanel implements TreeSelectionListener,MouseListener {

  JDObject[]  allObjects;
  JDrawEditor   invoker;
  Rectangle     oldRect;
  Color         fColor = new Color( 85,87,140 );
  JTree         theTree;
  JScrollPane   treeView;
  JDTreeNode    rootNode;
  DefaultTreeModel mainTreeModel;
  JDTreeNodeRenderer treeRenderer;


  public JDBrowserPanel(JDObject[] obj, JDrawEditor jc) {

    int i;
    allObjects = obj;
    invoker = jc;
    setLayout(new BorderLayout());

    // Create the tree

    rootNode = new JDTreeNode();

    // Add all object
    for(i=0;i<allObjects.length;i++)
      rootNode.add( new JDTreeNode(allObjects[i]) );

    // Create the tree

    mainTreeModel = new DefaultTreeModel(rootNode);
    treeRenderer = new JDTreeNodeRenderer();
    theTree = new JTree(mainTreeModel);
    theTree.setCellRenderer(treeRenderer);
    theTree.setEditable(false);
    theTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    //mainTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    theTree.setShowsRootHandles(false);
    theTree.setBorder(BorderFactory.createLoweredBevelBorder());
    treeView = new JScrollPane(theTree);
    treeView.setMinimumSize(new Dimension(200,0));
    //mainTree.addMouseListener(treeMousellistemner);
    theTree.addTreeSelectionListener(this);
    theTree.addMouseListener(this);

    add(treeView,BorderLayout.CENTER);
    setPreferredSize(new Dimension(300, 480));

  }

  public void mouseClicked(MouseEvent e) {
    if(e.getClickCount()==1 && e.getButton()==MouseEvent.BUTTON3) {
      TreePath selPath = theTree.getPathForLocation(e.getX(),e.getY());
      if( selPath!=null )
        processClick((JDTreeNode)selPath.getLastPathComponent());
    }
  }

  public void mousePressed(MouseEvent e) {}

  public void mouseReleased(MouseEvent e) {}

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {}

  // ---------------------------------------------------------
  // TreeSelection listener
  // ---------------------------------------------------------
  public void valueChanged(TreeSelectionEvent e) {
     TreePath selPath = e.getPath();
     if( selPath!=null ) {
       JDTreeNode n = (JDTreeNode)selPath.getLastPathComponent();
       // Allow selection into group
       invoker.unselectAll();
       invoker.selectObject(n.getObject());
     }
  }

  private void processClick(JDTreeNode n) {

    Vector v = new Vector();
    if(n.getObject()!=null) {
      v.add(n.getObject());
      JDUtils.showPropertyDialog(invoker,v,n.panelId);
      if(JDUtils.modified) {
        // Update node that has changed
        mainTreeModel.nodeChanged(n.master);

        // Update value mapper list
        for(int i=0;i<n.master.getChildCount();i++) {
          JDTreeNode c = (JDTreeNode)n.master.getChildAt(i);
          if(c.propertyName!=null) {
            mainTreeModel.removeNodeFromParent(c);
            i--;
          } else {
            mainTreeModel.nodeChanged(c);
          }
        }
        n.master.addPropertyNode(mainTreeModel);

      }
    }

  }

}
