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

    public boolean isExpert() {
	return getIntValue() == DispLevel._EXPERT;
    }
	
    public int getIntValue() {
	return ((DispLevel)value).value();
    }

    public String getVersion() {
	return "$Id$";
    }
 
    public void setValueFromString(String stringValue) {
        if ( OPERATOR.equalsIgnoreCase(stringValue.trim()) ) {
            setValue( DispLevel.OPERATOR );
        }
        else if ( EXPERT.equalsIgnoreCase(stringValue.trim()) ) {
            setValue( DispLevel.EXPERT );
        }
    }

}
