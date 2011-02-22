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

    private Vector                listModel;
    private Vector                scalarLabels, scalarViewers, scalarSetters, scalarPropButtons;
    private SimplePropertyFrame   propFrame=null;
        

    /* The bean properties */
    private java.awt.Font    theFont;
    private boolean          labelVisible;
    private boolean          setterVisible;
    private boolean          propertyButtonVisible;
    private boolean          propertyListEditable;
    

    /** Creates new form ScalarListViewer */
    public ScalarListViewer()
    {
        listModel = null;
	scalarLabels = null;
	scalarViewers = null;
	scalarSetters = null;
	scalarPropButtons = null;
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
	        ||  (elem instanceof IStringScalar) )
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
    
    
    
    
    private void removeComponents()
    {
       int                             indRow, nbRows;
       Object                          elem = null;
       INumberScalar                   ins;
       IStringScalar                   iss;
       LabelViewer                     scalarLabel=null;
       SimpleScalarViewer              viewer=null;
       NumberScalarWheelEditor         wheelSetter=null;
       StringScalarEditor              stringSetter=null;
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
		   if (ins != null)
	               if (ins.isWritable())
		          wheelSetter.setModel(null);
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
		   if (elem instanceof NumberScalarWheelEditor)
		   {
		      wheelSetter = (NumberScalarWheelEditor) elem;
		      wheelSetter.setFont(theFont);
		   }
		   else
		   {
		      if (elem instanceof StringScalarEditor)
		      {
			 stringSetter = (StringScalarEditor) elem;
			 stringSetter.setFont(theFont);
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
		   wheelSetter = (NumberScalarWheelEditor) elem;
		   wheelSetter.setVisible(setterVisible);
		}
		else
		{
		   if (elem instanceof StringScalarEditor)
		   {
		      stringSetter = (StringScalarEditor) elem;
		      stringSetter.setVisible(setterVisible);
		   }
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
	StringScalarEditor              stringSetter=null;
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
	   stringSetter = null;
           propertyButton = null;
	   
	   elem = scalarList.getElementAt(idx);
	   if ( (elem instanceof INumberScalar) || (elem instanceof IStringScalar) )
	   {
	      ins = null;
	      iss = null;
	      // Create setter
	      if (elem instanceof INumberScalar)
	      {
	         ins = (INumberScalar) elem;
        	 wheelSetter = new NumberScalarWheelEditor();
	      }
	      else
	      {
	         iss = (IStringScalar) elem;
        	 stringSetter = new StringScalarEditor();
	      }
	      
              scalarLabel = new LabelViewer();
              viewer = new SimpleScalarViewer();
              propertyButton = new javax.swing.JButton();

	      // Set the Label Viewer properties
	      scalarLabel.setFont(theFont);
	      scalarLabel.setHorizontalAlignment(JSmoothLabel.RIGHT_ALIGNMENT);
	      scalarLabel.setBackground(getBackground());
	      scalarLabel.setValueOffsets(0, -5);
	      if (labelVisible)
		 scalarLabel.setVisible(true);
	      else
		 scalarLabel.setVisible(false);
          
	      if (ins != null)
	         scalarLabel.setModel(ins);
	      else
	         scalarLabel.setModel(iss);
	      

	      // Set the Setter properties
	      if (ins != null)
	      {
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
	      }
	      else
	      {
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
	      }
	 
	 

	      // Set the Viewer properties
              viewer.setFont(theFont);
              viewer.setBackgroundColor(getBackground());
              viewer.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
	      viewer.setAlarmEnabled(true);
	      viewer.setValueOffsets(0, -5);
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
		    
	      
	      // to enable the viewers to be correctly sized!
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
		 
	      if (ins != null)
	         currH = wheelSetter.getPreferredSize().height;
	      else
	         currH = stringSetter.getPreferredSize().height;
		 
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
                 add(wheelSetter, gridBagConstraints);
	      }
	      else
	      {
        	 gridBagConstraints.insets = new java.awt.Insets(1,3,1,1);
                 add(stringSetter, gridBagConstraints);
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
	      if (ins != null)
	         scalarSetters.add(wheelSetter);
	      else
	         scalarSetters.add(stringSetter);
	      
	      viewerRow++;
	   }
	}
	

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
	     System.out.println("ScalarListViewer : propertyButtonActionPerformed : Caught exception  "+e.getMessage());
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
       ScalarListViewer               scalarlv = new ScalarListViewer();
       INumberScalar                        attn;
       IStringScalar                        attstr;
       JFrame                               mainFrame;
       

       //scalarlv.setBackground(java.awt.Color.white);
       //scalarlv.setForeground(java.awt.Color.black);

       // Connect to a list of number scalar attributes
       try
       {
          //attList.setPolled(true);
	  attn = (INumberScalar) attList.add("jlp/test/1/att_un");
          attn = (INumberScalar) attList.add("jlp/test/1/att_deux");
          attn = (INumberScalar) attList.add("jlp/test/1/att_trois");
          attn = (INumberScalar) attList.add("jlp/test/1/att_quatre");
          attstr = (IStringScalar) attList.add("jlp/test/1/att_cinq");
          attn = (INumberScalar) attList.add("jlp/test/1/att_six");
	  //attn = (INumberScalar) attList.add("dev/test/10/Short_attr_w");
	  //attn = (INumberScalar) attList.add("dev/test/10/Double_attr_w");
	  //attstr = (IStringScalar) attList.add("dev/test/10/String_attr");
	  //scalarlv.setTheFont(new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 18));
          //scalarlv.setLabelVisible(false);
          //scalarlv.setSetterVisible(false);
          //scalarlv.setPropertyButtonVisible(false);
	  scalarlv.setModel(attList);
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
				     

       mainFrame.setContentPane(scalarlv);
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
       
       //scalarlv.setTheFont(new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 18));
       //scalarlv.setLabelVisible(false);
       //scalarlv.setSetterVisible(false);
       //scalarlv.setPropertyButtonVisible(true);
       //mainFrame.pack();
       
    } // end of main ()
        
}
