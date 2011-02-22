package fr.esrf.tangoatk.widget.util.jdraw;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

class JDTreeNode extends DefaultMutableTreeNode {

  private boolean areChildrenDefined = false;
  JDObject      theObject;
  String          propertyName;
  int             panelId;
  JDTreeNode      master;

  public JDTreeNode(JDObject obj) {
    // Object node
    theObject = obj;
    propertyName=null;
    master=this;
  }

  public JDTreeNode() {
    /// Root node
    theObject=null;
    propertyName=null;
    panelId=0;
    master=null;
  }

  public JDTreeNode(JDTreeNode parent,JDObject obj,String pName,int pId) {
    //Property node
    theObject = obj;
    propertyName=pName;
    areChildrenDefined = true;
    panelId=pId;
    master=parent;
  }

  public int getChildCount() {
    if (!areChildrenDefined)
      defineChildNodes();
    return (super.getChildCount());
  }

  // *****************************************************************************************************************
  // Add dinamycaly nodes in the tree when the user open a branch.
  private void defineChildNodes() {

    int i;
    areChildrenDefined = true;

    // Root node
    if( theObject==null )
      return;

    // Add value mapper
    addPropertyNode(null);

    // Add children of group
    if( theObject instanceof JDGroup ) {

      JDGroup g = (JDGroup)theObject;
      for(i=0;i<g.getChildrenNumber();i++)
        add(new JDTreeNode(g.getChildAt(i)));

    }


  }

  /** Adds value mapper to the node */
  public void addPropertyNode(DefaultTreeModel model) {

    int nbProp = 0;

    if(theObject.isInteractive()) {
      insert(new JDTreeNode(this,theObject, "User intercation enabled: V0=" + theObject.getInitValue() +
                              " ["+theObject.getMinValue()+","+ theObject.getMaxValue() + "]",1) , nbProp);
      nbProp++;
    }

    if (theObject.hasBackgroundMapper()) {
      insert(new JDTreeNode(this, theObject, "Value affects background", 1),  nbProp);
      nbProp++;
    }
    if (theObject.hasForegroundMapper()) {
      insert(new JDTreeNode(this, theObject, "Value affects foreground", 1), nbProp);
      nbProp++;
    }
    if (theObject.hasVisibilityMapper()) {
      insert(new JDTreeNode(this, theObject, "Value affects visibility", 1), nbProp);
      nbProp++;
    }
    if (theObject.hasInvertShadowMapper()) {
      insert(new JDTreeNode(this, theObject, "Value affects invert shadow", 1), nbProp);
      nbProp++;
    }
    if (theObject.hasHTranslationMapper()) {
      insert(new JDTreeNode(this, theObject, "Value affects horizontal position", 1), nbProp);
      nbProp++;
    }
    if (theObject.hasVTranslationMapper()) {
      insert(new JDTreeNode(this, theObject, "Value affects vertical position", 1), nbProp);
      nbProp++;
    }

    // Refresh model if specified
    if(model!=null) {
      int[] idx = new int[nbProp];
      for(int i=0;i<nbProp;i++) idx[i]=i;
      model.nodesWereInserted(this,idx);
    }

  }

  public JDObject getObject() {
    return theObject;
  }

  public String toString() {
     return "Selection";
  }

}
