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
 
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.command;

import fr.esrf.tangoatk.core.*;

import java.util.*;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.CommandInfo;

/**
 * <code>CommandFactory</code> is an extension of {@link AEntityFactory}
 * which takes care of instantiating Command objects. It is a Singleton,
 * so please use getInstance to instantiate the Factory.
 */
public class CommandFactory extends AEntityFactory {

  private Vector<ACommand> commands = new Vector<ACommand> ();
  private String[] cmdNames = new String[0];  // For fast string search

  private static CommandFactory instance;

  /** Do not use */
  protected CommandFactory() {
  }

  public int getSize() {
    return commands.size();
  }

  /**
   * <code>getInstance</code> returns an instance of the CommandFactory.
   * There will be only one CommandFactory per running instance of the JVM.
   * @return an <code>AttributeFactory</code> value
   */
  public static CommandFactory getInstance() {
    if (instance == null) {
      instance = new CommandFactory();
    }
    return instance;
  }

  private int getCommandPos(String fqname) {
    return Arrays.binarySearch(cmdNames,fqname.toLowerCase());
  }

  protected synchronized List<IEntity> getWildCardEntities(String name, Device device)
          throws DevFailed {

    List<IEntity> list = new Vector<IEntity> ();
    CommandInfo[] info = device.getCommandList();
    for (int i = 0; i < info.length; i++) {
      String fqname = getFQName(device, info[i].cmd_name);
      IEntity entity = getSingleCommand(fqname, info[i], device);
      list.add(entity);
    }

    return list;
  }


  /* Added methos getSingleCommand on 11 june 2004 to improve performance */
  private ICommand getSingleCommand(String fqname, CommandInfo info, Device device) {

    // Check if the command already exists
    int pos = getCommandPos(fqname);
    if(pos>=0)
      return commands.get(pos);

    // Create it
    return initCommand(device,info,-(pos+1),fqname);
  }


  protected synchronized IEntity getSingleEntity(String fqname, Device device)
          throws DevFailed {

    String name = extractEntityName(fqname);
    CommandInfo info = device.getCommand(name);
    return getSingleCommand(fqname,info,device);

  }

  /**
   * Returns an ICommand corresponding to the given name. If such a command
   * already exists, the existing command is returned. Otherwise it is created.
   * @param fqname a <code>String</code> value containing the EntityName
   * fully qualified with device name.
   * @return null if the type of the IEntity is not ACommand, a valid entity otherwise.
   * @throws ConnectionException
   * @throws DevFailed
   */
  public ICommand getCommand(String fqname)
          throws ConnectionException, DevFailed {

    Device d = null;
    IEntity ie = null;

    synchronized(this) {
      // Check if the command already exists
      int pos = getCommandPos(fqname);
      if(pos>=0) ie = commands.get(pos);
    }

    if( ie==null ) {
      // Create the entity
      d  = getDevice(extractDeviceName(fqname));
      ie = getSingleEntity(fqname, d);
    }

    if (ie instanceof ACommand) {
      return (ACommand) ie;
    } else
      return null;

  }

  /**
   * Returns an array containing all commands.
   */
  public ACommand[] getCommands() {

    ACommand[] ret = new ACommand[commands.size()];
    synchronized(this) {
      for(int i=0;i<commands.size();i++)
        ret[i] = commands.get(i);
    }
    return ret;

  }

  public boolean isCommand(String fqname) {

    try {
      return (getCommand(fqname)!=null);
    } catch (Exception e) {
      return false;
    }

  }

  private ACommand initCommand(Device device,CommandInfo info,int insertionPos,String fqname) {

    ACommand command = getCommandOfType(device.getName(),info);
    command.init(device, info.cmd_name, info);

    // Build the new cmdNames array
    int lgth = cmdNames.length;
    String[] newCmdNames=new String[lgth+1];
    System.arraycopy(cmdNames,0,newCmdNames,0,insertionPos);
    System.arraycopy(cmdNames,insertionPos,newCmdNames,insertionPos+1,lgth-insertionPos);
    newCmdNames[insertionPos]=fqname.toLowerCase();
    cmdNames=newCmdNames;

    commands.add(insertionPos, command);

    dumpFactory("Adding " + fqname);

    return command;

  }

  private ACommand getCommandOfType(String devName,CommandInfo info) {

    if (info == null) {
      System.out.println( "Warning, DeviceFactory.getCommandOfType(): null CommandInfo pointer got from " + devName);
      return new InvalidCommand();
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

    System.out.println( "DeviceFactory.getCommandOfType() : Warning, Unsupported type for command "
                        + devName + " " + name + "(in type="+inType+",out type="+outType+")" );

    return new InvalidCommand();

  }

  private void dumpFactory(String msg) {
    if((DeviceFactory.getInstance().getTraceMode() & DeviceFactory.TRACE_CMDFACTORY)!=0) {
      System.out.println("-- CommnadFactory : " + msg + " ------------");
      for(int i=0;i<cmdNames.length;i++) {
        System.out.println("  " + i + ":" + cmdNames[i]);
      }
      System.out.println("-- CommnadFactory --------------------------------------");
    }
  }

  public String getVersion() {
    return "$Id$";
  }

}
