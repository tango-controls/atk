// File:          SimpleScalarViewer.java
// Created:       2002-06-27 13:02:31, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-01 17:37:9, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.attribute;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import fr.esrf.tangoatk.widget.util.*;
import fr.esrf.tangoatk.core.*;
import com.braju.format.Format;
import fr.esrf.TangoDs.AttrManip;

public class SimpleScalarViewer
    extends JAutoScrolledText
    implements INumberScalarListener,
	       IStringScalarListener,
               IErrorListener
	       {

    INumberScalar numberModel;
    IStringScalar stringModel;
    boolean alarmEnabled = true;
    ATKFormat userFormat;
    String format = "";
    String error = "-------";
    boolean unitVisible = true;
        
    public SimpleScalarViewer() {
	setOpaque(true);
    }

    public void stringScalarChange(StringScalarEvent evt) {
	String val;
	String s = evt.getValue();
	if (userFormat != null) {
	    val = userFormat.format(s);
	} else {
	    Object[] o = { s };
	    val = Format.sprintf(format, o);
	} // end of else
	
	if( unitVisible ) {   	
	  setText(val + " " + numberModel.getUnit());
	} else {
	  setText(val);
	}
    }
        
    public void numberScalarChange(NumberScalarEvent evt) {
	Double d = new Double(evt.getValue());
	String val;
	
	if (userFormat != null) {
	    val = userFormat.format(d);
	} else if (format.indexOf('%') == -1) {
	    val = AttrManip.format(format, evt.getValue());
	} else {
	    Object[] o = {d};
	    val = Format.sprintf(format, o);
	} // end of else
		
	if( unitVisible ) {   	
	  setText(val + " " + numberModel.getUnit());
	} else {
	  setText(val);
	}
	
    }

    public void setLabelVisible(boolean b) {
    }
	
    public boolean isLabelVisible() {
        return false;
    }

    public Border getValueBorder() {
	return getBorder();
    }
    
    public void setValueBorder(Border border) {
	setBorder(border);
    }

    public void setValueBackground(Color color) {
	setBackground(color);
    }
    
    public void setValueForeground(Color color) {
	setForeground(color);
    }

    public void setUserFormat(ATKFormat format) {
	userFormat = format;
    }

    public ATKFormat getUserFormat() {
	return userFormat;
    }

    public void setUnitVisible(boolean b) {
      unitVisible = b;
    }

    public boolean getUnitVisible() {
      return unitVisible;
    }
    
    public void setAlarmEnabled(boolean b) {
      alarmEnabled = b;
    }
	
    public void stateChange(AttributeStateEvent evt) {
	String state = evt.getState();
	if (!alarmEnabled) return;
	setBackground(AttributeStateViewer.getColor4State(state));	
    }

    public void errorChange(ErrorEvent evt) {
	setText(error);
	if (!alarmEnabled) return;
	setBackground(AttributeStateViewer.getColor4State("UNKNOWN"));
    }

    public void setModel(INumberScalar scalar) {
	if (numberModel != null) 
	    numberModel.removeNumberScalarListener(this);
	format = scalar.getProperty("format").getPresentation();
	numberModel = scalar;
	numberModel.addNumberScalarListener(this);
    }

    public void setModel(IStringScalar scalar) {
	if (stringModel != null) 
	    stringModel.removeStringScalarListener(this);

	format = scalar.getProperty("format").getPresentation();
	stringModel = scalar;
	stringModel.addStringScalarListener(this);
    }

    public static void main (String[] args) throws Exception {
	fr.esrf.tangoatk.core.AttributeList attributeList = new
	    fr.esrf.tangoatk.core.AttributeList();
	SimpleScalarViewer snv = new SimpleScalarViewer();
	snv.setModel((INumberScalar)attributeList.add("eas/test-api/1/Att_sinus"));
	snv.setValueBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
	snv.setValueBackground(java.awt.Color.blue);
	snv.setValueForeground(java.awt.Color.yellow);
	snv.setFont(new java.awt.Font("Dialog", 0, 12));
	JFrame f = new JFrame();
	attributeList.startRefresher();
	f.setContentPane(snv);
	f.pack();
	f.show();

    } // end of main ()
    
}
