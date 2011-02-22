/*
 * NumberScalarViewer.java
 *
 */

package fr.esrf.tangoatk.widget.attribute;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.properties.*;
import fr.esrf.tangoatk.widget.util.*;
import java.beans.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author  pons
 */
 
public class NumberScalarWheelEditor extends WheelSwitch
    implements INumberScalarListener,IWheelSwitchListener {

    INumberScalar model;
    long          lastTimeSet=0;

    // General constructor    
    public NumberScalarWheelEditor() {    
      model=null;
      addWheelSwitchListener(this);
    }
    
    public IAttribute getModel() {
      return model;
    }

    // Listen on "setpoint" change
    // this is not clean yet as there is no setpointChangeListener
    // Listen on valueChange and readSetpoint
    public void numberScalarChange(NumberScalarEvent evt) {
       long now = System.currentTimeMillis();

       // Dont update if the user has just click on the wheel
       if( (now-lastTimeSet)>1000 ) {
         double set = model.getNumberScalarSetpoint();
         if( getValue() != set ) setValue(set);
       }
    }
    
    public void errorChange(ErrorEvent e) {
      setValue( Double.NaN );
    }
    
    public void stateChange(AttributeStateEvent e) {
    }

    // Listen change on the WheelSwitch    
    public void valueChange(WheelSwitchEvent e) {
       if( model!=null ) model.setValue(e.getValue());
       lastTimeSet = System.currentTimeMillis();
    }
    
    public void setModel(INumberScalar m) {

	if( m==null )
	  throw new IllegalArgumentException("NumberScalarWheelEditor: Does not accept null model.");

	if( !m.isWriteable() )
	  throw new IllegalArgumentException("NumberScalarWheelEditor: Only accept writeable attribute.");
	
	// Remove old registered listener
	if (model != null)
	    model.removeNumberScalarListener(this);	  
	    
	model = m;

	// Register new listener
	model.addNumberScalarListener(this);	
	
	setFormat(model.getProperty("format").getPresentation());
	model.refresh();
	double d = model.getNumberScalarValue();
	if( !Double.isNaN(d) ) setValue(model.getNumberScalarValue());	
    }


    public static void main(String [] args) {
    
	fr.esrf.tangoatk.core.AttributeList attributeList = new
	    fr.esrf.tangoatk.core.AttributeList();

	final NumberScalarWheelEditor nsv = new NumberScalarWheelEditor();
	
	try {
	
	    final INumberScalar attr = (INumberScalar)attributeList.add("eas/test-api/1/Short_attr_rw");
	    nsv.setModel(attr);
	    attributeList.startRefresher();

	} catch (Exception e) {
	    System.out.println(e);
	} // end of try-catch
	

        javax.swing.JFrame f = new javax.swing.JFrame();
        f.getContentPane().add(nsv);
        f.pack();
        f.show();
    }
    

}
