// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) space 
// Source File Name:   NumberImageJTableAdapter.java

package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.*;
import java.awt.Window;
import java.util.EventObject;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class NumberImageJTableAdapter extends DefaultTableModel
    implements IImageListener {
    
    Double [] vals = new Double[256];
    double [][] value;

    public NumberImageJTableAdapter() {
	for (int i = 0; i < vals.length; i++) {
	    vals[i] = new Double(i);
	} // end of for ()
    }
    
    public void imageChange(NumberImageEvent evt) {
	value = evt.getValue();
	fireTableRowsUpdated(0, value.length);

    }

    public Object getValueAt(int row, int column) {
	double val = -1;
	try {
	    val = value[row][column];
	    if (java.lang.Math.rint(val) == val && (val > -1 && val < 256)) {
		return vals[(int)val];
	    }
	    return new Double(val);
	     
	} catch (Exception e) {
	    System.out.println("Caught exception at (" + row + ", " +
			       column + ") value = " + val);
	    return new Double(Double.NaN);
	} // end of try-catch
	
    }
	
    public void stateChange(AttributeStateEvent attributestateevent) {
    }

    public void errorChange(ErrorEvent errorevent) {
    }

    public void setModel(INumberImage inumberimage) {
	if (model != null)
	    model.removeImageListener(this);
	model = inumberimage;
	model.addImageListener(this);
	
	setColumnCount(model.getXDimension());
	setRowCount(model.getYDimension());
    }

    public INumberImage getModel() {
	return model;
    }

    public void setJTable(JTable jtable) {
	table = jtable;
	jtable.setModel(this);
    }

    public JTable getJTable() {
	return table;
    }

    public static void main(String args[]) throws Exception {
	fr.esrf.tangoatk.core.AttributeList attributelist =
	    new fr.esrf.tangoatk.core.AttributeList();
	NumberImageJTableAdapter numberimagejtableadapter =
	    new NumberImageJTableAdapter();
	JTable jtable = new JTable();
	numberimagejtableadapter.setJTable(jtable);
	numberimagejtableadapter.setModel((INumberImage)attributelist.add("fe/imacq/2/Image"));
	attributelist.startRefresher();
	JFrame jframe = new JFrame();
	jframe.setContentPane(jtable);
	jframe.pack();
	jframe.show();
    }


    JTable table;
    INumberImage model;
}
