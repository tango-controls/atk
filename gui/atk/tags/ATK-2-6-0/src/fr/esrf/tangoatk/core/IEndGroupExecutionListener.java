// File:          IEndGroupExecutionListener.java
// Created:       2006-04-07 10:31:47, poncet
// 
// $Id $
// 
// Description:       

package fr.esrf.tangoatk.core;

import java.util.EventListener;

/**
 * <code>IEndGroupExecutionListener</code> defines the behaviour of an object that listens to the end of the
 * execution of a group of commands. These listeners are used for CommandGroup class.
 * @see fr.esrf.tangoatk.core.CommandGroup
 */
public interface IEndGroupExecutionListener extends IErrorListener
{

    /**
     * <code>endExecution</code> is called when all the commands inside a CommandGroup
     * have finished to be executed.
     *
     * @param e a <code>EndGroupExecutionEvent</code> value
     */
    public void endGroupExecution(EndGroupExecutionEvent e);

}
