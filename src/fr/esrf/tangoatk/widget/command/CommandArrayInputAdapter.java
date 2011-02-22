// File:          CommandTableInputAdapter.java
// Created:       2002-06-03 17:16:50, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-03 18:30:4, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.widget.command;

import java.util.*;
import javax.swing.table.*;
import fr.esrf.tangoatk.core.ICommand;

public class CommandArrayInputAdapter extends AbstractTableModel
{
    public static final int INDEX_COL = 0;

    protected ICommand      model;
    protected Object        arrayModel[][] = new Object[2][];

    public CommandArrayInputAdapter()
    {
        arrayModel[INDEX_COL] = new String[3];
        arrayModel[INDEX_COL][0] = "0";
        arrayModel[INDEX_COL][1] = "1";
        arrayModel[INDEX_COL][2] = "2";
        arrayModel[INDEX_COL+1] = new Object[3];
    }

    public CommandArrayInputAdapter(ICommand  ic)
    {
	this();
	setModel(ic);
    }
    
    public int getRowCount()
    {
	return arrayModel[INDEX_COL].length;
    }

    public int getColumnCount()
    {
	return 2;
    }

    public boolean isCellEditable(int row, int column)
    {
       if (column == INDEX_COL)
          return false;
       else
          return true;
    }
    
    public Object getValueAt(int row, int column)
    {
	return arrayModel[column][row];	     
    }

    public String getColumnName(int column)
    {
	if (column == INDEX_COL)
	    return null;
	return model.getInTypeElemName();
    }
	
    public Class getColumnClass(int column)
    {
	return String.class;
    }
	
    public void setValueAt(Object o, int row, int column)
    {
	arrayModel[column][row] = o;
	if (row == arrayModel[0].length - 1)
	{
            Object [][] tmp = new Object[2][arrayModel[INDEX_COL].length + 1];
	    
	    System.arraycopy(arrayModel[INDEX_COL], 0, tmp[INDEX_COL], 0, arrayModel[INDEX_COL].length);
	    Integer next_row = new Integer(row+1);
	    tmp[INDEX_COL][row+1] = next_row.toString();
	    
	    System.arraycopy(arrayModel[INDEX_COL+1], 0, tmp[INDEX_COL+1], 0, arrayModel[INDEX_COL].length);
	    arrayModel = tmp;
	    
	    fireTableRowsInserted(0, tmp[INDEX_COL].length);
	}
	
    }

 
    
    public java.util.List getStrs()
    {
        java.util.List<Object>  vStrs;
	Object                  elem;
	
	vStrs = new Vector<Object> ();
	for (int ir = 0; ir < arrayModel[INDEX_COL+1].length; ir++)
	{
	    elem = getValueAt(ir, INDEX_COL+1);
	    if (elem != null)
	       vStrs.add(elem);
	}
	return vStrs;
    }
	
	
	
    public void setModel(ICommand command) {
	model = command;
    }

    public ICommand getModel() {
	return model;
    }
    
}
