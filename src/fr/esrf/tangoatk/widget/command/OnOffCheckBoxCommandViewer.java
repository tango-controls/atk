package fr.esrf.tangoatk.widget.command;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.command.*;


/** An OnOffCheckBoxCommandViewer is a BooleanVoidCommand viewer. This means that
 * the command used as the model for this viewer takes a boolean input argument
 * and returns no output argument.
 * When the checkbox is clicked the command is executed with an input argument
 * which is True or False depending on the state of the checkbox.
 */
public class OnOffCheckBoxCommandViewer extends JCheckBox 
                                        implements ActionListener, IErrorListener 
{


  ICommand cmdModel=null;

  // ---------------------------------------------------
  // Contruction
  // ---------------------------------------------------
  public OnOffCheckBoxCommandViewer()
  {
    addActionListener(this);
  }

  public OnOffCheckBoxCommandViewer(String title)
  {
    super(title);
    addActionListener(this);
  }

  // ---------------------------------------------------
  // Property stuff
  // ---------------------------------------------------
  
  public ICommand getCmdModel()
  {
     return cmdModel;
  }
  
  
  public void setCmdModel( ICommand onOffModel)
  {

      if (cmdModel != null)
      {
	  cmdModel.removeErrorListener(this);
	  cmdModel = null;
	  setText("");
      }

      if (onOffModel != null)
      {
	  if (onOffModel instanceof BooleanVoidCommand)
	  {
//System.out.println("The command is a BooleanVoidCommand");
	     cmdModel = onOffModel;
	     setText(onOffModel.getNameSansDevice() + " Off");
	     cmdModel.addErrorListener(this);
	  }
      }
  }
  

  public void clearModel()
  {
      setCmdModel( (ICommand) null);
  }


  // ---------------------------------------------------
  // Action Listener
  // ---------------------------------------------------
  public void actionPerformed(ActionEvent e)
  {
      JCheckBox                cb=null;
      java.util.List<String>   inarg = new java.util.Vector<String> ();
      String                   boolStr;
      String                   textStr;
      
 //System.out.println("OnOffCheckBoxCommandViewer : actionPerformed called");     

      cb = (JCheckBox) e.getSource();
      
      if (cb.isSelected())
      {
	    boolStr = new String("true");
	    textStr = getText();
	    textStr = textStr.replaceFirst(" Off", " On");
	    setText(textStr);
      }
      else
      {
	    boolStr = new String("false");
	    textStr = getText();
	    textStr = textStr.replaceFirst(" On", " Off");
	    setText(textStr);
      }
      

      if (cmdModel != null)
      {
	  inarg.add(0, boolStr);
	  cmdModel.execute(inarg);
      }
  }

  // ---------------------------------------------------
  // Scalar listener
  // ---------------------------------------------------
  public void errorChange(ErrorEvent e)
  {
      if(e.getSource()==cmdModel)
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
       OnOffCheckBoxCommandViewer onOff = new OnOffCheckBoxCommandViewer();
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
	      
	      onOff.setCmdModel(ic);
	      onOff.setText("Averaging");
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
