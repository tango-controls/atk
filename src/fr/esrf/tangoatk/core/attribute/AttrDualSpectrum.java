package fr.esrf.tangoatk.core.attribute;

/*
 * AttrDualSpectrum.java
 *
 * Created on 16 décembre 2003, 09:37
 */


import fr.esrf.tangoatk.core.*;
import fr.esrf.TangoApi.*;
import fr.esrf.Tango.DevFailed;

/**
 *
 * @author  OUNSY
 */
public class AttrDualSpectrum  extends AttrFunctionSpectrum {
    
    /** Creates a new instance of AttrDualSpectrum */
    public AttrDualSpectrum(Device device_x,String attr_x,Device device_y,String attr_y) {
        this.device_x = device_x;
        this.device_y = device_y;
        this.attr_x = attr_x;
        this.attr_y = attr_y;
        this.setXName(attr_x);
        this.setYName(attr_y);
        
        try
        {
            AttributeInfo attr_info = device_x.getAttributeInfo(attr_x);
            this.setXUnit(attr_info.unit);
            attr_info = device_y.getAttributeInfo(attr_y);
            this.setYUnit(attr_info.unit);
        }
        catch(DevFailed e)
        {
            for(int i=0;i<e.errors.length;i++)
            {
                System.out.println("error n° " + i + e.errors[i].reason + " " + e.errors[i].origin);
            }
        } 
    }
    
    public double[] updateX() {
        dev_attr_x = new DeviceAttribute(attr_x);
        double [] xvalue = null;
        try {
                xvalue = new double[dev_attr_x.getDimX()];
                dev_attr_x = device_x.read_attribute(attr_x);
                xvalue = dev_attr_x.extractDoubleArray();
         } catch ( DevFailed e) {
         }
         return xvalue;
    }
    public double[] updateY() {
        dev_attr_y = new DeviceAttribute(attr_y);
        double [] yvalue = null;
        try {
                yvalue = new double[dev_attr_x.getDimX()];
                dev_attr_y = device_y.read_attribute(attr_y);
                yvalue = dev_attr_y.extractDoubleArray();
         } catch ( DevFailed e) {
         }
         return yvalue;
    }
    
    private DeviceAttribute dev_attr_x;
    private DeviceAttribute dev_attr_y;
    private Device device_x;
    private Device device_y;
    String attr_x;
    String attr_y;
    
    
}
