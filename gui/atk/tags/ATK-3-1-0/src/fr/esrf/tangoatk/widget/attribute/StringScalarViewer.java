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
							  
