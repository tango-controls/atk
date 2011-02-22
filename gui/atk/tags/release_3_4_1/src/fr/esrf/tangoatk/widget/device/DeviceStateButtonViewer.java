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
    public void stateChange(StateEvent evt) {
        if(viewLabel && defaultLabel)
            setText(evt.getState());
        if(alarmEnabled)
            setBackground(ATKConstant.getColor4State(evt.getState(), deviceModel.getInvertedOpenClose(), deviceModel.getInvertedInsertExtract()));
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
    
    public void setDeviceModel(Device devModel) 
    {
        if (deviceModel != null)
            clearModel();
        if (devModel == null)
            return;
        deviceModel = devModel;
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
    public void errorChange(ErrorEvent evt)
    {
        if(viewLabel && defaultLabel)
            setText(IDevice.UNKNOWN);
        if(alarmEnabled)
            setBackground(ATKConstant.getColor4State(IDevice.UNKNOWN));
        
    }

}
