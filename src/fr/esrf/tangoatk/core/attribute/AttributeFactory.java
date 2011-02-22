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
import fr.esrf.TangoApi.DbAttribute;
/**
 * <code>AttributeFactory</code> is an extension of {@link AEntityFactory}
 * which takes care of instantiating Attribute objects. It is a Singleton,
 * so please use getInstance to instantiate the Factory.
 * @version $Revision$
 */
public class AttributeFactory extends AEntityFactory {

  private static AttributeFactory instance;
  protected Vector   attributes = new Vector();
  protected String[] attNames   = new String[0]; // For fast string search

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

  /**
   * Returns all attributes belonging to this factory.
   */
  public AAttribute[] getAttributes() {

    AAttribute[] ret = new AAttribute[attributes.size()];
    synchronized(this) {
      for(int i=0;i<attributes.size();i++)
        ret[i] = (AAttribute)attributes.get(i);
    }
    return ret;
    
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
    } catch (ConnectionException de) {
      System.out.println("AttributeFactory.isAttribute(" + fqname + ") : " + de.getErrors()[0].desc);
      return false;
    } catch (DevFailed dfe) {
      System.out.println("AttributeFactory.isAttribute(" + fqname + ") : " + dfe.errors[0].desc);
      return false;
    } catch (Exception e) {
      // Unexpected exception
      System.out.println("AttributeFactory.isAttribute(" + fqname + ") : Unexpected exception caught...");
      e.printStackTrace();
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
  protected AAttribute initAttribute(Device device,
                                     AttributeInfo config,
                                     int insertionPos,
                                     String fqname) {

    AAttribute attribute = getAttributeOfType(device,config);
    long t0 = System.currentTimeMillis();
    attribute.init(device, config.name, config, true);
    DeviceFactory.getInstance().trace(DeviceFactory.TRACE_SUCCESS,"AttributeFactory.init(" + fqname + ")",t0);

    // Build the new attNames array
    buildNames(fqname,insertionPos);

    attributes.add(insertionPos, attribute);

    dumpFactory("Adding " + fqname);

    return attribute;
  }

  protected void buildNames(String fqname,int insertionPos) {

    int lgth = attNames.length;
    String[] newAttNames=new String[lgth+1];
    System.arraycopy(attNames,0,newAttNames,0,insertionPos);
    System.arraycopy(attNames,insertionPos,newAttNames,insertionPos+1,lgth-insertionPos);
    newAttNames[insertionPos]=fqname.toLowerCase();
    attNames=newAttNames;

  }

  /**
   * <code>getAttributeOfType</code> figures out what type of
   * attribute is demanded, and deletates the work of instantiating the
   * attribute to getScalar, getSpectrum, or getImage. The typeinference
   * is based on the value of AttributeConfig.data_format.
   * @param device a <code>Device</code> value
   * @param config an <code>AttributeInfo</code> value
   * @return an <code>AAttribute</code> value
   * @throws IllegalArgumentException if the format is unknown.
   */
  protected AAttribute getAttributeOfType(Device device,AttributeInfo config) {

    if (config == null) {
      System.out.println("Warning, AttributeFactory.getAttributeOfType(): Warning, null AttributeInfo pointer got from " + device.getName());
      return new InvalidAttribute();
    }

    AttrDataFormat format = config.data_format;
    String name = config.name;

    switch (format.value()) {
      case AttrDataFormat._SCALAR:
        return getScalar(device,config);
      case AttrDataFormat._SPECTRUM:
        return getSpectrum(device,config);
      case AttrDataFormat._IMAGE:
        return getImage(device,config);
      default:
        System.out.println("Warning, AttributeFactory.getAttributeOfType(" + device.getName() + "/" + name +
                           ") : Unsupported attribute format [" + format.value() +"]");
        return new InvalidAttribute();
    }

  }
  
    
  private EnumScalar getEnumScalar(Device device,AttributeInfo config)
  {
      String         name = config.name;
      DbAttribute    dbAtt=null;
      String[]       propVal=null;
      
      String[]       enumLabs =null;
      EnumScalar     ens = null;
      
      try
      {
	 dbAtt = device.get_attribute_property(name);
	 if (dbAtt == null)
	 {
	    //System.out.println("get_attribute_property("+name+") returns null");
	 }
	 else
	    if (dbAtt.is_empty("EnumLabels"))
	    {
	       //System.out.println("EnumLabels property not found for "+name);
	    }
	    else
	    {
	       //System.out.println("Found EnumLabels property for "+name);
	       propVal = dbAtt.get_value("EnumLabels");
	       //System.out.println("EnumLabels = "+propVal);
	       if (propVal == null)
	       {
		   //System.out.println("propVal = null");
	       }
	       else
	       {
		   if (propVal[0].length() > 2)
		   {
		      enumLabs = propVal[0].split("\n");
		   }
	       }
	    }
      }
      catch (Exception ex)
      {
	 System.out.println("get_attribute_property("+name+") thrown exception");
	 ex.printStackTrace();
      }
      
      if (enumLabs == null)
          return null;

      if (enumLabs.length <= 1)
          return null;

      ens = new EnumScalar(enumLabs);
      return ens;
  }
  

  /**
   * <code>getScalar</code> figures out what kind of scalar is to be
   * created. This is done by looking at AttributeInfo.data_type
   *
   * @param device a <code>Device</code> value
   * @param config an <code>AttributeInfo</code> value
   * @return an <code>AAttribute</code> value being instanceof either
   * ShortScalar, DoubleScalar, LongScalar, StringScalar, BooleanScalar or DevStateScalar.
   * @throws IllegalArgumentException if the type is unknown.
   */
  private AAttribute getScalar(Device device,AttributeInfo config) {
    String name = config.name;
    int dataType = config.data_type;
    BooleanScalar   bs=null;
    DevStateScalar  dss=null;
    EnumScalar      ens=null;

    if (dataType == Tango_DEV_STRING) {
      return new StringScalar();
    }

    NumberScalar ns = new NumberScalar();

    switch (dataType) {
      case Tango_DEV_UCHAR:
        ns.setNumberHelper(new UCharScalarHelper(ns));
        return ns;
      case Tango_DEV_SHORT:
        ens = getEnumScalar(device, config);
	if (ens != null)
	{
	   ens.setEnumHelper(new EnumScalarHelper(ens));
	   return ens;
	}
	else
	   ns.setNumberHelper(new ShortScalarHelper(ns));
        break;
      case Tango_DEV_USHORT:
        ens = getEnumScalar(device, config);
	if (ens != null)
	{
	   ens.setEnumHelper(new EnumScalarHelper(ens));
	   return ens;
	}
	else
           ns.setNumberHelper(new UShortScalarHelper(ns));
        break;
      case Tango_DEV_DOUBLE:
        ns.setNumberHelper(new DoubleScalarHelper(ns));
        break;
      case Tango_DEV_FLOAT:
        ns.setNumberHelper(new FloatScalarHelper(ns));
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
        System.out.println("Warning, AttributeFactory.getScalar(" + device.getName() + "/" + name +
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
  private AAttribute getSpectrum(Device device,AttributeInfo config) {
    String name = config.name;
    int dataType = config.data_type;
    NumberSpectrum ns = new NumberSpectrum();
    StringSpectrum ss = null;
    BooleanSpectrum bs = null;

    switch (dataType) {
      case Tango_DEV_UCHAR:
        ns.setNumberHelper(new UCharSpectrumHelper(ns));
        return ns;
      case Tango_DEV_SHORT:
        ns.setNumberHelper(new ShortSpectrumHelper(ns));
        return ns;
      case Tango_DEV_USHORT:
        ns.setNumberHelper(new UShortSpectrumHelper(ns));
        return ns;
      case Tango_DEV_DOUBLE:
        ns.setNumberHelper(new DoubleSpectrumHelper(ns));
        return ns;
      case Tango_DEV_FLOAT:
        ns.setNumberHelper(new FloatSpectrumHelper(ns));
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
        System.out.println("Warning, AttributeFactory.getSpectrum(" + device.getName() + "/" + name +
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
  private AAttribute getImage(Device device,AttributeInfo config) {
    String name = config.name;
    int dataType = config.data_type;
    NumberImage ns = new NumberImage();
    
    BooleanImage bi = null;
    RawImage ri = null;
    
    switch (dataType) {
      case Tango_DEV_UCHAR:
        ns.setNumberHelper(new UCharImageHelper(ns));
        return ns;
//        ri = new RawImage();
//        return ri;
      case Tango_DEV_SHORT:
        ns.setNumberHelper(new ShortImageHelper(ns));
        return ns;
      case Tango_DEV_USHORT:
        ns.setNumberHelper(new UShortImageHelper(ns));
        return ns;
      case Tango_DEV_DOUBLE:
        ns.setNumberHelper(new DoubleImageHelper(ns));
        return ns;
      case Tango_DEV_FLOAT:
        ns.setNumberHelper(new FloatImageHelper(ns));
        return ns;
      case Tango_DEV_LONG:
        ns.setNumberHelper(new LongImageHelper(ns));
        return ns;
      case Tango_DEV_BOOLEAN:
        bi = new BooleanImage();
        return bi;
      default:
        System.out.println("Warning, AttributeFactory.getSpectrum(" + device.getName() + "/" + name +
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
