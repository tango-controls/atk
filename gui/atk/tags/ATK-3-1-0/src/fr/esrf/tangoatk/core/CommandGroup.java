// File:          CommandGroup.java
// Created:       2006-04-07 09:58:00, poncet
// 
// $Id $
// 
// Description:       

package fr.esrf.tangoatk.core;

import fr.esrf.tangoatk.core.command.CommandFactory;
import fr.esrf.tangoatk.core.command.VoidVoidCommand;
import java.util.List;


/**
 * The CommandGroup contains only the commands with no input and no output argument :
 * they are all "VoidVoidCommand"s.
 *
 * When execute() is called, all the commands of the group are called asynchronously.
 * When all the asynchronous calls are finished the "EndExecutionListeners" are
 * informed.
 * 
 * {@link CommandList}
 */
public class CommandGroup extends CommandList implements ICommandGroup
{
    protected EventSupport propChanges;

    /**
     * Creates a new <code>CommandGroup</code> instance, and 
     * instanciates its command factory.
     */
    public CommandGroup() {
	factory = CommandFactory.getInstance();
	
	filter = new IEntityFilter()
	         {
		     public boolean keep(IEntity entity)
		     {
		         if (entity instanceof VoidVoidCommand)
			    return true;
			 else
			    return false;
		     }
	         };
	propChanges = new EventSupport();
    }
    
    /* override the setFilter method of EntityList */
    public void setFilter(IEntityFilter filter)
    {
        return;
    }

    public void addEndGroupExecutionListener(IEndGroupExecutionListener l) {
	propChanges.addEndGroupExecutionListener(l);
    }

    public void removeEndGroupExecutionListener(IEndGroupExecutionListener l) {
	propChanges.removeEndGroupExecutionListener(l);
    }

    protected void publishEndExecution(List result) {
	propChanges.fireEndGroupExecutionEvent(this, result);
    }
    
    
    
    public void execute()
    {
        long          t0 = System.currentTimeMillis();
        IEntity       ie = null;
	ICommand      ic = null;

        DeviceFactory.getInstance().trace(DeviceFactory.TRACE_COMMAND, "CommandGroup.execute()  ", t0);
        for (int i = 0; i < size(); i++)
	{
            ie = (IEntity) get(i);
	    
	    if (! (ie instanceof VoidVoidCommand) )
	       continue;
	       
	    ic = (ICommand) ie;
	    ic.execute(); // Synchronous calls
	}
	DeviceFactory.getInstance().trace(DeviceFactory.TRACE_COMMAND, "CommandGroup.execute()  end of loop", t0);
	
	publishEndExecution(null);
	    
    }


    public String getVersion() {
	return "$Id $";
    }

}
