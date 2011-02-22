// File:          BasicErrorHandler.java<2>
// Created:       2001-10-04 15:04:22, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-01 16:51:30, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import org.apache.log4j.*;
import org.apache.log4j.spi.*;
import org.apache.log4j.varia.*;
import fr.esrf.Tango.*;

public class BasicLogAppender extends AppenderSkeleton {

    //    fr.esrf.TangoATK.Widget.BasicErrorHandler window;


    protected static BasicLogAppender instance = null;
    protected PriorityRangeFilter filter = new PriorityRangeFilter();
    protected ILogListener logListener;
    protected static Logger log =
	ATKLogger.getLogger(BasicLogAppender.class.getName());

    public static boolean USE_SWING = true;
    public static boolean DO_OUTPUT = true;
    
    protected BasicLogAppender() {
	instance = this;
	setLayout(new PatternLayout("[ %p ] - %c %m"));
	filter.setPriorityMin(Priority.DEBUG);
	addFilter(filter);
	logListener = new ILogListener() {
		public void append(String s) {
		    System.out.println(s);
		}

		public void close() {
		    return;
		}
	    };
    }

    public static BasicLogAppender getInstance() {
	if (instance == null) {
	    instance = new BasicLogAppender();
	}
	return instance;
    }
		    
    public void setLogListener(ILogListener l) {
	logListener = l;
    }

    public ILogListener getLogListener() {
	return logListener;
    }
    
    protected void append(LoggingEvent l) {
	
	StringBuffer message = new StringBuffer(layout.format(l));
	
	message.append(processThrowable(l.getThrowableInformation()));
	if (USE_SWING) {
	    logListener.append(message.toString().replace('.', '/'));
	    return;
	}
	if (DO_OUTPUT) {
	    System.out.println(message.toString().replace('.', '/'));
	}
    }

    protected String processThrowable(ThrowableInformation t) {
	if (t == null) return "";
	
	Throwable exception = t.getThrowable();

	if (exception == null) return "";
	
	if (exception instanceof ATKException) {
	    return formatATKException((ATKException)exception);
	}
	String[] stringrep = t.getThrowableStrRep();

	StringBuffer s = new StringBuffer();
	for (int i = 0; i < stringrep.length; i++) {
	    s.append("- " + stringrep[i]);
	    s.append("\n");
	}
	return s.toString();
    }

    protected String formatATKException(ATKException exception) {
	return exception.toString();
    }
		       
    public boolean requiresLayout() {
	return true;
    }

    public void setDebugOutput() {
	filter.setPriorityMin(Priority.DEBUG);
    }

    public void stopDebugOutput() {
	setInfoOutput();
    }
    
    public void setWarningOutput() {
	filter.setPriorityMin(Priority.WARN);
    }

    public void stopWarningOutput() {
	filter.setPriorityMin(Priority.ERROR);
    }

    public void setInfoOutput() {
	filter.setPriorityMin(Priority.INFO);
    }

    public void stopInfoOutput() {
	setWarningOutput();
    }
    
    public void setLevelMin(Level l) {
	filter.setPriorityMin(l);
    }
    
    public void close() {
	logListener.close();
    }

    public String getVersion() {
	return "$Id$";
    }
    
}


