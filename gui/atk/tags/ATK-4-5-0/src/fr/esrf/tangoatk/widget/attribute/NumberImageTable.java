/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
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
import fr.esrf.tangoatk.core.INumber;
import fr.esrf.tangoatk.core.INumberImage;
import fr.esrf.tangoatk.core.INumberSpectrum;
import fr.esrf.tangoatk.core.ISpectrumListener;
import fr.esrf.tangoatk.core.NumberImageEvent;
import fr.esrf.tangoatk.core.NumberSpectrumEvent;
import javax.swing.JOptionPane;
import fr.esrf.tangoatk.core.IAttribute;
import fr.esrf.tangoatk.core.IEntity;

/**
 * @author SOLEIL
 */
public class NumberImageTable extends JTable implements IImageListener, ISpectrumListener {
    protected INumber imageModel;
    protected NumberImageTableModel tableModel;
    private boolean valueEditable = false;

    public NumberImageTable () {
        super();
        imageModel = null;
        tableModel = new NumberImageTableModel();
        setModel( tableModel );
    }

    public void setImageModel(INumberImage v)
    {
        // Free old model
        if ( imageModel != null ) {
        	if(imageModel instanceof INumberImage)
        	{
	            ((INumberImage)imageModel).removeImageListener( this );
	            imageModel = null;
        	}
        	else if(imageModel instanceof INumberSpectrum)
        	{
	            ((INumberSpectrum)imageModel).removeSpectrumListener( this );
	            imageModel = null;
        	}
        }
        if ( v != null ) {
            // Init new model
            imageModel = v;
        	// I don't know why... but we need to add the test here because we may have INumberSpectrum with old jars. 
            if(imageModel instanceof INumberImage)
            	((INumberImage)imageModel).addImageListener( this );
            else
            {
            	if(imageModel instanceof INumberSpectrum) 
                   	((INumberSpectrum)imageModel).addSpectrumListener(this);
            }		
            // Force a reading to initialise the viewer size before
            // make it visible
            if(!imageModel.isWritable())
                setValueEditable(false);
            imageModel.refresh();
        }
    	
    }
        
    /**
     * <code>setSpectrumModel</code> Set the model.
     * 
     * @param v Value to assign to model. This image must have a height equals
     *            to 2.
     */
    public void setSpectrumModel (INumberSpectrum v) {
        // Free old model
        if ( imageModel != null ) {
        	if(imageModel instanceof INumberImage)
        	{
	            ((INumberImage)imageModel).removeImageListener( this );
	            imageModel = null;
        	}
        	else if(imageModel instanceof INumberSpectrum)
        	{
	            ((INumberSpectrum)imageModel).removeSpectrumListener( this );
	            imageModel = null;
        	}
        }
        if ( v != null ) {
            // Init new model
            imageModel = v;
           	((INumberSpectrum)imageModel).addSpectrumListener(this);
            if(!imageModel.isWritable())
                setValueEditable(false);
            // Force a reading to initialise the viewer size before
            // make it visible
            imageModel.refresh();
        }
    }
    
    public INumberImage getImageModel() {
      if(imageModel instanceof INumberImage)
        return (INumberImage)imageModel;
      return null;
    }
    
    public INumberSpectrum getSpectrumModel() {
        if(imageModel instanceof INumberSpectrum)
            return (INumberSpectrum)imageModel;
        return null;
    }


    /*
     * (non-Javadoc)
     * 
     * @see fr.esrf.tangoatk.core.IImageListener#imageChange(fr.esrf.tangoatk.core.NumberImageEvent)
     */
    public void imageChange (NumberImageEvent event) {
    	imageChange(event.getValue());
    }

    public void spectrumChange(NumberSpectrumEvent event) {
	    imageChange(new double[][]{event.getValue()});		
    }
    
    
    public void imageChange (double[][] val) {
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
    
    public boolean isValueEditable() {
        return valueEditable;
    }

    public void setValueEditable(boolean valueEditable) {
        this.valueEditable = valueEditable;
    }
 
    public static void main (String[] args) throws ConnectionException {
        JFrame frame = new JFrame("Test NumberImageTable");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        String parameter = "tango/tangotest/1/double_spectrum";
        if (args.length > 0) parameter = args[0];
        NumberImageTable table = new NumberImageTable();
        table.setValueEditable(true);
        AttributePolledList list = new AttributePolledList();
        IEntity image = list.add( parameter ); 
        if(image instanceof INumberSpectrum)
            table.setSpectrumModel( (INumberSpectrum)image );
        if(image instanceof INumberImage)
            table.setImageModel((INumberImage)image );      
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
        
        public void setValueAt (Object object, int row, int column) {
            try{
                double tmpDoubleValue = Double.parseDouble((String)object);                
                writeValue (tmpDoubleValue,row,column);
            }
            catch(NumberFormatException exc){}
        }
        
        public void writeValue (double aValue, int row, int column) {
            double[][] tmpReadValue = tableModel.getValue();  
            double[][] tmpNewValue = new double[0][0];
            if(tmpReadValue != null) {
                if(getSpectrumModel() != null)
                    tmpNewValue = new double[tmpReadValue.length][(tmpReadValue[0].length)/2];
                else
                    tmpNewValue = tmpReadValue;
               
                for (int i = 0; i < tmpNewValue.length; i++)
                {
                    for (int j = 0; j < tmpNewValue[i].length; j++){
                        if(i == row && j == column){                           
                            tmpNewValue [i][j] = aValue;
                        }
                        else{
                            tmpNewValue [i][j] =  tmpReadValue [i][j];
                        }
                    }
                }
                if(getImageModel() != null){
                    INumberImage model = getImageModel() ;                   
                    try {
                        model.setValue(tmpNewValue);
                    }
                    catch (Exception e) {     
                        JOptionPane.showMessageDialog(null,"Write error", e.getMessage(), JOptionPane.ERROR_MESSAGE); 
                    }
                }
                if(getSpectrumModel() != null){                    
                    INumberSpectrum model = getSpectrumModel() ;                   
                    try {
                        model.setValue(tmpNewValue[0]);
                    }
                    catch (Exception e) {     
                        JOptionPane.showMessageDialog(null,"Write error", e.getMessage(), JOptionPane.ERROR_MESSAGE); 
                    }
                }
            }
        }
 
        public boolean isCellEditable(int arg0, int arg1) {            
            return isValueEditable();
        }
    }

}
