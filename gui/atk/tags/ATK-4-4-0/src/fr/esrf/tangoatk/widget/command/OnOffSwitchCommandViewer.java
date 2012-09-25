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



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.command.*;
import fr.esrf.tangoatk.widget.util.ThreeStateSwitch;


/** An OnOffSwitchCommandViewer is a BooleanVoidCommand viewer. This means that
 * the command used as the model for this viewer takes a boolean input argument
 * and returns no output argument.
 * When the viewer is clicked the command is executed with an input argument
 * which is True or False depending on the position of the switch.
 */
public class OnOffSwitchCommandViewer extends ThreeStateSwitch 
                                      implements ActionListener,IErrorListener 
{


  ICommand model=null;

  // ---------------------------------------------------
  // Contruction
  // ---------------------------------------------------
  public OnOffSwitchCommandViewer() {

    addActionListener(this);

  }

  public OnOffSwitchCommandViewer(String title, Font tFont) {

    super(title, tFont);
    addActionListener(this);

  }

  // ---------------------------------------------------
  // Property stuff
  // ---------------------------------------------------
  public ICommand getModel()
  {
     return model;
  }
  
  
  public void setModel( ICommand onOffModel)
  {

      if (model != null)
      {
	  model.removeErrorListener(this);
	  model = null;
      }

      if (onOffModel != null)
      {
	  if (onOffModel instanceof BooleanVoidCommand)
	  {
//System.out.println("The command is a BooleanVoidCommand");
	     model = onOffModel;
	     model.addErrorListener(this);
	  }
      }
  }
  

  public void clearModel()
  {
      setModel(null);
  }

  // ---------------------------------------------------
  // Action listener
  // ---------------------------------------------------
  public void actionPerformed(ActionEvent e)
  {
      java.util.List<String>   inarg = new java.util.Vector<String> ();
      String                   boolStr;
      
      switch( getState() )
      {
	  case ThreeStateSwitch.ON_STATE:
	    boolStr = new String("true");
	    break;
	  case ThreeStateSwitch.OFF_STATE:
	    boolStr = new String("false");
	    break;
	  case ThreeStateSwitch.UNKNOWN_STATE:
	    boolStr = new String("false");
	    break;
	    
	    default: boolStr = new String("false");
      }

      if (model != null)
      {
	  inarg.add(0, boolStr);
	  model.execute(inarg);
      }
      
  }

  // ---------------------------------------------------
  // Scalar listener
  // ---------------------------------------------------
  public void errorChange(ErrorEvent e)
  {
      if(e.getSource()==model)
      {
	JOptionPane.showMessageDialog(this,"Failed to execute command:\n" + e.getError().getMessage());
      }
  }

  // ---------------------------------------------------
  // Main test fucntion
  // ---------------------------------------------------
  static public void main(String args[])
  {
       fr.esrf.tangoatk.core.IEntity   ie;
       fr.esrf.tangoatk.core.ICommand  ic;
       fr.esrf.tangoatk.core.CommandList commandlist =
	    new fr.esrf.tangoatk.core.CommandList();
       JFrame f = new JFrame();
       JPanel jp = new JPanel();
       OnOffSwitchCommandViewer onOff = new OnOffSwitchCommandViewer();
       onOff.setBorder(BorderFactory.createEtchedBorder());
       jp.add(onOff);
       
       try
       {
	  ie = commandlist.add("sr/Agilent_4395a/1/AveragingOnOff");
	  if (ie instanceof BooleanVoidCommand)
	     System.out.println("Oui c'est un booleanVoid command");
	  else
	     System.out.println("Desolee ce n'est pas un booleanVoid command");

	  if (ie instanceof fr.esrf.tangoatk.core.ICommand)
	  {
              ic = (fr.esrf.tangoatk.core.ICommand) ie;
	      
	      onOff.setModel(ic);
	      onOff.setTitle("Averaging");
	  }
       }
       catch (Exception ex)
       {
          System.out.println("Cannot connect to sr/agilent/1/AveragingOnOff");
       }
  
       
       f.setContentPane(jp);
       f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       f.pack();
       f.setVisible(true);
  }

}
