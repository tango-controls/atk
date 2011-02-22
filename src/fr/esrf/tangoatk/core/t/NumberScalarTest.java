// File:          NumberScalarTest.java
// Created:       2002-01-24 11:58:09, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-15 17:13:0, assum>
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

public class NumberScalarTest extends CoreTestCase {
    INumberScalar attribute;
    double[][] d = new double[1][1];
    double[][] imagevalue;
    double[]   spectrumvalue;
    double     scalarvalue = Double.NaN;
    String state = null;
    String imageproperty, scalarproperty, spectrumproperty;
    
    class NumberScalarListener implements INumberScalarListener {

	public void numberScalarChange(NumberScalarEvent e) {
	    INumberScalar source = (INumberScalar)e.getSource();
	    scalarvalue = e.getValue();
	    spectrumvalue = source.getSpectrumValue();
	    imagevalue = source.getValue();
	}

	public void errorChange(ErrorEvent e) {
	    fail("Should not receive propertychangeevent");
	}

	public void stateChange(AttributeStateEvent e) {
	    state = e.getState();
	}
	
    }

    class NumberSpectrumListener implements ISpectrumListener {

	public void spectrumChange(NumberSpectrumEvent e) {
	    INumberSpectrum source = (INumberSpectrum)e.getSource();
	    spectrumvalue = e.getValue();
	    imagevalue = source.getValue();
	}

	public void errorChange(ErrorEvent e) {
	    fail("should not receive propertychangeevent");
	}

	public void stateChange(AttributeStateEvent e) {
	    state = e.getState();
	}
	
    }


    public NumberScalarTest(String name) {
	super(name);
    }

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


    public void testSetMaxValue() {
	d[0][0] = 200;
	MyErrorListener l = new MyErrorListener();
	attribute.addErrorListener(l);
	double oldMax = attribute.getMaxValue();
	try {

	    double[][] d2;
	    attribute.setMaxValue(100);
	    assertTrue(attribute.getMaxValue() == 100);
	    
	    attribute.setValue(d);
	    d2 = attribute.getValue();

	    assertTrue(l.gotError);
	    assertTrue("API_WAttrOutsideLimit".equals(l.reason));
	    assertTrue(d2[0][0] != 200);
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("Couldn't setMaxValue()");
	}
	attribute.setMaxValue(oldMax);
    }

    public void testSetMinValue() {
	d[0][0] = -200;
	MyErrorListener l = new MyErrorListener();
	double oldMin = attribute.getMinValue();
	try {

	    double[][] d2;
	    attribute.setMinValue(-100);
	    assertTrue(attribute.getMinValue() == -100);
	    attribute.addErrorListener(l);
	    attribute.setValue(d);
	    assertTrue(l.gotError);
	    assertTrue("API_WAttrOutsideLimit".equals(l.reason));
	    d2 = attribute.getValue();
	    assertTrue(d2[0][0] + " should not be -200", d2[0][0] != -200);
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("Couldn't setMaxValue()");
	}
	attribute.setMinValue(oldMin);
    }

    public void testMinAlarm() {
	double oldMinAlarm = attribute.getMinAlarm();
	double d = 4;
	NumberScalarListener nls = new NumberScalarListener();
	attribute.addNumberScalarListener(nls);
	state = null;
	attribute.setMinAlarm(5);

	assertTrue(attribute.getMinAlarm() == 5);
	try {
	    attribute.setValue(d);
	    assertEquals("State should be alarm: " + state, "ALARM", state);
	    attribute.setMinAlarm(-100);
	    assertEquals("State should be valid: " + state, "VALID", state);
	} catch (Exception e) {
	    e.printStackTrace();
	}
     }
    
    public void testMaxAlarm() {
	NumberScalarListener nls = new NumberScalarListener();
		    
	double oldMinAlarm = attribute.getMinAlarm();
	d[0][0] = 6;
	attribute.setMaxAlarm(5);
	state = null;
	attribute.addNumberScalarListener(nls);
	assertTrue(attribute.getMaxAlarm() == 5);
	try {
	    attribute.setValue(d);
	    assertEquals("State should be alarm: " + state, "ALARM", state);
	    attribute.setMaxAlarm(100);
	    assertEquals("State should be valid: " + state, "VALID", state);
	} catch (Exception e) {
	    e.printStackTrace();
	}
     }
    

    class NumberImageListener implements IImageListener {

	public void errorChange(ErrorEvent e) {
	    fail("should not receive propertychangeevent");
	}

	public void imageChange(NumberImageEvent e) {
	    INumberImage source = (INumberImage)e.getSource();
	    imagevalue = e.getValue();
	}


	public void stateChange(AttributeStateEvent e) {
	    state = e.getState();
	}

    }

    

    protected void setUp() {
	super.setUp();
	try {
	    MyErrorListener lis = new MyErrorListener();
	    List l = AttributeFactory.getInstance().
		getEntities("eas/test-api/1/Att_eas");
	    attribute = (INumberScalar)l.get(0);
	    attribute.addErrorListener(lis);
	    attribute.setMinValue(-1000);
	    attribute.setMaxValue(1000);
	    attribute.setValue(0);
	    assertTrue("should not get any errors here " + lis.reason, lis.reason == null);
	    double val = attribute.getNumberScalarValue();
	    assertTrue(val + "should be 0", val == 0);
	} catch (Exception e ) {
	    e.printStackTrace();
	    throw new
		RuntimeException("Could not instanciate test system...");
	}
    }

    public void testInit() {
	assertTrue(attribute instanceof INumberScalar);
    }

    public void testSetNumberScalarValue() {
	NumberScalarListener l = new NumberScalarListener();

	attribute.addNumberScalarListener(l);
	attribute.setValue(5);
	attribute.refresh();
	assertTrue(scalarvalue + " should be 5", scalarvalue == 5);
	assertTrue(spectrumvalue[0] == 5);
	assertTrue(imagevalue[0][0] == 5);
    }
	
    public void testSetNumberSpectrumValue() {
	NumberSpectrumListener l = new NumberSpectrumListener();
	attribute.addSpectrumListener(l);
	attribute.setValue(5);
	attribute.refresh();
	assertTrue(spectrumvalue[0] == 5);
	assertTrue(imagevalue[0][0] == 5);
    }

    public void testSetNumberImageValue() {
	NumberImageListener l = new NumberImageListener();
	attribute.addImageListener(l);
	attribute.setValue(5);
	attribute.refresh();
	assertTrue(imagevalue[0][0] == 5);
    }

    public static Test suite() {
	TestSuite suite = new TestSuite();
	suite.addTest(new NumberScalarTest("testInit"));	
	suite.addTest(new NumberScalarTest("testSetMinValue"));
 	suite.addTest(new NumberScalarTest("testSetMaxValue"));
	suite.addTest(new NumberScalarTest("testMinAlarm"));
	suite.addTest(new NumberScalarTest("testMaxAlarm"));
	suite.addTest(new NumberScalarTest("testSetNumberScalarValue"));
	suite.addTest(new NumberScalarTest("testSetNumberSpectrumValue"));
	suite.addTest(new NumberScalarTest("testSetNumberImageValue"));

	return suite;
    }
    
    public static void main(String args[]) {
	junit.textui.TestRunner.run(suite());
    }
}
