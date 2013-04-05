/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.esrf.tangoatk.widget.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author poncet
 */
public class EditableTableRowModel extends TableRowModel 
{
    public EditableTableRowModel ()
    {
        super();
        editable = true;
        data = new Object[0][0];
        colName = new String[2];
        colName[0] = "Index";
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

    @Override
    public void setValueAt (Object aValue, int rowIndex, int columnIndex)
    {
        if ( !editable || (data == null) )  return;
        if (columnIndex < 1) return; // we don't accept to set the index of the data

        data[rowIndex][columnIndex] = aValue;
        if (rowIndex == data.length-1)
        {
            //System.out.println("Add a new line!");
            Object[][]  tmp = new Object[data.length+1][2];
            System.arraycopy(data, 0, tmp, 0, data.length);
            double lastIndex = data.length;
            tmp[data.length][0] = Double.toString(lastIndex);
            tmp[data.length][1] = null;
            data = tmp;
            fireTableRowsInserted(tmp.length-1, tmp.length-1);
        }
        //System.out.println("EditableTableRowModel.setValueAt called");
    }  

    public void setAttributeColumnName(String attName)
    {
        colName[1] = attName;
    }

    /**
    * Loads a data file and fill in the table with the corresponding data
    * @param data file
    */
    void loadDataFile(File dataFile)
    {
        BufferedReader    reader = null;

        try
        {
            reader = new BufferedReader(new FileReader(dataFile));
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "IO Error. Failed to load file: "+ dataFile.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        readTabbedDataLines(dataFile.getAbsolutePath(), reader);
        //System.out.println("EditableTableRowModel.loadDataFile called");
    }

    private void readTabbedDataLines(String fileName, BufferedReader dataReader)
    {
        String     line = null;

        try
        {
            line = dataReader.readLine();
            // First line contains the index and attribute column titles
            if (line == null) throw new Exception();
            if ("".equals(line.trim())) throw new Exception();
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null, "Failed to load file: "+ fileName +"\nNo title line.", "Error", JOptionPane.ERROR_MESSAGE);
            return;           
        }

        String[] parsedTitleLine = line.split("\t");  
        if  ( ( parsedTitleLine == null) || ( parsedTitleLine.length != this.getColumnCount()) )
        {
            JOptionPane.showMessageDialog(null, "Failed to load file: "+ fileName +"\nColumn count error.", "Error", JOptionPane.ERROR_MESSAGE);
            return;                           
        }


        Vector<String[]>   tableDataVector = new Vector<String[]> ();
        while(true)
        {
            try
            {
                line = dataReader.readLine();
            } 
            catch (IOException ex)
            {
                break;
            }
            if (line == null)
            {
                break;
            }
            if ("".equals(line.trim()))
            {
                continue; // empty line, try to read the other ones
            }

            String[]           parsedLine = line.split("\t");
            // Build one single line data
            String[]           lineData = new String[parsedTitleLine.length];

            if (parsedLine.length != lineData.length)
            {
                continue; // error on this line, try to read the other ones
            }

            try 
            {
                double  index = Double.parseDouble(parsedLine[0].trim());
                lineData[0] = parsedLine[0].trim();
            }
            catch (NumberFormatException nfe)
            {
                continue; // error on this line, try to read the other ones
            }

            int i;
            for (i=1; i<parsedLine.length; i++)
            {
                try
                {
                    double cellData = Double.parseDouble(parsedLine[i].trim());
                    lineData[i] = parsedLine[i].trim();
                }
                catch (NumberFormatException nfe)
                {
                    if (parsedLine[i].trim().equalsIgnoreCase("null"))
                        lineData[i] = "NaN";
                    else
                       if (parsedLine[i].trim().equalsIgnoreCase("NaN"))
                           lineData[i] = "NaN";
                       else
                          break; // error on this cell, try to go to the next line                      
                }
            }
            if (i < parsedLine.length) // end of loop by break
            {
                continue; // error on one of the cells of this line, try to go to the next line  
            }           
            tableDataVector.add(lineData);
        }

        if (tableDataVector.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Failed to load file: "+ fileName +"\nRead error occured on one of the lines.", "Error", JOptionPane.ERROR_MESSAGE);
            return;                                      
        }

        // Build data
        Object[][] temp_data = new Object[tableDataVector.size()][parsedTitleLine.length];

        for (int i = 0; i < tableDataVector.size(); i++)
        {
            String[]  lineValue = tableDataVector.get(i);
            for (int j = 0; j < lineValue.length; j++)
            {
                temp_data[i][j] = lineValue[j];
            }
        }
        data = temp_data;
        fireTableRowsInserted(0, data.length-1);
    }

    /**
    * Parses the table data (object[][]) from strings to a numeric spectrum data
    */
    public double[]  parseNumberSpectrumData()
    {
        if (getColumnCount() != 2) return null;
        if (data == null) return null;
        if (data.length == 0) return null; 
        if (data[0].length != getColumnCount()) return null;
        Vector<Double>   numberSpectrumDataVector = new Vector<Double> ();
        
        for (int i=0; i < data.length; i++)
        {
            if (data[i].length < 2) continue;
            if (data[i][1] == null)
            {
                if (i < data.length-1) // not the last line
                numberSpectrumDataVector.add(new Double(Double.NaN));
                continue;
            }
            if (!(data[i][1] instanceof String)) continue;
            String  cellStr = (String) data[i][1];

            try
            {
                double cellData = Double.parseDouble(cellStr.trim());
                numberSpectrumDataVector.add(new Double(cellStr.trim()));
            }
            catch (NumberFormatException nfe)
            {
                if (cellStr.trim().equalsIgnoreCase("null") || cellStr.trim().equalsIgnoreCase("NaN"))
                    numberSpectrumDataVector.add(new Double(Double.NaN));
            }
        }
        if (numberSpectrumDataVector.isEmpty()) return null;

        double[] result = new double[numberSpectrumDataVector.size()];
        for (int i=0; i<numberSpectrumDataVector.size(); i++)
            result[i] = numberSpectrumDataVector.get(i).doubleValue();
        return result;
    }
    
    void removeOneRow(int rowIndex)
    {
        //System.out.println("Remove one line!");
        Object[][]  temp_data = new Object[data.length-1][2];
        if (rowIndex == 0)
           System.arraycopy(data, 1, temp_data, 0, data.length-1);
        else
           if (rowIndex == data.length-1)
           {
              System.arraycopy(data, 0, temp_data, 0, data.length-1);
           }
           else
           {
              System.arraycopy(data, 0, temp_data, 0, rowIndex);
              System.arraycopy(data, rowIndex+1, temp_data, rowIndex, data.length-rowIndex-1);
           }
        data = temp_data;
        for (int i=0; i<data.length; i++)
        {
            data[i][0] = Double.toString(i);
        }
        
    }

}
