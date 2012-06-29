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
 
// File:          VoidVoidCommandGroup.java
// Created:       2006-04-07 09:58:00, poncet
// 
// $Id $
// 
// Description:       

package fr.esrf.tangoatk.core.command;

import fr.esrf.tangoatk.core.*;
import fr.esrf.Tango.DevError;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.Group.Group;
import fr.esrf.TangoApi.Group.GroupCmdReply;
import fr.esrf.TangoApi.Group.GroupCmdReplyList;
import java.util.List;


/**
 * The VoidVoidCommandGroup contains only the commands with no input and no output argument :
 * they are all "VoidVoidCommand"s.
 *
 * When execute() is called, all the commands of the group are called asynchronously thanks to Tango Group call
 * When all the asynchronous calls are finished the "EndExecutionListeners" are
 * informed.
 * 
 * {@link CommandList}
 */
public class VoidVoidCommandGroup extends CommandList implements ICommandGroup
{
    protected EventSupport propChanges;
    protected Group        devGroup;
    protected String       uniqueCmdName=null;


    /**
     * Creates a new <code>VoidVoidCommandGroup</code> instance, and
     * instanciates its command factory.
     */
    public VoidVoidCommandGroup() {
	factory = CommandFactory.getInstance();
        devGroup = new Group("VoidVoidCmd Group");
	
	filter = new IEntityFilter()
	         {
		     public boolean keep(IEntity entity)
		     {
		         if (entity instanceof VoidVoidCommand)
                         {
			    if (uniqueCmdName != null)
                            {
                                if ( !uniqueCmdName.equalsIgnoreCase(entity.getNameSansDevice()) )
                                    return false;
                            }
                            if (uniqueCmdName == null)
                                uniqueCmdName = new String(entity.getNameSansDevice());
                            try
                            {
                                devGroup.add(entity.getDevice().getName());
                            }
                            catch (DevFailed ex)
                            {
                                return false;
                            }
                            return true;
                         }
                         else
			    return false;
		     }
	         };
	propChanges = new EventSupport();
    }
    
    /* override the setFilter method of EntityList */
    @Override
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
    
    
    
    public void loopExecute()
    {
        long          t0 = System.currentTimeMillis();
        IEntity       ie = null;
	ICommand      ic = null;

        DeviceFactory.getInstance().trace(DeviceFactory.TRACE_COMMAND, "VoidVoidCommandGroup.loopExecute()  ", t0);
        for (int i = 0; i < size(); i++)
	{
            ie = (IEntity) get(i);
	    
	    if (! (ie instanceof VoidVoidCommand) )
	       continue;
	       
	    ic = (ICommand) ie;
	    ic.execute(); // Synchronous calls
	}
	long  t1 = System.currentTimeMillis();
        DeviceFactory.getInstance().trace(DeviceFactory.TRACE_COMMAND, "VoidVoidCommandGroup.loopExecute()  end of loop", t1);
	
	publishEndExecution(null);
	    
    }



    public void execute()
    {
        long                t0 = System.currentTimeMillis();
        GroupCmdReplyList   replies=null;


        if (uniqueCmdName == null) return;
        if (devGroup == null) return;
        if (devGroup.get_size(false) <= 0) return;
        try
        {
            replies = devGroup.command_inout(uniqueCmdName, true);
            DeviceFactory.getInstance().trace(DeviceFactory.TRACE_COMMAND, "VoidVoidCommandGroup.execute()  ", t0);
            t0 = System.currentTimeMillis();
            if (replies.has_failed())
            {
                //System.out.print("CommandGroup.execute() : devGroup.command_inout has failed");
                for (int i = 0; i < replies.size(); i++)
                {
                    GroupCmdReply reply = (GroupCmdReply) replies.get(i);
                    if (reply.has_failed())
                    {
                        IEntity ie = this.get(reply.dev_name() + "/" + uniqueCmdName);
                        if (ie != null)
                        {
                            ACommand acmd = (ACommand) ie;
                            String message = "execute(" + acmd.getName() + ") failed: ";
                            CommandExecuteException cee = buildCmdException(message, reply);
                            acmd.cmdError(message, cee); // will call error listeners
                        }
                    }
                }
            }
            // For performance reasons do a test before building trace string
            if ( (DeviceFactory.getInstance().getTraceMode() & DeviceFactory.TRACE_COMMAND) != 0)
            {
                String str = buildRepliesTrace(replies);
                DeviceFactory.getInstance().trace(DeviceFactory.TRACE_COMMAND, "VoidVoidCommandGroup replies trace :  " + str, t0);
            }
        }
        catch (Exception ex)
        {
        }

        DeviceFactory.getInstance().trace(DeviceFactory.TRACE_COMMAND, "VoidVoidCommandGroup.execute()  end of loop", t0);

        publishEndExecution(null);
    }

    private String buildRepliesTrace(GroupCmdReplyList rep)
    {
        String  str;

        if (rep == null)
        {
            str ="ReplyList is null\n";
            return str;
        }

        if (rep.size() <= 0)
        {
            str ="ReplyList is empty\n";
            return str;
        }

        str = "Nb replies = "+rep.size()+" \n";

        if (rep.has_failed())
            str = str + "At least one reply has failed\n";
        else
            str = str + "All replies successful\n";
        for (int i=0; i<rep.size(); i++)
        {
            GroupCmdReply reply = (GroupCmdReply) rep.get(i);
            str = str+reply.dev_name()+" ";
            if (reply.has_failed())
                str = str + "has failed ";
            else
                str = str + "successfull ";
            IEntity ie = this.get(reply.dev_name() + "/" + uniqueCmdName);
            if (ie == null)
                str = str + "  Cannot get IEntity ";
            else
                str = str  + "  IEntity = " + ie.getName();
            str = str + "\n";
        }

        return str;
    }

    private CommandExecuteException buildCmdException(String msg, GroupCmdReply reply)
    {
        CommandExecuteException  cee=null;

        DevError[] errs = reply.get_err_stack();

        if (errs == null)
        {
            cee = new CommandExecuteException(msg);
            return cee;
        }

        if (errs.length <= 0)
        {
            cee = new CommandExecuteException(msg);
            return cee;
        }

        if (errs[0].reason != null)
            cee = new CommandExecuteException(errs[0].reason, errs);
        else
            cee = new CommandExecuteException(msg, errs);
        return cee;
    }


    public String getCmdName()
    {
        return uniqueCmdName;
    }


    public String getVersion() {
	return "$Id $";
    }

    public String toString()
    {
        String   str = null;

        if (uniqueCmdName == null)
            str = "CmdName = none\n";
        else
            str = "CmdName = "+uniqueCmdName+"\n";

        if (devGroup == null)
        {
            str = str+"DeviceGroup = none\n";
            return str;
        }
        if (devGroup.get_size(true) <= 0)
        {
            str = str+"DeviceGroup = empty\n";
            return str;
        }
        str = str+"DeviceGroup = \n";
        String[] devNames = devGroup.get_device_list(true);

        for (int i=0; i<devNames.length; i++)
        {
            str = str+devNames[i]+"\n";
        }

        return str;

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        VoidVoidCommandGroup cmdg = new VoidVoidCommandGroup();
        try
        {
            cmdg.add("id/id/00/open");
            cmdg.add("fe/id/0/open");
            cmdg.add("fe/id/0/delivery");
        }
        catch (ConnectionException ex)
        {
        }
        cmdg.execute();
    }

}
