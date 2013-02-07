/*
 *  Copyright (C) :	2013,
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
 * BooleanSpectrumViewer.java
 *
 * Created on Jan 31, 2013, 18:20
 */

/**
 *
 * @author  poncet
 */
package fr.esrf.tangoatk.widget.attribute;
 
import java.beans.PropertyChangeEvent;
import java.util.Vector;

import java.beans.PropertyChangeListener;

import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JScrollPane;


public class BooleanSpectrumViewer extends javax.swing.JPanel
                                    implements IBooleanSpectrumListener, PropertyChangeListener
{    
    public static final int      TOOLTIP_NONE = 0;
    public static final int      TOOLTIP_ATTNAME = 1;
    public static final int      TOOLTIP_VALUE = 2;
    public static final int      TOOLTIP_ATTNAME_VALUE = 3;
    
    public static final int      DISPLAY_ICON = 0;
    public static final int      DISPLAY_TEXT = 1;
    public static final int      DISPLAY_ICON_TEXT = 2;
    
    private static ImageIcon           TRUE_ICON = null;
    private static ImageIcon           FALSE_ICON = null;
    private static ImageIcon           ERROR_ICON = null;
    
    private static final String        TRUE_LABEL = "True";
    private static final String        FALSE_LABEL = "False";
    private static final String        ERROR_LABEL = "Unknown";
    
    
    
    private Vector<JLabel>        labelJLabels;
    private Vector<JLabel>        valueJLabels;
    
    /* The bean properties */
    private Font                globalFont;
    private boolean             booleanLabelVisible;
    private int                 displayMode;
    private int                 toolTipMode;
    
    private IBooleanSpectrum    model;
    String[]                    modelBooleanLabels;
    String                      modelLabel;
    
    
    /** Creates new form DevStateSpectrumViewer */
    public BooleanSpectrumViewer()
    {
        if (TRUE_ICON == null)
            TRUE_ICON = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/icons/true.png"));
        if (FALSE_ICON == null)
            FALSE_ICON = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/icons/false.png"));
        if (ERROR_ICON == null)
            ERROR_ICON = new ImageIcon(getClass().getResource("/fr/esrf/tangoatk/widget/icons/unknown.png"));
        
        model = null;
	modelBooleanLabels = null;
	modelLabel = null;
	globalFont = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12);
	booleanLabelVisible = true;
	displayMode = 2;
	toolTipMode = TOOLTIP_ATTNAME;
        setLayout(new java.awt.GridBagLayout());
	
	setVisible(false);
    }
    
    
    
    
   /**
    * Returns the model for this viewer
    * @see #setModel
    */
    public IBooleanSpectrum getModel()
    {
       return model;
    }

   /**
    * Sets the model for this viewer. The model necessarily implements IBooleanSpectrum interface
    * @param boolSpec : the IBooleanSpectrum attribute to use as model
    * @see #getModel
    */
    public void setModel(IBooleanSpectrum boolSpec)
    {
	clearModel();
	if (boolSpec == null) return;
	
	model = boolSpec;
        if (!model.areAttPropertiesLoaded())
           model.loadAttProperties();
        
	modelBooleanLabels = model.getBooleanLabels();
	modelLabel = model.getLabel();
	
	initComponents();
        setVisible(true);
	
	model.getProperty("label").addPresentationListener(this);        
	model.addBooleanSpectrumListener(this);
    }
    
    
    public void clearModel()
    {
        if (model != null)
	{
	   model.removeBooleanSpectrumListener(this);
           model.getProperty("label").removePresentationListener(this);
	   removeComponents();
	   model = null;
	   modelBooleanLabels = null;
	   modelLabel = null;
	}
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
    * Sets the globalFont for this viewer. The globalFont is then applied to all booleanLabels and all booleanValues
    * @param ft : the font to use for globalFont
    * @see #getGlobalFont
    */
    public void setGlobalFont(Font  ft)
    {
        JLabel             jlab=null;

	if (ft == null) return;

	globalFont = ft;

 	if (labelJLabels != null)
        {
            for (int idx=0; idx < labelJLabels.size(); idx++)
            {
                jlab = labelJLabels.get(idx);
                jlab.setFont(globalFont);
            }
        }

 	if (valueJLabels != null)
        {
            for (int idx=0; idx < valueJLabels.size(); idx++)
            {
                jlab = valueJLabels.get(idx);
                jlab.setFont(globalFont);
            }
        }
    }
    

    
   /**
    * Returns the booleanLabel visiblity
    * @see #setBooleanLabelVisible
    */
    public boolean getBooleanLabelVisible()
    {
       return(booleanLabelVisible);
    }
    
   /**
    * Sets the visiblity for boolean labels.
    * @param blv : if true the labels associated with each element of the spectrum will be visible in the first column
    * @see #getStateLabelVisible
    */
    public void setBooleanLabelVisible(boolean  blv)
    {
        if (booleanLabelVisible != blv)
	{
	   booleanLabelVisible = blv;
	   changeBooleanLabelVisibility();
	}
    }

      
     
   /**
    * Returns the current displayMode
    * @see #setDisplayMode
    */
    public int getDisplayMode()
    {
         return displayMode;
    }
    
   /**
    * Sets the current displayMode. This property should be set before the call to setModel()
    * @param dispMode : one of the values : DISPLAY_ICON, DISPLAY_TEXT, DISPLAY_ICON_TEXT
    * @see #getDisplayMode
    */
    public void setDisplayMode(int  dispMode)
    {
        if (dispMode == displayMode)
	   return;
        
        displayMode = dispMode;
    }

    
     
   /**
    * Returns the current toolTipMode
    * @see #setToolTipMode
    */
    public int getToolTipMode()
    {
         return toolTipMode;
    }
    
   /**
    * Sets the current toolTipMode. This property should be set before the call to setModel()
    * @param ttMode : one of the values : TOOLTIP_NONE, TOOLTIP_ATTNAME, TOOLTIP_VALUE, TOOLTIP_ATTNAME_VALUE
    * @see #getToolTipMode
    */
    public void setToolTipMode(int  ttMode)
    {
        if (ttMode ==toolTipMode)
	   return;
        
        toolTipMode = ttMode;
    }
    
    
    
    
    private void removeComponents()
    {
       labelJLabels.removeAllElements();
       valueJLabels.removeAllElements();
       this.removeAll();
       labelJLabels = null;
       valueJLabels = null;
    }

    
    /** This method is called from the setModel() method to create all the visual
     * components necessary to display a spectrum of DevState.
     **/
    private void initComponents()
    {
        boolean[]                booleanValueElements;
        GridBagConstraints       gbc;
	JLabel                   elemLabel, elemValue;
	String                   ttip;
	
        this.setLayout(new java.awt.GridBagLayout());
        
        booleanValueElements = model.getDeviceValue();
	if (booleanValueElements.length <= 0) return;
	
	labelJLabels = new Vector<JLabel> ();
	valueJLabels = new Vector<JLabel> ();
	
	gbc = new GridBagConstraints();
	
	for (int idx=0; idx < booleanValueElements.length; idx++)
	{
	    elemLabel = new JLabel();
	    elemValue = new JLabel();
	    
	    ttip = null;
	    if (toolTipMode == TOOLTIP_ATTNAME)
            {
	       ttip = model.getName()+"["+idx+"]";
            }
            else
            {
                if (toolTipMode == TOOLTIP_VALUE)
                {
                    ttip = Boolean.toString(booleanValueElements[idx]);
                }
                else
                {
                    if (toolTipMode == TOOLTIP_ATTNAME_VALUE)
                    {
                        ttip = model.getName()+ "["+idx+"] = " + Boolean.toString(booleanValueElements[idx]);
                    }
                }                
            }
	       
	    elemLabel.setFont(globalFont);
            elemLabel.setBackground(getBackground());
	    elemLabel.setToolTipText(ttip);	    	    	    
	    if ((modelBooleanLabels != null) && (idx < modelBooleanLabels.length))
	       elemLabel.setText(modelBooleanLabels[idx]);
	    else
	       elemLabel.setText(modelLabel+"["+idx+"]");
	    
	    elemValue.setFont(globalFont);
	    elemValue.setToolTipText(ttip);
            displayBooleanValue(elemValue, booleanValueElements[idx]);
	       	    
	    labelJLabels.add(elemLabel);
	    valueJLabels.add(elemValue);
	    
	    elemLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            gbc.gridx = 0;
	    gbc.gridy = idx;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.EAST;
	    gbc.insets = new java.awt.Insets(6, 6, 6, 6);
	    add(elemLabel, gbc);
	    
	    gbc.gridx = 1;
	    gbc.gridy = idx;
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.insets = new java.awt.Insets(6, 6, 6, 6);
	    add(elemValue, gbc);
	    
            elemLabel.setVisible(booleanLabelVisible);
	}	
    }

        
    private void changeBooleanLabelVisibility()
    {
	JLabel       jlab = null;

	if (labelJLabels == null) return;
       
 	for (int idx=0; idx < labelJLabels.size(); idx++)
	{
	    try
	    {
	       jlab = labelJLabels.get(idx);
	       jlab.setVisible(booleanLabelVisible);
	    }
	    catch (Exception ex)
	    {
	    }
	}
    }
    

    private void updateSpectrumValues(boolean[] newBooleans)
    {
        if (valueJLabels == null) return;
        
        for (int idx=0; idx < newBooleans.length; idx++)
        {
	    if (idx >= valueJLabels.size()) break;
            displayBooleanValue(valueJLabels.get(idx), newBooleans[idx]);
            updateValueInTooltip(idx, newBooleans[idx]);
        }
    }
    
    private void displayBooleanValue(JLabel valueLabel, boolean value)
    {
        if (displayMode == DISPLAY_ICON_TEXT)
        {
            if (value)
            {
                valueLabel.setIcon(TRUE_ICON);
                valueLabel.setText(TRUE_LABEL);
            }
            else
            {
                valueLabel.setIcon(FALSE_ICON);
                valueLabel.setText(FALSE_LABEL);
            }
            return;
        }

        if (displayMode == DISPLAY_ICON)
        {
            valueLabel.setText("");
            if (value)
                valueLabel.setIcon(TRUE_ICON);
                
            else
                valueLabel.setIcon(FALSE_ICON);
            return;
        }

        if (displayMode == DISPLAY_TEXT)
        {
            valueLabel.setIcon(null);
            if (value)
                valueLabel.setText(TRUE_LABEL);
            else
                valueLabel.setText(FALSE_LABEL);
            return;
        }
    }
    
    
    
    private void updateValueInTooltip(int index, boolean val)
    {
        if (toolTipMode == TOOLTIP_ATTNAME) return;
        if (toolTipMode == TOOLTIP_NONE) return;
        
        String ttip;
        if (toolTipMode == TOOLTIP_VALUE)
            ttip = Boolean.toString(val);
        else
            ttip = model.getName()+ "["+index+"] = " + Boolean.toString(val);

        if (labelJLabels != null)
        {
            if (index < labelJLabels.size())
            {
                labelJLabels.get(index).setToolTipText(ttip);
            }
        }

        if (valueJLabels != null)
        {
            if (index < valueJLabels.size())
            {
                valueJLabels.get(index).setToolTipText(ttip);
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
            if ((modelBooleanLabels != null) && (idx < modelBooleanLabels.length))
                continue;
            else
               elemLabel.setText(modelLabel+"["+idx+"]"); 
        }
    }
    
    
    
    private void displayErrorAllBooleans()
    {
        if (valueJLabels == null) return;
        
        for (int idx=0; idx < valueJLabels.size(); idx++)
        {
            displayErrorValue(valueJLabels.get(idx));
            updateTooltipWithError(idx);
        }        
    }
    
    private void displayErrorValue(JLabel valueLabel)
    {
        if (displayMode == DISPLAY_ICON_TEXT)
        {
            valueLabel.setIcon(ERROR_ICON);
            valueLabel.setText(ERROR_LABEL);
            return;
        }

        if (displayMode == DISPLAY_ICON)
        {
            valueLabel.setText("");
            valueLabel.setIcon(ERROR_ICON);
            return;
        }

        if (displayMode == DISPLAY_TEXT)
        {
            valueLabel.setIcon(null);
            valueLabel.setText(ERROR_LABEL);
        }
    }
    
    private void updateTooltipWithError(int index)
    {
        if (toolTipMode == TOOLTIP_ATTNAME) return;
        if (toolTipMode == TOOLTIP_NONE) return;
        
        String ttip;
        if (toolTipMode == TOOLTIP_VALUE)
            ttip = ERROR_LABEL;
        else
            ttip = model.getName()+ "["+index+"] = " + ERROR_LABEL;

        if (labelJLabels != null)
        {
            if (index < labelJLabels.size())
            {
                labelJLabels.get(index).setToolTipText(ttip);
            }
        }

        if (valueJLabels != null)
        {
            if (index < valueJLabels.size())
            {
                valueJLabels.get(index).setToolTipText(ttip);
            }
        }
    }
    
    
    

    public void booleanSpectrumChange(BooleanSpectrumEvent e)
    {
        if (e.getValue().length < 1)
        {
            displayErrorAllBooleans();
            return;
        }
        updateSpectrumValues(e.getValue());
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
    

    public void stateChange(AttributeStateEvent e)
    {        
    }

    public void errorChange(ErrorEvent evt)
    {
        displayErrorAllBooleans();
    }
    

        
    public static void main(String[] args)
    {
       final fr.esrf.tangoatk.core.AttributeList  attList = new fr.esrf.tangoatk.core.AttributeList();
       BooleanSpectrumViewer               bsv = new BooleanSpectrumViewer();
       IBooleanSpectrum                    booleanSpectAtt;
       JFrame                               mainFrame;
       

//       bsv.setBackground(Color.white);
//       bsv.setForeground(Color.black);
//       bsv.setGlobalFont(new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 18));
//       bsv.setdisplayMode(DISPLAY_ICON_TEXT);
//       bsv.setdisplayMode(DISPLAY_ICON);
//       bsv.setdisplayMode(DISPLAY_TEXT);
//       bsv.setToolTipMode(TOOLTIP_NONE);
//       bsv.setToolTipMode(TOOLTIP_VALUE);
//       bsv.setToolTipMode(TOOLTIP_ATTNAME);

//       bsv.setBooleanLabelVisible(false);
       
       
       // Connect to a BooleanSpectrum attribute
       try
       {       
	  booleanSpectAtt = (IBooleanSpectrum) attList.add("dev/test/10/Boolean_spec_attr");
	  bsv.setModel(booleanSpectAtt);
	  
       }
       catch (Exception ex)
       {
          System.out.println("caught exception : "+ ex.getMessage());
	  ex.printStackTrace();
	  System.exit(-1);
       }
       
       
       mainFrame = new JFrame();
       mainFrame.setPreferredSize(new Dimension(500, 800));
//       
//       mainFrame.addWindowListener(
//	       new java.awt.event.WindowAdapter()
//			  {
//			      public void windowActivated(java.awt.event.WindowEvent evt)
//			      {
//				 // To be sure that the refresher (an independente thread)
//				 // will begin when the the layout manager has finished
//				 // to size and position all the components of the window
//				 
//			      }
//			  }
//                                     );
				     
       JScrollPane  jsp = new JScrollPane();
       jsp.setViewportView(bsv);
       mainFrame.setContentPane(jsp);
       mainFrame.pack();
       attList.startRefresher();
       
       mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       mainFrame.pack();

       mainFrame.setVisible(true);
       mainFrame.repaint();
       
    } // end of main ()

}
