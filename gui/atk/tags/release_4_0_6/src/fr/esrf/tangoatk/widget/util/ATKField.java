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
 
// File:          ATKField.java
// Created:       2002-03-21 12:42:14, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-05-28 10:49:6, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.util;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/**
 * <code>ATKField</code> is a superclass for ATKStringField and
 * ATKNumberField. 
 *
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Version$
 */
public class ATKField extends JTextField {
    protected boolean inserting = false;
    protected boolean editable = false;
    protected boolean modelEditable = false;
    protected boolean receivedEvent = false;
    protected int     length = 0;
    protected String  format = "";
    protected String  error = "-------";
    protected ATKFormat userFormat;


    public ATKField () {
	Keymap parent = getKeymap();

	Keymap map = JTextComponent.addKeymap("entermap", parent);

	KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,

						       0, false);
	map.addActionForKeyStroke(enterStroke, new AbstractAction () {
		public void actionPerformed(ActionEvent e) {
		    enter(e);
		}
	    }
				  );
	setKeymap(map);
    }

    /**
     * <code>setUserFormat</code> lets the application programmer
     * format the field independently of what format is specified
     * by the attribute.
     * @param format an <code>ATKFormat</code> value
     * @see ATKFormat
     */
    public void setUserFormat(ATKFormat format) {
	this.userFormat = format;
    }
    
    /**
     * <code>getUserFormat</code> returns the ATKFormat for this object.
     *
     * @return an <code>ATKFormat</code> value
     */
    public ATKFormat getUserFormat() {
	return userFormat;
    }
    

    /**
     * <code>setFont</code> sets the font of this field. Also readjusts
     * the size of the field if setMaximumLength has been called.
     * @param f a <code>Font</code> value
     */
    public void setFont(Font f) {
	super.setFont(f);
	if (length > 0) {
	    Dimension dim = UIManagerHelper.getRequiredSize
	    (getFontMetrics(getFont()), length);
	    setPreferredSize(dim);
	    setMinimumSize(dim);
	} 
    }
    

    /**
     * <code>enter</code> is called when a user presses enter.
     *
     * @param e an <code>ActionEvent</code> value
     */
    protected void enter(ActionEvent e) {}
    

    /**
     * <code>setState</code> sets the background-color of this field
     * on a statechange, unless the field is editable.
     * @param color a <code>Color</code> value
     */
    public void setState(Color color) {
	if (isEditable()) return;
	setBackground(color);
    }
		      
    /**
     * <code>setEditable</code> sets the editable property of this field.
     * A field representing an attribute which is not editable, will never
     * be editable even if setEditable is set to true.
     * @param b a <code>boolean</code> value
     */
    public void setEditable(boolean b) {
        super.setEditable(b && modelEditable);
        editable = b; 
        receivedEvent = false;
    }
    
    /**
     * <code>isEditable</code> returns true if this field is setEditable
     * and the attribute it is representing is editable.
     * @return a <code>boolean</code> value
     */
    public boolean isEditable() {
        return editable && modelEditable;
    }


    /**
     * <code>setError</code> sets the field to the error-text, normally
     * this is ------ if the field is editable.
     */
    public void setError() {
	if (isEditable()) return;
	setText(error);
    }

    
    /**
     * <code>setFormat</code> sets the format of this field. 
     *
     * @param format a <code>String</code> value containing a format
     * specifier in either Tango-style or C-style.
     */
    public void setFormat(String format) {
	if ("No format".equals(format)) {
	    format = "%s";
	    
	}
	
	this.format = format;
    }

    /**
     * <code>getFormat</code> returns the format string for this field.
     *
     * @return a <code>String</code> value
     */
    public String getFormat() {
	return format;
    }
    
    /**
     * <code>setMaximumLength</code> sets the maximum number of charachters
     * this field can occupy.
     * @param characters an <code>int</code> value
     */
    public void setMaximumLength(int characters) {
	length = characters;
	setPreferredSize
	    (UIManagerHelper.getRequiredSize(getFontMetrics(getFont()),
					     length));
    }

    /**
     * <code>getMaximumLength</code>
     *
     * @return an <code>int</code> value containing the maximum number of
     * charachter this field can occupy
     */
    public int getMaximumLength() {
	return length;
    }
}
