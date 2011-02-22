// File:          InvalidAttribute.java
// Created:       2002-01-17 16:56:27, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-05-22 11:32:39, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import fr.esrf.Tango.*;

public class InvalidAttribute extends AAttribute {

    public void setMinValue(double d, boolean b) {;}

    public void setMaxValue(double d, boolean b) {;}

    public void setMinAlarm(double d, boolean b) {;}

    public void setMaxAlarm(double d, boolean b) {;}

    public void refresh() {;}

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
