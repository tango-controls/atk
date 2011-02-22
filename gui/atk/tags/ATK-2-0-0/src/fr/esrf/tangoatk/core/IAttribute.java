// File:          IAttribute.java
// Created:       2001-10-30 14:15:34, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-07-17 14:37:9, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import fr.esrf.Tango.*;
import java.util.Map;
import fr.esrf.TangoApi.*;

/**
 * <code>IAttribute</code> is the top interface for all attributes. 
 * It defines the standard behaviour for the attributes. All attributes
 * at this level are images, eg their data is represented as an array in
 * two dimensions.
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Revision$
 */
public interface IAttribute extends IEntity
{
    public final static String OK = "OK";
    public final static String UNKNOWN = "UNKNOWN";
    public final static String VALID   = "VALID";
    public final static String INVALID = "INVALID";
    public final static String ALARM   = "ALARM";

    /**
     * <code>getFormat</code> returns the human readable representation
     * of the format of this attribute. To obtain the numeric code of the
     * format, please consult getPropertyMap("format");
     * @return a <code>String</code> value
     */
    public String getFormat();

    /**
     * <code>getUnit</code> returns the unit of this attribute.
     *
     * @return a <code>String</code> value
     */
    public String getUnit();

    /**
     * <code>getDisplayUnit</code> returns the display unit of this attribute
     *
     * @return a <code>String</code> value
     */
    public String  getDisplayUnit();

    /**
     * <code>getStandardUnit</code> returns the standardunit of this code.
     * The standard unit is the value which must be multiplied with the 
     * value of the attribute to obtain the value in a unit conforming to
     * the standard metric system.
     * @return a <code>double</code> value
     */
    public double getStandardUnit();

    /**
     * <code>getAttribute</code> returns the lowlevel attribute of this
     * attribute.
     * @return a <code>DeviceAttribute</code> value
     */
    public DeviceAttribute getAttribute();
    
    /**
     * <code>getLabel</code> returns the label of this attribute.
     *
     * @return a <code>String</code> value
     */
    public String getLabel();

    /**
     * <code>setLabel</code> sets the label of this attribute
     *
     * @param label a <code>String</code> value
     */
    public void setLabel(String label);
    
    /**
     * <code>getState</code> returns a human-readable representation of 
     * the state.
     * @return a <code>String</code> value
     */
    public String getState();

    /**
     * <code>getType</code> returns a human-readable representaion of the
     * type of this attribute.
     * @return a <code>String</code> value
     */
    public String getType();

    /**
     * <code>getDescription</code> returns the description of this attribute.
     *
     * @return a <code>String</code> value
     */
    public String getDescription();

    /**
     * <code>setDescription</code> sets the description of this attribute
     *
     * @param desc a <code>String</code> value
     */
    public void setDescription(String desc);

    /**
     * <code>setName</code> sets the name of this attribute.
     *
     * @param s a <code>String</code> value
     */
    public void setName(String s);    

    /**
     * <code>isWritable</code> returns true if this attribute is writable
     *
     * @return a <code>boolean</code> value
     */
    public boolean isWritable();

    /**
     * <code>addStateListener</code> adds a listener to state-changes
     * for this attribute.
     * @param l an <code>IAttributeStateListener</code> value
     */
    public void addStateListener(IAttributeStateListener l);

    /**
     * <code>removeStateListener</code> removes a listener to state-changes
     * for this attribute.
     * @param l an <code>IAttributeStateListener</code> value
     */
    public void removeStateListener(IAttributeStateListener l);

    /**
     * An <code>ISetErrorListener</code> is an object that listens to 
     * <tt>setting error</tt> property changes from this sttribute.
     * @param listener an <code>ISetErrorListener</code> value
     */
    public void addSetErrorListener (ISetErrorListener l);


    /**
     * <code>setProperty</code>
     *
     * @param name a <code>String</code> value containing the name of the
     * property
     * @param n a <code>Number</code> value containing the numeric value of
     * the property
     */
    void setProperty(String name, Number n);

    /**
     * <code>setProperty</code>
     *
     * @param name a <code>String</code> value containing the name of the
     * property
     * @param n a <code>Number</code> value containing the value of the
     * property
     * @param editable a <code>boolean</code> value which decides if the
     * property is editable or not.
     */
    void setProperty(String name, Number n, boolean editable);

    /**
     * <code>addImageListener</code> adds a listener to image-changes
     * for this attribute.
     * @param l an <code>IImageListener</code> value
     */
    public void addImageListener(IImageListener l);

    /**
     * <code>removeImageListener</code> removes a listener to image-changes
     *
     * @param l an <code>IImageListener</code> value
     */
    public void removeImageListener(IImageListener l);

    /**
     * <code>getMaxXDimension</code> returns the max x-dimension of the
     * attribute
     * @return an <code>int</code> value
     */
    public int getMaxXDimension();
    
    /**
     * <code>getMaxYDimension</code> returns the max y-dimension of the
     * attribute
     * @return an <code>int</code> value
     */
    public int getMaxYDimension();

    /**
     * <code>getXDimension</code> returns the actual x-dimension of the
     * attribute.
     * @return an <code>int</code> value
     */
    public int getXDimension();

    /**
     * <code>getYDimension</code> returns the actual y-dimension of the
     * attribute
     * @return an <code>int</code> value
     */
    public int getYDimension();

    /**
     * <code>getHeight</code> alias for getYDimension()
     *
     * @return an <code>int</code> value
     */
    public int getHeight();
    
    /**
     * <code>getWidth</code> alias for getXDimension()
     *
     * @return an <code>int</code> value
     */
    public int getWidth();
    
    
    /**
     * <code>hasEvents</code> returns true if the attribute is refreshed by event system
     *
     * @return true if the attribute is refreshed thanks to event system
     *         false if it is refreshed by the ATK refresher thread
     */
    public boolean hasEvents();


    /**
     * Setting this property to true means that the attribute should
     * not read nor distribute new values when its refresh is called
     */
    public void setSkippingRefresh(boolean b);

    public boolean isSkippingRefresh();
        
}
