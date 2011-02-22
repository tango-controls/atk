// File:          IApplicable.java
// Created:       2002-07-03 11:23:44, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-08 16:31:9, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.util;

/**
 * <code>IApplicable</code> is an interface which tells the ButtonBar
 * that the widget implementing this interface needs an apply and a cancel
 * button in addition to the ok button demanded by the IControlee
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @see ButtonBar
 * @see IControlee
 * @version $Revision$
 */
public interface IApplicable extends IControlee {

    /**
     * <code>apply</code> is called when the user presses the apply
     * button. Expected behaviour is to set all values on the 
     * model of the widget
     */
    public void apply();

    /**
     * <code>cancel</code> is called when the user presses the cancel
     * button. Expected behaviour is to close the window, eg
     * <code>getRootPane().getParent().setVisible(false);</code>
     *
     */
    public void cancel();
}
