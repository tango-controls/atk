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
    extends JPanel
    implements INumberScalarListener,
	       IStringScalarListener {
    JLabel label, value;
    INumberScalar numberModel;
    IStringScalar stringModel;
    boolean alarmEnabled = true;
    Color background = getBackground();
    ATKFormat userFormat;
    String format = "";
    String error = "-------";
    
    public SimpleScalarViewer() {
	label = new JLabel();
	value = new JLabel();
	add(label);
	add(value);
	value.setOpaque(true);
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
	value.setText(val + " " + stringModel.getUnit());
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
		   
	
	
	value.setText(val + " " + numberModel.getUnit());
    }

    public void setValueBorder(Border border) {
	value.setBorder(border);
    }

    public Border getValueBorder() {
	return value.getBorder();
    }
    
    public void setFont(Font font) {
	if (value == null && label == null) return;
	value.setFont(font);
	label.setFont(font);
	super.setFont(font);
    }

    public void setValueBackground(Color color) {
	value.setBackground(color);
    }

    public void setLabelBackground(Color color) {
	label.setBackground(color);
    }

    public void setValueForeground(Color color) {
	value.setForeground(color);
    }

    public void setLabelForeground(Color color) {
	label.setForeground(color);
    }

    public void setUserFormat(ATKFormat format) {
	userFormat = format;
    }

    public ATKFormat getUserFormat() {
	return userFormat;
    }
    
    public void stateChange(AttributeStateEvent evt) {
	String state = evt.getState();
	if (!alarmEnabled) return;

	if ("VALID".equals(state)) {
	    setBackground(background);
	    return;
	}

	setBackground(AttributeStateViewer.getColor4State(state));
	
    }



    public void errorChange(ErrorEvent evt) {
	value.setText(error);
    }

    public void setModel(INumberScalar scalar) {
	if (numberModel != null) 
	    numberModel.removeNumberScalarListener(this);
	format = scalar.getProperty("format").getPresentation();
	numberModel = scalar;
	label.setText(numberModel.getLabel());
	numberModel.addNumberScalarListener(this);
    }

    public void setModel(IStringScalar scalar) {
	if (stringModel != null) 
	    stringModel.removeStringScalarListener(this);

	format = scalar.getProperty("format").getPresentation();
	stringModel = scalar;
	label.setText(stringModel.getLabel());
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
