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
 
// File:          EnumSpectrumEvent.java
// Created:       2015-03-09 09:31:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

public class EnumSpectrumEvent extends ATKEvent
{
    String[]     value;
    long         timeStamp;
    
    public EnumSpectrumEvent(IEnumSpectrum source, String[] val, long timeStamp)
    {
	super(source, timeStamp);
	setValue(value);
    }

    public String[]  getValue()
    {
	return value;
    }

    public void setValue(String[] value)
    {
	this.value = value;
    }

    public void setSource(IEnumSpectrum source)
    {
	this.source = source;
    }

    public String getVersion()
    {
	return "$Id$";
    }
}
