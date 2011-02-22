// File:          IErrorHandler.java
// Created:       2002-02-08 14:42:09, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-02-08 14:53:9, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.core;

import java.util.EventListener;

public interface IErrorHandler extends EventListener {

    public void connectionException(ConnectionException e);

    public void attributeErrorException(AttributeErrorException e);

    public void attributeReadException(AttributeReadException e);

    public void attributeSetException(AttributeSetException e);

    public void commandExecuteException(CommandExecuteException e);

    public void unknownException(Exception e);
    
}
