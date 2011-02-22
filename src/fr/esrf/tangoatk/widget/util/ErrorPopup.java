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

/** A class which listens for command and attribute writing errors and display
  * an ErrorPane when an error occured.
 */
public class ErrorPopup implements IErrorListener, ISetErrorListener {

  private String getSourceName(ErrorEvent evt) {

    Object src = evt.getSource();
    String ret="";

    if (src != null) {
      if (src instanceof IEntity) {
        ret = ((IEntity)src).getName();
      } else if (src instanceof Device) {
        ret = ((Device)src).getName();
      } else if (src instanceof String) {
        ret = (String)src;
      } else {
        System.out.println("ErrorPopup.getSourceName() : Warning getting unknown source object.");
      }
    }

    return ret;

  }

  public void errorChange(ErrorEvent evt) {

    Throwable error = evt.getError();

    if (error instanceof AttributeSetException ||
        error instanceof CommandExecuteException) {
      ATKException atkex = (ATKException) error;
      ErrorPane.showErrorMessage(null,getSourceName(evt),atkex);
    }

  }

  public void setErrorOccured(ErrorEvent evt) {

    Throwable error = evt.getError();

    if (error instanceof AttributeSetException) {
      ATKException atkex = (ATKException) error;
      ErrorPane.showErrorMessage(null,"Set Attribute Error",getSourceName(evt),atkex);
    } else {
      System.out.println("ErrorPopup.setErrorOccured() : not an AttributeSetException.");
    }

  }

  public static void main(String[] args) throws Exception {
    AttributeList list = new AttributeList();
    ErrorHistory eh = new fr.esrf.tangoatk.widget.util.ErrorHistory();
    list.addErrorListener(eh);
    eh.show();

    list.addErrorListener(new fr.esrf.tangoatk.widget.util.ErrorPopup());
    list.add("eas/test-api/1/attr_wrong_type");
    list.startRefresher();
  } // end of main ()

}
