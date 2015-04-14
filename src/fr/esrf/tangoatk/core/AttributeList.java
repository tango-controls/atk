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

import java.util.*;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.tangoatk.core.attribute.AAttribute;
import fr.esrf.tangoatk.core.attribute.AttributeFactory;

public class AttributeList extends AEntityList {

  protected Vector<DeviceItem> deviceList = new Vector<DeviceItem> ();
  protected boolean forceRefresh = false;

  // Centralizing variables to reduce memory consumption.
  protected DeviceAttribute[] att = null;
  protected DeviceItem devItem = null;
  protected Device dev = null;
  protected long currentTime;

  public AttributeList() {
    factory = AttributeFactory.getInstance();
  }

  // --------------------------------------------------------------
  // Overrides addElement to build the optimized internal structure
  // --------------------------------------------------------------
  @Override
  public void addElement(Object entity) {

    if(!(entity instanceof AAttribute)) {
      System.out.println("Warning, AttributeList supports only IAttribute.");
      return;
    }

    AAttribute attToAdd = (AAttribute)entity;
    super.addElement(attToAdd);

    // Add this entity within the private per device structure
    // whether the attribute is not refreshed using event
    if(!attToAdd.hasEvents())
      addEntity(attToAdd);

  }

  // --------------------------------------------------------------
  // private stuff
  // --------------------------------------------------------------
  synchronized private void addEntity(AAttribute att) {

    int i = 0;
    boolean found = false;
    Device attDev = att.getDevice();

    while(i<deviceList.size() && !found) {
      found = (deviceList.get(i).getDevice() == attDev);
      if(!found) i++;
    }

    if( found ) {
      deviceList.get(i).add(att);
    } else {
      // Create a new entry
      DeviceItem item = new DeviceItem(attDev);
      item.add(att);
      deviceList.add(item);
    }

  }

  synchronized private void removeEntity(AAttribute att) {

    int i = 0;
    boolean found = false;
    Device attDev = att.getDevice();

    while(i<deviceList.size() && !found) {
      found = (deviceList.get(i).getDevice() == attDev);
      if(!found) i++;
    }

    if( found ) {
      deviceList.get(i).remove(att);
      if(deviceList.get(i).getEntityNumber()==0)
      {
        deviceList.remove(i);
      }
    }

  }

  void trace(int level,String msg,long time)
  {
    DeviceFactory.getInstance().trace(level,msg,time);
  }

  // --------------------------------------------------------------
  // Remove an entity from the internal structure
  // --------------------------------------------------------------
  @Override
  public Object remove(int index) {

    Object removed = super.remove(index);

    // look for this entity within the internal structre
    // and remove it
    if(removed!=null && removed instanceof AAttribute)
      removeEntity((AAttribute)removed);

    return removed;

  }

  /**
   * Returns whether the refresher execute default refresh loop
   */
  public boolean isForceRefresh()
  {
    return forceRefresh;
  }

  /**
   * Disable or enable the optimized refresher loop (Use of read_attributes)
   * @param forceRefresh true to enable optimized loop
   */
  public void setForceRefresh (boolean forceRefresh)
  {
    this.forceRefresh = forceRefresh;
  }

  public void removeAllElements()
  {
    for (int i = 0; i < this.size(); i++)
    {
      remove(i);
    }
  }

  public void clear()
  {
    removeAllElements();
  }

  // --------------------------------------------------------------
  // Refresh implementation
  // --------------------------------------------------------------
  @Override
  public void refresh() {

    if (isForceRefresh()) {

      // Default refresh
      super.refresh();

    } else {

      // Here, only polled attribute are added to the per device structure
      // Event based attributes are not refreshed here.
      for (int i = 0; i < deviceList.size(); i++) {

        synchronized (this) {

          devItem = deviceList.get(i);
          dev = devItem.getDevice();
          currentTime = System.currentTimeMillis();

          try {

            // Read all attributes from the device
            att = dev.read_attribute(devItem.getNames());
            trace(DeviceFactory.TRACE_REFRESHER, "Device.read_attribute("
                + dev.getName() + ") " + devItem.getEntityNumber()
                + " attributes read : OK", currentTime);

            // Refresh
            for (int j = 0; j < devItem.getEntityNumber(); j++)
              devItem.getEntity(j).dispatch(att[j]);

            att = null;

          } catch (DevFailed e) {

            att = null;
            trace(DeviceFactory.TRACE_REFRESHER, "Device.read_attribute("
                + dev.getName() + ") " + devItem.getEntityNumber()
                + " attributes read : Failed", currentTime);

            // Major failure , dispatch the error
            for (int j = 0; j < devItem.getEntityNumber(); j++)
              devItem.getEntity(j).dispatchError(e);

          } // end catch (DevFailed e)

          devItem = null;
          dev = null;

        } // end synchronized(this)
      } // end for(int i=0;i<deviceList.size();i++)

      fireRefresherStepEvent();

    }

  }

  public void startRefresher() {

    // Check that all items in this list are device IDL > 3
    for(int i=0;i<size();i++) {
      IAttribute att = (IAttribute)get(i);
      int idl = att.getDevice().getIdlVersion();
      if(idl<3) {
        System.out.println("Warning, " + att.getName() + " has an IDL<=2 and does not support multiple DevFailed, switching to classic refresh.");
        forceRefresh = true;
      }
    }

    super.startRefresher();

  }

  public String getVersion() {
    return "$Id$";
  }

  private void readObject(java.io.ObjectInputStream in)
      throws java.io.IOException, ClassNotFoundException {

    System.out.print("Loading AttributeList ");
    in.defaultReadObject();
    System.out.println("Starting refresher on list");
    startRefresher();

  }

}
