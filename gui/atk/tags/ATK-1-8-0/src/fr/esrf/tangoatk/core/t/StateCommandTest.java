// File:          StateCommandTest.java
// Created:       2001-10-30 15:47:47, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:35:10, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.t;

import junit.framework.*;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.attribute.*;
import java.util.*;
import fr.esrf.Tango.*;

public class StateCommandTest extends CoreTestCase {
    AAttribute attribute;

    StateCommandTest(String name) {
	super(name);
    }
    
    protected void setUp() {
	super.setUp();
	try {
	    List l = AttributeFactory.getInstance().
		getEntities("eas/test-api/1/Long_attr_w");
	    attribute = (AAttribute)l.get(0);
	} catch (Exception e ) {
	    throw new
		RuntimeException("Could not instanciate test system...");
	}
    }

    public void executeTest() {
	try {
	   System.out.println(attribute.getDevice().getState());
	} catch (Exception e) {
	    fail(e.toString());
	}
    }
	
    public static Test suite() {
	TestSuite suite = new TestSuite();
	
	suite.addTest(new StateCommandTest("executeTest"));

	return suite;
    }
    
    public static void main(String args[]) {
	junit.textui.TestRunner.run(suite());
    }
}
