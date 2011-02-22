// File:          DeviceFactoryTest.java
// Created:       2001-10-05 11:59:59, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-04-23 13:32:20, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.t;

import junit.framework.*;
import fr.esrf.tangoatk.core.*;
import java.util.*;

public class DeviceFactoryTest extends CoreTestCase {
    DeviceFactory deviceFactory;
    String goodDevice1 = "eas/test-api/1";
    String goodDevice2 = "eas/test-api/2";
    String badDevice  = "bad/test-api/1";

    public DeviceFactoryTest(String name) {
	super(name);
    }

    protected void setUp() {
	super.setUp();
	deviceFactory = DeviceFactory.getInstance();
    }

    public void testInit() {
	assertTrue("Device factory should not be null",
		   deviceFactory != null);
    }

    public void testGetDevice() {
	try {
	    Device d1 = null;
	    d1 = deviceFactory.getDevice(goodDevice1);
	    assertEquals("The device-names should be equal",
			 goodDevice1, d1.getName());
	} catch (Exception e) {
	    fail("Could not initialize " + goodDevice1 +
		 ". Make sure it's running..." + e);
	}
    }
	
    public void testNoDuplicateDevices() {
	Device d1 = null, d2 = null;

	try {
	    d1 = deviceFactory.getDevice(goodDevice2);
	    d2 = deviceFactory.getDevice(goodDevice2);
	    assertEquals("The two devices should be the same object",
			 d1, d2);
	} catch (Exception e) {
	    fail("Could not initialize " + goodDevice1 +
		 ". Make sure it's running..." + e);
	}

    }

    public void testNoBadDevices() {

	Device d1 = null;

	try {
	    d1 = deviceFactory.getDevice(badDevice);
	    fail("DeviceFactory did not throw DevFailed on " + badDevice);
	} catch (ConnectionException e) {
	    ;
	}
	assertEquals("We should not have a device", d1, null);
    }

    public void testDeleteDevice() {
	List i;
	try {
	    deviceFactory.getDevice(goodDevice1);
	    deviceFactory.getDevice(goodDevice2);
	    deviceFactory.deleteDevice(goodDevice1);
	} catch (ConnectionException e) {
	    fail("Could not initialize " + goodDevice1 +
		 ". Make sure it's running..." + e);
	}
	List l = deviceFactory.getDeviceNames();
	String name = (String)l.get(0);
	assertEquals("There should be to devices in the list", 1, l.size());
	assertEquals("The resulting device should be " + goodDevice2,
		   name, goodDevice2); 
    }
				 

    public static Test suite() {
	TestSuite suite= new TestSuite();
	
	suite.addTest(new DeviceFactoryTest("testInit"));
	suite.addTest(new DeviceFactoryTest("testGetDevice"));
	suite.addTest(new DeviceFactoryTest("testNoDuplicateDevices"));
	suite.addTest(new DeviceFactoryTest("testNoBadDevices"));
	suite.addTest(new DeviceFactoryTest("testDeleteDevice"));
	return suite;
    }
    
    public static void main(String args[]) {
	junit.textui.TestRunner.run(suite());
    }

}
