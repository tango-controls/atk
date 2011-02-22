// File:          AImageNumberAttributeTest.java
// Created:       2001-10-29 16:24:15, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:33:57, assum>
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
import org.apache.log4j.*;
import org.apache.log4j.spi.*;
import fr.esrf.Tango.*;
import java.beans.*;

public class NumberImageTest extends CoreTestCase {
    INumberImage attribute;
    double [][] d = new double[1][1];

    final class MyErrorListener implements IErrorListener {
	public boolean gotError = false;
	public String reason = null;
	public String desc = null;
	public void errorChange(ErrorEvent e) {
	    gotError = true;
	    DevError []es = ((ATKException)e.getError()).getErrors();
	    reason = es[0].reason;
	    desc = es[0].desc;
	}
    }


    public NumberImageTest(String name) {
	super(name);
    }


    protected void setUp() {
	super.setUp();
	try {
	    List l = AttributeFactory.getInstance().
		getEntities("eas/test-api/1/Att_eas");
	    attribute = (INumberImage)l.get(0);
	    d[0][0] = 8;
	    attribute.setValue(d);
	    d = attribute.getValue();
	    assertTrue(d[0][0] + "should be 8", d[0][0] == (double)8);
	} catch (Exception e ) {
	    System.out.println(e);
	    e.printStackTrace();
	    throw new
		RuntimeException("Could not instanciate test system...");
	}
    }


    public static Test suite() {
	TestSuite suite = new TestSuite();
	return suite;
    }
    
    public static void main(String args[]) {
	junit.textui.TestRunner.run(suite());
    }
}


