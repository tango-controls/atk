// File:          CoreTestRunner.java
// Created:       2001-10-05 13:23:08, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-08 11:35:25, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.t;
import junit.textui.*;
import junit.framework.*;
import java.io.*;
import java.util.*;

public class CoreTestRunner extends TestRunner {
    PrintStream fWriter= System.out;
    long startTime;
    Map errors = new HashMap();
    Map failures = new HashMap();
    
    public CoreTestRunner() {
    }
    
    public CoreTestRunner(PrintStream writer) {
	super();
	if (writer == null)
	    throw new IllegalArgumentException("Writer can't be null");
	fWriter= writer;
	writer.print("Starting CoreTest");
    }
	
    public synchronized void addError(Test test, Throwable t) {
	errors.put(test, test);
    }

    public synchronized void addFailure(Test test, AssertionFailedError t) {
	failures.put(test, test);
    }

    public synchronized void startTest(Test test) {

	String tname = test.toString();
	fWriter.print(tname);
	for (int i = 0; i < 60 - tname.length(); i++) {
	    fWriter.print(".");
	}
	startTime = System.currentTimeMillis();
    }

    public void endTest(Test test) {
	long endTime = System.currentTimeMillis();
	if (failures.get(test) != null) {
	    fWriter.print("Failed");
	} else if (errors.get(test) != null) {
	    fWriter.print("Error");
	} else {
	    fWriter.print("Ok");	    
	} // end of else
	
	fWriter.println(" (" + (endTime - startTime) + "ms)");
    }

    static public void run(Test suite) {
		TestRunner aTestRunner= new CoreTestRunner();
		aTestRunner.doRun(suite, false);
    }

    public TestResult doRun(Test suite, boolean wait) {
		TestResult result= createTestResult();
		result.addListener(this);
		long startTime= System.currentTimeMillis();
		suite.run(result);
		long endTime= System.currentTimeMillis();
		long runTime= endTime-startTime;
		writer().println();
		writer().println("Time: "+elapsedTimeAsString(runTime));
		print(result);

		writer().println();

		pause(wait);
		return result;
    }


}

