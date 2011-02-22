//+============================================================================
//Source: package fr.esrf.tangoatk.widget.attribute;/StringScalarComboEditor.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.core.INumberScalarListener;
import fr.esrf.tangoatk.core.NumberScalarEvent;

/**
 * @author ho
 *
 * This component is used for modifying the value of a attribute thanks to a combo box
 * This component allows to display a string list and associated for each string a real value to send
 */
public class StringScalarNumberComboEditor extends JComboBox implements ActionListener, INumberScalarListener
{
    
    private DefaultComboBoxModel 	comboModel;
    private String 					defActionCmd;
    private INumberScalar 			numberModel;
    private String optionList[] = {"0.0"};
    
    
    public StringScalarNumberComboEditor()
    {
        comboModel = null;
        defActionCmd = "setAttActionCmd";
        numberModel = null;
        numberModel = null;
        comboModel = new DefaultComboBoxModel(optionList);
        setModel(comboModel);
        setActionCommand(defActionCmd);
        addActionListener(this);
    }
    
    public void actionPerformed(ActionEvent actionevent)
    {
        double d = 0.0D;
        int i  = this.getSelectedIndex();
        
        if(!actionevent.getActionCommand().equals(defActionCmd))
            return;
        
        if(numberModel == null)
            return;
        
        d = (0.0D / 0.0D);
        d = getValues(i);
        if(!Double.isNaN(d))
            numberModel.setValue(d);
    }
    
    private void changeCurrentSelection(int i)
    {
        disableExecution();
        setSelectedIndex(i);
        repaint();
        enableExecution();
    }
    
    private void changeSelectedOption(double d)
    {
        changeCurrentSelection(getIndex(d));
    }
    
    public void disableExecution()
    {
        setActionCommand("dummy");
    }
    
    public void enableExecution()
    {
        setActionCommand(defActionCmd);
    }
    
    public void errorChange(ErrorEvent errorevent)
    {
        changeSelectedOption((0.0D / 0.0D));
    }
    
    private int getIndex(double value)
    {
        if(numberModel == null)
            return 0;
        double ad[] = numberModel.getPossibleValues();
        for(int i = 0 ; i < ad.length ;i++)
        {
            if(ad[i] == value)
                return i;
        }
        return 0;
    }
    
    public double getValues(int index)
    {
        double ad[] = numberModel.getPossibleValues();
        if (ad == null)
            return Double.NaN;
        if ((index > ad.length) || (index < 0))
            return Double.NaN;
        
        return ad[index];
    }
    
    public void numberScalarChange(NumberScalarEvent numberscalarevent)
    {
        double d = (0.0D / 0.0D);
        if(hasFocus())
            d = numberModel.getNumberScalarSetPointFromDevice();
        else
            d = numberModel.getNumberScalarSetPoint();
        changeSelectedOption(d);
    }
    
    public void setNumberModel(INumberScalar inumberscalar)
    {
        clearModel();
        
        if(inumberscalar == null)
            return;
        
        if(!inumberscalar.isWritable())
            return;
        
        numberModel = inumberscalar;
        comboModel = new DefaultComboBoxModel(optionList);
        setModel(comboModel);
        numberModel.addNumberScalarListener(this);
        numberModel.refresh();
        
    }
    
    public INumberScalar getNumberModel()
    {
        return numberModel;
    }
    
    public void clearModel()
    {
       if(numberModel != null)
            numberModel.removeNumberScalarListener(this);
    }
    
    public void setOptionList(String[] optionList) {
        this.optionList = optionList;
        comboModel = new DefaultComboBoxModel(optionList);
        setModel(comboModel);
        repaint();
    }
    
    public void stateChange(AttributeStateEvent attributestateevent)
    {
    }
}
