// File:          ErrorPopup.java
// Created:       2003-01-14 15:30:00, poncet
// By:            <poncet@esrf.fr>
// Time-stamp:    <2003-01-14 15:30:00, poncet>
// 
// $Id$
// 
// 
// Description:   A singleton class, essentially used to display the errors of
//                attribute setting and command execution. Using a singleton class
//                avoids multiple error windows for the same error.   

package fr.esrf.tangoatk.widget.util;

import fr.esrf.tangoatk.core.*;

/** A class which listens for command and attribute writing errors and display
  * an ErrorPane when an error occured.
 */
public class ErrorPopup implements IErrorListener, ISetErrorListener {

  
  private static ErrorPopup instance;


  /**
   * Creates the unique <code>ErrorPopup</code> instance. Cannot be used from outside.
   * Instead use getInstance() method.
   * @see #getInstance
   */
  private ErrorPopup()
  {
  }


  /**
   * <code>getInstance</code> returns the unique instance of the ErrorPopup.
   * There will be only one ErrorPopup per running instance of the JVM.
   * @return an <code>ErrorPopup</code> value
   */
  public static ErrorPopup getInstance()
  {
      if (instance == null)
      {
	  instance = new ErrorPopup();
      }
      return instance;
  }
  
  private String getSourceName(ErrorEvent evt)
  {

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

    list.addErrorListener(fr.esrf.tangoatk.widget.util.ErrorPopup.getInstance());
    list.addSetErrorListener(fr.esrf.tangoatk.widget.util.ErrorPopup.getInstance());
    list.add("jlp/test/1/att_trois");
    list.startRefresher();
  } // end of main ()

}
