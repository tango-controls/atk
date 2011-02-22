/*
 * NumberSpectrumViewer.java
 *
 * Author:JL Pons 2003
 */

package fr.esrf.tangoatk.widget.attribute;

import javax.swing.*;
import fr.esrf.tangoatk.widget.util.chart.*;
import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.INumberSpectrum;

/**
 *
 * @author  root
 */
public class NumberSpectrumViewer extends JLChart implements fr.esrf.tangoatk.core.ISpectrumListener {

    INumberSpectrum model;
    JLDataView      dvy;
    JLDataView      dvx;
    JLChart         theGraph;
    
    /** Creates new fNumberSpectrumViewer */
    public NumberSpectrumViewer() {
    
      // Create the graph
      /*
      theGraph = new JLChart();
      theGraph.setBorder(new javax.swing.border.EtchedBorder());
      theGraph.setBackground(new java.awt.Color(180, 180, 180));
      theGraph.getY1Axis().setAutoScale(true);
      theGraph.getY2Axis().setAutoScale(true);
      theGraph.getXAxis().setAutoScale(true);  
      theGraph.getXAxis().setAnnotation(JLAxis.VALUE_ANNO);
      */
      super();
      setBorder(new javax.swing.border.EtchedBorder());
      setBackground(new java.awt.Color(180, 180, 180));
      getY1Axis().setAutoScale(true);
      getY2Axis().setAutoScale(true);
      getXAxis().setAutoScale(true);  
      
      dvy = new JLDataView();
      dvx = new JLDataView();
      getY1Axis().addDataView( dvy );
      getXAxis().addDataView(  dvx );

    }

    public void errorChange(fr.esrf.tangoatk.core.ErrorEvent errorEvent) {
      // TODO
    }    

    public void stateChange(fr.esrf.tangoatk.core.AttributeStateEvent evt) {
    }    
	
    public void spectrumChange(fr.esrf.tangoatk.core.NumberSpectrumEvent numberSpectrumEvent) {
	double []value = numberSpectrumEvent.getValue();
	int length = value.length;
	dvx.reset();
	dvy.reset();
	for (int i = 0; i < length; i++) {
	  dvx.add( (double)i , (double)i );
	  dvy.add( (double)i , value[i] );
	}
	// Commit change
	repaint();
    }    

        
    /**
     * Set the value of model.
     * @param v  Value to assign to model.
     */
    public void setModel(INumberSpectrum  v) {
    
	if (model != null) {
	    model.removeSpectrumListener(this);
	}
	
	if( v!=null ) {
          dvy.setUnit( v.getUnit() );
          dvy.setName( v.getName() );
        }
	
	this.model = v;
	
	model.addSpectrumListener(this);
	
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
