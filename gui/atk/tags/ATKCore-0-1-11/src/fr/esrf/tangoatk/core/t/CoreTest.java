// File:          CoreTest.java
// Created:       2001-10-05 10:35:38, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-05-22 16:16:18, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.t;
import junit.framework.*;
import fr.esrf.tangoatk.core.*;


public class CoreTest extends TestCase {

    public CoreTest(String name) {
	super(name);
    }

    public static Test suite() {
	TestSuite suite= new TestSuite();

	suite.addTest(DeviceFactoryTest.suite());
	suite.addTest(DeviceTest.suite());
	suite.addTest(AttributeListTest.suite());
	suite.addTest(CommandListTest.suite());
	suite.addTest(CommandFactoryTest.suite());
	suite.addTest(AttributeFactoryTest.suite());
	suite.addTest(ImageAttributeTest.suite());
	suite.addTest(NumberImageTest.suite());
	suite.addTest(NumberSpectrumTest.suite());
	suite.addTest(NumberScalarTest.suite());
	suite.addTest(ScalarAttributeTest.suite());
	suite.addTest(SpectrumAttributeTest.suite());
	suite.addTest(BasicErrorHandlerTest.suite());	
	return suite;
    }

    public static void main(String args[]) {
	CoreTestRunner.run(suite());
	System.exit(0);
    }
}
