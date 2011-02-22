// File:          ErrorHandlerAdapter.java
// Created:       2002-02-08 14:46:01, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-27 11:43:34, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

public abstract class ErrorHandlerAdapter implements IErrorHandler {

    public void connectionException(ConnectionException e) {
	showError(e);
    }

    public void attributeErrorException(AttributeErrorException e) {
	showError(e);
    }

    public void attributeReadException(AttributeReadException e) {
	showError(e);
    }

    public void attributeSetException(AttributeSetException e) {
	showError(e);

    }

    public void commandExecuteException(CommandExecuteException e) {
	showError(e);
    }

    public void unknownException(Exception e) {
	showError(e);
    }

    protected void showError(Exception e) {
	System.err.println(e);
	e.printStackTrace();
    }

    public String getVersion() {
	return "$Id$";
    }
}
