/*
 * TrendRenderer.java
 *
 * Created on May 13, 2002, 4:28 PM
 */
 
package fr.esrf.tangoatk.widget.attribute;
 
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

class BooleanTrendRenderer extends DefaultTreeCellRenderer {

    ImageIcon noneIcon;
    ImageIcon xIcon;
    ImageIcon y1Icon;
    ImageIcon y2Icon;

    public BooleanTrendRenderer() {
      noneIcon = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/select_none.gif"));
      xIcon = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/select_x.gif"));
      y1Icon = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/select_y1.gif"));
      y2Icon = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/attribute/select_y2.gif"));
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
        
	    if (leaf ) {
          BooleanTrendSelectionNode n = (BooleanTrendSelectionNode)value;
          switch( n.getSelected() ) {
	        case Trend.SEL_NONE:
	          setIcon(noneIcon);
	        break;
	        case Trend.SEL_X:
	          setIcon(xIcon);
	        break;
	        case Trend.SEL_Y1:
	          setIcon(y1Icon);
	        break;
	        case Trend.SEL_Y2:
	          setIcon(y2Icon);
	        break;
	      }
	    }
	
        return this;
    }
}
