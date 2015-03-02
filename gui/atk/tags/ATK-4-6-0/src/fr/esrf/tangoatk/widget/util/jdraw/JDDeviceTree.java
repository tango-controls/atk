package fr.esrf.tangoatk.widget.util.jdraw;

import fr.esrf.TangoApi.ApiUtil;
import fr.esrf.TangoApi.Database;
import fr.esrf.TangoApi.AttributeInfo;
import fr.esrf.TangoApi.CommandInfo;
import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.AttrDataFormat;
import fr.esrf.tangoatk.widget.util.ErrorPane;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import fr.esrf.tangoatk.core.Device;
import fr.esrf.tangoatk.core.DeviceFactory;
import fr.esrf.tangoatk.core.ConnectionException;
import fr.esrf.TangoDs.TangoConst;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;
import java.io.ByteArrayInputStream;

/**
 * Device tree for jdraw
 */
public class JDDeviceTree extends JPanel implements DragGestureListener,DragSourceListener {

  /** Select all attributes */
  public final static int NODE_ATTRIBUTE = 1;
  /** Select command */
  public final static int NODE_COMMAND   = 2;

  static Database  db;
  JTree            tree;
  JScrollPane      treeView;
  DefaultTreeModel treeModel;

  public JDDeviceTree() {

    try {
      db = ApiUtil.get_db_obj();
    } catch (DevFailed e) {
      ErrorPane.showErrorMessage(null, "Database", e);
      return;
    }
    setLayout(new BorderLayout());
    createTree();
    add(treeView, BorderLayout.CENTER);

  }

  private void createTree() {

    treeModel = new DefaultTreeModel(new RootNode());
    tree = new JTree(treeModel);
    tree.setEditable(false);
    tree.setCellRenderer(new TreeNodeRenderer());
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    tree.setBorder(BorderFactory.createLoweredBevelBorder());
    treeView = new JScrollPane(tree);

    DragSource dragSource = DragSource.getDefaultDragSource();
    dragSource.createDefaultDragGestureRecognizer(tree,
            DnDConstants.ACTION_MOVE,
            this);

  }

  public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {

    // Can only drag leafs
    JTree tree = (JTree) dragGestureEvent.getComponent();
    TreePath path = tree.getSelectionPath();
    if (path == null) {
      // Nothing selected, nothing to drag
    } else {
      JDNode selectedNode = (JDNode)path.getLastPathComponent();
      DefaultMutableTreeNode selection = (DefaultMutableTreeNode)path.getLastPathComponent();
      if (selectedNode.isLeaf()) {
        dragGestureEvent.startDrag(DragSource.DefaultMoveDrop,
           (JDEntityNode)selectedNode, this);
      }
    }

  }

  public void dragDropEnd(DragSourceDropEvent dragSourceDropEvent) {
  }

  public void dragEnter(DragSourceDragEvent dragSourceDragEvent) {
    DragSourceContext context = dragSourceDragEvent.getDragSourceContext();
    context.setCursor(DragSource.DefaultMoveDrop);
  }

  public void dragExit(DragSourceEvent dragSourceEvent) {
  }

  public void dragOver(DragSourceDragEvent dragSourceDragEvent) {
  }

  public void dropActionChanged(DragSourceDragEvent dragSourceDragEvent) {
  }

   public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    d.width = 200;
    return d;
  }

  public static void main(String[] args) {

    final JDDeviceTree df = new JDDeviceTree();

    JFrame f = new JFrame();
    JPanel p = new JPanel();
    TestPanel tp = new TestPanel();
    p.setLayout(new BorderLayout());
    p.add(df,BorderLayout.CENTER);
    p.add(tp,BorderLayout.EAST);
    f.setContentPane(p);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ATKGraphicsUtils.centerFrameOnScreen(f);
    f.setVisible(true);

  }

}
// --------------------------------------------------------------------------------
// Test panel
// --------------------------------------------------------------------------------

class TestPanel extends JPanel implements DropTargetListener {

  DropTarget dropTarget;

  TestPanel() {
    setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    dropTarget = new DropTarget(this,this);
  }

  public Dimension getPreferredSize() {
    return new Dimension(400,300);
  }

  public void dragEnter(DropTargetDragEvent dtde) {
  }

  public void dragOver(DropTargetDragEvent dtde) {
  }

  public void dropActionChanged(DropTargetDragEvent dtde) {
  }

  public void dragExit(DropTargetEvent dte) {
  }

  public void drop(DropTargetDropEvent dtde) {
    Transferable trans = dtde.getTransferable();
    if (trans.isDataFlavorSupported(JDEntityNode.JDENTITY_NODE_FLAVOR)) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        try {
          JDEntityNode jde = (JDEntityNode)trans.getTransferData(JDEntityNode.JDENTITY_NODE_FLAVOR);
          Point location = dtde.getLocation();
          System.out.println(jde.getName() + " type:" + jde.getType() + " at " + location.x + "," + location.y);
        } catch(IOException e1) {
          JOptionPane.showMessageDialog(this,"Drag operation not allowed");
        } catch (UnsupportedFlavorException e2) {
          JOptionPane.showMessageDialog(this,"Drag operation not allowed");
        }
        dtde.dropComplete(true);
    } else {
      JOptionPane.showMessageDialog(this,"Drag operation not allowed");
    }
  }

}


// ---------------------------------------------------------------

class RootNode extends JDNode {

  RootNode() {
  }

  void populateNode() throws DevFailed {
    String[] list = JDDeviceTree.db.get_device_domain("*");
    for(int i=0;i<list.length;i++)
      add(new DomainNode(list[i]));
  }

  public String toString() {
    return "RootNode";
  }

}

// ---------------------------------------------------------------

class DomainNode extends JDNode {

  private String domain;

  DomainNode(String domain) {
    this.domain = domain;
  }

  void populateNode() throws DevFailed {
    String[] list = JDDeviceTree.db.get_device_family(domain+"/*");
    for(int i=0;i<list.length;i++)
      add(new FamilyNode(domain,list[i]));
  }

  public String toString() {
    return domain;
  }

}

// ---------------------------------------------------------------

class FamilyNode extends JDNode {

  private String domain;
  private String family;

  FamilyNode(String domain,String family) {
    this.domain = domain;
    this.family = family;
  }

  void populateNode() throws DevFailed {
    String[] list = JDDeviceTree.db.get_device_member(domain+"/"+family+"/*");
    for(int i=0;i<list.length;i++)
      add(new MemberNode(domain,family,list[i]));
  }

  public String toString() {
    return family;
  }

}

// ---------------------------------------------------------------

class MemberNode extends JDNode {

  private String domain;
  private String family;
  private String member;

  MemberNode(String domain,String family,String member) {
    this.domain = domain;
    this.family = family;
    this.member = member;
  }

  void populateNode() throws DevFailed {

    String devName = domain + "/" + family + "/" + member;
    try {
      Device ds = DeviceFactory.getInstance().getDevice(devName);
      // Add attribute
      AttributeInfo[] ai = ds.get_attribute_info();
      for(int i=0;i<ai.length;i++) {

        if (ai[i].data_format.value() == AttrDataFormat._SCALAR) {

          // Scalar -----------------------------------------------
          switch (ai[i].data_type) {
            case TangoConst.Tango_DEV_CHAR:
            case TangoConst.Tango_DEV_UCHAR:
            case TangoConst.Tango_DEV_SHORT:
            case TangoConst.Tango_DEV_USHORT:
            case TangoConst.Tango_DEV_LONG:
            case TangoConst.Tango_DEV_ULONG:
            case TangoConst.Tango_DEV_FLOAT:
            case TangoConst.Tango_DEV_DOUBLE:
              add(new JDEntityNode(JDDeviceTree.NODE_ATTRIBUTE, devName, "[number scalar]", ai[i].name));
              break;
            case TangoConst.Tango_DEV_STRING:
              add(new JDEntityNode(JDDeviceTree.NODE_ATTRIBUTE, devName, "[string scalar]", ai[i].name));
              break;
            case TangoConst.Tango_DEV_BOOLEAN:
              add(new JDEntityNode(JDDeviceTree.NODE_ATTRIBUTE, devName, "[boolean scalar]", ai[i].name));
              break;
            default:
              add(new JDEntityNode(JDDeviceTree.NODE_ATTRIBUTE, devName, "[scalar]", ai[i].name));
          }

        } else if (ai[i].data_format.value() == AttrDataFormat._SPECTRUM) {

          // Spectrum -----------------------------------------------
          switch (ai[i].data_type) {
            case TangoConst.Tango_DEV_CHAR:
            case TangoConst.Tango_DEV_UCHAR:
            case TangoConst.Tango_DEV_SHORT:
            case TangoConst.Tango_DEV_USHORT:
            case TangoConst.Tango_DEV_LONG:
            case TangoConst.Tango_DEV_ULONG:
            case TangoConst.Tango_DEV_FLOAT:
            case TangoConst.Tango_DEV_DOUBLE:
              add(new JDEntityNode(JDDeviceTree.NODE_ATTRIBUTE, devName, "[number spectrum]", ai[i].name));
              break;
            case TangoConst.Tango_DEV_STRING:
              add(new JDEntityNode(JDDeviceTree.NODE_ATTRIBUTE, devName, "[string spectrum]", ai[i].name));
              break;
            case TangoConst.Tango_DEV_BOOLEAN:
              add(new JDEntityNode(JDDeviceTree.NODE_ATTRIBUTE, devName, "[boolean spectrum]", ai[i].name));
              break;
            default:
              add(new JDEntityNode(JDDeviceTree.NODE_ATTRIBUTE, devName, "[spectrum]", ai[i].name));
          }

        } else if (ai[i].data_format.value() == AttrDataFormat._IMAGE) {

          // Image -----------------------------------------------
          switch (ai[i].data_type) {
            case TangoConst.Tango_DEV_CHAR:
            case TangoConst.Tango_DEV_UCHAR:
            case TangoConst.Tango_DEV_SHORT:
            case TangoConst.Tango_DEV_USHORT:
            case TangoConst.Tango_DEV_LONG:
            case TangoConst.Tango_DEV_ULONG:
            case TangoConst.Tango_DEV_FLOAT:
            case TangoConst.Tango_DEV_DOUBLE:
              add(new JDEntityNode(JDDeviceTree.NODE_ATTRIBUTE, devName, "[number image]", ai[i].name));
              break;
            case TangoConst.Tango_DEV_STRING:
              add(new JDEntityNode(JDDeviceTree.NODE_ATTRIBUTE, devName, "[string image]", ai[i].name));
              break;
            case TangoConst.Tango_DEV_BOOLEAN:
              add(new JDEntityNode(JDDeviceTree.NODE_ATTRIBUTE, devName, "[boolean image]", ai[i].name));
              break;
            default:
              add(new JDEntityNode(JDDeviceTree.NODE_ATTRIBUTE, devName, "[image]", ai[i].name));
          }

        } else {

          // Others -----------------------------------------------
          add(new JDEntityNode(JDDeviceTree.NODE_ATTRIBUTE, devName, "[unknown]", ai[i].name));

        }

      }

      ai = null;

      // Add command
      CommandInfo[] cmdList = ds.command_list_query();
      for (int i = 0; i < cmdList.length; i++)
        add(new JDEntityNode(JDDeviceTree.NODE_COMMAND,devName,"[command]",cmdList[i].cmd_name));
    } catch (ConnectionException e) {
      ErrorPane.showErrorMessage(null, devName, e);
    }

  }

  public boolean isLeaf() {
    return false;
  }

  public String toString() {
    return member;
  }

}

class JDDeviceTreeNodeRenderer extends JComponent {

  String str1;
  String str2;
  Dimension pSize;
  int p1,p2;
  int hText;
  ImageIcon icon;
  static BufferedImage dummy = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);

  JDDeviceTreeNodeRenderer() {
    pSize = new Dimension(10,18);
  }

  public void setValues(ImageIcon icon,String s1,String s2) {
    str1 = s1;
    str2 = s2;
    this.icon = icon;
    Graphics2D g2 = (Graphics2D)dummy.getGraphics();
    FontRenderContext frc = g2.getFontRenderContext();
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                        RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    Rectangle2D b1 = JDUtils.labelFontBold.getStringBounds(s1,frc);
    Rectangle2D b2 = JDUtils.labelFont.getStringBounds(s2,frc);
    p1 = icon.getIconWidth() + 3;
    p2 = icon.getIconWidth() + (int)(b1.getWidth()+0.5) + 6;
    if(s2.length()>0) {
      pSize.width = p2 + (int)(b2.getWidth()+0.5) + 2;
    } else {
      pSize.width = p2;
    }
    pSize.height = icon.getIconHeight();
    hText = (int)(b1.getHeight()*0.333 + icon.getIconHeight()*0.5);
  }

  public Dimension getPreferredSize() {
    return pSize;
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  public void paint(Graphics g) {
    Dimension sz = getSize();
    g.setColor(getBackground());
    g.fillRect(0,0,sz.width,sz.height);
    g.drawImage(icon.getImage(),0,0,null);

    // Center texts
    g.setColor(Color.black);
    g.setFont(JDUtils.labelFontBold);
    g.drawString(str1,p1,hText);
    g.setFont(JDUtils.labelFont);
    g.drawString(str2,p2,hText);
  }


}

class TreeNodeRenderer extends DefaultTreeCellRenderer {

  ImageIcon devicon;
  ImageIcon cmdicon;
  ImageIcon atticon;
  JDDeviceTreeNodeRenderer renderer;

  private static Color selColor = new Color(204,204,255);

  public TreeNodeRenderer() {
    devicon = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/device.gif"));
    cmdicon = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/command.gif"));
    atticon = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/util/attribute.gif"));
    renderer = new JDDeviceTreeNodeRenderer();
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

    renderer.setBackground((sel)?selColor:tree.getBackground());

    if (value instanceof JDEntityNode) {
      JDEntityNode n = (JDEntityNode)value;
      switch (n.getType()) {
        case JDDeviceTree.NODE_COMMAND:
          renderer.setValues(cmdicon,n.toString(),n.getInfo());
          return renderer;
        case JDDeviceTree.NODE_ATTRIBUTE:
          renderer.setValues(atticon,n.toString(),n.getInfo());
          return renderer;
      }
    }

    return this;
  }

}
