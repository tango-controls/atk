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
 
// File:          CommandGroup.java
// Created:       2006-04-07 09:58:00, poncet
// 
// $Id $
// 
// Description:       

package fr.esrf.tangoatk.core;

import fr.esrf.tangoatk.core.command.ACommand;
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

/**
 * @deprecated  As of release ATKCore-4.3.1 and higher, please use instead fr.esrf.tangoatk.core.command.VoidVoidCommandGroup
 */

@Deprecated
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

    public String getCmdName()
    {
        String cmd = null;
        if (this.getSize() > 0)
        {
            ACommand  acmd = (ACommand) (this.get(0));
            if (acmd != null)
                cmd = acmd.getNameSansDevice();
        }
        return cmd;
    }

    public String getVersion() {
	return "$Id $";
    }

}
