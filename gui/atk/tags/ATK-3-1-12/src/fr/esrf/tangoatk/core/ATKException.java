// File:          ATKException.java
// Created:       2001-10-17 11:31:23, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-10 14:8:17, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core;

import fr.esrf.Tango.*;

/**
 * A base class to handle error in ATK.
 */
public class ATKException extends Exception {

  private DevError[] errors = new DevError[0];
  private Object source=null;

  public static String[] severity = {"WARNING", "ERROR", "PANIC"};

  public static final int WARNING = 0;
  public static final int ERROR = 1;
  public static final int PANIC = 2;

  /**
   * Constructs an empty ATK exception.
   */
  public ATKException() {
    super();
    source=this;
  }

  /**
   * Constructs an ATK exception containing a single message.
   * @param s Exception message.
   */
  public ATKException(String s) {
    super(s);
    source=this;
  }

  /**
   * Constructs an ATK exception from a Tango DevFailed exception.
   * @param e Tango exception
   */
  public ATKException(DevFailed e) {
    // Copy the stack trace
    setStackTrace(e.getStackTrace());
    source=e;

    if(e.errors!=null) {
      errors = e.errors;
    } else {
      System.out.println("ATKException.ATKException() : Cannot handle DevFailed with null stack.");
    }
  }

  /*
   * Constructs an ATK exception from a Java exception.
   */
  public ATKException(Exception e) {
    super(e.getMessage());
    source=e;

    // Copy the stack trace
    setStackTrace(e.getStackTrace());
  }

  /**
   * Apply the given Tango DevFailed exception to this exception.
   * @param e Tango exception
   */
  public void setError(DevFailed e) {
    // Copy the stack trace
    setStackTrace(e.getStackTrace());
    source=e;

    if(e.errors!=null) {
      errors = e.errors;
    } else {
      System.out.println("ATKException.setError() : Cannot handle DevFailed with null stack.");
    }
  }

  /**
   * Returns the error stack.
   */
  public DevError[] getErrors() {
    return errors;
  }

  /**
   * Returns the Exception message. (Not from the stack)
   */
  public String getMessage() {
    String s = toString();
    if(s==null) {
      return getSourceName();
    } else {
      return s;
    }
  }

  /**
   * Returns the severity of this exception.
   */
  public int getSeverity() {
    return getSeverity(0);
  }

  /**
   * Gets the sevrity at the given stack level of this exception.
   * @param i Stack level
   * @see #WARNING
   * @see #ERROR
   * @see #PANIC
   */
  public int getSeverity(int i) {

    try {
      return errors[i].severity.value();
    } catch (Exception e) {
      return 1;
    } // end of try-catch

  }

  /**
   * Returns the description at the top level of the stack or the
   * message if no stack is present.
   */
  public String getDescription() {
    if( errors.length==0 ) {
      return getMessage();
    } else {
      return getDescription(0);
    }
  }

  /**
   * Returns the description at the specified level of the stack.
   * @param i Stack level
   */
  public String getDescription(int i) {
    try {
      return errors[i].desc.trim();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Returns the origin at the top level of the stack.
   */
  public String getOrigin() {
    return getOrigin(0);
  }

  /**
   * Returns the origin at the specified level of the stack.
   * @param i Stack level
   */
  public String getOrigin(int i) {
    try {
      return errors[i].origin.trim();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Returns the reason at the top level of the stack.
   */
  public String getReason() {
    return getReason(0);
  }

  /**
   * Returns the reason at the specified level of the stack.
   * @param i Stack level
   */
  public String getReason(int i) {
    try {
      return errors[i].reason.trim();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Returns the tango stack length.
   */
  public int getStackLength() {
    return errors.length;
  }


  /**
   * Get the class name of the source exception.
   * @return Class name
   */
  public String getSourceName() {
    String ret = source.getClass().toString();
    return ret.substring(6);
  }

  public String toString() {

    if (errors.length==0) return super.getMessage();

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
