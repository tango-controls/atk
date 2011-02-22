/*	Synchrotron Soleil 
 *  
 *   File          :  AttributeQualityLightViewer.java
 *  
 *   Project       :  ATKWidgetSoleil
 *  
 *   Description   :  
 *  
 *   Author        :  SOLEIL
 *  
 *   Original      :  18 août 2005 
 *  
 *   Revision:  					Author:  
 *   Date: 							State:  
 *  
 *   Log: AttributeQualityLightViewer.java,v 
 *
 */
package fr.esrf.tangoatk.widget.attribute;

import javax.swing.JButton;
import javax.swing.JFrame;

import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.IAttribute;
import fr.esrf.tangoatk.core.IAttributeStateListener;
import fr.esrf.tangoatk.core.IErrorListener;
import fr.esrf.tangoatk.widget.util.ATKConstant;

/**
 * A LED that will take the appropriate color for an attribute's quality 
 * @author SOLEIL
 */
public class AttributeQualityLightViewer extends JButton implements
        IAttributeStateListener, IErrorListener {

    private boolean viewLabel;
    private IAttribute attributeModel;
    
    /**
     * possibles values :
     * AttributeQualityLightViewer.label
     * AttributeQualityLightViewer.name
     * AttributeQualityLightViewer.completeName
     * AttributeQualityLightViewer.quality
     * AttributeQualityLightViewer.labelAndQuality
     * AttributeQualityLightViewer.nameAndQuality
     * AttributeQualityLightViewer.customLabel
     */
    private int chosenLabel;
    private final static String defaultName = "No attribute defined";

    /**
     * int representing the option to see attribute's label as label
     */
    public final static int label = 0;

    /**
     * int representing the option to see attribute's name as label
     */
    public final static int name = 1;

    /**
     * int representing the option to see attribute's quality as label
     */
    public final static int quality = 2;

    /**
     * int representing the option to see attribute's label and quality as label
     */
    public final static int labelAndQuality = 3;

    /**
     * int representing the option to see attribute's name and quality as label
     */
    public final static int nameAndQuality = 4;
    
    /**
     * int representing the option to see attribute's name and quality as label
     */
    public final static int customLabel = 5;
    
    /**
     * int representing the option to see attribute's complete name as label
     */
    public final static int completeName = 6;

    /**
     * Constructs a AttributeQualityLightViewer with a devicePropertyModel = null
     * No label will be shown. If you use <code>setViewLabel(true)</code>,
     * you will see the attribute's label as label by default.
     */
    public AttributeQualityLightViewer() {
        super();
        chosenLabel = label;
        viewLabel = false;
        setAttributeModel(null);
    }

    /**
     * Constructs a AttributeQualityLightViewer
     * 
     * @param attribute
     *            the attribute to associate with this viewer (devicePropertyModel of this
     *            viewer)
     * @param kindOfLabel
     *            the kind of label you wish to see
     * @param viewLbl
     *            to say whether you wish to display label or not
     */
    public AttributeQualityLightViewer(IAttribute attribute, int kindOfLabel,
            boolean viewLbl) {
        super();
        chosenLabel = kindOfLabel;
        viewLabel = viewLbl;
        setAttributeModel(attribute);
    }

    /**
     * @return Returns the associated Attribute.
     */
    public IAttribute getAttributeModel() {
        return attributeModel;
    }

    /**
     * Associates an attribute to this component.
     * 
     * @param attrModel
     *            The attribute to associate with this component.
     */
    public void setAttributeModel(IAttribute attrModel) {
        if (attributeModel != null) {
            attributeModel.removeStateListener(this);
            attributeModel.removeErrorListener(this);
        }
        attributeModel = null;
        attributeModel = attrModel;
        if (attributeModel != null) {
            attributeModel.addStateListener(this);
            attributeModel.addErrorListener(this);
            manageLabel(attributeModel.getState());
            setIcon(ATKConstant.getIcon4Quality(attributeModel.getState()));
        }
        else{
            manageLabel("Unknown quality");
            setIcon(ATKConstant.getIcon4Quality(IAttribute.UNKNOWN));
        }
    }

    /**
     * Clears all devicePropertyModel and listener attached to the components
     */
    public void clearAttributeModel() {
        setAttributeModel(null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.esrf.tangoatk.core.IAttributeStateListener#stateChange(fr.esrf.tangoatk.core.AttributeStateEvent)
     */
    public void stateChange(AttributeStateEvent arg0) {
        manageLabel(arg0.getState());
        setIcon(ATKConstant.getIcon4Quality(arg0.getState()));
        repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.esrf.tangoatk.core.IErrorListener#errorChange(fr.esrf.tangoatk.core.ErrorEvent)
     */
    public void errorChange(ErrorEvent arg0) {
        manageLabel("Error occured");
        setIcon(ATKConstant.getIcon4Quality(IAttribute.UNKNOWN));
        repaint();
    }

    /**
     * To set or unset devicePropertyModel's label as text of this JLabel
     * 
     * @param b
     *            a boolean to view or not label of this
     *            JLabel. if <code>true</code> and devicePropertyModel is not null, it will
     *            show label depending on the chosen label. if devicePropertyModel is null
     *            a message in label will warn user that there is no attribute
     * @see setChosenLabel
     */
    public void setViewLabel(boolean b) {
        viewLabel = b;
        if (attributeModel != null) {
            manageLabel(attributeModel.getState());
        } else {
            manageLabel("unknown state");
        }
    }

    /**
     * To know whether devicePropertyModel's label is text of this JLabel or not
     */
    public boolean isViewLabel() {
        return viewLabel;
    }

    /**
     * A method to know which kind of label you want to have.
     * The label will be visible only if you used <code>setViewLabel(true)</code>
     * @param chosen the kind of label you want to have
     *               use the associated static variables.
     */
    public void setChosenLabel(int chosen) {
        chosenLabel = chosen;
    }

    /**
     * A method to know which kind of label is associated with this JLabel
     * @return an int that represents the kind of label chosen.
     * Compare it with the static variables
     */
    public int getChosenLabel() {
        return chosenLabel;
    }

    private void manageLabel(String myQuality) {
        if (viewLabel) {
            switch (chosenLabel) {
            case label:
                if (attributeModel == null) {
                    setText(defaultName);
                } else {
                    setText(attributeModel.getLabel());
                }
                break;
            case name:
                if (attributeModel == null) {
                    setText(defaultName);
                } else {
                    setText(attributeModel.getNameSansDevice());
                }
                break;
            case quality:
                setText(myQuality);
            case nameAndQuality:
                if (attributeModel == null) {
                    setText(defaultName + " : " + myQuality);
                } else {
                    setText(attributeModel.getName() + " : " + myQuality);
                }
                break;
            case labelAndQuality:
                if (attributeModel == null) {
                    setText(defaultName + " : " + myQuality);
                } else {
                    setText(attributeModel.getLabel() + " : " + myQuality);
                }
                break;
            case completeName :
                if (attributeModel == null) {
                    setText(defaultName);
                } else {
                    setText(attributeModel.getName());
                }
                break;
            default:
                ;
            }// end switch (chosenLabel)
        }// end if (viewLabel)
    }// end manageLabel()

    /**
     * Main class, so you can have an example.
     * You can monitor your own attribute by giving its full path name in argument
     */
    public static void main(String[] args) {
        fr.esrf.tangoatk.core.AttributeList attributeList = new fr.esrf.tangoatk.core.AttributeList();
        AttributeQualityLightViewer aqlv = new AttributeQualityLightViewer();
	aqlv.setViewLabel(true);
	aqlv.setChosenLabel(AttributeQualityLightViewer.labelAndQuality);
        try {
            if (args.length != 0) {
                aqlv.setAttributeModel((IAttribute) attributeList.add(args[0]));
            } else {
                aqlv.setAttributeModel((IAttribute) attributeList
                        .add("test/testSignal2/1/signal"));
            }
            aqlv.setViewLabel(true);
            attributeList.setRefreshInterval(1000);
            attributeList.startRefresher();
            JFrame f = new JFrame(aqlv.getAttributeModel().getName().substring(0,
                    aqlv.getAttributeModel().getName().lastIndexOf("/")));
            f.getContentPane().add(aqlv);
            f.setSize(300, 50);
            f.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
