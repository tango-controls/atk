/*	Synchrotron Soleil 
 *  
 *   File          :  NumberImageTable.java
 *  
 *   Project       :  ATKWidgetSoleil
 *  
 *   Description   :  
 *  
 *   Author        :  SOLEIL
 *  
 *   Original      :  20 sept. 2005 
 *  
 *   Revision:  					Author:  
 *   Date: 							State:  
 *  
 *   Log: NumberImageTable.java,v 
 *
 */
package fr.esrf.tangoatk.widget.attribute;

import java.awt.Rectangle;

import javax.swing.JPanel;

import com.braju.format.Format;

import fr.esrf.TangoDs.AttrManip;
import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.IImageListener;
import fr.esrf.tangoatk.core.INumberImage;
import fr.esrf.tangoatk.core.NumberImageEvent;
import fr.esrf.tangoatk.widget.util.JTableRow;

/**
 * 
 * @author SOLEIL
 */
public class NumberImageTable extends JPanel implements IImageListener {
    
    protected double[][] data;
    protected INumberImage model;
    protected JTableRow modelTable;
    protected Rectangle selectionRect;

    /**
     * Sets data to display.
     * 
     * @param v
     *            Handle to data
     */
    public void setData(double[][] v) {

        // Synchronise access to critic data
        synchronized (this) {
            data = v;
            //refreshComponents();
        }
    }

    public void setSelectionRect(Rectangle rect) {
        selectionRect = rect;
    }
    
    public Rectangle getSelectionRect() {
        return selectionRect;
    }
    
    /**
     * <code>setModel</code> Set the model.
     * 
     * @param v
     *            Value to assign to model. This image must have a height equals
     *            to 2.
     */
    public void setModel(INumberImage v) {
        // Free old model
        if (model != null) {
            model.removeImageListener(this);
            model = null;
        }

        if (v != null) {
            // Init new model
            model = v;
            model.addImageListener(this);
            // Force a reading to initialise the viewer size before
            // make it visible
            model.refresh();
        }
    }

    private boolean buildTable() {

        Rectangle r = selectionRect;

        if (r == null) {
            return false;
        }

        if (r.width <= 0 || r.height <= 0) {
            return false;
        }

        String attFormat = null;
        if (model != null)
            attFormat = model.getFormat();

        if (attFormat != null && attFormat.length() > 0) {

            if (attFormat.indexOf('%') == -1) {

                String[][] d = new String[r.height][r.width];
                for (int j = 0; j < r.height; j++) {
                    for (int i = 0; i < r.width; i++) {
                        d[j][i] = AttrManip.format(attFormat, data[r.y
                                + j][r.x + i]);
                    }
                }
                modelTable.setData(d, r.x, r.y);

            }
            else {

                Double[] tmp = new Double[1];
                String[][] d = new String[r.height][r.width];
                for (int j = 0; j < r.height; j++) {
                    for (int i = 0; i < r.width; i++) {
                        tmp[0] = new Double(data[r.y + j][r.x + i]);
                        d[j][i] = Format.sprintf(attFormat, tmp);
                    }
                }
                modelTable.setData(d, r.x, r.y);

            }

        }
        else {

            Double[][] d = new Double[r.height][r.width];
            for (int j = 0; j < r.height; j++) {
                for (int i = 0; i < r.width; i++) {
                    d[j][i] = new Double(data[r.y + j][r.x + i]);
                }
            }
            modelTable.setData(d, r.x, r.y);

        }
        return true;
    } // end buildTable()

    /* (non-Javadoc)
     * @see fr.esrf.tangoatk.core.IImageListener#imageChange(fr.esrf.tangoatk.core.NumberImageEvent)
     */
    public void imageChange(NumberImageEvent arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see fr.esrf.tangoatk.core.IAttributeStateListener#stateChange(fr.esrf.tangoatk.core.AttributeStateEvent)
     */
    public void stateChange(AttributeStateEvent arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see fr.esrf.tangoatk.core.IErrorListener#errorChange(fr.esrf.tangoatk.core.ErrorEvent)
     */
    public void errorChange(ErrorEvent arg0) {
        // TODO Auto-generated method stub

    }

    public static void main(String[] args) {
    }
}
