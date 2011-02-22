// File:          ImageAttributeTest.java
// Created:       2001-10-22 10:45:44, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-24 18:33:38, assum>
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

public class ImageAttributeTest extends CoreTestCase {
    IAttribute attribute;

    public ImageAttributeTest(String name) {
	super(name);
    }

    protected void setUp() {
	super.setUp();
	try {
	    List l = AttributeFactory.getInstance().
		getEntities("eas/test-api/1/Double_ima_attr");
	    attribute = (IAttribute)l.get(0);
	} catch (Exception e ) {
	    e.printStackTrace();
	    throw new
		RuntimeException("Could not instanciate test system...");
	}
    }

    public void testInit() {
	assertTrue(attribute instanceof IAttribute);
    }

	
    public static Test suite() {
	TestSuite suite = new TestSuite();
	
	suite.addTest(new ImageAttributeTest("testInit"));
	return suite;
    }
    
    public static void main(String args[]) {
	junit.textui.TestRunner.run(suite());
    }
}
