// File:          DeviceFactoryTest.java
// Created:       2001-10-05 11:59:59, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:21:54, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.t;

import junit.framework.*;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.command.*;
import java.util.*;

public class CommandFactoryTest extends CoreTestCase {
    CommandFactory commandFactory;
    String goodCommand1 = "eas/test-api/1/Status";
    String goodCommand2 = "eas/test-api/2/State";
    String badCommand  = "bad/test-api/1/ugle";

    public CommandFactoryTest(String name) {
	super(name);
    }

    protected void setUp() {
	super.setUp();
	commandFactory = CommandFactory.getInstance();
    }

    public void testInit() {
	assertTrue(commandFactory != null);
    }

    public void testNoDuplicateCommands() {
	List l1 = null, l2 = null;
	try {
	    l1 = commandFactory.getEntities(goodCommand1);
	    l2 = commandFactory.getEntities(goodCommand1);
	    assertTrue(l1.size() == 1);
	    assertTrue(l1.size() == l2.size());
	    assertTrue(l1.get(0) == l2.get(0));
	} catch (Exception e) {
	    fail("Could not initialize " + goodCommand1 +
		 ". Make sure it's running..." + e);
	}

    }

    public void testNoBadCommands() {

	List l1 = null;

	try {
	    l1 = commandFactory.getEntities(badCommand);

	} catch (ConnectionException e) {
	    assertTrue(l1 == null);
	    return;
	}
	fail("CommandFactory did not throw DevFailed on " + badCommand);
    }

    public static Test suite() {
	TestSuite suite= new TestSuite();
	
	suite.addTest(new CommandFactoryTest("testInit"));
	suite.addTest(new CommandFactoryTest("testNoDuplicateCommands"));
	suite.addTest(new CommandFactoryTest("testNoBadCommands"));
	return suite;
    }
    
    public static void main(String args[]) {
	junit.textui.TestRunner.run(suite());
    }

}
