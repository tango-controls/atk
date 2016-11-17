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
 
// File:          StringScalarSetPanel.java
// Created:       2007-05-31 11:26:12, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.attribute;



import javax.swing.*;


import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.properties.LabelViewer;


/** A StringScalarSetPanel is a Swing JPanel which displays the "read value" of 
 * the StringScalar below a StringScalarEditor. At the left side the label of
 * the attribute is displayed. The label is optional.
 *
 */
public class StringScalarSetPanel extends JPanel
{
    
    private LabelViewer                  attLabelViewer;
    private SimpleScalarViewer           attStringScalarViewer;
    private StringScalarEditor           attStringScalarEditor;


    private IStringScalar        stringAtt=null;
    
    private boolean              labelVisible=true;
    private boolean              unitVisible=true;
    

    public StringScalarSetPanel()
    {
        initComponents();
    }
    
    //override setFont()
    public void setFont(java.awt.Font font)
    {
       super.setFont(font);
       if (attStringScalarEditor != null) attStringScalarEditor.setFont(font);
       if (attStringScalarViewer != null) attStringScalarViewer.setFont(font);
       if (attLabelViewer != null) attLabelViewer.setFont(font);
    }
    
    //override setBackground()
    public void setBackground(java.awt.Color bg)
    {
       super.setBackground(bg);
       //if (attStringScalarEditor != null) attStringScalarEditor.setBackground(bg);
       if (attStringScalarViewer != null) attStringScalarViewer.setBackground(bg);
       if (attLabelViewer != null) attLabelViewer.setBackground(bg);
    }
    
    public void setAttModel(IStringScalar iss)
    {
	if (stringAtt != null)
	   clearModel();
	if (iss == null) return;
	if (!iss.isWritable()) return;
	
	stringAtt = iss;
	
	attLabelViewer.setModel(stringAtt);
	attStringScalarViewer.setModel(stringAtt);
	//attStringScalarViewer.setToolTipText(stringAtt.getName()); -> attStringScalarViewer.setHasToolTip(true);
	attStringScalarEditor.setModel(stringAtt);
    }
    
    public IStringScalar getAttModel()
    {
        return stringAtt;
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
        if (stringAtt == null) return;
	
	attLabelViewer.setModel(null);
	attStringScalarViewer.clearModel();
	//attStringScalarViewer.setToolTipText(null); -> attStringScalarViewer.setHasToolTip(true);
	attStringScalarEditor.setModel(null);
	
	stringAtt=null;
    }
    
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        attStringScalarEditor = new StringScalarEditor();
        attLabelViewer = new LabelViewer();
        attStringScalarViewer = new SimpleScalarViewer();

        setLayout(new java.awt.GridBagLayout());

        attStringScalarEditor.setFont(getFont());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
	gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(attStringScalarEditor, gridBagConstraints);
	
        attLabelViewer.setFont(getFont());
        attLabelViewer.setBackground(getBackground());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(attLabelViewer, gridBagConstraints);

        attStringScalarViewer.setBackgroundColor(getBackground());
        attStringScalarViewer.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        attStringScalarViewer.setFont(getFont());
	attStringScalarViewer.setHasToolTip(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(attStringScalarViewer, gridBagConstraints);

/*
        attLabelViewer.setFont(getFont());
        attLabelViewer.setBackground(getBackground());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(attLabelViewer, gridBagConstraints);

        attStringScalarViewer.setBackgroundColor(getBackground());
        attStringScalarViewer.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        attStringScalarViewer.setFont(getFont());
	attStringScalarViewer.setHasToolTip(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(attStringScalarViewer, gridBagConstraints);

        attStringScalarEditor.setFont(getFont());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(attStringScalarEditor, gridBagConstraints);*/
    }
}
