// File:          EnumScalarHelper.java
// Created:       05/02/2007 poncet
// By:            <poncet@esrf.fr>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import java.lang.IllegalArgumentException;
import java.util.*;

import fr.esrf.tangoatk.core.*;

import fr.esrf.TangoApi.*;
import fr.esrf.Tango.AttrQuality;
import fr.esrf.Tango.DevFailed;

public class EnumScalarHelper implements java.io.Serializable
{
    IAttribute   enumAtt;
    EventSupport propChanges;

    public EnumScalarHelper(IAttribute attribute)
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
      this.enumAtt = attribute;
    }

    public IAttribute getAttribute()
    {
      return enumAtt;
    }
    
    
    void insert(String enumStr) throws DevFailed, AttributeSetException
    {
	DeviceAttribute      da;
	short                shortValue;
	int                  ushortValue;

	da = this.enumAtt.getAttribute();
	if (da == null)
	{
	   throw new AttributeSetException("Cannot set enumeration value. DeviceAttribute is null.");
	}
	
	try
	{
	   shortValue=getShortValueForEnum(enumStr);
	   if (((AAttribute) enumAtt).getTangoDataType() == AAttribute.Tango_DEV_USHORT)
	   {
	       ushortValue = (int) shortValue;
               da.insert_us(ushortValue);
	   }
	   else
	       da.insert(shortValue);
	}
	catch (IllegalArgumentException ex)
	{
	    throw new AttributeSetException("Invalid enumeration value");
	}
	
    }

  
    String getEnumScalarValue(DeviceAttribute devAtt) throws DevFailed, AttributeReadException
    {
       short[]    short_arr = null;
       
       short_arr = devAtt.extractShortArray();

       if (short_arr == null)
	  throw new AttributeReadException("Invalid shortArray");

       if (short_arr.length < 1)
	  throw new AttributeReadException("Invalid shortArray");

       try
       {
            String  str = getEnumValueFromShort(short_arr[0]);
	    return str;
       }
       catch (IllegalArgumentException ex)
       {
	    throw new AttributeReadException("Invalid enumeration value");
       }
    }
    

  
    String getEnumScalarSetPoint(DeviceAttribute devAtt) throws DevFailed, AttributeReadException
    {
       short[]    short_arr = null;
       
       short_arr = devAtt.extractShortArray();

       if (short_arr == null)
	  throw new AttributeReadException("Invalid shortArray");

       if (short_arr.length < 1)
	  throw new AttributeReadException("Invalid shortArray");

       try
       {
           String  str;
	   if (short_arr.length > 1)
	      str = getEnumValueFromShort(short_arr[1]);
	   else
	      str = getEnumValueFromShort(short_arr[0]);
	   return str;
       }
       catch (IllegalArgumentException ex)
       {
	    throw new AttributeReadException("Invalid enumeration set value");
       }
    }
    
    
    private short getShortValueForEnum(String enumVal) throws IllegalArgumentException
    {
	EnumScalar   ens;
	String[]     enums=null;
	int          indEnum;
	short        shVal;
	
	if (enumVal == null)
	   throw new IllegalArgumentException();
	   
	ens = (EnumScalar) enumAtt;
	enums = ens.getEnumValues();
	
	indEnum = -1;
	for (int i=0; i<enums.length; i++)
	    if (enumVal.equals(enums[i]))
	    {
	       indEnum = i;
	       break;
	    }
	    
	if (indEnum < 0)
	   throw new IllegalArgumentException();
	   
	shVal = (short) indEnum;
	return shVal;
    }
    
    private String getEnumValueFromShort(short shortVal) throws IllegalArgumentException
    {
	EnumScalar   ens;
	String[]     enums=null;
	String       val;
	
	if (shortVal < 0)
	   throw new IllegalArgumentException();
	   
	ens = (EnumScalar) enumAtt;
	enums = ens.getEnumValues();
	
	if (shortVal >= enums.length)
	   throw new IllegalArgumentException();
	   
	
	val = enums[shortVal];
        return val;
    }


    public void addEnumScalarListener(IEnumScalarListener l)
    {
      propChanges.addEnumScalarListener(l);
    }

    public void removeEnumScalarListener(IEnumScalarListener l)
    {
      propChanges.removeEnumScalarListener(l);
    }

    void fireEnumScalarValueChanged(String newEnum, long timeStamp)
    {
	propChanges.fireEnumScalarEvent((IEnumScalar)enumAtt, newEnum, timeStamp);
    }


    public String getVersion()
    {
       return "$Id$";
    }

}
