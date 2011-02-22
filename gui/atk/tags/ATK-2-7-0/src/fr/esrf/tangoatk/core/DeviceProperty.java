/*	Synchrotron Soleil 
 *  
 *   File          :  DeviceProperty.java
 *  
 *   Project       :  ATKCoreSoleil
 *  
 *   Description   :  
 *  
 *   Author        :  SOLEIL
 *  
 *   Original      :  5 sept. 2005 
 *  
 *   Revision:  					Author:  
 *   Date: 							State:  
 *  
 *   Log: DeviceProperty.java,v 
 *
 */
package fr.esrf.tangoatk.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JOptionPane;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DbDatum;

/**
 * <code>DeviceProperty</code> is a class which is responsible for holding
 * information about a given property of a given Device. DeviceProperties have
 * the following characteristics
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
 * @author SOLEIL
 * @version $Id$
 */
public class DeviceProperty {

    protected String name;
    protected String[] value;
    protected String[] oldValue;
    protected boolean editable;
    protected Device parent;
    protected boolean specified;
    protected PropertyChangeSupport propChanges;

    /**
     * Creates a new <code>Property</code> instance,
     * with empty name ("") and value ([]).
     * This property is editable but has no device associated.
     */
    public DeviceProperty() {
        name = "";
        value = new String[0];
        oldValue = new String[0];
        editable = true;
        parent = null;
        propChanges = new PropertyChangeSupport(this);
        specified = true;
    }

    /**
     * Creates a new <code>Property</code> instance.
     * This property is editable.
     * 
     * @param theParent
     *            the <code>IDevice</code> this property belongs to
     * @param name
     *            the name of this property
     * @param value
     *            the value of this property, specified as a String[]
     */
    public DeviceProperty(Device theParent, String name, String[] value) {
        propChanges = new PropertyChangeSupport(this);
        parent = theParent;
        setName(name);
        setValue(value);
        editable = true;
        specified = true;
    }

    /**
     * Creates a new <code>Property</code> instance.
     * 
     * @param theParent
     *            the <code>IDevice</code> this property belongs to
     * @param name
     *            the name of this property
     * @param value
     *            the value of this property, specified as a String[]
     * @param isEditable
     *            true if this property is editable
     */
    public DeviceProperty(Device theParent, String name, String[] value,
            boolean isEditable) {
        propChanges = new PropertyChangeSupport(this);
        parent = theParent;
        setName(name);
        setValue(value);
        editable = isEditable;
        specified = true;
    }

    /**
     * <code>addPresentationListener</code> add a presentation-listener for
     * this property
     * 
     * @param l
     *            a <code>PropertyChangeListener</code> value
     */
    public void addPresentationListener(PropertyChangeListener l) {
        propChanges.addPropertyChangeListener("presentation", l);
    }

    /**
     * <code>removePresentationListener</code> remove a presentation-listener
     * for this property
     * 
     * @param l
     *            a <code>PropertyChangeListener</code> value
     */
    public void removePresentationListener(PropertyChangeListener l) {
        propChanges.removePropertyChangeListener("presentation", l);
    }

    /**
     * Gets the value from database.
     * If successfull, this method also results in a propertyChange event
     * for the presentation property.
     */
    public void refresh() {
        if (parent!=null) {
            try {
                DbDatum property = parent.get_property(name);
                if (property!=null){
                    oldValue = value;
                    value = property.extractStringArray();
                    propChanges.firePropertyChange("presentation", oldValue, value);
                }
                else throw new DevFailed();
            }
            catch (DevFailed e) {
                // TODO : get reason
                JOptionPane.showMessageDialog(
                        null,
                        "Failed to update property " + name,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }//end if (parent!=null)
    }// end refresh()

    /**
     * <code>isEditable</code> true if this property is editable, else false
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * <code>setSpecified</code> lets you set the specified property of this
     * property. Setting it to <code>true</code> means that the value of this
     * property is meaningfull, <code>false</code> means it's garbage
     * 
     * @param b
     *            a <code>boolean</code> value
     */
    public void setSpecified(boolean b) {
        specified = b;
    }

    /**
     * <code>isSpecified</code> returns true if this property's value is
     * specified, false if not.
     * 
     * @return a <code>boolean</code> value
     */
    public boolean isSpecified() {
        return specified;
    }

    /**
     * <code>setName</code> sets the name of the property
     * 
     * @param s
     *            a <code>String</code> value
     */
    public void setName(String s) {
        name = s;
    }

    /**
     * <code>setValue</code> sets the value of the property
     * 
     * This will not affect the database. To apply the modification in database
     * (and in the device), you have to use the <code>store()</code> method
     * after.
     * 
     * @param o
     *            a <code>String[]</code> containing the value
     */
    public void setValue(String[] o) {
        setSpecified(true);
        oldValue = value;
        value = o;
    }

    /**
     * <code>setValue</code> sets the value of the property, transforming the
     * String into a String Array, using carriage return as separator
     * 
     * This will not affect the database. To apply the modification in database
     * (and in the device), you have to use the <code>store()</code> method
     * after.
     * 
     * @param o
     *            a <code>String[]</code> containing the value
     */
    public void setValue(String s) {
        String tmp = "";
        if (s != null) {
            tmp = s;
        }
        while (tmp.endsWith("\n")) {
            tmp = tmp.substring(0, tmp.length() - 1);
        }
        setValue(tmp.split("\n"));
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
     * @return a <code>String[]</code> value
     */
    public String[] getValue() {
        return value;
    }

    /**
     * <code>getStringValue</code> returns the value of the property as a
     * String. The separator used is "\n" (new line).
     * 
     * @return a <code>String</code> value
     */
    public String getStringValue() {
        String tmp = "";
        for (int i=0; i<value.length; i++){
            tmp += value[i] + "\n";
        }
        if (!"".equals(tmp)){
            tmp = tmp.substring(0,tmp.lastIndexOf("\n"));
        }
        return tmp;
    }

    public String toString() {
        return getName();
    }

    /**
     * <code>store</code> asks the <code>Device</code> of this property to
     * store the property. This method also results in a propertyChange event
     * for the presentation property.
     */
    public void store() {
        propChanges.firePropertyChange("presentation", oldValue, value);
        parent.storeProperty(name);
    }

    public String getVersion() {
        return "$Id$";
    }

}