// File:          IHelpable.java
// Created:       2002-07-03 11:24:28, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-08 16:35:31, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.util;

/**
 * <code>IHelpful</code> is an interface which specifies that the 
 * widget needs a help button in addition to the ok button demanded by
 * the IControlee interface
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Revision$
 */
public interface IHelpful extends IControlee {
    
    /**
     * <code>help</code> is called when the user presses the help button.
     * Expected behaviour is to give the user some help :)
     */
    public void help();

}
