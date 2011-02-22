/*
 * NumberScalarComboEditor.java
 *
 * Author:Faranguiss Poncet 2004
 */

package fr.esrf.tangoatk.widget.attribute;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

import fr.esrf.tangoatk.widget.util.jdraw.JDrawable;

import fr.esrf.tangoatk.core.*;
import com.braju.format.Format;
import fr.esrf.TangoDs.AttrManip;


/**
 * A class to set the value of a NumberScalar attribute by selecting the value
 * in a list of possible values.
 * 
 * @author  poncet
 */
public class NumberScalarComboEditor extends JComboBox 
                                     implements ActionListener, INumberScalarListener, JDrawable
{

    
    static String[] exts = {"valueList"};// Needed by JDrawable interface
    private String    jdrawExts = "";

    
    private DefaultComboBoxModel     comboModel=null;
    private String                   defActionCmd="setAttActionCmd";




    private String           modelFormat="";
    private String           modelUnit="";
    private String[]         defOptionList={"0.0"};
    private String[]         optionList={"0.0"};
    
     /* The bean properties */
    private INumberScalar    numberModel=null;
    private boolean          unitVisible;



    // Default constructor
    public NumberScalarComboEditor()
    {
       numberModel = null;
       unitVisible = true;

       comboModel = new DefaultComboBoxModel(optionList);
       this.setModel(comboModel);
       this.setActionCommand(defActionCmd);
       this.addActionListener(this);
    }
    
    
    

    public INumberScalar getNumberModel()
    {
       return numberModel;
    }


    public void setNumberModel(INumberScalar m)
    {
       double[]   valList = null;
       int        index, valListSize = 0;
       String[]   newOptions = null;
       String     strOpt;
       String     invalidOpt;
       Double     dblOpt;
       
       // Remove old registered listener
       if (numberModel != null)
       {
           numberModel.removeNumberScalarListener(this);
           numberModel = null;
	   modelFormat = "";
           optionList = defOptionList;
           comboModel = new DefaultComboBoxModel(optionList);
           this.setModel(comboModel);
       }

       if( m==null ) return;

       if (!m.isWritable())
	 throw new IllegalArgumentException("NumberScalarComboEditor: Only accept writeable attribute.");


       numberModel = m;
       modelFormat = numberModel.getProperty("format").getPresentation();
       modelUnit = numberModel.getProperty("unit").getPresentation();
	     
       Double     invalidOptDble = new Double(0.0);
       Object[]   invalidOptArr = {invalidOptDble};
       invalidOpt = Format.sprintf(modelFormat, invalidOptArr);
       invalidOpt = invalidOpt.replace('0', '?');
       
       // Update the comboBox model
       valList = numberModel.getPossibleValues();
       newOptions = null;
       if (valList != null)
       {
          valListSize = valList.length;
	  if (valListSize > 0)
	  {
	     newOptions = new String[valListSize+1];
	     index = 0;
	     newOptions[0] = new String(invalidOpt + " ");
	     
	     for (index = 0; index < valListSize; index++)
	     {		
		if (modelFormat.indexOf('%') == -1)
                   strOpt = AttrManip.format(modelFormat, valList[index]);
		else
		{
	           dblOpt = new Double(valList[index]);
                   Object[]   optArr = {dblOpt};
		   strOpt = Format.sprintf(modelFormat, optArr);
		}
		if (unitVisible)
		   newOptions[index+1] = new String(strOpt + " " + numberModel.getUnit());
		else
		   newOptions[index+1] = new String(strOpt + " ");
	     }
	  }
       }
       
       if (newOptions == null)
       {
	   newOptions = new String[1];
	   newOptions[0] = new String(invalidOpt + " ");
       }
       
       if (newOptions != null)
       {
          optionList = newOptions;
	  comboModel = new DefaultComboBoxModel(optionList);
	  this.setModel(comboModel);
       }

       // Register new listener
       numberModel.addNumberScalarListener(this);
       numberModel.refresh();
    }

    /**
     * Displays or hides the unit.
     * @param b true to display the unit, false otherwise
     */
    public void setUnitVisible(boolean b)
    {
       if (unitVisible != b)
          changeUnitVisibility(b);
       unitVisible = b;
    }

    /**
     * Detemines wether the unit is visible
     * @return true if unit is visible
     */
    public boolean getUnitVisible()
    {
      return unitVisible;
    }
    
    private void changeUnitVisibility(boolean newUnitVisible)
    {
       double[]   valList = null;
       int        index, valListSize = 0;
       String[]   newOptions = null;
       String     strOpt;
       String     invalidOpt;
       Double     dblOpt;
       
       if (numberModel == null)
          return;
	     
       Double     invalidOptDble = new Double(0.0);
       Object[]   invalidOptArr = {invalidOptDble};
       invalidOpt = Format.sprintf(modelFormat, invalidOptArr);
       invalidOpt = invalidOpt.replace('0', '?');
       
       // Update the comboBox model
       valList = numberModel.getPossibleValues();
       newOptions = null;
       if (valList != null)
       {
          valListSize = valList.length;
	  if (valListSize > 0)
	  {
	     newOptions = new String[valListSize+1];
	     index = 0;
	     newOptions[0] = new String(invalidOpt + " ");
	     
	     for (index = 0; index < valListSize; index++)
	     {		
		if (modelFormat.indexOf('%') == -1)
                   strOpt = AttrManip.format(modelFormat, valList[index]);
		else
		{
	           dblOpt = new Double(valList[index]);
                   Object[]   optArr = {dblOpt};
		   strOpt = Format.sprintf(modelFormat, optArr);
		}
		if (newUnitVisible)
		   newOptions[index+1] = new String(strOpt + " " + numberModel.getUnit());
		else
		   newOptions[index+1] = new String(strOpt + " ");
	     }
	  }
       }
       
       if (newOptions == null)
       {
	   newOptions = new String[1];
	   newOptions[0] = new String(invalidOpt + " ");
       }
       
       if (newOptions != null)
       {
          optionList = newOptions;
	  comboModel = new DefaultComboBoxModel(optionList);
	  this.setModel(comboModel);
       }
    }

    // Listen on "setpoint" change
    // this is not clean yet as there is no setpointChangeListener
    // Listen on valueChange and readSetpoint
    public void numberScalarChange(NumberScalarEvent evt)
    {
	double set = Double.NaN;

	if(hasFocus())
	    set = numberModel.getNumberScalarSetPointFromDevice();
	else
	    set = numberModel.getNumberScalarSetPoint();

	changeSelectedOption(set);
    }

    public void stateChange(AttributeStateEvent e)
    {
    }


    public void errorChange(ErrorEvent e)
    {
	changeSelectedOption(Double.NaN);
    }



    // ---------------------------------------------------
    // Action listener
    // ---------------------------------------------------
    public void actionPerformed(ActionEvent e)
    {

	JComboBox        cb=null;
	String           cmdOption = null;
	double           setValue = 0.0;
	int              idx = 0;
        String           optValueStr=null;

	cb = (JComboBox) e.getSource();
	cmdOption = (String) cb.getSelectedItem();

	if ( !(e.getActionCommand().equals(defActionCmd)) )
	{
	    return;
	}

	if (cmdOption == null)
           return;

	if (numberModel == null)
           return;

	idx = cmdOption.indexOf(" "+numberModel.getUnit());
	if (idx > 0)
	   optValueStr = cmdOption.substring(0, idx);
	else
	   optValueStr = cmdOption;
	   
	   
	setValue = Double.NaN;

	setValue = parseSelectedValue(optValueStr);

	if (!Double.isNaN(setValue))
           numberModel.setValue(setValue);
    }

    
    
    private double parseSelectedValue(String  doubleStr)
    {
	double   val = Double.NaN;

	try
	{
	    val = Double.parseDouble(doubleStr);
	}
	catch (NumberFormatException  nfe)
	{
	    val = Double.NaN;
	}

	return val;
    }
    
    private void changeSelectedOption (double  val)
    {
	double[]   valList = null;
	int        currentSelection, index, valListSize = 0;

	if (numberModel == null)
           return;
	 
	currentSelection = this.getSelectedIndex();  
	
	if (Double.isNaN(val))
	{
	   if (currentSelection != 0)
	       changeCurrentSelection(0);
	   return;
	}
	
	valList = numberModel.getPossibleValues();
	if (valList == null)
	{
	   if (currentSelection != 0)
	       changeCurrentSelection(0);
	   return;
	}

	valListSize = valList.length;
	if (valListSize <= 0)
	{
	   if (currentSelection != 0)
	       changeCurrentSelection(0);
	   return;
	}


	for (index = 0; index < valListSize; index++)
	{		
	   if (valList[index] == val)
	   {
	       if (currentSelection != index+1)
		   changeCurrentSelection(index+1);
	       return;
	   }
	}

	if (index >= valListSize)
	{
	   if (currentSelection != 0)
	       changeCurrentSelection(0);
	   return;
	}

    }
    
    private void changeCurrentSelection(int newIndex)
    {
	disableExecution();
	setSelectedIndex(newIndex);
	repaint();
	enableExecution();
    }
   
   
    public void enableExecution()
    {
	this.setActionCommand(defActionCmd);
    }


    public void disableExecution()
    {
	this.setActionCommand("dummy");
    }


    // ------------------------------------------------------
    // Implementation of JDrawable interface
    // ------------------------------------------------------
    public void initForEditing()
    {
       // Do nothing here
       // Default is ok.
    }

    public JComponent getComponent()
    {
        return this;
    }

    public String getDescription(String extName)
    {
	if (extName.equalsIgnoreCase("valueList"))
	{
	  return "The list of values to be proposed in the ComboBox.";
	}
	return "";
    }

    public String[] getExtensionList()
    {
        return exts;
    }

    public boolean setExtendedParam(String name,String value,boolean popupErr)
    {
	if (name.equalsIgnoreCase("valueList"))
	{
	   jdrawExts = "";
	   String[] c = value.split(",");
	   if (c.length == 0)
	      return true;
	      
	   if (c.length < 0)
	   {
              showJdrawError(popupErr,"valueList","Float value list expected: val,val,val");
              return false;
	   }
	   else
	   {
	       for (int i=0; i<c.length; i++)
	       {
	           if (c[i] == null)
		   {
        	      showJdrawError(popupErr,"valueList","Float value list expected: val,val,val; No empty value.");
        	      return false;
		   }
		   try
		   {
		       double dval = Double.parseDouble(c[i]);		       
		   }
		   catch (Exception  ex)
		   {
        	       showJdrawError(popupErr,"valueList","Float value list expected: val,val,val; All number values.");
        	       return false;
		   }
	       }
	   }
	   jdrawExts = value;
	   return true;
	}
	return false;
    }

    public String getExtendedParam(String name)
    {
      if(name.equalsIgnoreCase("valueList"))
      {
         return jdrawExts;
	 /*
          if (numberModel == null)
	     return "";
	  else
	  {
	     if (optionList.length <= 0)
	        return "";
	     else
	     {
	        String s = new String(optionList[0]);
		for (int i=1; i<optionList.length; i++)
		{
		   s = s.concat(",");
		   s = s.concat(optionList[i]);
		}
	     }
	  */
      }
      return "";
    }

    private void showJdrawError(boolean popup,String paramName,String message)
    {
       if (popup)
	  JOptionPane.showMessageDialog(null, "NumberScalarComboEditor: "+paramName+" incorrect.\n" + message,
                                      "Error",JOptionPane.ERROR_MESSAGE);
    }


    public static void main(String[] args)
    {
	 final fr.esrf.tangoatk.core.AttributeList  attList = new fr.esrf.tangoatk.core.AttributeList();
	 NumberScalarComboEditor                nsce = new NumberScalarComboEditor();
	 IEntity                                ie;
	 INumberScalar                          bw;
	 double[]                               vals = {0.1, 0.3, 1.0, 3.0, 10.0, 30.0, 100.0, 300.0};

         JFrame                                 mainFrame = null;
	 try
	 {
            ie = attList.add("sr/d-tm/ntm/BandWidth");

	    if (ie instanceof INumberScalar)
	       bw = (INumberScalar) ie;
	    else
	       bw = null;

	    if (bw == null)
	       System.exit(-1);

	    bw.setPossibleValues(vals);
	    nsce.setNumberModel(bw);
	 } 
	 catch (Exception e)
	 {
            System.out.println("caught exception : "+ e.getMessage());
	    System.exit(-1);
	 }

	 mainFrame = new JFrame();

	 mainFrame.addWindowListener(
		 new java.awt.event.WindowAdapter()
			    {
				public void windowActivated(java.awt.event.WindowEvent evt)
				{
				   // To be sure that the refresher (an independente thread)
				   // will begin when the the layout manager has finished
				   // to size and position all the components of the window
				   attList.startRefresher();
				}
			    }
                                       );


	 mainFrame.setContentPane(nsce);
	 mainFrame.pack();

	 mainFrame.setVisible(true);


    } // end of main ()


}
