/*
 *  Copyright (C) :	2015
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

import java.util.ArrayList;

public class StringArrayProperty extends Property
{

    public StringArrayProperty(IEntity parent, String name, ArrayList<String> arrListValue, boolean editable)
    {
        super(parent, name, arrListValue, editable);
    }

    public void setValue(ArrayList<String> arrListValue)
    {
        super.setValue(arrListValue);
    }

    public String getVersion()
    {
        return "$Id $";
    }

    public String[] getStringArrayValue()
    {
        
        if (this.getValue() instanceof ArrayList)
        {
            ArrayList<String> strArrayList = (ArrayList) this.getValue();
            if (strArrayList == null) return null;
            String[] stringArray = strArrayList.toArray(new String[strArrayList.size()]);
            return stringArray;
        }
        else
            return null;
    }

    public void setValueFromStringArray(String[] stringArrayValue)
    {
        ArrayList<String>  strList = new ArrayList<String>();
        if (stringArrayValue != null)
        {
            for (int i=0; i<stringArrayValue.length; i++)
            {
                strList.add(i, stringArrayValue[i]);
            }
        }
        setValue(strList);
    }
}
