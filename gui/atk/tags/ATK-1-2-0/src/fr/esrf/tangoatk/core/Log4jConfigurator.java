// File:          Log4jConfigurator.java
// Created:       2001-10-09 16:25:47, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-09 8:23:12, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import org.apache.log4j.*;

public class Log4jConfigurator extends BasicConfigurator {
    protected static ConsoleAppender consoleAppender;
    protected static boolean done = false;
    protected static Logger log =
	ATKLogger.getLogger(Log4jConfigurator.class.getName());


    public static void configure() {
	if (done) return;

	done = true;
	
	consoleAppender = new ConsoleAppender
	    (new PatternLayout("[ %p ] - %c %m%n"));
	Log4jConfigurator.configure(consoleAppender, Logger.getLogger("fr"));
	Logger.getLogger("fr").getHierarchy().disable("DEBUG");

    }
    /**
       Add <code>appender</code> to the root category.
       @param appender The appender to add to the root category.
    */
    static public void configure(Appender appender, Category root) {
	root.addAppender(appender);
    }
    
    public static void addLogger(Logger l) {
 	log.debug("added " + l.getName());
 	l.removeAppender(consoleAppender);
 	l.addAppender(BasicLogAppender.getInstance());
 	log.debug("done");
    }

    

    public String getVersion() {
	return "$Id$";
    }
}
