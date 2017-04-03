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

import fr.esrf.Tango.DevSource;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.Tango.DevFailed;
import fr.esrf.tangoatk.core.attribute.PolledAttributeFactory;

import javax.xml.transform.Source;

/**
  * A class to force the usage of client side polling (ATK refreshser)
  * (Ingore events)
  */
public class AttributePolledList extends AttributeList {

  public AttributePolledList() {
    factory = PolledAttributeFactory.getPolledInstance();
  }

  public void setSource(DevSource source) throws ATKException {

    if( isForceRefresh() ) {

      try {
        for (int i = 0; i < size(); i++) {
          IEntity ie = (IEntity) get(i);
          ((Device)ie.getDevice()).set_source(source);
        }
      } catch (DevFailed e) {
        throw new ATKException(e);
      }

    } else {

      try {
        synchronized (this) {
          for (int i = 0; i < deviceList.size(); i++) {
            devItem = deviceList.get(i);
            devItem.getDevice().set_source(source);
          }
        }
      } catch (DevFailed e) {
        throw new ATKException(e);
      }

    }

  }

  public String getVersion() {
    return "$Id$";
  }


}

