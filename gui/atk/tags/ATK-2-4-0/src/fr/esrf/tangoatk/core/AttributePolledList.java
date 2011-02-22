package fr.esrf.tangoatk.core;

import java.util.*;

import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.Tango.DevFailed;
import fr.esrf.tangoatk.core.attribute.PolledAttributeFactory;

/**
  * A class to force the usage of client side polling (ATK refreshser)
  * (Ingore events)
  */
public class AttributePolledList extends AttributeList {

  private Vector deviceList = new Vector();

  public AttributePolledList() {
    factory = PolledAttributeFactory.getPolledInstance();
  }

  // --------------------------------------------------------------
  // Overrides addElement to build the optimized internal structure
  // --------------------------------------------------------------
  public void addElement(Object entity) {

    if(!(entity instanceof IAttribute)) {
      System.out.println("Warning, AttributePolledList supports only IAttribute.");
      return;
    }

    IAttribute att = (IAttribute)entity;
    super.addElement(att);

    // Add this entity within the private device list
    addEntity(att);

  }

  // --------------------------------------------------------------
  // Remove an entity from the internal structure
  // --------------------------------------------------------------
  public Object remove(int index) {

    Object removed = super.remove(index);

    // look for this entity within the internal structre
    // and remove it
    if(removed!=null && removed instanceof IAttribute)
      removeEntity((IAttribute)removed);

    return removed;

  }

  // --------------------------------------------------------------
  // Refresh implementation
  // --------------------------------------------------------------
  public void refresh() {
 
    for(int i=0;i<deviceList.size();i++) {

      DeviceItem devItem = (DeviceItem)deviceList.get(i);
      Device dev = devItem.getDevice();
      long t0 = System.currentTimeMillis();
      
      try
      {

        synchronized(this) {

          // Read all attributes from the device
          DeviceAttribute[] att = dev.read_attribute(devItem.getNames());
          trace(DeviceFactory.TRACE_REFRESHER, "Device.read_attribute(" +
	            dev.getName() + ") " + devItem.getEntityNumber() + " attributes read : OK" , t0);

  	      // Refresh
          for (int j = 0; j < devItem.getEntityNumber(); j++)
            devItem.getEntity(j).dispatch(att[j]);

        }

      } catch (DevFailed e) {
      
         trace(DeviceFactory.TRACE_REFRESHER, "Device.read_attribute(" +
	       dev.getName() + ") " + devItem.getEntityNumber() + " attributes read : Failed" , t0);

  	     // Major failure , dispatch the error
         synchronized(this) {

          for (int j = 0; j < devItem.getEntityNumber(); j++)
            devItem.getEntity(j).dispatchError(e);

         }

      }

    }

    fireRefresherStepEvent();

  }

  
  // --------------------------------------------------------------
  // private stuff
  // --------------------------------------------------------------
  synchronized private void addEntity(IAttribute att) {
  
    int i = 0;
    boolean found = false;
    Device dev = att.getDevice();
    
    while(i<deviceList.size() && !found) {
      found = ((DeviceItem)deviceList.get(i)).getDevice() == dev;
      if(!found) i++;
    }
    if( found ) {
      ((DeviceItem)deviceList.get(i)).add(att);
    } else {
      // Create a new entry
      DeviceItem item = new DeviceItem(dev);
      item.add(att);
      deviceList.add(item);
    }
    
  }

  synchronized private void removeEntity(IAttribute att) {

    int i = 0;
    boolean found = false;
    Device dev = att.getDevice();

    while(i<deviceList.size() && !found) {
      found = ((DeviceItem)deviceList.get(i)).getDevice() == dev;
      if(!found) i++;
    }
    if( found ) {
      ((DeviceItem)deviceList.get(i)).remove(att);
      if(((DeviceItem)deviceList.get(i)).getEntityNumber()==0) {
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

}

// --------------------------------------------------------------
// A class to handle the relationship betwwen device and entities
// --------------------------------------------------------------

class DeviceItem {

  private Vector entities;
  private Device device;

  DeviceItem(Device dev) {
    device   = dev;
    entities = new Vector();
  }

  Device getDevice() {
    return device;
  }
  
  int getEntityNumber() {
    return entities.size();
  }
  
  IAttribute getEntity(int idx) {
    return (IAttribute)entities.get(idx);
  }
  
  String[] getNames() {
    String[] ret = new String[entities.size()];
    for(int i=0;i<entities.size();i++)
      ret[i] = getEntity(i).getNameSansDevice();
    return ret;
  }

  void add(IAttribute entity) {
    entities.add(entity);
  }

  void remove(IAttribute entity) {
    entities.remove(entity);
  }

}
