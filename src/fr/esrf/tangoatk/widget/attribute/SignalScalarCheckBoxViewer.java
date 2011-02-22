// File: BooleanScalarCheckBoxViewer.java
// Created: 2005-02-14 18:15:00, poncet
// By: <poncet@esrf.fr>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.widget.attribute;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.ATKConstant;
import fr.esrf.tangoatk.widget.util.jdraw.JDrawable;

/**
 * A SignalScalarCheckBoxViewer is a viewer, represented as a CheckBox, for a
 * signal attribute (this means an attribute representing a boolean value, but
 * of type BooleanScalar or NumberScalar). The viewer is updated when the
 * attribute value changes. The checkBox is "checked" if the attribute value is
 * "true" or does not equal "0" and it is unchecked if the attribute value is
 * "false" are equals "0".
 * 
 * @author GIRARDOT
 */
public class SignalScalarCheckBoxViewer extends JCheckBox
                                        implements ActionListener,
                                                   IBooleanScalarListener,
                                                   INumberScalarListener,
                                                   ISetErrorListener,
                                                   JDrawable {

    protected IAttribute attModel       = null;
    protected String     trueLabel      = null;
    protected String     falseLabel     = null;
    protected String     fixedLabel     = null;

    // model label
    protected boolean    hasToolTip     = false;
    protected boolean    qualityEnabled = false;
    protected Color      background;
    protected String[]   exts           = {"text"};
    protected boolean    enabled;

    // ---------------------------------------------------
    // Contruction
    // ---------------------------------------------------
    public SignalScalarCheckBoxViewer () {
        super();
        addActionListener( this );
        background = getBackground();
        enabled = isEnabled();
    }

    public SignalScalarCheckBoxViewer (String title) {
        super( title );
        addActionListener( this );
        background = getBackground();
        enabled = isEnabled();
    }

    // ---------------------------------------------------
    // Property stuff
    // ---------------------------------------------------
    public IAttribute getAttModel () {
        return attModel;
    }

    public void setBooleanScalarModel (IBooleanScalar boolModel) {
        if ( attModel != null ) {
            clearModel();
        }
        if ( boolModel != null ) {
            attModel = boolModel;
            ((IBooleanScalar)attModel).addBooleanScalarListener( this );
            if ( attModel.isWritable() ) attModel.addSetErrorListener( this );
            if ( ( trueLabel == null ) && ( falseLabel == null )
                    && ( fixedLabel == null ) ) setText( boolModel.getLabel() );
            if ( hasToolTip ) {
                setToolTipText( boolModel.getName() );
            }
            setBoolValue( ((IBooleanScalar)attModel).getDeviceValue() );
        }
    }

    public void setNumberScalarModel (INumberScalar numModel) {
        if ( attModel != null ) {
            clearModel();
        }
        if ( numModel != null ) {
            attModel = numModel;
            ((INumberScalar)attModel).addNumberScalarListener(this);
            if ( attModel.isWritable() ) attModel.addSetErrorListener(this);
            if ( ( trueLabel == null ) && ( falseLabel == null )
                    && ( fixedLabel == null ) ) setText( numModel.getLabel() );
            if ( hasToolTip ) {
                setToolTipText( numModel.getName() );
            }
            setBoolValue( ((INumberScalar)attModel).getNumberScalarDeviceValue() );
        }
    }

    public void clearModel() {
        if (attModel != null) {
            if (attModel instanceof IBooleanScalar) {
                ((IBooleanScalar)attModel).removeBooleanScalarListener(this);
            }
            else if (attModel instanceof INumberScalar) {
                ((INumberScalar)attModel).removeNumberScalarListener(this);
            }
            if ( attModel.isWritable() ) attModel.removeSetErrorListener(this);
            attModel = null;
            setText( "" );
        }
    }

    public String getTrueLabel () {
        return trueLabel;
    }

    public void setTrueLabel (String tLabel) {
        trueLabel = tLabel;
        if ( ( trueLabel == null ) || ( falseLabel == null ) ) {
            if ( attModel != null ) setText( attModel.getLabel() );
            else setText( null );
        }
        else if ( isSelected() ) setText( trueLabel );
    }

    public String getFalseLabel () {
        return falseLabel;
    }

    public void setFalseLabel (String fLabel) {
        falseLabel = fLabel;
        if ( ( trueLabel == null ) || ( falseLabel == null ) ) {
            if ( attModel != null ) setText( attModel.getLabel() );
            else setText( null );
        }
        else if ( !isSelected() ) setText( falseLabel );
    }

    /**
     * <code>getHasToolTip</code> returns true if the viewer has a tooltip
     * (attribute full name)
     * 
     * @return a <code>boolean</code> value
     */
    public boolean getHasToolTip () {
        return hasToolTip;
    }

    /**
     * <code>setHasToolTip</code> display or not a tooltip for this viewer
     * 
     * @param b
     *            If True the attribute full name will be displayed as tooltip
     *            for the viewer
     */
    public void setHasToolTip (boolean b) {
        if ( hasToolTip != b ) {
            if ( b == false ) setToolTipText( null );
            else if ( attModel != null ) setToolTipText( attModel.getName() );
            hasToolTip = b;
        }
    }

    /**
     * <code>getQualityEnabled</code> returns a boolean to know whether
     * quality will be displayed as background or not.
     * 
     * @return a <code>boolean</code> value
     */
    public boolean getQualityEnabled () {
        return qualityEnabled;
    }

    /**
     * <code>setQualityEnabled</code> view or not the attribute quality for
     * this viewer
     * 
     * @param b
     *            If True the attribute full name will be displayed as tooltip
     *            for the viewer
     * @param qualityEnabled
     *            If True the background Color represents the attribute quality
     *            factor
     */
    public void setQualityEnabled (boolean b) {
        qualityEnabled = b;
        if ( !qualityEnabled ) {
            super.setBackground( background );
            repaint();
        }
    }

    // ---------------------------------------------------
    // JDrawable implementation
    // ---------------------------------------------------
    public void initForEditing () {
        setText( "CheckBox" );
    }

    public JComponent getComponent () {
        return this;
    }

    public String getDescription (String extName) {
        if ( extName.equalsIgnoreCase( "text" ) ) {
            return "Overrides text given by the model.";
        }
        return "";
    }

    public String[] getExtensionList () {
        return exts;
    }

    public boolean setExtendedParam (String name, String value, boolean popupErr) {
        if ( name.equalsIgnoreCase( "text" ) ) {
            fixedLabel = value;
            if ( fixedLabel.length() == 0 ) {
                fixedLabel = null;
                setText( "CheckBox" );
            }
            else {
                setText( fixedLabel );
            }
            return true;
        }
        return false;
    }

    public String getExtendedParam (String name) {
        if ( name.equalsIgnoreCase( "text" ) ) {
            if ( fixedLabel != null ) {
                return fixedLabel;
            }
        }
        return "";
    }

    // ---------------------------------------------------
    // Action Listener
    // ---------------------------------------------------
    public void actionPerformed (ActionEvent e) {
        // System.out.println("BooleanScalarCheckBoxViewer : actionPerformed
        // called");
        if ( attModel == null ) return;
        if ( !attModel.isWritable() ) {
            setSelected( !isSelected() );
            return;
        }
        if (attModel instanceof IBooleanScalar) {
            if ( isSelected() ) {
                ((IBooleanScalar)attModel).setValue( true );
            }
            else {
                ((IBooleanScalar)attModel).setValue( false );
            }
        }
        else if (attModel instanceof INumberScalar) {
            if ( isSelected() ) {
                ((INumberScalar)attModel).setValue(1);
            }
            else {
                ((INumberScalar)attModel).setValue(0);
            }
        }
    }

    // ---------------------------------------------------
    // ScalarListener
    // ---------------------------------------------------
    public void booleanScalarChange (BooleanScalarEvent e) {
        setBoolValue( e.getValue() );
    }

    public void numberScalarChange (NumberScalarEvent e) {
        setBoolValue( e.getValue() );
    }

    public void stateChange (AttributeStateEvent evt) {
        String state = evt.getState();
        if ( !qualityEnabled ) return;
        super.setBackground( ATKConstant.getColor4Quality( state ) );
        repaint();
    }

    public void errorChange (ErrorEvent evt) {
        super.setEnabled( false );
    }

    protected void setBoolValue (boolean val) {
        if ( enabled && !isEnabled() ) super.setEnabled( enabled );
        setSelected( val );
        if ( ( trueLabel != null ) || ( falseLabel != null ) ) {
            if ( val ) setText( trueLabel );
            else setText( falseLabel );
        }
    }

    protected void setBoolValue (double val) {
        setBoolValue(val != 0);
    }

    // ---------------------------------------------------
    // ISetErrorListener listener
    // ---------------------------------------------------
    public void setErrorOccured (ErrorEvent evt) {
        if ( attModel == null ) return;
        if ( evt.getSource() != attModel ) return;
        if (attModel instanceof IBooleanScalar) {
            setBoolValue( ((IBooleanScalar)attModel).getDeviceValue() );
        }
        else if (attModel instanceof INumberScalar) {
            setBoolValue( ((INumberScalar)attModel).getNumberScalarDeviceValue() );
        }
    }

    public void setBackground (Color bg) {
        background = bg;
        super.setBackground( bg );
    }

    @Override
    public void setEnabled (boolean b) {
        enabled = b;
        super.setEnabled( b );
    }

    // ---------------------------------------------------
    // Main test fucntion
    // ---------------------------------------------------
    public static void main (String args[]) {
        IEntity ie;
        AttributeList attl = new AttributeList();
        JFrame f = new JFrame();
        SignalScalarCheckBoxViewer bsv = new SignalScalarCheckBoxViewer();
        String attributeName = "tango/tangotest/1/boolean_scalar";
        try {
            if (args.length > 0) {
                attributeName = args[0];
            }
            ie = attl.add(attributeName);
            if ( (!(ie instanceof IBooleanScalar))
                 && (!(ie instanceof INumberScalar))
               ) {
                System.out.println(attributeName + " is not a valid attribute");
                System.exit(0);
            }
            if (ie instanceof IBooleanScalar) {
                bsv.setBooleanScalarModel( (IBooleanScalar) ie );
            }
            else {
                bsv.setNumberScalarModel( (INumberScalar) ie );
            }
        }
        catch (Exception ex) {
            System.out.println( "Cannot connect to " + attributeName );
        }
        f.setContentPane( bsv );
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.pack();
        f.setVisible( true );
    }
}
