/*	Synchrotron Soleil 
 *  
 *   File          :  SimpleCommandButtonViewer.java
 *  
 *   Project       :  atkwidget
 *  
 *   Description   :  
 *  
 *   Author        :  SOLEIL
 *  
 *   Original      :  11 août 2005 
 *  
 *   Revision:  					Author:  
 *   Date: 							State:  
 *  
 *   Log: SimpleCommandButtonViewer.java,v 
 *
 */
package fr.esrf.tangoatk.widget.command;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import fr.esrf.tangoatk.core.ICommand;
import fr.esrf.tangoatk.core.command.InvalidCommand;
import fr.esrf.tangoatk.core.command.VoidVoidCommand;


/**
 * @author SOLEIL
 */
public class SimpleCommandButtonViewer extends JButton implements ActionListener {

    protected JFrame argFrame = new JFrame();
    protected AnyCommandViewer acv = new AnyCommandViewer();
    protected ICommand commandModel;
    protected boolean borderVisble;
    protected boolean descriptionVisible;
    protected boolean infoButtonVisible;
    protected boolean deviceButtonVisible;
    protected boolean cancelButtonVisible;

    /**
     * constructor
     */
    public SimpleCommandButtonViewer() {
        super();
        initComponents();
        argFrame.getContentPane().add(acv);
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setText("command-name");
        addActionListener(this);
    }

    public void actionPerformed(ActionEvent actionevent) {

        if (commandModel instanceof InvalidCommand) {
            javax.swing.JOptionPane.showMessageDialog(
                this,
                getName()+ " is not supported. It probably takes an array as input.",
                "Error", 
                1
            );
            return;
        }

        if (commandModel instanceof VoidVoidCommand) {
            commandModel.execute();
            return;
        }

        acv.initialize(commandModel);
        acv.setDeviceButtonVisible(deviceButtonVisible);
        acv.setDescriptionVisible(descriptionVisible);
        acv.setInfoButtonVisible(infoButtonVisible);

        acv.setBorder(null);
        acv.setInputVisible(true);
        if (!commandModel.takesInput()) {
            commandModel.execute();
        }

        argFrame.setTitle(commandModel.getName());
        argFrame.pack();
        argFrame.show();
    }

    /* The getter and setter methods for Bean properties */

    //---------------------------------------------------------

    public void setCommandModel(ICommand icommand) {
        commandModel = icommand;
        setText(commandModel.getNameSansDevice());
        setToolTipText(commandModel.getDevice().toString());
    }

    public ICommand getCommandModel() {
        return commandModel;
    }

    /**
     * Get the value of borderVisble.
     * 
     * @return value of borderVisble.
     */
    public boolean isBorderVisible() {
        return borderVisble;
    }

    /**
     * Set the value of borderVisble.
     * 
     * @param v Value to assign to borderVisble.
     */
    public void setBorderVisible(boolean v) {
        this.borderVisble = v;
    }

    //---------------------------------------------------------

    /**
     * Get the value of descriptionVisible.
     * 
     * @return value of descriptionVisible.
     */
    public boolean isDescriptionVisible() {
        return descriptionVisible;
    }

    /**
     * Set the value of descriptionVisible.
     * 
     * @param v Value to assign to descriptionVisible.
     */
    public void setDescriptionVisible(boolean v) {
        this.descriptionVisible = v;
    }

    //---------------------------------------------------------

    /**
     * Get the value of infoButtonVisible.
     * 
     * @return value of infoButtonVisible.
     */
    public boolean isInfoButtonVisible() {
        return infoButtonVisible;
    }

    /**
     * Set the value of infoButtonVisible.
     * 
     * @param v Value to assign to infoButtonVisible.
     */
    public void setInfoButtonVisible(boolean v) {
        this.infoButtonVisible = v;
    }

    //---------------------------------------------------------

    /**
     * Get the value of deviceButtonVisible.
     * 
     * @return value of deviceButtonVisible.
     */
    public boolean isDeviceButtonVisible() {
        return deviceButtonVisible;
    }

    /**
     * Set the value of deviceButtonVisible.
     * 
     * @param v Value to assign to deviceButtonVisible.
     */
    public void setDeviceButtonVisible(boolean v) {
        this.deviceButtonVisible = v;
    }

    //---------------------------------------------------------

    /**
     * Get the value of cancelButtonVisible.
     * 
     * @return value of cancelButtonVisible.
     */
    public boolean isCancelButtonVisible() {
        return cancelButtonVisible;
    }

    /**
     * Set the value of cancelButtonVisble.
     * 
     * @param v Value to assign to cancelButtonVisble.
     */
    public void setCancelButtonVisible(boolean v) {
        this.cancelButtonVisible = v;
    }

    public static void main(String[] args) throws Exception {
        fr.esrf.tangoatk.core.CommandList clist = new fr.esrf.tangoatk.core.CommandList();
        SimpleCommandButtonViewer cmdBut = new SimpleCommandButtonViewer();
        cmdBut.setCommandModel((ICommand) clist.add("tango/tangotest/1/DevVarFloatArray"));
        JFrame f = new JFrame();
        f.getContentPane().add(cmdBut);
        f.pack();
        f.show();
    } // end of main ()

}