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

public class CommandTableInputAdapter extends AbstractTableModel
{
    public static final int INDEX_COL = 0;
    
    protected ICommand model;
    protected Object tableModel[][] = new Object[3][];

    public CommandTableInputAdapter()
    {
        tableModel[INDEX_COL] = new String[3];
        tableModel[INDEX_COL][0] = "0";
        tableModel[INDEX_COL][1] = "1";
        tableModel[INDEX_COL][2] = "2";
	
	tableModel[INDEX_COL+1] = new Number[3];
	tableModel[INDEX_COL+2] = new String[3];
	
    }
    
    public int getRowCount() {
	return tableModel[INDEX_COL].length;
    }

    public int getColumnCount()
    {
	return 3;
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
	return tableModel[column][row];	     
    }
    

    public String getColumnName(int column)
    {
	if (column == INDEX_COL+1)
	    return "Numeric";
	    
	if (column == INDEX_COL+2)
	   return "String";
	   
        return null;
    }
    
	
    public Class getColumnClass(int column)
    {
	if (column == INDEX_COL+1) return Double.class;
	return String.class;
    }

	
    public void setValueAt(Object o, int row, int column)
    {
	tableModel[column][row] = o;
	if (row == tableModel[0].length - 1)
	{
            Object [][] tmp = new Object[3][tableModel[INDEX_COL].length + 1];
	    
	    System.arraycopy(tableModel[INDEX_COL], 0, tmp[INDEX_COL], 0, tableModel[INDEX_COL].length);
	    Integer next_row = new Integer(row+1);
	    tmp[INDEX_COL][row+1] = next_row.toString();
	    
	    System.arraycopy(tableModel[INDEX_COL+1], 0, tmp[INDEX_COL+1], 0, tableModel[INDEX_COL].length);
	    System.arraycopy(tableModel[INDEX_COL+2], 0, tmp[INDEX_COL+2], 0, tableModel[INDEX_COL].length);
	    tableModel = tmp;
	    /*
	    for (int i = 0; i < 3 ; i++)
	    {
		for (int j = 0; j < tableModel[i].length; j++)
		{
		    System.out.println(tableModel[i][j]);
                } 
		
	    }
	    */
	    
	    fireTableRowsInserted(0, tmp[INDEX_COL].length);
	}
	
    }

 
    
    public java.util.List getNums()
    {
        java.util.List  vNums;
	Object          elem;
	
	vNums = new Vector();
	for (int ir = 0; ir < tableModel[INDEX_COL+1].length; ir++)
	{
	    elem = getValueAt(ir, INDEX_COL+1);
	    if (elem != null)
	       vNums.add(elem);
        }
	return vNums;
    }
    
   
    
    public java.util.List getStrs()
    {
        java.util.List  vStrs;
	Object          elem;
	
	vStrs = new Vector();
	for (int ir = 0; ir < tableModel[INDEX_COL+2].length; ir++)
	{
	    elem = getValueAt(ir, INDEX_COL+2);
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
