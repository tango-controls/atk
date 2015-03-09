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
 
// File:          ATKStringField.java
// Created:       2002-03-21 13:37:06, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-05-28 11:38:2, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.util;

import fr.esrf.tangoatk.core.IString;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;

/**
 * <code>ATKStringField</code> implements a stringfield which responds to
 * <enter> keypresses. 
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version 1.0
 */
public class ATKStringField extends ATKField {
    protected IString model;


    /**
     * <code>setModel</code> sets the model for this field. 
     *
     * @param m an <code>IString</code> value
     * @see fr.esrf.tangoatk.core.IString
     */
    public void setModel(IString m) {
	model = m;
	receivedEvent = false;
	modelEditable = model.isWritable();
	super.setEditable(editable && modelEditable);
	String string = model.getString();
	if (string == null ) {
	    return;
	} 
	
	setValue(string);
    }

    /**
     * <code>setValue</code> sets the value of this stringfield
     *
     * @param d a <code>String</code> value
     */
    public void setValue(String d) {
        if (isEditable() && receivedEvent) return;
        receivedEvent = true;
	setText(d);
    }

    /**
     * <code>getValue</code> returns the string value of this field
     *
     * @return a <code>String</code> value
     */
    public String getValue() {
	return getText();
    }

    protected void enter(ActionEvent e) {
	inserting = false;
	model.setString(getValue());
    }

}
