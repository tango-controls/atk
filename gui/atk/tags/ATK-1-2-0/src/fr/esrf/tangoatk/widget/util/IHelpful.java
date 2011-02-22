// File:          IHelpable.java
// Created:       2002-07-03 11:24:28, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-10-03 18:30:11, erik>
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
     * <code>getHelpUrl</code> is called to get the url of the help-page
     * for this helpful component. :)
     */
    public java.net.URL getHelpUrl();

}

