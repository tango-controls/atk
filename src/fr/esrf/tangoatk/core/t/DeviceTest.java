// File:          DeviceTest.java
// Created:       2001-10-05 14:13:51, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-03-04 10:36:46, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.t;

import junit.framework.*;
import fr.esrf.tangoatk.core.*;
import java.util.*;
import fr.esrf.TangoDs.*;
import fr.esrf.TangoApi.*;

public class DeviceTest extends CoreTestCase {
    Device device;
    String goodAttribute = "eas/test-api/1/Double_attr_w";
    
    public DeviceTest(String name) {
	super(name);
    }

    protected void setUp() {
	super.setUp();
	try {
	    device = DeviceFactory.getInstance().getDevice("eas/test-api/1");
	} catch (Exception e) {
	    throw new RuntimeException("Could not instanciate test system...");
	}
    }

    public void testReadAttribute() {
	try {
	    DeviceAttribute de = device.readAttribute(goodAttribute);
	    assertTrue("DeviceAttribute should not be null for " +
		       goodAttribute, de != null); 
	} catch (Exception e) {
	    fail("Could not read attribute " + goodAttribute);
	}
    }    

    public void testGetState() {

	try {
	    String state = device.getState();
	    assertTrue(state == "ON" || state == "ALARM");
	} catch (Exception e) {
	    fail("Could not read state" + e);
	} // end of try-catch
    }

    public void testInit() {
	assertTrue(device != null);
    }

    public static Test suite() {
	TestSuite suite= new TestSuite();
	
	suite.addTest(new DeviceTest("testInit"));
	suite.addTest(new DeviceTest("testReadAttribute"));
	suite.addTest(new DeviceTest("testGetState"));
	return suite;

    }
    
    public static void main(String args[]) {
	junit.textui.TestRunner.run(suite());
    }
}


