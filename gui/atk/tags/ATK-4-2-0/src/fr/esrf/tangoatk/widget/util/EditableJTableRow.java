/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.esrf.tangoatk.widget.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author poncet
 */
public class EditableJTableRow extends JTableRow implements ActionListener
{
    protected JMenuItem           loadMenuItem;
    protected JMenuItem           deleteMenuItem;
    private   EditableTableRowModel etrm = null;
    /**
    * Construction
    */
    public EditableJTableRow()
    {
        super();
        editable=true;
        loadMenuItem = new JMenuItem("Load File");
        loadMenuItem.addActionListener(this);
        deleteMenuItem = new JMenuItem("Delete Rows");
        deleteMenuItem.addActionListener(this);
        int  saveIndex = tableMenu.getComponentIndex(saveMenuItem);
        tableMenu.remove(saveIndex);
        tableMenu.insert(loadMenuItem, saveIndex);
        tableMenu.insert(deleteMenuItem, saveIndex);
        etrm = new EditableTableRowModel();
        dm = etrm;
    }
    
    /**
    * Sets this table editable.
    * @param b Editable flag
    */
    @Override
    public void setEditable(boolean b) 
    {
        return;
    }

    /**
    * Returns true if this table is editable
    */
    @Override
    public boolean isEditable()
    {
       return true;
    }
    
    // It is preferable to call this method just after new EditableJTableRow() if needed
    public void setTableRowModel(TableRowModel trm)
    {
       if (trm != null) dm=trm;
    }

    public EditableTableRowModel getEditorTableRowModel()
    {
       return etrm;
    }

    public JTable getJTable()
    {
       return theTable;
    }
    
    //The method called when Load File in popup menu is selected
    public void loadDataFile()
    {
        JFileChooser    jfc = new JFileChooser(".");
        if(currentFile!=null)
        jfc.setSelectedFile(currentFile);
        
        int returnVal = jfc.showDialog(this, "Load");
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File f = jfc.getSelectedFile();
            if ( f != null )
            {
                currentFile = f;
                etrm.loadDataFile(f);
                String[]  colNames = etrm.getColName();
                this.setData(etrm.getData(), colNames);
                etrm.fireTableDataChanged();
            }
        }        
    }
    
    //The method called when Delete Rows in popup menu is selected
    public void deleteSelectedRows()
    {
        //System.out.println("EditableJTableRow.deleteSelectedRows called");
        int[] cols = theTable.getSelectedColumns();
        int[] rows = theTable.getSelectedRows();

        //If nothing is selected, do nothing
        if((cols == null || cols.length == 0)&& (rows == null|| rows.length == 0))
            return; 
        
        // Now remove effectively the selected rows
        theTable.clearSelection();
        Arrays.sort(rows);
        for (int i=rows.length-1; i>=0; i--)
            etrm.removeOneRow(rows[i]);
        
        //update the internal data of JTableRow
        String[]  colNames = etrm.getColName();
        this.setData(etrm.getData(), colNames);
        etrm.fireTableDataChanged();
    }
            
    
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        Object src = e.getSource();

        if ((src != loadMenuItem) && (src != deleteMenuItem))
        {
            super.actionPerformed(e);
            return;
        }
        
        if (src == deleteMenuItem)
            deleteSelectedRows();
        
        if (src == loadMenuItem)
            loadDataFile();
    }

}
