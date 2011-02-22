// File:          AttributeDisplayLevelProperty.java
// Created:       2002-05-17 10:47:51, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-10 13:53:42, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import fr.esrf.Tango.*;

public class DisplayLevelProperty extends Property {
    public final static String OPERATOR = "OPERATOR";
    public final static String EXPERT   = "EXPERT";

    public DisplayLevelProperty(IEntity parent, String name,
					 DispLevel value, boolean editable) {
	super(parent, name, value, editable);
    }
    public String getPresentation() {
	switch (((DispLevel)value).value()) {
	case DispLevel._OPERATOR: return  OPERATOR;

	case DispLevel._EXPERT: return EXPERT;
	} // end of switch ()
	return null;
    }

    public boolean isOperator() {
	return getIntValue() == DispLevel._OPERATOR;
    }

    public boolean isExptert() {
	return getIntValue() == DispLevel._EXPERT;
    }
	
    public int getIntValue() {
	return ((DispLevel)value).value();
    }

    public String getVersion() {
	return "$Id$";
    }

}
