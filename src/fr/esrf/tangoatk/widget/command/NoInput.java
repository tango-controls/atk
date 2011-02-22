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

				
