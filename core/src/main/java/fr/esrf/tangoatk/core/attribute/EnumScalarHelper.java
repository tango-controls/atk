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

// File:          EnumScalarHelper.java
// Created:       05/02/2007 poncet
// By:            <poncet@esrf.fr>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;


import fr.esrf.TangoDs.TangoConst;
import fr.esrf.tangoatk.core.*;

import fr.esrf.TangoApi.*;
import fr.esrf.Tango.DevFailed;

public class EnumScalarHelper implements java.io.Serializable,TangoConst {

  AAttribute enumAtt;
  EventSupport propChanges;

  public EnumScalarHelper(AAttribute attribute) {
    init(attribute);
  }

  void init(AAttribute attribute) {
    setAttribute(attribute);
    propChanges = attribute.getPropChanges();
  }


  public void setAttribute(AAttribute attribute) {
    this.enumAtt = attribute;
  }

  public AAttribute getAttribute() {
    return enumAtt;
  }


  void insert(String enumStr) throws DevFailed, AttributeSetException {

    DeviceAttribute da;
    short shortValue;

    da = this.enumAtt.getAttribute();

    if (da == null) {
      throw new AttributeSetException("Cannot set enumeration value. DeviceAttribute is null.");
    }

    try {

      shortValue = getValueForEnum(enumStr);

      switch(this.enumAtt.config.data_type)
      {
        case Tango_DEV_SHORT:
        case Tango_DEV_ENUM:
          da.insert(shortValue);
          break;
        case Tango_DEV_USHORT:
          da.insert_us(shortValue);
          break;
        case Tango_DEV_LONG:
          da.insert((int) shortValue);
          break;
        case Tango_DEV_ULONG:
          da.insert_ul(shortValue);
          break;
        case Tango_DEV_LONG64:
          da.insert((long) shortValue);
          break;
        case Tango_DEV_ULONG64:
          da.insert_u64(shortValue);
          break;
        default:
          throw new AttributeSetException("Invalid attribute type for enumeration");
      }

    } catch (IllegalArgumentException ex) {
      throw new AttributeSetException("Invalid enumeration value");
    }

  }

  String getEnumScalarValue(DeviceAttribute devAtt) throws DevFailed, AttributeReadException {
    return getEnumScalarValue(devAtt,0);
  }

  String getEnumScalarSetPoint(DeviceAttribute devAtt) throws DevFailed, AttributeReadException {
    if(enumAtt.isWritable())
      return getEnumScalarValue(devAtt,1);
    else
      // Return read value for READ ONLY attribute
      return getEnumScalarValue(devAtt,0);
  }

  String getEnumScalarValue(DeviceAttribute devAtt,int idx) throws DevFailed, AttributeReadException {

    try {

      switch (devAtt.getType()) {

        case Tango_DEV_SHORT:
        case Tango_DEV_ENUM:
          short[] short_arr = devAtt.extractShortArray();
          if (short_arr.length <= idx)
            throw new AttributeReadException("Invalid ShortArray");
          return getEnumValue(short_arr[idx]);

        case Tango_DEV_USHORT:
          int[] ushort_arr = devAtt.extractUShortArray();
          if (ushort_arr.length <= idx)
            throw new AttributeReadException("Invalid UShortArray");
          return getEnumValue((short) ushort_arr[idx]);

        case Tango_DEV_LONG:
          int[] long_arr = devAtt.extractLongArray();
          if (long_arr.length <= idx)
            throw new AttributeReadException("Invalid LongArray");
          return getEnumValue((short) long_arr[idx]);

        case Tango_DEV_ULONG:
          long[] ulong_arr = devAtt.extractULongArray();
          if (ulong_arr.length <= idx )
            throw new AttributeReadException("Invalid ULongArray");
          return getEnumValue((short) ulong_arr[idx]);

        case Tango_DEV_LONG64:
          long[] long64_arr = devAtt.extractLong64Array();
          if (long64_arr.length <= idx)
            throw new AttributeReadException("Invalid Long64Array");
          return getEnumValue((short) long64_arr[idx]);

        case Tango_DEV_ULONG64:
          long[] ulong64_arr = devAtt.extractULong64Array();
          if (ulong64_arr.length <= idx)
            throw new AttributeReadException("Invalid ULong64Array");
          return getEnumValue((short) ulong64_arr[idx]);


        default:
          throw new AttributeReadException("Invalid attribute type for enumeration");

      }

    } catch (IllegalArgumentException ex) {

      throw new AttributeReadException("Invalid enumeration value");

    }

  }

  short getValueForEnum(String enumVal) throws IllegalArgumentException {

    EnumScalar ens;
    String[] enums = null;
    short indEnum;

    if (enumVal == null)
      throw new IllegalArgumentException();

    ens = (EnumScalar) enumAtt;
    enums = ens.getEnumValues();

    indEnum = -1;
    for (int i = 0; i < enums.length; i++)
      if (enumVal.equals(enums[i])) {
        indEnum = (short) i;
        break;
      }

    if (indEnum >= enums.length)
      indEnum = -1;

    return indEnum;
  }

  String getEnumValue(short shortVal) throws IllegalArgumentException {

    EnumScalar ens;
    String[] enums = null;
    String val;

    if (shortVal < 0)
      throw new IllegalArgumentException();

    ens = (EnumScalar) enumAtt;
    enums = ens.getEnumValues();

    if (shortVal >= enums.length)
      throw new IllegalArgumentException();


    val = enums[shortVal];
    return val;

  }


  public void addEnumScalarListener(IEnumScalarListener l) {
    propChanges.addEnumScalarListener(l);
  }

  public void removeEnumScalarListener(IEnumScalarListener l) {
    propChanges.removeEnumScalarListener(l);
  }

  void fireEnumScalarValueChanged(String newEnum, long timeStamp) {
    propChanges.fireEnumScalarEvent((IEnumScalar) enumAtt, newEnum, timeStamp);
  }


  public String getVersion() {
    return "$Id$";
  }

}
