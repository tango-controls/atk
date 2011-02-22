//+============================================================================
//Source: package fr.esrf.tangoatk.widget.device;/DeviceStateButtonViewer.java
//
//project :     ATKSoleil
//
//Description: This class hides
//
//Author: SAINTIN
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
package fr.esrf.tangoatk.widget.device;

import javax.swing.JButton;

import fr.esrf.tangoatk.core.Device;
import fr.esrf.tangoatk.core.ErrorEvent;
import fr.esrf.tangoatk.core.IDevice;
import fr.esrf.tangoatk.core.IStateListener;
import fr.esrf.tangoatk.core.StateEvent;
import fr.esrf.tangoatk.widget.util.ATKConstant;

/**
 * @author SAINTIN
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DeviceStateButtonViewer extends JButton implements IStateListener{

    private Device	deviceModel = null;
    private boolean	alarmEnabled = true;
    private boolean viewLabel = true;
    private boolean defaultLabel = true;
    
    
    /**
     * 
     */
    public DeviceStateButtonViewer() {
        super();
        setHorizontalAlignment(JButton.CENTER);
    }


    /* (non-Javadoc)
     * @see fr.esrf.tangoatk.core.IStateListener#stateChange(fr.esrf.tangoatk.core.StateEvent)
     */
    public void stateChange(StateEvent arg0) {
        if(viewLabel && defaultLabel)
            setText(arg0.getState());
        if(alarmEnabled)
            setBackground(ATKConstant.getColor4State(arg0.getState()));
    }


    public boolean isDefaultLabel() {
        return defaultLabel;
    }
    public void setDefaultLabel(boolean defaultLabel) {
        this.defaultLabel = defaultLabel;
    }
    public boolean isViewLabel() {
        return viewLabel;
    }
    
    public void setViewLabel(boolean viewLabel) {
        this.viewLabel = viewLabel;
        if(!viewLabel)
            setText("");
        else
            setText(IDevice.UNKNOWN);
    }
    
    public boolean isAlarmEnabled() {
        return alarmEnabled;
    }
    public void setAlarmEnabled(boolean alarmEnabled) {
        this.alarmEnabled = alarmEnabled;
    }
    public Device getDeviceModel() {
        return deviceModel;
    }
    
    public void setDeviceModel(Device deviceModel) {
        this.deviceModel = deviceModel;
        deviceModel.addStateListener(this);
    }
    
    public void clearModel()
    {
        if(deviceModel != null)
	    {
            deviceModel.removeStateListener(this);
            deviceModel = null;
	    }
    }
   
    /* (non-Javadoc)
     * @see fr.esrf.tangoatk.core.IErrorListener#errorChange(fr.esrf.tangoatk.core.ErrorEvent)
     */
    public void errorChange(ErrorEvent arg0) {
        if(viewLabel && defaultLabel)
            setText(IDevice.UNKNOWN);
        if(alarmEnabled)
            setBackground(ATKConstant.getColor4State(IDevice.UNKNOWN));
        
    }

}
