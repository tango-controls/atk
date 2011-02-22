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
 
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;

import java.util.*;

import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.AttrDataFormat;
import fr.esrf.Tango.AttrWriteType;
import fr.esrf.Tango.AttrQuality;
import fr.esrf.TangoApi.*;
import fr.esrf.TangoApi.events.*;
import fr.esrf.tangoatk.util.AtkTimer;

public abstract class AAttribute implements IAttribute, ITangoPeriodicListener, ITangoChangeListener
{
  // CONFIG and attr info
  transient protected AttributeInfoEx config;
  protected PropertyStorage propertyStorage = null;

  // Event Support
  protected EventSupport propChanges;

  protected Device device;
  protected DeviceAttribute attribute;
  protected String error;
  protected String nameSansDevice, name;
  transient protected AtkTimer timer;
  protected String state = OK;
  protected long timeStamp;
  protected boolean skippingRefresh = false;
  protected String alias;
  protected long refreshCount = 0;
  protected long changeCount = 0;
  protected long periodicCount = 0;
  protected DevFailed eventError = null; // Event subscription error

  private  boolean  hasEvents=false;
  
  protected boolean attPropertiesLoaded=false;


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
      AttributeInfoEx config = getDevice().getAttributeInfo(name);
      init(device, nameSansDevice, config, true);
      System.out.println("Done");
    } catch (Exception e) {
      e.printStackTrace();
      throw new java.io.IOException(e.getMessage());
    } // end of try-catch
  }


  protected void init(fr.esrf.tangoatk.core.Device d, String name, AttributeInfoEx config, boolean doEvent)
  {
      timer = timer.getInstance();
      propertyStorage = new PropertyStorage();

      setDevice(d);
      nameSansDevice = name;
      setName(device + "/" + name);
      setConfiguration(config);

      if (doEvent && d.doesEvent())
      {
	 subscribeAttributeEvent();
      }


      if (!hasEvents)
      {
	 try
	 {
	   readFirstValueFromNetwork();
	 }
	 catch (DevFailed e)
	 {
	   readAttError("Couldn't read from network", new ConnectionException(e));
	 } // end of try-catch
      }
  }


  private void subscribeAttributeEvent()
  {
      TangoEventsAdapter   evtAdapt = null;
      String[]             filters = new String[0];

      try
      {
	   evtAdapt = new TangoEventsAdapter(getDevice());
      }
      catch (DevFailed dfe)
      {
	   hasEvents = false;
	   return;
      }

      long t0 = System.currentTimeMillis();

      try
      {
          evtAdapt.addTangoChangeListener(this, nameSansDevice, filters);
	  hasEvents = true;
          trace(DeviceFactory.TRACE_SUCCESS,"AATtribute.subscribeAttributeChangeEvent("+name+") ok:",t0);
          return;
      }
      catch (DevFailed dfe)
      {
	  // Test if the reason is Abs change and rel change not defined, try to subscribe on periodic
	  if (dfe.errors[0].reason.equals("API_EventPropertiesNotSet"))
	  { 
	     try
	     {
        	evtAdapt.addTangoPeriodicListener(this, nameSansDevice, filters);
	        hasEvents = true;
        	trace(DeviceFactory.TRACE_SUCCESS,"AATtribute.subscribeAttributePeriodicEvent("+name+") ok:",t0);
		return;
	     }
	     catch (DevFailed dfe2)
	     {
		hasEvents = false;
        	eventError = dfe2;
        	trace(DeviceFactory.TRACE_FAIL,"AATtribute.subscribeAttributePeriodicEvent("+name+") failed:",t0);
		return;
	     }
	  }
	  hasEvents = false;
          eventError = dfe;
          trace(DeviceFactory.TRACE_FAIL,"AATtribute.subscribeAttributeEvent("+name+") failed:",t0);
	  return;
      }
  }

  public boolean hasEvents()
  {
      return hasEvents;
  }

  public long getRefreshCount()
  {
      return refreshCount;
  }

  public long getChangeCount()
  {
      return changeCount;
  }

  public long getPeriodicCount()
  {
      return periodicCount;
  }

  /**
   * Returns a string that describe the error which occurs during event subscription.
   */
  public String getSubscriptionError() {

    if(getDevice().doesEvent()==false) {
      return "ATK does not manage event for the parent device "+getDevice().getName()+" (IDL<3)";
    } else {
      if(eventError!=null) {
        return eventError.errors[0].desc;
      }
    }
    return "";

  }

  public DeviceAttribute getDeviceAttribute() {
    return attribute;
  }


  public String toString() {
    return name;
  }


  public EventSupport getPropChanges() {
    return propChanges;
  }

  public String getType() {
    StringBuffer retval = new StringBuffer();
    int dataType = config.data_type;
    switch (dataType) {
      case Tango_DEV_SHORT:
        retval.append("Short");
        break;
      case Tango_DEV_USHORT:
        retval.append("UShort");
        break;
      case Tango_DEV_STATE:
        retval.append("State");
        break;
      case Tango_DEV_BOOLEAN:
        retval.append("Boolean");
        break;
      case Tango_DEV_DOUBLE:
        retval.append("Double");
        break;
      case Tango_DEV_LONG:
        retval.append("Long");
        break;
      case Tango_DEV_ULONG:
        retval.append("ULong");
        break;
      case Tango_DEV_STRING:
        retval.append("String");
        break;
      case Tango_DEV_UCHAR:
        retval.append("UChar");
        break;
      case Tango_DEV_FLOAT:
        retval.append("Float");
        break;
    }


    AttrDataFormat format = config.data_format;
    switch (format.value()) {
      case AttrDataFormat._SCALAR:
        retval.append("Scalar");
        break;
      case AttrDataFormat._SPECTRUM:
        retval.append("Spectrum");
        break;
      case AttrDataFormat._IMAGE:
        retval.append("Image");
        break;
    }

    return retval.toString();
  }
  
    
    public int getTangoDataType()
    {
	int dataType = config.data_type;
	return dataType;
    }
    
    public AttrDataFormat getTangoDataFormat()
    {
    AttrDataFormat dataFormat = config.data_format;
    return dataFormat;
    }
  

  public void storeConfig() {

    Property property;
    if (getProperty("unit").isSpecified()) {
      config.unit = getProperty("unit").getStringValue();
    }

    if (getProperty("standard_unit").isSpecified()) {
      config.standard_unit =
        getProperty("standard_unit").getStringValue();
    }

    if (getProperty("format").isSpecified()) {
      config.format = getProperty("format").getStringValue();
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
      if(config.alarms!=null)
        config.alarms.min_alarm = property.getStringValue();
      else
        config.min_alarm = property.getStringValue();
    }
    property = getProperty("max_alarm");
    if (property != null && property.isSpecified()) {
      if(config.alarms!=null)
        config.alarms.max_alarm = property.getStringValue();
      else
        config.max_alarm = property.getStringValue();
    }
    property = getProperty("min_warning");
    if (property != null && property.isSpecified()) {
      if(config.alarms!=null)
        config.alarms.min_warning = property.getStringValue();
    }
    property = getProperty("max_warning");
    if (property != null && property.isSpecified()) {
      if(config.alarms!=null)
        config.alarms.max_warning = property.getStringValue();
    }
    property = getProperty("delta_t");
    if (property != null && property.isSpecified()) {
      if(config.alarms!=null)
        config.alarms.delta_t = property.getStringValue();
    }
    property = getProperty("delta_val");
    if (property != null && property.isSpecified()) {
      if(config.alarms!=null)
        config.alarms.delta_val = property.getStringValue();
    }

    try {
      device.storeInfo(config);
      setConfiguration(device.getAttributeInfo(getName()));
      getState();
    } catch (DevFailed d) {
      setAttError("Couldn't store config", new AttributeSetException(d));
    }

  }

  public String getFormat() {
    return getProperty("format").getStringValue();
  }


  public void addErrorListener(IErrorListener l) {
    propChanges.addErrorListener(l);
  }

  public void removeErrorListener(IErrorListener l) {
    propChanges.removeErrorListener(l);
  }


  public void addSetErrorListener(ISetErrorListener l) {
    propChanges.addSetErrorListener(l);

  }

  public void removeSetErrorListener(ISetErrorListener l) {
    propChanges.removeSetErrorListener(l);
  }


  public void addStateListener(IAttributeStateListener l) {
    propChanges.addAttributeStateListener(l);
  }

  public void removeStateListener(IAttributeStateListener l) {
    propChanges.removeAttributeStateListener(l);
  }

  public void setDevice(fr.esrf.tangoatk.core.Device d) {
    device = d;
  }

  public fr.esrf.tangoatk.core.Device getDevice() {
    return device;
  }

  protected void readAttError(String s, Throwable t) {
    propChanges.fireReadErrorEvent(this, t);
  }

  protected void setAttError(String s, Throwable t) {
    propChanges.fireSetErrorEvent(this, t);
  }

  public String getUnit() {
    return getProperty("unit").getStringValue();
  }

  public String getStandardUnit() {
    return getProperty("standard_unit").getStringValue();
  }

  public double getStandardUnitFactor()
  {
    double  factor;
    
    factor = ((Double) getProperty("standard_unit").getValue()).doubleValue();
    return factor;
  }

  
  public String getDisplayUnit() {
    return getProperty("display_unit").getStringValue();
  }

  public double getDisplayUnitFactor()
  {
    double  factor;
    
    factor = ((Double) getProperty("display_unit").getValue()).doubleValue();
    return factor;
  }

  public String getLabel() {
    return getProperty("label").getStringValue();
  }

  public void setLabel(String label) {
    propertyStorage.setProperty(this, "label", label, true);
  }

  public void setName(String s) {
    name = s;
    propertyStorage.setProperty(this, "name", s, false);
  }

  public void setDescription(String desc) {
    propertyStorage.setProperty(this, "description", desc, true);
  }

  public String getDescription() {
    return propertyStorage.getProperty("description").getStringValue();
  }

  public int getLevel() {
    return propertyStorage.getProperty("level").getIntValue();
  }


  public boolean isOperator() {
    Property prop;
    DisplayLevelProperty dlp;


    prop = getProperty("level");

    if (prop != null) {
      if (prop instanceof DisplayLevelProperty) {
        dlp = (DisplayLevelProperty) prop;
        return (dlp.isOperator());
      }
    }
    return false;
  }


  public boolean isExpert() {
    Property prop;
    DisplayLevelProperty dlp;


    prop = getProperty("level");

    if (prop != null) {
      if (prop instanceof DisplayLevelProperty) {
        dlp = (DisplayLevelProperty) prop;
        return (dlp.isExpert());
      }
    }
    return false;
  }


  protected void setConfiguration(AttributeInfoEx c) {
      timer.startTimer(Thread.currentThread());
      config = c;
      propertyStorage.setProperty(this, "unit", config.unit, false);

      propertyStorage.setProperty(this, "data_format", config.data_format,
		      false);
      propertyStorage.setProperty(this, "format", config.format, true);
      propertyStorage.setProperty(this, "data_type", new Integer(
		      config.data_type), false);
      propertyStorage.setProperty(this, "description", config.description,
		      true);
      propertyStorage.setProperty(this, "label", config.label, true);
      propertyStorage.setProperty(this, "writable", config.writable, false);
      propertyStorage.setProperty(this, "writable_attr_name",
		      config.writable_attr_name, false);

      if ("None".equals(config.writable_attr_name)) {
	      propertyStorage.getProperty("writable_attr_name").setSpecified(
			      false);
      }

      // setProperty("display_unit", config.display_unit, true);
      double disp_unit;
      try {
	      disp_unit = Double.parseDouble(config.display_unit);
	      if (disp_unit <= 0)
		      disp_unit = 1.0;
      } catch (NumberFormatException nfe) {
	      disp_unit = 1.0;
      }
      propertyStorage.setProperty(this, "display_unit",
		      new Double(disp_unit), false);

      propertyStorage.setProperty(this, "max_dim_x", new Integer(
		      config.max_dim_x), false);
      propertyStorage.setProperty(this, "max_dim_y", new Integer(
		      config.max_dim_y), false);
      propertyStorage.setProperty(this, "level", config.level, false);

      double std_unit;
      try {
	      std_unit = Double.parseDouble(config.standard_unit);
	      if (std_unit <= 0)
		      std_unit = 1.0;
      } catch (NumberFormatException nfe) {
	      std_unit = 1.0;
      }
      setProperty("standard_unit", new Double(std_unit), false);

      timer.endTimer(Thread.currentThread());
  }


  public static String[] getPropertyNames() {
    String[] names = {"name", "unit", "data_format", "data_type",
                      "description", "label", "writable",
                      "writable_attr_name", "display_unit",
                      "max_dim_x", "max_dim_y", "level",
                      "standard_unit", "min_value", "max_value",
                      "min_alarm", "max_alarm", "min_warning",
                      "max_warning","delta_t","delta_val"};
    return names;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }


  public String getName() {
    return name;
  }

  public String getNameSansDevice() {
    return nameSansDevice;
  }

  public boolean isWritable() {
    AttrWriteType wt = (AttrWriteType) getProperty("writable").getValue();
    return wt == AttrWriteType.WRITE ||
      wt == AttrWriteType.READ_WRITE;
  }

  private final void readFirstValueFromNetwork()
    throws fr.esrf.Tango.DevFailed {
    attribute = device.readAttribute(nameSansDevice);
    timeStamp = attribute.getTimeValMillisSec();
    setState();
  }

  protected final DeviceAttribute readValueFromNetwork()
    throws fr.esrf.Tango.DevFailed {
    attribute = device.readAttribute(nameSansDevice);
    timeStamp = attribute.getTimeValMillisSec();
    setState();
    return attribute;
  }

  /* Force value reading via the device , ignore polling buffer */
  protected final DeviceAttribute readDeviceValueFromNetwork()
    throws fr.esrf.Tango.DevFailed {
    attribute = device.readAttributeFromDevice(nameSansDevice);
    timeStamp = attribute.getTimeValMillisSec();
    setState();
    return attribute;
  }

  protected void setState() throws DevFailed
  {
    AttrQuality q = attribute.getQuality();
    if (AttrQuality._ATTR_VALID == q.value()) {
      setState(IAttribute.VALID);
      return;
    }
    if (AttrQuality._ATTR_INVALID == q.value()) {
      setState(IAttribute.INVALID);
      return;
    }
    if (AttrQuality._ATTR_ALARM == q.value()) {
      setState(IAttribute.ALARM);
      return;
    }
    if (AttrQuality._ATTR_WARNING == q.value()) {
      setState(IAttribute.WARNING);
      return;
    }
    if (AttrQuality._ATTR_CHANGING == q.value()) {
      setState(IAttribute.CHANGING);
      return;
    }
    setState(IAttribute.UNKNOWN);
    return;
  }

  protected void setState(DeviceAttribute  da) throws DevFailed
  {
    AttrQuality q = da.getQuality();
    if (AttrQuality._ATTR_VALID == q.value()) {
      setState(IAttribute.VALID);
      return;
    }
    if (AttrQuality._ATTR_INVALID == q.value()) {
      setState(IAttribute.INVALID);
      return;
    }
    if (AttrQuality._ATTR_ALARM == q.value()) {
      setState(IAttribute.ALARM);
      return;
    }
    if (AttrQuality._ATTR_WARNING == q.value()) {
      setState(IAttribute.WARNING);
      return;
    }
    if (AttrQuality._ATTR_CHANGING == q.value()) {
      setState(IAttribute.CHANGING);
      return;
    }
    setState(IAttribute.UNKNOWN);
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
      readAttError("Couldn't read from network",
        new ConnectionException(e));
      return IAttribute.UNKNOWN;
    } catch (Exception e) {
      readAttError("Couldn't read from network", e);
      return IAttribute.UNKNOWN;
    } // end of catch

    return state;
  }

  protected void writeAtt() throws DevFailed {
    device.writeAttribute(attribute);
  }

  public int getMaxXDimension() {
    return getProperty("max_dim_x").getIntValue();
  }

  public int getMaxYDimension() {
    return getProperty("max_dim_y").getIntValue();
  }

  public int getYDimension() {
    try {
      return attribute.getDimY();
    } catch (Exception e) {
      return 0;
    }
  }

  public int getHeight() {
    return getYDimension();
  }

  public int getXDimension() {
    try {
      return attribute.getDimX();
    } catch (Exception e) {
      return 0;
    }
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

  protected void checkDimensions(boolean o [][]) {
    if (o[0].length > getMaxXDimension())
      throw new IllegalArgumentException();
    if (o.length > getMaxYDimension())
      throw new IndexOutOfBoundsException();
  }


  public static String[] flatten(String[][] src) {
    int size = src.length * src[0].length;
    String[] dst = new String[size];

    for (int i = 0; i < src.length; i++)
      System.arraycopy(src[i], 0, dst, i * src.length, src.length);
    return dst;
  }


  protected final DeviceDataHistory[] readAttHistoryFromNetwork()
    throws fr.esrf.Tango.DevFailed {
    return (device.attribute_history(nameSansDevice));
  }

  private void trace(int level,String msg,long time) {
    DeviceFactory.getInstance().trace(level,msg,time);
  }

  public AtkEventListenerList getListenerList() {
    if (propChanges == null) return null;
    else return propChanges.getListenerList();
  }
  
  public void freeInternalData()
  {
     this.attribute = null;
  }

  /**
   * <code>getProperty</code> returns property with the name given in the
   * first parameter.
   * 
   * @param name a <code>String</code> value
   * @return an <code>Property</code> value
   */
  public Property getProperty(String name) {
	  return propertyStorage.getProperty(name);
  }

  /**
   * <code>getPropertyMap</code> returns a Map containing this entitys
   * properties.
   * 
   * @return a <code>Map</code> value
   */
  public Map getPropertyMap() {
	  return propertyStorage.getPropertyMap();
  }

  /**
   * <code>setProperty</code>
   * 
   * @param name
   *            a <code>String</code> value containing the name of the
   *            property
   * @param n
   *            a <code>Number</code> value containing the numeric value of
   *            the property
   */
  public void setProperty(String name, Number value) {
	  propertyStorage.setProperty(name, value);
  }

  /**
   * <code>setProperty</code>
   * 
   * @param name
   *            a <code>String</code> value containing the name of the
   *            property
   * @param n
   *            a <code>Number</code> value containing the value of the
   *            property
   * @param editable
   *            a <code>boolean</code> value which decides if the property
   *            is editable or not.
   */
  public void setProperty(String name, Number value, boolean editable) {
	  propertyStorage.setProperty(this, name, value, editable);
  }

  public PropertyStorage getPropertyStorage() {
	  return propertyStorage;
  }

  public void setPropertyStorage(PropertyStorage propertyStorage) {
	  this.propertyStorage = propertyStorage;
  }
    
  public boolean areAttPropertiesLoaded()
  {
      return attPropertiesLoaded;
  }
    
  public void loadAttProperties()
  {
      // In most cases this method is empty where in some attribute types
      // this method should be overriden as in DevStateScalar and DevStateSpectrum to perform the necessary tasks
      attPropertiesLoaded = true;
  }
    

}


