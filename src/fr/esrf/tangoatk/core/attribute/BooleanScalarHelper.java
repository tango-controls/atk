// File:          BooleanScalarHelper.java
// Created:       2005-02-04 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;


import java.util.*;

import fr.esrf.tangoatk.core.*;

import fr.esrf.TangoApi.*;
import fr.esrf.Tango.AttrQuality;
import fr.esrf.Tango.DevFailed;

class BooleanScalarHelper implements java.io.Serializable
{
    IAttribute attribute;
    EventSupport propChanges;

    public BooleanScalarHelper(IAttribute attribute)
    {
      init(attribute);
    }

    void init(IAttribute attribute)
    {
      setAttribute(attribute);
      propChanges = ((AAttribute) attribute).getPropChanges();
    }


    public void setAttribute(IAttribute attribute)
    {
      this.attribute = attribute;
    }

    public IAttribute getAttribute()
    {
      return attribute;
    }

    protected void setProperty(String name, Number value)
    {
      attribute.setProperty(name, value);
      attribute.storeConfig();
    }

    protected void setProperty(String name, Number value, boolean writable)
    {
      attribute.setProperty(name, value, writable);
    }


    void fireValueChanged(boolean newValue, long timeStamp)
    {
      propChanges.fireBooleanScalarEvent((IBooleanScalar) attribute,
	newValue, timeStamp);
    }

    void insert(boolean b)
    {
	attribute.getAttribute().insert(b);
    }


    boolean extract() throws DevFailed
    {
      return attribute.getAttribute().extractBoolean();
    }


    void addBooleanScalarListener(IBooleanScalarListener l)
    {
	propChanges.addBooleanScalarListener(l);
    }


    void removeBooleanScalarListener(IBooleanScalarListener l)
    {
	propChanges.removeBooleanScalarListener(l);
    }



    public boolean getBooleanScalarSetPoint(DeviceAttribute attribute) throws DevFailed
    {

	boolean[]  bool_arr=null;

	bool_arr = attribute.extractBooleanArray();
      
	if (bool_arr == null)
	   return false;
	
	if (bool_arr.length < 1)
	   return false;
	   
	if (bool_arr.length > 1)
	   return bool_arr[1];
	else // The attribute WRITE (WRITE ONLY) return their setPoint in the first element
	   return bool_arr[0];
    }



    public String getVersion() {
	return "$Id$";
    }
}
