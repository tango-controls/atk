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
