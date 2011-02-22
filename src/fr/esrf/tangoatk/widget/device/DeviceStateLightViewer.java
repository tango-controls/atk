package fr.esrf.tangoatk.widget.device;

import javax.swing.JButton;
import javax.swing.JFrame;

import fr.esrf.tangoatk.core.Device;
import fr.esrf.tangoatk.core.DeviceFactory;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.IDevice;
import fr.esrf.tangoatk.core.IErrorListener;
import fr.esrf.tangoatk.core.IStateListener;
import fr.esrf.tangoatk.core.StateEvent;
import fr.esrf.tangoatk.widget.util.ATKConstant;

/**
 * <code>DeviceStateLightViewer</code> is a viewer to surveil the state of a
 * {@link fr.esrf.tangoatk.core.Device}represented by a DEL. DEL color
 * corresponds to the color defined for TANGO. <br>
 * See ATKWidget Manual to check these colors.
 */
public class DeviceStateLightViewer extends JButton implements IStateListener,
        IErrorListener {

    private Device deviceModel;
    private boolean viewLabel;
    private int chosenLabel;
    private final static String defaultName = "No device defined";

    /**
     * int representing the option to see device's name as label
     */
    public final static int name = 0;

    /**
     * int representing the option to see device's state as label
     */
    public final static int state = 1;

    /**
     * int representing the option to see device's name and state as label
     */
    public final static int nameAndState = 2;
    
    /**
     * int representing the option to see a custom as label
     */
    public final static int customLabel = 3;

    /**
     * Contructs a DeviceStateLightViewer with no label (ie you won't see the
     * state as label, unless you use the setStateTextVisible method).
     */
    public DeviceStateLightViewer() {
        super();
        chosenLabel = name;
        viewLabel = false;
        setDeviceModel(null);
    }

    /**
     * Contructs a DeviceStateLightViewer.
     * 
     * @param textLabel
     *            a boolean to know whether you want to see the state as label
     *            or not.
     */
    public DeviceStateLightViewer(Device device, int kindOfLabel,
            boolean viewLbl) {
        super();
        chosenLabel = kindOfLabel;
        viewLabel = viewLbl;
        setDeviceModel(device);
    }

    /**
     * <code>setModel</code> sets the devicePropertyModel of this viewer. If the textLabel
     * property is not set, the name of the device is shown on the textLabel.
     * 
     * @param device
     *            a <code>Device</code> to surveil
     */
    public void setDeviceModel(Device device) {
        if (deviceModel != null) {
            deviceModel.removeStateListener(this);
            deviceModel.removeErrorListener(this);
        }
        deviceModel = null;
        deviceModel = device;
        if (deviceModel != null) {
            deviceModel.addStateListener(this);
            deviceModel.addErrorListener(this);
            manageLabel(deviceModel.getState());
            setIcon(ATKConstant.getIcon4State(deviceModel.getState()));
        }
        else {
            manageLabel("Unknown state");
            setIcon(ATKConstant.getIcon4State("UNKNOWN"));
        }
    }

    /**
     * Clears all devicePropertyModel and listener attached to the components
     */
    public void clearDeviceModel(){
        setDeviceModel(null);
    }
    
    /**
     * <code>getModel</code> gets the devicePropertyModel of this stateviewer.
     * 
     * @return a <code>Device</code> value
     */
    public Device getDeviceModel() {
        return deviceModel;
    }

    /**
     * To set or unset devicePropertyModel's label as text of this JLabel
     * 
     * @param b
     *            a boolean to view or not label of this JLabel. if
     *            <code>true</code> and devicePropertyModel is not null, it will show label
     *            depending on the chosen label. if devicePropertyModel is null a message in
     *            label will warn user that there is no attribute
     * @see setChosenLabel
     */
    public void setViewLabel(boolean b) {
        viewLabel = b;
        if (deviceModel != null) {
            manageLabel(deviceModel.getState());
        } else {
            manageLabel("unknown state");
        }
    }

    /**
     * To know whether devicePropertyModel's label is text of this JLabel or not
     */
    public boolean isViewLabel() {
        return viewLabel;
    }

    /**
     * A method to know which kind of label you want to have.
     * The label will be visible only if you used <code>setViewLabel(true)</code>
     * @param chosen the kind of label you want to have
     *               use the associated static variables.
     */
    public void setChosenLabel(int chosen) {
        chosenLabel = chosen;
    }

    private void manageLabel(String myState) {
        if (viewLabel) {
            switch (chosenLabel) {
            case name:
                if (deviceModel == null) {
                    setText(defaultName);
                } else {
                    setText(deviceModel.getName());
                }
                break;
            case state:
                setText(myState);
                break;
            case nameAndState:
                if (deviceModel == null) {
                    setText(defaultName + " : " + myState);
                } else {
                    setText(deviceModel.getName() + " : " + myState);
                }
                break;
            default:
                ;
            }// end switch (chosenLabel)
        }// end if (viewLabel)
    }// end manageLabel()

    // --------------------------------------------------
    // State listener
    // --------------------------------------------------
    public void stateChange(StateEvent evt) {
        manageLabel(evt.getState());
        setIcon(ATKConstant.getIcon4State(evt.getState()));
        repaint();
    }

    public void errorChange(ErrorEvent evt) {
        manageLabel("Error occured");
        setIcon(ATKConstant.getIcon4State(IDevice.UNKNOWN));
        repaint();
    }

    /**
     * Main class, so you can have an example.
     * You can monitor your own device by giving its full path name in argument
     */
    public static void main(String[] args) {
        try {
            Device d;
            if (args.length != 0) {
                d = DeviceFactory.getInstance().getDevice(args[0]);
            }
            else {
                d = DeviceFactory.getInstance().getDevice("tango/tangotest/1");
            }
            DeviceStateLightViewer dslv = new DeviceStateLightViewer(d,DeviceStateLightViewer.nameAndState,true);
            JFrame f = new JFrame(dslv.getDeviceModel().getName());
            f.getContentPane().add(dslv);
            f.setSize(300, 50);
            f.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
