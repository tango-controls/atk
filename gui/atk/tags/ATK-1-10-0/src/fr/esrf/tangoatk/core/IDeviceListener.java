// File:          IDeviceListener.java
// Created:       2001-12-14 16:26:32, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-01 15:41:54, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

public interface IDeviceListener extends IStatusListener, IStateListener {

    public void setModel(Device device) ;
}

