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
//Source: package tangowidget.command;/CommandButton.java
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
package fr.esrf.tangoatk.widget.command;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import fr.esrf.Tango.DevFailed;
import fr.esrf.tangoatk.core.ConnectionException;
import fr.esrf.tangoatk.core.ICommand;
import fr.esrf.tangoatk.core.command.CommandFactory;

/**
 * A SimpleCommandButtonViewer which command's input can be set.
 * @author HO
 * @see SimpleCommandButtonViewer
 */
public class SettableCommandButtonViewer extends SimpleCommandButtonViewer {

    protected boolean        defaultHelpText      = false;
    protected String         helpText             = "";
    protected String         buttonText           = "";

    protected boolean        askConfirmation      = false;
    protected String         askConfirmationTitle = "Confirmation";
    protected String         askConfirmationText  = "Execute command ?";

    protected String         errorTitle           = "Error";
    protected String         errorText            = "Can not execute command";
    protected String         errorReasonTitle     = "Reason:";

    protected String[]     commandInput         = {""};
    protected List<String> m_argin              = null;

    protected boolean        threadedCommand      = false;

    /**
     * Constructor
     */
    public SettableCommandButtonViewer() {
        super();
        setEnabled(true);
        setHorizontalAlignment(CENTER);
    }

    /**
     * @return Returns the helpText.
     */
    public String getHelpText() {
        return helpText;
    }

    /**
     * Sets the button help text
     * 
     * @param helpText
     *            The help text to set.
     */
    public void setHelpText (String helpText) {
        this.helpText = helpText;
        if ( isDefaultHelpText() ) {
            super.setToolTipText( getText() );
            return;
        }
        if ( helpText.equals( "" ) ) {
            super.setToolTipText( null );
            return;
        }
        super.setToolTipText( helpText );
    }

    /**
     * Returns whether the default help text is used
     * 
     * @return a boolean value. <code>True</code> if the default help text is
     *         used, <code>False</code> otherwise
     */
    public boolean isDefaultHelpText() {
        return defaultHelpText;
    }

    /**
     * Sets whether you wish to use the default help textx
     * 
     * @param defaultHelpText
     *            a boolean value. <code>True</code> to use the default help
     *            text, <code>False</code> otherwise
     */
    public void setDefaultHelpText(boolean defaultHelpText) {
        this.defaultHelpText = defaultHelpText;
        setHelpText(helpText);
    }

    /**
     * Sets the command input
     * @param input
     *            The command input to set, represented as a String[].
     */
    public void setCommandInput (String[] input) {
        this.commandInput = input;
        if ( input == null || input.length == 0  ) {
            if (m_argin != null) {
                m_argin.clear();
            }
            m_argin = null;
            return;
        }
        m_argin = new Vector<String>();
        for (int i = 0; i < input.length; i++) {
            if ( "".equals(input[i]) ) {
                m_argin.clear();
                m_argin = null;
                return;
            }
            m_argin.add( input[i] );
        }
    }

    /**
     * Returns the command input
     * 
     * @return the command input represented as a String[]
     */
    public String[] getCommandInput() {
        return commandInput;
    }

    @Override
    public void actionPerformed (ActionEvent event) {
        if ( getCommandModel() == null ) {
            return;
        }

        int result = JOptionPane.OK_OPTION;

        if (askConfirmation) {
            result = JOptionPane.showConfirmDialog(
                    this,
                    askConfirmationText,
                    askConfirmationTitle,
                    JOptionPane.YES_NO_OPTION
            );
        }

        if (result == JOptionPane.OK_OPTION) {
            if ( m_argin != null && getCommandModel().takesInput() ) {
                if ( isThreadedCommand() ) {
                    new Thread() {
                        public void run () {
                            try {
                                getCommandModel().execute(m_argin);
                            }
                            catch (Throwable t) {
                                displayErrorMessage( t, getCommandModel() );
                            }
                        }
                    }.start();
                }
                else {
                    try {
                        getCommandModel().execute(m_argin);
                    }
                    catch (Throwable t) {
                        displayErrorMessage( t, getCommandModel() );
                    }
                }
            }
            else {
                super.actionPerformed(event);
            }
        }
    }

    /**
     * @return Returns the button text.
     */
    public String getButtonText() {
        return buttonText;
    }

    /**
     * @param buttonText The button text to set.
     */
    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
        setText(buttonText);
    }

    /**
     * Returns whether execution confirmation will be asked to user.
     * 
     * @return a boolean value
     */
    public boolean isAskConfirmation() {
        return askConfirmation;
    }

    /**
     * Sets whether execution confirmation will be asked to user.
     * 
     * @param askConfirmation
     *            a boolean value
     */
    public void setAskConfirmation(boolean askConfirmation) {
        this.askConfirmation = askConfirmation;
    }

    /**
     * Returns the execution confirmation text
     * 
     * @return a String
     */
    public String getAskConfirmationText() {
        return askConfirmationText;
    }

    /**
     * Sets the execution confirmation text
     * 
     * @param textAskConfirmation
     *            a String
     */
    public void setAskConfirmationText(String textAskConfirmation) {
        if( !textAskConfirmation.equals("") ) {
            this.askConfirmationText = textAskConfirmation;
        }
    }

    /**
     * Returns the confirmation title
     * 
     * @return the confirmation title
     */
    public String getAskConfirmationTitle() {
        return askConfirmationTitle;
    }

    /**
     * Sets the confirmation title
     * 
     * @param titleAskConfirmation
     */
    public void setAskConfirmationTitle(String titleAskConfirmation) {
        if( !titleAskConfirmation.equals("") ) {
            this.askConfirmationTitle = titleAskConfirmation;
        }
    }

    /**
     * Displays an error message in a dialog.
     * 
     * @param error
     *            The throwable which represents the reason of the error
     * @param command
     *            The command which is the source of the error
     */
    protected void displayErrorMessage(Throwable error, ICommand command) {
        StringBuffer errorMessage = new StringBuffer(errorText);
        if (command != null) {
            errorMessage.append(" ").append( command.getName() );
        }
        if (error != null) {
            errorMessage.append(errorReasonTitle);
            errorMessage.append("\n-").append( error.getClass() ).append("-");
            errorMessage.append("\n").append( error.getMessage() );
        }
        JOptionPane.showMessageDialog(
                this,
                error,
                errorMessage.toString(),
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * @return the errorTitle
     */
    public String getErrorTitle () {
        return errorTitle;
    }

    /**
     * @param errorTitle the errorTitle to set
     */
    public void setErrorTitle (String errorTitle) {
        if ( errorTitle != null && !"".equals(errorTitle) ) {
            this.errorTitle = errorTitle;
        }
    }

    /**
     * @return the errorText
     */
    public String getErrorText () {
        return errorText;
    }

    /**
     * @param errorText the errorText to set
     */
    public void setErrorText (String errorText) {
        if ( errorText != null && !"".equals(errorText) ) {
            this.errorText = errorText;
        }
    }

    /**
     * @return the errorReasonTitle
     */
    public String getErrorReasonTitle () {
        return errorReasonTitle;
    }

    /**
     * @param errorReasonTitle the errorReasonTitle to set
     */
    public void setErrorReasonTitle (String errorReasonTitle) {
        if ( errorReasonTitle != null && !"".equals(errorReasonTitle) ) {
            this.errorReasonTitle = errorReasonTitle;
        }
    }

    /**
     * Returns whether command will be launched through a thread or not.
     * 
     * @return a boolean value. <Code>True</code> if a thread will be used,
     *         <code>False</code> otherwise.
     */
    public boolean isThreadedCommand () {
        return threadedCommand;
    }

    /**
     * Sets whether to launch command through a thread or not.
     * 
     * @param threadedCommand
     *            a boolean value. <Code>True</code> if a thread will be used,
     *            <code>False</code> otherwise.
     */
    public void setThreadedCommand (boolean threadedCommand) {
        this.threadedCommand = threadedCommand;
    }

    public static void main (String[] args) throws ConnectionException,
            DevFailed {
        JFrame frame = new JFrame();
        String commandName = "tango/tangotest/1/DevShort";
        if (args.length > 0) {
            commandName = args[0];
        }
        SettableCommandButtonViewer f = new SettableCommandButtonViewer();
        f.setCommandModel(
                CommandFactory.getInstance().getCommand(commandName)
        );
        f.setAskConfirmation(true);
        f.setDefaultHelpText(true);
        frame.getContentPane().add(f, BorderLayout.CENTER);
        frame.setSize(640, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
