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
 
// File:          ICommand.java
// Created:       2002-01-14 10:37:18, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-06-24 13:51:32, assum>
// 
// $Id$
// 
// Description:

package fr.esrf.tangoatk.core;

import fr.esrf.TangoDs.*;

/**
 * <code>ICommand</code> is the interface that defines a
 * command. Generally speaking, to work with commands, one registers
 * as a result listener throught the <code>addResultListener</code>,
 * and calls the <code>execute</code> execute method that pleases
 * you. The result of the command is delivered shortly after through
 * the method <code>resultChange</code> in your
 * <code>IResultListener</code>
 * @see fr.esrf.tangoatk.core.IResultListener
 * @version $Version$
 */
public interface ICommand extends IEntity, TangoConst {
    
    /**
     * <code>execute</code> executes the command without input.
     * @see #execute(java.util.List input)
     */
    public void execute();

    /**
     * <code>execute</code> executes the command and uses the
     * <code>input</code> in whatever way it feels
     * appropriate. Results are published to all listeners which have
     * registered by calling <code>addResultListener</code>
     * @param input a <code>java.util.List</code> value
     */
    public void execute(java.util.List input);

    /**
     * <code>addResultListener</code> adds a listener to the result of
     * executing this command. 
     *
     * @param listener an <code>IResultListener</code> value
     * @see fr.esrf.tangoatk.core.IResultListener
     */
    public void addResultListener(IResultListener listener);    

    /**
     * <code>removeResultListener</code> removes a listenener from
     * this command
     *
     * @param listener an <code>IResultListener</code> value
     */
    public void removeResultListener(IResultListener listener);

    /**
     * <code>takesInput</code> returns true if this command takes input
     *
     * @return a <code>boolean</code> value
     */
    public boolean takesInput();

    /**
     * <code>takesArrayInput</code> returns true if this command takes
     * array-input
     *
     * @return a <code>boolean</code> value
     */
    public boolean takesArrayInput();

    /**
     * <code>takesArrayInput</code> returns true if this command takes
     * array-input
     *
     * @return a <code>boolean</code> value
     */
    public boolean takesTableInput();

    /**
     * <code>takesScalarInput</code> return strue if this command
     * takes scalar-input
     *
     * @return a <code>boolean</code> value
     */
    public boolean takesScalarInput();
    
    /**
     * <code>doesOutput</code> returns true if this command does ouput
     *
     * @return a <code>boolean</code> value
     */
    public boolean doesOutput();

    /**
     * <code>doesArrayOutput</code> returns true if this command does
     * output in form of an array
     *
     * @return a <code>boolean</code> value
     */
    public boolean doesArrayOutput();
    
    /**
     * <code>getInTypeElemName</code> returns the type name of an element of the array 
     * if this command has an "array" input argument.
     * @see fr.esrf.TangoDs.TangoConst
     * @return an <code>int</code> value containing the in_type.
     * @throws NoSuchElementException if no such property exists.
     */
    public String getInTypeElemName();

    public String getOutTypeElemName();

    public String getInTypeDescription();

    public String getOutTypeDescription();

    public String getLevel();

    public String getTag();


}
