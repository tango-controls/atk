// File:          IImageManipulatorPanel.java
// Created:       2002-06-10 15:32:12, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-17 11:28:17, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.image;

import javax.swing.JComponent;
import java.awt.*;

/**
 * <code>IImageManipulatorPanel</code> is an interface which is used 
 * to implement image-manipulators which have a need to interact with
 * the user. It will be available by right-clicking on the image.
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Version$
 * @deprecated Use IImageManipulator and IImagePanel
 */
public interface IImageManipulatorPanel extends IImageManipulator,
						IImagePanel {

}
