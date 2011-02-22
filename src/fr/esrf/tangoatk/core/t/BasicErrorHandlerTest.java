// File:          BasicErrorHandlerTest.java
// Created:       2001-10-22 15:26:44, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-21 17:57:12, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.t;
import junit.framework.*;
import fr.esrf.tangoatk.core.*;
import org.apache.log4j.*;
import org.apache.log4j.spi.*;
import fr.esrf.Tango.*;
import java.util.*;
public class BasicErrorHandlerTest extends CoreTestCase {




    protected BasicErrorHandlerTest(String name) {
	super(name);
    }
    
    protected void setUp() {
	BasicLogAppender.DO_OUTPUT = false;
 	BasicLogAppender.USE_SWING = false;
 	Logger.getDefaultHierarchy().disable("ERROR");
 	Logger.getDefaultHierarchy().disable("DEBUG");
    }

    public void testATKException() {
	log.getRootLogger().removeAllAppenders();
	log =  Logger.getLogger("test");
	log.removeAllAppenders();

	final TestChecker tc = new TestChecker();

	assertTrue(!tc.getSeen());

	BasicLogAppender beh =
	    new BasicLogAppender() {
		protected String formatATKException(ATKException e) {
		    assertTrue(e != null);
		    assertTrue(e instanceof ATKException);
		    DevError [] errors = e.getErrors();
		    assertTrue(errors.length == 1);
		    assertTrue(errors[0].severity.value() == 1);
		    assertTrue(errors[0].origin.equals("origin"));
		    assertTrue(errors[0].desc.equals("desc"));
		    assertTrue(errors[0].reason.equals("reason"));
		    tc.setSeen(true);
		    return "";
		}
	    };
	
	log.addAppender(beh);

	Log4jConfigurator.addLogger(log);
	DevError [] errors = new DevError[1];
	errors[0] = new DevError("reason", ErrSeverity.ERR, "desc",
				   "origin");
	DevFailed d = new DevFailed(errors);
	
	log.error("", new ConnectionException(d));

	assertTrue(tc.getSeen());
    }

    public static Test suite() {
	TestSuite suite= new TestSuite();
	suite.addTest(new BasicErrorHandlerTest("testATKException"));
	return suite;
    }

    public static void main(String args[]) {
	CoreTestRunner.run(suite());
    }

}

	  
class TestChecker {
    boolean seen = false;
	    
    void setSeen(boolean b) {
	seen = b;
    }
    
    boolean getSeen() {
	return seen;
    }
};

