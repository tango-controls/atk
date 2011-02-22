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
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @see fr.esrf.tangoatk.core.IResultListener
 * @version $Version$
 */
public interface ICommand extends IEntity, TangoConst {
    
    /**
     * <code>execute</code> executes the command without input.
     * @see #execute(java.util.List input);
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

}
