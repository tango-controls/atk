// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) space 
// Source File Name:   SimpleCommandViewer.java

package fr.esrf.tangoatk.widget.command;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.device.DeviceViewer;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

// Referenced classes of package fr.esrf.tangoatk.widget.command:
//      ScalarCommandInput, SimpleCommandOutput

public class SimpleCommandViewer extends JPanel
    implements IResultListener {

    public ICommand getModel() {
	return model;
    }

    public void setModel(ICommand icommand) {
	if (model != null)
	    model.removeResultListener(this);
	model = icommand;
	if (getBorder() != null)
	    ((TitledBorder)getBorder()).setTitle(model.getName());
	clearInput();
	clearOutput();
	model.addResultListener(this);
	propertyFrame.setModel(model);
	propertyFrame.pack();
	dv.setModel(model.getDevice());
	deviceFrame.getContentPane().add(dv);
	deviceFrame.pack();
	scalarCommandInput.setInputEnabled(model.takesInput());
	Property property = model.getProperty("in_type_desc");
	if (property != null)
	    descriptionLabel.setText(property.getPresentation());
    }

    private void initComponents() {
	infoButton = new JButton();
	deviceButton = new JButton();
	descriptionLabel = new JLabel();
	scalarCommandInput = new ScalarCommandInput();
	simpleCommandOutput = new SimpleCommandOutput();
	setLayout(new GridBagLayout());
	setBorder(new TitledBorder("Not Connected"));
	infoButton.setText("Info");
	infoButton.setToolTipText("Click to get Command info");
	infoButton.addActionListener(new _cls1());
	GridBagConstraints gridbagconstraints = new GridBagConstraints();
	gridbagconstraints.gridx = 1;
	gridbagconstraints.gridy = 3;
	gridbagconstraints.anchor = 17;
	add(infoButton, gridbagconstraints);
	deviceButton.setText("Device");
	deviceButton.addActionListener(new _cls2());
	gridbagconstraints = new GridBagConstraints();
	gridbagconstraints.gridx = 0;
	gridbagconstraints.gridy = 3;
	gridbagconstraints.anchor = 17;
	add(deviceButton, gridbagconstraints);
	descriptionLabel.setFont(new Font("Dialog", 0, 12));
	descriptionLabel.setHorizontalAlignment(2);
	descriptionLabel.setText("Not Connected");
	descriptionLabel.setBorder(new TitledBorder("Description"));
	gridbagconstraints = new GridBagConstraints();
	gridbagconstraints.gridx = 0;
	gridbagconstraints.gridy = 0;
	gridbagconstraints.gridwidth = 3;
	gridbagconstraints.fill = 2;
	gridbagconstraints.insets = new Insets(4, 4, 4, 4);
	add(descriptionLabel, gridbagconstraints);
	scalarCommandInput.setMinimumSize(new Dimension(200, 17));
	scalarCommandInput.setPreferredSize(new Dimension(200, 60));
	scalarCommandInput.addPropertyChangeListener(new _cls3());
	gridbagconstraints = new GridBagConstraints();
	gridbagconstraints.gridx = 0;
	gridbagconstraints.gridy = 1;
	gridbagconstraints.gridwidth = 3;
	gridbagconstraints.fill = 1;
	gridbagconstraints.weighty = 0.01D;
	add(scalarCommandInput, gridbagconstraints);
	gridbagconstraints = new GridBagConstraints();
	gridbagconstraints.gridx = 0;
	gridbagconstraints.gridy = 2;
	gridbagconstraints.gridwidth = 3;
	gridbagconstraints.fill = 1;
	gridbagconstraints.weightx = 0.10000000000000001D;
	gridbagconstraints.weighty = 0.29999999999999999D;
	add(simpleCommandOutput, gridbagconstraints);
    }

    private void scalarCommandInputPropertyChange(PropertyChangeEvent propertychangeevent) {
	if ("execute".equals(propertychangeevent.getPropertyName())) {
	    input.set(0, scalarCommandInput.getInput());
	    if (model == null)
		return;
	    model.execute(input);
	}
    }

    private void deviceButtonActionPerformed(ActionEvent actionevent) {
	deviceFrame.setVisible(true);
    }

    private void infoButtonActionPerformed(ActionEvent actionevent) {
	propertyFrame.setVisible(true);
    }

    public void errorChange(ErrorEvent errorevent) {
	simpleCommandOutput.setResult(errorevent.getError().toString());
    }

    public void resultChange(ResultEvent resultevent) {
	simpleCommandOutput.setResult(resultevent.getResult());
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
	scalarCommandInput.setVisible(flag);
    }

    public boolean isInputVisible() {
	return scalarCommandInput.isVisible();
    }

    public void setOutputVisible(boolean flag) {
	simpleCommandOutput.setVisible(flag);
    }

    public boolean isOutputVisible() {
	return simpleCommandOutput.isVisible();
    }

    public void clearInput() {
	scalarCommandInput.setInput(null);
    }

    public void clearOutput() {
	simpleCommandOutput.setResult("");
    }

    public void setOutputFont(Font font) {
	if (simpleCommandOutput == null) {
	    return;
	} else {
	    simpleCommandOutput.setFont(font);
	    return;
	}
    }

    public Font getOutputFont() {
	if (simpleCommandOutput == null)
	    return getFont();
	else
	    return simpleCommandOutput.getFont();
    }

    public void setInputFont(Font font) {
	if (scalarCommandInput == null) return;

	scalarCommandInput.setFont(font);

    }

    public Font getInputFont() {
	if (scalarCommandInput == null) return getFont();

	return scalarCommandInput.getFont();
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

    private void serializeInit() {
	System.out.println(scalarCommandInput + " " +
			   deviceButton + " " + infoButton);
	scalarCommandInput.addPropertyChangeListener(new _cls3());
	deviceButton.addActionListener(new _cls2());
	infoButton.addActionListener(new _cls1());
    }

    private void readObject(ObjectInputStream objectinputstream)
	throws IOException, ClassNotFoundException {
	objectinputstream.defaultReadObject();
	serializeInit();
    }

    public static void main(String args[]) throws Exception {
	fr.esrf.tangoatk.core.CommandList commandlist =
	    new fr.esrf.tangoatk.core.CommandList();
	commandlist.add("eas/test-api/1/IOString");
	SimpleCommandViewer simplecommandviewer = new SimpleCommandViewer();
	simplecommandviewer.setModel((ICommand)commandlist.get(0));
	JFrame jframe = new JFrame();
	jframe.getContentPane().add(simplecommandviewer);
	jframe.pack();
	jframe.setVisible(true);
    }

    public SimpleCommandViewer() {
	dv = new DeviceViewer();
	deviceFrame = new JFrame();
	input = new Vector();
	propertyFrame = new PropertyFrame();
	initComponents();
	input.add("");
    }

    ICommand model;
    DeviceViewer dv;
    JFrame deviceFrame;
    java.util.List input;
    public PropertyFrame propertyFrame;
    private JButton infoButton;
    private JButton deviceButton;
    private JLabel descriptionLabel;
    private ScalarCommandInput scalarCommandInput;
    private SimpleCommandOutput simpleCommandOutput;




    private class _cls1 implements ActionListener {

	public void actionPerformed(ActionEvent actionevent) {
	    infoButtonActionPerformed(actionevent);
	}
    }


    private class _cls2 implements ActionListener {

	public void actionPerformed(ActionEvent actionevent) {
	    deviceButtonActionPerformed(actionevent);
	}

    }


    private class _cls3 implements PropertyChangeListener {

	public void propertyChange(PropertyChangeEvent propertychangeevent) {
	    scalarCommandInputPropertyChange(propertychangeevent);
	}

    }

}
