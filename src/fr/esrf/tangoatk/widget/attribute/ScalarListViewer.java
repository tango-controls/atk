/*
 * ScalarListViewer.java
 *
 * Created on July 30, 2003, 4:45 PM
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

public class ScalarListViewer extends javax.swing.JPanel
             implements JAutoScrolledTextListener
{
    public static final String      BOOLEAN_DEFAULT_SETTER = "None";
    public static final String      BOOLEAN_COMBO_SETTER = "BooleanComboEditor";


    protected Vector<IAttribute>    listModel;
    protected Vector<LabelViewer>   scalarLabels;
    protected Vector<JComponent>    scalarViewers;
    protected Vector<JComponent>    scalarSetters;
    protected Vector<JButton>       scalarPropButtons;

    protected SimplePropertyFrame   propFrame=null;
        

    /* The bean properties */
    protected java.awt.Font    theFont;
    private boolean          labelVisible;
    private boolean          setterVisible;
    private boolean          propertyButtonVisible;
    private boolean          propertyListEditable;
    private boolean          unitVisible;
    private String           booleanSetterType;
    private Color            arrowColor;
    private String           toolTipDisplay;
    
    /* Deprecated bean properties: the setter type is automatically selected
       according the valueList present or not. */
    private String           numberSetterType = "deprecated";
    private String           stringSetterType = "deprecated";
    public static final String      NUMBER_DEFAULT_SETTER = "WheelEditor";
    public static final String      NUMBER_COMBO_SETTER = "ComboEditor";
    public static final String      STRING_DEFAULT_SETTER = "StringScalarEditor";
    public static final String      STRING_COMBO_SETTER = "StringComboEditor";
    
    public static final String      TOOLTIP_DISPLAY_NONE = "None";
    public static final String      TOOLTIP_DISPLAY_NAME_ONLY = "Name";
    public static final String      TOOLTIP_DISPLAY_ALL = "All";

    /** Creates new form ScalarListViewer */
    public ScalarListViewer()
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
        theFont = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12);
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 10);
	labelVisible = true;
	setterVisible = true;
	propertyButtonVisible = true;
	propertyListEditable = true;
	unitVisible = true;
	booleanSetterType = BOOLEAN_DEFAULT_SETTER;
	toolTipDisplay = TOOLTIP_DISPLAY_NONE;
        setLayout(new java.awt.GridBagLayout());
	
	setVisible(false);
    }
    
    
    public void setModel(fr.esrf.tangoatk.core.AttributeList scalarList)
    {
	int                          nbAtts, idx;
	boolean                      containsScalarAtt;
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

	containsScalarAtt = false;

	for (idx=0; idx < nbAtts; idx++)
	{
	   elem = scalarList.getElementAt(idx);
	   if (     (elem instanceof INumberScalar)
	        ||  (elem instanceof IStringScalar)
		||  (elem instanceof IBooleanScalar)
		||  (elem instanceof IEnumScalar) )
	   {
	      containsScalarAtt = true;
	      break;
	   }
	}

	if (containsScalarAtt == false)
	  return;

	initComponents(scalarList);

	setVisible(true);
	
    }
    
    
    
    
    protected void removeComponents()
    {
       int                             indRow, nbRows;
       IAttribute                      iatt = null;
       JComponent                      jcomp = null;
       INumberScalar                   ins;
       IStringScalar                   iss;
       IBooleanScalar                  ibs;
       IEnumScalar                     ies;
       LabelViewer                     scalarLabel=null;
       SimpleScalarViewer              ssViewer=null;
       BooleanScalarCheckBoxViewer     bsViewer=null;
       SimpleEnumScalarViewer          esViewer=null;
       NumberScalarWheelEditor         wheelSetter=null;
       NumberScalarComboEditor         nComboSetter=null;
       StringScalarEditor              stringSetter=null;
       StringScalarComboEditor         sComboSetter=null;
       BooleanScalarComboEditor        bComboSetter=null;
       EnumScalarComboEditor           eComboSetter=null;
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
	     ibs = null;
	     ies = null;
	     iatt = listModel.get(indRow);
	     if (iatt instanceof INumberScalar)
	     {
		ins = (INumberScalar) iatt;
	     }
	     else
	        if (iatt instanceof IStringScalar)
	           iss = (IStringScalar) iatt;
		else
	           if (iatt instanceof IBooleanScalar)
	              ibs = (IBooleanScalar) iatt;
		   else
	              if (iatt instanceof IEnumScalar)
	        	 ies = (IEnumScalar) iatt;
		
	     if ( (ins != null) || (iss != null) || (ibs != null) || (ies != null) ) // if attribute model found
	     {
	        // remove this model from all viewers
	        scalarLabel = scalarLabels.get(indRow);
		scalarLabel.setModel(null);
		
		jcomp = scalarViewers.get(indRow);
		if (jcomp instanceof SimpleScalarViewer)
		{
		   ssViewer = (SimpleScalarViewer) jcomp;
		   ssViewer.clearModel();
		}
		else
		   if (jcomp instanceof BooleanScalarCheckBoxViewer)
		   {
		      bsViewer = (BooleanScalarCheckBoxViewer) jcomp;
		      bsViewer.clearModel();
		   }
		   else
		      if (jcomp instanceof SimpleEnumScalarViewer)
		      {
			 esViewer = (SimpleEnumScalarViewer) jcomp;
			 esViewer.clearModel();
		      }
		
		
		jcomp = scalarSetters.get(indRow);
		
		if (jcomp != null)
		{
		   if (jcomp instanceof NumberScalarWheelEditor)
		   {
		      wheelSetter = (NumberScalarWheelEditor) jcomp;
		      if (ins != null)
	        	  if (ins.isWritable())
		             wheelSetter.setModel(null);
		   }
		   else
		   {
		      if (jcomp instanceof NumberScalarComboEditor)
		      {
			 nComboSetter = (NumberScalarComboEditor) jcomp;
			 if (ins != null)
	        	     if (ins.isWritable())
		        	nComboSetter.setNumberModel(null);
		      }
		      else
		      {
			 if (jcomp instanceof StringScalarEditor)
			 {
			    stringSetter = (StringScalarEditor) jcomp;
			    if (iss != null)
	                	if (iss.isWritable())
				   stringSetter.setModel(null);
			 }
			 else
			 {
			    if (jcomp instanceof StringScalarComboEditor)
			    {
			       sComboSetter = (StringScalarComboEditor) jcomp;
			       if (iss != null)
	                	   if (iss.isWritable())
				      sComboSetter.setStringModel(null);
			    }
			    else
			    {
			       if (jcomp instanceof BooleanScalarComboEditor)
			       {
				  bComboSetter = (BooleanScalarComboEditor) jcomp;
				  if (ibs != null)
	                	      if (ibs.isWritable())
					 bComboSetter.setAttModel(null);
			       }
			       else
			       {
				  if (jcomp instanceof EnumScalarComboEditor)
				  {
				     eComboSetter = (EnumScalarComboEditor) jcomp;
				     if (ies != null)
	                		 if (ies.isWritable())
					    eComboSetter.setEnumModel(null);
				  }
			       }
			    }
			 }
		      }
		   }
		}
	     }
	  }
	  catch (Exception e)
	  {
	    System.out.println("ScalarListViewer : setTheFont : Caught exception  "+e.getMessage());
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
       LabelViewer                     scalarLabel=null;
       JComponent                      viewer = null;
       JComponent                      setter = null;
       JButton                         propertyButton=null;


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
	           scalarLabel = scalarLabels.get(indRow);
		   scalarLabel.setFont(theFont);

	           viewer = scalarViewers.get(indRow);
		   if (viewer != null)
		   {
		      if (   (viewer instanceof SimpleScalarViewer)
			  || (viewer instanceof BooleanScalarCheckBoxViewer)
			  || (viewer instanceof SimpleEnumScalarViewer) )
		      {
			  viewer.setFont(theFont);
		      }
		   }

	           setter = scalarSetters.get(indRow);
		   if (setter != null)
		   {
		      if (   (setter instanceof NumberScalarWheelEditor)
			  || (setter instanceof NumberScalarComboEditor)
			  || (setter instanceof StringScalarEditor)
			  || (setter instanceof StringScalarComboEditor)
			  || (setter instanceof BooleanScalarComboEditor)
			  || (setter instanceof EnumScalarComboEditor) )
		      {
			  setter.setFont(theFont);
		      }
		   }

	           propertyButton = scalarPropButtons.get(indRow);
		   propertyButton.setFont(theFont);
		}
		catch (Exception e)
		{
		  System.out.println("ScalarListViewer : setTheFont : Caught exception  "+e.getMessage());
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
       int                        indRow, nbRows;
       LabelViewer                scalarLabel=null;


       if (scalarLabels != null)
       {
	  nbRows = scalarLabels.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        scalarLabel = scalarLabels.get(indRow);
		scalarLabel.setVisible(labelVisible);
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListViewer : changeLabelVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if scalarLabels != null

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


       if (scalarSetters != null)
       {
	  nbRows = scalarSetters.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        setter = scalarSetters.get(indRow);
		if (setter != null)
		   if (   (setter instanceof NumberScalarWheelEditor)
		       || (setter instanceof NumberScalarComboEditor)
		       || (setter instanceof StringScalarEditor)
		       || (setter instanceof StringScalarComboEditor)
		       || (setter instanceof BooleanScalarComboEditor)
		       || (setter instanceof EnumScalarComboEditor) )
		   {
		       setter.setVisible(setterVisible);
		   }
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListViewer : changeSetterVisibility : Caught exception  "+e.getMessage());
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


       if (scalarPropButtons != null)
       {
	  nbRows = scalarPropButtons.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        propertyButton = scalarPropButtons.get(indRow);
		propertyButton.setVisible(propertyButtonVisible);
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListViewer : changePropButtonVisibility : Caught exception  "+e.getMessage());
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
       JComponent                      jcomp = null;
       SimpleScalarViewer              viewer=null;
       NumberScalarComboEditor         setter=null;


       if (scalarViewers != null)
       {
	  nbRows = scalarViewers.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        jcomp = scalarViewers.get(indRow);
		if (jcomp instanceof SimpleScalarViewer)
		{
		   viewer = (SimpleScalarViewer) jcomp;
		   viewer.setUnitVisible(unitVisible);
		}
		jcomp = scalarSetters.get(indRow);
		if (jcomp != null)
		   if (jcomp instanceof NumberScalarComboEditor)
		   {
		      setter = (NumberScalarComboEditor) jcomp;
		      setter.setUnitVisible(unitVisible);
		   }
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListViewer : changeUnitVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if scalarViewers != null

    }
     
     
   /**
    * Returns the current BooleanSetterType used for all BooleanScalar attributes
    * @see #setBooleanSetterType
    */
    public String getBooleanSetterType()
    {
         return booleanSetterType;
    }
    
   /**
    * Sets the current BooleanSetterType used for all BooleanScalar attributes
    * @see #getBooleanSetterType
    */
    public void setBooleanSetterType(String  setType)
    {
	if (listModel != null)
	   return;
	   
        if (setType.equalsIgnoreCase(BOOLEAN_DEFAULT_SETTER))
	   booleanSetterType = BOOLEAN_DEFAULT_SETTER;
	else
	   if (setType.equalsIgnoreCase(BOOLEAN_COMBO_SETTER))
	       booleanSetterType = BOOLEAN_COMBO_SETTER;
	   else
	       booleanSetterType = BOOLEAN_DEFAULT_SETTER;
    }
     
     
   /**
    * Returns the current toolTipDisplay
    * @see #setToolTipDisplay
    */
    public String getToolTipDisplay()
    {
         return toolTipDisplay;
    }
    
   /**
    * Sets the current toolTipDisplay. This property should be set before the call to setModel()
    * @see #getToolTipDisplay
    */
    public void setToolTipDisplay(String  ttType)
    {
	if (listModel != null)
	   return;
	   
        if (ttType.equalsIgnoreCase(TOOLTIP_DISPLAY_ALL))
	   toolTipDisplay = TOOLTIP_DISPLAY_ALL;
	else
	   if (ttType.equalsIgnoreCase(TOOLTIP_DISPLAY_NAME_ONLY))
	       toolTipDisplay = TOOLTIP_DISPLAY_NAME_ONLY;
	   else
	       toolTipDisplay = TOOLTIP_DISPLAY_NONE;
    }
     
     
    /**
     * @deprecated As of ATKWidget-2.5.8 and higher
     * The method getNumberSetterType should not be used.
     * The setterType for each NumberScalar attribute is selected automatically by
     * the ScalarListViewer.
     */
    public String getNumberSetterType()
    {
         return numberSetterType;
    }
    
    /**
     * @deprecated As of ATKWidget-2.5.8 and higher this method has no effect.
     * The setterType for each NumberScalar attribute is selected automatically by
     * the ScalarListViewer.
     */
    public void setNumberSetterType(String  setType)
    {
        /* deprecated
	if (listModel != null)
	   return;
	   
        if (setType.equalsIgnoreCase(NUMBER_DEFAULT_SETTER))
	   numberSetterType = NUMBER_DEFAULT_SETTER;
	else
	   if (setType.equalsIgnoreCase(NUMBER_COMBO_SETTER))
	       numberSetterType = NUMBER_COMBO_SETTER;
	   else
	       numberSetterType = NUMBER_DEFAULT_SETTER;
	*/
    }
     
     
     
    /**
     * @deprecated As of ATKWidget-2.5.8 and higher
     * The method getStringSetterType should not be used.
     * The setterType for each StringScalar attribute is selected automatically by
     * the ScalarListViewer.
     */
    public String getStringSetterType()
    {
         return stringSetterType;
    }
    
    
    /**
     * @deprecated As of ATKWidget-2.5.8 and higher this method has no effect.
     * The setterType for each StringScalar attribute is selected automatically by
     * the ScalarListViewer.
     */
    public void setStringSetterType(String  setType)
    {
        /* deprecated
	if (listModel != null)
	   return;
	   
        if (setType.equalsIgnoreCase(STRING_DEFAULT_SETTER))
	   stringSetterType = STRING_DEFAULT_SETTER;
	else
	   if (setType.equalsIgnoreCase(STRING_COMBO_SETTER))
	       stringSetterType = STRING_COMBO_SETTER;
	   else
	       stringSetterType = STRING_DEFAULT_SETTER;
	 */
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


       if (scalarSetters != null)
       {
	  nbRows = scalarSetters.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        jcomp = scalarSetters.get(indRow);
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
    
    public void setCheckBoxStrings(IBooleanScalar ibs, String trueLabel, String falseLabel)
    {
    
       int                          ibsIndex=-1;
       JComponent                   jcomp=null;
       
              
       if (ibs == null)
          return;

       if (listModel == null)
	  return;

       ibsIndex = listModel.indexOf(ibs);
       if ( ibsIndex < 0)
          return;
	  
       
       jcomp = scalarViewers.get(ibsIndex);
       if (jcomp == null) return;
	  
       
       if ( !(jcomp instanceof BooleanScalarCheckBoxViewer) )
          return;
	  
       BooleanScalarCheckBoxViewer cb = (BooleanScalarCheckBoxViewer) jcomp;
       
       if (trueLabel == null)
          cb.setTrueLabel(new String());
       else
          cb.setTrueLabel(trueLabel);
       
       
       if (falseLabel == null)
          cb.setFalseLabel(new String());
       else
          cb.setFalseLabel(falseLabel);
 
       this.revalidate();

    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    protected void initComponents(fr.esrf.tangoatk.core.AttributeList scalarList)
    {
	int                             nbAtts, idx, viewerRow, nbScalarViewers;
	boolean                         containsNumberScalar;
	Object                          elem;
	INumberScalar                   ins;
	IStringScalar                   iss;
	IBooleanScalar                  ibs;
	IEnumScalar                     ies;
        java.awt.GridBagConstraints     gridBagConstraints;
	
	LabelViewer                     scalarLabel=null;
	SimpleScalarViewer              ssViewer=null;
	BooleanScalarCheckBoxViewer     boolViewer=null;
	BooleanScalarComboEditor        boolComboSetter=null;
	SimpleEnumScalarViewer          enumViewer=null;
	EnumScalarComboEditor           enumComboSetter=null;
	NumberScalarWheelEditor         wheelSetter=null;
	NumberScalarComboEditor         comboSetter=null;
	StringScalarEditor              stringSetter=null;
	StringScalarComboEditor         stringComboSetter=null;
	JComponent                      jcomp=null;
	JComponent                      viewer=null;
	JComponent                      setter=null;
	JButton                         propertyButton=null;
	
	int                             maxRowElementHeight;
	int                             currH;
	int                             hMargin;
	boolean                         insHasValueList, issHasValueList;


	listModel = new Vector<IAttribute> ();
	scalarLabels = new Vector<LabelViewer> ();
	scalarViewers = new Vector<JComponent> ();
	scalarSetters = new Vector<JComponent> ();
	scalarPropButtons = new Vector<JButton> ();
	
	
	viewerRow = 0;
	nbAtts = scalarList.size();
	maxRowElementHeight = 0;
	
	for (idx=0; idx < nbAtts; idx++)
	{
           scalarLabel = null;
           viewer = null;
	   boolViewer=null;
           boolComboSetter = null;
	   enumViewer=null;
           enumComboSetter = null;
	   ssViewer=null;
           wheelSetter = null;
           comboSetter = null;
	   setter = null;
	   stringSetter = null;
           stringComboSetter = null;
           propertyButton = null;
	   
	   elem = scalarList.getElementAt(idx);
	   if (    (elem instanceof INumberScalar)
	        || (elem instanceof IStringScalar)
		|| (elem instanceof IBooleanScalar)
		|| (elem instanceof IEnumScalar))
	   {
	      ins = null;
	      iss = null;
	      ibs = null;
	      ies = null;
	      
	      // Create setter
	      if (elem instanceof INumberScalar)
	      {
                 ssViewer = new SimpleScalarViewer();
		 java.awt.Insets  marge = ssViewer.getMargin();
		 marge.left = marge.left + 2;
		 marge.right = marge.right + 2;
		 ssViewer.setMargin(marge);
		 
		 viewer = ssViewer;
	         ins = (INumberScalar) elem;
		 insHasValueList = false;
		 if (ins.getPossibleValues() != null)
		    if (ins.getPossibleValues().length > 0)
		        insHasValueList = true;
		 if (insHasValueList)
		 {
         	    comboSetter = new NumberScalarComboEditor();
        	    comboSetter.setFont(theFont);
        	    comboSetter.setBackground(getBackground());
	            comboSetter.setUnitVisible(unitVisible);
		    if (ins.isWritable())
		    {
		       comboSetter.setNumberModel(ins);
        	       comboSetter.setVisible(setterVisible);
		    }
		    else
		       comboSetter.setVisible(false);

	            scalarSetters.add(comboSetter);
		    setter = comboSetter;
		 }
		 else
		 {
         	    wheelSetter = new NumberScalarWheelEditor();
        	    wheelSetter.setFont(theFont);
        	    wheelSetter.setBackground(getBackground());
		    if (ins.isWritable())
		    {
		       wheelSetter.setModel(ins);
        	       wheelSetter.setVisible(setterVisible);
		    }
		    else
		       wheelSetter.setVisible(false);

	            scalarSetters.add(wheelSetter);
		    setter = wheelSetter;
		 }
	      }
	      else
		 if (elem instanceof IBooleanScalar)
		 {
	            ibs = (IBooleanScalar) elem;
                    boolViewer = new BooleanScalarCheckBoxViewer();
		    boolViewer.setTrueLabel(new String());
		    boolViewer.setFalseLabel(new String());
		    viewer = boolViewer;
		    if (ibs.isWritable())
		    {
		       if (booleanSetterType.equalsIgnoreCase(BOOLEAN_COMBO_SETTER))
		       {
		           boolComboSetter = new BooleanScalarComboEditor();
        		   boolComboSetter.setFont(theFont);
        		   boolComboSetter.setBackground(getBackground());
			   boolComboSetter.setAttModel(ibs);
        		   boolComboSetter.setVisible(setterVisible);
			   setter = boolComboSetter;
		       }
		       else
			   setter = null;
		    }
		    else
		    {
		       setter = null;
		    }
		    scalarSetters.add(setter);
		 }
		 else
		    if (elem instanceof IEnumScalar)
		    {
	               ies = (IEnumScalar) elem;
                       enumViewer = new SimpleEnumScalarViewer();
		       viewer = enumViewer;
		       if (ies.isWritable())
		       {
		          enumComboSetter = new EnumScalarComboEditor();
        		  enumComboSetter.setFont(theFont);
        		  enumComboSetter.setBackground(getBackground());
			  enumComboSetter.setEnumModel(ies);
        		  enumComboSetter.setVisible(setterVisible);
			  setter = enumComboSetter;
		       }
		       else
		       {
			  setter = null;
		       }
		       scalarSetters.add(setter);
		    }
		    else //IStringScalar
		    {
                       ssViewer = new SimpleScalarViewer();
		       java.awt.Insets  marge = ssViewer.getMargin();
		       marge.left = marge.left + 2;
		       marge.right = marge.right + 2;
		       ssViewer.setMargin(marge);
		       viewer = ssViewer;
	               iss = (IStringScalar) elem;
		       issHasValueList = false;
		       if (iss.getPossibleValues() != null)
			  if (iss.getPossibleValues().length > 0)
		              issHasValueList = true;
		       if (issHasValueList)
		       {
         		  stringComboSetter = new StringScalarComboEditor();
        		  stringComboSetter.setFont(theFont);
        		  //stringComboSetter.setBackground(getBackground());
			  if (iss.isWritable())
			  {
			     stringComboSetter.setStringModel(iss);
			     if (setterVisible)
        			stringComboSetter.setVisible(true);
			     else
				stringComboSetter.setVisible(false);
			  }
			  else
			     stringComboSetter.setVisible(false);

	        	  scalarSetters.add(stringComboSetter);
			  setter = stringComboSetter;
		       }
		       else
		       {
         		  stringSetter = new StringScalarEditor();
        		  stringSetter.setFont(theFont);
        		  //stringSetter.setBackground(getBackground());
			  if (iss.isWritable())
			  {
			     stringSetter.setModel(iss);
			     if (setterVisible)
        			stringSetter.setVisible(true);
			     else
				stringSetter.setVisible(false);
			  }
			  else
			     stringSetter.setVisible(false);

	        	  scalarSetters.add(stringSetter);
			  setter = stringSetter;
		       }
		    }
	      
              scalarLabel = new LabelViewer();
              propertyButton = new javax.swing.JButton();

	      // Set the Label Viewer properties
	      scalarLabel.setFont(theFont);
	      scalarLabel.setHorizontalAlignment(JSmoothLabel.RIGHT_ALIGNMENT);
	      scalarLabel.setBackground(getBackground());
	      //scalarLabel.setValueOffsets(0, -5);
          
	      if (ins != null)
	         scalarLabel.setModel(ins);
	      else
	         if (iss != null)
		     scalarLabel.setModel(iss);
		 else
	            if (ibs != null)
	               scalarLabel.setModel(ibs);
		    else
	               if (ies != null)
	        	  scalarLabel.setModel(ies);
	      	 

	      // Set the Viewer properties
	      if (ssViewer != null) // SimpleScalarViewer
	      {
		 if (toolTipDisplay.equalsIgnoreCase(TOOLTIP_DISPLAY_ALL))
		 {
		    ssViewer.setHasToolTip(true);
		    ssViewer.setQualityInTooltip(true);
		 }
		 else
		    if (toolTipDisplay.equalsIgnoreCase(TOOLTIP_DISPLAY_NAME_ONLY))
		    {
		       ssViewer.setHasToolTip(true);
		       ssViewer.setQualityInTooltip(false);
		    }
		    else
		       {
			  ssViewer.setHasToolTip(false);
			  ssViewer.setQualityInTooltip(false);
		       }
                 ssViewer.setFont(theFont);
		 ssViewer.setUnitVisible(unitVisible);
        	 ssViewer.setBackgroundColor(getBackground());
        	 ssViewer.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
		 ssViewer.setAlarmEnabled(true);
	         ssViewer.addTextListener(this);		 
		 //ssViewer.setValueOffsets(0, -5);
		 if (ins != null)
	            ssViewer.setModel(ins);
		 else
	            ssViewer.setModel(iss);
	      }
	      else // should be a BooleanScalarCheckBoxViewer or SimpleEnumScalarViewer
	      {
	         if (boolViewer != null)
		 {
		    if (     toolTipDisplay.equalsIgnoreCase(TOOLTIP_DISPLAY_ALL)
		         ||  toolTipDisplay.equalsIgnoreCase(TOOLTIP_DISPLAY_NAME_ONLY))
		       boolViewer.setHasToolTip(true);
		    else
		        boolViewer.setHasToolTip(false);
		     boolViewer.setAttModel(ibs);
        	     boolViewer.setBackground(getBackground());
		 }
		 else // should a SimpleEnumScalarViewer
		 {
	            if (enumViewer != null)
		    {
                	enumViewer.setFont(theFont);
        		enumViewer.setBackgroundColor(getBackground());
        		enumViewer.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
			enumViewer.setAlarmEnabled(true);
	        	enumViewer.addTextListener(this);		 
			enumViewer.setModel(ies);
		    }
		 }
	      }
	         
	      
              propertyButton.setFont(theFont);
              propertyButton.setBackground(getBackground());
              propertyButton.setText(" ... ");
              propertyButton.setMargin(new java.awt.Insets(-3, 0, 3, 0));
              propertyButton.setToolTipText("Attribute Properties");	      
	      propertyButton.addActionListener(
	         new java.awt.event.ActionListener() 
		       {
	                  public void actionPerformed(java.awt.event.ActionEvent evt)
			  {
	                     propertyButtonActionPerformed(evt);
	                  }
	               });
		    
	      	 
	      // Set the Label, PropertyButton visibility
	      scalarLabel.setVisible(labelVisible);
	      propertyButton.setVisible(propertyButtonVisible);
		    
	      
	      // to enable the viewers / setters to be correctly sized!
	      if (ins != null)
	         ins.refresh();
	      else
	         if (iss != null)
	            iss.refresh();
		 else
	            if (ibs != null)
		       ibs.refresh();
		    else
	               if (ies != null)
			  ies.refresh();
		
	      // Compute the height of the "highest" element of the CURRENT row
	      // apply vertical margin to the viewer and setters if needed
	      maxRowElementHeight = 0;
	      currH = scalarLabel.getPreferredSize().height+4;
	      if (currH > maxRowElementHeight)
	         maxRowElementHeight = currH;
		 
	      currH = viewer.getPreferredSize().height+4;
	      if (currH > maxRowElementHeight)
	         maxRowElementHeight = currH;
	      
	      if (setter != null)
	      {
		 if (setter.isVisible())
		 { 
		    currH = setter.getPreferredSize().height+4;
		    if (currH > maxRowElementHeight)
	               maxRowElementHeight = currH;
		 }
	      }
	      
	      
	      // Add all these viewers to the panel	      
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridx = 0;
              gridBagConstraints.gridy = viewerRow;
              gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
              gridBagConstraints.insets = new java.awt.Insets(2,6,2,1);
              add(scalarLabel, gridBagConstraints);

              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridx = 1;
              gridBagConstraints.gridy = viewerRow;
              gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
              gridBagConstraints.insets = new java.awt.Insets(2,3,2,1);
              add(viewer, gridBagConstraints);
	      
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridx = 2;
              gridBagConstraints.gridy = viewerRow;
              gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;

	      if (setter != null)
	      {
      		 if (ins != null)
     		 {
           	    gridBagConstraints.insets = new java.awt.Insets(1,3,1,1);
                    add(setter, gridBagConstraints);
   		 }
   		 else
   		    if (iss != null)
   		    {
           	       gridBagConstraints.insets = new java.awt.Insets(2,3,2,1);
                       add(setter, gridBagConstraints);
   		    }
   		    else
   		       if (ibs != null)
   		       {
           		  gridBagConstraints.insets = new java.awt.Insets(2,3,2,1);
                   	  add(setter, gridBagConstraints);
   		       }
   		       else
   			  if (ies != null)
   			  {
           		     gridBagConstraints.insets = new java.awt.Insets(2,3,2,1);
                   	     add(setter, gridBagConstraints);
   			  }
   	      }
	      
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridx = 3;
              gridBagConstraints.gridy = viewerRow;
              gridBagConstraints.insets = new java.awt.Insets(2,3,2,6);
              add(propertyButton, gridBagConstraints);
	      
	      // Add to the vectors
	      if (ins != null)
		 listModel.add(ins);
	      else
	         if (iss != null)
		    listModel.add(iss);
		 else
		    if (ibs != null)
		       listModel.add(ibs);
		    else
		       if (ies != null)
			  listModel.add(ies);
	      scalarLabels.add(scalarLabel);
	      scalarViewers.add(viewer);
	      scalarPropButtons.add(propertyButton);
	      
	      // Apply Vertical Margins if needed

	      if (viewer instanceof SimpleScalarViewer)
	      {
	          SimpleScalarViewer  sv = (SimpleScalarViewer) viewer;
		  currH = viewer.getPreferredSize().height;
		  if (currH < maxRowElementHeight)
	             hMargin = (maxRowElementHeight - currH) / 2;
		  else
		     hMargin = 0;
		  java.awt.Insets  marge = sv.getMargin();
		  marge.top = marge.top + hMargin;
		  marge.bottom = marge.bottom + hMargin;
		  marge.left = marge.left+2;
		  marge.right = marge.right+2;
		  sv.setMargin(marge);
	      }
	      else	      
		  if (viewer instanceof SimpleEnumScalarViewer)
		  {
	              SimpleEnumScalarViewer  sesv = (SimpleEnumScalarViewer) viewer;
	              currH = viewer.getPreferredSize().height;
		      if (currH < maxRowElementHeight)
	        	 hMargin = (maxRowElementHeight - currH) / 2;
		      else
			 hMargin = 0;
		      java.awt.Insets  marge = sesv.getMargin();
		      marge.top = marge.top + hMargin;
		      marge.bottom = marge.bottom + hMargin;
		      marge.left = marge.left+2;
		      marge.right = marge.right+2;
		      sesv.setMargin(marge);
		  } 
	      
	      if ( (setter instanceof StringScalarEditor) && (setter.isVisible()) )
	      {
	          StringScalarEditor   sse = (StringScalarEditor) setter;
		  currH = setter.getPreferredSize().height;
		  if (currH < maxRowElementHeight)
	             hMargin = (maxRowElementHeight - currH) / 2;
		  else
		     hMargin = 0;
		  java.awt.Insets  marge = sse.getMargin();
		  marge.top = marge.top + hMargin;
		  marge.bottom = marge.bottom + hMargin;
		  marge.left = marge.left+2;
		  marge.right = marge.right+2;
		  sse.setMargin(marge);
	      } 

	      viewerRow++;
	   }
	}
	

	/*
	nbScalarViewers = scalarViewers.size();
	for (idx=0; idx < nbScalarViewers; idx++)
	{
	    jcomp = scalarViewers.get(idx);
	    if (jcomp instanceof SimpleScalarViewer)
	    {
	       ssViewer = (SimpleScalarViewer) jcomp;
	       currH = ssViewer.getPreferredSize().height+2;
	       if (currH < maxRowElementHeight)
	       {
	          hMargin = (maxRowElementHeight - currH) / 2;
		  java.awt.Insets  marge = ssViewer.getMargin();
		  marge.top = marge.top + hMargin;
		  marge.bottom = marge.bottom + hMargin;
		  ssViewer.setMargin(marge);
	       }
	    }
	    else
	       if (jcomp instanceof SimpleEnumScalarViewer)
	       {
		  enumViewer = (SimpleEnumScalarViewer) jcomp;
		  currH = enumViewer.getPreferredSize().height+2;
		  if (currH < maxRowElementHeight)
		  {
	             hMargin = (maxRowElementHeight - currH) / 2;
		     java.awt.Insets  marge = enumViewer.getMargin();
		     marge.top = marge.top + hMargin;
		     marge.bottom = marge.bottom + hMargin;
		     enumViewer.setMargin(marge);
		  }
	       }

	    jcomp = scalarSetters.get(idx);
	    if (jcomp != null)
	       if (jcomp instanceof StringScalarEditor)
	       {
		  stringSetter = (StringScalarEditor) jcomp;
		  currH = stringSetter.getPreferredSize().height+2;
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
	*/
	
    }
    
    
    private void propertyButtonActionPerformed (java.awt.event.ActionEvent evt)
    {
        int              buttonIndex=-1;
	int              ind, nbButtons;
	JButton          propertyButton;
	IAttribute       iatt;
	INumberScalar    ins;
	IStringScalar    iss;
	IBooleanScalar   ibs;
	IEnumScalar      ies;
	
	
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
	      propertyButton = scalarPropButtons.get(ind);
	      if (propertyButton.equals(evt.getSource()))
	      {
		 buttonIndex = ind;
		 break;
	      }
	   }
	   catch (Exception e)
	   {
	     System.out.println("ScalarListViewer : propertyButtonActionPerformed : Caught exception  "+e.getMessage());
	     return;
	   }
	}
	
	if (buttonIndex < 0)
	   return;
	
	// find the Scalar attribute corresponding to the button

	ins = null;
	iss = null;
	ibs = null;
	ies = null;
	
	try
	{
	   iatt = listModel.get(buttonIndex);
	   if (iatt instanceof INumberScalar)
	      ins = (INumberScalar) iatt;
	   else
	      if (iatt instanceof IStringScalar)
	         iss = (IStringScalar) iatt;
	      else
		 if (iatt instanceof IBooleanScalar)
	            ibs = (IBooleanScalar) iatt;
		 else
		    if (iatt instanceof IEnumScalar)
	               ies = (IEnumScalar) iatt;
	}
	catch (Exception e)
	{
	}
	
	if ((ins == null) && (iss == null) && (ibs == null) && (ies == null))
	   return;
	

	if (propFrame != null)
	{
	   if (ins != null)
	      propFrame.setModel(ins);
	   else
	      if (iss != null)
		 propFrame.setModel(iss);
	      else
		 if (ibs != null)
		    propFrame.setModel(ibs);
		 else
		    if (ies != null)
		       propFrame.setModel(ies);
		       	      
	   propFrame.setVisible(true);
	}
	
    }


    
    public static void main(String[] args)
    {
       final fr.esrf.tangoatk.core.AttributeList  attList = new fr.esrf.tangoatk.core.AttributeList();
       ScalarListViewer               scalarlv = new ScalarListViewer();
       INumberScalar                        attn;
       IStringScalar                        attstr;
       IBooleanScalar                       attbool, attcomp, attrefsubst;
       IEnumScalar                          attEnum;
       IAttribute                           iatt;
       JFrame                               mainFrame;
       

       //scalarlv.setBackground(Color.white);
       //scalarlv.setForeground(Color.black);
       scalarlv.setTheFont(new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 18));
       //scalarlv.setLabelVisible(false);
       //scalarlv.setSetterVisible(false);
       //scalarlv.setPropertyButtonVisible(false);
       scalarlv.setBooleanSetterType(ScalarListViewer.BOOLEAN_COMBO_SETTER);

       // Connect to a list of scalar attributes
       try
       {
       
	  //attn = (INumberScalar) attList.add("sy/d-tm/profile/Average");
          //attn = (INumberScalar) attList.add("sy/d-tm/profile/ShakerStrength");
          //attn = (INumberScalar) attList.add("sy/d-tm/profile/SpectrumIndex");
          //attstr = (IStringScalar) attList.add("sy/d-tm/profile/MeasurementPlane");
          //attstr = (IStringScalar) attList.add("sy/d-tm/profile/ShakerMode");
	  //attbool = (IBooleanScalar) attList.add("sy/d-tm/profile/Complement");
	  //attcomp = attbool;
          //if (attcomp != null) System.out.println("attcomp != null");
	  //attbool = (IBooleanScalar) attList.add("sy/d-tm/profile/ReferenceSubstraction");
	  //attrefsubst = attbool;
  	  //scalarlv.setCheckBoxStrings(attcomp, "comp", null);
	  //scalarlv.setCheckBoxStrings(attrefsubst, "Pulsed", "W Noise");
     
       
	  attn = (INumberScalar) attList.add("jlp/test/1/att_un");
          attn = (INumberScalar) attList.add("jlp/test/1/att_deux");
          attn = (INumberScalar) attList.add("jlp/test/1/att_trois");
          attn = (INumberScalar) attList.add("jlp/test/1/att_quatre");
          attstr = (IStringScalar) attList.add("jlp/test/1/att_cinq");
	  attbool = (IBooleanScalar) attList.add("jlp/test/1/Att_boolean");
          //attn = (INumberScalar) attList.add("jlp/test/1/att_six");
          //attEnum = (IEnumScalar) attList.add("jlp/test/1/att_six");
          iatt = (IAttribute) attList.add("jlp/test/1/att_six");

	  scalarlv.setModel(attList);
	  
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
				     

       mainFrame.setContentPane(scalarlv);
       mainFrame.pack();

       mainFrame.setVisible(true);
       
    } // end of main ()
        
}
