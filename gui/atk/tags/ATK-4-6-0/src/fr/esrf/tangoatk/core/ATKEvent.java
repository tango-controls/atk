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
 
// File:          ATKEvent.java
// Created:       2002-02-06 16:23:28, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-27 11:37:36, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import java.util.EventObject;

public abstract class ATKEvent extends EventObject {
    long timeStamp;


    public ATKEvent(Object source, long timeStamp) {
	super(source);
	setTimeStamp(timeStamp);
    }
    
    public void setTimeStamp(long ms) {
	    timeStamp = ms;
    }

    public long getTimeStamp() {
	return timeStamp;
    }

    public String getVersion() {
	return "$Id$";
    }

}
