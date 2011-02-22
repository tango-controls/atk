/*
 * BooleanScalarComboEditor.java Author:Faranguiss Poncet (december 2006)
 */

package fr.esrf.tangoatk.widget.attribute;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.AttributeStateEvent;
import fr.esrf.tangoatk.core.BooleanScalarEvent;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.IAttribute;
import fr.esrf.tangoatk.core.IBooleanScalar;
import fr.esrf.tangoatk.core.IBooleanScalarListener;
import fr.esrf.tangoatk.core.IEntity;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.core.INumberScalarListener;
import fr.esrf.tangoatk.core.ISetErrorListener;
import fr.esrf.tangoatk.core.NumberScalarEvent;

/**
 * A class to set the value of a signal attribute (this means an attribute
 * representing a boolean value, but of type BooleanScalar or NumberScalar) by
 * selecting the value True or False in a combobox.
 * 
 * @author GIRARDOT
 */
public class SignalScalarComboEditor extends JComboBox
                                     implements ActionListener,
                                                IBooleanScalarListener,
                                                INumberScalarListener,
                                                ISetErrorListener {

    protected IAttribute           attModel     = null;
    protected String               trueLabel    = "True";
    protected String               falseLabel   = "False";
    protected DefaultComboBoxModel comboModel   = null;
    protected String               defActionCmd = "setAttActionCmd";
    protected String[]             optionList   = {trueLabel, falseLabel};
    final protected static int     trueIndex    = 0;
    final protected static int     falseIndex   = 1;

    // ---------------------------------------------------
    // Contruction
    // ---------------------------------------------------
    public SignalScalarComboEditor () {
        attModel = null;
        comboModel = new DefaultComboBoxModel(optionList);
        this.setModel(comboModel);
        this.setActionCommand(defActionCmd);
        this.addActionListener(this);
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
        if ( boolModel == null ) return;
        if ( !boolModel.isWritable() ) throw new IllegalArgumentException(
                "SignalScalarComboEditor: Only accept writeable attribute." );
        optionList = new String[] { trueLabel, falseLabel };
        comboModel = new DefaultComboBoxModel( optionList );
        this.setModel( comboModel );
        attModel = boolModel;
        ((IBooleanScalar)attModel).addBooleanScalarListener( this );
        attModel.addSetErrorListener(this);
        attModel.refresh();
        repaint();
    }

    public void setNumberScalarModel (INumberScalar boolModel) {
        if ( attModel != null ) {
            clearModel();
        }
        if ( boolModel == null ) return;
        if ( !boolModel.isWritable() ) throw new IllegalArgumentException(
                "SignalScalarComboEditor: Only accept writeable attribute." );
        optionList = new String[] { trueLabel, falseLabel };
        comboModel = new DefaultComboBoxModel( optionList );
        this.setModel( comboModel );
        attModel = boolModel;
        ((INumberScalar)attModel).addNumberScalarListener(this);
        attModel.addSetErrorListener(this);
        attModel.refresh();
        repaint();
    }

    public void clearModel () {
        if (attModel != null) {
            if (attModel instanceof IBooleanScalar) {
                ((IBooleanScalar)attModel).removeBooleanScalarListener(this);
            }
            else if (attModel instanceof INumberScalar) {
                ((INumberScalar)attModel).removeNumberScalarListener(this);
            }
            attModel.removeSetErrorListener( this );
            attModel = null;
        }
    }

    // ---------------------------------------------------
    // Action Listener
    // ---------------------------------------------------
    public void actionPerformed (ActionEvent e) {
        JComboBox cb = null;
        boolean selectedOption;
        if ( !( e.getActionCommand().equals(defActionCmd) ) ) {
            return;
        }
        if ( attModel == null ) {
            return;
        }
        cb = (JComboBox) e.getSource();
        if ( cb.getSelectedIndex() < 0 ) {
            return;
        }
        selectedOption = ( cb.getSelectedIndex() == trueIndex );
        if (attModel instanceof IBooleanScalar) {
            if ( selectedOption == true ) {
                ((IBooleanScalar)attModel).setValue(true);
            }
            else {
                ((IBooleanScalar)attModel).setValue(false);
            }
        }
        else if (attModel instanceof INumberScalar) {
            if ( selectedOption == true ) {
                ((INumberScalar)attModel).setValue(1);
            }
            else {
                ((INumberScalar)attModel).setValue(0);
            }
        }
    }

    // ---------------------------------------------------
    // IBooleanScalarListener listener
    // ---------------------------------------------------
    // Listen on "setpoint" change
    // this is not clean yet as there is no setpointChangeListener
    // Listen on valueChange and readSetpoint
    public void booleanScalarChange (BooleanScalarEvent e) {
        boolean setpoint;
        if ( hasFocus() ) setpoint = ((IBooleanScalar)attModel).getDeviceSetPoint();
        else setpoint = ((IBooleanScalar)attModel).getSetPoint();
        changeCurrentSelection( setpoint );
    }

    public void stateChange (AttributeStateEvent e) {
    }

    public void errorChange (ErrorEvent evt) {
        disableExecution();
        setSelectedIndex( -1 );
        repaint();
        enableExecution();
    }

    // ---------------------------------------------------
    // ISetErrorListener listener
    // ---------------------------------------------------
    public void setErrorOccured (ErrorEvent evt) {
        if ( attModel == null ) return;
        if ( evt.getSource() != attModel ) return;
        if (attModel instanceof IBooleanScalar) {
            changeCurrentSelection( ((IBooleanScalar)attModel).getDeviceSetPoint() );
        }
        else if (attModel instanceof INumberScalar) {
            changeCurrentSelection( ((INumberScalar)attModel).getNumberScalarDeviceSetPoint() != 0 );
        }
    }

    protected void changeCurrentSelection (boolean newValue) {
        disableExecution();
        if ( newValue == true ) setSelectedIndex(trueIndex);
        else setSelectedIndex(falseIndex);
        repaint();
        enableExecution();
    }

    public void enableExecution () {
        this.setActionCommand(defActionCmd);
    }

    public void disableExecution () {
        this.setActionCommand("dummy");
    }

    // ---------------------------------------------------
    // Main test fucntion
    // ---------------------------------------------------
    static public void main (String args[]) {
        IEntity ie;
        AttributeList attl = new AttributeList();
        JFrame f = new JFrame();
        SignalScalarComboEditor bsce = new SignalScalarComboEditor();
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
                bsce.setBooleanScalarModel( (IBooleanScalar) ie );
            }
            else {
                bsce.setNumberScalarModel( (INumberScalar) ie );
            }
        }
        catch (Exception ex) {
            System.out.println( "Cannot connect to " + attributeName );
        }
        attl.startRefresher();
        f.setContentPane(bsce);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

    public String getFalseLabel () {
        return falseLabel;
    }

    public void setFalseLabel (String falseLabel) {
        this.falseLabel = falseLabel;
    }

    public String getTrueLabel () {
        return trueLabel;
    }

    public void setTrueLabel (String trueLabel) {
        this.trueLabel = trueLabel;
    }

    public void numberScalarChange (NumberScalarEvent arg0) {
        boolean setpoint;
        if ( hasFocus() ) {
            setpoint = ( ( (INumberScalar) attModel )
                    .getNumberScalarDeviceSetPoint() != 0 );
        }
        else {
            setpoint = ( ( (INumberScalar) attModel )
                    .getNumberScalarSetPoint() != 0 );
        }
        changeCurrentSelection(setpoint);
    }
}
