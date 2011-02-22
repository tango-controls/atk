// File:          IResultListener.java
// Created:       2002-01-17 16:25:21, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-14 11:18:47, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import java.beans.*;
import java.util.EventListener;

/**
 * <code>IResultListener</code> defines the behaviour of an object that listens to results from commands.
 * @see fr.esrf.tangoatk.core.ICommand
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Version$
 */
public interface IResultListener extends IErrorListener {

    /**
     * <code>resultChange</code> is called each time someone has
     * executed a command that does output which this resultlistener
     * is registered with.
     *
     * @param e a <code>ResultEvent</code> value
     */
    public void resultChange(ResultEvent e);

}
