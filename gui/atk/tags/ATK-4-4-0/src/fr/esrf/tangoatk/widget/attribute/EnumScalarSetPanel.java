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
 
// File:          EnumScalarSetPanel.java
// Created:       2007-05-31 15:18:20, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.attribute;



import javax.swing.*;


import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.properties.LabelViewer;


/** A EnumScalarSetPanel is a Swing JPanel which displays the "read value" of 
 * the EnumScalar together with the corresponding EnumScalarEditor. At the left side the 
 * label of the attribute is displayed. The label is optional.
 *
 */
public class EnumScalarSetPanel extends JPanel
{
    
    private LabelViewer                  attLabelViewer;
    private SimpleEnumScalarViewer       attEnumScalarViewer;
    private EnumScalarComboEditor        attEnumScalarEditor;

    private IEnumScalar          enumAtt=null;
    
    private boolean              labelVisible=true;
    private boolean              unitVisible=true;
    

    public EnumScalarSetPanel()
    {
        initComponents();
    }
    
    //override setFont()
    public void setFont(java.awt.Font font)
    {
       super.setFont(font);
       if (attEnumScalarEditor != null) attEnumScalarEditor.setFont(font);
       if (attEnumScalarViewer != null) attEnumScalarViewer.setFont(font);
       if (attLabelViewer != null) attLabelViewer.setFont(font);
    }
    
    //override setBackground()
    public void setBackground(java.awt.Color bg)
    {
       super.setBackground(bg);
       if (attEnumScalarEditor != null) attEnumScalarEditor.setBackground(bg);
       if (attEnumScalarViewer != null) attEnumScalarViewer.setBackground(bg);
       if (attLabelViewer != null) attLabelViewer.setBackground(bg);
    }
    
    public void setAttModel(IEnumScalar ies)
    {
	if (enumAtt != null)
	   clearModel();
	if (ies == null) return;
	if (!ies.isWritable()) return;
	
	enumAtt = ies;
	
	attLabelViewer.setModel(enumAtt);
	attEnumScalarViewer.setModel(enumAtt);
	attEnumScalarViewer.setToolTipText(enumAtt.getName());
	attEnumScalarEditor.setEnumModel(enumAtt);
    }
    
    public IEnumScalar getAttModel()
    {
        return enumAtt;
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
        if (enumAtt == null) return;
	
	attLabelViewer.setModel(null);
	attEnumScalarViewer.clearModel();
	attEnumScalarViewer.setToolTipText(null);
	attEnumScalarEditor.setEnumModel(null);
	
	enumAtt=null;
    }
    
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        attEnumScalarEditor = new EnumScalarComboEditor();
        attLabelViewer = new LabelViewer();
        attEnumScalarViewer = new SimpleEnumScalarViewer();

        setLayout(new java.awt.GridBagLayout());


        attLabelViewer.setFont(getFont());
        attLabelViewer.setBackground(getBackground());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(attLabelViewer, gridBagConstraints);

        attEnumScalarViewer.setBackgroundColor(getBackground());
        attEnumScalarViewer.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        attEnumScalarViewer.setFont(getFont());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(attEnumScalarViewer, gridBagConstraints);

        attEnumScalarEditor.setFont(getFont());
	attEnumScalarEditor.setBackground(getBackground());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(attEnumScalarEditor, gridBagConstraints);
    }
}
