// File:          PropertyListAdapter.java
// Created:       2002-04-26 14:29:53, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-09 10:41:6, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.properties;
import java.beans.*;
import javax.swing.*;
import javax.swing.text.Caret;
import java.util.*;
import java.awt.GridBagConstraints;
import fr.esrf.tangoatk.core.*;

class PropertyListAdapter implements PropertyChangeListener { 
    JPanel valuePanel, namePanel;
    
    Map properties, valueMap, fieldMap;
    List keys;

    protected java.awt.GridBagConstraints constraints =
	new java.awt.GridBagConstraints();

    protected void gbAdd(JPanel parent, JComponent c, int x, int y) {
	constraints.anchor = GridBagConstraints.NORTHWEST;
	constraints.gridx = x; constraints.gridy = y;
	constraints.fill = GridBagConstraints.HORIZONTAL;
	constraints.weightx = 0.1;
	parent.add(c, constraints);
	constraints.gridy = y + 1;
	parent.add(new javax.swing.JSeparator(), constraints);
	    
    }
    

    public void setModel(Map model, JPanel valuePanel, JPanel namePanel) {
	this.valuePanel = valuePanel;
	this.namePanel = namePanel;
	valuePanel.removeAll();
	namePanel.removeAll();
	valueMap = new HashMap();
	fieldMap = new HashMap();

	properties = model;

	keys = new Vector();
	keys.addAll(model.keySet());
	
	Iterator it = keys.iterator();
	int i = 0;

	while (it.hasNext()) {
	    String s = (String)it.next();
	    JLabel name = new JLabel(s);
	    name.setFont(namePanel.getFont());
	    Property property = (Property)model.get(s);
	    property.addPresentationListener(this);

	    JTextField value = new JTextField(property.getPresentation());
	    valueMap.put(value, property);
	    fieldMap.put(property, value);
	    value.setBorder(BorderFactory.createEmptyBorder());
	    value.setBackground(valuePanel.getBackground());
	    value.setDisabledTextColor(valuePanel.getForeground());

	    if (property.isEditable() && isEditable()) {

		value.setEnabled(false);
		value.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
			    JTextField source = (JTextField)evt.getSource();

			    source.setEnabled(true);
			    Caret c = source.getCaret();
			    c.setDot(source.getText().length());
			    source.setCaret(c);
			    source.setBackground(java.awt.Color.white);
			}
		    });

		value.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
			    inputKeyPressed(evt);
			}
		    });

	    } else {
		value.setEditable(false);
	    }
	    
	    gbAdd(namePanel, name, 0, i);
	    gbAdd(valuePanel, value, 0, i);
	    i += 2;
	} // end of for ()
    }

    void store() {
	Iterator it = fieldMap.keySet().iterator();
	while (it.hasNext()) {
	    Property p = (Property)it.next();

	    if (!p.isEditable()) continue;

	    JTextField field = (JTextField)fieldMap.get(p);
	    done(field);

	    p.setValue(field.getText());
	    p.store();	
	} // end of while ()

    }

    void cancel() {
	Iterator it = fieldMap.keySet().iterator();
	while (it.hasNext()) {
	    Property p = (Property)it.next();
	    if (!p.isEditable()) continue;
	    JTextField field = (JTextField)fieldMap.get(p);
	    field.setText(p.getPresentation());
	    done(field);
	} // end of while ()
    }

	
    public void inputKeyPressed(java.awt.event.KeyEvent evt) {

	JTextField src = ((JTextField)evt.getComponent());
	Property model;

	
	if (evt.getKeyCode() == evt.VK_ESCAPE) {
	    model = (Property)valueMap.get(src);
	    src.setText(model.getPresentation());
	    done(src);
	    return;
	} // end of if ()
    }

    boolean editable;
    
    /**
     * Get the value of editable.
     * @return value of editable.
     */
    public boolean isEditable() {
	return editable;
    }
    
    /**
     * Set the value of editable.
     * @param v  Value to assign to editable.
     */
    public void setEditable(boolean  v) {
	this.editable = v;
    }
    
    public void done(JTextField textField) {
	Caret c = textField.getCaret();
	c.setVisible(false);
	textField.setCaret(c);
	textField.setEnabled(false);
	textField.setBackground(valuePanel.getBackground());
		    
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
	Property src = (Property)evt.getSource();
	JTextField field = (JTextField)fieldMap.get(src);
	if (field == null) return;
	
	field.setText(evt.getNewValue().toString());
    }
}
