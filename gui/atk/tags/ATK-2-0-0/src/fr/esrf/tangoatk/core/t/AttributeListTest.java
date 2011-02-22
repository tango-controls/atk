// File:          AttributeListeTest.java
// Created:       2001-10-05 13:15:44, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-27 19:35:29, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core.t;
import junit.framework.*;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.*;

public class AttributeListTest extends CoreTestCase {
    AttributeList attributeList;
    String goodAttribute = "eas/test-api/1/Double_attr_w";
    String goodAttribute1 = "eas/test-api/1/String_attr_w";

    protected AttributeListTest(String name) {
	super(name);
    }
    
    protected void setUp() {
	super.setUp();
	attributeList = new AttributeList();
    }

    public void testInit() {
	assertTrue(attributeList != null);
    }

    public void testAdd() {
	try {
	    attributeList.add(goodAttribute);
	    assertTrue(attributeList.size() == 1);
	} catch (Exception e) {
	    fail("coudn't add attribute " + goodAttribute);
	}
    }

    public void testAddEqual() {
	try {
	    attributeList.add(goodAttribute);
	    attributeList.add(goodAttribute);
	    assertTrue(attributeList.size() == 1);
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add attribute " + goodAttribute + " " + e);

	}
    }

    public void testAddTwo() {
	try {
	    attributeList.add(goodAttribute);
	    attributeList.add(goodAttribute1);
	    assertTrue(attributeList.size() == 2);
	    assertTrue(attributeList.get(0) != attributeList.get(1));
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add attribute " + goodAttribute + " " + e);

	}
    }

    public void testAddArray() {
	String [] names = new String[2];
	names[0] = goodAttribute;
	names[1] = goodAttribute1;
	try {
	    attributeList.add(names);
	    assertTrue(attributeList.size() == 2);
	    assertTrue(attributeList.get(0) != attributeList.get(1));
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add attribute " + goodAttribute + " " + e);

	}
    }

    public void testAddEqualArray() {
	String [] names = new String[2];
	names[0] = goodAttribute;
	names[1] = goodAttribute;
	try {
	    attributeList.add(names);
	    assertTrue(attributeList.size() == 1);
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add attribute " + goodAttribute + " " + e);

	}
    }

    public void testAddNullArray() {
	String [] names = new String[2];
	names[0] = goodAttribute;

	try {
	    attributeList.add(names);
	    assertTrue(attributeList.size() == 1);
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add attribute " + goodAttribute + " " + e);

	}
    }

    public void testRemove() {
	try {
	    attributeList.add(goodAttribute);
	    assertTrue(attributeList.size() == 1);
	    attributeList.remove(goodAttribute);
	    assertTrue(attributeList.size() == 0);
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add attribute " + goodAttribute + " " + e);

	}
    }
    
    public void testGetIndex() {
	try {
	    attributeList.add(goodAttribute);
	    assertTrue(attributeList.size() == 1);
	    IEntity e = (IEntity)attributeList.get(0);
	    assertTrue(e.getName().equals(goodAttribute));
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add attribute " + goodAttribute + " " + e);

	}
    }

    public void testGetName() {
	try {
	    attributeList.add(goodAttribute);
	    assertTrue(attributeList.size() == 1);
	    IEntity e = attributeList.get(goodAttribute);
	    assertTrue(e.getName().equals(goodAttribute));
	} catch (Exception e) {
	    e.printStackTrace();
	    fail("coudn't add attribute " + goodAttribute + " " + e);

	}
    }

    public void testGetSingleAttribute() {
	try {
	    IAttribute attr = (IAttribute)attributeList.add(goodAttribute);
	    assertEquals(attr.getName() + " should equal " + goodAttribute,
			attr.getName(), goodAttribute);

	    assertEquals(attributeList.size() + " should be 1",
			 attributeList.size(), 1);
	} catch (Exception e) {
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
	    attributeList.add(device1);
	    IAttribute attr = (IAttribute)attributeList.get(attr1);
	    assertEquals("the names should be equal, ",
			 attr.getName(), attr1);
	    attributeList.clear();
	    attributeList.add(device2);
	    attr = (IAttribute)attributeList.get(attr2);
	    assertEquals("the names should be equal, ",
			 attr.getName(), attr2);
	} catch (Exception  e) {
	    e.printStackTrace();
	    fail("should not throw exception here" + e);
	} // end of try-catch
    }

    public void testRefresher() {
	attributeList.startRefresher();
	attributeList.stopRefresher();
    }
    
    public static Test suite() {
	TestSuite suite= new TestSuite();
	
	suite.addTest(new AttributeListTest("testInit"));
	suite.addTest(new AttributeListTest("testAdd"));
	suite.addTest(new AttributeListTest("testAddEqual"));
	suite.addTest(new AttributeListTest("testAddTwo"));
	suite.addTest(new AttributeListTest("testAddArray"));
	suite.addTest(new AttributeListTest("testAddEqualArray"));
	suite.addTest(new AttributeListTest("testAddNullArray"));
	suite.addTest(new AttributeListTest("testRemove"));
	suite.addTest(new AttributeListTest("testGetIndex"));
	suite.addTest(new AttributeListTest("testGetName"));
	suite.addTest(new AttributeListTest("testGetSingleAttribute"));
	suite.addTest(new AttributeListTest("testReuseList"));
	suite.addTest(new AttributeListTest("testRefresher"));
	return suite;
    }
    
    public static void main(String args[]) {
	CoreTestRunner.run(suite());
    }
    
}
