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
 
package fr.esrf.tangoatk.core.util;

/*
 * AttrVectorSpectrum.java
 *
 * Created on 12 septembre 2003, 16:22
 */


import fr.esrf.tangoatk.core.*;
import fr.esrf.TangoApi.*;
import fr.esrf.Tango.DevFailed;

/**
 *
 * @author  OUNSY
 */
public class AttrVectorSpectrum extends AttrFunctionSpectrum {
    
    /** Creates a new instance of AttrVectorSpectrum */
    public AttrVectorSpectrum(Device[] devices, String attr_name) {
        this.devices = devices;
        this.attr_name = attr_name;
        value = new double[devices.length];
        xvalue = new double[devices.length];
        for (int i=0 ; i < devices.length ; i++)
        {
            xvalue[i] = (double)i;
        }
    }
    
    public double[] updateX() {
        return xvalue;
    }
    public double[] updateY() {
        DeviceAttribute dev_attr = new DeviceAttribute(attr_name);
        for (int i=0 ; i < devices.length ; i++)
        {
            try {
                dev_attr = devices[i].read_attribute(attr_name);
                value[i] = dev_attr.extractDouble();
            } catch ( DevFailed e) {
                value[i] = Double.NaN;
            }
        }
        return value;
    }
    
    private double[] value;
    private double[] xvalue;
    private Device[] devices;
    String attr_name;
    
}
