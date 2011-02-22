/*
 * StringScalarComboEditor.java
 *
 * Author:Faranguiss Poncet 2004
 */

package fr.esrf.tangoatk.widget.attribute;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

import fr.esrf.tangoatk.core.*;
import fr.esrf.TangoDs.AttrManip;


/**
 * A class to set the value of a StringScalar attribute by selecting the value
 * in a list of possible values.
 * 
 * @author  poncet
 */
public class StringScalarComboEditor extends JComboBox 
                                     implements ActionListener, IStringScalarListener
{


    private DefaultComboBoxModel     comboModel=null;
    private String                   defActionCmd="setAttActionCmd";




     /* The bean properties */
    private java.awt.Font    theFont;
    private IStringScalar    stringModel=null;
    private String[]         defOptionList={"None"};
    private String[]         optionList={"None"};

    // Default constructor
    public StringScalarComboEditor()
    {
       stringModel = null;
       theFont = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 14);

       comboModel = new DefaultComboBoxModel(optionList);
       this.setModel(comboModel);
       this.setActionCommand(defActionCmd);
       this.addActionListener(this);
    }
    
    
    

    public IStringScalar getStringModel()
    {
       return stringModel;
    }


    public void setStringModel(IStringScalar m)
    {
       String[]   valList = null;
       int        index, valListSize = 0;
       String[]   newOptions = null;
       String     strOpt;
       String     invalidOpt;
       Double     dblOpt;
       
       // Remove old registered listener
       if (stringModel != null)
       {
           stringModel.removeStringScalarListener(this);
           stringModel = null;
           optionList = defOptionList;
           comboModel = new DefaultComboBoxModel(optionList);
           this.setModel(comboModel);
       }

       if( m==null ) return;

       if (!m.isWritable())
	 throw new IllegalArgumentException("StringScalarComboEditor: Only accept writable attribute.");


       stringModel = m;	     
       invalidOpt = "???";
       
       // Update the comboBox model
       valList = stringModel.getPossibleValues();
       newOptions = null;
       if (valList != null)
       {
          valListSize = valList.length;
	  if (valListSize > 0)
	  {
	     newOptions = new String[valListSize];	     
	     for (index = 0; index < valListSize; index++)
	     {		
		newOptions[index] = new String(valList[index]);
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
       stringModel.addStringScalarListener(this);
       stringModel.refresh();
    }

    // Listen on "setpoint" change
    // this is not clean yet as there is no setpointChangeListener
    // Listen on valueChange and readSetpoint
    public void stringScalarChange(StringScalarEvent evt)
    {
        String     set = null;
	int        currentSelection, index;

	currentSelection = this.getSelectedIndex();  
	
	if(hasFocus())
	    set = stringModel.getStringDeviceSetPoint();
	else
	    set = stringModel.getStringSetPoint();

	if ( set == null )
	{
	    try
	    {
	        changeCurrentSelection(-1); //No item selected
	    }
	    catch (IllegalArgumentException  iaex)
	    {
                System.out.println("caught exception : "+ iaex.getMessage());
	    }
	    return;
	}
	
	if (optionList == null)
	   return;
	   
	for (index = 0; index < optionList.length; index++)
	{		
	   if (set.equalsIgnoreCase(optionList[index]))
	   {
	       if (currentSelection != index)
		   changeCurrentSelection(index);
	       return;
	   }
	}
	
	// set not found in option list : perhaps set = "Not initialised"
	try
	{
	    changeCurrentSelection(-1); //No item selected
	}
	catch (IllegalArgumentException  iaex)
	{
            System.out.println("caught exception : "+ iaex.getMessage());
	}
    }

    public void stateChange(AttributeStateEvent e)
    {
    }


    public void errorChange(ErrorEvent e)
    {
	int        currentSelection, index;

	if (stringModel == null)
           return;
	 
	currentSelection = this.getSelectedIndex();  
	if (currentSelection != -1)
	{
	    try
	    {
	        changeCurrentSelection(-1); //No item selected
	    }
	    catch (IllegalArgumentException  iaex)
	    {
                System.out.println("caught exception : "+ iaex.getMessage());
	    }
	    return;
	}
    }



    // ---------------------------------------------------
    // Action listener
    // ---------------------------------------------------
    public void actionPerformed(ActionEvent e)
    {

	JComboBox        cb=null;
	String           cmdOption = null;

	if ( !(e.getActionCommand().equals(defActionCmd)) )
	{
	    return;
	}

	if (stringModel == null)
           return;

	cb = (JComboBox) e.getSource();
	cmdOption = (String) cb.getSelectedItem();

	if (cmdOption == null)
           return;
	   
	stringModel.setString(cmdOption);
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



    public static void main(String[] args)
    {
	 final fr.esrf.tangoatk.core.AttributeList  attList = new fr.esrf.tangoatk.core.AttributeList();
	 StringScalarComboEditor                ssce = new StringScalarComboEditor();
	 IEntity                                ie;
	 IStringScalar                          pulseType;
	 String[]                               vals = {"short", "long"};

         JFrame                                 mainFrame = null;
	 try
	 {
            ie = attList.add("elin/gun/run/PulseType");

	    if (ie instanceof IStringScalar)
	       pulseType = (IStringScalar) ie;
	    else
	       pulseType = null;

	    if (pulseType == null)
	       System.exit(-1);

	    pulseType.setPossibleValues(vals);
	    ssce.setStringModel(pulseType);
	 } 
	 catch (Exception e)
	 {
            System.out.println("caught exception : "+ e.getMessage());
	    System.exit(-1);
	 }
	 
	 attList.startRefresher();

	 mainFrame = new JFrame();
	 mainFrame.setContentPane(ssce);
	 mainFrame.pack();

	 mainFrame.show();


    } // end of main ()


}
