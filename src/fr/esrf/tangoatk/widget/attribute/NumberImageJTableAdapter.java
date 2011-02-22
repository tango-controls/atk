// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) space 
// Source File Name:   NumberImageJTableAdapter.java

package fr.esrf.tangoatk.widget.attribute;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.IImageListener;
import fr.esrf.tangoatk.core.INumberImage;
import fr.esrf.tangoatk.core.NumberImageEvent;

public class NumberImageJTableAdapter extends AbstractTableModel
    implements IImageListener {
    
    Double [] vals = new Double[256];
    double [][] value;

    public NumberImageJTableAdapter() {
	for (int i = 0; i < vals.length; i++) {
	    vals[i] = new Double(i);
	} // end of for ()
    }

    public int getRowCount() {
	if (model == null) return 0;

	return model.getYDimension();
    }

    public int getColumnCount() {
	if (model == null) return 0;

	return model.getXDimension();
    }
	
	
    public void imageChange(NumberImageEvent evt) {
    double[][] tempValue = evt.getValue();
    if (tempValue == null)
    {
        tempValue = new double[0][0];
    }
    // Let's checkout whether dimensions have changed
    boolean changeAll = false;
    if (value == null)
    {
        changeAll = true;
    }
    else if (value.length != tempValue.length)
    {
        changeAll = true;
    }
    else if (value.length > 0 && value[0].length != tempValue[0].length)
    {
        changeAll = true;
    }
	value = tempValue;
    if (changeAll)
    {
        //dimensions did change
        fireTableStructureChanged();
    }
    else
    {
        //dimensions did not change
        fireTableRowsUpdated(0, value.length);
    }

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
	fireTableStructureChanged();
    }

    public INumberImage getModel() {
	return model;
    }

    /**
     * <code>setJTable</code>
     *
     * @param jtable a <code>JTable</code> value
     * @deprecated use setViewer instead
     */
    public void setJTable(JTable jtable) {
	setViewer(jtable);
    }

    /**
     * <code>getJTable</code>
     *
     * @return a <code>JTable</code> value
     * @deprecated use getViewer instead
     */
    public JTable getJTable() {
	return getViewer();
    }

    public void setViewer(JTable jtable) {
	table = jtable;
	jtable.setModel(this);
    }

    public JTable getViewer() {
	return table;
    }

    public static void main(String args[]) throws Exception {
	fr.esrf.tangoatk.core.AttributeList attributelist =
	    new fr.esrf.tangoatk.core.AttributeList();
	NumberImageJTableAdapter numberimagejtableadapter =
	    new NumberImageJTableAdapter();
	JTable jtable = new JTable();
	numberimagejtableadapter.setViewer(jtable);
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
