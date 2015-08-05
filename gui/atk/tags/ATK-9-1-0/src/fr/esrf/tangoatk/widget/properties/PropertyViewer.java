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
 

package fr.esrf.tangoatk.widget.properties;
import fr.esrf.tangoatk.core.*;
import java.awt.*;
import javax.swing.border.*;
import javax.swing.*;
import java.beans.*;
import fr.esrf.tangoatk.widget.util.*;
/**
 * @author  root
 */
public class PropertyViewer extends javax.swing.JPanel implements PropertyChangeListener {

    protected fr.esrf.tangoatk.core.Property model;
    protected java.awt.Color inputForeground, inputBackground;
    int inputLength = 0;
    int typeLength = 0;
    boolean borderVisible = false;
    Border border = null;
    
    public void propertyChange(PropertyChangeEvent evt) {
	if ("presentation".equals(evt.getPropertyName()) &&
	    evt.getSource() instanceof Property) {
	    setValue((String)evt.getNewValue());
	} 
    }
	
    /** Creates new form AProperty */
    public PropertyViewer() {
        initComponents();
	border =  type.getBorder();
	input.setBackground(getBackground());
	type.setBackground(getBackground());
	input.setForeground(getForeground());
	type.setForeground(getForeground());
    }

    public void setValueMaximumLength(int characters) {
	inputLength = characters;
	input.setPreferredSize
	    (UIManagerHelper.getRequiredSize(input.getFontMetrics(input.getFont()), characters));
    }
    
    public int getValueMaximumLength() {
	return inputLength;
    }

    public void setTypeGMaximumLength(int characters) {
	typeLength = characters;
	type.setPreferredSize
	    (UIManagerHelper.getRequiredSize(type.getFontMetrics(type.getFont()), characters));
    }
    
    public int getTypeMaximumLength() {
	return typeLength;
    }

    public void setBorderVisible(boolean isVisible) {
	borderVisible = isVisible;
	if (borderVisible) {
	    type.setBorder(border);
	    input.setBorder(border);
	    return;
	}
	
	type.setBorder(BorderFactory.createEmptyBorder());
	input.setBorder(BorderFactory.createEmptyBorder());
    }

    public void setBorder(javax.swing.border.Border border) {
	if (type == null || input == null) return;
	type.setBorder(border);
	input.setBorder(border);
    }

    public javax.swing.border.Border getBorder() {
	return super.getBorder();
    }
    
	
    public boolean isBorderVisible() {
	return borderVisible;
    }
    
    public void setFont(Font f) {
	super.setFont(f);
	
	if (type == null || input == null) return;
	
	type.setFont(f);
	input.setFont(f);
	if (inputLength > 0) {
	    input.setPreferredSize
		(UIManagerHelper.getRequiredSize
		 (input.getFontMetrics(input.getFont()),
		  inputLength));
	    
	}

	if (typeLength > 0) {
	    type.setPreferredSize
		(UIManagerHelper.getRequiredSize
		 (type.getFontMetrics
		  (type.getFont()), typeLength));
	    
	}
	
    }

    protected void labelType(String s) {
	type.setText(s);
    }

    public void setValue(String s) {
        value = s;
	input.setText(value);
    }

    public String getValue() {
        return value;
    }
    
    public void setLabelVisible(boolean b) {
	type.setVisible(b);
    }

    public boolean isLabelVisible() {
	return type.isVisible();
    }

    public double getLabelWidth() {
	return type.getPreferredSize().getWidth();
    }

    public void setLabelWidth(double width) {
	java.awt.Dimension d = type.getPreferredSize();
	d.setSize(width, d.getHeight());
	type.setPreferredSize(d);
	type.setMinimumSize(d);
    }

    public double getValueWidth() {
	return input.getPreferredSize().getWidth();
    }
    
    public void setValueWidth(double i) {
	java.awt.Dimension d =
	    new java.awt.Dimension((int) i,
				    (int)type.getPreferredSize().getHeight());
       
	input.setPreferredSize(d);
	input.setMinimumSize(d);
	
    }

    public void setValueHorizontalAlignment(int alignment) {
	input.setHorizontalAlignment(alignment);
    }

    public int getValueHorizontalAlignment() {
	return input.getHorizontalAlignment();
    }
    
    public void setModel(fr.esrf.tangoatk.core.Property p) {
	if (model != null) {
	    model.removePresentationListener(this);
	}
	
	model = p;
	model.addPresentationListener(this);
	type.setText(model.getName());
	setValue("***");

	if (model.isSpecified()) {
	    setValue(model.getPresentation());
	} 
	
	input.setVisible(true);
    }

    public fr.esrf.tangoatk.core.Property getModel() {
	return model;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        type = new javax.swing.JLabel();
        input = new javax.swing.JTextField();
        
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        type.setText("type");
        type.setBorder(new javax.swing.border.EtchedBorder());
        type.setMinimumSize(new java.awt.Dimension(40, 20));
        type.setMaximumSize(new java.awt.Dimension(100, 20));
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 0.1;
        add(type, gridBagConstraints1);
        
        input.setEditable(false);
        input.setForeground(new java.awt.Color(102, 102, 153));
        input.setFont(new java.awt.Font("Dialog", 1, 12));
        input.setText("jTextField1");
        input.setBorder(new javax.swing.border.EtchedBorder());
        input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputKeyPressed(evt);
            }
        });
        
        input.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inputMouseClicked(evt);
            }
        });
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 0.1;
        add(input, gridBagConstraints1);
        
    }//GEN-END:initComponents

    private void inputMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inputMouseClicked
        // Add your handling code here:
        if (isEditable()) setInputEnabled(true);
    }//GEN-LAST:event_inputMouseClicked

    private void inputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputKeyPressed
        if (evt.getKeyCode() == evt.VK_ENTER) {
            setInputEnabled(false);
            value = input.getText();
	    model.setValue(value);
	    model.store();
	    return;
        }

	if (evt.getKeyCode() == evt.VK_ESCAPE) {
            setInputEnabled(false);
	    input.setText(value);
	    return;
	} // end of if ()
	
            // Add your handling code here:
    }//GEN-LAST:event_inputKeyPressed

    private void setInputEnabled(boolean b) {
        if (!b) {
            input.setEditable(false);
            input.setFont(getFont());
            input.setForeground(inputForeground);
	    input.setBackground(inputBackground);
	    input.setBorder(border);
            return;
        }
        if (!isEditable()) return;
        
        input.setEditable(true);
        input.setFont(getFont().deriveFont(Font.PLAIN));
	inputForeground = input.getForeground();
	inputBackground = input.getBackground();
        input.setForeground(java.awt.Color.black);
	input.setBackground(java.awt.Color.white);
	input.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public void setForeground(java.awt.Color color) {

	if (input == null || type == null) return;

	input.setForeground(color);
	type.setForeground(color);
    }

    public void setBackground(java.awt.Color color) {
	super.setBackground(color);
	if (input == null || type == null) return;

	input.setBackground(color);
	type.setBackground(color);
    }

    public void setEditable(boolean b) {
        editable = b;
        
    }

    public void setOpaque(boolean isOpaque) {
	super.setOpaque(isOpaque);
	if (input == null || type == null) return;

	input.setOpaque(isOpaque);
	type.setOpaque(isOpaque);
    }

    public boolean isOpaque() {
	return super.isOpaque();
    }

    public boolean isEditable() {
	if (model != null && model.isEditable()) return editable;	     
        return false;
    }
    
    private boolean editable = false;
    private String value;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel type;
    private javax.swing.JTextField input;
    // End of variables declaration//GEN-END:variables

}
