// File:          BeanPropertyVerifier.java
// Created:       2002-03-22 10:22:56, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-11 15:20:55, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.util;
import java.beans.*;
import java.lang.reflect.*;
import java.util.*;

public class BeanPropertyVerifier {
    Class bean;
    SimpleBeanInfo beanInfo;
    Map methods = new HashMap();

    public BeanPropertyVerifier(String beanName, String beanInfoName)
	throws Exception {
	bean = Class.forName(beanName);
	beanInfo =
	    (SimpleBeanInfo)Class.forName(beanInfoName).newInstance();
	Method [] m = bean.getMethods();
	String fqName = beanName + ".";
	for (int i = 0; i < m.length; i++) {
	    methods.put(m[i].getName(), m[i]);
	    
	} // end of for ()
	
	
    }
    


    public boolean verify() {

	return verifyProperties();

    }

    public String stripQualifier(String methodName) {

	if (methodName.lastIndexOf(".") == -1) return methodName;

	String method = methodName.substring(methodName.lastIndexOf("."));
	return method;
    }

    public boolean verifyProperties() {
	PropertyDescriptor [] properties = beanInfo.getPropertyDescriptors();
	PropertyDescriptor property;
	boolean result = true;

	if (properties == null) return true;
	
	for (int i = 0; i < properties.length; i++) {
	    property = properties[i];
	    String readMethod = null;
	    String writeMethod = null;
	    try {
		readMethod =
		    stripQualifier(property.getReadMethod().getName());
		result = result &&
		    verifyReadMethod((Method)methods.get(readMethod));
	    } catch (NullPointerException e) {
		result = false;
	    } 
	    
	    try {
		writeMethod =
		    stripQualifier(property.getWriteMethod().getName());
		result = result &&
		verifyWriteMethod((Method)methods.get(writeMethod));
	    } catch (NullPointerException e) {
		result = false;
	    } // end of try-catch

	} // end of for ()

	return result;
    }

    public boolean verifyReadMethod(Method method) {
	Class [] params = method.getParameterTypes();
	if (params.length != 0) {
	    System.out.println(bean.getName() + " does not have a " +
			       method.getName() + " with 0 argument");
	    return false;
	}

	
	return true;
    }

    public boolean verifyWriteMethod(Method method) {
	Class [] params = method.getParameterTypes();
	if (params.length != 1) {
	    System.out.println(bean.getName() + " does not have a " +
			       method.getName() + " with 1 argument");
	    return false;
	}

	return true;
    }

    
    public static void main (String[] args) throws Exception {
	String bean = args[0];
	String beanInfo = bean + "BeanInfo"; 
	String out = "Verifying " + bean;
	System.out.print(out);
	for (int i = 0; i < 75 - out.length(); i++) {
	    System.out.print(".");
	}

	if (new BeanPropertyVerifier(bean, beanInfo).verify()) {
	    System.out.println("Ok");
	    System.exit(0);
	}

	System.exit(1);

    } // end of main ()
    
}
