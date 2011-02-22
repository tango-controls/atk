package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.Device;
import fr.esrf.tangoatk.core.DeviceFactory;
import fr.esrf.TangoApi.AttributeInfo;

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
                                     AttributeInfo config,
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
