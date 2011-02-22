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
 
package fr.esrf.tangoatk.widget.util;

import fr.esrf.tangoatk.core.ATKException;
import fr.esrf.tangoatk.core.ConnectionException;
import fr.esrf.tangoatk.core.command.CommandFactory;
import fr.esrf.Tango.DevFailed;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** A tango error message popup with a 'detail' (show error stack) button. */
public class ErrorPane {

  static Image errorImage=null;

  private static ErrorDialog createDialog(Component parentComponent, ATKException e, String title,String devName) {

    Window window = ATKGraphicsUtils.getWindowForComponent(parentComponent);
    ErrorDialog ret;

    if (errorImage == null) {
      try {
        Class theClass = Class.forName("fr.esrf.tangoatk.widget.util.ErrorPane");
        errorImage = ImageIO.read(theClass.getResource("/fr/esrf/tangoatk/widget/util/error.gif"));
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    if( window==null ) {
      ret = new ErrorDialog((Frame)null, e,title,devName);
    } else {
      if (window instanceof Frame) {
        ret = new ErrorDialog((Frame)window, e,title,devName);
      } else {
        ret = new ErrorDialog((Dialog)window, e,title,devName);
      }
    }


    return ret;

  }

  /**
   * Show an Error Popup dialog.
   * @param parentComponent Parent component.
   * @param devName Device name that has triggered the error.
   * @param e The error.
   */
  public static void showErrorMessage(Component parentComponent,String devName,ATKException e) {

    showErrorMessage(parentComponent,"Error",devName,e);

  }

  /**
   * Show an Error Popup dialog.
   * @param parentComponent Parent component.
   * @param title Dialog title.
   * @param devName Device name that has triggered the error.
   * @param e The error.
   */
  public static void showErrorMessage(Component parentComponent,String title,String devName,ATKException e) {

    ErrorDialog dlg = createDialog(parentComponent,e,title,devName);
    ATKGraphicsUtils.centerDialog(dlg);
    dlg.setVisible(true);

  }

  /**
   * Show an Error Popup dialog.
   * @param parentComponent Parent component.
   * @param devName Device name that has triggered the error.
   * @param e The error.
   */
  public static void showErrorMessage(Component parentComponent,String devName,DevFailed e) {
    ATKException ae = new ATKException(e);
    showErrorMessage(parentComponent,"Error",devName,ae);
  }

  /**
   * Show an Error Popup dialog.
   * @param parentComponent Parent component.
   * @param title Dialog title.
   * @param devName Device name that has triggered the error.
   * @param e The error.
   */
  public static void showErrorMessage(Component parentComponent,String title,String devName,DevFailed e) {
    ATKException ae = new ATKException(e);
    showErrorMessage(parentComponent,title,devName,ae);
  }

  /**
   * Show an Error Popup dialog.
   * @param parentComponent Parent component.
   * @param title Dialog title.
   * @param devName Device name that has triggered the error.
   * @param e The error.
   */
  public static void showErrorMessage(Component parentComponent,String title,String devName,Exception e) {
    ATKException ae;
    if(e instanceof DevFailed)
      ae = new ATKException((DevFailed)e);
    else
      ae = new ATKException(e);
    showErrorMessage(parentComponent,title,devName,ae);
  }

  /**
   * Show an Error Popup dialog.
   * @param parentComponent Parent component.
   * @param devName Device name that has triggered the error.
   * @param e The error.
   */
  public static void showErrorMessage(Component parentComponent,String devName,Exception e) {
    showErrorMessage(parentComponent,"Error",devName,e);
  }

  public static void main(String[] args) {

    ATKException e = new ATKException("This is an error");
    ErrorPane.showErrorMessage(null,"My error","",e);

    try {
      CommandFactory.getInstance().getCommand("jlp/test/1/Onn");
    } catch( ConnectionException e1) {
      ErrorPane.showErrorMessage(null,"My error","jlp/test/1",e1);
    } catch ( DevFailed e2) {
      ErrorPane.showErrorMessage(null,"My error","jlp/test/1",e2);
    } catch (Exception e3) {
      ErrorPane.showErrorMessage(null,"My error",null,new ATKException(e3));
    }
    System.exit(0);

  }

}

// --------------------------------------------------------------------------------
// Custom error dialog.
// --------------------------------------------------------------------------------
class ErrorDialog extends JDialog {

  JTextArea simpleError;
  JPanel simpleErrorPanel;
  JPanel buttonPanel;
  JButton okButton;
  JButton moreButton;
  JLabel iconLabel;
  ATKException theError;
  Icon errorIcon;

  JTabbedPane tabPanel;
  ErrorTree stackPanel;
  JScrollPane stackView;

  JTextArea stackTraceText;
  JScrollPane stackTraceView;

  String deviceName;
  boolean showDetails=false;

  ErrorDialog(Frame parent,ATKException e,String title,String devName) {
    super(parent,title,true);
    theError = e;
    deviceName = devName;
    initComponents();
  }

  ErrorDialog(Dialog parent,ATKException e,String title,String devName) {
    super(parent,title,true);
    theError = e;
    deviceName = devName;
    initComponents();
  }

  private void fillInStackTrace() {

    StackTraceElement[] st = theError.getStackTrace();
    StringBuffer str = new StringBuffer();
    str.append(theError.getSourceName()+"\n at\n");
    for(int i=0;i<st.length;i++) {
      str.append(st[i]);
      str.append('\n');
    }
    stackTraceText.setText(str.toString());

  }

  private void initComponents() {

    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    okButton = new JButton("Ok");
    moreButton = new JButton("Details...");

    moreButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if( !showDetails ) {
          getContentPane().add(tabPanel,BorderLayout.CENTER);
          moreButton.setText("Hide...");
          showDetails = true;
          pack();
          fillInStackTrace();
          setResizable(true);
        } else {
          getContentPane().remove(tabPanel);
          moreButton.setText("Details...");
          showDetails = false;
          pack();
          //setResizable(false);
        }
      }
    });

    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
        setVisible(false);
      }
    });

    simpleError = new JTextArea();
    simpleError.setEditable(false);
    simpleError.setBackground(contentPane.getBackground());
    simpleError.setFont(okButton.getFont());
    if( deviceName!=null && deviceName.length()>0 )
      simpleError.setText(deviceName + " :\n" + theError.getDescription());
    else
      simpleError.setText(theError.getDescription());

    simpleErrorPanel= new JPanel();
    simpleErrorPanel.setLayout(new FlowLayout(FlowLayout.LEFT,15,10));
    iconLabel = new JLabel(new ImageIcon(ErrorPane.errorImage));
    simpleErrorPanel.add(iconLabel,BorderLayout.WEST);
    simpleErrorPanel.add(simpleError,BorderLayout.CENTER);

    buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout());
    buttonPanel.setPreferredSize(new Dimension(260,40));
    buttonPanel.add(okButton);

    //if( theError.getStackLength()>0 )
      buttonPanel.add(moreButton);

    contentPane.add(simpleErrorPanel,BorderLayout.NORTH);
    contentPane.add(buttonPanel,BorderLayout.SOUTH);

    if( theError.getStackLength()>0 ) {
      stackPanel = new ErrorTree();
      stackPanel.addErrors(theError.getErrors());
      stackView = new JScrollPane(stackPanel);
      stackView.setBorder(BorderFactory.createEtchedBorder());
    }

    stackTraceText = new JTextArea();
    stackTraceText.setEditable(false);
    stackTraceView = new JScrollPane(stackTraceText);
    stackTraceView.setBorder(BorderFactory.createEtchedBorder());

    tabPanel = new JTabbedPane();
    if( theError.getStackLength()>0 )
      tabPanel.add("Error",stackView);
    else
      stackTraceView.setPreferredSize(new Dimension(0,200));
    tabPanel.add("Trace",stackTraceView);

    setResizable(false);

  }

}

