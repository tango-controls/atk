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

    static ImageIcon bulbOff = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/bulbDisabled.gif"));
    static ImageIcon bulbOn = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/bulbEnabled.gif"));
    static ImageIcon blueLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledBlue.gif"));
    static ImageIcon brownGrayLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledBrownGray.gif"));
    static ImageIcon darkGrayLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledDarkGray.gif"));
    static ImageIcon darkOrangeLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledDarkOrange.gif"));
    static ImageIcon grayLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledGray.gif"));
    static ImageIcon greenLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledGreen.gif"));
    static ImageIcon lightOrangeLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledLightOrange.gif"));
    static ImageIcon pinkLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledPink.gif"));
    static ImageIcon redLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledRed.gif"));
    static ImageIcon whiteLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledWhite.gif"));
    static ImageIcon yellowLED = new ImageIcon(SignalScalarLightViewer.class.getResource("/fr/esrf/tangoatk/widget/icons/ledYellow.gif"));
    
    private ImageIcon m_iconLightOn;
    private ImageIcon m_iconLightOff;
    private IAttribute attributeModel;
    private boolean viewLabel;

    /**
     * Constructs a SignalScalarLightViewer with a devicePropertyModel = null and will not
     * show devicePropertyModel's label on setModel(...)
     */
    public SignalScalarLightViewer() {
        super();
        attributeModel = null;
        viewLabel = false;
        setIconLightOn(redLED);
        setIconLightOff(grayLED);
        setIcon(m_iconLightOff);
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
        setIcon(m_iconLightOff);
    }

    /**
     * @see fr.esrf.tangoatk.core.INumberScalarListener#numberScalarChange(fr.esrf.tangoatk.core.NumberScalarEvent)
     */
    public void numberScalarChange(NumberScalarEvent arg0) {
        try {
            if (arg0.getValue() == 1)
                setIcon(m_iconLightOn);
            else
                setIcon(m_iconLightOff);
        } catch (Exception e) {
            setIcon(m_iconLightOff);
        }

    }
    
    /**
     * To set or unset devicePropertyModel's label as text of this JLabel
     * @param b a boolean to set or unset devicePropertyModel's label as text of this JLabel.
     * if <code>true</code> and devicePropertyModel is not null, it will set devicePropertyModel's label as text.
     * otherwise it will erase text
     */
    public void setViewLabel(boolean b){
        viewLabel = b;
        if (viewLabel && (attributeModel!=null)){
            setText(attributeModel.getLabel());
        }
        else{
            setText("");
        }
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
                && !(numberModel instanceof INumberScalar)) {
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
     * @see fr.esrf.tangoatk.core.IBooleanScalarListener#booleanScalarChange(fr.esrf.tangoatk.core.BooleanScalarEvent)
     */
    public void booleanScalarChange(BooleanScalarEvent arg0) {
        try {
            if (arg0.getValue())
                setIcon(m_iconLightOn);
            else
                setIcon(m_iconLightOff);
        } catch (Exception e) {
            setIcon(m_iconLightOff);
        }
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
            f.getContentPane().add(sslv);
            f.setSize(300,50);
            f.show();
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

}
