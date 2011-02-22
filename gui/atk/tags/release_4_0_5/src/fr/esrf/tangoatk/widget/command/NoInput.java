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
 
// File:          NoInput.java
// Created:       2002-06-03 17:28:33, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-03 17:30:28, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.command;
import fr.esrf.tangoatk.core.ICommand;
import java.awt.*;

public class NoInput implements IInput
{

    public NoInput(ICommand command)
    {

    }

    public void setModel(ICommand command)
    {

    }

    public ICommand getModel()
    {
	return null;
    }
      
    public void setInputEnabled(boolean b)
    {
    }

    public boolean isInputEnabled()
    {
      return false;
    }
    
    public java.util.List getInput()
    {
      return null;
    }
    
    public void setInput(java.util.List  l)
    {
    }
    
    public void setVisible(boolean b)
    {
    }

    public boolean isVisible()
    {
      return false;
    }
    
    public void setFont(Font font)
    {
    }
    
    public Font getFont()
    {
       return null;
    }
    
}

				
