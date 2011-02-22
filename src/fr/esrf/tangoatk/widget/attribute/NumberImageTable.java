/*
 * Synchrotron Soleil File : NumberImageTable.java Project : ATK Description :
 * Author : SOLEIL Original : 20 sept. 2005 Revision: Author: Date: State: Log:
 * NumberImageTable.java,v
 */

package fr.esrf.tangoatk.widget.attribute;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import fr.esrf.tangoatk.core.AttributePolledList;
import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.ConnectionException;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.IImageListener;
import fr.esrf.tangoatk.core.INumberImage;
import fr.esrf.tangoatk.core.NumberImageEvent;

/**
 * @author SOLEIL
 */
public class NumberImageTable extends JTable implements IImageListener {
    protected INumberImage imageModel;
    protected NumberImageTableModel tableModel;

    public NumberImageTable () {
        super();
        imageModel = null;
        tableModel = new NumberImageTableModel();
        setModel( tableModel );
    }

    /**
     * <code>setModel</code> Set the model.
     * 
     * @param v
     *            Value to assign to model. This image must have a height equals
     *            to 2.
     */
    public void setImageModel (INumberImage v) {
        // Free old model
        if ( imageModel != null ) {
            imageModel.removeImageListener( this );
            imageModel = null;
        }
        if ( v != null ) {
            // Init new model
            imageModel = v;
            imageModel.addImageListener( this );
            // Force a reading to initialise the viewer size before
            // make it visible
            imageModel.refresh();
        }
    }

    public INumberImage getImageModel() {
        return imageModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.esrf.tangoatk.core.IImageListener#imageChange(fr.esrf.tangoatk.core.NumberImageEvent)
     */
    public void imageChange (NumberImageEvent event) {
        double[][] val = event.getValue();
        boolean change = false;
        if (val == null)
        {
            val = new double[0][0];
        }
        double[][] formerVal = tableModel.getValue();
        if (formerVal == null){
            formerVal = new double[0][0];
        }
        if (val.length != formerVal.length) {
            change = true;
        }
        else if ( val.length > 0
                  && (val[0].length != formerVal[0].length) 
                ) {
            change = true;
        }
        else {
            for (int i = 0; i < val.length; i++)
            {
                for (int j = 0; j < val[0].length; j++)
                {
                    if (val[i][j] != formerVal[i][j])
                    {
                        change = true;
                        break;
                    }
                }
                if (change) break;
            }
        }
        if (change) tableModel.setValue( val );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.esrf.tangoatk.core.IAttributeStateListener#stateChange(fr.esrf.tangoatk.core.AttributeStateEvent)
     */
    public void stateChange (AttributeStateEvent arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.esrf.tangoatk.core.IErrorListener#errorChange(fr.esrf.tangoatk.core.ErrorEvent)
     */
    public void errorChange (ErrorEvent arg0) {
        double[][] formerVal = tableModel.getValue();
        if ( formerVal != null
             || formerVal.length != 0
             || formerVal[0].length != 0
           ) {
            tableModel.setValue( new double[0][0] );
        }
    }

    public static void main (String[] args) throws ConnectionException {
        JFrame frame = new JFrame("test math");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        String parameter = "tango/tangotest/1/double_spectrum";
        if (args.length > 0) parameter = args[0];
        NumberImageTable table = new NumberImageTable();
        AttributePolledList list = new AttributePolledList();
        INumberImage image = (INumberImage)list.add( parameter ); 
        table.setImageModel( image );
        frame.getContentPane().add( new JScrollPane(table) );
        frame.setSize( 400,400 );
        frame.setVisible( true );
        list.startRefresher();
    }

    protected class NumberImageTableModel extends AbstractTableModel {
        double[][] value;

        public NumberImageTableModel () {
            super();
        }

        public int getRowCount () {
            if ( value == null ) return 0;
            return value.length;
        }

        public int getColumnCount () {
            if ( value == null ) return 0;
            if ( value.length == 0 ) return 0;
            return value[0].length;
        }

        public void setValue (double[][] theValue) {
            boolean changeAll = false;
            synchronized(this) {
                double[][] tempValue = theValue;
                if ( tempValue == null ) {
                    tempValue = new double[0][0];
                }
                // Let's checkout whether dimensions have changed
                if ( value == null ) {
                    changeAll = true;
                }
                else if ( value.length != tempValue.length ) {
                    changeAll = true;
                }
                else if ( value.length > 0
                        && value[0].length != tempValue[0].length ) {
                    changeAll = true;
                }
                value = tempValue;
            }
            if ( changeAll ) {
                // dimensions did change
                fireTableStructureChanged();
            }
            else {
                // dimensions did not change
                fireTableRowsUpdated( 0, value.length );
            }
        }

        public double[][] getValue()
        {
            return value;
        }

        public Object getValueAt (int row, int column) {
            double val = -1;
            val = value[row][column];
            return new Double( val );
        }

        public String getColumnName (int column) {
            return Integer.toString( column );
        }

    }

}
