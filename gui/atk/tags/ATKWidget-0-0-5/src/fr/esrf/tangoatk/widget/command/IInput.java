// File:          IInput.java
// Created:       2002-06-03 16:45:31, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-03 17:25:36, assum>
// 
// $Id$
// 
// Description:       


package fr.esrf.tangoatk.widget.command;

import fr.esrf.tangoatk.core.ICommand;

public interface IInput {

    public void setModel(ICommand model); 

    public ICommand getModel();
}
