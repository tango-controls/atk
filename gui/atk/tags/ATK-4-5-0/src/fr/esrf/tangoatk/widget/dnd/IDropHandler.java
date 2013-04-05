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
 
// File:          IDropHandler.java
// Created:       2002-09-12 13:13:29, erik
// By:            <erik@skiinfo.fr>
// Time-stamp:    <2002-09-17 10:14:42, erik>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.dnd;

import java.awt.datatransfer.DataFlavor;

public interface IDropHandler {

    public void handleDrop(String name, DataFlavor flavor);
    
    public boolean isDragOn(DataFlavor []flavors);
    
}
