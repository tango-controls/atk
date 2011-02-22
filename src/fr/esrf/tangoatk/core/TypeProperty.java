// File:          TypeProperty.java
// Created:       2001-12-17 16:20:33, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-05-17 11:26:25, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import fr.esrf.TangoDs.*;
public class TypeProperty extends Property
    implements TangoConst {

    public TypeProperty(ICommand parent, String name,
			       Number value, boolean editable) {
	super(parent, name, value, editable);
    }

    public String getPresentation() {
	return Tango_CmdArgTypeName[((Number)value).intValue()];
    }

    public String getVersion() {
	return "$Id$";
    }

}

	
