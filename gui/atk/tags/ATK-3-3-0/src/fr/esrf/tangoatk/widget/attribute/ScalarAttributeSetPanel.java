// File:          ScalarAttributeSetPanel.java
// Created:       2007-05-31 14:32:58, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.attribute;



import javax.swing.*;


import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.properties.LabelViewer;
import fr.esrf.tangoatk.widget.properties.UnitViewer;
import java.awt.Component;


/** A ScalarAttributeSetPanel is a Swing JPanel which displays the "read value" of 
 *  a scalar attribute together with it's corresponding setter (or editor).
 *  At the left side the label of the attribute is displayed and at the right side
 *  the unit is displayed (when relevant). The label and the unit are optional.
 *  The ScalarAttributeSetPanel selects an adapted setPanel according to the type of
 *  it's model (INumberScalar, IStringScalar).
 *
 */
public class ScalarAttributeSetPanel extends JPanel
{
    
    private NumberScalarSetPanel         numberSetPanel;
    private StringScalarSetPanel         stringSetPanel;
    private EnumScalarSetPanel           enumSetPanel;
    private BooleanScalarSetPanel        booleanSetPanel;



    private IAttribute                   attModel=null;
    
    private boolean                      labelVisible=true;
    private boolean                      unitVisible=true;
    

    public ScalarAttributeSetPanel()
    {
        initComponents();
	numberSetPanel.setVisible(false);
	stringSetPanel.setVisible(false);
	enumSetPanel.setVisible(false);
	booleanSetPanel.setVisible(false);
    }
    
    //override setFont()
    public void setFont(java.awt.Font font)
    {
	super.setFont(font);
	
	if (numberSetPanel != null)
	    numberSetPanel.setFont(font);
	if (stringSetPanel != null)
	    stringSetPanel.setFont(font);
	if (enumSetPanel != null)
	    enumSetPanel.setFont(font);
	if (booleanSetPanel != null)
	    booleanSetPanel.setFont(font);
    }
    
    //override setBackground()
    public void setBackground(java.awt.Color bg)
    {
	super.setBackground(bg);
	
	if (numberSetPanel != null)
	    numberSetPanel.setBackground(bg);
	if (stringSetPanel != null)
	    stringSetPanel.setBackground(bg);
	if (enumSetPanel != null)
	    enumSetPanel.setBackground(bg);
	if (booleanSetPanel != null)
	    booleanSetPanel.setBackground(bg);
    }
    
    public void setAttModel(IAttribute iatt)
    {
	if (attModel != null)
	   clearModel();
	if (iatt == null) return;
	
	if (   !(iatt instanceof INumberScalar)
	    && !(iatt instanceof IStringScalar)
	    && !(iatt instanceof IEnumScalar)
	    && !(iatt instanceof IBooleanScalar) )
	   return;
	   
	if (!iatt.isWritable()) return;
	
	attModel = iatt;
	
	if (attModel instanceof INumberScalar)
	{
	   numberSetPanel.setAttModel( (INumberScalar) attModel);
	   numberSetPanel.setVisible(true);
	   revalidate();
	   return;
	}
	
	if (attModel instanceof IStringScalar)
	{
	   stringSetPanel.setAttModel( (IStringScalar) attModel);
	   stringSetPanel.setVisible(true);
	   revalidate();
	   return;
	}
	
	if (attModel instanceof IEnumScalar)
	{
	   enumSetPanel.setAttModel( (IEnumScalar) attModel);
	   enumSetPanel.setVisible(true);
	   revalidate();
	   return;
	}
	
	if (attModel instanceof IBooleanScalar)
	{
	   booleanSetPanel.setAttModel( (IBooleanScalar) attModel);
	   booleanSetPanel.setVisible(true);
	   revalidate();
	   return;
	}
    }
    
    public IAttribute getAttModel()
    {
        return attModel;
    }
     
    public void setLabelVisible(boolean lv)
    {
       if (lv == labelVisible) return;
       
       labelVisible = lv;

       numberSetPanel.setLabelVisible(labelVisible);
       stringSetPanel.setLabelVisible(labelVisible);
       enumSetPanel.setLabelVisible(labelVisible);
       booleanSetPanel.setLabelVisible(labelVisible);
    }
    
    public boolean getLabelVisible()
    {
        return labelVisible;
    }
      
    public void setUnitVisible(boolean uv)
    {
       if (uv == unitVisible) return;
       
       unitVisible = uv;

       numberSetPanel.setUnitVisible(unitVisible);
    }
    
    public boolean getUnitVisible()
    {
        return unitVisible;
    }
  
    public void clearModel()
    {
        if (attModel == null) return;
	
	numberSetPanel.setVisible(false);
	stringSetPanel.setVisible(false);
	enumSetPanel.setVisible(false);
	booleanSetPanel.setVisible(false);
	
	numberSetPanel.clearModel();
	stringSetPanel.clearModel();
	enumSetPanel.clearModel();
	booleanSetPanel.clearModel();
	
	attModel=null;
    }
    
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        
	numberSetPanel = new NumberScalarSetPanel();
        stringSetPanel = new StringScalarSetPanel();
        enumSetPanel = new EnumScalarSetPanel();
        booleanSetPanel = new BooleanScalarSetPanel();

        setLayout(new java.awt.GridBagLayout());


        numberSetPanel.setFont(getFont());
        numberSetPanel.setBackground(getBackground());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
	gridBagConstraints.weightx = 1.0;
	gridBagConstraints.weighty = 1.0;
        add(numberSetPanel, gridBagConstraints);


        stringSetPanel.setFont(getFont());
        stringSetPanel.setBackground(getBackground());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
	gridBagConstraints.weightx = 1.0;
	gridBagConstraints.weighty = 1.0;
        add(stringSetPanel, gridBagConstraints);


        enumSetPanel.setFont(getFont());
        enumSetPanel.setBackground(getBackground());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
	gridBagConstraints.weightx = 1.0;
	gridBagConstraints.weighty = 1.0;
        add(enumSetPanel, gridBagConstraints);


        booleanSetPanel.setFont(getFont());
        booleanSetPanel.setBackground(getBackground());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
	gridBagConstraints.weightx = 1.0;
	gridBagConstraints.weighty = 1.0;
        add(booleanSetPanel, gridBagConstraints);
    }
}
