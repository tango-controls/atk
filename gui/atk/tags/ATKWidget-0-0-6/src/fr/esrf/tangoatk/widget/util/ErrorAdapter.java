// File:          ErrorAdapter.java
// Created:       2002-04-25 14:56:45, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-05-16 10:51:38, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.util;
import javax.swing.table.AbstractTableModel;
import fr.esrf.tangoatk.core.*;
import fr.esrf.Tango.DevError;
import javax.swing.*;
import java.util.*;
import java.text.SimpleDateFormat;

class ErrorAdapter extends AbstractTableModel {
    boolean showError = true;
    boolean showWarning = true;
    boolean showPanic   = true;

    public void addError(ErrorEvent errorevent) {
	ErrorEvent errorevent1 = null;
	if (!wanted(errorevent)) return;
	
	if (errors.size() > 0)
	    errorevent1 = (ErrorEvent)errors.get(errors.size() - 1);
	
	if (errorevent1 != null &&
	    errorevent.getError().toString().
	    equals(errorevent1.getError().toString())) {
	    errorevent1.setTimeStamp(errorevent.getTimeStamp());
	    fireTableRowsUpdated(errors.size() - 1, errors.size() - 1);
	    return;
	} 
	errors.add(errorevent.clone());
	fireTableRowsInserted(errors.size() - 1, errors.size());
    }

    protected boolean wanted(ErrorEvent evt) {
	Throwable t = evt.getError();

	if (!(t instanceof ATKException)) return true;

	ATKException  exp = (ATKException)t;
	
	
	if (exp.getSeverity() == ATKException.PANIC && showPanic)
	    return true;

	if (exp.getSeverity() == ATKException.ERROR && showError)
	    return true;
	
	if (exp.getSeverity() == ATKException.WARNING && showWarning)
	    return true;
	
	return false;
    }

    public void showPanic(boolean b) {
	showPanic = b;
    }

    public void showError(boolean b) {
	showError = b;
    }

    public void showWarning(boolean b) {
	showWarning = b;
    }
    
    public DevError[] getErrorNumber(int i) {
	Throwable throwable = ((ErrorEvent)errors.get(i)).getError();

	if (!(throwable instanceof ATKException))  return null;

	return ((ATKException)throwable).getErrors();
    }

    public void setTable(JTable jtable) {
	table = jtable;
    }

    public Object getValueAt(int i, int j) {
	ErrorEvent errorevent = (ErrorEvent)errors.get(i);
	Throwable throwable = errorevent.getError();

	Object obj = errorevent.getSource();
	switch (j) {
	case 0: 
	    return getTime(errorevent);

	case 1: 
	    return getSeverity(errorevent);

	case 2: 
	    return getMessage(errorevent);

	case 3: 
	    return getSource(errorevent);

	case 4: 
	    return getOrigin(errorevent);

	case 5:
	    return getReason(errorevent);
	}
	return "";
    }

    public void setTimeFormat(SimpleDateFormat simpledateformat) {
	format = simpledateformat;
    }

    public SimpleDateFormat getTimeFormat()  {
	return format;
    }

    public String getTime(ErrorEvent errorevent) {
	Date date = new Date(errorevent.getTimeStamp());
	return format.format(date);
    }

    public String getSeverity(ErrorEvent errorevent) {
	Throwable throwable = errorevent.getError();
	if (!(throwable instanceof ATKException)) return "";
	return ATKException.severity[((ATKException)throwable).getSeverity()];
    }

    public String getMessage(ErrorEvent errorevent) {
	Throwable throwable = errorevent.getError();

	if (!(throwable instanceof ATKException))
	    return throwable.toString();

	return ((ATKException)throwable).getDescription();
    }

    public Object getSource(ErrorEvent errorevent) {
	return errorevent.getSource();
    }

    public String getOrigin(ErrorEvent errorevent) {
	Throwable throwable = errorevent.getError();
	if (!(throwable instanceof ATKException))
	    return "";
	return ((ATKException)throwable).getOrigin();
    }

    public String getReason(ErrorEvent errorevent) {
	Throwable throwable = errorevent.getError();
	if (!(throwable instanceof ATKException))
	    return "";
	
	return ((ATKException)throwable).getReason();
    }

    public int getRowCount() {
	return errors.size();
    }

    public int getColumnCount() {
	return columnNames.length;
    }

    public String getColumnName(int i) {
	return columnNames[i];
    }

    IErrorListener listener;
    JTable table;
    final int TIME = 0;
    final int SEVERITY = 1;
    final int MESSAGE = 2;
    final int SOURCE = 3;
    final int ORIGIN = 4;
    final int REASON = 5;
    String columnNames[] = { "Time",
			     "Severity",
			     "Message",
			     "Source",
			     "Origin",
			     "Reason"
    };

    java.util.List errors;
    SimpleDateFormat format;
    
    ErrorAdapter() {
	format = new SimpleDateFormat("HH:mm:ss");
	errors = new Vector();
    }
}


