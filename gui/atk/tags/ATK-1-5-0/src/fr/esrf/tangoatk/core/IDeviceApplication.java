// File:          IDeviceApplication.java
// Created:       2002-03-04 19:03:40, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-03-04 19:6:9, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

public interface IDeviceApplication extends Runnable {


    public void setModel(Device model);

    public Device getModel();

}
