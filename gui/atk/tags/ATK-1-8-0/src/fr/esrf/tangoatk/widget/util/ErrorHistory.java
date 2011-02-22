/*
 * ErrorHistory.java
 *
 * Created on September 25, 2001, 10:58 AM
 */

package fr.esrf.tangoatk.widget.util;
import fr.esrf.tangoatk.core.*;
import javax.swing.*;
import java.util.*;
import java.text.*;
import java.awt.*;

/**
 * <code>ErrorHistory</code> a basic viewer for errors.
 * <code>
	ErrorHistory errorHistory = new ErrorHistory();<br>
	attributeList.addErrorListener(errorHistory);<br>
   </code>
 * @author <a href="mailto:assum@esrf.fr">Erik Assum</a>
 * @version $Revision$
 */
public class ErrorHistory extends JFrame implements IErrorListener,
                                                    ISetErrorListener,
						    IControlee {
    ErrorPanel panel;
    
    public ErrorHistory() {
	panel = new ErrorPanel();
	getContentPane().setLayout(new GridBagLayout());
	GridBagConstraints constraints = new GridBagConstraints();
	constraints.weighty  = 1;
	constraints.fill = GridBagConstraints.BOTH;
	getContentPane().add(panel, constraints);
	ButtonBar b = new ButtonBar();
	b.setControlee(this);
	constraints.weighty  = 0;
	constraints.gridy  = 1;
	constraints.weightx  = 1;
	constraints.fill = GridBagConstraints.HORIZONTAL;
	getContentPane().add(b, constraints);
	pack();
    }

    public void ok() {
	getRootPane().getParent().setVisible(false);
    }

    
    public void errorChange(ErrorEvent evt) {
	panel.errorChange(evt);
    }
    
    public void setErrorOccured(ErrorEvent evt)
    {
	panel.setErrorOccured(evt);
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

