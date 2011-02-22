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
//Source: package tangowidget.attribute;/AttributeBooleanBulb.java
//
//project :     globalscreen
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



import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.BooleanScalarEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.IAttribute;
import fr.esrf.tangoatk.core.IBooleanScalar;
import fr.esrf.tangoatk.core.IBooleanScalarListener;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.core.INumberScalarListener;
import fr.esrf.tangoatk.core.NumberScalarEvent;



/**
 * A light to show the value of a signal attribute (this means an attribute
 * representing a boolean value, but of type BooleanScalar or NumberScalar)
 * 
 * @author ho
 */
public class SignalScalarLightViewer extends JButton 
                                     implements INumberScalarListener, 
                                                IBooleanScalarListener {

    public final static ImageIcon bulbOff = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/bulbDisabled.gif"));
    public final static ImageIcon bulbOn = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/bulbEnabled.gif"));
    public final static ImageIcon bulbKO = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/bulbKO.gif"));
    public final static ImageIcon blueLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledBlue.gif"));
    public final static ImageIcon brownGrayLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledBrownGray.gif"));
    public final static ImageIcon darkGrayLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledDarkGray.gif"));
    public final static ImageIcon darkOrangeLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledDarkOrange.gif"));
    public final static ImageIcon grayLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledGray.gif"));
    public final static ImageIcon greenLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledGreen.gif"));
    public final static ImageIcon lightOrangeLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledLightOrange.gif"));
    public final static ImageIcon pinkLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledPink.gif"));
    public final static ImageIcon redLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledRed.gif"));
    public final static ImageIcon whiteLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledWhite.gif"));
    public final static ImageIcon yellowLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledYellow.gif"));
    public final static ImageIcon KOLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledKO.gif"));

    private ImageIcon m_iconLightOn;
    private ImageIcon m_iconLightOff;
    private ImageIcon m_iconLightKO;
    private IAttribute attributeModel;
    private String  falseLabel  = "FALSE";
    private String  trueLabel   = "TRUE";
    private String  errorLabel  = "ERROR";
    private Boolean formerValue = null;

    /**
     * int representing the option to see attribute's boolean value as label
     */
    public static final int booleanLabel = 0;
    /**
     * int representing the option to see attribute's label as label
     */
    public static final int label = 1;
    /**
     * int representing the option to see attribute's name as label
     */
    public static final int name = 2;
    /**
     * int representing the option to see attribute's complete Name as label
     */
    public static final int completeName = 3;
    /**
     * int representing the option to see a custom Label as label
     */
    public static final int customLabel = 4;
    /**
     * int representing the option to see attribute's label and the boolean value as label
     */
    public static final int labelAndBoolean = 5;
    /**
     * int representing the option to see attribute's name and the boolean value as label
     */
    public static final int nameAndBoolean = 6;
    
    /**
     * possibles values :
     * SignalScalarLightViewer.booleanLabel
     * SignalScalarLightViewer.label
     * SignalScalarLightViewer.labelAndBoolean
     * SignalScalarLightViewer.name
     * SignalScalarLightViewer.nameAndBoolean
     * SignalScalarLightViewer.completeName
     * SignalScalarLightViewer.customLabel
     */
    private int chosenLabel = booleanLabel;
    
   
    
    
    private boolean viewLabel;

    /**
     * Constructs a SignalScalarLightViewer with a devicePropertyModel = null and will not
     * show devicePropertyModel's label on setModel(...)
     */
    public SignalScalarLightViewer() {
        this(false);
    }

    /**
     * Constructs a SignalScalarLightViewer with a devicePropertyModel = null
     * will show devicePropertyModel's label on setModel(...)
     */
    public SignalScalarLightViewer(boolean viewLabel) {
        super();
        attributeModel = null;
        this.viewLabel = viewLabel;
        setIconLightOn(redLED);
        setIconLightOff(grayLED);
        setIconLightKO(KOLED);
        setIcon(m_iconLightKO);
        repaint();
    }

    /**
     * @see fr.esrf.tangoatk.core.INumberScalarListener#numberScalarChange(fr.esrf.tangoatk.core.NumberScalarEvent)
     */
    public void numberScalarChange(NumberScalarEvent arg0) {
        Boolean boolValue = null;
        try
        {
            if (arg0.getValue() == 1)
            {
                setIcon(m_iconLightOn);
                boolValue = new Boolean(true);
            }
            else
            {
                setIcon(m_iconLightOff);
                boolValue = new Boolean(false);
            }
        } catch (Exception e) {
            setIcon(m_iconLightKO);
        }
        manageLabel(boolValue);
        repaint();
    }
    
    /**
     * To set or unset devicePropertyModel's label as text of this JLabel
     * @param b a boolean to set or unset devicePropertyModel's label as text of this JLabel.
     * if <code>true</code> and devicePropertyModel is not null, it will set devicePropertyModel's label as text.
     * otherwise it will erase text
     */
    public void setViewLabel(boolean b){
        viewLabel = b;
        manageLabel(formerValue);
    }
    /**
     * To know whether devicePropertyModel's label is text of this JLabel or not
     */
    public boolean isViewLabel(){
        return viewLabel;
    }

    /**
     * @see fr.esrf.tangoatk.core.IAttributeStateListener#stateChange(fr.esrf.tangoatk.core.AttributeStateEvent)
     */
    public void stateChange(AttributeStateEvent arg0) {
    }

    /**
     * @see fr.esrf.tangoatk.core.IErrorListener#errorChange(fr.esrf.tangoatk.core.ErrorEvent)
     */
    public void errorChange(ErrorEvent arg0) {
        setIcon(m_iconLightKO);
        manageLabel(null);
        repaint();
    }

    /**
     * @return Returns the numberModel.
     */
    public IAttribute getAttributeModel() {
        return attributeModel;
    }

    /**
     * Associates an attribute to this component. This attribute should be of
     * type INumberScalar or IBooleanScalar. Otherwise nothing is done.
     * 
     * @param numberModel The numberModel to set.
     */
    public void setAttributeModel(IAttribute numberModel) {
        if (!(numberModel instanceof INumberScalar)
                && !(numberModel instanceof IBooleanScalar)) {
            return;
        }
        clearAttributeModel();
        this.attributeModel = numberModel;
        if (this.attributeModel instanceof INumberScalar) {
            ((INumberScalar) numberModel).addNumberScalarListener(this);
            if (viewLabel) setText(attributeModel.getLabel());
        }
        if (this.attributeModel instanceof IBooleanScalar) {
            ((IBooleanScalar) numberModel).addBooleanScalarListener(this);
            if (viewLabel) setText(attributeModel.getLabel());
        }
    }

    /**
     * Clears all devicePropertyModel and listener attached to the components
     */
    public void clearAttributeModel() {
        if (attributeModel != null) {
            if (this.attributeModel instanceof INumberScalar) {
                ((INumberScalar) attributeModel).removeNumberScalarListener(this);
            }
            if (this.attributeModel instanceof IBooleanScalar) {
                ((IBooleanScalar) attributeModel).removeBooleanScalarListener(this);
            }
            attributeModel = null;
        }
    }

    public int getChosenLabel() {
        return chosenLabel;
    }
    
    public void setChosenLabel(int chosenLabel) {
        this.chosenLabel = chosenLabel;
    }
    
    public String getFalseLabel() {
        return falseLabel;
    }
    
    public void setFalseLabel(String falseLabel) {
        this.falseLabel = falseLabel;
    }
    
    public String getTrueLabel() {
        return trueLabel;
    }
    
    public void setTrueLabel(String trueLabel) {
        this.trueLabel = trueLabel;
    }
    
    private void manageLabel(Boolean booleanValue) {
        formerValue = booleanValue;
        if (viewLabel) {
            if (attributeModel == null)
            {
                setText("Unknown Attribute");
                return;
            }
            switch (chosenLabel) {
            case label:
                setText(attributeModel.getLabel());
                break;
            case name:
                setText(attributeModel.getNameSansDevice());
                break;
            case booleanLabel:
                if (booleanValue == null)
                {
                    setText(errorLabel);
                }
                else
                {
                    if(booleanValue.booleanValue())
                        setText(trueLabel);
                    else
                        setText(falseLabel);
                }
                break;
            case labelAndBoolean:
                if (booleanValue == null)
                {
                    setText(attributeModel.getLabel() + ":" + errorLabel);
                }
                else
                {
                    if(booleanValue.booleanValue())
                        setText(attributeModel.getLabel() + ":" + trueLabel);
                    else
                        setText(attributeModel.getLabel() + ":" + falseLabel);
                }
                break;
            case nameAndBoolean:
                if (booleanValue == null)
                {
                    setText(attributeModel.getNameSansDevice() + ":" + errorLabel);
                }
                else
                {
                    if(booleanValue.booleanValue())
                        setText(attributeModel.getNameSansDevice() + ":" + trueLabel);
                    else
                        setText(attributeModel.getNameSansDevice() + ":" + falseLabel);
                }
                break;
            case completeName :
                setText(attributeModel.getName());
                break;
            default: //nothing to do
            }// end switch (chosenLabel)
        }// end if (viewLabel)
    }// end manageLabel()

    /**
     * sets the icon associated with the "true" or "1" value
     * (default : SignalScalarLightViewer.redLED)
     */
    public void setIconLightOn(ImageIcon icon) {
        m_iconLightOn = icon;
    }

    /**
     * sets the icon associated with the "false" or "0" value
     * (default : SignalScalarLightViewer.grayLED)
     */
    public void setIconLightOff(ImageIcon icon) {
        m_iconLightOff = icon;
    }

    /**
     * sets the icon associated with "KO" value = "could not get value"
     * (default : SignalScalarLightViewer.KOLED)
     */
    public void setIconLightKO(ImageIcon icon) {
        m_iconLightKO = icon;
    }

    /**
     * @see fr.esrf.tangoatk.core.IBooleanScalarListener#booleanScalarChange(fr.esrf.tangoatk.core.BooleanScalarEvent)
     */
    public void booleanScalarChange(BooleanScalarEvent arg0) {
        try
        {
            if (arg0.getValue())
                setIcon(m_iconLightOn);
            else
                setIcon(m_iconLightOff);
        }
        catch (Exception e)
        {
            setIcon(m_iconLightKO);
        }
        manageLabel(new Boolean(arg0.getValue()));
        repaint();
    }
    
    /**
     * Main class, so you can have an example.
     * You can monitor your own attribute by giving its full path name in argument
     */
    public static void main(String[] args){
        fr.esrf.tangoatk.core.AttributeList attributeList = new fr.esrf.tangoatk.core.AttributeList();
        SignalScalarLightViewer sslv = new SignalScalarLightViewer(true);
        try{
            if (args.length!=0){
                sslv.setAttributeModel((IAttribute)attributeList.add(args[0]));
            }
            else{
                sslv.setAttributeModel((IAttribute)attributeList.add("test/testSignal2/1/signal"));
            }
            attributeList.setRefreshInterval(1000);
            attributeList.startRefresher();
            JFrame f = new JFrame(sslv.getAttributeModel().getName().substring(0,sslv.getAttributeModel().getName().lastIndexOf("/")));
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.getContentPane().add(sslv);
            f.setSize(300,50);
            f.setVisible(true);
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

}
