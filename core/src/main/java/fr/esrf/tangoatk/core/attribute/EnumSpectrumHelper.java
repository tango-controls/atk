/*
 *  Copyright (C) :	2015
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
 
// File:          EnumSpectrumHelper.java
// Created:       2015-03-09 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;


import fr.esrf.tangoatk.core.*;

import fr.esrf.TangoApi.*;
import fr.esrf.Tango.DevFailed;

public class EnumSpectrumHelper implements java.io.Serializable
{
    AAttribute   enumSpectAtt;
    EventSupport propChanges;

    public EnumSpectrumHelper(AAttribute attribute)
    {
	init(attribute);
    }

    void init(AAttribute attribute)
    {
        setAttribute(attribute);
        propChanges = attribute.getPropChanges();
    }


    public void setAttribute(AAttribute attribute)
    {
      this.enumSpectAtt = attribute;
    }

    public AAttribute getAttribute()
    {
      return enumSpectAtt;
    }
    
    private short getOneShortValueFromEnum(String enumVal) throws IllegalArgumentException
    {
	short        indEnum;
	
	if (enumVal == null)
	   throw new IllegalArgumentException();
        
        String[] enumLabels = ((EnumSpectrum) enumSpectAtt).getEnumValues();
	
	if (enumLabels == null)
	   throw new IllegalArgumentException();
        
	
	indEnum = -1;
	for (int i=0; i<enumLabels.length; i++)
	    if (enumVal.equals(enumLabels[i]))
	    {
	       indEnum =(short) i;
	       break;
	    }
	    
	if (indEnum >= enumLabels.length)
	   indEnum = -1;
        
	return indEnum;
    }
    
    private String getOneEnumValueFromShort(short shortVal) throws IllegalArgumentException
    {        
        String[] enumLabels = ((EnumSpectrum) enumSpectAtt).getEnumValues();
	
	if (enumLabels == null)
	   throw new IllegalArgumentException();
	
	if ( (shortVal < 0) || (shortVal >= enumLabels.length) )
	   throw new IllegalArgumentException();

        return enumLabels[shortVal];
    }

    public short[] getShortValuesFromEnumValues(String[] enumStr)
    {
        short[] shortValues = new short[enumStr.length];

        for (int i=0; i<enumStr.length; i++)
        {
             try
             {
                 shortValues[i] = this.getOneShortValueFromEnum(enumStr[i]);
             } 
             catch (IllegalArgumentException ex)
             {
                 return null;
             }
        }
        return shortValues;
    }
    
    public String[] getEnumValuesFromShort(short[] shVals)
    {
        String[]  retVal = new String[shVals.length];
        for (int i=0; i<shVals.length; i++)
        {
             try
             {
                 retVal[i] = this.getOneEnumValueFromShort(shVals[i]);
             } 
             catch (IllegalArgumentException ex)
             {
                 return null;
             }
        }
        return retVal;
    }
    
    
    void insert(String[] enumValues) throws DevFailed, AttributeSetException
    {
	DeviceAttribute      da;
	short[]              shortValues;

	da = this.enumSpectAtt.getAttribute();
	if (da == null)
	{
	   throw new AttributeSetException("Cannot set enumeration value. DeviceAttribute is null.");
	}
	
	try
	{
	   shortValues=getShortValuesFromEnumValues(enumValues);
           da.insert(shortValues);
	}
	catch (IllegalArgumentException ex)
	{
	    throw new AttributeSetException("Invalid enumeration value");
	}
	
    }
   
    String[] getEnumSpectrumValue(DeviceAttribute devAtt) throws DevFailed, AttributeReadException
    {
       short[]   val_rw = devAtt.extractShortArray();
       int       nbReadElements = devAtt.getNbRead();
       short[]   short_read = new short[nbReadElements];

       if (nbReadElements < 1)
	  throw new AttributeReadException("Invalid shortArray");
       System.arraycopy(val_rw, 0, short_read, 0, nbReadElements);
       
       return getEnumValuesFromShort(short_read);
    }
    
   
    String[] getEnumSpectrumSetPoint(DeviceAttribute devAtt) throws DevFailed, AttributeReadException
    {
       short[]   val_rw = devAtt.extractShortArray();
       int       nbReadElements = devAtt.getNbRead();
       int       nbSet = val_rw.length - nbReadElements;
       
       // The attributes WRITE (WRITE ONLY) return their setPoint in the first sequence of elements
       // In all cases when no "set" element sequence is returned, return the read elements for setPoint
       if (nbSet < 1)
       {
          return getEnumSpectrumValue(devAtt);
       }

       short[]   short_setPoints = new short[nbSet];

       System.arraycopy(val_rw, nbReadElements, short_setPoints, 0, nbSet);
       
       return getEnumValuesFromShort(short_setPoints);
    }
    

    public void addEnumSpectrumListener(IEnumSpectrumListener l)
    {
      propChanges.addEnumSpectrumListener(l);
    }

    public void removeEnumSpectrumListener(IEnumSpectrumListener l)
    {
      propChanges.removeEnumSpectrumListener(l);
    }

    void fireEnumSpectrumValueChanged(String[] newEnum, long timeStamp)
    {
	propChanges.fireEnumSpectrumEvent((IEnumSpectrum)enumSpectAtt, newEnum, timeStamp);
    }


    public String getVersion()
    {
       return "$Id$";
    }

}
