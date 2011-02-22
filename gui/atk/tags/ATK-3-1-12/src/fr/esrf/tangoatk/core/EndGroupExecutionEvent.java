// File:          EndGroupExecutionEvent.java
// Created:       2006-04-07 10:31:47, poncet
// 
// $Id $
// 
// Description:
       
package fr.esrf.tangoatk.core;

import java.util.EventObject;
import java.util.List;

public class EndGroupExecutionEvent extends ATKEvent
{
    List result;
    
    public EndGroupExecutionEvent(ICommandGroup source, List result, long timeStamp) {
	super(source, timeStamp);
	setResult(result);
    }

    public List getResult() {
	return result;
    }

    public void setResult(List result) {
	this.result = result;
    }

    public void setSource(ICommandGroup source) {
	this.source = source;
    }
    
    public String getVersion() {
	return "$Id$";
    }
}
