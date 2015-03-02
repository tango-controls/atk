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
 * AttrDualSpectrum.java
 *
 * Created on 16 december 2003, 09:37
 */
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.AttributeInfo;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.TangoDs.TangoConst;
import fr.esrf.tangoatk.core.Device;
import fr.esrf.tangoatk.core.IDevice;

/**
 *
 * @author  OUNSY
 */
public class AttrDualSpectrum extends AttrFunctionSpectrum {

    protected DeviceAttribute dev_attr_x;
    protected DeviceAttribute dev_attr_y;
    protected Device          device_x;
    protected Device          device_y;
    protected String          attr_x;
    protected String          attr_y;

    /**
     * Creates a new instance of AttrDualSpectrum
     */
    public AttrDualSpectrum (IDevice device_x, String attr_x, IDevice device_y,
            String attr_y) {
        this.device_x = (Device) device_x;
        this.device_y = (Device) device_y;
        this.attr_x = attr_x;
        this.attr_y = attr_y;
        this.setXName( attr_x );
        this.setYName( attr_y );
        try {
            AttributeInfo attr_info = this.device_x.getAttributeInfo(attr_x);
            this.setXUnit(attr_info.unit);
            attr_info = this.device_y.getAttributeInfo(attr_y);
            this.setYUnit(attr_info.unit);
        }
        catch (DevFailed e) {
            for (int i = 0; i < e.errors.length; i++) {
                System.out.println(
                        "error number " + i + e.errors[i].reason
                        + " " + e.errors[i].origin
                );
            }
        }
    }

    public double[] updateX () {
        dev_attr_x = new DeviceAttribute(attr_x);
        double[] xvalue = null;
        try {
            dev_attr_x = device_x.read_attribute(attr_x);
            //managing the different number types
            int data_type = dev_attr_x.getType();
            switch (data_type) {
                case TangoConst.Tango_DEV_UCHAR:
                case TangoConst.Tango_DEV_CHAR:
                    byte[] cval = dev_attr_x.extractCharArray();
                    if (cval == null) {
                        cval = new byte[0];
                    }
                    xvalue = new double[cval.length];
                    for (int i = 0; i < cval.length; i++) {
                        xvalue[i] = (double) cval[i];
                    }
                    break;
                case TangoConst.Tango_DEV_USHORT:
                case TangoConst.Tango_DEV_SHORT:
                    short[] sval = dev_attr_x.extractShortArray();
                    if (sval == null) {
                        sval = new short[0];
                    }
                    xvalue = new double[sval.length];
                    for (int i = 0; i < sval.length; i++) {
                        xvalue[i] = (double) sval[i];
                    }
                    break;
                case TangoConst.Tango_DEV_ULONG:
                case TangoConst.Tango_DEV_LONG:
                    int[] lval = dev_attr_x.extractLongArray();
                    if (lval == null) {
                        lval = new int[0];
                    }
                    xvalue = new double[lval.length];
                    for (int i = 0; i < lval.length; i++) {
                        xvalue[i] = (double) lval[i];
                    }
                    break;
                case TangoConst.Tango_DEV_FLOAT:
                    float[] fval = dev_attr_x.extractFloatArray();
                    if (fval == null) {
                        fval = new float[0];
                    }
                    xvalue = new double[fval.length];
                    for (int i = 0; i < fval.length; i++) {
                        xvalue[i] = (double) fval[i];
                    }
                    break;
                case TangoConst.Tango_DEV_DOUBLE:
                default:
                    xvalue = dev_attr_x.extractDoubleArray();
            }
        }
        catch (DevFailed e) {
            // nothing to do : return null value
        }
        return xvalue;
    }

    public double[] updateY () {
        dev_attr_y = new DeviceAttribute(attr_y);
        double[] yvalue = null;
        try {
            int min = dev_attr_y.getDimX();
            yvalue = new double[min];
            dev_attr_y = device_y.read_attribute(attr_y);
            //managing the different number types
            int data_type = dev_attr_y.getType();
            switch (data_type) {
                case TangoConst.Tango_DEV_UCHAR:
                case TangoConst.Tango_DEV_CHAR:
                    byte[] cval = dev_attr_y.extractCharArray();
                    if (cval == null) {
                        cval = new byte[0];
                    }
                    yvalue = new double[cval.length];
                    for (int i = 0; i < cval.length; i++) {
                        yvalue[i] = (double) cval[i];
                    }
                    break;
                case TangoConst.Tango_DEV_USHORT:
                case TangoConst.Tango_DEV_SHORT:
                    short[] sval = dev_attr_y.extractShortArray();
                    if (sval == null) {
                        sval = new short[0];
                    }
                    yvalue = new double[sval.length];
                    for (int i = 0; i < sval.length; i++) {
                        yvalue[i] = (double) sval[i];
                    }
                    break;
                case TangoConst.Tango_DEV_ULONG:
                case TangoConst.Tango_DEV_LONG:
                    int[] lval = dev_attr_y.extractLongArray();
                    if (lval == null) {
                        lval = new int[0];
                    }
                    yvalue = new double[lval.length];
                    for (int i = 0; i < lval.length; i++) {
                        yvalue[i] = (double) lval[i];
                    }
                    break;
                case TangoConst.Tango_DEV_FLOAT:
                    float[] fval = dev_attr_y.extractFloatArray();
                    if (fval == null) {
                        fval = new float[0];
                    }
                    yvalue = new double[fval.length];
                    for (int i = 0; i < fval.length; i++) {
                        yvalue[i] = (double) fval[i];
                    }
                    break;
                case TangoConst.Tango_DEV_DOUBLE:
                default:
                    yvalue = dev_attr_y.extractDoubleArray();
            }
        }
        catch (DevFailed e) {
            // nothing to do : return null value
        }
        return yvalue;
    }

    /**
     * @return the dev_attr_x
     */
    public DeviceAttribute getDev_attr_x () {
        return dev_attr_x;
    }

    /**
     * @return the dev_attr_y
     */
    public DeviceAttribute getDev_attr_y () {
        return dev_attr_y;
    }

    /**
     * @return the device_x
     */
    public Device getDevice_x () {
        return device_x;
    }

    /**
     * @return the device_y
     */
    public Device getDevice_y () {
        return device_y;
    }

    /**
     * @return the attr_x
     */
    public String getAttr_x () {
        return attr_x;
    }

    /**
     * @return the attr_y
     */
    public String getAttr_y () {
        return attr_y;
    }

}
