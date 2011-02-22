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
 
//+============================================================================
//Source: package fr.esrf.tangoatk.widget.attribute;/SimpleScalarAttributeWriterViewer.java
//
//project :     GlobalscreenProject
//
//Description: This class hides
//
//Author: ho
//
//Revision: 1.1
//
//Log:
//
//copyleft :Synchrotron SOLEIL
//			L'Orme des Merisiers
//			Saint-Aubin - BP 48
//			91192 GIF-sur-YVETTE CEDEX
//			FRANCE
//
//+============================================================================
package fr.esrf.tangoatk.widget.attribute;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.esrf.tangoatk.core.AttributeSetException;
import fr.esrf.tangoatk.core.IAttribute;
import fr.esrf.tangoatk.core.IBooleanScalar;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.core.IStringScalar;

/**
 * @author ho
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ScalarInput extends JPanel implements ActionListener{
    
    private static final long serialVersionUID = 1L;
    private String textButton = "Write";
    private boolean buttonEnabled = true;
    private boolean valueEditable = true;
    
    private Color backgroundButton = Color.GRAY;
    private Color backgroundField = Color.WHITE;
    
    
    private IAttribute scalarModel = null;
    private JTextField attributeTextField = new JTextField();
    private JButton writeButton =new JButton();
    
    /**
     * 
     */
    public ScalarInput() {
        super();

        attributeTextField.setPreferredSize(new Dimension(100, 20));
        //attributeTextField.setMinimumSize(new Dimension(30, 20));
        attributeTextField.setEditable(valueEditable);
        attributeTextField.setBackground(backgroundField);
        attributeTextField.addActionListener(this);
        add(attributeTextField,BorderLayout.WEST);
        
        writeButton.setText(textButton);
        writeButton.setEnabled(buttonEnabled);
        writeButton.setBackground(backgroundButton);
        writeButton.addActionListener(this);
        add(writeButton,BorderLayout.EAST);
   }
    
    public IAttribute getScalarModel() {
        return scalarModel;
    }
    
    public void setScalarModel(IAttribute scalarModel) {
        this.scalarModel = scalarModel;
        if(scalarModel == null)
            return;
       
        if(!scalarModel.isWritable())
        {
            setValueEditable(false);
            setButtonEnabled(false);
        }
    }
    
    public void clearModel()
    {
        scalarModel = null;
        textButton = "Write";
        attributeTextField.setText("");
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        if(scalarModel == null || arg0.getSource() instanceof JTextField)
            return;
        
        try
        {
            if(scalarModel instanceof INumberScalar)
                ((INumberScalar)scalarModel).setValue(getValue());
            else if(scalarModel instanceof IStringScalar)
                ((IStringScalar)scalarModel).setValue(attributeTextField.getText());
            else if(scalarModel instanceof IBooleanScalar)
                ((IBooleanScalar)scalarModel).setValue(getBooleanValue());
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "Cannot write attribute because " + e.getMessage());
        }
    }
    
   private double getValue()
   {
       String tmpStringValue = attributeTextField.getText();
       double tmpDoubleValue = 0;
       try
       {
           tmpDoubleValue = Double.parseDouble(tmpStringValue);
       }
       catch (NumberFormatException e) {}
       return tmpDoubleValue;
   }
   
   private boolean getBooleanValue()
   {
       String tmpStringValue = attributeTextField.getText();
       if(getValue() == 1)
           tmpStringValue = "true";       
       return Boolean.parseBoolean(tmpStringValue);
   }
    
    public boolean isButtonEnabled() {
        return buttonEnabled;
    }
    public void setButtonEnabled(boolean buttonEnabled) {
        this.buttonEnabled = buttonEnabled;
        writeButton.setEnabled(buttonEnabled);
    }
    public String getTextButton() {
        return textButton;
    }
    public void setTextButton(String textButton) {
        this.textButton = textButton;
        writeButton.setText(textButton);
    }
    
    public boolean isValueEditable() {
        return valueEditable;
    }
    public void setValueEditable(boolean valueEditable) {
        this.valueEditable = valueEditable;
        attributeTextField.setEditable(valueEditable);
    }
    
    public Color getBackgroundButton() {
        return backgroundButton;
    }
    
    public void setBackgroundButton(Color backgroundButton) {
        if(backgroundButton == null)
            return;
        this.backgroundButton = backgroundButton;
        writeButton.setBackground(backgroundButton);
    }
    
    public Color getBackgroundField() {
        return backgroundField;
    }
    
    public void setBackgroundField(Color backgroundField) {
        if(backgroundField == null)
            return;
        this.backgroundField = backgroundField;
        attributeTextField.setBackground(backgroundField);
    }
    
    public void setFont(Font arg0) {
        if(arg0 == null || writeButton == null || attributeTextField == null)
            return;
        writeButton.setFont(arg0);
        attributeTextField.setFont(arg0);
        super.setFont(arg0);
    }
    
    public void setNumberValue(double avalue)
    {
        if(!attributeTextField.hasFocus())
            attributeTextField.setText(Double.toString(avalue));
    }
    
    public void setStringValue(String avalue)
    {
        if(!attributeTextField.hasFocus())
            attributeTextField.setText(avalue);
    }
    
    public void setBooleanValue(boolean avalue)
    {
        if(!attributeTextField.hasFocus())
            attributeTextField.setText(Boolean.toString(avalue));
    }
   
    
    public void writeNumberValue(double avalue)
    {
        attributeTextField.setText(Double.toString(avalue));
        if(scalarModel!= null && scalarModel.isWritable() && scalarModel instanceof INumberScalar)
            ((INumberScalar)scalarModel).setValue(avalue);
    }
    
    public void writeStringValue(String avalue)
    {
        attributeTextField.setText(avalue);
        if(scalarModel!= null && scalarModel.isWritable() && scalarModel instanceof IStringScalar)
            try {
                ((IStringScalar)scalarModel).setValue(avalue);
            }
        	catch (AttributeSetException e) {
                e.printStackTrace();
            }
    }
    
    public String getText()
    {
        return attributeTextField.getText();
    }
    
    
    public void writeBooleanValue(boolean avalue)
    {
        attributeTextField.setText(Boolean.toString(avalue));
        if(scalarModel!= null && scalarModel.isWritable() && scalarModel  instanceof IBooleanScalar)
            ((IBooleanScalar)scalarModel).setValue(avalue);
    }

    /**
     * Main class, so you can have an example.
     * You can monitor your own attribute by giving its full path name in argument
     */
    static public void main(String args[])
    {
        fr.esrf.tangoatk.core.AttributeList attributeList = new fr.esrf.tangoatk.core.AttributeList();
        ScalarInput si = new ScalarInput();
        try
	{
            if (args.length!=0)
	    {
                si.setScalarModel((IAttribute)attributeList.add(args[0]));
            }
            else
	    {
                si.setScalarModel((IAttribute)attributeList.add("LT1/AE/CH.1/current"));
            }
            attributeList.setRefreshInterval(1000);
            attributeList.startRefresher();
        }
        catch (Exception e)
	{
            e.printStackTrace();
            System.exit(1);
        }
        JFrame f = new JFrame();
        f.getContentPane().add(si);
        f.pack();
        f.setVisible(true);
    }
    
}
