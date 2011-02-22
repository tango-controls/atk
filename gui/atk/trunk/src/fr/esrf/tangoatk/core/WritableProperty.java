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
import fr.esrf.Tango.*;
public class WritableProperty extends Property {

    public WritableProperty(IAttribute parent, String name,
				    AttrWriteType value, boolean editable) {
	super(parent, name, value, editable);
    }

    public String getPresentation() {
	String tmp = "";
	AttrWriteType wt = (AttrWriteType)value;
	if (wt == AttrWriteType.READ) tmp = "READ";
	if (wt == AttrWriteType.READ_WITH_WRITE) tmp = "READ_WITH_WRITE";
	if (wt == AttrWriteType.WRITE) tmp = "WRITE";
	if (wt == AttrWriteType.READ_WRITE) tmp = "READ_WRITE";
	
	return tmp;

    }

    public String getVersion() {
	return "$Id$";
    }

    public void setValueFromString(String stringValue) {
        if ( "READ".equalsIgnoreCase(stringValue.trim()) ) {
            setValue(AttrWriteType.READ);
        }
        else if ( "READ_WITH_WRITE".equalsIgnoreCase(stringValue.trim()) ) {
            setValue(AttrWriteType.READ_WITH_WRITE);
        }
        else if ( "WRITE".equalsIgnoreCase(stringValue.trim()) ) {
            setValue(AttrWriteType.WRITE);
        }
        else if ( "READ_WRITE".equalsIgnoreCase(stringValue.trim()) ) {
            setValue(AttrWriteType.READ_WRITE);
        }
    }

}
