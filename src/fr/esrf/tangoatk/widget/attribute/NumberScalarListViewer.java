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

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.attribute.NumberScalarWheelEditor;
import fr.esrf.tangoatk.widget.attribute.SimpleScalarViewer;
import fr.esrf.tangoatk.widget.util.JSmoothLabel;
import fr.esrf.tangoatk.widget.util.JAutoScrolledText;
import fr.esrf.tangoatk.widget.util.JAutoScrolledTextListener;
import fr.esrf.tangoatk.widget.properties.LabelViewer;
import fr.esrf.tangoatk.widget.attribute.SimplePropertyFrame;

public class NumberScalarListViewer extends javax.swing.JPanel
             implements JAutoScrolledTextListener
{
    public static final String      DEFAULT_SETTER = "WheelEditor";
    public static final String      COMBO_SETTER = "ComboEditor";

    private Vector                listModel;
    private Vector                nsLabels, nsViewers, nsSetters, nsPropButtons;
    private SimplePropertyFrame   propFrame=null;
        

    /* The bean properties */
    private java.awt.Font    theFont;
    private boolean          labelVisible;
    private boolean          setterVisible;
    private boolean          propertyButtonVisible;
    private boolean          propertyListEditable;
    private boolean          unitVisible;
    private String           setterType;
    

    /** Creates new form NumberScalarListViewer */
    public NumberScalarListViewer()
    {
        listModel = null;
	nsLabels = null;
	nsViewers = null;
	nsSetters = null;
	nsPropButtons = null;
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
    
    
    private void removeComponents()
    {
       int                             indRow, nbRows;
       Object                          elem = null;
       INumberScalar                   ins = null;
       LabelViewer                     nsLabel=null;
       SimpleScalarViewer              viewer=null;
       NumberScalarWheelEditor         setter=null;
       NumberScalarComboEditor         comboSetter=null;
       JButton                         propertyButton=null;


       propFrame = null;
       propFrame = new SimplePropertyFrame();
       
       nbRows = listModel.size();
       for (indRow=0; indRow < nbRows; indRow++)
       {
	  try
	  {
	     elem = listModel.get(indRow);
	     if (elem instanceof INumberScalar)
	     {
		ins = (INumberScalar) elem;
	     }
	     else
	        ins = null;
		
	     if (ins != null) // remove this model from all viewers
	     {
	        elem = nsLabels.get(indRow);
		if (elem instanceof LabelViewer)
		{
		   nsLabel = (LabelViewer) elem;
		   nsLabel.setModel(null);
		}
		elem = nsViewers.get(indRow);
		if (elem instanceof SimpleScalarViewer)
		{
		   viewer = (SimpleScalarViewer) elem;
		   viewer.clearModel();
		}
		
		elem = nsSetters.get(indRow);
		if (elem instanceof NumberScalarWheelEditor)
		{
		   setter = (NumberScalarWheelEditor) elem;
	           if (ins.isWritable())
		      setter.setModel(null);
		}
		else
		   if (elem instanceof NumberScalarComboEditor)
		   {
		      comboSetter = (NumberScalarComboEditor) elem;
	              if (ins.isWritable())
			 comboSetter.setNumberModel(null);
		   }
	     
	        /*elem = nsLabels.get(indRow);
		if (elem instanceof LabelViewer)
		{
		   nsLabel = (LabelViewer) elem;
                   ins.getProperty("label").removePresentationListener(nsLabel);
		}
		elem = nsViewers.get(indRow);
		if (elem instanceof SimpleScalarViewer)
		{
		   viewer = (SimpleScalarViewer) elem;
		   ins.removeNumberScalarListener(viewer);
		}
		
		elem = nsSetters.get(indRow);
		if (elem instanceof NumberScalarWheelEditor)
		{
		   setter = (NumberScalarWheelEditor) elem;
	           if (ins.isWritable())
		      ins.removeNumberScalarListener(setter);
		}*/
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
       Object                          elem = null;
       LabelViewer                     nsLabel=null;
       SimpleScalarViewer              viewer=null;
       NumberScalarWheelEditor         setter=null;
       NumberScalarComboEditor         comboSetter=null;
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
	           elem = nsLabels.get(indRow);
		   if (elem instanceof LabelViewer)
		   {
		      nsLabel = (LabelViewer) elem;
		      nsLabel.setFont(theFont);
		   }

	           elem = nsViewers.get(indRow);
		   if (elem instanceof SimpleScalarViewer)
		   {
		      viewer = (SimpleScalarViewer) elem;
		      viewer.setFont(theFont);
		   }

	           elem = nsSetters.get(indRow);
		   if (elem instanceof NumberScalarWheelEditor)
		   {
		      setter = (NumberScalarWheelEditor) elem;
		      setter.setFont(theFont);
		   }
		   else
		      if (elem instanceof NumberScalarComboEditor)
		      {
			 comboSetter = (NumberScalarComboEditor) elem;
			 comboSetter.setFont(theFont);
		      }

	           elem = nsPropButtons.get(indRow);
		   if (elem instanceof JButton)
		   {
		      propertyButton = (JButton) elem;
		      propertyButton.setFont(theFont);
		   }
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
       Object                          elem = null;
       LabelViewer                     nsLabel=null;


       if (nsLabels != null)
       {
	  nbRows = nsLabels.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        elem = nsLabels.get(indRow);
		if (elem instanceof LabelViewer)
		{
		   nsLabel = (LabelViewer) elem;
		   nsLabel.setVisible(labelVisible);
		}
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
       Object                          elem = null;
       NumberScalarWheelEditor         setter=null;
       NumberScalarComboEditor         comboSetter=null;


       if (nsSetters != null)
       {
	  nbRows = nsSetters.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        elem = nsSetters.get(indRow);
		if (elem instanceof NumberScalarWheelEditor)
		{
		   setter = (NumberScalarWheelEditor) elem;
		   setter.setVisible(setterVisible);
		}
		else
		   if (elem instanceof NumberScalarComboEditor)
		   {
		      comboSetter = (NumberScalarComboEditor) elem;
		      comboSetter.setVisible(setterVisible);
		   }
	     }
	     catch (Exception e)
	     {
	       System.out.println("NumberScalarListViewer : changeSetterVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if nsSetters != null

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


       if (nsPropButtons != null)
       {
	  nbRows = nsPropButtons.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        elem = nsPropButtons.get(indRow);
		if (elem instanceof JButton)
		{
		   propertyButton = (JButton) elem;
		   propertyButton.setVisible(propertyButtonVisible);
		}
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
       Object                          elem = null;
       SimpleScalarViewer              viewer=null;


       if (nsViewers != null)
       {
	  nbRows = nsViewers.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        elem = nsViewers.get(indRow);
		if (elem instanceof SimpleScalarViewer)
		{
		   viewer = (SimpleScalarViewer) elem;
		   viewer.setUnitVisible(unitVisible);
		}
	     }
	     catch (Exception e)
	     {
	       System.out.println("NumberScalarListViewer : changeUnitVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if nsSetters != null

    }
     
     
     
    public String getSetterType()
    {
         return setterType;
    }
    
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
        java.awt.GridBagConstraints     gridBagConstraints;
	
	LabelViewer                     nsLabel=null;
	SimpleScalarViewer              viewer=null;
	NumberScalarWheelEditor         wheelSetter=null;
	NumberScalarComboEditor         comboSetter=null;
	JComponent                      setter=null;
	JButton                         propertyButton=null;
	
	int                             arrowHeight=0;


	listModel = new Vector();
	nsLabels = new Vector();
	nsViewers = new Vector();
	nsSetters = new Vector();
	nsPropButtons = new Vector();
	
	
	viewerRow = 0;
	nbAtts = scalarList.size();
	
	for (idx=0; idx < nbAtts; idx++)
	{
	   elem = scalarList.getElementAt(idx);
	   if (elem instanceof INumberScalar)
	   {
	      ins = (INumberScalar) elem;
	      
              nsLabel = new LabelViewer();
              viewer = new SimpleScalarViewer();
              propertyButton = new javax.swing.JButton();

	      nsLabel.setFont(theFont);
	      nsLabel.setHorizontalAlignment(JSmoothLabel.RIGHT_ALIGNMENT);
	      nsLabel.setBackground(getBackground());
	      nsLabel.setValueOffsets(0, -5);
	      nsLabel.setText(ins.getLabel());
	      if (labelVisible)
		 nsLabel.setVisible(true);
	      else
		 nsLabel.setVisible(false);
	      nsLabel.setModel(ins);
		      
	      if (setterType.equalsIgnoreCase(COMBO_SETTER))
	      {
        	   comboSetter = new NumberScalarComboEditor();
        	   comboSetter.setFont(theFont);
        	   comboSetter.setBackground(getBackground());
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
	      else // setterType = DEFAULT_SETTER
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
	      viewer.setValueOffsets(0, -5);
	      viewer.setModel(ins);
	      

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
	Object           elem;
	JButton          propertyButton;
	PropertyFrame    pf;
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
	      elem = nsPropButtons.get(ind);
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
	   elem = listModel.get(buttonIndex);
	   if (elem instanceof INumberScalar)
	      ins = (INumberScalar) elem;
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

	/* 
	pf = new PropertyFrame();
	pf.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
	pf.setSize(300, 400);
	pf.setEditable(propertyListEditable);
	pf.setModel(ins);
	pf.pack();
        pf.show();
	*/
	
    }


    
    public static void main(String[] args)
    {
       final fr.esrf.tangoatk.core.AttributeList  attList = new fr.esrf.tangoatk.core.AttributeList();
       NumberScalarListViewer               nslv = new NumberScalarListViewer();
       INumberScalar                        att;
       JFrame                               mainFrame;
       double[]                               vals = {0.1, 0.3, 1.0, 3.0, 10.0, 30.0, 100.0, 300.0};
       

       //nslv.setBackground(java.awt.Color.white);
       //nslv.setForeground(java.awt.Color.black);

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
          //nslv.setSetterType(NumberScalarListViewer.COMBO_SETTER);
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


/*
       try
       {
         Thread.sleep(4000);
       }
       catch(Exception e)
       {
       }
       */
       
       //nslv.setTheFont(new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 18));
       //nslv.setLabelVisible(false);
       //nslv.setSetterVisible(false);
       //nslv.setPropertyButtonVisible(true);
       //mainFrame.pack();
       
    } // end of main ()
        
}
