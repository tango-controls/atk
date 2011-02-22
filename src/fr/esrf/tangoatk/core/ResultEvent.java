// File:          ResultEvent.java
// Created:       2002-02-01 16:19:47, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-27 11:46:31, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import java.util.EventObject;
import java.util.List;
public class ResultEvent extends ATKEvent {
    List result;
    
    public ResultEvent(ICommand source, List result, long timeStamp) {
	super(source, timeStamp);
	setResult(result);
    }

    public List getResult() {
	return result;
    }

    public void setResult(List result) {
	this.result = result;
    }

    public void setSource(ICommand source) {
	this.source = source;
    }
    
    public String getVersion() {
	return "$Id$";
    }
}
