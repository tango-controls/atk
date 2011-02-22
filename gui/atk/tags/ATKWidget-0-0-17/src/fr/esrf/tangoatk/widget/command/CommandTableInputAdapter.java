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

public class CommandTableInputAdapter extends AbstractTableModel {
    protected ICommand model;
    protected Object tableModel[][] = new Object[2][];

    public CommandTableInputAdapter() {
	tableModel[0] = new String[3];
	tableModel[1] = new Number[3];
    }
    public int getRowCount() {
	return tableModel[0].length;
    }

    public int getColumnCount() {
	return 2;
    }

    public boolean isCellEditable(int row, int column) {
	return true;
    }
    
    public Object getValueAt(int row, int column) {
	return tableModel[column][row];	     
    }

    public String getColumnName(int column) {
	if (column == 0) {
	    return "String";
	}
	return "Numeric";
    }
	
    public Class getColumnClass(int column) {
	if (column == 0) return String.class;
	return Double.class;
    }
	
    public void setValueAt(Object o, int row, int column) {
	tableModel[column][row] = o;
	if (row == tableModel[0].length - 1) {
	    Object [][] tmp = new Object[2][tableModel[0].length + 1];
	    System.arraycopy(tableModel[0], 0, tmp[0], 0, tableModel[0].length);
	    System.arraycopy(tableModel[1], 0, tmp[1], 0, tableModel[0].length);
	    tableModel = tmp;
	    for (int i = 0; i < 2 ; i++) {
		for (int j = 0; j < tableModel[i].length; j++) {
		    System.out.println(tableModel[i][j]);
		} // end of for ()
		
		
	    } // end of for ()
	    
	    fireTableRowsInserted(0, tmp[0].length);
	}
	
    }
	
	
	
    public void setModel(ICommand command) {
	model = command;
    }

    public ICommand getModel() {
	return model;
    }
    
}
