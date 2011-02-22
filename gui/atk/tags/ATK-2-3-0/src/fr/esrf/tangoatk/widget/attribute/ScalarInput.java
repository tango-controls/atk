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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JFrame;

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
    
    private String textLabel = "Input";
    private String textButton = "Write";
    private boolean buttonEnabled = true;
    private boolean valueEditable = true;
    private boolean unitVisible = true;
    
    private Color backgroundButton = Color.GRAY;
    private Color backgroundField = Color.WHITE;
    
    
    private IAttribute scalarModel = null;
    private JLabel attributeNameLabel;
    private JTextField attributeTextField;
    private JButton writeButton;
    
    /**
     * 
     */
    public ScalarInput() {
        super();
        
        attributeNameLabel = new JLabel();
        attributeTextField = new JTextField();
        writeButton = new JButton();
        
        setLayout(new GridBagLayout());
        
        attributeNameLabel.setText(textLabel);
        attributeNameLabel.setPreferredSize(new Dimension(200,17));
        attributeNameLabel.setMinimumSize(new Dimension(60,17));
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.anchor = 17;
        add(attributeNameLabel, gridbagconstraints);
        
        attributeTextField.setPreferredSize(new Dimension(100, 20));
        attributeTextField.setMinimumSize(new Dimension(30, 20));
        attributeTextField.setEditable(valueEditable);
        attributeTextField.setBackground(backgroundField);
        attributeTextField.addActionListener(this);
        gridbagconstraints.fill = 1;
        gridbagconstraints.weightx = 0.29999999999999999D;
        add(attributeTextField, gridbagconstraints);
        
        writeButton.setText(textButton);
        writeButton.setEnabled(buttonEnabled);
        writeButton.setBackground(backgroundButton);
        writeButton.addActionListener(this);
        
        gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.anchor = 13;
        add(writeButton, gridbagconstraints);
        
        setMinimumSize(new Dimension(200, 20));
        setPreferredSize(new Dimension(400, 20));
   }
    
    public IAttribute getScalarModel() {
        return scalarModel;
    }
    
    public void setScalarModel(IAttribute scalarModel) {
        this.scalarModel = scalarModel;
        if(scalarModel == null)
            return;
        
        if(textLabel.equals("Input"))
            setTextLabel(scalarModel.getLabel());
        if(!scalarModel.isWritable())
        {
            setValueEditable(false);
            setButtonEnabled(false);
        }
    }
    
    public void clearModel()
    {
        scalarModel = null;
        textLabel = "Input";
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
                ((IBooleanScalar)scalarModel).setValue(Boolean.getBoolean(attributeTextField.getText()));
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "Cannot write attribute because " + e.getMessage());
        }
    }
    
   private double getValue()
   {
       String textVal = attributeTextField.getText();
       if(textVal.equals(""))
           return 0;
       else
           return Double.parseDouble(textVal);
   }
    
    public boolean isUnitVisible() {
        return unitVisible;
    }
    public void setUnitVisible(boolean unitVisible) {
        this.unitVisible = unitVisible;
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
    public String getTextLabel() {
        return textLabel;
    }
    public void setTextLabel(String atextLabel) {
        this.textLabel = atextLabel;
        if(isUnitVisible() && scalarModel != null)
            attributeNameLabel.setText(textLabel + " " + scalarModel.getUnit());
        else
            attributeNameLabel.setText(textLabel);
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
        this.backgroundButton = backgroundButton;
        writeButton.setBackground(backgroundButton);
    }
    
    public Color getBackgroundField() {
        return backgroundField;
    }
    
    public void setBackgroundField(Color backgroundField) {
        this.backgroundField = backgroundField;
        attributeTextField.setBackground(backgroundField);
    }
    
    public void setFont(Font arg0) {
        if(attributeNameLabel == null)
            return;
        attributeNameLabel.setFont(arg0);
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
                si.setScalarModel((IAttribute)attributeList.add("jlp/test/1/att_six"));
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
        f.show();
    }
    
}
