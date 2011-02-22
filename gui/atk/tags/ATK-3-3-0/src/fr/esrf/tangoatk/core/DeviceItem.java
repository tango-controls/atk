package fr.esrf.tangoatk.core;

import java.util.Vector;

// --------------------------------------------------------------
// A class to handle the relationship betwwen device and entities
// --------------------------------------------------------------
public class DeviceItem 
{

     private Vector<IAttribute> entities;
     private Device device;

     DeviceItem(Device dev) {
       device   = dev;
       entities = new Vector<IAttribute> ();
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

