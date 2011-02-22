// File:          IControlee.java
// Created:       2002-07-03 11:22:59, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-08 17:57:59, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.util;

/**
 * <code>IControlee</code> is an interface used by all widgets controlled
 * by the ButtonBar. Implementing this interface will allow a component
 * to be added to the ButtonBar.
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @see ButtonBar
 * @see IHelpful
 * @see IApplicable
 * @version $Revision$
 */
public interface IControlee {

    /**
     * <code>ok</code> will be called when the component is visible and 
     * the user presses the OK button on the ButtonBar
     * Expected behaviour is to set all the values on the model the
     * widget is representing followed by a
     * <code>getRootPane().getParent().setVisible(false);</code>
     * @see ButtonBar
     */
    public void ok();


}
