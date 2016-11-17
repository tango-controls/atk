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
 
/*
 * ErrorHistory.java
 *
 * Created on September 25, 2001, 10:58 AM
 */

package fr.esrf.tangoatk.widget.util;

import fr.esrf.tangoatk.core.*;

import javax.swing.*;
import java.awt.*;

/**
 * <code>ErrorHistory</code> a basic viewer for errors.
 * <pre>
 *	 ErrorHistory errorHistory = new ErrorHistory();
 *	 attributeList.addErrorListener(errorHistory);
 * </pre>
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
    constraints.weighty = 1;
    constraints.fill = GridBagConstraints.BOTH;
    getContentPane().add(panel, constraints);
    ButtonBar b = new ButtonBar();
    b.setControlee(this);
    constraints.weighty = 0;
    constraints.gridy = 1;
    constraints.weightx = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    getContentPane().add(b, constraints);
    setTitle("Tango error manager");
    setErrorBufferSize(300);
    pack();
  }

  public void ok() {
    //getRootPane().getParent().setVisible(false);
    setVisible(false);
  }

  public void errorChange(ErrorEvent evt) {
    panel.errorChange(evt);
  }

  public void setErrorOccured(ErrorEvent evt) {
    panel.setErrorOccured(evt);
  }

  boolean errorPopupEnabled = true;

  public void setErrorPopupEnabled(boolean v) {
    this.errorPopupEnabled = v;
  }
  
  public void setErrorBufferSize (int nbErrors)
  {
      panel.setErrorBufferSize(nbErrors);
  }

  public static void main(String[] args) throws Exception {

    ErrorHistory hist = new ErrorHistory();
    AttributeList list = new AttributeList();
    list.addErrorListener(hist);
    try {
      list.add("jlp/test/1/att_un");
      list.add("jlp/test/1/att_float");
      //list.add("tango://localhost:12345/pss/daresbury/local#dbase=no/Interlocks");
    } catch (Exception e) {
    }

    list.startRefresher();
    ATKGraphicsUtils.centerFrameOnScreen(hist);
    hist.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    hist.setVisible(true);

  }

}

