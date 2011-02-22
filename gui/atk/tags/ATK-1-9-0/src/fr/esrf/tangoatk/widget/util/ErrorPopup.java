// File:          ErrorPopup.java
// Created:       2003-01-14 15:30:00, poncet
// By:            <poncet@esrf.fr>
// Time-stamp:    <2003-01-14 15:30:00, poncet>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.util;
import fr.esrf.tangoatk.core.*;
import javax.swing.JOptionPane;

public class ErrorPopup implements IErrorListener,ISetErrorListener
{

    public void errorChange(ErrorEvent evt)
    {
	Throwable error = evt.getError();
	if (error instanceof AttributeSetException ||
	    error instanceof CommandExecuteException) {
	    ATKException  atkex = (ATKException) error;
	    JOptionPane.showMessageDialog(null, atkex.getDescription(), "Error",
					  JOptionPane.ERROR_MESSAGE);
	}
    }
    
    public void setErrorOccured(ErrorEvent evt)
    {
	Throwable error = evt.getError();
	if (error instanceof AttributeSetException)
	{
	   ATKException  atkex = (ATKException) error;
	   JOptionPane.showMessageDialog(null, atkex.getDescription(), "Set Attribute Error",
	        JOptionPane.ERROR_MESSAGE);
	}
	else
	   System.out.println("ErrorPopup : setErrorOccured : not an AttributeSetException");
    }

    public static void main (String[] args) throws Exception {
	AttributeList list = new AttributeList();
	ErrorHistory eh = new fr.esrf.tangoatk.widget.util.ErrorHistory();
	list.addErrorListener(eh);
	eh.show();
	
	list.addErrorListener(new fr.esrf.tangoatk.widget.util.ErrorPopup());
	list.add("eas/test-api/1/attr_wrong_type");
	list.startRefresher();
    } // end of main ()
    
}
	
