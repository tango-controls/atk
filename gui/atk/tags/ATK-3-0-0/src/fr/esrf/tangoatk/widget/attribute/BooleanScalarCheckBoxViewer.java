// File:          BooleanScalarCheckBoxViewer.java
// Created:       2005-02-14 18:15:00, poncet
// By:            <poncet@esrf.fr>
//
// $Id$
//
// Description:
package fr.esrf.tangoatk.widget.attribute;



import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.jdraw.JDrawable;


/** An BooleanScalarCheckBoxViewer is a BooleanScalar attribute viewer. This means that
 * the attribute used as the model for this viewer should implement the IBooleanScalar
 * interface. The viewer is updated when the boolean attribute value changes.
 * The checkBox is "checked" if the attribute value is "true" and it is unchecked
 * if the attribute value is "false".
 *
 */
public class BooleanScalarCheckBoxViewer extends JCheckBox
                                        implements ActionListener,
					           IBooleanScalarListener, ISetErrorListener,
                                                   JDrawable
{


  private IBooleanScalar   attModel=null;
  private String           trueLabel=null;
  private String           falseLabel=null;
  private String           fixedLabel=null;   // JDraw extension, overrides the
                                              // model label

  private boolean          hasToolTip=false;
  private boolean          qualityEnabled = false;
  private Color            background;
  
  static String[] exts = {"text"};

  // ---------------------------------------------------
  // Contruction
  // ---------------------------------------------------
  public BooleanScalarCheckBoxViewer()
  {
    addActionListener(this);
    background = getBackground();
  }

  public BooleanScalarCheckBoxViewer(String title)
  {
    super(title);
    addActionListener(this);
    background = getBackground();
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
	  if (attModel.isWritable())
	      attModel.removeSetErrorListener(this);
	  attModel = null;
	  setText("");
      }

      if (boolModel != null)
      {
	  attModel = boolModel;
	  attModel.addBooleanScalarListener(this);
	  if (attModel.isWritable())
	      attModel.addSetErrorListener(this);
	  if ( (trueLabel == null) && (falseLabel == null) && (fixedLabel==null) )
	      setText(boolModel.getLabel());
          if (hasToolTip) {
              setToolTipText(boolModel.getName());
          }
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
	 if (!isSelected())
 	    setText(falseLabel);
  }
  
  /**
   * <code>getHasToolTip</code> returns true if the viewer has a tooltip (attribute full name)
   *
   * @return a <code>boolean</code> value
   */
  public boolean getHasToolTip()
  {
     return hasToolTip;
  }

  /**
   * <code>setHasToolTip</code> display or not a tooltip for this viewer
   *
   * @param b If True the attribute full name will be displayed as tooltip for the viewer
   */
  public void setHasToolTip(boolean b)
  {
      if (hasToolTip != b)
      {
	 if (b == false)
            setToolTipText(null);
	 else
            if (attModel != null)
               setToolTipText(attModel.getName());
	 hasToolTip=b;
      }
  }

  /**
   *<code>getQualityEnabled</code> returns a boolean to know whether quality will be displayed as background
   * or not.
   * 
   * @return a <code>boolean</code> value
   */
  public boolean getQualityEnabled ()
  {
      return qualityEnabled;
  }

  /**
   * <code>setQualityEnabled</code> view or not the attribute quality for this viewer
   *
   * @param b If True the attribute full name will be displayed as tooltip for the viewer
   * @param qualityEnabled If True the background Color represents the attribute quality factor
   */
  public void setQualityEnabled (boolean b)
  {
     qualityEnabled = b;
     if (!qualityEnabled)
     {
	super.setBackground(background);
	repaint();
     }
  }



  public void clearModel()
  {
      setAttModel( (IBooleanScalar) null);
  }

  // ---------------------------------------------------
  // JDrawable implementation
  // ---------------------------------------------------
  public void initForEditing() {
    setText("CheckBox");
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
      fixedLabel = value;
      if(fixedLabel.length()==0) {
        fixedLabel=null;
        setText("CheckBox");
      } else {
        setText(fixedLabel);
      }
      return true;
    }

    return false;

  }

  public String getExtendedParam(String name) {

    if(name.equalsIgnoreCase("text")) {
      if(fixedLabel!=null) {
        return fixedLabel;
      }
    }

    return "";

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
      }
      else
      {
         attModel.setValue(false);
      }
  }

  // ---------------------------------------------------
  // IBooleanScalarListener listener
  // ---------------------------------------------------
  public void booleanScalarChange(BooleanScalarEvent e)
  {
      setBoolValue(e.getValue());
  }

  public void stateChange(AttributeStateEvent evt)
  {
      String state = evt.getState();
      if (!qualityEnabled) return;
      super.setBackground(ATKConstant.getColor4Quality(state));
      repaint();
  }

  public void errorChange(ErrorEvent evt)
  {
      setEnabled(false);
  }

  protected void setBoolValue (boolean val)
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
  // ISetErrorListener listener
  // ---------------------------------------------------
  public void setErrorOccured(ErrorEvent evt)
  {
     if (attModel == null)
        return;

     if (evt.getSource() != attModel)
        return;
	
     setBoolValue(attModel.getDeviceValue());
  }


  public void setBackground (Color bg) {
    background = bg;
    super.setBackground( bg );
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

       // bsv.setTrueLabel("oui");
       //bsv.setFalseLabel("non");
       try
       {
	  ie = attl.add("jlp/test/1/att_boolean");
	  //ie = attl.add("dev/test/10/Boolean_attr");
	  if (!(ie instanceof IBooleanScalar))
	  {
              System.out.println("jlp/test/1/att_boolean is not a booleanScalar");
	      System.exit(0);
	  }

          bAtt = (IBooleanScalar) ie;
	  bsv.setAttModel(bAtt);
       }
       catch (Exception ex)
       {
          System.out.println("Cannot connect to elin/mod/aux-1/Economy");
       }


       f.setContentPane(bsv);
       f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       f.pack();
       f.setVisible(true);
  }

}
