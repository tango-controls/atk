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
