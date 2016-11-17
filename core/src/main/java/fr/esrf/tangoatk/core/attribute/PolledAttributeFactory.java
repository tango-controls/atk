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
 
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.Device;
import fr.esrf.tangoatk.core.DeviceFactory;
import fr.esrf.TangoApi.AttributeInfoEx;

/**
 * <code>PolledAttributeFactory</code> is an extension of {@link fr.esrf.tangoatk.core.attribute.AttributeFactory}.
 * It manages polled attributes (client side) and does not subscribe events.
 * @version $Revision$
 */
public class PolledAttributeFactory extends AttributeFactory {

  private static PolledAttributeFactory instance;

  /**
   * <code>getPolledInstance</code> returns an instance of the PollledAttributeFactory.
   * There will be only one PolledAttributeFactory per running instance of the JVM.
   * @return an <code>AttributeFactory</code> value
   */
  public static PolledAttributeFactory getPolledInstance() {
    if (instance == null) {
      instance = new PolledAttributeFactory();
    }
    return instance;
  }

  /**
   * @deprecated
   */
  public static AttributeFactory getInstance() {
    throw new IllegalStateException("Use getPollledInstance() with PolledAttributeFactory");
  }

  /**
   * Creates a new <code>PolledAttributeFactory</code> instance. Do not use
   * this.
   * @see #getPolledInstance
   */
  protected PolledAttributeFactory() {}

  protected AAttribute initAttribute(Device device,
                                     AttributeInfoEx config,
                                     int insertionPos,
                                     String fqname) {

    AAttribute attribute = getAttributeOfType(device,config);
    long t0 = System.currentTimeMillis();
    attribute.init(device, config.name, config, false);
    DeviceFactory.getInstance().trace(DeviceFactory.TRACE_SUCCESS,"PolledAttributeFactory.init(" + fqname + ")",t0);

    // Build the new attNames array
    buildNames(fqname,insertionPos);

    attributes.add(insertionPos, attribute);

    return attribute;

  }

}
