// File:          AbstractAttribute.java
// Created:       2001-09-24 13:59:22, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-15 17:9:36, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;
import java.beans.*;
import java.util.*;

import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.AttrDataFormat;
import fr.esrf.Tango.AttrWriteType;
import fr.esrf.Tango.TimeVal;
import fr.esrf.Tango.AttributeValue;
import fr.esrf.Tango.AttrQuality;
import fr.esrf.TangoDs.*;
import fr.esrf.TangoApi.*;
import fr.esrf.tangoatk.core.util.AtkTimer;
import org.apache.log4j.Logger;

public abstract class AAttribute implements IAttribute {
    transient static Logger log =
	ATKLogger.getLogger(AAttribute.class.getName());
    transient protected Logger attributeLog;
    protected Device device;
    transient protected AttributeInfo config;
    protected DeviceAttribute attribute;
    protected String error;
    protected EventSupport propChanges;
    protected Map propertyMap;
    protected String nameSansDevice, name;
    transient protected AtkTimer timer;
    protected AttributeReadException readException =
	new AttributeReadException();
    protected String state = OK;
    protected long timeStamp;
    protected boolean skippingRefresh = false;

    
    private static String VERSION = "$Id$";

    public String getVersion() {
	return VERSION;
    }
    
    protected AAttribute() {

	if (propChanges == null) {
	    propChanges = new EventSupport();
	}
    }

    protected void serializeInit() throws java.io.IOException {
	System.out.print(name + "...");
	timer = AtkTimer.getInstance();
	try {
	    AttributeInfo config = getDevice().getAttributeInfo(name);
	    init(device, nameSansDevice, config);
	    System.out.println("Done");
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new java.io.IOException(e.getMessage());
	} // end of try-catch
    }	

	
    protected void init (fr.esrf.tangoatk.core.Device d, String name,
			 AttributeInfo config) {
	timer = timer.getInstance();
	propertyMap = new HashMap();
	log.info("Initializing propertyChangeSupport");

	setDevice(d);
	nameSansDevice = name;
	setName(device + "/" + name);
	setConfiguration(config);
	attributeLog = ATKLogger.getLogger(getName().replace('/', '.'));
	log.debug("created attributeLogger");
	try {
	    readFirstValueFromNetwork();	     
	} catch (DevFailed e) {
	    setError("Couldn't read from network", new ConnectionException(e));
	} // end of try-catch
	


	attributeLog.info("Initialized.");
	//	refresh();
	log.debug("done initializing " + getName());
    }


    public DeviceAttribute getDeviceAttribute() {
	return attribute;
    }
    
    public void setProperty(String name, Number value) {
	NumberProperty p = (NumberProperty)propertyMap.get(name);
	if (p == null) {
	    log.warn("setting unknown attribute: " + name);
	    timer.endTimer(Thread.currentThread());
	    return;
	    
	} // end of if ()
	
	p.setValue(value);
    }

    protected void setProperty(String name, Object value, boolean editable) {

	propertyMap.put(name, new Property(this, name,
					   value, editable));
    }

    public void setProperty(String name, Number value, boolean editable) {
	Property p = (Property)propertyMap.get(name);
	if (p == null) 
	    propertyMap.put(name,new NumberProperty(this,
						    name,
						    value,
						    editable));
	else 
	    p.setValue(value);
    }

    protected void setProperty(String name, fr.esrf.Tango.AttrWriteType value,
			       boolean editable) {
	Property p = (Property)propertyMap.get(name);
	if (p == null)
	    propertyMap.put(name, new WritableProperty(this,
						       name,
						       value,
						       editable));
	else 
	    p.setValue(value);
    }	

    protected void setProperty(String name, fr.esrf.Tango.AttrDataFormat value,
			       boolean editable) {
	timer.startTimer(Thread.currentThread());
	Property p = (Property)propertyMap.get(name);
	if (p == null) 	
	    propertyMap.put(name, new FormatProperty(this,
						     name,
						     value,
						     editable));
	else 
	    p.setValue(value);
	timer.endTimer(Thread.currentThread());
    }	

    protected void setProperty(String name, fr.esrf.Tango.DispLevel value,
			       boolean editable) {
	Property p = (Property)propertyMap.get(name);
	if (p == null) {
	    propertyMap.put(name, new DisplayLevelProperty(this,
							   name,
							   value,
							   editable));
	} else {
	    p.setValue(value);
	} // end of else
    }
	
    protected void setProperty(String name, String value,
			       boolean editable) {
	Property p = (Property)propertyMap.get(name);
	if (p == null) 
	    propertyMap.put(name, new StringProperty(this,
						     name,
						     value,
						     editable));
	else
	    p.setValue(value);
    }	

    public void setProperty(String name, Object value) {
	Property p = (Property)propertyMap.get(name);
	p.setValue(value);
    }
			
    public String toString() {
	return nameSansDevice;
    }

    public Map getPropertyMap() {
	return propertyMap;
    }

    public EventSupport getPropChanges() {
	return propChanges;
    }
    
    public String getType() {
	StringBuffer retval = new StringBuffer();
	int dataType = config.data_type;
	switch (dataType) {
	case	Tango_DEV_SHORT:
	    retval.append("Short");break;
	case	Tango_DEV_DOUBLE:
	    retval.append("Double");break;
	case	Tango_DEV_LONG:
	    retval.append("Long");break;
	case	Tango_DEV_STRING:
	    retval.append("String");break;
	}

	AttrDataFormat format = config.data_format;
	switch (format.value()) {
	case AttrDataFormat._SCALAR:
	    retval.append("Scalar");break;
	case AttrDataFormat._SPECTRUM:
	    retval.append("Spectrum");break;
	case AttrDataFormat._IMAGE:
	    retval.append("Image");break;
	}

	return retval.toString();
    }
    
    public void storeConfig() {
	timer.startTimer(Thread.currentThread());
	Property property;
	if (getProperty("unit").isSpecified()) {
	    config.unit = getProperty("unit").getStringValue();	    	
	} 

	if (getProperty("standard_unit").isSpecified()) {
	    config.standard_unit =
		getProperty("standard_unit").getStringValue();
	} 

	if (getProperty("data_format").isSpecified()) {
	    config.data_format =
		(AttrDataFormat)getProperty("data_format").getValue();
	} 

	if (getProperty("data_type").isSpecified()) {
	    config.data_type = getProperty("data_type").getIntValue();
	} 

	if (getProperty("max_dim_x").isSpecified()) {
	    config.max_dim_x = getProperty("max_dim_x").getIntValue();
	} 

	if (getProperty("max_dim_y").isSpecified()) {
	    config.max_dim_y = getProperty("max_dim_y").getIntValue();
	} 

	if (getProperty("description").isSpecified()) {
	    config.description = getProperty("description").getStringValue();
	} 

	if (getProperty("label").isSpecified()) {
	    config.label = getProperty("label").getStringValue();
	} 

	if (getProperty("display_unit").isSpecified()) {
	    config.display_unit =
		getProperty("display_unit").getStringValue();
	} 

	property = getProperty("min_value");
	if (property != null && property.isSpecified()) {
	    config.min_value = property.getStringValue();
	} 
	property = getProperty("max_value");
	if (property != null && property.isSpecified()) {
	    config.max_value = property.getStringValue();
	} 
	property = getProperty("min_alarm");
	if (property != null && property.isSpecified()) {
	    config.min_alarm = property.getStringValue();
	} 
	property = getProperty("max_alarm");
	if (property != null && property.isSpecified()) {
	    config.max_alarm = property.getStringValue();
	} 

	try {
	    device.storeInfo(config);
	    setConfiguration(device.getAttributeInfo(getName()));
	    getState();
	} catch (DevFailed d) {
	    setError("Couldn't store config", new AttributeSetException(d));
	}
	timer.endTimer(Thread.currentThread());
    }
    
    public String getFormat() {
	return getProperty("format").getStringValue();
    }


    public void addErrorListener (IErrorListener l) {
	propChanges.addErrorListener(l);

	if (device == null) return;

	device.addErrorListener(l);
    }

    public void removeErrorListener(IErrorListener l) {
	propChanges.removeErrorListener(l);
	device.removeErrorListener(l);
    }

    public void addStateListener (IAttributeStateListener l) {
	propChanges.addAttributeStateListener(l);
    }

    public void removeStateListener(IAttributeStateListener l) {
	propChanges.removeAttributeStateListener(l);
    }

    public void setDevice(fr.esrf.tangoatk.core.Device d) {
	log.debug("Setting device to " + d);
	device = d;
    }

    public fr.esrf.tangoatk.core.Device getDevice() {
	return device;
    }

    protected void setError(String s, Throwable t) {
	try {
	    attributeLog.error(s, t);	     
	} catch (Exception e) {
	    ;
	} // end of try-catch
	
	propChanges.fireErrorEvent(this, t);
    }
    
    public String getUnit() {
	return getProperty("unit").getStringValue();
    }

    void refreshProperties() {
	Iterator i = getPropertyMap().values().iterator();
	while (i.hasNext()) {
	    Property prop = (Property)i.next();
	    prop.refresh();
	} // end of while ()
    }
    
    public Property getProperty(String s) {

	timer.startTimer(Thread.currentThread());
	Property p = null;
	if (propertyMap != null) {
	    p = (Property)propertyMap.get(s); 
	} 
	timer.endTimer(Thread.currentThread());
	return p;
    }

    
    public double getStandardUnit() {
	return new Double(getProperty("standard_unit").getStringValue()).doubleValue();
    }

    public String getDisplayUnit() {
	return getProperty("display_unit").getStringValue();
    }

    public String getLabel() {
	return getProperty("label").getStringValue();
    }

    public void setLabel(String label) {
	setProperty("label", label, true);
    }
    
    public void setName(String s) {
	log.info("Setting name to " + s);
	name = s;
	setProperty("name", s, false);
    }

    public void setDescription(String desc) {
	setProperty("description", desc, true);
    }

    public String getDescription() {
	return getProperty("description").getStringValue();
    }

    public int getLevel() {
	return getProperty("level").getIntValue();
    }

    protected void setConfiguration(AttributeInfo c) {
	timer.startTimer(Thread.currentThread());
	log.debug("entering setConfiguration " + c);
	config = c;
	setProperty("unit", config.unit, false);

	setProperty("data_format",   config.data_format, false);
	setProperty("format",        config.format, false);
	setProperty("data_type",     new Integer(config.data_type), false);
	setProperty("description",   config.description, true);
	setProperty("label",         config.label, true);
	setProperty("writable",      config.writable, false);
	setProperty("writable_attr_name",
		    config.writable_attr_name, false);


	if ("None".equals(config.writable_attr_name)) {
	    getProperty("writable_attr_name").setSpecified(false);
	} 
	
	setProperty("display_unit",  config.display_unit, true);
	setProperty("max_dim_x",     new Integer(config.max_dim_x), false);
	setProperty("max_dim_y",     new Integer(config.max_dim_y), false);

	setProperty("level", config.level, false);

	try {
	    setProperty("standard_unit",
			new Double(config.standard_unit),
			false);	     
	} catch (NumberFormatException e) {
	    setProperty("standard_unit", new Double(Double.NaN),
			false);
	    getProperty("standard_unit").setSpecified(false); 
	} // end of try-catch
	log.debug("Done standard_unit...");	
	//	refresh();
	log.debug("done setConfiguration");
	timer.endTimer(Thread.currentThread());
    }

    public String getName() {
	return name;
    }

    public String getNameSansDevice() {
	return nameSansDevice;
    }
    
    public boolean isWritable() {
	AttrWriteType wt = (AttrWriteType)getProperty("writable").getValue();
	return wt == AttrWriteType.WRITE ||
	    wt == AttrWriteType.READ_WRITE;
    }

    protected void readValueFromNetwork(int i) throws DevFailed {
	attribute = device.quickReadAttribute(nameSansDevice);
	timeStamp  = attribute.getTimeValMillisSec();
	
	setState();
    }

    private final void readFirstValueFromNetwork() throws DevFailed  {
	attribute = new DeviceAttribute(device.readAttributeValue(nameSansDevice));
	timeStamp  = attribute.getTimeValMillisSec();
	setState();
    }
    
    protected final DeviceAttribute readValueFromNetwork()
	throws fr.esrf.Tango.DevFailed {
	attribute.setAttributeValue
	    (device.readAttributeValue(nameSansDevice));
	timeStamp  = attribute.getTimeValMillisSec();
	setState();
	return attribute;
    }

    protected void setState() {
	AttrQuality q = attribute.getQuality();
	if (AttrQuality._ATTR_VALID   == q.value() ) {
	    setState(VALID);
	    return;
	}
	if (AttrQuality._ATTR_INVALID == q.value()) {
	    setState(INVALID);
	    return;
	}
	if (AttrQuality._ATTR_ALARM   == q.value()) {
	    setState(ALARM);
	    return;
	}
	setState(UNKNOWN);
	return;
    }
	
    public DeviceAttribute getAttribute() {
	return attribute;
    }
       
    protected void setState(String s) {
	state = s;
	propChanges.fireAttributeStateEvent(this, s);
    }

    public String getState() {
	try {
	    readValueFromNetwork();	     
	} catch (DevFailed e) {
	    setError("Couldn't read from network",
		     new ConnectionException(e));
	    return "UNKNOWN";
	} catch (Exception e) {
	    setError("Couldn't read from network", e);
	    return "UNKNOWN";
	} // end of catch
	
	

	return state;
    }
    
    protected void store() throws DevFailed {
	device.writeAttribute(attribute);
    }

    String [][] oldVal;
	    
    public void addImageListener(IImageListener l) {
	propChanges.addImageListener(l);
    }

    public void removeImageListener(IImageListener l) {
	propChanges.removeImageListener(l);
    }

    public int getMaxXDimension() {
	return getProperty("max_dim_x").getIntValue();
    }
    
    public int getMaxYDimension() {
	return getProperty("max_dim_y").getIntValue();
    }

    public int getYDimension() {
	return attribute.getDimY();
    }
    public int getHeight() {
	return getYDimension();
    }

    public int getXDimension() {
	return attribute.getDimX();
    }

    public int getWidth() {
	return getYDimension();
    }

    public void setSkippingRefresh(boolean b) {
	skippingRefresh = b;
    }

    public boolean isSkippingRefresh() {
	return skippingRefresh;
    }

    
    protected void checkDimensions(Object o [][]) {
	if (o.length > getMaxXDimension())
	    throw new IllegalArgumentException();
	if (o[0].length > getMaxYDimension())
	    throw new IndexOutOfBoundsException();
    }

    protected void checkDimensions(double o [][]) {
	if (o[0].length > getMaxXDimension())
	    throw new IllegalArgumentException();
	if (o.length > getMaxYDimension())
	    throw new IndexOutOfBoundsException();
    }


    public static String[] flatten (String [][] src) {
	int size = src.length * src[0].length;
	String[] dst= new String[size];

	for (int i = 0; i < src.length; i++) 
	    System.arraycopy(src[i], 0, dst, i * src.length, src.length);
	return dst;
    }


}
