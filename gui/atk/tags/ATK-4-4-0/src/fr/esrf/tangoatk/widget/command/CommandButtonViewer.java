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
 

package fr.esrf.tangoatk.widget.command;

import fr.esrf.tangoatk.core.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import fr.esrf.tangoatk.core.ICommand;
import fr.esrf.tangoatk.core.command.VoidVoidCommand;
import fr.esrf.tangoatk.core.command.InvalidCommand;

/**
 * @deprecated use SimpleCommandButtonViewer instead
 */
public class CommandButtonViewer extends JPanel
{
    JFrame argFrame = new JFrame();
    AnyCommandViewer acv = new AnyCommandViewer();    

    private JButton executeButton;


  public CommandButtonViewer()
  {
        initComponents();
	argFrame.getContentPane().add(acv);
  }

  private void initComponents()
  {
    executeButton = new JButton();
    setLayout(new BorderLayout());
    executeButton.setText("command-name");
    executeButton.addActionListener
	     ( new java.awt.event.ActionListener()
               {
        	   public void actionPerformed(java.awt.event.ActionEvent evt)
		   {
                       executeButtonActionPerformed(evt);
        	   }
               });
    add(executeButton, "Center");
  }

  private void executeButtonActionPerformed(ActionEvent actionevent)
  {

      if (model instanceof InvalidCommand) {
	  javax.swing.JOptionPane.showMessageDialog(this, model.getName() + " is not supported. It probably takes an array as input.", "Error", 1);
	  return;
      }

      if (model instanceof VoidVoidCommand) {
	  model.execute();
	  return;
      }


      acv.initialize(model);
      acv.setDeviceButtonVisible(deviceButtonVisible);
      acv.setDescriptionVisible(descriptionVisible);
      acv.setInfoButtonVisible(infoButtonVisible);

      acv.setBorder(null);
      acv.setInputVisible(true);	     
      if (!model.takesInput()) {
	  model.execute();
      } 


      argFrame.setTitle(model.getName());
      argFrame.pack();
      argFrame.setVisible(true);
  }




  public void setEnabled(boolean flag)
  {
    executeButton.setEnabled(flag);
  }

  public boolean isEnabled()
  {
    return executeButton.isEnabled();
  }




     /* The getter and setter methods for Bean properties */

    //---------------------------------------------------------
    

    ICommand   model;

    public void setModel(ICommand icommand)
    {
      model = icommand;
      executeButton.setText(model.getNameSansDevice());
      executeButton.setToolTipText(model.getDevice().toString());
    }

    public ICommand getModel()
    {
      return model;
    }

    //---------------------------------------------------------
   
    public void setFont(java.awt.Font font) {		
	if (executeButton == null)
	    return;
		
	executeButton.setFont(font);
    }

    public java.awt.Font getFont() {	
	if (executeButton == null)
	    return super.getFont();
	return executeButton.getFont();
    }

    //---------------------------------------------------------
    
    public void setBackground(java.awt.Color color) {
	super.setBackground(color);
	if (executeButton == null)
	    return;
	executeButton.setBackground(color);
    }

    //---------------------------------------------------------
    
    public void setForeground(java.awt.Color color) {
	super.setForeground(color);
	if (executeButton == null)
	    return;
	executeButton.setForeground(color);
    }

    //---------------------------------------------------------
    
    boolean borderVisble;
    
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

    //---------------------------------------------------------
    
    boolean descriptionVisible;
    
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

    //---------------------------------------------------------
    
    boolean infoButtonVisible;
    
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

    //---------------------------------------------------------
    
    boolean deviceButtonVisible;
    
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

    //---------------------------------------------------------
    
    boolean cancelButtonVisible;
    
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



    public static void main (String[] args) throws Exception {
	fr.esrf.tangoatk.core.CommandList clist =
	    new fr.esrf.tangoatk.core.CommandList();
	CommandButtonViewer cmdBut = new CommandButtonViewer();
	clist.add("eas/test-api/1/IOFloatArray");
	cmdBut.setModel((ICommand) clist.add("eas/test-api/1/IOFloatArray"));
	JFrame f = new JFrame();
	f.getContentPane().add(cmdBut);
	f.pack();
	f.setVisible(true);
	
    } // end of main ()

}
