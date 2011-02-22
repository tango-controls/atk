// File:          TangoEntity.java
// Created:       2001-09-28 10:54:51, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-05-17 11:10:50, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;
import java.beans.*;
import fr.esrf.TangoDs.TangoConst;
import java.util.Map;
import java.io.*;

/**
 * IEntity is the basic, uh, entity in TangoATK. Normally it is
 * incarnated as an {@link fr.esrf.tangoatk.core.AAttribute} or
 * a {@link fr.esrf.tangoatk.core.ACommand } or one of their subclasses.
 * This inteface is created so that the operations which are common to
 * both the Commands and Attributes can be handled by the same code.
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version 1.0
 */
public interface IEntity extends TangoConst, IRefreshee, Serializable {

    /**
     * Gets the name of this <code>IEntity</code>
     *
     * @return  <code>String</code> the name of the <code>IEntity</code>
     */
    public String getName();

    public String getNameSansDevice();
    
    /**
     * <code>getProperty</code> returns property with the name given in 
     * the first parameter.
     * @param name a <code>String</code> value
     * @return an <code>Property</code> value
     */
    public Property getProperty(String name);

    /**
     * <code>getPropertyMap</code> returns a Map containing this entitys
     * properties.
     * @return a <code>Map</code> value
     */
    public Map getPropertyMap();

    /**
     * An <code>IErrorListener</code> is an object that listens to 
     * <tt>error</tt> property changes from this object.
     * @param listener an <code>IErrorListener</code> value
     */
    public void addErrorListener(IErrorListener listener);

    /**
     * Removes the given <code>IErrorListener</code> from this objects 
     * list of errorlisteners.
     * @param listener an <code>IErrorListener</code> value
     */
    public void removeErrorListener(IErrorListener listener);


    /**
     * <code>getDevice</code> returns the device this IEntity belongs to.
     *
     * @return a <code>fr.esrf.tangoatk.core.Device</code> value
     */
    public fr.esrf.tangoatk.core.Device getDevice();

    public void storeConfig();

}
