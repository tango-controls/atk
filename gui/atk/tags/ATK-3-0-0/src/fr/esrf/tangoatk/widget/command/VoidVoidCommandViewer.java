package fr.esrf.tangoatk.widget.command;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.command.VoidVoidCommand;
import fr.esrf.tangoatk.widget.util.jdraw.JDrawable;
import fr.esrf.tangoatk.widget.util.jdraw.JDSwingObject;

import java.awt.event.*;
import javax.swing.*;

/**
 * Simple command button.
 */
public class VoidVoidCommandViewer extends JButton
        implements IResultListener,JDrawable {

  protected ICommand model;
  private String overridedText = ""; // Used by jdraw extension

  static String[] exts = {"text"};

  public VoidVoidCommandViewer() {

    setText("command-name");
    addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        executeButtonActionPerformed(evt);
      }
    });

  }

  protected void executeButtonActionPerformed(ActionEvent actionevent) {

    //System.out.println("Called executeButtonActionPerformed in VoidVoidCommandViewer");
    if (model != null)
      model.execute();

  }

  public void resultChange(ResultEvent resultevent) {
  }

  public void errorChange(ErrorEvent errorevent) {
  }

  public void setModel(ICommand cmd) {

    if (model != null) {
      setText("command-name");
      setToolTipText(null);
      model = null;
    }

    if (cmd != null) {
      if (cmd instanceof VoidVoidCommand) {
        //System.out.println("The command is a VoidVoidCommand");
        model = cmd;
        if(overridedText.length()==0)
          setText(model.getNameSansDevice());
        else
          setText(overridedText);
        setToolTipText(model.getDevice().toString());
      }
    }

  }

  // ------------------------------------------------------
  // Implementation of JDrawable interface
  // ------------------------------------------------------
  public void initForEditing() {
    setBorder(JDSwingObject.etchedBevelBorder);
  }

  public JComponent getComponent() {
    return this;
  }

  public String getDescription(String extName) {

    if (extName.equalsIgnoreCase("text")) {
      return "Overrides text given by the model.";
    }
    return "";

  }

  public String[] getExtensionList() {
    return exts;
  }

  public boolean setExtendedParam(String name,String value,boolean popupErr) {

    if (name.equalsIgnoreCase("text")) {
      overridedText = value;
      if(overridedText.length()>0)
        setText(value);
      return true;
    }

    return false;

  }

  public String getExtendedParam(String name) {

    if(name.equalsIgnoreCase("text")) {
      return overridedText;
    }

    return "";

  }
  
  // To test the viewer



  public static void main(String [] args)
  {
     fr.esrf.tangoatk.core.CommandList  cmdl = new fr.esrf.tangoatk.core.CommandList();

     VoidVoidCommandViewer  vvcv = new VoidVoidCommandViewer();

     try 
     {
	ICommand  ic = (ICommand)cmdl.add("elin/gun/beam/Off");
	vvcv.setModel(ic);
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
     f.getContentPane().add(vvcv, gbc);
     f.pack();
     f.setVisible(true);
  }


}
