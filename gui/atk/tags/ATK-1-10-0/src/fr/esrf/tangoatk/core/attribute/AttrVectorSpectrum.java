package fr.esrf.tangoatk.core.attribute;

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
