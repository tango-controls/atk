/*
 * BooleanScalarComboEditor.java Author:Faranguiss Poncet (december 2006)
 */

package fr.esrf.tangoatk.widget.attribute;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
public class SignalScalarButtonSetter extends JButton
                                      implements ActionListener,
                                                IBooleanScalarListener,
                                                INumberScalarListener,
                                                ISetErrorListener {

    protected IAttribute attModel    = null;
    protected String     trueLabel   = "True";
    protected String     falseLabel  = "False";
    protected boolean    setValue;
    protected String     tooltipText = null;
    protected boolean    enabled     = true;
    protected boolean    inError     = false;

    // ---------------------------------------------------
    // Contruction
    // ---------------------------------------------------
    public SignalScalarButtonSetter (boolean setValue) {
        super(setValue?"True":"False");
        this.setValue = setValue;
        attModel = null;
        this.addActionListener(this);
        super.setEnabled(false);
    }

    // ---------------------------------------------------
    // Property stuff
    // ---------------------------------------------------
    public IAttribute getAttModel () {
        return attModel;
    }

    public void setBooleanScalarModel (IBooleanScalar boolModel) {
        if (attModel != null) {
            clearModel();
        }
        if (boolModel == null) return;
        if ( !boolModel.isWritable() ) throw new IllegalArgumentException(
                "SignalScalarButtonSetter: Only accept writeable attribute." );
        inError = false;
        attModel = boolModel;
        manageToolTip();
        manageDisponibility();
        attModel.addSetErrorListener(this);
        attModel.refresh();
        repaint();
    }

    public void setNumberScalarModel (INumberScalar boolModel) {
        if (attModel != null) {
            clearModel();
        }
        if (boolModel == null) return;
        if ( !boolModel.isWritable() ) throw new IllegalArgumentException(
                "SignalScalarButtonSetter: Only accept writeable attribute." );
        inError = false;
        attModel = boolModel;
        manageToolTip();
        manageDisponibility();
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
            attModel.removeSetErrorListener(this);
            attModel = null;
        }
    }

    // ---------------------------------------------------
    // Action Listener
    // ---------------------------------------------------
    public void actionPerformed (ActionEvent e) {
        if (attModel instanceof IBooleanScalar) {
            ((IBooleanScalar)attModel).setValue(setValue);
        }
        else if (attModel instanceof INumberScalar) {
            ((INumberScalar)attModel).setValue( setValue?1:0 );
        }
    }

    public void booleanScalarChange (BooleanScalarEvent e) {
        inError = false;
        manageToolTip();
        manageDisponibility();
    }

    public void numberScalarChange (NumberScalarEvent arg0) {
        inError = false;
        manageToolTip();
        manageDisponibility();
    }

    public void stateChange (AttributeStateEvent e) {
    }

    public void errorChange (ErrorEvent evt) {
        inError = true;
        manageToolTip();
        manageDisponibility();
    }

    public void setErrorOccured (ErrorEvent evt) {
        errorChange(evt);
    }

    public String getFalseLabel () {
        return falseLabel;
    }

    public void setFalseLabel (String falseLabel) {
        this.falseLabel = falseLabel;
        manageText();
        manageToolTip();
        repaint();
    }

    public String getTrueLabel () {
        return trueLabel;
    }

    public void setTrueLabel (String trueLabel) {
        this.trueLabel = trueLabel;
        manageText();
        manageToolTip();
        repaint();
    }

    protected void manageToolTip() {
        if (inError) {
            if (attModel == null) {
                super.setToolTipText("Error");
            }
            else {
                super.setToolTipText( attModel.getName() + " is in Error" );
            }
        }
        else {
            if (tooltipText == null) {
                if (attModel == null) {
                    super.setToolTipText( setValue?trueLabel:falseLabel );
                }
                else {
                    super.setToolTipText( attModel.getName() );
                }
            }
            else {
                super.setToolTipText(tooltipText);
            }
        }
    }

    protected void manageText() {
        if (setValue) {
            setText(trueLabel);
        }
        else {
            setText(falseLabel);
        }
    }

    protected void manageDisponibility() {
        if (inError) {
            super.setEnabled(false);
        }
        else {
            super.setEnabled(enabled);
        }
    }

    @Override
    public void setEnabled (boolean b) {
        enabled = b;
        manageDisponibility();
    }

    @Override
    public void setToolTipText (String text) {
        tooltipText = text;
        manageToolTip();
    }

    // ---------------------------------------------------
    // Main test fucntion
    // ---------------------------------------------------
    public static void main (String args[]) {
        IEntity ie;
        AttributeList attl = new AttributeList();
        JFrame f = new JFrame();
        SignalScalarButtonSetter ssbsTrue = new SignalScalarButtonSetter(true);
        SignalScalarButtonSetter ssbsFalse = new SignalScalarButtonSetter(false);
        String attributeName = "tango/tangotest/1/boolean_scalar";
        SimpleScalarViewer label;
        try {
            if (args.length > 0) {
                attributeName = args[0];
            }
            label = new SimpleScalarViewer();
            label.setText(attributeName);
            label.setHasToolTip(true);
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout( new GridLayout(1,3) );
            ie = attl.add(attributeName);
            if ( (!(ie instanceof IBooleanScalar))
                 && (!(ie instanceof INumberScalar))
               ) {
                System.out.println(attributeName + " is not a valid attribute");
                System.exit(0);
            }
            if (ie instanceof IBooleanScalar) {
                label.setModel( (IBooleanScalar) ie );
                ssbsTrue.setBooleanScalarModel( (IBooleanScalar) ie );
                ssbsFalse.setBooleanScalarModel( (IBooleanScalar) ie );
            }
            else {
                label.setModel( (INumberScalar) ie );
                ssbsTrue.setNumberScalarModel( (INumberScalar) ie );
                ssbsFalse.setNumberScalarModel( (INumberScalar) ie );
            }
            mainPanel.add(label);
            mainPanel.add(ssbsTrue);
            mainPanel.add(ssbsFalse);
            f.setContentPane(mainPanel);
        }
        catch (Exception ex) {
            System.out.println( "Cannot connect to " + attributeName );
        }
        attl.startRefresher();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }

}
