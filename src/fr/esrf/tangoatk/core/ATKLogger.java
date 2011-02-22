// File:          ATKLogger.java
// Created:       2002-07-01 16:26:50, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-09 9:26:9, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import org.apache.log4j.*;

public class ATKLogger extends Logger {
    static boolean debug = false;
    protected ATKLogger() {
	super("");
    }

    public static Logger getLogger(String s) {
	if (System.getProperty("DEBUG") != null) {
	    Log4jConfigurator.configure();
	    return Logger.getLogger(s);
	}
	return new ATKLogger();
    }

    public void warn(Object s) {
    }

    public void info(Object s) {
    }

    public void debug(Object s) {
    }

    public void error(Object s) {
    }

    public void fatal(Object s) {
    }


    public void warn(Object s, Throwable t) {
    }

    public void info(Object s, Throwable t) {
    }

    public void debug(Object s, Throwable t) {
    }

    public void error(Object s, Throwable t) {
    }

    public void fatal(Object s, Throwable t) {
    }
}
