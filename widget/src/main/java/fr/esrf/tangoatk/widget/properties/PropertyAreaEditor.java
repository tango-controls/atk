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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import fr.esrf.tangoatk.core.Property;

/**
 * A JTextArea which allows to edit a Property
 * @author SAINTIN, GIRARDOT
 */
public class PropertyAreaEditor extends JTextArea implements MouseListener,
        ActionListener, PropertyChangeListener, KeyListener {

    private boolean    askConfirmation      = false;
    private String     titleAskConfirmation = "Confirmation";
    private String     textAskConfirmation  = "Do you want to modify the property ?";
    private String     sendValueText        = "Apply modification";
    private JPopupMenu sendPopupMenu        = null;
    private JMenuItem  sendMenu             = null;
    private Property   model                = null;

    /**
     * Constructor
     */
    public PropertyAreaEditor () {
        super();
        addMouseListener( this );
        addKeyListener( this );
        try {
            sendPopupMenu = new JPopupMenu();
            sendMenu = new JMenuItem();
            sendPopupMenu.setDoubleBuffered( true );
            sendMenu.setText( sendValueText );
            sendPopupMenu.add( sendMenu );
            sendMenu.addActionListener( this );
            setEditable( false );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBackground (Color arg0) {
        if ( arg0 == null ) arg0 = Color.LIGHT_GRAY;
        super.setBackground( arg0 );
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
        if ( textAskConfirmation.equals( "" ) ) return;
        this.textAskConfirmation = textAskConfirmation;
    }

    public String getTitleAskConfirmation () {
        return titleAskConfirmation;
    }

    public void setTitleAskConfirmation (String titleAskConfirmation) {
        if ( titleAskConfirmation.equals( "" ) ) return;
        this.titleAskConfirmation = titleAskConfirmation;
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

    public void propertyChange (PropertyChangeEvent evt) {
        if ( "presentation".equals( evt.getPropertyName() )
                && evt.getSource() instanceof Property ) {
            setText( ((Property)evt.getSource()).getPresentation() );
            setEditable( ((Property)evt.getSource()).isEditable() );
        }
    }

    public void mouseClicked (MouseEvent arg0) {
    }

    public void mousePressed (MouseEvent arg0) {
        if ( arg0.getClickCount() == 1
                && arg0.getButton() == MouseEvent.BUTTON3 ) {
            sendPopupMenu.show(
                    arg0.getComponent(),
                    arg0.getX(),
                    arg0.getY()
            );
        }
    }

    public void mouseReleased (MouseEvent arg0) {
    }

    public void mouseEntered (MouseEvent arg0) {
    }

    public void mouseExited (MouseEvent arg0) {
    }

    public void actionPerformed (ActionEvent arg0) {
        setProperty();
    }

    public String getSendValueText () {
        return sendValueText;
    }

    public void setSendValueText (String sendValueText) {
        this.sendValueText = sendValueText;
        sendMenu.setText( sendValueText );
    }

    public void keyPressed (KeyEvent e) {
        if ( e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK ) {
            if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                e.consume();
                setProperty();
            }
        }
    }

    public void keyReleased (KeyEvent e) {
    }

    public void keyTyped (KeyEvent e) {
    }

}
