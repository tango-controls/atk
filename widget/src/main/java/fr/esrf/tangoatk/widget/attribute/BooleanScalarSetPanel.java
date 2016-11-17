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
 
// File:          BooleanScalarSetPanel.java
// Created:       2007-05-31 17:35:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.attribute;



import javax.swing.*;


import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.properties.LabelViewer;


/** A BooleanScalarSetPanel is a Swing JPanel which displays the "read value" of 
 * the boolean scalar attribute with a booleanCheckboxViewer and the setPoint
 * of the boolean scalar attribute with a booleanComboEditor which is used to set
 * the attribute. At the left side the label of the attribute is displayed. The label is optional.
 *
 */
public class BooleanScalarSetPanel extends JPanel
{
    
    private LabelViewer                     attLabelViewer;
    private BooleanScalarCheckBoxViewer     attBooleanScalarViewer;
    private BooleanScalarComboEditor        attBooleanScalarEditor;

    private IBooleanScalar          boolAtt=null;
    
    private boolean                 labelVisible=true;
    private boolean                 unitVisible=true;
    

    public BooleanScalarSetPanel()
    {
        initComponents();
    }
    
    //override setFont()
    public void setFont(java.awt.Font font)
    {
       super.setFont(font);
       if (attBooleanScalarEditor != null) attBooleanScalarEditor.setFont(font);
       if (attBooleanScalarViewer != null) attBooleanScalarViewer.setFont(font);
       if (attLabelViewer != null) attLabelViewer.setFont(font);
    }
    
    //override setBackground()
    public void setBackground(java.awt.Color bg)
    {
       super.setBackground(bg);
       if (attBooleanScalarEditor != null) attBooleanScalarEditor.setBackground(bg);
       if (attBooleanScalarViewer != null) attBooleanScalarViewer.setBackground(bg);
       if (attLabelViewer != null) attLabelViewer.setBackground(bg);
    }
    
    public void setAttModel(IBooleanScalar ibs)
    {
	if (boolAtt != null)
	   clearModel();
	if (ibs == null) return;
	if (!ibs.isWritable()) return;
	
	boolAtt = ibs;
	
	attLabelViewer.setModel(boolAtt);
	attBooleanScalarViewer.setAttModel(boolAtt);
	//attBooleanScalarViewer.setToolTipText(boolAtt.getName()); -> attBooleanScalarViewer.setHasToolTip(true);
	attBooleanScalarEditor.setAttModel(boolAtt);
    }
    
    public IBooleanScalar getAttModel()
    {
        return boolAtt;
    }
     
    public void setLabelVisible(boolean lv)
    {
       if (lv == labelVisible) return;
       
       labelVisible = lv;
       attLabelViewer.setVisible(labelVisible);
       revalidate();
    }
    
    public boolean getLabelVisible()
    {
        return labelVisible;
    }
      
    public void setUnitVisible(boolean uv)
    {
       unitVisible = uv;
    }
    
    public boolean getUnitVisible()
    {
        return unitVisible;
    }
  
    public void clearModel()
    {
        if (boolAtt == null) return;
	
	attLabelViewer.setModel(null);
	attBooleanScalarViewer.clearModel();
	//attBooleanScalarViewer.setToolTipText(null); -> attBooleanScalarViewer.setHasToolTip(true);
	attBooleanScalarEditor.clearModel();
	
	boolAtt=null;
    }
    
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        attBooleanScalarEditor = new BooleanScalarComboEditor();
        attLabelViewer = new LabelViewer();
        attBooleanScalarViewer = new BooleanScalarCheckBoxViewer();

        setLayout(new java.awt.GridBagLayout());


        attLabelViewer.setFont(getFont());
        attLabelViewer.setBackground(getBackground());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(attLabelViewer, gridBagConstraints);

	attBooleanScalarViewer.setTrueLabel(new String());
	attBooleanScalarViewer.setFalseLabel(new String());
	attBooleanScalarViewer.setHasToolTip(true);
	attBooleanScalarViewer.setBackground(getBackground());
        attBooleanScalarViewer.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        attBooleanScalarViewer.setFont(getFont());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(attBooleanScalarViewer, gridBagConstraints);

        attBooleanScalarEditor.setFont(getFont());
	attBooleanScalarEditor.setBackground(getBackground());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(attBooleanScalarEditor, gridBagConstraints);
    }
}
