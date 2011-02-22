package fr.esrf.tangoatk.widget.command;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.ICommand;
import fr.esrf.tangoatk.core.IErrorListener;
import fr.esrf.tangoatk.core.command.ScalarVoidCommand;
import fr.esrf.tangoatk.core.command.StringVoidCommand;


/** An OptionComboCommandViewer is a StringVoidCommand viewer. This means that
 * the command used as the model for this viewer takes a String input argument
 * and returns no output argument.
 * To be able to use a String-Void Command as the model for OptionComboCommandViewer
 * the String-Void command should accept only a limited number of strings
 * as it's input. Typically this viewer is used for a command which accepts a
 * limited number of "options" defined as strings.
 * The valid options for the command are displayed in a comboBox. When an item is
 * selected in the comboBox, the command is executed with the
 * input parameter = selected item.
 * The list of possible options is given to the viewer by calling the
 * method "setOptionList".
 */
public class OptionComboCommandViewer extends JPanel 
                                      implements ActionListener, IErrorListener 
{

   private JComboBox                jComboBox;
   private JLabel                   jLabel;
   private DefaultComboBoxModel     comboModel=null;
   private String                   defActionCmd="userActionCmd";


        

    /* The bean properties */
   private java.awt.Font    theFont;
   private ICommand         model=null;
   private String[]         optionList = {"None"};
   private String           title=null;

   // ---------------------------------------------------
   // Contruction
   // ---------------------------------------------------
   public OptionComboCommandViewer()
   {
	 if (title == null)
	    title = new String("no title");
         theFont = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 14);
	    
	 initComponents();
	 comboModel = new DefaultComboBoxModel(optionList);
	 jComboBox.setModel(comboModel);
	 jComboBox.setActionCommand(defActionCmd);
   }

   public OptionComboCommandViewer(String cmdTitle)
   {
         title = cmdTitle;
	 if (title == null)
	    title = new String("no title");
         theFont = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 14);
	    
	 initComponents();
	 comboModel = new DefaultComboBoxModel(optionList);
	 jComboBox.setModel(comboModel);
	 jComboBox.setActionCommand(defActionCmd);
   }
  
  
  
   private void initComponents()
   {
      java.awt.GridBagConstraints gridBagConstraints;

      jLabel = new javax.swing.JLabel();
      jComboBox = new javax.swing.JComboBox();


      setLayout(new java.awt.GridBagLayout());

      jLabel.setText(title);
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 2);
      add(jLabel, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
      add(jComboBox, gridBagConstraints);
      
      jComboBox.addActionListener(this);

   }
   
   
   public void enableExecution()
   {
       jComboBox.setActionCommand(defActionCmd);
   }
   
   
   public void disableExecution()
   {
       jComboBox.setActionCommand("dummy");
   }



  // ---------------------------------------------------
  // Property stuff
  // ---------------------------------------------------

    public java.awt.Font getTheFont()
    {
       return(theFont);
    }
    

    public void setTheFont(java.awt.Font  ft)
    {
       if (ft != null)
       {
	  theFont = ft;
	  jLabel.setFont(theFont);
	  jComboBox.setFont(theFont);
	  
       }
    }


  public ICommand getModel( )
  {
     return model;
  }
  
  public void setModel( ICommand optionCmdModel)
  {

      if (model != null)
      {
	  model.removeErrorListener(this);
	  model = null;
      }

      if (optionCmdModel != null)
      {
          //Command String and Numerical works.
          if (optionCmdModel instanceof ScalarVoidCommand)
          {
              //System.out.println("The command is a ScalarVoidCommand");
              model = optionCmdModel;
              model.addErrorListener(this);
          }
      }
  }
  

  public void clearModel()
  {
      setModel( (ICommand) null);
  }




  public String[] getOptionList( )
  {
     return optionList;
  }
  
  public void setOptionList( String[] optStrList)
  {
      DefaultComboBoxModel     cbModel=null;
      
      if (optStrList == null)
      {
         String[]  opts = {"None"};
         optionList = opts;
      }
      else
         optionList = optStrList;
	 
      cbModel = new DefaultComboBoxModel(optionList);
      jComboBox.setModel(cbModel);
      comboModel = cbModel;
      
  }




  public String getTitle( )
  {
     return title;
  }
  
  public void setTitle( String newTitle)
  {
      if (newTitle == null)
	    title = new String("no title");
      else
	    title = newTitle;
      jLabel.setText(title);
  }
  
  
  public int getSelectedIndex()
  {
      return jComboBox.getSelectedIndex();
  }
  
  
  public void setSelectedIndex(int  idx) throws IllegalArgumentException
  {
      jComboBox.setSelectedIndex(idx);
  }





  // ---------------------------------------------------
  // Action listener
  // ---------------------------------------------------
  public void actionPerformed(ActionEvent e)
  {
      
      JComboBox        cb=null;
      String           cmdOption = null;
      java.util.List   inarg = new java.util.Vector();
      //String           boolStr;


      cb = (JComboBox) e.getSource();
      cmdOption = (String) cb.getSelectedItem();
      
      if ( !(e.getActionCommand().equals(defActionCmd)) )
      {
          jComboBox.setActionCommand(defActionCmd);
	  return;
      }
      
      if (cmdOption == null)
         return;
 
      if (model == null)
         return;
	 
      inarg.add(0, cmdOption);
      model.execute(inarg);
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
       String[]   opts = {"ext", "imm", "bus"};
       fr.esrf.tangoatk.core.IEntity   ie;
       fr.esrf.tangoatk.core.ICommand  ic;
       fr.esrf.tangoatk.core.CommandList commandlist =
	    new fr.esrf.tangoatk.core.CommandList();
       JFrame f = new JFrame();
       JPanel jp = new JPanel();
       OptionComboCommandViewer optCmd = new OptionComboCommandViewer();
       optCmd.setBorder(BorderFactory.createEtchedBorder());
       jp.add(optCmd);
       
       try
       {
	  ie = commandlist.add("sr/Agilent_4395a/1/SetTriggerSource");
	  if (ie instanceof StringVoidCommand)
	     System.out.println("Oui c'est un stringVoid command");
	  else
	     System.out.println("Desolee ce n'est pas un stringVoid command");

	  if (ie instanceof fr.esrf.tangoatk.core.ICommand)
	  {
              ic = (fr.esrf.tangoatk.core.ICommand) ie;
	      
	      optCmd.setModel(ic);
	      optCmd.setOptionList(opts);
	      optCmd.setTitle(ic.getNameSansDevice());
	  }
       }
       catch (Exception ex)
       {
          System.out.println("Cannot connect to sr/agilent/1/SetTriggerSource");
       }
  
       
       f.setContentPane(jp);
       f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       f.pack();
       f.setVisible(true);
  }

}
