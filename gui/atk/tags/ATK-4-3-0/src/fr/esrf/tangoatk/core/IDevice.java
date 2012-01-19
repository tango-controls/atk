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
 
// File:          IDevice.java
// Created:       2002-07-18 15:13:51, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-18 15:16:12, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

public interface IDevice extends IRefreshee {

    public static final String ON = "ON";
    public static final String OFF = "OFF";
    public static final String CLOSE = "CLOSE";
    public static final String OPEN = "OPEN";
    public static final String INSERT = "INSERT";
    public static final String EXTRACT = "EXTRACT";
    public static final String MOVING = "MOVING";
    public static final String STANDBY = "STANDBY";
    public static final String FAULT = "FAULT";
    public static final String INIT = "INIT";
    public static final String RUNNING = "RUNNING";
    public static final String ALARM = "ALARM";
    public static final String DISABLE = "DISABLE";
    public static final String UNKNOWN = "UNKNOWN";



    public String getState() ;

    public String getName();

    public void removeStatusListener(IStatusListener listener);

    public void addStatusListener(IStatusListener listener);

    public void removeStateListener(IStateListener listener);

    public void addStateListener(IStateListener listener);

    public String getStatus();

    //public void setAlias(String alias);

    public String getAlias();

    public AtkEventListenerList getListenerList();
    

    public boolean getInvertedOpenClose();
  
    public boolean getInvertedInsertExtract();

    public int getIdlVersion();

    public boolean doesEvent();

}
