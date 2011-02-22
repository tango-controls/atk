/*
 * NumberScalarViewer.java
 *
 * Created on November 21, 2001, 3:35 PM
 */

package fr.esrf.tangoatk.widget.attribute;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.properties.*;
import fr.esrf.tangoatk.widget.util.*;
import java.beans.*;
import java.awt.*;
import javax.swing.*;
/**
 *
 * @author  root
 */
public class NumberScalarViewer extends AScalarViewer implements
INumberScalarListener {
    INumberScalar model;
    /** Creates new form NumberScalarViewer */
    public NumberScalarViewer() {
	setValueField(new ATKNumberField());
	setValueBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
	UIManagerHelper.setAll("NumberScalarViewer.Label", getLabel());
	UIManagerHelper.setAll("NumberScalarViewer.Value", getValue());
	UIManagerHelper.setAll("NumberScalarViewer.Unit", getUnit());
    }
    

    public NumberScalarViewer(INumberScalar numberScalar) {
	this();
	setModel(numberScalar);
    }

    public void numberScalarChange(NumberScalarEvent evt) {
	((ATKNumberField)getValue()).setValue(new Double(evt.getValue()));
    }

    public void setModel(IAttribute model) {
	if (!(model instanceof INumberScalar)) {
	    throw new IllegalArgumentException("Only accept INumberScalars");
	}
	setModel((IStringScalar)model);
    }
	

    public void setModel(INumberScalar numberScalar) {

	if (model != null) {
	    model.removeNumberScalarListener(this);
	}


        model = numberScalar;
	getValue().setFormat(model.getProperty("format").getPresentation());
	((ATKNumberField)getValue()).setModel(model);        
	init(model);

	model.addNumberScalarListener(this);
    }


    public static void main(String [] args) {
	fr.esrf.tangoatk.core.AttributeList attributeList = new
	    fr.esrf.tangoatk.core.AttributeList();
	long start = System.currentTimeMillis();
	long time;
	final NumberScalarViewer nsv = new NumberScalarViewer();
	time = System.currentTimeMillis();
	System.out.println("nsv creation time = " + (time - start));
	NumberScalarViewer nsv2 = new NumberScalarViewer();
	nsv.setLabelVisible(true);
	nsv.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
	nsv.setValueBorder(null);
	nsv2.setLabelVisible(true);
	//	nsv2.setOpaque(true);

	//	nsv.setLabelMaximumLength(20);
	try {
	    final INumberScalar attr = (INumberScalar)attributeList.add("eas/test-api/1/Short_attr_rw");
	    //	    nsv.setValueEditable(false);
	    //	    nsv.setValueMaximumLength(1);
	    start = System.currentTimeMillis();

	    nsv.setModel(attr);
	    nsv.setBackground(java.awt.Color.blue);
	    time = System.currentTimeMillis();
	    System.out.println("nsv setModel time = " + (time - start));

	    nsv2.setModel(attr);
	    nsv2.setBorder(null);
	    //	    nsv2.setBackground(java.awt.Color.red);
	    nsv.setBackground(java.awt.Color.blue);
	    nsv.setBorder(null);
	    //	    nsv.setValueBorder(null);
	    //	    nsv.setFont(new java.awt.Font("Times", 1, 60));
 	    nsv.setUserFormat(new ATKFormat() {
 		    public String format(Number d) {
 			return "urk  " + d.toString();
 		    }
		}
			      );
	    final INumberScalar attr1 = (INumberScalar)attributeList.add("eas/test-api/1/Short_attr_w");
	    nsv.setPropertyListEditable(true);
	    nsv2.setPropertyListEditable(true);
	    attributeList.startRefresher();
	    int i = 0;
// 	    new Thread() {
// 		public void run() {
// 		    while (true) {
// 			nsv.setModel(attr1);
// 			try {
// 			    Thread.sleep(3000);			     
// 			} catch (Exception e) {
// 			    ;
// 			} // end of try-catch
			

// 			nsv.setModel(attr);
// 		    }
// 		}
// 	    }.start();
	    

	} catch (Exception e) {
	    System.out.println(e);
	} // end of try-catch
	

        javax.swing.JFrame f = new javax.swing.JFrame();
	f.getContentPane().setLayout(new java.awt.GridBagLayout());
	java.awt.GridBagConstraints                 gbc;
	gbc = new java.awt.GridBagConstraints();
	gbc.gridx = 0; gbc.gridy = 0;
	gbc.fill = java.awt.GridBagConstraints.BOTH;
	gbc.insets = new java.awt.Insets(0, 0, 0, 5);
	f.getContentPane().add(nsv, gbc);
	gbc.gridx = 0; gbc.gridy = 1;
	f.getContentPane().add(nsv2, gbc);
	f.setBackground(java.awt.Color.green);
        f.pack();
        f.show();
    }


    public String toString() {
	return "{numberscalarviewer}";
    }
}
	    
