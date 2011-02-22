// File:          LuserErrorPopup.java
// Created:       2002-04-30 16:32:22, assum
// By:            <erik@assum.net>
// Time-stamp:    <2002-05-03 11:1:17, assum>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.util;
import fr.esrf.tangoatk.core.*;
import javax.swing.JOptionPane;

public class LuserErrorPopup implements IErrorListener {

    public void errorChange(ErrorEvent evt) {
	Throwable error = evt.getError();
	if (error instanceof AttributeSetException ||
	    error instanceof CommandExecuteException) {
	    JOptionPane.showMessageDialog(null, error, "Error",
					  JOptionPane.ERROR_MESSAGE);
	}
    }

    public static void main (String[] args) throws Exception {
	AttributeList list = new AttributeList();
	ErrorHistory eh = new fr.esrf.tangoatk.widget.util.ErrorHistory();
	list.addErrorListener(eh);
	eh.show();
	
	list.addErrorListener(new fr.esrf.tangoatk.widget.util.LuserErrorPopup());
	list.add("eas/test-api/1/attr_wrong_type");
	list.startRefresher();
    } // end of main ()
    
}
	
