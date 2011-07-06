/*
 *  Copyright (C) :	2009
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
 
package fr.esrf.tangoatk.widget.attribute;


import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.DevStateScalarEvent;
import fr.esrf.tangoatk.core.Device;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.IDevStateScalar;
import fr.esrf.tangoatk.core.IDevStateScalarListener;
import fr.esrf.tangoatk.core.IDevice;
import fr.esrf.tangoatk.core.IDeviceApplication;
import fr.esrf.tangoatk.widget.device.IDevicePopUp;
import fr.esrf.tangoatk.widget.device.SingletonStatusViewer;
import fr.esrf.tangoatk.widget.util.*;
import java.awt.GridBagConstraints;
import javax.swing.JFrame;

/**
 * <code>SimpleStateViewer</code> is a viewer to surveil a state attribute
 * {@link fr.esrf.tangoatk.core.attribute.DevStateScalar}. Background color are defined in
 * {@link fr.esrf.tangoatk.widget.util.ATKConstant} . SimpleStateViewer offer
 * the possibility to use Antialiased font for better rendering.
 * SimpleStateViewer has no label. Normally one connects the DevStateScalar attribute
 * with the viewer like this:
 * <p>
 * <pre>
 * IDevStateScalar stateAtt = (IDevStateScalar) attributeList.add("myStateAttName");
 * SimpleStateViewer statev = new SimpleStateViewer();
 * statev.setModel(stateAtt);
 * </pre>
 */
public class SimpleStateViewer extends JSmoothLabel implements IDevStateScalarListener
{

    private IDevStateScalar   model=null;
    private String            currentState = "UNKNOWN";
    /*private String            currentText = "";
    private boolean           externalSetText = false;*/
    private boolean           stateClickable = true;

    // For backward compatibility with fr.esrf.tangoatk.widget.device.SimpleStateViewer
    private IDeviceApplication     application;
    private IDevicePopUp           popUp = SingletonStatusViewer.getInstance();

    private boolean          hasToolTip=true;
    private boolean          stateInTooltip=true;
    
    /**
    * Contructs a SimpleStateViewer.
    */
    public SimpleStateViewer()
    {
        setFont(ATKConstant.labelFont);
        setPreferredSize(new java.awt.Dimension(40, 14));
        setOpaque(true);
        setState(IDevice.UNKNOWN);
        addMouseListener(
                new java.awt.event.MouseAdapter()
                {
                  public void mouseClicked(java.awt.event.MouseEvent evt)
                  {
                    stateViewerMouseClicked(evt);
                  }
                });
    }

    private void stateViewerMouseClicked(java.awt.event.MouseEvent evt)
    {
        if (!stateClickable) return;
        if (model == null) return;
        
        IDevice  dev = model.getDevice();
        
        if ( (evt.getModifiers() & java.awt.event.InputEvent.BUTTON3_MASK) != 0)
        {
            if (application == null) return;
            application.setModel(dev);
            application.run();
        }
        else
        {
            popUp.setModel(dev);
            popUp.setVisible(true);
        }
    }
    /**
     * <code>setHasToolTip</code> display or not a tooltip for this viewer
     *
     * @param b If True the attribute full name will be displayed as tooltip for the viewer
     */
    public void setHasToolTip(boolean b)
    {

        if (hasToolTip == b)
        {
            return;
        }

        hasToolTip = b;

        if (!hasToolTip)
        {
            setToolTipText(null);
            return;
        }
    }

    /**
    * <code>setModel</code> sets the model of this viewer.
    * If the textLabel property is not set, the name of the device is
    * shown on the textLabel.
    * @param IDevStateScalar a <code>stateAtt</code> to surveil
    */
    public void setModel(IDevStateScalar stateAtt)
    {
        if (model != null) clearModel();

        if (stateAtt == null) return;

        model = stateAtt;
        if (!stateAtt.areAttPropertiesLoaded())
           stateAtt.loadAttProperties();
        
        model.addDevStateScalarListener(this);
        if (hasToolTip)
           setToolTipText(model.getDevice().getName());
        model.refresh();
    }

    public void clearModel()
    {
        if (model != null)
        {
            model.removeDevStateScalarListener(this);
            model = null;
            setState(IDevice.UNKNOWN);
            if (hasToolTip)
               setToolTipText("no device");
            else
               setToolTipText(null);
        }
    }

    /**
    * <code>getModel</code> gets the model of this stateviewer.
    *
    * @return a <code>IDevStateScalar</code> value
    */
    public IDevStateScalar getModel()
    {
        return model;
    }

    /**
    * <code>setState</code>
    *
    * @param state a <code>String</code> value
    */
    private void setState(String state)
    {
        currentState = state;
        if (model != null)
        {
            setBackground(ATKConstant.getColor4State(currentState, model.getInvertedOpenClose(), model.getInvertedInsertExtract()));
            if ((hasToolTip) && (stateInTooltip))
               setToolTipText(model.getDevice().getName() + " : " + currentState);
        }
        else
        {
            setBackground(ATKConstant.getColor4State(state));
            if (hasToolTip)
                setToolTipText("");
            else
                setToolTipText(null);
        }
    }


    /**
    * <code>setStateClickable</code> will the state be clickable?
    *
    * @param clickable a <code>boolean</code> value
    */
    public void setStateClickable(boolean clickable)
    {
        stateClickable = clickable;
    }

    /**
    * <code>getStateClickable</code> returns if the state is clickable or not.
    *
    * @return a <code>boolean</code> value
    */
    public boolean getStateClickable()
    {
        return stateClickable;
    }

    /**
    * Set the application which will be displayed on right mouse click.
    * @param runnable Application to be launched
    */
    public void setApplication(IDeviceApplication runnable)
    {
        application = runnable;
    }

    /**
    * Gets the application attached to this state viewer.
    * @see #setApplication
    */
    public IDeviceApplication getApplication()
    {
        return application;
    }

    /**
    * Get the value of popUp.
    * @return value of popUp.
    * @see #setPopUp
    */
    public IDevicePopUp getPopUp()
    {
        return popUp;
    }

    /**
    * Set the popup which will be displayed on left mouse click.
    * @param v  Value to assign to popUp.
    */
    public void setPopUp(IDevicePopUp v)
    {
        this.popUp = v;
    }

    public void devStateScalarChange(DevStateScalarEvent evt)
    {
        setState(evt.getValue());
    }

    public void stateChange(AttributeStateEvent e)
    {
    }

    public void errorChange(ErrorEvent evt)
    {
        setState(IDevice.UNKNOWN);
    }
    
    
    public static void main(String[] args)
    {
       final fr.esrf.tangoatk.core.AttributeList  attList = new fr.esrf.tangoatk.core.AttributeList();
       IDevStateScalar                            attState;
       JFrame                                     mainFrame;
       
       SimpleStateViewer                          sstv;
       GridBagConstraints                         gdbc;

        /*java.awt.Font f = new java.awt.Font("Serif", 1, 22);
        java.awt.geom.AffineTransform t = new java.awt.geom.AffineTransform();
        t.scale(1.0, 2.0);
        java.awt.Font large_font = f.deriveFont(t);*/
       
       java.awt.Font large_font = new java.awt.Font("Dialog", 0, 18);
       mainFrame = new JFrame();
       
       mainFrame.addWindowListener(
	       new java.awt.event.WindowAdapter()
			  {
			      public void windowActivated(java.awt.event.WindowEvent evt)
			      {
				 // To be sure that the refresher (an independente thread)
				 // will begin when the the layout manager has finished
				 // to size and position all the components of the window
				 attList.startRefresher();
			      }
			  }
                                     );
       javax.swing.JPanel   jp = new javax.swing.JPanel();
       jp.setLayout(new java.awt.GridBagLayout());

       // Connect to a list of DevStateScalar attributes
       try
       {
          gdbc = new GridBagConstraints();
          gdbc.insets = new java.awt.Insets(5, 5, 5, 5);
          
          sstv = new SimpleStateViewer();
          sstv.setFont(large_font);
          //sstv.setBorder(new javax.swing.border.EtchedBorder(new java.awt.Color(204, 204, 204), java.awt.Color.gray));
          sstv.setHorizontalAlignment(JSmoothLabel.CENTER_ALIGNMENT);
          //sstv.setSizingBehavior(JSmoothLabel.MATRIX_BEHAVIOR);
          sstv.setStateClickable(false);
          attState = (IDevStateScalar) attList.add("sr/d-mfdbk/horizontal/State");
	  sstv.setModel(attState);
	  sstv.setText("Horizontal");
          gdbc.gridx = 0;
          gdbc.gridy = 0;
          jp.add(sstv, gdbc);
          
          sstv = new SimpleStateViewer();
          sstv.setFont(large_font);
          //sstv.setBorder(new javax.swing.border.EtchedBorder(new java.awt.Color(204, 204, 204), java.awt.Color.gray));
          sstv.setHorizontalAlignment(JSmoothLabel.CENTER_ALIGNMENT);
          //sstv.setSizingBehavior(JSmoothLabel.MATRIX_BEHAVIOR);
          sstv.setStateClickable(false);
          attState = (IDevStateScalar) attList.add("sr/d-mfdbk/vertical/State");
	  sstv.setModel(attState);
	  sstv.setText("Vertical");
          gdbc.gridx = 1;
          gdbc.gridy = 0;
          jp.add(sstv, gdbc);
          
          sstv = new SimpleStateViewer();
          sstv.setFont(large_font);
          sstv.setBorder(new javax.swing.border.EtchedBorder(new java.awt.Color(204, 204, 204), java.awt.Color.gray));
          sstv.setHorizontalAlignment(JSmoothLabel.CENTER_ALIGNMENT);
          //sstv.setSizingBehavior(JSmoothLabel.MATRIX_BEHAVIOR);
          attState = (IDevStateScalar) attList.add("sr/d-gfdbk/horizontal/State");
	  sstv.setModel(attState);
	  sstv.setText("Horizontal");
          gdbc.gridx = 0;
          gdbc.gridy = 1;
          jp.add(sstv, gdbc);
          
          sstv = new SimpleStateViewer();
          sstv.setFont(large_font);
          sstv.setBorder(new javax.swing.border.EtchedBorder(new java.awt.Color(204, 204, 204), java.awt.Color.gray));
          sstv.setHorizontalAlignment(JSmoothLabel.CENTER_ALIGNMENT);
          //sstv.setSizingBehavior(JSmoothLabel.MATRIX_BEHAVIOR);
          attState = (IDevStateScalar) attList.add("sr/d-gfdbk/vertical/State");
	  sstv.setModel(attState);
	  sstv.setText("Vertical");
          gdbc.gridx = 1;
          gdbc.gridy = 1;
          jp.add(sstv, gdbc);
          
       }
       catch (Exception ex)
       {
          System.out.println("caught exception : "+ ex.getMessage());
	  System.exit(-1);
       }
				     

       mainFrame.setContentPane(jp);
       mainFrame.pack();

       mainFrame.setVisible(true);

    } // end of main ()

}
