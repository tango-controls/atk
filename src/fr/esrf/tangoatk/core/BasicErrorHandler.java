// File:          BasicErrorHandler.java
// Created:       2001-10-04 15:04:22, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-23 15:31:48, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import org.apache.log4j.*;
import org.apache.log4j.spi.*;
import org.apache.log4j.varia.*;
import fr.esrf.Tango.*;

public class BasicErrorHandler implements IErrorListener {
    IErrorHandler handler;

    public void setErrorHandler(IErrorHandler h) {
	handler = h;
    }

    public IErrorHandler getErrorHandler(IErrorHandler h) {
	return handler;

    }
    
    public BasicErrorHandler() {
	handler = (IErrorHandler) new ErrorHandlerAdapter() {
		protected void showError(Exception e) {
		    System.err.println(e);
		    e.printStackTrace();
		}
	    };
    }
	
    public void errorChange(ErrorEvent e) {
	Throwable value = e.getError();

	if (value instanceof ATKException) {

	    if (value instanceof ConnectionException) {
		handler.connectionException((ConnectionException)value);
		return;
	    }
	    
	    if (value instanceof AttributeErrorException) {
		handler.attributeErrorException
		    ((AttributeErrorException)value);
		return;
	    }
	    
	    if (value instanceof AttributeReadException) {
		handler.attributeReadException((AttributeReadException)value);
		return;
	    }
	    
	    if (value instanceof AttributeSetException) {
		handler.attributeSetException((AttributeSetException)value);
		return;
	    }
	    
	    if (value instanceof CommandExecuteException) {
		handler.commandExecuteException
		    ((CommandExecuteException)value);
		return;
	    }
	    
	}
	handler.unknownException((Exception)value);
    }

    public String getVersion() {
	return "$Id$";
    }
    
}
