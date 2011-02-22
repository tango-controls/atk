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
 
// File:          IResultListener.java
// Created:       2002-01-17 16:25:21, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-14 11:18:47, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core;
import java.beans.*;
import java.util.EventListener;

/**
 * <code>IResultListener</code> defines the behaviour of an object that listens to results from commands.
 * @see fr.esrf.tangoatk.core.ICommand
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Version$
 */
public interface IResultListener extends IErrorListener {

    /**
     * <code>resultChange</code> is called each time someone has
     * executed a command that does output which this resultlistener
     * is registered with.
     *
     * @param e a <code>ResultEvent</code> value
     */
    public void resultChange(ResultEvent e);

}
