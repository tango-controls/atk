package fr.esrf.tangoatk.widget.attribute;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import fr.esrf.tangoatk.core.AttributePolledList;
import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.core.INumberScalarListener;
import fr.esrf.tangoatk.core.NumberScalarEvent;
import fr.esrf.tangoatk.widget.util.ATKConstant;

/**
 * @author HO
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class NumberScalarSpinner extends JSpinner implements Serializable, INumberScalarListener,ActionListener, MouseListener, KeyListener
{
    private boolean 			displayReadValue = true;
    private boolean 			valueEditable = true;
    private double 			stepValue = 1;
    private boolean 			alarmEnabled = true;
    
    private SpinnerNumberModel          numberSpinnerModel = null;
    private INumberScalar 		numberModel = null;
        
    public static final String		ACTION_ARROW = "ARROW";
    public static final String		ACTION_CHANGE_VALUE = "VALUE";
    
    
    /**
	* Constructor
	*/
	public NumberScalarSpinner()
	{
        super();
        Double value = new Double(0);
		Double min = new Double(-Double.MAX_VALUE);
		Double max = new Double(Double.MAX_VALUE);
		Double step = new Double(stepValue);
        numberSpinnerModel = new SpinnerNumberModel(value, min, max, step);
        setModel(numberSpinnerModel);
        
        //Component Arrow index 0 and 1
        getComponent(0).setName(ACTION_ARROW);
        getComponent(0).addMouseListener(this);
        getComponent(1).setName(ACTION_ARROW);
        getComponent(1).addMouseListener(this);
        
        ((NumberEditor)getEditor()).getTextField().addMouseListener(this);
        ((NumberEditor)getEditor()).getTextField().setName(ACTION_CHANGE_VALUE);
        ((NumberEditor)getEditor()).getTextField().addActionListener(this);
        ((NumberEditor)getEditor()).getTextField().addKeyListener(this);
    }

//----------------------------------------------------
// Bean Property getters and setters
//----------------------------------------------------	
	
    /**
     * @return Returns the m_displayReadValue.
     */
    public boolean getDisplayReadValue() {
        return displayReadValue;
    }
    /**
     * @param readValue The m_displayReadValue to set.
     */
    public void setDisplayReadValue(boolean isDisplayReadValue) {
        if(numberModel!= null && !numberModel.isWritable())
        {
            isDisplayReadValue = true;
            return;
        }
        displayReadValue = isDisplayReadValue;
    }
    
    public boolean getAlarmEnabled() {
        return alarmEnabled;
    }
    
    /**
     * Enables or disables alarm background (shows quality factor of the attribute).
     * @param b
     */
    public void setAlarmEnabled(boolean b) {
      alarmEnabled = b;
    }
    
    /**
     * @param editable The valueEditable to set.
     */
    public boolean getValueEditable() {
       return  valueEditable;
    }
  
    /**
     * @param editable The valueEditable to set.
     */
    public void setValueEditable(boolean editable) {
        this.valueEditable = editable;
	if (!numberModel.isWritable())
	   ((NumberEditor)getEditor()).getTextField().setEditable(false);
	else
           ((NumberEditor)getEditor()).getTextField().setEditable(editable);
    }
    
    /**
     * @return Returns the stepValue.
     */
    public double getStepValue() {
        return stepValue;
    }
     
    /**
    * Modified the incrementation step
    * @param step
    */
    public void setStepValue(double step)
    {
        stepValue = step;
        numberSpinnerModel.setStepSize(new Double(step));
    }




    public INumberScalar getNumberModel()
    {
        return numberModel;
    }
    
      /**
     * Sets the model for this viewer.
     * @param Number scalar model
     */
    public void setNumberModel(INumberScalar scalar)
    {

      clearModel();
     
      if (scalar != null)
      {
        numberModel = scalar;
        if(!scalar.isWritable() )
            ((NumberEditor)getEditor()).getTextField().setEditable(false);
	else
	    ((NumberEditor)getEditor()).getTextField().setEditable(valueEditable);
        numberModel.addNumberScalarListener(this);
      }

    }
    
     /**
     * Clears all model and listener attached to the components
     */
    public void clearModel()
    {
        if (numberModel != null)
        {
	        numberModel.removeNumberScalarListener(this);
	        numberModel = null;
        }
    }

    public void stateChange(AttributeStateEvent evt)
    {
      if (!alarmEnabled) return;
      String state = evt.getState();
      setBackground(ATKConstant.getColor4Quality(state));
    }

    public void errorChange(ErrorEvent evt) {
      if (!alarmEnabled) return;
      setValue(new Double(0));
      setBackground(ATKConstant.getColor4Quality("UNKNOWN"));
    }
    
    public void setBackground(Color arg0) {
        ((NumberEditor)getEditor()).getTextField().setBackground(arg0);
    }
    
    public Color getBackground() {
        return  ((NumberEditor)getEditor()).getTextField().getBackground();
    }
    
    public void numberScalarChange(NumberScalarEvent evt)
    {
        try
        {
            Double doubleValue;
            if ( (displayReadValue) || (!numberModel.isWritable()) )
    	    {
    		doubleValue = new Double(evt.getValue());
    	    }
            else
            {
		doubleValue = new Double(evt.getNumberSource().getNumberScalarSetPoint());
            }
            setValue(doubleValue);
        }
    	catch (Exception e)
    	{
    	    setValue(new Double(0));
	}
   }


    
   /*
    *  (non-Javadoc)
    * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
   */
   public void mousePressed(MouseEvent e)
   {
   }
	
   /*
    *  (non-Javadoc)
    * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
    */
   public void mouseReleased(MouseEvent e)
   {
   }

   /*
    *  (non-Javadoc)
    * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
    */
   public void mouseEntered(MouseEvent e)
   {
   }

   public void mouseExited(MouseEvent e)
   {
   }


		
   public void mouseClicked(MouseEvent e)
   {
       if (e.getComponent().getName().equals(ACTION_ARROW))
	   arrowActionPerformed();
   }

   public void actionPerformed(ActionEvent e)
   {
       if (((Component)e.getSource()).getName().equals(ACTION_CHANGE_VALUE))
	   valueChangePerformed();
   }

   /**
    * This method is called when the one of the arrow is activated
    *
    */
   public void arrowActionPerformed()
   {
       if(numberModel == null)
	   return;
        if (!numberModel.isWritable())
	{
           try
	   {
	       Double doubleValue = new Double(numberModel.getNumberScalarValue());
               setValue(doubleValue);
           }
	   catch (Exception devFailed)
	   {
           }
	   return;
	}
       double value = ((Double)getValue()).doubleValue();
       if (((NumberEditor)getEditor()).getTextField().hasFocus())
           numberModel.setValue(value);
   }

   /**
    * This method is called a new value is enter in the field and validated by enter
    *
    */
    public void valueChangePerformed()
    {
	if(numberModel == null)
	    return;
        if (!numberModel.isWritable())
	{
           try
	   {
	       Double doubleValue = new Double(numberModel.getNumberScalarValue());
               setValue(doubleValue);
           }
	   catch (Exception devFailed)
	   {
           }
	   return;
	}
	double value = ((Double)getValue()).doubleValue();
        if(((NumberEditor)getEditor()).getTextField().hasFocus())
            numberModel.setValue(value);
    }
    
    
    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent arg0)
    {
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent arg0) {
        //System.out.println("keyPressed" + arg0.getKeyCode());
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(KeyEvent arg0)
    {
        if((arg0.getKeyCode() == KeyEvent.VK_UP) || (arg0.getKeyCode() == KeyEvent.VK_DOWN))
            arrowActionPerformed();
    }
    
    /**
     * Main class, so you can have an example.
     */
    public static void main(String[] args) {
        try {
            INumberScalar attribute;
            AttributePolledList attributeList = new AttributePolledList();
    	    if (args.length != 0)
    	        attribute = (INumberScalar)attributeList.add(args[0].trim());
    	    else
                attribute = (INumberScalar)attributeList.add("LT1/AE/CH.1/current".toLowerCase());
            attributeList.startRefresher();
    	    NumberScalarSpinner spinner = new NumberScalarSpinner();
            spinner.setNumberModel(attribute);
            spinner.setAlarmEnabled(true);
            //spinner.setValueEditable(false);
            //spinner.setDisplayReadValue(false);
            //spinner.setEnabled(false);
            spinner.setStepValue(2);
            JFrame f = new JFrame("NumberScalarSpinner");
            f.getContentPane().add(spinner);
            f.setSize(300, 50);
            f.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

   
}
