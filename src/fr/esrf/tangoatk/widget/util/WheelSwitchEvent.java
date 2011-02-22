// WheelSwitchEvent.java
// 
// Description:       
//   Event send when value change in a wheelswitch

package fr.esrf.tangoatk.widget.util;

import java.util.EventObject;

public class WheelSwitchEvent extends EventObject {

    double value;
    
    public WheelSwitchEvent(Object source, double val) {
	super(source);
	setValue(val);
    }

    public void setValue(double val) {
	this.value = val;
    }

    public double getValue() {
	return value;
    }

    public void setSource(Object source) {
	this.source = source;
    }
    
    public String getVersion() {
	return "$Id$";
    }

    public Object clone() {
	return new WheelSwitchEvent(source, value);
    }
    
}
