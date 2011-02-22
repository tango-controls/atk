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
