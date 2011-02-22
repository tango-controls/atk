// File:          NumberArrayAttribute.java
// Created:       2001-10-10 10:41:58, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-10 15:38:39, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;
import fr.esrf.tangoatk.core.*;
import org.apache.log4j.Logger;
import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import java.beans.*;

public class NumberSpectrum extends NumberImage
    implements INumberSpectrum {
    double [] spectrumValue;

    ANumberSpectrumHelper numberHelper;
    static Logger log =
	ATKLogger.getLogger(NumberSpectrum.class.getName());

    public void init(fr.esrf.tangoatk.core.Device d, String name,
			AttributeInfo config) {
	super.init(d, name, config);
    }

    public double [][] getStandardValue() {
	return numberHelper.getStandardNumberValue();
    }

    
    public void setNumberHelper(ANumberSpectrumHelper helper) {
	numberHelper = helper;
    }

    public void addImageListener(IImageListener l) {
	numberHelper.addImageListener(l);
    }

    public void removeImageListener(IImageListener l) {
	numberHelper.removeImageListener(l);
    }
    
    public void addSpectrumListener(ISpectrumListener l) {
	propChanges.addSpectrumListener(l);
    }

    public void removeSpectrumListener(ISpectrumListener l) {
	propChanges.removeSpectrumListener(l);
    }

    
    public void setValue(double[][] d) throws AttributeSetException {
	setValue(d[0]);
    }
    
    public double[][] getValue() {
	double[][] val = new double[1][];
	val[0] = getSpectrumValue();
	return val;
    }

    public void setValue(double [] d) throws AttributeSetException {
	try {
	    insert(d);
	    store();
	    numberHelper.fireSpectrumValueChanged(d,
						  System.currentTimeMillis());
	} catch (DevFailed df) {
	    throw new AttributeSetException(df);
	}
    }

    public double[] getStandardSpectrumValue() {
	double[] retval = getSpectrumValue();
	for (int i = 0; i < retval.length; i++)
	    retval[i] *= getStandardUnit();

	return retval;
    }
    
    protected void checkDimensions(double [] o) {
	if (o.length > getMaxXDimension()) {
	    throw new IllegalStateException();
	}
    }


    protected void insert(double [] d) {
	checkDimensions(d);
	numberHelper.insert(d);
    }

    public void refresh() {

	if (skippingRefresh) return;

	try {
 	    spectrumValue = numberHelper.
		getNumberSpectrumValue(readValueFromNetwork());
 	    numberHelper.fireSpectrumValueChanged(spectrumValue, timeStamp);
	} catch (DevFailed e) {
	    readException.setError(e);
	    setError(e.getMessage(), readException);
	    spectrumValue = null;;
	} catch (Exception e) {
	    setError(e.getMessage(), e);
	    spectrumValue = null;
	} // end of catch
	//	refreshProperties();
    }

    public double[] getSpectrumValue() {
	return spectrumValue;
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

    public int getYDimension() {
	return 1;
    }

    public int getMaxYDimension() {
	return 1;
    }
	

}
