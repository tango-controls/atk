// File:          DevStateSpectrumHelper.java
// Created:       2008-07-07 15:23:16, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;


import java.util.*;

import fr.esrf.tangoatk.core.*;

import fr.esrf.Tango.DevFailed;
import fr.esrf.Tango.DevState;
import fr.esrf.TangoApi.DeviceAttribute;

class DevStateSpectrumHelper implements java.io.Serializable
{
    IAttribute attribute;
    EventSupport propChanges;

    public DevStateSpectrumHelper(IAttribute attribute)
    {
       init(attribute);
    }

    void init(IAttribute attribute)
    {
      setAttribute(attribute);
      propChanges = ((AAttribute) attribute).getPropChanges();
    }


    public void setAttribute(IAttribute attribute)
    {
      this.attribute = attribute;
    }

    public IAttribute getAttribute()
    {
      return attribute;
    }

    protected void setProperty(String name, Number value)
    {
      attribute.setProperty(name, value);
      attribute.storeConfig();
    }

    protected void setProperty(String name, Number value, boolean writable)
    {
      attribute.setProperty(name, value, writable);
    }

    void fireDevStateSpectrumValueChanged(String[] newValue, long timeStamp)
    {
        propChanges.fireDevStateSpectrumEvent( (IDevStateSpectrum) attribute,
                                                newValue, timeStamp);
    }

    void insert(String[] stateSpect)
    {
        DevState[]        devStatesArray = new DevState[stateSpect.length];      
        DeviceAttribute   da = this.attribute.getAttribute();
      
        for (int i = 0; i < stateSpect.length; i++)
        {
           devStatesArray[i] = Device.getStateFromString(stateSpect[i]);
        }

        da.insert(devStatesArray);
    }

    String[] getStateSpectrumValue(DeviceAttribute deviceAttribute) throws DevFailed
    {
        String[]    retval = null;
        DevState[]  devStates = null;
        
        devStates = deviceAttribute.extractDevStateArray();
        retval = new String[devStates.length];
        
        for (int i = 0; i < devStates.length; i++)
        {
            retval[i] = Device.toString(devStates[i]);
        }
        return retval;
    }


    void addDevStateSpectrumListener(IDevStateSpectrumListener l)
    {
        propChanges.addDevStateSpectrumListener(l);
    }


    void removeDevStateSpectrumListener(IDevStateSpectrumListener l)
    {
        propChanges.removeDevStateSpectrumListener(l);
    }



    public String getVersion() {
	return "$Id$";
    }
}
