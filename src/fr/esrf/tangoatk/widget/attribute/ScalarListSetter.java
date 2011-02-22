/*
 * ScalarListSetter.java
 *
 * Created on January 28, 2005, 4:10 PM
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
import fr.esrf.tangoatk.core.attribute.AAttribute;
import fr.esrf.tangoatk.widget.attribute.NumberScalarWheelEditor;
import fr.esrf.tangoatk.widget.attribute.StringScalarEditor;
import fr.esrf.tangoatk.widget.attribute.SimpleScalarViewer;
import fr.esrf.tangoatk.widget.util.JSmoothLabel;
import fr.esrf.tangoatk.widget.util.JAutoScrolledText;
import fr.esrf.tangoatk.widget.util.JAutoScrolledTextListener;
import fr.esrf.tangoatk.widget.properties.LabelViewer;
import fr.esrf.tangoatk.widget.attribute.SimplePropertyFrame;

public class ScalarListSetter extends javax.swing.JPanel
             implements JAutoScrolledTextListener
{
    public static final String      NUMBER_DEFAULT_SETTER = "WheelEditor";
    public static final String      NUMBER_COMBO_SETTER = "ComboEditor";
    public static final String      STRING_DEFAULT_SETTER = "StringScalarEditor";
    public static final String      STRING_COMBO_SETTER = "StringComboEditor";


    private Vector                listModel;
    private Vector                scalarLabels, scalarViewers, scalarSetters, scalarPropButtons;
    private SimplePropertyFrame   propFrame=null;
        

    /* The bean properties */
    private java.awt.Font    theFont;
    private boolean          labelVisible;
    private boolean          viewerVisible;
    private boolean          propertyButtonVisible;
    private boolean          propertyListEditable;
    private boolean          unitVisible;
    private String           numberSetterType;
    private String           stringSetterType;
    private Color            arrowColor;
    

    /** Creates new form ScalarListSetter */
    public ScalarListSetter()
    {
        listModel = null;
	scalarLabels = null;
	scalarViewers = null;
	scalarSetters = null;
	scalarPropButtons = null;
	arrowColor = null;
	propFrame = new SimplePropertyFrame();
	
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.BOLD, 22);
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.BOLD, 20);
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.BOLD, 14);
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 14);
        theFont = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 14);
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 10);
	labelVisible = true;
	viewerVisible = true;
	propertyButtonVisible = true;
	propertyListEditable = true;
	unitVisible = true;
	numberSetterType = NUMBER_DEFAULT_SETTER;
	stringSetterType = STRING_DEFAULT_SETTER;
        setLayout(new java.awt.GridBagLayout());
	
	setVisible(false);
    }
    
    
    public void setModel(fr.esrf.tangoatk.core.AttributeList scalarList)
    {
	int                          nbAtts, idx;
	boolean                      containsWritableScalarAtt;
	Object                       elem;
	
	if (scalarList == null)
	{
	   if (listModel != null)
	   {
	      removeComponents();
	   }
	   return;
	}
	   
	if (listModel != null) // Not yet implemented
	   return;
	   
	nbAtts = scalarList.getSize();
	
	if (nbAtts <= 0)
	   return;
	   
	containsWritableScalarAtt = false;
	
	for (idx=0; idx < nbAtts; idx++)
	{
	   elem = scalarList.getElementAt(idx);
	   if (     (elem instanceof INumberScalar)
	        ||  (elem instanceof IStringScalar) )
	   {
	      IScalarAttribute   isa = (IScalarAttribute) elem;
	      if (isa.isWritable())
	      {
		 containsWritableScalarAtt = true;
		 break;
	      }
	   }
	}
	
	if (containsWritableScalarAtt == false)
	  return;
	
	initComponents(scalarList);
	
	setVisible(true);
	
    }
    
    
    
    
    private void removeComponents()
    {
       int                             indRow, nbRows;
       Object                          elem = null;
       INumberScalar                   ins;
       IStringScalar                   iss;
       LabelViewer                     scalarLabel=null;
       SimpleScalarViewer              viewer=null;
       NumberScalarWheelEditor         wheelSetter=null;
       NumberScalarComboEditor         nComboSetter=null;
       StringScalarEditor              stringSetter=null;
       StringScalarComboEditor         sComboSetter=null;
       JButton                         propertyButton=null;


       propFrame = null;
       propFrame = new SimplePropertyFrame();
       
       nbRows = listModel.size();
       for (indRow=0; indRow < nbRows; indRow++)
       {
	  try
	  {
	     // Find the scalar attribute model
	     ins = null;
	     iss = null;
	     elem = listModel.get(indRow);
	     if (elem instanceof INumberScalar)
	     {
		ins = (INumberScalar) elem;
	     }
	     else
	        if (elem instanceof IStringScalar)
	           iss = (IStringScalar) elem;
		
	     if ( (ins != null) || (iss != null) ) // if attribute model found
	     {
	        // remove this model from all viewers
	        elem = scalarLabels.get(indRow);
		if (elem instanceof LabelViewer)
		{
		   scalarLabel = (LabelViewer) elem;
		   scalarLabel.setModel(null);
		}
		elem = scalarViewers.get(indRow);
		if (elem instanceof SimpleScalarViewer)
		{
		   viewer = (SimpleScalarViewer) elem;
		   viewer.clearModel();
		}
		
		elem = scalarSetters.get(indRow);
		if (elem instanceof NumberScalarWheelEditor)
		{
		   wheelSetter = (NumberScalarWheelEditor) elem;
		   wheelSetter.setModel(null);
		}
		else
		{
		   if (elem instanceof NumberScalarComboEditor)
		   {
		      nComboSetter = (NumberScalarComboEditor) elem;
		      nComboSetter.setNumberModel(null);
		   }
		   else
		   {
		      if (elem instanceof StringScalarEditor)
		      {
			 stringSetter = (StringScalarEditor) elem;
			 stringSetter.setModel(null);
		      }
		      else
		      {
			 if (elem instanceof StringScalarComboEditor)
			 {
			    sComboSetter = (StringScalarComboEditor) elem;
			    sComboSetter.setStringModel(null);
			 }
		      }
		   }
		}
	     }
	  }
	  catch (Exception e)
	  {
	    System.out.println("ScalarListSetter : setTheFont : Caught exception  "+e.getMessage());
	  }
       }
       
       scalarLabels.removeAllElements();
       scalarViewers.removeAllElements();
       scalarSetters.removeAllElements();
       scalarPropButtons.removeAllElements();
       listModel.removeAllElements();
       this.removeAll();
       listModel = null;
       scalarLabels = null;
       scalarViewers = null;
       scalarSetters = null;
       scalarPropButtons = null;
    }
    
    


    public java.awt.Font getTheFont()
    {
       return(theFont);
    }
    

    public void setTheFont(java.awt.Font  ft)
    {
       int                             indRow, nbRows;
       Object                          elem = null;
       LabelViewer                     scalarLabel=null;
       SimpleScalarViewer              viewer=null;
       NumberScalarWheelEditor         wheelSetter=null;
       StringScalarEditor              stringSetter=null;
       JButton                         propertyButton=null;
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
	           elem = scalarLabels.get(indRow);
		   if (elem instanceof LabelViewer)
		   {
		      scalarLabel = (LabelViewer) elem;
		      scalarLabel.setFont(theFont);
		   }

	           elem = scalarViewers.get(indRow);
		   if (elem instanceof SimpleScalarViewer)
		   {
		      viewer = (SimpleScalarViewer) elem;
		      viewer.setFont(theFont);
		   }

	           elem = scalarSetters.get(indRow);
		   if (   (elem instanceof NumberScalarWheelEditor)
		       || (elem instanceof NumberScalarComboEditor)
		       || (elem instanceof StringScalarEditor)
		       || (elem instanceof StringScalarComboEditor) )
		   {
		       setter = (JComponent) elem;
		       setter.setFont(theFont);
		   }

	           elem = scalarPropButtons.get(indRow);
		   if (elem instanceof JButton)
		   {
		      propertyButton = (JButton) elem;
		      propertyButton.setFont(theFont);
		   }
		}
		catch (Exception e)
		{
		  System.out.println("ScalarListSetter : setTheFont : Caught exception  "+e.getMessage());
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
       Object                          elem = null;
       LabelViewer                     scalarLabel=null;


       if (scalarLabels != null)
       {
	  nbRows = scalarLabels.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        elem = scalarLabels.get(indRow);
		if (elem instanceof LabelViewer)
		{
		   scalarLabel = (LabelViewer) elem;
		   scalarLabel.setVisible(labelVisible);
		}
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListSetter : changeLabelVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if scalarLabels != null

    }

    

    public boolean getViewerVisible()
    {
       return(viewerVisible);
    }
    

    public void setViewerVisible(boolean  sv)
    {
        if (viewerVisible != sv)
	{
	   viewerVisible = sv;
	   changeViewerVisibility();
	}
    }

    
    private void changeViewerVisibility()
    {
       int                             indRow, nbRows;
       Object                          elem = null;
       JComponent                      viewer = null;


       if (scalarViewers != null)
       {
	  nbRows = scalarViewers.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        elem = scalarViewers.get(indRow);
		if (elem instanceof SimpleScalarViewer)
		{
		    viewer = (JComponent) elem;
		    viewer.setVisible(viewerVisible);
		}
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListSetter : changeViewerVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if scalarViewers != null

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
       Object                          elem = null;
       JButton                         propertyButton=null;


       if (scalarPropButtons != null)
       {
	  nbRows = scalarPropButtons.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        elem = scalarPropButtons.get(indRow);
		if (elem instanceof JButton)
		{
		   propertyButton = (JButton) elem;
		   propertyButton.setVisible(propertyButtonVisible);
		}
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListSetter : changePropButtonVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if scalarPropButtons != null

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
       Object                          elem = null;
       SimpleScalarViewer              viewer=null;
       NumberScalarComboEditor         setter=null;


       if (scalarViewers != null)
       {
	  nbRows = scalarViewers.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        elem = scalarViewers.get(indRow);
		if (elem instanceof SimpleScalarViewer)
		{
		   viewer = (SimpleScalarViewer) elem;
		   viewer.setUnitVisible(unitVisible);
		}
		elem = scalarSetters.get(indRow);
		if (elem instanceof NumberScalarComboEditor)
		{
		   setter = (NumberScalarComboEditor) elem;
		   setter.setUnitVisible(unitVisible);
		}
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListSetter : changeUnitVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if nsSetters != null

    }
     
     
     
    public String getNumberSetterType()
    {
         return numberSetterType;
    }
    
    public void setNumberSetterType(String  setType)
    {
        if (listModel != null)
	   return;
	   
        if (setType.equalsIgnoreCase(NUMBER_DEFAULT_SETTER))
	   numberSetterType = NUMBER_DEFAULT_SETTER;
	else
	   if (setType.equalsIgnoreCase(NUMBER_COMBO_SETTER))
	       numberSetterType = NUMBER_COMBO_SETTER;
	   else
	       numberSetterType = NUMBER_DEFAULT_SETTER;
    }
     
     
     
    public String getStringSetterType()
    {
         return stringSetterType;
    }
    
    public void setStringSetterType(String  setType)
    {
        if (listModel != null)
	   return;
	   
        if (setType.equalsIgnoreCase(STRING_DEFAULT_SETTER))
	   stringSetterType = STRING_DEFAULT_SETTER;
	else
	   if (setType.equalsIgnoreCase(STRING_COMBO_SETTER))
	       stringSetterType = STRING_COMBO_SETTER;
	   else
	       stringSetterType = STRING_DEFAULT_SETTER;
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
       Object                          elem = null;
       NumberScalarWheelEditor         setter=null;


       if (scalarSetters != null)
       {
	  nbRows = scalarSetters.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        elem = scalarSetters.get(indRow);
		if (elem instanceof NumberScalarWheelEditor)
		{
		   setter = (NumberScalarWheelEditor) elem;
		   if (ac == null)
		      setter.setButtonColor(setter.getBackground());
		   else
		      setter.setButtonColor(ac);
		}
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListViewer : changeArrowColors : Caught exception  "+e.getMessage());
	     }
	  }
       } // if scalarSetters != null

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
    private void initComponents(fr.esrf.tangoatk.core.AttributeList scalarList)
    {
	int                             nbAtts, idx, viewerRow;
	boolean                         containsNumberScalar;
	Object                          elem;
	INumberScalar                   ins;
	IStringScalar                   iss;
        java.awt.GridBagConstraints     gridBagConstraints;
	
	LabelViewer                     scalarLabel=null;
	SimpleScalarViewer              viewer=null;
	NumberScalarWheelEditor         wheelSetter=null;
	NumberScalarComboEditor         comboSetter=null;
	StringScalarEditor              stringSetter=null;
	StringScalarComboEditor         stringComboSetter=null;
	JComponent                      setter=null;
	JButton                         propertyButton=null;
	
	int                             maxRowElementHeight;
	int                             currH;
	int                             hMargin;


	listModel = new Vector();
	scalarLabels = new Vector();
	scalarViewers = new Vector();
	scalarSetters = new Vector();
	scalarPropButtons = new Vector();
	
	
	viewerRow = 0;
	nbAtts = scalarList.size();
	maxRowElementHeight = 0;
	
	for (idx=0; idx < nbAtts; idx++)
	{
           scalarLabel = null;
           viewer = null;
           wheelSetter = null;
           comboSetter = null;
	   setter = null;
	   stringSetter = null;
           stringComboSetter = null;
           propertyButton = null;
	   
	   elem = scalarList.getElementAt(idx);
	   
	   if (!(elem instanceof AAttribute))
	      continue;
	      
	   AAttribute  att = (AAttribute) elem;
	   if (!(att.isWritable()) )
	      continue;
	      
	   if ( (elem instanceof INumberScalar) || (elem instanceof IStringScalar) )
	   {
	      ins = null;
	      iss = null;
	      
	      // Create setter
	      if (elem instanceof INumberScalar)
	      {
	         ins = (INumberScalar) elem;
		 if (numberSetterType.equalsIgnoreCase(NUMBER_COMBO_SETTER))
		 {
         	    comboSetter = new NumberScalarComboEditor();
        	    comboSetter.setFont(theFont);
        	    comboSetter.setBackground(getBackground());
	            comboSetter.setUnitVisible(unitVisible);
		    comboSetter.setNumberModel(ins);

	            scalarSetters.add(comboSetter);
		    setter = comboSetter;
		 }
		 else
		 {
         	    wheelSetter = new NumberScalarWheelEditor();
        	    wheelSetter.setFont(theFont);
        	    wheelSetter.setBackground(getBackground());
		    wheelSetter.setModel(ins);

	            scalarSetters.add(wheelSetter);
		    setter = wheelSetter;
		 }
	      }
	      else //IStringScalar
	      {
	         iss = (IStringScalar) elem;
		 if (stringSetterType.equalsIgnoreCase(STRING_COMBO_SETTER))
		 {
         	    stringComboSetter = new StringScalarComboEditor();
        	    stringComboSetter.setFont(theFont);
        	    //stringComboSetter.setBackground(getBackground());
		    stringComboSetter.setStringModel(iss);

	            scalarSetters.add(stringComboSetter);
		    setter = stringComboSetter;
		 }
		 else
		 {
         	    stringSetter = new StringScalarEditor();
        	    stringSetter.setFont(theFont);
        	    //stringSetter.setBackground(getBackground());
		    stringSetter.setModel(iss);

	            scalarSetters.add(stringSetter);
		    setter = stringSetter;
		 }
	      }
	      
              scalarLabel = new LabelViewer();
              viewer = new SimpleScalarViewer();
              propertyButton = new javax.swing.JButton();

	      // Set the Label Viewer properties
	      scalarLabel.setFont(theFont);
	      scalarLabel.setHorizontalAlignment(JSmoothLabel.RIGHT_ALIGNMENT);
	      scalarLabel.setBackground(getBackground());
	      //scalarLabel.setValueOffsets(0, -5);
	      if (labelVisible)
		 scalarLabel.setVisible(true);
	      else
		 scalarLabel.setVisible(false);
          
	      if (ins != null)
	         scalarLabel.setModel(ins);
	      else
	         scalarLabel.setModel(iss);
	      	 

              viewer.setFont(theFont);
	      // Set the Viewer properties
              viewer.setFont(theFont);
	      viewer.setUnitVisible(unitVisible);
              viewer.setBackgroundColor(getBackground());
              viewer.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
	      viewer.setAlarmEnabled(true);
	      //viewer.setValueOffsets(0, -5);
	      viewer.setVisible(viewerVisible);
	      if (ins != null)
	         viewer.setModel(ins);
	      else
	         viewer.setModel(iss);
	      

              propertyButton.setFont(theFont);
              propertyButton.setBackground(getBackground());
              propertyButton.setText(" ... ");
              propertyButton.setMargin(new java.awt.Insets(-3, 0, 3, 0));
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
		    
	      
	      // to enable the viewers / setters to be correctly sized!
	      if (ins != null)
	         ins.refresh();
	      else
	         iss.refresh();
		
              viewer.addTextListener(this);		 

	      currH = scalarLabel.getPreferredSize().height;
	      if (currH > maxRowElementHeight)
	         maxRowElementHeight = currH;
		 
	      currH = viewer.getPreferredSize().height;
	      if (currH > maxRowElementHeight)
	         maxRowElementHeight = currH;
		 	 
	      currH = setter.getPreferredSize().height;
	      if (currH > maxRowElementHeight)
	         maxRowElementHeight = currH;
	      


	      
	      // Add all these viewers to the panel	      
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridx = 0;
              gridBagConstraints.gridy = viewerRow;
              gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
              gridBagConstraints.insets = new java.awt.Insets(1,5,1,1);
              add(scalarLabel, gridBagConstraints);
	      
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
	      if (ins != null)
	      {
        	 gridBagConstraints.insets = new java.awt.Insets(1,1,1,1);
                 add(setter, gridBagConstraints);
	      }
	      else
	      {
        	 gridBagConstraints.insets = new java.awt.Insets(1,3,1,1);
                 add(setter, gridBagConstraints);
	      }
	      
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridx = 3;
              gridBagConstraints.gridy = viewerRow;
              gridBagConstraints.insets = new java.awt.Insets(1,3,1,5);
              add(propertyButton, gridBagConstraints);
	      
	      // Add to the vectors
	      if (ins != null)
		 listModel.add(ins);
	      else
		 listModel.add(iss);
	      scalarLabels.add(scalarLabel);
	      scalarViewers.add(viewer);
	      scalarPropButtons.add(propertyButton);

	      viewerRow++;
	   }
	}
	

	nbAtts = listModel.size();
	for (idx=0; idx < nbAtts; idx++)
	{
	    elem = scalarViewers.get(idx);
	    if (elem instanceof SimpleScalarViewer)
	    {
	       viewer = (SimpleScalarViewer) elem;
	       currH = viewer.getPreferredSize().height;
	       if (currH < maxRowElementHeight)
	       {
	          hMargin = (maxRowElementHeight - currH) / 2;
		  java.awt.Insets  marge = viewer.getMargin();
		  marge.top = marge.top + hMargin;
		  marge.bottom = marge.bottom + hMargin;
		  viewer.setMargin(marge);
	       }
	    }

	    elem = scalarSetters.get(idx);
	    if (elem instanceof StringScalarEditor)
	    {
	       stringSetter = (StringScalarEditor) elem;
	       currH = stringSetter.getPreferredSize().height;
	       if (currH < maxRowElementHeight)
	       {
	          hMargin = (maxRowElementHeight - currH) / 2;
		  java.awt.Insets  marge = stringSetter.getMargin();
		  marge.top = marge.top + hMargin;
		  marge.bottom = marge.bottom + hMargin;
		  stringSetter.setMargin(marge);
		  stringSetter.setMargin(new java.awt.Insets(hMargin, 3, hMargin+2, 3));
	       }
	    }
	}
	
    }
    
    
    private void propertyButtonActionPerformed (java.awt.event.ActionEvent evt)
    {
        int              buttonIndex=-1;
	int              ind, nbButtons;
	Object           elem;
	JButton          propertyButton;
	PropertyFrame    pf;
	INumberScalar    ins;
	IStringScalar    iss;
	
	
	if (scalarPropButtons == null)
	   return;
	
	if (listModel == null)
	   return;
	   
	nbButtons = scalarPropButtons.size();
	
	// Look for the button in the vector
	for (ind=0; ind<nbButtons; ind++)
	{
	   try
	   {
	      elem = scalarPropButtons.get(ind);
	      if (elem instanceof JButton)
	      {
		 propertyButton = (JButton) elem;
		 if (propertyButton.equals(evt.getSource()))
		 {
		    buttonIndex = ind;
		    break;
		 }
	      }
	   }
	   catch (Exception e)
	   {
	     System.out.println("ScalarListSetter : propertyButtonActionPerformed : Caught exception  "+e.getMessage());
	     return;
	   }
	}
	
	if (buttonIndex < 0)
	   return;
	
	// find the INumberScalar corresponding to the button

	ins = null;
	iss = null;
	
	try
	{
	   elem = listModel.get(buttonIndex);
	   if (elem instanceof INumberScalar)
	      ins = (INumberScalar) elem;
	   else
	      if (elem instanceof IStringScalar)
	         iss = (IStringScalar) elem;
	}
	catch (Exception e)
	{
	}
	
	if ((ins == null) && (iss == null))
	   return;
	

	if (propFrame != null)
	{
	   if (ins != null)
	      propFrame.setModel(ins);
	   else
	      propFrame.setModel(iss);
	      
	   propFrame.setVisible(true);
	}
		
	
    }


    
    public static void main(String[] args)
    {
       final fr.esrf.tangoatk.core.AttributeList  attList = new fr.esrf.tangoatk.core.AttributeList();
       ScalarListSetter               scalarls = new ScalarListSetter();
       INumberScalar                        attn;
       IStringScalar                        attstr;
       JFrame                               mainFrame;
       

       //scalarls.setBackground(java.awt.Color.white);
       //scalarls.setForeground(java.awt.Color.black);

       // Connect to a list of number scalar attributes
       try
       {
          scalarls.setViewerVisible(false);
	  attn = (INumberScalar) attList.add("jlp/test/1/att_un");
          attn = (INumberScalar) attList.add("jlp/test/1/att_deux");
          attn = (INumberScalar) attList.add("jlp/test/1/att_trois");
          attn = (INumberScalar) attList.add("jlp/test/1/att_quatre");
          attstr = (IStringScalar) attList.add("jlp/test/1/att_cinq");
          attn = (INumberScalar) attList.add("jlp/test/1/att_six");
	  //attn = (INumberScalar) attList.add("dev/test/10/Short_attr_w");
	  //attn = (INumberScalar) attList.add("dev/test/10/Double_attr_w");
	  //attstr = (IStringScalar) attList.add("dev/test/10/String_attr");
	  //scalarls.setTheFont(new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 18));
          //scalarls.setLabelVisible(false);
          scalarls.setPropertyButtonVisible(false);
	  scalarls.setModel(attList);
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
				     

       mainFrame.setContentPane(scalarls);
       mainFrame.pack();

       mainFrame.show();


/* A temporary solution : start the refresher after a delay to allow the
   layout manger finish it's work! 
   But the best solution is to synchronize with a componentListener method
   as it is done above    
       try
       {
         Thread.sleep(4000);
       }
       catch(Exception e)
       {
       }
       attList.startRefresher();
       */



       try
       {
         Thread.sleep(4000);
       }
       catch(Exception e)
       {
       }
       
       //scalarls.setTheFont(new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 18));
       //scalarls.setLabelVisible(false);
       //scalarls.setViewerVisible(false);
       //scalarls.setPropertyButtonVisible(true);
       //mainFrame.pack();
       
    } // end of main ()
        
}
