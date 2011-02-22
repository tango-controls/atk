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
 * NumberScalarListViewer.java
 *
 * Created on July 21, 2003, 4:45 PM
 */

/**
 *
 * @author  poncet
 */
package fr.esrf.tangoatk.widget.attribute;
 
import javax.swing.*;
import java.util.Vector;
import java.awt.Color;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.JSmoothLabel;
import fr.esrf.tangoatk.widget.util.JAutoScrolledText;
import fr.esrf.tangoatk.widget.util.JAutoScrolledTextListener;
import fr.esrf.tangoatk.widget.properties.LabelViewer;

public class NumberScalarListViewer extends javax.swing.JPanel
             implements JAutoScrolledTextListener
{
    public static final String      DEFAULT_SETTER = "WheelEditor";
    public static final String      COMBO_SETTER = "ComboEditor";

    protected Vector<IAttribute>    listModel;
    protected Vector<LabelViewer>   nsLabels;
    protected Vector<JComponent>    nsViewers;
    protected Vector<JComponent>    nsSetters;
    protected Vector<JButton>       nsPropButtons;

    protected SimplePropertyFrame   propFrame=null;
        

    /* The bean properties */
    protected java.awt.Font    theFont;
    private boolean          labelVisible;
    private boolean          setterVisible;
    private boolean          propertyButtonVisible;
    private boolean          propertyListEditable;
    private boolean          unitVisible;
    private String           setterType;
    private Color            arrowColor;
    

    /** Creates new form NumberScalarListViewer */
    public NumberScalarListViewer()
    {
        listModel = null;
	nsLabels = null;
	nsViewers = null;
	nsSetters = null;
	nsPropButtons = null;
	arrowColor = null;
	propFrame = new SimplePropertyFrame();
	
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.BOLD, 22);
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.BOLD, 20);
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.BOLD, 14);
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 14);
        theFont = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 14);
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 10);
	labelVisible = true;
	setterVisible = true;
	propertyButtonVisible = true;
	propertyListEditable = true;
	unitVisible = true;
	setterType = DEFAULT_SETTER;
        setLayout(new java.awt.GridBagLayout());
	
	setVisible(false);
    }
    
    
    public void setModel(fr.esrf.tangoatk.core.AttributeList scalarList)
    {
	int                          nbAtts, idx;
	boolean                      containsNumberScalar;
	Object                       elem;
	

        if (listModel != null)
        {
            removeComponents();
            listModel = null;
        }
               
        if (scalarList == null)
	{
	   return;
	}
	   
	nbAtts = scalarList.getSize();
	
	if (nbAtts <= 0)
	   return;
	   
	containsNumberScalar = false;
	
	for (idx=0; idx < nbAtts; idx++)
	{
	   elem = scalarList.getElementAt(idx);
	   if (elem instanceof INumberScalar)
	   {
	      containsNumberScalar = true;
	      break;
	   }
	}
	
	if (containsNumberScalar == false)
	  return;

	initComponents(scalarList);
	
	setVisible(true);
	
    }
    
    
    protected void removeComponents()
    {
       int                             indRow, nbRows;
       IAttribute                      iatt = null;
       INumberScalar                   ins = null;
       LabelViewer                     nsLabel=null;
       JComponent                      jcomp = null;
       SimpleScalarViewer              viewer=null;
       NumberScalarWheelEditor         setter=null;
       NumberScalarComboEditor         comboSetter=null;


       propFrame = null;
       propFrame = new SimplePropertyFrame();
       
       nbRows = listModel.size();
       for (indRow=0; indRow < nbRows; indRow++)
       {
	  try
	  {
	     iatt = listModel.get(indRow);
	     if (iatt instanceof INumberScalar)
	     {
		ins = (INumberScalar) iatt;
		
	        // remove this model from all viewers
	        nsLabel = nsLabels.get(indRow);
		nsLabel.setModel(null);
		
		jcomp = nsViewers.get(indRow);
		if (jcomp instanceof SimpleScalarViewer)
		{
		   viewer = (SimpleScalarViewer) jcomp;
		   viewer.clearModel();
		}
		
		jcomp = nsSetters.get(indRow);
		if (jcomp instanceof NumberScalarWheelEditor)
		{
		   setter = (NumberScalarWheelEditor) jcomp;
	           if (ins.isWritable())
		      setter.setModel(null);
		}
		else
		   if (jcomp instanceof NumberScalarComboEditor)
		   {
		      comboSetter = (NumberScalarComboEditor) jcomp;
	              if (ins.isWritable())
			 comboSetter.setNumberModel(null);
		   }
	     }
	  }
	  catch (Exception e)
	  {
	    System.out.println("NumberScalarListViewer : setTheFont : Caught exception  "+e.getMessage());
	  }
       }
       
       nsLabels.removeAllElements();
       nsViewers.removeAllElements();
       nsSetters.removeAllElements();
       nsPropButtons.removeAllElements();
       listModel.removeAllElements();
       this.removeAll();
       listModel = null;
       nsLabels = null;
       nsViewers = null;
       nsSetters = null;
       nsPropButtons = null;
    }
    


    public java.awt.Font getTheFont()
    {
       return(theFont);
    }
    

    public void setTheFont(java.awt.Font  ft)
    {
       int                             indRow, nbRows;
       LabelViewer                     nsLabel=null;
       JButton                         propertyButton=null;
       JComponent                      viewer = null;
       JComponent                      setter = null;


       if (ft != null)
       {
	  
	  theFont = ft;
	  
          if (listModel != null)
	  {
	     nbRows = listModel.size();
	     for (indRow=0; indRow<nbRows; indRow++)
	     {
		try
		{
	           nsLabel = nsLabels.get(indRow);
		   nsLabel.setFont(theFont);
		   
	           viewer = nsViewers.get(indRow);
		   if (viewer != null)
		   {
		      if (viewer instanceof SimpleScalarViewer)
		      {
			  viewer.setFont(theFont);
		      }
		   }

	           setter = nsSetters.get(indRow);
		   if (setter != null)
		   {
		      if (   (setter instanceof NumberScalarWheelEditor)
			  || (setter instanceof NumberScalarComboEditor) )
		      {
			  setter.setFont(theFont);
		      }
		   }
		   
	           propertyButton = nsPropButtons.get(indRow);
		   propertyButton.setFont(theFont);
		}
		catch (Exception e)
		{
		  System.out.println("NumberScalarListViewer : setTheFont : Caught exception  "+e.getMessage());
		}
	     }
	     
	  } // if listModel != null
	  
       } // if ft != null

    }
    

    
    public boolean getLabelVisible()
    {
       return(labelVisible);
    }
    
    public void setLabelVisible(boolean  lv)
    {
        if (labelVisible != lv)
	{
	   labelVisible = lv;
	   changeLabelVisibility();
	}
    }
    
    private void changeLabelVisibility()
    {
       int                             indRow, nbRows;
       LabelViewer                     nsLabel=null;


       if (nsLabels != null)
       {
	  nbRows = nsLabels.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        nsLabel = nsLabels.get(indRow);
		nsLabel.setVisible(labelVisible);
	     }
	     catch (Exception e)
	     {
	       System.out.println("NumberScalarListViewer : changeLabelVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if nsLabels != null

    }

    

    public boolean getSetterVisible()
    {
       return(setterVisible);
    }
    

    public void setSetterVisible(boolean  sv)
    {
        if (setterVisible != sv)
	{
	   setterVisible = sv;
	   changeSetterVisibility();
	}
    }

    
    private void changeSetterVisibility()
    {
       int                             indRow, nbRows;
       JComponent                      setter = null;


       if (nsSetters != null)
       {
	  nbRows = nsSetters.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        setter = nsSetters.get(indRow);
		if (setter != null)
		   if (   (setter instanceof NumberScalarWheelEditor)
		       || (setter instanceof NumberScalarComboEditor) )
		   {
		       setter.setVisible(setterVisible);
		   }
	     }
	     catch (Exception e)
	     {
	       System.out.println("NumberScalarListViewer : changeSetterVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if scalarSetters != null

    }
    
    

    public boolean getPropertyButtonVisible()
    {
       return(propertyButtonVisible);
    }
    
    public void setPropertyButtonVisible(boolean  pv)
    {
        if (propertyButtonVisible != pv)
	{
	   propertyButtonVisible = pv;
	   changePropButtonVisibility();
	}
    }
    
    private void changePropButtonVisibility()
    {
       int                             indRow, nbRows;
       JButton                         propertyButton=null;


       if (nsPropButtons != null)
       {
	  nbRows = nsPropButtons.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
		propertyButton = nsPropButtons.get(indRow);
		propertyButton.setVisible(propertyButtonVisible);
	     }
	     catch (Exception e)
	     {
	       System.out.println("NumberScalarListViewer : changePropButtonVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if nsPropButtons != null

    }
    
    

    public boolean getPropertyListEditable()
    {
       return(propertyListEditable);
    }
    
    public void setPropertyListEditable(boolean  pv)
    {
        /*if (propertyListEditable != pv)
	{
	   propertyListEditable = pv;
	   changePropertyListEditable();
	}*/
    }
    
    

    public boolean getUnitVisible()
    {
       return(unitVisible);
    }
    
    public void setUnitVisible(boolean  uv)
    {
        if (unitVisible != uv)
	{
	   unitVisible = uv;
	   changeUnitVisibility();
	}
    }

    
    private void changeUnitVisibility()
    {
       int                             indRow, nbRows;
       JComponent                      jcomp = null;
       SimpleScalarViewer              viewer=null;
       NumberScalarComboEditor         setter=null;


       if (nsViewers != null)
       {
	  nbRows = nsViewers.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
		jcomp = nsViewers.get(indRow);
		if (jcomp instanceof SimpleScalarViewer)
		{
		   viewer = (SimpleScalarViewer) jcomp;
		   viewer.setUnitVisible(unitVisible);
		}
		jcomp = nsSetters.get(indRow);
		if (jcomp != null)
		   if (jcomp instanceof NumberScalarComboEditor)
		   {
		      setter = (NumberScalarComboEditor) jcomp;
		      setter.setUnitVisible(unitVisible);
		   }
		
	     }
	     catch (Exception e)
	     {
	       System.out.println("NumberScalarListViewer : changeUnitVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if nsSetters != null

    }
     

     
    /**
     * @deprecated As of ATKWidget-2.5.8 and higher
     * The method getSetterType should not be used.
     * The setterType for each NumberScalar attribute is selected automatically by
     * the NumberScalarListViewer.
     */
    public String getSetterType()
    {
         return setterType;
    }
    
    
    /**
     * @deprecated As of ATKWidget-2.5.8 and higher this method has no effect.
     * The setterType for each NumberScalar attribute is selected automatically by
     * the NumberScalarListViewer.
     */
    public void setSetterType(String  setType)
    {
        if (listModel != null)
	   return;
	   
        if (setType.equalsIgnoreCase(DEFAULT_SETTER))
	   setterType = DEFAULT_SETTER;
	else
	   if (setType.equalsIgnoreCase(COMBO_SETTER))
	       setterType = COMBO_SETTER;
	   else
	       setterType = DEFAULT_SETTER;
    }




/**
 * Returns the current arrowButton colour for the WheelEditor used as number setter
 * @see #setArrowColor
 */
   public Color getArrowColor()
   {
     if (arrowColor == null)
        return (getBackground());
     else
        return(arrowColor);
   }


/**
 * Sets the current arrowButton colour for the WheelEditor used as number setter
 * @param java.awt.Color  ac
 */
   public void setArrowColor( Color  ac)
   {
        if (ac == arrowColor)
	   return;
	
	changeArrowColors(ac);
	
	arrowColor = ac;   
   }


    
    private void changeArrowColors(Color  ac)
    {
       int                             indRow, nbRows;
       JComponent                      jcomp = null;
       NumberScalarWheelEditor         setter=null;


       if (nsSetters != null)
       {
	  nbRows = nsSetters.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        jcomp = nsSetters.get(indRow);
		if (jcomp instanceof NumberScalarWheelEditor)
		{
		   setter = (NumberScalarWheelEditor) jcomp;
		   if (ac == null)
		      setter.setButtonColor(setter.getBackground());
		   else
		      setter.setButtonColor(ac);
		}
	     }
	     catch (Exception e)
	     {
	       System.out.println("NumberScalarListViewer : changeArrowColors : Caught exception  "+e.getMessage());
	     }
	  }
       } // if nsSetters != null
    }

    
    /* Method for JAutoScrolledTextListener interface */
    public void textExceedBounds(JAutoScrolledText source)
    {
       this.revalidate();
    }
   
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    protected void initComponents(fr.esrf.tangoatk.core.AttributeList scalarList)
    {
	int                             nbAtts, idx, viewerRow;
	boolean                         containsNumberScalar;
	Object                          elem;
	INumberScalar                   ins;
        java.awt.GridBagConstraints     gridBagConstraints;
	
	LabelViewer                     nsLabel=null;
	SimpleScalarViewer              viewer=null;
	NumberScalarWheelEditor         wheelSetter=null;
	NumberScalarComboEditor         comboSetter=null;
	JComponent                      setter=null;
	JButton                         propertyButton=null;
	
	int                             arrowHeight=0;
	boolean                         insHasValueList;


	listModel = new Vector<IAttribute> ();
	nsLabels = new Vector<LabelViewer> ();
	nsViewers = new Vector<JComponent> ();
	nsSetters = new Vector<JComponent> ();
	nsPropButtons = new Vector<JButton> ();
	
	
	viewerRow = 0;
	nbAtts = scalarList.size();
	
	for (idx=0; idx < nbAtts; idx++)
	{
	   elem = scalarList.getElementAt(idx);
	   if (elem instanceof INumberScalar)
	   {
	      ins = (INumberScalar) elem;
	      insHasValueList = false;
	      if (ins.getPossibleValues() != null)
		 if (ins.getPossibleValues().length > 0)
		     insHasValueList = true;
	      
              nsLabel = new LabelViewer();
              viewer = new SimpleScalarViewer();
              propertyButton = new javax.swing.JButton();

	      nsLabel.setFont(theFont);
	      nsLabel.setHorizontalAlignment(JSmoothLabel.RIGHT_ALIGNMENT);
	      nsLabel.setBackground(getBackground());
	      //nsLabel.setValueOffsets(0, -5);
	      nsLabel.setText(ins.getLabel());
	      if (labelVisible)
		 nsLabel.setVisible(true);
	      else
		 nsLabel.setVisible(false);
	      nsLabel.setModel(ins);
		      
	      if (insHasValueList)
	      {
        	   comboSetter = new NumberScalarComboEditor();
        	   comboSetter.setFont(theFont);
        	   comboSetter.setBackground(getBackground());
	           comboSetter.setUnitVisible(unitVisible);
		   if (ins.isWritable())
		   {
		      comboSetter.setNumberModel(ins);
		      if (setterVisible)
        		 comboSetter.setVisible(true);
		      else
			 comboSetter.setVisible(false);
		   }
		   else
		      comboSetter.setVisible(false);
	           nsSetters.add(comboSetter);
		   setter = comboSetter;
	      }
	      else // NumberScalar has no possibleValues list
	      {
        	   wheelSetter = new NumberScalarWheelEditor();
        	   wheelSetter.setFont(theFont);
        	   wheelSetter.setBackground(getBackground());
		   if (ins.isWritable())
		   {
		      wheelSetter.setModel(ins);
		      if (setterVisible)
        		 wheelSetter.setVisible(true);
		      else
			 wheelSetter.setVisible(false);
		   }
		   else
		      wheelSetter.setVisible(false);
	           nsSetters.add(wheelSetter);
		   setter = wheelSetter;
	      }
	 

              viewer.setFont(theFont);
	      viewer.setUnitVisible(unitVisible);
              viewer.setBackgroundColor(getBackground());
              viewer.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
	      viewer.setAlarmEnabled(true);
	      //viewer.setValueOffsets(0, -5);
	      viewer.setModel(ins);
	      

              propertyButton.setFont(theFont);
              propertyButton.setBackground(getBackground());
              propertyButton.setText(" ... ");
              propertyButton.setMargin(new java.awt.Insets(-3, 0, 3, 0));
              propertyButton.setToolTipText("Attribute Properties");	      
	      if (!propertyButtonVisible)
		 if (propertyButton != null)
		    propertyButton.setVisible(false);
	      propertyButton.addActionListener(
	         new java.awt.event.ActionListener() 
		       {
	                  public void actionPerformed(java.awt.event.ActionEvent evt)
			  {
	                     propertyButtonActionPerformed(evt);
	                  }
	               });
		    
	      ins.refresh(); // to enable the viewers to be correctly sized!
	      
	      viewer.addTextListener(this);

              // Increase the height of viewers to the height of setters
	      arrowHeight = (setter.getPreferredSize().height - viewer.getPreferredSize().height)/2;
	      if (arrowHeight > 0)
	         viewer.setMargin(
		    new java.awt.Insets(arrowHeight+2, 5, arrowHeight+2, 5));

	      
	      // Add all these viewers to the panel	      
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridx = 0;
              gridBagConstraints.gridy = viewerRow;
              gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
              gridBagConstraints.insets = new java.awt.Insets(1,5,1,1);
              add(nsLabel, gridBagConstraints);
	      
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridx = 1;
              gridBagConstraints.gridy = viewerRow;
              gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
              gridBagConstraints.insets = new java.awt.Insets(1,2,1,1);
              add(viewer, gridBagConstraints);
	      
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridx = 2;
              gridBagConstraints.gridy = viewerRow;
              gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
              gridBagConstraints.insets = new java.awt.Insets(1,1,1,1);
              add(setter, gridBagConstraints);
	      
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridx = 3;
              gridBagConstraints.gridy = viewerRow;
              gridBagConstraints.insets = new java.awt.Insets(1,3,1,5);
              add(propertyButton, gridBagConstraints);
	      
	      // Add to the vectors
	      // Setter has already been added to nsSetters above
	      listModel.add(ins);
	      nsLabels.add(nsLabel);
	      nsViewers.add(viewer);
	      nsPropButtons.add(propertyButton);
	      
	      viewerRow++;
	   }
	}
    }
    
    
    private void propertyButtonActionPerformed (java.awt.event.ActionEvent evt)
    {
        int              buttonIndex=-1;
	int              ind, nbButtons;
	IAttribute       iatt;
	JButton          propertyButton;
	INumberScalar    ins;
	
	
	if (nsPropButtons == null)
	   return;
	
	if (listModel == null)
	   return;
	   
	nbButtons = nsPropButtons.size();
	
	// Look for the button in the vector
	for (ind=0; ind<nbButtons; ind++)
	{
	   try
	   {
	      propertyButton = nsPropButtons.get(ind);
	      if (propertyButton.equals(evt.getSource()))
	      {
		 buttonIndex = ind;
		 break;
	      }
	   }
	   catch (Exception e)
	   {
	     System.out.println("NumberScalarListViewer : propertyButtonActionPerformed : Caught exception  "+e.getMessage());
	     return;
	   }
	}
	
	if (buttonIndex < 0)
	   return;
	
	// find the INumberScalar corresponding to the button
	
	ins = null;
	
	try
	{
	   iatt = listModel.get(buttonIndex);
	   if (iatt instanceof INumberScalar)
	      ins = (INumberScalar) iatt;
	}
	catch (Exception e)
	{
	}
	
	if (ins == null)
	   return;
	

	if (propFrame != null)
	{
	   propFrame.setModel(ins);
	   propFrame.setVisible(true);
	}
    }


    
    public static void main(String[] args)
    {
       final fr.esrf.tangoatk.core.AttributeList  attList = new fr.esrf.tangoatk.core.AttributeList();
       NumberScalarListViewer               nslv = new NumberScalarListViewer();
       INumberScalar                        att;
       JFrame                               mainFrame;
       double[]                               vals = {0.1, 0.3, 1.0, 3.0, 10.0, 30.0, 100.0, 300.0};
       

       //nslv.setBackground(Color.white);
       //nslv.setForeground(Color.black);

       // Connect to a list of number scalar attributes
       try
       {
          att = (INumberScalar) attList.add("jlp/test/1/att_un");
          att = (INumberScalar) attList.add("jlp/test/1/att_deux");
          att = (INumberScalar) attList.add("jlp/test/1/att_trois");
          att = (INumberScalar) attList.add("jlp/test/1/att_quatre");
	  //nslv.setTheFont(new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 18));
          //nslv.setLabelVisible(false);
          //nslv.setSetterVisible(false);
          //nslv.setPropertyButtonVisible(false);
          //att = (INumberScalar) attList.add("sr/d-tm/ntm/BandWidth");
	  //att.setPossibleValues(vals);
	  nslv.setModel(attList);
       }
       catch (Exception ex)
       {
          System.out.println("caught exception : "+ ex.getMessage());
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
				     

       mainFrame.setContentPane(nslv);
       mainFrame.pack();

       mainFrame.setVisible(true);
       
    } // end of main ()
        
}
