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

import fr.esrf.tangoatk.core.attribute.AAttribute;
import java.util.Vector;

// --------------------------------------------------------------
// A class to handle the relationship betwwen device and entities
// --------------------------------------------------------------
public class DeviceItem 
{

     private Vector<AAttribute> entities;
     private Device device;

     DeviceItem(Device dev) {
       device   = dev;
       entities = new Vector<AAttribute> ();
     }

     Device getDevice() {
       return device;
     }

     int getEntityNumber() {
       return entities.size();
     }

     AAttribute getEntity(int idx) {
       return (AAttribute)entities.get(idx);
     }

     String[] getNames() {
       String[] ret = new String[entities.size()];
       for(int i=0;i<entities.size();i++)
	 ret[i] = getEntity(i).getNameSansDevice();
       return ret;
     }

     void add(AAttribute entity) {
       entities.add(entity);
     }

     void remove(AAttribute entity) {
       entities.remove(entity);
     }
}

