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

    public void setValueFromString(String stringValue) {
        try {
            if (value instanceof Double) {
                Double tempVal = Double.valueOf(stringValue);
                setValue( tempVal );
            }
            else if (value instanceof Float) {
                Float tempVal = Float.valueOf(stringValue);
                setValue( tempVal );
            }
            else if (value instanceof Integer) {
                Integer tempVal = Integer.valueOf(stringValue);
                setValue( tempVal );
            }
            else if (value instanceof Long) {
                Long tempVal = Long.valueOf(stringValue);
                setValue( tempVal );
            }
            else if (value instanceof Short) {
                Short tempVal = Short.valueOf(stringValue);
                setValue( tempVal );
            }
            else {
                Byte tempVal = Byte.valueOf(stringValue);
                setValue( tempVal );
            }
        }
        catch (NumberFormatException nfe) {
            //nothing to do
        }
    }

}

	
