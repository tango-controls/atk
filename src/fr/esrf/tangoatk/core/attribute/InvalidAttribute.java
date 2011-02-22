// File:          InvalidAttribute.java
// Created:       2002-01-17 16:56:27, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:30:33, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;
import fr.esrf.Tango.*;
import fr.esrf.TangoApi.events.*;
import fr.esrf.TangoApi.DeviceAttribute;

public class InvalidAttribute extends AAttribute {

    public void setMinValue(double d, boolean b) {;}

    public void setMaxValue(double d, boolean b) {;}

    public void setMinAlarm(double d, boolean b) {;}

    public void setMaxAlarm(double d, boolean b) {;}

    public void refresh() {;}

    public void dispatch(DeviceAttribute attVal) {}

    public void dispatchError(DevFailed e) {}

    // Implement the method of ITangoPeriodicListener
    public void periodic (TangoPeriodicEvent evt) {;}
    // Implement the method of  ITangoChangeListener
    public void change (TangoChangeEvent evt) {;}

    protected void init(fr.esrf.tangoatk.core.Device d, String name,
	AttributeConfig config) {
	this.name = "Invalid(" + name + ")";
    }

    public void insert(String[][] s) { ; }

    public String[][] extract() { return null; }
    
    public String getVersion() {
	return "$Id$";
    }

    public String getName() {
	return "Invalid " + name;
    }
}
