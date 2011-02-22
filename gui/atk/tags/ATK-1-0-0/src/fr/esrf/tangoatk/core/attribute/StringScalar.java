// File:          StringAttribute.java
// Created:       2001-09-24 13:24:05, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-10 15:33:21, assum>
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

public class StringScalar extends AAttribute
    implements IStringScalar {
    static Logger log =
	ATKLogger.getLogger(StringScalar.class.getName());

    ANumberScalarHelper numberHelper;
    String oldVal = null;

    public StringScalar() {
	numberHelper = new StringAttributeHelper(this);
    }

    public String getString() {
	return getStringValue();
    }

    public void setValue(String s) {
	try {
	    attributeLog.info("setValue(" + s + ")");
	    attribute.insert(s);
	    store();
	    fireValueChanged(s);
	} catch (DevFailed df) {
	    	    readException.setError(df);
	    setError("Couldn't set value",  readException);
	}
    }

    public IScalarAttribute getWritableAttribute() {
	return null;
    }

    public IScalarAttribute getReadableAttribute() {
	return null;
    }

    public void setString(String s) {
	setValue(s);
    }

    public int getXDimension() {
	return 1;
    }

    public int getMaxXDimension() {
	return 1;
    }

    public String getStringValue() {
	try {
	    readValueFromNetwork();
	    return attribute.extractString();
	} catch (DevFailed d) {
	    setError("Couldn't read from network",
		     new AttributeReadException(d));
	    return null;
		
	} // end of try-catch
    }

    public void refresh() {

	if (skippingRefresh) return;
	 
	try {
	    String newVal;
	    newVal = getStringValue();
	    fireValueChanged(newVal);
	} catch (Exception e) {
	    setError(e.getMessage(), e);
	}
	//	refreshProperties();
    }

    public boolean isWritable() {
	return super.isWritable();
    }
    
    protected void fireValueChanged(String newValue) {
	propChanges.fireStringScalarEvent(this, newValue, timeStamp);
    }
    
    public void addStringScalarListener(IStringScalarListener l) {
	propChanges.addStringScalarListener(l);
    }

    public void removeStringScalarListener(IStringScalarListener l) {
	propChanges.removeStringScalarListener(l);
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
