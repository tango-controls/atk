package fr.esrf.tangoatk.widget.jdraw;

import com.sun.swing.internal.plaf.metal.resources.metal;

import javax.swing.*;

/**
 * A class to display a message
 */
public class SimpleMessage {
  public SimpleMessage(String message) {

    String title = "Message";
    String text = message;
    int idx = message.indexOf('\n');
    if(idx>=0) {
      title = message.substring(0,idx);
      message = message.substring(idx+1);
    }
    JOptionPane.showMessageDialog(null,message,title,JOptionPane.OK_OPTION|JOptionPane.INFORMATION_MESSAGE);

  }
}
