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
 
/*
 * AnyCommandViewer.java
 *
 * Created on July 18, 2002, 4:13 PM
 */

package fr.esrf.tangoatk.widget.command;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.device.DeviceViewer;
import fr.esrf.tangoatk.widget.util.IControlee;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

// Referenced classes of package fr.esrf.tangoatk.widget.command:
//      ScalarCommandInput, CommandOutput

public class AnyCommandViewer extends JPanel
    implements IResultListener {

    ICommand                     model;
    DeviceViewer                 dv;
    JFrame                       deviceFrame;
    public PropertyFrame         propertyFrame;
    private JButton              infoButton;
    private JButton              deviceButton;
    private JLabel               descriptionLabel;
    private IInput               commandInput;
    private CommandOutput        commandOutput;
    private IControlee           controlee = new CommandArgControlee();
    private fr.esrf.tangoatk.widget.util.ButtonBar buttonBar1;


    public AnyCommandViewer()
    {
       model = null;       
       dv = new DeviceViewer();
       deviceFrame = new JFrame();
       propertyFrame = new PropertyFrame();
       infoButton = null;
       deviceButton = null;
       commandInput = null;
       commandOutput = null;
    }


    public AnyCommandViewer(ICommand  icommand)
    {
        model = null;       
        dv = new DeviceViewer();
        deviceFrame = new JFrame();
        propertyFrame = new PropertyFrame();
        infoButton = null;
        deviceButton = null;
        commandInput = null;
        commandOutput = null;
        setModel(icommand);
	try
	{
	   initComponents();
	}
	catch (Exception e)
	{
	    throw new IllegalStateException
		("Please do a initialize(ICommand) with a not null icommand. ");
	}
    }
    
    public void initialize(ICommand icommand)
    {
	
	if (model != null)
	    model.removeResultListener(this);
	model = icommand;
	model.addResultListener(this);
	propertyFrame.setModel(model);
	propertyFrame.pack();
	dv.setModel(model.getDevice());
	deviceFrame.getContentPane().add(dv);
	deviceFrame.pack();

		
	try
	{
	    if (infoButton == null)
	    {
	       initComponents();
	    }
	    else
	    {
	       if (commandInput != null)
	          remove((JPanel) commandInput);
	       if (commandOutput != null)
	          remove((JPanel) commandOutput);
	       commandInput = null;
	       commandOutput = null;
	       createInputOutput();
	    }
	
	    if (getBorder() != null)
	       ((TitledBorder)getBorder()).setTitle(model.getName());
	       
	    
	    Property property = model.getProperty("out_type_desc");
	    if (property != null)
	       descriptionLabel.setText(property.getPresentation());
	    
	    clearInput();
	    clearOutput();
	
	}
	catch (Exception e)
	{
	    throw new IllegalStateException
		("Please do a initialize(ICommand) with a not null icommand. ");
	}
    }
    
    
    public ICommand getModel()
    {
	return model;
    }

    public void setModel(ICommand icommand)
    {
        if (infoButton == null)
	{
	    throw new IllegalStateException
		("This AnyCommandViewer object has never been initialized.\n"+
		"Please use initialize(ICommand) instead of setModel(ICommand). ");
	}
	
	if (model != null)
	    model.removeResultListener(this);
	model = icommand;
	model.addResultListener(this);
	propertyFrame.setModel(model);
	propertyFrame.pack();
	dv.setModel(model.getDevice());
	deviceFrame.getContentPane().add(dv);
	deviceFrame.pack();

	if (commandInput != null)
	   remove((JPanel) commandInput);
	if (commandOutput != null)
	   remove((JPanel) commandOutput);
	commandInput = null;
	commandOutput = null;
	createInputOutput();
	
	if (getBorder() != null)
	    ((TitledBorder)getBorder()).setTitle(model.getName());
	    

	Property property = model.getProperty("out_type_desc");
	if (property != null)
	    descriptionLabel.setText(property.getPresentation());
	    
	clearInput();
	clearOutput();
	
	
    }





    private void initComponents()
    {
	GridBagConstraints gridbagconstraints;


	infoButton = new JButton();
	deviceButton = new JButton();
	descriptionLabel = new JLabel();
	
	setLayout(new GridBagLayout());

        // Border
	setBorder(new TitledBorder("Not Connected"));
	if (getBorder() != null)
	    ((TitledBorder)getBorder()).setTitle(model.getName());


        // Description Label
	descriptionLabel.setFont(new Font("Dialog", 0, 12));
	descriptionLabel.setHorizontalAlignment(2);
	descriptionLabel.setText("Not Connected");
	descriptionLabel.setBorder(new TitledBorder("Description"));
	Property property = model.getProperty("out_type_desc");
	if (property != null)
	    descriptionLabel.setText(property.getPresentation());
	gridbagconstraints = new GridBagConstraints();
	gridbagconstraints.gridx = 0;
	gridbagconstraints.gridy = 0;
	gridbagconstraints.gridwidth = 3;
	gridbagconstraints.fill = 2;
	gridbagconstraints.insets = new Insets(4, 4, 4, 4);
	add(descriptionLabel, gridbagconstraints);
	    
	
	// infoButton
	infoButton.setText("Info");
	infoButton.setToolTipText("Click to get Command info");
	
        infoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoButtonActionPerformed(evt);
            }
        });
	
	gridbagconstraints = new GridBagConstraints();
	gridbagconstraints.gridx = 1;
	gridbagconstraints.gridy = 3;
	gridbagconstraints.anchor = 17;
	add(infoButton, gridbagconstraints);

	// deviceButton
	deviceButton.setText("Device");
	
        deviceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deviceButtonActionPerformed(evt);
            }
        });
	
	gridbagconstraints = new GridBagConstraints();
	gridbagconstraints.gridx = 0;
	gridbagconstraints.gridy = 3;
	gridbagconstraints.anchor = 17;
	add(deviceButton, gridbagconstraints);

        createInputOutput();    

	// ButtonBar
        buttonBar1 = new fr.esrf.tangoatk.widget.util.ButtonBar();
	buttonBar1.setControlee(controlee);
	
	gridbagconstraints = new GridBagConstraints();
	gridbagconstraints.gridx = 0;
	gridbagconstraints.gridy = 4;
	gridbagconstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
	gridbagconstraints.weightx = 1.0;
	add(buttonBar1, gridbagconstraints);

    }
    
    private void createInputOutput()
    {
	GridBagConstraints gridbagconstraints;
	    
        commandInput = CommandInputOutputFactory.getInstance().getInputter4Command(model);
	
        if (commandInput != null)
	   if (commandInput instanceof NoInput)
	      commandInput = null;
	   
	if (commandInput != null)
	{
	   commandInput.setInputEnabled(model.takesInput());
	   
	   JPanel panelInput = (JPanel) commandInput;
	   panelInput.addPropertyChangeListener(new _cls1());
	   gridbagconstraints = new GridBagConstraints();
	   gridbagconstraints.gridx = 0;
	   gridbagconstraints.gridy = 1;
	   gridbagconstraints.gridwidth = 3;
	   gridbagconstraints.fill = java.awt.GridBagConstraints.BOTH;
	   gridbagconstraints.weightx = 1.0;
	   gridbagconstraints.weighty = 0.3;
	   add(panelInput, gridbagconstraints);
	}
	
	
	
	commandOutput = new CommandOutput();

	gridbagconstraints = new GridBagConstraints();
	gridbagconstraints.gridx = 0;
	gridbagconstraints.gridy = 2;
	gridbagconstraints.gridwidth = 3;
	gridbagconstraints.fill = java.awt.GridBagConstraints.BOTH;
	gridbagconstraints.weightx = 1.0;
	gridbagconstraints.weighty = 0.7;
	add(commandOutput, gridbagconstraints);
    }


    private void commandInputPropertyChange(PropertyChangeEvent propertychangeevent)
    {
	if ("execute".equals(propertychangeevent.getPropertyName()))
	{
	    if (model == null)
		return;
	    if (model.takesInput())
	    {
	       java.util.List  inputArg;
	       
	       if (commandInput == null)
	          return;
	       if (commandInput instanceof NoInput)
	          return;
	       inputArg = commandInput.getInput();
	       model.execute(inputArg);
	    }
	    else
	       model.execute();
	}
    }

    private void deviceButtonActionPerformed(ActionEvent actionevent) {
	deviceFrame.setVisible(true);
    }

    private void infoButtonActionPerformed(ActionEvent actionevent) {
	propertyFrame.setVisible(true);
    }

    public void errorChange(ErrorEvent errorevent) {
	if (commandOutput == null) return;
	commandOutput.setResult(errorevent.getError().toString());
    }

    public void resultChange(ResultEvent resultevent) {
	if (commandOutput == null) return;
	commandOutput.setResult(resultevent.getResult());
    }

    public void setDeviceButtonVisible(boolean flag) {
	deviceButton.setVisible(flag);
    }

    public boolean isDeviceButtonVisible() {
	return deviceButton.isVisible();
    }

    public void setDescriptionVisible(boolean flag) {
	descriptionLabel.setVisible(flag);
    }

    public boolean isDescriptionVisible() {
	return descriptionLabel.isVisible();
    }

    public void setInfoButtonVisible(boolean flag) {
	infoButton.setVisible(flag);
    }

    public boolean isInfoButtonVisible() {
	return infoButton.isVisible();
    }

    public void setInputVisible(boolean flag) {
	if (commandInput == null) return;
	commandInput.setVisible(flag);
    }

    public boolean isInputVisible() {
	if (commandInput == null) return false;
	return commandInput.isVisible();
    }

    public void setOutputVisible(boolean flag) {
	if (commandOutput == null) return;
	commandOutput.setVisible(flag);
    }

    public boolean isOutputVisible() {
	if (commandOutput == null) return false;
	return commandOutput.isVisible();
    }

    public void clearInput()
    {
	if (commandInput == null) return;
	   commandInput.setInput(null);
    }

    public void clearOutput()
    {
	if (commandOutput == null) return;
        commandOutput.setResult("");
    }

    public void setOutputFont(Font font)
    {
	if (commandOutput == null) return;
        commandOutput.setFont(font);
    }

    public Font getOutputFont() {
	if (commandOutput == null)
	    return getFont();
	else
	    return commandOutput.getFont();
    }

    public void setInputFont(Font font) {
	if (commandInput == null) return;

	commandInput.setFont(font);

    }

    public Font getInputFont() {
	if (commandInput == null) return getFont();

	return commandInput.getFont();
    }

    public void setDescriptionFont(Font font) {
	if (descriptionLabel == null) return;

	descriptionLabel.setFont(font);
    }

    public Font getDescriptionFont() {
	if (descriptionLabel == null)
	    return getFont();
	else
	    return descriptionLabel.getFont();
    }

    public void setDeviceButtonFont(Font font) {
	if (deviceButton == null) return;
	deviceButton.setFont(font);
	return;
    }

    public Font getDeviceButtonFont() {
	if (deviceButton == null) return getFont();

	return deviceButton.getFont();
    }

    public void setInfoButtonFont(Font font) {
	if (infoButton == null)  return;

	infoButton.setFont(font);
	return;
    }

    public Font getInfoButtonFont() {
	if (infoButton == null) return getFont();
    
	return infoButton.getFont();
    }


    class CommandArgControlee implements IControlee {
	public void ok() {
	    getRootPane().getParent().setVisible(false);
	}
	
	public void cancel() {
	    getRootPane().getParent().setVisible(false);
	}

    }






//    private void readObject(ObjectInputStream objectinputstream)
//	throws IOException, ClassNotFoundException {
//	objectinputstream.defaultReadObject();
//	serializeInit();
//    }

    public static void main(String args[]) throws Exception {
	fr.esrf.tangoatk.core.CommandList commandlist =
	    new fr.esrf.tangoatk.core.CommandList();
	ICommand  ic = (ICommand) commandlist.add("fp/test/1/DevVarCharArray");
	AnyCommandViewer anyCommandViewer = new AnyCommandViewer();
        anyCommandViewer.initialize(ic);

	//anyCommandViewer.setModel((ICommand)commandlist.get(0));
	JFrame jframe = new JFrame();
	jframe.getContentPane().add(anyCommandViewer);
	jframe.pack();
	jframe.setVisible(true);
    }




    private class _cls1 implements PropertyChangeListener {

	public void propertyChange(PropertyChangeEvent propertychangeevent) {
	    commandInputPropertyChange(propertychangeevent);
	}

    }

}
