// File:          IImagePanel.java
// Created:       2002-06-17 11:26:56, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-03 15:0:44, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.image;

import javax.swing.JComponent;
import fr.esrf.tangoatk.widget.util.IControlee;

/**
 * <code>IImagePanel</code> is an interface to specify a graphical object
 * which is to appear in an image-viewers control-panel.
 *
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Revision$
 */
public interface IImagePanel extends IControlee {

    /**
     * <code>getName</code> returns the name by which this controller is
     * to be presented by in the control-panel.
     *
     * @return a <code>String</code> value
     */
    public String getName();

    /**
     * <code>getComponent</code> returns the visual part of this controller.
     * Normally the visual part is a panel.
     *
     * @return a <code>JComponent</code> value
     */
    public JComponent getComponent();

}
