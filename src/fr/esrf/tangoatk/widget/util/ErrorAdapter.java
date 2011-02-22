// File:          ErrorAdapter.java
// Created:       2002-04-25 14:56:45, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-05 15:6:41, assum>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.widget.util;

import javax.swing.table.AbstractTableModel;

import fr.esrf.tangoatk.core.*;
import fr.esrf.Tango.DevError;

import java.util.*;
import java.text.SimpleDateFormat;

// An inner class to handle error
class ErrorObject {

  private Throwable exception;
  private long      time;
  private String    source;
  private String    severity;

  ErrorObject(Throwable err,long t,String src,String severity) {
    time = t;
    exception = err;
    source=src;
    this.severity=severity;
  }

  public String toString() {
    return exception.toString();
  }

  public String getMessage() {

    if (!(exception instanceof ATKException))
      return exception.toString();

    return ((ATKException) exception).getDescription();

  }
  public String getSource() {
    return source;
  }
  public String getSeverity() {
    return severity;
  }
  public Throwable getError() {
    return exception;
  }
  public long getTime() {
    return time;
  }
  public void setTime(long t) {
    time=t;
  }

}

// An inner class to handle errors coming from the same source
class ErrorList {

  String source;  // Source of the error (Attribute,Device,Command name)
  Vector errors;  // List of errors (ErrorObject) belonging to the source

  ErrorList(String source) {
    this.source=source;
    errors=new Vector();
  }

  boolean addError(Throwable t,long timeStamp) {

    if(errors.size()>0) {
      ErrorObject last = (ErrorObject)errors.lastElement();
      if(last.toString().equals(t.toString())) {
        // Does not add same error
        // Simply update the error time
        last.setTime(timeStamp);
        return false;
      }
    }

    String severity = "";
    if (t instanceof ATKException)
      severity = ATKException.severity[((ATKException) t).getSeverity()];

    // Add the error
    errors.add( new ErrorObject(t,timeStamp,source,severity) );
    return true;

  }

  ErrorObject get(int idx) {
    return (ErrorObject)errors.get(idx);
  }

  int size() {
    return errors.size();
  }

  void removeFirst() {
    errors.remove(0);
  }

}

class ErrorAdapter extends AbstractTableModel {

  public static final int TIME = 0;
  public static final int SEVERITY = 1;
  public static final int SOURCE = 2;
  public static final int DESCRIPTION = 3;
  public static final int ORIGIN = 4;
  public static final int REASON = 5;

  // The error buffer size (number of errors saved before deciding to delete)
  // should at least be >= to MIN_ERROR_BUFFER_SIZE
  private static final int MIN_ERROR_BUFFER_SIZE = 100;

  String columnNames[] = {"Time", "Severity", "Source", "Description"};
  SimpleDateFormat format;

  int errorBufferSize;     // Buffer size
  int nbError;             // Total number of error
  Vector errors;           // The global errors array

  Vector filteredErrors;   // Filtered errors array
  int sortColumn = -1;     // No sort
  String visibleSource = null; // Filter source (null=no filter)
  boolean showPanic = true;
  boolean showError = true;
  boolean showWarning = true;

  ErrorPanel peerPanel = null;

  /**
   * Construct an error adapter.
   */
  ErrorAdapter() {

    format = new SimpleDateFormat("HH:mm:ss");
    errors = new Vector();
    filteredErrors = new Vector();
    errorBufferSize = MIN_ERROR_BUFFER_SIZE;
    nbError=0;

  }

  /**
   * Sets the error panel which handle this ErrorAdapter.
   * This will trigger updateSource() on the ErrorPanel
   * when the source list is modified.
   * @param p ErrorPanel
   */
  public void setErrorPanel(ErrorPanel p) {
    peerPanel = p;
  }

  /**
   * Clear all error present in this adapter.
   */
  synchronized void clearError() {
    errors = new Vector();
    filteredErrors = new Vector();
    nbError=0;
    fireSourceChange();
    updateFilters();
  }

  /**
   * Add an error to this error table.
   * @param errorevent Error to be added
   */
  synchronized public void addError(ErrorEvent errorevent) {

    String src = errorevent.getSource().toString();

    if(src==null) {
      System.out.println("ErrorAdapter.addError() : Warning cannot handle error with null source !");
      System.out.println("ErrorAdapter.addError() : " + errorevent.getError());
      return;
    }

    // Find the source
    boolean added;
    int i=getSourceIndex(src);

    if( i>=0 ) {
      added = ((ErrorList)errors.get(i)).addError(errorevent.getError(),errorevent.getTimeStamp());
    } else {
      // Create a new ErrorList
      ErrorList nList = new ErrorList(src);
      added = nList.addError(errorevent.getError(),errorevent.getTimeStamp());
      errors.add(nList);
      fireSourceChange();
    }

    // Truncate the array
    if(added) {

      if(nbError>=errorBufferSize) {

        // Find the oldest error
        int  imin=-1;
        long tmin=Long.MAX_VALUE;
        i=0;
        for(i=0;i<errors.size();i++) {
          ErrorList el = (ErrorList)errors.get(0);
          if(el.get(0).getTime()<tmin) {
            tmin=el.get(0).getTime();
            imin=i;
          }
        }

        // Remove it
        if(imin>=0) {
          ErrorList el = (ErrorList)errors.get(imin);
          el.removeFirst();
          if(el.size()==0) {
            // Remove this source
            errors.remove(el);
            fireSourceChange();
          }
        }

      } else {
        nbError++;
      }

    }

    updateFilters();

  }

  /**
   * @return The error buffer size.
   */
  public int getErrorBufferSize() {
    return errorBufferSize;
  }

  /**
   * Sets the Error buffer size.
   * @param bs Buffer size
   */
  public void setErrorBufferSize(int bs) {
    if (bs > MIN_ERROR_BUFFER_SIZE) {
      errorBufferSize = bs;
    }
  }

  /**
   * Sets the date format.
   * @param simpledateformat Date format
   */
  public void setTimeFormat(SimpleDateFormat simpledateformat) {
    format = simpledateformat;
  }

  /**
   * @return Current data format.
   */
  public SimpleDateFormat getTimeFormat() {
    return format;
  }

  /**
   * Sets the source to be displayed.
   * @param s Source (pass null for all source)
   */
  public void setSourceFilter(String s) {
    visibleSource = s;
    updateFilters();
  }

  /**
   * Sorts the given column.
   * @param column Column to be sorted (Pass -1 for no sort)
   */
  public void setSortedColumn(int column) {
    sortColumn = column;
    updateFilters();
  }

  /**
   * Display Panic message.
   * @param b True to display false otherwise.
   */
  public void showPanic(boolean b) {
    showPanic = b;
    updateFilters();
  }

  /**
   * Display Error message.
   * @param b True to display false otherwise.
   */
  public void showError(boolean b) {
    showError = b;
    updateFilters();
  }

  /**
   * Display Warning message.
   * @param b True to display false otherwise.
   */
  public void showWarning(boolean b) {
    showWarning = b;
    updateFilters();
  }

  /**
   * Return the error associated to the selected line.
   * @param i Selected line
   * @return A tango error stack or null.
   */
  public DevError[] getErrorNumber(int i) {
    Throwable throwable = ((ErrorObject) filteredErrors.get(i)).getError();
    if (!(throwable instanceof ATKException)) return null;
    return ((ATKException) throwable).getErrors();
  }

  /**
   * Return the error associated to the selected line.
   * @param i Selected line
   * @return A Throwable object
   */
  public Throwable getErrorAt(int i) {    
    return ((ErrorObject) filteredErrors.get(i)).getError();
  }

  /**
   * Returns an array of string containing all source.
   */
  public Vector getAllSource() {

    Vector ret = new Vector();
    for(int i=0;i<errors.size();i++)
      ret.add( ((ErrorList)errors.get(i)).source );
    return ret;

  }

  // ----------------------------------------------------------------
  // Table model
  // ----------------------------------------------------------------

  public Object getValueAt(int i, int j) {

    ErrorObject err = (ErrorObject) filteredErrors.get(i);

    switch (j) {
      case TIME:
        Date date = new Date(err.getTime());
        return format.format(date);

      case SEVERITY:
        return err.getSeverity();

      case DESCRIPTION:
        return err.getMessage();

      case SOURCE:
        return err.getSource();

    }
    return "";

  }

  public int getRowCount() {
    return filteredErrors.size();
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  public String getColumnName(int i) {
     return columnNames[i];
  }

  // ----------------------------------------------------------------
  // Private stuff
  // ----------------------------------------------------------------

  private int getSourceIndex(String src) {

    int i=0;
    boolean found=false;
    if(src==null) return -1;

    while(!found && i<errors.size()) {
      found = src.equalsIgnoreCase( ((ErrorList)errors.get(i)).source );
      if(!found) i++;
    }

    if(!found)
      return -1;
    else
      return i;

  }

  private void updateFilters() {

    Vector nErrors = new Vector();

    // Source filter
    int src = getSourceIndex(visibleSource);
    int i,j;

    if(src>=0) {
      ErrorList el = (ErrorList)errors.get(src);
      for(i=0;i<el.size();i++) insertError(nErrors,el.get(i));
    } else {
      // Get all source
      for(j=0;j<errors.size();j++) {
        ErrorList el = (ErrorList)errors.get(j);
        for(i=0;i<el.size();i++) insertError(nErrors,el.get(i));
      }
    }

    // Now update the table
    filteredErrors = nErrors;
    fireTableDataChanged();

  }

  private void insertError(Vector errs,ErrorObject e) {

    if(!wanted(e))
      return;

    int sz = errs.size();
    int i=0;
    int cp;
    boolean found=false;

    switch(sortColumn) {
      case TIME:
        while(!found && i<sz) {
          ErrorObject c = (ErrorObject)errs.get(i);
          found = e.getTime() <= c.getTime();
          if(!found) i++;
        }
        break;
      case SEVERITY:
        while(!found && i<sz) {
          ErrorObject c = (ErrorObject)errs.get(i);
          cp = e.getSeverity().compareToIgnoreCase(c.getSeverity());
          found = (cp<0) || (cp==0 && (e.getTime() <= c.getTime()));
          if(!found) i++;
        }
        break;
      case SOURCE:
        while(!found && i<sz) {
          ErrorObject c = (ErrorObject)errs.get(i);
          cp = e.getSource().compareToIgnoreCase(c.getSource());
          found = (cp<0) || (cp==0 && (e.getTime() <= c.getTime()));
          if(!found) i++;
        }
        break;
      case DESCRIPTION:
        while(!found && i<sz) {
          ErrorObject c = (ErrorObject)errs.get(i);
          cp = e.getMessage().compareToIgnoreCase(c.getMessage());
          found = (cp<0) || (cp==0 && (e.getTime() <= c.getTime()));
          if(!found) i++;
        }
        break;
      default:
        // No sort
        errs.add(e);
        return;
    }

    errs.add(i,e);

  }

  private boolean wanted(ErrorObject evt) {

    if (!showPanic && evt.getSeverity().equals(ATKException.severity[ATKException.PANIC]))
      return false;

    if (!showError && evt.getSeverity().equals(ATKException.severity[ATKException.ERROR]))
      return false;

    if (!showWarning && evt.getSeverity().equals(ATKException.severity[ATKException.WARNING]))
      return false;

    return true;

  }

  private void fireSourceChange() {
    if(peerPanel!=null)
      peerPanel.sourceChange();
  }

}


