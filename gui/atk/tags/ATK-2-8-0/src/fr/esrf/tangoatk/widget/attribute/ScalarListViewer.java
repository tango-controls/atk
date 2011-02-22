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
import fr.esrf.tangoatk.widget.attribute.NumberScalarWheelEditor;
import fr.esrf.tangoatk.widget.attribute.StringScalarEditor;
import fr.esrf.tangoatk.widget.attribute.SimpleScalarViewer;
import fr.esrf.tangoatk.widget.util.JSmoothLabel;
import fr.esrf.tangoatk.widget.util.JAutoScrolledText;
import fr.esrf.tangoatk.widget.util.JAutoScrolledTextListener;
import fr.esrf.tangoatk.widget.properties.LabelViewer;
import fr.esrf.tangoatk.widget.attribute.SimplePropertyFrame;

public class ScalarListViewer extends javax.swing.JPanel
             implements JAutoScrolledTextListener
{
    public static final String      BOOLEAN_DEFAULT_SETTER = "None";
    public static final String      BOOLEAN_COMBO_SETTER = "BooleanComboEditor";


    protected Vector                listModel;
    protected Vector                scalarLabels, scalarViewers, scalarSetters, scalarPropButtons;
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
    
    /* Deprecated bean properties: the setter type is automatically selected
       according the valueList present or not. */
    private String           numberSetterType = "deprecated";
    private String           stringSetterType = "deprecated";
    public static final String      NUMBER_DEFAULT_SETTER = "WheelEditor";
    public static final String      NUMBER_COMBO_SETTER = "ComboEditor";
    public static final String      STRING_DEFAULT_SETTER = "StringScalarEditor";
    public static final String      STRING_COMBO_SETTER = "StringComboEditor";

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
        theFont = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 14);
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 10);
	labelVisible = true;
	setterVisible = true;
	propertyButtonVisible = true;
	propertyListEditable = true;
	unitVisible = true;
	booleanSetterType = BOOLEAN_DEFAULT_SETTER;
        setLayout(new java.awt.GridBagLayout());
	
	setVisible(false);
    }
    
    
    public void setModel(fr.esrf.tangoatk.core.AttributeList scalarList)
    {
	int                          nbAtts, idx;
	boolean                      containsScalarAtt;
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
       Object                          elem = null;
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
	     elem = listModel.get(indRow);
	     if (elem instanceof INumberScalar)
	     {
		ins = (INumberScalar) elem;
	     }
	     else
	        if (elem instanceof IStringScalar)
	           iss = (IStringScalar) elem;
		else
	           if (elem instanceof IBooleanScalar)
	              ibs = (IBooleanScalar) elem;
		   else
	              if (elem instanceof IEnumScalar)
	        	 ies = (IEnumScalar) elem;
		
	     if ( (ins != null) || (iss != null) || (ibs != null) || (ies != null) ) // if attribute model found
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
		   ssViewer = (SimpleScalarViewer) elem;
		   ssViewer.clearModel();
		}
		else
		   if (elem instanceof BooleanScalarCheckBoxViewer)
		   {
		      bsViewer = (BooleanScalarCheckBoxViewer) elem;
		      bsViewer.clearModel();
		   }
		   else
		      if (elem instanceof SimpleEnumScalarViewer)
		      {
			 esViewer = (SimpleEnumScalarViewer) elem;
			 esViewer.clearModel();
		      }
		
		
		elem = scalarSetters.get(indRow);
		
		if (elem != null)
		{
		   if (elem instanceof NumberScalarWheelEditor)
		   {
		      wheelSetter = (NumberScalarWheelEditor) elem;
		      if (ins != null)
	        	  if (ins.isWritable())
		             wheelSetter.setModel(null);
		   }
		   else
		   {
		      if (elem instanceof NumberScalarComboEditor)
		      {
			 nComboSetter = (NumberScalarComboEditor) elem;
			 if (ins != null)
	        	     if (ins.isWritable())
		        	nComboSetter.setNumberModel(null);
		      }
		      else
		      {
			 if (elem instanceof StringScalarEditor)
			 {
			    stringSetter = (StringScalarEditor) elem;
			    if (iss != null)
	                	if (iss.isWritable())
				   stringSetter.setModel(null);
			 }
			 else
			 {
			    if (elem instanceof StringScalarComboEditor)
			    {
			       sComboSetter = (StringScalarComboEditor) elem;
			       if (iss != null)
	                	   if (iss.isWritable())
				      sComboSetter.setStringModel(null);
			    }
			    else
			    {
			       if (elem instanceof BooleanScalarComboEditor)
			       {
				  bComboSetter = (BooleanScalarComboEditor) elem;
				  if (ibs != null)
	                	      if (ibs.isWritable())
					 bComboSetter.setAttModel(null);
			       }
			       else
			       {
				  if (elem instanceof EnumScalarComboEditor)
				  {
				     eComboSetter = (EnumScalarComboEditor) elem;
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
       Object                          elem = null;
       LabelViewer                     scalarLabel=null;
       SimpleScalarViewer              ssViewer=null;
       BooleanScalarCheckBoxViewer     bsViewer=null;
       SimpleEnumScalarViewer          esViewer=null;
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
		      ssViewer = (SimpleScalarViewer) elem;
		      ssViewer.setFont(theFont);
		   }
		   else
		      if (elem instanceof BooleanScalarCheckBoxViewer)
		      {
			 bsViewer = (BooleanScalarCheckBoxViewer) elem;
			 bsViewer.setFont(theFont);
		      }
		      else
			 if (elem instanceof SimpleEnumScalarViewer)
			 {
			    esViewer = (SimpleEnumScalarViewer) elem;
			    esViewer.setFont(theFont);
			 }

	           elem = scalarSetters.get(indRow);
		   if (elem != null)
		   {
		      if (   (elem instanceof NumberScalarWheelEditor)
			  || (elem instanceof NumberScalarComboEditor)
			  || (elem instanceof StringScalarEditor)
			  || (elem instanceof StringScalarComboEditor)
			  || (elem instanceof BooleanScalarComboEditor)
			  || (elem instanceof EnumScalarComboEditor) )
		      {
			  setter = (JComponent) elem;
			  setter.setFont(theFont);
		      }
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
       Object                          elem = null;
       NumberScalarWheelEditor         wheelSetter=null;
       StringScalarEditor              stringSetter=null;
       JComponent                      setter = null;


       if (scalarSetters != null)
       {
	  nbRows = scalarSetters.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        elem = scalarSetters.get(indRow);
		if (elem != null)
		   if (   (elem instanceof NumberScalarWheelEditor)
		       || (elem instanceof NumberScalarComboEditor)
		       || (elem instanceof StringScalarEditor)
		       || (elem instanceof StringScalarComboEditor)
		       || (elem instanceof BooleanScalarComboEditor)
		       || (elem instanceof EnumScalarComboEditor) )
		   {
		       setter = (JComponent) elem;
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
		if (elem != null)
		   if (elem instanceof NumberScalarComboEditor)
		   {
		      setter = (NumberScalarComboEditor) elem;
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
    
    public void setCheckBoxStrings(IBooleanScalar ibs, String trueLabel, String falseLabel)
    {
    
       int                          ibsIndex=-1;
       Object                       elem=null;
       
              
       if (ibs == null)
          return;

       if (listModel == null)
	  return;

       ibsIndex = listModel.indexOf(ibs);
       if ( ibsIndex < 0)
          return;
	  
       
       elem = scalarViewers.get(ibsIndex);
       if (elem == null) return;
	  
       
       if ( !(elem instanceof BooleanScalarCheckBoxViewer) )
          return;
	  
       BooleanScalarCheckBoxViewer cb = (BooleanScalarCheckBoxViewer) elem;
       
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
	JComponent                      viewer=null;
	JComponent                      setter=null;
	JButton                         propertyButton=null;
	
	int                             maxRowElementHeight;
	int                             currH;
	int                             hMargin;
	boolean                         insHasValueList, issHasValueList;


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
	      else // should a BooleanScalarCheckBoxViewer or SimpleEnumScalarViewer
	      {
	         if (boolViewer != null)
		 {
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
		
	      currH = scalarLabel.getPreferredSize().height+2;
	      if (currH > maxRowElementHeight)
	         maxRowElementHeight = currH;
		 
	      currH = viewer.getPreferredSize().height+2;
	      if (currH > maxRowElementHeight)
	         maxRowElementHeight = currH;
		 	 
	      if (setter != null)
	      {
		 currH = setter.getPreferredSize().height+2;
		 if (currH > maxRowElementHeight)
	            maxRowElementHeight = currH;
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

	      viewerRow++;
	   }
	}
	

	nbScalarViewers = scalarViewers.size();
	for (idx=0; idx < nbScalarViewers; idx++)
	{
	    elem = scalarViewers.get(idx);
	    if (elem instanceof SimpleScalarViewer)
	    {
	       ssViewer = (SimpleScalarViewer) elem;
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
	       if (elem instanceof SimpleEnumScalarViewer)
	       {
		  enumViewer = (SimpleEnumScalarViewer) elem;
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

	    elem = scalarSetters.get(idx);
	    if (elem != null)
	       if (elem instanceof StringScalarEditor)
	       {
		  stringSetter = (StringScalarEditor) elem;
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
	
    }
    
    
    private void propertyButtonActionPerformed (java.awt.event.ActionEvent evt)
    {
        int              buttonIndex=-1;
	int              ind, nbButtons;
	Object           elem;
	JButton          propertyButton;
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
	   elem = listModel.get(buttonIndex);
	   if (elem instanceof INumberScalar)
	      ins = (INumberScalar) elem;
	   else
	      if (elem instanceof IStringScalar)
	         iss = (IStringScalar) elem;
	      else
		 if (elem instanceof IBooleanScalar)
	            ibs = (IBooleanScalar) elem;
		 else
		    if (elem instanceof IEnumScalar)
	               ies = (IEnumScalar) elem;
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

       mainFrame.show();
       
    } // end of main ()
        
}
