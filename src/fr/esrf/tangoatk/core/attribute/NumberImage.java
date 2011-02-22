// File:          ShortSpectrum.java
// Created:       2001-10-10 13:50:57, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-16 10:31:19, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;
import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;
import org.apache.log4j.Category;

public class NumberImage extends AAttribute
    implements INumberImage {
    protected NumberAttributeHelper numberHelper;
    double [][] imageValue;
    public void init(fr.esrf.tangoatk.core.Device d, String name,
			AttributeInfo config) {
	super.init(d, name, config);
    }

    public void setNumberHelper(ANumberImageHelper helper) {
	numberHelper = helper;
    }
    
    public void addNumberImageListener(IImageListener l) {
	numberHelper.addImageListener(l);
    }

    public void removeNumberImageListener(IImageListener l) {
	numberHelper.removeImageListener(l);
    }

    protected void insert(String[][] s) {
	checkDimensions(s);
	insert(NumberAttributeHelper.flatten2double(s));
    }

    public void refresh() {

	if (skippingRefresh) return;
	
	try {
	    imageValue = numberHelper.
		getNumberImageValue(readValueFromNetwork());
	    numberHelper.fireImageValueChanged(imageValue, timeStamp);
	} catch (DevFailed e) {
	    readException.setError(e);
	    setError(e.getMessage(), readException);
	    imageValue = null;
	} catch (Exception e) {
	    setError(e.getMessage(), e);
	    imageValue = null;
	}
	//	refreshProperties();
    }



    public void setValue(double[][] d) throws AttributeSetException {
	try {
	    checkDimensions(d);
	    insert(NumberAttributeHelper.flatten(d));
	    store();
	    numberHelper.fireImageValueChanged(d, System.currentTimeMillis());
	} catch (DevFailed df) {
	    throw new AttributeSetException(df);
	}
    }

    
    protected double getNumberProperty(String s) {
	NumberProperty p =
	    (NumberProperty)getProperty(s);
	if (p != null && p.isSpecified()) 
	    return ((Number)p.getValue()).doubleValue();

	return Double.NaN;
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

    public double getStandardUnit() {
	NumberProperty p =
	    (NumberProperty)getProperty("standard_unit");
	if (p.isSpecified()) 
	    return ((Number)p.getValue()).doubleValue();

	return Double.NaN;
    }

    public double [][] getStandardValue() {
	double[][] retval = getValue();
	for (int i = 0; i < retval.length; i++)
	    for (int j = 0; j < retval.length; j++) 
		retval[i][j] *= getStandardUnit();
	
	return retval;
    }

    void insert(double [] d) {
	numberHelper.insert(d);
    }

    public String [][] extract() throws DevFailed {
	return numberHelper.getImageValue(readValueFromNetwork());
	
    }
    
    public double[][] getValue() {
	return imageValue;
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
