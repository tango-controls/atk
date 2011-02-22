/*
 * ConfirmCommandViewer.java
 *
 * Created on May 12, 2005
 */

package fr.esrf.tangoatk.widget.command;

import fr.esrf.tangoatk.core.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 *
 * @author  poncet
 */
 
public class ConfirmCommandViewer extends VoidVoidCommandViewer
{
    protected Component   confirmDialParent=null;
    protected String      confirmTitle="Command Execute Confirm Window";
    protected String      confirmMessage="Do you really want to execute this command?\n";


    /** Creates new ConfirmCommandViewer */
    public ConfirmCommandViewer()
    {
        super();
    }




    
    public Component getConfirmDialParent()
    {
        return confirmDialParent;
    }

    
    public void setConfirmDialParent(Component  parent)
    {
        confirmDialParent = parent;
    }


    
    public String getConfirmTitle()
    {
        return confirmTitle;
    }

    
    public void setConfirmTitle(String title)
    {
        confirmTitle = title;
    }


    
    public String getConfirmMessage()
    {
        return confirmMessage;
    }

    
    public void setConfirmMessage(String msg)
    {
        confirmMessage = msg;
    }
    
    protected void executeButtonActionPerformed(ActionEvent actionevent)
    {
	int    userAnswer;
	
 //System.out.println("Called executeButtonActionPerformed in ConfirmCommandViewer");

        userAnswer = JOptionPane.NO_OPTION;
	
	try
	{
	    userAnswer = JOptionPane.showConfirmDialog(confirmDialParent, confirmMessage,
                                    confirmTitle, JOptionPane.YES_NO_OPTION);

	}
	catch (HeadlessException hex)
	{
	}
	
	if (userAnswer == JOptionPane.YES_OPTION)
	    if (model != null)
		model.execute();
    }


    public static void main(String [] args)
    {
       String     title, msg;
       fr.esrf.tangoatk.core.CommandList  cmdl = new fr.esrf.tangoatk.core.CommandList();

       ConfirmCommandViewer  ccv = new ConfirmCommandViewer();

       try 
       {
	  ICommand  ic = (ICommand)cmdl.add("elin/gun/aux/Off");

	  title = "Gun aux OFF command confirmation";
	  msg =   "If you turn off the Gun Aux device now, \n"
	        + "next time when it is turned on it will take 7 minutes for the Gun AUX to heat again.\n\n"
                + "Do you really want to turn off the Gun AUX?\n";
          
	  ccv.setModel(ic);
	  ccv.setConfirmTitle(title);
	  ccv.setConfirmMessage(msg);
	  
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
       gbc.weightx = 1.0;
       gbc.weighty = 1.0;
       f.getContentPane().add(ccv, gbc);
       ccv.setConfirmDialParent(f);
       f.pack();
       f.show();
    }


    public String toString()
    {
       return "{ConfirmCommandViewer}";
    }
}
	    
