/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
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
