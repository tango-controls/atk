// File:          ATKException.java
// Created:       2001-10-17 11:31:23, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-01 16:51:30, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import fr.esrf.Tango.*;
import fr.esrf.TangoDs.*;
import org.apache.log4j.Logger;
public class ATKException extends Exception {
    private static Logger log =
	ATKLogger.getLogger(ATKException.class.getName());

    private DevError[] errors;

    public static String [] severity = {"WARNING", "ERROR", "PANIC" };

    public static final int WARNING = 0;
    public static final int ERROR   = 1;
    public static final int PANIC   = 2;
    
    public ATKException() {
	super();
    }


    public ATKException(String s) {
	super(s);
    }

    public ATKException (DevFailed e) {
	errors = e.errors;
    }

    public DevError[] getErrors() {
	return errors;
    }

    public String getMessage() {
	return toString();
    }

    public int getSeverity() {
	return getSeverity(0);
    }

    public int getSeverity(int i) {
	try {
	    return errors[i].severity.value();	     
	} catch (Exception e) {
	    return 1;
	} // end of try-catch
	

    }

    public String getDescription() {
	try {
	    return getDescription(0);
	} catch (Exception e) {
	    return "";
	}
    }

    public String getDescription(int i) {
	try {
	    return errors[i].desc.trim();
	} catch (Exception e) {
	    return "";
	}
    }

    public String getOrigin() {
	return getOrigin(0);
    }

    public String getOrigin(int i) {
	try {
	    return errors[i].origin.trim();
	} catch (Exception e) {
	    return "";
	}
    }

    public String getReason() {
	return getReason(0);
    }

    public String getReason(int i) {
	try {
	    return errors[i].reason.trim();
	} catch (Exception e) {
	    return "";
	}
    }

    public int getStackLength() {
	try {
	    return errors.length;
	} catch (Exception e) {
	    return 0;
	}
    }

    
    public String toString() {

	if (errors == null) return super.getMessage();

	StringBuffer buff = new StringBuffer();
	for (int i = 0; i < errors.length; i++) {
	    DevError e = errors[i];
	    buff.append("Severity: ");
	    buff.append(severity[e.severity.value()]);
	    buff.append("\nOrigin: ");
	    buff.append(e.origin.trim());
	    buff.append("\nDescription: ");
	    buff.append(e.desc.trim());
	    buff.append("\nReason: ");
	    buff.append(e.reason.trim());
	    buff.append("\n");
	}
	return buff.toString();
    }

    public String getVersion() {
	return "$Id$";
    }
    
}
