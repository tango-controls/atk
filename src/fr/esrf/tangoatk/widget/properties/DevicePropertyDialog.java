/*	Synchrotron Soleil 
 *  
 *   File          :  DevicePropertyDialog.java
 *  
 *   Project       :  ATKWidgetSoleil
 *  
 *   Description   :  
 *  
 *   Author        :  SOLEIL
 *  
 *   Original      :  8 sept. 2005 
 *  
 *   Revision:  					Author:  
 *   Date: 							State:  
 *  
 *   Log: DevicePropertyDialog.java,v 
 *
 */
package fr.esrf.tangoatk.widget.properties;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import fr.esrf.tangoatk.core.Device;
import fr.esrf.tangoatk.core.DeviceFactory;
import fr.esrf.tangoatk.core.DeviceProperty;

/**
 * A modal JDialog to view and modify a device property
 * @author SOLEIL
 */
public class DevicePropertyDialog extends JDialog 
                                  implements PropertyChangeListener {

    protected DeviceProperty devicePropertyModel;

    protected JTextArea valueField;  // A JTextArea representing the value of the device property
    protected JButton ok; // A button to apply the modifications to the device property and close the JDialog
    protected JButton apply; // A button to apply the modifications to the device property
    protected JButton cancel; // A button to close the JDialog
    protected JButton refresh; // A button to refresh the device property
    protected JLabel nameLabel; // A JLabel representing the name of the device property

    protected Box propertyBox; // A Box that contains nameLabel and valueField
    protected Box buttonBox; // A Box that contains all the buttons
    protected Box globalBox; // Box that contains the other Boxes

    /**
     * Constructs the dialog with a parent Dialog and a title.
     * Use <code>setModel</code> to associate a DeviceProperty.
     * @param parent
     * @param title
     */
    public DevicePropertyDialog(Dialog parent, String title) {
        super(parent, title, true);
        initComponents();
    }

    /**
     * Constructs the dialog with a parent Frame and a title.
     * Use <code>setModel</code> to associate a DeviceProperty.
     * @param parent
     * @param title
     */
    public DevicePropertyDialog(Frame parent, String title) {
        super(parent, title, true);
        initComponents();
    }

    /**
     * Constructs the dialog with a parent Dialog and no title.
     * Use <code>setModel</code> to associate a DeviceProperty.
     * @param parent
     * @param title
     */
    public DevicePropertyDialog(Dialog parent) {
        super(parent, "", true);
        initComponents();
    }

    /**
     * Constructs the dialog with a parent Frame and no title.
     * Use <code>setModel</code> to associate a DeviceProperty.
     * @param parent
     * @param title
     */
    public DevicePropertyDialog(Frame parent) {
        super(parent, "", true);
        initComponents();
    }

    /**
     * Constructs the dialog with no parent and no title.
     * Use <code>setModel</code> to associate a DeviceProperty.
     * @param parent
     * @param title
     */
    public DevicePropertyDialog() {
        super((Frame) null, "", true);
        initComponents();
    }

    /**
     * Associates a DeviceProperty with this JDialog
     * @param property the DeviceProperty you wish to view
     */
    public void setDevicePropertyModel(DeviceProperty property) {
        if (devicePropertyModel != null) {
            devicePropertyModel.removePresentationListener(this);
        }
        devicePropertyModel = null;
        devicePropertyModel = property;
        if (devicePropertyModel != null) {
            devicePropertyModel.addPresentationListener(this);
            ok.setEnabled(devicePropertyModel.isEditable());
            apply.setEnabled(devicePropertyModel.isEditable());
            refresh.setEnabled(true);
            valueField.setText(property.getStringValue());
            valueField.setEditable(devicePropertyModel.isEditable());
            nameLabel.setText(devicePropertyModel.getName());
        }
        else {
            ok.setEnabled(false);
            apply.setEnabled(false);
            refresh.setEnabled(false);
            valueField.setText("No value");
            valueField.setEditable(false);
            nameLabel.setText("No property defined");
        }
    }

    /**
     * A method to know the associated device property
     * @return a <code>DeviceProperty</code>
     */
    public DeviceProperty getDevicePropertyModel(){
        return devicePropertyModel;
    }
    
    /**
     * A method to dissociate this JDialog with any <code>DeviceProperty</code>
     */
    public void clearDevicePropertyModel() {
        setDevicePropertyModel(null);
    }

    protected void initComponents() {
        ok = new JButton("ok");
        cancel = new JButton("cancel");
        apply = new JButton("apply");
        refresh = new JButton("refresh");
        nameLabel = new JLabel("No property defined");
        nameLabel.setToolTipText("Property Name");
        valueField = new JTextArea("No value");
        valueField.setToolTipText("Property Value");
        valueField.setAutoscrolls(true);

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                apply();
                cancel();
            }
        });
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                cancel();
            }
        });
        apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                apply();
            }
        });
        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                refresh();
            }
        });

        globalBox = new Box(BoxLayout.Y_AXIS);
        propertyBox = new Box(BoxLayout.X_AXIS);
        buttonBox = new Box(BoxLayout.X_AXIS);

        buttonBox.add(ok);
        buttonBox.add(apply);
        buttonBox.add(cancel);
        buttonBox.add(refresh);

        propertyBox.add(nameLabel);
        propertyBox.add(new JScrollPane(valueField));

        globalBox.add(propertyBox);
        globalBox.add(buttonBox);

        getContentPane().add(globalBox);

        setSize(350, 200);
    }

    protected void apply() {
        if (devicePropertyModel != null) {
            devicePropertyModel.setValue(valueField.getText());
            devicePropertyModel.store();
        }
    }

    protected void cancel() {
        clearDevicePropertyModel();
        dispose();
        setVisible(false);
    }

    protected void refresh() {
        if (devicePropertyModel != null) {
            devicePropertyModel.refresh();
        }
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent arg0) {
        valueField.setText(
                ((DeviceProperty) arg0.getSource()).getStringValue()
        );
    }

    /**
     * This allows you to have an example.<br>
     * To view your own device property, launch with 2 arguments :
     * <ul type="disc">
     * <li> The firts argument is the name of the device </li>
     * <li> The second one is the name of its property </li>
     * </ul>
     */
    public static void main(String[] args) {
        try {
            if (args.length != 0 && args.length != 2) {
                System.out.println("wrong arguments");
                System.exit(2);
            }
            Device d;
            DeviceProperty p;
            System.out.println("accessing device and property...");
            if (args.length > 0) {
                d = DeviceFactory.getInstance().getDevice(args[0]);
                System.out.println("device accessed");
                p = d.getProperty(args[1]);
                System.out.println("property accessed");
            }
            else {
                d = DeviceFactory.getInstance().getDevice("test/testSignal2/1");
                System.out.println("device accessed");
                p = d.getProperty("propertyTest");
                System.out.println("property accessed");
            }
            System.out.println("loading widget...");
            DevicePropertyDialog dpd = new DevicePropertyDialog();
            dpd.setDevicePropertyModel(p);
            dpd.setVisible(true);
            System.out.println("widget closed... goodbye !");
            System.exit(0);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
