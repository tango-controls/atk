// File:          IDevice.java
// Created:       2002-07-18 15:13:51, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-18 15:16:12, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import fr.esrf.TangoApi.*;
import fr.esrf.Tango.DevFailed;

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
    public DeviceData executeCommand(String command, DeviceData argin)
	throws DevFailed;

    public AttributeInfo getAttributeInfo(String name) throws DevFailed;

    public void removeStatusListener(IStatusListener listener);

    public void addStatusListener(IStatusListener listener);

    public void removeStateListener(IStateListener listener);

    public void addStateListener(IStateListener listener);

    public String getStatus();

    //public void setAlias(String alias);

    public String getAlias();



    public AtkEventListenerList getListenerList();


}
