/*
 * NumberScalarProgressBar.java
 *
 * Created on June 16, 2004, 2:45 PM
 */

package fr.esrf.tangoatk.widget.attribute;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
    private final static String  ERROR="Error";


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

        if (numberScalar == null)
            return;
	  
        nsModel = numberScalar;
       
        if ( nsModel instanceof NumberScalar )
        {
            ns = (NumberScalar) nsModel;
            currVal = ns.getNumberScalarValue();
            if ( min == -1 && max == -1 )
            {
                minVal = ns.getMinValue();
                maxVal = ns.getMaxValue();
                min = (int) minVal;
                max = (int) maxVal;
            }
            if ( ( min == max ) && ( min < 0 ) )
            {
                min = -1;
                max = -1;
            }
            else
            {
                setMinimum( min );
                setMaximum( max );
            }
            nsDispFormat = nsModel.getProperty( "format" ).getPresentation();
            val = (int) currVal;
            setValue( val );
            setIndeterminate( false );
            if ( printVal )
            {
                progressString = getDisplayString( currVal );
                setToolTipText(getString());
            }
            else if (Double.isNaN(getPercentComplete()))
            {
                progressString = ERROR;
                setToolTipText("Min: " + getMinimum() + ", Max: " + getMaximum() + ", Value: " + val);
            }
            else
            {
                setToolTipText(getString());
            }
        }
        nsModel.addNumberScalarListener(this);
        repaint();
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
       progressBarVal = (int) Math.rint(newVal);
       setValue(progressBarVal);
       if ( printVal )
       {
           progressString = getDisplayString( newVal );
           setToolTipText(getString());
       }
       else if (Double.isNaN(getPercentComplete()))
       {
           progressString = ERROR;
           setToolTipText("Min: " + getMinimum() + ", Max: " + getMaximum() + ", Value: " + evt.getValue());
       }
       else
       {
           setToolTipText(getString());
       }
       repaint();
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
      fr.esrf.tangoatk.core.AttributeList attributeList = new fr.esrf.tangoatk.core.AttributeList();
      final NumberScalarProgressBar nspb = new NumberScalarProgressBar();
      String attName = "elin/gun/aux/Temporization";
      INumberScalar attr = null;
      if (args.length > 0)
      {
        attName = args[0];
      }
      if (args.length == 3)
      {
        nspb.setPrintVal( false );
        nspb.setMin(Integer.parseInt(args[1]));
        nspb.setMax(Integer.parseInt(args[2]));
      }
      else
      {
        nspb.setPrintVal( true );
      }
      try
      {
        attr = (INumberScalar) attributeList.add( attName );
        nspb.setNsModel( attr );
        attributeList.startRefresher();
      }
      catch (Exception e)
      {
        System.out.println( e );
      } // end of try-catch
      JFrame f;
      if (attr != null)
      {
        f = new JFrame(attr.getName());
      }
      else
      {
        f = new JFrame(ERROR);
      }
      f.getContentPane().setLayout( new GridBagLayout() );
      GridBagConstraints gbc;
      gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.fill = GridBagConstraints.BOTH;
      gbc.insets = new Insets( 0, 0, 0, 5 );
      f.getContentPane().add( nspb, gbc );
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.pack();
      f.show();
    }


    public String toString()
    {
       return "{NumberScalarProgressBar}";
    }
}
	    
