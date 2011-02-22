// File:          StringAttributeTest.java
// Created:       2001-10-05 16:13:29, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-25 17:44:13, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.t;

import junit.framework.*;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.attribute.*;
import java.util.*;

public class ScalarAttributeTest extends CoreTestCase {
    IStringScalar attribute;
    String scalarValue = "";
    String state;

    public ScalarAttributeTest(String name) {
	super(name);
    }

    class ScalarListener implements IStringScalarListener {
	
	public void stringScalarChange(StringScalarEvent e) {
	    scalarValue = e.getValue();
	}

	public void errorChange(ErrorEvent e) {
	    fail("should not receive errors...");
	}

	public void stateChange(AttributeStateEvent e) {
	    state = e.getState();
	}

		    
    };

    protected void setUp() {
	super.setUp();
	try {
	    List l = AttributeFactory.getInstance().
		getEntities("eas/test-api/1/String_attr_w");
	    attribute = (IStringScalar)l.get(0);
	    attribute.setValue("ugle");
	} catch (Exception e ) {
	    e.printStackTrace();
	    throw new
		RuntimeException("Could not instanciate test system...");
	}
    }

    public void testInit() {
	assertTrue(attribute instanceof IScalarAttribute);
    }

    public void testSetValue() {
	try {
	    String val = "foo";
	    attribute.setValue(val);
	    String s = attribute.getStringValue();
	    assertTrue("foo".equals(s));
	} catch (Exception e) {
	    fail("Couldn't setValue()");
	}

    }

	    
    public void testPropertyChange() {
	try {
	    attribute.setValue("1234");

	    ScalarListener sl;
	    sl = new ScalarListener();

	    attribute.addStringScalarListener(sl);
	    attribute.setValue("foo");
	
	    assertTrue("foo".equals(scalarValue));
	}  catch (Exception e) {
	    fail("Couldn't setValue()");
	}

    }
	
    public static Test suite() {
	TestSuite suite = new TestSuite();
	
	suite.addTest(new ScalarAttributeTest("testInit"));
	suite.addTest(new ScalarAttributeTest("testSetValue"));
	suite.addTest(new ScalarAttributeTest("testPropertyChange"));
	return suite;
    }
    
    public static void main(String args[]) {
	junit.textui.TestRunner.run(suite());
    }
}
