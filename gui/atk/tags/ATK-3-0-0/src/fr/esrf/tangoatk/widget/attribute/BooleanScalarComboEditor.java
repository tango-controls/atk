/*
 * BooleanScalarComboEditor.java
 *
 * Author:Faranguiss Poncet (december 2006)
 */

package fr.esrf.tangoatk.widget.attribute;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

import fr.esrf.tangoatk.core.*;


/**
 * A class to set the value of a BooleanScalar attribute
 * by selecting the value True or False in a combobox.
 * 
 * @author  poncet
 */
public class BooleanScalarComboEditor extends JComboBox 
                                     implements ActionListener, IBooleanScalarListener, ISetErrorListener
{


  private IBooleanScalar   attModel=null;
  private String           trueLabel="True";
  private String           falseLabel="False";
    
  private DefaultComboBoxModel     comboModel=null;
  protected String                 defActionCmd="setAttActionCmd";
  private String[]                 optionList={trueLabel, falseLabel};
  static final int                 trueIndex=0;
  static final int                 falseIndex=1;
  

  // ---------------------------------------------------
  // Contruction
  // ---------------------------------------------------
  public BooleanScalarComboEditor()
  {
       attModel = null;
       comboModel = new DefaultComboBoxModel(optionList);
       
       this.setModel(comboModel);
       this.setActionCommand(defActionCmd);
       this.addActionListener(this);
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
    	  attModel.removeSetErrorListener(this);
    	  attModel = null;
      }

      if( boolModel==null ) return;

      if (!boolModel.isWritable())
	throw new IllegalArgumentException("BooleanScalarComboEditor: Only accept writeable attribute.");
      
      optionList = new String[]{trueLabel,falseLabel};
      comboModel = new DefaultComboBoxModel(optionList);
      this.setModel(comboModel);
      attModel = boolModel;
      attModel.addBooleanScalarListener(this);
      attModel.addSetErrorListener(this);
      attModel.refresh();
      repaint();
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
      JComboBox        cb=null;
      boolean          selectedOption;
      int              idx = 0;
      String           optValueStr=null;

      if ( !(e.getActionCommand().equals(defActionCmd)) )
	  return;

      if (attModel == null)
         return;
	 
      cb = (JComboBox) e.getSource();
      if (cb.getSelectedIndex() < 0) return;
      
      selectedOption = (cb.getSelectedIndex() == trueIndex);
      if (selectedOption == true)
      {
         attModel.setValue(true);
      }
      else
      {
         attModel.setValue(false);
      }
  }

  // ---------------------------------------------------
  // IBooleanScalarListener listener
  // ---------------------------------------------------

  // Listen on "setpoint" change
  // this is not clean yet as there is no setpointChangeListener
  // Listen on valueChange and readSetpoint
  public void booleanScalarChange(BooleanScalarEvent e)
  {
	boolean        setpoint;

	if(hasFocus())
	    setpoint = attModel.getDeviceSetPoint();
	else
	    setpoint = attModel.getSetPoint();
    
    changeCurrentSelection(setpoint);
  }

  public void stateChange(AttributeStateEvent e)
  {
  }

  public void errorChange(ErrorEvent evt)
  {
      disableExecution();
      setSelectedIndex(-1);
      repaint();
      enableExecution();
  }


  // ---------------------------------------------------
  // ISetErrorListener listener
  // ---------------------------------------------------
  public void setErrorOccured(ErrorEvent evt)
  {
     if (attModel == null)
        return;

     if (evt.getSource() != attModel)
        return;
	
     changeCurrentSelection(attModel.getDeviceSetPoint());
  }


    
  private void changeCurrentSelection(boolean newValue)
  {
      disableExecution();
      if (newValue == true)
         setSelectedIndex(trueIndex);
      else
         setSelectedIndex(falseIndex);
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

  // ---------------------------------------------------
  // Main test fucntion
  // ---------------------------------------------------
  static public void main(String args[])
  {
       IEntity                    ie;
       IBooleanScalar             bAtt;
       AttributeList              attl = new AttributeList();
       JFrame                     f = new JFrame();
       BooleanScalarComboEditor   bsce = new BooleanScalarComboEditor();

       try
       {
	  ie = attl.add("jlp/test/1/att_boolean");
	  //ie = attl.add("dev/test/10/Boolean_attr_w");
	  if (!(ie instanceof IBooleanScalar))
	  {
              System.out.println("jlp/test/1/att_boolean is not a booleanScalar");
	      System.exit(0);
	  }

          bAtt = (IBooleanScalar) ie;
	  bsce.setAttModel(bAtt);
       }
       catch (Exception ex)
       {
          System.out.println("Cannot connect to jlp/test/1/att_boolean");
	  System.exit(-1);
       }
       
       attl.startRefresher();

       f.setContentPane(bsce);
       f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       f.pack();
       f.setVisible(true);
  }


public String getFalseLabel() {
    return falseLabel;
}


public void setFalseLabel(String falseLabel) {
    this.falseLabel = falseLabel;
}


public String getTrueLabel() {
    return trueLabel;
}


public void setTrueLabel(String trueLabel) {
    this.trueLabel = trueLabel;
}

}
