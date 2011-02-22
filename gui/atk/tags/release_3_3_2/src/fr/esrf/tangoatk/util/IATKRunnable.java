// File:          IATKRunnable.java
// Created:       2001-12-12 13:57:51, assum
// By:            <erik@assum.net>
// Time-stamp:    <2001-12-12 14:0:54, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.util;

public interface IATKRunnable extends Runnable {

    public void setController(AppLauncher launcher);
}
