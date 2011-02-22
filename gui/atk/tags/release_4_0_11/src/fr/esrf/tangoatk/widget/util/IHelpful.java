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
 * @version $Revision$
 */
public interface IHelpful extends IControlee {
    
    /**
     * <code>getHelpUrl</code> is called to get the url of the help-page
     * for this helpful component.
     */
    public java.net.URL getHelpUrl();

}

