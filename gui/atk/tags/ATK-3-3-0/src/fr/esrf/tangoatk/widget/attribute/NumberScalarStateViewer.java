/*
 * NumberScalarStateViewer.java
 *
 * Created on July 31, 2003, 15:00 PM
 */

/** <code>NumberScalarStateViewer</code>
 * NumberScalarStateViewer is a viewer which translates the value of a
 * NumberScalar attribute to a Tango Device state and maps it to the
 * resulting state color.
 *
 * @author  <a href="mailto:poncet@esrf.fr">Faranguiss Poncet</a>
 */
 
package fr.esrf.tangoatk.widget.attribute;


import javax.swing.*;

import fr.esrf.Tango.DevState;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.ATKConstant;

public class NumberScalarStateViewer
             extends JLabel
	     implements INumberScalarListener
{
    
    private INumberScalar       model;


    public NumberScalarStateViewer()
    {
        model = null;
	initComponents();
    }
    

    private void initComponents()
    {
	setFont(new java.awt.Font("Dialog", 0, 12));
	setPreferredSize(new java.awt.Dimension(20, 20));
	setMinimumSize(new java.awt.Dimension(20, 20));
	setOpaque(true);
    }
    


    public static String getStringState(int  numberState)
    {
	switch (numberState)
	{
	    case DevState._ON:       return IDevice.ON;
	    case DevState._OFF:      return IDevice.OFF;
	    case DevState._CLOSE:    return IDevice.CLOSE;
	    case DevState._OPEN:     return IDevice.OPEN;
	    case DevState._INSERT:   return IDevice.INSERT;
	    case DevState._EXTRACT:  return IDevice.EXTRACT;
	    case DevState._MOVING:   return IDevice.MOVING;
	    case DevState._STANDBY:  return IDevice.STANDBY;
	    case DevState._FAULT:    return IDevice.FAULT;
	    case DevState._INIT:     return IDevice.INIT;
	    case DevState._RUNNING:  return IDevice.RUNNING;
	    case DevState._ALARM:    return IDevice.ALARM;
	    case DevState._DISABLE:  return IDevice.DISABLE;
	    default:                 return IDevice.UNKNOWN;	    
        } // end of switch ()
    }
    


    public INumberScalar getModel()
    {
        return model;
    }
    

    public void setModel(INumberScalar  ins)
    {
	if (model != null)
	    clearModel();

	model = ins;
	
	if (model != null)
	{
	    model.addNumberScalarListener(this);
	    setToolTipText(model.getName());
	}
    }
    
    public void clearModel()
    {
  	if (model != null)
        {
	   model.removeNumberScalarListener(this);
           model = null;
        }
    }

    public void numberScalarChange(NumberScalarEvent evt)
    {
        int      newStateValue;
	String   newStateString;
	
	newStateValue = (int) evt.getValue();
	newStateString = getStringState(newStateValue);
        setBackground(ATKConstant.getColor4State(newStateString));
    }

    public void stateChange(AttributeStateEvent evt)
    {
    }

    public void errorChange(ErrorEvent evt)
    {
    }

 
    
    public static void main(String[] args)
    {
       fr.esrf.tangoatk.core.AttributeList  attList = new fr.esrf.tangoatk.core.AttributeList();
       NumberScalarStateViewer          nssv = new NumberScalarStateViewer();
       INumberScalar               att;
       JFrame                      mainFrame;
       
       // Connect to a "writable" string scalar attribute
       try
       {
          att = (INumberScalar) attList.add("id14/cryospy/eh3/IcingStatus");
	  nssv.setModel(att);
       }
       catch (Exception ex)
       {
          System.out.println("caught exception : "+ ex.getMessage());
	  System.exit(-1);
       }
       
       attList.startRefresher();
       
       mainFrame = new JFrame();
       
       mainFrame.getContentPane().add(nssv);
       mainFrame.pack();

       mainFrame.setVisible(true);

    } // end of main ()



}

