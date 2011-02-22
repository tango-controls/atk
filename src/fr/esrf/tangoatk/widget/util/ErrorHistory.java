/*
 * BasicErrorHandler.java
 *
 * Created on September 25, 2001, 10:58 AM
 */

package fr.esrf.tangoatk.widget.util;
import fr.esrf.tangoatk.core.*;
import javax.swing.*;
import java.util.*;
import java.text.*;


/**
 * <code>ErrorHistory</code> a basic viewer for errors.
 * <code>
	ErrorHistory errorHistory = new ErrorHistory();<br>
	attributeList.setErrorListener(errorHistory);<br>
   </code>
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version 1.0
 */
public class ErrorHistory extends JFrame implements IErrorListener {
    ErrorPanel panel;
    
    public ErrorHistory() {
	panel = new ErrorPanel();
	setContentPane(panel);
	pack();
    }

    public void errorChange(ErrorEvent evt) {
	panel.errorChange(evt);
    }

    boolean errorPopupEnabled = true;
    
    public void setErrorPopupEnabled(boolean  v) {
	this.errorPopupEnabled = v;
    }
    
    public static void main (String[] args) throws Exception {
	ErrorHistory hist = new ErrorHistory();
	AttributeList list = new AttributeList();
	list.addErrorListener(hist);
	list.add("eas/test-api/1/*");
	list.startRefresher();
	hist.pack();
	hist.show();
	
    } // end of main ()
    
}

