/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.esrf.tangoatk.widget.util;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author poncet
 */
public class TableRowModel extends DefaultTableModel
{
    protected String[]   colName  = null;
    protected Object[][] data     = null;
    protected boolean    editable = false;

    public TableRowModel ()
    {
        super();
        data = new Object[0][0];
        colName = new String[0];
    }

    @Override
    public void setValueAt (Object aValue, int rowIndex, int columnIndex)
    {
        if ( editable && data != null ) 
            data[rowIndex][columnIndex] = aValue;
        System.out.println("JTableRowModel setValueAt called");
    }

    @Override
    public Class<?> getColumnClass (int columnIndex)
    {
        return String.class;
    }

    @Override
    public boolean isCellEditable (int row, int col)
    {
        return editable;
    }

    @Override
    public String getColumnName (int column)
    {
        if ( colName != null ) 
            return colName[column];
        else 
            return "";
    }

    @Override
    public int getRowCount ()
    {
        if (data == null)
        {
            return 0;
        }
        else
        {
            return data.length;
        }
    }

    @Override
    public int getColumnCount ()
    {
        if (colName == null)
        {
            return 0;
        }
        else
        {
            return colName.length;
        }
    }

    @Override
    public Object getValueAt (int row, int column)
    {
        if ( data != null )
        {
            return data[row][column];
        }
        else
        {
            return "";
        }
    }

    public String[] getColName ()
    {
        return colName;
    }

    public void setColName (String[] colName)
    {
        this.colName = colName;
    }

    public boolean isEditable ()
    {
        return editable;
    }

    public void setEditable (boolean editable)
    {
        this.editable = editable;
    }

    public Object[][] getData ()
    {
        return data;
    }

    public void setData (Object[][] theData)
    {
        this.data = theData;
    }

}
