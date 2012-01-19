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

import java.util.*;

import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.Tango.DevFailed;
import fr.esrf.tangoatk.core.attribute.AAttribute;
import fr.esrf.tangoatk.core.attribute.PolledAttributeFactory;

/**
  * A class to force the usage of client side polling (ATK refreshser)
  * (Ingore events)
  */
public class AttributePolledList extends AttributeList {

  //private Vector deviceList = new Vector();
  private Vector<DeviceItem> deviceList = new Vector<DeviceItem> ();
  private boolean forceRefresh = false;

  // Centralizing variables to reduce memory consumption.
  private DeviceAttribute[] att = null;
  private DeviceItem devItem = null;
  private Device dev = null;
  private long currentTime;

  public AttributePolledList() {
    factory = PolledAttributeFactory.getPolledInstance();
  }

  // --------------------------------------------------------------
  // Overrides addElement to build the optimized internal structure
  // --------------------------------------------------------------
    @Override
  public void addElement(Object entity) {

    if(!(entity instanceof AAttribute)) {
      System.out.println("Warning, AttributePolledList supports only IAttribute.");
      return;
    }

    AAttribute attToAdd = (AAttribute)entity;
    super.addElement(attToAdd);

    // Add this entity within the private device list
    addEntity(attToAdd);

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

  // --------------------------------------------------------------
  // Refresh implementation
  // --------------------------------------------------------------
    @Override
  public void refresh()
  {

    for(int i=0;i<deviceList.size();i++)
    {
      synchronized(this)
      {
        //devItem = (DeviceItem)deviceList.get(i);
        devItem = deviceList.get(i);
        dev = devItem.getDevice();
        currentTime = System.currentTimeMillis();
        if ( isForceRefresh () )
        {
          for (int j = 0; j < devItem.getEntityNumber(); j++)
            devItem.getEntity(j).refresh();
        }
        else
        {
          try
          {
            // Read all attributes from the device
            att = dev.read_attribute(devItem.getNames());
            trace(DeviceFactory.TRACE_REFRESHER, "Device.read_attribute("
                  + dev.getName() + ") " + devItem.getEntityNumber() 
                  + " attributes read : OK" , currentTime);

            // Refresh
            for (int j = 0; j < devItem.getEntityNumber(); j++)
              devItem.getEntity(j).dispatch(att[j]);

            att = null;
          }
          catch (DevFailed e)
          {
            att = null;
            trace(DeviceFactory.TRACE_REFRESHER, "Device.read_attribute("
                  + dev.getName() + ") " + devItem.getEntityNumber() 
                  + " attributes read : Failed" , currentTime);

            // Major failure , dispatch the error
            synchronized(this)
            {
              for (int j = 0; j < devItem.getEntityNumber(); j++)
                devItem.getEntity(j).dispatchError(e);
            }
          } // end catch (DevFailed e)
        } // end if ( isForceRefresh () ) ... else
        devItem = null;
        dev = null;
      } // end synchronized(this)
    } // end for(int i=0;i<deviceList.size();i++)

    fireRefresherStepEvent();

  } // end refresh()

  
  // --------------------------------------------------------------
  // private stuff
  // --------------------------------------------------------------
  synchronized private void addEntity(AAttribute att) {
  
    int i = 0;
    boolean found = false;
    Device attDev = att.getDevice();
    
    while(i<deviceList.size() && !found) {
      //found = ((DeviceItem)deviceList.get(i)).getDevice() == dev;
      found = (deviceList.get(i).getDevice() == attDev);
      if(!found) i++;
    }
    if( found ) {
      //((DeviceItem)deviceList.get(i)).add(att);
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
      //found = ((DeviceItem)deviceList.get(i)).getDevice() == dev;
      found = (deviceList.get(i).getDevice() == attDev);
      if(!found) i++;
    }
    if( found ) {
      //((DeviceItem)deviceList.get(i)).remove(att);
      deviceList.get(i).remove(att);
      //if(((DeviceItem)deviceList.get(i)).getEntityNumber()==0)
      if(deviceList.get(i).getEntityNumber()==0)
      {
        deviceList.remove(i);
      }
    }

  }

  private void trace(int level,String msg,long time)
  {
    DeviceFactory.getInstance().trace(level,msg,time);
  }

  public String getVersion() {
    return "$Id$";
  }

  public boolean isForceRefresh ()
  {
    return forceRefresh;
  }

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

}

