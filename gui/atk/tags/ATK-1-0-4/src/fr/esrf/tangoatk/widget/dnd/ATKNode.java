// File:          ATKNode.java
// Created:       2002-09-12 11:53:41, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-09-16 20:53:47, erik>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.dnd;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import fr.esrf.tangoatk.core.*;

public abstract class ATKNode implements Transferable {
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

    protected String[] getMimeTypes() {
	String types[] = new String [1];
	types[0] = ATKNodeFactory.MIME_ENTITY; 
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
