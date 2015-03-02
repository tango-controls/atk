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
 
// File:          Node.java
// Created:       2002-09-12 11:53:41, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-09-30 15:15:1, erik>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.dnd;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import fr.esrf.tangoatk.core.*;

public abstract class Node implements Transferable {
    Object value;
    
    public DataFlavor[] getTransferDataFlavors() {
	String [] mimeTypes = getMimeTypes();
	DataFlavor[] ret = new DataFlavor[mimeTypes.length];

	try {	
	    for (int i = 0; i < mimeTypes.length; i++) {
		ret[i] = new DataFlavor(mimeTypes[i]);
	    }

	} catch (ClassNotFoundException e) {
	    System.out.println(e);
	    return null;
	}
	return ret;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
	return true;
    }


    public Object getTransferData(DataFlavor f) {
	if (value instanceof IEntity) {
	    return ((IEntity)value).getName();
	}

	if (value instanceof IDevice) {
	    return ((IDevice)value).getName();
	}
	return value.toString();
    }

    public String getFQName() {
	if (value instanceof IEntity) {
	    return ((IEntity)value).getName();
	}
	if (value instanceof IDevice) {
	    return ((IDevice)value).getName();
	}
	return value.toString();
    }
    
    protected String[] getMimeTypes() {
	String types[] = new String [0];
	return types;
    }

    public String toString() {
	return value.toString();
    }

    public String getName() {
	if (value instanceof IEntity) {
	    return ((IEntity)value).getNameSansDevice();
	}

	if (value instanceof IDevice) {
	    return ((IDevice)value).getName();
	}
	return value.toString();
    }
	
}
