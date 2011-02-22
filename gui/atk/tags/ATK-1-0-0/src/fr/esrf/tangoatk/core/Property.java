// File:          AttributeProperty.java
// Created:       2001-11-22 10:25:11, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-10 12:57:49, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.beans.*;

/**
 * <code>Property</code> is a class which responsible for holding
 * information about a given Property of a given IEntity. Properties have
 * the following, uh, properties
 * <ul>
 * <li>editable wether or not this property is editable
 * <li>specified wether or not this property is specified
 * <li>value the value of this property
 * <li>name  the name of this property
 * </ul>
 * Each time a property is <code>store</code>d, a presentation
 * propertychange event is fired, so that the listeners of this property
 * can update its values.
 * 
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Id$
 */
public class Property  {
    protected String name;
    protected Object value;
    protected Object oldValue;
    protected boolean editable;
    protected IEntity parent;
    protected boolean specified = true;
    protected PropertyChangeSupport propChanges;
    


    public Property() {
	;
    }
    
    /**
     * Creates a new <code>Property</code> instance.
     *
     * @param parent the <code>IEntity</code> this property belongs to
     * @param name the name of this property
     * @param value the value of this property, specified a an object
     * @param editable true if this property is editable
     */
    public Property(IEntity parent, String name,
			     Object value, boolean editable) {
	propChanges = new PropertyChangeSupport(this);
	this.parent = (IEntity)parent;
	setName(name);
	setValue(value);
	this.editable = editable;
    }


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

    public void refresh() {
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
	if (value != null) return value.toString();
	return "";
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


    /**
     * <code>store</code> asks the <code>IEntity</code> of this
     * property to store its properies. This method also results in a
     * propertyChange event for the presentation property.
     */
    public void store() {
	propChanges.firePropertyChange("presentation", oldValue, value);
	((IEntity)parent).storeConfig();
    }

    public String getVersion() {
	return "$Id$";
    }
    
}
