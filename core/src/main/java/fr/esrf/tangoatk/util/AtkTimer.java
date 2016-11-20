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
 
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.util;
import java.util.*;
import java.io.*;

/**
 * <code>AtkTimer</code> is a singleton class which takes care of
 * timing methods. The two main entry points are {@link #startTimer}
 * {@link #endTimer}. The results are printed when {@link #printResults}
 * is called. 
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version 1.0
 */
public class AtkTimer {
    protected Map threadMap = new HashMap();
    protected Map methodMap = new HashMap();
    protected static AtkTimer self;
    protected int maxMethodName = Integer.MIN_VALUE;
    protected String skip = "fr.esrf.TangoATK.Core.";
    myPrintWriter pw = new myPrintWriter(System.out);
    protected boolean timer = false;

    protected AtkTimer() {
	if ("on".equals(System.getProperty("TIMER"))) {
	    timer = true;
	}
	//	timer = true;
	;
    }

    /**
     * <code>getInstance</code> Gives you the AtkTimer instance
     *
     */
    public static AtkTimer getInstance() {
	if (self == null) {
	    self = new AtkTimer();
	}
	return self;
    }
    
	
    /**
     * <code>startTimer</code> starts the timer for the caller of this method
     * You can time the same method in different treads. The timers are
     * stored on a stack, so it is able to time recursions correctly.
     * Normally startTimer is called like this
     * AtkTimer.getInstance().startTimer(Thread.currentThread());
     * @param t The <code>Thread</code> running the calling method.
     */
    public void startTimer(Thread t) {
	if (!timer) return;

	String method = getCaller();
	int methodLength = method.length() - skip.length();
	maxMethodName = maxMethodName < methodLength ? methodLength :
	    maxMethodName;
	synchronized(threadMap) {
	    Stack s = (Stack)threadMap.get(t);
	
	    if (s == null) {
		s = new Stack();
		threadMap.put(t, s);
	    }
	    s.push(new Timee(method));
	}

	    
    }
    
    /**
     * <code>endTimer</code> ends the timer for the caller of this method.
     * It pops the current method of its stack, calculates the 
     * difference between the methods starttime and the current time, and
     * adds this time to a list containing the other times for this method.
     *
     *  @param t The <code>Thread</code> running the calling method.
     *  @throws IllegalStateException if the popped method name is not
     *  that of the caller.
     */
    public void endTimer(Thread t) {
	if (!timer) return;
	
	Timee timee = null;
	synchronized (threadMap) {
	    Stack s = (Stack)threadMap.get(t);
	    timee = (Timee)s.pop();
	}
	long duration = System.currentTimeMillis() - timee.getStartTime() ;
	String method = getCaller();

	if (!timee.getName().equals(method)) {
	    System.out.println("not popping correct method, " +
				      "wanted " + method + ", got " +
				      timee.getName() + "\n");
	    
	}
	
	List l = (List)methodMap.get(method);
	if (l == null) {
	    l = new Vector();
	    methodMap.put(method, l);
	}
	l.add(new Long(duration));
    }

    public static String padding(int length) {
	StringBuffer b = new StringBuffer();
	for (int i = 0; i < length; i++) {
	    b.append(" ");
	}
	return b.toString();
    }
	
    /**
     * <code>printResults</code> prints the results of the timing.
     * Gives you Average, min, and max time as well as number of calls
     * recorded for each method.
     */
    public void printResults() {
	if (!timer) return;
	
	System.out.println("\nMethodname" +
			   padding((maxMethodName - "methodName".length()) + 5) +
			   "Average\tMin\tMax\tCalls");

	for (Iterator i = methodMap.keySet().iterator(); i.hasNext();) {
	    String methodName = (String)i.next();
	    
	    List l = (List)methodMap.get(methodName);
	    long methodSum = 0;
	    long minVal = Long.MAX_VALUE;
	    long maxVal = Long.MIN_VALUE;
	    long val = 0;
	    int j;
	    for ( j = 0; j < l.size(); j++) {

		val = ((Long)l.get(j)).longValue();
		methodSum += val;
		maxVal = val > maxVal ? val : maxVal;
		minVal = val < minVal ? val : minVal;
		
	    } // end of for ()
	    String blanks = "";
	    if (methodName.indexOf(skip) != -1) {
		methodName = methodName.substring(skip.length());
	    }

	    for (int k = 0; k < maxMethodName - methodName.length(); k++) {
		blanks += " ";
	    } // end of for ()
	    String avg = new Long(methodSum/j).toString();
	    
	    System.out.println(methodName +
			       padding((maxMethodName - methodName.length()) +
				       5)  +
			       padding(5 - avg.length()) +
			       avg +"ms\t" +
			       minVal + "\t" + maxVal + "\t" + j);

	} // end of for ()
	
    }

    
    /**
     * <code>getCaller</code> Figures out the calling method. It does this
     * by creating a new Throwable to get a hold of a stack trace and 
     * treats this in its own way to get a hold of the calling methods name.
     * @return a <code>String</code> value
     */
    protected String getCaller() {
	Throwable e = new Throwable();
	e.printStackTrace(pw);
	return pw.getCaller();
    }

}

class Timee {
    protected String name;
    protected long startTime;

    Timee(String name) {
	this.name = name;
	startTime = System.currentTimeMillis();
    }

    String getName() {
	return name;
    }

    long getStartTime() {
	return startTime;
    }
}

class myPrintWriter extends PrintWriter {
    int i = 0;
    String caller;
    public myPrintWriter(OutputStream w) {
	super(w);
	;
    }

    public void println(Object o) {
    }
    
    public void println(char [] x) {
	if (++i == 3) {
	    caller = new String(x);
	}
    }

    void reset() {
	i = 0;
    }

    String getCaller() {
	reset();
	try {
	    return caller.substring(caller.indexOf(" "),
				    caller.indexOf("(")).trim();
	     
	} catch (Exception e) {
	    return " ";
	} // end of try-catch
    }
}
