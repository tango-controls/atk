// File:          TangoFactory.java
// Created:       2001-09-28 09:45:55, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-10 13:53:25, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core;

import java.util.*;

import fr.esrf.Tango.DevFailed;

import fr.esrf.TangoDs.*;

/**
 * <code>AEntityFactory</code> keeps all the code that is common for
 * the AttributeFactory and the CommandFactory. It is used by
 * {@link AEntityList}.
 * @version $Revision$
 */
public abstract class AEntityFactory implements TangoConst,
        java.io.Serializable {

  private DeviceFactory deviceFactory = DeviceFactory.getInstance();

  /**
   * <code>extractDeviceName</code> extracts the device name from
   * an entityName
   * @param entityName a <code>String</code> value
   * @return a <code>String</code> value containing the deviceName.
   */
  public static String extractDeviceName(String entityName) {
    int i = entityName.lastIndexOf("/");
    return entityName.substring(0, i);
  }


  public static String getFQName(Device device, String name) {
    return new StringBuffer(device.toString()).append("/").append(name).toString();
  }

  /**
   * <code>extractEntityName</code> Given an entityname of the form
   * aa/bb/cc/EntityName, this method will return EntityName
   * @param entityName a <code>String</code> value
   * @return a <code>String</code> value containing the entity name.
   */
  public static String extractEntityName(String entityName) {
    int i;
    if ((i = entityName.lastIndexOf("/")) == -1) return entityName;
    return entityName.substring(i + 1, entityName.length());
  }

  /**
   * <code>isWildCard</code> returns true if the entityName contains a
   * *
   * @param entityName a <code>String</code> value
   * @return a <code>boolean</code> value
   */
  public static boolean isWildCard(String entityName) {
    return entityName.indexOf("*") != -1;
  }


  /**
   * <code>getDevice</code>
   *
   * @param deviceName a <code>String</code> value containing the
   * name of the device to get.
   * @return a <code>Device</code> if this device is already known.
   * @exception ConnectionException if an error occurs
   * @see DeviceFactory#getDevice
   */
  protected Device getDevice(String deviceName) throws ConnectionException {
    return deviceFactory.getDevice(deviceName);
  }


  /**
   * <code>getEntities</code> returns a list of entities corresponding to
   * the name passed as a parameter. If the name is a wildcard, the returned
   * list might contain more than one entity.
   * @param name a <code>String</code> value containing the name of the
   * entity wanted. Might be a wildcard.
   * @return a <code>List</code> value containing the corresponding entities
   * @exception ConnectionException if an error occurs
   * @see AEntityFactory#isWildCard(String)
   */
  public List getEntities(String name)
          throws ConnectionException {

    List l;
    try {
      Device d = getDevice(extractDeviceName(name));

      if (!isWildCard(name)) {
        l = new Vector();
        l.add(getSingleEntity(name, d));
      } else {
        l = getWildCardEntities(name, d);
      }

      return l;
    } catch (DevFailed d) {
      throw new ConnectionException(d);
    }

  }


  /**
   * <code>getWildCardEntities</code> is called if isWildCard(name) is
   * true.
   * @param name a <code>String</code> value containing the name of the
   * entity
   * @param device a <code>Device</code> value containing the device from
   * which the entity is to be obtained.
   * @return a <code>List</code> value containing the entities corresponding
   * to the name
   * @exception DevFailed if an error occurs
   * @see AEntityFactory#isWildCard(String)
   */
  protected abstract List getWildCardEntities(String name, Device device)
          throws DevFailed;

  /**
   * <code>getSingleEntity</code> is called if isWIldCard(name) is false
   * @param name a <code>String</code> value containing hte name of the
   * entity
   * @param d a <code>Device</code> value containing the device from
   * which the entity is to be obtained
   * @return an <code>IEntity</code> value containing the entity
   * corresponding to the name.
   * @exception DevFailed if an error occurs
   */
  protected abstract IEntity getSingleEntity(String name, Device d)
          throws DevFailed;

  /**
   * Returns the number of entity present in this factory.
   * @return Number of entity
   */
  public abstract int getSize();

  /**
   * Return release information of this factory.
   * @return Release information.
   */
  public abstract String getVersion();

}
