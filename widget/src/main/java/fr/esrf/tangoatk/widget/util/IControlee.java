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
