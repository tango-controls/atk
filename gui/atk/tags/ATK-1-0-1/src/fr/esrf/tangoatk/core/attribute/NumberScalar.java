// File:          NumberAttribute.java
// Created:       2001-10-08 16:35:01, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-10 15:40:49, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;
import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;
import org.apache.log4j.Logger;

public class NumberScalar extends NumberSpectrum
    implements INumberScalar {
    double scalarValue;
    ANumberScalarHelper numberHelper;

    static Logger log =
	ATKLogger.getLogger(NumberScalar.class.getName());



    public boolean isWriteable() {
	return super.isWritable();
    }

    public void addSpectrumListener(ISpectrumListener l) {
	numberHelper.addSpectrumListener(l);
	addStateListener(l);
    }

    public void removeSpectrumListener(ISpectrumListener l) {
	numberHelper.removeSpectrumListener(l);
	removeStateListener(l);
    }

    public void addImageListener(IImageListener l) {
	numberHelper.addImageListener(l);
	addStateListener(l);
    }
    
    public void removeImageListener(IImageListener l) {
	numberHelper.removeImageListener(l);
	removeStateListener(l);
    }
    
    public double [] getStandardSpectrumValue() {
	return null;

    }

    public IScalarAttribute getWritableAttribute() {
	return null;
    }

    public IScalarAttribute getReadableAttribute() {
	return null;
    }

    public int getXDimension() {
	return 1;
    }

    public int getMaxXDimension() {
	return 1;
    }

    public double[][] getStandardValue() {
	return null;
    }

    public double[][] getValue() {
	double [][]d = new double[1][1];
	d[0][0] = getNumberScalarValue();
	return d;
    }

    public void setNumberHelper(ANumberScalarHelper helper) {
	numberHelper = helper;
    }
    
    public void setConfiguration(AttributeInfo c) {
	super.setConfiguration(c);
	try {
	    setMinValue(new Double(config.min_value).doubleValue(), true);
	} catch (NumberFormatException e) {
	    setMinValue(Double.NaN, true);
	    getProperty("min_value").setSpecified(false); 
	} // end of try-catch
	log.debug("Done min_value...");	
	try {
	    setMaxValue(new Double(config.max_value).doubleValue(), true);
	} catch (NumberFormatException e) {
	    setMaxValue(Double.NaN, true);
	    getProperty("max_value").setSpecified(false); 
	} // end of try-catch
	log.debug("Done max_value...");	
	try {
	    setMinAlarm(new Double(config.min_alarm).doubleValue(), true);
	} catch (NumberFormatException e) {
	    setMinAlarm(Double.NaN, true);
	    getProperty("min_alarm").setSpecified(false); 
	} // end of try-catch
	log.debug("Done min_alarm...");	
	try {
	    setMaxAlarm(new Double(config.max_alarm).doubleValue(), true);
	} catch (NumberFormatException e) {
	    setMaxAlarm(Double.NaN, true);
	    getProperty("max_alarm").setSpecified(false); 
	} // end of try-catch
	log.debug("Done max_alarm...");
    }

    public void setMinValue(double d) {
	numberHelper.setMinValue(d);
    }
    
    public void setMaxValue(double d) {
	numberHelper.setMaxValue(d);
    }

    public void setMinAlarm(double d) {
	numberHelper.setMinAlarm(d);
    }
    
    public void setMaxAlarm(double d) {
	numberHelper.setMaxAlarm(d);
    }

    public void setMinValue(double d, boolean writable) {
	numberHelper.setMinValue(d, writable);
    }
    
    public void setMaxValue(double d, boolean writable) {
	numberHelper.setMaxValue(d, writable);
    }

    public void setMinAlarm(double d, boolean writable) {
	numberHelper.setMinAlarm(d, writable);
    }
    
    public void setMaxAlarm(double d, boolean writable) {
	numberHelper.setMaxAlarm(d, writable);
    }
    

    public void addNumberScalarListener(INumberScalarListener l) {
	numberHelper.addNumberScalarListener(l);
	addStateListener(l);
    }

    public void removeNumberScalarListener(INumberScalarListener l) {
	log.debug("removeNumberScalarListener: removing " + l);
	log.debug("removeNumberScalarListener" + numberHelper);
	numberHelper.removeNumberScalarListener(l);
	removeStateListener(l);
	log.debug("removeNumberScalarListener: done");
    }

    public double[][] getNumberValue() {
	double[][] retval = new double[1][1];
	retval[0][0] = getNumberScalarValue();
	return retval;
    }

    public double[] getSpectrumValue() {
	double[] retval = new double[1];
	retval[0] = getNumberScalarValue();
	return retval;
    }

    public Number getNumber() {
	try {
	    return new Double(getNumberScalarValue());	     
	} catch (Exception d) {
	    setError("Couldn't read from network",
		     new ConnectionException(d));
	} // end of try-catch
	return new Double(Double.NaN);

    }

    public void setNumber(Number n) throws IllegalArgumentException {
	double d = n.doubleValue();
// 	if (!Double.isNaN(getMaxValue()) && !(d < getMaxValue()) ||	
// 	    !Double.isNaN(getMinValue())&& !(d > getMinValue())) {
// 	    throw new IllegalArgumentException();
// 	}
	

	setValue(d);
    }
    
    public void setValue(double d[])  {
	setValue(d[0]);
    }

    public void setValue(double d[][])  {
	setValue(d[0][0]);
    }

    protected String scalarExtract() {
	return new Double(getNumberScalarValue()).toString();
    }

    public final void refresh() {

	if (skippingRefresh) return;

	try {
 	    scalarValue = numberHelper.
		getNumberScalarValue(readValueFromNetwork());
 	    numberHelper.fireScalarValueChanged(scalarValue, timeStamp);
	} catch (DevFailed e) {
	    readException.setError(e);
	    setError(e.getMessage(), readException);
	    scalarValue = Double.NaN;
	} catch (Exception e) {
	    setError(e.getMessage(), e);
	    scalarValue = Double.NaN;
	} // end of catch
    }

    public double getMinValue() {
	return getNumberProperty("min_value");
    }

    public double getMaxValue() {
	return getNumberProperty("max_value");
    }

    public double getMinAlarm() {
	return getNumberProperty("min_alarm");
    }

    public double getMaxAlarm() {
	return getNumberProperty("max_alarm");
    }

    protected double getNumberProperty(String s) {
	NumberProperty p =
	    (NumberProperty)getProperty(s);
	if (p != null && p.isSpecified()) 
	    return ((Number)p.getValue()).doubleValue();

	return Double.NaN;
    }

    
    public void setValue(double d) {
	try {
	    insert(d);
	    store();
	    refresh();
	} catch (DevFailed e) {
	    setError("Couldn't set value",  new AttributeSetException(e));
	}
    }
    
    protected fr.esrf.TangoApi.DeviceAttribute scalarInsert(String s)
	throws fr.esrf.Tango.DevFailed {
	log.info("insert(" + s + ")");
	insert(s);
	log.info("done");
	return attribute;
    }
    
    public double getStandardNumberScalarValue() {
	return getNumberScalarValue() * getStandardUnit();
    }

    protected void insert(double[] d) {
	 insert(d[0]);
    }

    protected void insert(double d) {
	numberHelper.insert(d);
    }

    protected void insert(String s) {
	insert(Double.parseDouble(s));
    }

    public double getNumberScalarValue() {
	return scalarValue;
    }

    public String getVersion() {
	return "$Id$";
    }

    private void readObject(java.io.ObjectInputStream in)
	throws java.io.IOException, ClassNotFoundException {
	System.out.print("Loading attribute ");
	in.defaultReadObject();
	serializeInit();
    }


}
