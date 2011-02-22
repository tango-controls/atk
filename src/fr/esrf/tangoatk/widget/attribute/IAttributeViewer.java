// File:          IAttributeViewer.java
// Created:       2002-05-17 13:51:49, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-05-21 15:7:44, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.attribute;
import fr.esrf.tangoatk.core.*;

public interface IAttributeViewer {

    public IAttribute getModel();

    public boolean isValueEditable();

}
