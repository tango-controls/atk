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
 
/*
 * DevStateSpectrumViewer.java
 *
 * Created on July 09, 2008, 11:25
 */

/**
 *
 * @author  poncet
 */
package fr.esrf.tangoatk.widget.attribute;
 
import java.beans.PropertyChangeEvent;
import java.util.Vector;

import javax.swing.*;
import java.beans.PropertyChangeListener;

import java.awt.Font;
import java.awt.GridBagConstraints;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.ATKConstant;


public class DevStateSpectrumViewer extends javax.swing.JPanel
                                    implements IDevStateSpectrumListener, PropertyChangeListener
{    
    public static final String      TOOLTIP_NONE = "None";
    public static final String      TOOLTIP_ATTNAME = "Name";
    
    
    private Vector<JLabel>        labelJLabels;
    private Vector<JLabel>        colorJLabels;
    private Vector<JLabel>        stateJLabels;
    
    /* The bean properties */
    private Font                globalFont;
    private boolean             stateLabelVisible;
    private boolean             stateStringVisible;
    private String              toolTipMode;
    
    private IDevStateSpectrum   model;
    String[]                    modelStateLabels;
    String                      modelLabel;
    
    /** Creates new form DevStateSpectrumViewer */
    public DevStateSpectrumViewer()
    {
        model = null;
	modelStateLabels = null;
	modelLabel = null;
	globalFont = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12);
	stateLabelVisible = true;
	stateStringVisible = true;
	toolTipMode = TOOLTIP_ATTNAME;
        setLayout(new java.awt.GridBagLayout());
	
	setVisible(false);
    }
    
    
    
    
   /**
    * Returns the model for this viewer
    * @see #setModel
    */
    public IDevStateSpectrum getModel()
    {
       return model;
    }

   /**
    * Sets the model for this viewer. The model necessarily implements IDevStateSpectrum interface
    * @param stateSpec : the IDevStateSpectrum attribute to use as model
    * @see #getModel
    */
    public void setModel(IDevStateSpectrum stateSpec)
    {
	clearModel();
	if (stateSpec == null) return;
	
	model = stateSpec;
        if (!model.areAttPropertiesLoaded())
           model.loadAttProperties();
	modelStateLabels = model.getStateLabels();
	modelLabel = model.getLabel();
	model.getProperty("label").addPresentationListener(this);
	
	initComponents();
        setVisible(true);
	
	model.addDevStateSpectrumListener(this);
    }
    
    
    public void clearModel()
    {
        if (model != null)
	{
	   model.removeDevStateSpectrumListener(this);
           model.getProperty("label").removePresentationListener(this);
	   removeComponents();
	   model = null;
	   modelStateLabels = null;
	   modelLabel = null;
	}
    }
    
    
    
    /** This method is called from the setModel() method to create all the visual
     * components necessary to display a spectrum of DevState.
     **/
    private void initComponents()
    {
        String[]                 stateElements;
        GridBagConstraints       gbc;
	JLabel                   elemLabel, elemColor, elemState;
	String                   ttip;
	
        this.setLayout(new java.awt.GridBagLayout());
        stateElements = model.getDeviceValue();
	if (stateElements.length <= 0) return;
	
	labelJLabels = new Vector<JLabel> ();
	colorJLabels = new Vector<JLabel> ();
	stateJLabels = new Vector<JLabel> ();
	
	gbc = new GridBagConstraints();
	
	for (int idx=0; idx < stateElements.length; idx++)
	{
	    elemLabel = new JLabel();
	    elemColor = new JLabel();
	    elemState = new JLabel();
	    
	    ttip = null;
	    if (toolTipMode.equals(TOOLTIP_ATTNAME))
	       ttip = model.getName()+"["+idx+"]";
	       
	    elemLabel.setFont(globalFont);
            elemLabel.setBackground(getBackground());
	    elemLabel.setToolTipText(ttip);
	    
	    elemState.setFont(globalFont);
	    elemState.setBackground(getBackground());
	    elemState.setText(stateElements[idx]);
	    elemState.setToolTipText(ttip);
	    
	    elemColor.setPreferredSize(new java.awt.Dimension(40, 14));
	    elemColor.setOpaque(true);	    	    
	    elemColor.setBackground(ATKConstant.getColor4State(stateElements[idx]));
	    elemColor.setToolTipText(ttip);
	    	    	    
	    if ((modelStateLabels != null) && (idx < modelStateLabels.length))
	       elemLabel.setText(modelStateLabels[idx]);
	    else
	       elemLabel.setText(modelLabel+"["+idx+"]");
	       	    
	    labelJLabels.add(elemLabel);
	    colorJLabels.add(elemColor);
	    stateJLabels.add(elemState);
	    
	    gbc.gridx = 0;
	    gbc.gridy = idx;
	    gbc.fill = GridBagConstraints.BOTH;
	    gbc.insets = new java.awt.Insets(5,5,5,5);
	    add(elemLabel, gbc);
	    
	    gbc.gridx = 1;
	    gbc.gridy = idx;
	    gbc.fill = GridBagConstraints.BOTH;
	    gbc.insets = new java.awt.Insets(5,5,5,5);
	    add(elemColor, gbc);
	    
	    gbc.gridx = 2;
	    gbc.gridy = idx;
	    gbc.fill = GridBagConstraints.BOTH;
	    gbc.insets = new java.awt.Insets(5,5,5,5);
	    add(elemState, gbc);
            
            elemLabel.setVisible(stateLabelVisible);
            elemState.setVisible(stateStringVisible);            
	}
	
    }
    
    private void removeComponents()
    {
       labelJLabels.removeAllElements();
       colorJLabels.removeAllElements();
       stateJLabels.removeAllElements();
       this.removeAll();
       labelJLabels = null;
       colorJLabels = null;
       stateJLabels = null;
    }
    
    


   /**
    * Returns the globalFont used by the viewer
    * @see #setGlobalFont
    */
    public Font getGlobalFont()
    {
       return(globalFont);
    }    
    
   /**
    * Sets the globalFont for this viewer. The globalFont is then applied to all stateLabels and stateStrings
    * @param ft : the font to use for globalFont
    * @see #getGlobalFont
    */
    public void setGlobalFont(Font  ft)
    {
	int                nbStateElements;  
        JLabel             jlab=null;

	if (ft == null) return;

	globalFont = ft;
	if (model == null) return;
       
        nbStateElements = model.getValue().length;

 	for (int idx=0; idx < nbStateElements; idx++)
	{
	    if (labelJLabels != null)
	    {
		try
		{
	           jlab = labelJLabels.get(idx);
		   jlab.setFont(globalFont);
		}
		catch (Exception ex)
		{
		}
	    }

	    if (stateJLabels != null)
	    {
		try
		{
	           jlab = stateJLabels.get(idx);
		   jlab.setFont(globalFont);
		}
		catch (Exception ex)
		{
		}
	    }
	}
    }
    

    
   /**
    * Returns the stateLabel visiblity
    * @see #setStateLabelVisible
    */
    public boolean getStateLabelVisible()
    {
       return(stateLabelVisible);
    }
    
   /**
    * Sets the visiblity for state labels.
    * @param slv : if true the labels associated with each element of the spectrum will be visible in the first column
    * @see #getStateLabelVisible
    */
    public void setStateLabelVisible(boolean  slv)
    {
        if (stateLabelVisible != slv)
	{
	   stateLabelVisible = slv;
	   changeStateLabelVisibility();
	}
    }
        
    private void changeStateLabelVisibility()
    {
	JLabel       jlab = null;

	if (labelJLabels == null) return;
       
 	for (int idx=0; idx < labelJLabels.size(); idx++)
	{
	    try
	    {
	       jlab = labelJLabels.get(idx);
	       jlab.setVisible(stateLabelVisible);
	    }
	    catch (Exception ex)
	    {
	    }
	}
    }

    
    
     
   /**
    * Returns the stateString visiblity
    * @see #setStateStringVisible
    */
    public boolean getStateStringVisible()
    {
       return(stateStringVisible);
    }
        
   /**
    * Sets the visiblity for state strings.
    * @param ssv : if true the string correspondant to each state will be visible in the last column
    * @see #getStateStringVisible
    */
    public void setStateStringVisible(boolean  ssv)
    {
        if (stateStringVisible != ssv)
	{
	   stateStringVisible = ssv;
	   changeStateStringVisibility();
	}
    }

    
    private void changeStateStringVisibility()
    {
	JLabel       jlab = null;

	if (stateJLabels == null) return;
       
 	for (int idx=0; idx < stateJLabels.size(); idx++)
	{
	    try
	    {
	       jlab = stateJLabels.get(idx);
	       jlab.setVisible(stateStringVisible);
	    }
	    catch (Exception ex)
	    {
	    }
	}
    }
    
     
   /**
    * Returns the current toolTipMode
    * @see #setToolTipMode
    */
    public String getToolTipMode()
    {
         return toolTipMode;
    }
    
   /**
    * Sets the current toolTipMode. This property should be set before the call to setModel()
    * @param ttMode : one of the values TOOLTIP_ATTNAME or TOOLTIP_NONE
    * @see #getToolTipMode
    */
    public void setToolTipMode(String  ttMode)
    {
        if (ttMode.equalsIgnoreCase(toolTipMode))
	   return;
	   
	if (ttMode.equalsIgnoreCase(TOOLTIP_ATTNAME))
	   toolTipMode = TOOLTIP_ATTNAME;
	else
	   toolTipMode = TOOLTIP_NONE;
    }
    
    

    public void devStateSpectrumChange(DevStateSpectrumEvent evt)
    {
        String[]    newStates = evt.getValue();
        if (newStates == null)
            setAllStates(IDevice.UNKNOWN);
        else
            updateSpectrumValues(newStates);
    }

    public void stateChange(AttributeStateEvent e)
    {        
    }

    public void errorChange(ErrorEvent evt)
    {
        setAllStates(IDevice.UNKNOWN);
    }

    public void propertyChange(PropertyChangeEvent evt) 
    {
        Property src = (Property) evt.getSource();
        if (model != null)
        {
          if (src.getName().equalsIgnoreCase("label"))
          {
            setNewAttLabel(src.getValue().toString());
          }
        }
    }

    

    private void updateSpectrumValues(String[] newStates)
    {
        JLabel     colLab, stateLab;
        
        for (int idx=0; idx < newStates.length; idx++)
        {
	    if (colorJLabels != null)
	    {
		try
		{
	           colLab = colorJLabels.get(idx);
                   colLab.setBackground(ATKConstant.getColor4State(newStates[idx]));
		}
		catch (Exception ex)
		{
		}
	    }

	    if (stateJLabels != null)
	    {
		try
		{
	           stateLab = stateJLabels.get(idx);
		   stateLab.setText(newStates[idx]);
		}
		catch (Exception ex)
		{
		}
	    }            
        }
    }

    private void setAllStates(String devStateStr)
    {
        JLabel     colLab, stateLab;
        
        for (int idx=0; idx < colorJLabels.size(); idx++)
        {
            try
            {
               colLab = colorJLabels.get(idx);
               colLab.setBackground(ATKConstant.getColor4State(devStateStr, model.getInvertedOpenCloseForElement(idx), model.getInvertedInsertExtractForElement(idx)));
            }
            catch (Exception ex)
            {
            }
        }
        
        for (int idx=0; idx < stateJLabels.size(); idx++)
        {
            try
            {
               stateLab = stateJLabels.get(idx);
               stateLab.setText(devStateStr);
            }
            catch (Exception ex)
            {
            }
        }
    }

    private void setNewAttLabel(String newLabel)
    {
	modelLabel = newLabel;
        if (labelJLabels == null) return;
        for (int idx=0; idx < labelJLabels.size(); idx++)
        {
            JLabel   elemLabel = labelJLabels.get(idx);
            if ((modelStateLabels != null) && (idx < modelStateLabels.length))
                continue;
            else
               elemLabel.setText(modelLabel+"["+idx+"]"); 
        }
    }
    
    

        
    public static void main(String[] args)
    {
       final fr.esrf.tangoatk.core.AttributeList  attList = new fr.esrf.tangoatk.core.AttributeList();
       DevStateSpectrumViewer               dssv = new DevStateSpectrumViewer();
       IDevStateSpectrum                    stateSpectAtt;
       JFrame                               mainFrame;
       

       //dssv.setBackground(Color.white);
       //dssv.setForeground(Color.black);
       //dssv.setGlobalFont(new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 18));
       //dssv.setStateStringVisible(false);
       //dssv.setStateLabelVisible(false);

       // Connect to a devStateSpectrum attribute
       try
       {       
	  stateSpectAtt = (IDevStateSpectrum) attList.add("sy/rf-trawrapper/tra0/SubDevicesStates");
	  dssv.setModel(stateSpectAtt);
	  
       }
       catch (Exception ex)
       {
          System.out.println("caught exception : "+ ex.getMessage());
	  ex.printStackTrace();
	  System.exit(-1);
       }
       
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
				     

       mainFrame.setContentPane(dssv);
       mainFrame.pack();

       mainFrame.setVisible(true);
       
    } // end of main ()
}
