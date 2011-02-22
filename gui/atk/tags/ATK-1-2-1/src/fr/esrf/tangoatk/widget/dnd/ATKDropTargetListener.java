// File:          ATKDropTargetListener.java
// Created:       2002-09-12 12:46:58, erik
// By:            <erik@assum.net>
// Time-stamp:    <2002-09-17 10:21:8, erik>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.dnd;

import java.awt.dnd.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class ATKDropTargetListener implements DropTargetListener {
    int acceptableActions = 1;

    IDropHandler handler;
    
    public void setAccepableActions(int i) {
	acceptableActions = i;
    }

    public int getAcceptiableActions() {
	return acceptableActions;
    }

    public void setDropHandler(IDropHandler handler) {
	this.handler = handler;
    }

    public IDropHandler getDropHandler() {
	return handler;
    }
    
    private boolean isDragFlavorSupported(DropTargetDragEvent evt) {
	DataFlavor [] flavors = evt.getCurrentDataFlavors();
	for (int i = 0; i < flavors.length; i++) {
	    if (flavors[i].getMimeType().startsWith("tango/entity")       ||
		flavors[i].getMimeType().startsWith("tango/numberscalar") ||
		flavors[i].getMimeType().startsWith("tango/numberspectrum")||
		flavors[i].getMimeType().startsWith("tango/numberimage")  ||
		flavors[i].getMimeType().startsWith("tango/numberimage")  ||
		flavors[i].getMimeType().startsWith("tango/command"))
		return true;
	}
	return false;
    }

    private DataFlavor chooseDropFlavor(DropTargetDropEvent evt) {
	DataFlavor [] flavors = evt.getCurrentDataFlavors();
	return flavors[flavors.length -1];
    }

    private boolean isDragOn(DropTargetDragEvent evt) {
	if (!isDragFlavorSupported(evt)) {
	    return false;
	}

	int i = evt.getDropAction();
	return (i & acceptableActions) != 0;
    }

    public void dragEnter(DropTargetDragEvent evt) {
	if (handler.isDragOn(evt.getCurrentDataFlavors())) {
	    evt.acceptDrag(evt.getDropAction());
	    return;
	} 
	evt.rejectDrag();
    }

    public void dragOver(DropTargetDragEvent evt) {
	if (handler.isDragOn(evt.getCurrentDataFlavors())) {
	    evt.acceptDrag(evt.getDropAction());
	    return;
	}
	evt.rejectDrag();
    }

    public void dropActionChanged(DropTargetDragEvent evt) {
	if (handler.isDragOn(evt.getCurrentDataFlavors())) {
	    evt.acceptDrag(evt.getDropAction());
	    return;
	}
	evt.rejectDrag();
    }

    public void dragExit(DropTargetEvent droptargetevent) {
    }

    public void drop(DropTargetDropEvent evt) {

	DataFlavor dataflavor = chooseDropFlavor(evt);

	if (dataflavor == null) {
	    evt.rejectDrop();
	    return;
	}

	int i = evt.getDropAction();
	int j = evt.getSourceActions();

	if ((j & acceptableActions) == 0) {
	    evt.rejectDrop();
	    return;
	}


	Object obj = null;
	try {
	    evt.acceptDrop(acceptableActions);
	    obj = evt.getTransferable().getTransferData(dataflavor);
	    if (obj == null)
		throw new NullPointerException();
	} catch (Throwable throwable) {
	    System.err.println("Couldn't get transfer data: " +
			       throwable.getMessage());
	    throwable.printStackTrace();
	    evt.dropComplete(false);
	    return;
	}

	handler.handleDrop(obj.toString(), dataflavor);

	evt.dropComplete(true);
    }
}
