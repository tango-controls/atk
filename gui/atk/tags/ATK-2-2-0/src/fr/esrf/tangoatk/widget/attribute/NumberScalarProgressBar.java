/*
 * NumberScalarProgressBar.java
 *
 * Created on June 16, 2004, 2:45 PM
 */

package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.attribute.NumberScalar;
import javax.swing.*;
import com.braju.format.Format;


/**
 *
 * @author  poncet
 */
 
public class NumberScalarProgressBar extends javax.swing.JProgressBar
                                     implements INumberScalarListener
{
    protected INumberScalar      nsModel=null;
    protected int                min=-1, max=-1;
    protected boolean            printVal=true;
    protected String             nsDispFormat=null;


    /** Creates new form NumberScalarProgressBar */
    public NumberScalarProgressBar()
    {
       super();
       min = -1;
       max = -1;
       nsModel = null;
       printVal = true;
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
	   nsModel = null;
       }
       progressString=null;
       setIndeterminate(true);
       nsDispFormat = null;
       min = -1;
       max = -1;

       if (numberScalar == null)
          return;
	  
       nsModel = numberScalar;
       
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
	   }

           nsDispFormat = nsModel.getProperty("format").getPresentation();
	   val = (int) currVal;
	   setValue(val);
	   setIndeterminate(false);
	   if (printVal)
	      progressString=getDisplayString(currVal);
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
    
    public boolean getPrintVal()
    {
        return printVal;
    }
    
    public void setPrintVal(boolean  b)
    {
        printVal=b;
	if (printVal==false)
           progressString=null;
    }


    public void numberScalarChange(NumberScalarEvent evt)
    {
       double    newVal;
       int       progressBarVal;
       
       newVal = evt.getValue();
       progressBarVal = (int) newVal;
       setValue(progressBarVal);
       if (printVal)
	  progressString=getDisplayString(newVal);
    }
    
    
    protected String getDisplayString(double val)
    {
	Double attDouble = new Double(val);
	String dispStr;

	Object[] o = {attDouble};

	try
	{
	   if (nsDispFormat == null)
	   {
              dispStr = Format.sprintf("%3.0f", o);
	      return dispStr;
	   }

	   if (nsDispFormat.equalsIgnoreCase("Not Specified"))
	   {
              dispStr = Format.sprintf("%3.0f", o);
	      return dispStr;
	   }

	   if (nsDispFormat.indexOf('%') < 0)
	   {
              dispStr = Format.sprintf("%3.0f", o);
	      return dispStr;
	   }
	   else
	   {
              dispStr = Format.sprintf(nsDispFormat, o);
	      return dispStr;
	   }
	}
	catch (Exception e)
	{
	   return "Exception while formating";
	}
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
	  final INumberScalar attr = (INumberScalar)attributeList.add("elin/gun/aux/Temporization");

	  nspb.setNsModel(attr);
	  nspb.setPrintVal(true);
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
	    
