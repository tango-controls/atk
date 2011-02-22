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
 
// File:          StringScalarViewer.java
// Created:       2002-03-21 13:23:01, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-05-21 15:41:50, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.attribute;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.*;

public class StringScalarViewer extends AScalarViewer
    implements IStringScalarListener {
    IStringScalar model;
    
    public StringScalarViewer() {
	setValueField(new ATKStringField());
	setValueBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
	UIManagerHelper.setAll("StringScalarViewer.Label", getLabel());
	UIManagerHelper.setAll("StringScalarViewer.Value", getValue());
	UIManagerHelper.setAll("StringScalarViewer.Unit", getUnit());

    }

    public StringScalarViewer(IStringScalar stringScalar) {
	this();
	setModel(stringScalar);
    }
    
    public void stringScalarChange(StringScalarEvent stringScalarEvent) {
	((ATKStringField)getValue()).setValue(stringScalarEvent.getValue());

    }


    public void setModel(IAttribute model) {
	if (!(model instanceof IStringScalar)) {
	    throw new IllegalArgumentException("Only accept IStringScalars");
	}
	setModel((IStringScalar)model);
    }
	
    public void setModel(IStringScalar stringScalar) {

	if (model != null) {
	    model.removeStringScalarListener(this);
	}
        
        model = stringScalar;
        

	getValue().setFormat(model.getProperty("format").getPresentation());
 	((ATKStringField)getValue()).setModel(model);
	init(model);
	model.addStringScalarListener(this);
    }

}
							  
