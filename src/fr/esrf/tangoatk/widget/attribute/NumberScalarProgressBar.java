/*
 * NumberScalarProgressBar.java
 *
 * Created on June 16, 2004, 2:45 PM
 */

package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.attribute.NumberScalar;
import javax.swing.*;


/**
 *
 * @author  poncet
 */
 
public class NumberScalarProgressBar extends javax.swing.JProgressBar
                                     implements INumberScalarListener
{
    private INumberScalar      nsModel;
    private int                min, max;


    /** Creates new form NumberScalarProgressBar */
    public NumberScalarProgressBar()
    {
       super();
       min = -1;
       max = -1;
       nsModel = null;
       setStringPainted(true);
       setIndeterminate(true);
    }


    
    public INumberScalar getNsModel()
    {
        return nsModel;
    }


    public void setNsModel(INumberScalar numberScalar)
    {
       NumberScalar    ns;
       double          minVal, maxVal, currVal;
       int             val;
       
       if (nsModel != null)
       {
	   nsModel.removeNumberScalarListener(this);
           setIndeterminate(true);
	   min = -1;
	   max = -1;
	   nsModel = null;
       }

       nsModel = numberScalar;
       
       if ( (min == max) && (min < 0) )
       {
           if (nsModel instanceof NumberScalar)
	   {
	       ns = (NumberScalar) nsModel;
	       minVal = ns.getMinValue();
	       maxVal = ns.getMaxValue();
	       currVal = ns.getNumberScalarValue();
	       
	       min = (int) minVal;
	       max = (int) maxVal;
	       
	       if ( (min == max) && (min < 0) )
	       {
		   min = -1;
		   max = -1;
	       }
	       else
	       {
		   setMinimum(min);
		   setMaximum(max);
		   val = (int) currVal;
		   setValue(val);
		   setIndeterminate(false);
	       }
	   }
       }
       
       nsModel.addNumberScalarListener(this);
    }
    
    
    public int getMin()
    {
        return min;
    }
    
    public void setMin(int  m)
    {
        min = m;
    }
    
    public int getMax()
    {
        return max;
    }
    
    public void setMax(int  m)
    {
        max = m;
    }


    public void numberScalarChange(NumberScalarEvent evt)
    {
       double    newVal;
       int       progressBarVal;
       
       newVal = evt.getValue();
       progressBarVal = (int) newVal;
       setValue(progressBarVal);
    }



    public void stateChange(AttributeStateEvent evt)
    {
    }



    public void errorChange(ErrorEvent evt)
    {
    }
    

    public static void main(String [] args)
    {
       fr.esrf.tangoatk.core.AttributeList  attributeList = new fr.esrf.tangoatk.core.AttributeList();

       final NumberScalarProgressBar nspb = new NumberScalarProgressBar();

       try 
       {
	  final INumberScalar attr = (INumberScalar)attributeList.add("fe/d-bsm/c06/scanProgress");

	  nspb.setNsModel(attr);
	  attributeList.startRefresher();
       } 
       catch (Exception e)
       {
	  System.out.println(e);
       } // end of try-catch


       javax.swing.JFrame f = new javax.swing.JFrame();
       f.getContentPane().setLayout(new java.awt.GridBagLayout());
       java.awt.GridBagConstraints                 gbc;
       gbc = new java.awt.GridBagConstraints();
       gbc.gridx = 0; gbc.gridy = 0;
       gbc.fill = java.awt.GridBagConstraints.BOTH;
       gbc.insets = new java.awt.Insets(0, 0, 0, 5);
       f.getContentPane().add(nspb, gbc);
       f.pack();
       f.show();
    }


    public String toString()
    {
       return "{NumberScalarProgressBar}";
    }
}
	    
