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
