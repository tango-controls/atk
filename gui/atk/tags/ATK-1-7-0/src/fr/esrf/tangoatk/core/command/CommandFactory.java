// File:          CommandFactory.java
// Created:       2001-09-28 09:18:50, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-07-10 15:6:25, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.command;

import fr.esrf.tangoatk.core.*;
import java.util.*;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.CommandInfo;

public class CommandFactory extends AEntityFactory {
    private Map commands = new HashMap();
    
    
    private static CommandFactory instance;

    protected CommandFactory() {
	;
    }
    
    public int getSize() {
	return commands.size();
    }
    
    public static CommandFactory getInstance() {
	if (instance == null) {
	    instance = new CommandFactory();
	}

	return instance;
    }



    protected List getWildCardEntities(String fqname , Device device)
    throws DevFailed {
	List list = new Vector();
	CommandInfo [] info = device.getCommandList();

/* Replaced the following code block on 11/june/2004 (F. Poncet)
   The old code did not make use of "commandInfo" already returned
   by getCommandList. The old call to Entity(fqName, device)
   recalled in TangORB "command_query" where all the necessay info was
   already returned by getCommandList().
   The new code make use of CommandInfos already returned!
   
	for (int i = 0; i < info.length; i++) {
	    IEntity entity = getSingleEntity(getFQName(device,
						       info[i].cmd_name),
					     device);
	    if (entity == null) continue;
	    list.add(entity);
	}
	
	End of replaced code ****/
	
	
	for (int i = 0; i < info.length; i++)
	{
	    IEntity entity = getSingleCommand( getFQName(device,info[i].cmd_name),
	                                       info[i], device);
	    if (entity == null) continue;
	    list.add(entity);
	}

	return list;
    }
    

  /* Added methos getSingleCommand on 11 june 2004 to improve performance */
    protected ICommand getSingleCommand(String fqname, CommandInfo info, Device device)
                       throws DevFailed
    {
	ACommand command = (ACommand)commands.get(fqname);
	if (command != null) return command;
	
	command = getCommandOfType(info);
	command.init(device, info.cmd_name, info);

	if (command == null) return null;

	commands.put(fqname, command);

	return command;
    }
    


    protected IEntity getSingleEntity(String fqname, Device device)
    throws DevFailed {
	ACommand command = (ACommand)commands.get(fqname);
	if (command != null) return command;
	String name = extractEntityName(fqname);
	CommandInfo info= device.getCommand(name);
	command = getCommandOfType(info);
	command.init(device, name, info);

	if (command == null) return null;

	commands.put(fqname, command);

	return command;
    }


    public ICommand getCommand(String fqname)
                              throws ConnectionException, DevFailed
    {
       Device d = null;
       IEntity ie = null;
       
       ACommand command = (ACommand)commands.get(fqname);
       if (command != null) return command;
       
       
       d = getDevice(extractDeviceName(fqname));
       ie = this.getSingleEntity(fqname, d);
       
       if (ie == null)
          return null;
	  
       if (ie instanceof ACommand)
       {
          command = (ACommand) ie;
	  return command;
       }
       else
          return null;
    }


    public boolean isCommand(String fqname)
    {
       if (commands.get(fqname) != null)
       {
	  return true;
       }
       
       Device d = null;
       try
       {
	  d = getDevice(extractDeviceName(fqname));
       }
       catch (Exception e)
       {
	  return false;
       }
       
       try
       {
	  d.getCommand(extractEntityName(fqname));
       }
       catch (Exception e)
       {
	  return false;
       }
       
       return true;
    }

    
    private ACommand getCommandOfType(CommandInfo info) {

	if (info == null) {
	    return null;
	}
	
	int inType = info.in_type;
	int outType = info.out_type;
	String name = info.cmd_name;
	
	if (ACommand.isTable(outType)) {
	    if (ACommand.isTable(inType)) {
		return new TableTableCommand();
	    }

	    if (ACommand.isArray(inType)) {
		return new ArrayTableCommand();
	    }
	    
	    if (ACommand.isScalar(inType)) {
		return new ScalarTableCommand();
	    }

	    if (ACommand.isVoid(inType)) {
		return new VoidTableCommand();
	    }
	}

	if (ACommand.isArray(outType)) {
	    if (ACommand.isTable(inType)) {
		return new TableArrayCommand();
	    }

	    if (ACommand.isArray(inType)) {
		return new ArrayArrayCommand();
	    }
	    
	    if (ACommand.isScalar(inType)) {
		return new ScalarArrayCommand();
	    }

	    if (ACommand.isVoid(inType)) {
		return new VoidArrayCommand();
	    }
	}

	if (ACommand.isScalar(outType)) {

	    if (ACommand.isTable(inType)) {
		return new TableScalarCommand();
	    }
	    
	    if (ACommand.isArray(inType)) {
		return new ArrayScalarCommand();
	    }
	    
	    if (ACommand.isScalar(inType)) {
		return new ScalarScalarCommand();
	    }

	    if (ACommand.isVoid(inType)) {
		return new VoidScalarCommand();
	    }
	}

	if (ACommand.isVoid(outType)) {

	    if (ACommand.isTable(inType)) {
		return new TableVoidCommand();
	    }
	    
	    if (ACommand.isArray(inType)) {
		return new ArrayVoidCommand();
	    }
	    
	    if (ACommand.isBoolean(inType)) {
		return new BooleanVoidCommand();
	    }
	    
	    if (ACommand.isString(inType)) {
		return new StringVoidCommand();
	    }
	    
	    if (ACommand.isScalar(inType)) {
		return new ScalarVoidCommand();
	    }
	    
	    if (ACommand.isVoid(inType)) {
		return new VoidVoidCommand();
	    }
	}

	return new InvalidCommand();

    }

    public String getVersion() {
	return "$Id$";
    }
    
}
