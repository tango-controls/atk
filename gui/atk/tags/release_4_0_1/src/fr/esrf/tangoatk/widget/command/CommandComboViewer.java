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
 * CommandComboViewer.java
 *
 * Created on March 20, 2002, 4:13 PM
 */

package fr.esrf.tangoatk.widget.command;

import fr.esrf.tangoatk.core.ICommand;
import fr.esrf.tangoatk.core.CommandList;
import fr.esrf.tangoatk.core.command.VoidVoidCommand;
import fr.esrf.tangoatk.core.command.InvalidCommand;

import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JFrame;
/**
 *
 * @author  root
 */
public class CommandComboViewer extends JComboBox 
                                implements ActionListener
{

    private JFrame                argFrame = null;
    private AnyCommandViewer      acv = null;    


    private boolean       borderVisble;
    private boolean       infoButtonVisible;    
    private boolean       cancelButtonVisible;    
    private boolean       deviceButtonVisible;
    private boolean       descriptionVisible;


    private CommandList   commandList=null;
       
    
    /** Creates new CommandComboViewer */
   public CommandComboViewer()
   {
       commandList=null;
       this.addActionListener(this);
   }

   protected void commandsActionPerformed(java.awt.event.ActionEvent evt)
   {

	JComboBox cb = (JComboBox)evt.getSource();
        ICommand command = (ICommand)cb.getSelectedItem();

	if (command instanceof InvalidCommand)
	{
	    javax.swing.JOptionPane.showMessageDialog(this, command.getName() + " is not supported. It probably takes an array as input.", "Error", 1);
	    return;
	}
	
	if (command instanceof VoidVoidCommand)
	{
	    command.execute();
	    return;
	}
	
	// The command needs an input and / or output argument
	if( acv==null )
	{
	   acv      =new AnyCommandViewer();
	   argFrame =new JFrame();
	   argFrame.getContentPane().add(acv);
	}

	acv.initialize(command);
	acv.setDeviceButtonVisible(deviceButtonVisible);
	acv.setDescriptionVisible(descriptionVisible);
	acv.setInfoButtonVisible(infoButtonVisible);
	acv.setBorder(null);
	acv.setInputVisible(true);
		     
	if (!command.takesInput())
	{
	    command.execute();
	} 
	
	argFrame.setTitle(command.getName());
	argFrame.pack();
	argFrame.setVisible(true);
	
    }



   // ---------------------------------------------------
   // Action Listener
   // ---------------------------------------------------
   @Override
   public void actionPerformed(java.awt.event.ActionEvent e)
   {
         commandsActionPerformed(e);
   }

    
    public void setModel(CommandList list) 
    {
        this.commandList = list;
        super.setModel(list);
    }
    

    @Override
    public javax.swing.ComboBoxModel getModel()
    {
        return super.getModel();
    }

    public fr.esrf.tangoatk.core.CommandList getCmdListModel()
    {
        return commandList;
    }


    
    /**
     * Get the value of borderVisble.
     * @return value of borderVisble.
     */
    public boolean isBorderVisible() {
	return borderVisble;
    }
    
    /**
     * Set the value of borderVisble.
     * @param v  Value to assign to borderVisble.
     */
    public void setBorderVisible(boolean  v) {
	this.borderVisble = v;
    }
    
    
    /**
     * Get the value of infoButtonVisible.
     * @return value of infoButtonVisible.
     */
    public boolean isInfoButtonVisible() {
	return infoButtonVisible;
    }
    
    /**
     * Set the value of infoButtonVisible.
     * @param v  Value to assign to infoButtonVisible.
     */
    public void setInfoButtonVisible(boolean  v) {
	this.infoButtonVisible = v;
    }
    
    /**
     * Get the value of cancelButtonVisible.
     * @return value of cancelButtonVisible.
     */
    public boolean isCancelButtonVisible() {
	return cancelButtonVisible;
    }
    
    /**
     * Set the value of cancelButtonVisble.
     * @param v  Value to assign to cancelButtonVisble.
     */
    public void setCancelButtonVisible(boolean  v) {
	this.cancelButtonVisible = v;
    }
    
    /**
     * Get the value of deviceButtonVisible.
     * @return value of deviceButtonVisible.
     */
    public boolean isDeviceButtonVisible() {
	return deviceButtonVisible;
    }
    
    /**
     * Set the value of deviceButtonVisible.
     * @param v  Value to assign to deviceButtonVisible.
     */
    public void setDeviceButtonVisible(boolean  v) {
	this.deviceButtonVisible = v;
    }
    
    /**
     * Get the value of descriptionVisible.
     * @return value of descriptionVisible.
     */
    public boolean isDescriptionVisible() {
	return descriptionVisible;
    }
    
    /**
     * Set the value of descriptionVisible.
     * @param v  Value to assign to descriptionVisible.
     */
    public void setDescriptionVisible(boolean  v) {
	this.descriptionVisible = v;
    }

    
    public static void main (String[] args)
    {
        JFrame                     f = new JFrame();     	
	CommandComboViewer         ccv = new CommandComboViewer();
        
        CommandList               clist = new CommandList();
        try
        {
	  clist.add("fp/test/1/*");
        }
        catch (Exception ex)
        {
          System.out.println("Cannot connect to fp/test/1/*");
	  System.exit(-1);
        }
	
	ccv.setModel(clist);
	f.getContentPane().add(ccv);
	f.pack();
	f.setVisible(true);        
	
    } // end of main ()

}
