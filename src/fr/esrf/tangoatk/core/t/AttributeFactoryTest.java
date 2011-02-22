// File:          DeviceFactoryTest.java
// Created:       2001-10-05 11:59:59, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:33:20, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.t;
import junit.framework.*;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.attribute.*;
import org.apache.log4j.*;
import java.util.*;

public class AttributeFactoryTest extends CoreTestCase {
    AttributeFactory attributeFactory;
    String goodAttribute1 = "eas/test-api/1/Double_attr_w";
    String goodAttribute2 = "eas/test-api/1/String_attr_w";
    String badAttribute  = "bad/test-api/1";

    public AttributeFactoryTest(String name) {
	super(name);
    }

    protected void setUp() {
	super.setUp();
	attributeFactory = AttributeFactory.getInstance();
    }

    public void testInit() {
	assertTrue(attributeFactory != null);
    }

    public void testNoDuplicateAttributes() {
	List d1 = null, d2 = null;
	try {
	    d1 = attributeFactory.getEntities(goodAttribute1);
	    d2 = attributeFactory.getEntities(goodAttribute1);
	    assertTrue(d1.size() == 1);
	    assertTrue(d1.size() == d2.size());
	    assertTrue(d1.get(0) == d2.get(0));
	} catch (Exception e) {
	    fail("Could not initialize " + goodAttribute1 +
		 ". Make sure it's running..." + e);
	}

    }

    public void testNoBadAttributes() {

	List d1 = null;

	try {
	    d1 = attributeFactory.getEntities(badAttribute);
	    fail("AttributeFactory did not throw DevFailed on " + badAttribute);
	} catch (ConnectionException e) {
	    ;
	}
	assertTrue(d1 == null);
    }

    public static Test suite() {
	TestSuite suite= new TestSuite();
	
	suite.addTest(new AttributeFactoryTest("testInit"));
	suite.addTest(new AttributeFactoryTest("testNoDuplicateAttributes"));
	suite.addTest(new AttributeFactoryTest("testNoBadAttributes"));
	return suite;
    }
    
    public static void main(String args[]) {
	junit.textui.TestRunner.run(suite());
    }

}
