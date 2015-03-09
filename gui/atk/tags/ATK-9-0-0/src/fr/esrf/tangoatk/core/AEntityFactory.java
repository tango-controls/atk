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

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.ApiUtil;
import fr.esrf.TangoApi.Database;
import fr.esrf.TangoDs.TangoConst;

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

  /**
     * <code>extractDeviceNameAdvanced</code> extracts the device name from an
     * entity name, a device name, or an alias.
     * 
     * @param name
     *            a <code>String</code> value
     * @return a <code>String</code> value containing the deviceName. 
     *         Returns <code>null</code> if no device name can match.
     * @throws DevFailed
     *             in case of error connecting with database
     */
  public static String extractDeviceNameAdvanced(String name) throws DevFailed
  {
    int i = name.lastIndexOf("/");
    //return entityName.substring(0, i);
    StringTokenizer tokenizer = new StringTokenizer(name, "/");
    int tokenCount = tokenizer.countTokens();
    Database db;
    String alias, referenceName;
    switch(tokenCount)
    {
      case 4:
        //entity name
        return name.substring(0, i);
      case 3:
        //device name
        return name;
      case 2:
        //device alias followed by entity
        alias = name.substring(0, i);
        db = ApiUtil.get_db_obj();
        try
        {
          referenceName = db.get_alias_device(alias);
          if (referenceName == null || "".equals(referenceName) || alias.equals(referenceName))
          {
            //no device name found
            return null;
          }
          else
          {
            return referenceName;
          }
        }
        catch(DevFailed d)
        {
          //no device name found
          return null;
        }
      case 1:
        //device alias or entity alias
        alias = new String(name);
        db = ApiUtil.get_db_obj();
        try
        {
          referenceName = db.get_alias_device(alias);
          if (referenceName == null || "".equals(referenceName) || alias.equals(referenceName))
          {
            referenceName = db.get_attribute_alias(alias);
            if (referenceName == null || "".equals(referenceName) || alias.equals(referenceName))
            {
              //no attribute name found
              return null;
            }
            else
            {
              //attribute name found, return device name
              i = referenceName.lastIndexOf("/");
              return referenceName.substring(0, i);
            }
          }
          else
          {
            //device name found
            return referenceName;
          }
        }
        catch(DevFailed d1)
        {
          try
          {
            referenceName = db.get_attribute_alias(alias);
            if (referenceName == null || "".equals(referenceName) || alias.equals(referenceName))
            {
              //no attribute name found
              return null;
            }
            else
            {
              //attribute name found, return device name
              i = referenceName.lastIndexOf("/");
              return referenceName.substring(0, i);
            }
          }
          catch(DevFailed d2)
          {
            //no attribute name found
            return null;
          }
        }
      default:
        //unknown case
        return null;
    }
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
   * @return a <code>List<IEntity></code> value containing the corresponding entities
   * @exception ConnectionException if an error occurs
   * @see AEntityFactory#isWildCard(String)
   */
    public List<IEntity> getEntities(String name) throws ConnectionException
    {
        List<IEntity> l;
        try
        {
            Device d = getDevice(extractDeviceName(name));

            if (!isWildCard(name))
            {
                l = new Vector<IEntity>();
                l.add(getSingleEntity(name, d));
            }
            else
            {
                l = getWildCardEntities(name, d);
            }

            return l;
        }
        catch (ConnectionException ce)
        {
            if (name.toLowerCase().endsWith("/state"))
            {
                List<IEntity> eList = getConnectionlessSingleAttribute(name);
                if (eList == null) // we are in a CommandFactory so we should return exception
                    throw ce;
                else
                    return eList;
            }
            else
                throw ce;
        }
        catch (DevFailed d)
        {
            throw new ConnectionException(d);
        }
    }

  protected List<IEntity> getConnectionlessSingleAttribute(String name) throws ConnectionException
  {
      // This mothod is overrid in AttributeFactory because it does not concern
      // CommandFactory we return null
      return null;
  }

  /**
   * <code>getWildCardEntities</code> is called if isWildCard(name) is
   * true.
   * @param name a <code>String</code> value containing the name of the
   * entity
   * @param device a <code>Device</code> value containing the device from
   * which the entity is to be obtained.
   * @return a <code>List<IEntity></code> value containing the entities corresponding
   * to the name
   * @exception DevFailed if an error occurs
   * @see AEntityFactory#isWildCard(String)
   */
  protected abstract List<IEntity> getWildCardEntities(String name, Device device)
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
