// File:          NumberSpectrumTest.java
// Created:       2002-03-25 16:36:07, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-05-22 16:5:30, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.t;
import junit.framework.*;
import fr.esrf.tangoatk.core.*;
import java.util.*;


public class NumberSpectrumTest extends CoreTestCase {
    INumberSpectrum attribute;
    double   [] spectrumvalue;
    double [][] imagevalue;
    String state;
    class NumberSpectrumListener implements ISpectrumListener {


	public void spectrumChange(NumberSpectrumEvent e) {
	    INumberSpectrum source = (INumberSpectrum)e.getSource();
	    spectrumvalue = e.getValue();
	    imagevalue    = source.getValue();
	}

	public void errorChange(ErrorEvent e) {
	    fail("Should not receive propertychangeevent");
	}
	
	public void stateChange(AttributeStateEvent e) {
	    state = e.getState();
	}

    }

    public NumberSpectrumTest(String name) {
	super(name);
    }


    protected void setUp() {
	super.setUp();

	try {
	    AttributeList l = new AttributeList();
		l.add("eas/test-api/1/Short_spec_attr");
	    attribute = (INumberSpectrum)l.get(0);
	    spectrumvalue = new double[attribute.getMaxXDimension()];
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException("Could not instanciate test system...");
	}
    }

    public void testInit() {
	assertTrue(attribute instanceof INumberSpectrum);
    }

    public void testSetNumberSpectrumValue() {
	NumberSpectrumListener l = new NumberSpectrumListener();

	attribute.addSpectrumListener(l);
	attribute.refresh();
	assertTrue(spectrumvalue[0] == 10);
	assertTrue(spectrumvalue[1] == 20);
	assertTrue(spectrumvalue[2] == 30);
	assertTrue(spectrumvalue[3] == 40);

    }

    public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new NumberSpectrumTest("testInit"));	
	suite.addTest(new NumberSpectrumTest("testSetNumberSpectrumValue"));

	return suite;
    }
    
    public static void main(String args[]) {
	junit.textui.TestRunner.run(suite());
    }
}
