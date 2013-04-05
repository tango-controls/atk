/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file ins part of Tango.
 * 
 *  Tango ins free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango ins distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * StringScalarEditor.java
 *
 * Created on July 29, 2003, 11:00 AM
 */
/**
 *
 * @author  poncet
 */
package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.tangoatk.core.NumberScalarEvent;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JTextField;

import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.core.INumberScalarListener;
import fr.esrf.tangoatk.core.Property;
import fr.esrf.tangoatk.widget.util.ATKFormat;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class NumberScalarTextEditor extends JTextField
                                    implements INumberScalarListener, PropertyChangeListener
{
    String format = "";

    private INumberScalar model;
    private String lastSet;

    /** Creates new form StringScalarEditor */
    public NumberScalarTextEditor()
    {
        model = null;
        lastSet = null;
        this.addActionListener(
                new java.awt.event.ActionListener()
                {

                    public void actionPerformed(java.awt.event.ActionEvent evt)
                    {
                        textInsertActionPerformed(evt);
                    }
                });
        setMargin(new Insets(0, 0, 0, 0)); // text will have the maximum available space
    }

    public void clearModel()
    {
        if (model == null) return;
        model.removeNumberScalarListener(this);
        model = null;
    }

    public void setModel(INumberScalar ins)
    {
        // Remove old registered listener
        clearModel();

        if (ins == null) return;

        if (!ins.isWritable())
        {
            throw new IllegalArgumentException("NumberScalarTextEditor: Only accept writeable attribute.");
        }

        model = ins;
        format = model.getProperty("format").getPresentation();

        // Register new listener
        model.addNumberScalarListener(this);
        model.getProperty("format").addPresentationListener(this);
        model.refresh();
    }

    public INumberScalar getModel()
    {
        return model;
    }

    private String getDisplayString(double value)
    {
        Double attDouble = new Double(value);
        String dispStr;

        if (Double.isNaN(value) || Double.isInfinite(value))
        {
            dispStr = Double.toString(value);
        }
        else
        {
            try
            {
                if (format.indexOf('%') == -1)
                {
                    dispStr = String.format("%.2f", value);
                }
                else
                {
                    dispStr = ATKFormat.format(format, attDouble);
                }
            }
            catch (Exception e)
            {
                dispStr = String.format("%.2f", value);
            }
        }

        return dispStr;
    }

    public void numberScalarChange(NumberScalarEvent evt)
    {
        double set = Double.NaN;

        if(hasFocus())
          set = model.getNumberScalarSetPointFromDevice();
        else
          set = model.getNumberScalarSetPoint();

        String  strValue = getDisplayString(set);
        if (lastSet == null)
        {
            setText(strValue);
            lastSet = strValue;
        }
        else
        {
            if (!lastSet.equals(strValue))
            {
                setText(strValue);
                lastSet = strValue;
            }
        }
    }

    public void errorChange(ErrorEvent e)
    {
        setText("Read Error");
        lastSet = "Read Error";
    }

    public void stateChange(AttributeStateEvent e)
    {
    }

    public void propertyChange(PropertyChangeEvent evt)
    {

        Property src = (Property) evt.getSource();

        if (model != null)
        {
            if (src.getName().equalsIgnoreCase("format"))
            {
                format = src.getValue().toString();
            }
            model.refresh();
        }
    }

    private void textInsertActionPerformed(java.awt.event.ActionEvent evt)
    {
        if (model == null) return;
        String  valStr = this.getText();
        try
        {
            double value = Double.parseDouble(valStr);
            model.setValue(value);
        }
        catch (NumberFormatException nfe)
        {
            lastSet = null;
            model.refresh();
        }
    }

    public static void main(String[] args)
    {
        fr.esrf.tangoatk.core.AttributeList attList = new fr.esrf.tangoatk.core.AttributeList();
        NumberScalarTextEditor nste = new NumberScalarTextEditor();
        INumberScalar att;
        JFrame mainFrame;

        // Connect to a "writable" string scalar attribute
        try
        {
            att = (INumberScalar) attList.add("id-carr/TD13/GAP/Position");
            nste.setModel(att);
        }
        catch (Exception ex)
        {
            System.out.println("caught exception : " + ex.getMessage());
            System.exit(-1);
        }

        mainFrame = new JFrame();

        mainFrame.getContentPane().add(nste);
        mainFrame.pack();

        mainFrame.setVisible(true);

        attList.startRefresher();

    } // end of main ()
}

