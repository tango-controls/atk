
package fr.esrf.tangoatk.widget.properties;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import fr.esrf.tangoatk.core.Property;

public class PropertyFieldEditor extends JTextField implements
        PropertyChangeListener {

    private boolean    askConfirmation      = false;
    private String     titleAskConfirmation = "Confirmation";
    private String     textAskConfirmation  = "Do you want to modify the property ?";
    private Property   model                = null;

    public PropertyFieldEditor() {
        super();
        addActionListener( new ActionListener() {
            public void actionPerformed (ActionEvent e) {
                setProperty();
            }
        });
        setEditable( false );
    }

    public void propertyChange (PropertyChangeEvent evt) {
        if ( "presentation".equals( evt.getPropertyName() )
                && evt.getSource() instanceof Property ) {
            setText( ((Property)evt.getSource()).getPresentation() );
            setEditable( ((Property)evt.getSource()).isEditable() );
        }
    }

    protected void setProperty () {
        if ( askConfirmation ) {
            int ok = JOptionPane.showConfirmDialog( this, textAskConfirmation,
                    titleAskConfirmation, JOptionPane.YES_NO_OPTION );
            if ( ok != JOptionPane.YES_OPTION ) {
                if (model != null) {
                    setText( model.getPresentation() );
                }
                else {
                    setText( "" );
                }
                return;
            }
        }
        if ( model != null ) {
            if (model.isEditable()) {
                model.setValueFromString( getText() );
                setText( model.getPresentation() );
                model.store();
            }
            else {
                setText( model.getPresentation() );
            }
        }
        else {
            setText( "" );
        }
    }

    public Property getModel () {
        return model;
    }

    public void setModel (Property model) {
        if ( this.model != null ) {
            this.model.removePresentationListener( this );
        }
        this.model = model;
        if (model != null) {
            model.addPresentationListener( this );
            setText( model.getPresentation() );
            setEditable( model.isEditable() );
        }
        else {
            setText( "" );
            setEditable( false );
        }
    }

    public void clearModel() {
        setModel( null );
    }

    public boolean isAskConfirmation () {
        return askConfirmation;
    }

    public void setAskConfirmation (boolean askConfirmation) {
        this.askConfirmation = askConfirmation;
    }

    public String getTextAskConfirmation () {
        return textAskConfirmation;
    }

    public void setTextAskConfirmation (String textAskConfirmation) {
        this.textAskConfirmation = textAskConfirmation;
    }

    public String getTitleAskConfirmation () {
        return titleAskConfirmation;
    }

    public void setTitleAskConfirmation (String titleAskConfirmation) {
        this.titleAskConfirmation = titleAskConfirmation;
    }

}
