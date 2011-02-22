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
import org.apache.log4j.Logger;

public class CommandFactory extends AEntityFactory {
    private Map commands = new HashMap();
    
    static Logger log =
	ATKLogger.getInstance(CommandFactory.class.getName());
    
    private static CommandFactory instance;

    protected CommandFactory() {
	;
    }

    public void clear() {
	deviceFactory.clear();
	commands.clear();
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

	for (int i = 0; i < info.length; i++) {
	    IEntity entity = getSingleEntity(getFQName(device,
						       info[i].cmd_name),
					     device);
	    if (entity == null) continue;
	    list.add(entity);
	}

	return list;
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
	    
	    if (ACommand.isScalar(inType)) {
		return new ScalarVoidCommand();
	    }
	    
	    if (ACommand.isVoid(inType)) {
		return new VoidVoidCommand();
	    }
	}

	log.warn("TangoATK commands do not support this " + name);
	return new InvalidCommand();

    }

    public String getVersion() {
	return "$Id$";
    }
    
}
