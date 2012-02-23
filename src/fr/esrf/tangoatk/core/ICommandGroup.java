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
 
// File:          ICommandGroup.java
// Created:       2006-04-07 09:58:00, poncet
// 
// $Id $
// 
// Description:       

package fr.esrf.tangoatk.core;


/**
 * <code>ICommandGroup</code> is the interface that defines a
 * commandGroup. Generally speaking, to work with command groups, one registers
 * as an EndGroupExecution listener throught the <code>addEndGroupExecutionListener</code>,
 * and calls the <code>execute</code> execute method.
 * The method <code>endGroupExecution</code> of the listener is called when all the commands inside the
 * group are executed.
 * @see fr.esrf.tangoatk.core.IEndGroupExecutionListener
 * @version $Version$
 */
public interface ICommandGroup extends IEntityCollection
{
    /* overrides the setFilter method of EntityList */
    public String getCmdName();

    public void addEndGroupExecutionListener(IEndGroupExecutionListener l);

    public void removeEndGroupExecutionListener(IEndGroupExecutionListener l);
    
    public void execute();
}
