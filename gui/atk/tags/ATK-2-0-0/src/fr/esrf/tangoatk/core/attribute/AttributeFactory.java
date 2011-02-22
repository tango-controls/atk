// File:          AttributeFactory.java
// Created:       2001-09-24 12:51:53, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-09 16:32:41, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;

import java.util.*;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.AttributeInfo;
import fr.esrf.Tango.AttrDataFormat;

/**
 * <code>AttributeFactory</code> is an extension of {@link AEntityFactory}
 * which takes care of instantiating Attribute objects. It is a Singleton,
 * so please use getInstance to instantiate the Factory.
 * @version $Revision$
 */
public class AttributeFactory extends AEntityFactory {

  private static AttributeFactory instance;
  private Vector   attributes = new Vector();
  private String[] attNames   = new String[0]; // For fast string search

  /**
   * Creates a new <code>AttributeFactory</code> instance. Do not use
   * this.
   * @see #getInstance
   */
  protected AttributeFactory() {}

  /**
   * <code>getInstance</code> returns an instance of the AttributeFactory.
   * There will be only one AttributeFactory per running instance of the JVM.
   * @return an <code>AttributeFactory</code> value
   */
  public static AttributeFactory getInstance() {
    if (instance == null) {
      instance = new AttributeFactory();
    }
    return instance;
  }

  public int getSize() {
    return attributes.size();
  }

  private int getAttributePos(String fqname) {
    return Arrays.binarySearch(attNames,fqname.toLowerCase());
  }

  /**
   * <code>getWildCardEntities</code>
   *
   * @param name a <code>String</code> value containing the name of the
   * attributes to be instantiated.
   * @param device a <code>Device</code> value containging the device
   * that the attribute belongs to
   * @return a <code>List</code> value containing the corresponding
   * IAttributes
   * @exception DevFailed if an error occurs
   * @see IAttribute
   */
  protected synchronized List getWildCardEntities(String name, Device device)
          throws DevFailed {

    List list = new Vector();

    /*
     * Tango_AllAttr is the magic name which signifies that we want
     * all the attributes from a device. It's defined in the
     * interface fr.esrf.TangoDs.TangoConst
     */
    name = Tango_AllAttr;
    String[] attributeNames = {name};

    /*
     * Get the array of configs from the device
     */
    AttributeInfo[] config = device.getAttributeInfo(attributeNames);

    /*
     * loop through all these configs and see if we havn't already
     * imported it.
     */
    for (int i = 0; i < config.length; i++) {

      String fqname = getFQName(device, config[i].name);
      AAttribute attribute;

      int pos = getAttributePos(fqname);

      if( pos>=0 ) {
        attribute = (AAttribute) attributes.get(pos);
      } else {
        attribute = initAttribute(device, config[i],-(pos+1),fqname);
      }

      list.add(attribute);
    }

    return list;
  }

  /**
   * <code>getSingleEntity</code> returns an attribute corresponding
   * to the name given in the first parameter. If such an attribute already
   * exists, the existing attribute is returned. Otherwise it is created.
   * @param fqname a <code>String</code> value containing the EntityName
   * fully qualified with device name.
   * @param device a <code>Device</code> value the device
   * @return an <code>IEntity</code> value
   * @exception DevFailed if an error occurs
   */
  protected synchronized IEntity getSingleEntity(String fqname, Device device)
          throws DevFailed {

    /*
     * Check if the attribute has already been imported
     */
    int pos = getAttributePos(fqname);

    /*
     * if so we return the old attribute and exit.
     */
    if (pos>=0) return (AAttribute) attributes.get(pos);

    /*
     * To obtain a new attribute we must find its name.
     * The name is passed fully-qualified, that is with the
     * device-name prefixed, like eas/test-api/1/Attr_name.
     */
    String name = extractEntityName(fqname);

    /*
     * We obtain the config for our attribute from the device and
     * return an initialized attribute.
     */
    AttributeInfo config = device.getAttributeInfo(name);
    return initAttribute(device, config,-(pos+1),fqname);
  }


  /**
   * Returns an Attribute corresponding to the given name. If such an attribute
   * already exists, the existing attribute is returned. Otherwise it is created.
   * @param fqname a <code>String</code> value containing the EntityName
   * fully qualified with device name.
   * @return null if the type of the IEntity is not AAttribute, a valid entity otherwise.
   * @throws ConnectionException
   * @throws DevFailed
   */
  public IAttribute getAttribute(String fqname)
          throws ConnectionException, DevFailed {

    Device d = null;
    IEntity ie = null;
    AAttribute att = null;

    // Check wether the attribute has alrerady been imported.
    synchronized(this) {
      int pos = getAttributePos(fqname);
      if (pos>=0) ie = (IEntity) attributes.get(pos);
    }

    if (ie == null) {
      // Create the entity
      d  = getDevice(extractDeviceName(fqname));
      ie = getSingleEntity(fqname, d);
    }

    // Does not exists.
    if (ie == null)
      return null;

    // Check entity type
    if (ie instanceof AAttribute) {
      att = (AAttribute) ie;
      return att;
    } else
      return null;

  }

  /**
   * Check wether the given name correspond to an existing attribute.
   * @param fqname Full entity name
   * @return True if the attribute exists.
   */
  public boolean isAttribute(String fqname) {

    try {
      return (getAttribute(fqname)!=null);
    } catch (Exception e) {
      return false;
    }

  }

  /**
   * <code>initAttribute</code> ask getAttributeOfType for an
   * AAttribute of the type given in the AttributeInfo passed as
   * parameter, and then calls <code>init()</code> on the attribute
   *
   * @param device a <code>Device</code> value
   * @param config an <code>AttributeInfo</code> value
   * @param insertionPos Insertion postion of this new attribute in the global list
   * @param fqname Full entity name (can include host and port)
   * @return an <code>AAttribute</code> value
   */
  private AAttribute initAttribute(Device device,
                                     AttributeInfo config,
                                     int insertionPos,
                                     String fqname) {

    AAttribute attribute = getAttributeOfType(device.getName(),config);
    long t0 = System.currentTimeMillis();
    attribute.init(device, config.name, config);
    DeviceFactory.getInstance().trace(DeviceFactory.TRACE_SUCCESS,"AttributeFactory.init(" + fqname + ")",t0);

    // Build the new attNames array
    int lgth = attNames.length;
    String[] newAttNames=new String[lgth+1];
    System.arraycopy(attNames,0,newAttNames,0,insertionPos);
    System.arraycopy(attNames,insertionPos,newAttNames,insertionPos+1,lgth-insertionPos);
    newAttNames[insertionPos]=fqname.toLowerCase();
    attNames=newAttNames;

    attributes.add(insertionPos, attribute);

    dumpFactory("Adding " + fqname);

    return attribute;
  }

  /**
   * <code>getAttributeOfType</code> figures out what type of
   * attribute is demanded, and deletates the work of instantiating the
   * attribute to getScalar, getSpectrum, or getImage. The typeinference
   * is based on the value of AttributeConfig.data_format.
   * @param config an <code>AttributeInfo</code> value
   * @return an <code>AAttribute</code> value
   * @throws IllegalArgumentException if the format is unknown.
   */
  private AAttribute getAttributeOfType(String devName,AttributeInfo config) {

    if (config == null) {
      System.out.println("Warning, AttributeFactory.getAttributeOfType(): Warning, null AttributeInfo pointer got from " + devName);
      return new InvalidAttribute();
    }

    AttrDataFormat format = config.data_format;
    String name = config.name;

    switch (format.value()) {
      case AttrDataFormat._SCALAR:
        return getScalar(devName,config);
      case AttrDataFormat._SPECTRUM:
        return getSpectrum(devName,config);
      case AttrDataFormat._IMAGE:
        return getImage(devName,config);
      default:
        System.out.println("Warning, AttributeFactory.getAttributeOfType(" + devName + "/" + name +
                           ") : Unsupported attribute format [" + format.value() +"]");
        return new InvalidAttribute();
    }

  }

  /**
   * <code>getScalar</code> figures out what kind of scalar is to be
   * created. This is done by looking at AttributeInfo.data_type
   *
   * @param config an <code>AttributeInfo</code> value
   * @return an <code>AAttribute</code> value being instanceof either
   * ShortScalar, DoubleScalar, LongScalar, StringScalar, BooleanScalar or DevStateScalar.
   * @throws IllegalArgumentException if the type is unknown.
   */
  private AAttribute getScalar(String devName,AttributeInfo config) {
    String name = config.name;
    int dataType = config.data_type;
    BooleanScalar   bs=null;
    DevStateScalar  dss=null;

    if (dataType == Tango_DEV_STRING) {
      return new StringScalar();
    }

    NumberScalar ns = new NumberScalar();

    switch (dataType) {
      case Tango_DEV_SHORT:
        ns.setNumberHelper(new ShortScalarHelper(ns));
        break;
      case Tango_DEV_DOUBLE:
        ns.setNumberHelper(new DoubleScalarHelper(ns));
        break;
      case Tango_DEV_LONG:
        ns.setNumberHelper(new LongScalarHelper(ns));
        break;
      case Tango_DEV_BOOLEAN:
        bs = new BooleanScalar();
        return bs;
      case Tango_DEV_STATE:
        dss = new DevStateScalar();
        return dss;
      default:
        System.out.println("Warning, AttributeFactory.getScalar(" + devName + "/" + name +
                           ") : Unsupported data type [" + dataType +"]");
        return new InvalidAttribute();
    }
    return ns;
  }

  /**
   * <code>getSpectrum</code> figures out what kind of spectrum is to be
   * created. This is done by looking at AttributeInfo.data_type
   *
   * @param config an <code>AttributeInfo</code> value
   * @return an <code>AAttribute</code> value being instanceof either
   * ShortSpectrum, DoubleSpectrum, LongSpectrum or BooleanSpectrum
   * @throws IllegalArgumentException if the type is unknown.
   */
  private AAttribute getSpectrum(String devName,AttributeInfo config) {
    String name = config.name;
    int dataType = config.data_type;
    NumberSpectrum ns = new NumberSpectrum();
    StringSpectrum ss = null;
    BooleanSpectrum bs = null;

    switch (dataType) {
      case Tango_DEV_SHORT:
        ns.setNumberHelper(new ShortSpectrumHelper(ns));
        return ns;
      case Tango_DEV_DOUBLE:
        ns.setNumberHelper(new DoubleSpectrumHelper(ns));
        return ns;
      case Tango_DEV_LONG:
        ns.setNumberHelper(new LongSpectrumHelper(ns));
        return ns;
      case Tango_DEV_STRING:
        ss = new StringSpectrum();
        return ss;
      case Tango_DEV_BOOLEAN:
        bs = new BooleanSpectrum();
        return bs;
      default:
        System.out.println("Warning, AttributeFactory.getSpectrum(" + devName + "/" + name +
                           ") : Unsupported data type [" + dataType +"]");
        return new InvalidAttribute();

    }
  }

  /**
   * <code>getSpectrum</code> figures out what kind of spectrum is to be
   * created. This is done by looking at AttributeInfo.data_type
   *
   * @param config an <code>AttributeInfo</code> value
   * @return an <code>AAttribute</code> value being instanceof either
   * ShortSpectrum, DoubleSpectrum, or LongSpectrum
   * @throws IllegalArgumentException if the type is unknown.
   */
  private AAttribute getImage(String devName,AttributeInfo config) {
    String name = config.name;
    int dataType = config.data_type;
    NumberImage ns = new NumberImage();
    
    BooleanImage bi = null;
    
    switch (dataType) {
      case Tango_DEV_SHORT:
        ns.setNumberHelper(new ShortImageHelper(ns));
        return ns;
      case Tango_DEV_DOUBLE:
        ns.setNumberHelper(new DoubleImageHelper(ns));
        return ns;
      case Tango_DEV_LONG:
        ns.setNumberHelper(new LongImageHelper(ns));
        return ns;
      case Tango_DEV_BOOLEAN:
        bi = new BooleanImage();
        return bi;
      default:
        System.out.println("Warning, AttributeFactory.getSpectrum(" + devName + "/" + name +
                           ") : Unsupported data type [" + dataType +"]");
        return new InvalidAttribute();
    }
  }

  private void dumpFactory(String msg) {
    if((DeviceFactory.getInstance().getTraceMode() & DeviceFactory.TRACE_ATTFACTORY)!=0) {
      System.out.println("-- AttributeFactory : " + msg + " --");
      for(int i=0;i<attNames.length;i++) {
        System.out.println("  " + i + ":" + attNames[i]);
      }
      System.out.println("-- AttributeFactory --------------------------------------");
    }
  }

  public String getVersion() {
    return "$Id$";
  }

}
