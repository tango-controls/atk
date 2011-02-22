// File:          IImageControlPanel.java
// Created:       2002-06-04 15:42:51, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-10 15:33:50, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.image;

import javax.swing.JComponent;
import java.awt.*;

/**
 * <code>IImageControlPanel</code>
 *
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version 1.0
 * @deprecated use IImageManipulatorPanel
 */
public interface IImageControlPanel extends IImageControl {

    public String getName();

    public JComponent getComponent();

}
