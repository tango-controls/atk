// File:          BooleanScalarCheckBoxViewer.java
// Created:       2005-02-14 18:15:00, poncet
// By:            <poncet@esrf.fr>
//
// $Id$
//
// Description:
package fr.esrf.tangoatk.widget.attribute;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.core.attribute.*;


/** An BooleanScalarCheckBoxViewer is a BooleanScalar attribute viewer. This means that
 * the attribute used as the model for this viewer should implement the IBooleanScalar
 * interface. The viewer is updated when the boolean attribute value changes.
 * The checkBox is "checked" if the attribute value is "true" and it is unchecked
 * if the attribute value is "false".
 *
 */
public class BooleanScalarCheckBoxViewer extends JCheckBox 
                                        implements ActionListener, IBooleanScalarListener 
{


  private IBooleanScalar   attModel=null;
  private String           trueLabel=null;
  private String           falseLabel=null;

  // ---------------------------------------------------
  // Contruction
  // ---------------------------------------------------
  public BooleanScalarCheckBoxViewer()
  {
    addActionListener(this);
  }

  public BooleanScalarCheckBoxViewer(String title)
  {
    super(title);
    addActionListener(this);
  }

  // ---------------------------------------------------
  // Property stuff
  // ---------------------------------------------------
  
  public IBooleanScalar getAttModel()
  {
     return attModel;
  }
  
  
  public void setAttModel( IBooleanScalar boolModel)
  {
      if (attModel != null)
      {
	  attModel.removeBooleanScalarListener(this);
	  attModel = null;
	  setText("");
      }

      if (boolModel != null)
      {
	  attModel = boolModel;
	  attModel.addBooleanScalarListener(this);
	  if ( (trueLabel == null) && (falseLabel == null) )
	      setText(boolModel.getLabel());
	  //attModel.refresh();
	  setBoolValue(attModel.getDeviceValue());
      }
  }
  
  
  public String getTrueLabel()
  {
     return trueLabel;
  }
  
  
  public void setTrueLabel(String tLabel)
  {
      trueLabel = tLabel;
      
      if ((trueLabel == null) || (falseLabel == null))
      {
         if (attModel != null)
	    setText(attModel.getLabel());
	 else
	    setText(null);
      }
      else
	 if (isSelected())
 	    setText(trueLabel);
  }
  
  
  public String getFalseLabel()
  {
     return falseLabel;
  }
  
  
  public void setFalseLabel(String fLabel)
  {
      falseLabel = fLabel;
      
      if ((trueLabel == null) || (falseLabel == null))
      {
         if (attModel != null)
	    setText(attModel.getLabel());
	 else
	    setText(null);
      }
      else
	 if (isSelected())
 	    setText(falseLabel);
  }
  
  

  public void clearModel()
  {
      setAttModel( (IBooleanScalar) null);
  }


  // ---------------------------------------------------
  // Action Listener
  // ---------------------------------------------------
  public void actionPerformed(ActionEvent e)
  {      
 //System.out.println("BooleanScalarCheckBoxViewer : actionPerformed called");     

      if (attModel == null) return;
      
      if (!attModel.isWritable())
      {
         setSelected(!isSelected());
	 return;
      }
             
      if (isSelected())
      {
         attModel.setValue(true);
	 //if ((trueLabel != null) || (falseLabel != null))
	    //setText(trueLabel);
      }
      else
      {
         attModel.setValue(false);
	 //if ((trueLabel != null) || (falseLabel != null))
	    //setText(falseLabel);
      }
  }

  // ---------------------------------------------------
  // Scalar listener
  // ---------------------------------------------------
  public void booleanScalarChange(BooleanScalarEvent e)
  {
      setBoolValue(e.getValue());
  }
  
  public void stateChange(AttributeStateEvent e)
  {
  }
  
  public void errorChange(ErrorEvent evt)
  {
      setEnabled(false);
  }
  
  private void setBoolValue (boolean val)
  {
      if (!isEnabled())
         setEnabled(true);
	 
      setSelected(val);
      if ((trueLabel != null) || (falseLabel != null))
      {
	 if (val)
	    setText(trueLabel);
	 else
	    setText(falseLabel);
      }
  }
  

  // ---------------------------------------------------
  // Main test fucntion
  // ---------------------------------------------------
  static public void main(String args[])
  {
       IEntity   ie;
       IBooleanScalar  bAtt;
       fr.esrf.tangoatk.core.AttributeList attl =
	    new fr.esrf.tangoatk.core.AttributeList();
       JFrame f = new JFrame();
       BooleanScalarCheckBoxViewer bsv = new BooleanScalarCheckBoxViewer();
       
       bsv.setTrueLabel("oui");
       bsv.setFalseLabel("non");
       try
       {
	  ie = attl.add("dev/test/10/Boolean_attr_w");
	  //ie = attl.add("dev/test/10/Boolean_attr");
	  if (!(ie instanceof IBooleanScalar))
	  {
              System.out.println("dev/test/10/Boolean_attr_w is not a booleanScalar");
	      System.exit(0);
	  }
	  
          bAtt = (IBooleanScalar) ie;
	  bsv.setAttModel(bAtt);
       }
       catch (Exception ex)
       {
          System.out.println("Cannot connect to dev/test/10/Boolean_attr_w");
       }
  
       
       f.setContentPane(bsv);
       f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       f.pack();
       f.setVisible(true);
  }

}
