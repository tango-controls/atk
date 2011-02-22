
// File:          AttributeListeTest.java
// Created:       2001-10-05 13:15:44, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-21 20:24:58, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.t;
import junit.framework.*;
import fr.esrf.tangoatk.core.*;
import org.apache.log4j.*;

public class CommandListTest extends CoreTestCase {
    CommandList commandList;
    String goodCommand = "eas/test-api/1/State";
    String goodCommand1 = "eas/test-api/1/Status";

    protected CommandListTest(String name) {
	super(name);
    }
    
    protected void setUp() {
	super.setUp();
	commandList = new CommandList();
    }

    public void testInit() {
	assertTrue(commandList != null);
    }

    
    public void testAdd() {
	try {
	    commandList.add(goodCommand);
	    assertTrue(commandList.size() == 1);
	} catch (Exception e) {
	    fail("coudn't add command " + goodCommand);
	}
    }

    public void testAddEqual() {
	try {
	    commandList.add(goodCommand);
	    commandList.add(goodCommand);
	    assertTrue(commandList.size() == 1);
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add command " + goodCommand + " " + e);

	}
    }

    public void testAddTwo() {
	try {
	    commandList.add(goodCommand);
	    commandList.add(goodCommand1);
	    assertTrue(commandList.size() == 2);
	    assertTrue(commandList.get(0) != commandList.get(1));
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add command " + goodCommand + " " + e);

	}
    }

    public void testAddArray() {
	String [] names = new String[2];
	names[0] = goodCommand;
	names[1] = goodCommand1;
	try {
	    commandList.add(names);
	    assertTrue(commandList.size() == 2);
	    assertTrue(commandList.get(0) != commandList.get(1));
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add command " + goodCommand + " " + e);

	}
    }

    public void testAddEqualArray() {
	String [] names = new String[2];
	names[0] = goodCommand;
	names[1] = goodCommand;
	try {
	    commandList.add(names);
	    assertTrue(commandList.size() == 1);
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add command " + goodCommand + " " + e);

	}
    }

    public void testAddNullArray() {
	String [] names = new String[2];
	names[0] = goodCommand;

	try {
	    commandList.add(names);
	    assertTrue(commandList.size() == 1);
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add command " + goodCommand + " " + e);

	}
    }

    public void testRemove() {
	try {
	    commandList.add(goodCommand);
	    assertTrue(commandList.size() == 1);
	    commandList.remove(goodCommand);
	    assertTrue(commandList.size() == 0);
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add command " + goodCommand + " " + e);

	}
    }
    
    public void testGetIndex() {
	try {
	    commandList.add(goodCommand);
	    assertTrue(commandList.size() == 1);
	    IEntity e = (IEntity)commandList.get(0);
	    assertTrue(e.getName().equals(goodCommand));
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add command " + goodCommand + " " + e);

	}
    }

    public void testGetName() {
	try {
	    commandList.add(goodCommand);
	    assertTrue(commandList.size() == 1);
	    IEntity e = commandList.get(goodCommand);
	    assertTrue(e.getName().equals(goodCommand));
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add command " + goodCommand + " " + e);

	}
    }

    public void testGetSingleCommand() {
	try {
	    ICommand cmd = (ICommand)commandList.add(goodCommand);
	    assertEquals(cmd.getName() + " should equal " + goodCommand,
			cmd.getName(), goodCommand);

	    assertEquals(commandList.size() + " should be 1",
			 commandList.size(), 1);
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("should not throw exception here" + e);
	} // end of try-catch
	
    }

    public void testAddWildCard() {
	String device1 = "eas/test-api/1/*";
	try {
	    commandList.add(device1);
	    assertTrue(commandList.size() > 0);
	} catch (Exception  e) {
	    e.printStackTrace();
	    fail("should not throw exception here" + e);
	} // end of try-catch
    }
    
    public void testReuseList() {
	String device1 = "eas/test-api/1/*";
	String device2 = "eas/test-api/2/*";
	String attr1 = "eas/test-api/1/Att_sinus";
	String attr2 = "eas/test-api/2/Att_sinus";

	try {
	    commandList.add(device1);
	    ICommand attr = (ICommand)commandList.get(attr1);
	    assertEquals("the names should be equal, ",
			 attr.getName(), attr1);
	    commandList.clear();
	    commandList.add(device2);
	    attr = (ICommand)commandList.get(attr2);
	    assertEquals("the names should be equal, ",
			 attr.getName(), attr2);
	} catch (Exception  e) {
	    e.printStackTrace();
	    fail("should not throw exception here" + e);
	} // end of try-catch
    }

    public static Test suite() {
	TestSuite suite= new TestSuite();
	
	suite.addTest(new CommandListTest("testInit"));
	suite.addTest(new CommandListTest("testAdd"));
	suite.addTest(new CommandListTest("testAddEqual"));
	suite.addTest(new CommandListTest("testAddTwo"));
	suite.addTest(new CommandListTest("testAddArray"));
	suite.addTest(new CommandListTest("testAddEqualArray"));
	suite.addTest(new CommandListTest("testAddNullArray"));
	suite.addTest(new CommandListTest("testRemove"));
	suite.addTest(new CommandListTest("testGetIndex"));
	suite.addTest(new CommandListTest("testGetName"));
	suite.addTest(new CommandListTest("testGetSingleCommand"));
	suite.addTest(new CommandListTest("testAddWildCard"));
	return suite;
    }
    
    public static void main(String args[]) {
	CoreTestRunner.run(suite());
	System.exit(0);
    }
    
}
