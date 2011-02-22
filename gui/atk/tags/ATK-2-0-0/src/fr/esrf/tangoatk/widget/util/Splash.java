/*
 * Splash2.java
 *
 * Created on February 28, 2002, 10:37 AM
 */

package fr.esrf.tangoatk.widget.util;

import javax.swing.*;
import java.awt.*;

public class Splash extends JWindow {

  private SplashPanel splashPanel;

  /** Creates new form Splash */
  public Splash() {

    setBackground(new Color(100,110,140));

    splashPanel = new SplashPanel();
    setContentPane(splashPanel);
    pack();

    // Center the splash window
    Dimension d = splashPanel.getPreferredSize();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((screenSize.width - d.width)/2 , (screenSize.height - d.height)/2,
              d.width,d.height);
    setVisible(true);

  }

  public void setCopyright(String copyright) {
    splashPanel.setCopyright(copyright);
  }

  public String getCopyright() {
    return splashPanel.getCopyright();
  }

  public void setMessage(String message) {
    splashPanel.setMessage(message);
  }

  public String getMessage() {
    return splashPanel.getMessage();
  }

  public void setTitle(String title) {
    splashPanel.setTitle(title);
  }

  public String getTitle() {
    return splashPanel.getTitle();
  }

  public JSmoothProgressBar getProgressBar() {
    return splashPanel.getProgress();
  }

  public void initProgress(int i) {
    splashPanel.getProgress().setMaximum(i);
    splashPanel.setProgress(i);
  }

  public void initProgress() {
    splashPanel.setProgress(0);
  }

  public void setMaxProgress(int i) {
    splashPanel.getProgress().setMaximum(i);
  }

  public void progress(int i) {
    splashPanel.setProgress(i);
  }

  public void setIndeterminateProgress(boolean b) {
    splashPanel.getProgress().setIndeterminate(b);
  }

  // For backward compatibilty (No longer used)

  /** @deprecated */
  public void setAuthor(String s) {}
  /** @deprecated */
  public void setPanelForeground(java.awt.Color color) {}
  /** @deprecated */
  public String getAuthor() { return ""; }
  /** @deprecated */
  public void setVersion(String s) {}
  /** @deprecated */
  public String getVersion() { return ""; }


  public static void main(String[] args) {

    Splash s = new Splash();
    s.setTitle("SplashScreen");
    s.setMessage("This is the free message line");
    for(int i=1;i<100;i++) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {}
      s.progress(i);
    }
    System.exit(0);

  } // end of main ()


}
