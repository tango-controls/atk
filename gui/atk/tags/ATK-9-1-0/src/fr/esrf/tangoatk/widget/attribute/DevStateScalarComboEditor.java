/*
 *  Copyright (C) :	2013
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
 * DevStateScalarComboEditor.java
 *
 * Author:Faranguiss Poncet 2013
 *
 * A setter for an EnumScalar attribute. The enumerated strings and the corresponding
 * tango device values are handled by the core class EnumScalar and the associated
 * classes. 
 * This widget provides a way to set the EnumScalar attributes.
 */

package fr.esrf.tangoatk.widget.attribute;


import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.DevStateScalarEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.IDevStateScalar;
import fr.esrf.tangoatk.core.IDevStateScalarListener;
import fr.esrf.tangoatk.core.IDevice;
import fr.esrf.tangoatk.core.IEntity;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;

//import javax.swing.*;


//import fr.esrf.tangoatk.core.*;


/**
 * A class to set the value of a EnumScalar attribute by selecting the value
 * in a the list of the enumerated values.
 * 
 * @author  poncet
 */
public class DevStateScalarComboEditor extends JComboBox 
                                     implements ActionListener, IDevStateScalarListener
{

    private DefaultComboBoxModel     comboModel=null;
    private String                   defActionCmd="setAttActionCmd";



     /* The bean properties */
    private IDevStateScalar      stateModel=null;
    
    private String[]             defOptionList = {  IDevice.ON,
                                                    IDevice.OFF,
                                                    IDevice.CLOSE,
                                                    IDevice.OPEN,
                                                    IDevice.INSERT,
                                                    IDevice.EXTRACT,
                                                    IDevice.MOVING,
                                                    IDevice.STANDBY,
                                                    IDevice.FAULT,
                                                    IDevice.INIT,
                                                    IDevice.RUNNING,
                                                    IDevice.ALARM,
                                                    IDevice.DISABLE,
                                                    IDevice.UNKNOWN   };
    
    private String[]             optionList={"None"};

    // Default constructor
    public DevStateScalarComboEditor()
    {
       stateModel = null;

       comboModel = new DefaultComboBoxModel(optionList);
       this.setModel(comboModel);
       this.setActionCommand(defActionCmd);
       this.addActionListener(this);
    }
    
    
    

    public IDevStateScalar getStateModel()
    {
       return stateModel;
    }


    public void setStateModel(IDevStateScalar m)
    {
       clearModel();
       
       if( m==null ) return;

       if (!m.isWritable())
	 throw new IllegalArgumentException("DevStateScalarComboEditor: Only accept writable attribute.");


       stateModel = m;	     
       
       // Update the comboBox model
       optionList = defOptionList;
       comboModel = new DefaultComboBoxModel(optionList);
       this.setModel(comboModel);

       changeCurrentSelection(-1); //No item selected

       // Register new listener
       stateModel.addDevStateScalarListener(this);
       stateModel.refresh();
    }
    
    public void clearModel()
    {
       // Remove old registered listener
       if (stateModel != null)
       {
           stateModel.removeDevStateScalarListener(this);
           stateModel = null;
           optionList = defOptionList;
           comboModel = new DefaultComboBoxModel(optionList);
           this.setModel(comboModel);
       }
        
    }

    // Listen on "setpoint" change
    // this is not clean yet as there is no setpointChangeListener
    // Listen on valueChange and read the Setpoint

    public void devStateScalarChange(DevStateScalarEvent e)
    {
        String     set = null;
	int        currentSelection, index;

	currentSelection = this.getSelectedIndex();  
	
	if(hasFocus())
	    set = stateModel.getDeviceSetPoint(); 
	else
	    set = stateModel.getSetPoint();

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
	   if (set.equals(optionList[index]))
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

	if (stateModel == null)
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

	if (stateModel == null)
           return;

	cb = (JComboBox) e.getSource();
	cmdOption = (String) cb.getSelectedItem();

	if (cmdOption == null)
           return;
	   
	stateModel.setValue(cmdOption);
    }
    
    
    private void changeCurrentSelection(int newIndex)
    {
	disableExecution();
	setSelectedIndex(newIndex);
//System.out.println("call to setSelectedIndex("+newIndex+") passed!");	    
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
	 DevStateScalarComboEditor                  dssce = new DevStateScalarComboEditor();
	 IEntity                                ie;
	 IDevStateScalar                        stateAtt;
         JFrame                                 mainFrame = null;
	 try
	 {
            ie = attList.add("dev/test/10/State_attr_rw");

	    if (ie instanceof IDevStateScalar)
	    {
	       stateAtt = (IDevStateScalar) ie;
               System.out.println("Is an IDevStateScalar!");	    
	    }
	    else
	       stateAtt = null;

	    if (stateAtt == null)
	       System.exit(-1);

	    dssce.setStateModel(stateAtt);
	 } 
	 catch (Exception e)
	 {
            System.out.println("caught exception : "+ e.getMessage());
	    System.exit(-1);
	 }
	 
	 attList.startRefresher();

	 mainFrame = new JFrame();
         mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 mainFrame.setContentPane(dssce);
	 mainFrame.pack();

	 mainFrame.setVisible(true);


    } // end of main ()

}
