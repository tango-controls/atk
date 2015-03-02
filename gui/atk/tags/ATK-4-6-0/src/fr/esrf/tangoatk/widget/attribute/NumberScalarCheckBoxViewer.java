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
 
// $Id$
//
// Description:
package fr.esrf.tangoatk.widget.attribute;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

import fr.esrf.Tango.DevFailed;
import fr.esrf.tangoatk.core.IBooleanScalar;
import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.core.INumberScalarListener;
import fr.esrf.tangoatk.core.NumberScalarEvent;

/**
 * An NumberScalarCheckBoxViewer is a NumberScalar attribute viewer. This means
 * that the attribute used as the devicePropertyModel for this viewer should implement the
 * INumberScalar interface. The viewer is updated when the scalar attribute
 * value changes. The checkBox is "checked" if the attribute value is "1" and it
 * is unchecked if the attribute value is "0".
 */
public class NumberScalarCheckBoxViewer extends BooleanScalarCheckBoxViewer 
                                        implements INumberScalarListener
{

    private INumberScalar numberModel = null;

    // ---------------------------------------------------
    // Contruction
    // ---------------------------------------------------
    public NumberScalarCheckBoxViewer() {
        super();
    }

    public NumberScalarCheckBoxViewer(String title) {
        super(title);
    }

    // ---------------------------------------------------
    // Property stuff
    // ---------------------------------------------------


    /**
     * Overloads the BooleanScalarCheckBoxViewer method
     */
    public IBooleanScalar getAttModel()
    {
       return null;
    }
    /**
     * Overloads the BooleanScalarCheckBoxViewer method
     */
    public void setAttModel(IBooleanScalar ibs)
    {
       return;
    }



    /**
     * Returns the associated attribute
     */
    public INumberScalar getNumberModel() {
        return numberModel;
    }
    /**
     * Associates an attribute with this checkbox
     */
    public void setNumberModel(INumberScalar numModel) {
        if (numberModel != null) {
            numberModel.removeNumberScalarListener(this);
            numberModel = null;
            setText("");
        }

        if (numModel != null) {
            numberModel = numModel;
            numberModel.addNumberScalarListener(this);
            if ((getTrueLabel() == null) && (getFalseLabel() == null))
                setText(numModel.getLabel());
            //numberModel.refresh();
            setNumberValue(numberModel.getNumberScalarValue());
        }
    }

    /**
     * Removes associated attribute
     */
    public void clearModel() {
        setNumberModel((INumberScalar) null);
    }

    // ---------------------------------------------------
    // Action Listener
    // ---------------------------------------------------
    public void actionPerformed(ActionEvent e) {
        if (numberModel == null)
            return;
        if (!numberModel.isWritable()) {
            setSelected(!isSelected());
            return;
        }
        if (isSelected()) {
            numberModel.setValue(1);
        } else {
            numberModel.setValue(0);
        }
    }

    // ---------------------------------------------------
    // Scalar listener
    // ---------------------------------------------------
    public void numberScalarChange(NumberScalarEvent e) {
        setNumberValue(e.getValue());
    }

    public void stateChange(AttributeStateEvent e) {
    }

    public void errorChange(ErrorEvent evt) {
        setEnabled(false);
    }

    private void setNumberValue(double val) {
        if (!isEnabled())
            setEnabled(true);

	if (val == 0)
	{
            setSelected(false);
            if (getFalseLabel()!=null){
                setText(getFalseLabel());
            }
            else if (numberModel!=null){
                setText(numberModel.getLabel());
            }
        }
        else
	{
            setSelected(true);
            if (getTrueLabel()!=null){
                setText(getTrueLabel());
            }
            else if (numberModel!=null){
                setText(numberModel.getLabel());
            }
        }
    }

    /**
     * Main class, so you can have an example.
     * You can monitor your own attribute by giving its full path name in argument
     */
    static public void main(String args[]) {
        fr.esrf.tangoatk.core.AttributeList attributeList = new fr.esrf.tangoatk.core.AttributeList();
        NumberScalarCheckBoxViewer nscbv = new NumberScalarCheckBoxViewer();
        nscbv.setTrueLabel("yes");
        nscbv.setFalseLabel("no");
        try {
            if (args.length!=0){
                nscbv.setNumberModel((INumberScalar)attributeList.add(args[0]));
            }
            else{
                nscbv.setNumberModel((INumberScalar)attributeList.add("test/testSignal2/1/signal"));
            }
            attributeList.setRefreshInterval(1000);
            attributeList.startRefresher();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        JFrame f = new JFrame();
        f.getContentPane().add(nscbv);
        f.pack();
        f.setVisible(true);
    }

}
