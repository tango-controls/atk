// File:          IRefreshee.java
// Created:       2001-12-06 10:56:30, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-01-21 15:11:54, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

/**
 * The interface <code>IRefreshee</code> has one method
 * {@link #refresh} which is called by someone whenever it is 
 * time for the refreshee to refresh its listeners.
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version 1.0
 */
public interface IRefreshee {

    /**
     * refreshes the listeners of this particular object.
     *
     */
    public void refresh();
}
