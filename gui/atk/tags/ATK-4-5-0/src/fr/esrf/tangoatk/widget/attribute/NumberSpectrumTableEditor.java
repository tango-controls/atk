/*
 * SpectrumTableEditorFrame.java
 *
 * Created on August 26, 2009, 9:24 AM
 */

package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.INumberSpectrum;
import fr.esrf.tangoatk.core.ISpectrumListener;
import fr.esrf.tangoatk.core.NumberSpectrumEvent;
import fr.esrf.tangoatk.widget.util.EditableTableRowModel;
import fr.esrf.tangoatk.widget.util.EditableJTableRow;
import fr.esrf.tangoatk.widget.util.JTableRow;
import fr.esrf.tangoatk.widget.util.TableRowModel;
import fr.esrf.tangoatk.widget.util.MultiExtFileFilter;
import fr.esrf.tangoatk.widget.util.chart.JLDataView;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author  poncet
 */
public class NumberSpectrumTableEditor extends javax.swing.JFrame 
                                       implements ISpectrumListener, TableModelListener
{

    // Variables declaration for initComponents()
    private javax.swing.JButton closeFileJButton;
    private javax.swing.JPanel dummyJPanel1;
    private javax.swing.JButton resetJButton;
    private javax.swing.JPanel dummyJPanel2;
    private javax.swing.JButton writeAttJButton;
    // End of variables declaration for initComponents()
    
    private EditableJTableRow     theTable;
    private EditableTableRowModel   etrm;
    private JLDataView            setDvy;
    private boolean               updatedOnce = false;
    private double                A0 = 0.0;
    private double                A1 = 1.0;
    
    protected INumberSpectrum     model = null;
    
    private   JFileChooser        jfc = null;
    
    
    
    /** Creates new form SpectrumTableEditorFrame */
    public NumberSpectrumTableEditor()
    { 
       theTable = new EditableJTableRow();
       etrm = theTable.getEditorTableRowModel();
       etrm.addTableModelListener(this);
       theTable.setTableRowModel(etrm);
       theTable.setEditable(true);
       setDvy = new JLDataView(); // it will remain invisible
       initComponents();
       jfc = new JFileChooser();
       jfc.addChoosableFileFilter(new MultiExtFileFilter("Text files", "txt"));
       jfc.setDialogTitle("Load Graph Data (Text file with TAB separated fields)");
    }
    
    /**
    * Sets the data.
    * @param data Handle to data array.
    * @param colNames Name of columns
    */
    public void setData(Object[][] data, String[] colNames)
    {
       theTable.setData(data,colNames);
    }

    /**
    * Clear the table
    */
    public void clearData()
    {
       theTable.clearData();
    }

    // Center the window
    public void centerWindow()
    {
        theTable.adjustColumnSize();
        theTable.adjustSize();

        // Center the frame and saturate to 800*600
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension scrsize = toolkit.getScreenSize();
        pack();
        Dimension appsize = getPreferredSize();
        if( appsize.height>600 )
        {
          appsize.height=600;
          if(appsize.width<800)
          {
            // When we saturate the height
            // it is better to reserver space for
            // the vertical scrollbar
            appsize.width += 16;
          }
        }
        if( appsize.width>800 ) appsize.width=800;

        int x = (scrsize.width - appsize.width) / 2;
        int y = (scrsize.height - appsize.height) / 2;
        setBounds(x, y, appsize.width, appsize.height);
    }
    
   /**
    * Sets an affine tranform to the X axis. This allows to transform
    * spectra index displayed on X axis.
    * @param a0
    * @param a1
    */
    public void setXAxisAffineTransform(double a0,double a1)
    {
        A0 = a0;
        A1 = a1;
    }

    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        closeFileJButton = new javax.swing.JButton();
        dummyJPanel1 = new javax.swing.JPanel();
        /*resetJButton = new javax.swing.JButton();
        dummyJPanel2 = new javax.swing.JPanel();*/
        writeAttJButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());


        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(theTable, gridBagConstraints);

        closeFileJButton.setText("Close");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(closeFileJButton, gridBagConstraints);
        closeFileJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeJButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(dummyJPanel1, gridBagConstraints);
        
        
        /*resetJButton.setText("Reset Table");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(resetJButton, gridBagConstraints);
        resetJButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
        resetJButtonActionPerformed(evt);
        }
        });
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(dummyJPanel2, gridBagConstraints);*/
        
        
        writeAttJButton.setText("Write Attribute");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(writeAttJButton, gridBagConstraints);
        writeAttJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeAttJButtonActionPerformed(evt);
            }
        });

        pack();
    }
    
    private void closeJButtonActionPerformed(java.awt.event.ActionEvent evt)
    {   
        setVisible(false);
    }                                        
    
        
    private void resetJButtonActionPerformed(java.awt.event.ActionEvent evt)
    {      
        updateTable();
    }                                        
       
    private void writeAttJButtonActionPerformed(java.awt.event.ActionEvent evt)
    {                                         
        double[]  numberSpectrumData = null;
        
        if (theTable == null) return;
        if (theTable.getEditorTableRowModel() == null) return;
        etrm = theTable.getEditorTableRowModel();
        numberSpectrumData = etrm.parseNumberSpectrumData();
        
        if (numberSpectrumData == null) return;
        model.setValue(numberSpectrumData);
    }                                        

    
   /**<code>setModel</code> Set the model.
    * @param v  Value to assign to model.
    */
    public void setModel(INumberSpectrum v)
    {
       if (model!=null) clearModel();
       if (v != null)
       {
           if (v.isWritable())
           {
              model = v;
              etrm = theTable.getEditorTableRowModel();
              etrm.setAttributeColumnName(model.getName());
              model.addSpectrumListener(this);
              model.refresh();
           }
       }
       repaint();
    }

   /**<code>clearModel</code> removes the model.
    */
  
    public void clearModel()
    {
        if (model!=null)
        {
            model.removeSpectrumListener(this);
            model = null;
            updatedOnce = false;
        }
        clearData();
        theTable = new EditableJTableRow();
        etrm.removeTableModelListener(this);
//        etrm = new EditableTableRowModel();
        etrm = theTable.getEditorTableRowModel();
        etrm.addTableModelListener(this);
        theTable.setTableRowModel(etrm);
        theTable.setEditable(true);
        setDvy = new JLDataView(); // it will remain invisible
    }

    public void spectrumChange(NumberSpectrumEvent e)
    {
        if (isVisible() && updatedOnce)
           return;
        updateTable();
        updatedOnce = true;
    }

    public void stateChange(AttributeStateEvent e)
    {
    }

    public void errorChange(ErrorEvent evt)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void updateTable()
    {
        clearData();
        double[]  setValue = model.getSpectrumSetPoint();
        String[]  cols = new String[2];
        cols[0] = "Index";
        cols[1] = model.getName();

        synchronized(setDvy)
        {
            setDvy.reset();
            for (int i = 0; i < setValue.length; i++)
            {
                setDvy.add(A0 + A1 * (double) i, setValue[i], false);
            }
            setDvy.updateFilters();

            // Build data
            Object[][] data = new Object[setValue.length][2];

            for (int i = 0; i < setValue.length; i++)
            {
                String[]  lineValue = new String[2];
                lineValue[0] = Double.toString(i);
                lineValue[1] = setDvy.formatValue(setDvy.getYValueByIndex(i));
                data[i][0] = lineValue[0];
                data[i][1] = lineValue[1];
            }
            setData( data, cols );
        }             
    }
    
    /**
    * @param args the command line arguments
    */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NumberSpectrumTableEditor().setVisible(true);
            }
        });
    }
    

    public void tableChanged(TableModelEvent e)
    {
        if ((e.getType() == TableModelEvent.INSERT) || (e.getType() == TableModelEvent.UPDATE))
        {
            //centerWindow();
            theTable.adjustColumnSize();
            theTable.adjustSize();
            pack();
            //((JPanel)getContentPane()).revalidate();
            // Saturate the frame to 800*600
            Dimension appsize = getPreferredSize();
            if( appsize.height>600 )
            {
                appsize.height=600;
                if(appsize.width<800)
                {
                // When we saturate the height
                // it is better to reserver space for
                // the vertical scrollbar
                appsize.width += 16;
                }
            }
            if( appsize.width>800 ) appsize.width=800;
            setBounds(getBounds().x, getBounds().y, appsize.width, appsize.height);
            if ((e.getType() == TableModelEvent.INSERT) && (theTable.getJTable() != null) )
            {
            theTable.getJTable().changeSelection(e.getLastRow(), 1, false, false);
            theTable.getJTable().editCellAt(e.getLastRow(), 1);
            }
        }
    }

}
