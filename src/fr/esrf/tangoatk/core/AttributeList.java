// File:          AttributeList.java
// Created:       2001-09-24 12:31:56, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-01 16:55:16, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.util.*;
import fr.esrf.tangoatk.core.attribute.AttributeFactory;

public class AttributeList extends AEntityList {



    public AttributeList() {
	factory = AttributeFactory.getInstance();
    }

    public String getVersion() {
	return "$Id$";
    }

    private void readObject(java.io.ObjectInputStream in)
	throws java.io.IOException, ClassNotFoundException {
	System.out.print("Loading AttributeList ");
	in.defaultReadObject();
	System.out.println("Starting refresher on list");
	startRefresher();
    }

}
