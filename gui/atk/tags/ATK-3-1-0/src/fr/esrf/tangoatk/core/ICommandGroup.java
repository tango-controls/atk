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
    public void setFilter(IEntityFilter filter);

    public void addEndGroupExecutionListener(IEndGroupExecutionListener l);

    public void removeEndGroupExecutionListener(IEndGroupExecutionListener l);
    
    public void execute();
}
