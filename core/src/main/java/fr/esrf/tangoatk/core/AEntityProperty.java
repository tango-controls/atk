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
 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.io.*;
import java.beans.*;

public abstract class AEntityProperty implements Serializable {
    protected String name;
    protected Object value;
    protected Object oldValue;
    protected boolean editable;
    protected IEntity parent;
    protected boolean specified = true;
    protected PropertyChangeSupport propChanges;
    
    /**
     * <code>addPresentationListener</code> add a presentation-listener
     * for this property
     * @param l a <code>PropertyChangeListener</code> value
     */
    public void addPresentationListener (PropertyChangeListener l) {
	propChanges.addPropertyChangeListener("presentation", l);
    }

    /**
     * <code>removePresentationListener</code> remove a presentation-listener
     * for this property
     * @param l a <code>PropertyChangeListener</code> value
     */
    public void removePresentationListener(PropertyChangeListener l) {
	propChanges.removePropertyChangeListener("presentation", l);
    }

    void refresh() {
	propChanges.firePropertyChange("presentation", oldValue, value);
	oldValue = value;
    }
    
    /**
     * <code>isEditable</code> true if this property is editable, else false 
     */
    public boolean isEditable() {
	return editable;
    }

    
    /**
     * <code>setSpecified</code> lets you set the specified property of
     * this property. Setting it to <code>true</code> means that the
     * value of this property is meaningfull, <code>false</code> mean it's
     * garbage
     * @param b a <code>boolean</code> value
     */
    public void setSpecified(boolean b) {
	specified = b;
    }

    /**
     * <code>isSpecified</code> returns true if this property's value is
     * specified, false if not.
     * @return a <code>boolean</code> value
     */
    public boolean isSpecified() {
	return specified;
    }

    /**
     * <code>setName</code> sets the name of the property
     *
     * @param s a <code>String</code> value
     */
    public void setName(String s) {
	name = s;
    }

    /**
     * <code>setValue</code> sets the value of the property
     *
     * @param o an <code>Object</code> containing the value
     */
    public void setValue(Object o) {
	setSpecified(true);
	oldValue = value;
	value = o;
    }


        /**
     * <code>getName</code> returns the name of the property
     *
     * @return a <code>String</code> value
     */
    public String getName() {
	return name;
    }

    /**
     * <code>getValue</code> return the value of the property
     *
     * @return an <code>Object</code> value
     */
    public Object getValue() {
	return value;
    }

    /**
     * <code>getStringValue</code> returns the value of the property as a 
     * string.
     * @return a <code>String</code> value
     */
    public String getStringValue() {
	String tmp = "";
	if (value != null) tmp = value.toString();
	
	return tmp;
    }

    /**
     * <code>getIntValue</code> returns the value of the property as an
     * int. 
     * @return an <code>int</code> value
     * @throws NumberFormatException if the value of the property is not an
     * int.
     */
    public int getIntValue() {
	return ((Integer)value).intValue();
    }

    /**
     * <code>getPresentation</code> returns a nicely formated 
     * <code>String</code> representation of this property. To be used by
     * the widgets that show the value of this property.
     * @return a <code>String</code> value
     */
    public String getPresentation() {
	String tmp = "";
	if (value != null) tmp = value.toString();

	return tmp;
    }

    /**
     * <code>toString</code> does exactly what you'd think it does.
     *
     * @return a <code>String</code> value
     */
    public String toString() {
	return getName();
    }

    public abstract void store() ;



    public String getVersion() {
	return "$Id$";
    }

}
