// File:          SpectrumAttributeTest.java
// Created:       2001-10-22 10:45:44, assum
// By:            <erik@assum.net>
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

public class SpectrumAttributeTest extends CoreTestCase {
    INumberSpectrum attribute;

    public SpectrumAttributeTest(String name) {
	super(name);
    }

    protected void setUp() {
	super.setUp();
	try {
	    List l = AttributeFactory.getInstance().
		getEntities("eas/test-api/1/Short_spec_attr");
	    attribute = (INumberSpectrum)l.get(0);
	} catch (Exception e ) {
	    throw new
		RuntimeException("Could not instanciate test system...");
	}
    }

    public void testInit() {
	assertTrue(attribute instanceof INumberSpectrum);
    }

	
    public static Test suite() {
	TestSuite suite = new TestSuite();
	
	suite.addTest(new SpectrumAttributeTest("testInit"));
	return suite;
    }
    
    public static void main(String args[]) {
	junit.textui.TestRunner.run(suite());
    }
}
