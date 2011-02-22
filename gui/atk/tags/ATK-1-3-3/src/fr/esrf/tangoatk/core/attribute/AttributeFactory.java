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
import org.apache.log4j.Logger;

/**
 * <code>AttributeFactory</code> is an extension of {@link AEntityFactory}
 * which takes care of instantiating Attribute objects. It is a Singleton,
 * so please use getInstance to instantiate the Factory.
 * @version $Revision$
 */
public class AttributeFactory extends AEntityFactory {

    static Logger log =
	ATKLogger.getLogger(AttributeFactory.class.getName());


    private DeviceFactory deviceFactory = DeviceFactory.getInstance();
    private static AttributeFactory instance;
    protected Map attributes = new HashMap();

    /**
     * Creates a new <code>AttributeFactory</code> instance. Do not use 
     * this.
     * @see #getInstance
     */
    protected AttributeFactory() { }


    public void clear() {
	deviceFactory.clear();
	attributes.clear();
    }
    
    /**
     * <code>getInstance</code> returns an instance of the AttributeFactory.
     * There will be only one AttributeFactory pr running instance of the JVM.
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
    protected List getWildCardEntities(String name, Device device)
    throws DevFailed {
	List list = new Vector();

	/*
	 * Tango_AllAttr is the magic name which signifies that we want
	 * all the attributes from a device. It's defined in the 
	 * interface fr.esrf.TangoDs.TangoConst
	 */
	name = Tango_AllAttr;
	String [] attributeNames = {name};

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

	    AAttribute attribute = (AAttribute)attributes.get(fqname);

	    if (attribute == null) {
		attribute = initAttribute(device, config[i]);
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
    protected IEntity getSingleEntity(String fqname, Device device)
    throws DevFailed {

	/*
	 * Check if the attribute has already been imported
	 */
	AAttribute attribute = (AAttribute)attributes.get(fqname);

	/*
	 * if so we return the old attribute and exit.
	 */
	if (attribute != null) return attribute;

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
	return initAttribute(device, config);
    }


    public boolean isAttribute(String fqname) {
	if (attributes.get(fqname) != null) {
	    return true;
	}
	
	Device d = null;

	try {
	    d = getDevice(extractDeviceName(fqname));
	} catch (Exception e) {
	    return false;
	}
	
	try {
	    d.getAttributeInfo(extractEntityName(fqname));
	} catch (Exception e) {
	    return false;
	} // end of try-catch
	return true;
    }

    /**
     * <code>initAttribute</code> ask getAttributeOfType for an
     * AAttribute of the type given in the AttributeInfo passed as 
     * parameter, and then calls <code>init()</code> on the attribute
     *
     * @param device a <code>Device</code> value
     * @param config an <code>AttributeInfo</code> value
     * @return an <code>AAttribute</code> value
     */
    protected AAttribute initAttribute(Device device,
				       AttributeInfo config) {
	AAttribute attribute = getAttributeOfType(config);
	attribute.init(device, config.name, config);
	attributes.put(attribute.getName(), attribute);

	return attribute;
    }

    /**
     * <code>getAttributeOfType</code> figures out what type of
     * attribute is demanded, and deletates the work of instantiating the
     * attribute to getScalar, getSpectrum, or getImage. The typeinference
     * is based on the value of AttributeConfig.data_format.
     * @param device a <code>Device</code> value
     * @param name a <code>String</code> value, the name of the attribute
     * @param ac an <code>AttributeConfig</code> value
     * @return an <code>AAttribute</code> value
     * @throws IllegalArgumentException if the format is unknown.
     */
    protected AAttribute getAttributeOfType(AttributeInfo config) {
	AttrDataFormat format = config.data_format;
	String name = config.name;
	
	switch (format.value()) {
	case AttrDataFormat._SCALAR:
	    return getScalar(config);
	case AttrDataFormat._SPECTRUM:
	    return getSpectrum(config);
	case AttrDataFormat._IMAGE:
	    return getImage(config);
	default:
	    throw new IllegalArgumentException("Unknown dataformat for " +
					       name + " (" + format + ")" );
	}
    }
    
    /**
     * <code>getScalar</code> figures out what kind of scalar is to be
     * created. This is done by looking at AttributeInfo.data_type
     *
     * @param device a <code>Device</code> value
     * @param name a <code>String</code> value
     * @param config an <code>AttributeInfo</code> value
     * @return an <code>AAttribute</code> value being instanceof either
     * ShortScalar, DoubleScalar, LongScalar, or StringScalar.
     * @throws IllegalArgumentException if the type is unknown.
     */
    protected AAttribute getScalar(AttributeInfo config) {
	String name = config.name;
	int dataType = config.data_type;

	if (dataType == Tango_DEV_STRING) {
	    return new StringScalar();
	}

	NumberScalar ns = new NumberScalar();

	switch (dataType) {
	case	Tango_DEV_SHORT:
	    ns.setNumberHelper(new ShortScalarHelper(ns));
	    break;

	case	Tango_DEV_DOUBLE:
	    ns.setNumberHelper(new DoubleScalarHelper(ns));
	    break;
	case	Tango_DEV_LONG:
	    ns.setNumberHelper(new LongScalarHelper(ns));
	    break;
	default:
	    throw new IllegalArgumentException("Unknown datatype for " +
					       name + " (" + dataType + ")" );
	}
	return ns;
    }

    /**
     * <code>getSpectrum</code> figures out what kind of spectrum is to be
     * created. This is done by looking at AttributeInfo.data_type
     *
     * @param device a <code>Device</code> value
     * @param name a <code>String</code> value
     * @param config an <code>AttributeInfo</code> value
     * @return an <code>AAttribute</code> value being instanceof either
     * ShortSpectrum, DoubleSpectrum, or LongSpectrum
     * @throws IllegalArgumentException if the type is unknown.
     */
    protected AAttribute getSpectrum(AttributeInfo config) {
	String name = config.name;
	int dataType = config.data_type;
	NumberSpectrum ns = new NumberSpectrum();
	
	switch (dataType) {
	case	Tango_DEV_SHORT:
	    ns.setNumberHelper(new ShortSpectrumHelper(ns));
	    return ns;
	case	Tango_DEV_DOUBLE:
	    ns.setNumberHelper(new DoubleSpectrumHelper(ns));
	    return ns;
	case	Tango_DEV_LONG:
	    ns.setNumberHelper(new LongSpectrumHelper(ns));
	    return ns;
 	case	Tango_DEV_STRING:
	    log.warn("String spectrum not supported! " + name);
	    return new InvalidAttribute();
	default:
	    throw new IllegalArgumentException("Unknown datatype for " +
					       name + " (" + dataType + ")" );
	}
    }

    /**
     * <code>getSpectrum</code> figures out what kind of spectrum is to be
     * created. This is done by looking at AttributeInfo.data_type
     *
     * @param device a <code>Device</code> value
     * @param name a <code>String</code> value
     * @param config an <code>AttributeInfo</code> value
     * @return an <code>AAttribute</code> value being instanceof either
     * ShortSpectrum, DoubleSpectrum, or LongSpectrum
     * @throws IllegalArgumentException if the type is unknown.
     */
    protected AAttribute getImage(AttributeInfo config) {
	String name = config.name;
	int dataType = config.data_type;
	NumberImage ns = new NumberImage();
	switch (dataType) {
	case	Tango_DEV_SHORT:
	    ns.setNumberHelper(new ShortImageHelper(ns));
	    return ns;
	case	Tango_DEV_DOUBLE:
	    ns.setNumberHelper(new DoubleImageHelper(ns));
	    return ns;
	case	Tango_DEV_LONG:
	    ns.setNumberHelper(new LongImageHelper(ns));
	    return ns;
 	case	Tango_DEV_STRING:
	    log.warn("String image not supported! " + name);
	    return new InvalidAttribute();
	default:
	    throw new IllegalArgumentException("Unknown datatype for " +
					       name + " (" + dataType + ")" );
	}
    }

    public String getVersion() {
	return "$Id$";
    }

}	
