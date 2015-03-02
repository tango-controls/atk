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
 
package fr.esrf.tangoatk.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.esrf.tangoatk.util.AtkTimer;

public class PropertyStorage {
	protected Map<String, Property> propertyMap;

	public PropertyStorage() {
		propertyMap = new HashMap<String, Property>();

	}

	public void setProperty(String name, Number value) {
		NumberProperty p = (NumberProperty) propertyMap.get(name);
		if (p == null) {
			AtkTimer.getInstance().endTimer(Thread.currentThread()); // XXX
			return;

		} // end of if ()

		p.setValue(value);
	}

	public void setProperty(IEntity entity, String name, Object value, boolean editable) {

		propertyMap.put(name, new Property(entity, name, value, editable));
	}

	public void setProperty(IEntity entity, String name, Number value, boolean editable) {
		Property p = propertyMap.get(name);
		if (p == null)
			propertyMap.put(name, new NumberProperty(entity, name, value, editable));
		else
			p.setValue(value);
	}

	public void setProperty(IAttribute entity, String name, fr.esrf.Tango.AttrWriteType value, boolean editable) {
		Property p = propertyMap.get(name);
		if (p == null)
			propertyMap.put(name, new WritableProperty(entity, name, value, editable));
		else
			p.setValue(value);
	}

	public void setProperty(IAttribute entity, String name, fr.esrf.Tango.AttrDataFormat value, boolean editable) {
		AtkTimer.getInstance().startTimer(Thread.currentThread());
		Property p = propertyMap.get(name);
		if (p == null)
			propertyMap.put(name, new FormatProperty(entity, name, value, editable));
		else
			p.setValue(value);
		AtkTimer.getInstance().endTimer(Thread.currentThread());
	}

	public void setProperty(IEntity entity, String name, fr.esrf.Tango.DispLevel value, boolean editable) {

		Property p = propertyMap.get(name);

		if (p == null) {
			propertyMap.put(name, new DisplayLevelProperty(entity, name, value, editable));
		} else {
			p.setValue(value);
		} // end of else

	}

	public void setProperty(IEntity entity, String name, String value, boolean editable) {
		Property p = propertyMap.get(name);
		if (p == null)
			propertyMap.put(name, new StringProperty(entity, name, value, editable));
		else
			p.setValue(value);
	}

	public void setProperty(IEntity entity, String name, Object value) {
		Property p = propertyMap.get(name);
		p.setValue(value);
	}

	public Map<String, Property> getPropertyMap() {
		return propertyMap;
	}

	public void refreshProperties() {
		Iterator<Property> i = getPropertyMap().values().iterator();
		while (i.hasNext()) {
			Property prop = i.next();
			prop.refresh();
		} // end of while ()
	}

	public Property getProperty(String s) {

		AtkTimer.getInstance().startTimer(Thread.currentThread());
		Property p = null;
		if (propertyMap != null) {
			p = propertyMap.get(s);
		}
		AtkTimer.getInstance().endTimer(Thread.currentThread());
		return p;
	}

	  public double getNumberProperty(String s) {

		    NumberProperty p = (NumberProperty) getProperty(s);

		    if (p != null && p.isSpecified()) {
		      if (p.getValue() instanceof Number) {
		        return ((Number) p.getValue()).doubleValue();
		      }
		      if (p.getValue() instanceof String) {
		        try {
		          double value = Double.parseDouble((String) p.getValue());
		          return value;
		        }
		        catch (NumberFormatException nfe) {
		          return Double.NaN;
		        }
		      }
		    }

		    return Double.NaN;
	 }	
}
