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
 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;
import java.beans.*;
import java.util.Map;
import java.io.*;

import fr.esrf.TangoDs.TangoConst;


/**
 * IEntity is the basic, uh, entity in TangoATK. Normally it is
 * incarnated as an {@link fr.esrf.tangoatk.core.attribute.AAttribute} or
 * a {@link fr.esrf.tangoatk.core.command.ACommand } or one of their subclasses.
 * This inteface is created so that the operations which are common to
 * both the Commands and Attributes can be handled by the same code.
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
    
    
    public void setAlias(String alias);

    public String getAlias();
    
    
    public boolean isOperator();
    
    public boolean isExpert();

    public AtkEventListenerList getListenerList();

}
